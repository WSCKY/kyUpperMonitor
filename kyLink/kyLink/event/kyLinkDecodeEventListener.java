package kyLink.event;

import java.util.EventListener;

public interface kyLinkDecodeEventListener extends EventListener {
	public void getNewPackage(kyLinkDecodeEvent event);
	public void badCRCEvent(kyLinkDecodeEvent event);
	public void lenOverFlow(kyLinkDecodeEvent event);
}
