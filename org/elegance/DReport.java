package org.elegance;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.*;

import java.net.*;
import java.io.*;

import java.sql.Connection;
import java.util.Map;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.GridLayout;

public class DReport {

	public JPanel panel;

	public DReportViewer rp;
	public JasperPrintManager jpm;
	private JasperPrint rd;

	private boolean iscreated;
	public boolean ischild;
	public boolean ispopup;
	public int linkkey;

	private Connection conn;
	private String reportname;
	private String jasperfile;
	private int printcopies;
	private Map<String, String> parameters;

	public DReport(DElement fielddef, Connection ldb, String reportpath) {

		conn = ldb;

		parameters = new HashMap<String, String>();
        parameters.put("reporttitle", reportname);
		parameters.put("reportpath", reportpath);

        jasperfile = reportpath + fielddef.getAttribute("jasperfile");
		reportname = fielddef.getAttribute("name");
		ischild = false;
		ispopup = false;
		linkkey = 0;

        if(!fielddef.getAttribute("linkkey", "").equals("")) {
			ischild = true;
			linkkey = Integer.valueOf(fielddef.getAttribute("linkkey")).intValue();
		}

		iscreated = false;
		if(fielddef.getAttribute("filtered", "").equals("true")) 
			ischild = true;

		if(fielddef.getAttribute("popup", "false").equals("true")) {			
			ispopup = true;						
			}

		printcopies = 0;
		if(!fielddef.getAttribute("printcopies", "").equals(""))
			printcopies = Integer.valueOf(fielddef.getAttribute("printcopies")).intValue();

		panel = new JPanel(new GridLayout(1,0));
		jpm = new JasperPrintManager();
  	}

	public void showReport() {
		
		if(!ischild) {//if its a parent ie NOT A CHILD
			try {
				// Reab from http and from file
				if(jasperfile.startsWith("http")) {
            		URL url = new URL(jasperfile);
                	InputStream in = url.openStream();
					rd = JasperFillManager.fillReport(in, parameters, conn);
					} 
				else {
					rd = JasperFillManager.fillReport(jasperfile, parameters, conn);
					}

				if(iscreated) {
                    rp.loadReport(rd);
					rp.refreshPage();
					} 
				else {
					rp = new DReportViewer(rd);
					panel.add(rp);
					iscreated = true;
					}
				} 
			catch (JRException ex) {
                System.out.println("Jasper Compile error : " + ex);
				JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
				} 
			catch (MalformedURLException ex) {
				System.out.println("HTML Error : " + ex);
				JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
				} 
			catch (IOException ex) {
				System.out.println("IO Error : " + ex);
				JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
				} 
			}
		}

	public void printreport(String filtername, String filterid) {
		System.out.println("DEBUG: at DContainer:printreport()");
		if(ischild) {
    		parameters.put(filtername, filterid);
			try {
				// Reab from http and from file
				if(jasperfile.startsWith("http")) {
            		URL url = new URL(jasperfile);
                	InputStream in = url.openStream();
					rd = JasperFillManager.fillReport(in, parameters, conn);
				} else {
					rd = JasperFillManager.fillReport(jasperfile, parameters, conn);
				}
				
				for(int i=0; i<printcopies; i++)
					jpm.printReport(rd, false);

				System.out.println("Trying to print. " + filterid);
			} catch (JRException ex) {
                System.out.println("Jasper Compile error : " + ex);
				JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
			} catch (MalformedURLException ex) {
				System.out.println("HTML Error : " + ex);
				JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
				System.out.println("IO Error : " + ex);
				JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
            }
		}
		System.out.println("Filter = " + filtername + " key = " + filterid);
	}

	public void	putparams(String filtername, String filterid) {
		System.out.println("DEBUG: at DReport:putparams()");
    	parameters.put(filtername, filterid);
		System.out.println("Filter = " + filtername + " key = " + filterid);
	}

	public void	putparams(Map<String, String> param) {
    	parameters.putAll(param);
		drillReport();
		System.out.println("Param filter Done Filter.");
	}

	public void drillReport() {
    	try {
			// Read from http and from file
            if(jasperfile.startsWith("http")) {
            	URL url = new URL(jasperfile);
                InputStream in = url.openStream();
				rd = JasperFillManager.fillReport(in, parameters, conn);
				} 
			else {
				rd = JasperFillManager.fillReport(jasperfile, parameters, conn);
				}

			if(iscreated){
				rp.loadReport(rd);
            	rp.refreshPage();
				} 
			else {
				rp = new DReportViewer(rd);
				panel.add(rp);
				rp.setVisible(false);
				rp.setVisible(true);
				iscreated = true;
				}
			} 
		catch (JRException ex) {
        	System.out.println("Jasper Compile error : " + ex);
			JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
			} 
		catch (MalformedURLException ex) {
			System.out.println("HTML Error : " + ex);
			JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
			} 
		catch (IOException ex) {
			System.out.println("IO Error : " + ex);
			JOptionPane.showMessageDialog(panel, ex.getMessage(), "Query error", JOptionPane.ERROR_MESSAGE);
			} 
		}

	//display report in popup window
	public void popUpReport(){
		}

}
