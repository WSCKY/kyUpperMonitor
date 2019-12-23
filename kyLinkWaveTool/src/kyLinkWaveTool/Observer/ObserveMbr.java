package kyLinkWaveTool.Observer;

import java.awt.Color;

public class ObserveMbr {
	private int groupId = -1;
	private int mbrOffset = 0;
	private String mbrType = "";
	private String mbrName = "Datax";
	private Color color = Color.RED;

	public ObserveMbr() {}
	public ObserveMbr(Color c) { this.color = c; }
	public ObserveMbr(int gid) { this.groupId = gid; }
	public ObserveMbr(String name) { this.mbrName = name; }
	public ObserveMbr(int gid, String type, int off) { this.groupId = gid; this.mbrType = type; this.mbrOffset = off; }
	public ObserveMbr(Color c, int gid, String type, int off) { this.color = c; this.groupId = gid; this.mbrType = type; this.mbrOffset = off; }

	public void setColor(Color c) { this.color = c; }
	public void setGroupId(int id) { this.groupId = id; }
	public void setMbrType(String type) { this.mbrType = type; }
	public void setMbrOffset(int off) { this.mbrOffset = off; }
	public void setMbrInfo(String type, int off) { this.mbrType = type; this.mbrOffset = off; }
	public void setMbrName(String name) { this.mbrName = name; }

	public Color getColor() { return this.color; }
	public int getGroupId() { return this.groupId; }
	public int getMbrOffset() { return this.mbrOffset; }
	public String getMbrType() { return this.mbrType; }
	public String getMbrName() { return this.mbrName; }
}
