package MainWindow;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.Semaphore;

import javax.swing.JPanel;

import Canvas.myCanvas;
import Coordinate.CoordTransEvent;
import Coordinate.CoordTransEventListener;
import Coordinate.CoordinateRG;
import kyLink.kyLinkPackage;
import kyLink.event.kyLinkDecodeEvent;
import kyLink.event.kyLinkDecodeEventListener;
import sonic.uiDist;
import sonic.uiSensor;

public class SimulatorPane extends JPanel implements Runnable, CoordTransEventListener, kyLinkDecodeEventListener {
	private static final long serialVersionUID = 1L;
	private myCanvas canvas = null;
	private CoordinateRG Coord = null;
	private uiSensor uiSonicTOF = null;
	private uiDist uiDistance = null;
	private double dist = 0.0;
	private kyLinkPackage rxData = null;
	private Semaphore semaphore = null;

	public SimulatorPane() {
		this.setLayout(new BorderLayout());
		Coord = new CoordinateRG(2.5, 2.5);
		canvas = new myCanvas(Coord);
		canvas.setZoomEnable(true);
		Point p = Coord.getORG();
		uiSonicTOF = new uiSensor(p.x, p.y);
		canvas.addLayer(uiSonicTOF);

		uiDistance = new uiDist();
		canvas.addLayer(uiDistance);

		this.add(canvas, BorderLayout.CENTER);
		Coord.addCoordTransEventListener(this);
		this.addComponentListener(compListener);

		semaphore = new Semaphore(1, true);
		(new Thread(this)).start();
	}
	@Override
	public void CoordinateUpdate(CoordTransEvent event) {
		// TODO Auto-generated method stub
		Point p = Coord.getORG();
		uiSonicTOF.setPos(p.x, p.y);
		p = Coord.Real2UI(0, -dist);
		uiDistance.setPos(p.x, p.y);
		canvas.repaint();
	}
	@Override
	public void badCRCEvent(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void getNewPackage(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		rxData = (kyLinkPackage)arg0.getSource();
		if(rxData.msg_id == (byte)0x22) {
			dist = rxData.readoutCharacter(0) / 1000.0;
			semaphore.release();
		}
	}
	@Override
	public void lenOverFlow(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	private ComponentAdapter compListener = new ComponentAdapter() {
		@Override
		public void componentResized(ComponentEvent e) {
			// TODO Auto-generated method stub
			Coord.moveTo(getWidth() / 2, getHeight() - 30);
		}
	};

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
			Point p = Coord.Real2UI(0, -dist);
			uiDistance.setPos(p.x, p.y);
			uiDistance.setDistString(dist + "(m)");
			canvas.repaint();
		}
	}
}
