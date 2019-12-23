package kyLinkWaveTool.DataUnit;

import java.util.ArrayList;

public class kyLinkGroup {
	public String groupName = "package";
	public String groupId = "0x00";
	private ArrayList<kyLinkMember> mbrList = new ArrayList<kyLinkMember>();

	public kyLinkGroup() {}
	public kyLinkGroup(String name) {
		groupName = name;
	}
	public kyLinkGroup(String name, String id) {
		groupName = name;
		groupId = id;
	}
	public ArrayList<kyLinkMember> getMemberList() {
		return mbrList;
	}
	public kyLinkMember getMember(int index) {
		if(index < mbrList.size()) {
			return mbrList.get(index);
		}
		return null;
	}
	public void addMember(kyLinkMember mbr) {
		mbrList.add(mbr);
	}
	public void removeMember(kyLinkMember mbr) {
		mbrList.remove(mbr);
	}
	public void removeAllMember() {
		mbrList.clear();
	}
	public int getIdInteger() {
		int p = this.groupId.indexOf('x');
		if(p != -1) {
			return Integer.parseInt(groupId.substring(p + 1), 16);
		} else {
			return Integer.parseInt(groupId, 10);
		}
	}
}
