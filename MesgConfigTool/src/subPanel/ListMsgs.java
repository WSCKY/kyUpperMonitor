package subPanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import kyLink.kyLinkPackage;
import kyLink.event.kyLinkDecodeEvent;
import kyLink.event.kyLinkDecodeEventListener;

public class ListMsgs extends JPanel implements Runnable, kyLinkDecodeEventListener {
	private static final long serialVersionUID = 1L;
	private static final int MSG_ID_CACHE_SIZE = 255;

	private JTextArea InfoLabel = null;
	private JScrollPane scollPane = null;
	private JButton RefreshBtn = null;
	private int msgCounter = 0;
	private byte[] msgIdList = null;
	private Semaphore listSync;
	private Semaphore listUpdate;

	public ListMsgs() {
		listSync = new Semaphore(1);
		listUpdate = new Semaphore(1);
		msgIdList = new byte[MSG_ID_CACHE_SIZE];

		InfoLabel = new JTextArea();
		InfoLabel.setEditable(false);
		InfoLabel.setAutoscrolls(true);
		scollPane = new JScrollPane(InfoLabel);

		RefreshBtn = new JButton("Refresh");
		RefreshBtn.addActionListener(act);

		this.setLayout(new BorderLayout());
		this.add(scollPane, BorderLayout.CENTER);
		this.add(RefreshBtn, BorderLayout.SOUTH);

		(new Thread(this)).start();
	}

	private ActionListener act = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent event) {
			// TODO Auto-generated method stub
			try {
				listSync.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			msgCounter = 0;
			InfoLabel.setText("refresh ...");
			listSync.release();
		}
	};

	@Override
	public void badCRCEvent(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getNewPackage(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		int i;
		kyLinkPackage rPack = (kyLinkPackage)(arg0.getSource());
		try {
			listSync.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(i = 0; i < msgCounter; i ++) {
			if(msgIdList[i] == rPack.msg_id) {
				break;
			}
		}
		if(i == msgCounter) {
			if(msgCounter < MSG_ID_CACHE_SIZE) {
				msgIdList[i] = rPack.msg_id;
				msgCounter ++;
				listUpdate.release();
			}
		}
		listSync.release();
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
				listUpdate.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				listSync.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(msgCounter > 0) {
				InfoLabel.setText("message list:\n");
				for(int i = 0; i < msgCounter; i ++) {
					String hex = Integer.toHexString(msgIdList[i] & 0xFF);
					if(hex.length() < 2) {
						hex = "0" + hex;
					}
					InfoLabel.append("\t id " + (i+1) + ", " + msgIdList[i] + " <0x" + hex + ">" + "\n");
				}
			}
			listSync.release();
		}
	}

}
