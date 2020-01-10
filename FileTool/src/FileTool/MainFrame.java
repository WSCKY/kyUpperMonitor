package FileTool;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

import event.CtrlEvent;
import event.CtrlEventListener;
import kyLink.kyLinkPackage;
import kyLink.event.kyLinkDecodeEvent;
import kyMainFrame.kyMainFrame;

public class MainFrame extends kyMainFrame implements Runnable, CtrlEventListener {
	private static final long serialVersionUID = 1L;

	private JPanel MainPanel = null;
	private JSplitPane MainSplitPane = null;
	private InfoFrame infoPanel = null;
	private CtrlFrame ctrlPanel = null;

	private Semaphore opt_sem;
	private CtrlInfo CtrlCmd;

	kyLinkPackage txPack = null;
	private Semaphore f_ack_sem;
	private Semaphore f_dat_sem;

	private MainFrame() {
		this.setTitle("kyChu.File Transfer Tool");
		this.setSize(1000, 600);
		MainPanel = this.getUsrMainPanel();
		MainPanel.setLayout(new BorderLayout());
		
		MainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		infoPanel = new InfoFrame();
		ctrlPanel = new CtrlFrame();
		ctrlPanel.setCtrlEventListener(this);
		MainSplitPane.setLeftComponent(ctrlPanel);
		MainSplitPane.setRightComponent(infoPanel);
		MainSplitPane.setDividerLocation(0.5);
		MainSplitPane.setEnabled(false);
		MainSplitPane.addComponentListener(compLis);

		MainPanel.add(MainSplitPane);

		txPack = new kyLinkPackage();
		txPack.dev_id = (byte)0x02;

		f_ack_sem = new Semaphore(1);
		f_dat_sem = new Semaphore(1);

		CtrlCmd = new CtrlInfo();
		opt_sem = new Semaphore(1);
		(new Thread(this)).start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			try {
				opt_sem.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int code = CtrlCmd.getEventCode();
			String dir = CtrlCmd.getEventDir();
			String file = CtrlCmd.getEventFile();
			switch(code) {
			case CtrlInfo.CTRL_READ_FILE: break;
			case CtrlInfo.CTRL_WRITE_FILE:
				write_data_to_file(dir, file);
			break;
			case CtrlInfo.CTRL_LIST_DIR:
				file_list_content(dir, file);
			break;
			case CtrlInfo.CTRL_CREATE:
				file_create(dir, file);
			break;
			case CtrlInfo.CTRL_DELETE:
				file_delete(dir, file);
			break;
			default: break;
			}
		}
	}

	private int new_msg_type = 0;
	private void file_list_content(String dir, String file) {
		int timeout = 0;
		String name;
		String f_type;

		ctrlPanel.disable();
		if(dir.endsWith("/")) name = dir.substring(0, dir.length() - 1);
		else name = dir;
		infoPanel.logln("list content of " + name);
		txPack.msg_id = (byte)0xB0;
		txPack.length = 69;
		txPack.addByte((byte)CtrlInfo.CTRL_LIST_DIR, 0);
		byte[] path = name.getBytes();
		txPack.addBytes(path, path.length, 5);
		txPack.addByte((byte) 0, path.length + 5);
		new_msg_type = 0;
		this.TxPackage(txPack);
		do {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			timeout ++;
		} while(new_msg_type == 0 && timeout < 200); // timeout 1s.
		if(new_msg_type == 0xB1) {
			do {
				new_msg_type = 0;
				if((recvMsg.data.FileAttr & 0x10) == 0) {
					f_type = "<f>";
				} else {
					f_type = "<d>";
				}
				infoPanel.logln(recvMsg.data.DataId + ": " + f_type + recvMsg.data.FilePath + "/" + recvMsg.data.FileName);
	
				txPack.msg_id = (byte)0xB2;
				txPack.length = 6;
				txPack.addByte((byte)MsgType.OPT_LIST_DIR, 0); // OptCmd
				txPack.addByte((byte)0, 1); // OptSta
				txPack.addInteger(recvMsg.data.DataId, 2);
				this.TxPackage(txPack);
				timeout = 0;
				while(new_msg_type == 0 && timeout < 200) {
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					timeout ++;
				}
				if(new_msg_type == 0xB2) {
					infoPanel.logln("list done! ret = " + recvMsg.ack.OptSta);
				}
			} while(new_msg_type == 0xB1);
		} else if(new_msg_type == 0xB2) {
			wait_for_ack(MsgType.OPT_LIST_DIR, 500);
			infoPanel.logln("no file in " + name + ", ret = " + recvMsg.ack.OptSta);
		} else {
			infoPanel.logln("lost response! <list>");
		}
		
		ctrlPanel.enable();
	}

	private void write_data_to_file(String dir, String file) {
		
	}

