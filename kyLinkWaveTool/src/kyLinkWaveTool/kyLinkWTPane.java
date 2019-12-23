package kyLinkWaveTool;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JSplitPane;

import org.dom4j.DocumentException;

import kyLink.kyLinkPackage;
import kyLink.event.kyLinkDecodeEvent;
import kyLink.event.kyLinkDecodeEventListener;

public class kyLinkWTPane extends JSplitPane implements ComponentListener, kyLinkDecodeEventListener {
	private static final long serialVersionUID = 1L;
	private int ManagerUIWidth = 360;

	private WavePlayer wave = null;
	private ManagerGUI mgui = null;
	public kyLinkWTPane(String ToolName) {
		super(JSplitPane.HORIZONTAL_SPLIT);

		wave = new WavePlayer(ToolName);
		mgui = new ManagerGUI();
		mgui.addObserverEventListener(wave);

		this.setLeftComponent(wave);
		this.setRightComponent(mgui);

		this.setEnabled(false);
		this.addComponentListener(this);
	}

	public void setConfigFile(String path) throws DocumentException {
		mgui.setConfigFile(path);
	}

	public void setTablePanelWidth(int width) {
		ManagerUIWidth = width;
		this.setDividerLocation(this.getWidth() - ManagerUIWidth);
	}

	@Override
	public void badCRCEvent(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getNewPackage(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		kyLinkPackage rxData = (kyLinkPackage)arg0.getSource();
		wave.DataPackageProcess(rxData);
		mgui.DataPackageProcess(rxData);
	}

	@Override
	public void lenOverFlow(kyLinkDecodeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		this.setDividerLocation(this.getWidth() - ManagerUIWidth);
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
