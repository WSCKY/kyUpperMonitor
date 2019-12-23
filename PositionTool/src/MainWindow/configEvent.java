package MainWindow;

import java.util.EventObject;

public class configEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	public static final int CMD_NULL = 0;
	public static final int PAINT_START = 1;
	public static final int PAINT_STOP = 2;
	public static final int PAINT_CLEAR = 3;
	public static final int MOVE_TO_CENTER = 4;
	public static final int AUTO_SCALE = 5;
	public static final int RESET_VIEW = 6;
	public static final int LOAD_GNSS_DATA = 7;
	public static final int SAVE_GNSS_DATA = 8;

	private int eCmd = CMD_NULL;
	private Object eParam = null;
	public configEvent(Object source, int cmd) {
		super(source);
		// TODO Auto-generated constructor stub
		eCmd = cmd;
	}

	public configEvent(Object source, int cmd, Object param) {
		super(source);
		this.eCmd = cmd;
		this.eParam = param;
	}

	public Object getParam() {
		return this.eParam;
	}
	public int getCommand() {
		return this.eCmd;
	}
}
