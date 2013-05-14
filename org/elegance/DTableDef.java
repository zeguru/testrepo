package org.elegance;

import java.sql.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

import java.util.Map;
import java.util.HashMap;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Icon;

import java.io.StringWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DTableDef extends AbstractTableModel {

	JPanel parentPanel;

	List<String> columnTitle;
	public List<String> columnName;
	List<Integer> columnWidth;
	List<Boolean> columnEdit;
	List<String> columnType;
	List<DElement> children;
	Vector rows;

	Statement st, upst, autost;
   	public ResultSet rs;
	ResultSetMetaData metaData;

	public String updatetable, updatefield, tablename, sql, autofield, autonumber;

	String keyfield;


	//crosstab stuff
	String rowid;		//field that does row identification
	String[] xcol;		//set of fields (pk, display col, table name) that asist in
	String insertmissing;	//wether or not to INSERT missing records
	String injectlinkb4;	//when necessary to MODIFY XTAB QUERY ON THE FLY
	String crosstab_where;	//used to link to the selected linkkey/linkfield


	public List<String> keylist;
	public List<String> rowidlist;
	public DSecurity security;
	Connection mydb;
	Map<Integer, DGridCombo> combolist;

	public DTableDef(Connection db) {
		columnName = new ArrayList<String>();
		columnTitle = new ArrayList<String>();
		columnWidth = new ArrayList<Integer>();
		columnEdit = new ArrayList<Boolean>();
		keylist = new ArrayList<String>();
		rowidlist = new ArrayList<String>();
		columnType = new ArrayList<String>();
		rows = new Vector();
		security = new DSecurity();

        // create a record set
		mydb = db;
		try {
			st = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			}
		catch(SQLException ex) {
			System.out.println("Grid Data Create SQLException : " + ex.getMessage());
		}
	}

	public DTableDef(DElement fielddef, Connection db, Map<Integer, DGridCombo> combolist, JPanel p) {

		children = new ArrayList<DElement>(fielddef.getElements());
		this.combolist = combolist;
		this.parentPanel = p;

		columnName = new ArrayList<String>();
		columnTitle = new ArrayList<String>();
		columnWidth = new ArrayList<Integer>();
		columnEdit = new ArrayList<Boolean>();
		keylist = new ArrayList<String>();
		rowidlist = new ArrayList<String>();
		columnType = new ArrayList<String>();
		rows = new Vector();
		security = new DSecurity();

		tablename = fielddef.getAttribute("table");
		keyfield = fielddef.getAttribute("keyfield");
		autofield = fielddef.getAttribute("autofield", "");
		autonumber = fielddef.getAttribute("autonumber", "");
		updatetable = fielddef.getAttribute("updatetable", "");
		updatefield = fielddef.getAttribute("updatefield", "");

		rowid = fielddef.getAttribute("rowid");
		xcol = fielddef.getAttribute("xcol","").split(",");
		insertmissing = fielddef.getAttribute("insertmissing","false");
		injectlinkb4 = fielddef.getAttribute("injectlinkb4","");

		boolean isfirst = true;
		sql = "SELECT ";
		for(DElement el : children) {
			if(el.getName().equals("TEXTFIELD")) {

        		if(!isfirst)
					sql += ", ";
            	else
					isfirst = false;

            	sql += el.getValue().trim();

				columnName.add(el.getValue().trim());
				columnTitle.add(el.getAttribute("title"));
				columnWidth.add(Integer.valueOf(el.getAttribute("w")) + 40);

				if(el.getAttribute("edit", "false").equals("true"))
					columnEdit.add(true);
				else
					columnEdit.add(false);

				String mytype = el.getAttribute("type");
				columnType.add(mytype);
				}
			else if(el.getName().equals("SECURITY")) {
				security.addsecurity(el, db);
				}
			}

		sql += ", " + rowid + " as rowid";		//crosstab rowid
		sql += ", " + keyfield + " as keyfield";
		if(!autofield.equals(""))
			sql += ", " + autofield;
		if(!autonumber.equals(""))
			sql += ", " + autonumber;



		String newsql = "";
		if(!injectlinkb4.equals("")){
		    sql += " FROM " + tablename;
		    newsql = sql;
		    }
		else{
		    sql += " FROM " + tablename;
		    newsql = sql;
		    if(!fielddef.getAttribute("linkkey", "").equals("")) {
			    String linkfield = fielddef.getAttribute("linkfield");
			    newsql += " WHERE " + linkfield + " is null";
			    }
		    }

		//if(!user.equals("")){	//if only user related data is needed
		//    }
		System.out.println("NEWSQL = " + newsql);

        // create a record set
		mydb = db;
		try {
			upst = db.createStatement();
        	st = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = st.executeQuery(newsql);
			metaData = rs.getMetaData();

			if(!autofield.equals(""))
				autost = db.createStatement();
			if(!autonumber.equals(""))
				autost = db.createStatement();
			}
		catch(SQLException ex) {
			//JOptionPane.showMessageDialog(this, "Title......." + "\n" + "33","Info", JOptionPane.INFORMATION_MESSAGE);
			JOptionPane.showMessageDialog(parentPanel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
			System.out.println("Grid Data Create SQLException : " + ex.getMessage());
			}
	}

	public void filter(String where) {
		String oldsql = sql;

		if(!injectlinkb4.equals("")){
		    sql = sql.replace(injectlinkb4, " " + where + " " + injectlinkb4);
		    crosstab_where = where;		//do we need to remove the parentheses?
		    }
		else{
		    // Create a filter expresion
		    sql += where;
		    }
		refresh();

		sql = oldsql;
	}

	public void filterquery(String mywhere) {
		String oldsql = sql;
		createquery(sql + " " + mywhere);
		sql = oldsql;
	}


	public void createquery(String mysql) {



		try {
			sql = mysql;
			rs = st.executeQuery(sql);
     		metaData = rs.getMetaData();

			columnName.clear();
        	columnTitle.clear();

			System.out.println(metaData.getColumnCount());

			for(int i=1;i<=metaData.getColumnCount();i++) {
				columnName.add(metaData.getColumnName(i));
				columnTitle.add(metaData.getColumnName(i));
				}
			rows.clear();
           	while (rs.next()) {
           		Vector newRow = new Vector();
               	for (int i = 1; i <= columnTitle.size(); i++) {
               		newRow.addElement(rs.getObject(i));
					}
			   	rows.addElement(newRow);
				}
			}
		catch(SQLException ex) {
		  JOptionPane.showMessageDialog(parentPanel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
       		 System.out.println("Grid Query Creation SQLException : " + ex.getMessage());
			}

		fireTableChanged(null); // Tell the listeners a new table has arrived.
	}

	public void refresh() { // Get all rows.
		if(security.isread) {

			//System.out.println("Grid Refresh SQL = " + sql);
			try {
				rs.close();
				rs = st.executeQuery(sql);

				keylist.clear();
				rowidlist.clear();
            	rows.clear();
            	while (rs.next()) {
            		Vector newRow = new Vector();
                	for (int i = 1; i <= columnTitle.size(); i++) {

						if(columnType.get(i-1)!=null) {
							if(columnType.get(i-1).equals("boolean")) newRow.addElement(rs.getBoolean(i));
						} else newRow.addElement(rs.getObject(i));
				    }
					keylist.add(rs.getString("keyfield"));
					rowidlist.add(rs.getString("rowid"));
                	rows.addElement(newRow);
	      		}
  		} catch(SQLException ex) {
			JOptionPane.showMessageDialog(parentPanel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
       		 	System.out.println("Grid Refresh SQLException : " + ex.getMessage());
     		}

        	fireTableChanged(null); // Tell the listeners a new table has arrived.
		}
	}

    //////////////////////////////////////////////////////////////////////////
    //
    //             Implementation of the TableModel Interface
    //
    //////////////////////////////////////////////////////////////////////////
    public String getColumnName(int column) {
        if (columnTitle.get(column) != null) {
            return columnTitle.get(column);
        } else {
            return "";
        }
    }

    public Class getColumnClass(int column) {
        int type;
        try {
            type = metaData.getColumnType(column+1);
	    }
        catch (SQLException e) {
            return super.getColumnClass(column);
	    }

	if(columnType.get(column)!=null) {
	    if(columnType.get(column).equals("boolean"))
		return Boolean.class;
	    if(columnType.get(column).equals("icon"))
		return Icon.class;
	    }

        switch(type) {
        case Types.CHAR:
        case Types.VARCHAR:
        case Types.LONGVARCHAR: return String.class;
        case Types.BIT: return Boolean.class;
	case Types.TINYINT:
        case Types.SMALLINT:
        case Types.INTEGER: return Integer.class;
        case Types.BIGINT: return Long.class;
        case Types.FLOAT:
	case Types.REAL:
        case Types.DOUBLE: return Double.class;
	case Types.TIME: return Time.class;
	case Types.TIMESTAMP: return Timestamp.class;
        case Types.DATE: return Date.class;
        default: return Object.class;
        }
    }

    public boolean isCellEditable(int row, int column) {
		return columnEdit.get(column);
    }

    public int getColumnCount() {
        return columnTitle.size();
    }

    public int getRowCount() {
        return rows.size();
    }

    public Object getValueAt(int aRow, int aColumn) {
        Vector row = (Vector)rows.elementAt(aRow);
        return row.elementAt(aColumn);
    }

    // To edit a value on a table
    public void setValueAt(Object value, int row, int column) {
		System.out.println("Row = " + row + " Col = " + column + " Key List = " + keylist.get(row) + " Value = " + value);
		String mystr = "";

		String myvalue = null;
		if(value != null)
			  myvalue = value.toString();

		try {
			if(getColumnClass(column)==String.class) {
				DGridCombo gc = combolist.get(column);
				if(gc == null) {

					if(myvalue == null)
					      mystr = "UPDATE " + updatetable + " SET " + columnName.get(column) + " = null";
					else {
					      myvalue = myvalue.replaceAll("'", "\\'");
					      mystr = "UPDATE " + updatetable + " SET " + columnName.get(column) + " = '" + myvalue + "'";
					      }
					}

				else {		//if grid combo
				      myvalue = gc.getKey(myvalue);
				      String updatefield = gc.getUpdateField();

				      //if xrosstab
 				      if (rowid != null){	//ie gridcombo in crosstab
					    System.out.println("GRID COMBO in a crosstab! myvalue = " + myvalue);

// 					    //check if entry for the intersection exists
 					    String colsearch = "SELECT " + xcol[0] + " FROM " + xcol[2] + " WHERE UPPER(" + xcol[1] + ") = UPPER('" + columnName.get(column) +"')";
 					    String rowsearch = "SELECT " + keyfield + ", " + rowid + "," + xcol[0]  + " FROM " + updatetable + " WHERE " + rowid + "= " + rowidlist.get(row) + " AND " + xcol[0] + " = (" + colsearch + ") LIMIT 1";
// 					    //" WHERE " + keyfield + " = '" + keylist.get(row) + "'"
//
 					    Statement xst = mydb.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
 					    ResultSet xrs = xst.executeQuery(rowsearch);

 					    String pk = "";
 					    String r = "";		//row id
 					    String c = "";		//col

 					    if(xrs.next()) {
 						pk = xrs.getString(1);
 						r = xrs.getString(2);
 						c = xrs.getString(3);
 						}

// 					    if(pk.equals("") && insertmissing.equals("true")){	//insert MISSING = TRUE
// 						//else INSERT
// 						mystr = "INSERT INTO " + updatetable + "(" + rowid + "," + xcol[0] + "," + updatefield + ") VALUES (" + rowidlist.get(row) + ",(" + colsearch + ")," + myvalue + ")";
// 						}
 					    if(!pk.equals("")){
// 						//if exists UPDATE
 						//mystr = "UPDATE " + updatetable + " SET " + updatefield + "=" + myvalue + " WHERE " + rowid + "=" + rowidlist.get(row) + " AND " + xcol[0] + "=" + c;
						mystr = "UPDATE " + updatetable + " SET " + updatefield + "=" + myvalue + " " + crosstab_where +" AND " + rowid + "=" + rowidlist.get(row) + " AND " + xcol[0] + "=" + c;

 						}

//
// 					    //
 					    xrs.close();
 					    xst.close();

 					    }


				      else{	//if not crosstab (ie gridcombo but not in crosstab)
					  //myvalue = gc.getKey(myvalue);	--moved up
					  //String updatefield = gc.getUpdateField();
					  if(updatefield == null)
					      updatefield = columnName.get(column);
					  if(myvalue == null)
						mystr = "UPDATE " + updatetable + " SET " + updatefield + " = null";
					  else {
						myvalue = myvalue.replaceAll("'", "\\'");
						mystr = "UPDATE " + updatetable + " SET " + updatefield + " = '" + myvalue + "'";
						}
					  }

				      }
				}
			else if(getColumnClass(column)==Date.class){
				System.out.println("DEBUG: found DATE but cant update for now");
				}
			else if(getColumnClass(column)== java.sql.Timestamp.class){
				System.out.println("DEBUG: found TIMESTAMP but cant update for now");
				}
			else {//if NOT STRING

				if(myvalue == null) {
				    mystr = "UPDATE " + updatetable + " SET " + columnName.get(column) + " = null";
				    myvalue = "";
				    }
				else{
				      //if xrosstab
				      if (rowid != null){
					    //check if entry for the intersection exists
					    String colsearch = "SELECT " + xcol[0] + " FROM " + xcol[2] + " WHERE UPPER(" + xcol[1] + ") = UPPER('" + columnName.get(column) +"')";
					    String rowsearch = "SELECT " + keyfield + ", " + rowid + "," + xcol[0]  + " FROM " + updatetable + " WHERE " + rowid + "= " + rowidlist.get(row) + " AND " + xcol[0] + " = (" + colsearch + ") LIMIT 1";
					    //" WHERE " + keyfield + " = '" + keylist.get(row) + "'"
					    //System.out.println("YROW SEARCH QUERY = " + rowsearch);

					    Statement xst = mydb.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
					    ResultSet xrs = xst.executeQuery(rowsearch);

					    String pk = "";
					    String r = "";		//row id
					    String c = "";		//col

					    if(xrs.next()) {
						pk = xrs.getString(1);
						r = xrs.getString(2);
						c = xrs.getString(3);
						}


					    if(pk.equals("") && insertmissing.equals("true")){	//insert MISSING = TRUE
						//else INSERT
						mystr = "INSERT INTO " + updatetable + "(" + rowid + "," + xcol[0] + "," + updatefield + ") VALUES (" + rowidlist.get(row) + ",(" + colsearch + ")," + myvalue + ")";
						}
					    else if(!pk.equals("")){
						//if exists UPDATE
						mystr = "UPDATE " + updatetable + " SET " + updatefield + "=" + myvalue + " " + crosstab_where.replace("'","") + " AND " + rowid + "=" + rowidlist.get(row) + " AND " + xcol[0] + "=" + c;
						}
					    else{
						//mystr = "IGNORE CROSSTAB COLUMN";
						}

					    //
					    xrs.close();
					    xst.close();
					    }
				      else{
					    mystr = "UPDATE " + updatetable + " SET " + columnName.get(column) + " = " + myvalue;
					    }
				      }

				if(myvalue.equals("true")) {
					DElement el = children.get(column);
					if(el.getAttribute("email") != null) {
						String email = el.getAttribute("email");
						String subject = el.getAttribute("subject");
						String mailbody = el.getAttribute("mailbody");
						String mstr = "SELECT " + email + ", " + subject + ", " + mailbody;
						mstr += " FROM " + el.getAttribute("table");
						mstr += " WHERE " + keyfield + " = '" + keylist.get(row) + "'";

						Statement mst = mydb.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
						ResultSet mrs = mst.executeQuery(mstr);
						if(mrs.next()) {
						      DMail mail = new DMail(el);
						      mail.sendmail(mrs.getString(email), mrs.getString(subject), mrs.getString(mailbody));
						      System.out.println("Sending Mail : " + mrs.getString(email));
						      }
						}
					}
			      }

			  if (rowid == null){ //if not crosstab
			      mystr += " WHERE " + keyfield + " = '" + keylist.get(row) + "'";
			      }

			System.out.println("UPDATE mystr = " + mystr);
			recAudit("Grid Edit", keylist.get(row),  myvalue);

		    if(insertmissing.equals("false"))
			upst.executeUpdate(mystr);

		} catch (SQLException ex) {
			//JOptionPane.showMessageDialog(, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
			//System.out.println("PARENT = " + this);
			JOptionPane.showMessageDialog(parentPanel, ex.getMessage(), "Update Error", JOptionPane.ERROR_MESSAGE);
			System.out.println("GRID SQL Exeption : " + ex);
			}

		Vector dataRow = (Vector)rows.elementAt(row);
		dataRow.setElementAt(value, column);
	}

	public void insertrows(Vector vect) {
		//System.out.println("DEBUG: at DTableDef:insertrows("+vect+")");
		int i, j;

    	try {
			for(i=0; i<vect.size();i++) {		//include header row
			//for(i=1; i<vect.size();i++) {		//exclude header row
				Vector row = (Vector)vect.elementAt(i);
				//System.out.println("BEFORE moveToInsertRow()");
				rs.moveToInsertRow();

				//DLogin.databasetype.compareToIgnoreCase("oracle")			//we will need this to take care of the autofield/keyfield mayhem
				String autosql = "";

				if(!autofield.equals("")) {
					autosql = "select nextval('" + tablename + "_" + autofield + "_seq')";
					System.out.println("AUTOSQL = " + autosql);
                	ResultSet autors = autost.executeQuery(autosql);
					autors.next();
					int autoid = autors.getInt(1);
					autors.close();
					rs.updateInt(autofield, autoid);
					}
				if(!autonumber.equals("")) {
					//String autosql = "select nextval('" + tablename + "_" + autonumber + "_seq')";
					autosql = "SELECT max(" + autonumber + ") + 1 FROM " + tablename;
					System.out.println("AUTOSQL = " + autosql);
                	ResultSet autors = autost.executeQuery(autosql);
					autors.next();
					int autoid = autors.getInt(1);
					autors.close();
					rs.updateInt(autonumber, autoid);
					}



				for(j=0;j<row.size();j++) {
					updateRow((j+1), (String)row.elementAt(j));
					}
				rs.insertRow();

				//recAudit("Insert", rs.getString(primarykey), "");
			}
 		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(parentPanel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
			System.out.println("Import Error : " + ex);
			//JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
        }

		// Show the new table data
		refresh();
	}

	public void recAudit(String changetype, String recordid, String narrative) {
    	try {
			String inssql = "INSERT INTO audit_trail (user_name, tablename, recordid, changetype, narrative) ";
			//inssql += " VALUES(current_user, '" + updatetable + "', '" + recordid  + "', '" + changetype + "', '" + narrative + "')";
			inssql += " VALUES(" + DLogin.getLoggedInUser() + ", '" + tablename + "', '" + recordid  + "', '" + changetype + "')";
			//System.out.println(inssql);
			Statement stUP = mydb.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stUP.executeUpdate(inssql);
			stUP.close();
 		} catch (SQLException ex) {
         	System.out.println("Audit record error : " + ex);
			//JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void updateRow(int columnindex, String fvalue) {
		int type;
		columnindex--;
		String fname =  columnName.get(columnindex);

        try {
			columnindex = rs.findColumn(fname);
		    if(fvalue.length()<1) {
				rs.updateNull(fname);
				}
			else {
				type = metaData.getColumnType(columnindex);

				/*Debug: System.out.println(fname + " = " + fvalue + " type = " + type); */
				switch(type) {
        			case Types.CHAR:
        			case Types.VARCHAR:
        			case Types.LONGVARCHAR:
            			rs.updateString(fname, fvalue);
						break;
       				case Types.BIT:
						if(fvalue.equals("true")) rs.updateBoolean(fname, true);
						else rs.updateBoolean(fname, false);
						break;
        			case Types.TINYINT:
        			case Types.SMALLINT:
        			case Types.INTEGER:
						int ivalue = Integer.valueOf(fvalue).intValue();
						rs.updateInt(fname, ivalue);
						break;
		        	case Types.BIGINT:
						long lvalue = Long.valueOf(fvalue).longValue();
						rs.updateLong(fname, lvalue);
						break;
		        	case Types.FLOAT:
		        	case Types.DOUBLE:
					case Types.REAL:
						double dvalue = Double.valueOf(fvalue).doubleValue();
						rs.updateDouble(fname, dvalue);
						break;
		        	case Types.DATE:
						java.sql.Date dtvalue = Date.valueOf(fvalue);
						rs.updateDate(fname, dtvalue);
						break;
		        	case Types.TIME:
						java.sql.Time tvalue = Time.valueOf(fvalue);
						rs.updateTime(fname, tvalue);
						break;
					}
				}
			}
		catch (SQLException ex) {
        	System.out.println("The SQL Exeption on " + fname + " : " + ex);
			}
		}

	public void savecvs(String filename) {
		int i, j;
		String mystr;

    	try {
			FileWriter output = new FileWriter(filename);

			for(i=0; i<getRowCount();i++) {
				mystr = "";
				int colcount = getColumnCount()-1;
				for(j=0;j<=colcount;j++) {
					if(j==colcount) mystr += getcvsValueAt(i, j) + "\r\n";
					else mystr += getcvsValueAt(i, j) + ",";
				}

				output.write(mystr);
			}

			if (output != null) output.close();
 		} catch (IOException ex) {
         	System.out.println("Export error : " + ex);
			//JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
        }
	}

    public String getcvsValueAt(int aRow, int aColumn) {
        Vector row = (Vector)rows.elementAt(aRow);
        Object myobj = row.elementAt(aColumn);
		Class myclass = getColumnClass(aColumn);

		String mystr = "";
		if(myobj!=null) {
			if(myclass==String.class) {
				if(myobj.toString().startsWith("0")) mystr = "\"'" + myobj.toString() + "\"";
				else mystr = "\"" + myobj.toString() + "\"";
			}
			else mystr = myobj.toString();
		}

		//System.out.println(mystr);

		return mystr;
    }

//    public String setParentPanel(JPanel p){
// 	this.parentPanel = p;
// 	}
}
