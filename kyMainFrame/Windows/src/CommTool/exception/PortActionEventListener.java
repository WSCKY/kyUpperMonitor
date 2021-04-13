package CommTool.exception;

import java.util.EventListener;

public interface PortActionEventListener extends EventListener {
	public static enum PortActionCode {
		PortAction_Opened,
		PortAction_Closed,
		PortAction_Failed,
	}
	public void PortNewActionOccured(PortActionCode code);
}
