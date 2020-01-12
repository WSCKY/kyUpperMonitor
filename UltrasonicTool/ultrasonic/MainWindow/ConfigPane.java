package MainWindow;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import CommTool.CommTool;
import CommTool.exception.SendDataFailure;
import kyLink.kyLinkPackage;
import kyLink.event.kyLinkDecodeEvent;
import kyLink.event.kyLinkDecodeEventListener;

public class ConfigPane extends JPanel implements Runnable, kyLinkDecodeEventListener {
	private static final long serialVersionUID = 1L;

	private JPanel CtrlPanel = new JPanel();
	private JPanel BtnsPanel = new JPanel();
	private JButton CmdSendBtn = new JButton("发送配置");
	private JLabel DisLab = new JLabel("0.0cm");
	private JSlider ValueSlider = new JSlider(0, 127);
	Dictionary<Integer, Component> labelTable = new Hashtable<Integer, Component>();
	private JLabel SliderValLab = new JLabel("000");
	Dictionary<Integer, Component> AdjPercentTab = new Hashtable<Integer, Component>();
	private JSlider DeadbandSlider = new JSlider(0, 100);
	private JLabel DeadbandValLab = new JLabel("46000");
	private JCheckBox ASW_EN = new JCheckBox("固定增益");
	private JCheckBox SND_EN = new JCheckBox("发送使能");
	private JCheckBox AutoSND_EN = new JCheckBox("自动发送");

//	private kyMainFrame FatherFrame = null;
	private CommTool comTool = null;
	private kyLinkPackage txData = null;
	private kyLinkPackage rxData = null;
	private Semaphore semaphore = null;

