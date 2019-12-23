package sonic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import Canvas.uiComponent.uiComponent;

public class uiDist extends uiComponent {
	private Font StringFont = new Font("Courier New", Font.BOLD, 16);
	private int MaxWidth = 200;

	private int imgW = 0, imgH = 0, fontHeight = 0, fontAscent = 0;
	public uiDist() {
		Graphics2D g = (Graphics2D)this.getGraphics();
		fontHeight = g.getFontMetrics(StringFont).getHeight();
		fontAscent = g.getFontMetrics(StringFont).getAscent();
		BufferedImage img = new BufferedImage(MaxWidth, fontHeight + 4, BufferedImage.TYPE_4BYTE_ABGR);
		imgW = img.getWidth();
		imgH = img.getHeight();
		this.setImage(img, imgW, imgH);
		g = (Graphics2D)this.getGraphics();
		g.setFont(StringFont);
		g.setColor(Color.RED);
		int width = g.getFontMetrics(StringFont).stringWidth("0.0(m)");
		g.drawString("0.0(m)", (MaxWidth - width) / 2, fontAscent);
		g.setColor(Color.GREEN);
		g.setStroke(new BasicStroke(4));
		g.drawLine(0, fontHeight, MaxWidth, fontHeight);
	}

	public void setDistString(String s) {
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

//	public void setAlpha(int alpha) {
//		this.setImage(new BufferedImage(50, 8, BufferedImage.TYPE_4BYTE_ABGR), 50, 8);
//		Graphics2D g = (Graphics2D)this.getGraphics();
//		g.setColor(new Color(255, 0, 0, alpha));
//		g.setStroke(new BasicStroke(5));
//		g.drawLine(0, 3, 50, 3);
//	}

	public void setPos(int x, int y) {
		super.setPos(x - MaxWidth / 2, y - imgH - 20);
	}
}
