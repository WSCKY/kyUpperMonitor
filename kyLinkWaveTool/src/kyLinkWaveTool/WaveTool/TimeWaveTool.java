package kyLinkWaveTool.WaveTool;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.general.SeriesChangeListener;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class TimeWaveTool extends ChartPanel implements SeriesChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static JFreeChart freeChart = null;
	private static TimeSeriesCollection DataSet = null;
	private Millisecond CurrentMillis = null;

	public TimeWaveTool(String Title) {
		super(createChart(Title));
		// TODO Auto-generated constructor stub
		CurrentMillis = new Millisecond();
	}

	private static JFreeChart createChart(String title) {
		DataSet = new TimeSeriesCollection();
		freeChart = ChartFactory.createTimeSeriesChart(title, "Time", "", DataSet, true, true, false);
		XYPlot xyplot = freeChart.getXYPlot();
		ValueAxis valueaxis = xyplot.getDomainAxis();
		valueaxis.setAutoRange(true);
//		valueaxis.setInverted(true);
		valueaxis.setFixedAutoRange(500);
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
		TimeSeries series = new TimeSeries(SeriesName);
		series.addChangeListener(this);
		DataSet.addSeries(series);
		return DataSet.indexOf(series);
	}
	public void removeSeries(String SeriesName) {
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
	}
	public int getSeriesCount() {
		return DataSet.getSeriesCount();
	}
	public int getSeriesIndex(String SeriesName) {
		return DataSet.getSeriesIndex(SeriesName);
	}
	public void addDataToSeries(String SeriesName, double d) {
		TimeSeries s = DataSet.getSeries(SeriesName);
		s.add(CurrentMillis, d);
		CurrentMillis = (Millisecond) CurrentMillis.next();
	}
	public void setAutoRange(boolean auto) {
		freeChart.getXYPlot().getRangeAxis().setAutoRange(auto);
	}
	public void setLockZeroPoint(boolean flag) {
		NumberAxis numAxis = ((NumberAxis)freeChart.getXYPlot().getRangeAxis());
		numAxis.setAutoRangeIncludesZero(flag);
	}
	public void setDataPoints(int n) {
		freeChart.getXYPlot().getDomainAxis().setFixedAutoRange(n);
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
}
