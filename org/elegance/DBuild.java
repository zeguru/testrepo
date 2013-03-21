package org.elegance;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import javax.swing.JPanel;

import javax.swing.JDesktopPane;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.*;

import javax.swing.border.*;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.JButton;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.net.URL;
import javax.swing.JTree;

import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;

import javax.swing.event.InternalFrameListener;
import javax.swing.event.InternalFrameEvent;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;

public class DBuild implements TreeSelectionListener, ActionListener, Runnable, InternalFrameListener  {
	public DElement root;

	public Connection dbmain;

	public JPanel mainpanel, menupanel, buttonpanel;
	public LogoPanel logopanel;

	//used by openDesk() method
	public DImageDesktop dtop;
	public List<DContainer> cont;

	public JSplitPane splitpane,vsplitpane,vvsplit;
	public static DImageDesktop desktop;
	private static final int GAP = 5;
	JScrollPane deskscroll;

	public static List<DContainer> container;

	private List<DElement> children;
	public static List<String> keylist;

	private AboutSesame aboutsesame;

	private DAboutBox baboutbox;
	private DChangePass bchangepass;
	public DTree btree;
	public DMenu bmenu;
	public DToolBar btoolbar;
	public DMessageBar bmessagebar;
	public DQuery bquery;

	//JButton btnFour, btnThree, btnTwo, btnOne;
	public JButton[] buttons;
	private String[] buttonactions;

	public static String databasetype;

	private int activeform;

	private Thread updateThread = null;
	private String reportpath;

	private static String application_dictionary;		//table holding application specific data eg. version, installation (in case of multiple db installations)

	public DBuild(String xmlfile, Connection db, String lreportpath, String userrole) {
		dbmain = db;
		reportpath = lreportpath;

		activeform = -1;

		// XML Processor for the application XML configurations
		// Open the XML File and read the program list
		DXML dxml = new DXML(xmlfile);
		DElement root = dxml.getRoot().getFirst();
/*
		if(root.getAttribute("databasetype")==null)
			databasetype = "postgres";
		else
			databasetype = root.getAttribute("databasetype");

		System.out.println("DATABASE TYPE = " + databasetype);
*/

		// Create the menus
		container = new ArrayList<DContainer>();
		children = root.getElements();

		String bgimage = "images/logo.jpg";
		String clientlogo = "images/clientlogo.jpg";
		String buttonlabels = "Network Status,Server Config"; //reports, control panel, system config
		String buttontargets = "system=>ping 127.0.0.1,desk=>1";
		String buttonroles = "";//"all,admin";

		//for each element
		for(DElement el : children) {
			if(el.getName().equals("TREE")){
				if(el.getAttribute("background") != null)
					bgimage = el.getAttribute("background");
				if(el.getAttribute("clientlogo") != null)
					clientlogo = el.getAttribute("clientlogo");
				if(el.getAttribute("buttonlabels") != null)
					buttonlabels = el.getAttribute("buttonlabels");
				if(el.getAttribute("buttontargets") != null)
					buttontargets = el.getAttribute("buttontargets");
				//if(el.getAttribute("buttonroles") != null)

				buttonroles = el.getAttribute("buttonroles");
				btree = new DTree(el, db, userrole);			//do this for TREE element(s)
				}
			}

		// Create all other things
		mainpanel = new JPanel(new BorderLayout());
		menupanel = new JPanel(new GridLayout(2, 0));
		logopanel = new LogoPanel(clientlogo);		//for clients logo


		String[] labels = buttonlabels.split(",");		//get the labels as an array
		buttons = new JButton[labels.length];
		String[] targets = buttontargets.split(",");
		String[] btnroles = null;
		boolean ignore_roles = false;

		try{
		    btnroles = buttonroles.split(",");
		    if(btnroles != null){
			btnroles = buttonroles.split(",");	//we need this again?
			//if roles are not enough
			if (btnroles.length != labels.length){
			    ignore_roles = true;
			    }
			}
		    else{
			ignore_roles = true;
			}
		    }
		catch(NullPointerException e){
		      ignore_roles = true;
		      System.out.println("NPE");
		      }

		System.out.println("IGNORE ROLES = " + ignore_roles);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(buttons.length, 1, GAP, GAP));

