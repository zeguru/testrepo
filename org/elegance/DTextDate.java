package org.elegance;

import javax.swing.JOptionPane;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import javax.swing.JTextField;

import javax.swing.JPanel;

public class DTextDate extends DField implements MouseListener {

	public JTextField datafield;
	public DCalendar calendar;
	public int ch = 120;
	public boolean showcal = false;
	public boolean isEnabled = true;

  	public DTextDate(DElement el, JPanel lpanel) {
		super(el);

		datafield = new JTextField();
		calendar = new DCalendar(el);

		if(title.length()>0) lpanel.add(label);
		lpanel.add(datafield);
		lpanel.add(calendar.panel);
		calendar.panel.setVisible(false);

		datafield.addMouseListener(this);
		calendar.table.addMouseListener(this);

		if(!el.getAttribute("ch", "").equals("")) {
			ch = Integer.valueOf(el.getAttribute("ch")).intValue();
		}

		isEnabled = true;
		if (el.getAttribute("enabled", "").equals("false")) {
			datafield.setEnabled(false);
			isEnabled = false;
		}

		setPos();
	}

    public void setPos() {
    	super.setPos();
    	datafield.setLocation(x+lw, y);
     	datafield.setSize(w, h);

		calendar.panel.setLocation(x, y+20);
		calendar.panel.setSize(w+lw, h+ch);
    }

	public void setText(String ldata) {
		if(ldata==null) {
			datafield.setText("");
			return;
		}

		if(ldata.length()>0) {
			try {
				Date mydate = new Date();
				Locale locale = Locale.getDefault();

				SimpleDateFormat dateparse = new SimpleDateFormat("yyyy-MM-dd");
				mydate = dateparse.parse(ldata);
				dateparse.applyPattern("MMM dd, yyyy");

				datafield.setText(dateparse.format(mydate));
			} catch(ParseException ex) {
				System.out.println("String to date conversion problem : " + ex);
				JOptionPane.showMessageDialog(datafield, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public String getText() {
		String mydate = datafield.getText();

		if(mydate.length()>0) {
			
    		try {
                Date psdate = new Date();
				SimpleDateFormat dateparse = new SimpleDateFormat();

                if(mydate.indexOf('/')>0) dateparse.applyPattern("dd/MM/yyyy");
				else if(mydate.indexOf('-')>0) dateparse.applyPattern("dd-MM-yyyy");
				else if(mydate.indexOf('.')>0) dateparse.applyPattern("dd.MM.yyyy");
				else if(mydate.indexOf(' ')>0) dateparse.applyPattern("MMM dd, yyyy");

                psdate = dateparse.parse(mydate);
                
        		SimpleDateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd");
        		mydate = dateformatter.format(psdate);
            } catch(ParseException ex) {
				mydate = "";
                System.out.println("String to date conversion problem : " + ex);
				JOptionPane.showMessageDialog(datafield, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
            }
		}
      
		return mydate;
	}

	public String getOrclText() {
		String mydate = datafield.getText();

		if(mydate.length()>0) {
			
    		try {
                Date psdate = new Date();
				SimpleDateFormat dateparse = new SimpleDateFormat();

                if(mydate.indexOf('/')>0) dateparse.applyPattern("dd/MM/yyyy");
				else if(mydate.indexOf('-')>0) dateparse.applyPattern("dd-MM-yyyy");
				else if(mydate.indexOf('.')>0) dateparse.applyPattern("dd.MM.yyyy");
				else if(mydate.indexOf(' ')>0) dateparse.applyPattern("MMM dd, yyyy");

                psdate = dateparse.parse(mydate);
                
        		SimpleDateFormat dateformatter = new SimpleDateFormat("dd-MMM-yyyy");
        		mydate = dateformatter.format(psdate);
            } catch(ParseException ex) {
				mydate = "";
                System.out.println("String to date conversion problem : " + ex);
				JOptionPane.showMessageDialog(datafield, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
            }
		}
      
		return mydate;
	}

	// Get the grid listening mode
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {
		if(showcal) {
			setText(calendar.getKey());
			calendar.panel.setVisible(false);
			showcal = false;
		}
		if ((e.getClickCount()==2) && (!showcal) && (isEnabled)) {
			calendar.show(getText());
			calendar.panel.setVisible(true);
			showcal = true;
		}
	}
}
