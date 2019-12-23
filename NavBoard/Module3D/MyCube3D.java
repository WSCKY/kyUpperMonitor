package Module3D;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.concurrent.Semaphore;

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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;

import NavBoardTool.TypeNavBoard;
import kyLink.kyLinkPackage;
import kyLink.event.kyLinkDecodeEvent;
import kyLink.event.kyLinkDecodeEventListener;
import kyMainFrame.kyMainFrame;

public class MyCube3D extends Canvas3D implements Runnable, kyLinkDecodeEventListener {
	private static final long serialVersionUID = 1L;
	private kyLinkPackage rxData = null;
	private Semaphore semaphore = null;
	private Quat4f InitialQuat = null;

	static GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
	static GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
	static GraphicsDevice device = env.getDefaultScreenDevice();
	static GraphicsConfiguration config = device.getBestConfiguration(template);

	public MyCube3D() {
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

		InitialQuat = new Quat4f(0, 0, 0, 1);
//        InitialQuat.mul(constQuat(new Vector3f(1.f, .0f, .0f), Math.toRadians(90)));
//        InitialQuat.mul(constQuat(new Vector3f(.0f, .0f, 1.f), Math.toRadians(90)));
//		InitialQuat.mul(constQuat(new Vector3f(.0f, -1.f, .0f), Math.toRadians(90)));

		infoLabel.setFont(new Font("Courier New", Font.BOLD, 18));
		infoPanel.add(infoLabel);

		semaphore = new Semaphore(1, true);
		(new Thread(this)).start();
	}

	public JPanel infoPanel = new JPanel();
	public JLabel infoLabel = new JLabel("Pitch: " + 0 + "     Roll: " + 0 + "     Yaw: " + 0);

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

		ObjectFile objFile = new ObjectFile(ObjectFile.RESIZE);// , 100);
		Scene scenen = null;
		try {
			scenen = objFile.load("C:\\kyChu\\MyMonitor\\resoure\\fighter_model\\atmo_fighter2.obj");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("OBJ Module Load Failed" + e.getMessage());
		}
		bg.addChild(scenen.getSceneGroup());
		// 创建场景图分支
		BranchGroup objRoot = new BranchGroup();
		// 几何变换
		TransferObj = new Transform3D();
		TransferObj.setScale(0.6f);// 缩放变换

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

	private final float RAD_TO_DEG = 57.295779513082320876798154814105f;
	private Vector3f quat2euler(Quat4f q) {
		Vector3f eur = new Vector3f();
		eur.x = (float) (Math.atan2(2 * (q.w * q.x + q.y * q.z) , 1 - 2 * (q.x * q.x + q.y * q.y)) * RAD_TO_DEG);
		eur.y = (float) (Math.asin(2 * (q.w * q.y - q.z * q.x)) * RAD_TO_DEG);
		eur.z = (float) (Math.atan2(2 * (q.w * q.z + q.x * q.y) , 1 - 2 * (q.y * q.y + q.z * q.z)) * RAD_TO_DEG);
		return eur;
	}

	float qw = 1.0f, qx = 0.0f, qy = 0.0f, qz = 0.0f;

	@Override
	public void getNewPackage(kyLinkDecodeEvent event) {
		// TODO Auto-generated method stub
		rxData = (kyLinkPackage) event.getSource();
		if (rxData.msg_id == TypeNavBoard.TYPE_ATT_QUAT_Resp) {
			qw = rxData.readoutFloat(0);
			qx = rxData.readoutFloat(4);
			qy = rxData.readoutFloat(8);
			qz = rxData.readoutFloat(12);
			semaphore.release();
		}
	}

	@Override
	public void badCRCEvent(kyLinkDecodeEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void lenOverFlow(kyLinkDecodeEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Quat4f q = new Quat4f(qx, qz, -qy, qw);
			q.mul(InitialQuat);
			TransferObj.setRotation(q);
			TransGroup.setTransform(TransferObj);
			Vector3f eur = quat2euler(new Quat4f(qx, qy, qz, qw));
			eur.z = -eur.z;
			if(eur.z < 0) eur.z = 360 + eur.z;
			infoLabel.setText(String.format("Pitch: " + "%3.2f" + "     Roll: " + "%3.2f" + "     Yaw: " + "%3.2f", eur.x, eur.y, eur.z));
		}
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Couldn't use system look and feel.");
		}
		kyMainFrame mf = new kyMainFrame();
		mf.setTitle("Wave Player Test");
		mf.setSize(1000, 600);
		JPanel mp = mf.getUsrMainPanel();
		mp.setLayout(new BorderLayout());
		MyCube3D bdy = new MyCube3D();
		mp.add(bdy, BorderLayout.CENTER);
		mp.add(bdy.infoPanel, BorderLayout.SOUTH);
		mf.addDecodeListener(bdy);
		mf.setResizable(false);
		mf.setVisible(true);
	}
}
