package uiLayers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import Canvas.uiComponent.uiComponent;
import Coordinate.CoordTransEvent;
import Coordinate.CoordTransEventListener;
import Coordinate.CoordinateRG;

public class uiAxis extends uiComponent implements CoordTransEventListener {
	private static final int COORD_SIZE = 200;
	private Font StringFont = new Font("»ªÎÄÁ¥Êé", Font.BOLD, 24);

//	private boolean HV_SWAP = false;
//	private boolean H_Mirror = false;
//	private boolean V_Mirror = false;
	private String Hname = "x", Vname = "y";
	private Color Hcolor = Color.RED, Vcolor = Color.GREEN;
	private int[] px = new int[3];
	private int[] py = new int[3];

	private int Hname_Width = 0;
	private int Hname_Height = 0;
	private int Vname_Width = 0;
	private int Vname_Height = 0;
	private int HLine_Length = 0;
	private int VLine_Length = 0;

	private int Cross_X = 0;
	private int Cross_Y = 0;

	public uiAxis() {
		BufferedImage img = new BufferedImage(COORD_SIZE, COORD_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
		this.setImage(img, COORD_SIZE, COORD_SIZE);

		Graphics2D g = (Graphics2D)this.getGraphics();
		g.setFont(StringFont);
		int fontAscent = g.getFontMetrics(StringFont).getAscent();
		Hname_Height = Vname_Height = fontAscent;
		Hname_Width = g.getFontMetrics(StringFont).stringWidth(Hname);
		Vname_Width = g.getFontMetrics(StringFont).stringWidth(Vname);
		HLine_Length = COORD_SIZE - 10 - Hname_Width;
		VLine_Length = COORD_SIZE - 10 - Vname_Height;

		g.setColor(Hcolor);
		g.drawLine(0, COORD_SIZE - VLine_Length / 2, HLine_Length, COORD_SIZE - VLine_Length / 2);
		px[0] = HLine_Length - 18;                 px[1] = HLine_Length - 18;                 px[2] = HLine_Length;
		py[0] = COORD_SIZE - VLine_Length / 2 - 4; py[1] = COORD_SIZE - VLine_Length / 2 + 4; py[2] = COORD_SIZE - VLine_Length / 2;
		g.fillPolygon(px, py, 3);
		g.drawString(Hname, COORD_SIZE - Hname_Width, COORD_SIZE - VLine_Length / 2 + Hname_Height / 2);

		g.setColor(Vcolor);
		g.drawLine(HLine_Length / 2, COORD_SIZE, HLine_Length / 2, COORD_SIZE - VLine_Length);
		px[0] = HLine_Length / 2 - 4;           px[1] = HLine_Length / 2 + 4;           px[2] = HLine_Length / 2;
		py[0] = COORD_SIZE - VLine_Length + 18; py[1] = COORD_SIZE - VLine_Length + 18; py[2] = COORD_SIZE - VLine_Length;
		g.fillPolygon(px, py, 3);
		g.drawString(Vname, HLine_Length / 2 - Vname_Width / 2, Hname_Height);

		Cross_X = HLine_Length / 2;
		Cross_Y = COORD_SIZE - VLine_Length / 2;
	}
	@Override
	public void CoordinateUpdate(CoordTransEvent event) {
		// TODO Auto-generated method stub
		if(event.getEventType() == CoordTransEvent.EventType_MOVE || 
			event.getEventType() == CoordTransEvent.EventType_ZOOM || 
			event.getEventType() == CoordTransEvent.EventType_SIZE || 
			event.getEventType() == CoordTransEvent.EventType_SHOW) {
			CoordinateRG Coord = (CoordinateRG) event.getSource();
			super.setPos(Coord.getOrgX() - Cross_X, Coord.getOrgY() - Cross_Y);
		}
	}
}
