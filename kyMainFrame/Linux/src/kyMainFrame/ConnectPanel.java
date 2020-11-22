package kyMainFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

public class ConnectPanel extends JPanel {
	private static final long serialVersionUID = 1234567L;

	public final static int PanelTypeUART = 1;
	public final static int PanelTypeWIFI = 2;
	public final static int PanelTypeFILE = 3;
	private final String[] BaudRateList = {"9600", "57600", "115200", "230400"};

	private final Color BackgroundLost = new Color(255, 100, 100);
	private final Color BackgroundConnected = new Color(100, 255, 100);

	private JPanel WidgetPanel = null;

	private int PanelType = PanelTypeUART;
	/* public widget */
	private JLabel InfoLabel = null;
	/* Uart Connection */
	private JComboBox<String> portNameBox = null;
	private JComboBox<String> BaudrateBox = null;
	private JButton OpenPortBtn = null;
	/* Wifi Connection */
	private JLabel port_lab = null;
	private JTextField Port_Txt = null;
	/* File Connection */
	private String tarFileName = null;
	private JTextField FileNameText = null;
	private JButton OpenFileBtn = null;
	private JButton RunPauseBtn = null;
	private JButton ResetButton = null;
	private JFileChooser FileChoose = null;
	private static File SystemHomeDirectory = FileSystemView.getFileSystemView().getHomeDirectory();
	public ConnectPanel() {
		WidgetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
		WidgetPanel.setBackground(BackgroundLost);
		this.setLayout(new BorderLayout());
		this.add(WidgetPanel, BorderLayout.CENTER);
		this.setBackground(BackgroundLost);

		/* UART WIDGET */
		portNameBox = new JComboBox<String>();
		portNameBox.setEditable(true);
		portNameBox.setPreferredSize(new Dimension(140, 30));
		portNameBox.setFont(portNameBox.getFont().deriveFont(Font.BOLD, 14));
		portNameBox.setToolTipText("select uart port");

		BaudrateBox = new JComboBox<String>();
		BaudrateBox.setPreferredSize(new Dimension(100, 30));
		BaudrateBox.setMaximumRowCount(5);
		BaudrateBox.setEditable(false);
		for(String s : BaudRateList) { BaudrateBox.addItem(s); }
		BaudrateBox.setSelectedIndex(2);
		BaudrateBox.setFont(BaudrateBox.getFont().deriveFont(Font.BOLD, 14));
		BaudrateBox.setToolTipText("set baudrate");

		OpenPortBtn = new JButton("OPEN");
		OpenPortBtn.setPreferredSize(new Dimension(90, 30));
		OpenPortBtn.setFont(new Font("FreeMono", Font.BOLD, 18));
		OpenPortBtn.setToolTipText("open port");

		/* WIFI WIDGET */
		port_lab = new JLabel("UDP PORT:");
		port_lab.setPreferredSize(new Dimension(90, 30));
		port_lab.setFont(port_lab.getFont().deriveFont(Font.ITALIC, 18));

		Port_Txt = new JTextField("6000");
		Port_Txt.setPreferredSize(new Dimension(80, 30));
		Port_Txt.setFont(new Font("Courier New", Font.BOLD, 18));
		Port_Txt.setToolTipText("UDP Port");
		Port_Txt.setHorizontalAlignment(JTextField.CENTER);

		/* FILE WIDGET */
		FileNameText = new JTextField("no file selected.");
		FileNameText.setAutoscrolls(true);
		FileNameText.setEditable(false);
		FileNameText.setEnabled(false);
		FileNameText.setFont(new Font("Courier New", Font.BOLD, 18));
		FileNameText.setPreferredSize(new Dimension(240, 30));
		FileChoose = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("binary file(*.bin)", "bin");
		FileChoose.setFileFilter(filter);
		FileChoose.setCurrentDirectory(SystemHomeDirectory);
		OpenFileBtn = new JButton("Browse");
		OpenFileBtn.setPreferredSize(new Dimension(90, 30));
		OpenFileBtn.setFont(new Font("FreeMono", Font.BOLD, 18));
		OpenFileBtn.setToolTipText("open file");
		OpenFileBtn.addActionListener(SelectNewFileListener);

		RunPauseBtn = new JButton("Run");
		RunPauseBtn.setPreferredSize(new Dimension(90, 30));
		RunPauseBtn.setFont(new Font("FreeMono", Font.BOLD, 18));
		RunPauseBtn.setToolTipText("run/pause");
		
		ResetButton = new JButton("Reset");
		ResetButton.setPreferredSize(new Dimension(90, 30));
		ResetButton.setFont(new Font("FreeMono", Font.BOLD, 18));
		ResetButton.setToolTipText("reset player");

		/* PUBLIC WIDGET */
		InfoLabel = new JLabel("ready.");
		InfoLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		InfoLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		InfoLabel.setFont(InfoLabel.getFont().deriveFont(Font.ITALIC, 18));
//		InfoLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
		InfoLabel.setToolTipText("Connection Info");
		InfoLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
		this.add(InfoLabel, BorderLayout.EAST);

		if(PanelType == PanelTypeWIFI) {
			showWifiPanel();
		} else if(PanelType == PanelTypeFILE) {
			showFilePanel();
		} else {
			showUartPanel();
		}
	}

