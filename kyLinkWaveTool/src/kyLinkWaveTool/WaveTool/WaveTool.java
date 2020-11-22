package kyLinkWaveTool.WaveTool;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.general.SeriesChangeListener;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class WaveTool extends ChartPanel implements SeriesChangeListener {
	private static final long serialVersionUID = 1L;

	private static JFreeChart freeChart = null;
	private static XYSeriesCollection DataSet = null;
	AutoIndexer autoIndex = null;

	private static double DataPoints = 500;
	private JMenuItem DataPointsItem = null;
	public WaveTool(String Title) {
		super(createChart(Title));
		autoIndex = new AutoIndexer();

		DataPointsItem = new JMenuItem("DataPoint");
		DataPointsItem.addActionListener(DataPointActionListener);
		JPopupMenu popm = this.getPopupMenu();
		popm.add(DataPointsItem);
	}

	private static JFreeChart createChart(String title) {
		DataSet = new XYSeriesCollection();
		freeChart = ChartFactory.createXYLineChart(title, "Time", "", DataSet, PlotOrientation.VERTICAL, true, true, false);
		XYPlot xyplot = freeChart.getXYPlot();
		ValueAxis valueaxis = xyplot.getDomainAxis();
		valueaxis.setAutoRange(true);
//		valueaxis.setInverted(true);
		valueaxis.setFixedAutoRange(DataPoints);
		valueaxis.setTickLabelFont(new Font("Courier New", Font.BOLD, 12));
		valueaxis.setLabelFont(new Font("Courier New", Font.BOLD, 14));
		xyplot.getRenderer().setSeriesStroke(1, new BasicStroke(3.0f));
		valueaxis = xyplot.getRangeAxis();
		valueaxis.setAutoRange(true);
		NumberAxis numAxis = ((NumberAxis)valueaxis);
		numAxis.setAutoRangeIncludesZero(false);
		freeChart.getLegend().setVisible(false);
		return freeChart;
	}

	public int addNewSeries(String SeriesName) {
		XYSeries series = new XYSeries(SeriesName);
		series.addChangeListener(this);
		autoIndex.add(SeriesName);
		DataSet.addSeries(series);
		return DataSet.indexOf(series);
	}
	public void removeSeries(String SeriesName) {
		autoIndex.removeByName(SeriesName);
		DataSet.removeSeries(DataSet.getSeries(SeriesName));
	}
	public void setTitle(String title) {
		freeChart.setTitle(title);
	}
	public void setValueAxisLabel(String label) {
		freeChart.getXYPlot().getRangeAxis().setLabel(label);
	}
	public void removeAllSeries() {
		DataSet.removeAllSeries();
		autoIndex.removeAll();
	}
	public int getSeriesCount() {
		return DataSet.getSeriesCount();
	}
	public int getSeriesIndex(String SeriesName) {
		return DataSet.getSeriesIndex(SeriesName);
	}
	public void addDataToSeries(String SeriesName, double d) {
		XYSeries series = DataSet.getSeries(SeriesName);
		series.add(autoIndex.UpdateIndex(SeriesName), d);
	}
	public void setAutoRange(boolean auto) {
		freeChart.getXYPlot().getRangeAxis().setAutoRange(auto);
	}
	public void setLockZeroPoint(boolean flag) {
		NumberAxis numAxis = ((NumberAxis)freeChart.getXYPlot().getRangeAxis());
		numAxis.setAutoRangeIncludesZero(flag);
	}
	public void setDataPoints(double dataPoints2) {
		freeChart.getXYPlot().getDomainAxis().setFixedAutoRange(dataPoints2);
	}
	public void setAutoRangeMinimumSize(double size) {
		freeChart.getXYPlot().getRangeAxis().setAutoRangeMinimumSize(size);
	}
	public void setSeriesColor(int index, Color c) {
		freeChart.getXYPlot().getRenderer().setSeriesPaint(index, c);
	}
	public void setSeriesColor(String SeriesName, Color c) {
		int index = DataSet.getSeriesIndex(SeriesName);
		setSeriesColor(index, c);
	}
	public void setSeriesLineWidth(int index, float width) {
		freeChart.getXYPlot().getRenderer().setSeriesStroke(index, new BasicStroke(width));
	}
	public void setSeriesLineWidth(String SeriesName, float width) {
		int index = DataSet.getSeriesIndex(SeriesName);
		setSeriesLineWidth(index, width);
	}

	@Override
	public void seriesChanged(SeriesChangeEvent arg0) {
		// TODO Auto-generated method stub
	}

	private ActionListener DataPointActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String ret = JOptionPane.showInputDialog("set Data Points:", DataPoints);
			if(ret != null) {
				DataPoints = Double.parseDouble(ret);
				setDataPoints(DataPoints);
			}
		}
	};
}
