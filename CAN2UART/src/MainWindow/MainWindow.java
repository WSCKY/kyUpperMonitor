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
	
	private MainWindow() {
		this.setTitle("kyChu.NavBoard Monitor");
		this.setSize(1000, 600);
		MainPanel = this.getUsrMainPanel();
		MainPanel.setLayout(new BorderLayout());
		MainTabPane = new JTabbedPane();
		MainTabPane.setFont(MainTabPane.getFont().deriveFont(Font.BOLD, 16));

		NodeTable NodesManage = new NodeTable();
		MainTabPane.addTab("NodeTable", null, NodesManage, "Node Manager");
		kyLinkWTPane wtPanel = new kyLinkWTPane("Data Wave");
		MainTabPane.addTab("WaveTool", null, wtPanel, "Wave Tool");

		this.addDecodeListener(NodesManage);

		MainPanel.add(MainTabPane);
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
