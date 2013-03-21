package org.elegance;

import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JToolBar.Separator;

public class DToolBar {

	public JButton[] button;
	public JToolBar toolbar;
	public JLabel label;
	public JTextField text;

	public DToolBar() {

		//should load buttons for accessibility eg launch notepad, browser, calc, terminal

		button = new JButton[13];

		//public JToolBar(JToolBar.HORIZONTAL)

		toolbar = new JToolBar();
		toolbar.setOpaque(true);

		//need to fix the absolute path requirement here
		//ImageIcon alarm = new ImageIcon(getClass().getResource("/usr/sesame/build/images/gif/alarm-16.gif"));

		ImageIcon alarm = new ImageIcon(getClass().getResource("images/gif/alarm-16.gif"));
		ImageIcon chemistry = new ImageIcon(getClass().getResource("images/gif/chemistry-16.gif"));
		ImageIcon dartboard = new ImageIcon(getClass().getResource("images/gif/dartboard-16.gif"));
		ImageIcon bulb = new ImageIcon(getClass().getResource("images/gif/bulb-16.gif"));

		ImageIcon diary = new ImageIcon(getClass().getResource("images/gif/diary-16.gif"));
		ImageIcon envelope = new ImageIcon(getClass().getResource("images/gif/envelope-16.gif"));
		ImageIcon globe = new ImageIcon(getClass().getResource("images/gif/globe-16.gif"));
		ImageIcon pda = new ImageIcon(getClass().getResource("images/gif/pda-16.gif"));

		ImageIcon pencil = new ImageIcon(getClass().getResource("images/gif/pencil-16.gif"));
		ImageIcon scanner = new ImageIcon(getClass().getResource("images/gif/scanner-16.gif"));
		ImageIcon world = new ImageIcon(getClass().getResource("images/gif/world-16.gif"));
		ImageIcon torch = new ImageIcon(getClass().getResource("images/gif/torch-16.gif"));

		ImageIcon calendar = new ImageIcon(getClass().getResource("images/gif/calendar.gif"));
		ImageIcon wizard = new ImageIcon(getClass().getResource("images/gif/wizard_wand.gif"));
		ImageIcon notenew = new ImageIcon(getClass().getResource("images/gif/note_new.gif"));


		button[0] = new JButton(diary);		button[0].setToolTipText("Organizer");
		button[1] = new JButton(alarm);		button[1].setToolTipText("Alarm");	//alarm
		button[2] = new JButton(envelope);	button[2].setToolTipText("EMail");	//mail
		button[3] = new JButton(globe);		button[3].setToolTipText("Browser");	//browser

		button[4] = new JButton(pda);		button[4].setToolTipText("Chat");	//chat
		button[5] = new JButton(scanner);	button[5].setToolTipText("Currency Converter");	//

		button[6] = new JButton("Refresh");
		button[7] = new JButton("Filter");
		button[8] = new JButton("Action");
		button[9] = new JButton("Export");

		//Sesame Auxilliary tools
		button[10] = new JButton("Sticky Note",notenew);
		button[11] = new JButton("Calculator",calendar);
		button[12] = new JButton("Notepad",wizard);

		for(int j=0;j<13;j++) {
			toolbar.add(button[j]);
			button[j].setOpaque(true);

			if(j==5 || j==9)
				toolbar.addSeparator();

			//button[j].setActionCommand(Integer.toString(j));
			if(button[j].getText()!="")		//if button has some text
				button[j].setActionCommand(button[j].getText());
			else
				button[j].setActionCommand(Integer.toString(j));

		}
	}
}

