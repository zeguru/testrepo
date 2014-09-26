package org.elegance;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.AbstractCellEditor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DCalendar implements ActionListener {
	public JButton prevmonth;
	public JButton themonth;
	public JButton nextmonth;

	public JTable table;
	public JScrollPane scrollpane;
	public JPanel toppanel;
	public JSplitPane panel;

	JTextField datetext;

	public String filtername;

	GregorianCalendar calendar;
	String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
	String[] columnNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
	String[][] data;

	public DCalendar(DElement fielddef) {
		calendar = new GregorianCalendar();
		datetext = new JTextField();

		if(calendar.getFirstDayOfWeek() == Calendar.MONDAY) {
			String[] calendardays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
			columnNames = calendardays;
		} else {
			String[] calendardays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
			columnNames = calendardays;
		}

		data = new String[6][7];
		for(int j=0;j<6;j++) for(int i=0;i<7;i++) data[j][i] = "";

		filtername = fielddef.getAttribute("filtername", "filterid");

		prevmonth = new JButton("<<");
		themonth = new JButton("February");
		nextmonth = new JButton(">>");

		table = new JTable(data, columnNames);
		table.setCellSelectionEnabled(true);
		table.setRowHeight(21);
		table.setShowGrid(false);
		scrollpane = new JScrollPane(table);

		toppanel = new JPanel(new BorderLayout());
		toppanel.add(prevmonth, BorderLayout.LINE_START);
		toppanel.add(themonth, BorderLayout.CENTER);
		toppanel.add(nextmonth, BorderLayout.LINE_END);
		panel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, toppanel, scrollpane);
		panel.setOneTouchExpandable(true);
		panel.setDividerLocation(25);

		// Set the background to opaque
		prevmonth.setOpaque(true);
		themonth.setOpaque(true);
		nextmonth.setOpaque(true);
		toppanel.setOpaque(true);
		panel.setOpaque(true);

		nextmonth.addActionListener(this);
		prevmonth.addActionListener(this);

		// Center Align the calendar
		DefaultTableCellRenderer renderer =  new DefaultTableCellRenderer();
		renderer.setVerticalAlignment(DefaultTableCellRenderer.CENTER);
		renderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		table.setDefaultRenderer(Object.class, renderer);

		show();
	}

      //testing this no-arg constructor with DatePickerCellEditor
      public DCalendar() {
		calendar = new GregorianCalendar();
		datetext = new JTextField();

		if(calendar.getFirstDayOfWeek() == Calendar.MONDAY) {
			String[] calendardays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
			columnNames = calendardays;
		} else {
			String[] calendardays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
			columnNames = calendardays;
		}

		data = new String[6][7];
		for(int j=0;j<6;j++) for(int i=0;i<7;i++) data[j][i] = "";

		//filtername = fielddef.getAttribute("filtername", "filterid");
		filtername = "filterid";

		prevmonth = new JButton("<<");
		themonth = new JButton("February");
		nextmonth = new JButton(">>");

		table = new JTable(data, columnNames);
		table.setCellSelectionEnabled(true);
		table.setRowHeight(21);
		table.setShowGrid(false);
		scrollpane = new JScrollPane(table);

		toppanel = new JPanel(new BorderLayout());
		toppanel.add(prevmonth, BorderLayout.LINE_START);
		toppanel.add(themonth, BorderLayout.CENTER);
		toppanel.add(nextmonth, BorderLayout.LINE_END);
		panel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, toppanel, scrollpane);
		panel.setOneTouchExpandable(true);
		panel.setDividerLocation(25);

		// Set the background to opaque
		prevmonth.setOpaque(true);
		themonth.setOpaque(true);
		nextmonth.setOpaque(true);
		toppanel.setOpaque(true);
		panel.setOpaque(true);

		nextmonth.addActionListener(this);
		prevmonth.addActionListener(this);

		// Center Align the calendar
		DefaultTableCellRenderer renderer =  new DefaultTableCellRenderer();
		renderer.setVerticalAlignment(DefaultTableCellRenderer.CENTER);
		renderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		table.setDefaultRenderer(Object.class, renderer);

		show();
}

	public void show(String strdate) {
		if(strdate==null) show();
		if(strdate.length()>0) {
			try {
				Date mydate = new Date();
				SimpleDateFormat dateparse = new SimpleDateFormat("yyyy-MM-dd");
				mydate = dateparse.parse(strdate);
				calendar.setTime(mydate);

				show();
			} catch(ParseException ex) {
				System.out.println("String to date conversion problem : " + ex);
			}
		}
	}

	public void show() {
		calendar.set(Calendar.DAY_OF_MONTH, 1);

		themonth.setText(monthNames[calendar.get(Calendar.MONTH)] + "  " + calendar.get(Calendar.YEAR));
		int maxdays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		for(int j=0;j<6;j++) for(int i=0;i<7;i++)
			table.setValueAt("", j, i);

		int addvalue = 0;
		for(int i = 1; i<=maxdays; i++) {
			int row = calendar.get(Calendar.WEEK_OF_MONTH) - 1;
			int col = calendar.get(Calendar.DAY_OF_WEEK) - 1;
			if(calendar.getFirstDayOfWeek() == Calendar.MONDAY) col -= 1;
			if(row<0) addvalue = 1;
			row += addvalue;
			if(col<0) col = 6;
			String myday = Integer.toString(i);

			//System.out.println(myday + ", " + row + ", " + col);
			table.setValueAt(myday, row, col);
			calendar.add(Calendar.DATE, 1);
		}
		calendar.add(Calendar.DATE, -1);	// reverse to be in present month
	}

    public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("<<"))
		      calendar.add(Calendar.MONTH, -1);
		else
		      calendar.add(Calendar.MONTH, 1);
		show();
    }

	public String getKey() {
		int row = table.getSelectedRow();
		int col = table.getSelectedColumn();
		String mydate = "";
		String value = "";
		if((row>=0) && (col>=0)) value = (String)table.getValueAt(row, col);

		SimpleDateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd");

		if(!value.equals("")) {
			int day = Integer.valueOf(value).intValue();
			calendar.set(Calendar.DAY_OF_MONTH, day);
			mydate = dateformatter.format(calendar.getTime());
			}
		else {
			GregorianCalendar tmpcalendar = new GregorianCalendar();
			mydate = dateformatter.format(tmpcalendar.getTime());
		      }

		datetext.setText(mydate);		//render hack ???
	   	return mydate;
  	}


}
