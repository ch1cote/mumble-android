package org.pcgod.mumbleclient.service;

import java.io.IOException;

/**
 * Protocol interface for the connection
 * 
 * @author Rantanen
 * 
 */
public interface Protocol {
	public void processTcp(short type, byte[] data) throws IOException;

	public void processUdp(byte[] data, int length) throws IOException;
}
