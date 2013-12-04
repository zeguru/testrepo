package org.elegance;

import java.sql.*;
import javax.swing.*;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import java.awt.GridLayout;

import javax.swing.Box;
import java.awt.BorderLayout;
import java.awt.Container;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;

//import java.awt.Container;


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

import javax.swing.JButton;
import javax.swing.JFrame;

import javax.swing.border.*;


public class DForm extends JPanel implements ActionListener {

//public class DForm implements ActionListener {

	public JPanel panel;
	JTabbedPane tabs;

	public Map<String, String> defaultfield;
	public List<DTextField> textfield;
	public List<DTextArea> textarea;
	public List<DCheckBox> checkbox;
	public List<DComboBox> combobox;
	public List<DComboList> combolist;
	public List<DTextLookup> textlookup;
	public List<DTextDate> textdate;
	public List<DTextTime> texttime;
	public List<DDateSpin> datespin;
	public List<DTimeSpin> timespin;
	public List<DDateTimeSpin> datetimespin;
	public List<DTextDecimal> textdecimal;
	public List<DEditor> editor;
	public List<DImage> image;
	public List<DFile> file;		//file/document upload tool
	//public List<DBlob> blob;
	public List<DButton> button;
	public List<DualList> dual;
	public List<JPanel> panellist;
	public DSecurity security;
	public Map<String, JPanel> tabList;

	DAttributeTable	attributetable;				//used by hstore column to store arbitrary key=>value pairs

	private javax.swing.JButton btnNew;
	private javax.swing.JButton btnSave;
	private javax.swing.JButton btnCancel;
	private javax.swing.JButton btnModify;
	private javax.swing.JButton btnRefresh;
	private javax.swing.JButton btnDelete;

	private javax.swing.JButton btnFirst;
	private javax.swing.JButton btnLast;
	private javax.swing.JButton btnNext;
	private javax.swing.JButton btnPrevious;
	private javax.swing.JLabel formlabel;

	public String sql, tablename;
	public boolean ischild, isinputfield, islookup;
	public int inputkey, linkkey, lookupkey;

	private String linkfield, inputfield, lookupfield;
	private String linkdata, lookupdata;

	private String primarykey;
	private boolean iscmb = false;

	public Connection mydb;
	Statement st, autost;
	ResultSet rs;
	ResultSetMetaData metaData;

	boolean isAddNew, isEdit, autoincr;
	String autofield;				//used by postgres
	String primaryfield;			//used by oracle

	String autonumber;				//used by mysql

	Box bh;		//horizontal box
	Box bv;		//vertical box
	Box bl;

	DContainer parent;		//access to the parent is required for refresh() and JOptionPane()
	String link;

	//String  hidebuttons;		//form buttons to hide
	boolean label, isduallist;	//should we display label? does the form have a dual list ?
	String labelfield, labeltable, labeltext, labeldescr;
	String disabledbuttons = "";

	static String linktablename;
	static String linktablekey;
	String labeldata;

	public boolean ispopup;

	int tw=350, th=50, lh=10;

