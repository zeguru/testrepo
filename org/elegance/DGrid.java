package org.elegance;

import java.io.IOException;

import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JComboBox;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.DefaultCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableModel;
import javax.swing.table.TableCellRenderer;
import javax.imageio.ImageIO;

import javax.swing.JTextField;
import javax.swing.JFileChooser;
import javax.swing.JButton;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Color;
import javax.swing.border.Border;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;


import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.net.URL;
import java.net.MalformedURLException;

public class DGrid implements MouseListener, ActionListener, ListSelectionListener {		//implement FocusListener
	public JPanel panel, filterpanel;
	public JScrollPane tableview;
	public JTable table;
	public JTableHeader th;
	JTextField filterdata;
	JComboBox fieldlist, filterlist;

	public DTableDef tabledef;
	public TableRowSorter<DTableDef> sorter;
	public String linkfield, filtername, wheresql, ordersql;
	public int filterkey = 0;
	public int linkkey = 0;
	String linkdata = null;
	List<String> lplist;
	Map<Integer, DGridCombo> combolist;
	boolean xmlgrid;

	DElement view;
	Connection db;

	int index = -1;

	String labelfield = null;
	String labelvalue = null;
	String labeltable = null;
	String gridlines;	//horizontal, vertical, both
	String rowheight;	//
	String bgurl;

	public List<DContainer> cont;
	public DImageDesktop dtop;

	public boolean ispopup;
	public String doubleclick;


