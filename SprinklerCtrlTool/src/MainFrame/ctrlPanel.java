package MainFrame;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import CommTool.CommTool;
import CommTool.exception.SendDataFailure;
import kyLink.kyLinkPackage;

public class ctrlPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private kyLinkPackage txData = null;
	private CommTool commTool = null;

	private JCheckBox cbOUT3 = null;
	private JCheckBox cbOUT4 = null;

	private JTextField txtPitch = null;
	private JTextField txtYaw = null;

	private JButton updateBtn = null;
	private JButton originBtn = null;
	private JButton forgetBtn = null;

	public ctrlPanel(CommTool tool) {
		this.commTool = tool;
		txData = new kyLinkPackage();
	
		this.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 20));

		cbOUT3 = new JCheckBox("OUT3");
		cbOUT3.setFont(new Font("Courier NEW", Font.BOLD, 24));

		cbOUT4 = new JCheckBox("OUT4");
		cbOUT4.setFont(new Font("Courier NEW", Font.BOLD, 24));

		JPanel panelPitch = new JPanel();
		JLabel labelPitch = new JLabel("PITCH: ");
		labelPitch.setFont(new Font("Courier NEW", Font.BOLD, 24));
		txtPitch = new JTextField("0");
		txtPitch.setPreferredSize(new Dimension(150, 30));
		txtPitch.setFont(new Font("Courier New", Font.BOLD, 18));
		txtPitch.setToolTipText("set pitch");
		txtPitch.setHorizontalAlignment(JTextField.CENTER);
		panelPitch.add(labelPitch);
		panelPitch.add(txtPitch);

		JPanel panelYaw = new JPanel();
		JLabel labelYaw = new JLabel("YAW: ");
		labelYaw.setFont(new Font("Courier NEW", Font.BOLD, 24));
		txtYaw = new JTextField("0");
		txtYaw.setPreferredSize(new Dimension(150, 30));
		txtYaw.setFont(new Font("Courier New", Font.BOLD, 18));
		txtYaw.setToolTipText("set yaw");
		txtYaw.setHorizontalAlignment(JTextField.CENTER);
		panelYaw.add(labelYaw);
		panelYaw.add(txtYaw);

		updateBtn = new JButton("update");
		updateBtn.setPreferredSize(new Dimension(140, 30));
		updateBtn.setFont(new Font("FreeMono", Font.BOLD, 18));
		updateBtn.setToolTipText("send configuration");
		updateBtn.addActionListener(updateBtnListener);

		originBtn = new JButton("set origin");
		originBtn.setPreferredSize(new Dimension(140, 30));
		originBtn.setFont(new Font("FreeMono", Font.BOLD, 18));
		originBtn.setToolTipText("set origin");
		originBtn.addActionListener(originBtnListener);

		forgetBtn = new JButton("forget origin");
		forgetBtn.setPreferredSize(new Dimension(140, 30));
		forgetBtn.setFont(new Font("FreeMono", Font.BOLD, 18));
		forgetBtn.setToolTipText("forget origin");
		forgetBtn.addActionListener(forgetBtnListener);

		this.add(cbOUT3);
		this.add(cbOUT4);
		this.add(panelPitch);
		this.add(panelYaw);
		this.add(updateBtn);
		this.add(originBtn);
		this.add(forgetBtn);
	}

	private ActionListener updateBtnListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			float exp_pitch;
			exp_pitch = Float.parseFloat(txtPitch.getText());
			float exp_yaw;
			exp_yaw = Float.parseFloat(txtYaw.getText());
			int flag = 0;
			if(cbOUT3.isSelected()) flag |= 0x00040000;
			if(cbOUT4.isSelected()) flag |= 0x00080000;

			txData.msg_id = (byte) 0x61;
			txData.dev_id = (byte) 0x02;
			txData.length = 13;
			txData.addByte((byte) 1, 0);
			txData.addFloat(exp_pitch, 1);
			txData.addFloat(exp_yaw, 5);
			txData.addInteger(flag, 9);
			try {
				commTool.sendPackage(txData);
			} catch (SendDataFailure e) {
				// TODO Auto-generated catch block
				System.err.println("send update failed!");
				e.printStackTrace();
			}
		}
	};

	private ActionListener originBtnListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			txData.msg_id = (byte) 0xCC;
			txData.dev_id = (byte) 0x02;
			txData.length = 1;
			txData.addByte((byte) 0x23, 0);
			try {
				commTool.sendPackage(txData);
			} catch (SendDataFailure e) {
				// TODO Auto-generated catch block
				System.err.println("reset origin failed!");
				e.printStackTrace();
			}
		}
	};

	private ActionListener forgetBtnListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			txData.msg_id = (byte) 0xCC;
			txData.dev_id = (byte) 0x02;
			txData.length = 1;
			txData.addByte((byte) 0x45, 0);
			try {
				commTool.sendPackage(txData);
			} catch (SendDataFailure e) {
				// TODO Auto-generated catch block
				System.err.println("reset origin failed!");
				e.printStackTrace();
			}
		}
	};
}
