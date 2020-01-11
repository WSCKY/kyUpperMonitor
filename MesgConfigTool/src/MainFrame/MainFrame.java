package MainFrame;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.UIManager;

import CommTool.CommTool;
import kyMainFrame.kyMainFrame;
import subPanel.CtrlMsgs;
import subPanel.ListMsgs;

public class MainFrame extends kyMainFrame {
	private static final long serialVersionUID = 1L;

	private JPanel MainPanel = null;
	private CommTool commTool = null;
	private ListMsgs listPanel = null;
	private CtrlMsgs ctrlPanel = null;
	public MainFrame() {
		this.setTitle("kyChu.kyLink Message Tool");
		this.setSize(1000, 600);
		commTool = this.getCommTool();
		MainPanel = this.getUsrMainPanel();
		MainPanel.setLayout(new GridLayout(1, 2));

		ctrlPanel = new CtrlMsgs(commTool);
		MainPanel.add(ctrlPanel);
		listPanel = new ListMsgs();
		this.addDecodeListener(listPanel);
		MainPanel.add(listPanel);
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
