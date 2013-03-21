package org.elegance;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Component;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;

import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.JPanel;

import javax.swing.tree.*;

class DImageTree extends JTree {

	private BufferedImage img = null;
	private int iw, ih;

	public DImageTree(BufferedImage img, TreeNode view) {
		super(view);
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

			g.drawImage(img, 0, 0, w, h, 0, 0, iw/7, ih, null);
		}

		super.paintComponent(g);
	}
}
