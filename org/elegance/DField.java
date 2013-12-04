package org.elegance;

import javax.swing.JLabel;

public class DField {

	public String name;
	public String title;
	public String defaultvalue;
	public String tooltip;
	//field.setToolTipText("This is the textfield's tooltip");
	String filter;
	public JLabel label;
	public int x, y, w, h, lw;

	public DField(DElement el) {
		x = Integer.valueOf(el.getAttribute("x")).intValue();
		y = Integer.valueOf(el.getAttribute("y")).intValue();
		w = Integer.valueOf(el.getAttribute("w")).intValue();
		h = Integer.valueOf(el.getAttribute("h")).intValue();
		String slw = el.getAttribute("lw", "");
		lw = 120;
		if(!slw.equals(""))
		    lw = Integer.valueOf(slw).intValue();

		defaultvalue = el.getAttribute("defaultvalue", "");
		filter = el.getAttribute("filter");
		tooltip = el.getAttribute("tooltip", "");

		name = el.getValue().trim();		//this is the value between the tags ie >x<
		title = el.getAttribute("title");
		if(title != null)
			label = new JLabel(title + " : ");
		else
			label = new JLabel("");
	}

	public void setPos() {
		label.setVerticalTextPosition(JLabel.TOP);
		label.setHorizontalTextPosition(JLabel.LEFT);
		label.setLocation(x, y);
		if (h<21) label.setSize(lw, h);
		else label.setSize(lw, 20);
	}

	public String getFilter() {
		return filter;
	}
}
