package org.elegance;

import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;

import java.io.*; 
import javax.imageio.ImageIO;

public class LogoPanel extends JPanel {

	private BufferedImage img;
    
    public LogoPanel(String path){

		setPreferredSize(new Dimension(100, 250));

		try {
			//File file = new File(path);		//only considers absolute/filesystem path
			//if(file.exists())					//there4 files in jar wont be seen
			//	img = SesameUtil.createBufferImage(path);
			//else
			//	img = SesameUtil.createBufferImage("images/sailing.jpg");

			img = SesameUtil.createBufferImage(path);
			}
		catch(Exception e){
			System.out.println("Unable to load company logo");
			System.out.println("Error: " + e.getMessage());			
			}
			
		}

    public void paintComponent(Graphics g)  {
		super.paintComponent(g);
		if(img != null) g.drawImage(img,0,0,getWidth(),getHeight(),this);
		}

  }