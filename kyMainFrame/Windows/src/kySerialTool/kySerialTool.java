package kySerialTool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import java.util.concurrent.Semaphore;

import CommTool.CommTool;
import CommTool.exception.OpenPortFailure;
import CommTool.exception.PortActionEventListener.PortActionCode;
import CommTool.exception.ReadDataFailure;
import CommTool.exception.SendDataFailure;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import kySerialTool.serialException.NoSuchPort;
import kySerialTool.serialException.NotASerialPort;
import kySerialTool.serialException.PortInUse;
import kySerialTool.serialException.SerialPortParameterFailure;
import kySerialTool.serialException.TooManyListeners;

public class kySerialTool extends CommTool implements SerialPortEventListener {
	private ArrayList<String> portNameList = null;
	private SerialPort OpenedSerialPort = null;
	private int Baudrate = 115200;
	private int DataBits = SerialPort.DATABITS_8;
	private int StopBits = SerialPort.STOPBITS_1;
	private int Parity = SerialPort.PARITY_NONE;
	public kySerialTool() {
		portNameList = new ArrayList<>();
		dataAvailable = new Semaphore(1);
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

	public boolean isOpened() {
		if(OpenedSerialPort == null) return false;
		return true;
	}

	public boolean openPort(String[] args) throws OpenPortFailure {
		return false;
	}

	public SerialPort openPort(String portName) throws NoSuchPort, PortInUse, NotASerialPort, SerialPortParameterFailure, TooManyListeners {
		try {
			CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName); // throw no such port.
			CommPort comPort = portIdentifier.open(portName, 1000); // throw port in use.
			if(comPort instanceof SerialPort) { // throw not a serial port.
				OpenedSerialPort = (SerialPort)comPort;
				try {
					OpenedSerialPort.setSerialPortParams(Baudrate, DataBits, StopBits, Parity);
					try {
						OpenedSerialPort.addEventListener(this);
					} catch (TooManyListenersException e) {
						// TODO Auto-generated catch block
						this.notifyAllListeners(PortActionCode.PortAction_Failed);
						throw new TooManyListeners();
					}
					OpenedSerialPort.notifyOnDataAvailable(true);
					OpenedSerialPort.notifyOnBreakInterrupt(true);
					this.notifyAllListeners(PortActionCode.PortAction_Opened);
					return OpenedSerialPort;
				} catch (UnsupportedCommOperationException e) {
					// TODO Auto-generated catch block
					this.notifyAllListeners(PortActionCode.PortAction_Failed);
					throw new SerialPortParameterFailure();
				}
			} else {
				this.notifyAllListeners(PortActionCode.PortAction_Failed);
				throw new NotASerialPort();
			}
		} catch (NoSuchPortException e) {
			// TODO Auto-generated catch block
			this.notifyAllListeners(PortActionCode.PortAction_Failed);
			throw new NoSuchPort();
		} catch (PortInUseException e) {
			// TODO Auto-generated catch block
			this.notifyAllListeners(PortActionCode.PortAction_Failed);
			throw new PortInUse();
		}
	}

	private OutputStream serialOutputStream = null;
	public int sendData(byte[] data, int size) throws SendDataFailure {
		if(OpenedSerialPort == null) return -1;
		try {
			serialOutputStream = OpenedSerialPort.getOutputStream();
			serialOutputStream.write(data);
			serialOutputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new SendDataFailure();
		} finally {
			if(serialOutputStream != null) {
				try {
					serialOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.err.println("serial output stream close failed.");
					throw new SendDataFailure();
				}
				serialOutputStream = null;
			}
		}
		return size;
	}

	private Semaphore dataAvailable;
	private InputStream serialInputStream = null;
	public int readData(byte[] data, int size) throws ReadDataFailure {
		if(OpenedSerialPort == null) return -1;
		int len;
		try {
			dataAvailable.acquire();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			throw new ReadDataFailure();
		}
		try {
			serialInputStream = OpenedSerialPort.getInputStream();
			len = serialInputStream.available();
			if(len > size) {
				len = size;
				dataAvailable.release();
			}
			serialInputStream.read(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new ReadDataFailure();
		} finally {
			if(serialInputStream != null) {
				try {
					serialInputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					throw new ReadDataFailure();
				}
				serialInputStream = null;
			}
		}
		return len;
	}

	@Override
	public void serialEvent(SerialPortEvent serialPortEvent) {
		// TODO Auto-generated method stub
		switch (serialPortEvent.getEventType()) {
		case SerialPortEvent.BI: // 10 通讯中断
			System.err.println("UART Communicatoin Interrupt!");
    	break;
    	case SerialPortEvent.OE: // 7 溢位（溢出）错误
    	case SerialPortEvent.FE: // 9 帧错误
    	case SerialPortEvent.PE: // 8 奇偶校验错误
    	case SerialPortEvent.CD: // 6 载波检测
    	case SerialPortEvent.CTS: // 3 清除待发送数据
    	case SerialPortEvent.DSR: // 4 待发送数据准备好了
    	case SerialPortEvent.RI: // 5 振铃指示
    	case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 2 输出缓冲区已清空
    	break;
    	case SerialPortEvent.DATA_AVAILABLE: // 1 串口存在可用数据
    		dataAvailable.release();
    	break;
		}
	}

	public void closePort() {
		if(OpenedSerialPort != null) {
			OpenedSerialPort.removeEventListener();
			OpenedSerialPort.close();
		}
		OpenedSerialPort = null;
		this.notifyAllListeners(PortActionCode.PortAction_Closed);
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
