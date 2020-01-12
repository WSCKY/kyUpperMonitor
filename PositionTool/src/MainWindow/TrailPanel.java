package MainWindow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import Canvas.myCanvas;
import Coordinate.CoordTransEvent;
import Coordinate.CoordinateRG;
import kyLink.kyLinkPackage;
import kyLink.event.kyLinkDecodeEvent;
import kyLink.event.kyLinkDecodeEventListener;
import uiLayers.uiAxis;
import uiLayers.uiPoint2D;
import uiLayers.uiRuler;

public class TrailPanel extends JSplitPane implements configEventListener, kyLinkDecodeEventListener {
	private static final long serialVersionUID = 1L;

	private myCanvas canvas = null;
	private CoordinateRG Coord = null;

	private uiAxis mapAxis = null;
	private uiRuler mapRuler = null;

	private ArrayList<uiPoint2D> DotList = new ArrayList<uiPoint2D>();

	private JTabbedPane MainTabbedPane = null;
	private NavInfoPanel infoPanel_a = null;
	private NavInfoPanel infoPanel_b = null;

	private Semaphore semaphoreUpdate = null;

	public TrailPanel() {
		super(JSplitPane.HORIZONTAL_SPLIT);
		JPanel CanvasPanel = new JPanel();
		CanvasPanel.setLayout(new BorderLayout());
		Coord = new CoordinateRG(100.0);
		canvas = new myCanvas(Coord);
		canvas.setZoomEnable(true);
		canvas.setDragEnable(true);

		mapAxis = new uiAxis();
		mapRuler = new uiRuler();
		canvas.addTopLayer(mapAxis);
		canvas.addTopLayer(mapRuler);

		Coord.y_mirror(true);

		CanvasPanel.add(canvas, BorderLayout.CENTER);
		Coord.addCoordTransEventListener(mapAxis);
		Coord.addCoordTransEventListener(mapRuler);
		Coord.addCoordTransEventListener(canvas);

		this.setLeftComponent(CanvasPanel);

		MainTabbedPane = new JTabbedPane();
		MainTabbedPane.setFont(MainTabbedPane.getFont().deriveFont(Font.BOLD, 16));

		JPanel infoPanel = new JPanel();
		infoPanel_a = new NavInfoPanel("F9PA");
		infoPanel_b = new NavInfoPanel("F9PB");
		infoPanel.setLayout(new GridLayout(2, 1));
		infoPanel.add(infoPanel_a);
		infoPanel.add(infoPanel_b);
		MainTabbedPane.addTab("Info", null, infoPanel, "GNSS information");

		ConfigPanel configPanel = new ConfigPanel();
		configPanel.setListener(this);
		MainTabbedPane.addTab("Config", null, configPanel, "configuration");

		this.setRightComponent(MainTabbedPane);
		this.setDividerLocation(0.8f);
		this.setEnabled(false);
		this.addComponentListener(compLis);

		semaphoreUpdate = new Semaphore(0, true);
		(new Thread(testRun)).start();
	}

	private static final double EARTH_RADIUS = 6371.393;

