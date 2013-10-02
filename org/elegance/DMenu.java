package org.elegance;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.AbstractButton;
import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class DMenu {

   	public JMenuBar menubar;
	public JMenu filemenu, editmenu, toolsmenu, settingsmenu, helpmenu, skinsmenu, backgroundsmenu;
	public JMenuItem[] menuitem;
	public ButtonGroup skingroup,backgroundgroup;

	public DMenu() {
		menubar = new JMenuBar();

		filemenu = new JMenu("File");
		editmenu = new JMenu("Edit");

		toolsmenu = new JMenu("Tools");			//calc,notepad,
		settingsmenu = new JMenu("Settings");	//look n feel,
		helpmenu = new JMenu("Help");			//about baraza, html help, online help

		menubar.add(filemenu);
		  menubar.add(editmenu);
		menubar.add(toolsmenu);
		menubar.add(settingsmenu);
		menubar.add(helpmenu);

		menuitem = new JMenuItem[18];

		//JMenuItem(String text, Icon icon)	//  Creates a JMenuItem with the specified text and icon.

		//menuitem[9] = new JMenuItem("Refresh");
		menuitem[0] = new JMenuItem("Filter");
		menuitem[1] = new JMenuItem("Action");
		menuitem[2] = new JMenuItem("Export");

		for(int j=0;j<3;j++) {
			editmenu.add(menuitem[j]);
		}

		//file
		//menuitem[3] = new JMenuItem("Change Password");
		menuitem[3] = new JMenuItem("Query");
		menuitem[4] = new JMenuItem("Batch Printing");
		menuitem[5] = new JMenuItem("Logout");
		menuitem[6] = new JMenuItem("Exit");

		menuitem[3].setEnabled(false);
		menuitem[4].setEnabled(false);

		for(int j=3;j<7;j++) {
			filemenu.add(menuitem[j]);
			if(j==5) filemenu.addSeparator();
		}

		//settings
		skinsmenu = new JMenu("Emulation");			//Submenu
		skingroup = new ButtonGroup();

		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				//AbstractButton aButton = (AbstractButton) actionEvent.getSource();
				//boolean selected = aButton.getModel().isSelected();
				//System.out.println(actionEvent.getActionCommand()+ " - selected ? " + selected);

				//test
				String cmd = actionEvent.getActionCommand();

				//System.out.println("Action Command = " + cmd);

				//if(activeform>-1)
				//container.get(activeform).command(Integer.parseInt(cmd));

				if(cmd.compareToIgnoreCase("Apple")==0) {
					System.out.println("Apple Background selected");
					//DImagePanel.changeBackground(DLogin.createBufferImage("images/apple.jpg"));
					}
				}


			};

		JRadioButtonMenuItem macmenuitem = new JRadioButtonMenuItem("Mac");
		macmenuitem.setActionCommand("Mac");
		macmenuitem.addActionListener(actionListener);
		skingroup.add(macmenuitem);
		skinsmenu.add(macmenuitem);

		macmenuitem.setSelected(true);

		JRadioButtonMenuItem sesamemenuitem = new JRadioButtonMenuItem("Sesame");
		sesamemenuitem.setActionCommand("Sesame");
		sesamemenuitem.addActionListener(actionListener);
		skingroup.add(sesamemenuitem);
		skinsmenu.add(sesamemenuitem);

		sesamemenuitem.setEnabled(false);

		JRadioButtonMenuItem liquidmenuitem = new JRadioButtonMenuItem("Liquid");
		liquidmenuitem.setActionCommand("Liquid");
		liquidmenuitem.addActionListener(actionListener);
		skingroup.add(liquidmenuitem);
		skinsmenu.add(liquidmenuitem);

		liquidmenuitem.setEnabled(false);

		JRadioButtonMenuItem systemmenuitem = new JRadioButtonMenuItem("System");
		systemmenuitem.setActionCommand("System");
		systemmenuitem.addActionListener(actionListener);
		skingroup.add(systemmenuitem);
		skinsmenu.add(systemmenuitem);

		systemmenuitem.setEnabled(false);

		//menuitem[7] = new JMenuItem("Skins");

		backgroundsmenu = new JMenu("Backdrop");			//Submenu
		backgroundgroup = new ButtonGroup();

		ActionListener backgroundActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				//AbstractButton aButton = (AbstractButton) actionEvent.getSource();
				//boolean selected = aButton.getModel().isSelected();
				//System.out.println(actionEvent.getActionCommand()+ " - selected ? " + selected);

				//test
				String cmd = actionEvent.getActionCommand();

				String path = null;
				BufferedImage img = null;
				java.net.URL imgURL = null;

				if(cmd.compareToIgnoreCase("Apple")==0) {

					System.out.println("Apple Background selected");
					path = "images/apple.jpg";

					}

				//finaly display the new image
				try{
					imgURL = DContainer.class.getResource(path);
					System.out.println("Before ImageIO");
					if (imgURL != null)
						img = ImageIO.read(imgURL);
					else
						System.out.println("imgURL is null");
					}
				catch(Exception e){
					System.out.println(e.getMessage());
					e.printStackTrace();
					}

				DImagePanel.changeBackground(img);

				}

			};

		JRadioButtonMenuItem applemenuitem = new JRadioButtonMenuItem("Apple");
		applemenuitem.setActionCommand("Apple");
		applemenuitem.addActionListener(backgroundActionListener);
		backgroundgroup.add(applemenuitem);
		backgroundsmenu.add(applemenuitem);

		JRadioButtonMenuItem lightmenuitem = new JRadioButtonMenuItem("Light");
		lightmenuitem.setActionCommand("Light");
		lightmenuitem.addActionListener(backgroundActionListener);
		backgroundgroup.add(lightmenuitem);
		backgroundsmenu.add(lightmenuitem);

		lightmenuitem.setSelected(true);

		JRadioButtonMenuItem titaniummenuitem = new JRadioButtonMenuItem("Titanium");
		titaniummenuitem.setActionCommand("Titanium");
		titaniummenuitem.addActionListener(backgroundActionListener);
		backgroundgroup.add(titaniummenuitem);
		backgroundsmenu.add(titaniummenuitem);

		JRadioButtonMenuItem abstractmenuitem = new JRadioButtonMenuItem("Abstract");
		abstractmenuitem.setActionCommand("Titanium");
		abstractmenuitem.addActionListener(backgroundActionListener);
		backgroundgroup.add(abstractmenuitem);
		backgroundsmenu.add(abstractmenuitem);

		JRadioButtonMenuItem windowsmenuitem = new JRadioButtonMenuItem("Windows");
		windowsmenuitem.setActionCommand("Windows");
		windowsmenuitem.addActionListener(backgroundActionListener);
		backgroundgroup.add(windowsmenuitem);
		backgroundsmenu.add(windowsmenuitem);

		JRadioButtonMenuItem heartmenuitem = new JRadioButtonMenuItem("Heart");
		heartmenuitem.setActionCommand("Heart");
		heartmenuitem.addActionListener(backgroundActionListener);
		backgroundgroup.add(heartmenuitem);
		backgroundsmenu.add(heartmenuitem);

		menuitem[7] = new JMenuItem("Lock...");
		menuitem[8] = new JMenuItem("Preferences...");
		menuitem[9] = new JMenuItem("Change Password");

		menuitem[7].setEnabled(false);
		menuitem[8].setEnabled(false);

		settingsmenu.add(skinsmenu);
		settingsmenu.add(backgroundsmenu);

		for(int j=7;j<10;j++) {
			settingsmenu.add(menuitem[j]);
			if((j==8) || (j==9)) settingsmenu.addSeparator();
		}


		//tools
		menuitem[10] = new JMenuItem("Calculator");
		menuitem[11] = new JMenuItem("Notepad");
		menuitem[12] = new JMenuItem("Sticky Note");
		menuitem[13] = new JMenuItem("Reminder");

		menuitem[13].setEnabled(false);

		for(int j=10;j<14;j++) {
			toolsmenu.add(menuitem[j]);
			if(j==12) toolsmenu.addSeparator();
		}

		//help
		menuitem[14] = new JMenuItem("Introduction");
		menuitem[15] = new JMenuItem("Help Contents");
		menuitem[16] = new JMenuItem("About Sesame");
		menuitem[17] = new JMenuItem("Credits");		//sun-java, fedora-os, jasper-report, postgres-db

		menuitem[14].setEnabled(false);
		menuitem[15].setEnabled(false);
		menuitem[17].setEnabled(false);

		for(int j=14;j<18;j++) {
			helpmenu.add(menuitem[j]);
			if((j==15) || (j==17))helpmenu.addSeparator();
		}

		//Mouse Click Action Commands
		for(int j=0;j<18;j++) {
			menuitem[j].setActionCommand(menuitem[j].getLabel());
			//System.out.println("Menu Item (Action Command) = " + menuitem[j].getLabel());
			}

	}

	public void formMenu(String lformname) {
		filemenu.add(new JMenuItem(lformname));
	}

	public void addMenuItem(JMenu menu, JMenuItem menuitem) {
		//if menu does not exist in the panel
		//menubar.add(menu)	.... add it to the panel first
		//continue
		menu.add(menuitem);
	}

}
