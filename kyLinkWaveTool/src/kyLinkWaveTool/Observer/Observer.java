package kyLinkWaveTool.Observer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import kyLinkWaveTool.ColorAlloc.ColorAlloc;

public class Observer {
	private ColorAlloc allocColor = new ColorAlloc();
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
		ObsMbrList.add(om);
		publishAddEvent(new ObserverEvent(om));
		return c;
	}

	public void removeObserveMbr(ObserveMbr om) {
		for(ObserveMbr mbr : ObsMbrList) {
			if(mbr.getGroupId() == om.getGroupId() && mbr.getMbrOffset() == om.getMbrOffset()) {
				allocColor.releaseColor(mbr.getColor());
				ObsMbrList.remove(mbr);
				publishRemoveEvent(new ObserverEvent(mbr));
				return; /* search end */
			}
		}
	}

	public void removeObserveMbr(String MbrName) {
		for(ObserveMbr mbr : ObsMbrList) {
			if(mbr.getMbrName().equals(MbrName)) {
				allocColor.releaseColor(mbr.getColor());
				ObsMbrList.remove(mbr);
				publishRemoveEvent(new ObserverEvent(mbr));
				return; /* search end */
			}
		}
	}

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
		return ObsMbrList.size();
	}
}
