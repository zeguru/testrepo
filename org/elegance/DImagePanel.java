package org.elegance;

import java.awt.Image;
import java.awt.image.BufferedImage;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;

import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class DImagePanel extends JPanel {

	private static BufferedImage img = null;
	private int iw, ih;

	public DImagePanel(BufferedImage img, GridLayout layout) {
		super(layout);
		this.img = img;

		iw = img.getWidth();
		ih = img.getHeight();

		super.setOpaque(true);
	}

	protected void paintComponent(Graphics g) {
		if(img != null) {
			Dimension d = getSize();
			int w = (int)d.getWidth();
			int h = (int)d.getHeight();

			g.drawImage(img, 0, 0, w, h, 0, 0, iw, ih, null);
		}

		super.paintComponent(g);
	}

	public static void changeBackground(BufferedImage image){
		img = image;
		//paintComponent();

		}

}
