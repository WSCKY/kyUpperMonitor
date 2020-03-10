package subPanel;

public class CtrlInfo {
	public static final int CTRL_NULL = 0;
	public static final int CTRL_READ_FILE = 2;
	public static final int CTRL_WRITE_FILE = 1;
	public static final int CTRL_LIST_DIR = 3;
	public static final int CTRL_CREATE = 4;
	public static final int CTRL_DELETE = 5;

	private int eCode = CTRL_NULL;
	private String tarDir = null;
	private String tarFile = null;

	public CtrlInfo() {}
	public CtrlInfo(int code, String dir, String file) {
		this.eCode = code;
		this.tarDir = dir;
		this.tarFile = file;
	}

	public void setEventCode(int code) {
		this.eCode = code;
	}

	public void setEventDir(String dir) {
		this.tarDir = dir;
	}

	public void setEventFile(String file) {
		this.tarFile = file;
	}

	public int getEventCode() {
		return this.eCode;
	}
	
	public String getEventDir() {
		return this.tarDir;
	}
	
	public String getEventFile() {
		return this.tarFile;
	}
}
