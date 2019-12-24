package MainWindow;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.UIManager;

import kyLinkWaveTool.kyLinkWTPane;
import kyMainFrame.kyMainFrame;

public class MainWindow extends kyMainFrame {
	private static final long serialVersionUID = 1L;

	private JPanel MainPanel = null;

	public MainWindow() {
		this.setTitle("kyChu.kyLink Monitor");
		this.setSize(1300, 800);
		this.setLocationRelativeTo(null);
		MainPanel = this.getUsrMainPanel();
		MainPanel.setLayout(new BorderLayout());

		kyLinkWTPane wtPanel = new kyLinkWTPane("Data Wave");

		MainPanel.add(wtPanel, BorderLayout.CENTER);

		this.addDecodeListener(wtPanel);

		this.setResizable(false);
		this.setVisible(true);
	}

	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }
		new MainWindow();
	}
}
