package org.elegance;

import java.awt.Image;
import java.awt.image.BufferedImage;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;

import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import java.awt.Toolkit;

class DImageDesktop extends JDesktopPane {

	private BufferedImage img = null;
	private int iw, ih;

	public DImageDesktop(BufferedImage img) {
		super();
		this.img = img;

		iw = img.getWidth();
		ih = img.getHeight();

	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(img != null) {
			Dimension d = getSize();
			int w = (int)d.getWidth();
			int h = (int)d.getHeight();

			g.drawImage(img, 0, 0, w, h, 0, 0, iw, ih, null);
		}
	}
}
