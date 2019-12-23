package Calibration.ViewerChart.Component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Coord2D {
	public static final float COORDINATE_BOUNDS = 2.0f;

	private int fontAscent = 0;
	private Font StringFont = new Font("»ªÎÄÁ¥Êé", Font.BOLD, 24);

	private String Hname, Vname;
	private Color Hcolor, Vcolor;
	private int[] px = new int[3];
	private int[] py = new int[3];

	private int uiCoordWidth;
	private int uiSizeX, uiSizeY;
	private int uiCenterX, uiCenterY;
	private int uiCenterOffX, uiCenterOffY;
	public Coord2D() {
		this("x", "y", Color.RED, Color.GREEN);
	}

	public Coord2D(Color hc, Color vc) {
		this("x", "y", hc, vc);
	}

	public Coord2D(String hn, String vn) {
		this(hn, vn, Color.RED, Color.GREEN);
	}

	public Coord2D(String hn, String vn, Color hc, Color vc) {
		Hname = hn; Vname = vn;
		Hcolor = hc; Vcolor = vc;
	}

	public void update(int xs, int ys) {
		uiSizeX = xs; uiSizeY = ys;
		uiCenterX = xs / 2; uiCenterY = ys / 2;
		uiCoordWidth = Math.min(uiSizeX, uiSizeY);
		uiCenterOffX = (xs - uiCoordWidth) / 2;
		uiCenterOffY = (ys - uiCoordWidth) / 2;
	}

	private int transformX(float x) {
		return (int) ((x * uiCoordWidth) / COORDINATE_BOUNDS / 2 + uiCenterX);
	}
	private int transformY(float y) {
		return (int) (-(y * uiCoordWidth) / COORDINATE_BOUNDS / 2 + uiCenterY);
	}

	public void transform(Dot2D dot) {
		dot.uiPx = transformX(dot.px);
		dot.uiPy = transformY(dot.py);
	}

	public void transform(Line2D line) {
		line.uiPx1 = transformX(line.px1);
		line.uiPx2 = transformX(line.px2);
		line.uiPy1 = transformY(line.py1);
		line.uiPy2 = transformY(line.py2);
	}

	public void show(Graphics g) {
		g.setFont(StringFont);
		fontAscent = g.getFontMetrics(StringFont).getAscent();

		g.setColor(Hcolor);
		int tmp = uiCenterOffX + (uiCoordWidth * 4) / 5;
		g.drawLine(uiCenterOffX + uiCoordWidth / 5, uiSizeY / 2, tmp, uiSizeY / 2);
		px[0] = tmp - 18;  px[1] = tmp - 18;  px[2] = tmp;
		py[0] = uiSizeY / 2 - 4; py[1] = uiSizeY / 2 + 4; py[2] = uiSizeY / 2;
		g.fillPolygon(px, py, 3);
		g.drawString(Hname, tmp + 10, uiSizeY / 2 + fontAscent / 2);
		g.setColor(Vcolor);
		g.drawLine(uiSizeX / 2, uiCenterOffY + uiCoordWidth / 5, uiSizeX / 2, uiCenterOffY + (uiCoordWidth * 4) / 5);
		px[0] = uiSizeX / 2 - 4;  px[1] = uiSizeX / 2 + 4;  px[2] = uiSizeX / 2;
		py[0] = uiCenterOffY + uiCoordWidth / 5 + 18; py[1] = uiCenterOffY + uiCoordWidth / 5 + 18; py[2] = uiCenterOffY + uiCoordWidth / 5;
		g.fillPolygon(px, py, 3);
		int width = g.getFontMetrics(StringFont).stringWidth(Vname);
		g.drawString(Vname, uiSizeX / 2 - width / 2, uiCenterOffY + uiCoordWidth / 5 - 10);
	}
}
