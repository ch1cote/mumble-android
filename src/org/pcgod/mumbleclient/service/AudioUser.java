package org.pcgod.mumbleclient.service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.pcgod.mumbleclient.Globals;
import org.pcgod.mumbleclient.jni.Native;
import org.pcgod.mumbleclient.service.model.User;

import android.util.Log;

/**
 * Thread safe buffer for audio data.
 * Implements audio queue and decoding.
 *
 * @author pcgod, Rantanen
 */
class AudioUser {

	public interface PacketReadyHandler {
		public void packetReady(AudioUser user);
	}

	private final Object jbLock = new Object();
	private final long jitterBuffer;
	private final int[] currentTimestamp = new int[1];

	private final long celtMode;
	private final long celtDecoder;
	private final Queue<byte[]> dataArrayPool = new ConcurrentLinkedQueue<byte[]>();
	float[] lastFrame = new float[MumbleConnection.FRAME_SIZE];
	private final User user;

	private int missedFrames = 0;

	public AudioUser(User user) {
		this.user = user;
		celtMode = Native.celt_mode_create(
			MumbleConnection.SAMPLE_RATE,
			MumbleConnection.FRAME_SIZE);
		celtDecoder = Native.celt_decoder_create(celtMode, 1);

		jitterBuffer = Native.jitter_buffer_init(MumbleConnection.FRAME_SIZE);
		Native.jitter_buffer_ctl(
			jitterBuffer,
			0,
			new int[] { 5 * MumbleConnection.FRAME_SIZE });

		Log.i(Globals.LOG_TAG, "AudioUser created");
	}

	private byte[] acquireDataArray() {
		byte[] data = dataArrayPool.poll();

		if (data == null) {
			data = new byte[128];
		}

		return data;
	}

	public boolean addFrameToBuffer(
		final PacketDataStream pds,
		final PacketReadyHandler readyHandler) {

		final int packetHeader = pds.next();

		// Make sure this is supported voice packet.
		//
		// (Yes this check is included in MumbleConnection as well but I believe
		// it should be made here since the decoding support is built into this
		// class anyway. In theory only this class needs to know what packets
		// can be decoded.)
		final int type = (packetHeader >> 5) & 0x7;
		if (type != MumbleConnection.UDPMESSAGETYPE_UDPVOICECELTALPHA &&
			type != MumbleConnection.UDPMESSAGETYPE_UDPVOICECELTBETA) {
			return false;
		}

		/* long session = */pds.readLong();
		final long sequence = pds.readLong();

		int dataHeader;
		int frameCount = 0;
		final byte[] data = acquireDataArray();
		do {
			dataHeader = pds.next();
			final int dataLength = dataHeader & 0x7f;
			if (dataLength > 0) {
				pds.dataBlock(data, dataLength);

				final Native.JitterBufferPacket jbp = new Native.JitterBufferPacket();
				jbp.data = data;
				jbp.len = dataLength;
				jbp.timestamp = (short) (sequence + frameCount) *
								MumbleConnection.FRAME_SIZE;
				jbp.span = MumbleConnection.FRAME_SIZE;

				synchronized (jbLock) {
					Native.jitter_buffer_put(jitterBuffer, jbp);
				}

				readyHandler.packetReady(this);
				frameCount++;

			}
		} while ((dataHeader & 0x80) > 0 && pds.isValid());

		freeDataArray(data);
		return true;
	}

	@Override
	protected final void finalize() {
		Native.celt_decoder_destroy(celtDecoder);
		Native.celt_mode_destroy(celtMode);
		Native.jitter_buffer_destroy(jitterBuffer);
	}

	public void freeDataArray(byte[] data) {
		dataArrayPool.add(data);
	}

	public User getUser() {
		return this.user;
	}

	/**
	 * Checks if this user has frames and sets lastFrame.
	 *
	 * @return
	 */
	public boolean hasFrame() {
		byte[] data = null;
		int dataLength = 0;

		final Native.JitterBufferPacket jbp = new Native.JitterBufferPacket();
		jbp.data = acquireDataArray();
		jbp.len = jbp.data.length;

		synchronized (jbLock) {
			if (Native.jitter_buffer_get(
				jitterBuffer,
				jbp,
				MumbleConnection.FRAME_SIZE,
				currentTimestamp) == 0) {

				data = jbp.data;
				dataLength = jbp.len;
				missedFrames = 0;
			} else {
				missedFrames++;
			}

			Native.jitter_buffer_update_delay(jitterBuffer, null, null);
		}

		if (missedFrames > 20) {
			return false;
		}

		Native.celt_decode_float(celtDecoder, data, dataLength, lastFrame);
		if (data != null) {
			freeDataArray(data);
		}

		synchronized (jbLock) {
			Native.jitter_buffer_tick(jitterBuffer);
		}
		return true;
	}
}
