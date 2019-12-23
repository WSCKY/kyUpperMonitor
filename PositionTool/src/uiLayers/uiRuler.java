package uiLayers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import Canvas.uiComponent.uiComponent;
import Coordinate.CoordTransEvent;
import Coordinate.CoordTransEventListener;
import Coordinate.CoordinateRG;

public class uiRuler extends uiComponent implements CoordTransEventListener {
	private Font StringFont = new Font("Courier New", Font.BOLD, 16);
	private static final int MaxWidth = 100;

	private double lastScaleVal = 0;
	private int imgW = 0, imgH = 0, fontHeight = 0, fontAscent = 0;
	public uiRuler() {
		Graphics2D g = (Graphics2D)this.getGraphics();
		fontHeight = g.getFontMetrics(StringFont).getHeight();
		fontAscent = g.getFontMetrics(StringFont).getAscent();
		imgW = MaxWidth;
		imgH = fontHeight + 4;
		BufferedImage img = new BufferedImage(imgW, imgH, BufferedImage.TYPE_4BYTE_ABGR);
		this.setImage(img, imgW, imgH);
		g = (Graphics2D)this.getGraphics();
		g.setFont(StringFont);
		g.setColor(Color.RED);
		int width = g.getFontMetrics(StringFont).stringWidth("5m");
		g.drawString("5m", (MaxWidth - width) / 2, fontAscent);
		g.setColor(Color.GREEN);
		g.setStroke(new BasicStroke(4));
		g.drawLine(0, fontHeight, MaxWidth, fontHeight);
	}

	private void setString(String s) {
		BufferedImage img = new BufferedImage(imgW, imgH, BufferedImage.TYPE_4BYTE_ABGR);
		this.setImage(img, imgW, imgH);
		Graphics2D g = (Graphics2D)this.getGraphics();
		int width = g.getFontMetrics(StringFont).stringWidth(s);
		g.setFont(StringFont);
		g.setColor(Color.RED);
		g.drawString(s, (MaxWidth - width) / 2, fontAscent);
		g.setColor(Color.GREEN);
		g.setStroke(new BasicStroke(4));
		g.drawLine(0, fontHeight, MaxWidth, fontHeight);
	}

	public void setScaleVal(double val) {
		if(Math.abs(lastScaleVal - val) < 0.01) return;
		int cnt = 0;
		val = MaxWidth / val;
		if(val < 1) {
			val *= 100;
			cnt --;
		} else {
			while(val > 1000) {
				val = val / 1000;
				cnt ++;
			}
		}
		String str = String.format("%.2f", val);
		if(cnt == -1) {
			str += "c";
		} else if(cnt == 1) {
			str += "k";
		} else if(cnt > 1) {
			str += ("e" + (cnt - 1) + "k");
		}
		str += "m";
		setString(str);
	}

//	public void setAlpha(int alpha) {
//		this.setImage(new BufferedImage(50, 8, BufferedImage.TYPE_4BYTE_ABGR), 50, 8);
//		Graphics2D g = (Graphics2D)this.getGraphics();
//		g.setColor(new Color(255, 0, 0, alpha));
//		g.setStroke(new BasicStroke(5));
//		g.drawLine(0, 3, 50, 3);
//	}
	@Override
	public void CoordinateUpdate(CoordTransEvent event) {
		// TODO Auto-generated method stub
		if(event.getEventType() == CoordTransEvent.EventType_ZOOM || event.getEventType() == CoordTransEvent.EventType_SIZE) {
			CoordinateRG Coord = (CoordinateRG) event.getSource();
			super.setPos(Coord.getUISizeX() - 75 - MaxWidth / 2, Coord.getUISizeY() - 25 - imgH / 2);
			setScaleVal(Coord.getTransGain());
		}
	}
}
