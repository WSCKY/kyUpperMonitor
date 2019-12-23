package NavBoardTool;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.dom4j.DocumentException;

import Calibration.CalibratePanel;
import Module3D.MyCube3D;
import kyLinkWaveTool.kyLinkWTPane;
import kyMainFrame.kyMainFrame;

public class MainFrame extends kyMainFrame implements ChangeListener {
	private static final long serialVersionUID = 1L;

	private JPanel MainPanel = null;
	private JTabbedPane MainTabPane = null;

	private MyCube3D cube = new MyCube3D();
	private kyLinkWTPane wtPanel = new kyLinkWTPane("Data Wave");
	private CalibratePanel cbPanel = new CalibratePanel();

	private MainFrame() {
		this.setTitle("kyChu.NavBoard Monitor");
		this.setSize(1000, 600);
		MainPanel = this.getUsrMainPanel();
		MainPanel.setLayout(new BorderLayout());
		MainTabPane = new JTabbedPane();
		MainTabPane.setFont(MainTabPane.getFont().deriveFont(Font.BOLD, 16));

		JPanel cubePanel = new JPanel();
		cubePanel.setLayout(new BorderLayout());
		cubePanel.add(cube, BorderLayout.CENTER);
		cubePanel.add(cube.infoPanel, BorderLayout.SOUTH);
		MainTabPane.addTab("Simulator", null, cubePanel, "3D View");
		MainTabPane.addTab("WaveTool", null, wtPanel, "Wave Tool");
		MainTabPane.addTab("Calibrate", null, cbPanel, "Calibration");

		MainTabPane.addChangeListener(this);

		try {
			wtPanel.setConfigFile("C:\\kyChu\\MyMonitor\\NavBoard\\cfgFile\\Navigation.pdesc");
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MainTabPane.setSelectedIndex(0);
		this.addDecodeListener(cube);
		this.addDecodeListener(wtPanel);
//		this.addDecodeListener(cbPanel);

		MainPanel.add(MainTabPane);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		try {
			this.removeAllDecodeListeners();
		} catch (InterruptedException e1) {
			System.err.println("Failed to remove all listeners.");
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		switch(MainTabPane.getSelectedIndex()) {
		case 0: this.addDecodeListener(cube); break;
		case 1: this.addDecodeListener(wtPanel); break;
		case 2: this.addDecodeListener(cbPanel); break;
		default: break;
		}
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
