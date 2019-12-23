package kyLinkWaveTool;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

import kyLink.kyLinkPackage;
import kyLinkWaveTool.Observer.ObserveMbr;
import kyLinkWaveTool.Observer.ObserverEvent;
import kyLinkWaveTool.Observer.ObserverEventListener;
import kyLinkWaveTool.WaveTool.WaveTool;

public class WavePlayer extends WaveTool implements ObserverEventListener, Runnable {
	private static final long serialVersionUID = 1L;
	private ArrayList<ObserveMbr> ObsWaveMbrList = new ArrayList<ObserveMbr>();
	private Semaphore ObsListMutex = null;
	private BlockingQueue<WaveData> WaveDataQueue = null;

	public WavePlayer(String Title) {
		super(Title);
		// TODO Auto-generated constructor stub
		WaveDataQueue = new ArrayBlockingQueue<WaveData>(10);
		ObsListMutex = new Semaphore(1);
		(new Thread(this)).start();
	}

	public void DataPackageProcess(kyLinkPackage rxData) {
		try {
			ObsListMutex.acquire();
		} catch (InterruptedException e1) {
			System.err.println("take Mutex failed at <DataPackageProcess>");
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			for(ObserveMbr om : ObsWaveMbrList) {
				if(om.getGroupId() == (int)(rxData.msg_id & 0xFF)) {
					int off = om.getMbrOffset();
					String type = om.getMbrType();
					float f = (float) rxData.readoutTypedData(type, off);
					WaveData wd = new WaveData(om.getMbrName(), f);
					WaveDataQueue.offer(wd);
//					try {
//						WaveDataQueue.put(wd);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				}
			}
		ObsListMutex.release();
	}

	@Override
	public void addObserverMember(ObserverEvent e) {
		// TODO Auto-generated method stub
		ObserveMbr om = (ObserveMbr)e.getSource();
		int index = this.addNewSeries(om.getMbrName());
		this.setSeriesColor(index, om.getColor());
		this.setSeriesLineWidth(index, 3);
		try {
			ObsListMutex.acquire();
		} catch (InterruptedException e1) {
			System.err.println("take Mutex failed at <addObserverMember>");
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			ObsWaveMbrList.add(om);
		ObsListMutex.release();
	}

	@Override
	public void removeObserverMember(ObserverEvent e) {
		// TODO Auto-generated method stub
		ObserveMbr om = (ObserveMbr)e.getSource();
		try {
			ObsListMutex.acquire();
		} catch (InterruptedException e1) {
			System.err.println("take Mutex failed at <removeObserverMember>");
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			for(ObserveMbr owm : ObsWaveMbrList) {
				if(owm.getGroupId() == om.getGroupId() && owm.getMbrOffset() == om.getMbrOffset()) {
					this.removeSeries(owm.getMbrName());
					ObsWaveMbrList.remove(owm);
					break;
				}
			}
			// refresh stroke.
			for(ObserveMbr owm : ObsWaveMbrList) {
				this.setSeriesColor(owm.getMbrName(), owm.getColor());
				this.setSeriesLineWidth(owm.getMbrName(), 3);
			}
			// remove all elements who not shown.
			WaveDataQueue.clear();
		ObsListMutex.release();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			try {
				WaveData wd = WaveDataQueue.take();
				this.addDataToSeries(wd.name, wd.data);
			} catch (InterruptedException e) {
				System.err.println("take data from wave data queue failed!");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

class WaveData {
	String name;
	float data;
	public WaveData(String name, float d) { this.name = name; this.data = d; }
}
