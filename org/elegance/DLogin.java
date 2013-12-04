package org.elegance;

import java.util.List;
import java.sql.*;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Box;
import java.awt.BorderLayout;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Image;

import java.io.IOException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DLogin implements ActionListener {

	private volatile Thread clockThread = null;

	public JLabel lblprogram, lbluser, lblpassword, lblmessage;
	public JComboBox cmbprogram;
	public JTextField txtuser;
	public JPasswordField txtpassword;
	public JButton btnok, btnclear;
	public static DImagePanel mainpanel;
	public static JPanel panel;
	public JPanel loginpanel;
	public JPanel [] p;
	private JLabel[] l;

	public List<DElement> programs;

	public static DBuild build;
	public DMail mail;
	BufferedImage img;

	public Connection db;
	String dir;

	//powered by
	Box bh;		//horizontal box
	JLabel sun;

	private static String loggedin;			//full name of the logged in user
	private static String currentUserId;		//id of currently logged in user
	public static String databasetype;
	public static String dbprefix;			//support for multitenancy (single DB multiple apps)
	static String application_dictionary;
	static String logindesk;
	static String loginsql;;

	//jLabel4.setIcon(new javax.swing.ImageIcon("/usr/sesame/build/images/sailing.jpg")); // NOI18N
	//jLabel4.setVerticalAlignment(javax.swing.SwingConstants.TOP);


	public DLogin(String ldir, String xmlfile) {
		// Open the runtime.XML File and read the program list
		DXML dxml = new DXML(ldir + xmlfile);
		DElement root = dxml.getRoot().getFirst();

		// This is the image used in the Login Window. Its not visible/apparent because we have super.setOpaque(true) in DImagePanel
		img = createBufferImage("images/logo.jpg");

		// Create all the objects
		dir = ldir;
		lblprogram = new JLabel("Module : ");
		lbluser = new JLabel("User Name : ");
		lblpassword = new JLabel("Password : ");
		lblmessage = new JLabel();

		cmbprogram = new JComboBox();
		txtuser = new JTextField(25);
		txtpassword = new JPasswordField(25);

		//setSize(w, h);
		//txtuser.setSize(150,30);
		//txtpassword.setSize(150,30);

		txtpassword.setActionCommand("Login");


		btnok = new JButton("Login");
		btnclear = new JButton("Clear");

		//POWERD BY
		bh = Box.createHorizontalBox();
		sun = new JLabel("SUN");

		//sun.setIcon(new javax.swing.ImageIcon("/usr/sesame/build/images/sailing.jpg")); // NOI18N
		//sun.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
		//sun.setMaximumSize(new Dimension(0,0,400,20));
		//sun.setForeground(Color.blue);
		//sun.setText("SUN");

		bh.add(sun);
		//bh.add(btnPrevious);
		//bh.add(btnNext);
		//bh.add(btnLast);

		loginpanel = new JPanel(new GridLayout(4, 2));
		panel = new JPanel(new GridLayout(4, 3));

		mainpanel = new DImagePanel(img, new GridLayout(1, 0));
		mainpanel.add(panel);

		// Create panel and add components
		p = new JPanel[12];
		for (int j=0;j<12;j++) {
			if(j != 4)
				p[j] = new JPanel(new GridLayout(1, 0));
			else
				p[j] = new JPanel(new GridLayout(2, 0));

			panel.add(p[j]);
			}

		//panel.add(bh);

		// add the objects to the panel
		p[4].add(loginpanel);
		p[4].add(lblmessage);
		loginpanel.add(lblprogram);
	 	loginpanel.add(cmbprogram);
		loginpanel.add(lbluser);
	 	loginpanel.add(txtuser);
		loginpanel.add(lblpassword);
	 	loginpanel.add(txtpassword);
		loginpanel.add(btnclear);
		loginpanel.add(btnok);

        programs = root.getElements();
        for(DElement program : programs)
			cmbprogram.addItem(program.getValue());
		cmbprogram.setSelectedIndex(0);

		btnclear.addActionListener(this);
		btnok.addActionListener(this);
		txtpassword.addActionListener(this);

		//testing cursor stuff
		//ActionListener cursorDoIt = CursorController.createListener(this, btnok.getActionListener());
		//btnok.addActionListener(cursorDoIt);

		// set focus to the text box
		txtuser.requestFocusInWindow();
	}

	public void actionPerformed(ActionEvent e) {
		if ("Clear".equals(e.getActionCommand())) {
			txtuser.setText("");
			txtpassword.setText("");
			}
		else if ("Login".equals(e.getActionCommand())) {
			DElement prog = (DElement) programs.get(cmbprogram.getSelectedIndex());

			String mailhost = prog.getAttribute("mailhost");
			String jaas = prog.getAttribute("jaas");
			String dbuser = prog.getAttribute("dbuser");
			String dbpasswd = prog.getAttribute("dbpasswd");
			String authserver = prog.getAttribute("authserver","localhost");
			String authtable = prog.getAttribute("authtable","users");  //AUth table must have at least the following: role column = rolename, user column = username, cred column=userpassword
			String org = prog.getAttribute("org","");	//example org="client_id:1"	where client_id = 1

			dbprefix = prog.getAttribute("dbprefix","");
			logindesk = prog.getAttribute("logindesk","");
			loginsql = prog.getAttribute("loginsql","");

			if(prog.getAttribute("databasetype")==null)
				databasetype = "postgres";
			else
				databasetype = prog.getAttribute("databasetype");

			System.out.println("DATABASE TYPE = " + databasetype);


			String password = new String(txtpassword.getPassword());

			boolean sysclear = false;
			boolean dbauth = true;
			if(mailhost != null) {
				sysclear = Mailconnect(mailhost);
				dbauth = false;
			}
			if(jaas != null) {
				System.setProperty("java.security.auth.login.config", jaas);
				sysclear = jaasAuth();
				dbauth = false;
			}

			if(dbpasswd != null)
				password = dbpasswd;


			if(dbauth) {
				sysclear = DBconnect(prog.getAttribute("dbclass"), prog.getAttribute("dbpath"), dbuser, password, prog.getAttribute("schema"), dir + prog.getAttribute("xmlfile"));
				}
			else if(sysclear) {//if already authenticated by other facilities
				sysclear = DBconnect(prog.getAttribute("dbclass"), prog.getAttribute("dbpath"), dbuser, password, prog.getAttribute("schema"), dir + prog.getAttribute("xmlfile"));
				}

			if (sysclear) {		//ie if ok to proceed
				String xmlfile = dir + prog.getAttribute("xmlfile");
				String reportpath = prog.getAttribute("reportpath", "");

				//Now authenticate using the users table
				//and get roles
				String userrole = getRole(txtuser.getText(), txtpassword.getText(), org);
				//System.out.println("FOUND USER ROLE = " + userrole);
				if (userrole != null){
				      build = new DBuild(xmlfile, db, reportpath, userrole);
				      panel.setVisible(false);
				      mainpanel.remove(panel);
				      mainpanel.add(build.mainpanel);

				      //clear
				      txtpassword.setText("");
				      txtuser.setText("");
				      }
				else{		//user with matching username and password
				    lblmessage.setText("Incorrect Username/Password. Please try again.");
				    //lblmessage.setText("getRole failed");
				    }

			} else {
				lblmessage.setText("Could Not Connect To Database");
			}
		}
	}

	public boolean DBconnect(String dbclass, String dbpath, String usr, String password, String schema, String xmlfile) {

		boolean connected = false;


		DXML dxml = new DXML(xmlfile);
		DElement root = dxml.getRoot().getFirst();

		application_dictionary = root.getAttribute("dict");		//application dictionary
		String xml_app_name = root.getAttribute("name");
		String db_app_name;

		//to accommodate existing systems that do not have this variable
		String dbuser = txtuser.getText();		//from now on the default db user should be sesame
		if(usr != null){	//if db user is defined in runtime.xml
		    dbuser = usr;
		    }

		try {
			Class.forName(dbclass);
			db = DriverManager.getConnection(dbpath, dbuser, password);
			connected = true;
			if(schema != null) {
				Statement exst = db.createStatement();
				exst.execute("ALTER session set current_schema=" + schema);
				exst.close();
				}

			if(application_dictionary != null){

				String sql = "";

				//check expiry info
				if(databasetype.equals("postgres")){

				      sql = "SELECT application_name,application_verson,expiry_date,eol_md5 FROM " + (dbprefix.equals("")?"":dbprefix) + application_dictionary + "";
				      sql += " WHERE md5(to_char(expiry_date,'YYYY-Mon-DD')) = eol_md5 ";	//make sure the expiry date was not changed
				      sql += " AND (expiry_date >= current_date)";
				      sql += " ORDER BY " + application_dictionary + "_id LIMIT 1";
				      }
				else if(databasetype.equals("mysql")){
				      sql = "SELECT application_name,application_verson,expiry_date,eol_md5 FROM " + (dbprefix.equals("")?"":dbprefix) + application_dictionary + "";
				      sql += " WHERE md5(expiry_date) = eol_md5 ";	//make sure the expiry date was not changed
				      sql += " AND (expiry_date >= current_date)";
				      sql += " ORDER BY " + application_dictionary + "_id LIMIT 1";
				      }

				  //System.out.println("APPLICATION dictionary sql = " + sql);
				  Statement stmt = db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				  ResultSet rs = stmt.executeQuery(sql);

				  if(rs.next()){
				    //then confirm the app name has not been changed in the XML
				    db_app_name = rs.getString("application_name");
				    if(!db_app_name.equals(xml_app_name)){
					  System.out.println("EOL");	//APP NAME was changed
					  connected = false;
					  }
				    }
				  else{
					System.out.println("EOL");	//System EOL reached
					connected = false;
				      }

				}

			}
		catch (ClassNotFoundException ex) {
			System.err.println("Could not find the database driver classes.");
			System.err.println(ex);
			connected = false;
			}
		catch (SQLException ex) {
			System.err.println("Cannot connect to this database.");
			System.err.println(ex.getMessage());
			connected = false;
			}

		return connected;
		}


	public boolean Mailconnect(String host) {
		boolean connected = false;

		if(!host.equals("")) {
			String password = new String(txtpassword.getPassword());
			mail = new DMail(host, txtuser.getText(), password);
			if(mail.folder != null) connected = true;
		}

		return connected;
	}

	public boolean jaasAuth() {
		String username = txtuser.getText();
		char[] password = txtpassword.getPassword();

		DAuth auth = new DAuth();
		auth.setAuth(username, password);
		boolean is_logged_in = auth.doLogin();

		return is_logged_in;
	}

	// Create an icon
    protected BufferedImage createBufferImage(String path) {
		java.net.URL imgURL = DContainer.class.getResource(path);
        if (imgURL != null) {
			try {
				return ImageIO.read(imgURL);
				}
			catch (IOException ex) {
				System.out.println(ex);
			}
			}
		else {
            System.err.println("Image path not defined");
		}

		return null;
    }



  public String getRole(String user, String pwd, String g){
	String role = null;
	String fullname = null;
	try {
	    // check for super user privillages
	    String sql;
	    if(application_dictionary == null){ //backward compatibility OLD SYSTEM - before 2013
		if (user.equals("root") && pwd.equals(""))		//this should never be used any more
		    sql = "SELECT user_id as user_id, is_super_user as superuser, role_name as rolename, full_name as fullname FROM " + (dbprefix.equals("")?"":dbprefix) + "users WHERE UPPER(user_name) = UPPER('" + user + "')";
		else
		    sql = "SELECT userid as user_id, superuser, rolename, fullname FROM " + (dbprefix.equals("")?"":dbprefix) + "users WHERE UPPER(username) = UPPER('" + user + "') AND userpasswd = MD5('" + pwd + "')";
		}
	    else{//NEW generation
		sql = "SELECT user_id, is_super_user as superuser, role_name as rolename, full_name as fullname FROM " + (dbprefix.equals("")?"":dbprefix) + "users WHERE UPPER(user_name) = UPPER('" + user + "') AND user_passwd = MD5('" + pwd + "')";
		}

	    if(!g.equals("")){
		sql += " AND " + g;
		}

	    //System.out.println("ROLE SQL = " + sql);

	    Statement stmt = db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	    ResultSet rs = stmt.executeQuery(sql);

	    if(databasetype.equals("mysql"))
		System.out.println("getting role");
	    else if(databasetype.equals("postgres"))
		rs.absolute(0);
		//System.out.println("getting role");


	    if(rs.next()) {
		//role check
		if(rs.getBoolean("superuser"))
		    role = "superuser";
		else if (rs.getString("rolename")!=null)
		    role = rs.getString("rolename");


		if(rs.getString("user_id")!=null)
		    currentUserId = rs.getString("user_id");

		fullname = rs.getString("fullname");

		}

	    //System.out.println("ROLE here = " + role);

	    loggedin = fullname;

	    rs.close();
	    stmt.close();
	    }
	catch(SQLException ex) {
	    System.out.println("Security SQLException: " + ex.getMessage());
	    role = null;
	    }

	return role;

	}


    public static void logOut(){
	  System.out.println("at DLogin Logging Out..");
	  mainpanel.remove(build.mainpanel);
	  mainpanel.add(panel);
	  panel.setVisible(true);
	}
    public static String getLoggedInUser(){
	    return loggedin;
	  }
    public static String getAppDictionary(){
	  return application_dictionary;
	  }
    public static String getCurrentUserId(){
	    return currentUserId;
	  }

}

