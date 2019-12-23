package kyLinkWaveTool.TableColorRenderer;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class ColorTableRenderer {
	private Color defaultBackground = null;
	private Color defaultSelectionBackground = null;
	private ArrayList<TableRowColor> tabCol = null;

	DefaultTableCellRenderer TableColorRenderer = new DefaultTableCellRenderer() {
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table,Object value, boolean isSelected, boolean hasFocus,int row, int column) {
			this.setBackground(defaultBackground);
			this.setHorizontalAlignment(SwingConstants.CENTER);
			if(column != 0) {
				for(TableRowColor tc : tabCol) {
					if(tc.row == row) {
						setBackground(tc.color);
						if(isSelected) {
							table.setSelectionBackground(tc.color);
						}
						break;
					}
				}
			}
			Component comp = super.getTableCellRendererComponent(table, value,isSelected, hasFocus, row, column);
			table.setSelectionBackground(defaultSelectionBackground);
			return comp;
		}
	};

	public ColorTableRenderer() {
		tabCol = new ArrayList<TableRowColor>();
		defaultBackground = Color.WHITE;
		defaultSelectionBackground = Color.LIGHT_GRAY;
	}

	public void applyTableColorRenderer(TableColumnModel tcm) {
        for (int i = 1, n = tcm.getColumnCount(); i < n; i++) {
            TableColumn tc = tcm.getColumn(i);
            tc.setCellRenderer(TableColorRenderer);
        }
	}

	public void clearRowColor() {
		tabCol.clear();
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
