package org.elegance;

import javax.swing.JOptionPane;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

import javax.swing.JTextField;

import javax.swing.JPanel;

public class DTextTime extends DField {

	public JTextField datafield;

  	public DTextTime(DElement el, JPanel lpanel) {
		super(el);

		datafield = new JTextField();

		if(title.length()>0) lpanel.add(label);
		lpanel.add(datafield);

		setPos();
	}

    public void setPos() {
    	super.setPos();
    	datafield.setLocation(x+lw, y);
     	datafield.setSize(w, h);
    }

	public void setText(String ldata) {
		if(ldata==null) return;
		if(ldata.length()>0) {
			try {
				Date mydate = new Date();
				Locale locale = Locale.getDefault();

				SimpleDateFormat dateparse = new SimpleDateFormat("HH:mm");
				mydate = dateparse.parse(ldata);
				String s = DateFormat.getTimeInstance(DateFormat.SHORT, locale).format(mydate);
				datafield.setText(s);
			} catch(ParseException ex) {
				System.out.println("String to date conversion problem : " + ex);
				JOptionPane.showMessageDialog(datafield, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public String getText() {
		String mydate = "";

		if(datafield.getText().length()>0) {
    		try {
                Date psdate = new Date();

                SimpleDateFormat dateparse = new SimpleDateFormat();               
                if(mydate.indexOf(':')>0) dateparse.applyPattern("hh:mm a");
				else if(mydate.indexOf('.')>0) dateparse.applyPattern("hh.mm a");
				else dateparse.applyPattern("HHmm");
				psdate = dateparse.parse(datafield.getText());

        		SimpleDateFormat dateformatter = new SimpleDateFormat("HH:mm");
        		mydate = dateformatter.format(psdate);
            } catch(ParseException ex) {
                System.out.println("String to date conversion problem : " + ex);
				JOptionPane.showMessageDialog(datafield, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
            }
		}
      
		return mydate;
	}		
}
