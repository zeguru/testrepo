package org.elegance;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;

import java.awt.GridLayout;

public class DDrillDown {
	
	public JPanel panel;
	public JScrollPane treeview;
	public DTreeNode topnode;
	DefaultTreeModel treemodel;
	public JTree tree;
	public String filtername;
	public DElement mainfielddef;

	Connection db;

	public DDrillDown(DElement fielddef, Connection ldb) {
		db = ldb;
		mainfielddef = fielddef;

		panel = new JPanel(new GridLayout(1,0));

		topnode = new DTreeNode(fielddef.getAttribute("name"), "");		
		treemodel = new DefaultTreeModel(topnode);

       	tree = new JTree(treemodel);

    	treeview = new JScrollPane(tree);
		panel.add(treeview);

		createtree();
	}

	public void createtree() {
		topnode.removeAllChildren();
		addNode(mainfielddef, topnode, "");
		treemodel.reload();
	}

	public void addNode(DElement fielddef, DTreeNode lnode, String wherekey) {

		filtername = fielddef.getAttribute("filtername", "");
		if(filtername.equals("")) filtername = "filterid";
        String keyfield = fielddef.getAttribute("keyfield");
		String listfield = fielddef.getAttribute("listfield");
		String sortfield = fielddef.getAttribute("sortfield", "");
		String wheresql = fielddef.getAttribute("wheresql");
		if(sortfield.equals("")) sortfield = listfield;
		String wherefield = fielddef.getAttribute("wherefield");
        String sql = "SELECT " + keyfield + ", " + listfield;
        sql += " FROM " + fielddef.getAttribute("table");

		if(wheresql == null) {
			if(wherefield != null) sql += " WHERE " + wherefield + " = '" + wherekey + "'";
		} else {
			sql += " WHERE " + wheresql;
			if(wherefield != null) sql += " AND " + wherefield + " = '" + wherekey + "'";
		}
		
		sql += " ORDER BY " + sortfield;

        // create a record set
        try {
        	Statement st = db.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery(sql);
			
			while(rs.next()) {
				DTreeNode subnode = new DTreeNode(rs.getString(listfield), rs.getString(keyfield));
                lnode.add(subnode);

				// Add the sub tree elements
				List<DElement> child = fielddef.getElements();
                for(DElement el : child) addNode(el, subnode, rs.getString(keyfield));
			}
     	} catch(SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
			JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
        }
	}

	public String getKey() {
		DTreeNode node = (DTreeNode)tree.getLastSelectedPathComponent();

		if (node == null) return "";
		if (!node.isLeaf()) return "";

		return node.key;
	}
}