		for(int i=0; i<buttons.length;i++){
			//if userrole is superuser, if button role is all, or if userrole and button role are matching or if btnroles has nothing (backward compatibility
			//  || ){
			if (ignore_roles==true || userrole.equals("superuser") || btnroles[i].compareToIgnoreCase("all")==0 || btnroles[i].compareToIgnoreCase(userrole)==0){
			    //System.out.println("Button role = " + btnroles[i] + " USER role = " + userrole);
			    buttons[i] = new JButton(labels[i]);		//add a new button
			    buttonPanel.add(buttons[i]);				//add button to panel
			    //buttons[i].setActionCommand(targets[i].split("=")[0]);		//get the left side of '='
			    buttons[i].setActionCommand(targets[i]);		//get the left side of '='
			    buttons[i].addActionListener(this);		//uses button text as the default action command
			    }
			}

		bmenu = new DMenu();
		btoolbar = new DToolBar();
		bmessagebar = new DMessageBar();
		bquery = new DQuery(db);
		baboutbox = new DAboutBox(root.getAttribute("name"));

		aboutsesame = new AboutSesame(root.getAttribute("name","Open Sesame"));

		bchangepass = new DChangePass(db);
		//desktop = new DImageDesktop(SesameUtil.createBufferImage("images/logo.jpg"));
		desktop = new DImageDesktop(SesameUtil.createBufferImage(bgimage));

		desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		desktop.setPreferredSize(new Dimension(700, 600));

		deskscroll = new JScrollPane(desktop);

		vvsplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, btree.panel, buttonPanel);
		vvsplit.setDividerLocation(350);

		vsplitpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, logopanel, vvsplit);
		vsplitpane.setDividerLocation(120);
		//vsplitpane.setOneTouchExpandable(true);



		splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, vsplitpane, deskscroll);
		splitpane.setDividerLocation(250);
		splitpane.setOneTouchExpandable(true);

		//BufferedImage img = createBufferImage("images/sun.jpg");

		mainpanel.add(menupanel, BorderLayout.PAGE_START);

		mainpanel.add(splitpane, BorderLayout.CENTER);
		mainpanel.add(bmessagebar.panel, BorderLayout.PAGE_END);

		menupanel.add(bmenu.menubar);
		menupanel.add(btoolbar.toolbar);

		//++logopanel.add(img); 	//company logo on top left

		// Toolbar button event listener
		for(int j=0;j<13;j++)
			btoolbar.button[j].addActionListener(this);

		// Menu event listner
		for(int j=0;j<18;j++)
			bmenu.menuitem[j].addActionListener(this);

		// Make a tread available
		if (updateThread == null)
			updateThread = new Thread(this, "Main Thread");
		updateThread.start();
	}

	public void run() {
		System.out.println("Starting....");
		Thread myThread = Thread.currentThread();
		keylist = new ArrayList<String>();

		bmessagebar.setmaxbar(children.size());
		int i = 0;
		int j = 0;
		for(DElement el : children) {
			if(el.getName().equals("DESK")) {
				container.add(new DContainer(el, dbmain, reportpath, Integer.toString(j)));
				container.get(j).addInternalFrameListener(this);
				keylist.add(el.getAttribute("key", ""));
				j++;
				}

			//System.out.println(el.getAttributeValue("name") + " - ok");
			bmessagebar.showMessage("Loading... " + el.getAttribute("name") + " - ok");
			bmessagebar.setbarvalue(++i);

			try {
			    Thread.sleep(100);
			    }
			catch (InterruptedException e) {
			    System.out.println("");
			    }

			}



		// Show application start message
		bmessagebar.showMessage(DLogin.getLoggedInUser());
		//bmessagebar.showMessage("Ready");

		// Listen to the Tree events
		btree.tree.addTreeSelectionListener(this);


		//if loginsql is not empty
		if(!DLogin.loginsql.equals("")){
		    String res = "";
		    String query = "SELECT ";
		    if(DLogin.databasetype.equals("postgres")){
			query += DLogin.loginsql;
			}
		    else if(DLogin.databasetype.equals("mysql")){
			query += DLogin.loginsql;
			}
		    else if(DLogin.databasetype.equals("oracle")){
			query += DLogin.loginsql;
			query += " FROM dual";
			}
		    res = execproc(query);
		    if (!res.equals("OK"))
			bmessagebar.showMessage(res);
		    }

		//if logindesk is not empty
		if(!DLogin.logindesk.equals(""))
		  openDesk(DLogin.logindesk);


		updateThread = null;		// This is to stop the thread
	}

	// Tree action listener
    public void valueChanged(TreeSelectionEvent e) {
    	DTreeNode node = (DTreeNode)btree.tree.getLastSelectedPathComponent();

		if (node == null) return;

		activeform = keylist.indexOf(node.key);

		if(activeform < 0) return;

		if(!container.get(activeform).started) {		//if form is not started...
			container.get(activeform).makeContainer();	//make another container
			}

		if(!container.get(activeform).isVisible) {		//if form is not visible
			desktop.add(container.get(activeform));
			container.get(activeform).setVisible(true);
			}
		else {
			try {
        		container.get(activeform).setSelected(true);
				if(container.get(activeform).isIcon()) {
					container.get(activeform).setIcon(false);
					}
				}
			catch (java.beans.PropertyVetoException err) {
				activeform = -1;
				}
			}

		btree.tree.clearSelection();
    }

    public void internalFrameClosing(InternalFrameEvent e) {  }
    public void internalFrameClosed(InternalFrameEvent e) { }
    public void internalFrameOpened(InternalFrameEvent e) { }
    public void internalFrameIconified(InternalFrameEvent e) { }
    public void internalFrameDeiconified(InternalFrameEvent e) { }
    public void internalFrameDeactivated(InternalFrameEvent e) { }

    public void internalFrameActivated(InternalFrameEvent e) {
		// System.out.println("Internal frame activated " + e.getInternalFrame().getTitle() +  " Name : " + e.getInternalFrame().getName());
		activeform = Integer.valueOf(e.getInternalFrame().getName()).intValue();
		}

	// Menu Bar (+ buttonPanel) button listening
	public void actionPerformed(ActionEvent e) {
		String cmd;

		if(e.getActionCommand().contains("="))	//if there is an equal sign. this comes from button panel
			cmd = e.getActionCommand().split("=")[0];
		else
			cmd = e.getActionCommand();


		//System.out.println("DBuild Action Command = " + cmd);

		if(activeform>-1){
			if(cmd.compareTo("Export")==0)
				container.get(activeform).command(9);
			if(cmd.compareTo("Refresh")==0)
				container.get(activeform).command(6);
			if(cmd.compareTo("Filter")==0)
				container.get(activeform).command(7);
			}

		//Button Panel
		if(cmd.compareTo("java")==0){
			//execute java class
			//if(args == null)
				loadClass(e.getActionCommand().split("=>")[1]);		//get string on the right side of '>'
			//else
			//	loadClass(javaclass,args);
			}
		if(cmd.compareTo("jar")==0){
			//run executable java archive
			//runCommand("java -Xmx32m -jar " + javaclass);
			loadJarFile(e.getActionCommand().split("=>")[1]);
			}
		if(cmd.compareTo("desk")==0){
			//load baraza desk node
			openDesk(e.getActionCommand().split("=>")[1]);	//get string on the right side of '>'
			}
		if(cmd.compareTo("system")==0){
			//runCommand(e.getActionCommand().split(">")[1]);	//get string on the right side of '>'
			new RunCommandThread(e.getActionCommand().split("=>")[1]).start();
			}

		if(cmd.compareTo("Sticky Note")==0) {
			//desktop.add(new StickyNote());
			new StickyNote();
			}
		if(cmd.compareTo("Calculator")==0) {
			new Calculator();
			}
		if(cmd.compareTo("Notepad")==0) {
			new Notepad();
			}
		if(cmd.compareToIgnoreCase("Exit")==0) {
			System.exit(0);
			}
		if(cmd.compareToIgnoreCase("Logout")==0) {
			System.out.println("Logging out");
			//container.get()
			}


		//change password
		if(cmd.compareToIgnoreCase("Change Password")==0) {
			if(!bchangepass.isVisible) {
				desktop.add(bchangepass);
				bchangepass.setVisible(true);
			} else {
				try {
        			bchangepass.setSelected(true);
    			} catch (java.beans.PropertyVetoException err) {
					activeform = -1;
				}
			}
		}

		//Query
		if(cmd.compareToIgnoreCase("Query")==0) {
			if(!bquery.isVisible) {
				desktop.add(bquery);
				bquery.setVisible(true);
				}
			else {
				try {
        			bquery.setSelected(true);
    			} catch (java.beans.PropertyVetoException err) {
					activeform = -1;
				}
			}
		}

		//Test
		if(cmd.compareToIgnoreCase("About Sesame")==0) {
			if(!aboutsesame.isVisible) {
				desktop.add(aboutsesame);
				aboutsesame.setVisible(true);
			} else {
				try {
        			aboutsesame.setSelected(true);
    			} catch (java.beans.PropertyVetoException err) {
					activeform = -1;
				}
			}
		}

   	}


