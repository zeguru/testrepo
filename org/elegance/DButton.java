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
import java.awt.Desktop;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import bsh.*;		//Bean Shell stuff

public class DButton {

	private Connection conn;
	private String function;

	public JPanel panel;
	public List<JButton> buttons;
	public List<JLabel> labels;

	public JButton button;
	private DElement ell;

	public int linkkey;
	public boolean ischild;
	private String linkfield, linkdata;
	private Map<String, String> statements;
	private boolean isString;
	private String parameters;
	private String aftermath;		//action to be done after successfull button operation

	public DImageDesktop dtop;
	public List<DContainer> cont;

	int x,y,w,h;
	private String target,javaclass,args, mainclass, javapackage,jarfile,deskkey,cmd;
	private String title,tooltip;

	JPanel p;
	DForm form;
	Connection con;

	public DButton(DElement el,JPanel lpanel, Connection db, DForm fm) {

		ell=el;
		form=fm;
		con=db;

		System.out.println("DEBUG: @ DButton constructor" );

		p=lpanel;

		x = Integer.valueOf(el.getAttribute("x")).intValue();
		y = Integer.valueOf(el.getAttribute("y")).intValue();
		w = Integer.valueOf(el.getAttribute("w")).intValue();
		h = Integer.valueOf(el.getAttribute("h")).intValue();

		title = el.getAttribute("title","No Title");
		//notitle = el.getAttribute("notitle");

		if(el.getAttribute("tooltip")!=null)
			tooltip = el.getAttribute("tooltip",title);

		target = el.getAttribute("target");
		cmd = el.getAttribute("command");
		javaclass = el.getAttribute("javaclass");
		args = el.getAttribute("args");
		aftermath = el.getAttribute("aftermath");

		initButton();
		}

	public void initButton(){
		button = new JButton(title);
		if(tooltip!=null) button.setToolTipText(tooltip);
		button.setActionCommand(target);

		//listener
		button.addActionListener(new java.awt.event.ActionListener() {		//Listener..
		    public void actionPerformed(java.awt.event.ActionEvent evt) {	//..for this event (click)
			buttonActionPerformed(evt);										//..call this method
		    }
		});

		p.add(button);
		setPos();
		}

	public void setPos() {
		button.setLocation(x,y);
    	button.setSize(w,h);
		}

	public String execproc(String query) {

		//psql -d imlu -U root -c 'select * from users'
		//query = "psql -d imlu -U root -c '" + query + "';";

		System.out.println(query);

 		// Execute the procedure
		  try {
 			Statement smt = con.createStatement();
 			ResultSet rs = smt.executeQuery(query);

			//need to display return value of the funciton

 			rs.close();
 			smt.close();

			return ell.getAttribute("successmessage","Operation Successfull");

			}
		catch(SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
			return ex.getMessage();
			}
		}