	public DGrid(DElement fielddef, Connection db) {

		view = fielddef;
		this.db = db;

		lplist = new ArrayList<String>();
		combolist =  new HashMap<Integer, DGridCombo>();

		if(fielddef.getAttribute("filterkey") != null)
			filterkey = Integer.valueOf(fielddef.getAttribute("filterkey")).intValue();

		filtername = fielddef.getAttribute("filtername", "filterid");
		wheresql = fielddef.getAttribute("wheresql");
		ordersql = fielddef.getAttribute("ordersql");

		linkfield = fielddef.getAttribute("linkfield");
		if(linkfield != null)
				linkkey = Integer.valueOf(fielddef.getAttribute("linkkey")).intValue();

		doubleclick = fielddef.getAttribute("doubleclick","false");
		String dctarget = fielddef.getAttribute("dctarget","message=>OK");		//?????postgres exception message style
		final String[] dcoption = dctarget.split("=>");


		gridlines = fielddef.getAttribute("gridlines","no");
		rowheight = fielddef.getAttribute("rowheight");
		bgurl = fielddef.getAttribute("bgurl");

		tabledef = new DTableDef(fielddef, db, combolist);
		sorter = new TableRowSorter<DTableDef>(tabledef);
		table = new JTable(tabledef);

		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		System.out.println("DEBUG: Table doubleclick =  " + doubleclick);
		if(doubleclick.equals("true")){
			//table.setGridColor(Color.GRAY);
			//table.selectionForeground(Color.ORANGE);
			//table.setBackground(Color.GRAY);
			table.setForeground(Color.BLUE);
			}

// 		table = new JTable(tabledef){
// 				public Component prepareRenderer(TableCellRenderer renderer,
// 												int rowIndex, int vColIndex) {
// 					Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
// 					if (rowIndex % 2 == 0 && !isCellSelected(rowIndex, vColIndex)) {
// 						c.setBackground(Color.yellow);
// 					} else {
// 						// If not shaded, match the table's background
// 						c.setBackground(getBackground());
// 					}
// 					return c;
// 				}
// 			};

		//table.addFocusListener(this);
		if (fielddef.getAttribute("doubleclick","false").equals("true")){
			//if (!fielddef.getAttribute("tooltip","Double click for more details").equals("")){
				//table.setTooltipText(fielddef.getAttribute("tooltip","Double click for more"));
			//	}
			table.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){
					//if (e.getClickCount() == 2){
					if (e.getClickCount() == 2){
							JTable target = (JTable)e.getSource();
							int row = target.getSelectedRow();
							int column = target.getSelectedColumn();

							// do some action
							if(dcoption[0].contains("message")){
								JOptionPane.showMessageDialog(target, dcoption[1] + "\n" + target.getValueAt(row,column),"Info", JOptionPane.INFORMATION_MESSAGE);
								//JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
								}
							else if(dcoption[0].contains("desk")){
								openDesk(dcoption[1]);
								}
							else if(dcoption[0].contains("other")){
								//openDesk(dcoption[1]);
								System.out.println("other test");
								}
							System.out.println("Double click event on JTable \n\t\t row = " + row + " col = " + column);
							}
						}
				} );
			}


		table.setRowSorter(sorter);
		table.getSelectionModel().addListSelectionListener(this);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 		//just in case there are many columns
		table.setFillsViewportHeight(true);


		if (rowheight != null){
		    table.setRowHeight(Integer.parseInt(rowheight));
		    }
		if(gridlines.equals("horizontal")){
		    table.setShowHorizontalLines(true);
		    }
		else if(gridlines.equals("vertical")){
		    table.setShowVerticalLines(true);
		    }
		else if(gridlines.equals("both")){
		    table.setShowHorizontalLines(true);
		    table.setShowVerticalLines(true);
		    }

		//table.setShowGrid(false);
		table.setColumnSelectionAllowed(false);
		panel = new JPanel(new BorderLayout());
		tableview = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		if(bgurl != null){
		    try{
			//table.setOpague(false);
			System.out.println("FOUND bgurl = " + bgurl);
			Border bg = new GridBorder(ImageIO.read(new URL(bgurl)));
			tableview.setViewportBorder(bg);
			//tableview.setOpaque(false);
			//tableview.getViewport().setOpaque(false);
			tableview.repaint();
			}
		    catch(MalformedURLException uex){
			System.out.println("Faulty URL: " + uex.getMessage());
			}
		    catch(IOException ex){
			System.out.println("Could not fetch URL content: " + ex.getMessage());
			}
		  }

		// change the formater
		table.setDefaultRenderer(Timestamp.class, new DDateTimeRenderer());
		table.setDefaultRenderer(Time.class, new DTimeRenderer());

		// create filter panel on grid
		filterdata = new JTextField();
		filterdata.addActionListener(this);
		String[] filterstr = {"LIKE", "ILIKE", "BETWEEN", "=", ">", "<", "<=", ">="};
		fieldlist = new JComboBox();
		filterlist = new JComboBox(filterstr);
		filterlist.setSelectedIndex(0);

		String gridfilter = fielddef.getAttribute("gridfilter", "");
		//for backward compatibility
		if(gridfilter.equals("true"))
		      gridfilter = "bottom";

		List<DElement> children = new ArrayList<DElement>(fielddef.getElements());
		int i = 0;
		for(DElement el : children) {
			fieldlist.addItem(el.getAttribute("title"));
			lplist.add(el.getValue());

			if(el.getAttribute("lptable") != null) {
			//if(el.getName == "GRIDCOMBO") {
				DGridCombo gc = new DGridCombo(el, db);
				combolist.put(i, gc);
				}
			i++;
			}

		filterpanel = new JPanel(new GridLayout(1, 0));
		filterpanel.add(fieldlist);
		filterpanel.add(filterlist);
		filterpanel.add(filterdata);

		if(gridfilter.equals("top"))
			panel.add(filterpanel, BorderLayout.PAGE_START);
		else if(gridfilter.equals("bottom"))
			panel.add(filterpanel, BorderLayout.PAGE_END);

		panel.add(tableview, BorderLayout.CENTER);

		JTableHeader th = table.getTableHeader();
		th.addMouseListener(this);

		xmlgrid = true;

		// create grid data refresh
		makechild(null);

		//System.out.println(fielddef.getAttribute("name") + " : Grid Done");
	}

	public DGrid(Connection ldb) {
		filtername = "filterid";

		System.out.println("DEBUG: at constructor DGrid(Connection)");

		tabledef = new DTableDef(ldb);
		sorter = new TableRowSorter<DTableDef>(tabledef);

		table = new JTable(tabledef);
		table.setRowSorter(sorter);

		// Use a scrollbar, in case there are many columns.
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		table.setShowGrid(false);
		table.setColumnSelectionAllowed(false);
		panel = new JPanel(new BorderLayout());
		tableview = new JScrollPane(table);

		// change the formater
		table.setDefaultRenderer(Timestamp.class, new DDateTimeRenderer());
		table.setDefaultRenderer(Time.class, new DTimeRenderer());

		// create filter panel on grid
		filterdata = new JTextField();
		filterdata.addActionListener(this);
		String[] filterstr = {"LIKE", "ILIKE", "BETWEEN", "=", "<>", ">", "<", "<=", ">="};
		lplist = new ArrayList<String>();
		fieldlist = new JComboBox();
		filterlist = new JComboBox(filterstr);
		filterlist.setSelectedIndex(0);

		filterpanel = new JPanel(new GridLayout(1, 0));
		filterpanel.add(fieldlist);
		filterpanel.add(filterlist);
		filterpanel.add(filterdata);

		//panel.add(filterpanel, BorderLayout.PAGE_END);
		panel.add(tableview, BorderLayout.CENTER);

		JTableHeader th = table.getTableHeader();
		th.addMouseListener(this);

		xmlgrid = false;
	}

	public void setlist() {
		lplist.clear();
		fieldlist.removeAllItems();

		for(int i=0; i<tabledef.columnName.size(); i++) {
			fieldlist.addItem(tabledef.columnName.get(i));
			lplist.add(tabledef.columnName.get(i));
		}
	}

	public void refresh() {

		System.out.println("DEBUG: refresh():");

		makechild(null);

		// adjust column width
		int i = 0;
		for(Integer w : tabledef.columnWidth) {
			TableColumn column = table.getColumnModel().getColumn(i);
			column.setPreferredWidth(w.intValue());
			i++;
		}
	}

	public void filter(String where) {



		if(ordersql != null)
		      where += " ORDER BY " + ordersql;

		tabledef.filter(where);

		// adjust column width
		int i = 0;
		for(Integer w : tabledef.columnWidth) {
			TableColumn column = table.getColumnModel().getColumn(i);
			column.setPreferredWidth(w.intValue());
			i++;
		}

		// Make combobox editors
		comboEditor();
	}

	public void makechild(String key) {

		//System.out.println("DEBUG: START MODIFYING XTAB QUERY = ");
		String mysql = makequery(key);
		filter(mysql);
	}

	public String getKey() {

			if(table.getSelectedRow()<0) return "-1";

			index = table.convertRowIndexToModel(table.getSelectedRow());

			//System.out.println("DEBUG: getKey(): index = " + index);
			//System.out.println("\t tabledef.keylist.get(index) = " + tabledef.keylist.get(index));
			return tabledef.keylist.get(index);

  	}

	public void comboEditor() {
		for(Integer i : combolist.keySet()) {
			DGridCombo gc = combolist.get(i);
			TableColumn comboColumn = table.getColumnModel().getColumn(i);
			comboColumn.setCellEditor(new DefaultCellEditor(gc.combo));
		}
	}

	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {
		TableColumnModel columnModel = table.getColumnModel();
    	int viewColumn = columnModel.getColumnIndexAtX(e.getX());
    	int column = table.convertColumnIndexToModel(viewColumn);
    	if(e.getClickCount() == 1 && column != -1) {
    		int shiftPressed = e.getModifiers() & InputEvent.SHIFT_MASK;
			List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
			if(shiftPressed == 0) sortKeys.add(new RowSorter.SortKey(column, SortOrder.ASCENDING));
			else sortKeys.add(new RowSorter.SortKey(column, SortOrder.DESCENDING));
			sorter.setSortKeys(sortKeys);
    	}
    }

	/*original
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if(filterdata.getText().length()>0) {
			String mystr = makequery(null);

			if(mystr.equals("")) mystr = " WHERE (";
			else mystr += " AND (";

			mystr += lplist.get(fieldlist.getSelectedIndex()) + " ";
			if (filterlist.getSelectedIndex()<2) {
				mystr += filterlist.getSelectedItem() + " '%";
				mystr += filterdata.getText() + "%')";
			} else {
				mystr += filterlist.getSelectedItem() + " '";
				mystr += filterdata.getText() + "')";
			}

			if(xmlgrid) filter(mystr);
			else tabledef.filterquery(mystr);
		}
	}
	*/

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand().toUpperCase();

		if(filterdata.getText().length()>0) {
			String mystr = makequery(null);


			if (filterlist.getSelectedIndex()<2) {		//for LIKE and ILIKE
				if(mystr.equals(""))
					mystr = " WHERE (UPPER(";
				else
					mystr += " AND (UPPER(";

				mystr += lplist.get(fieldlist.getSelectedIndex()) + ") ";

				//mystr = mystr.replace(" ","%");
				mystr += filterlist.getSelectedItem() + " '%";
				mystr += filterdata.getText().toUpperCase().replace(" ","%") + "%')";

				}
			else if (filterlist.getSelectedIndex()==2) {		//for BETWEEN

				if(mystr.equals(""))
					mystr = " WHERE (";
				else
					mystr += " AND (";

				mystr += lplist.get(fieldlist.getSelectedIndex()) + ") ";

				//get rid of the word UPPER
				//mystr.replace("UPPER");
				mystr += filterlist.getSelectedItem() + " ";
				mystr += filterdata.getText().toUpperCase() + "";
				}
			else {

				if(mystr.equals(""))
					mystr = " WHERE (";
				else
					mystr += " AND (";

				mystr += lplist.get(fieldlist.getSelectedIndex()) + " ";

				mystr += filterlist.getSelectedItem() + " '";
				mystr += filterdata.getText() + "')";
				}

			System.out.println("Action Command = " + command);
			System.out.println("Filter String = " + mystr);

			if(xmlgrid)
				filter(mystr);
			else
				tabledef.filterquery(mystr);
		}
	else{
		//if empty use %
		String mystr = makequery(null);

		if (filterlist.getSelectedIndex()<2) {		//for LIKE and ILIKE
			if(mystr.equals(""))
				mystr = " WHERE (UPPER(";
			else
				mystr += " AND (UPPER(";

			mystr += lplist.get(fieldlist.getSelectedIndex()) + ") ";

			//mystr = mystr.replace(" ","%");
			mystr += filterlist.getSelectedItem() + " '%";
			mystr += filterdata.getText().toUpperCase().replace(" ","%") + "%')";

			}
		if(xmlgrid)
			filter(mystr);
		else
			tabledef.filterquery(mystr);
		}
	}


	public void valueChanged(ListSelectionEvent event) {
		if (!event.getValueIsAdjusting()) {
			for(Integer i : combolist.keySet()) {
				DGridCombo gc = combolist.get(i);
				gc.filterList(getKey());
			}
		}
	}

	public void savecvs() {
		String export = view.getAttribute("export", "");

		if(export.equals("accessfile")) {
			for(DElement el : view.getElements()) {
				if(el.getName().equals("ACCESS")){
					DExportAccess access = new DExportAccess(el, db);
				}
			}
		} else {
			// export to cvs
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showSaveDialog(panel);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				String filename = fc.getSelectedFile().getAbsolutePath();
				tabledef.savecvs(filename);
			}
		}
	}


	public String makequery(String key) {
		String mysql = "";
		String mylink = " WHERE ";


		if(wheresql != null) {
			mysql += " WHERE " + wheresql;
			mylink = " AND ";
		}

		if(key != null)
			linkdata = key;

		if(linkfield != null) {
			if(linkdata == null) mysql += mylink + " (" + linkfield + " is null)";
			else mysql += mylink + " (" + linkfield + " = '" + linkdata + "')";
		}

		return mysql;
	}

	public int getIndex(){
		return index;
		}


	//focus tests.. events OK but side effects kibao....affects TextLookup
