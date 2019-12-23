
public class NotifyTest {
	private Object obj = new Object();
	private Thread ThreadA = null;
	private Thread ThreadB = null;
	private boolean exit = false;
	public NotifyTest() {
		ThreadA = new Thread(runA);
		ThreadB = new Thread(runB);
	}

	public void StartTaskA() {
		ThreadA.start();
	}
	public void StartTaskB() {
		ThreadB.start();
	}

	private Runnable runA = new Runnable() {
		public void run() {
			System.out.println("Task A Running ...");
			while(!exit) {
				synchronized(obj) {
					try {
						obj.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("A: received notify signal ...");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("A: wait notify signal ...");
				}
			}
			System.out.println("A: EXIT ...");
		}
	};
	private Runnable runB = new Runnable() {
		public void run() {
			System.out.println("Task B Running ...");
			while(!exit) {
				synchronized(obj) {
					try {
						obj.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("B: received notify signal ...");
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("B: wait notify signal ...");
			}
			System.out.println("B: EXIT ...");
		}
	};
	public void exit() {
		exit = true;
	}
	public void trig() {
		synchronized(obj) {
			obj.notifyAll();
		}
	}
	public static void main(String[] args) {
		NotifyTest nt = new NotifyTest();
		nt.StartTaskB();
		nt.StartTaskA();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("TEST TRIG >>>>>>>");
		nt.trig();
		System.out.println("TEST TRIG >>>>>>>");
		nt.trig();
		System.out.println("TEST TRIG DONE >>>>>>>>");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("/* ---------- TEST EXIT ---------- */");
		nt.exit();
		nt.trig();
	}
}
