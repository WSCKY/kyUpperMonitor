package kySerialTool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import kySerialTool.serialException.NoSuchPort;
import kySerialTool.serialException.NotASerialPort;
import kySerialTool.serialException.PortInUse;
import kySerialTool.serialException.ReadDataFromSerialPortFailure;
import kySerialTool.serialException.SendDataToSerialPortFailure;
import kySerialTool.serialException.SerialPortInputStreamCloseFailure;
import kySerialTool.serialException.SerialPortOutputStreamCloseFailure;
import kySerialTool.serialException.SerialPortParameterFailure;
import kySerialTool.serialException.TooManyListeners;

public class kySerialTool {
	private ArrayList<String> portNameList = null;
	private SerialPort OpenedSerialPort = null;
	private SerialPortEventListener PortListener = null;
	private int Baudrate = 115200;
	private int DataBits = SerialPort.DATABITS_8;
	private int StopBits = SerialPort.STOPBITS_1;
	private int Parity = SerialPort.PARITY_NONE;
	public kySerialTool() {
		portNameList = new ArrayList<>();
	}

	public ArrayList<String> refreshPortList() {
		portNameList.clear();
		@SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier portIdentifier = portEnum.nextElement();
			if(portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL)
				portNameList.add(portIdentifier.getName());
        }
		return portNameList;
	}

	public void setBaudrate(int baud) {
		this.Baudrate = baud;
	}

	public SerialPort openPort(String portName) throws NoSuchPort, PortInUse, NotASerialPort, SerialPortParameterFailure, TooManyListeners {
		try {
			CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName); // throw no such port.
			CommPort comPort = portIdentifier.open(portName, 1000); // throw port in use.
			if(comPort instanceof SerialPort) { // throw not a serial port.
				OpenedSerialPort = (SerialPort)comPort;
				try {
					OpenedSerialPort.setSerialPortParams(Baudrate, DataBits, StopBits, Parity);
					applyUserListener();
					return OpenedSerialPort;
				} catch (UnsupportedCommOperationException e) {
					// TODO Auto-generated catch block
					throw new SerialPortParameterFailure();
				}
			} else {
				throw new NotASerialPort();
			}
		} catch (NoSuchPortException e) {
			// TODO Auto-generated catch block
			throw new NoSuchPort();
		} catch (PortInUseException e) {
			// TODO Auto-generated catch block
			throw new PortInUse();
		}
	}

	private void applyUserListener() throws TooManyListeners {
		try {
			if(PortListener != null) {
				OpenedSerialPort.addEventListener(PortListener);
				OpenedSerialPort.notifyOnDataAvailable(true);
				OpenedSerialPort.notifyOnBreakInterrupt(true);
			}
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			throw new TooManyListeners();
		}
	}
	public void addEventListener(SerialPortEventListener listener) throws TooManyListeners {
		PortListener = listener;
		if(OpenedSerialPort != null) {
			OpenedSerialPort.removeEventListener();
			applyUserListener();
		}
	}

	private OutputStream serialOutputStream = null;
	public void sendData(byte[] data) throws SendDataToSerialPortFailure, SerialPortOutputStreamCloseFailure {
		if(OpenedSerialPort == null) return;
		try {
			serialOutputStream = OpenedSerialPort.getOutputStream();
			serialOutputStream.write(data);
			serialOutputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new SendDataToSerialPortFailure();
		} finally {
			if(serialOutputStream != null) {
				try {
					serialOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					throw new SerialPortOutputStreamCloseFailure();
				}
				serialOutputStream = null;
			}
		}
	}

	private InputStream serialInputStream = null;
	public byte[] readData() throws ReadDataFromSerialPortFailure, SerialPortInputStreamCloseFailure {
		if(OpenedSerialPort == null) return null;
		byte[] bytes = null;
		try {
			serialInputStream = OpenedSerialPort.getInputStream();
			int len = serialInputStream.available();
			if(len > 0) {
				bytes = new byte[len];
				serialInputStream.read(bytes);
			}
			return bytes;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new ReadDataFromSerialPortFailure();
		} finally {
			if(serialInputStream != null) {
				try {
					serialInputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					throw new SerialPortInputStreamCloseFailure();
				}
				serialInputStream = null;
			}
		}
	}

	public void closePort() {
		if(OpenedSerialPort != null) {
			OpenedSerialPort.removeEventListener();
			OpenedSerialPort.close();
		}
		OpenedSerialPort = null;
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
