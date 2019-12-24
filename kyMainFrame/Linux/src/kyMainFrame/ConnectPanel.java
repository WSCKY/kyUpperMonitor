package kyMainFrame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class ConnectPanel extends JPanel implements ComponentListener {
	private static final long serialVersionUID = 1234567L;

	public final static int PanelTypeUART = 1;
	public final static int PanelTypeWIFI = 2;
	private final String[] BaudRateList = {"9600", "57600", "115200", "230400"};

	private final Color BackgroundLost = new Color(255, 100, 100);
	private final Color BackgroundConnected = new Color(100, 255, 100);

	private int PanelType = PanelTypeUART;
	/* public widget */
	private JLabel InfoLabel = null;
	/* Uart Connection */
	private JComboBox<String> portNameBox = null;
	private JComboBox<String> BaudrateBox = null;
	private JButton OpenPortBtn = null;
	/* Wifi Connection */
	private JLabel IP_Label = null;
	private JTextField IP_TXT = null;
	private JLabel port_lab = null;
	private JTextField Port_Txt = null;
	public ConnectPanel() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
		this.setBackground(BackgroundLost);

		/* UART WIDGET. */
		portNameBox = new JComboBox<String>();
		portNameBox.setEditable(true);
		portNameBox.setPreferredSize(new Dimension(90, 30));
		portNameBox.setFont(portNameBox.getFont().deriveFont(Font.BOLD, 14));
		portNameBox.setToolTipText("select uart port");

		BaudrateBox = new JComboBox<String>();
		BaudrateBox.setPreferredSize(new Dimension(90, 30));
		BaudrateBox.setMaximumRowCount(5);
		BaudrateBox.setEditable(false);
		for(String s : BaudRateList) { BaudrateBox.addItem(s); }
		BaudrateBox.setSelectedIndex(2);
		BaudrateBox.setFont(BaudrateBox.getFont().deriveFont(Font.BOLD, 14));
		BaudrateBox.setToolTipText("set baudrate");

		OpenPortBtn = new JButton("OPEN");
		OpenPortBtn.setPreferredSize(new Dimension(90, 30));
		OpenPortBtn.setFont(new Font("FreeMono", Font.BOLD, 18));
		OpenPortBtn.setToolTipText("open com port");

		/* WIFI WIDGET */
		IP_Label = new JLabel("IP:");
		IP_Label.setPreferredSize(new Dimension(28, 30));
		IP_Label.setFont(IP_Label.getFont().deriveFont(Font.ITALIC, 18));

		IP_TXT = new JTextField("192.168.4.1");
		IP_TXT.setPreferredSize(new Dimension(130, 30));
		IP_TXT.setFont(new Font("Courier New", Font.BOLD, 18));
		IP_TXT.setToolTipText("IP Address");
		IP_TXT.setHorizontalAlignment(JTextField.CENTER);
		IP_TXT.setEditable(false);

		port_lab = new JLabel("port:");
		port_lab.setPreferredSize(new Dimension(45, 30));
		port_lab.setFont(port_lab.getFont().deriveFont(Font.ITALIC, 18));

		Port_Txt = new JTextField("6000");
		Port_Txt.setPreferredSize(new Dimension(50, 30));
		Port_Txt.setFont(new Font("Courier New", Font.BOLD, 18));
		Port_Txt.setToolTipText("UDP Port");
		Port_Txt.setHorizontalAlignment(JTextField.CENTER);
		Port_Txt.setEditable(false);

		/* PUBLIC WIDGET */
		InfoLabel = new JLabel("ready.");
		InfoLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		InfoLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		InfoLabel.setFont(InfoLabel.getFont().deriveFont(Font.ITALIC));
//		InfoLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
		InfoLabel.setToolTipText("Connection Info");
		InfoLabel.setPreferredSize(new Dimension(500, 30));

		if(PanelType == PanelTypeWIFI) {
			showWifiPanel();
		} else {
			showUartPanel();
		}

		this.addComponentListener(this);
	}

	public void addOpenPortActionListener(ActionListener l) {
		OpenPortBtn.addActionListener(l);
	}

	public void setDebugInfo(String info) {
		InfoLabel.setText(info);
	}

	public void indicateConnectionState(boolean conn) {
		if(conn) {
			this.setBackground(BackgroundConnected);
		} else {
			this.setBackground(BackgroundLost);
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
		} else {
			Port_Txt.setEnabled(flag);
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
	public int getSocketPort() {
		return Integer.parseInt(Port_Txt.getText());
	}

	private void showUartPanel() {
		this.removeAll();
		this.add(portNameBox);
		this.add(BaudrateBox);
		this.add(OpenPortBtn);
		this.add(InfoLabel);

		this.validate();
		this.repaint();

		PanelType = PanelTypeUART;
	}

	private void showWifiPanel() {
		this.removeAll();

		this.add(IP_Label);
		this.add(IP_TXT);
		this.add(port_lab);
		this.add(Port_Txt);
		this.add(InfoLabel);

		this.validate();
		this.repaint();

		PanelType = PanelTypeWIFI;
	}

	public void setPanelType(int t) {
		if(t == PanelTypeWIFI) {
			showWifiPanel();
		} else {
			showUartPanel();
		}
	}
	public int getPanelType() { return PanelType; }

	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		this.remove(InfoLabel);
		InfoLabel.setPreferredSize(new Dimension(this.getWidth() - 313, 30));
		this.add(InfoLabel);
		this.validate();
//		this.repaint();
	}
	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
}
