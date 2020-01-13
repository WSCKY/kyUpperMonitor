package Calibration;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Semaphore;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import Calibration.Event.CtrlEvent;
import Calibration.Event.CtrlEventListener;
import Calibration.Viewer3D.Viewer3D;
import Calibration.Viewer3D.Component.Dot3D;
import Calibration.ViewerChart.ViewerChart;
import Calibration.ViewerChart.Component.Dot2D;
import kyLink.kyLinkPackage;
import kyLink.event.kyLinkDecodeEvent;
import kyLink.event.kyLinkDecodeEventListener;

public class CalibratePanel extends JSplitPane implements Runnable, CtrlEventListener, kyLinkDecodeEventListener {
	private static final long serialVersionUID = 1L;

	private Viewer3D View3D = null;
	private ViewerChart xyChart = null;
	private ViewerChart xzChart = null;
	private ViewerChart yzChart = null;

	private CalibCtrlPanel ctrlPanel = null;

	private float SensorValueRadius;
	private Vector3f orgVal[] = null;
	private Semaphore valMutex = null;
	private Semaphore valUpdate = null;

	private boolean _sample_enable = false;
	private boolean _calibrated_flag = false;
	private int sample_counter = 0;
	private ellipCalibration calibrater = null;

	private ellipParam[] calRet = null;
	private Vector3f[][] orgVal_save = null;
	private Vector3f[][] calVal_save = null;
	private int org_dot_id = 0, cal_dot_id = 0;
	public CalibratePanel() {
		super(JSplitPane.HORIZONTAL_SPLIT);

		orgVal = new Vector3f[CalibCtrlPanel.MAX_SUPPORT_SENSORS];
		calRet = new ellipParam[CalibCtrlPanel.MAX_SUPPORT_SENSORS];
		orgVal_save = new Vector3f[CalibCtrlPanel.MAX_SUPPORT_SENSORS][];
		calVal_save = new Vector3f[CalibCtrlPanel.MAX_SUPPORT_SENSORS][];
		for(int j = 0; j < CalibCtrlPanel.MAX_SUPPORT_SENSORS; j ++) {
			orgVal[j] = new Vector3f();
			orgVal_save[j] = new Vector3f[ellipCalibration.SAMPLE_NUMBER];
			calVal_save[j] = new Vector3f[ellipCalibration.SAMPLE_NUMBER];
			for(int i = 0; i < ellipCalibration.SAMPLE_NUMBER; i ++) {
				orgVal_save[j][i] = new Vector3f();
				calVal_save[j][i] = new Vector3f();
			}
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

		ctrlPanel = new CalibCtrlPanel(this);
		this.setRightComponent(ctrlPanel);
		this.setLeftComponent(ShowSP);
		this.setDividerLocation(0.5);

		this.setEnabled(false);
		this.addComponentListener(compLis);

		(new Thread(this)).start();
	}

	private void startCalibrate() {
		if(_sample_enable == false) {
			_sample_enable = true;
			_calibrated_flag = false;
			sample_counter = 0; // reset sample counter.
			SensorValueRadius = ctrlPanel.getSensorValueRadius();
			View3D.removeAllDot(org_dot_id);
			View3D.removeAllDot(cal_dot_id);
			xyChart.removeAllDot2D();
			xzChart.removeAllDot2D();
			yzChart.removeAllDot2D();
			ctrlPanel.operationForbidden();
			System.out.println("sample start");
		}
	}

	private void stopCalibrate() {
		if(_sample_enable == true) {
			_sample_enable = false;
			ctrlPanel.operationAllowed();
		}
	}

	private void showCalibrate() {
		if(_calibrated_flag == true) {
			float x, y, z;
			int id = ctrlPanel.getSelectedSensorID();
			View3D.removeAllDot(cal_dot_id);
			View3D.removeAllDot(org_dot_id);
			xyChart.removeAllDot2D();
			xzChart.removeAllDot2D();
			yzChart.removeAllDot2D();
			if(ctrlPanel.getOrgDataSelected()) {
				for(int i = 0; i < ellipCalibration.SAMPLE_NUMBER; i ++) {
					x = orgVal_save[id][i].x / SensorValueRadius;
					y = orgVal_save[id][i].y / SensorValueRadius;
					z = orgVal_save[id][i].z / SensorValueRadius;
					View3D.addDot3DTo(new Dot3D(x, y, z, new Color3f(1, 0, 0)), org_dot_id);
					xyChart.addDot2D(new Dot2D(x, y, Color.RED));
					xzChart.addDot2D(new Dot2D(x, z, Color.RED));
					yzChart.addDot2D(new Dot2D(y, z, Color.RED));
				}
			}
			if(ctrlPanel.getCalDataSelected()) {
				for(int i = 0; i < ellipCalibration.SAMPLE_NUMBER; i ++) {
					x = calVal_save[id][i].x / SensorValueRadius;
					y = calVal_save[id][i].y / SensorValueRadius;
					z = calVal_save[id][i].z / SensorValueRadius;
					View3D.addDot3DTo(new Dot3D(x, y, z, new Color3f(0, 0, 1)), cal_dot_id);
					xyChart.addDot2D(new Dot2D(x, y, Color.BLUE));
					xzChart.addDot2D(new Dot2D(x, z, Color.BLUE));
					yzChart.addDot2D(new Dot2D(y, z, Color.BLUE));
				}
			}
			xyChart.refresh();
			xzChart.refresh();
			yzChart.refresh();
		}
	}

	private void SaveToFile() {
		if(_calibrated_flag == true) {
			int idx;
			byte[] header = ".mcd\n".getBytes();
			SimpleDateFormat df = new SimpleDateFormat("HH_mm_ss");
			String FileName = ctrlPanel.getCalDataSavePath() + File.separator + df.format(new Date()) + ".dat";
			System.out.println(FileName);
//			byte[] b = Float2Bytes(12.4f);
//			System.out.println("b[]: " + Byte2HexString(b[0]) + Byte2HexString(b[1]) + Byte2HexString(b[2]) + Byte2HexString(b[3]));
//			System.out.println("float: " + Bytes2Float(b));
			File file = new File(FileName);
			if(!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.err.println("Failed to create file " + FileName);
					e.printStackTrace();
				}
			} else {
				System.err.println("WARNING: file already exist!");
			}
			byte[] bytes = new byte[5 + 1 + 25 * ctrlPanel.getSensorNumber()];
			System.arraycopy(header, 0, bytes, 0, header.length);
			idx = header.length;
			bytes[idx ++] = (byte) ctrlPanel.getSensorNumber();
			for(int i = 0; i < ctrlPanel.getSensorNumber(); i ++) {
				byte[] fb;
				bytes[idx ++] = calRet[i].checksum();
				fb = Float2Bytes(calRet[i].offX);
				System.arraycopy(fb, 0, bytes, idx, 4);
				idx += 4;
				fb = Float2Bytes(calRet[i].offY);
				System.arraycopy(fb, 0, bytes, idx, 4);
				idx += 4;
				fb = Float2Bytes(calRet[i].offZ);
				System.arraycopy(fb, 0, bytes, idx, 4);
				idx += 4;
				fb = Float2Bytes(calRet[i].sclX);
				System.arraycopy(fb, 0, bytes, idx, 4);
				idx += 4;
				fb = Float2Bytes(calRet[i].sclY);
				System.arraycopy(fb, 0, bytes, idx, 4);
				idx += 4;
				fb = Float2Bytes(calRet[i].sclZ);
				System.arraycopy(fb, 0, bytes, idx, 4);
				idx += 4;
			}
			try {
				FileOutputStream outputStream  = new FileOutputStream(file);
				outputStream.write(bytes);
	            outputStream.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

//	private String Byte2HexString(byte b) {
//		String hex = Integer.toHexString(b & 0xFF);
//		if(hex.length() < 2) {
//			hex = "0" + hex;
//		}
//		return hex;
//	}
//	private float Bytes2Float(byte[] b) {
//		byte[] bs = {b[3], b[2], b[1], b[0]};
//		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bs));
//		float f = 0.0f;
//		try {
//			f = dis.readFloat();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return f;
//	}

	private byte[] Float2Bytes(float f) {
		byte[] b = new byte[4];
		int data = Float.floatToIntBits(f);
		b[0] = (byte)(data & 0xFF);
		b[1] = (byte)((data & 0xFF00) >> 8);
		b[2] = (byte)((data & 0xFF0000) >> 16);
		b[3] = (byte)((data & 0xFF000000) >> 24);
		return b;
	}

	@Override
	public void UserCtrlCommand(CtrlEvent event) {
		// TODO Auto-generated method stub
		switch(event.getEventId()) {
		case CtrlEvent.CTRL_EVENT_NULL:
			break;
		case CtrlEvent.CTRL_EVENT_START:
			startCalibrate();
			break;
		case CtrlEvent.CTRL_EVENT_STOP:
			stopCalibrate();
			break;
		case CtrlEvent.CTRL_EVENT_REFRESH:
			showCalibrate();
			break;
		case CtrlEvent.CTRL_EVENT_SAVE:
			SaveToFile();
			break;
		default: break;
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		float x, y, z;
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
				int id = ctrlPanel.getSelectedSensorID();
				for(int i = 0; i < ctrlPanel.getSensorNumber(); i ++) {
					orgVal_save[i][sample_counter].x = orgVal[i].x;
					orgVal_save[i][sample_counter].y = orgVal[i].y;
					orgVal_save[i][sample_counter].z = orgVal[i].z;
				}
				valMutex.release();
				x = orgVal_save[id][sample_counter].x / SensorValueRadius;
				y = orgVal_save[id][sample_counter].y / SensorValueRadius;
				z = orgVal_save[id][sample_counter].z / SensorValueRadius;
				sample_counter ++;
				View3D.addDot3DTo(new Dot3D(x, y, z, new Color3f(1, 0, 0)), org_dot_id);
				xyChart.addDot2D(new Dot2D(x, y, Color.RED));
				xzChart.addDot2D(new Dot2D(x, z, Color.RED));
				yzChart.addDot2D(new Dot2D(y, z, Color.RED));
				xyChart.refresh();
				xzChart.refresh();
				yzChart.refresh();
				if(sample_counter == ellipCalibration.SAMPLE_NUMBER) {
					_sample_enable = false;
					ctrlPanel.operationAllowed();
					for(int i = 0; i < ctrlPanel.getSensorNumber(); i ++) {
						for(int j = 0; j < ellipCalibration.SAMPLE_NUMBER; j ++) {
							calibrater.setSampleVal(j, orgVal_save[i][j].x, orgVal_save[i][j].y, orgVal_save[i][j].z);
						}
						calibrater.computeRet(SensorValueRadius);
						calRet[i] = calibrater.getCalibRet();
						for(int j = 0; j < ellipCalibration.SAMPLE_NUMBER; j ++) {
							calVal_save[i][j].x = (orgVal_save[i][j].x + calRet[i].offX) * calRet[i].sclX;
							calVal_save[i][j].y = (orgVal_save[i][j].y + calRet[i].offY) * calRet[i].sclY;
							calVal_save[i][j].z = (orgVal_save[i][j].z + calRet[i].offZ) * calRet[i].sclZ;
						}
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
			for(int i = 0; i < ctrlPanel.getSensorNumber(); i ++) {
				orgVal[i].x = rxData.readoutShort(i * 7 + 1);
				orgVal[i].y = rxData.readoutShort(i * 7 + 3);
				orgVal[i].z = rxData.readoutShort(i * 7 + 5);
			}
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
