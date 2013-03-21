package org.elegance;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Map;
import java.util.HashMap;

import java.awt.event.*;

import javax.swing.JComboBox;

public class DGridCombo {

	JComboBox combo;
	String lpfield, lptable, wheresql, lplink, lpkey, updatefield;
	Map<String, String> hash;

	Connection db;

	public DGridCombo(DElement el, Connection db) {
		//combolist definations
		lptable = el.getAttribute("lptable");
		lpfield = el.getAttribute("lpfield"); 
		lplink = el.getAttribute("lplink");
		lpkey = el.getAttribute("lpkey");
		updatefield = el.getAttribute("updatefield");
		wheresql = el.getAttribute("wheresql");

		this.db = db;

		combo = new JComboBox();
		hash = new HashMap<String, String>();

		getList("-1");
	}

	public void filterList(String filterkey) {
		if ((lplink != null) && (filterkey != null)) getList(filterkey);
	}

	public void getList(String filterkey) {
		String combosql = "SELECT " + lpkey;
		if(!lpkey.equals(lpfield)) combosql += "," + lpfield;
		combosql += " FROM " + lptable;
		if(wheresql != null) {
			combosql += " WHERE " + wheresql;
			if(lplink != null) combosql += " AND " + lplink + " = '" + filterkey + "'";
			} 
		else if(lplink != null) {
			combosql += " WHERE " + lplink + " = '" + filterkey + "'";
		}

		System.out.println("BASE 30 : " + combosql);

		combo.removeAllItems();
		hash.clear();

		try {
			Statement cst = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet crs = cst.executeQuery(combosql);
			while(crs.next()){
				combo.addItem(crs.getString(lpfield));
				hash.put(crs.getString(lpfield), crs.getString(lpkey));
				}
			crs.close();
			cst.close();
			} 
		catch (SQLException ex) {
			System.out.println("The SQL Exeption on : " + ex.getMessage());
			}
		}

	public String getKey(String value) {
		return hash.get(value);
	}

	public String getUpdateField() {
		return updatefield;
	}
}