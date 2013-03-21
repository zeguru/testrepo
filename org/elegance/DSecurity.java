package org.elegance;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;

public class DSecurity {

	String tablename;
	boolean isread, isadd, isedit, isdelete, isexec;

	public DSecurity() {
		isread = true;
		isadd = true;
		isedit = true;
		isdelete = true;
		isexec = true;
	}

	public void addsecurity(DElement fielddef, Connection db) {
		isread = false;
		isadd = false;
		isedit = false;
		isdelete = false;
		isexec = false;


        try {
			// check for super user privillages
			String str;
			if(DLogin.getAppDictionary() == null)
			    str = "SELECT superuser, rolename FROM users WHERE userid = getUserID()";
			else
			    str = "SELECT is_super_user as superuser, role_name as rolename FROM users WHERE user_name = '" + DLogin.getLoggedInUser() +"'";
			//WHERE UPPER(user_name) = UPPER('" + user + "') AND user_passwd = MD5('" + pwd + "')";

			Statement gst = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet grs = gst.executeQuery(str);
			if(grs.next()) {
				if(grs.getBoolean("superuser")) {
					isread = true;
					isadd = true;
					isedit = true;
					isdelete = true;
					isexec = true;
				} else {
					getsecurity(fielddef, grs.getString("rolename"));
				}
			}
			grs.close();
			gst.close();
        } catch(SQLException ex) {
            System.out.println("Security SQLException: " + ex.getMessage());
        }
	}

	public void getsecurity(DElement fielddef, String rolename) {
		List<DElement> children = new ArrayList<DElement>(fielddef.getElements());
		for(DElement el : children) {
			String role = el.getAttribute("name");
			if(role.equals(rolename)) {
				String rights = el.getAttribute("rights");
				if (rights.contains("r")) isread = true;
				if (rights.contains("a")) isadd = true;
				if (rights.contains("e")) isedit = true;
				if (rights.contains("d")) isdelete = true;
				if (rights.contains("x")) isexec = true;
			}
		}
	}
}

