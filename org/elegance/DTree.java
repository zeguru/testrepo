package org.elegance;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;

import javax.swing.JTree;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;

import javax.imageio.ImageIO;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.awt.GridLayout;

public class DTree {

	public JPanel panel;
	public JScrollPane treeview;
	DTreeNode topnode;
	public DImageTree tree;

	private boolean issuperuser = false;
	private String rolename = "";

	//public List<DButton> button;	//list of buttons
	//Box bv;							//vertical box
	//Box bh;							//horizontal box

	public DTree(DElement lchild, Connection db, String userrole) {
        //try {
// 			// check for super user privillages
// 			String str = "SELECT superuser, rolename FROM users WHERE userid = getUserID()";
// 			Statement gst = db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
// 			ResultSet grs = gst.executeQuery(str);
// 			if(grs.next()) {
// 				if(grs.getBoolean("superuser"))
// 					issuperuser = true;
// 				else if (grs.getString("rolename")!=null)
// 					rolename = grs.getString("rolename");
// 				}
// 			grs.close();
// 			gst.close();
// 			}
// 		catch(SQLException ex) {
//             System.out.println("Security SQLException: " + ex.getMessage());
// 			}

		rolename = userrole;

		// Load Tree background image
		BufferedImage img = SesameUtil.createBufferImage("images/tree_bg.jpg");

		panel = new JPanel(new GridLayout(1,0));

		topnode = new DTreeNode(lchild.getAttribute("name"), lchild.getAttribute("key", ""));
       	tree = new DImageTree(img, topnode);

    	tree.setCellRenderer(new SECellRenderer());		//my custom cell renderer
    	treeview = new JScrollPane(tree);
		tree.setOpaque(false);
		panel.setOpaque(false);

		(treeview.getViewport()).setOpaque(false);

		panel.add(treeview);

        if(checkRole(lchild))
			addNode(lchild, topnode);

		//buttons test
		//button = new ArrayList<DButton>();		//list of buttons

		//bv = Box.createVerticalBox();
		//bh = Box.createHorizontalBox();

		//for each button
		//if(el.getName().equals("BUTTON")) {
		//bv.add(new DButton(el,panel,db,this));	//add
		//		}
		//panel.add(BorderLayout.EAST,bv);		//add the box to the panel
	}

	public void addNode(DElement lchild, DTreeNode lnode) {

		List<DElement> child = lchild.getElements();
		for(DElement el : child) {
			String noderole = el.getAttribute("role", "");
			boolean donode = false;
			if((noderole.equals("")) || (rolename.equals("superuser"))) 	//if no role defined for the node..or if this guy is superuser.......
				donode = true;
			else if (rolename.equals("")) 				//if the user's role isnt defined on the users table
				donode = false;
			else if (noderole.contains(rolename)) 		//if the element contains this users role in the comma separated list of roles...
				donode = true;

			if(checkRole(el)) {
				DTreeNode subnode = new DTreeNode(el.getAttribute("name"), el.getAttribute("key", ""));

				if(el.getAttribute("hide")==null){
					lnode.add(subnode);
					}
				else{
					System.out.println("Hidden Node. key = " + el.getAttribute("key"));
					}

				// Add the sub tree elements
				if(el.getContentSize()>0)
					addNode(el, subnode);

				}
			}
		}

	public boolean checkRole(DElement el) {
		boolean donode = false;
		String noderole = el.getAttribute("role", "");

		if((noderole.equals("")) || (rolename.equals("superuser")))
			donode = true;
		else if (rolename.equals(""))
			donode = false;
		else if (noderole.contains(rolename))
			donode = true;

		return donode;
	}

	// Create an icon
//     protected BufferedImage createBufferImage(String path) {
// 		java.net.URL imgURL = DContainer.class.getResource(path);
//         if (imgURL != null) {
// 			try {
// 				return ImageIO.read(imgURL);
// 				}
// 			catch (IOException ex) {
// 				System.out.println(ex);
// 				}
// 			}
// 		else {
//             System.err.println("Couldn't find file: " + path);
// 			}
// 		return null;
// 		}
}

