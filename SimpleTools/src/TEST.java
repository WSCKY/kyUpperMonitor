import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TEST {

	private static void FileHexDump(String path) {
		int rd;
		byte[] reads = new byte[16];
		File file = new File(path);
		try {
			FileInputStream fis = new FileInputStream(file);
			do {
				rd = fis.read(reads);
				for(int i = 0; i < rd; i ++) {
					System.out.print("0x" + Byte2HexString(reads[i]) + ", ");
				}
				System.out.println("");
			} while (rd != -1);
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String Byte2HexString(byte b) {
	String hex = Integer.toHexString(b & 0xFF);
	if(hex.length() < 2) {
		hex = "0" + hex;
	}
	return hex;
}
	
	private static byte[] Float2Bytes(float f) {
		byte[] b = new byte[4];
		int data = Float.floatToIntBits(f);
		b[0] = (byte)(data & 0xFF);
		b[1] = (byte)((data & 0xFF00) >> 8);
		b[2] = (byte)((data & 0xFF0000) >> 16);
		b[3] = (byte)((data & 0xFF000000) >> 24);
		return b;
	}

	private static float Bytes2Float(byte[] b) {
	byte[] bs = {b[3], b[2], b[1], b[0]};
	DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bs));
	float f = 0.0f;
	try {
		f = dis.readFloat();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return f;
}
public static void main(String[] args) {
//	float[] datas = {-54.060955f, 25.600948f, -8.433739f, 1.0030581f, 0.9998321f, 0.9938334f};
//	float[] datas = {-15.092868f, 2.3865275f, 18.627262f, 0.90483314f, 0.9160937f, 0.9430122f};
//for(int i = 0; i < 6; i ++) {
//	byte[] bs = Float2Bytes(datas[i]);
//	for(int j = 0; j < 4; j ++) {
//		System.out.print("0x" + Byte2HexString(bs[j]) + ", ");
//	}
//	System.out.println("");
//}
	
	FileHexDump("/home/kychu/myWork/10_03_44.dat");
	
}
}
