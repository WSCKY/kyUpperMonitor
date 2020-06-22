package MainFrame;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.UIManager;

import org.dom4j.DocumentException;

import kyLinkWaveTool.kyLinkWTPane;
import kyMainFrame.kyMainFrame;

public class MainFrame extends kyMainFrame {
	private static final long serialVersionUID = 1L;

	private JPanel MainPanel = null;
	private kyLinkWTPane wtPanel = null;

	private MainFrame(String path) {
		this.setTitle("kyChu.DataMonitor");
		this.setSize(1000, 800);
		MainPanel = this.getUsrMainPanel();
		MainPanel.setLayout(new BorderLayout());

		wtPanel = new kyLinkWTPane("Data Wave");
		if(path != null) {
			try {
				wtPanel.setConfigFile(path);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				System.err.println("invalid package descriptor file");
				System.exit(-1);
			}
		}
		this.addDecodeListener(wtPanel);
		MainPanel.add(wtPanel);
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Couldn't use system look and feel.");
		}
		String path = null;
		if(args.length > 0) path = args[0];
		(new MainFrame(path)).setVisible(true);
	}
}
