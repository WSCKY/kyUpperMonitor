package Calibration.Viewer3D;

import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Locale;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.media.j3d.VirtualUniverse;
import javax.swing.JFrame;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;

import Calibration.Viewer3D.Component.Arrow3D;
import Calibration.Viewer3D.Component.Coord3D;
import Calibration.Viewer3D.Component.Dot3D;

public class Viewer3D extends Canvas3D implements Runnable {
	private static final long serialVersionUID = 1L;

	static GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
	static GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
	static GraphicsDevice device = env.getDefaultScreenDevice();
	static GraphicsConfiguration config = device.getBestConfiguration(template);

	private ArrayList<BranchGroup> BranchList = null;

	public Viewer3D() {
		super(config);// (SimpleUniverse.getPreferredConfiguration());
		// TODO Auto-generated constructor stub
		/* create virtual universe */
		VirtualUniverse vu = new VirtualUniverse();
		Locale locale = new Locale(vu);
		// (create view branch)
		BranchGroup bgView = createViewBranch(this);
		bgView.compile();
		locale.addBranchGraph(bgView);
		BranchGroup bg = loadBranchGroup();
		bg.compile();
		locale.addBranchGraph(bg);

		BranchList = new ArrayList<BranchGroup>();

		this.addMouseListener(mouseLis);
	}

	/**
	 * view branch
	 * 
	 * @param cv Canvas3D object
	 */
	private BranchGroup createViewBranch(Canvas3D cv) {
		// 创建View组件对象
		View view = new View();
		// 设置投影方式
		view.setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
		// 创建ViewPlatform叶节点
		ViewPlatform vp = new ViewPlatform();
		view.addCanvas3D(cv);
		view.attachViewPlatform(vp);
		view.setPhysicalBody(new PhysicalBody());
		// 设置View对象属性
		view.setPhysicalEnvironment(new PhysicalEnvironment());
		// 几何变换
		Transform3D trans = new Transform3D();
		// 观察者眼睛的位置
		Point3d eye = new Point3d(0, 0, 4);
		// 观察者方向指向的点
		Point3d center = new Point3d(0, 0, 0);
		// 垂直于观察者方向向上的方向
		Vector3d vup = new Vector3d(0, 4, 0);
		// 生成几何变换矩阵
		trans.lookAt(eye, center, vup);
		// 求矩阵的逆
		trans.invert();
		// 几何变换组点
		TransformGroup tg = new TransformGroup(trans);
		tg.addChild(vp);
		// 创建视图分支
		BranchGroup bgView = new BranchGroup();
		bgView.addChild(tg);
		return bgView;
	}

	private Transform3D TransferObj;
	private TransformGroup TransGroup;
	private BranchGroup DotBranchGroup;
	private BranchGroup VecBranchGroup;
	/* load branch */
	private BranchGroup loadBranchGroup() {
		// 创建场景图分支
		BranchGroup objRoot = new BranchGroup();
		// 几何变换
		TransferObj = new Transform3D();
		/* default: show x-z panel */
		TransferObj.setRotation(new Quat4d(Math.sin(-Math.PI / 4), 0, 0, Math.cos(-Math.PI / 4)));
//		TransferObj.setScale(0.5f);// 缩放变换

		// 几何变换组节点
		TransGroup = new TransformGroup();
		TransGroup.setTransform(TransferObj);
		TransGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		TransGroup.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
		TransGroup.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		TransGroup.addChild(new Coord3D()); // add coordinate

		DotBranchGroup = new BranchGroup();
		DotBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		DotBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		DotBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		DotBranchGroup.setCapability(BranchGroup.ALLOW_DETACH);
		TransGroup.addChild(DotBranchGroup); // add dot group

		VecBranchGroup = new BranchGroup();
		VecBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		VecBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		VecBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		VecBranchGroup.setCapability(BranchGroup.ALLOW_DETACH);
		TransGroup.addChild(VecBranchGroup); // add vector group

		// 球体作用范围边界对象
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 30.0);
		// 添加通过鼠标左键控制3D物体旋转的对象
		MouseRotate behavior = new MouseRotate();
		behavior.setTransformGroup(TransGroup);
		behavior.setSchedulingBounds(bounds);
		TransGroup.addChild(behavior);
		// 添加鼠标右键的拖拉运动控制3D物体（X,Y）平移
		MouseTranslate tr = new MouseTranslate();
		tr.setTransformGroup(TransGroup);
		tr.setSchedulingBounds(bounds);
		TransGroup.addChild(tr);
		// 添加鼠标滚轮控制3D物体沿Z轴
		MouseWheelZoom tr1 = new MouseWheelZoom();
		tr1.setTransformGroup(TransGroup);
		tr1.setSchedulingBounds(bounds);
		TransGroup.addChild(tr1);
		objRoot.addChild(TransGroup);

		AmbientLight ambientLight = new AmbientLight(new Color3f(1.0f, 1.0f, 1.0f));
		ambientLight.setCapability(AmbientLight.ALLOW_COLOR_WRITE);
		ambientLight.setInfluencingBounds(bounds);
		objRoot.addChild(ambientLight);

		Background bgd = new Background(new Color3f(0.17f, 0.65f, 0.92f)); // sky color
		bgd.setApplicationBounds(bounds);
		objRoot.addChild(bgd);

		// 设置光源
		Color3f light1Color = new Color3f(Color.WHITE);
		Vector3f light1Direction = new Vector3f(0f, 0f, -10f);
		DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
		light1.setInfluencingBounds(bounds);
		objRoot.addChild(light1);
		return objRoot;
	}

	public void addDot3D(Dot3D d) {
		DotBranchGroup.addChild(d);
	}

	public int addDotBranch() {
		BranchGroup bg = new BranchGroup();
		bg.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		bg.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		bg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		bg.setCapability(BranchGroup.ALLOW_DETACH);
		TransGroup.addChild(bg); // add dot group
		BranchList.add(bg);
		return BranchList.indexOf(bg);
	}
	public void addDot3DTo(Dot3D d, int idx) {
		BranchGroup bg = BranchList.get(idx);
		bg.addChild(d);
	}
	public void removeDotBranch() {
		
	}
	public void removeAllDot(int idx) {
		BranchGroup bg = BranchList.get(idx);
		bg.removeAllChildren();
	}

	MouseAdapter mouseLis = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount() == 2) {
				TransferObj.setRotation(new Quat4d(Math.sin(-Math.PI / 4), 0, 0, Math.cos(-Math.PI / 4)));
				TransGroup.setTransform(TransferObj);
			}
		}
	};

	@Override
	public void run() {
		// TODO Auto-generated method stub
		int i = 100;
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		do {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			double rad1 = Math.toRadians((Math.random() * 360));
			double rad2 = Math.toRadians((Math.random() * 360));
			float z = (float) Math.sin(rad1);
			float l = (float) Math.cos(rad1);
			float x = (float) (l * Math.sin(rad2));
			float y = (float) (l * Math.cos(rad2));
			addDot3D(new Dot3D(x, y, z, new Color3f(0, 0, 1)));
			VecBranchGroup.addChild(new Arrow3D(new Vector3f(x, y, z), new Color3f(0, 1, 0)));
		} while((-- i) != 0);
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		DotBranchGroup.removeAllChildren();
		VecBranchGroup.removeAllChildren();
	}

	public static void main(String[] args) {
		JFrame f = new JFrame("Viewer3D Test");
		f.setSize(1000, 800);
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Viewer3D vp = new Viewer3D();
		f.add(vp);
		f.setVisible(true);
		(new Thread(vp)).start();
	}
}
