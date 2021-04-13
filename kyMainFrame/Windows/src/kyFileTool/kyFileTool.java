package kyFileTool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import CommTool.CommTool;
import CommTool.exception.OpenPortFailure;
import CommTool.exception.ReadDataFailure;

public class kyFileTool extends CommTool {
//	private String FileName = null;
	private InputStream fs = null;

//	public void setName(String name) {
//		this.FileName = name;
//	}
	public boolean openPort(String portName) throws OpenPortFailure
	{
		if(portName != null) {
			try {
				fs = new FileInputStream(new File(portName));
			} catch (FileNotFoundException e1) {
				throw new OpenPortFailure("File Not Found!");
			}
			return true;
		}
		throw new OpenPortFailure("null File Name");
	}
	public boolean isOpened() {
		return (this.fs != null);
	}
	public int readData(byte[] data, int size) throws ReadDataFailure {
		int len = 0;
		if(this.fs != null) {
			try {
				len = fs.read(data, 0, 26); // about 5KB/s
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				throw new ReadDataFailure("IO Exception while read");
			}
			if(len == -1) {
				try {
					fs.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				fs = null;
			}
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				throw new ReadDataFailure("Interrupted Exception while sleep");
			}
		} else {

		}
		return len;
	}
	public void closePort()
	{
		if(fs != null) {
			try {
				fs.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fs = null;
		}
	}
}
