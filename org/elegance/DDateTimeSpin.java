package org.elegance;

import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.JSpinner;
import javax.swing.JSpinner.DateEditor;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerDateModel;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class DDateTimeSpin extends DField {

	public JSpinner datafield;

  	public DDateTimeSpin(DElement el, JPanel lpanel) {
		super(el);

		Calendar calendar = Calendar.getInstance();
		Date initDate = calendar.getTime();
        calendar.add(Calendar.YEAR, -100);
        Date earliestDate = calendar.getTime();
        calendar.add(Calendar.YEAR, 200);
        Date latestDate = calendar.getTime();
        SpinnerModel datemodel = new SpinnerDateModel(initDate, earliestDate, latestDate, Calendar.YEAR);
		datafield = new JSpinner(datemodel);
        datafield.setEditor(new JSpinner.DateEditor(datafield, "dd/MM/yyyy hh:mm aa"));

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
		if (ldata==null) return;

		if(ldata.length()>0) {
			try {
				Date mydate = new Date();

				SimpleDateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	
				mydate = dateformatter.parse(ldata);
				SpinnerModel datemodel = datafield.getModel();
           		datemodel.setValue(mydate);
			} catch(ParseException ex) {
				System.out.println("String to date conversion problem : " + ex);
				JOptionPane.showMessageDialog(datafield, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public String getText() {
		SpinnerModel datemodel = datafield.getModel();

		SimpleDateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String mydate = dateformatter.format(((SpinnerDateModel)datemodel).getDate());
      
		return mydate;
	}		
}
