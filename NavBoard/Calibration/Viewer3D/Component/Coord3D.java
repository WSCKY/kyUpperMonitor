package Calibration.Viewer3D.Component;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Cone;

public class Coord3D extends BranchGroup {
	private Color3f RED = new Color3f(1, 0, 0);
	private Color3f GREEN = new Color3f(0, 1, 0);
	private Color3f BLUE = new Color3f(0, 0, 1);
	public Coord3D() {
		this.addChild(new CoordArrow(new Vector3f(.0f, 1.0f, .0f), 
				new AxisAngle4d(0, 1, 0, Math.toRadians(0)), GREEN)); // Y
		this.addChild(new CoordArrow(new Vector3f(1.0f, .0f, .0f), 
				new AxisAngle4d(0, 0, 1, Math.toRadians(-90)), RED)); // X
		this.addChild(new CoordArrow(new Vector3f(.0f, .0f, 1.0f), 
				new AxisAngle4d(1, 0, 0, Math.toRadians(90)), BLUE)); // Z
		this.addChild(new CoordAxis());
	}
}

class CoordArrow extends BranchGroup {
	public CoordArrow(Vector3f v, AxisAngle4d r, Color3f c) {
		TransformGroup tg = new TransformGroup();
		Transform3D transform = new Transform3D();
		transform.setTranslation(v);
		Transform3D rotTrans = new Transform3D();
		rotTrans.setRotation(r);
		transform.mul(rotTrans);
		tg.setTransform(transform);

		Appearance ap = new Appearance();
		ColoringAttributes colorAtt = new ColoringAttributes();
		colorAtt.setColor(c);
		ap.setColoringAttributes(colorAtt);
//		TransparencyAttributes t_attr = new TransparencyAttributes(TransparencyAttributes.BLENDED, 
//				0.5f, TransparencyAttributes.BLEND_SRC_ALPHA, TransparencyAttributes.BLEND_ONE);
		TransparencyAttributes t_attr = new TransparencyAttributes(TransparencyAttributes.BLENDED, 0.5f);
		ap.setTransparencyAttributes( t_attr );
		Cone x_cone = new Cone(0.05f, 0.25f, ap);
		tg.addChild(x_cone);
		this.addChild(tg);
	}
}

class CoordAxis extends Shape3D {
	private float vert[] = { // 直线的定点坐标
    		-1.0f,0.0f,0.0f, 1.0f,0.0f,0.0f,
    		0.0f,-1.0f,0.0f, 0.0f,1.0f,0.0f,
    		0.0f,0.0f,-1.0f, 0.0f,0.0f,1.0f};
	private float color[] = { // 各定点的颜色
			1.0f,0.0f,0.0f,  1.0f,0.0f,0.0f,
			0.0f,1.0f,0.0f,  0.0f,1.0f,0.0f,
			0.0f,0.0f,1.0f,  0.0f,0.0f,1.0f};

	public CoordAxis() {
		LineArray line = new LineArray(6, LineArray.COORDINATES | LineArray.COLOR_3); // 创建直线数组对象
		line.setCoordinates(0, vert); // 设置直线对象的坐标数组
		line.setColors(0, color); // 设置直线对象的颜色数组
		LineAttributes lineAtt = new LineAttributes(); // 创建直线属性对象
		lineAtt.setLineWidth(1.0f); // 设置线宽
		lineAtt.setLineAntialiasingEnable(true); // 设置直线的渲染效果

		Appearance ap = new Appearance();
		ap.setLineAttributes(lineAtt);
//		TransparencyAttributes t_attr = new TransparencyAttributes(TransparencyAttributes.BLENDED, 
//				0.5f, TransparencyAttributes.BLEND_SRC_ALPHA, TransparencyAttributes.BLEND_ONE);
		TransparencyAttributes t_attr = new TransparencyAttributes(TransparencyAttributes.BLENDED, 0.5f);
		ap.setTransparencyAttributes( t_attr );
		this.setGeometry(line);
		this.setAppearance(ap);
	}
}
