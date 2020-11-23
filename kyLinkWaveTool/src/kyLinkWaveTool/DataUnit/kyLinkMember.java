package kyLinkWaveTool.DataUnit;

public class kyLinkMember {
	public String mbrName = "unknow";
	public String mbrType = "uint8_t";
	public int mbrOffset = 0;
	public float mbrValue = 0;
	public static final String[] DataTypes = 
		{"uint8_t", "int8_t", "uint16_t", "int16_t", "uint32_t", "int32_t", "float", "double", "uint64_t", "int64_t"};
	public static final int[] TypeBytes = {1, 1, 2, 2, 4, 4, 4, 8, 8, 8};
	public kyLinkMember() {}
	public kyLinkMember(String name) {
		mbrName = name;
	}
	public kyLinkMember(String name, String type) {
		mbrName = name;
		mbrType = type;
	}
	public kyLinkMember(String name, String type, int off) {
		mbrName = name;
		mbrType = type;
		mbrOffset = off;
	}
	public kyLinkMember(String name, String type, int off, float val) {
		this(name, type, off);
		this.setValue(val);
	}

	public void setName(String name) {
		this.mbrName = name;
	}
	public void setType(String type) {
		this.mbrType = type;
	}
	public void setOffset(int off) {
		this.mbrOffset = off;
	}
	public void setValue(float val) {
		this.mbrValue = val;
	}

	public int checkType(String type) {
		int idx = 0;
		for(String s : DataTypes) {
			if(s.equals(type)) {
				return TypeBytes[idx];
			}
			idx ++;
		}
		return -1;
	}
}
