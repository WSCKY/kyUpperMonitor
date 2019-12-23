package NodeManage;

import java.util.EventListener;

public interface NodeManagerEventListener extends EventListener {
	public void NodeListUpdate(NodeManagerEvent event);
}
