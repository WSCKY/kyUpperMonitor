package Calibration.ViewerChart.Component;

import java.awt.Color;
import java.awt.Graphics;

public class Dot2D {
	public float px, py;
	public int uiPx, uiPy;
	private Color pc;
	public Dot2D() {
		this(0, 0, Color.WHITE);
	}

	public Dot2D(float x, float y) {
		this(x, y, Color.WHITE);
	}

	public Dot2D(float x, float y, Color c) {
		px = x; py = y; pc = c;
	}

	public void show(Graphics g) {
		g.setColor(pc);
		g.fillArc(uiPx - 2, uiPy - 2, 4, 4, 0, 360);
	}
}
