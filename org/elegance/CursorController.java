package org.elegance;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.Cursor;
import java.util.Timer;
import java.util.TimerTask;

public final class CursorController {

	public final static Cursor busyCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
	public final static Cursor defaultCursor = Cursor.getDefaultCursor();
	public static final int delay = 500;

	private CursorController(){}

	public static ActionListener createListener(final Component component, final ActionListener mainActionListener) {
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {

				TimerTask timerTask = new TimerTask() {
                    public void run() {
                        component.setCursor(busyCursor);
						}
					};
                Timer timer = new Timer(); 

				try {
					timer.schedule(timerTask, delay);                    
					mainActionListener.actionPerformed(ae);
					} 
				finally {
					timer.cancel();
					component.setCursor(defaultCursor);
				}
			}
		};
	return actionListener;
	}
}
