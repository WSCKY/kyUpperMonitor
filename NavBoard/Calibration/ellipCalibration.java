package Calibration;

public class ellipCalibration {
	static final int SAMPLE_NUMBER = 300;
	float[][] matrixQ = null;
	double[][] matrixA = null;
	double[] matrixB = null;
	double[] matrixX = null;
	float[] matrixM = null;

	public ellipCalibration() {
		matrixQ = new float[SAMPLE_NUMBER][3];
		matrixA = new double[6][6];
		matrixB = new double[6];
		matrixX = new double[6];
		matrixM = new float[7];
	}

	public void setSampleVal(int idx, int axs, float val) {
		matrixQ[idx][axs] = val;
	}
	public void setSampleVal(int idx, float val1, float val2, float val3) {
		matrixQ[idx][0] = val1;
		matrixQ[idx][1] = val2;
		matrixQ[idx][2] = val3;
	}

	public void computeRet(double radius) {
		ellipsoid_init();
		ellipsoid_step1();
		ellipsoid_step2();
		ellipsoid_step3(radius);
	}

//	public float[] getCalibRet() {
//		return matrixM;
//	}
	public ellipParam getCalibRet() {
		ellipParam param = new ellipParam();
		param.offX = matrixM[0];
		param.offY = matrixM[1];
		param.offZ = matrixM[2];
		param.sclX = matrixM[4];
		param.sclY = matrixM[5];
		param.sclZ = matrixM[6];
		return param;
	}

	private void ellipsoid_init() {
	    for(int i = 0; i < 6; i++) {
	        for(int j = 0; j < 6; j++) {
	            matrixA[i][j] = 0;
	        }
	    }
	    for(int i = 0; i < 6; i++) {
	        matrixB[i] = 0;
	    }
	    for(int i = 0; i < 6; i++) {
	        matrixX[i] = 0;
	    }
	    for(int i = 0; i < 7; i++) {
	        matrixM[i] = 0;
	    }
	}

	private void ellipsoid_step1() {
	    float[][] matrix_temp1 = new float[SAMPLE_NUMBER][6];

	    for (int i = 0; i < SAMPLE_NUMBER; i++) {
	        matrix_temp1[i][0] = matrixQ[i][0] * matrixQ[i][0];
	        matrix_temp1[i][1] = matrixQ[i][1] * matrixQ[i][1];
	        matrix_temp1[i][2] = matrixQ[i][2] * matrixQ[i][2];
	        matrix_temp1[i][3] = matrixQ[i][0];
	        matrix_temp1[i][4] = matrixQ[i][1];
	        matrix_temp1[i][5] = matrixQ[i][2];
	    }
	    for (int j = 0; j < 6; j++) {
	        for (int i = 0; i < SAMPLE_NUMBER; i++) {
	            matrixA[j][0] += matrix_temp1[i][j] * matrix_temp1[i][0];
	            matrixA[j][1] += matrix_temp1[i][j] * matrix_temp1[i][1];
	            matrixA[j][2] += matrix_temp1[i][j] * matrix_temp1[i][2];
	            matrixA[j][3] += matrix_temp1[i][j] * matrix_temp1[i][3];
	            matrixA[j][4] += matrix_temp1[i][j] * matrix_temp1[i][4];
	            matrixA[j][5] += matrix_temp1[i][j] * matrix_temp1[i][5];
	        }
	    }

	    for (int i = 0; i < 6; i++) {
	        for (int j = 0; j < SAMPLE_NUMBER; j++) { matrixB[i] += -matrix_temp1[j][i]; }
	    }
	}

	private void ellipsoid_step2() {
	    int i = 0, j = 0, k = 0;
	    double temp = 0;
	    double[] x = new double[6];

	    double w = 0.9, precision = 0.000000001;

	    for(k = 0; k < 6000; k++) {
	        for(i = 0; i < 6; i++) {
	            temp = 0;
	            for(j = 0; j < 6; j++) {
	                if(j != i)  { temp += matrixA[i][j] * matrixX[j]; }
	            }
	            matrixX[i] = (1 - w) * x[i] + w * (matrixB[i] - temp) / matrixA[i][i];
	        }
	        for(i = 0; i < 6; i++) {
	            if(Math.abs(matrixX[i] - x[i]) < precision) {
	                if(i == 3) { return ; }
	            }
	            else  { break; }
	        }
	        for(i = 0; i < 6; i++)  { x[i] = matrixX[i]; }
	    }
	}

	private void ellipsoid_step3(double unitValue) {
	    matrixM[0] = (float) (matrixX[3] / 2 / matrixX[0]);
	    matrixM[1] = (float) (matrixX[4] / 2 / matrixX[1]);
	    matrixM[2] = (float) (matrixX[5] / 2 / matrixX[2]);
	    //(1G^2) = (4096*4096) =16777216
	    matrixM[3] = (float) (unitValue * unitValue / ((double)matrixX[3] * matrixX[3] / matrixX[0] / 4
	                                           + matrixX[4] * matrixX[4] / matrixX[1] / 4
	                                           + matrixX[5] * matrixX[5] / matrixX[2] / 4
	                                           - 1));
	    matrixM[4] = (float) Math.sqrt((matrixM[3]) * matrixX[0] );
	    matrixM[5] = (float) Math.sqrt((matrixM[3]) * matrixX[1] );
	    matrixM[6] = (float) Math.sqrt((matrixM[3]) * matrixX[2] );
	    System.out.println("ret: " + matrixM[0] + ", " + matrixM[1] + ", " + matrixM[2] + ", " + matrixM[3] + ", " + matrixM[4] + ", " + matrixM[5] + ", " + matrixM[6]);
	}
}

class ellipParam {
	float offX, offY, offZ;
	float sclX, sclY, sclZ;

	public byte checksum() {
		byte sum;
		byte[] ret;
		ret = Float2Bytes(offX);
		sum = (byte) (ret[0]^ret[1]^ret[2]^ret[3]);
		ret = Float2Bytes(offY);
		sum ^= (byte) (ret[0]^ret[1]^ret[2]^ret[3]);
		ret = Float2Bytes(offZ);
		sum ^= (byte) (ret[0]^ret[1]^ret[2]^ret[3]);
		ret = Float2Bytes(sclX);
		sum ^= (byte) (ret[0]^ret[1]^ret[2]^ret[3]);
		ret = Float2Bytes(sclY);
		sum ^= (byte) (ret[0]^ret[1]^ret[2]^ret[3]);
		ret = Float2Bytes(sclZ);
		sum ^= (byte) (ret[0]^ret[1]^ret[2]^ret[3]);
		return sum;
	}

	private byte[] Float2Bytes(float f) {
		byte[] b = new byte[4];
		int data = Float.floatToIntBits(f);
		b[0] = (byte)(data & 0xFF);
		b[1] = (byte)((data & 0xFF00) >> 8);
		b[2] = (byte)((data & 0xFF0000) >> 16);
		b[3] = (byte)((data & 0xFF000000) >> 24);
		return b;
	}
}