	public ConfigPane(CommTool cTool) {
		this.comTool = cTool;
		CtrlPanel.setLayout(new GridLayout(4, 1, 0, 0));

		DisLab.setFont(new Font("宋体", Font.BOLD, 40));
		JPanel p3 = new JPanel(); p3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
		p3.add(DisLab); CtrlPanel.add(p3);

		ValueSlider.setPaintLabels(true);
		ValueSlider.setValue(0);
		labelTable.put(0, new JLabel("000"));
		labelTable.put(64, new JLabel("064"));
		labelTable.put(127, new JLabel("128"));
		ValueSlider.setLabelTable(labelTable);
		ValueSlider.addChangeListener(scl);
		ValueSlider.setCursor(new Cursor(Cursor.HAND_CURSOR));
		ValueSlider.setPreferredSize(new Dimension(500, 50));
//		ValueSlider.setBorder(BorderFactory.createLineBorder(Color.RED));
		SliderValLab.setFont(SliderValLab.getFont().deriveFont(Font.BOLD, 28));
//		SliderValLab.setBorder(BorderFactory.createLineBorder(Color.RED));
		JPanel p = new JPanel(); p.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 0));
		p.add(ValueSlider); p.add(SliderValLab);
		p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "动态增益", TitledBorder.LEFT, TitledBorder.TOP));
		CtrlPanel.add(p);
		DeadbandSlider.setPaintLabels(true); DeadbandSlider.setValue(50);
		AdjPercentTab.put(0, new JLabel("00000"));
		AdjPercentTab.put(50, new JLabel("46000"));
		AdjPercentTab.put(100, new JLabel("92000")); DeadbandSlider.setLabelTable(AdjPercentTab);
		DeadbandSlider.addChangeListener(scl);
		DeadbandSlider.setPreferredSize(new Dimension(470, 50));
		DeadbandValLab.setFont(DeadbandValLab.getFont().deriveFont(Font.BOLD, 28));
		JPanel p1 = new JPanel(); p1.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 0));
		p1.add(DeadbandSlider); p1.add(DeadbandValLab);
		p1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "死区时间", TitledBorder.LEFT, TitledBorder.TOP));
		CtrlPanel.add(p1);

		ASW_EN.setFont(new Font("宋体", Font.BOLD, 24)); ASW_EN.setSelected(false);
		SND_EN.setFont(new Font("宋体", Font.BOLD, 24)); SND_EN.setSelected(true);
		AutoSND_EN.setFont(new Font("宋体", Font.BOLD, 24)); AutoSND_EN.setSelected(false); AutoSND_EN.addChangeListener(asl);
		JPanel p2 = new JPanel(); p2.setLayout(new FlowLayout(FlowLayout.CENTER, 65, 35));
		p2.add(ASW_EN); p2.add(SND_EN); p2.add(AutoSND_EN);
		CtrlPanel.add(p2);

		CmdSendBtn.setPreferredSize(new Dimension(160, 40));
		CmdSendBtn.setFont(new Font("宋体", Font.BOLD, 20));
		CmdSendBtn.addActionListener(csl);
		CmdSendBtn.setEnabled(true);
		BtnsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 20));
		BtnsPanel.add(CmdSendBtn);

		this.setLayout(new BorderLayout(0, 0));
		this.add(CtrlPanel, BorderLayout.CENTER);
		this.add(BtnsPanel, BorderLayout.SOUTH);

		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			public void eventDispatched(AWTEvent event) {
				if(((KeyEvent)event).getID()==KeyEvent.KEY_PRESSED) {
//					System.out.println("Pressed: " + KeyEvent.getKeyText(((KeyEvent)event).getKeyCode()));
					switch(((KeyEvent)event).getKeyCode()) {
					case KeyEvent.VK_1:
						ASW_EN.setSelected(!ASW_EN.isSelected());
					break;
					case KeyEvent.VK_2:
						SND_EN.setSelected(!SND_EN.isSelected());
					break;
					case KeyEvent.VK_3:
						AutoSND_EN.setSelected(!AutoSND_EN.isSelected());
					break;
					case KeyEvent.VK_LEFT:
					case KeyEvent.VK_DOWN:
						if(!ValueSlider.hasFocus() && !DeadbandSlider.hasFocus()) {
							if(ValueSlider.getValue() > 0)
								ValueSlider.setValue(ValueSlider.getValue() - 1);
						}
					break;
					case KeyEvent.VK_RIGHT:
					case KeyEvent.VK_UP:
						if(!ValueSlider.hasFocus() && !DeadbandSlider.hasFocus()) {
							if(ValueSlider.getValue() < 127)
								ValueSlider.setValue(ValueSlider.getValue() + 1);
						}
					break;
					case KeyEvent.VK_A:
					case KeyEvent.VK_S:
						if(DeadbandSlider.getValue() > 0)
							DeadbandSlider.setValue(DeadbandSlider.getValue() - 1);
					break;
					case KeyEvent.VK_W:
					case KeyEvent.VK_D:
						if(DeadbandSlider.getValue() < 100)
							DeadbandSlider.setValue(DeadbandSlider.getValue() + 1);
					break;
					}
				}
			}
		}, AWTEvent.KEY_EVENT_MASK);

		txData = new kyLinkPackage();
		semaphore = new Semaphore(1, true);
		(new Thread(this)).start();
		new Thread(new TxDataThread()).start();
	}

	private ActionListener csl = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			TxConfigure();
		}
	};

	private ChangeListener scl = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			if((JSlider)e.getSource() == ValueSlider) {
				SliderValLab.setText(String.format("%03d", ValueSlider.getValue()));
			} else if((JSlider)e.getSource() == DeadbandSlider) {
				DeadbandValLab.setText(String.format("%d", DeadbandSlider.getValue() * 920));
			}
		}
	};

	private ChangeListener asl = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			if((JCheckBox)e.getSource() == AutoSND_EN) {
				if(AutoSND_EN.isSelected())
					CmdSendBtn.setEnabled(false);
				else
					CmdSendBtn.setEnabled(true);
			}
		}
	};

	private class TxDataThread implements Runnable {
		public void run() {
			while(true) {
				if(AutoSND_EN.isSelected()) {
					TxConfigure();
				}
				try {
					TimeUnit.MILLISECONDS.sleep(100);//100ms
				} catch (InterruptedException e) {
					System.err.println("Interrupted");
				}
			}
		}
	}

	private void TxConfigure() {
		txData.dev_id = (byte) 0x01;
		txData.msg_id = (byte) 0xE0;
		txData.length = (byte) 0x0B;
		txData.addByte((byte) ValueSlider.getValue(), 0);
		txData.addByte((byte) (ASW_EN.isSelected() ? 1 : 0), 1);
		txData.addByte((byte) (SND_EN.isSelected() ? 1 : 0), 2);
		txData.addInteger(DeadbandSlider.getValue() * 920, 3);
		txData.addInteger(0, 7);
		try {
			comTool.sendPackage(txData);
		} catch (SendDataFailure e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void badCRCEvent(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	private double dist = 0.0;
	@Override
	public void getNewPackage(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		rxData = (kyLinkPackage)arg0.getSource();
		if(rxData.msg_id == (byte)0x22) {
			dist = rxData.readoutUShort(0) / 10.0;
			semaphore.release();
		}
	}

	@Override
	public void lenOverFlow(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DisLab.setText(String.format("%.1fcm", dist));
		}
	}
}
