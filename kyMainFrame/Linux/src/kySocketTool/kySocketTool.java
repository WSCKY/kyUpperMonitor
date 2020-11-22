package kySocketTool;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import CommTool.CommTool;
import CommTool.exception.OpenPortFailure;
import CommTool.exception.ReadDataFailure;

public class kySocketTool extends CommTool {
	private int CommPort = 6000;
	private DatagramSocket CommSocket = null;

	public kySocketTool() {

	}

	public boolean openPort(String portName) throws OpenPortFailure {
		this.CommPort = Integer.parseInt(portName);
		try {
			CommSocket = new DatagramSocket(CommPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			this.CommSocket = null;
			System.err.println("Socket Initialize Failed!");
			throw new OpenPortFailure("Socket Initialize Failed!");
		}
		try {
			CommSocket.setSoTimeout(1000);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			this.CommSocket = null;
			throw new OpenPortFailure("configure timeout Failed!");
		}
		return true;
	}

	public void closePort() {
		if(CommSocket != null) {
			CommSocket.close();
		}
	}

	public boolean isOpened() {
		return (this.CommSocket != null);
	}

	public int readData(byte[] recv, int size) throws ReadDataFailure {
		DatagramPacket packet = new DatagramPacket(recv, 0, size);
		try {
			CommSocket.receive(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return -1;
//			throw new ReadDataFailure("Socket IO Exception");
		}

		return packet.getLength();
	}
}

