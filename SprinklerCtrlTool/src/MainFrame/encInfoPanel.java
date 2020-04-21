package MainFrame;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class encInfoPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JLabel infoLabel = null;
	
	public encInfoPanel() {
		infoLabel = new JLabel("Encoder1: " + "00000" + "     Encoder2: " + "00000");
		infoLabel.setFont(new Font("Courier New", Font.BOLD, 18));
		this.add(infoLabel);
	}
	public void setValue(int e1, int e2) {
		float v1 = e1 * 0.02197265625f;
		float v2 = e2 * 0.02197265625f;
		if(v1 > 180) v1 -= 360;
		v1 = -v1;
		if(v2 > 180) v2 -= 360;
		v2 = -v2;
		infoLabel.setText(String.format("Encoder1: " + "%05d(%3.2f)" + "     Encoder2: " + "%05d(%3.2f)", e1, v1, e2, v2));
	}
}
