package org.elegance;
/*
* Used for file upload purposes
*/
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.awt.*;
import java.awt.event.*;
import javax.imageio.*;

import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class DFile extends DField implements ActionListener { 

	//public JPanel datahold, bpanel;		//.... and button panel
	public JLabel filelabel;				//(initialy)displays the file path. later will store some description eg ibrahim's cv, 
	public JLabel attachfile,clearfile;		//
	private JScrollPane scrollhold;
	private String imageserver;
	private JButton[] b;
	private Statement autost;
	private ResultSet rs;
	private String filepath, filefilter, filterdescription;
	private String[] filters;
	private JTextField datafield;

	FileInputStream fis = null;
	int flen = 0;					//file length

	public DFile(DElement el, JPanel lpanel) {
		super(el);

		fis = null;
		flen = 0;
		filepath = "no file";

		if(!el.getAttribute("filefilter", "").equals("")) 
		    filefilter = el.getAttribute("filefilter");					
		else
		    filefilter = "pdf";

		if(!el.getAttribute("filterdescription", "").equals("")) 
		    filterdescription = el.getAttribute("filterdescription");	
		else
		    filterdescription = "pdf documents";							
								
		filelabel = new JLabel("no file");			//defaults to 'no file attached'
		attachfile = new JLabel("<html><u>browse</u></html>");		
		clearfile = new JLabel("<html><u>clear</u></html>");		
		
		filelabel.setToolTipText(filepath);
		attachfile.setToolTipText("Attach a file: " + filterdescription);
		clearfile.setToolTipText("Remove attached file");

		Border border = LineBorder.createGrayLineBorder();
		filelabel.setBorder(border);

		filelabel.setForeground(Color.blue);
		filelabel.setBackground(Color.white);

		attachfile.setForeground(Color.blue);
		clearfile.setForeground(Color.blue);

		attachfile.addMouseListener(new MouseAdapter() {
		public void mouseClicked(MouseEvent e) { 
			// Do whatever you want to do here
			readFile();
		}});

		clearfile.addMouseListener(new MouseAdapter() {
		public void mouseClicked(MouseEvent e) { 
			// Do whatever you want to do here
			clearFile();
		}});

		filelabel.addMouseListener(new MouseAdapter() {
		public void mouseClicked(MouseEvent e) { 
			// Do whatever you want to do here
			//if 'no file' exit
			//displayFile();
		}});



		if(title.length()>0){
		  lpanel.add(label);
		  }
		
	    //lpanel.add(datahold);
		lpanel.add(filelabel);
		lpanel.add(attachfile);
		lpanel.add(clearfile);
	    setPos();
	}

	public void setPos() {
		super.setPos();
		filelabel.setLocation(x+lw, y);
		attachfile.setLocation(x+lw+w-80, y+15);
		clearfile.setLocation(x+lw+w-30, y+15);

		filelabel.setSize(w, h);
		attachfile.setSize(60, h);
		clearfile.setSize(60, h);
	}

	public void setFile(InputStream in) {		

		if (in == null) {
		    clearFile();
		    } 
		else {
		    try {
			if(in.available()>0) {	
			    //filepath = file.getPath();
			    //filepath = "test path";
			    filelabel.setText("download file");
			    } 
			else clearFile();
			} 
		    catch (IOException ex) {
			System.out.print("The system cannot open file : " + ex);
			}
		    }
	      }

	public InputStream getFile() {
	      return fis;
	      }


	public int getFileLength() {
	      return flen;
	      }

	public String getFilePath() {
	      return filepath;
	      }


	public void actionPerformed(ActionEvent e) {
	      String acmd = e.getActionCommand();
	      if(acmd.equals("browse")) readFile();
	      if(acmd.equals("clear")) clearFile();
	      }

	public void clearFile() {
		filelabel.setText("no file");
		fis = null;
		flen = 0;
		}

	public void readFile() {
		JFileChooser fc = new JFileChooser();
		DFileFilter filter = new DFileFilter();
		//we need a data.split(:) then we loop for all filter choices, description is the last element
		filters = filefilter.split(":");
		for(int i=0; i<filters.length; i++)
		    filter.addExtension(filters[i]);

		filter.setDescription(filterdescription);

		fc.setFileFilter(filter);
		fc.setAcceptAllFileFilterUsed(false);
		
		int returnVal = fc.showOpenDialog(datafield);		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
			    File file = fc.getSelectedFile();

			    //Icon icon = new ImageIcon(file.getPath());
			    filepath = file.getPath();
			    filelabel.setText(filepath);
				filelabel.setToolTipText(filepath);
	
			    flen = (int)file.length();
			    System.out.println("Opening: " + file.getPath() + " size : " + flen);

			    fis = new FileInputStream(file.getPath());
			    } 
			catch (FileNotFoundException ex) {
			    System.out.print("The system cannot open file : " + ex);
			    }
			}
		}
	}