	public void addOpenPortActionListener(ActionListener l) {
		OpenPortBtn.addActionListener(l);
	}
	public void addFilePlayCtrlActionListener(ActionListener l) {
		RunPauseBtn.addActionListener(l);
		ResetButton.addActionListener(l);
	}

	public void setDebugInfo(String info) {
		InfoLabel.setText(info);
	}

	public void indicateConnectionState(boolean conn) {
		if(conn) {
			this.setBackground(BackgroundConnected);
			WidgetPanel.setBackground(BackgroundConnected);
		} else {
			this.setBackground(BackgroundLost);
			WidgetPanel.setBackground(BackgroundLost);
		}
	}

	public void setPortConfigPanelState(boolean flag) {
		if(PanelType == PanelTypeUART) {
			portNameBox.setEnabled(flag);
			BaudrateBox.setEnabled(flag);
			if(flag)
				OpenPortBtn.setText("OPEN");
			else
				OpenPortBtn.setText("CLOSE");
		} else if(PanelType == PanelTypeWIFI) {
			Port_Txt.setEnabled(flag);
			Port_Txt.setEditable(flag);
			if(flag)
				OpenPortBtn.setText("OPEN");
			else
				OpenPortBtn.setText("CLOSE");
		} else if(PanelType == PanelTypeFILE) {
			OpenFileBtn.setEnabled(flag);
			if(flag)
				RunPauseBtn.setText("Run");
			else
				RunPauseBtn.setText("Pause");
		}
	}

	public void setPortNameList(ArrayList<String> list) {
		if(list != null && list.size() > 0) {
			// add new
			for(String s : list) {
				boolean existed = false;
				for(int i = 0; i < portNameBox.getItemCount(); i ++) {
					if(s.equals(portNameBox.getItemAt(i))) {
						existed = true;
						break;
					}
				}
				if(!existed) {
					portNameBox.addItem(s);
				}
			}
			// remove invalid
			for(int i = 0; i < portNameBox.getItemCount(); i ++) {
				boolean Invalid = true;
				for(String s : list) {
					if(s.equals(portNameBox.getItemAt(i))) {
						Invalid = false;
						break;
					}
				}
				if(Invalid) {
					portNameBox.removeItemAt(i);
				}
			}
		} else {
			portNameBox.removeAllItems();
		}
	}

	public String getUartPortName() {
		return (String)portNameBox.getSelectedItem();
	}
	public int getUartBaudrate() {
		return Integer.parseInt((String)BaudrateBox.getSelectedItem());
	}
	public String getSocketPort() {
		return Port_Txt.getText();
	}
	public String getFilePortName() {
		return tarFileName;
	}
	private ActionListener SelectNewFileListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
			int ret = FileChoose.showDialog(null, "Choose");
			if(ret == JFileChooser.APPROVE_OPTION ) {
				File file = FileChoose.getSelectedFile();
				FileNameText.setText(file.getName());
				tarFileName = file.getAbsolutePath();
				System.out.println("target file:" + tarFileName);
			}
		}
	};

	private void showUartPanel() {
		WidgetPanel.removeAll();
		WidgetPanel.add(portNameBox);
		WidgetPanel.add(BaudrateBox);
		WidgetPanel.add(OpenPortBtn);

		this.validate();
		this.repaint();

		PanelType = PanelTypeUART;
	}

	private void showWifiPanel() {
		WidgetPanel.removeAll();

		WidgetPanel.add(port_lab);
		WidgetPanel.add(Port_Txt);
		WidgetPanel.add(OpenPortBtn);

		this.validate();
		this.repaint();

		PanelType = PanelTypeWIFI;
	}

	private void showFilePanel() {
		WidgetPanel.removeAll();

		WidgetPanel.add(FileNameText);
		WidgetPanel.add(OpenFileBtn);
		WidgetPanel.add(RunPauseBtn);
		WidgetPanel.add(ResetButton);

		this.validate();
		this.repaint();
		PanelType = PanelTypeFILE;
	}

	public void setPanelType(int t) {
		if(t == PanelTypeWIFI) {
			showWifiPanel();
		} else if(t == PanelTypeFILE) {
			showFilePanel();
		} else {
			showUartPanel();
		}
	}
	public int getPanelType() { return PanelType; }
}
