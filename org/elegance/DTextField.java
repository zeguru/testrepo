package org.elegance;

/*
we can have an attribute upload="true" (+ file="pdf" or "image", etc) so that the textbox is uneditable and pops a file open dialog when clicked. 
may also have a tooltip
*/

import javax.swing.JTextField;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class DTextField extends DField {

	public JTextField datafield;
	public int cmbkey=0;
	public boolean enabled=true;
	public String tooltip;

    public DTextField(DElement el, JPanel lpanel) {
		super(el);
		
			datafield = new JTextField();

			datafield.setHorizontalAlignment(JTextField.LEADING);
			datafield.setCaretPosition(0);
				
			//if(!el.getAttribute("tooltip", "").equals("")) {			
			//	tooltip = fielddef.getAttribute("tooltip");			
			//	}
			
			if (el.getAttribute("enabled", "").equals("false")) {
				enabled = false;
				datafield.setEnabled(false);
			}
			if(!el.getAttribute("cmbkey", "").equals("")) {
				cmbkey = Integer.valueOf(el.getAttribute("cmbkey")).intValue();
			}
			//if (el.getAttribute("upload", "").equals("true")) {
				//enabled = false;
				//datafield.setEnabled(false);
			//}

			datafield.setActionCommand(Integer.toString(cmbkey));

			if(title.length()>0) 
				lpanel.add(label);
			lpanel.add(datafield);

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
		datafield.setText(ldata);
		datafield.moveCaretPosition(0);
	}

	public String getText() {
		return datafield.getText();
	}		
}
