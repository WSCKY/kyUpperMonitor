package kyLink.decoder;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import kyLink.kyLinkPackage;
import kyLink.event.kyLinkDecodeEvent;
import kyLink.event.kyLinkDecodeEventListener;
import kyLink.CRC.CalculateCRC;

public final class kyLinkDecoder {
	public static enum DECODE_STATE {
		DECODE_STATE_UNSYNCED,
		DECODE_STATE_GOT_STX1,
		DECODE_STATE_GOT_STX2,
		DECODE_STATE_GOT_DEVID,
		DECODE_STATE_GOT_MSGID,
		DECODE_STATE_GOT_LEN_L,
		DECODE_STATE_GOT_LEN_H,
		DECODE_STATE_GOT_DATA,
		DECODE_STATE_GOT_CRC_L,
	}

	private kyLinkPackage rPacket = new kyLinkPackage();

	private int _rxlen = 0;
	private DECODE_STATE _decode_state = DECODE_STATE.DECODE_STATE_UNSYNCED;

	private boolean _should_exit = false;

	private Thread DecodeThread = null;
	private byte[] takeData = new byte[256];
	private RingBufferFlip ringBuffer = null;
	private Semaphore ringBufferMutex = new Semaphore(1);

	private Timer StatisticTimer = new Timer();
	private Semaphore ListMutex = new Semaphore(1);
	private ArrayList<kyLinkDecodeEventListener> ListenerList = new ArrayList<kyLinkDecodeEventListener>();

	public kyLinkDecoder() {
		System.out.println("kyLink Decoder V1.0.0");

		DecodeThread = new Thread(decode_run);
		ringBuffer = new RingBufferFlip(2048);
		ringBuffer.reset();
		DecodeThread.start();
		StatisticTimer.scheduleAtFixedRate(RateStat, 0, 1000);
	}

	public void push(byte data) throws InterruptedException {
		ringBufferMutex.acquire();
		ringBuffer.put(data);
		ringBufferMutex.release();
	}
	public void push(byte[] bytes, int length) throws InterruptedException {
		ringBufferMutex.acquire();
		ringBuffer.put(bytes, length);
		ringBufferMutex.release();
	}

	public boolean isIdle() throws InterruptedException {
		ringBufferMutex.acquire();
		int available = ringBuffer.available();
		ringBufferMutex.release();
		return (available == 0);
	}

	private Runnable decode_run = new Runnable() {
		public void run() {
			int available = 0;
			int decode_idx = 0;
			while(_should_exit != true) {
				try {
					ringBufferMutex.acquire();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				available = ringBuffer.take(takeData, 256);
				ringBufferMutex.release();
				if(available > 0) {
					decode_idx = 0;
					do {
						rx_decode(takeData[decode_idx ++]);
					} while(decode_idx < available);
				}
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	private int frameCnt = 0, frameRate = 0;
	private TimerTask RateStat = new TimerTask() {
		public void run() {
			frameRate = frameCnt;
			frameCnt = 0;
		}
	};
	public int frameRate() {
		return frameRate;
	}

	public void exit() throws InterruptedException {
		StatisticTimer.cancel();
		_should_exit = true;
		DecodeThread.join();
	}

	public final DECODE_STATE rx_decode(byte data) {
		switch(_decode_state) {
			case DECODE_STATE_UNSYNCED:
				if(data == kyLinkPackage.kySTX1) {
					_decode_state = DECODE_STATE.DECODE_STATE_GOT_STX1;
					rPacket.stx1 = kyLinkPackage.kySTX1;
				}
			break;
			case DECODE_STATE_GOT_STX1:
				if(data == kyLinkPackage.kySTX2) {
					_decode_state = DECODE_STATE.DECODE_STATE_GOT_STX2;
					rPacket.stx2 = kyLinkPackage.kySTX2;
				}
				else
					_decode_state = DECODE_STATE.DECODE_STATE_UNSYNCED;
			break;
			case DECODE_STATE_GOT_STX2:
				rPacket.dev_id = data;
				_decode_state = DECODE_STATE.DECODE_STATE_GOT_DEVID;
			break;
			case DECODE_STATE_GOT_DEVID:
				rPacket.msg_id = data;
				_decode_state = DECODE_STATE.DECODE_STATE_GOT_MSGID;
			break;
			case DECODE_STATE_GOT_MSGID:
				rPacket.length = data;
				_decode_state = DECODE_STATE.DECODE_STATE_GOT_LEN_L;
			break;
			case DECODE_STATE_GOT_LEN_L:
				rPacket.length = (short) (((short)data << 8) | (rPacket.length & 0xFF));
				if(rPacket.length <= kyLinkPackage.MAIN_DATA_CACHE && rPacket.length > 0) {
					_rxlen = 0;
					_decode_state = DECODE_STATE.DECODE_STATE_GOT_LEN_H;
				} else {
					_decode_state = DECODE_STATE.DECODE_STATE_UNSYNCED;
					if(ListMutex.tryAcquire()) {
						for(kyLinkDecodeEventListener l : ListenerList) {
							l.lenOverFlow(null);
						}
						ListMutex.release();
					}
				}
			break;
			case DECODE_STATE_GOT_LEN_H:
				rPacket.rData[_rxlen ++] = data;
				if(_rxlen == rPacket.length) {
					_decode_state = DECODE_STATE.DECODE_STATE_GOT_DATA;
				}
				if(_rxlen > rPacket.rData.length) {
					_decode_state = DECODE_STATE.DECODE_STATE_UNSYNCED;
				}
			break;
			case DECODE_STATE_GOT_DATA:
				rPacket.crc = data;
				_decode_state = DECODE_STATE.DECODE_STATE_GOT_CRC_L;
			break;
			case DECODE_STATE_GOT_CRC_L:
				rPacket.crc = (short) (((short)data << 8) | (rPacket.crc & 0xFF));
				if(CalculateCRC.ComputeCRC16(rPacket.getCRCBuffer(), rPacket.length + 4) == rPacket.crc) {
					if(ListMutex.tryAcquire()) {
						for(kyLinkDecodeEventListener l : ListenerList) {
							l.getNewPackage(new kyLinkDecodeEvent(rPacket.copy()));
						}
						ListMutex.release();
					}
					frameCnt ++;
				} else {
					if(ListMutex.tryAcquire()) {
						for(kyLinkDecodeEventListener l : ListenerList) {
							l.badCRCEvent(null);
						}
						ListMutex.release();
					}
				}
				_decode_state = DECODE_STATE.DECODE_STATE_UNSYNCED;
			break;
			default:
				_decode_state = DECODE_STATE.DECODE_STATE_UNSYNCED;
			break;
		}
		return _decode_state;
	}

	public void addDecodeListener(kyLinkDecodeEventListener listener) {
		ListenerList.add(listener);
	}

	public void removeDecodeListener(kyLinkDecodeEventListener listener) throws InterruptedException {
		ListMutex.acquire();
		for(kyLinkDecodeEventListener l : ListenerList) {
			if(l.equals(listener)) {
				ListenerList.remove(l);
				break;
			}
		}
		ListMutex.release();
	}

	public void removeAllListeners() throws InterruptedException {
		ListMutex.acquire();
		ListenerList.clear();
		ListMutex.release();
	}
}
