package Calibration.Viewer3D.Component;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Sphere;

public class Dot3D extends BranchGroup {
	private float x, y, z;
	private Color3f c;
	public Dot3D() {
		this(0, 0, 0, new Color3f(1, 0, 0));
	}

	public Dot3D(Color3f c) {
		this(0, 0, 0, c);
	}

	public Dot3D(float x, float y, float z) {
		this(x, y, z, new Color3f(1, 0, 0));
	}

	public Dot3D(float x, float y, float z, Color3f c) {
		this.x = x; this.y = y; this.z = z; this.c = c;
		TransformGroup tg = new TransformGroup();
		Transform3D transform = new Transform3D();
		transform.setTranslation(new Vector3f(this.x, this.y, this.z));
		tg.setTransform(transform);
		Appearance ap = new Appearance();
		ColoringAttributes colorAtt = new ColoringAttributes();
		colorAtt.setColor(this.c);
		ap.setColoringAttributes(colorAtt);
		Sphere dot = new Sphere(.02f, ap);
		tg.addChild(dot);
		this.addChild(tg);
		this.setCapability(BranchGroup.ALLOW_DETACH);
	}
}
