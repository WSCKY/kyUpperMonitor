package CommTool;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import CommTool.exception.OpenPortFailure;
import CommTool.exception.PortActionEventListener;
import CommTool.exception.PortActionEventListener.PortActionCode;
import CommTool.exception.ReadDataFailure;
import CommTool.exception.SendDataFailure;
import kyLink.kyLinkPackage;

public abstract class CommTool {
	private Semaphore ListMutex = new Semaphore(1);
	private ArrayList<PortActionEventListener> Listeners = new ArrayList<PortActionEventListener>();

	public boolean openPort(String[] args) throws OpenPortFailure {
		throw new OpenPortFailure();
	}

	public boolean isOpened() {
		return false;
	}

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

	public void closePort() {
		
	}

	/*
	 * FOR LISTENERS
	 */
	public void addPortActionEventListener(PortActionEventListener listener) {
		Listeners.add(listener);
	}

	public void removePortActionEventListener(PortActionEventListener listener) throws InterruptedException {
		ListMutex.acquire();
		for(PortActionEventListener l : Listeners) {
			if(l.equals(listener)) {
				Listeners.remove(l);
				break;
			}
		}
		ListMutex.release();
	}

	protected void notifyAllListeners(PortActionCode code) {
		if(ListMutex.tryAcquire()) {
			for(PortActionEventListener l : Listeners) {
				l.PortNewActionOccured(code);
			}
			ListMutex.release();
		}
	}
}
