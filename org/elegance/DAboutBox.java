package org.elegance;

import java.sql.Connection;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import java.awt.GridLayout;

public class DAboutBox extends JInternalFrame {
	public boolean isVisible;

	private JLabel[] lbl;

	public DAboutBox(String appname) {	

		super("About Box", true, true, true, true);

		setLayout(new GridLayout(9, 1));

		lbl = new JLabel[15];
		lbl[0] = new JLabel(appname + " - Application", JLabel.CENTER);
		lbl[1] = new JLabel("Open Sesame Appliance", JLabel.CENTER);
		lbl[2] = new JLabel("Software Elegance Ltd", JLabel.CENTER);
		lbl[3] = new JLabel("P.O. Box 53199 - 00200", JLabel.CENTER);
		lbl[4] = new JLabel("House 25, Nairobi West, Kisauni Road", JLabel.CENTER);
		lbl[5] = new JLabel("Nairobi, Kenya", JLabel.CENTER);
		lbl[6] = new JLabel("Tel : +254 - 20 - 2058726", JLabel.CENTER);
		lbl[7] = new JLabel("Email : info@software-elegance.com", JLabel.CENTER);
		lbl[8] = new JLabel("Web : www.software-elegance.com", JLabel.CENTER);

		for(int j=0;j<9;j++) { 
			lbl[j].setVerticalTextPosition(JLabel.CENTER);
			lbl[j].setHorizontalTextPosition(JLabel.CENTER);
			add(lbl[j]);
		}

		isVisible = false;

 		// Set the default size
       	setSize();
	}

  	public void setSize() {
        super.setLocation(150, 50);
        super.setSize(350, 150);
 	}

 	public void setVisible(boolean visible) {
        super.setVisible(visible);
		isVisible = visible;
  	}
}