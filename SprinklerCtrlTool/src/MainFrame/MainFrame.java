package MainFrame;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.UIManager;

import kyLink.kyLinkPackage;
import kyLink.event.kyLinkDecodeEvent;
import kyLink.event.kyLinkDecodeEventListener;
import kyMainFrame.kyMainFrame;

public class MainFrame extends kyMainFrame implements kyLinkDecodeEventListener {
	private static final long serialVersionUID = 1L;

	private JPanel MainPanel = null;

	private ctrlPanel uiCtrlPanel = null;
	private encInfoPanel uiEncoderInfo = null;
	private staInfoPanel uiStatusInfo = null;

	private int encoder1, encoder2, error_flag;

	public MainFrame() {
		this.setTitle("kyChu.SprinklerCtrl Tool");
		this.setSize(900, 600);
		this.setResizable(false);
		MainPanel = this.getUsrMainPanel();
		
		initGUI();
		(new Thread(refreshTask)).start();
	}

	private void initGUI() {
		MainPanel.setLayout(new GridLayout(3, 1));
		uiEncoderInfo = new encInfoPanel();
		uiStatusInfo = new staInfoPanel();
		uiCtrlPanel = new ctrlPanel(this.getCommTool());
		MainPanel.add(uiStatusInfo);
		MainPanel.add(uiEncoderInfo);
		MainPanel.add(uiCtrlPanel);
	}

	private kyLinkPackage rxData = null;
	@Override
	public void getNewPackage(kyLinkDecodeEvent event) {
		// TODO Auto-generated method stub
		super.getNewPackage(event);
		rxData = (kyLinkPackage) event.getSource();
		if(rxData.msg_id == 0x60) {
			encoder1 = rxData.readoutUShort(8);
			encoder2 = rxData.readoutUShort(10);
			error_flag = rxData.readoutInteger(20);
		}
	}

	private Runnable refreshTask = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				uiEncoderInfo.setValue(encoder1, encoder2);
				uiStatusInfo.setErrorFlag(error_flag);
			}
		}
	};

	@Override
	public void badCRCEvent(kyLinkDecodeEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void lenOverFlow(kyLinkDecodeEvent event) {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Couldn't use system look and feel.");
		}
		(new MainFrame()).setVisible(true);
	}
}
