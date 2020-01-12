package Calibration.ViewerChart;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Calibration.ViewerChart.Component.Coord2D;
import Calibration.ViewerChart.Component.Dot2D;
import Calibration.ViewerChart.Component.Line2D;

public class ViewerChart extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;

	private Semaphore DotListMutex = null;
	private ArrayList<Dot2D> DotList = null;
	private Semaphore LineListMutex = null;
	private ArrayList<Line2D> LineList = null;

	private Coord2D coord = null;
	public ViewerChart(String hName, String vName, Color hColor, Color vColor) {
		coord = new Coord2D(hName, vName, hColor, vColor);

		DotListMutex = new Semaphore(1);
		DotList = new ArrayList<Dot2D>();
		LineListMutex = new Semaphore(1);
		LineList = new ArrayList<Line2D>();

//		DotList.add(new Dot2D(1, 1, Color.WHITE));
//		DotList.add(new Dot2D(-1, 1, Color.RED));
//		DotList.add(new Dot2D(1, -1, Color.BLUE));
//		DotList.add(new Dot2D(-1, -1, Color.GREEN));
//		DotList.add(new Dot2D(0.5f, 0.8f, Color.WHITE));
//
//		LineList.add(new Line2D(1, 0, 1, 1, Color.WHITE));
//		LineList.add(new Line2D(-1, 0, -1, 1, Color.RED));
//		LineList.add(new Line2D(1, 0, 1, -1, Color.BLUE));
//		LineList.add(new Line2D(-1, 0,- 1, -1, Color.GREEN));
//		LineList.add(new Line2D(0.5f, 0, 0.5f, 0.8f, Color.WHITE));
	}

	public void addDot2D(Dot2D d) {
		try {
			DotListMutex.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DotList.add(d);
		DotListMutex.release();
//		if(this.isShowing() == true) {
//			Graphics g = this.getGraphics();
//			coord.transform(d);
//			d.show(g);
//		}
	}

	public void removeAllDot2D() {
		try {
			DotListMutex.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DotList.clear();
		DotListMutex.release();
//		if(this.isShowing() == true) {
//			this.repaint();
//		}
	}

	public void addLine2D(Line2D l) {
		try {
			LineListMutex.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LineList.add(l);
		LineListMutex.release();
//		if(this.isShowing() == true) {
//			Graphics g = this.getGraphics();
//			coord.transform(l);
//			l.show(g);
//		}
	}

	public void removeAllLine2D() {
		try {
			LineListMutex.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LineList.clear();
		LineListMutex.release();
//		if(this.isShowing() == true) {
//			this.repaint();
//		}
	}

	public void refresh() {
		if(this.isShowing() == true) {
			this.repaint();
		}
	}

	public void paintComponent(Graphics g) {
		g.setColor(new Color(43, 166, 235));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		coord.update(this.getWidth(), this.getHeight());
		coord.show(g);
		try {
			DotListMutex.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(Dot2D d : DotList) {
			coord.transform(d);
			d.show(g);
		}
		DotListMutex.release();
		try {
			LineListMutex.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(Line2D l : LineList) {
			coord.transform(l);
			l.show(g);
		}
		LineListMutex.release();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		int i = 50;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		do {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			double rad = Math.random() * Math.PI * 2;
			float x = (float) Math.sin(rad);
			float y = (float) Math.cos(rad);
			addDot2D(new Dot2D(x, y, Color.WHITE));
			addLine2D(new Line2D(0, 0, x, y, Color.GREEN));
		} while((-- i) != 0);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		removeAllLine2D();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		removeAllDot2D();
	}

	public static void main(String[] args) {
		JFrame jf = new JFrame("Viewer Chart Test View");
		jf.setSize(800, 800);
		jf.setLocationRelativeTo(null);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ViewerChart vc = new ViewerChart("x", "y", Color.RED, Color.GREEN);
		jf.add(vc);
		jf.setVisible(true);
		(new Thread(vc)).start();
	}
}
