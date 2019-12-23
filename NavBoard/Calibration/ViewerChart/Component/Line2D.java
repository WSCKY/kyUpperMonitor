package Calibration.ViewerChart.Component;

import java.awt.Color;
import java.awt.Graphics;

public class Line2D {
	public float px1, py1, px2, py2;
	public int uiPx1, uiPy1, uiPx2, uiPy2;
	private Color lc;

	public Line2D() {
		this(0, 0, 1, 1, Color.WHITE);
	}
	public Line2D(Color c) {
		this(0, 0, 1, 1, c);
	}
	public Line2D(float x2, float y2) {
		this(0, 0, x2, y2, Color.WHITE);
	}
	public Line2D(float x2, float y2, Color c) {
		this(0, 0, x2, y2, c);
	}
	public Line2D(float x1, float y1, float x2, float y2) {
		this(x1, y1, x2, y2, Color.WHITE);
	}
	public Line2D(float x1, float y1, float x2, float y2, Color c) {
		px1 = x1; py1 = y1; px2 = x2; py2 = y2; lc = c;
	}

	public void show(Graphics g) {
		g.setColor(lc);
		g.drawLine(uiPx1, uiPy1, uiPx2, uiPy2);
	}
}
