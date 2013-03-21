package org.elegance;

import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Map;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.TableModelEvent;

public class DAttributeTable extends AbstractTableModel {

	public List<String> columnTitle;
	public List<Integer> columnWidth;
	public List<Integer> dataWidth;
	public Vector<Vector> rows;
	public DTreeNode node = null;
	DefaultTreeModel treemodel;

	public DAttributeTable() {
		columnTitle = new ArrayList<String>();
		rows = new Vector<Vector>();

		columnTitle.add("Name");
		columnTitle.add("Value");
	}

 	public String getColumnName(int col) {
		return columnTitle.get(col);
	}

	public int getRowCount() {
		return rows.size();
	}

	public int getColumnCount() {
		return columnTitle.size();
	}

    public Object getValueAt(int aRow, int aColumn) {
        Vector row = (Vector)rows.elementAt(aRow);
        return row.elementAt(aColumn);
    }

	public boolean isCellEditable(int row, int col) {
		return true;
	}

	public void setValueAt(Object value, int row, int col) {
		Vector<Object> dataRow = rows.elementAt(row);
		dataRow.setElementAt(value, col);

		if (node == null) return;

		String mydata = (String)dataRow.elementAt(1);
		mydata = mydata.replaceAll("<", "&lt;");

		node.dkey.setAttribute((String)dataRow.elementAt(0), mydata);
		treemodel.reload(node);
	}

	public void setdata(DTreeNode node) { // Get all rows.
		this.node = node;
		if (node == null) return;

		Map<String, String> attributes = node.dkey.getAttributes();
		rows.removeAllElements();

		for (Map.Entry<String, String> el : attributes.entrySet()) {
			Vector<String> row = new Vector<String>();
    		row.add(el.getKey());
			row.add(el.getValue());
			rows.add(row);
		}

		treemodel = new DefaultTreeModel(node);

       	fireTableChanged(null); // Tell the listeners a new table has arrived.
	}

	public void addRow() {
		Vector<String> row = new Vector<String>();
   		row.add("");
		row.add("");
		rows.add(row);

       	fireTableChanged(null); // Tell the listeners a new table has arrived.
	}

	public void delRow(int row) {
		if(row<0) return;

		Vector<Object> dataRow = rows.elementAt(row);

		if (node == null) return;
		node.dkey.removeAttribute((String)dataRow.elementAt(0));

		rows.remove(row);
		fireTableChanged(null); // Tell the listeners a new table has arrived.
	}
}
