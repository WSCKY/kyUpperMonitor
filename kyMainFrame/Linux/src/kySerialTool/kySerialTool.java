package kySerialTool;

import java.util.ArrayList;

import CommTool.CommTool;
import CommTool.exception.OpenPortFailure;
import CommTool.exception.ReadDataFailure;
import CommTool.exception.SendDataFailure;
import jserial.kySerialDrv;

public final class kySerialTool extends CommTool {
	private kySerialDrv serialDrv = null;
	private ArrayList<String> portNameList = null;
	private boolean serialOpened = false;
	private String Baudrate = "115200";
	public kySerialTool() {
		serialDrv = new kySerialDrv();
		portNameList = serialDrv.list_dev();
		serialDrv.setBlockTime(1, 0);
	}

	public ArrayList<String> refreshPortList() {
		portNameList = serialDrv.list_dev();
		return portNameList;
	}

	public void setBaudrate(int baud) {
		this.Baudrate = "" + baud;
	}

	public boolean isOpened() {
		return serialOpened;
	}

	public boolean openPort(String portName) throws OpenPortFailure {
		boolean dev_exit = false;
		if(serialOpened) return true;
		for(String s : portNameList) {
			if(s.equals(portName)) {
				dev_exit = true;
			}
		}
		if(dev_exit == false) {
//			throw new NoSuchPort();
//			System.err.println("No Such Port!");
			throw new OpenPortFailure("No Such Port!");
		}
		if(serialDrv.open(portName, this.Baudrate) < 0) {
//			throw new PortOpenFailed();
			throw new OpenPortFailure("Port Open Failed!");
		} else {
			serialOpened = true;
		}
		return true;
	}

	public int sendData(byte[] data, int size) throws SendDataFailure {
		int wr;
		if(!serialOpened) return -1;
		wr = serialDrv.send(data, size);
		if(wr < 0) {
			throw new SendDataFailure();
		}
		return wr;
	}

	public int readData(byte[] recv, int size) throws ReadDataFailure {
		int rd;
		if(!serialOpened) return -1;
		rd = serialDrv.read(recv, size);
		if(rd < 0) {
			throw new ReadDataFailure();
		}
		return rd;
	}

	public void closePort() {
		if(serialOpened) {
			serialDrv.close();
			serialOpened = false;
		}
	}

	public static void main(String[] args) {
		System.out.println("!!!TEST APP START!!!");
		kySerialTool sp = new kySerialTool();
		ArrayList<String> name = sp.refreshPortList();
		for(String s : name) {
			System.out.println("PORT: " + s);
		}
		System.out.println("!!!TEST APP ENDED!!!");
	}
}
