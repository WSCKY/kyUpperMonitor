package Coordinate;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class CoordinateRG {
	private int UI_OrgX = 400, UI_OrgY = 400; // default origin.
	private int UI_SizeX = 800, UI_SizeY = 800;
	private double RealSizeX = 8, RealSizeY = 8;

	private double TransGain = 100.0; // 100 pixels per meter.

	private boolean XY_SWAP = false;
	private boolean X_Mirror = false;
	private boolean Y_Mirror = false;

	private CoordConfig ConfigPane = null;

	private ArrayList<CoordTransEventListener> Listeners = new ArrayList<CoordTransEventListener>();

	public CoordinateRG() {
		ConfigPane = new CoordConfig(this);
	}

	/**
	 * @author kyChu
	 * @param  ppm: pixels per meter.
	 */
	public CoordinateRG(double ppm) {
		TransGain = ppm;
		ConfigPane = new CoordConfig(this);
	}

	public CoordConfig getConfigPane() {
		return ConfigPane;
	}

	public void setPPMG(double ppm) {
		this.TransGain = ppm;
	}
	public void setTransGain(double ppm) {
		this.TransGain = ppm;
	}
	public double getTransGain() {
		return TransGain;
	}

	public void swap(boolean flag) {
		XY_SWAP = flag;
	}
	public void x_mirror(boolean flag) {
		X_Mirror = flag;
	}
	public void y_mirror(boolean flag) {
		Y_Mirror = flag;
	}
	public boolean isXY_SWAP() {
		return XY_SWAP;
	}
	public boolean isX_Mirror() {
		return X_Mirror;
	}
	public boolean isY_Mirror() {
		return Y_Mirror;
	}

	public void move(int x, int y) {
		UI_OrgX += x;
		UI_OrgY += y;
	}
	public void moveTo(int x, int y) {
		UI_OrgX = x;
		UI_OrgY = y;
	}
	public void zoom(double scale) {
		double ret = TransGain * scale;
		if(ret > 10000) ret = 10000;
		if(ret < 0.01) ret = 0.01;
		TransGain = ret;
	}

	public void moveToCenter(int x, int y) {
		
	}
	public void moveToCenter(double x, double y) {
		Point p = Real2UI(x, y);
		move((UI_SizeX / 2) - p.x, (UI_SizeY / 2) - p.y);
	}

	public Point getORG() {
		return new Point(UI_OrgX, UI_OrgY);
	}
	public int getOrgX() {
		return UI_OrgX;
	}
	public int getOrgY() {
		return UI_OrgY;
	}

	public int getUISizeX() {
		return UI_SizeX;
	}
	public int getUISizeY() {
		return UI_SizeY;
	}
	public void setUISize(int x, int y) {
		UI_SizeX = x;
		RealSizeX = x / TransGain;
		UI_SizeY = y;
		RealSizeY = y / TransGain;
	}
	public void setRealSize(double x, double y) {
		double sx = UI_SizeX / x;
		double sy = UI_SizeY / y;
		TransGain = (sx < sy) ? sx : sy;
		RealSizeX = UI_SizeX / TransGain;
		RealSizeY = UI_SizeY / TransGain;
	}

	public double getRealX() {
		return RealSizeX;
	}
	public double getRealY() {
		return RealSizeY;
	}

	public void refreshAll(int EventType) {
		CoordTransEvent event = new CoordTransEvent(this, EventType);
		synchronized(Listeners) {
			for(CoordTransEventListener listener : Listeners) {
				listener.CoordinateUpdate(event);
			}
		}
	}
	public void addCoordTransEventListener(CoordTransEventListener listener) {
		synchronized(Listeners) {
			Listeners.add(listener);
		}
	}
	public void removeCoordTransEventListener(CoordTransEventListener listener) {
		synchronized(Listeners) {
			Listeners.remove(listener);
		}
	}

	public int Real2UI(double l) { return (int) (l * TransGain); }
	public Point Real2UI(double x, double y) {
		int p;
		int px = (int) (x * TransGain);
		int py = (int) (y * TransGain);
		if(XY_SWAP) { p = px; px = py; py = p; }
		if(X_Mirror) { px = -px; }
		if(Y_Mirror) { py = -py; }
		
		return (new Point(px + UI_OrgX, py + UI_OrgY));
	}
	public double UI2Real(int l) { return l / TransGain; }
	public Point2D.Double UI2Real(int x, int y) {
		int p;
		x = x - UI_OrgX; y = y - UI_OrgY;
		if(XY_SWAP) { p = x; x = y; y = p; }
		if(X_Mirror) { x = -x; }
		if(Y_Mirror) { y = -y; }
		return (new Point2D.Double(x / TransGain, y / TransGain));
	}
}
