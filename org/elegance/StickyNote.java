package org.elegance;

// File   : GUI/components/textarea/TextAreaDemoB.java
// Purpose: Illustrate JTextArea contained in JScrollPane
//          where scroll bars appear as needed.
// Author : Fred Swartz, 2006-07-27

import java.awt.*;
import javax.swing.*;

public class StickyNote extends JFrame {
    //============================================== instance variables
   JTextArea notesArea = new JTextArea(6, 21);
   static int notecount = 0;     
    //====================================================== constructor
    public StickyNote() {
        
			notecount ++;

			//... Set textarea's initial text, scrolling, and border.
			notesArea.setText(new java.util.Date().toString() + "\n");
			notesArea.setForeground(Color.blue);
			notesArea.setBackground(Color.yellow);
			
			JScrollPane scrollingArea = new JScrollPane(notesArea);
			
			//... Get the content pane, set layout, add to center
			JPanel content = new JPanel();
			content.setLayout(new BorderLayout());
			content.add(scrollingArea, BorderLayout.CENTER);
			
			this.setUndecorated(true);      //no title
			
			//enhancements
			//scrollbar style

			//... Set window characteristics.
			this.setContentPane(content);
			this.setTitle("Sesame Notes : " + notecount);
			this.setLocation(150,100);
			//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);			
			this.pack();
			
			this.setVisible(true);
			
    }
    
    //============================================================= main
/*
    public static void main(String[] args) {
         try{
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");			//ok								
            JFrame win = new StickyNote();
            win.setVisible(true);
            }
         catch(Exception e){
            System.out.println(e.getMessage());
            }
    }
*/

}
