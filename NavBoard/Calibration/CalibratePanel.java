package Calibration;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import Calibration.Viewer3D.Viewer3D;
import Calibration.Viewer3D.Component.Dot3D;
import Calibration.ViewerChart.ViewerChart;
import Calibration.ViewerChart.Component.Dot2D;
import kyLink.kyLinkPackage;
import kyLink.event.kyLinkDecodeEvent;
import kyLink.event.kyLinkDecodeEventListener;

public class CalibratePanel extends JSplitPane implements Runnable, kyLinkDecodeEventListener {
	private static final long serialVersionUID = 1L;
	
	private static final float DEFAULT_RADIUS = 105;

	private Viewer3D View3D = null;
	private ViewerChart xyChart = null;
	private ViewerChart xzChart = null;
	private ViewerChart yzChart = null;
// UI objects
	JButton startBtn = null;
	JButton stopButton = null;
	JCheckBox ck_org = null;
	JCheckBox ck_cal = null;
	private JLabel idLabel = null;
	private JLabel rateLabel = null;
	private JLabel sampleLabel = null;
//
	private int SensorID = -1;
	private Vector3f orgVal = null;
	private Semaphore valMutex = null;
	private Semaphore valUpdate = null;

	private boolean _sample_enable = false;
	private boolean _calibrated_flag = false;
	private int sample_counter = 0;
	private ellipCalibration calibrater = null;

	private Vector3f[] orgVal_save = null;
	private Vector3f[] calVal_save = null;
	private int org_dot_id = 0, cal_dot_id = 0;
	public CalibratePanel() {
		super(JSplitPane.HORIZONTAL_SPLIT);

		orgVal = new Vector3f();
		orgVal_save = new Vector3f[ellipCalibration.SAMPLE_NUMBER];
		calVal_save = new Vector3f[ellipCalibration.SAMPLE_NUMBER];
		for(int i = 0; i < ellipCalibration.SAMPLE_NUMBER; i ++) {
			orgVal_save[i] = new Vector3f();
			calVal_save[i] = new Vector3f();
		}
		
		valMutex = new Semaphore(1);
		valUpdate = new Semaphore(0);
		
		calibrater = new ellipCalibration();

		View3D = new Viewer3D();
		org_dot_id = View3D.addDotBranch();
		cal_dot_id = View3D.addDotBranch();
		xyChart = new ViewerChart("x", "y", Color.RED, Color.GREEN);
		yzChart = new ViewerChart("y", "z", Color.GREEN, Color.BLUE);
		xzChart = new ViewerChart("x", "z", Color.RED, Color.BLUE);

		JPanel ShowSP = new JPanel();
		ShowSP.setLayout(new GridLayout(2, 2, 0, 0));
		ShowSP.add(View3D); ShowSP.add(xyChart); ShowSP.add(yzChart); ShowSP.add(xzChart);

		this.setRightComponent(RightPanel());
		this.setLeftComponent(ShowSP);
		this.setDividerLocation(0.5);
		
		this.setEnabled(false);
		this.addComponentListener(compLis);
		
		(new Thread(this)).start();
	}

	private JPanel RightPanel() {
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new GridLayout(3, 1));

		startBtn = new JButton("START");
		stopButton = new JButton("STOP");
		ck_org = new JCheckBox("ORG", true);
		ck_cal = new JCheckBox("CAL", false);

