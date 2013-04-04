package org.elegance;

import java.sql.Connection;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.awt.Color;

import javax.swing.JInternalFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import javax.swing.UIManager;
import javax.swing.JFrame;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;

/*
The Container for Grids, Forms, Tabs, Reports etc
*/

public class DContainer extends JInternalFrame implements MouseListener, Runnable, FocusListener {

	public List<DForm> forms;
	public List<DReport> reports;
	public List<DGrid> grids;
	public List<DFilter> filters;
	public List<SearchEngine> searchengines;
	public List<DDrillDown> drilldowns;
	public List<DAction> actions;
	public List<DCalendar> calendars;
	public List<DImport> imports;

	public List<DTabs> toplist;
	public List<DTabs> bottomlist;
	public List<DTabs> leftlist;


	public JTabbedPane toppane, bottompane, leftpane;
	public JSplitPane splitpane, lsplitpane;

	public String name, key;

	public boolean isVisible = false;

	private int w, h, splitloc, leftsplit;

	private int type, index, subkey;

	public boolean started = false;

	DElement llchild;
	Connection ldb;
	String lreportpath;
	String lcname;

	boolean ispopup;
	static String popupfilter;

	public DContainer(DElement lchild, Connection db, String reportpath, String cname) {

		super(lchild.getAttribute("name"), true, true, true, true);

		String[] location = lchild.getAttribute("location","0,0").split(",");

		try{
			setName(cname);
			//setLocation(Integer.parseInt(location[0]), Integer.parseInt(location[1]));

			//this.setIconImage(new ImageIcon("images/juice.jpeg"));
			//this.setIcon(true);

			name = lchild.getAttribute("name");
			key = lchild.getAttribute("key", "");
			w = Integer.valueOf(lchild.getAttribute("w")).intValue()+80;
			h = Integer.valueOf(lchild.getAttribute("h")).intValue();
			splitloc = Integer.valueOf(lchild.getAttribute("splitloc", "200")).intValue();
			leftsplit = Integer.valueOf(lchild.getAttribute("leftsplit", "200")).intValue();

			llchild = lchild;
			ldb = db;
			lreportpath = reportpath;
			lcname = cname;

			popupfilter = "";

			//this.setDrawDecoration(true);
			ImageIcon icon = createImageIcon("images/book.jpeg");
			this.setFrameIcon(icon);

			}
		//catch(java.beans.PropertyVetoException ex){
		catch(Exception ex){
			System.out.println("Error at DContainer : " + ex.getMessage());
			}
		}

	public void makeContainer() {
		makeContainer(llchild, ldb, lreportpath, lcname);
		}

