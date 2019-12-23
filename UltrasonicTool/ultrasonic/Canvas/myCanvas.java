package Canvas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import Canvas.uiComponent.uiComponent;
import Coordinate.CoordinateRG;

public class myCanvas extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Color BackColor = new Color(180, 180, 180);

	private CoordinateRG coord = null;
	private Image DeskTop = null;
	private ArrayList<uiComponent> Layers = new ArrayList<uiComponent>();

	private boolean ZoomEnable = false;
	private boolean DragEnable = false;

	public myCanvas() {
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		DeskTop = new BufferedImage(screensize.width, screensize.height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = DeskTop.getGraphics();
		g.setColor(BackColor);
		g.fillRect(0, 0, screensize.width, screensize.height);

		this.addComponentListener(compListener);
		if(DragEnable) {
			this.addMouseListener(mouseListener);
			this.addMouseMotionListener(mouseListener);
		}
		if(ZoomEnable) {
			this.addMouseWheelListener(mouseListener);
		}
	}

	public myCanvas(CoordinateRG c) {
		this();
		coord = c;
	}

	public void setCoordTrans(CoordinateRG c) {
		coord = c;
	}
	public void addLayer(uiComponent ui) {
		Layers.add(ui);
	}
	public void delLayer(uiComponent ui) {
		Layers.remove(ui);
	}
	public void setDeskTop(Image img) {
		DeskTop = img;
	}

	public void setDragEnable(boolean e) {
		if(e != DragEnable) {
			if(DragEnable) {
				this.removeMouseListener(mouseListener);
				this.removeMouseMotionListener(mouseListener);
			} else {
				this.addMouseListener(mouseListener);
				this.addMouseMotionListener(mouseListener);
			}
			DragEnable = e;
		}
	}

	public void setZoomEnable(boolean e) {
		if(e != ZoomEnable) {
			if(ZoomEnable) {
				this.removeMouseWheelListener(mouseListener);
			} else {
				this.addMouseWheelListener(mouseListener);
			}
			ZoomEnable = e;
		}
	}

	public void paintComponent(Graphics g) {
		g.drawImage(DeskTop, 0, 0, this);
		for(uiComponent ui : Layers) {
			g.drawImage(ui.getImage(), ui.getXPos(), ui.getYPos(), this);
		}
	}

	private int xMouse = 0, yMouse = 0;
	private MouseAdapter mouseListener = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			xMouse = e.getX();
			yMouse = e.getY();
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			coord.move(e.getX() - xMouse, e.getY() - yMouse);
			xMouse = e.getX();
			yMouse = e.getY();
		}
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			// TODO Auto-generated method stub
			coord.zoom(-e.getWheelRotation() * 0.2 + 1.0);
		}
	};

	private ComponentAdapter compListener = new ComponentAdapter() {
		@Override
		public void componentResized(ComponentEvent e) {
			// TODO Auto-generated method stub
			coord.setImageArea(getWidth(), getHeight());
		}
	};
}