		idLabel = new JLabel("sensor id: " + SensorID);
		rateLabel = new JLabel("sample rate: 10Hz");
		sampleLabel = new JLabel("samples: " + sample_counter + "/" + ellipCalibration.SAMPLE_NUMBER);
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridLayout(3, 1));
		infoPanel.add(idLabel);
		infoPanel.add(rateLabel);
		infoPanel.add(sampleLabel);

		startBtn.setFont(new Font("Courier NEW", Font.BOLD, 24));
		startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(_sample_enable == false) {
					_sample_enable = true;
					_calibrated_flag = false;
					sample_counter = 0; // reset sample counter.
					View3D.removeAllDot(org_dot_id);
					View3D.removeAllDot(cal_dot_id);
					startBtn.setEnabled(false);
					System.out.println("sample start");
				}
			}
		});

		stopButton.setFont(new Font("Courier NEW", Font.BOLD, 24));
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(_sample_enable == true) {
					_sample_enable = false;
					startBtn.setEnabled(true);
				}
			}
		});
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new GridLayout(2, 1));
		btnPanel.add(startBtn);
		btnPanel.add(stopButton);

		ck_org.setFont(new Font("Courier NEW", Font.BOLD, 24));
		ck_org.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(_calibrated_flag == true) {
						if(ck_org.isSelected()) {
							float x, y, z;
							for(int i = 0; i < ellipCalibration.SAMPLE_NUMBER; i ++) {
								x = orgVal_save[i].x / DEFAULT_RADIUS;
								y = orgVal_save[i].y / DEFAULT_RADIUS;
								z = orgVal_save[i].z / DEFAULT_RADIUS;
								View3D.addDot3DTo(new Dot3D(x, y, z, new Color3f(1, 0, 0)), org_dot_id);
							}
						} else {
							View3D.removeAllDot(org_dot_id);
						}
				} else ck_org.setSelected(true);
			}
		});

		ck_cal.setFont(new Font("Courier NEW", Font.BOLD, 24));
		ck_cal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(_calibrated_flag == true) {
						if(ck_cal.isSelected()) {
							float x, y, z;
							for(int i = 0; i < ellipCalibration.SAMPLE_NUMBER; i ++) {
								x = calVal_save[i].x / DEFAULT_RADIUS;
								y = calVal_save[i].y / DEFAULT_RADIUS;
								z = calVal_save[i].z / DEFAULT_RADIUS;
								View3D.addDot3DTo(new Dot3D(x, y, z, new Color3f(0, 0, 1)), cal_dot_id);
							}
						} else {
							View3D.removeAllDot(cal_dot_id);
						}
				} else ck_cal.setSelected(false);
			}
		});
		JPanel cbPanel = new JPanel();
		cbPanel.setLayout(new GridLayout(2, 1));
		cbPanel.add(ck_org);
		cbPanel.add(ck_cal);

		rightPanel.add(btnPanel);
		rightPanel.add(cbPanel);
		rightPanel.add(infoPanel);
		return rightPanel;
	}

	private void resetDividerLocation() {
		int local = this.getWidth() / 3;
		if(local > 320) local = 320;
		this.setDividerLocation(this.getWidth() - local);
		}
	private ComponentAdapter compLis = new ComponentAdapter() {
		public void componentResized(ComponentEvent e) {
			// TODO Auto-generated method stub
			resetDividerLocation();
		}
	};

	@Override
	public void run() {
		// TODO Auto-generated method stub
		float x, y, z;
		float[] m;
		while(true) {
			try {
				valUpdate.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(_sample_enable == true) {
				try {
					valMutex.acquire(); // mutex on, indicate that do not update.
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				calibrater.setSampleVal(sample_counter, orgVal.x, orgVal.y, orgVal.z);
				orgVal_save[sample_counter].x = orgVal.x;
				orgVal_save[sample_counter].y = orgVal.y;
				orgVal_save[sample_counter].z = orgVal.z;
				sample_counter ++;
				x = orgVal.x / DEFAULT_RADIUS;
				y = orgVal.y / DEFAULT_RADIUS;
				z = orgVal.z / DEFAULT_RADIUS;
				valMutex.release();
				View3D.addDot3DTo(new Dot3D(x, y, z, new Color3f(1, 0, 0)), org_dot_id);
				xyChart.addDot2D(new Dot2D(x, y, Color.WHITE));
				xzChart.addDot2D(new Dot2D(x, z, Color.WHITE));
				yzChart.addDot2D(new Dot2D(y, z, Color.WHITE));
				
				idLabel.setText("sensor id: " + SensorID);
				sampleLabel.setText("samples: " + sample_counter + "/" + ellipCalibration.SAMPLE_NUMBER);
				
				if(sample_counter == ellipCalibration.SAMPLE_NUMBER) {
					_sample_enable = false;
					startBtn.setEnabled(true);
					calibrater.computeRet(DEFAULT_RADIUS);
					m = calibrater.getCalibRet();
					for(int i = 0; i < ellipCalibration.SAMPLE_NUMBER; i ++) {
						calVal_save[i].x = (orgVal_save[i].x + m[0]) * m[4];
						calVal_save[i].y = (orgVal_save[i].y + m[1]) * m[5];
						calVal_save[i].z = (orgVal_save[i].z + m[2]) * m[6];
					}
					_calibrated_flag = true;
					System.out.println("calibrate done.");
				}
			}
		}
	}

	@Override
	public void getNewPackage(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		kyLinkPackage rxData = (kyLinkPackage)arg0.getSource();
		if(rxData.msg_id == (byte)0xC2 && _sample_enable) { // compass origin data.
			try {
				valMutex.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SensorID = rxData.rData[0];
			orgVal.x = rxData.readoutShort(1);
			orgVal.y = rxData.readoutShort(3);
			orgVal.z = rxData.readoutShort(5);
			valMutex.release();
			valUpdate.release();
		}
	}

	@Override
	public void badCRCEvent(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void lenOverFlow(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
	}

	public static void main(String[] args) {
		JFrame f = new JFrame("CalibPanel Test");
		f.setSize(1000, 800);
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		CalibratePanel cp = new CalibratePanel();
		f.add(cp);
		f.setVisible(true);
		(new Thread(cp)).start();
	}
}
