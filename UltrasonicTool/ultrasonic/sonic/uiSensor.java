package sonic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import Canvas.uiComponent.uiComponent;

public class uiSensor extends uiComponent {
	private static final String ImgFile = "C:\\kyChu\\MyMonitor\\UltrasonicTool\\ultrasonic\\image\\sonic3d_s.png";//"sonic3d_s.png";//
	private BufferedImage img = null;
	public uiSensor(int xp, int yp) {
		super();
		try {
			img = ImageIO.read(new File(ImgFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setImage(img, img.getWidth(), img.getHeight());
		this.setPos(xp - img.getWidth() / 2, yp - img.getHeight() / 2);
	}

	public void setPos(int x, int y) {
		super.setPos(x - img.getWidth() / 2, y - img.getHeight() / 2);
	}
}
