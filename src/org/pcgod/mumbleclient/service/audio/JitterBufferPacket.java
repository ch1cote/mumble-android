package org.pcgod.mumbleclient.service.audio;

import java.nio.ByteBuffer;

class JitterBufferPacket {
	public ByteBuffer data;
	public int timestamp;
	public int span;
	public int flags;
}