	public DForm(DContainer p, DElement fielddef, Connection db){

		parent = p;		//this is the parent container

// 		Border blackline, raisedetched, loweredetched,raisedbevel, loweredbevel, empty;
// 		TitledBorder titled;

		//get the layout
		if(fielddef.getAttribute("layout") != null) {
		    panel = new JPanel(new GridLayout(1, 1));
		    }
		else {
		    panel = new JPanel(new BorderLayout());
			//panel = new JPanel(new GridLayout(2, 2));
		    }

		ispopup = false;
		/*

		blackline = BorderFactory.createLineBorder(Color.black);
		raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		raisedbevel = BorderFactory.createRaisedBevelBorder();
		loweredbevel = BorderFactory.createLoweredBevelBorder();
		empty = BorderFactory.createEmptyBorder();

		TitledBorder titled;
		titled = BorderFactory.createTitledBorder(raisedbevel, fielddef.getAttribute("name","Title"));
		titled.setTitleJustification(TitledBorder.LEFT);
		panel.setBorder(titled);
		panel.setOpaque(false);

		//Matte borders
		ImageIcon icon = createImageIcon("images/wavy.gif",
										"wavy-line border icon"); //20x22

		jComp5.setBorder(BorderFactory.createMatteBorder(
										-1, -1, -1, -1, icon));
		jComp6.setBorder(BorderFactory.createMatteBorder(
											1, 5, 1, 1, Color.red));
		jComp7.setBorder(BorderFactory.createMatteBorder(
											0, 20, 0, 0, icon));
		//Compound borders
		Border compound;
		Border redline = BorderFactory.createLineBorder(Color.red);

		//This creates a nice frame.
		compound = BorderFactory.createCompoundBorder(
					raisedbevel, loweredbevel);
		jComp13.setBorder(compound);

		//Add a red outline to the frame.
		compound = BorderFactory.createCompoundBorder(
					redline, compound);
		jComp14.setBorder(compound);

		//Add a title to the red-outlined frame.
		compound = BorderFactory.createTitledBorder(
					compound, "title",
					TitledBorder.CENTER,
					TitledBorder.BELOW_BOTTOM);
		jComp15.setBorder(compound);

		*/

		List<DElement> children = new ArrayList<DElement>(fielddef.getElements());

		defaultfield = new HashMap<String, String>();
		textfield = new ArrayList<DTextField>();
		textarea = new ArrayList<DTextArea>();
		checkbox = new ArrayList<DCheckBox>();
		combobox = new ArrayList<DComboBox>();
		combolist = new ArrayList<DComboList>();
		textlookup = new ArrayList<DTextLookup>();
		textdate = new ArrayList<DTextDate>();
		texttime = new ArrayList<DTextTime>();
		datespin = new ArrayList<DDateSpin>();
		timespin = new ArrayList<DTimeSpin>();
		datetimespin =  new ArrayList<DDateTimeSpin>();
		textdecimal = new ArrayList<DTextDecimal>();
		editor = new ArrayList<DEditor>();
		image = new ArrayList<DImage>();
		file = new ArrayList<DFile>();
		//blob = new ArrayList<DBlob>();
		button = new ArrayList<DButton>();
		dual = new ArrayList<DualList>();
		panellist = new ArrayList<JPanel>() ;
		security = new DSecurity();
		tabList = new HashMap<String, JPanel>();
		tabs = new JTabbedPane();

		attributetable = new DAttributeTable();

		label = false;
		isduallist = false;
		labelfield = "";	//field to display on lable
		labeltable = "";	//source table/view
		labeltext = "";		//static text: has higher precedence
		labeldescr = "";	//if present: it gives a description of the info contained in the label eg Client: Mabura Ze Guru
		labeldata = "";		//a result/combination of the above variables. this is the actual data displayed

		formlabel = new javax.swing.JLabel("",JLabel.LEFT);
		formlabel.setMaximumSize(new Dimension(600,20));
		formlabel.setForeground(Color.blue);

		bl = Box.createHorizontalBox();		//box for form label (input label)
		bl.add(formlabel);

		if(fielddef.getAttribute("hideallbuttons") == null){

			btnNew = new javax.swing.JButton("New");
			btnSave = new javax.swing.JButton("Save");
			btnCancel = new javax.swing.JButton("Cancel");
			btnModify = new javax.swing.JButton("Edit");
			btnRefresh = new javax.swing.JButton("Refresh");
			btnDelete = new javax.swing.JButton("Delete");

			btnFirst = new javax.swing.JButton("|<");
			btnLast = new javax.swing.JButton(">|");
			btnNext = new javax.swing.JButton(">>");
			btnPrevious = new javax.swing.JButton("<<");


			btnNew.setMaximumSize(new Dimension(100,20));
			btnSave.setMaximumSize(new Dimension(100,20));
			btnCancel.setMaximumSize(new Dimension(100,20));
			btnModify.setMaximumSize(new Dimension(100,20));
			btnRefresh.setMaximumSize(new Dimension(100,20));
			btnDelete.setMaximumSize(new Dimension(100,20));


			btnNew.setToolTipText("Add a new record");
			btnSave.setToolTipText("Save new or modified record");
			btnCancel.setToolTipText("Cancel this operation");
			btnModify.setToolTipText("Edit this record");
			btnRefresh.setToolTipText("Refresh display");
			btnDelete.setToolTipText("Delete this record");

			btnFirst.setToolTipText("View first record");
			btnPrevious.setToolTipText("View previous record");
			btnNext.setToolTipText("View next record");
			btnLast.setToolTipText("View last record");


			if(fielddef.getAttribute("disabledbuttons")!=null){

				disabledbuttons = fielddef.getAttribute("disabledbuttons");

				if(disabledbuttons.contains("new"))
					btnNew.setEnabled(false);
				if(disabledbuttons.contains("save"))
					btnSave.setEnabled(false);
				if(disabledbuttons.contains("cancel"))
					btnCancel.setEnabled(false);
				if(disabledbuttons.contains("edit"))
					btnModify.setEnabled(false);
				if(disabledbuttons.contains("delete"))
					btnDelete.setEnabled(false);
				if(disabledbuttons.contains("refresh"))
					btnRefresh.setEnabled(false);
				if(disabledbuttons.contains("rsadvance")){		//rsadvance represents resultset advance facility
					btnFirst.setEnabled(false);
					btnPrevious.setEnabled(false);
					btnNext.setEnabled(false);
					btnLast.setEnabled(false);
					}
				}

			bv = Box.createVerticalBox();
			bh = Box.createHorizontalBox();
			//bl = Box.createHorizontalBox();		//box for form label (input label)

			//listeners
			btnNew.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnNewActionPerformed(evt);
				}
			});

			btnCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnCancelActionPerformed(evt);
				}
			});

			btnModify.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnModifyActionPerformed(evt);
				}
			});

			btnSave.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnSaveActionPerformed(evt);
				}
			});

			btnRefresh.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnRefreshActionPerformed(evt);
				}
			});

			btnDelete.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnDeleteActionPerformed(evt);
				}
			});

			btnFirst.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnFirstActionPerformed(evt);
				}
			});

			btnPrevious.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent evt) {
							btnPreviousActionPerformed(evt);
						}
					});
			btnNext.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent evt) {
							btnNextActionPerformed(evt);
						}
					});
			btnLast.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent evt) {
							btnLastActionPerformed(evt);
						}
					});

			initButtons();
			}	//end if hideallbuttons == null

		isAddNew = false;
		isEdit = false;

		tablename = fielddef.getAttribute("table");

		//hstore table
		//table = new JTable(attributetable);
		//table.setPreferredScrollableViewportSize(new Dimension(500, 120));
		//scrollPane = new JScrollPane(table);


		// get the auto increment key
		autoincr = false;
		autofield = null;
		if(!fielddef.getAttribute("autofield", "").equals("")) {
			autoincr = true;			//postgres sequence implied
			autofield = fielddef.getAttribute("autofield");
			}

		primaryfield = null;
		if(fielddef.getAttribute("primaryfield") != null) {
			autoincr = false;
			primaryfield = fielddef.getAttribute("primaryfield");
			//linktablekey = autofield;
			}

		autonumber = null;
		if(fielddef.getAttribute("autonumber") != null) {
			autoincr = false;
			autonumber = fielddef.getAttribute("autonumber");

			}



		// Get if the form is a child
		ischild = false;
		linkkey = 0;
		if(!fielddef.getAttribute("linkkey", "").equals("")) {
			ischild = true;
			linkfield = fielddef.getAttribute("linkfield");
			linkkey = Integer.valueOf(fielddef.getAttribute("linkkey")).intValue();

			//form label test
			//labelfield = fielddef.getAttribute("labelfield");
			//labeltable = fielddef.getAttribute("labeltable");
		    //System.out.println("DEBUG: linktablename = " + linktablename + ", linktablekey = " + linktablekey);
			}
		else if(fielddef.getAttribute("popup","false").equals("true")){
			System.out.println("DEBUG: found a popup FORM !!");
			ispopup = true;
			linkfield = fielddef.getAttribute("linkfield");
			}
		else{
			//linktablename = fielddef.getAttribute("labeltable");
		    //linktablekey = primaryfield;
		    System.out.println("DEBUG: orphaned");
		    }


		// Get if the form has a link data field
		isinputfield = false;
		inputkey = 0;

        if(!fielddef.getAttribute("inputkey", "").equals("")) {
        	isinputfield = true;
			inputfield = fielddef.getAttribute("inputfield");
			inputkey = Integer.valueOf(fielddef.getAttribute("inputkey")).intValue();

			//input label then labelfield (also labeltext)

		//FORM LABEL IS ONLY USEFUL WHERE the grid table is different/disjoint from the form table. ie when its hard to connect the two.
		 if(fielddef.getAttribute("formlabel", "").equals("true")) {
		    label = true;

		    labeldescr = fielddef.getAttribute("labeldescr");
		    labeltext = fielddef.getAttribute("labeltext");		//literal/static text

		    if(labeltext != null){	//if there is labeltext
				//formlabel.setText(labeldescr +": "+ labeltext);
				labeldata = labeldescr +": "+ labeltext;
				}
		    else{
				labelfield = fielddef.getAttribute("labelfield");
				labeltable = fielddef.getAttribute("labeltable");
				//String query = "select " + inputfield + "," + labelfield + " from " + labeltable + " where " + inputfield + " = " + linkdata;
				//System.out.println("\nDEBUG: Form Label Query : " + query);

				//create a record set
				//try {
					//Statement s = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
					//ResultSet r = s.executeQuery(query);

					//r.first();

					//while(r.next()){
						//linkdata = r.getString(1);
						//formlabel.setText(labelfield.toUpperCase() + " : " + (String)r.getObject(2));			//update the linkvalue
						//labeldata = "No Data Yet";			//update the linkvalue
						//System.out.println("DEBUG: linkdata = " + linkdata);
					//	}
					//r.close();
				//	}

				//catch(SQLException ex) {
				//	System.out.println("SQException at Form Label initialization : " + ex.getMessage());
				//	//JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
				//	}

				}
		    }

		}

		// Get a lookup field
		islookup = false;
		lookupkey = 0;
		if(!fielddef.getAttribute("lookupkey", "").equals("")) {
			islookup = true;
			lookupfield = fielddef.getAttribute("lookupfield");
			lookupkey = Integer.valueOf(fielddef.getAttribute("lookupkey")).intValue();
			}

		sql = "SELECT ";
		boolean isfirst = true;


		//get the form elements
	    for(DElement el : children) {

			String tabName = el.getAttribute("tab");

			//if(!el.getName().equals("PANEL")) {			    //if not a panel.. continue kama kawa
			if (tabName == null) {
				lh = Integer.parseInt(el.getAttribute("y")) + Integer.parseInt(el.getAttribute("h"));
				String cmbkey = el.getAttribute("cmbkey", "");
				if((cmbkey.equals("")) && (!el.getName().equals("SECURITY"))) {
					if(!isfirst) sql += ", ";
					else isfirst = false;
					sql += el.getValue().trim();
					}



				//fields.add(new BField(logHandle, db, el));
 				//if(tabName == null) {
 				//	lh = fields.get(i).getY() + fields.get(i).getH();
 				//	fields.get(i++).addToPanel(this);
 				//	}
				//else{


				if(el.getName().equals("DEFAULTFIELD")) {
					defaultfield.put(el.getValue().trim(), el.getAttribute("defaultvalue", ""));
					}
				else if(el.getName().equals("TEXTFIELD")) {
					textfield.add(new DTextField(el, panel));
					}
				else if (el.getName().equals("TEXTAREA")) {
					textarea.add(new DTextArea(el, panel));
					}
				else if (el.getName().equals("CHECKBOX")) {
					checkbox.add(new DCheckBox(el, panel));
					}
				else if (el.getName().equals("COMBOBOX")) {
					combobox.add(new DComboBox(el, panel, db));
					}
				else if(el.getName().equals("COMBOLIST")) {
					combolist.add(new DComboList(el, panel));
					}
				else if(el.getName().equals("TEXTLOOKUP")) {
					textlookup.add(new DTextLookup(el, panel, db));
					}
				else if(el.getName().equals("TEXTDATE")) {
					textdate.add(new DTextDate(el, panel));
					}
				else if(el.getName().equals("TEXTTIME")) {
					texttime.add(new DTextTime(el, panel));
					}
				else if(el.getName().equals("DATESPIN")) {
					datespin.add(new DDateSpin(el, panel));
					}
				else if(el.getName().equals("TIMESPIN")) {
					timespin.add(new DTimeSpin(el, panel));
					}
				else if(el.getName().equals("DATETIMESPIN")) {
					datetimespin.add(new DDateTimeSpin(el, panel));
					}
				else if(el.getName().equals("TEXTDECIMAL")) {
					textdecimal.add(new DTextDecimal(el, panel));
					}
				else if(el.getName().equals("EDITOR")) {
					editor.add(new DEditor(el, panel, db));
					}
				else if(el.getName().equals("IMAGE")) {
					image.add(new DImage(el, panel));
					}
				else if(el.getName().equals("FILE")) {
					file.add(new DFile(el, panel));
					}
				else if(el.getName().equals("BUTTON")) {
					button.add(new DButton(el,panel,db,this));
					}
				//else if(el.getName().equals("BLOB")) {
				//    blob.add(new DBlob(el, panel));
				//    }
				else if(el.getName().equals("DUALLIST")) {
					isduallist = true;
					dual.add(new DualList(el,panel,db));
					}

				else if(el.getName().equals("SECURITY")) {
					security.addsecurity(el, db);
					}


				//finally
				//add a dummy element eg image to cover up the overflow bug
				textarea.add(new DTextArea(el,panel,true));		//yes it is a dummy

				}

			else{		// (tabName != null) {
				System.out.println("FOUND TAB: " + tabName);
				tw = Integer.valueOf(fielddef.getAttribute("tw", "400"));
				th = Integer.valueOf(fielddef.getAttribute("th", "200"));
				if(!tabList.containsKey(tabName)) {
					System.out.println("inside the if block");
					tabList.put(tabName, new JPanel(null));
					tabs.add(tabName, tabList.get(tabName));
					}
				//fields.get(i++).addToPanel(tabList.get(tabName));
				if(el.getName().equals("TEXTFIELD")) {
					textfield.add(new DTextField(el, tabList.get(tabName)));
					}
				else if (el.getName().equals("TEXTAREA")) {
					textarea.add(new DTextArea(el, tabList.get(tabName)));
					}
				else if (el.getName().equals("CHECKBOX")) {
					checkbox.add(new DCheckBox(el, tabList.get(tabName)));
					}
				else if (el.getName().equals("COMBOBOX")) {
					combobox.add(new DComboBox(el, tabList.get(tabName), db));
					}
				else if(el.getName().equals("COMBOLIST")) {
					combolist.add(new DComboList(el, tabList.get(tabName)));
					}
				else if(el.getName().equals("TEXTLOOKUP")) {
					textlookup.add(new DTextLookup(el, tabList.get(tabName), db));
					}
				else if(el.getName().equals("TEXTDATE")) {
					textdate.add(new DTextDate(el, tabList.get(tabName)));
					}
				else if(el.getName().equals("TEXTTIME")) {
					texttime.add(new DTextTime(el, tabList.get(tabName)));
					}
				else if(el.getName().equals("DATESPIN")) {
					datespin.add(new DDateSpin(el, tabList.get(tabName)));
					}
				else if(el.getName().equals("TIMESPIN")) {
					timespin.add(new DTimeSpin(el, tabList.get(tabName)));
					}
				else if(el.getName().equals("DATETIMESPIN")) {
					datetimespin.add(new DDateTimeSpin(el, tabList.get(tabName)));
					}
				else if(el.getName().equals("TEXTDECIMAL")) {
					textdecimal.add(new DTextDecimal(el, tabList.get(tabName)));
					}
				else if(el.getName().equals("EDITOR")) {
					editor.add(new DEditor(el, tabList.get(tabName), db));
					}
				else if(el.getName().equals("IMAGE")) {
					image.add(new DImage(el, tabList.get(tabName)));
					}
				else if(el.getName().equals("FILE")) {
					file.add(new DFile(el, tabList.get(tabName)));
					}
				else if(el.getName().equals("BUTTON")) {
					button.add(new DButton(el,tabList.get(tabName),db,this));
					}
				//else if(el.getName().equals("BLOB")) {
				//    blob.add(new DBlob(el, panel));
				//    }
				else if(el.getName().equals("DUALLIST")) {
					isduallist = true;
					dual.add(new DualList(el,tabList.get(tabName),db));
					}

				}


		//FINALY ADD THE TABS TO THE DISPLAY PANEL
		if(tabList.size()>0) {
			tabs.setBounds(0, lh+2, tw, th+8);
			lh += th + 10;
			panel.add(tabs);
			}
		}


		/*hstore test - working but overflows the whole form
		System.out.println("DEBUG hstore test (attributetable)");
		JTable htable = new JTable(attributetable);
		htable.setPreferredScrollableViewportSize(new Dimension(500, 120));
		JScrollPane hScrollPane = new JScrollPane(htable);
		panel.add(hScrollPane);  */


		if(autoincr)
			sql += ", " + autofield;
		if(isinputfield)
			sql += ", " + inputfield;
		if(primaryfield != null)
			sql += ", " + primaryfield;
		if(autonumber != null)
			sql += ", " + autonumber;

		sql += " FROM " + tablename;

		String newsql = sql;
		if(ischild)
			newsql += " WHERE " + linkfield + " is null";
		if(ispopup)
			newsql += " WHERE " + linkfield + " = " + DContainer.popupfilter;

		System.out.println("DEBUG: new sql = " + newsql);

		// create a record set
		try {

			mydb = db;
			st = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = st.executeQuery(newsql);
			metaData = rs.getMetaData();

			DatabaseMetaData dbmd = mydb.getMetaData();
			ResultSet prs = dbmd.getPrimaryKeys("", "", tablename);
			while(prs.next())
				primarykey = prs.getString("COLUMN_NAME");
			prs.close();

			/*
			if(primarykey == null) {
				prs = dbmd.getPrimaryKeys("", "", tablename.toUpperCase());
				while(prs.next()) primarykey = prs.getString("COLUMN_NAME");
				prs.close();
				}
			*/

			if(primarykey == null)
				primarykey = primaryfield;

			//if(autoincr)
					autost = mydb.createStatement();
			}
		catch(SQLException ex) {
			System.out.println("SQLException SQL creation : " + ex.getMessage());
			JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
			}

		// Show first record
		String firstchild = null;
		if(ischild)
			recChild(firstchild);
		else
			moveNext();

		// Add action listner to the combobox
		for(DComboBox field : combobox)	field.datafield.addActionListener(this);

		//System.out.println(fielddef.getAttribute("name") + " : Form Done");

	}

	//get data in the field with this title. Either TEXTFIELD or COMBOBOX or COMBOLIST
	//its up to the sql function to cast the parameters accordingly
	public String getData(String name){
		String txt = name;

		//in some situations i need to use the value of the inputfield
		if (name.compareToIgnoreCase("inputval")==0)
		    return linkdata;

		for(DTextField field : textfield) {
			if(name.compareToIgnoreCase(field.name)==0) return "'" + field.getText() + "'";
			}
		for(DComboBox field : combobox) {
			if(name.compareToIgnoreCase(field.name)==0) return "'" + field.getText() + "'";
			}
		for(DTextDate field : textdate) {
			if(name.compareToIgnoreCase(field.name)==0) return "'" + field.getText() + "'";
			}
		 for(DCheckBox field : checkbox) {
			if(name.compareToIgnoreCase(field.name)==0) return "'" + field.getText() + "'";
			}
		for(DTextDecimal field : textdecimal) {
			if(name.compareToIgnoreCase(field.name)==0) return "'" + field.getText() + "'";
			}
		for(DComboList field : combolist) {
			if(name.compareToIgnoreCase(field.name)==0) return "'" + field.getText() + "'";
			}
		for(DTextLookup field : textlookup) {
			if(name.compareToIgnoreCase(field.name)==0) return "'" + field.getDataField() + "'";
			}
		for(DTextArea field : textarea) {
			if(name.compareToIgnoreCase(field.name)==0) return "'" + field.getText() + "'";
			}
		return name;		//if its a string literal or unrecognized

		}




	public void showData() {

		System.out.println("DEBUG: @ Form:showData()");

		if (security.isread) {
			try {

				//newlinkvalue = rs.getString(0);
				//System.out.println("\t newlinkvalue " + newlinkvalue);
				for(JPanel panel : panellist){
					for(Component comp : panel.getComponents()){
						TextField field = (TextField)comp;
						field.setText(rs.getString(field.getName()));
						}
					System.out.println("\n\n\t\t ++ PANEL : ");
					}

				for(DTextField field : textfield) {
					field.setText(rs.getString(field.name));
					if(field.cmbkey!=0) {
						for(DComboBox subfield : combobox){
							if (field.cmbkey==subfield.linkkey)
								subfield.getsubList(field.getText());
								}
							}
						}
				for(DTextArea field : textarea) {
					if (field.dummy == false){		//if not a dummy textarea
						int columnindex = rs.findColumn(field.name);
						if(metaData.getColumnType(columnindex) == Types.CLOB) {
							Clob cl = rs.getClob(field.name);
							if(cl != null) field.setText(cl.getSubString((long)1, (int)cl.length()));
							else field.setText("");
							}
							else field.setText(rs.getString(field.name));
						}
				}
				for(DCheckBox field : checkbox) field.setText(rs.getBoolean(field.name));
				for(DTextDate field : textdate) field.setText(rs.getString(field.name));
				for(DTextTime field : texttime) field.setText(rs.getString(field.name));
				for(DDateSpin field : datespin) field.setText(rs.getString(field.name));
				for(DTimeSpin field : timespin) field.setText(rs.getString(field.name));
				for(DDateTimeSpin field : datetimespin) field.setText(rs.getString(field.name));
				for(DTextDecimal field : textdecimal) field.setText(rs.getString(field.name));
				for(DComboList field : combolist) field.setText(rs.getString(field.name));
				for(DTextLookup field : textlookup) field.setText(rs.getString(field.name));
				for(DComboBox field : combobox) if(field.cmbkey==0) {
					if(field.inputfield) field.getList(linkdata);
					if(field.linkkey!=0) {
						Map<Integer, String>  m =  new TreeMap<Integer, String>();
						getComboKey(field.linkkey, field.getKey(rs.getString(field.name)), m);
						for(int ky : m.keySet()) {
							for(DComboBox lfield : combobox) {
								if(lfield.cmbkey==ky) lfield.setText(m.get(ky));
							}
						}
					}
					field.setText(rs.getString(field.name));
				}
				for(DEditor field : editor) field.setText(rs.getString(field.name));
				for(DImage field : image) field.setImage(rs.getBinaryStream(field.name));	//bytea
				for(DFile field : file) field.setFile(rs.getBinaryStream(field.name));		//display the file path -- bytea = byte array
				//for(DBlob field : blob) field.setBlob(rs.getBinaryStream(field.name));	//are we getting the OID ???????
				//for(DualList list : dual) list.refreshDualList();		//update the lists accordingly

				}
			catch(SQLException ex) {
			    System.out.println("Show Data SQLException : " + ex.getMessage());
				//ex.printStackTrace();
				JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void getComboKey(int lkey, String lvalue, Map<Integer, String>  m) {
		m.put(lkey, lvalue);
        for(DComboBox field : combobox) {
        	if((lkey==field.cmbkey) && (field.linkkey!=0))
				getComboKey(field.linkkey, field.getKey(lvalue), m);
        }
	}

    public void moveFirst() {
    	try {
        	if(rs.first()) showData();
			else clearFields();
        } catch(SQLException ex) {
        	System.out.println("SQLException move first: " + ex.getMessage());
			JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
       	}
	}

	public void moveNext() {

		try {
			if(rs.next())
				showData();
			else
				clearFields();
		} catch(SQLException ex) {
            System.out.println("SQLException move next: " + ex.getMessage());
			JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
    	}
 	}

    public void movePrevious() {
        try {
    		if(rs.previous())
				showData();
			else
				clearFields();
        } catch(SQLException ex) {
        	System.out.println("SQLException move previous : " + ex.getMessage());
			JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
        }
 	}

	public void moveLast() {
    	try {
        	if(rs.last())
				showData();
			else
				clearFields();
        } catch(SQLException ex) {
        	System.out.println("SQLException move last: " + ex.getMessage());
			JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
        }
	}

	public void recAdd() {
		if ((!isEdit) && (security.isadd)) {
			isAddNew = true;
			clearFields();
			}

		for(DImage field : image){
			//field.imagechanged = false;
			field.b[0].setEnabled(true);
			field.b[1].setEnabled(true);
			}
	}

	public void clearFields() {
		for(DTextField field : textfield) {
			field.setNew();
			if(field.cmbkey!=0) {
				for(DComboBox subfield : combobox){
					if(field.cmbkey==subfield.linkkey)
						subfield.getsubList(field.getText());
				}
			}
		}
		for(DCheckBox field : checkbox) field.setNew();
		for(DTextDate field : textdate) field.setNew();
		for(DTextArea field : textarea) field.setNew();
		for(DTextDecimal field : textdecimal) field.setNew();
		for(DEditor field : editor) field.setText("");
		for(DImage field : image) field.clearImage();
		for(DFile field : file) field.clearFile();
		//for(DBlob field : blob) field.clearImage();
		//for(DualList list : dual) list.clearLists();
		for(DComboBox field : combobox) {
			if ((field.cmbkey==0) && field.inputfield) field.getList(linkdata);
			if (field.cmbkey == 1) comboparse(field, field.cmbkey);
			field.setNew();
		}
	}

	//meaning edit should be clicked
	public void recEdit() {
		if((!isAddNew) && (security.isedit))
			isEdit = true;

		//image management
		for(DImage field : image){
			//System.out.println("\n\tDEBUG Image title=" + field.title + " imagechanged=" + field.imagechanged);
			field.imagechanged = false;
			field.b[0].setEnabled(true);
			field.b[1].setEnabled(true);
			}
		}

	public void recSave() {

		if(isAddNew)
			recInsert();
		if(isEdit)
			recUpdate();

		for(DImage field : image){
			//field.imagechanged = false;
			field.b[0].setEnabled(false);
			field.b[1].setEnabled(false);
			}
		}

	public void recInsert() {

		System.out.println("DEBUG: at recInsert()");

		try {
			rs.moveToInsertRow();

			// Get an input linked from a grid
			if(isinputfield) {	//and no formlabel

				System.out.println("DEBUG: inputfield = " + inputfield + ", linkdata = " + linkdata);

				if ((inputfield==null) || (linkdata==null)) {
					//JOptionPane.showMessageDialog(panel, "Click on the Reference Data on Grid", "Query error", JOptionPane.ERROR_MESSAGE);
					JOptionPane.showMessageDialog(panel, "Please Select Reference data on the Table", "Link Error", JOptionPane.ERROR_MESSAGE);
					rs.moveToCurrentRow();
					return;
					}

				updateRow(inputfield, linkdata);
				}

			//if(autoincr) {	//insert pk first  -- for postgres
			if(DLogin.databasetype.compareTo("postgres")==0){
				String autosql = "select nextval('" + tablename + "_" + autofield + "_seq');";
				ResultSet autors = autost.executeQuery(autosql);
				autors.next();
				int autoid = autors.getInt(1);
				autors.close();

				updateRow(autofield, Integer.toString(autoid));
				//updateRow("created_by", DLogin.getCurrentUserId());
				}
			//else if(autonumber != null){			// if Oracle
			else if (DLogin.databasetype.compareTo("oracle")==0){

				String sequence = "select " + tablename + "_id_seq.nextval from dual";
				System.out.println("Oracle Sequence = " + sequence);
				ResultSet autors = autost.executeQuery(sequence);
				System.out.println("\tb4 autors.next()");
				autors.next();

				long key = autors.getLong(1);
				System.out.println("\tkey = " + key);
				autors.close();
				updateRow(primaryfield, Long.toString(key));
				//updateRow("created_by", DLogin.getCurrentUserId());
				}
			else if (DLogin.databasetype.compareTo("mysql")==0){
				System.out.println("\nGetting autonumber for mysql");

				//String autosql = "select last_insert_id()";
				String autosql = "select max(" + autonumber + ") from " + tablename;
				ResultSet autors = autost.executeQuery(autosql);
				autors.next();

				long key = autors.getLong(1);
				System.out.println("\tkey = " + key);
				autors.close();
				updateRow(autonumber, Long.toString(key+1));
				//updateRow("created_by", DLogin.getCurrentUserId());
				}


			updateRows();
			rs.insertRow();

			isAddNew = false;
			//recAudit("Insert", rs.getString(primarykey));
			}
		catch (SQLException ex) {
         	System.out.println("New row error : " + ex);
			JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
			}

		// Refresh the combo list
		for(DComboBox field : combobox) if(field.cmbkey==0) field.getList();

		}

 	public void recUpdate() {

		updateRows();

		try {
			rs.updateRow();
			rs.moveToCurrentRow();

			System.out.println("BASE 10 " + primarykey);
			System.out.println("BASE 11 " + rs.getString(primarykey));

			//if(isEdit) recAudit("Edit", rs.getString(primarykey));

			isEdit = false;
			}
		catch (SQLException ex) {
        	System.out.println("Edit row error : " + ex);
			JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
			}
		}

	public void updateRows() {
		for (String field : defaultfield.keySet())
				updateRow(field, defaultfield.get(field));

		for(DTextField field : textfield) {
			//if upload (ie blob).... behave accordingly (for oracle, postgres n mysql)
			//rs.updateBinaryStream(field.name, field.getBlob(), field.getBloblen());
			if((field.cmbkey==0) && (field.enabled)) {
				updateRow(field.name, field.getText());
				}
			}
		for(DTextArea field : textarea){
			if(field.dummy == false)updateRow(field.name, field.getText());
			}
		for(DCheckBox field : checkbox) updateRow(field.name, field.getText());
		for(DTextDate field : textdate) updateRow(field.name, field.getText());
		for(DTextTime field : texttime) updateRow(field.name, field.getText());
		for(DDateSpin field : datespin) updateRow(field.name, field.getText());
		for(DTimeSpin field : timespin) updateRow(field.name, field.getText());
		for(DDateTimeSpin field : datetimespin) updateRow(field.name, field.getText());
		for(DTextDecimal field : textdecimal) updateRow(field.name, field.getText());
		for(DComboList field : combolist) updateRow(field.name, field.getText());
		for(DTextLookup field : textlookup) updateRow(field.name, field.getText());
		for(DComboBox field : combobox)  {
			if(field.cmbkey==0) {
				updateRow(field.name, field.getText());
				System.out.println(field.name + " : " + field.getText());
				}
			  }
		for(DEditor field : editor) updateRow(field.name, field.getText());
		//for(DualList list : dual)		//execute sql

		try {

			for(DImage field : image){
				System.out.println("\n\tDEBUG Image title=" + field.title + " imagechanged=" + field.imagechanged);

				if(field.imagechanged == true){		//update only when the image has changed otherwise ignore
					System.out.println("\n\tDEBUG:\n\t\tat recUpdate:imagechanged = " + field.imagechanged);
					rs.updateBinaryStream(field.name, field.getImage(), field.getImagelen());
					}
				}
			}
		catch (SQLException ex) {
				System.out.println("The SQL Exeption for image update : " + ex);
				JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
				}

		try {
		    for(DFile field : file){
				rs.updateBinaryStream(field.name, field.getFile(), field.getFileLength());
				System.out.println("File " + field.getFilePath() + " updated successfully");
				}
		    }
		catch (SQLException ex) {
		    System.out.println("The SQL Exeption for image update : " + ex);
		    JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
		    }

		//try {
		//	for(DBlob field : blob) rs.updateBinaryStream(field.name, field.getBlob(), field.getBlobLength());
		//    }
		//catch (SQLException ex) {
		//    System.out.println("The SQL Exeption for image update : " + ex);
		//    JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
		//    }
	      }

	public void updateRow(String fname, String fvalue) {

		System.out.println("DEBUG: AT updateRow(): fname = " + fname + ", fvalue = " + fvalue );

		int type;

        try {
			int columnindex = rs.findColumn(fname);
			if(fvalue.length()<1) {
				rs.updateNull(fname);
				}
			else {
				type = metaData.getColumnType(columnindex);

				// System.out.println(fname + " = " + fvalue + " type = " + type);
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
				  case Types.NUMERIC:
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
				  case Types.TIMESTAMP:
						  java.sql.Timestamp tsvalue = java.sql.Timestamp.valueOf(fvalue);
						  rs.updateTimestamp(fname, tsvalue);
						  break;
				  case Types.CLOB:
						  Clob clb = mydb.createClob();
						  clb.setString(1, fvalue);		//give
						  rs.updateClob(fname, clb);
						  break;
				  case Types.BLOB:
						  //Blob lob = mydb.createBlob();
						  //lob.setBlob(1, fvalue);	//
						  //rs.updateBlob(fname, lob);
						  System.out.println("DEBUG: Trying to save a BLOB. Under Con");
						  break;
				}
		   	}
        } catch (SQLException ex) {
        	System.out.println("The SQL Exeption on " + fname + " : " + ex);
			JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
        }
	}

	public void recDelete() {
		if (security.isdelete) {
			try {
				String recordid = rs.getString(primarykey);
				rs.deleteRow();

				rs.refreshRow();

				//recAudit("Delete", recordid);
			} catch (SQLException ex) {
        		System.out.println("Delete row error : " + ex);
				JOptionPane.showMessageDialog(panel, ex.getMessage(), "Delete error", JOptionPane.ERROR_MESSAGE);
			}

			// show data
			showData();
		}
	}

	public void recCancel() {
		isAddNew = false;
		isEdit = false;

		showData();

		for(DImage field : image){
			//field.imagechanged = false;
			field.b[0].setEnabled(false);
			field.b[1].setEnabled(false);
			}
		}

	public void recRefresh() {

		try {
			// Refresh the combo list
			for(DComboBox field : combobox) if(field.cmbkey==0) field.getList();

            rs.refreshRow();
		} catch (SQLException ex) {
        	System.out.println("Refresh row error : " + ex);
			//JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
		}

		// show data
		showData();
	}

	public void linkinput(String data) {

		linkdata = data;

		System.out.println("DEBUG: @Form:linkinput(); isduallist = " + isduallist);

		if(label == true){
			//Form Label Query
			String query = "select " + inputfield + "," + labelfield + " from " + labeltable + " where " + inputfield + " = " + data;
			System.out.println("\nDEBUG: Form Label Query : " + query);

			try {
				Statement s = mydb.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
				ResultSet r = s.executeQuery(query);

				r.first();

				labeldata = r.getString(2);			//update the linkvalue
				formlabel.setText("<html><b>&nbsp;&nbsp;" + labeldescr + ": " + labeldata + "</b></html>");
				System.out.println("DEBUG: labeldata = " + formlabel.getText());

				r.close();
				s.close();
				}

			catch(SQLException ex) {
				System.out.println("SQException at Form Label initialization : " + ex.getMessage());
				}
			}
		if(isduallist == true){
			//System.out.println("DEBUG: processing updateDualList()");
			for(DualList list : dual) list.refreshDualList(tablename,inputfield,data);
			}
	}

	public void lookupinput(String data) {
		lookupdata = data;
		for(DTextLookup field : textlookup)
			field.setlookup(data);
	}

	public void recChild(String linkvalue) {

		link = linkvalue;
		String newsql = "";
		if(linkvalue==null)
			newsql = sql + " WHERE " + linkfield + " is null";
		//else if(ispopup)
		//	newsql += " WHERE " + linkfield + " = " + DContainer.popupfilter;
		else
			newsql = sql + " WHERE " + linkfield + " = '" + linkvalue + "'";

		// create a record set
		try {

			rs.close();
            rs = st.executeQuery(newsql);

		} catch(SQLException ex) {
        	System.out.println("SQLException on recChild : " + ex.getMessage());
			JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
		}

        // Show first record
        moveNext();
	}

	public void recAudit(String changetype, String recordid) {
    	try {
			String inssql = "INSERT INTO audit_trail (user_name, tablename, recordid, changetype) ";
			//inssql += " VALUES(current_user, '" + tablename + "', '" + recordid  + "', '" + changetype + "')";
			inssql += " VALUES(" + DLogin.getLoggedInUser() + ", '" + tablename + "', '" + recordid  + "', '" + changetype + "')";
			Statement stUP = mydb.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			stUP.executeUpdate(inssql);
			stUP.close();
 		} catch (SQLException ex) {
         	System.out.println("Audit record error : " + ex);
			JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
        }
	}

	// Listen to the combobox action event
	public void actionPerformed(ActionEvent e) {
		if(!iscmb) {
			iscmb = true;

			for(DComboBox field : combobox){
				if(field.cmbkey!=0) {
					int cmbkey = Integer.valueOf(e.getActionCommand()).intValue();
                    comboparse(field, cmbkey);
				}
			}

			for(DTextField field : textfield){
				if(field.cmbkey!=0) {
					int cmbkey = Integer.valueOf(e.getActionCommand()).intValue();
					for(DComboBox subfield : combobox){
						if((cmbkey==field.cmbkey) && (field.cmbkey==subfield.linkkey)) {
							subfield.getsubList(field.getText());
							if ((subfield.datafield.getItemCount() > 0) && (subfield.datafield.getSelectedIndex() == -1))
								subfield.datafield.setSelectedIndex(0);
						}
					}
				}
			}

			iscmb = false;
		}
	}

	private void comboparse(DComboBox field, int cmbkey) {
		for(DComboBox subfield : combobox) {
			if((cmbkey == field.cmbkey) && (field.cmbkey == subfield.linkkey)) {
				subfield.getList(field.getText());
				if (subfield.datafield.getItemCount() > 0) {
					subfield.datafield.setSelectedIndex(0);
					if(subfield.cmbkey!=0) comboparse(subfield, subfield.cmbkey);
				}
			}
		}
	}


	private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {
 			// TODO add your handling code here:
			recAdd();
			btnNew.setEnabled(false);
			btnModify.setEnabled(false);
			btnDelete.setEnabled(false);
			btnRefresh.setEnabled(false);
 			}

	private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {
 			// TODO add your handling code here:
			recSave();
 			parent.refreshGrids();
			if(!disabledbuttons.contains("new"))	//if not supposed to be disabled
				btnNew.setEnabled(true);
			if(!disabledbuttons.contains("cancel"))	//if not supposed to be disabled
				btnCancel.setEnabled(true);
			if(!disabledbuttons.contains("edit"))	//if not supposed to be disabled
				btnModify.setEnabled(true);
			if(!disabledbuttons.contains("refresh"))	//if not supposed to be disabled
				btnRefresh.setEnabled(true);
			if(!disabledbuttons.contains("delete"))	//if not supposed to be disabled
				btnDelete.setEnabled(true);

			//if(isChild){
			    //update form label with latest data
			    //1. run sql query or read from textbox
			  //  }
 		}

	private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {
 			// TODO add your handling code here:
			recCancel();

			System.out.println("Disabled buttons : " + disabledbuttons);

			if(!disabledbuttons.contains("new"))	//if not supposed to be disabled
				btnNew.setEnabled(true);
			if(!disabledbuttons.contains("cancel"))	//if not supposed to be disabled
				btnCancel.setEnabled(true);
			if(!disabledbuttons.contains("edit"))	//if not supposed to be disabled
				btnModify.setEnabled(true);
			if(!disabledbuttons.contains("refresh"))	//if not supposed to be disabled
				btnRefresh.setEnabled(true);
			if(!disabledbuttons.contains("delete"))	//if not supposed to be disabled
				btnDelete.setEnabled(true);
 		}

	private void btnModifyActionPerformed(java.awt.event.ActionEvent evt) {
 			// TODO add your handling code here:
			recEdit();

			btnModify.setEnabled(false);
			btnCancel.setEnabled(true);
 			btnNew.setEnabled(false);
			btnRefresh.setEnabled(false);
			btnDelete.setEnabled(false);
 		}

	private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {

			// Refresh the combo list
		for(DComboBox field : combobox){
			if(field.cmbkey==0)
				field.getList();
			}

			parent.refreshGrids();

			if(!disabledbuttons.contains("new"))	//if not supposed to be disabled
				btnNew.setEnabled(true);
			if(!disabledbuttons.contains("cancel"))	//if not supposed to be disabled
				btnCancel.setEnabled(true);
			if(!disabledbuttons.contains("edit"))	//if not supposed to be disabled
				btnModify.setEnabled(true);
			if(!disabledbuttons.contains("save"))	//if not supposed to be disabled
				btnSave.setEnabled(true);
			if(!disabledbuttons.contains("delete"))	//if not supposed to be disabled
				btnDelete.setEnabled(true);
 		}

	private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {

			//Custom button text
			Object[] options = {"YES","NO","CANCEL"};
			int n = JOptionPane.showOptionDialog(parent, "Are your sure you want to delete this record ?","Delete Check",JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE,null, options, options[2]);

			//YES_OPTION, NO_OPTION, CANCEL_OPTION, OK_OPTION,
			if(n==JOptionPane.YES_OPTION){
				recDelete();
				parent.refreshGrids();
				System.out.println("delete");
				}

 		}

	String newlinkvalue;

	//rs advance mechs
	private void btnFirstActionPerformed(java.awt.event.ActionEvent evt) {

			String innersql = "select min(" + linkfield +") from " + tablename;
			advanceRS(innersql);

			}
	private void btnPreviousActionPerformed(java.awt.event.ActionEvent evt) {

			String innersql = "select max(" + linkfield +") from " + tablename + " where " + linkfield + " < " + link ;
			advanceRS(innersql);

			}
	private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {

			String innersql = "select min(" + linkfield +") from " + tablename + " where " + linkfield + " > " + link ;
			advanceRS(innersql);

			}
	private void btnLastActionPerformed(java.awt.event.ActionEvent evt) {

			String innersql = "select max(" + linkfield +") from " + tablename;
			advanceRS(innersql);

			}


	private void advanceRS(String innerselect){

		String advancesql = sql + " WHERE " + linkfield + " = (" + innerselect + ")";
		System.out.println("\nDEBUG: advancesql " + advancesql);
		// create a record set
		try {
			Statement st = mydb.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		rs = st.executeQuery(advancesql);

			if(rs.next()){
				//if(primaryfield != null) sql += ", " + primaryfield;
				link = rs.getString(autofield!=null?autofield:(primaryfield!=null?primaryfield:autonumber));			//update the linkvalue
				//System.out.println("\t new link: " + linkfield + " = " + link);
				showData();
				}
			}
		catch(SQLException ex) {
        	System.out.println("SQLException on recChild : " + ex.getMessage());
			JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
			}
		}


public DContainer getParent(){
	return parent;
	}


private void initButtons(){

	//bl.add(formlabel);

	bv.add(btnNew);
	bv.add(btnSave);
	bv.add(btnCancel);
	bv.add(btnModify);
	bv.add(btnRefresh);
	bv.add(btnDelete);

	bh.add(btnFirst);
	bh.add(btnPrevious);
	bh.add(btnNext);
	bh.add(btnLast);

	//if(label)
	panel.add(BorderLayout.NORTH,bl);
	panel.add(BorderLayout.EAST,bv);
	panel.add(BorderLayout.SOUTH,bh);
	}

}

//BUG REPORT
//1. narrative/details text area malfunction
