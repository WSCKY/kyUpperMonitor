package kyLinkWaveTool.WaveTool;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.UIManager;

final class WaveToolTest extends JFrame implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WaveTool myWT = null;
	private WaveToolTest() {
		this.setTitle("kyChu Wave Tool");
		this.setSize(1000, 600);
		this.setResizable(true);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myWT = new WaveTool("WaveTool");
		this.setLayout(new BorderLayout());
		this.add(myWT);

		myWT.addNewSeries("Data0");
		myWT.addNewSeries("Data1");
		myWT.addNewSeries("Data2");
		myWT.setTitle("TEST TOOL");
		myWT.setValueAxisLabel("Distance");
		this.setVisible(true);
		new Thread(this).start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		double value = 0;
		while(true) {
			value = value + Math.random( ) - 0.5;
			myWT.addDataToSeries("Data0", value);
			myWT.addDataToSeries("Data1", value - 1);
			myWT.addDataToSeries("Data2", value + 1);
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }
		new WaveToolTest();
	}
}
