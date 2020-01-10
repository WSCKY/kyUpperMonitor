package FileTool;

public class MsgType {
	public static final int OPT_NULL = 0;
	public static final int OPT_WRITE_FILE = 1;
	public static final int OPT_READ_FILE = 2;
	public static final int OPT_LIST_DIR = 3;
	public static final int OPT_CREATE = 4;
	public static final int OPT_DELETE = 5;
	
	public static final int FR_OK = 0;                     /* (0) Succeeded */
	public static final int FR_DISK_ERR = 1;               /* (1) A hard error occurred in the low level disk I/O layer */
	public static final int FR_INT_ERR = 2;                /* (2) Assertion failed */
	public static final int FR_NOT_READY = 3;              /* (3) The physical drive cannot work */
	public static final int FR_NO_FILE = 4;                /* (4) Could not find the file */
	public static final int FR_NO_PATH = 5;                /* (5) Could not find the path */
	public static final int FR_INVALID_NAME = 6;           /* (6) The path name format is invalid */
	public static final int FR_DENIED = 7;                 /* (7) Access denied due to prohibited access or directory full */
	public static final int FR_EXIST = 8;                  /* (8) Access denied due to prohibited access */
	public static final int FR_INVALID_OBJECT = 9;         /* (9) The file/directory object is invalid */
	public static final int FR_WRITE_PROTECTED = 10;       /* (10) The physical drive is write protected */
	public static final int FR_INVALID_DRIVE = 11;         /* (11) The logical drive number is invalid */
	public static final int FR_NOT_ENABLED = 12;           /* (12) The volume has no work area */
	public static final int FR_NO_FILESYSTEM = 13;         /* (13) There is no valid FAT volume */
	public static final int FR_MKFS_ABORTED = 14;          /* (14) The f_mkfs() aborted due to any problem */
	public static final int FR_TIMEOUT = 15;               /* (15) Could not get a grant to access the volume within defined period */
	public static final int FR_LOCKED = 16;                /* (16) The operation is rejected according to the file sharing policy */
	public static final int FR_NOT_ENOUGH_CORE = 17;       /* (17) LFN working buffer could not be allocated */
	public static final int FR_TOO_MANY_OPEN_FILES = 18;   /* (18) Number of open files > _FS_LOCK */
	public static final int FR_INVALID_PARAMETER = 19;     /* (19) Given parameter is invalid */
	public static final int FR_NO_MEMORY_FOR_OPERATE = 20; /* (20) failed to alloc memory for operation */

	public MsgAck ack;
	public MsgData data;

	public MsgType() {
		ack = new MsgAck();
		data = new MsgData();
	}
}

class MsgAck {
	public int OptCmd;
	public int OptSta;
	public int ParamId;
}

class MsgData {
	public int DataId;
	public byte FileAttr;
	public String FilePath;
	public String FileName;

	public void copyfrom(MsgData msg) {
		this.DataId = msg.DataId;
		this.FileAttr = msg.FileAttr;
		this.FilePath = msg.FilePath;
		this.FileName = msg.FileName;
	}
}
