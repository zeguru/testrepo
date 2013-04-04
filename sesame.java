
import org.elegance.DLogin;
import javax.swing.JFrame;
import javax.swing.JApplet;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;

import com.oyoaha.swing.plaf.oyoaha.*;
import javax.swing.LookAndFeel;
import java.awt.event.ActionEvent;
import ch.randelshofer.quaqua.*;
import java.awt.Toolkit;

//import javax.swing.Exception;
import java.lang.reflect.InvocationTargetException;
import java.awt.Window;
import java.lang.reflect.Method;

import java.awt.Cursor;

public class sesame extends JApplet {

	public static String imagedir;
	public static JFrame frame;

//testing cursor stuff
// ActionListener cursorDoIt = CursorController.createListener(this, doIt);
//   button.addActionListener(cursorDoIt);

	public void init() {		// Run an applet
		DLogin login;
		String dir = getParameter("dir");
		String mylook = getParameter("mylook");
		String bg_image = getParameter("bg_image");

		try{

			if(mylook==null)
				UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");
			else if(mylook.compareToIgnoreCase("neutral")==0){
				String lookAndFeelType = UIManager.getCrossPlatformLookAndFeelClassName();
				UIManager.setLookAndFeel(lookAndFeelType);
				}
			else if(mylook.compareToIgnoreCase("system")==0){
				String lookAndFeelType = UIManager.getSystemLookAndFeelClassName();
				UIManager.setLookAndFeel(lookAndFeelType);
				}
			else if(mylook.compareToIgnoreCase("liquid")==0)
				UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");		//ok
			else if(mylook.compareToIgnoreCase("plastic")==0)
				UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");		//ok
			else if(mylook.compareToIgnoreCase("quaqua")==0){
				UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");				//original

				//test
				//ch.randelshofer.quaqua.QuaquaManager.setProperty("Quaqua.design", "panther");
				//ch.randelshofer.quaqua.QuaquaManager.setProperty("Quaqua.TabbedPane.design", "jaguar");
				//ch.randelshofer.quaqua.QuaquaManager.setProperty("Quaqua.FileChooser.autovalidate", "true");

				//UIManager.put("ClassLoader", getClass().getClassLoader());
				//UIManager.setLookAndFeel(ch.randelshofer.quaqua.QuaquaManager.getLookAndFeel());
				}
			else{
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");			//ok
				//OyoahaLookAndFeel lnf = new OyoahaLookAndFeel(true);
				//UIManager.setLookAndFeel(lnf);
				}

			//UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel"); //ok
			//UIManager.setLookAndFeel("oyoahalnfb.com.oyoaha.swing.plaf.oyoaha.OyoahaLookAndFeel");
			//UIManager.setLookAndFeel(new org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel());
			}
		catch(Exception e){
			System.out.println("Error Loading Look n Feel\n" + e.getMessage());
			e.printStackTrace();
			}

		login = new DLogin(dir, "runtime.xml");
		getContentPane().add(login.mainpanel);
		}

