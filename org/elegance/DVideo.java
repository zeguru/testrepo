package org.elegance;

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
import javax.imageio.*;

public class DVideo extends DField implements ActionListener { 

	public JPanel datahold, bpanel;
	public JLabel datafield;
	private JScrollPane scrollhold;
	private String imageserver;
	private JButton[] b;
	private Statement autost;
	private ResultSet rs;

	FileInputStream fis = null;
	int fl = 0;

	public DVideo(DElement el, JPanel lpanel) {
		super(el);

		fis = null;
		fl = 0;

		datafield = new JLabel();
		datafield.setSize(w-lw, h);
		datahold = new JPanel(new BorderLayout());
		bpanel = new JPanel();
		scrollhold = new JScrollPane(datafield);
		datahold.add(scrollhold, BorderLayout.CENTER);
		datahold.add(bpanel, BorderLayout.PAGE_END);

		b = new JButton[2];
		b[0] = new JButton("Add Image");
		b[0].addActionListener(this);
		b[1] = new JButton("Clear Image");
		b[1].addActionListener(this);
		for(int j = 0; j<2; j++) {
			bpanel.add(b[j]);
		}

		if(title.length()>0) lpanel.add(label);
        lpanel.add(datahold);

		setPos();
	}

	public void setPos() {
		super.setPos();
		datahold.setLocation(x+lw, y);
		datahold.setSize(w, h);
	}

	public void setImage(InputStream in) {		
		if (in == null) {
			clearImage();
		} else {
			try {
				if(in.available()>0) {	
					Image img = ImageIO.read(in);
					Icon icon = new ImageIcon(img);
					datafield.setIcon(icon);
				} else clearImage();
       		} catch (IOException ex) {
				System.out.print("The system cannot open file : " + ex);
			}
		}
	}

	public InputStream getImage() {
		return fis;
	}

	public int getImagelen() {
		return fl;
	}

    public void actionPerformed(ActionEvent e) {
		String ac = e.getActionCommand();
		if(ac.equals("Add Image")) readimage();
		if(ac.equals("Clear Image")) clearImage();
	}

	public void clearImage() {
		datafield.setIcon(null);
		fis = null;
		fl = 0;
		}

	public void readimage() {
		JFileChooser fc = new JFileChooser();
		DFileFilter filter = new DFileFilter();
		filter.addExtension("jpg");
		filter.addExtension("jpeg");
		filter.addExtension("gif");
		filter.setDescription("Picture Images");
		fc.setFileFilter(filter);
		fc.setAcceptAllFileFilterUsed(false);
		int returnVal = fc.showOpenDialog(datafield);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				File file = fc.getSelectedFile();

 				Icon icon = new ImageIcon(file.getPath());
				datafield.setIcon(icon);
            
				fl = (int)file.length();
    	       	System.out.println("Opening: " + file.getPath() + " size : " + fl);

				fis = new FileInputStream(file.getPath());

        		} 
			catch (FileNotFoundException ex) {
				System.out.print("The system cannot open file : " + ex);
				}
			}
		}
	}
