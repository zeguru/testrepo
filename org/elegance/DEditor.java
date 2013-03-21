package org.elegance;

import java.sql.Connection;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JButton;

import javax.swing.text.StyledEditorKit;
import javax.swing.Action;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.URL;
import java.net.MalformedURLException;

public class DEditor extends DField implements ActionListener { 

	public JEditorPane datafield;
	public JPanel datahold, bpanel;
	private JScrollPane scrollhold;
	private String imageserver;
	private JButton[] b;
	private String mydata;
	URL url;

	private DElement ell;

	public DEditor(DElement el, JPanel lpanel, Connection conn) {
		super(el);
		
		this.ell = el;

		//url = new URL("http://softwareelegance.webuda.com");
		//TextSamplerDemo.class.getResource(                                "TextSamplerDemoHelp.html");


		datafield = new JEditorPane();				//only compatible with HTML 3.x
		datafield.setContentType("text/html");
		datahold = new JPanel(new BorderLayout());
		bpanel = new JPanel();
		scrollhold = new JScrollPane(datafield);
		datahold.add(scrollhold, BorderLayout.CENTER);
		datahold.add(bpanel, BorderLayout.PAGE_END);

		b = new JButton[4];
		Action action = null;
		action = new StyledEditorKit.UnderlineAction();
		action.putValue(Action.NAME, "<html><u>u</u></html>");
		b[0] = new JButton(action);
		action = new StyledEditorKit.BoldAction();
    	action.putValue(Action.NAME, "<html><b>b</b></html>");
	    b[1] = new JButton(action);
        action = new StyledEditorKit.ItalicAction();
        action.putValue(Action.NAME, "<html><i>i</i></html>");
        b[2] = new JButton(action);
		for(int j = 0; j<3; j++) {
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

	public void setText(String ldata) {
		mydata = ldata;

		try {
			if (ell.getAttribute("isurl") != null){				
				datafield.setPage(ldata);
				datafield.setEditable(false);
				}
			else{
				datafield.setText(ldata);
				}
			} 
		catch (IOException e) {
			//System.err.println("Attempted to read a bad URL: " + ldata);
			System.err.println("Input Error : " + e.getMessage() + " \tViolating input : "+ ldata);
		}
	}

	public String getText() {
		return mydata;
	}

    public void actionPerformed(ActionEvent e) {
		String ac = e.getActionCommand();
    }
}
