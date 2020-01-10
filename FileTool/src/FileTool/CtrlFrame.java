package FileTool;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import event.CtrlEvent;
import event.CtrlEventListener;

public class CtrlFrame extends JPanel {
	private static final long serialVersionUID = 1L;

	private JButton readFileBtn = null;
	private JButton writeFileBtn = null;
	private JButton listDirButton = null;
	private JButton createBtn = null;
	private JButton deleteBtn = null;

	private JLabel dirLabel = null;
	private JLabel fileLabel = null;
	private JTextField tarDir = null;
	private JTextField tarFile = null;

	public CtrlFrame() {
		readFileBtn = new JButton("read");
		writeFileBtn = new JButton("write");
		listDirButton = new JButton("listdir");
		createBtn = new JButton("create");
		deleteBtn = new JButton("delete");

		dirLabel = new JLabel("target directory: ");
		fileLabel = new JLabel("target file: ");
		tarDir = new JTextField("0:/");
		tarFile = new JTextField("");

		this.setLayout(new GridLayout(7, 1));
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(dirLabel, BorderLayout.WEST);
		p.add(tarDir, BorderLayout.CENTER);
		this.add(p);
		p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(fileLabel, BorderLayout.WEST);
		p.add(tarFile, BorderLayout.CENTER);
		this.add(p);
		this.add(listDirButton);
		this.add(readFileBtn);
		this.add(writeFileBtn);
		this.add(createBtn);
		this.add(deleteBtn);

		readFileBtn.addActionListener(acts);
		writeFileBtn.addActionListener(acts);
		listDirButton.addActionListener(acts);
		createBtn.addActionListener(acts);
		deleteBtn.addActionListener(acts);
	}

	public void enable() {
		readFileBtn.setEnabled(true);
		writeFileBtn.setEnabled(true);
		listDirButton.setEnabled(true);
		createBtn.setEnabled(true);
		deleteBtn.setEnabled(true);
		tarDir.setEnabled(true);
		tarFile.setEnabled(true);
	}

	public void disable() {
		readFileBtn.setEnabled(false);
		writeFileBtn.setEnabled(false);
		listDirButton.setEnabled(false);
		createBtn.setEnabled(false);
		deleteBtn.setEnabled(false);
		tarDir.setEnabled(false);
		tarFile.setEnabled(false);
	}

	private CtrlEventListener listener = null;

	public void setCtrlEventListener(CtrlEventListener lis) {
		this.listener = lis;
	}

	private ActionListener acts = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			String name = ((JButton)arg0.getSource()).getText();
			if(name.equals("read")) {
				listener.UserCtrlCommand(new CtrlEvent(new CtrlInfo(CtrlInfo.CTRL_READ_FILE, tarDir.getText(), tarFile.getText())));
			} else if(name.equals("write")) {
				listener.UserCtrlCommand(new CtrlEvent(new CtrlInfo(CtrlInfo.CTRL_WRITE_FILE, tarDir.getText(), tarFile.getText())));
			} else if(name.equals("listdir")) {
				listener.UserCtrlCommand(new CtrlEvent(new CtrlInfo(CtrlInfo.CTRL_LIST_DIR, tarDir.getText(), tarFile.getText())));
			} else if(name.equals("create")) {
				listener.UserCtrlCommand(new CtrlEvent(new CtrlInfo(CtrlInfo.CTRL_CREATE, tarDir.getText(), tarFile.getText())));
			} else if(name.equals("delete")) {
				listener.UserCtrlCommand(new CtrlEvent(new CtrlInfo(CtrlInfo.CTRL_DELETE, tarDir.getText(), tarFile.getText())));
			} else {
				System.out.println("unknown command.");
			}
		}
	};
}
