package kyLinkWaveTool;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import org.dom4j.DocumentException;

import kyLink.kyLinkPackage;
import kyLinkWaveTool.DataUnit.DataCollecter;
import kyLinkWaveTool.DataUnit.kyLinkGroup;
import kyLinkWaveTool.DataUnit.kyLinkMember;
import kyLinkWaveTool.Observer.ObserveMbr;
import kyLinkWaveTool.Observer.Observer;
import kyLinkWaveTool.Observer.ObserverEventListener;
import kyLinkWaveTool.TableColorRenderer.ColorTableRenderer;

public class ManagerGUI extends JPanel implements Runnable {
	private static final long serialVersionUID = 0x55AAL;

	private ArrayList<kyLinkGroup> GroupList = null;

	private static final String GroupNameTotal = "All Packages";
	private JComboBox<String> groupBox = new JComboBox<String>();
	private JComboBox<String> typeBox = new JComboBox<String>(kyLinkMember.DataTypes);

	private DataCollecter DataTool = null;
	private Observer MainObserv = null;

	private static final String ColumnName_Enable = "EN";
	private static final String ColumnName_Member = "Member";
	private static final String ColumnName_Group = "Group";
	private static final String ColumnName_Value = "Value";
	private static final String ColumnName_Type = "Type";
	private static String[] ColumnNames =
		{ColumnName_Enable, ColumnName_Member, ColumnName_Group, ColumnName_Value, ColumnName_Type};
	private DefaultTableModel Model = null;
	private JTable mTable = null;
	private ColorTableRenderer TableRowColorRenderer = null;

	private JTextField FileNameText = null;
	private JFileChooser FileChoose = null;
	private static File SystemHomeDirectory = FileSystemView.getFileSystemView().getHomeDirectory();

	private Semaphore TabDataUpdateSemap = null;

	public ManagerGUI() {
		DataTool = new DataCollecter();
		MainObserv = new Observer();
/* GUI Initialize */
		this.setLayout(new BorderLayout());
		groupBox.setFont(groupBox.getFont().deriveFont(Font.BOLD, 18));
		groupBox.setToolTipText("kyLink packages");
		groupBox.setEditable(false);
		groupBox.setMaximumRowCount(8);
		groupBox.addItem(GroupNameTotal);

		this.add(groupBox, BorderLayout.NORTH);

		Model = new DefaultTableModel(null, ColumnNames);
		mTable = new JTable(Model);
		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
		tcr.setHorizontalAlignment(SwingConstants.CENTER);
		mTable.getTableHeader().setDefaultRenderer(tcr);
		TableColumn tc = mTable.getColumnModel().getColumn(0); // "checkbox"
		tc.setCellEditor(mTable.getDefaultEditor(Boolean.class));
		tc.setCellRenderer(mTable.getDefaultRenderer(Boolean.class));
		mTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(typeBox)); // "combobox"
		mTable.setRowHeight(25);
		mTable.setFont(mTable.getFont().deriveFont(Font.BOLD, 16));
		mTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // select one list index at a time.
		mTable.setSelectionBackground(Color.LIGHT_GRAY);

		TableRowColorRenderer = new ColorTableRenderer();
		TableRowColorRenderer.applyTableColorRenderer(mTable.getColumnModel());

		this.add(new JScrollPane(mTable), BorderLayout.CENTER);

		FileChoose = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("desc file(*.pdesc)", "pdesc");
		FileChoose.setFileFilter(filter);
		FileChoose.setCurrentDirectory(SystemHomeDirectory);

