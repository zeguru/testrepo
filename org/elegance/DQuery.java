package org.elegance;

import java.sql.Connection;

import javax.swing.JInternalFrame;
import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JFileChooser;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DQuery extends JInternalFrame implements ActionListener {
	public boolean isVisible;
	JPanel buttonpanel, toppanel, bottompanel;
	JSplitPane splitpanel;
	JScrollPane tableview;
	JButton execute, export;
	JTextArea querytext;
	JFileChooser fc;

	DGrid grid;

	public DQuery(Connection db) {
		super("Query Builder", true, true, true, true);
		
		toppanel = new JPanel(new BorderLayout());
		bottompanel = new JPanel(new BorderLayout());
		buttonpanel = new JPanel(new GridLayout(1,2));

		splitpanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, toppanel, bottompanel);
		splitpanel.setOneTouchExpandable(true);
		splitpanel.setDividerLocation(150);

		execute = new JButton("Execute");
		export = new JButton("Export");
		execute.addActionListener(this);
		export.addActionListener(this);
		querytext = new JTextArea();


		buttonpanel.add(execute);
		buttonpanel.add(export);
		toppanel.add(buttonpanel, BorderLayout.PAGE_START);
		toppanel.add(querytext, BorderLayout.CENTER);		

		grid = new DGrid(db);	

		bottompanel.add(grid.panel, BorderLayout.CENTER);

		isVisible = false;

		add(splitpanel);

 		// Set the default size
       	setSize();
	}

  	public void setSize() {
        super.setLocation(30, 30);
        super.setSize(500, 500);
 	}

 	public void setVisible(boolean visible) {
        super.setVisible(visible);
		isVisible = visible;
  	}

	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Execute")) {
			
			String sql = querytext.getText();
			System.out.println("DEBUG: sql = " + sql);

			if(grid.tabledef!=null)
				grid.tabledef.createquery(sql);
			else	
				System.out.println("TableDef is NULL !!");

			System.out.println("DEBUG:After tabledef.createquery()");	

			grid.setlist();			
		}
		if(e.getActionCommand().equals("Export")) {
			savecvs();
		}
	}

	public void savecvs() { // export to cvs
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(this);
       	if (returnVal == JFileChooser.APPROVE_OPTION) {
 			String filename = fc.getSelectedFile().getAbsolutePath();
			grid.tabledef.savecvs(filename);
		}
	}
}