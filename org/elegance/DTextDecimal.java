package org.elegance;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.text.ParseException;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;

import javax.swing.JPanel;

public class DTextDecimal extends DField {

	public JFormattedTextField datafield;

  	public DTextDecimal(DElement el, JPanel lpanel) {
		super(el);

		NumberFormat numberformat = NumberFormat.getNumberInstance();
		String format = el.getAttribute("format", "");

		datafield = new JFormattedTextField(numberformat);

		if(title.length()>0) lpanel.add(label);
		lpanel.add(datafield);

		if (el.getAttribute("enabled", "").equals("false")) datafield.setEnabled(false);

		setPos();
	}

    public void setPos() {
    	super.setPos();
    	datafield.setLocation(x+lw, y);
     	datafield.setSize(w, h);
    }

	public void setNew() {
		setText(defaultvalue);
	}

	public void setText(String ldata) {
		if(ldata==null) ldata = "0";

		if(ldata.length()>0) {
			Double myvalue = Double.valueOf(ldata).doubleValue();
			datafield.setValue(myvalue);
		}
	}

	public String getText() {
		String myvalue = "";

		if(datafield.getText().length()>0) {
 			Double d = ((Number)datafield.getValue()).doubleValue();;
			System.out.println(d);
			myvalue = d.toString();
		}
      
		return myvalue;
	}		
}
