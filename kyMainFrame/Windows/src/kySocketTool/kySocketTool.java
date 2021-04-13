package kySocketTool;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import CommTool.CommTool;
import CommTool.exception.OpenPortFailure;
import CommTool.exception.ReadDataFailure;
import CommTool.exception.PortActionEventListener.PortActionCode;

public class kySocketTool extends CommTool {
	private int CommPort = 6000;
	private boolean open_flag = false;
	private DatagramSocket CommSocket = null;

	public kySocketTool() {

	}

	public boolean openPort(String[] args) throws OpenPortFailure {
		this.CommPort = Integer.parseInt(args[0]);
		try {
			CommSocket = new DatagramSocket(CommPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			this.CommSocket = null;
			System.err.println("error while initialize socket");
			this.notifyAllListeners(PortActionCode.PortAction_Failed);
			throw new OpenPortFailure("Socket Initialize Failed!");
		}
		try {
			CommSocket.setSoTimeout(1000);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			this.notifyAllListeners(PortActionCode.PortAction_Failed);
			this.CommSocket = null;
			throw new OpenPortFailure("configure timeout Failed!");
		}
		open_flag = true;
		this.notifyAllListeners(PortActionCode.PortAction_Opened);
		return true;
	}

	public void setPort(int port) {
		CommPort = port;
	}

	public void closePort() {
		if(CommSocket != null) {
			CommSocket.close();
			open_flag = false;
		}
		this.notifyAllListeners(PortActionCode.PortAction_Closed);
	}

	public boolean isOpened() {
		return open_flag;
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