	private void file_create(String dir, String file) {
		byte file_attr = 0;
		ctrlPanel.disable();
		String name = dir;
		if(file.equals("")) {
			file_attr |= 0x10;
			if(dir.endsWith("/"))
				name = dir.substring(0, dir.length() - 1);
			infoPanel.logln("create new directory " + name);
		} else {
			if(!name.endsWith("/")) name += "/";
			name += file;
			infoPanel.logln("create new file " + name);
		}
		txPack.msg_id = (byte)0xB0;
		txPack.length = 69;
		txPack.addByte((byte)CtrlInfo.CTRL_CREATE, 0);
		txPack.addByte(file_attr, 1);
		byte[] path = name.getBytes();
		txPack.addBytes(path, path.length, 5);
		txPack.addByte((byte) 0, path.length + 5);
		this.TxPackage(txPack);
		if(wait_for_ack(MsgType.OPT_CREATE, 500) == 0) { // wait 500ms
			infoPanel.logln("create done! ret = " + recvMsg.ack.OptSta);
		} else {
			infoPanel.logln("lost response! <create>");
		}
		ctrlPanel.enable();
	}

	private void file_delete(String dir, String file) {
		ctrlPanel.disable();
		String name = dir;
		if(file.equals("")) {
			if(dir.endsWith("/"))
				name = dir.substring(0,  dir.length() - 1);
			infoPanel.logln("delete directory " + name);
		} else {
			if(!name.endsWith("/")) name += "/";
			name += file;
			infoPanel.logln("delete file " + name);
		}
		txPack.msg_id = (byte)0xB0;
		txPack.length = 69;
		txPack.addByte((byte)CtrlInfo.CTRL_DELETE, 0);
//		txPack.addByte(file_attr, 1);
		byte[] path = name.getBytes();
		txPack.addBytes(path, path.length, 5);
		txPack.addByte((byte) 0, path.length + 5);
		this.TxPackage(txPack);
		if(wait_for_ack(MsgType.OPT_DELETE, 500) == 0) { // wait 500ms
			infoPanel.logln("delete done! ret = " + recvMsg.ack.OptSta);
		} else {
			infoPanel.logln("lost response! <delete>");
		}
		ctrlPanel.enable();
	}

	@Override
	public void UserCtrlCommand(CtrlEvent event) {
		// TODO Auto-generated method stub
		CtrlCmd = (CtrlInfo)event.getSource();
		opt_sem.release();
	}

	private MsgType recvMsg = new MsgType();

	public int wait_for_ack(int opt, int timeout) {
		long time_start = System.currentTimeMillis();
		recvMsg.ack.OptCmd = MsgType.OPT_NULL;
		try {
			do {
				f_ack_sem.tryAcquire(5, TimeUnit.MILLISECONDS);
			} while(recvMsg.ack.OptCmd != opt && System.currentTimeMillis() - time_start < timeout);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(recvMsg.ack.OptCmd != opt) return -1;
		return 0;
	}

//	public MsgData wait_for_data(int id, int timeout) {
//		long time_start = System.currentTimeMillis();
//		recvMsg.data.DataId = -1;
//		try {
//			do {
//				f_dat_sem.tryAcquire(5,  TimeUnit.MILLISECONDS);
//			} while(recvMsg.data.DataId != id && System.currentTimeMillis() - time_start < timeout);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		if(recvMsg.data.DataId != id) return null;
//		MsgData msg = new MsgData();
//		msg.copyfrom(recvMsg.data);
//		return msg;
//	}

	byte[] path_cache = new byte[32];
	byte[] name_cache = new byte[32];
	public void getNewPackage(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		super.getNewPackage(arg0);
		kyLinkPackage rPack = (kyLinkPackage)(arg0.getSource());
		if(rPack.msg_id == (byte)0xB1) { // data
			recvMsg.data.DataId = rPack.rData[0];
			recvMsg.data.FileAttr = rPack.rData[1];
			for(int i = 0; i < 32; i ++) {
				path_cache[i] = 0;
				name_cache[i] = 0;
			}
			for(int i = 0; i < 32; i ++) {
				if(rPack.rData[2 + i] != 0)
					path_cache[i] = rPack.rData[2 + i];
				else
					break;
			}
			for(int i = 0; i < 32; i ++) {
				if(rPack.rData[34 + i] != 0)
					name_cache[i] = rPack.rData[34 + i];
				else
					break;
			}
			recvMsg.data.FilePath = new String(path_cache);
			recvMsg.data.FileName = new String(name_cache);
			f_dat_sem.release();
			new_msg_type = 0xB1;
			System.out.println("data");
		} else if(rPack.msg_id == (byte)0xB2) { // ack
			recvMsg.ack.OptCmd = rPack.rData[0];
			recvMsg.ack.OptSta = rPack.rData[1];
			recvMsg.ack.ParamId = rPack.rData[2];
			f_ack_sem.release();
			new_msg_type = 0xB2;
			System.out.println("ack");
		}
	}

	private ComponentAdapter compLis = new ComponentAdapter() {
		public void componentResized(ComponentEvent e) {
			// TODO Auto-generated method stub
			MainSplitPane.setDividerLocation(MainSplitPane.getWidth() / 3); // set divider location to center.
		}
	};

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Couldn't use system look and feel.");
		}
		(new MainFrame()).setVisible(true);
	}
}
