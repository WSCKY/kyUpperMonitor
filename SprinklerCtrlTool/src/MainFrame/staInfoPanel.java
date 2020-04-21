package MainFrame;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class staInfoPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JLabel infoLabel = null;

	public staInfoPanel() {
		infoLabel = new JLabel("<S><E>");
		infoLabel.setFont(new Font("Courier New", Font.BOLD, 18));
		this.add(infoLabel);
	}

	public void setErrorFlag(int error) {
		String error_str = "<S>";
		if((error & 0x00040000) != 0) {
			error_str += "<OUT3 ON> ";
		}
		if((error & 0x00080000) != 0) {
			error_str += "<OUT4 ON> ";
		}
		if((error & 0x01000000) != 0) {
			error_str += "<PIT CTRL LOST> ";
		}
		if((error & 0x02000000) != 0) {
			error_str += "<YAW CTRL LOST> ";
		}
		if((error & 0x04000000) != 0) {
			error_str += "<E1 ERROR> ";
		}
		if((error & 0x08000000) != 0) {
			error_str += "<E2 ERROR> ";
		}
		if((error & 0x10000000) != 0) {
			error_str += "<PIT ADJ> ";
		}
		if((error & 0x20000000) != 0) {
			error_str += "<YAW ADJ> ";
		}
		error_str += "<E>";
		infoLabel.setText(error_str);
	}
}
