package org.elegance;
/*
	Dual JList
*/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.Box;
import java.awt.BorderLayout;
import java.awt.Container;

public class DualListOriginal extends DField{

  private static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);
  private static final String ADD_BUTTON_LABEL = ">>";
  private static final String REMOVE_BUTTON_LABEL = "<<";
  private static final String DEFAULT_SOURCE_CHOICE_LABEL = "Available Choices";
  private static final String DEFAULT_DEST_CHOICE_LABEL = "Your Choices";

  private JLabel sourceLabel;
  private JList sourceList;
  private SortedListModel sourceListModel;

  private JLabel destLabel;
  private JList destList;
  private SortedListModel destListModel;

  private JButton addButton;
  private JButton removeButton;  

  Box bheader;		//horizontal header	//can be used for formlabel
  Box bfooter;		//horizontal footer

  String lpfield,lptable,wheresql;
  String datatype;
  String w,x,y;
  Vector vs,vd;	//vector for source and destination

  Connection mydb;
  Statement st;
  ResultSet rs;

  DElement ell;		

  public DualListOriginal(DElement el, JPanel lpanel, Connection con) {	
	super(el);
	this.ell = el;

	lpfield = el.getAttribute("lpfield");
	lptable = el.getAttribute("lptable");
	wheresql = el.getAttribute("wheresql");
	datatype = el.getAttribute("datatype");

	w = el.getAttribute("w");
	x = el.getAttribute("x");
	y = el.getAttribute("y");

    bheader = Box.createHorizontalBox();
    bfooter = Box.createHorizontalBox();		
	//bv = Box.createVerticalBox();

    initScreen(lpanel);
	
	System.out.println("lpfield = " + lpfield);
	System.out.println("lptable = " + lptable);
	System.out.println("wheresql = " + wheresql);	
	
	//by default show all available options/selections
	try{
		mydb = con;
		st = mydb.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

		String defaultquery = "SELECT " + el.getValue().trim() + "," + lpfield + " FROM " + lptable;
		if(wheresql != null)
			defaultquery += " WHERE " + wheresql;
		System.out.println("DEBUG: @ DualList: defaultquery = " + defaultquery );	
		rs = st.executeQuery(defaultquery);

		vs = new Vector();		//source
		vd = new Vector();		//destination

		//rs.first();
		while(rs.next()){
			vs.addElement(rs.getString(2));
			}
		rs.close();
		st.close();
		sourceList.setListData(vs);
		}	
	catch(SQLException x){
		System.out.println("Dual List Query Error: " + x.getMessage());
		}
   }

  public void clearLists(){
		vs.removeAllElements();
		vd.removeAllElements();
		
		sourceList.setListData(vs);
		destList.setListData(vs);
		}

  public void refreshDualList(String tab, String field, String value){

	System.out.println("DEBUG: @ DualList:updateDualList()");

	try{
		
		st = mydb.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

		String query = "SELECT " + ell.getValue().trim() + "," + lpfield + " FROM " + lptable;		
		query += " WHERE " + ell.getValue().trim() + " IN (SELECT " + ell.getValue().trim() + " FROM " + tab + " WHERE " + field + " = " + value +")";

		if(wheresql != null)
			query += " AND " + wheresql;
		
		System.out.println("DEBUG: @ refreshDualList: query = " + query );	
		rs = st.executeQuery(query);

		//UPDATE DESTINATION
		vd.removeAllElements();
		while(rs.next()){			
			vd.addElement(rs.getString(2));
			}
		destList.setListData(vd);

		//DO A REVERSE QUERY AND UPDATE THE SOURCE
		query = query.replace("IN"," NOT IN");
		
		System.out.println("DEBUG: @ refreshDualList: REVERSE query = " + query );	

		rs = st.executeQuery(query);

		//UPDATE DESTINATION
		vs.removeAllElements();
		while(rs.next()){			
			vs.addElement(rs.getString(2));
			}
		sourceList.setListData(vs);
	
		rs.close();
		st.close();
		
		}	
	catch(SQLException x){
		System.out.println("Dual List Query Error: " + x.getMessage());
		}
	}

  public boolean updateSourceList(){
	System.out.println("@ updateSourceList");
	return false;
	}

  public boolean updateDestinationList(){
	System.out.println("@ updateDestinationList");
	return false;
	}

  public String getSourceChoicesTitle() {
    return sourceLabel.getText();
  }

  public void setSourceChoicesTitle(String newValue) {
    sourceLabel.setText(newValue);
  }

  public String getDestinationChoicesTitle() {
    return destLabel.getText();
  }

  public void setDestinationChoicesTitle(String newValue) {
    destLabel.setText(newValue);
  }

  public void clearSourceListModel() {
    sourceListModel.clear();
  }

  public void clearDestinationListModel() {
    destListModel.clear();
  }

  public void addSourceElements(ListModel newValue) {
    fillListModel(sourceListModel, newValue);
  }

  public void setSourceElements(ListModel newValue) {
    clearSourceListModel();
    addSourceElements(newValue);
  }

