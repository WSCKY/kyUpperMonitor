package kyLinkWaveTool.Observer;

import java.util.EventListener;

public interface ObserverEventListener extends EventListener {
	public void addObserverMember(ObserverEvent e);
	public void removeObserverMember(ObserverEvent e);
}
