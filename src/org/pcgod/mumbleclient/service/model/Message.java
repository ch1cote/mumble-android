package org.pcgod.mumbleclient.service.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Message implements Parcelable {
	public static final int DIRECTION_SENT = 0;
	public static final int DIRECTION_RECEIVED = 1;

	public static final Parcelable.Creator<Message> CREATOR = new Creator<Message>() {

		@Override
		public Message[] newArray(int size) {
			return new Message[size];
		}

		@Override
		public Message createFromParcel(Parcel source) {
			return new Message(source);
		}
	};

	public String message;
	public String sender;
	public User actor;
	public Channel channel;
	public long timestamp;
	public int channelIds;
	public int treeIds;

	public int direction;

	public Message() { }

	public Message(Parcel parcel) {
		readFromParcel(parcel);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(0); // Version

		dest.writeString(message);
		dest.writeString(sender);
		dest.writeParcelable(actor, 0);
		dest.writeParcelable(channel, 0);
		dest.writeLong(timestamp);
		dest.writeInt(channelIds);
		dest.writeInt(treeIds);
		dest.writeInt(direction);
	}

	public void readFromParcel(Parcel in) {
		in.readInt(); // Version

		message = in.readString();
		sender = in.readString();
		actor = in.readParcelable(null);
		channel = in.readParcelable(null);
		timestamp = in.readLong();
		channelIds = in.readInt();
		treeIds = in.readInt();
		direction = in.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
