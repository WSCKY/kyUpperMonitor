package Canvas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import Canvas.uiComponent.uiComponent;
import Coordinate.CoordTransEvent;
import Coordinate.CoordTransEventListener;
import Coordinate.CoordinateRG;

public class myCanvas extends JPanel implements CoordTransEventListener {
	private static final long serialVersionUID = 1L;
	private static final Color BackColor = new Color(180, 180, 180);

	private CoordinateRG coord = null;
	private Image DeskTop = null;
	private ArrayList<uiComponent> Layers = new ArrayList<uiComponent>();
	private ArrayList<uiComponent> topLayers = new ArrayList<uiComponent>();
	private ArrayList<uiComponent> bottomLayers = new ArrayList<uiComponent>();

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
		synchronized(Layers) {
			Layers.add(ui);
		}
	}
	public void delLayer(uiComponent ui) {
		synchronized(Layers) {
			Layers.remove(ui);
		}
	}
	public void addTopLayer(uiComponent ui) {
		synchronized(topLayers) {
			topLayers.add(ui);
		}
	}
	public void delTopLayer(uiComponent ui) {
		synchronized(topLayers) {
			topLayers.remove(ui);
		}
	}
	public void addBottomLayer(uiComponent ui) {
		synchronized(bottomLayers) {
			bottomLayers.add(ui);
		}
	}
	public void delBottomLayer(uiComponent ui) {
		synchronized(bottomLayers) {
			bottomLayers.remove(ui);
		}
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
		synchronized(topLayers) {
			for(uiComponent ui : topLayers) {
				g.drawImage(ui.getImage(), ui.getXPos(), ui.getYPos(), this);
			}
		}
		synchronized(Layers) {
			for(uiComponent ui : Layers) {
				g.drawImage(ui.getImage(), ui.getXPos(), ui.getYPos(), this);
			}
		}
		synchronized(bottomLayers) {
			for(uiComponent ui : bottomLayers) {
				g.drawImage(ui.getImage(), ui.getXPos(), ui.getYPos(), this);
			}
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
		public void mouseClicked(MouseEvent e) {
			
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			coord.move(e.getX() - xMouse, e.getY() - yMouse);
			xMouse = e.getX();
			yMouse = e.getY();
			coord.refreshAll(CoordTransEvent.EventType_MOVE);
		}
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			// TODO Auto-generated method stub
			Point2D.Double pr = coord.UI2Real(e.getX(), e.getY());
			coord.zoom(-e.getWheelRotation() * 0.2 + 1.0);
			Point pu = coord.Real2UI(pr.x, pr.y);
			coord.move(e.getX() - pu.x, e.getY() - pu.y);
			coord.refreshAll(CoordTransEvent.EventType_ZOOM);
//			System.out.println("wheel: " + e.getWheelRotation() + ", tg: " + coord.getTransGain());
		}
	};

	private ComponentAdapter compListener = new ComponentAdapter() {
		@Override
		public void componentResized(ComponentEvent e) {
			// TODO Auto-generated method stub
			coord.setUISize(getWidth(), getHeight());
			coord.refreshAll(CoordTransEvent.EventType_SIZE);
		}
	};

	@Override
	public void CoordinateUpdate(CoordTransEvent event) {
		// TODO Auto-generated method stub
		this.repaint();
	}
}
