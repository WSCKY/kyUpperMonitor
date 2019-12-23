package NodeManage;

import java.util.ArrayList;

public class NodeManager {
	private ArrayList<Node> NodeList = null;
	private ArrayList<NodeManagerEventListener> Listeners = null;
	public NodeManager() {
		NodeList = new ArrayList<Node>();
		Listeners = new ArrayList<NodeManagerEventListener>();
	}

	public void addListener(NodeManagerEventListener l) {
		Listeners.add(l);
	}

	public void removeListener(NodeManagerEventListener l) {
		Listeners.remove(l);
	}

	private void publishListener(NodeManagerEvent event) {
		for(NodeManagerEventListener l : Listeners) {
			l.NodeListUpdate(event);
		}
	}

	public void updateNode(Node node) {
		int idx = 0;
		for(Node n : NodeList) {
			if(n.id == node.id) {
				n.update(node.fval1, node.fval2);
				NodeManagerEvent e = new NodeManagerEvent(n, idx, NodeManagerEvent.UPD);
				publishListener(e);
				return;
			}
			idx ++;
		}
		// new node
		NodeList.add(new Node(node));
		NodeManagerEvent e = new NodeManagerEvent(node, idx, NodeManagerEvent.ADD);
		publishListener(e);
	}

	public void refreshRate() {
		for(Node n : NodeList) {
			n.compRate();
		}
	}
}
