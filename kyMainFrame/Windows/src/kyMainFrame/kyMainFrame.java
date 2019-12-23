package kyMainFrame;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import kyLink.kyLinkPackage;
import kyLink.decoder.kyLinkDecoder;
import kyLink.event.kyLinkDecodeEvent;
import kyLink.event.kyLinkDecodeEventListener;
import kyMainFrame.optEvent.ChangeIfEvent;
import kyMainFrame.optEvent.ChangeIfEventListener;
import kySerialTool.kySerialTool;
import kySerialTool.serialException.NoSuchPort;
import kySerialTool.serialException.NotASerialPort;
import kySerialTool.serialException.PortInUse;
import kySerialTool.serialException.ReadDataFromSerialPortFailure;
import kySerialTool.serialException.SendDataToSerialPortFailure;
import kySerialTool.serialException.SerialPortInputStreamCloseFailure;
import kySerialTool.serialException.SerialPortOutputStreamCloseFailure;
import kySerialTool.serialException.SerialPortParameterFailure;
import kySerialTool.serialException.TooManyListeners;

public class kyMainFrame extends JFrame implements ChangeIfEventListener, SerialPortEventListener, kyLinkDecodeEventListener {
	private static final long serialVersionUID = 112233L;

	private static final String kyFrameVersion = "V0.8.8 kyChu@2019/01/25 15:50";

	private kySerialTool UartTool = null;
	private kyLinkDecoder decoder = null;

	private Thread SignalCheckTask = null;
	public kyMainFrame() {
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }
		initGUI();

		decoder = new kyLinkDecoder();
		decoder.addDecodeListener(this);

		SignalCheckTask = new Thread(SignalCheckThread);