// 	public void focusGained(FocusEvent e) {
// 		//if(e.getSource() instanceof JButton) System.out.println("Button won");
// 		if(e.getSource() instanceof JTable){
// 			System.out.println("JTABLE GOT FOCUS");
// 			refresh();
// 			}
// 		}
//
//    public void focusLost(FocusEvent e) {
// 		//if(e.getSource() instanceof JButton) System.out.println("Button lost");
// 		if(e.getSource() instanceof JTable)
// 			System.out.println("JTABLE LOST FOCUS");
// 		}


	public boolean openDesk(String key){

		System.out.println("\nDEBUG: at openDesk() key = " + key);
		int activeform = -1;

		activeform = DBuild.keylist.indexOf(key);

		cont = DBuild.container;
		dtop = DBuild.desktop;

		if(activeform < 0) return false;

		if(!cont.get(activeform).started) {		//if window is not started...
			System.out.println("DGrid:openDesk(). making Container");
			cont.get(activeform).makeContainer();	//make another container
			}

		if(!cont.get(activeform).isVisible){		//if window is not visible
			System.out.println("DGrid:openDesk(). setting visible");
			dtop.add(cont.get(activeform));
			cont.get(activeform).updateView();			//TESTING
			cont.get(activeform).setVisible(true);
			}

		else {										//if visible but out of focus
			System.out.println("DGrid:openDesk(). giving focus");
			try {
        		cont.get(activeform).setSelected(true);
				cont.get(activeform).updateView();			//TESTING

				//cont.get(activeform).setVisible(true);		//not tested... we need to update report data when it gets focus

				if(cont.get(activeform).isIcon()) {
					cont.get(activeform).setIcon(false);
					}
				}
			catch (java.beans.PropertyVetoException err) {
				activeform = -1;
				}
			}

		//btree.tree.clearSelection();
		return true;
		}


}
