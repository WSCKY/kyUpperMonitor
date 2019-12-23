import java.util.concurrent.Semaphore;

public class SemaTest implements Runnable {

	private static Semaphore sem = new Semaphore(1);
private boolean exit_flag = false;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int cnt = 0;
		while(exit_flag != true) {
		try {
			sem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("count = " + ++ cnt);
		}
	}

	public void exit() {
		exit_flag = true;
	}

	public void release() {
		sem.release();
	}

	private Runnable run = new Runnable() {
		public void run() {
			while(exit_flag != true) {
				if(sem.tryAcquire()) {
					System.out.println("Acquire Success ..........");
				} else {
					System.out.println("Failed to acquire");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	};

	public void start_2() {
		(new Thread(run)).start();
	}

	public void tryacquire() {
		sem.tryAcquire();
	}

	public static void main(String[] args) {
		SemaTest st = new SemaTest();
//		(new Thread(st)).start();
		st.start_2();
		System.out.println("sleeping ...");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("release ... 1");
		st.release();
//		System.out.println("GIVE CHANCE ......");
//		st.tryacquire();
		System.out.println("sleeping ...");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("release ... 2");
		st.release();
		System.out.println("sleeping ...");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		st.exit();
		st.release();
	}
}
