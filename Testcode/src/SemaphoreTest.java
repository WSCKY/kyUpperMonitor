import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class SemaphoreTest {
	public static void main(String[] args) {
		final Semaphore semp = new Semaphore(3);
		ExecutorService exec = Executors.newCachedThreadPool();
		for (int index = 0; index < 20; index++) {
		    final int NO = index;
		    Runnable run = new Runnable() {
		        public void run() {
		            try {
		                semp.acquire();
		                System.out.println("Accessing: " + NO);
		                Thread.sleep(500);
		                semp.release();
		                System.out.println("-----------------" + semp.availablePermits());
		            } catch (InterruptedException e) {
		                e.printStackTrace();
		            }
		        }
		    };
		    exec.execute(run);
		}
		exec.shutdown();
	}
}
