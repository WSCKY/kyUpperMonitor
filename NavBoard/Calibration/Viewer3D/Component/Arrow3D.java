package Calibration.Viewer3D.Component;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class Arrow3D extends BranchGroup {
	private Point3f p1, p2;
	private Color3f c;
	private LineArray line;

	public Arrow3D() {
		this(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Color3f(1, 1, 1));
	}

	public Arrow3D(Vector3f ve) {
		this(new Vector3f(0, 0, 0), ve, new Color3f(1, 1, 1));
	}

	public Arrow3D(Vector3f ve, Color3f cl) {
		this(new Vector3f(0, 0, 0), ve, cl);
	}

	public Arrow3D(Vector3f vs, Vector3f ve, Color3f cl) {
		p1 = new Point3f(vs.x, vs.y, vs.z);
		p2 = new Point3f(ve.x, ve.y, ve.z);
		c = cl;
		line = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
		line.setCoordinate(0, p1);
		line.setCoordinate(1, p2);
		line.setColor(0, c);
		line.setColor(1, c);
		Shape3D s = new Shape3D();
		s.setGeometry(line);
		this.addChild(s);
		this.setCapability(BranchGroup.ALLOW_DETACH);
	}
}
