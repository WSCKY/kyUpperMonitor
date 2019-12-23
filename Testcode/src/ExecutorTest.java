import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorTest {
	private ExecutorService exec = Executors.newCachedThreadPool();
	private Runnable run = new Runnable() {
		public void run() {
			System.out.println("START RUN");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("STOP RUN");
		}
	};
	public void StartTest() {
		exec.execute(run);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		exec.execute(run);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		exec.execute(run);
		System.out.println("Executor Shutdown");
		exec.shutdown();
	}
	public static void main(String[] args) {
		ExecutorTest et = new ExecutorTest();
		et.StartTest();
	}
}
