package org.elegance;

import javax.swing.table.DefaultTableCellRenderer;
import java.text.DateFormat;

public class DTimeRenderer extends DefaultTableCellRenderer {
    
	DateFormat formatter;
    
	public DTimeRenderer() {
		super();
	}

    public void setValue(Object value) {
        if (formatter==null) {
            formatter = DateFormat.getTimeInstance();
        }
		if(value==null) setText("");
		else setText(formatter.format(value));
    }
}
