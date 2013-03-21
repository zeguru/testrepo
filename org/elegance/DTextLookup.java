package org.elegance;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

public class DTextLookup extends DField implements MouseListener {

	public JTextField datafield;
	public int cmbkey=0;
	public boolean enabled=true;

	public boolean showgrid = false;

	private Connection mydb;
	private Statement st;
	private String sql;
	private String lptable;
	private String lpfield;
	private String lookupdata;
	private String lookupfield;
	private String lookupkey;

	private String datakey;

	private DGrid grid;

    public DTextLookup(DElement el, JPanel lpanel, Connection db) {
		super(el);
		datafield = new JTextField();

		mydb = db;

		datafield.setHorizontalAlignment(JTextField.LEADING);
		datafield.setCaretPosition(0);

		enabled = false;
		datafield.setEnabled(false);

		lookupfield = el.getAttribute("lookupfield", "");
		lptable = el.getAttribute("lptable", "");
		lpfield = el.getAttribute("lpfield", "");

		if(el.getAttribute("lpkey") == null) lookupkey = name;
		else lookupkey = el.getAttribute("lpkey");

		if(!el.getAttribute("cmbkey", "").equals("")) {
			cmbkey = Integer.valueOf(el.getAttribute("cmbkey")).intValue();
		}
		datafield.setActionCommand(Integer.toString(cmbkey));

		if(title.length()>0) lpanel.add(label);
		lpanel.add(datafield);

		// Make the lookup grid
		grid = new DGrid(el.getFirst(), db);
		lpanel.add(grid.panel);
		grid.panel.setVisible(false);

		datafield.addMouseListener(this);
		grid.table.addMouseListener(this);

		try {
			st = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		} catch(SQLException ex) {
			System.out.println("SQLException : " + ex.getMessage());
			JOptionPane.showMessageDialog(datafield, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
      	}

		setPos();
	}

    public void setPos() {
    	super.setPos();
        datafield.setLocation(x+lw, y);
    	datafield.setSize(w, h);

		grid.panel.setLocation(x, y+h);
		grid.panel.setSize(lw+w, 120);
    }

	public void setlookup(String lpdata) {
		lookupdata = lpdata;
		System.out.println(lpdata);
	}

	public void setText(String ldata) {
		try {
			sql = "SELECT (" + lpfield + ") as lpfield  FROM " + lptable + " WHERE " + lookupkey + " = ";
			if(ldata == null)
				sql += ldata + ";";
 			else
				sql += " '" + ldata + "';";

			System.out.println("Text Lookup sql = " + sql);

			ResultSet rs = st.executeQuery(sql);
			if(rs.next())
				datafield.setText(rs.getString(1));
			else
				datafield.setText("");
			rs.close();
			datakey = ldata;
        } catch(SQLException ex) {
        	System.out.println("Text Lookup SQL Error : " + ex.getMessage());
			//JOptionPane.showMessageDialog(datafield, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
        }
		datafield.moveCaretPosition(0);
	}

	public String getText() {
	      return datakey;
	      }

	public String getDataField(){
	      return datafield.getText();
	      }

	// Get the grid listening mode
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {
		if(!showgrid) {
			grid.panel.setVisible(true);
			if(lookupfield.equals("")) grid.refresh();
			else grid.makechild(lookupdata);

			showgrid = true;
			System.out.println(lookupdata);
		} else {
			grid.panel.setVisible(false);
			datakey = grid.getKey();
			setText(datakey);

			showgrid = false;
		}
	}
}
