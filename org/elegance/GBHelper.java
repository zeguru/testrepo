package org.elegance;

// File   : layoutDemos/layoutDemoGB/GBHelper.java
// Purpose: Keeps track of current position in GridBagLayout.
//          Supports a few GridBag features: position, width, height, expansion.
//          All methods return GBHelper object for call chaining.
// Author : Fred Swartz - January 30, 2007 - Placed in public domain.

//Using the raw GridBagConstraints class to specify the layout characteristics
//of each element is awkward, the two constructors take either no arguments or eleven arguments.
//The properties are all public fields, so it's possible to assign to them and share one GridBagConstraints object, 
//but this usually becomes a source of much aggravation. 
//Helper class. A common solution is to define a helper class which makes it
// easier to work with a GridBagConstraints object. I've defined one such class below which is a subclass of GridBagConstraints,
// but there are many other variations available on the Internet. 

import java.awt.*;

//////////////////////////////////////////////////////////////////// Class
public class GBHelper extends GridBagConstraints {
    
    //============================================================== constructor
    /* Creates helper at top left, component always fills cells. */
    public GBHelper() {
        gridx = 0;
        gridy = 0;
        fill = GridBagConstraints.BOTH;  // Component fills area 
    }
    
    //================================================================== nextCol
    /* Moves the helper's cursor one column to the right. */
    public GBHelper nextCol() {
        gridx++;
        return this;
    }
    
    //================================================================== nextRow
    /* Moves the helper's cursor to first col in next row. */
    public GBHelper nextRow() {
        gridx = 0;
        gridy++;
        return this;
    }
    
    //================================================================== expandW
    /* Expandable Width.  Returns new helper allowing horizontal expansion. 
       A new helper is created so the expansion values don't
       pollute the origin helper. */
    public GBHelper expandW() {
        GBHelper duplicate = (GBHelper)this.clone();
        duplicate.weightx = 1.0;
        return duplicate;
    }
    
    //================================================================== expandH
    /* Expandable Height. Returns new helper allowing vertical expansion. */
    public GBHelper expandH() {
        GBHelper duplicate = (GBHelper)this.clone();
        duplicate.weighty = 1.0;
        return duplicate;
    }
    
    //==================================================================== width
    /* Sets the width of the area in terms of number of columns. */
    public GBHelper width(int colsWide) {
        GBHelper duplicate = (GBHelper)this.clone();
        duplicate.gridwidth = colsWide;
        return duplicate;
    }
    
    //==================================================================== width
    /* Width is set to all remaining columns of the grid. */
    public GBHelper width() {
        GBHelper duplicate = (GBHelper)this.clone();
        duplicate.gridwidth = REMAINDER;
        return duplicate;
    }
    
    //=================================================================== height
    /* Sets the height of the area in terms of rows. */
    public GBHelper height(int rowsHigh) {
        GBHelper duplicate = (GBHelper)this.clone();
        duplicate.gridheight = rowsHigh;
        return duplicate;
    }
    
    //=================================================================== height
    /* Height is set to all remaining rows. */
    public GBHelper height() {
        GBHelper duplicate = (GBHelper)this.clone();
        duplicate.gridheight = REMAINDER;
        return duplicate;
    }
    
    //==================================================================== align
    /* Alignment is set by parameter. */
    public GBHelper align(int alignment) {
        GBHelper duplicate = (GBHelper)this.clone();
        duplicate.fill   = NONE;
        duplicate.anchor = alignment;
        return duplicate;
    }
}
