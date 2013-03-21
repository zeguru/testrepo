package org.elegance;

import javax.mail.*;
import javax.mail.internet.*;
import com.sun.mail.smtp.*;
import java.io.*;
import java.net.InetAddress;
import java.util.Date;
import java.util.Properties;

public class DMail {
	Session session = null;
	Store store = null;
	Folder folder = null;
	SMTPTransport trans = null;
	private String mailfrom = null;
	private String sentbox = null;
	private String inbox = null;
	private boolean saveAttachments = false;
 	private int attnum = 1;	

	public DMail(String host, String user, String password) {
		String protocol = "imap";
		String mbox = "INBOX";
		int port = 143;
		
		try {
    		// Get a Properties object
    		Properties props = System.getProperties();

			// Get a Session object
			session = Session.getInstance(props, null);
						
			store = session.getStore(protocol);
    		store.connect(host, port, user, password);
			folder = store.getDefaultFolder();
			
			if (folder != null) {
    			folder = folder.getFolder(mbox);
			
				// try to open read/write and if that fails try read-only
				try {
    				folder.open(Folder.READ_WRITE);
				} catch (MessagingException ex) {
    				folder.open(Folder.READ_ONLY);
				}
				int totalMessages = folder.getMessageCount();
				int newMessages = folder.getNewMessageCount();
				System.out.println("Total messages = " + totalMessages);
				System.out.println("New messages = " + newMessages);
			}
		} catch (Exception ex) {
			folder = null;
			System.out.println("Oops, got mail exception! " + ex.getMessage());
    		ex.printStackTrace();
		}
	}

	public DMail(DElement root) {
		String host = root.getAttribute("host", "localhost");
		String mailuser = root.getAttribute("mailuser", "root");
		String mailpassword = root.getAttribute("mailpassword", "");
		String smtppauth = root.getAttribute("smtppauth");
		mailfrom = root.getAttribute("mailfrom", "root");

		try {
    		// Get a Properties object
    		Properties props = System.getProperties();

			// Get a Session object
			session = Session.getInstance(props, null);

			trans = (SMTPTransport)session.getTransport("smtp");
			if (smtppauth != null)
		    	trans.connect(host, mailuser, mailpassword);
			else
		    	trans.connect();			
		} catch (Exception ex) {
			System.out.println("Mail User " + mailuser);
			System.out.println("Mail exception! " + ex);
		}
	}

	public void sendmail(String messageto, String subject, String mymail) {
		
		try { 
			Message message = new MimeMessage(session);

			Multipart mp = new MimeMultipart();
			MimeBodyPart eheader = new MimeBodyPart();
			eheader.setContent(mymail, "text/html");
			mp.addBodyPart(eheader);
			
			Address fromAddress = new InternetAddress(mailfrom);
			message.setFrom(fromAddress);
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(messageto, false));

			message.setSubject(subject);
			//message.setContent(mymail, "text/html");
			message.setContent(mp);

			message.setHeader("X-Mailer", "Baraza Java Mailer");
			message.setSentDate(new Date());

			//System.out.println("Sending Message to : " + messageto);
			trans.sendMessage(message, message.getAllRecipients());
		//System.out.println("Mail was recorded successfully.");
		} catch (Exception e) {
	    	if (e instanceof SendFailedException) {
				MessagingException sfe = (MessagingException)e;
				if (sfe instanceof SMTPSendFailedException) {
					SMTPSendFailedException ssfe = (SMTPSendFailedException)sfe;
					System.out.println("SMTP SEND FAILED:");
					System.out.println(ssfe.toString());
					System.out.println("  Command: " + ssfe.getCommand());
					System.out.println("  RetCode: " + ssfe.getReturnCode());
					System.out.println("  Response: " + ssfe.getMessage());
				} else {
					System.out.println("Send failed: " + sfe.toString());
				}

				Exception ne = sfe.getNextException();
				while ((ne != null) && (ne instanceof MessagingException)) {
					sfe = (MessagingException)ne;
					if (sfe instanceof SMTPAddressFailedException) {
						SMTPAddressFailedException ssfe = (SMTPAddressFailedException)sfe;
						System.out.println("ADDRESS FAILED:");
						System.out.println(ssfe.toString());
						System.out.println("  Address: " + ssfe.getAddress());
						System.out.println("  Command: " + ssfe.getCommand());
						System.out.println("  RetCode: " + ssfe.getReturnCode());
						System.out.println("  Response: " + ssfe.getMessage());
					} else if (sfe instanceof SMTPAddressSucceededException) {
						System.out.println("ADDRESS SUCCEEDED:");
						SMTPAddressSucceededException ssfe = (SMTPAddressSucceededException)sfe;
						System.out.println(ssfe.toString());
						System.out.println("  Address: " + ssfe.getAddress());
						System.out.println("  Command: " + ssfe.getCommand());
						System.out.println("  RetCode: " + ssfe.getReturnCode());
						System.out.println("  Response: " + ssfe.getMessage());
					}
				}
	    	} else {
				System.out.println("Got Exception: " + e);
	    	}
		}
	}
}