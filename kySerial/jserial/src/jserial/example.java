package jserial;

import java.util.ArrayList;

public class example {
	private static final String SerialPort = "/dev/ttyUSB0";
	public static void main(String[] args) {
		System.out.println("Serial ECHO example!");
		byte[] data = new byte[128];
		kySerialDrv s = new kySerialDrv();
		ArrayList<String> devs = s.list_dev();
		boolean dev_exit = false;
		for(String dev : devs) {
			System.out.println(dev);
			if(dev.equals(SerialPort))
				dev_exit = true;
		}
		if(dev_exit == false) {
			System.err.println("can NOT find device " + SerialPort);
			System.exit(0);
		}
		if(s.open(SerialPort, "115200") < 0) {
			System.err.println("serial port open failed!");
			System.exit(-1);
		}
		s.flush_read();
		s.flush_write();
		s.setBlockTime(1, 0);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int read = 0;
		do {
			read = s.read(data, 128);
			if(read < 0) {
				System.out.println("read failed!");
				break;
			} else if(read == 0) {
				System.out.println("no data received.");
			} else {
				s.send(data, read);
			}
		} while(true);
		s.close();
	}
}
