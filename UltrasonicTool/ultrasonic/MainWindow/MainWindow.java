package MainWindow;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import kyLinkWaveTool.kyLinkWTPane;
import kyMainFrame.kyMainFrame;

public class MainWindow extends kyMainFrame {
	private static final long serialVersionUID = 1L;

	private JPanel MainPanel = null;
	private JTabbedPane MainTabPane = null;

	public MainWindow() {
		this.setTitle("kyChu.Ultrasonic Toolkit");
		MainPanel = this.getUsrMainPanel();
		MainPanel.setLayout(new BorderLayout());
		MainTabPane = new JTabbedPane();
		MainTabPane.setFont(MainTabPane.getFont().deriveFont(Font.BOLD, 16));

		SimulatorPane sp = new SimulatorPane();
		MainTabPane.addTab("Simulator", null, sp, "Simulator");
		this.addDecodeListener(sp);

		kyLinkWTPane wtPanel = new kyLinkWTPane("Ultrasonic");
		MainTabPane.addTab("WaveTool", null, wtPanel, "Wave Tool");
		this.addDecodeListener(wtPanel);

		ConfigPane cfgPanel = new ConfigPane(this.getCommTool());
		MainTabPane.addTab("Settings", null, cfgPanel, "Configure");
		this.addDecodeListener(cfgPanel);

		MainPanel.add(MainTabPane, BorderLayout.CENTER);
	}

	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }
		(new MainWindow()).setVisible(true);
	}
}
