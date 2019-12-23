import java.util.EventListener;

public interface TestListener extends EventListener {
	public boolean status = false;
	public void NewEvent(TestEvent e);
}
