import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockQueue {
	private BlockingQueue<Integer> IntQueue = new ArrayBlockingQueue<Integer>(5);
	private Runnable runA = new Runnable() {
		public void run() {
			int cnt = 8;
			int a = 0;
			do {
				a ++;
				IntQueue.offer(a);
//				try {
//					IntQueue.put(a);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				System.out.println("put Integer " + a + " ... done.");
			} while((-- cnt) != 0);
		}
	};

	public void startA() {
		(new Thread(runA)).start();
	}
	public void takeData() {
		try {
			System.out.println("take data is: " + IntQueue.take());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		BlockQueue bq = new BlockQueue();
		bq.startA();
		bq.takeData();
		bq.takeData();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0; i < 6; i ++) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			bq.takeData();
		}
	}
}
