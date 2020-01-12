package Calibration.Event;

import java.util.EventObject;

public class CtrlEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	public static final int CTRL_EVENT_NULL = 0;
	public static final int CTRL_EVENT_START = 1;
	public static final int CTRL_EVENT_STOP = 2;
	public static final int CTRL_EVENT_REFRESH = 3;
	public static final int CTRL_EVENT_SAVE = 4;

	private int EventId = CTRL_EVENT_NULL;

	public CtrlEvent(Object source, int eid) {
		super(source);
		// TODO Auto-generated constructor stub
		this.EventId = eid;
	}

	public int getEventId() {
		return this.EventId;
	}
}
