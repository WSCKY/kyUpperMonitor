package subPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import CommTool.CommTool;
import CommTool.exception.SendDataFailure;
import kyLink.kyLinkPackage;

public class CtrlMsgs extends JPanel {
	private static final long serialVersionUID = 1L;

	private uiCtrl ctrlPanel = null;
	private JButton sendBtn = null;

	kyLinkPackage txPack = null;
	private CommTool commTool = null;

	public CtrlMsgs(CommTool ctool) {
		this.commTool = ctool;
		this.setLayout(new BorderLayout());

		txPack = new kyLinkPackage();
		txPack.dev_id = (byte)0x02;
		txPack.msg_id = (byte)0xEE; // message id of subscribe message.
		txPack.length = 8;

		ctrlPanel = new uiCtrl();
		this.add(ctrlPanel, BorderLayout.CENTER);
		sendBtn = new JButton("Send");
		sendBtn.addActionListener(act);
		this.add(sendBtn, BorderLayout.SOUTH);
	}

	private ActionListener act = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			byte[] param = ctrlPanel.getParam();
			txPack.addByte(ctrlPanel.getId(), 0);
			txPack.addByte(ctrlPanel.getStat(), 1);
			txPack.addByte(ctrlPanel.getRate(), 2);
			txPack.addBytes(param, 5, 3);
//			System.out.println("id: " + ctrlPanel.getId() + ", en: " + ctrlPanel.getStat() + ", rate: " + ctrlPanel.getRate() + 
//					", param: " + param[0] + ", " + param[1] + ", " + param[2] + ", " + param[3] + ", " + param[4]);
			try {
				commTool.sendPackage(txPack);
			} catch (SendDataFailure e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
}

class uiCtrl extends JPanel {
	private static final long serialVersionUID = 1L;
	// msg id
	private JLabel labelMsgID = null;
	private JComboBox<String> selMsgID = null;
	// msg status
	private JLabel labelMsgSta = null;
	private JCheckBox selMsgEn = null;
	//msg rate
	private JLabel labelMsgRate = null;
	private JComboBox<Integer> selMsgRt = null;
	//msg parameter
	private JLabel labelMsgParam = null;
	private JTextField[] setMsgParam = null;
	
	public uiCtrl() {
		this.setLayout(new GridLayout(4, 1));

		labelMsgID = new JLabel("msg id: ");
		selMsgID = new JComboBox<String>();
		selMsgID.setPreferredSize(new Dimension(90, 30));
		selMsgID.setFont(selMsgID.getFont().deriveFont(Font.BOLD, 14));
		for(int i = 0; i < 0xFF; i ++) {
			String hex = Integer.toHexString(i & 0xFF).toUpperCase();
			if(hex.length() < 2) {
				hex = "0x0" + hex;
			} else {
				hex = "0x" + hex;
			}
			selMsgID.addItem(hex);
		}
		
		labelMsgSta = new JLabel("msg status: ");
		selMsgEn = new JCheckBox();
		
		labelMsgRate = new JLabel("msg rate: ");
		selMsgRt = new JComboBox<Integer>();
		selMsgRt.setPreferredSize(new Dimension(90, 30));
		selMsgRt.setFont(selMsgID.getFont().deriveFont(Font.BOLD, 14));
		selMsgRt.addItem(0);
		selMsgRt.addItem(1);
		selMsgRt.addItem(5);
		for(int i = 10; i <= 200; i += 10) {
			selMsgRt.addItem(i);
		}

		JPanel idPanel = new JPanel();
		idPanel.add(labelMsgID);
		idPanel.add(selMsgID);
		JPanel staPanel = new JPanel();
		staPanel.add(labelMsgSta);
		staPanel.add(selMsgEn);
		JPanel ratePanel = new JPanel();
		ratePanel.add(labelMsgRate);
		ratePanel.add(selMsgRt);
		ratePanel.add(new JLabel("Hz"));

		JPanel paramPanel = new JPanel();
		labelMsgParam = new JLabel("msg param: ");
		paramPanel.add(labelMsgParam);
		setMsgParam = new JTextField[5];
		for(int i = 0; i < 5; i ++) {
			setMsgParam[i] = new JTextField("0");
			setMsgParam[i].setPreferredSize(new Dimension(50, 30));
			setMsgParam[i].setFont(new Font("Courier New", Font.BOLD, 18));
			setMsgParam[i].setHorizontalAlignment(JTextField.CENTER);
			paramPanel.add(setMsgParam[i]);
		}
		paramPanel.add(new JLabel("(HEX)"));
		
		this.add(idPanel);
		this.add(staPanel);
		this.add(ratePanel);
		this.add(paramPanel);
	}

	public byte getId() {
		String hex = (String)selMsgID.getSelectedItem();
		return (byte)(Integer.parseInt(hex.substring(2), 16) & 0xFF);
	}
	public byte getStat() {
		if(selMsgEn.isSelected())
			return 0x01;
		else
			return 0x00;
	}
	public byte getRate() {
		return ((Integer)selMsgRt.getSelectedItem()).byteValue();
	}
	public byte[] getParam() {
		byte[] params = new byte[5];
		for(int i = 0; i < 5; i ++) {
			String val = setMsgParam[i].getText();
			if(!val.equals("")) {
				params[i] = (byte)(Integer.parseInt(val, 16) & 0xFF);
			} else {
				params[i] = 0;
			}
		}
		return params;
	}
}
