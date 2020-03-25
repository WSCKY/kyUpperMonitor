package kySocketTool;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import CommTool.CommTool;
import CommTool.exception.ReadDataFailure;
import kySocketTool.socketException.SocketInitFailed;

public class kySocketTool extends CommTool {
	private int CommPort = 6000;
	private DatagramSocket CommSocket = null;

	public kySocketTool() {

	}

	public void openPort() throws SocketInitFailed {
		try {
			CommSocket = new DatagramSocket(CommPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			throw new SocketInitFailed();
		}
		try {
			CommSocket.setSoTimeout(100);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setPort(int port) {
		CommPort = port;
	}

	public void closePort() {
		if(CommSocket != null) {
			CommSocket.close();
		}
	}

	public int readData(byte[] recv, int size) throws ReadDataFailure {
		DatagramPacket packet = new DatagramPacket(recv, 0, size);
		try {
			CommSocket.receive(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			System.err.println("timeout");
		}

		return packet.getLength();
	}
}