	//arguments 0=dir, 1 = Look n Feel, 2 = Title, 3 = Image for login
  	public static void main (String [] args) {	// run an application


		//Class oclass = Class.forName("com.oyoaha.swing.plaf.oyoaha.OyoahaLookAndFeel");
        //Object olnf = oclass.newInstance();

		try {

			//UIManager.setLookAndFeel(ch.randelshofer.quaqua.QuaquaManager.getLookAndFeelClassName());

			DLogin login;

			String dir = "../../systems/";
			String bg_image = "logo.jpg";
			//imagedir = "../../systems/images/";

			String mylook = null;
			if (args.length>0)
				dir = args[0];
			else if(System.getenv("BARAZA_CONF")!=null)
				dir = System.getenv("BARAZA_CONF");

			if (args.length==2){
				mylook = args[1];
				System.out.println("LnF = " + mylook);
				}

			String title = "Open Sesame";
			if (args.length==3){
				title = args[2];
				System.out.println("Title = " + title);
				}
			if (args.length == 4){
				bg_image = args[3];
				System.out.println("Background Image = " + bg_image);
				}


			if(mylook==null){
				UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");			//original
				//OyoahaLookAndFeel lnf = new OyoahaLookAndFeel();
				//UIManager.setLookAndFeel(lnf);

				//test 1
/*				Skin theSkinToUse = SkinLookAndFeel.loadThemePack("themepack.zip");
				SkinLookAndFeel.setSkin(theSkinToUse);

				// finally set the Skin Look And Feel
				UIManager.setLookAndFeel(new SkinLookAndFeel());

					*/
				//test
				//UIManager.setLookAndFeel(ch.randelshofer.quaqua.QuaquaManager.getLookAndFeelClassName());

// 				System.setProperty("Quaqua.design", "jaguar");
// 				System.setProperty("Quaqua.TabbedPane.design", "jaguar");
// 				System.setProperty("Quaqua.FileChooser.autovalidate", "true");
				}
			else if(mylook.compareToIgnoreCase("neutral")==0){
				String lookAndFeelType = UIManager.getCrossPlatformLookAndFeelClassName();
				UIManager.setLookAndFeel(lookAndFeelType);
				}
			else if(mylook.compareToIgnoreCase("system")==0){
				String lookAndFeelType = UIManager.getSystemLookAndFeelClassName();
				UIManager.setLookAndFeel(lookAndFeelType);
				}
			else if(mylook.compareToIgnoreCase("liquid")==0)
				UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");		//ok
			else if(mylook.compareToIgnoreCase("plastic")==0)
				UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");		//ok
			else if(mylook.compareToIgnoreCase("quaqua")==0){
				UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");			//original
				}
			else{
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");			//ok
				//OyoahaLookAndFeel lnf = new OyoahaLookAndFeel(true);
				//UIManager.setLookAndFeel(lnf);
				}

			//UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel"); //ok
			//UIManager.setLookAndFeel("oyoahalnfb.com.oyoaha.swing.plaf.oyoaha.OyoahaLookAndFeel");
			//UIManager.setLookAndFeel(new org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel());

			frame.setDefaultLookAndFeelDecorated(true);

			frame = new JFrame(title);

			login = new DLogin(dir, "runtime.xml");
			frame.add(login.mainpanel);

			Toolkit tk = Toolkit.getDefaultToolkit();
			int w = ((int) tk.getScreenSize().getWidth());
			int h = ((int) tk.getScreenSize().getHeight());

			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			//opacity test
// 			try {
// 			Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
// 			Method mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
// 			mSetWindowOpacity.invoke(null, frame, Float.valueOf(0.75f));
// 				} catch (NoSuchMethodException ex) {
// 				ex.printStackTrace();
// 				} catch (SecurityException ex) {
// 				ex.printStackTrace();
// 				} catch (ClassNotFoundException ex) {
// 				ex.printStackTrace();
// 				} catch (IllegalAccessException ex) {
// 				ex.printStackTrace();
// 				} catch (IllegalArgumentException ex) {
// 				ex.printStackTrace();
// 				} catch (InvocationTargetException ex) {
// 				ex.printStackTrace();
// 				}

			//end opacity test


			//frame.setSize(w,h);
			//frame.setSize(940, 700);	//fix windows bug...force resize
			frame.setSize(1024,768);
			frame.setVisible(true);

			//frame.pack();

			}
		catch(Exception e){
			System.out.println("Error Loading Look n Feel\n" + e.getMessage());
			e.printStackTrace();
			}
// 		} catch (UnsupportedLookAndFeelException e) {
// 			System.out.println("Unsupported Look n Feel\n" + e.getMessage());
// 		} catch (ClassNotFoundException e) {
// 			System.out.println("Error Looding Look n Feel\n" + e.getMessage());
// 		} catch (InstantiationException e) {
// 			System.out.println("Instantiation Exception\n:" + e.getMessage() );
// 		} catch (IllegalAccessException e) {
// 			System.out.println("Illegal Access Exception\n:" + e.getMessage() );
// 		}



  		}

}
