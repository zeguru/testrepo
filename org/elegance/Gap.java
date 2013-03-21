package org.elegance; 

// File   : layoutDemos/layoutDemoGB/Gap.java
// Purpose: Gaps for use in GridBagLayout (or any other).
//          Library alternatives are available in the Box class.
// Author : Fred Swartz - January 30, 2007 - Placed in public domain.

//This class is used just to create elements to fill in the gaps. 
//There are other ways, such as using the Box class methods to create rigid areas or glue.  

import java.awt.*;
import javax.swing.*;

////////////////////////////////////////////////////////////////////// Class Gap
public class Gap extends JComponent {
    
    //============================================================== constructor
    /* Creates filler with minimum size, but expandable infinitely. */
    public Gap() {
        Dimension min = new Dimension(0, 0);
        Dimension max = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        setMinimumSize(min);
        setPreferredSize(min);
        setMaximumSize(max);
    }
    
    //============================================================== constructor
    /* Creates rigid filler. */
    public Gap(int size) {
        Dimension dim = new Dimension(size, size);
        setMinimumSize(dim);
        setPreferredSize(dim);
        setMaximumSize(dim);
    }
}
