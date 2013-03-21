package org.elegance;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.*;

public class DReportViewer extends JRViewer {

	public DReportViewer(JasperPrint jrPrint) throws JRException {
		super(jrPrint);
	}

	public void loadReport(JasperPrint jrPrint) {
		super.loadReport(jrPrint);
	}

	public void refreshPage() {
		super.refreshPage();
	}
}
