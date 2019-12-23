package NodeManage;

import java.util.EventObject;

public class NodeManagerEvent extends EventObject {
	private static final long serialVersionUID = 0xC8;

	public static final int ADD = 0;
	public static final int DEL = 1;
	public static final int UPD = 2;

	private int Type = ADD;
	private int Index = 0;

	public NodeManagerEvent(Object source, int idx, int type) {
		super(source);
		// TODO Auto-generated constructor stub
		this.Type = type;
		this.Index = idx;
	}

	public int getType() {
		return this.Type;
	}

	public int getIndex() {
		return this.Index;
	}
}
