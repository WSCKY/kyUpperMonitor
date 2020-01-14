package CompassInfo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.concurrent.Semaphore;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.vecmath.Vector3f;

import kyLink.kyLinkPackage;
import kyLink.event.kyLinkDecodeEvent;
import kyLink.event.kyLinkDecodeEventListener;
import kyMainFrame.kyMainFrame;

public class CompassInfo extends JPanel implements Runnable, kyLinkDecodeEventListener {
	private static final long serialVersionUID = 1L;

	private Vector3f[] MagData = null;
	private Semaphore semaMutex = null;
	private Semaphore semaUpdate = null;

	private JPanel infoPanel = null;
	private JLabel infoLabel = null;
	public CompassInfo() {
		MagData = new Vector3f[2];
		MagData[0] = new Vector3f();
		MagData[1] = new Vector3f();
		semaMutex = new Semaphore(1);
		semaUpdate = new Semaphore(0);

		infoLabel = new JLabel("deflection: 0");
		infoPanel = new JPanel();
		infoPanel.setBackground(new Color(100, 255, 100));
		infoLabel.setFont(new Font("Courier NEW", Font.BOLD, 20));
		infoPanel.add(infoLabel);
		this.setLayout(new BorderLayout());
		this.add(new JPanel(), BorderLayout.CENTER);
		this.add(infoPanel, BorderLayout.SOUTH);

		(new Thread(this)).start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			try {
				semaUpdate.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				semaMutex.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			float normalize = (float) (1.0 / Math.sqrt(MagData[0].x * MagData[0].x + MagData[0].y * MagData[0].y + MagData[0].z * MagData[0].z));
			MagData[0].x *= normalize;
			MagData[0].y *= normalize;
			MagData[0].z *= normalize;
			normalize = (float) (1.0 / Math.sqrt(MagData[1].x * MagData[1].x + MagData[1].y * MagData[1].y + MagData[1].z * MagData[1].z));
			MagData[1].x *= normalize;
			MagData[1].y *= normalize;
			MagData[1].z *= normalize;
			double alpha = Math.toDegrees(Math.acos(MagData[0].x * MagData[1].x + MagData[0].y * MagData[1].y + MagData[0].z * MagData[1].z));
			semaMutex.release();
			infoLabel.setText("deflection: " + alpha);
			if(alpha < 10) {
				infoPanel.setBackground(new Color(100, 255, 100));
			} else if(alpha < 20) {
				infoPanel.setBackground(new Color(255, 255, 100));
			} else {
				infoPanel.setBackground(new Color(255, 100, 100));
			}
		}
	}

	@Override
	public void badCRCEvent(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getNewPackage(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		kyLinkPackage rxData = (kyLinkPackage)arg0.getSource();
		if(rxData.msg_id == (byte)0xC5) {
			if(rxData.length < 24) {
				System.err.println("length error");
			} else {
				try {
					semaMutex.acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				MagData[0].x = rxData.readoutFloat(0);
				MagData[0].y = rxData.readoutFloat(4);
				MagData[0].z = rxData.readoutFloat(8);
				MagData[1].x = rxData.readoutFloat(12);
				MagData[1].y = rxData.readoutFloat(16);
				MagData[1].z = rxData.readoutFloat(20);
				semaMutex.release();
				semaUpdate.release();
			}
		}
	}

	@Override
	public void lenOverFlow(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Couldn't use system look and feel.");
		}
		kyMainFrame mf = new kyMainFrame();
		mf.setTitle("Compass INFO TEST FRAME");
		mf.setSize(600, 400);
		CompassInfo cp = new CompassInfo();
		JPanel MainPanel = mf.getUsrMainPanel();
		MainPanel.setLayout(new BorderLayout());
		MainPanel.add(cp);
		mf.addDecodeListener(cp);
		mf.setVisible(true);
	}
}
