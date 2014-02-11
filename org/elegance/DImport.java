package org.elegance;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import ui.jtunestable.JTunesTable;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.JButton;

import javax.swing.JTextField;
import javax.swing.JComboBox;

import java.sql.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Component;
import java.awt.Color;


public class DImport implements ActionListener {

	public JPanel toppanel, filterpanel;
	public JSplitPane panel, mainpanel;
	public JTable importtable, datatable;
	public JScrollPane importpane, datapane;

	JTextField filterdata;
	JComboBox fieldlist, filterlist;
	List<String> lplist;

	JButton openfile, upload, process, clear;

	DImportModel importmodel;
	DTableDef tabledef;
	Map<Integer, DGridCombo> combolist;

	private Connection conn;
	private String procedure, deletesql, filetype, worksheet, delimiter;
	private String webserviceserver;
	private String gridfilter;
	private String gridlines;	//horizontal,vertical,both

	public DImport(DElement fielddef, Connection db) {
		conn = db;
		procedure = fielddef.getAttribute("procedure", "");
		filetype = fielddef.getAttribute("filetype", "");
		worksheet = fielddef.getAttribute("worksheet", "0");
		delimiter = fielddef.getAttribute("delimiter");
		deletesql = fielddef.getAttribute("deletesql");
		webserviceserver = fielddef.getAttribute("webserviceserver");

		gridlines = fielddef.getAttribute("gridlines","no");	//horizontal, vertical, both, no


		combolist =  new HashMap<Integer, DGridCombo>();

		importmodel = new DImportModel(fielddef);
		tabledef = new DTableDef(fielddef, db, combolist, toppanel);
		//tabledef = new DTableDef(fielddef, db, combolist);

		datatable = new JTable(tabledef);	//db
		importtable = new JTable(importmodel);	//xl

		//import from file
		importtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //AUTO_RESIZE_ALL_COLUMNS, AUTO_RESIZE_OFF
		importtable.setFillsViewportHeight(true);
		if(gridlines.equals("horizontal")){
		    importtable.setShowHorizontalLines(true);
		    }
		else if(gridlines.equals("vertical")){
		    importtable.setShowVerticalLines(true);
		    }
		else if(gridlines.equals("both")){
		    importtable.setShowHorizontalLines(true);
		    importtable.setShowVerticalLines(true);
		    }

		//importtable.setShowGrid(true);
		importpane = new JScrollPane(importtable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		//data from db
		datatable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 		//just in case there are many columns
		datapane = new JScrollPane(datatable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		toppanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		openfile = new JButton("Open File");
		upload = new JButton("Upload");
		process = new JButton("Process");
		clear = new JButton("Clear");

		toppanel.add(openfile);
		toppanel.add(upload);
		toppanel.add(process);
		toppanel.add(clear);

		openfile.addActionListener(this);
		upload.addActionListener(this);
		process.addActionListener(this);
		clear.addActionListener(this);

		mainpanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, importpane, datapane);
		mainpanel.setOneTouchExpandable(true);
		mainpanel.setDividerLocation(200);
		panel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, toppanel, mainpanel);



		// create filter panel on grid
		filterdata = new JTextField();
		filterdata.addActionListener(this);
		String[] filterstr = {"LIKE", "ILIKE", "BETWEEN", "=", ">", "<", "<=", ">="};
		fieldlist = new JComboBox();
		filterlist = new JComboBox(filterstr);
		filterlist.setSelectedIndex(0);

		String gridfilter = fielddef.getAttribute("gridfilter", "false");
		lplist = new ArrayList<String>();
		List<DElement> children = new ArrayList<DElement>(fielddef.getElements());
		for(DElement el : children) {
			fieldlist.addItem(el.getAttribute("title"));
			lplist.add(el.getValue());
			}

		filterpanel = new JPanel(new GridLayout(1, 0));
		filterpanel.add(fieldlist);
		filterpanel.add(filterlist);
		filterpanel.add(filterdata);

		//if(gridfilter.equals("true"))
		//	datapane.add(filterpanel, BorderLayout.PAGE_END);

		//end filter






		upload.setEnabled(false);		//just ux stuff
		tabledef.refresh();
	}

    public void actionPerformed(ActionEvent e) {
		try{
			if(e.getActionCommand().equals("Open File")) {
				if(filetype.equals("text")) importmodel.gettextdata(panel, delimiter);
				if(filetype.equals("record")) importmodel.getrecorddata(panel);
				if(filetype.equals("excel")){
				    if(worksheet.compareTo("ask")==0){
					    worksheet = JOptionPane.showInputDialog (toppanel, "Enter Worksheet Number" );

					    if (worksheet != null){
						int option = JOptionPane.showConfirmDialog(toppanel, "Import worksheet number " + worksheet + "?","Confirmation", JOptionPane.YES_NO_OPTION);

						if (option == JOptionPane.YES_OPTION ){ 		//     YES_OPTION,  NO_OPTION, CANCEL_OPTION, OK_OPTION, CLOSED_OPTION
						    importmodel.getexceldata(panel, worksheet);
						    upload.setEnabled(true);
						    }
						else{
						    System.out.println("OPTION  = " + option);
						    upload.setEnabled(false);
						    }
						}
					    else{
						upload.setEnabled(false);
						}
					    worksheet = "ask";		//allow recovery
					}
				    else{	//just import
					importmodel.getexceldata(panel, worksheet);
					upload.setEnabled(true);
					}


				}
			    }
			if(e.getActionCommand().equals("Upload")) {
				tabledef.insertrows(importmodel.rows);
				importmodel.clearupload();
				upload.setEnabled(false);
				}
			if(e.getActionCommand().equals("Process")) {
				processdata(false);		//this is not a delete operation....
				if(webserviceserver != null) {
					DServerClient sc = new DServerClient(webserviceserver, "webservice");
					}
				//upload.setEnabled(true);
				}
			if(e.getActionCommand().equals("Clear")) {
				processdata(true);	//this is a delete operation
				importmodel.clearupload();
				}
			}
		catch(Exception ex){
			JOptionPane.showMessageDialog(toppanel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
			System.out.println("SQLException: " + ex.getMessage());
			}
    }

	//wether or not we should delete
	public void processdata(boolean del) {

		String mystr = "";

		if(del==true && deletesql != null){
			//if(){
				if(DLogin.databasetype.compareToIgnoreCase("oracle")==0)
					mystr = "SELECT " + deletesql + " from dual";
				else
					mystr = "SELECT " + deletesql + "()";
			//	}
			}
		else{
			if(DLogin.databasetype.compareToIgnoreCase("oracle")==0)
				mystr = "SELECT " + procedure + " from dual";
			else
				mystr = "SELECT " + procedure + "()";
			}

		System.out.println(mystr);

		// Execute the procedure
     	try {
			Statement cs = conn.createStatement();
			ResultSet rs = cs.executeQuery(mystr);
			rs.close();
			cs.close();
			}
		catch(SQLException ex) {
			JOptionPane.showMessageDialog(toppanel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
        	System.out.println("SQLException: " + ex.getMessage());
			}
		// refresh Data
		tabledef.refresh();
		}


}

