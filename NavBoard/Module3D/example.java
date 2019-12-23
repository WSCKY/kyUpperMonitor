package Module3D;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

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
import javax.swing.UIManager;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;

public class example extends Canvas3D implements Runnable {
	private static final long serialVersionUID = 1L;
//	private Semaphore semaphore = null;

	static GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
	static GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
	static GraphicsDevice device = env.getDefaultScreenDevice();
	static GraphicsConfiguration config = device.getBestConfiguration(template);

	public example() {
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

//        (new Thread(this)).start();
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
		Vector3d vup = new Vector3d(0, 1, 0);
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

	/* load branch */
	private BranchGroup loadBranchGroup() {
		// 创建一个用来包含对象的数据结构
		BranchGroup bg = new BranchGroup();

		myCube mcb = new myCube();
		bg.addChild(mcb);
//		ColorCube cube = new ColorCube(0.5);
//		Text2D txt = new Text2D("kyChu", new Color3f(Color.RED), "Courier New", 20, Font.BOLD);
//		bg.addChild(cube);
//		bg.addChild(txt);
		// 创建场景图分支
		BranchGroup objRoot = new BranchGroup();
		// 几何变换
		TransferObj = new Transform3D();
		TransferObj.setScale(0.6f);// 缩放变换
		TransferObj.setTranslation(new Vector3d(1, 0, 0));

		// 几何变换组节点
		TransGroup = new TransformGroup();
		TransGroup.setTransform(TransferObj);
		TransGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		TransGroup.addChild(bg);
		objRoot.addChild(TransGroup);
		// 球体作用范围边界对象
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 300.0);
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

	public Quat4f constQuat(Vector3f en, double rad) {
		en.normalize();
		float w = (float) Math.cos(rad / 2.0);
		float p = (float) Math.sin(rad / 2.0);
		float x = p * en.x;
		float y = p * en.y;
		float z = p * en.z;
		return new Quat4f(x, y, z, w);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Quat4f rotQ;
		Vector3f rotV = new Vector3f(1, 1, 1);
		double deg = 0;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (true) {
			rotQ = constQuat(rotV, Math.toRadians(deg));
			TransferObj.setRotation(rotQ);
			TransGroup.setTransform(TransferObj);
			deg++;
			if (deg >= 360)
				deg -= 360;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Couldn't use system look and feel.");
		}
		JFrame mf = new JFrame("3D show example");
		mf.setSize(1000, 600);
		mf.setLayout(new BorderLayout());
		example bdy = new example();
		mf.add(bdy, BorderLayout.CENTER);
		mf.setLocationRelativeTo(null);
		mf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mf.setResizable(false);
		mf.setVisible(true);
	}
}
