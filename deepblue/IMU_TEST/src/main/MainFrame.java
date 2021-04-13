package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import kyMainFrame.kyMainFrame;

public class MainFrame extends kyMainFrame implements Runnable {
	private static final long serialVersionUID = 1L;

	private dataProc imu_data = null;

	private JPanel MainPanel = null;
	private JLabel VersionLabel = null;
	private JLabel StateLabel = null;
	private JPanel PitchPanel = null;
	private JLabel PitchLabel = null;
	private JPanel RollPanel = null;
	private JLabel RollLabel = null;
	private JPanel RatePanel = null;
	private JLabel RateLabel = null;
	private JPanel YawPanel = null;
	private JLabel YawLabel = null;
	private MainFrame() {
		this.VersionLabel = new JLabel("Version:");
		this.VersionLabel.setPreferredSize(new Dimension(780, 32));
		this.VersionLabel.setFont(new Font("Courier New", Font.BOLD, 16));
		JPanel north_panel = new JPanel();
		north_panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 15));
		north_panel.add(this.VersionLabel);

		this.StateLabel = new JLabel();
		this.StateLabel.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("2.png")).getImage().getScaledInstance(256, 256, Image.SCALE_DEFAULT)));
		JPanel center_panel = new JPanel();
		center_panel.setLayout(new FlowLayout());
		center_panel.add(this.StateLabel);

		this.PitchPanel = new JPanel();
		this.PitchLabel = new JLabel("Pitch: 0.00");
		this.PitchLabel.setPreferredSize(new Dimension(190, 28));
		this.PitchLabel.setFont(new Font("Courier New", Font.BOLD, 22));
		this.PitchPanel.add(this.PitchLabel);
		this.RollPanel = new JPanel();
		this.RollLabel = new JLabel("Roll: 0.00");
		this.RollLabel.setPreferredSize(new Dimension(180, 28));
		this.RollLabel.setFont(new Font("Courier New", Font.BOLD, 22));
		this.RollPanel.add(this.RollLabel);
		this.YawPanel = new JPanel();
		this.YawLabel = new JLabel("Yaw: 0.00");
		this.YawLabel.setPreferredSize(new Dimension(180, 28));
		this.YawLabel.setFont(new Font("Courier New", Font.BOLD, 22));
		this.YawPanel.add(this.YawLabel);
		this.RatePanel = new JPanel();
		this.RateLabel = new JLabel("Rate: 0");
		this.RateLabel.setPreferredSize(new Dimension(150, 28));
		this.RateLabel.setFont(new Font("Courier New", Font.BOLD, 22));
		this.RatePanel.add(this.RateLabel);
		JPanel south_panel = new JPanel();
		south_panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		south_panel.add(this.PitchPanel);
		south_panel.add(this.RollPanel);
		south_panel.add(this.YawPanel);
		south_panel.add(this.RatePanel);

		this.setTitle("kyChu.Deepblue IMU Test Tool V1.0.0@20210413-20210901");
		this.setSize(780, 520);
		this.setResizable(false);
		MainPanel = this.getUsrMainPanel();
		MainPanel.setLayout(new BorderLayout());

		MainPanel.add(north_panel, BorderLayout.NORTH);
		MainPanel.add(center_panel, BorderLayout.CENTER);
		MainPanel.add(south_panel, BorderLayout.SOUTH);
		this.setIconImage(getToolkit().getImage(MainFrame.class.getResource("Tool.png")));
		this.setVisible(true);

		imu_data = new dataProc(this.getCommTool());
		this.addDecodeListener(imu_data);

		(new Thread(this)).start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		boolean state_changed = true, connected = false;
		while(true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Vector3f eur = quat2euler(new Quat4f(imu_data.qx, imu_data.qy, imu_data.qz, imu_data.qw));
			this.PitchLabel.setText(String.format("Pitch: " + "%3.2f", eur.x));
			this.RollLabel.setText(String.format("Roll: " + "%3.2f", eur.y));
			this.YawLabel.setText(String.format("Yaw: " + "%3.2f", eur.z));
			this.RateLabel.setText("Rate: " + imu_data.frameRate);
			this.VersionLabel.setText("Version: " + imu_data.version);
			if(imu_data.frameRate > 190) {
				if(connected == false) {
					connected = true;
					state_changed = true;
				}
			} else {
				if(connected == true) {
					connected = false;
					state_changed = true;
				}
			}
			if(state_changed) {
			if(connected) {
				this.StateLabel.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("1.png")).getImage().getScaledInstance(256, 256, Image.SCALE_DEFAULT)));
			} else {
				this.StateLabel.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("2.png")).getImage().getScaledInstance(256, 256, Image.SCALE_DEFAULT)));
			}
			state_changed = false;
			}
		}
 	}

	private final float RAD_TO_DEG = 57.295779513082320876798154814105f;
	private Vector3f quat2euler(Quat4f q) {
		Vector3f eur = new Vector3f();
		eur.x = (float) (Math.atan2(2 * (q.w * q.x + q.y * q.z) , 1 - 2 * (q.x * q.x + q.y * q.y)) * RAD_TO_DEG);
		eur.y = (float) (Math.asin(2 * (q.w * q.y - q.z * q.x)) * RAD_TO_DEG);
		eur.z = (float) (Math.atan2(2 * (q.w * q.z + q.x * q.y) , 1 - 2 * (q.y * q.y + q.z * q.z)) * RAD_TO_DEG);
		return eur;
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Couldn't use system look and feel.");
		}
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date Today = new Date();
		try {
			Date InvalidDay = df.parse("2021-9-1");
			if(Today.getTime() > InvalidDay.getTime()) {
//				System.err.println("System error.");
				JOptionPane.showMessageDialog(null, "Sorry, Exit With Unknow Error!", "error!", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new MainFrame();
	}
}