//open an XML desk
public boolean openDesk(String key){

		System.out.println("\nDEBUG: at openDesk() key = " + key);
		int activeform = -1;

		activeform = DBuild.keylist.indexOf(key);

		cont = this.container;
		dtop = DBuild.desktop;

		if(activeform < 0) return false;

		if(!cont.get(activeform).started) {		//if form is not started...
			cont.get(activeform).makeContainer();	//make another container
			}

		if(!cont.get(activeform).isVisible) {		//if form is not visible
			dtop.add(cont.get(activeform));
			cont.get(activeform).setVisible(true);
			}
		else {
			try {
        		cont.get(activeform).setSelected(true);
				if(cont.get(activeform).isIcon()) {
					cont.get(activeform).setIcon(false);
					}
				}
			catch (java.beans.PropertyVetoException err) {
				activeform = -1;
				}
			}

		//btree.tree.clearSelection();

		return true;
		}

//load java class
	public void loadClass(String classname){
		try{
			Class oclass = Class.forName(classname);
			Object obj = oclass.newInstance();

			// Create a class Christmas object, with an interface HolInfo reference
			//HolInfo holidayDelegate = (HolInfo)( Class.forName( className ).newInstance() );

			// Execute methods of the Christmas object.
			//String celebrated = holidayDelegate.getHowCelebrated();
			}
		catch(ClassNotFoundException e){
			System.out.println(e.getMessage());
			}
		catch(InstantiationException ex){
			System.out.println(ex.getMessage());
			}
		catch(IllegalAccessException exx){
			System.out.println(exx.getMessage());
			}
		}

	//load executable jar
	public static void loadJarFile(String jarfile){
		try{
			 //Desktop.getDesktop().open(new File("/root/Download/Java/JavaNote/JavaNotePad.jar"))
			Desktop.getDesktop().open(new File(jarfile));
			}
		catch(Exception exx){
			System.out.println(exx.getMessage());
			}
		}

	public String execproc(String query) {

		//psql -d imlu -U root -c 'select * from users'
		//query = "psql -d imlu -U root -c '" + query + "';";

		System.out.println(query);

 		// Execute the procedure
		  try {
 			Statement smt = dbmain.createStatement();
 			ResultSet rs = smt.executeQuery(query);

			//need to display return value of the funciton

 			rs.close();
 			smt.close();

			return "OK";

			}
		catch(SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			return ex.getMessage();
			}
		}


	public static String getAppDictionary(){
	    return application_dictionary;
	    }



}//END OF CLASS DBUILD



//ANONYMOUS CLASS
class RunCommandThread extends Thread{
			private String cmd;

			public RunCommandThread(String c){
				//setDaemon( true );		//not good for applets
				System.out.println("DEBUG: @RunCommand. Thread has been created");
				cmd = c;
				}

			public void run(){
				System.out.println("\t\tThread Started: command = " + cmd);

				try{
					Runtime rt = Runtime.getRuntime();
					Process p = rt.exec(cmd);

					BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
					BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));

					// read the output from the command
					String s;
					System.out.println("Begin:" + cmd + "\n");

					while ((s = in.readLine()) != null) {
						System.out.println(s);
						}
					System.out.println("End:");

					int exitval=0;
					try {
						exitval = p.waitFor();
						}
					catch(InterruptedException e) {
						// Handle exception that could occur when waiting
						// for a spawned process to terminate
						System.out.println("Exited with error code " + exitval);
						return;
						}
					p.destroy();
					return;
					}
					catch(Exception e){
						e.printStackTrace();
						return;
					}
				}
		}

