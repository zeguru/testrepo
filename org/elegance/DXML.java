package org.elegance;

import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.FactoryConfigurationError;  
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;  
import org.xml.sax.SAXParseException;

import java.io.InputStream;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;

public class DXML {
	private DElement root;

	public DXML(String xmlfile) {
        try {
			// initialise Specifications from a Local Property file		
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();	
   			Document document = builder.parse(xmlfile);
			root = new DElement(document);
        } catch (SAXParseException ex) {
        	System.out.println("XML Error : " + ex.getMessage());
        } catch (ParserConfigurationException ex) {
            System.out.println("File IO error : " + ex);
        } catch (SAXException ex) {
            System.out.println("File IO error : " + ex);
        } catch (IOException ex) {
            System.out.println("File IO error : " + ex);
        }
	}

	public DXML(InputStream xmlfile) {
        try {
			// initialise Specifications from a Local Property file		
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();	
   			Document document = builder.parse(xmlfile);
			root = new DElement(document);
        } catch (SAXParseException ex) {
        	System.out.println("XML Error : " + ex.getMessage());
        } catch (ParserConfigurationException ex) {
            System.out.println("File IO error : " + ex);
        } catch (SAXException ex) {
            System.out.println("File IO error : " + ex);
        } catch (IOException ex) {
            System.out.println("File IO error : " + ex);
        }
	}

	public DElement getRoot() {
		return root;
	}
}
