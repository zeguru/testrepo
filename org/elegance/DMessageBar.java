package org.elegance;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import java.awt.GridLayout;
import java.awt.Color;

public class DMessageBar {

	public JPanel panel;
	public JLabel label;
	public JProgressBar progressbar;
	
	private int maxbar=100;

	public DMessageBar() {
		panel = new JPanel(new GridLayout(0, 2));
		label = new JLabel("");
		label.setForeground(Color.BLUE);
		progressbar = new JProgressBar();
		progressbar.setStringPainted(true);
		progressbar.setValue(0);

		panel.add(label);
		panel.add(progressbar);
		progressbar.setVisible(false);
	}

	public void showMessage(String lmessage) {
		label.setText(lmessage);
	}

	public void setmaxbar(int lmaxbar) {
		maxbar = lmaxbar;
		progressbar.setVisible(true);
		progressbar.setMaximum(maxbar);
	}

	public void setbarvalue(int barvalue) {
		progressbar.setValue(barvalue);
		if(barvalue==maxbar) {
			progressbar.setVisible(false);
		}
	}
}
