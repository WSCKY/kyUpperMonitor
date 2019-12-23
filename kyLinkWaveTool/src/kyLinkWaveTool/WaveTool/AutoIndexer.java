package kyLinkWaveTool.WaveTool;

import java.util.ArrayList;

public class AutoIndexer {
	private long maxIndex = 0;
	private ArrayList<Indexer> IndexerList = new ArrayList<Indexer>();

	public void add(String name) {
		IndexerList.add(new Indexer(name));
	}
	public long getIndexByName(String name) {
		for(Indexer i : IndexerList) {
			if(i.name.equals(name)) {
				return i.index;
			}
		}
		return -1;
	}
	public void removeByName(String name) {
		for(Indexer i : IndexerList) {
			if(i.name.equals(name)) {
				IndexerList.remove(i);
				return;
			}
		}
	}
	public void removeAll() {
		IndexerList.clear();
	}
	public long UpdateIndex(String name) {
		for(Indexer i : IndexerList) {
			if(i.name.equals(name)) {
				if(i.index < maxIndex) {
					i.index = maxIndex;
				} else {
					maxIndex = i.index;
				}
				long ret = i.index;
				i.index ++;
				return ret;
			}
		}
		return maxIndex;
	}
}

class Indexer {
	public long index = 0;
	public String name = "";

	public Indexer(String n) {
		name = n;
		index = 0;
	}
	public Indexer(long idx, String n) {
		index = idx;
		name = n;
	}

	public long getIndex() { return index; }
	public String getName() { return name; }
}
