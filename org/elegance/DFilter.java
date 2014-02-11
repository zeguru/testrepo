package org.elegance;

import java.sql.Connection;

import javax.swing.JPanel;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class DFilter {

	public JPanel panel;

	public List<DTextField> textfield;
	public List<DTextArea> textarea;
	public List<DCheckBox> checkbox;
	public List<DComboBox> combobox;
	public List<DTextDate> textdate;
	public List<DTextTime> texttime;
	public List<DDateSpin> datespin;
	public List<DTimeSpin> timespin;

	private boolean isfirst;
	public boolean isWhere = false;
	boolean filterand = false;
	boolean filterwhere = false;

	public DFilter(DElement fielddef, Connection ldb) {

		panel = new JPanel(null);

		List<DElement> children = new ArrayList<DElement>(fielddef.getElements());

		textfield = new ArrayList<DTextField>();
		textarea = new ArrayList<DTextArea>();
		checkbox = new ArrayList<DCheckBox>();
		combobox = new ArrayList<DComboBox>();
		textdate = new ArrayList<DTextDate>();
		texttime = new ArrayList<DTextTime>();
		datespin = new ArrayList<DDateSpin>();
		timespin = new ArrayList<DTimeSpin>();

		if(fielddef.getAttribute("wherefilter") != null) isWhere = true;
		if(fielddef.getAttribute("filterand") != null) filterand = true;
		if(fielddef.getAttribute("filterwhere") != null) filterwhere = true;

        for(DElement el : children) {
			if(el.getName().equals("TEXTFIELD")) {
            	textfield.add(new DTextField(el, panel));
			} else if (el.getName().equals("TEXTAREA")) {
            	textarea.add(new DTextArea(el, panel));
            } else if (el.getName().equals("CHECKBOX")) {
            	checkbox.add(new DCheckBox(el,	panel));
			} else if (el.getName().equals("COMBOBOX")) {
                combobox.add(new DComboBox(el, panel, ldb));
			} else if(el.getName().equals("TEXTDATE")) {
                textdate.add(new DTextDate(el, panel, el.getAttribute("defaultvalue","")));
			} else if(el.getName().equals("TEXTTIME")) {
                texttime.add(new DTextTime(el, panel));
			} else if(el.getName().equals("DATESPIN")) {
                datespin.add(new DDateSpin(el, panel));
			} else if(el.getName().equals("TIMESPIN")) {
                timespin.add(new DTimeSpin(el, panel));
			}
		}
	}

	public Map<String, String> getParam() {
		Map<String, String> param = new HashMap<String, String>();

		for(DTextField field : textfield) param.put(field.name, field.getText());
		for(DTextArea field : textarea) param.put(field.name, field.getText());
		for(DCheckBox field : checkbox) param.put(field.name, field.getText());
		for(DComboBox field : combobox) param.put(field.name, field.getText(false));
		for(DTextDate field : textdate) param.put(field.name, field.getText());
		for(DTextTime field : texttime) param.put(field.name, field.getText());
		for(DDateSpin field : datespin) param.put(field.name, field.getText());
		for(DTimeSpin field : timespin) param.put(field.name, field.getText());

		return param;
	}

	public String getWhere() {
		isfirst = true;
		String mystr = "";

		for(DTextField field : textfield) mystr += makewhere(field.name, field.getText(), field.getFilter());
		for(DTextArea field : textarea) mystr += makewhere(field.name, field.getText(), field.getFilter());
		for(DCheckBox field : checkbox) mystr += makewhere(field.name, field.getText(), field.getFilter());
		for(DComboBox field : combobox) mystr += makewhere(field.name, field.getText(false), field.getFilter());
		//for(DTextDate field : textdate) mystr += makewhere(field.name, field.getText(), field.getFilter());
		for(DTextDate field : textdate) mystr += makewhere(field.name, field.getOrclText(), field.getFilter());

		for(DTextTime field : texttime) mystr += makewhere(field.name, field.getText(), field.getFilter());
		for(DDateSpin field : datespin) mystr += makewhere(field.name, field.getText(), field.getFilter());
		for(DTimeSpin field : timespin) mystr += makewhere(field.name, field.getText(), field.getFilter());

		return mystr;
	}

	private String makewhere(String name, String text, String filter) {
		String mystr = "";
		if(!text.equals("")) {
			if(isfirst) {
				isfirst = false;
				if(filterand) mystr = " AND ";
				if(filterwhere) mystr = " WHERE ";
			}
			else { mystr += " AND "; }

			if(filter == null) mystr += "(" + name + " = '" + text + "')";
			else if(filter.toUpperCase().equals("LIKE")) mystr += "(upper(" + name + ") LIKE '%' || upper('" + text + "') || '%')";
			else if(filter.toUpperCase().equals("ILIKE")) mystr += "(" + name + " ILIKE '%" + text + "%')";
			else mystr += "(" + name + " " + filter + " '" + text + "')";
		}
	System.out.println(mystr);

		return mystr;
	}
}
