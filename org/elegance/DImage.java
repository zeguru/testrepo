package org.elegance;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JSlider;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.awt.*;
import javax.imageio.*;

public class DImage extends DField implements ActionListener {

	public JPanel datahold, bpanel;
	public JLabel datafield;
	private JScrollPane scrollhold;
	private String imageserver;
	public JButton[] b;			//button array
	private Statement autost;
	private ResultSet rs;

	private String filepath, filefilter, filterdescription;
	private String[] filters;

	public FileInputStream fis = null;
	int fl = 0;

	public boolean imagechanged;				//we want to know if the image has changed so that we can mark this for update

	Rectangle clip;
	boolean crop_image = false;
	//JSlider slider = new JSlider(1, 400 / SLIDER_INCREMENT, 1 + currentSize / SLIDER_INCREMENT);
	//Creates a horizontal slider using the specified min, max and value.
	public DImage(DElement el, JPanel lpanel) {
		super(el);

		fis = null;
		fl = 0;

		imagechanged = false;

		//test
		if(!el.getAttribute("filefilter", "").equals(""))
		    filefilter = el.getAttribute("filefilter");
		else
		    filefilter = "gif";

		if(!el.getAttribute("filterdescription", "").equals(""))
		    filterdescription = el.getAttribute("filterdescription");
		else
		    filterdescription = "Images";

		//wether or not to crop
		if(!el.getAttribute("crop", "").equals(""))
		    crop_image = true;


		datafield = new JLabel();
		datafield.setSize(w-lw, h);
		datahold = new JPanel(new BorderLayout());
		bpanel = new JPanel();

		//add scroll bars
		scrollhold = new JScrollPane(datafield);
		datahold.add(scrollhold, BorderLayout.CENTER);

		//without scroll bars
		//datahold.add(datafield, BorderLayout.CENTER);

		datahold.add(bpanel, BorderLayout.PAGE_END);

		b = new JButton[2];
		b[0] = new JButton("Browse...");
		b[0].addActionListener(this);
		b[1] = new JButton("Clear");
		b[1].addActionListener(this);
		for(int j = 0; j<2; j++) {
			bpanel.add(b[j]);
			b[j].setEnabled(false);		//disable the buttons
			}

		if(title.length()>0)
			lpanel.add(label);

        lpanel.add(datahold);

		setPos();
	}

	public void setPos() {
		super.setPos();
		datahold.setLocation(x+lw, y);
		datahold.setSize(w, h);
	}

	public void setImage(InputStream in) {
		if (in == null) {
			clearImage();
			}
		else {
			try {
				if(in.available()>0) {
					Image img = ImageIO.read(in);
					Icon icon = new ImageIcon(img);

					//testing cropping
					int new_x = img.getWidth(null)/2 - w/2;
					int new_y = img.getHeight(null)/2 - h/2;
					BufferedImage b_image = cropImage((BufferedImage)img, w, h, new_x, new_y);
					//BufferedImage dimg = dimg = new BufferedImage(newW, newH, img.getType());
					//String outputFileLocation = "/usr/sesame/systems/ceragem/images/testimage";
					//System.out.println("Writing the cropped image to: " + outputFileLocation);
					//writeImage(b_image, outputFileLocation, "jpg");
					//System.out.println("...Done");

					icon = new ImageIcon(b_image);
					  //end test

					datafield.setIcon(icon);
					}
				else clearImage();
				}
			catch (IOException ex) {
				System.out.print("The system cannot open file : " + ex);
				}
			catch (NullPointerException xx) {
				System.out.println("Byte Array received in hex format instead of escape: plz set bytea_output = 'escape' \n" + xx);
				}
			catch(Exception e){
			      System.out.println("Image Cropping Exception " + e.getMessage());
			      }
			}
		}

	public InputStream getImage() {
		return fis;
	}

	public int getImagelen() {
		return fl;
	}

    public void actionPerformed(ActionEvent e) {
		String ac = e.getActionCommand();
		if(ac.equals("Browse...")) readimage();
		if(ac.equals("Clear")) clearImage();
	}

	public void clearImage() {
		datafield.setIcon(null);
		fis = null;	//file input stream
		fl = 0;		//file length
		}