		JPanel panel = new JPanel(); panel.setLayout(new BorderLayout());
		JButton SelectBtn = new JButton("Browse");
		SelectBtn.setFont(SelectBtn.getFont().deriveFont(Font.BOLD));
		SelectBtn.addActionListener(SelectNewFileListener);
		FileNameText = new JTextField("no desc file.");
		FileNameText.setAutoscrolls(true);
		FileNameText.setEditable(false);
		FileNameText.setFont(new Font("Courier New", Font.BOLD, 20));
		panel.add(FileNameText, BorderLayout.CENTER); panel.add(SelectBtn, BorderLayout.EAST);
		this.add(panel, BorderLayout.SOUTH);
/* Listeners */
		/* update table row selection background */
		mTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						// TODO Auto-generated method stub
						Color c = TableRowColorRenderer.getRowColor(mTable.getSelectedRow());
						if(c == null) c = Color.LIGHT_GRAY;
						mTable.setSelectionBackground(c);
					}
				}
			);
		/* new group selected */
		groupBox.addItemListener(
				new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent e) {
						// TODO Auto-generated method stub
						if(GroupList == null) return;
						if(e.getStateChange() == ItemEvent.SELECTED) { // new group selected
							RefreshTableByGroup(groupBox.getItemAt(groupBox.getSelectedIndex())); // 1, refresh table content
							FitTableColumns(mTable); // 2, fit table columns
							RefreshTableRowColor(); // 3, refresh table colors
						}
					}
				}
			);
		/* component resized */
		this.addComponentListener(
				new ComponentAdapter() {
					@Override
					public void componentResized(ComponentEvent e) {
						FitTableColumns(mTable);
					}
				}
			);
		/* listen the value update */
		Model.addTableModelListener(TableListener);

		TabDataUpdateSemap = new Semaphore(1);
		(new Thread(this)).start();
	}

	public void setConfigFile(String path) throws DocumentException {
		if(DataTool.setConfigFile(path) == true) {
			FileNameText.setText(path);
			GroupList = DataTool.decode();
			if(GroupList != null) {
				groupBox.removeAllItems();
				groupBox.addItem(GroupNameTotal);
				for(kyLinkGroup g : GroupList) {
					groupBox.addItem(g.groupName);
				}
				groupBox.setSelectedIndex(0);
				/* refresh table automatically by listener */
			}
		}
	}

	public void addObserverEventListener(ObserverEventListener l) {
		MainObserv.addObserverEventListener(l);
	}

	private boolean TableListenerEnableFlag = true;
	private TableModelListener TableListener = new TableModelListener() {
		@Override
		public void tableChanged(TableModelEvent e) {
			// TODO Auto-generated method stub
			int type = e.getType(); // get event type.
            int row = e.getFirstRow(); // get row index.
            int column = e.getColumn(); // get line index.
            if(type == TableModelEvent.INSERT) {
            	
            } else if(type == TableModelEvent.UPDATE) {
            	if(Model.getColumnName(column).equals(ColumnName_Enable)) { // check if we changed the column of "EN"
            		if(TableListenerEnableFlag == true) {
	            		boolean isEnabled = false;
	            		String groupName = null, membrName = null;
	            		synchronized(Model) { // we must make sure the "Model" is NOT in changing
	            			isEnabled = (boolean)Model.getValueAt(row, column);
	            			groupName = (String)Model.getValueAt(row, Model.findColumn(ColumnName_Group));
	            			membrName = (String)Model.getValueAt(row, Model.findColumn(ColumnName_Member));
	            		}
	            		if(isEnabled) {
	            			Color c = addToObserver(groupName, membrName);
	            			TableRowColorRenderer.addRowColor(row, c);
	            			/* update selection background */
	            			if(row == mTable.getSelectedRow()) {
	            				mTable.setSelectionBackground(c);
	            			}
	            		} else {
	            			removeFromObserver(groupName, membrName);
	            			TableRowColorRenderer.removeRowColor(row);
	            			/* update selection background */
	            			if(row == mTable.getSelectedRow()) {
	            				mTable.setSelectionBackground(Color.LIGHT_GRAY);
	            			}
	            		}
            		}
            	}
            } else if (type == TableModelEvent.DELETE) {
            	
            } else {
            	
            }
		}
	};
	/* Observer operation */
	private Color addToObserver(String gName, String mName) {
		ObserveMbr om = new ObserveMbr(gName + File.separator + mName);
		for(kyLinkGroup g : GroupList) {
			if(g.groupName.equals(gName)) {
				om.setGroupId(g.getIdInteger());
				for(kyLinkMember m : g.getMemberList()) {
					if(m.mbrName.equals(mName)) {
						om.setMbrInfo(m.mbrType, m.mbrOffset);
					}
				}
			}
		}
		return MainObserv.addObserveMbr(om);
	}
	private void removeFromObserver(String gName, String mName) {
		MainObserv.removeObserveMbr(gName + File.separator + mName);
	}

	private void RefreshTableByGroup(String groupName) {
		synchronized(Model) { // lock Model resource
			Model.setRowCount(0); // clear all elements
			int Index = DataTool.getGroupIndexByName(groupName);
			if(Index != -1) {
				ArrayList<kyLinkMember> l = GroupList.get(Index).getMemberList();
				for(kyLinkMember m : l) {
					Model.addRow(new Object[]{false, m.mbrName, groupName, String.valueOf(m.mbrValue), m.mbrType});
				}
			} else {
				// show all packages.
				for(kyLinkGroup g : GroupList) {
					ArrayList<kyLinkMember> l = g.getMemberList();
					for(kyLinkMember m : l) {
						Model.addRow(new Object[]{false, m.mbrName, g.groupName, String.valueOf(m.mbrValue), m.mbrType});
					}
				}
			}
		}
	}

	public void DataPackageProcess(kyLinkPackage rxData) {
		if(GroupList == null) return;
		for(kyLinkGroup g : GroupList) {
			ArrayList<kyLinkMember> ml = g.getMemberList();
			if(g.getIdInteger() == (int)(rxData.msg_id & 0xFF)) {
				for(kyLinkMember m : ml) {
					m.mbrValue = (float) rxData.readoutTypedData(m.mbrType, m.mbrOffset);
				}
				TabDataUpdateSemap.release();
				return;
			}
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		int column = Model.findColumn(ColumnName_Value);
		while(true) {
			try {
				TabDataUpdateSemap.acquire();
			} catch (InterruptedException e) {
				System.err.println("Semaphore acquire failed <ManagerGUI/run>");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(GroupList == null) continue;
			int Index = DataTool.getGroupIndexByName(groupBox.getItemAt(groupBox.getSelectedIndex()));
			if(Index != -1) {
				ArrayList<kyLinkMember> ml = GroupList.get(Index).getMemberList();
				for(kyLinkMember m : ml) {
					Model.setValueAt(m.mbrValue, ml.indexOf(m), column);
				}
			} else {
				int tabOff = 0;
				for(kyLinkGroup g : GroupList) {
					ArrayList<kyLinkMember> ml = g.getMemberList();
					for(kyLinkMember m : ml) {
						Model.setValueAt(m.mbrValue, tabOff + ml.indexOf(m), column);
					}
					tabOff += ml.size();
				}
			}
		}
	}

	private void FitTableColumns(JTable myTable) {
		/* return if this table is NOT showing on screen */
		if(!myTable.isShowing()) return;
        JTableHeader header = myTable.getTableHeader();
        int rowCount = myTable.getRowCount();
        int totalWidth = myTable.getWidth();
        Enumeration<TableColumn> columns = myTable.getColumnModel().getColumns();
        Vector<Integer> allWidth = new Vector<Integer>();
        int addWidth = 0;
        while (columns.hasMoreElements()) {
            TableColumn column = (TableColumn) columns.nextElement();
            int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
            int width = (int) myTable.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(myTable, column.getIdentifier(), false, false, -1, col).getPreferredSize().getWidth();
            for (int row = 0; row < rowCount; row ++) {
                int preferedWidth = (int) myTable.getCellRenderer(row, col).getTableCellRendererComponent(myTable, myTable.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();
                width = Math.max(width, preferedWidth);
            }
            int w = width + myTable.getIntercellSpacing().width;
            addWidth += w;
            allWidth.add(w);
        }

        int remainWidth = (totalWidth - addWidth) / myTable.getColumnCount();
        Enumeration<Integer> enumWidth = allWidth.elements();
        int col_idx = 0;
        while(enumWidth.hasMoreElements()) {
        	int w = enumWidth.nextElement();
        	TableColumn column = myTable.getColumnModel().getColumn(col_idx ++);
        	w += remainWidth;
        	header.setResizingColumn(column);
        	column.setWidth(w);
        }
    }

	private ActionListener SelectNewFileListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			int ret = FileChoose.showDialog(null, "Choose");
			if(ret == JFileChooser.APPROVE_OPTION ) {
				File file = FileChoose.getSelectedFile();
				try {
					setConfigFile(file.getAbsolutePath());
				} catch (DocumentException e1) {
					System.err.println("FAILED to set kylink descriptor file!");
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	};

	private void RefreshTableRowColor() {
		int idx = groupBox.getSelectedIndex();
		if(idx == 0) {
			int tabOff = 0;
			TableRowColorRenderer.clearRowColor(); // clear all colors
			if(GroupList == null) return;
			for(kyLinkGroup g : GroupList) { // show all groups
				ArrayList<kyLinkMember> ml = g.getMemberList();
				if(ml == null) continue;
				int gid = g.getIdInteger();
				MainObserv.takObserveMbr();
				ArrayList<ObserveMbr> ol = MainObserv.getObserverMbrList();
				if(ol != null) {
					for(ObserveMbr omb : ol) {
						if(omb.getGroupId() == gid) {
							for(kyLinkMember m : ml) {
								if(m.mbrOffset == omb.getMbrOffset()) {
									int row = tabOff + ml.indexOf(m);
									/* !!!WE MUST CLOSE MODEL UPDATE LISTENER!!! */
									TableListenerEnableFlag = false; /* !!!STOP LISTENER!!! */
									Model.setValueAt(true, row, 0);
									TableRowColorRenderer.addRowColor(row, omb.getColor());
									TableListenerEnableFlag = true; /* !!!START LISTENER!!! */
									break;
								}
							}
						}
					}
				}
				MainObserv.relObserveMbr();
				tabOff += ml.size();
			}
		} else {
			kyLinkGroup g = null;
			try {
				g = GroupList.get(idx - 1); // get group object
			} catch (IndexOutOfBoundsException e) {
				System.err.println("ERROR: select index out of bounds!");
				return;
			}
			TableRowColorRenderer.clearRowColor(); // clear all colors
			ArrayList<kyLinkMember> ml = g.getMemberList(); // get member list
			if(ml == null) return;
			int gid = g.getIdInteger(); // get group id
			MainObserv.takObserveMbr();
			ArrayList<ObserveMbr> ol = MainObserv.getObserverMbrList(); // get observer list
			if(ol != null) { // null check
				for(ObserveMbr omb : ol) {
					if(omb.getGroupId() == gid) {
						for(kyLinkMember m : ml) {
							if(m.mbrOffset == omb.getMbrOffset()) {
								int row = ml.indexOf(m);
								/* !!!WE MUST CLOSE MODEL UPDATE LISTENER!!! */
								TableListenerEnableFlag = false; /* !!!STOP LISTENER!!! */
								Model.setValueAt(true, row, 0);
								TableRowColorRenderer.addRowColor(row, omb.getColor());
								TableListenerEnableFlag = true; /* !!!START LISTENER!!! */
								break;
							}
						}
					}
				}
			}
			MainObserv.relObserveMbr();
		}
	}

	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }
		String path = null;
		if(args.length > 0) path = args[0];
		ManagerGUI mg = new ManagerGUI();
		if(path != null) {
			try {
				mg.setConfigFile(path);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		JFrame f = new JFrame("example");
		f.setSize(360, 600);
		f.setTitle("kyLink Package Manager");
		f.setResizable(true);
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(mg);
		f.setVisible(true);
	}
}
