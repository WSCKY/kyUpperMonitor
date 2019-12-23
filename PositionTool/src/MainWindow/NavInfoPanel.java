package MainWindow;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class NavInfoPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name = "A";
	private String mode = "No Fix";
	private double lon = 0.0;
	private double lat = 0.0;
	private int numSV = 0;
	private int year = 2019;
	private int month = 9;
	private int day = 24;
	private int hour = 12;
	private int minute = 25;
	private int second = 30;
	private double height = 0;
	private double hMSL = 0;
	private double vel_n = 0;
	private double vel_e = 0;
	private double vel_d = 0;
	private double g_speed = 0;
	private double g_heading = 0;
	private double h_accuracy = 0;
	private double v_accuracy = 0;

	private JTextArea InfoLabel = null;
	private JScrollPane scollPane = null;
	public NavInfoPanel() {
		InfoLabel = new JTextArea("RTK NAVIGATOR: ?");
		scollPane = new JScrollPane(InfoLabel);
		this.setLayout(new BorderLayout());
		this.add(scollPane, BorderLayout.CENTER);
		InfoLabel.setFont(InfoLabel.getFont().deriveFont(Font.BOLD, 16));
		InfoLabel.setEnabled(false);
		InfoLabel.setEditable(false);
		InfoLabel.setAutoscrolls(true);

		this.updateDisplay();
	}

	public NavInfoPanel(String name) {
		this();
		this.name = name;
		this.updateDisplay();
	}

	public void setName(String name) {
		this.name = name;
	}
	public void setDate(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}
	public void setTime(int hour, int minute, int second) {
		this.hour = hour;
		this.minute = minute;
		this.second = second;
	}
	public void setPosition(double lon, double lat) {
		this.lon = lon;
		this.lat = lat;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public void setHeight(double height, double hMSL) {
		this.height = height;
		this.hMSL = hMSL;
	}
	public void setVelNED(double vel_n, double vel_e, double vel_d) {
		this.vel_n = vel_n;
		this.vel_e = vel_e;
		this.vel_d = vel_d;
	}
	public void set2DSpeed(double g_speed) {
		this.g_speed = g_speed;
	}
	public void set2DHeading(double g_heading) {
		this.g_heading = g_heading;
	}
	public void setSVNumber(int numSV) {
		this.numSV = numSV;
	}

	public void updateDisplay() {
		InfoLabel.setText("RTK NAVIGATOR: " + name + "\n"
				+ "Mode: " + mode + "\n"
				+ "Date: " + year + "/" + month + "/" + day + "\n"
				+ "Time: " + hour + ":" + minute + ":" + second + "\n"
				+ "Longitude: " + lon + "\n"
				+ "Latitude: " + lat + "\n"
				+ "Height: " + height + "m\n"
				+ "Height(sea level): " + hMSL + "m\n"
				+ "Satellites:" + numSV + "\n"
				+ "2D Speed: " + g_speed + "m/s\n"
				+ "2D Heading: " + g_heading + "deg\n"
				+ "North velocity: " + vel_n + "m/s\n"
				+ "East velocity: " + vel_e + "m/s\n"
				+ "Down velocity: " + vel_d + "m/s\n"
				+ "Horizontal accuracy: " + h_accuracy + "m\n"
				+ "Vertical accuracy: " + v_accuracy + "m");
	}
}
