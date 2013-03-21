package org.elegance;

import java.awt.image.BufferedImage;

import java.io.*; 
import javax.imageio.ImageIO;

public class SesameUtil {

	static BufferedImage createBufferImage(String path) {

		java.net.URL imgURL = DContainer.class.getResource(path);

        if (imgURL != null) {
			try {
				return ImageIO.read(imgURL);
				}
			catch (IOException ex) {
				System.out.println(ex);
				}
			}
		else {
            System.err.println("Couldn't find file: " + path);
			}

		return null;
		}


  }