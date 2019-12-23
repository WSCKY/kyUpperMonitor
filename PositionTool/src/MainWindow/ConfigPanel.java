package MainWindow;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

public class ConfigPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static File SystemHomeDirectory = FileSystemView.getFileSystemView().getHomeDirectory();

	private JButton resetButton = null;
	private JButton startButton = null;
	private JButton stopButton = null;
	private JButton clearButton = null;

	private JButton loadButton = null;
	private JButton saveButton = null;
	private JFileChooser fileChooser = null;

	private configEventListener configListener = null;
	
	public ConfigPanel() {
		resetButton = new JButton("resetView");
		startButton = new JButton("Start");
		clearButton = new JButton("Clear");
		stopButton = new JButton("Stop");

		loadButton = new JButton("LOAD");
		saveButton = new JButton("SAVE");
		
		fileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("gnss file(*.txt)", "txt");
		fileChooser.setFileFilter(filter);
		fileChooser.setCurrentDirectory(SystemHomeDirectory);

		resetButton.setPreferredSize(new Dimension(120, 40));
		startButton.setPreferredSize(new Dimension(110, 40));
		clearButton.setPreferredSize(new Dimension(110, 40));
		stopButton.setPreferredSize(new Dimension(110, 40));
		loadButton.setPreferredSize(new Dimension(110, 40));
		saveButton.setPreferredSize(new Dimension(110, 40));

		resetButton.setFont(new Font("宋体", Font.BOLD, 16));
		startButton.setFont(new Font("宋体", Font.BOLD, 16));
		clearButton.setFont(new Font("宋体", Font.BOLD, 16));
		stopButton.setFont(new Font("宋体", Font.BOLD, 16));
		loadButton.setFont(new Font("宋体", Font.BOLD, 16));
		saveButton.setFont(new Font("宋体", Font.BOLD, 16));

		resetButton.addActionListener(actionListener);
		startButton.addActionListener(actionListener);
		clearButton.addActionListener(actionListener);
		stopButton.addActionListener(actionListener);
		loadButton.addActionListener(actionListener);
		saveButton.addActionListener(actionListener);

		JPanel axisPanel = new JPanel();
		axisPanel.add(resetButton);

		JPanel recordPanel = new JPanel();
		recordPanel.add(startButton);
		recordPanel.add(stopButton);
		recordPanel.add(clearButton);
		
		JPanel filePanel = new JPanel();
		filePanel.add(loadButton);
		filePanel.add(saveButton);

		this.add(axisPanel);
		this.add(recordPanel);
		this.add(filePanel);
	}

	public void setListener(configEventListener lis) {
		configListener = lis;
	}

	private ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(configListener != null) {
				JButton button = (JButton)e.getSource();
				if(button == resetButton) {
					configListener.configUpdateNotify(new configEvent(this, configEvent.RESET_VIEW));
				} else if(button == startButton) {
					configListener.configUpdateNotify(new configEvent(this, configEvent.PAINT_START));
				} else if(button == clearButton) {
					configListener.configUpdateNotify(new configEvent(this, configEvent.PAINT_CLEAR));
				} else if(button == stopButton) {
					configListener.configUpdateNotify(new configEvent(this, configEvent.PAINT_STOP));
				} else if(button == loadButton) {
					if(fileChooser.showDialog(null, "OK") == JFileChooser.APPROVE_OPTION) {
						configListener.configUpdateNotify(new configEvent(this, configEvent.LOAD_GNSS_DATA, fileChooser.getSelectedFile()));
					}
				} else if(button == saveButton) {
					if(fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
						configListener.configUpdateNotify(new configEvent(this, configEvent.SAVE_GNSS_DATA, fileChooser.getSelectedFile()));
					}
				}
			}
		}
	};
}