	public void buttonActionPerformed(ActionEvent e) {

		try{
			String action = e.getActionCommand();
			//action event
			if(action.compareTo("java")==0){
				//execute java class
				//if(args == null)
					loadClass(javaclass);
				//else
				//	loadClass(javaclass,args);
				}
			if(action.compareTo("jar")==0){
				//run executable java archive
				//runCommand("java -Xmx32m -jar " + javaclass);
				loadJarFile(ell.getAttribute("jarfile"));
				}
			if(action.compareTo("desk")==0){
				//load baraza desk node
				openDesk(ell.getAttribute("desk"));
				}

			if(action.compareTo("bsh")==0){
				//load baraza desk node
				runBeanShellScript(ell.getAttribute("script"));
				}

			if(action.compareTo("system")==0){
				runCommand(cmd);
				}
			if(action.compareTo("sql")==0){	//we need to login to the database before
				String sql = "select ";
				System.out.println("At sql action");
				function = ell.getAttribute("function");
				if(ell.getAttribute("parameters")!=null){
					parameters = ell.getAttribute("parameters");	//these are the names of the value placeholders >x<
					String[] para = parameters.split(",");
					sql += function + "(";
					for(int i=0;i<para.length-1;i++){
						sql += form.getData(para[i]) + ",";
						}
					sql += form.getData(para[para.length-1]) + ")";
					}
				else{
					sql += function + "()";
					}


				//DBMS dependancy
				if(DLogin.databasetype.compareToIgnoreCase("oracle")==0)
					sql += " from dual";

				System.out.println("function statement = " + sql);


	//   			linkkey = 0;
	//   			ischild = false;
	//   			if(!el.getAttribute("linkkey", "").equals("")) {
	//   				ischild = true;
	//   				linkfield = el.getAttribute("linkfield");
	//   				linkkey = Integer.valueOf(el.getAttribute("linkkey")).intValue();
	//  				}
	//
	// 			String sql = "SELECT " + statement;
	// 			if(isString) sql += "('" + linkdata + "');";
	// 			else sql += "(" + linkdata + ");";
	//
				String sqlreturn = execproc(sql);
				System.out.println("SQL FUNCTION returned " + sqlreturn);

				//call refresh
				form.getParent().refreshGrids();


				//form.hide();
				//p.dispose ();
				//p.setVisible(false);
				  if(aftermath.equals("hide")){
				    form.getParent().setVisible(false);
				    }
				  else if(aftermath.contains("desk")){
				      String key = aftermath.split("=>")[1];
				      form.getParent().setVisible(false);
				      openDesk(key);
				      }

				}

			//call refresh
			//form.getParent().refreshGrids();
			}
		catch(Exception ex){
			System.out.println("SQLException: " + ex.getMessage());
			JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	* Inspired by: http://www.beanshell.org/examples/callscript.html
	*/
	public boolean runBeanShellScript(String _script){

		try {
		    Object obj = new bsh.Interpreter().source(_script);
		    return true;
		    }
		catch(FileNotFoundException e){
		    System.out.println("Script file missing: " + e.getMessage());
		    return false;
		    }
		catch(IOException exxx){
		    System.out.println("IO Exception: " + exxx.getMessage());
		    return false;
		    }
		catch (TargetError ex ) {
		    System.out.println("The script or code called by the script threw an exception: " + ex.getTarget() );
		    return false;
		    }
		catch (EvalError exx ) {
		    System.out.println("There was an error in evaluating the script:" + exx );
		    return false;
		    }
	    }



	public boolean openDesk(String key){

		System.out.println("\nDEBUG: at openDesk() key = " + key);
		int activeform = -1;

		activeform = DBuild.keylist.indexOf(key);

		cont = DBuild.container;
		dtop = DBuild.desktop;

		if(activeform < 0)
			return false;

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






	//this command runs on the client side...!!!!! not very useful for now
	public boolean runCommand(String cmd){

			System.out.println("DEBUG @ runCommand() cmd = : " + cmd);

			try{
				Runtime rt = Runtime.getRuntime();
				Process p = rt.exec(cmd);

				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));

				// read the output from the command
				String s;
				System.out.println("Begin: "+ cmd);
				while ((s = in.readLine()) != null) {
					System.out.println(s);
					}
				System.out.println("End Command Output: " + cmd);


					//InputStream in = p.getInputStream() ;
					//OutputStream out = p.getOutputStream ();
					//InputStream err = p.getErrorStream() ;

// 					String line;
// 					while((line = in.read()) != null) {
// 						System.out.println(line);	//alternative is to launch a log window on baraza
// 						}

 					int exitval=0;
					try {
						exitval = p.waitFor();
						}
					catch(InterruptedException e) {
						// Handle exception that could occur when waiting
						// for a spawned process to terminate
						System.out.println("Exited with error code " + exitval);
						return false;
						}

					p.destroy() ;
					}
				catch(Exception e){
					e.printStackTrace();
					return false;
					}
		return true;
		}


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


	//overloaded
// 	public void loadClass(String classname, String[] args){
// 		try{
// 			Class oclass = Class.forName(classname);
// 			Object obj = oclass.newInstance();
//
// 			// Create a class Christmas object, with an interface HolInfo reference
// 			//HolInfo holidayDelegate = (HolInfo)( Class.forName( className ).newInstance() );
//
// 			// Execute methods of the Christmas object.
// 			//String celebrated = holidayDelegate.getHowCelebrated();
// 			}
// 		catch(ClassNotFoundException e){
// 			System.out.println(e.getMessage());
// 			}
// 		catch(InstantiationException ex){
// 			System.out.println(ex.getMessage());
// 			}
// 		catch(IllegalAccessException exx){
// 			System.out.println(exx.getMessage());
// 			}
// 		}

	public void loadJarFile(String jarfile){
		try{
			 //Desktop.getDesktop().open(new File("/root/Download/Java/JavaNote/JavaNotePad.jar"))
			Desktop.getDesktop().open(new File(jarfile));
			}
		catch(Exception exx){
			System.out.println("SQLException: " + exx.getMessage());
			JOptionPane.showMessageDialog(panel, exx.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
			}
		}


}

