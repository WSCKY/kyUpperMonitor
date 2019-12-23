package MainWindow;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.UIManager;

import kyMainFrame.kyMainFrame;

public class MainFrame extends kyMainFrame {
	private static final long serialVersionUID = 1L;

	private JPanel MainPanel = null;
	private TrailPanel trailPane = null;
	public MainFrame() {
		this.setTitle("kyChu.GNSS Position Toolkit");

		trailPane = new TrailPanel();

		MainPanel = this.getUsrMainPanel();
		MainPanel.setLayout(new BorderLayout());
		MainPanel.add(trailPane, BorderLayout.CENTER);

		this.addDecodeListener(trailPane);
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
