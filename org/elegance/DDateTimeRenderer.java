package org.elegance;

import javax.swing.table.DefaultTableCellRenderer;
import java.text.DateFormat;

public class DDateTimeRenderer extends DefaultTableCellRenderer {
    
	DateFormat formatter;
    
	public DDateTimeRenderer() {
		super();
	}

    public void setValue(Object value) {
        if (formatter==null) {
            formatter = DateFormat.getDateTimeInstance();
        }
		if(value==null) setText("");
		else setText(formatter.format(value));
    }
}
