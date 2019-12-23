package kySocketTool;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import kySocketTool.socketEvent.kySocketEvent;
import kySocketTool.socketEvent.kySocketEventListener;
import kySocketTool.socketException.SocketInitFailed;

public class kySocketTool implements Runnable {
	private int CommPort = 6000;
	private DatagramSocket CommSocket = null;

	private Thread RecvThread = null;
	private kySocketEventListener Listener = null;
	public kySocketTool() {
		RecvThread = new Thread(this);
	}

	public void openPort() throws SocketInitFailed {
		try {
			CommSocket = new DatagramSocket(CommPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			throw new SocketInitFailed();
		}
		RecvThread.start();
	}

	public void setPort(int port) {
		CommPort = port;
	}

	public void setListener(kySocketEventListener l) {
		Listener = l;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		byte[] data = new byte[100];
		DatagramPacket packet = new DatagramPacket(data, 0, data.length);
		while(true) {
			try {
				CommSocket.receive(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(Listener != null) {
				Listener.kySocketRecvDataCallback(new kySocketEvent(new SocketDataPackage(packet.getData(), packet.getLength())));
			}
		}
	}
}

class SocketDataPackage {
	private byte[] data = null;
	private int length = 0;
	public SocketDataPackage(byte[] d, int l) {
		data = d;
		length = l;
	}

	public int getLength() {
		return length;
	}
	public byte[] getBytes() {
		return data;
	}
}