//   public void addDestinationElements(ListModel newValue) {
//     fillListModel(destListModel, newValue);
//   }

  private void fillListModel(SortedListModel model, ListModel newValues) {
    int size = newValues.getSize();
    for (int i = 0; i < size; i++) {
      model.add(newValues.getElementAt(i));
    }
  }

  public void addSourceElements(Object newValue[]) {    
	//fillListModel(destListModel, newValue);
	for(int i=0; i < newValue.length; i++){
		vs.add(newValue[i]);
		}
	sourceList.setListData(vs);
  }

  public void setSourceElements(Object newValue[]) {
    clearSourceListModel();
    addSourceElements(newValue);
  }

  public void addDestinationElements(Object newValue[]) {
    //fillListModel(destListModel, newValue);
	for(int i=0; i < newValue.length; i++){
		vd.add(newValue[i]);
		}
	destList.setListData(vd);
  }

  private void fillListModel(SortedListModel model, Object newValues[]) {
    model.addAll(newValues);
  }

  public Iterator sourceIterator() {
    return sourceListModel.iterator();
  }

  public Iterator destinationIterator() {
    return destListModel.iterator();
  }

  public void setSourceCellRenderer(ListCellRenderer newValue) {
    sourceList.setCellRenderer(newValue);
  }

  public ListCellRenderer getSourceCellRenderer() {
    return sourceList.getCellRenderer();
  }

  public void setDestinationCellRenderer(ListCellRenderer newValue) {
    destList.setCellRenderer(newValue);
  }

  public ListCellRenderer getDestinationCellRenderer() {
    return destList.getCellRenderer();
  }

  public void setVisibleRowCount(int newValue) {
    sourceList.setVisibleRowCount(newValue);
    destList.setVisibleRowCount(newValue);
  }

  public int getVisibleRowCount() {
    return sourceList.getVisibleRowCount();
  }

  public void setSelectionBackground(Color newValue) {
    sourceList.setSelectionBackground(newValue);
    destList.setSelectionBackground(newValue);
  }

  public Color getSelectionBackground() {
    return sourceList.getSelectionBackground();
  }

  public void setSelectionForeground(Color newValue) {
    sourceList.setSelectionForeground(newValue);
    destList.setSelectionForeground(newValue);
	}

  public Color getSelectionForeground() {
    return sourceList.getSelectionForeground();
  }

  private void clearSourceSelected() {
    Object selected[] = sourceList.getSelectedValues();
    //for (int i = selected.length - 1; i >= 0; --i) {		//move backwards
	for(int i=0; i< selected.length; i++){
        vs.remove(selected[i]);
		}
	vs.trimToSize();
	sourceList.setListData(vs);
    //sourceList.getSelectionModel().clearSelection();		//clear selection
    }

  private void clearDestinationSelected() {
    Object selected[] = destList.getSelectedValues();
    //for (int i = selected.length - 1; i >= 0; --i) {
	for(int i=0; i< selected.length; i++){
		vd.remove(selected[i]);
		}    
	vd.trimToSize();
	destList.setListData(vd);
    //destList.getSelectionModel().clearSelection();		//clear selection
    }

  private void initScreen(JPanel p) {
    p.setBorder(BorderFactory.createEtchedBorder());	
    p.setLayout(new GridBagLayout());
	//p.setLayout(new BorderLayout());

	JLabel formlabel = new JLabel("Form Label");

    sourceLabel = new JLabel(DEFAULT_SOURCE_CHOICE_LABEL);
    sourceListModel = new SortedListModel();
    sourceList = new JList(sourceListModel);
    
	bheader.add(formlabel);	
	//p.add(BorderLayout.NORTH, bheader)
	//formlabel.setLocation(0,0);
	 
	 //GridBagConstraints headerconstraints = new GridBagConstraints();

      //headerconstraints.gridwidth = GridBagConstraints.REMAINDER;
      //headerconstraints.anchor = GridBagConstraints.CENTER;
      //headerconstraints.insets = new Insets(20,0,15,0);
		// Place a component at cell location (1,1) 
	//headerconstraints.gridx = 1; 
	//headerconstraints.gridy = 1; 


    p.add(sourceLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0,
        GridBagConstraints.CENTER, GridBagConstraints.NONE,
        EMPTY_INSETS, 0, 0));
    p.add(new JScrollPane(sourceList), new GridBagConstraints(0, 1, 1, 5, .5,
        1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        EMPTY_INSETS, 0, 0));

    addButton = new JButton(ADD_BUTTON_LABEL);
	addButton.setToolTipText("Add Selected");
    p.add(addButton, new GridBagConstraints(1, 2, 1, 2, 0, .25,
        GridBagConstraints.CENTER, GridBagConstraints.NONE,
        EMPTY_INSETS, 0, 0));
    addButton.addActionListener(new AddListener());
    removeButton = new JButton(REMOVE_BUTTON_LABEL);
	removeButton.setToolTipText("Remove Selected");
    p.add(removeButton, new GridBagConstraints(1, 4, 1, 2, 0, .25,
        GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
            0, 5, 0, 5), 0, 0));
    removeButton.addActionListener(new RemoveListener());

    destLabel = new JLabel(DEFAULT_DEST_CHOICE_LABEL);
    destListModel = new SortedListModel();
    destList = new JList(destListModel);
    p.add(destLabel, new GridBagConstraints(2, 0, 1, 1, 0, 0,
        GridBagConstraints.CENTER, GridBagConstraints.NONE,
        EMPTY_INSETS, 0, 0));
    p.add(new JScrollPane(destList), new GridBagConstraints(2, 1, 1, 5, .5,
        1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        EMPTY_INSETS, 0, 0));

	//position the lists here
  }


  private class AddListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      Object selected[] = sourceList.getSelectedValues();
      addDestinationElements(selected);
      clearSourceSelected();
    }
  }

  private class RemoveListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      Object selected[] = destList.getSelectedValues();
      addSourceElements(selected);
      clearDestinationSelected();
    }
  }	

  public class SortedListModel extends AbstractListModel {

	SortedSet model;

	public SortedListModel() {
		model = new TreeSet();
		}

	public int getSize() {
		return model.size();
		}

	public Object getElementAt(int index) {
		return model.toArray()[index];
		}

	public void add(Object element) {
		if (model.add(element)) {
		fireContentsChanged(this, 0, getSize());
		}
		}

	public void addAll(Object elements[]) {
		Collection c = Arrays.asList(elements);
		model.addAll(c);
		fireContentsChanged(this, 0, getSize());
		}
	
	public void clear() {
		model.clear();
		fireContentsChanged(this, 0, getSize());
		}

	public boolean contains(Object element) {
		return model.contains(element);
		}

	public Object firstElement() {
		return model.first();
		}

	public Iterator iterator() {
		return model.iterator();
		}

	public Object lastElement() {
		return model.last();
		}

	public boolean removeElement(Object element) {
		boolean removed = model.remove(element);
		if (removed) {
		fireContentsChanged(this, 0, getSize());
		}
		return removed;
		}


	}//end class sorted list model

}
