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

import kyLink.kyLinkPackage;
import kyLink.decoder.kyLinkDecoder;
import kyLink.event.kyLinkDecodeEvent;
import kyLink.event.kyLinkDecodeEventListener;
import kyMainFrame.optEvent.ChangeIfEvent;
import kyMainFrame.optEvent.ChangeIfEventListener;
import kySerialTool.kySerialTool;
import kySerialTool.serialException.NoSuchPort;
import kySerialTool.serialException.PortOpenFailed;
import kySerialTool.serialException.ReadDataFromSerialPortFailure;
import kySerialTool.serialException.SendDataToSerialPortFailure;

public class kyMainFrame extends JFrame implements ChangeIfEventListener, kyLinkDecodeEventListener {
	private static final long serialVersionUID = 112233L;
	private static final int recv_cache_size = 1024;
	private static final String kyFrameVersion = "V0.9.9 kyChu@2019/12/20 16:50";

	private kySerialTool UartTool = null;
	private kyLinkDecoder decoder = null;

	private boolean _close_port_req = false;
	private boolean _should_exit = false;

	private Thread DataRecvTask = null;
	private Thread SignalCheckTask = null;

	private byte[] recv_cache = null;
	public kyMainFrame() {
		if(!System.getProperty("os.name").equals("Linux")) {
			System.err.println("this frame only suitable for Linux");
			System.exit(-1);
		}
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }
		initGUI();

		decoder = new kyLinkDecoder();
		decoder.addDecodeListener(this);

		UartTool = new kySerialTool();
		recv_cache = new byte[recv_cache_size];

		DataRecvTask = new Thread(DataRecvThread);
		SignalCheckTask = new Thread(SignalCheckThread);

		DataRecvTask.start();
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
		if(!UartTool.isOpened() || _close_port_req == true) return;
		byte[] txe = pack.getSendBuffer();
		try {
			UartTool.sendData(txe, txe.length);
		} catch (SendDataToSerialPortFailure e) {
			// TODO Auto-generated catch block
			System.err.println("Package Send Failed.");
		}
	}

	public void TxBuffer(byte[] data) {
		if(!UartTool.isOpened() || _close_port_req == true) return;
		try {
			UartTool.sendData(data, data.length);
		} catch (SendDataToSerialPortFailure e) {
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
						_close_port_req = false;
						UartTool.openPort(PortName);
						MainCP.setPortConfigPanelState(false);
						MainCP.setDebugInfo(PortName + " Opened.");
					} catch (NoSuchPort | PortOpenFailed e1) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, e1, "Failed!", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					System.err.println("PORT NULL ERROR");
				}
			} else if(Text.equals("CLOSE")) {
				_close_port_req = true;
			}
		}
	};

	private Runnable DataRecvThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int len;
			while(!_should_exit) {
				if(UartTool.isOpened()) {
					try {
						len = UartTool.readData(recv_cache, recv_cache_size);
						if(len > 0) {
							decoder.push(recv_cache, len);
						}
					} catch (ReadDataFromSerialPortFailure e) {
						// TODO Auto-generated catch block
						System.err.println("UartTool: Error while read data.");
					} catch (InterruptedException e) {
						System.err.println("Failed to push data into decoder.");
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(_close_port_req) {
						UartTool.closePort();
						_close_port_req = false;
						MainCP.setPortConfigPanelState(true);
						MainCP.setDebugInfo("UART Closed.");
					}
				} else {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						System.err.println("UART Refresh Thread SLEEP EXCEPTION.");
					}
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
			while(!_should_exit) {
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

				if(!UartTool.isOpened()) {
					MainCP.setPortNameList(UartTool.refreshPortList());
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
			_should_exit = true;
			try {
				DataRecvTask.join();
				SignalCheckTask.join();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			UartTool.closePort();
			UartTool = null;
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
			UartTool.closePort();
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
