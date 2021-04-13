package main;

import java.util.Timer;
import java.util.TimerTask;

import CommTool.CommTool;
import CommTool.exception.PortActionEventListener;
import CommTool.exception.SendDataFailure;
import kyLink.kyLinkPackage;
import kyLink.event.kyLinkDecodeEvent;
import kyLink.event.kyLinkDecodeEventListener;

public class dataProc implements kyLinkDecodeEventListener, PortActionEventListener {
	private CommTool commTool = null;
	private boolean data_reset = false;
	private boolean port_opened = false;
	private kyLinkPackage rxData = null;
	private boolean version_got = false;
	private kyLinkPackage txData = null;
	private Timer StatisticTimer = null;

	public int frameRate = 0;
	public float qw = 1.0f, qx = 0.0f, qy = 0.0f, qz = 0.0f;
	public String version = "";

	public dataProc(CommTool ctool) {
		this.commTool = ctool;
		this.commTool.addPortActionEventListener(this);
		data_reset = true;
		qw = 1.0f; qx = qy = qz = 0.0f;
		version = "";
		txData = new kyLinkPackage();
		txData.dev_id = 0x01;
		txData.msg_id = 0x0A;
		txData.length = 1;
		txData.addByte((byte) 1, 0);
		StatisticTimer = new Timer();
		StatisticTimer.scheduleAtFixedRate(RateStat, 0, 1000);
	}

	private int frameCnt = 0;
	private TimerTask RateStat = new TimerTask() {
		public void run() {
			frameRate = frameCnt;
			frameCnt = 0;
			if(frameRate == 0 && data_reset == false) {
				qw = 1.0f; qx = qy = qz = 0.0f;
				version = "";
				data_reset = true;
			}
			if(port_opened == true) {
				if(version_got == false) {
					try {
						commTool.sendPackage(txData);
						System.out.println("request version");
					} catch (SendDataFailure e) {
						// TODO Auto-generated catch block
						System.err.println("failed to request version form IMU");
						e.printStackTrace();
					}
				}
			}
		}
	};

	@Override
	public void badCRCEvent(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void getNewPackage(kyLinkDecodeEvent event) {
		// TODO Auto-generated method stub
		rxData = (kyLinkPackage) event.getSource();
		if (rxData.msg_id == 0x42) {
			frameCnt ++;
			qw = rxData.readoutFloat(32);
			qx = rxData.readoutFloat(36);
			qy = rxData.readoutFloat(40);
			qz = rxData.readoutFloat(44);
			if(data_reset == true) data_reset = false;
		} else if(rxData.msg_id == 0x0A) {
			version_got = true;
			version = rxData.readoutString(0, rxData.length);
		}
	}

	@Override
	public void lenOverFlow(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void PortNewActionOccured(PortActionCode arg0) {
		// TODO Auto-generated method stub
		switch(arg0) {
		case PortAction_Opened: {
			System.out.println("imu data: port opened");
			port_opened = true;
			break;
		}
		case PortAction_Closed: {
			System.out.println("imu data: port closed");
			port_opened = false;
			version_got = false;
			break;
		}
		case PortAction_Failed: System.out.println("imu data: port failed"); break;
		}
	}
}
