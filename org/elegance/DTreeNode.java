package org.elegance;

import javax.swing.tree.DefaultMutableTreeNode;

public class DTreeNode extends DefaultMutableTreeNode {

	public String key;
	public DTreeNode(String lname, String lkey) {
	      super(lname);
	      key = lkey;
	      }

      //used by DAttributeTable
      public DElement dkey;
      public DTreeNode(String lname, DElement dk) {
		super(lname);

		this.dkey = dk;
	}
}
