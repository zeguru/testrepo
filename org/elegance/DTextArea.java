package org.elegance;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class DTextArea extends DField { 

	public JTextArea datafield;
	public JScrollPane datahold;
	public boolean dummy = false;

	public DTextArea(DElement el, JPanel lpanel) {
		super(el);
		datafield = new JTextArea();
		datafield.setLineWrap(true);
		datafield.setWrapStyleWord(true);
		datahold = new JScrollPane(datafield);

		if (el.getAttribute("enabled", "").equals("false")) datafield.setEnabled(false);

        if(title.length()>0) lpanel.add(label);
        lpanel.add(datahold);

		setPos();
	}

//this is a dummy textarea to get around the form overflow problem
public DTextArea(DElement el, JPanel lpanel, Boolean d) {		
		super(el);
		dummy = true;
		datafield = new JTextArea();
		datafield.setLineWrap(false);
		datafield.setWrapStyleWord(false);		
		datafield.setVisible(false);

		datahold = new JScrollPane(datafield);
		datahold.setVisible(false);		

		//if (el.getAttribute("enabled", "").equals("false")) datafield.setEnabled(false);

        //if(title.length()>0) 		//line commented to bypass the bug when loading dual list
			lpanel.add(label);

        lpanel.add(datahold);

		setPos();
	}


	public void setPos() {
		super.setPos();
		datahold.setLocation(x+lw, y);
		datahold.setSize(w, h);
	}

	public void setNew() {
		setText(defaultvalue);
	}

	public void setText(String ldata) {
		datafield.setText(ldata);
		datafield.moveCaretPosition(0);
	}

	public String getText() {
		return datafield.getText();
	}		
}
