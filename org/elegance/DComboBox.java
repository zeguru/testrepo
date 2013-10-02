package org.elegance;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

import java.util.List;
import java.util.ArrayList;

public class DComboBox extends DField {

	public JComboBox datafield;
	public int linkkey = 0;
	public int cmbkey = 0;

	private Statement st;
	private Connection mydb;
	private String sql;
	private String lookupname;
	private String blankselect;
	private String lptable, lpfield, lpkey, wheresql, ordersql;
	public String linkfield;
	private List<String> lplist;
	private boolean iseditable;
	public boolean inputfield = false;

    public DComboBox(DElement el, JPanel lpanel, Connection db) {
		super(el);

		datafield = new JComboBox();
		lplist = new ArrayList<String>();

		mydb = db;

		lpkey = el.getAttribute("lpkey", "");
		if(lpkey.equals(""))
			lpkey = name;

		lptable = el.getAttribute("lptable");
		lpfield = el.getAttribute("lpfield");

		if(!el.getAttribute("cmbkey", "").equals(""))
			cmbkey = Integer.valueOf(el.getAttribute("cmbkey")).intValue();
		linkfield = el.getAttribute("linkfield", "");

		if(!linkfield.equals(""))
			linkkey = Integer.valueOf(el.getAttribute("linkkey")).intValue();
		blankselect = el.getAttribute("blankselect", "");

		if(!el.getAttribute("inputfield", "").equals("")) {
			inputfield = true;
			linkfield = el.getAttribute("inputfield");
			}

		wheresql = el.getAttribute("wheresql");
		ordersql = el.getAttribute("ordersql");

		datafield.setActionCommand(Integer.toString(cmbkey));

		if(title.length()>0)
			lpanel.add(label);

		lpanel.add(datafield);

		lookupname = lpfield;

		setPos();

		try {
			if(lpkey.equals(lpfield))
				sql = "SELECT " + lpfield + " FROM " + lptable;
			else
				sql = "SELECT " + lpkey + ", " + lpfield + " FROM " + lptable;

			if(wheresql != null)
				sql += " WHERE " + wheresql;
			if(ordersql != null)
				sql += " ORDER BY " + ordersql;
			else
				sql += " ORDER BY " + lpfield;

			st = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			}
		catch(SQLException ex) {
			System.out.println("SQLException : " + ex.getMessage());
			JOptionPane.showMessageDialog(datafield, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
      	}

		// Populate the list
		if(!inputfield)
			getList();

		// alter if it is editable
		iseditable = false;
		if (el.getAttribute("editable", "").equals("true")) {
			datafield.setEditable(true);
			datafield.setSelectedItem("");
			iseditable = true;
		}

		if (el.getAttribute("enabled", "").equals("false")) datafield.setEnabled(false);
 	}

	public void setPos() {
		super.setPos();
		datafield.setLocation(x+lw, y);
		datafield.setSize(w, h);
	}

	public void getList() {
		System.out.println("DEBUG:::: at DComboBox: getList()");
		datafield.removeAllItems();
		lplist.clear();

		if(!blankselect.equals("")) {
			lplist.add("");
			datafield.addItem(blankselect);
		}

		try {
			System.out.println("\n\t\tgetList() sql = " + sql);
			ResultSet rs = st.executeQuery(sql);
			while(rs.next()) {
                lplist.add(rs.getString(lpkey));
				datafield.addItem(rs.getString(lpfield));
			}
			rs.close();
        } catch(SQLException ex) {
        	System.out.println("Combobox get List SQL Error : " + ex.getMessage());
			JOptionPane.showMessageDialog(datafield, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
        }
	}

	public void getList(String wherevalue) {
		datafield.removeAllItems();
		lplist.clear();

		try {
			if(lpkey.equals(lpfield))
				sql = "SELECT " + lpfield + " FROM " + lptable;
			else
				sql = "SELECT " + lpkey + ", " + lpfield + " FROM " + lptable;

			if(wheresql != null) sql += " WHERE " + wheresql + " AND " + linkfield + " = '" + wherevalue + "'";
			else sql += " WHERE " + linkfield + " = '" + wherevalue + "'";
			if(ordersql != null) sql += " ORDER BY " + ordersql;
			else sql += " ORDER BY " + lpfield;

			ResultSet rs = st.executeQuery(sql);
			while(rs.next()) {
                lplist.add(rs.getString(lpkey));
				datafield.addItem(rs.getString(lpfield));
			}
			rs.close();
        } catch(SQLException ex) {
        	System.out.println("Combobox get List 2 SQL Error : " + ex.getMessage());
			JOptionPane.showMessageDialog(datafield, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
        }
	}

	public void getsubList(String wherevalue) {
		datafield.removeAllItems();
		lplist.clear();

		try {
			if(lpkey.equals(lpfield)) sql = "SELECT " + lpfield + " FROM " + lptable;
			else sql = "SELECT " + lpkey + ", " + lpfield + " FROM " + lptable;

			if(wheresql != null) sql += " WHERE " + wheresql + " AND " + linkfield + " ilike '%" + wherevalue + "%'";
			else sql += " WHERE " + linkfield + " ilike '%" + wherevalue + "%'";
			if(ordersql != null) sql += " ORDER BY " + ordersql;
			else sql += " ORDER BY " + lpfield;

			ResultSet rs = st.executeQuery(sql);
			while(rs.next()) {
                lplist.add(rs.getString(lpkey));
				datafield.addItem(rs.getString(lpfield));
			}
			rs.close();
        } catch(SQLException ex) {
        	System.out.println("Combobox get sub List SQL Error : " + ex.getMessage());
			JOptionPane.showMessageDialog(datafield, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
        }
	}

	public void setText(String ldata) {
		if(ldata==null) ldata="";
		int lp = lplist.indexOf(ldata);
		datafield.setSelectedIndex(lp);
	}

	public void setNew() {
		if(!defaultvalue.equals("")) setText(defaultvalue);
	}

	public String getKey(String ldata) {
		String rtkey = "";
		if(linkkey!=0) {
			try {
				sql = "SELECT " + linkfield + " FROM " + lptable;
				sql += " WHERE " + lpkey + " = '" + ldata + "'";

				ResultSet rs = st.executeQuery(sql);
				if(rs.next()) rtkey = rs.getString(linkfield);
				rs.close();
        	} catch(SQLException ex) {
        		System.out.println("Combobox getkey SQL Error : " + ex.getMessage());
				JOptionPane.showMessageDialog(datafield, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
        	}
		}

		return rtkey;
	}

	public String getText() {
		String combovalue = "";
		if(datafield.getSelectedIndex() == -1) {
			combovalue = "null";
		} else if (iseditable) {
			combovalue = (String)datafield.getSelectedItem();
		} else {
			int lp = datafield.getSelectedIndex();
			combovalue = lplist.get(lp);
		}

		return combovalue;
	}

	public String getText(boolean dt) {
		String combovalue = "";
		if(datafield.getSelectedIndex() == -1) {
			combovalue = "";
		} else if (iseditable) {
			combovalue = (String)datafield.getSelectedItem();
		} else {
			int lp = datafield.getSelectedIndex();
			combovalue = lplist.get(lp);
		}

		return combovalue;
	}
}
