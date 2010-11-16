package org.pcgod.mumbleclient.service;

import org.pcgod.mumbleclient.service.model.Channel;
import org.pcgod.mumbleclient.service.model.Message;
import org.pcgod.mumbleclient.service.model.User;

import android.os.IBinder;
import android.os.RemoteException;

public class BaseServiceObserver implements IServiceObserver {

	@Override
	public IBinder asBinder() {
		return null;
	}

	@Override
	public void onChannelAdded(Channel channel) throws RemoteException {
	}

	@Override
	public void onChannelRemoved(Channel channel) throws RemoteException {
	}

	@Override
	public void onChannelUpdated(Channel channel) throws RemoteException {
	}

	@Override
	public void onConnectionStateChanged(int state) throws RemoteException {
	}

	@Override
	public void onCurrentChannelChanged() throws RemoteException {
	}

	@Override
	public void onCurrentUserUpdated() throws RemoteException {
	}

	@Override
	public void onMessageReceived(Message msg) throws RemoteException {
	}

	@Override
	public void onMessageSent(Message msg) throws RemoteException {
	}

	@Override
	public void onUserAdded(User user) throws RemoteException {
	}

	@Override
	public void onUserRemoved(User user) throws RemoteException {
	}

	@Override
	public void onUserUpdated(User user) throws RemoteException {
	}
}