		UartTool = new kySerialTool();
		try {
			UartTool.addEventListener(this);
		} catch (TooManyListeners e) {
			// TODO Auto-generated catch block
			System.err.println("UartTool: Failed to add Event Listenr!!!");
		}
		(new Thread(UartRefreshThread)).start();
		SignalCheckTask.start();
	}

	/* Menubar */
	private FrameMenuBar RootMenuBar = null;
	/* connect panel */
	private ConnectPanel MainCP = null;
	/* main panel */
	private JPanel UserMainPanel = null;
	public void initGUI() {
		this.setTitle("kyChu.kyFrame");
		this.setSize(1000, 600);
		this.setResizable(true);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		RootMenuBar = new FrameMenuBar();
		RootMenuBar.addChangeIfActionListener(this);
		this.setJMenuBar(RootMenuBar);
		MainCP = new ConnectPanel();
		MainCP.addOpenPortActionListener(OpenPortListener);
		this.add(MainCP, BorderLayout.NORTH);

		UserMainPanel = new JPanel();
		this.add(UserMainPanel, BorderLayout.CENTER);

		this.addWindowListener(MainWindowListener);
	}

	public JPanel getUsrMainPanel() {return UserMainPanel;}

	public void addDecodeListener(kyLinkDecodeEventListener listener) {
		decoder.addDecodeListener(listener);
	}
	public void removeDecoderListener(kyLinkDecodeEventListener listener) throws InterruptedException {
		decoder.removeDecodeListener(listener);
	}
	public void removeAllDecodeListeners() throws InterruptedException {
		decoder.removeAllListeners();
		decoder.addDecodeListener(this);
	}

	public void TxPackage(kyLinkPackage pack) {
		try {
			UartTool.sendData(pack.getSendBuffer());
		} catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure e) {
			// TODO Auto-generated catch block
			System.err.println("Package Send Failed.");
		}
	}

	public void TxBuffer(byte[] data) {
		try {
			UartTool.sendData(data);
		} catch (SendDataToSerialPortFailure | SerialPortOutputStreamCloseFailure e) {
			// TODO Auto-generated catch block
			System.err.println("Data Send Failed.");
		}
	}

	private ActionListener OpenPortListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String Text = ((JButton)e.getSource()).getText();
			if(Text.equals("OPEN")) {
				String PortName = MainCP.getUartPortName();
				if(PortName != null) {
					UartTool.setBaudrate(MainCP.getUartBaudrate());
					try {
						UartTool.openPort(PortName);
						MainCP.setPortConfigPanelState(false);
						MainCP.setDebugInfo(PortName + " Opened.");
					} catch (NoSuchPort | PortInUse | NotASerialPort e1) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, e1, "Failed!", JOptionPane.ERROR_MESSAGE);
					} catch (SerialPortParameterFailure e2) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, e2, "Failed!", JOptionPane.ERROR_MESSAGE);
						UartTool.closePort();
					} catch (TooManyListeners e3) {
						// TODO Auto-generated catch block
						System.err.println("UartTool: Failed to add Event Listenr!!!");
						UartTool.closePort();
					}
				} else {
					System.err.println("PORT NULL ERROR");
				}
			} else if(Text.equals("CLOSE")) {
				UartTool.closePort();
				MainCP.setPortConfigPanelState(true);
				MainCP.setDebugInfo("UART Closed.");
			}
		}
	};

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
	    		byte[] data = null;
				try {
					data = UartTool.readData();
					if (data == null || data.length < 1) {
						System.err.println("No Valid Data. (maybe closed.)");
					} else {
						try {
							decoder.push(data, data.length);
						} catch (InterruptedException e) {
							System.err.println("Failed to push data into decoder.");
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} catch (ReadDataFromSerialPortFailure | SerialPortInputStreamCloseFailure e) {
					// TODO Auto-generated catch block
					System.err.println("UartTool: Error while read data.");
				}
	    	break;
		}
	}

	private Runnable UartRefreshThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true) {
				if(UartTool != null) {
					MainCP.setPortNameList(UartTool.refreshPortList());
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					System.err.println("UART Refresh Thread SLEEP EXCEPTION.");
				}
			}
		}
	};

	private boolean GotResponseFlag = false;
	private Runnable SignalCheckThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int SignalLostCnt = 0;
			while(true) {
				if(GotResponseFlag == false) {
					if(SignalLostCnt < 10) {
						SignalLostCnt ++;
					} else {
						if(SignalLostCnt < 11) {
							SignalLostCnt ++;
							MainCP.indicateConnectionState(false);
							MainCP.setDebugInfo("Signal Lost.");
						}
					}
				} else {
					SignalLostCnt = 0;
					GotResponseFlag = false;
					MainCP.indicateConnectionState(true);
					MainCP.setDebugInfo("frame rate: " + decoder.frameRate());
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.err.println("SIGNAL CHECK Thread SLEEP EXCEPTION.");
				}
			}
		}
	};

	@Override
	public void badCRCEvent(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getNewPackage(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		GotResponseFlag = true;
	}

	@Override
	public void lenOverFlow(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	WindowAdapter MainWindowListener = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			// TODO Auto-generated method stub
			if(UartTool != null) {
				UartTool.closePort();
			}
			try {
				decoder.exit();
			} catch (InterruptedException e1) {
				System.err.println("Failed to exit decoder.");
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		@Override
		public void windowClosed(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
	};

	@Override
	public void InterfaceChanged(ChangeIfEvent e) {
		// TODO Auto-generated method stub
		String s = (String)e.getSource();
		if(s.equals("UART")) {
			MainCP.setPanelType(ConnectPanel.PanelTypeUART);
		} else if(s.equals("WIFI")) {
			if(UartTool != null) {
				UartTool.closePort();
			}
			MainCP.setPortConfigPanelState(true);
			MainCP.setPanelType(ConnectPanel.PanelTypeWIFI);
			MainCP.setDebugInfo("WIFI MODE UNSUPPORTED NOW.");
		}
	}

	public String getVersionString() { return kyFrameVersion; }

	public static void main(String[] args) {
		System.out.println("!!!TEST APP START!!!");
		kyMainFrame myFrame = new kyMainFrame();
		myFrame.setSize(600, 400);
//		myFrame.setResizable(false);
		myFrame.setTitle("kyChu.kyFrame.TESTAPP");
		myFrame.setVisible(true);
		System.out.println("!!!TEST APP ENDED!!!");
	}
}
