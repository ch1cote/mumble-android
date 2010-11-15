package org.pcgod.mumbleclient.service;

interface IServiceObserver {
	
	void onChannelAdded(); //Channel channel);

	void onChannelRemoved(); //int channelId);

	void onChannelUpdated(); //Channel channel);

	void onCurrentChannelChanged();
	
	void onCurrentUserUpdated();
	
	void onUserAdded();
	
	void onUserRemoved();
	
	void onUserUpdated();
	
	void onMessageReceived(); //Message msg);

	void onMessageSent(); //Message msg);
	
	/**
	 * Called when the connection state changes.
	 */
	void onConnectionStateChanged(int state);
}
