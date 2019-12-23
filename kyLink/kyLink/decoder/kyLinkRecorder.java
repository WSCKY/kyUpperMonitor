package kyLink.decoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.filechooser.FileSystemView;

import kyLink.kyLinkPackage;

public class kyLinkRecorder {
	private static String filePath = null;
	private static String fileName = null;
	private static OutputStream fileStream = null;

	private static SimpleDateFormat timeFormat = null;
	public kyLinkRecorder() {
		/* default file path && file name */
		filePath = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + File.separator + "kyLinkLog" + File.separator;
		fileName = (new SimpleDateFormat("yyyyMMdd")).format(new Date()) + ".txt";
		timeFormat = new SimpleDateFormat("HH:mm:ss");
//		File logFile = new File(filePath + fileName);
//		System.out.println(filePath + fileName);
//		if(!logFile.getParentFile().exists()) {
//			logFile.getParentFile().mkdirs();
//		}
//		if(!logFile.exists()) {
//			try {
//				logFile.createNewFile();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		System.out.println("name: " + logFile.getName());
	}

	public void setLogPath(String path) {
		filePath = path;
	}
	public void setLogName(String name) {
		fileName = name;
	}
	public void setLogFile(String file) {
		File f = new File(file);
		filePath = f.getParent();
		fileName = f.getName();
	}

	public void start() throws FileNotFoundException {
		File logFile = new File(filePath + fileName);
		if(!logFile.getParentFile().exists()) {
			logFile.getParentFile().mkdirs();
		}
		if(!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		fileStream = new FileOutputStream(logFile, true);
	}

	public void record(String s) throws IOException {
		fileStream.write((timeFormat.format(new Date()) + ": " + s + "\r\n").getBytes());
		fileStream.flush();
	}
	public void record(kyLinkPackage p) throws IOException {
		String s = timeFormat.format(new Date()) + ": ";
		s += Integer.toHexString(p.stx1 & 0xFF) + " ";
		s += Integer.toHexString(p.stx2 & 0xFF) + " ";
		s += Integer.toHexString(p.dev_id & 0xFF) + " ";
		s += Integer.toHexString(p.msg_id & 0xFF) + " ";
		s += Integer.toHexString(p.length & 0xFF) + " ";
		s += "\r\n";
		fileStream.write(s.getBytes());
//		fileStream.write((p.getSendBuffer()).toString().getBytes());
		fileStream.flush();
	}

	public void stop() throws IOException {
		fileStream.close();
	}

	public static void main(String[] args) {
		System.out.println("Hello .");
		kyLinkRecorder kl = new kyLinkRecorder();
		try {
			kl.start();
			kl.record("1234567890");
			kl.record(new kyLinkPackage());
			kl.stop();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
