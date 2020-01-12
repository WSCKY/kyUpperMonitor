package Calibration.Event;

import java.util.EventListener;

public interface CtrlEventListener extends EventListener {
	public void UserCtrlCommand(CtrlEvent event);
}
