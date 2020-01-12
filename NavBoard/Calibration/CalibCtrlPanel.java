package Calibration;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;

import Calibration.Event.CtrlEvent;
import Calibration.Event.CtrlEventListener;

public class CalibCtrlPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public static final int MAX_SUPPORT_SENSORS = 3;
	private static final float DEFAULT_RADIUS = 105;

	private JButton startBtn = null;
	private JButton stopButton = null;
	private JButton saveButton = null;

	private JCheckBox ck_org = null;
	private JCheckBox ck_cal = null;
	private JComboBox<Integer> cbSelShow = null;

//	private JLabel idLabel = null;
	private JLabel rateLabel = null;
//	private JLabel sampleLabel = null;

	private JButton pathButton = null;
	private JTextField pathLabel = null;

	private JComboBox<Integer> cbSelNbr = null;

	private CtrlEventListener listener = null;

	public CalibCtrlPanel(CtrlEventListener lis) {
		listener = lis;
		this.setLayout(new GridLayout(5, 1));

//		idLabel = new JLabel("sensor id: " + SensorID);
		rateLabel = new JLabel("sample rate: 10Hz");
//		sampleLabel = new JLabel("samples: " + sample_counter + "/" + ellipCalibration.SAMPLE_NUMBER);
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridLayout(3, 1));
//		infoPanel.add(idLabel);
		infoPanel.add(rateLabel);
//		infoPanel.add(sampleLabel);

		cbSelNbr = new JComboBox<Integer>();
		cbSelNbr.setPreferredSize(new Dimension(90, 30));
		cbSelNbr.setFont(cbSelNbr.getFont().deriveFont(Font.BOLD, 14));
		for(int i = 0; i < MAX_SUPPORT_SENSORS; i ++) {
			cbSelNbr.addItem(i + 1);
		}
		cbSelNbr.addActionListener(numSensorAct);
		JLabel numStrLabel = new JLabel("Sensor Number: ");
		numStrLabel.setFont(new Font("Courier NEW", Font.PLAIN, 16));
		JPanel nbrPanel = new JPanel();
		nbrPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 30));
		nbrPanel.add(numStrLabel);
		nbrPanel.add(cbSelNbr);

		startBtn = new JButton("START");
		stopButton = new JButton("STOP");
		saveButton = new JButton("SAVE");
		startBtn.setFont(new Font("Courier NEW", Font.BOLD, 24));
		startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				CtrlEvent event = new CtrlEvent(e.getSource(), CtrlEvent.CTRL_EVENT_START);
				listener.UserCtrlCommand(event);
			}
		});
		stopButton.setFont(new Font("Courier NEW", Font.BOLD, 24));
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CtrlEvent event = new CtrlEvent(e.getSource(), CtrlEvent.CTRL_EVENT_STOP);
				listener.UserCtrlCommand(event);
			}
		});
		saveButton.setFont(new Font("Courier NEW", Font.BOLD, 24));
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CtrlEvent event = new CtrlEvent(e.getSource(), CtrlEvent.CTRL_EVENT_SAVE);
				listener.UserCtrlCommand(event);
			}
		});
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 30));
		btnPanel.add(startBtn);
		btnPanel.add(stopButton);
		btnPanel.add(saveButton);

		ck_org = new JCheckBox("ORG", true);
		ck_cal = new JCheckBox("CAL", false);
		ck_org.setFont(new Font("Courier NEW", Font.BOLD, 24));
		ck_cal.setFont(new Font("Courier NEW", Font.BOLD, 24));
		cbSelShow = new JComboBox<Integer>();
		cbSelShow.setPreferredSize(new Dimension(90, 30));
		cbSelShow.setFont(cbSelShow.getFont().deriveFont(Font.BOLD, 14));
		cbSelShow.addItem(0);
		ck_org.addActionListener(refreshAct);
		ck_cal.addActionListener(refreshAct);
		cbSelShow.addActionListener(refreshAct);
		JPanel showPanel = new JPanel();
		showPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 30));
		showPanel.add(cbSelShow);
		showPanel.add(ck_org);
		showPanel.add(ck_cal);

		pathButton = new JButton("CHOOSE DIR");
		pathButton.addActionListener(selPathAct);
		pathLabel = new JTextField();
		pathLabel.setFont(new Font("Courier NEW", Font.PLAIN, 16));
		pathLabel.setColumns(28);
		pathLabel.setText(System.getProperty("user.dir"));
		JPanel pathPanel = new JPanel();
		pathPanel.setLayout(new GridLayout(2, 1));
		pathPanel.add(pathLabel);
		pathPanel.add(pathButton);

		this.add(nbrPanel);
		this.add(btnPanel);
		this.add(showPanel);
		this.add(infoPanel);
		this.add(pathPanel);
	}

	private ActionListener refreshAct = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			CtrlEvent event = new CtrlEvent(e.getSource(), CtrlEvent.CTRL_EVENT_REFRESH);
			listener.UserCtrlCommand(event);
		}
	};
	
	private ActionListener numSensorAct = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			int num = (int)cbSelNbr.getSelectedItem();
			cbSelShow.removeAllItems();
			for(int i = 0; i < num; i ++) {
				cbSelShow.addItem(i);
			}
		}
	};

	private ActionListener selPathAct = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			JFileChooser chooser = new JFileChooser();
			FileSystemView fsv = FileSystemView.getFileSystemView();
			System.out.println(fsv.getHomeDirectory());
			chooser.setCurrentDirectory(fsv.getHomeDirectory());
			chooser.setDialogTitle("select save path");
			chooser.setApproveButtonText("OK");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				pathLabel.setText(chooser.getSelectedFile().getPath());
			}
		}
	};

	public void operationForbidden() {
		cbSelNbr.setEnabled(false);
		startBtn.setEnabled(false);
		saveButton.setEnabled(false);
		ck_org.setEnabled(false);
		ck_cal.setEnabled(false);
		cbSelShow.setEnabled(false);
	}

	public void operationAllowed() {
		cbSelNbr.setEnabled(true);
		startBtn.setEnabled(true);
		saveButton.setEnabled(true);
		ck_org.setEnabled(true);
		ck_cal.setEnabled(true);
		cbSelShow.setEnabled(true);
	}

	public int getSensorNumber() {
		return (int) cbSelNbr.getSelectedItem();
	}
	public float getSensorValueRadius() {
		return DEFAULT_RADIUS;
	}
	public int getSelectedSensorID() {
		return (int) cbSelShow.getSelectedItem();
	}

	public boolean getOrgDataSelected() {
		return ck_org.isSelected();
	}
	public boolean getCalDataSelected() {
		return ck_cal.isSelected();
	}

	public String getCalDataSavePath() {
		return pathLabel.getText();
	}
}
