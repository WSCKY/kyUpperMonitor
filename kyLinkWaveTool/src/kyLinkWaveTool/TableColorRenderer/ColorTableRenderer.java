package kyLinkWaveTool.TableColorRenderer;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class ColorTableRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1993L;
	private ArrayList<TableRowColor> tabCol = null;

	@Override
	public Component getTableCellRendererComponent(JTable table,Object value, boolean isSelected, boolean hasFocus,
      int row, int column) {
		// TODO Auto-generated method stub
		setHorizontalAlignment(SwingConstants.CENTER);
		boolean bg_set = false;
		if(column != 0) {
			for(TableRowColor tc : tabCol) {
				if(tc.row == row) {
					setBackground(tc.color);
					bg_set = true;
					break;
				}
			}
		}
		if(!bg_set) this.setBackground(Color.WHITE);
		// extends super default renderer
		return super.getTableCellRendererComponent(table, value,isSelected, hasFocus, row, column);
	}

	public ColorTableRenderer() {
		tabCol = new ArrayList<TableRowColor>();
	}

	public void applyTableColorRenderer(TableColumnModel tcm) {
        for (int i = 1, n = tcm.getColumnCount(); i < n; i++) {
            TableColumn tc = tcm.getColumn(i);
            tc.setCellRenderer(this);//(TableColorRenderer);
        }
	}

	public void clearRowColor() {
		tabCol.clear();
	}

	public Color getRowColor(int row)
	{
		for(TableRowColor tc : tabCol) {
			if(tc.row == row) {
				return tc.color;
			}
		}
		return null;
	}

	public void addRowColor(int row, Color c) {
		tabCol.add(new TableRowColor(row, c));
	}

	public void removeRowColor(int row) {
		for(TableRowColor r : tabCol) {
			if(r.row == row) {
				tabCol.remove(r);
				return;
			}
		}
	}
	public int getColorTableSize() {
		return tabCol.size();
	}
}

class TableRowColor {
	public int row;
	public Color color;
	public TableRowColor(int r, Color c) {
		row = r;
		color = c;
	}
}
