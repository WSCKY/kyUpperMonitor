package kyLinkWaveTool.Observer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

import kyLinkWaveTool.ColorAlloc.ColorAlloc;

public class Observer {
	private ColorAlloc allocColor = new ColorAlloc();
	private ReentrantLock listLock = new ReentrantLock();
	private ArrayList<ObserveMbr> ObsMbrList = new ArrayList<ObserveMbr>();
	private Collection<ObserverEventListener> ObsEvtListener = new HashSet<ObserverEventListener>();

	private void publishAddEvent(ObserverEvent e) {
		Iterator<ObserverEventListener> iter = ObsEvtListener.iterator();
		while(iter.hasNext()) {
			ObserverEventListener l = (ObserverEventListener)iter.next();
			l.addObserverMember(e);
		}
	}
	private void publishRemoveEvent(ObserverEvent e) {
		Iterator<ObserverEventListener> iter = ObsEvtListener.iterator();
		while(iter.hasNext()) {
			ObserverEventListener l = (ObserverEventListener)iter.next();
			l.removeObserverMember(e);
		}
	}

	public Color addObserveMbr(ObserveMbr om) {
		Color c = allocColor.getColor();
		om.setColor(c);
		listLock.lock();
		ObsMbrList.add(om);
		listLock.unlock();
		publishAddEvent(new ObserverEvent(om));
		return c;
	}

	public void removeObserveMbr(ObserveMbr om) {
		listLock.lock();
		for(ObserveMbr mbr : ObsMbrList) {
			if(mbr.getGroupId() == om.getGroupId() && mbr.getMbrOffset() == om.getMbrOffset()) {
				allocColor.releaseColor(mbr.getColor());
				ObsMbrList.remove(mbr);
				publishRemoveEvent(new ObserverEvent(mbr));
				listLock.unlock();
				return; /* search end */
			}
		}
		listLock.unlock();
	}

	public void removeObserveMbr(String MbrName) {
		listLock.lock();
		for(ObserveMbr mbr : ObsMbrList) {
			if(mbr.getMbrName().equals(MbrName)) {
				allocColor.releaseColor(mbr.getColor());
				ObsMbrList.remove(mbr);
				publishRemoveEvent(new ObserverEvent(mbr));
				listLock.unlock();
				return; /* search end */
			}
		}
		listLock.unlock();
	}

	public void takObserveMbr() {listLock.lock();}
	public void relObserveMbr() {listLock.unlock();}

	public void addObserverEventListener(ObserverEventListener l) {
		ObsEvtListener.add(l);
	}

	public void removeObserverEventListener(ObserverEventListener l) {
		ObsEvtListener.remove(l);
	}

	public ArrayList<ObserveMbr> getObserverMbrList() {
		return ObsMbrList;
	}

	public int getObserveNumber() {
		listLock.lock();
		int s = ObsMbrList.size();
		listLock.unlock();
		return s;
	}
}
