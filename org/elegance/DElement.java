package org.elegance;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

public class DElement {	
	Map<String, String> attributes;
	List<DElement> elements;
	
	int type = 0;
	String name = null;
	String value = "";

	public DElement() {
		type = 0;
		name = "";

		attributes = new TreeMap<String, String>();		
		elements = new ArrayList<DElement>();
		}
	
	public DElement(Node node) {
		type = node.getNodeType();
        name = node.getNodeName();
		
		attributes = new TreeMap<String, String>();		
		if(type == 1) readAttibutes(node);		//Node.ATTRIBUTE_NODE

		if(type == Node.COMMENT_NODE) value = node.getNodeValue();

		elements = new ArrayList<DElement>();

		NodeList elementlist = node.getChildNodes();
		for (int i=0; i < elementlist.getLength(); i++) {
			if(elementlist.item(i).getNodeType()!=3) 
				elements.add(new DElement(elementlist.item(i)));
			else {
				if(elementlist.item(i).getNodeValue()!=null)
					value += elementlist.item(i).getNodeValue().trim();
				}
			}
		}

	public void readAttibutes(Node node) {
		NamedNodeMap attmap = node.getAttributes();
		for(int i=0; i<attmap.getLength(); i++) 
			attributes.put(attmap.item(i).getNodeName(), attmap.item(i).getNodeValue());
		}

	public DElement getElement(int index) {
		if(elements.size()>index) return elements.get(index);
		return null;
		}

	public void addElement(DElement element) {
		elements.add(element);
		}

	public DElement getFirst() {
		return getElement(0);
	}

	public List<DElement> getElements() {
		return elements;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getContentSize() {
		return elements.size();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String myvalue) {
		value = myvalue;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public String getAttribute(String attributename) {
		return attributes.get(attributename);
	}

	public void setAttribute(String attributename, String attributevalue) {
		attributes.put(attributename, attributevalue);
	}

	public void removeAttribute(String attributename) {
		attributes.remove(attributename);
	}

	public String getAttribute(String attributename, String defaultvalue) {
		String mystr = attributes.get(attributename);
		if(mystr==null) mystr = defaultvalue;
	
		return mystr;
	}
}