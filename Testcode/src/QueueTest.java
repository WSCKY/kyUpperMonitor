import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueTest implements Runnable {
	private ConcurrentLinkedQueue<Integer> IntQueue = new ConcurrentLinkedQueue<Integer>();
	public QueueTest() {
		(new Thread(this)).start();
	}
	public static void main(String args[]) {
		new QueueTest();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int idx = 4;
		int a = 0;
		System.out.println("test take one data: " + IntQueue.poll());
		do {
			a ++;
			System.out.print(idx + ": put a = " + a);
			IntQueue.offer(a);
			System.out.println("... done!");
		} while((-- idx) != 0);
		System.out.println("delay 1s ...");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		idx = 3;
		do {
			System.out.println("take one data: " + IntQueue.poll());
		} while((idx --) != 0);
	}
}
