package Calibration;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
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

	private Viewer3D View3D = null;
	private ViewerChart xyChart = null;
	private ViewerChart xzChart = null;
	private ViewerChart yzChart = null;

	private Vector3f gyrVal = null;
	private Vector3f calVal = null;
	private boolean _val_update_flag = false;
	private boolean _sample_enable = false;
	private boolean _calibrate_ready = true;
	private boolean _calibrated_flag = false;
	private boolean _rw_mutex = false;
//	private BlockingQueue<Vector3f> VectDataQueue = null;
	private int sample_counter = 0;
	private ellipCalibration calibrater = null;

	private Vector3f[] calVal_save = null;
	private int org_dot_id = 0, cal_dot_id = 0;
	public CalibratePanel() {
		super(JSplitPane.HORIZONTAL_SPLIT);

		gyrVal = new Vector3f();
		calVal = new Vector3f();
		calVal_save = new Vector3f[ellipCalibration.SAMPLE_NUMBER];
		for(int i = 0; i < ellipCalibration.SAMPLE_NUMBER; i ++) {
			calVal_save[i] = new Vector3f();
		}
//		VectDataQueue = new ArrayBlockingQueue<Vector3f>(5);
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

		initRightPanel();

		this.setRightComponent(rightPanel);
		this.setLeftComponent(ShowSP);
		this.setDividerLocation(0.5);
		
		this.setEnabled(false);
		this.addComponentListener(compLis);
		
		(new Thread(this)).start();
//		(new Timer()).scheduleAtFixedRate(RateStat, 0, 1000);
	}

	private JPanel rightPanel = null;
	private void initRightPanel() {
		rightPanel = new JPanel();
		JButton startBtn = new JButton("START");
		startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(_calibrate_ready == true) {
					if(_sample_enable == false) {
						_sample_enable = true;
						_calibrate_ready = false;
						sample_counter = 0;
						View3D.removeAllDot(org_dot_id);
						View3D.removeAllDot(cal_dot_id);
						System.out.println("sample start");
					}
				}
			}
		});
		JCheckBox ck_org = new JCheckBox("ORG", true);
		ck_org.setFont(new Font("Courier NEW", Font.BOLD, 32));
		ck_org.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(_calibrated_flag == true) {
					if(_calibrate_ready == true) {
						if(ck_org.isSelected()) {
							float x, y, z;
							for(int i = 0; i < ellipCalibration.SAMPLE_NUMBER; i ++) {
								x = calVal_save[i].x / 35;
								y = calVal_save[i].y / 35;
								z = calVal_save[i].z / 35;
								View3D.addDot3DTo(new Dot3D(x, y, z, new Color3f(1, 0, 0)), org_dot_id);
							}
						} else {
							View3D.removeAllDot(org_dot_id);
						}
					} else ck_org.setSelected(true);
				} else ck_org.setSelected(true);
			}
		});
		JCheckBox ck_cal = new JCheckBox("CAL", false);
		ck_cal.setFont(new Font("Courier NEW", Font.BOLD, 32));
		ck_cal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(_calibrated_flag == true) {
					if(_calibrate_ready == true) {
						if(ck_cal.isSelected()) {
							float[] m = calibrater.getCalibRet();
							float x, y, z;
							for(int i = 0; i < ellipCalibration.SAMPLE_NUMBER; i ++) {
								x = (calVal_save[i].x + m[0]) * m[4] / 35;
								y = (calVal_save[i].y + m[1]) * m[5] / 35;
								z = (calVal_save[i].z + m[2]) * m[6] / 35;
								View3D.addDot3DTo(new Dot3D(x, y, z, new Color3f(0, 0, 1)), cal_dot_id);
							}
						} else {
							View3D.removeAllDot(cal_dot_id);
						}
					} else ck_cal.setSelected(false);
				} else ck_cal.setSelected(false);
			}
		});
		rightPanel.add(startBtn);
		rightPanel.add(ck_org);
		rightPanel.add(ck_cal);
	}

	private void resetDividerLocation() {this.setDividerLocation(this.getWidth() - 360);}
	private ComponentAdapter compLis = new ComponentAdapter() {
		public void componentResized(ComponentEvent e) {
			// TODO Auto-generated method stub
			resetDividerLocation();
		}
	};

	@Override
	public void run() {
		// TODO Auto-generated method stub
//		Vector3f v = null;
		while(true) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(_sample_enable == true) {
				if(_val_update_flag == true) {
					_val_update_flag = false;
					_rw_mutex = true; // mutex on, indicate that do not update.
					calibrater.setSampleVal(sample_counter, calVal.x, calVal.y, calVal.z);
					calVal_save[sample_counter].x = calVal.x;
					calVal_save[sample_counter].y = calVal.y;
					calVal_save[sample_counter].z = calVal.z;
					sample_counter ++;
					float x = calVal.x / 35;
					float y = calVal.y / 35;
					float z = calVal.z / 35;
					_rw_mutex = false;
					View3D.addDot3DTo(new Dot3D(x, y, z, new Color3f(1, 0, 0)), org_dot_id);
					xyChart.addDot2D(new Dot2D(x, y, Color.WHITE));
					xzChart.addDot2D(new Dot2D(x, z, Color.WHITE));
					yzChart.addDot2D(new Dot2D(y, z, Color.WHITE));
					if(sample_counter == ellipCalibration.SAMPLE_NUMBER) {
						sample_counter = 0;
						calibrater.computeRet(35);
						_sample_enable = false;
						_calibrate_ready = true;
						_calibrated_flag = true;
						System.out.println("calibrate done.");
					}
				}
			}
//			try {
//				v = VectDataQueue.take();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			float z = v.z / 9.8f;
//			float x = v.x / 9.8f;
//			float y = v.y / 9.8f;
//			z += 1;

		}
	}

