package org.elegance;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import java.util.List;
import java.util.ArrayList;

public class DComboList extends DField {

	public JComboBox datafield;
	public int linkkey = 0;
	public int cmbkey = 0;
	
	private String lookupname;
	public String linkfield;
	private List<String> lplist;

    public DComboList(DElement el, JPanel lpanel) {
		super(el);

		datafield = new JComboBox();	
		lplist = new ArrayList<String>();

		if(title.length()>0) lpanel.add(label);
		lpanel.add(datafield);

		if (el.getAttribute("editable", "").equals("true")) datafield.setEditable(true);

		if (el.getAttribute("enabled", "").equals("false")) datafield.setEnabled(false);

		List<DElement> listitems = new ArrayList<DElement>(el.getElements());
		for(DElement lel : listitems) {
			lplist.add(lel.getValue());
			datafield.addItem(lel.getValue());
		}

		setPos();
 	}

	public void setPos() {
		super.setPos();
		datafield.setLocation(x+lw, y);
		datafield.setSize(w, h);
	}

	public void setText(String ldata) {
		int lp = lplist.indexOf(ldata);
		datafield.setSelectedIndex(lp);
	}

	public String getText() {
		int lp = datafield.getSelectedIndex();	
		return lplist.get(lp);
	}		
}
