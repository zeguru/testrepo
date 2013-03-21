package org.elegance;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DAction implements ActionListener {

	private Connection conn;
	private String statement;

	public JPanel panel;
	public List<JButton> buttons;
	public List<JLabel> labels;

	public int linkkey;
	public boolean ischild;
	private String linkfield, linkdata;
	private Map<String, String> statements;
	private boolean isString;

	public DAction(DElement fielddef, Connection db) {

		conn = db;
		statement = fielddef.getAttribute("statement");
		statements = new HashMap<String, String>();
		
		isString = false;
		if(fielddef.getAttribute("isString", "").equals("true"))
			isString = true;

		linkkey = 0;
		ischild = false;
		if(!fielddef.getAttribute("linkkey", "").equals("")) {
			ischild = true;
			linkfield = fielddef.getAttribute("linkfield");
            linkkey = Integer.valueOf(fielddef.getAttribute("linkkey")).intValue();
   		}
		
		panel = new JPanel(new GridLayout(4, 4));
		buttons =  new ArrayList<JButton>();
		labels =  new ArrayList<JLabel>();
		List<DElement> childen = fielddef.getElements();
		
		for(DElement el : childen) {
			String cmdtitle = el.getAttribute("title", "");
			String stm = el.getAttribute("statement");
			statements.put(cmdtitle, stm);
			
			buttons.add(new JButton(cmdtitle));
			labels.add(new JLabel(""));
			panel.add(buttons.get(buttons.size()-1));
			panel.add(labels.get(labels.size()-1));
			buttons.get(buttons.size()-1).addActionListener(this);
		}
	}

    public void linkinput(String data) {
     	linkdata = data;
    }

	public void execproc() {
		String mystr = "SELECT " + statement;
		if(isString) mystr += "('" + linkdata + "');";
		else mystr += "(" + linkdata + ");";

		System.out.println(mystr);

		// Execute the procedure
     	try {			
			Statement cs = conn.createStatement();
			ResultSet rs = cs.executeQuery(mystr);

			rs.close();
			cs.close();			
		} catch(SQLException ex) {
        	System.out.println("SQLException: " + ex.getMessage());
        }
	}

	public void actionPerformed(ActionEvent e) {
		statement = statements.get(e.getActionCommand());
		execproc();
	}
}