//	private int frameCnt = 0;
//	private TimerTask RateStat = new TimerTask() {
//		public void run() {
//			System.out.println("FrameRate = " + frameCnt);
//			frameCnt = 0;
//		}
//	};

	@Override
	public void badCRCEvent(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
	}
//private float max = 0, min = 0;
//	private long now = 0, ts = 0;
	@Override
	public void getNewPackage(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		kyLinkPackage rxData = (kyLinkPackage)arg0.getSource();
		if(rxData.msg_id == 0x13) {
			if(_rw_mutex == false) {
				gyrVal.x = rxData.readoutFloat(12);
				gyrVal.y = rxData.readoutFloat(16);
				gyrVal.z = rxData.readoutFloat(20);
				calVal.x = rxData.readoutFloat(24);
				calVal.y = rxData.readoutFloat(28);
				calVal.z = rxData.readoutFloat(32);
				_val_update_flag = true;
			}
////			now = System.currentTimeMillis();
////			if(now - ts > 100) {
////				ts = now;
////				float x = rxData.readoutFloat(0) * 0.002392578125f;
////				float y = rxData.readoutFloat(4) * 0.002392578125f;
////				float z = rxData.readoutFloat(8) * 0.002392578125f;
//
//				float x = rxData.readoutFloat(24) / 100;
//				float y = rxData.readoutFloat(28) / 100;
//				float z = rxData.readoutFloat(32) / 100;
//
////				float a = rxData.readoutFloat(24);
////				float b = rxData.readoutFloat(28);
////				float c = rxData.readoutFloat(32);
////				float qrt = (float) Math.sqrt(a * a + b * b + c * c);
////				if(min < 1.0) min = qrt;
////				if(qrt > max) max = qrt;
////				if(qrt < min) min = qrt;
////				System.out.println("sqrt: " + qrt + ", max: " + max + ", min: " + min + ", avg: " + (max + min) / 2);
//				try {
//					VectDataQueue.put(new Vector3f(x, y, z));
//				} catch (InterruptedException e) {
//					System.err.println("put data failed");
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
////			}
//				try {
//					Thread.sleep(200);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
		}
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
