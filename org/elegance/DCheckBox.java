package org.elegance;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class DCheckBox extends DField {

	public JCheckBox datafield;
	boolean ischar;

	public DCheckBox(DElement el, JPanel lpanel) {
		super(el);
		datafield = new JCheckBox();

		ischar = false;
		if(el.getAttribute("ischar") != null) ischar = true;

		if (el.getAttribute("enabled", "").equals("false")) datafield.setEnabled(false);

        if(title.length()>0) lpanel.add(label);
        lpanel.add(datafield);

		setPos();
	}

	public void setPos() {
		super.setPos();
		datafield.setLocation(x+lw,y);
		datafield.setSize(w, h);
	}

	public void setNew() {
		if(defaultvalue.equals("true")) setText(true);
		else setText(false);
	}

	public void setText(boolean ldata) {
		datafield.setSelected(ldata);
	}

	public String getText() {
		String ldata = "false";
		if(ischar) ldata = "0";

		if(datafield.isSelected()) {
			ldata = "true";
			if(ischar) ldata = "1";
		}

		return ldata;
	}		
}
