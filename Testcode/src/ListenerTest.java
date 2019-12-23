import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class ListenerTest {
	private Semaphore ListMutex = new Semaphore(1);
	private ArrayList<ListenerManager> ListenerList = new ArrayList<ListenerManager>();

	public void addListener(TestListener l) {
		ListenerList.add(new ListenerManager(l));
	}
	public void removeListener(TestListener l) {
		try {
			ListMutex.acquire();
		} catch (InterruptedException e) {
			System.err.println("Failed to remove listener.");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(ListenerManager lm : ListenerList) {
			if(lm.equalsTo(l)) {
				ListenerList.remove(lm);
			}
		}
		ListMutex.release();
	}
	public void publishEvent(TestEvent event) {
		if(ListMutex.tryAcquire()) {
			for(ListenerManager l : ListenerList) {
				if(l.start(event)) {
					System.out.println("start success!");
				} else {
					System.out.println("start failed!");
				}
			}
			ListMutex.release();
		}
	}

	public static void main(String[] args) {
		ListenerTest MainTest = new ListenerTest();
		Listener1 lis1 = new Listener1();
		Listener2 lis2 = new Listener2();
		MainTest.addListener(lis1);
		MainTest.addListener(lis2);
		System.out.println("publish test event ...");
		MainTest.publishEvent(new TestEvent("event - 1"));
		System.out.println("publish test event again ...");
		MainTest.publishEvent(new TestEvent("event - 2"));
//		System.out.println("Main Thread Running ...");
		System.out.println("Main Thread wait 1.5s ...");
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Main Thread Wait Exit ...");
		System.out.println("publish test event again ...");
		MainTest.publishEvent(new TestEvent("event - 3"));
		System.out.println("publish test event again ...");
		MainTest.publishEvent(new TestEvent("event - 4"));
		System.out.println("Main Thread Exit ...");
	}
}

class Listener1 implements TestListener {

	@Override
	public void NewEvent(TestEvent e) {
		// TODO Auto-generated method stub
		System.out.println("I'M LISTENER 1, Event caller: " + e.getSource());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("LISTENER 1 EXIT!");
	}
	
}

class Listener2 implements TestListener {

	@Override
	public void NewEvent(TestEvent e) {
		// TODO Auto-generated method stub
		System.out.println("I'M LISTENER 2, Event caller: " + e.getSource());
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("LISTENER 2 EXIT!");
	}
	
}

class ListenerManager {
	private boolean status = false;
	private TestListener listener = null;
	private TestEvent event = null;
	public ListenerManager(TestListener listener) {
		this.listener = listener;
	}
	private Runnable run = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(listener != null) {
				listener.NewEvent(event);
			}
			status = false;
		}
	};

	public void setEvent(TestEvent e) {
		if(status == false) {
			this.event = e;
		}
	}

	public boolean start(TestEvent e) {
		this.setEvent(e);
		return this.start();
	}

	public boolean start() {
		if(status == false) {
			status = true;
			(new Thread(run)).start();
			return true;
		}
		return false;
	}
	public boolean isRunning() {
		return status;
	}
	public boolean equalsTo(TestListener listener) {
		return this.listener.equals(listener);
	}
}
