package uiLayers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import Canvas.uiComponent.uiComponent;
import Coordinate.CoordTransEvent;
import Coordinate.CoordTransEventListener;
import Coordinate.CoordinateRG;

public class uiPoint2D extends uiComponent implements CoordTransEventListener {
	private static final int DOT_SIZE = 6;

	private double x = 0, y = 0;
	private Color DotColor = Color.BLUE;
	public uiPoint2D() {
		BufferedImage img = new BufferedImage(DOT_SIZE, DOT_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
		this.setImage(img, DOT_SIZE, DOT_SIZE);

		Graphics2D g = (Graphics2D)this.getGraphics();
		g.setColor(DotColor);
		g.fillArc(0, 0, DOT_SIZE, DOT_SIZE, 0, 360);
	}
	public uiPoint2D(Color dColor) {
		DotColor = dColor;
		BufferedImage img = new BufferedImage(DOT_SIZE, DOT_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
		this.setImage(img, DOT_SIZE, DOT_SIZE);

		Graphics2D g = (Graphics2D)this.getGraphics();
		g.setColor(dColor);
		g.fillArc(0, 0, DOT_SIZE, DOT_SIZE, 0, 360);
	}
	public void setPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public double getX() {
		return this.x;
	}
	public double getY() {
		return this.y;
	}
	@Override
	public void CoordinateUpdate(CoordTransEvent event) {
		// TODO Auto-generated method stub
		if(event.getEventType() == CoordTransEvent.EventType_MOVE || 
			event.getEventType() == CoordTransEvent.EventType_ZOOM || 
			event.getEventType() == CoordTransEvent.EventType_SHOW) {
			CoordinateRG Coord = (CoordinateRG) event.getSource();
			Point p = Coord.Real2UI(this.x, this.y);
			super.setPos(p.x - DOT_SIZE / 2, p.y - DOT_SIZE / 2);
		}
	}
}
