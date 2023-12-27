package it.valvorobica.thip.base.web;

import java.io.IOException;

import javax.servlet.ServletException;

import com.thera.thermfw.ad.ClassADCollection;
import com.thera.thermfw.web.ServletEnvironment;
import com.thera.thermfw.web.WebToolBar;
import com.thera.thermfw.web.WebToolBarButton;
import com.thera.thermfw.web.servlet.GridActionAdapter;

/**
 * <h1>Softre Solutions</h1>
 * <br>
 * @author Daniele Signoroni 22/12/2023
 * <br><br>
 * <b>71366	DSSOF3	22/12/2023</b>    <p>Aggiungere bottone per importazione misure da csv</p>
 */

public class YMisureGridActionAdapter extends GridActionAdapter {

	private static final long serialVersionUID = 1L;

	public static final String IMPORTA_MISURE = "IMPORTA_MISURE";
	public static final String RES = "it.valvorobica.thip.base.resources.YImportazioneMisureBatch";
	public static final String IMPORTA_MISURE_IMG = "it/valvorobica/thip/base/img/Export.gif";

	@Override
	public void modifyToolBar(WebToolBar toolBar) {
		super.modifyToolBar(toolBar);
		WebToolBarButton emettiCert = new WebToolBarButton(IMPORTA_MISURE, "action_submit", "new", "no", RES,
				IMPORTA_MISURE, IMPORTA_MISURE_IMG, IMPORTA_MISURE, "none", false);
		toolBar.addButton(emettiCert);
	}

	@Override
	protected void otherActions(ClassADCollection cadc, ServletEnvironment se) throws ServletException, IOException {
		String action = se.getRequest().getParameter(ACTION);
		if (action.equals(IMPORTA_MISURE)) {
			se.sendRequest(getServletContext(), "it/valvorobica/thip/base/YImportazioneMisureBatch.jsp", true);
		} else {
			super.otherActions(cadc, se);
		}
	}
}
