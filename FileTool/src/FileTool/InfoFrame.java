package FileTool;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class InfoFrame extends JPanel {
	private static final long serialVersionUID = 1L;

	private JTextArea InfoLabel = null;
	private JScrollPane scollPane = null;

	public InfoFrame() {
		InfoLabel = new JTextArea("START.\n");
		scollPane = new JScrollPane(InfoLabel);
		this.setLayout(new BorderLayout());
		this.add(scollPane, BorderLayout.CENTER);
//		InfoLabel.setEnabled(false);
		InfoLabel.setEditable(false);
		InfoLabel.setAutoscrolls(true);
	}

	public void log(String s) {
		InfoLabel.append(s);
	}
	public void logln(String s) {
		InfoLabel.append(s + "\n");
	}
}