	public void readimage() {
		JFileChooser fc = new JFileChooser();
		fc.setAccessory(new ImagePreview(fc));

		DFileFilter filter = new DFileFilter();

// 		filter.addExtension("jpg");
// 		filter.addExtension("jpeg");
// 		filter.addExtension("gif");
// 		filter.setDescription("Picture Images");

		filters = filefilter.split(":");
		for(int i=0; i<filters.length; i++)
		    filter.addExtension(filters[i]);

		filter.setDescription(filterdescription);


		fc.setFileFilter(filter);
		fc.setAcceptAllFileFilterUsed(false);
		int returnVal = fc.showOpenDialog(datafield);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				File file = fc.getSelectedFile();

 				Icon icon = new ImageIcon(file.getPath());
				datafield.setIcon(icon);

				fl = (int)file.length();
				System.out.println("Opening: " + file.getPath() + " size : " + fl);

				fis = new FileInputStream(file.getPath());

				//
				imagechanged = true;
        		}
			catch (FileNotFoundException ex) {
				System.out.print("The system cannot open file : " + ex);
				imagechanged = false;
				}
			}
		else{
			imagechanged = false;
			}
		}


	public BufferedImage cropImage(BufferedImage img, int cropWidth, int cropHeight, int cropStartX, int cropStartY) throws Exception {


		BufferedImage clipped = null;
		Dimension size = new Dimension(cropWidth, cropHeight);

		createClip(img, size, cropStartX, cropStartY);

		try {
		    int w = clip.width;
		    int h = clip.height;

		    System.out.println("Crop Width " + w);
		    System.out.println("Crop Height " + h);
		    System.out.println("Crop Location " + "(" + clip.x + "," + clip.y + ")");

		    clipped = img.getSubimage(clip.x, clip.y, w, h);

		    System.out.println("Image Cropped. New Image Dimension: "
		    + clipped.getWidth() + "w X " + clipped.getHeight() + "h");
		    }
		catch (RasterFormatException rfe) {
		    System.out.println("Raster format error: " + rfe.getMessage());
		    return null;
		    }
		return clipped;
		}


	private void createClip(BufferedImage img, Dimension size, int clipX, int clipY) throws Exception {


	    /**
	    * Some times clip area might lie outside the original image,
	    * fully or partially. In such cases, this program will adjust
	    * the crop area to fit within the original image.
	    *
	    * isClipAreaAdjusted flas is usded to denote if there was any
	    * adjustment made.
	    */


	    boolean isClipAreaAdjusted = false;

	    /**Checking for negative X Co-ordinate**/
	    if (clipX < 0) {
	      clipX = 0;
	      isClipAreaAdjusted = true;
	      }
	    /**Checking for negative Y Co-ordinate**/
	    if (clipY < 0) {
	      clipY = 0;
	      isClipAreaAdjusted = true;
	      }

	    /**Checking if the clip area lies outside the rectangle**/
	    if ((size.width + clipX) <= img.getWidth() && (size.height + clipY) <= img.getHeight()) {

		/**
		* Setting up a clip rectangle when clip area
		* lies within the image.
		*/

		clip = new Rectangle(size);
		clip.x = clipX;
		clip.y = clipY;
		}
	  else {

		/**
		* Checking if the width of the clip area lies outside the image.
		* If so, making the image width boundary as the clip width.
		*/
		if ((size.width + clipX) > img.getWidth())
		    size.width = img.getWidth() - clipX;

		/**
		* Checking if the height of the clip area lies outside the image.
		* If so, making the image height boundary as the clip height.
		*/
		if ((size.height + clipY) > img.getHeight())
		    size.height = img.getHeight() - clipY;

		    /**Setting up the clip are based on our clip area size adjustment**/
		    clip = new Rectangle(size);
		    clip.x = clipX;
		    clip.y = clipY;

		    isClipAreaAdjusted = true;

		    }
		if (isClipAreaAdjusted)
		    System.out.println("Crop Area Lied Outside The Image." + " Adjusted The Clip Rectangle\n");

		}


	/**
	* This method writes a buffered image to a file
	*
	* @param img -- > BufferedImage
	* @param fileLocation --> e.g. "C:/testImage.jpg"
	* @param extension --> e.g. "jpg","gif","png"
	*/
	public static void writeImage(BufferedImage img, String fileLocation,String extension) {
	    try {
		BufferedImage bi = img;
		File outputfile = new File(fileLocation);
		ImageIO.write(bi, extension, outputfile);
		}
	    catch (IOException e) {
		e.printStackTrace();
		}
	    catch (NullPointerException ex){
		ex.printStackTrace();
		}
	    }

	}//end class
