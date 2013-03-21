package org.elegance;

import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;

import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.JPanel;

import java.awt.Color;

import java.awt.Component;
public class SECellRenderer extends JLabel implements TreeCellRenderer {

	public SECellRenderer() {

		setOpaque(false);		
		
		//setBackground(Color.RED);		
	
		//setIcon(new ImageIcon("/usr/sesame/build/images/gif/alarm-16.gif"));	
		//renderer.setLeafIcon(new ImageIcon("/usr/sesame/build/images/gif/alarm-16.gif"));		
		//renderer.setClosedIcon(new ImageIcon("/usr/sesame/build/images/gif/alarm-16.gif"));
		//renderer.setOpenIcon(new ImageIcon("/usr/sesame/build/images/gif/alarm-16.gif"));

		//renderer.setFont();	
		//setTextSelectionColor(Color.GRAY);
		//setTextNonSelectionColor(Color.RED);

		}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		
		
		String stringValue = tree.convertValueToText(value, sel, expanded, leaf, row, hasFocus);
	
		setEnabled(tree.isEnabled());
		setText(stringValue);

		setFont(tree.getFont());


		//custom renderer
		if (leaf) {
		    			
			if (hasFocus){
				setForeground(Color.red);
				//setBackground(Color.yellow);
				setIcon(new ImageIcon("/usr/sesame/build/images/gif/calendar.gif"));	
				} 
			else{ 
				setForeground(Color.blue);
				//setBackground(Color.white);
				setIcon(UIManager.getIcon("Tree.leafIcon"));			
				}

			} 		
		else if (expanded) {
			setForeground(Color.orange);
			//setBackground(Color.blue);
		    setIcon(UIManager.getIcon("Tree.openIcon"));
			} 
		else {
			setForeground(Color.white);
			//setBackground(Color.blue);
		    setIcon(UIManager.getIcon("Tree.closedIcon"));
			}

		return this;
	}
}
