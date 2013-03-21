package org.elegance;

import java.util.List;
import java.util.Date;

import java.sql.*;

public class DExportAccess {
	DElement root;
	Connection accdb;
	Connection prdb;

	public DExportAccess(DElement root, Connection db) {
		this.root = root;
		prdb = db;

		try {
			String accessdb = root.getAttribute("accessdb");
			Driver d = (Driver)Class.forName("sun.jdbc.odbc.JdbcOdbcDriver").newInstance();
    		accdb = DriverManager.getConnection("jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=" + accessdb);
		} catch (ClassNotFoundException ex) {
			System.out.println("Cannot find the database driver classes. : " + ex);
		} catch (SQLException ex) {
			System.out.println("SQL Error : " + ex);
		} catch (Exception ex) {
	        ex.printStackTrace();
	    }

		System.out.println(new Date() + " Access database creation : Done");
		for(DElement view : root.getElements()) MakeAccess(view);

		close();
	}

	public void MakeAccess(DElement view) {
		String mysql = "";
		for(DElement el : view.getElements()) {
			if(!mysql.equals("")) mysql += ", ";
			if(el.getAttribute("function") != null) {
				mysql += el.getAttribute("function") + " as " + el.getValue();
			} else if(el.getAttribute("data") != null) {
				if(el.getAttribute("type") == null) {
					mysql += "'" + el.getAttribute("data") + "' as " + el.getValue();
				} else if(el.getAttribute("data").equals("null")) {
					mysql += "CAST(null as " +  el.getAttribute("type") + ") as " + el.getValue();
				} else {
					mysql += "CAST('" + el.getAttribute("data") + "' as " +  el.getAttribute("type") + ") as " + el.getValue();
				}
			} else {
				mysql += el.getValue();
			}
		}
			
		mysql = "SELECT " + mysql + " FROM " + view.getAttribute("table");
		if(view.getAttribute("wheresql") != null) mysql += " WHERE " + view.getAttribute("wheresql");
		if(view.getAttribute("ordersql") != null) mysql += " ORDER BY " + view.getAttribute("ordersql");
		
		System.out.println(mysql);
		
		try {
			Statement st = prdb.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = st.executeQuery(mysql);

			// delete from table
			mysql = "DELETE FROM " + view.getAttribute("acctable");
			Statement stDEL = accdb.createStatement();
			stDEL.executeUpdate(mysql);
			stDEL.close();

			while(rs.next()) {
				boolean ss = false;
				mysql = "INSERT INTO " + view.getAttribute("acctable") + " (";

				for(DElement el : view.getElements()) {
					if(ss) mysql += ", ";
					else ss = true;
					mysql += el.getValue();
				}
				mysql += ") VALUES (";

				ss = false;
				for(DElement el : view.getElements()) {
					if(ss) mysql += ", ";
					else ss = true;
					
					if(rs.getString(el.getValue()) == null) mysql += "''";
					else mysql += "'" + rs.getString(el.getValue()) + "'";
				}
				mysql += ");";
			System.out.println(mysql);

				Statement stUP = accdb.createStatement();
				stUP.executeUpdate(mysql);
				stUP.close();
			}

			rs.close();
			st.close();
		} catch (SQLException ex) {
			System.out.println("SQL Error : " + ex);
		}
	}

	public void close() {
		try {
			accdb.close();
		} catch (SQLException ex) {
			System.out.println("SQL Error : " + ex);
		}
	}

}