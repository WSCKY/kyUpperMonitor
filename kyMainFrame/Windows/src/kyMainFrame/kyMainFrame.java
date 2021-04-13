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

import CommTool.CommTool;
import CommTool.exception.OpenPortFailure;
import CommTool.exception.ReadDataFailure;
import kyFileTool.kyFileTool;
import kyLink.decoder.kyLinkDecoder;
import kyLink.event.kyLinkDecodeEvent;
import kyLink.event.kyLinkDecodeEventListener;
import kyMainFrame.optEvent.ChangeIfEvent;
import kyMainFrame.optEvent.ChangeIfEventListener;
import kySerialTool.kySerialTool;
import kySocketTool.kySocketTool;

public class kyMainFrame extends JFrame implements ChangeIfEventListener, kyLinkDecodeEventListener {
	private static final long serialVersionUID = 112233L;
	private static final int recv_cache_size = 1024;
	private static final String kyFrameVersion = "V0.9.9 kyChu@2021/04/13 18:00";

	private enum IF_TYPE {
		IF_UART,
		IF_WIFI,
		IF_FILE,
	}

	private CommTool commTool = null;
	private kySerialTool UartTool = null;
	private kySocketTool SockTool = null;
	private kyFileTool   FileTool = null;
	private kyLinkDecoder decoder = null;

	private IF_TYPE ifType = IF_TYPE.IF_UART;

	private boolean _close_port_req = false;
	private boolean _should_exit = false;
	
	private boolean file_read_pause_flag = false;

	private Thread DataRecvTask = null;
	private Thread SignalCheckTask = null;

	private byte[] recv_cache = null;
	public kyMainFrame() {
/*		if(!System.getProperty("os.name").contains("Windows")) {
			System.err.println("this frame only suitable for Windows");
			System.exit(-1);
		}*/
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }
		initGUI();

		decoder = new kyLinkDecoder();
		decoder.addDecodeListener(this);

		UartTool = new kySerialTool();
		SockTool = new kySocketTool();
		FileTool = new kyFileTool();
		recv_cache = new byte[recv_cache_size];

		commTool = UartTool;

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
	private void initGUI() {
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
		MainCP.addFilePlayCtrlActionListener(FilePlayListener);
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

	public CommTool getCommTool() {
		return commTool;
	}

	private ActionListener OpenPortListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String Text = ((JButton)e.getSource()).getText();
			if(ifType == IF_TYPE.IF_UART) {
				if(Text.equals("OPEN")) {
					String PortName = MainCP.getUartPortName();
					if(PortName != null) {
						UartTool.setBaudrate(MainCP.getUartBaudrate());
						try {
							_close_port_req = false;
							UartTool.openPort(PortName);
							
						} catch (OpenPortFailure e1) {
							// TODO Auto-generated catch block
							System.err.println(e1.toString());
							JOptionPane.showMessageDialog(null, e1, "Failed!", JOptionPane.ERROR_MESSAGE);
							return;
						}
						file_read_pause_flag = false; // enable read
						MainCP.setPortConfigPanelState(false);
						MainCP.setDebugInfo(PortName + " Opened.");
					} else {
						System.err.println("PORT NULL ERROR");
					}
				} else if(Text.equals("CLOSE")) {
					_close_port_req = true;
					file_read_pause_flag = true;
				}
			} else if(ifType == IF_TYPE.IF_WIFI) {
				if(Text.equals("OPEN")) {
					if(!SockTool.isOpened()) {
						try {
							commTool.openPort(MainCP.getSocketPort());
						} catch (OpenPortFailure e1) {
							// TODO Auto-generated catch block
							System.err.println(e1.toString());
							JOptionPane.showMessageDialog(null, e1, "Failed!", JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
					file_read_pause_flag = false; // enable read
					MainCP.setPortConfigPanelState(false);
					MainCP.setDebugInfo("UDP connected to " + MainCP.getSocketPort() + ".");
				} else if(Text.equals("CLOSE")) {
					file_read_pause_flag = true;
					commTool.closePort();
					MainCP.setPortConfigPanelState(true);
					MainCP.setDebugInfo("Socket Closed.");
				}
			}
		}
	};

	private ActionListener FilePlayListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String Text = ((JButton)e.getSource()).getText();
			if(Text.equals("Run")) {
				if(!FileTool.isOpened()) {
					try {
						commTool.openPort(MainCP.getFilePortName());
					} catch (OpenPortFailure e1) {
						// TODO Auto-generated catch block
						System.err.println(e1.toString());
						JOptionPane.showMessageDialog(null, e1, "ERROR!", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				MainCP.setPortConfigPanelState(false);
				file_read_pause_flag = false;
				MainCP.setDebugInfo("File read start!");
			} else if(Text.equals("Pause")) {
				MainCP.setPortConfigPanelState(true);
				file_read_pause_flag = true;
				MainCP.setDebugInfo("File read paused!");
			} else if(Text.equals("Reset")) {
				commTool.closePort();
				file_read_pause_flag = true;
				MainCP.setPortConfigPanelState(true);
				MainCP.setDebugInfo("File read stopped!");
			}
		}
	};

	private Runnable DataRecvThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int len;
			while(!_should_exit) {
				if(!commTool.isOpened() || (file_read_pause_flag)) {
					if(_close_port_req) {
						UartTool.closePort();
						_close_port_req = false;
						MainCP.setPortConfigPanelState(true);
						MainCP.setDebugInfo("UART Closed.");
					}
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						System.err.println("UART Refresh Thread SLEEP EXCEPTION.");
					}
					continue;
				}

				try {
					len = commTool.readData(recv_cache, recv_cache_size);
					if(len > 0) {
						decoder.push(recv_cache, len);
					}
				} catch (ReadDataFailure e) {
					// TODO Auto-generated catch block
					System.err.println("commTool: Error while read data." + e.toString());
				} catch (InterruptedException e) {
					System.err.println("Failed to push data into decoder.");
					// TODO Auto-generated catch block
					e.printStackTrace();
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

				if(ifType == IF_TYPE.IF_UART) {
					if(!UartTool.isOpened()) {
						MainCP.setPortNameList(UartTool.refreshPortList());
					}
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
				System.err.println("Failed to exit decoder.");
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			UartTool.closePort();
			SockTool.closePort();
			FileTool.closePort();
			UartTool = null;
			SockTool = null;
			FileTool = null;
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
			commTool.closePort();
			commTool = UartTool;
			ifType = IF_TYPE.IF_UART;
			MainCP.setPanelType(ConnectPanel.PanelTypeUART);
			MainCP.setDebugInfo("receive data from UART.");
		} else if(s.equals("WIFI")) {
			commTool.closePort();
			commTool = SockTool;
			ifType = IF_TYPE.IF_WIFI;
			MainCP.setPanelType(ConnectPanel.PanelTypeWIFI);
			MainCP.setDebugInfo("UDP port selected.");
		} else if(s.equals("FILE")) {
			commTool.closePort();
			commTool = FileTool;
			ifType = IF_TYPE.IF_FILE;
			MainCP.setPanelType(ConnectPanel.PanelTypeFILE);
			MainCP.setDebugInfo("choose a binary file.");
		}
	}

	public String getVersionString() { return kyFrameVersion; }

	public static void main(String[] args) {
		System.out.println("!!!TEST APP START!!!");
		kyMainFrame myFrame = new kyMainFrame();
		myFrame.setSize(800, 600);
//		myFrame.setResizable(false);
		myFrame.setTitle("kyChu.kyFrame.TESTAPP");
		myFrame.setVisible(true);
		System.out.println("!!!TEST APP ENDED!!!");
	}
}
