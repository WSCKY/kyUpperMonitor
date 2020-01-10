package CommTool;

import CommTool.exception.ReadDataFailure;
import CommTool.exception.SendDataFailure;
import kyLink.kyLinkPackage;

public abstract class CommTool {
	public int sendData(byte[] data, int size) throws SendDataFailure {
		throw new SendDataFailure();
	}

	public void sendPackage(kyLinkPackage pack) throws SendDataFailure {
		byte[] txe = pack.getSendBuffer();
		this.sendData(txe, txe.length);
	}

	public int readData(byte[] data, int size) throws ReadDataFailure {
		throw new ReadDataFailure();
	}
}