	public void makeContainer(DElement lchild, Connection db, String reportpath, String cname) {
		// Initialize holders
		forms = new ArrayList<DForm>();
		reports = new ArrayList<DReport>();
		grids = new ArrayList<DGrid>();
		filters = new ArrayList<DFilter>();
		searchengines = new ArrayList<SearchEngine>();
		drilldowns = new ArrayList<DDrillDown>();
		actions = new ArrayList<DAction>();
		calendars = new ArrayList<DCalendar>();
		imports =  new ArrayList<DImport>();

		toplist = new ArrayList<DTabs>();
		bottomlist = new ArrayList<DTabs>();
		leftlist = new ArrayList<DTabs>();

		List<DElement> fchild = lchild.getElements();

		toppane = new JTabbedPane(JTabbedPane.TOP);
		bottompane = new JTabbedPane(JTabbedPane.TOP);
		leftpane = new JTabbedPane(JTabbedPane.TOP);

		if (lchild.getAttribute("splittype", "").equals("vert")) {
			splitpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, toppane, bottompane);
			splitpane.setOneTouchExpandable(true);
			splitpane.setDividerLocation(splitloc);
			// Add the split pane to the main container.
			add(splitpane);

			}
		else if(lchild.getAttribute("splittype", "").equals("horl")){
			splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, toppane, bottompane);
			splitpane.setOneTouchExpandable(true);
			splitpane.setDividerLocation(splitloc);
			// Add the split pane to the main container.
			add(splitpane);

			}
		else if(lchild.getAttribute("splittype", "").equals("hybrid")){
		      splitpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, toppane, bottompane);
		      lsplitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftpane, splitpane);

		      //splitpane.setOneTouchExpandable(true);
		      splitpane.setDividerLocation(splitloc);

		      //lsplitpane.setOneTouchExpandable(true);
		      lsplitpane.setDividerLocation(leftsplit);
		      // Add the split pane to the main container.
		      add(lsplitpane);
		      }



		for(DElement el : fchild) {
			String tmpstr = el.getAttribute("key", "");
			int fkey = 0;
			if(!tmpstr.equals("")) fkey = Integer.valueOf(tmpstr);

			if(el.getName().equals("FORM")) {
				ImageIcon icon = createImageIcon("images/book.jpeg");
				//ImageIcon icon = createImageIcon("images/binoculars.jpeg");
				forms.add(new DForm(this, el, db));
				if(el.getAttribute("pos").equals("top")){
					toppane.addTab(el.getAttribute("name"), icon, forms.get(forms.size()-1).panel);
					toplist.add(new DTabs(0, forms.size()-1, fkey));
					}
				else{
					bottompane.addTab(el.getAttribute("name"), icon, forms.get(forms.size()-1).panel);
					bottomlist.add(new DTabs(0, forms.size()-1, fkey));
					}
				//if its a popup form
				//System.out.println("DEBUG: found FORM popup = " + el.getAttribute("popup"));
				//System.out.println("DEBUG: FORM ispopup = " + forms.get(forms.size()-1).ispopup);
				//forms.get(forms.size()-1).recChild(popupfilter);
				}
			else if(el.getName().equals("REPORT")) {
				ImageIcon icon = createImageIcon("images/chart.jpeg");
				reports.add(new DReport(el, db, reportpath));
				if(el.getAttribute("pos").equals("top")){
					toppane.addTab(el.getAttribute("name"), icon, reports.get(reports.size()-1).panel);
					toplist.add(new DTabs(1, reports.size()-1, fkey));
					}
				else {
					bottompane.addTab(el.getAttribute("name"), icon, reports.get(reports.size()-1).panel);
					bottomlist.add(new DTabs(1, reports.size()-1, fkey));
					}
			} else if(el.getName().equals("GRID")) {
				ImageIcon icon = createImageIcon("images/calendar.jpeg");
				grids.add(new DGrid(el, db));
				if(el.getAttribute("pos").equals("top")) {
					toppane.addTab(el.getAttribute("name"), icon, grids.get(grids.size()-1).panel);
					toplist.add(new DTabs(2, grids.size()-1, fkey));
					}
				else if(el.getAttribute("pos").equals("bottom")){
					bottompane.addTab(el.getAttribute("name"), icon, grids.get(grids.size()-1).panel);
					bottomlist.add(new DTabs(2, grids.size()-1, fkey));
					}
				else if(el.getAttribute("pos").equals("left")){
					leftpane.addTab(el.getAttribute("name"), icon, grids.get(grids.size()-1).panel);
					leftlist.add(new DTabs(2, grids.size()-1, fkey));
					}
			}
			else if(el.getName().equals("FILTER")) {
				ImageIcon icon = createImageIcon("images/binoculars.jpeg");
				filters.add(new DFilter(el, db));
				if(el.getAttribute("pos").equals("top")) {
					toppane.addTab(el.getAttribute("name"), icon, filters.get(filters.size()-1).panel);
					toplist.add(new DTabs(3, filters.size()-1, fkey));
					}
				else {
				      bottompane.addTab(el.getAttribute("name"), icon, filters.get(filters.size()-1).panel);
				      bottomlist.add(new DTabs(3, filters.size()-1, fkey));
				      }
				}
			else if(el.getName().equals("SEARCH")) {

					ImageIcon icon = createImageIcon("images/binoculars.jpeg");
					searchengines.add(new SearchEngine(el, db));
					if(el.getAttribute("pos").equals("top")){
						toppane.addTab(el.getAttribute("name"), icon, searchengines.get(searchengines.size()-1).panel);
						toplist.add(new DTabs(3, searchengines.size()-1, fkey));
						}
					else{
						bottompane.addTab(el.getAttribute("name"), icon, searchengines.get(searchengines.size()-1).panel);
						bottomlist.add(new DTabs(3, searchengines.size()-1, fkey));
						}

				}

			else if(el.getName().equals("DRILLDOWN")) {
				ImageIcon icon = createImageIcon("images/juice.jpeg");
				drilldowns.add(new DDrillDown(el, db));
                if(el.getAttribute("pos").equals("top")) {
                	toppane.addTab(el.getAttribute("name"), icon, drilldowns.get(drilldowns.size()-1).panel);
                    toplist.add(new DTabs(4, drilldowns.size()-1, fkey));
					}
				else {
                	bottompane.addTab(el.getAttribute("name"), icon, drilldowns.get(drilldowns.size()-1).panel);
					bottomlist.add(new DTabs(4, drilldowns.size()-1, fkey));
					}
				}
			else if(el.getName().equals("CALENDAR")) {
				//ImageIcon icon = createImageIcon("images/calender.gif");
				ImageIcon icon = createImageIcon("images/calendar.jpeg");
				calendars.add(new DCalendar(el));
                if(el.getAttribute("pos").equals("top")) {
                	toppane.addTab(el.getAttribute("name"), icon, calendars.get(calendars.size()-1).panel);
                    toplist.add(new DTabs(5, calendars.size()-1, fkey));
              	} else {
                	bottompane.addTab(el.getAttribute("name"), icon, calendars.get(calendars.size()-1).panel);
					bottomlist.add(new DTabs(5, calendars.size()-1, fkey));
				}
        	} else if(el.getName().equals("IMPORT")) {
				ImageIcon icon = createImageIcon("images/flash.jpeg");
				imports.add(new DImport(el, db));
                if(el.getAttribute("pos").equals("top")) {
                	toppane.addTab(el.getAttribute("name"), icon, imports.get(imports.size()-1).panel);
                    toplist.add(new DTabs(6, imports.size()-1, fkey));
              	} else {
                	bottompane.addTab(el.getAttribute("name"), icon, imports.get(imports.size()-1).panel);
					bottomlist.add(new DTabs(6, imports.size()-1, fkey));
				}
			}  else if(el.getName().equals("ACTION")) {
				ImageIcon icon = createImageIcon("images/action.gif");
				actions.add(new DAction(el, db));
				if(el.getAttribute("name") != null) {
                	if(el.getAttribute("pos").equals("top")) {
                		toppane.addTab(el.getAttribute("name"), icon, actions.get(actions.size()-1).panel);
                    	toplist.add(new DTabs(7, actions.size()-1, fkey));
              		} else {
                		bottompane.addTab(el.getAttribute("name"), icon, actions.get(actions.size()-1).panel);
						bottomlist.add(new DTabs(7, actions.size()-1, fkey));
					}
				}
			}
		}

		// add mouse listner for tab selections
		toppane.addMouseListener(this);
		bottompane.addMouseListener(this);
		leftpane.addMouseListener(this);

		// add the grid click action listener (for all grids)
		for(DGrid grid : grids) grid.table.addMouseListener(this);

		// add click to the form components
		for(DForm form : forms) {
			form.panel.addMouseListener(this);
			for(DTextField field : form.textfield) field.datafield.addFocusListener(this);
			for(DTextArea field : form.textarea) field.datafield.addFocusListener(this);
			for(DCheckBox field : form.checkbox) field.datafield.addFocusListener(this);
			for(DTextDate field : form.textdate) field.datafield.addFocusListener(this);
			for(DTextTime field : form.texttime) field.datafield.addFocusListener(this);
			for(DDateSpin field : form.datespin) field.datafield.addFocusListener(this);
			for(DTimeSpin field : form.timespin) field.datafield.addFocusListener(this);
			for(DTextDecimal field : form.textdecimal) field.datafield.addFocusListener(this);
			for(DComboList field : form.combolist) field.datafield.addFocusListener(this);
			for(DTextLookup field : form.textlookup) field.datafield.addFocusListener(this);
			for(DComboBox field : form.combobox) field.datafield.addFocusListener(this);
			for(DEditor field : form.editor) field.datafield.addFocusListener(this);
			//for(DFile field : form.file) field.datafield.addFocusListener(this);
		}

		// add click to the filter components
        for(DFilter filter : filters)
			filter.panel.addMouseListener(this);

		// add the grid click action listener
		for(DCalendar calendar : calendars)
			calendar.table.addMouseListener(this);

		// add the tree to the listener
		for(DDrillDown drilldown : drilldowns)
			drilldown.tree.addMouseListener(this);

		// Index initial value
		index = 0;
		type = 2;
		subkey = 0;
		started = true;

 		// Set the default size
		setSize();
	}

	public void run() { }

  	public void setSize() {
        super.setLocation(10, 10);
        super.setSize(w, h);
 	}

	//here
	public void setItem(String pos) {
		//if(toppane.getSelectedIndex()<0) istop=false;
		//if(bottompane.getSelectedIndex()<0) istop=true;


		if(bottompane.getSelectedIndex()<0) pos="istop";
		if(toppane.getSelectedIndex()<0) pos="isbottom";



		for(int i=0; i<toppane.getTabCount(); i++)
		    toppane.setForegroundAt(i, Color.BLACK);
		for(int i=0; i<bottompane.getTabCount(); i++)
		    bottompane.setForegroundAt(i, Color.BLACK);
		for(int i=0; i<leftpane.getTabCount(); i++)
		    leftpane.setForegroundAt(i, Color.BLACK);

		if(pos.equals("isleft")) {
			type = leftlist.get(leftpane.getSelectedIndex()).type;
			index = leftlist.get(leftpane.getSelectedIndex()).index;
			subkey = leftlist.get(leftpane.getSelectedIndex()).key;
			leftpane.setForegroundAt(leftpane.getSelectedIndex(), Color.BLUE);
			}
		else if(pos.equals("istop")) {
			type = toplist.get(toppane.getSelectedIndex()).type;
			index = toplist.get(toppane.getSelectedIndex()).index;
			subkey = toplist.get(toppane.getSelectedIndex()).key;
			toppane.setForegroundAt(toppane.getSelectedIndex(), Color.BLUE);
			}
		else if(pos.equals("isbottom")) {
			type = bottomlist.get(bottompane.getSelectedIndex()).type;
			index = bottomlist.get(bottompane.getSelectedIndex()).index;
			subkey = bottomlist.get(bottompane.getSelectedIndex()).key;
			bottompane.setForegroundAt(bottompane.getSelectedIndex(), Color.BLUE);
			}

		System.out.println("Type = " + type + " index = " + index + " key = " + subkey + " pos = " + pos);
	}

 	public void setVisible(boolean visible) {
		if(started) {
			for(DReport report: reports){
				System.out.println("REPORT ispopup = " + report.ispopup);
				//String keyvalue = grids.get(index).getKey();
				if(report.ispopup) {
					System.out.println("POPUP report found");
					report.putparams("filterid", popupfilter);			//use previously storead poupfilter value hardcoded for now
					report.drillReport();
					}
				else{
					report.showReport();
					}
				}
			}

        super.setVisible(visible);
		isVisible = visible;
  	}


	//this is where the toolbar is controlled from ie button click events
	public void command(int lcmd) {
		if(type==0) {
			switch(lcmd) {
				case 0: forms.get(index).moveFirst(); break;
				case 1: forms.get(index).movePrevious(); break;
				case 2: forms.get(index).moveNext(); break;
				case 3: forms.get(index).moveLast(); break;
				case 4: forms.get(index).recAdd(); break;
				case 5: forms.get(index).recEdit(); break;
				case 6: forms.get(index).recSave(); refreshGrids(); refreshDrilldown(); break;
				case 7: forms.get(index).recDelete(); refreshGrids(); break;
				case 8: forms.get(index).recCancel(); break;
				case 9: forms.get(index).recRefresh(); break;
				}
			}
		else if(type==1) {
			if(lcmd==9)
				reports.get(index).showReport();
				}
		else if(type==2) {		//grid ?
			if(lcmd==6)
				grids.get(index).refresh();
			if(lcmd==1) {					//action (original index = 11)
				for(DAction action : actions) {
					if(action.linkkey==subkey)
						action.execproc();
					}
				}
			if(lcmd==9)
				grids.get(index).savecvs();			//export original index = 12
			if(lcmd==15) {
				String filtername = grids.get(index).filtername;
				for(DReport report : reports) {
					//System.out.println("REPORT ispopup = " + report.ispopup);
					for(String mylist : grids.get(index).tabledef.keylist) {
						report.printreport(filtername, mylist);
						}
					}
				}
			if(lcmd==16) {		//filter
				String filtername = grids.get(index).filtername;
				for(DAction action : actions) {
					for(String mylist : grids.get(index).tabledef.keylist) {
						action.linkinput(mylist);
						action.execproc();
					}
				}
			}
		} else if(type==3) {
			if(lcmd==7) {		//filter
				for(DGrid grid : grids) {
					if((grid.filterkey != 0) && (grid.filterkey==subkey))
						grid.filter(filters.get(index).getWhere());
				}
				for(DReport report : reports) {
					//System.out.println("\n\t\tcommand() type=3");
					if(report.ischild) {
						if(filters.get(index).isWhere) {
							report.putparams("wherefilter", filters.get(index).getWhere());
							report.drillReport();
							}
						else {
							report.putparams(filters.get(index).getParam());
							}
						}
				}
			}
		} else if(type==4) {
			if(lcmd==9) drilldowns.get(index).createtree();
		}
	}

	// Get the grids
	public void refreshGrids() {
		System.out.println("DEBUG: refreshing grids");
		for(DGrid grid : grids)
			grid.refresh();
		}

	public void refreshDrilldown() {
		System.out.println("DEBUG: recreating the drilldown tree");
		for(DDrillDown drilldown : drilldowns)
			drilldown.createtree();
		}

	// Create an icon
    protected ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = DContainer.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

	// Get the grid listening mode
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

	public void mouseClicked(MouseEvent e) {

		//System.out.println("DEBUG: type = " + type);
		//do this if we got grids
		//if(type!= 0 && type!=2 && type !=4 && type!=5){
		String keyval;
		try{
			keyval = grids.get(index).getKey();
			System.out.println("\t\tSaving..... popup filter value = " + keyval);
			popupfilter = keyval;
			}
		catch(IndexOutOfBoundsException ex){
			System.out.println("DEBUG: poupup filter \n Error: " + ex.getMessage());
			System.out.println("Caused By: " + ex.getCause());
			keyval = "-1";
			}
		//	}

		if(e.getComponent().equals(toppane)){
			//setItem(true);
			setItem("istop");
			}
		else if(e.getComponent().equals(bottompane)) {
			//setItem(false);
			setItem("isbottom");
			}
		else if(e.getComponent().equals(leftpane)) {
			setItem("isleft");
			}
		else {
			if(e.getComponent().getParent().getParent().getParent().getParent().equals(toppane)) {
				setItem("istop");
				}
			else if(e.getComponent().getParent().getParent().getParent().getParent().equals(bottompane)) {
				setItem("isbottom");
				}
			else if(e.getComponent().getParent().getParent().getParent().getParent().equals(leftpane)) {
				setItem("isleft");
				}
			else if(e.getComponent().getParent().equals(toppane)) {
				setItem("istop");
				}
			else if(e.getComponent().getParent().equals(bottompane)) {
				setItem("isbottom");
				}
			else if(e.getComponent().getParent().equals(leftpane)) {
				setItem("isleft");
				}
			}

		if(type==2) {		//after grid selection
			String keyvalue = grids.get(index).getKey();

			System.out.println("DEBUG: At DContainer:mouseClicked\n\t\tkeyvalue = " + keyvalue);

			for(DForm form : forms) {
				if((form.ischild) && (form.linkkey==subkey))		//if this is a child
					form.recChild(keyvalue);
				if((form.isinputfield) && (form.inputkey==subkey))	//if input should come from a grid
					form.linkinput(keyvalue);
				if((form.islookup) && (form.lookupkey==subkey))
					form.lookupinput(keyvalue);
				}
			for(DGrid grid : grids) {
			    if((grid.linkkey != 0) && (grid.linkkey==subkey))
					grid.makechild(keyvalue);
				}
			for(DAction action : actions){
				if((action.ischild) && (action.linkkey==subkey))
					action.linkinput(keyvalue);
				}

			for(DReport report : reports){
				//System.out.println("\n\t\tmouseClicked() type=2");
				if((report.ischild) && (keyvalue.length()>0) && (report.linkkey==subkey)) {
					report.putparams(grids.get(index).filtername, keyvalue);
					report.drillReport();
					}
				}
			//}
		}

		// Drill down selection listener
		if(type==4) {
			int selRow = drilldowns.get(index).tree.getRowForLocation(e.getX(), e.getY());
         	TreePath selPath = drilldowns.get(index).tree.getPathForLocation(e.getX(), e.getY());
			if(selRow != -1) {
				if(e.getClickCount() == 1) {
                	String keyvalue = drilldowns.get(index).getKey();
					System.out.println("DEBUG:\ndrilldown keyvalue = " + keyvalue);
					for(DReport report : reports) {
						if((report.ischild) && (keyvalue.length()>0)) {
							// get filter name and index and put on report
							report.putparams(drilldowns.get(index).filtername, keyvalue);
							report.drillReport();
							}
						}
					//testing drilling to forms
					for(DForm form : forms) {

						if((form.ischild) && (form.linkkey==subkey))		//if this is a child
							form.recChild(keyvalue);
						if((form.isinputfield) && (form.inputkey==subkey))	//if input should come from a grid
							form.linkinput(keyvalue);
						if((form.islookup) && (form.lookupkey==subkey))
							form.lookupinput(keyvalue);
						}

					for(DGrid grid : grids) {
						System.out.println("Grid found");
						if((grid.linkkey != 0) && (grid.linkkey==subkey))
							grid.makechild(keyvalue);
							}
					}
				}
			}

		// Calendar listener
		if(type==5) {
			String keyvalue = calendars.get(index).getKey();
			for(DForm form : forms) {
				if((form.ischild) && (form.linkkey==subkey))
					form.recChild(keyvalue);
				if((form.isinputfield) && (form.inputkey==subkey))
					form.linkinput(keyvalue);
			}
            for(DGrid grid : grids) {
            	if((grid.linkkey != 0) && (grid.linkkey == subkey))
					grid.makechild(keyvalue);
            }
			for(DReport report : reports) {
				//System.out.println("\n\t\tmouseClicked() type=5");
				if((report.ischild) && (keyvalue.length()>0) && (report.linkkey==subkey)) {
					report.putparams(calendars.get(index).filtername, keyvalue);
					report.drillReport();
				}
			}
		}
	}

    public void focusGained(FocusEvent e) {
		//System.out.println("Focus gained :" + e.getComponent().getParent().getParent().getName());
		if(e.getComponent().getParent().getParent().equals(toppane)) {
			setItem("istop");
			}
		else if(e.getComponent().getParent().getParent().equals(bottompane)) {
			setItem("isbottom");
			}
		else if(e.getComponent().getParent().getParent().equals(leftpane)) {
			setItem("isleft");
			}

		else if(e.getComponent().getParent().getParent().getParent().equals(toppane)) {
			setItem("istop");
			}
		else if(e.getComponent().getParent().getParent().getParent().equals(bottompane)) {
			setItem("isbottom");
			}
		else if(e.getComponent().getParent().getParent().getParent().equals(leftpane)) {
			setItem("isleft");
			}

		else if(e.getComponent().getParent().getParent().getParent().getParent().equals(toppane)) {
			setItem("istop");
			}
		else if(e.getComponent().getParent().getParent().getParent().getParent().equals(bottompane)) {
			setItem("isbottom");
			}
		else if(e.getComponent().getParent().getParent().getParent().getParent().equals(leftpane)) {
			setItem("isleft");
			}
	}

    public void focusLost(FocusEvent e) { }

	public void updateView(){
			System.out.println("UPDATING VIEW");
			for(DForm form : forms){
				if(form.ispopup)
					form.recChild(popupfilter);
				}
            for(DGrid grid : grids) {
            	if(grid.ispopup)
					grid.makechild(popupfilter);
				}
			}
}

