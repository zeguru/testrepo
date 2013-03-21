package org.elegance;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JInternalFrame;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.GridLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DChangePass extends JInternalFrame implements ActionListener {
	public boolean isVisible;

	private Connection conn;

	private JPanel panel;
	private JButton btnok, btncls;
	private JLabel lbloldpass, lblnewpass, lblconfpass;
	private JPasswordField oldpass;
	private JPasswordField newpass;
	private JPasswordField confpass;

	public DChangePass(Connection db) {
		super("Change Password", true, true, true, true);

		conn = db;

		lbloldpass = new JLabel("Old Password : ");
		lblnewpass = new JLabel("New Password : ");
		lblconfpass = new JLabel("Confirm Password : ");

		oldpass = new JPasswordField();
		newpass = new JPasswordField();
		confpass = new JPasswordField();

		btnok = new JButton("Update");
		btncls = new JButton("Clear");

		panel = new JPanel(new GridLayout(4, 2));
		panel.add(lbloldpass);
		panel.add(oldpass);
		panel.add(lblnewpass);
		panel.add(newpass);
		panel.add(lblconfpass);
		panel.add(confpass);
		panel.add(btnok);
		panel.add(btncls);

		add(panel);

		isVisible = false;

 		// Set the default size
       	setSize();

		btnok.addActionListener(this);
		btncls.addActionListener(this);
	}

  	public void setSize() {
        super.setLocation(10, 10);
        super.setSize(300, 150);
 	}

 	public void setVisible(boolean visible) {
        super.setVisible(visible);
		isVisible = visible;
  	}

    public void actionPerformed(ActionEvent e) {
		String oldpassword = new String(oldpass.getPassword());
		String newpassword = new String(newpass.getPassword());
		String confpassword = new String(confpass.getPassword());

		// Execute the procedure
		if (!newpassword.equals(confpassword)) {
		      JOptionPane.showMessageDialog(panel, "New password does not match with confirmed password.", "password Change Error", JOptionPane.ERROR_MESSAGE);
		      }
		else if (newpassword.length() < 5) {
		      JOptionPane.showMessageDialog(panel, "New password must be more that 5 letters.", "password Change Error", JOptionPane.ERROR_MESSAGE);
		      }
		else {
		      try {

				String username = DLogin.getLoggedInUser();

				String mystr = "SELECT updatePassword ('" +  username + "', '" + oldpassword + "', '" + newpassword + "');";
				Statement cs = conn.createStatement();
				ResultSet rs = cs.executeQuery(mystr);
				rs.next();
				String result = rs.getString(1);

				if (result == null) {
				     //JOptionPane.showMessageDialog(panel, "Your old password does not match.", "Confirm Old password", JOptionPane.ERROR_MESSAGE);
				    JOptionPane.showMessageDialog(panel, "Could not update your password. Try again", "System Error", JOptionPane.ERROR_MESSAGE);
				} else {
				/*	mystr = "ALTER ROLE " + username + " WITH PASSWORD '" + newpassword + "';";
					System.out.println(mystr);
					Statement stps = conn.createStatement();
					stps.execute(mystr);
					stps.close();
					rs.close();
					cs.close();
				*/
					JOptionPane.showMessageDialog(panel, "Password Changed Successfully", "password Changed", JOptionPane.ERROR_MESSAGE);
				}
			} catch(SQLException ex) {
        		System.out.println("SQLException: " + ex.getMessage());
				JOptionPane.showMessageDialog(panel, "Password Not Changed", "password Change Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}