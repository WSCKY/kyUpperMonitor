package MainWindow;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import NodeManage.Node;
import NodeManage.NodeManager;
import NodeManage.NodeManagerEvent;
import NodeManage.NodeManagerEventListener;
import kyLink.kyLinkPackage;
import kyLink.event.kyLinkDecodeEvent;
import kyLink.event.kyLinkDecodeEventListener;

public class NodeTable extends JPanel implements Runnable, kyLinkDecodeEventListener, NodeManagerEventListener {
	private static final long serialVersionUID = 2L;

	private kyLinkPackage rxData = null;

	private DefaultTableModel tabModel = null;
	private JTable nodeTable = null;
	private static String[] ColumnNames = {"SEQ", "ID", "FVAL1", "FVAL2", "RATE", "RESERVE"};

	NodeManager nodes = null;
	private BlockingQueue<Node> NodeQueue = null;
	
	private Timer RefreshTimer = null;

	public NodeTable() {
		this.setLayout(new BorderLayout());

		tabModel = new DefaultTableModel(null, ColumnNames);
		nodeTable = new JTable(tabModel);
		nodeTable.setRowHeight(25);
		nodeTable.setFont(new Font("Courier New", Font.PLAIN, 14));
		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
		tcr.setHorizontalAlignment(SwingConstants.CENTER);
		nodeTable.setDefaultRenderer(Object.class, tcr);
		nodeTable.getTableHeader().setDefaultRenderer(tcr);
		JScrollPane sp = new JScrollPane();
		sp.setViewportView(nodeTable);

		this.add(sp, BorderLayout.CENTER);

		nodes = new NodeManager();
		nodes.addListener(this);
		NodeQueue = new ArrayBlockingQueue<Node>(5);

		RefreshTimer = new Timer();
		(new Thread(this)).start();
		RefreshTimer.scheduleAtFixedRate(refTask, 0, 1000);
	}

	private TimerTask refTask = new TimerTask() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			nodes.refreshRate();
		}};

	@Override
	public void badCRCEvent(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getNewPackage(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		rxData = (kyLinkPackage)arg0.getSource();
		if(rxData.msg_id == (byte)0xC8) {
			try {
				NodeQueue.put(new Node(
				(int)rxData.readoutTypedData("uint32_t", 0),
				(float)rxData.readoutTypedData("float", 4) / 10.0f,
				(float)rxData.readoutTypedData("float", 8) / 10.0f
				));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void lenOverFlow(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			try {
				Node node = NodeQueue.take();
				nodes.updateNode(node);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void NodeListUpdate(NodeManagerEvent event) {
		// TODO Auto-generated method stub
		if(event.getType() == NodeManagerEvent.UPD) {
			Node n = (Node)event.getSource();
			tabModel.setValueAt(String.valueOf(n.fval1), event.getIndex(), 2);
			tabModel.setValueAt(String.valueOf(n.fval2), event.getIndex(), 3);
			tabModel.setValueAt(String.valueOf(n.rate), event.getIndex(), 4);
		} else if(event.getType() == NodeManagerEvent.ADD) {
			Node n = (Node)event.getSource();
			tabModel.addRow(new Object[]{event.getIndex(), String.valueOf(n.id), String.valueOf(n.fval1), String.valueOf(n.fval2), String.valueOf(n.rate), ""});
		} else if(event.getType() == NodeManagerEvent.DEL) {
			// process delete event.
		}
	}
}
