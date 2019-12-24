package jserial;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

public class kySerialDrv {

	public native int serial_open(String dev, String baud);
	public native int serial_baud(String baud);
	public native int serial_send(byte[] data, int len);
	public native int serial_read(byte[] data, int len);
	public native int serial_block(long sec, long us);
	public native int serial_flush_read();
	public native int serial_flush_write();
	public native int serial_close();

	static {
		System.load("/home/kychu/JavaProj/kyUpperMonitor/kySerial/library/libkyserial.so");
	}

	public ArrayList<String> list_dev() {
		ArrayList<String> devs = new ArrayList<String>();
		File folder = new File("/dev");

		File[] files = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return false;
                }
                return serial_filter(file.getName());
            }
        });

		for(File f : files) {
//			System.out.println(f.getAbsolutePath());
//			System.out.println(f.getName());
			devs.add(f.getAbsolutePath());
		}
		return devs;
	}

	private boolean serial_filter(String name) {
		return name.contains("ttyUSB") || name.contains("ttyACM");
	}

	public int open(String dev, String baud) {
		return serial_open(dev, baud);
	}
	public int baud(String baud) {
		return serial_baud(baud);
	}
	public int send(byte[] data, int len) {
		return serial_send(data, len);
	}
	public int read(byte[] data, int len) {
		return serial_read(data, len);
	}
	public void setBlockTime(long sec, long us) {
		serial_block(sec, us);
	}
	public void flush_read() {
		serial_flush_read();
	}
	public void flush_write() {
		serial_flush_write();
	}
	public void close() {
		serial_close();
	}
}