	private Color dotColor = Color.RED;
	private GNSS_INFO gps_info;
	private double org_lon, org_lat;
	private boolean paint_start = false;
	private boolean origin_select = false;
	private Runnable testRun = new Runnable() {
		public void run() {
//			for(int i = 0; i < 20; i ++) {
//				uiPoint2D dot = new uiPoint2D();
//				double x = (Math.random() - 0.5) * 3000;
//				double y = (Math.random() - 0.5) * 3000;
//				dot.setPoint(x, y);
//				synchronized(DotList) {
//					DotList.add(dot);
//				}
//				canvas.addLayer(dot);
//				Coord.addCoordTransEventListener(dot);
//				Coord.refreshAll(CoordTransEvent.EventType_SHOW);
//				try {
//					Thread.sleep(500);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}

			gps_info = new GNSS_INFO();
			while(true) {
				try {
					semaphoreUpdate.acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				infoPanel_a.setDate(gps_info.year, gps_info.month, gps_info.day);
				infoPanel_a.setTime(gps_info.hour, gps_info.min, gps_info.sec);
				infoPanel_a.setPosition(gps_info.lon, gps_info.lat);
				infoPanel_a.setSVNumber(gps_info.num_sv);
				infoPanel_a.setHeight(gps_info.height, gps_info.hMSL);
				infoPanel_a.set2DHeading(gps_info.head_veh);
				int fixt = ((gps_info.fix_type & 0xFF) >> 6) & 0x000000FF;
				if(fixt == 0) { infoPanel_a.setMode("No Fix"); dotColor = Color.RED; }
				if(fixt == 1) { infoPanel_a.setMode("Float"); dotColor = Color.BLUE; }
				if(fixt == 2) { infoPanel_a.setMode("Fixed"); dotColor = Color.GREEN; }
				infoPanel_a.updateDisplay();
				if(paint_start) {
					if(!origin_select) {
						org_lon = gps_info.lon;
						org_lat = gps_info.lat;
						origin_select = true;
					} else {
						uiPoint2D dot = new uiPoint2D(dotColor);
						double x = Math.toRadians(gps_info.lon - org_lon) * EARTH_RADIUS * 1000;
						double y = Math.toRadians(gps_info.lat - org_lat) * EARTH_RADIUS * 1000;
						dot.setPoint(x, y);
						synchronized(DotList) {
							DotList.add(dot);
						}
						canvas.addLayer(dot);
						Coord.addCoordTransEventListener(dot);
						Coord.refreshAll(CoordTransEvent.EventType_SHOW);
					}
				}
			}
		}
	};

	@Override
	public void configUpdateNotify(configEvent e) {
		// TODO Auto-generated method stub
		switch(e.getCommand()) {
		case configEvent.CMD_NULL: break;
		case configEvent.RESET_VIEW: autoCenterScale(); break;
		case configEvent.PAINT_START: paint_start = true; break;
		case configEvent.PAINT_CLEAR: clearAllPoints(); break;
		case configEvent.PAINT_STOP: paint_start = false; origin_select = false; break;
		case configEvent.LOAD_GNSS_DATA:
			File file = (File) e.getParam();
			System.out.println("load file: " + file.getAbsolutePath()); break;
		case configEvent.SAVE_GNSS_DATA:
			System.out.println("save file: " + ((File) e.getParam()).getAbsolutePath()); break;
		default: break;
		}
	}

	private void resetDividerLocation() {this.setDividerLocation(this.getWidth() - 360);}
	private ComponentAdapter compLis = new ComponentAdapter() {
		public void componentResized(ComponentEvent e) {
			// TODO Auto-generated method stub
			resetDividerLocation();
		}
	};

	private void autoCenterScale() {
		double min_x = 0, min_y = 0, max_x = 0, max_y = 0;
		double x, y;
		synchronized(DotList) {
			for(uiPoint2D dot : DotList) {
				x = dot.getX();
				y = dot.getY();
				if(x < min_x) min_x = x;
				if(x > max_x) max_x = x;
				if(y < min_y) min_y = y;
				if(y > max_y) max_y = y;
			}
		}
		if(min_x != max_x && min_y != max_y) {
			Coord.setRealSize((max_x - min_x) * 1.1, (max_y - min_y) * 1.1);
		}
		Coord.moveToCenter((max_x + min_x) / 2.0, (max_y + min_y) / 2.0);
		Coord.refreshAll(CoordTransEvent.EventType_ZOOM);
	}

	private void clearAllPoints() {
		synchronized(DotList) {
			for(uiPoint2D dot : DotList) {
				canvas.delLayer(dot);
				Coord.removeCoordTransEventListener(dot);
			}
			DotList.clear();
		}
		Coord.refreshAll(CoordTransEvent.EventType_SHOW);
	}

	@Override
	public void badCRCEvent(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	private kyLinkPackage rxData = null;
	@Override
	public void getNewPackage(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		rxData = (kyLinkPackage) arg0.getSource();
		if(rxData.msg_id == (byte) 0x56) {
			gps_info.year = rxData.readoutUShort(4);
			gps_info.month = rxData.rData[6];
			gps_info.day = rxData.rData[7];
			gps_info.hour = rxData.rData[8];
			gps_info.min = rxData.rData[9];
			gps_info.sec = rxData.rData[10];
			gps_info.fix_type = rxData.rData[21]; /* flags */
			gps_info.num_sv = rxData.rData[23];
			gps_info.lon = rxData.readoutInteger(24) * 1e-7;
			gps_info.lat = rxData.readoutInteger(28) * 1e-7;
			gps_info.height = rxData.readoutInteger(32) * 1e-3;
			gps_info.hMSL = rxData.readoutInteger(36) * 1e-3;
			gps_info.head_veh = rxData.readoutInteger(78) * 1e-5;
			semaphoreUpdate.release();
		}
	}

	@Override
	public void lenOverFlow(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }
		JFrame tf = new JFrame("TEST PANEL");
		tf.setSize(1200, 800);
		tf.setLocationRelativeTo(null);
		tf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tf.add(new TrailPanel());
		tf.setVisible(true);
	}
}

class GNSS_INFO {
	public int year, month, day;
	public int hour, min, sec;
	public double lon, lat;
	public int fix_type;
	public int num_sv;
	public double height, hMSL;
	public double head_veh;
}
