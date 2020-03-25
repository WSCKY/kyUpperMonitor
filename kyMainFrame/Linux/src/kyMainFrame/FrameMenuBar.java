package kyMainFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import kyMainFrame.optEvent.ChangeIfEvent;
import kyMainFrame.optEvent.ChangeIfEventListener;

public class FrameMenuBar extends JMenuBar implements ActionListener {
	private static final long serialVersionUID = 1L;

	private ArrayList<ChangeIfEventListener> ListenerList = new ArrayList<ChangeIfEventListener>();

	private JMenu SettingMenu = null;
	private JMenu ComIfMenu = null;
	private JCheckBoxMenuItem ItemUartIf = null;
	private JCheckBoxMenuItem ItemWifiIf = null;
	private ButtonGroup ItemIfGroup = null;

	public FrameMenuBar() {
		ItemIfGroup = new ButtonGroup();
		ItemUartIf = new JCheckBoxMenuItem("UART", true);
		ItemUartIf.addActionListener(this);
		ItemWifiIf = new JCheckBoxMenuItem("WIFI", false);
		ItemWifiIf.addActionListener(this);
		ItemIfGroup.add(ItemUartIf);
		ItemIfGroup.add(ItemWifiIf);
		ComIfMenu = new JMenu("Interface(i)");
		ComIfMenu.setMnemonic('i');
		ComIfMenu.setFont(ComIfMenu.getFont().deriveFont(14));
		ComIfMenu.add(ItemUartIf);
		ComIfMenu.add(ItemWifiIf);
		SettingMenu = new JMenu("Setting(s)");
		SettingMenu.setMnemonic('s');
		SettingMenu.setFont(SettingMenu.getFont().deriveFont(14));
		SettingMenu.add(ComIfMenu);
		this.add(SettingMenu);
	}

	public String getInterfaceName() {
		if(ItemUartIf.isSelected()) return "UART";
		else if(ItemWifiIf.isSelected()) return "WIFI";
		return "ERROR";
	}

	public void addChangeIfActionListener(ChangeIfEventListener listener) {
		ListenerList.add(listener);
	}

	private void publishEvent(String Interface) {
		for(ChangeIfEventListener l : ListenerList) {
			l.InterfaceChanged(new ChangeIfEvent(Interface));
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(ItemUartIf.isSelected()) {
			publishEvent("UART");
		} else if(ItemWifiIf.isSelected()) {
			publishEvent("WIFI");
		}
	}
}
