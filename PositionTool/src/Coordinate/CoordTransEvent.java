package Coordinate;

import java.util.EventObject;

public class CoordTransEvent extends EventObject {
	private static final long serialVersionUID = 0x90;

	public static final int EventType_NULL = 0;
	public static final int EventType_MOVE = 1;
	public static final int EventType_ZOOM = 2;
	public static final int EventType_SIZE = 3;
	public static final int EventType_SHOW = 4;

	private int eType = EventType_NULL;
	public CoordTransEvent(Object source, int type) {
		super(source);
		// TODO Auto-generated constructor stub
		eType = type;
	}
	public int getEventType() {
		return eType;
	}
}
