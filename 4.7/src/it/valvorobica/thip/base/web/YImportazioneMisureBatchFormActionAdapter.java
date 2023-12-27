package it.valvorobica.thip.base.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.ServletException;

import com.thera.thermfw.ad.ClassADCollection;
import com.thera.thermfw.base.IniFile;
import com.thera.thermfw.base.TimeUtils;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.base.Utils;
import com.thera.thermfw.batch.web.BatchFormActionAdapter;
import com.thera.thermfw.common.ErrorMessage;
import com.thera.thermfw.web.MultipartFile;
import com.thera.thermfw.web.MultipartHandler;
import com.thera.thermfw.web.ServletEnvironment;
import com.thera.thermfw.web.WebElement;
import com.thera.thermfw.web.WebToolBar;

/**
 * <h1>Softre Solutions</h1>
 * <br>
 * @author Daniele Signoroni 22/12/2023
 * <br><br>
 * <b>71366	DSSOF3	22/12/2023</b>    <p>Gestione file di importazione</p>
 */

public class YImportazioneMisureBatchFormActionAdapter extends BatchFormActionAdapter {

	private static final long serialVersionUID = 1L;

	public static final String YVALVO_004 = "YVALVO_004";

	@Override
	public void modifyToolBar(WebToolBar toolBar) {
		super.modifyToolBar(toolBar);
		toolBar.removeButton("Tickler");
		toolBar.removeButton("RunAndNewBatch");
		toolBar.removeButton("CheckAll");
	}

	public void processAction(ServletEnvironment se) throws ServletException, IOException {
		String action = se.getRequest().getParameter(ACTION);
		if (action != null && !(action.equals(RUN_BATCH_ACTION))) {
			super.processAction(se);
		}else {
			String url;
			MultipartHandler mh = new MultipartHandler(se);
			mh.initialize();
			action = mh.getMultipartRequest().getParameter(ACTION);
			if (action.equals(RUN_AND_NEW_BATCH_ACTION) || action.equals(RUN_BATCH_ACTION)) {
				ClassADCollection cadc = getClassADCollection(mh.getMultipartRequest().getParameter(CLASS_NAME));
				setCurrentViewSelector(getViewSelector(cadc, se));
				url = getCurrentViewSelector().getSaveBatchURL(se, false, true);
				url = buildParameterRequest(mh, url);
				saveBatch(se, mh, url);
			}
		}
	}

	@SuppressWarnings({ "deprecation", "unused" })
	protected void saveBatch(ServletEnvironment se, MultipartHandler mh, String url)
			throws ServletException, IOException {
		File tmpFile = handleFile(se, mh);
		if (tmpFile != null) {
			PrintWriter out = se.getResponse().getWriter();
			out.println("<script type=\"text/javascript\">");
			out.println("parent.disableMultipart();");
			out.println("parent.document.getElementById('NomeFile').value = '"
					+ WebElement.formatStringForHTML(tmpFile.getAbsolutePath()) + "';");
			out.println("</script>");
			// Metto il file in sessione (lo recupero dopo)
			// Lancio la servlet di save
			if (tmpFile != null) {
				url += URLEncoder.encode("TemporaryFileName") + "=" + URLEncoder.encode(tmpFile.getAbsolutePath());
				MultipartFile mf = (MultipartFile) mh.getMultipartFiles().get(0);
				url += "&" + URLEncoder.encode("NomeFileTemp") + "=" + URLEncoder.encode(mf.getFileName());

			} else
				url = url.substring(0, url.length() - 1);
		} else {
			url = "com/thera/thermfw/common/ErrorListHandler.jsp";
			url += "?thClassName=" + mh.getMultipartRequest().getParameter(CLASS_NAME); // serve non chiedetemi perche'
		}
		se.sendRequest(getServletContext(), url, true);
	}

	@SuppressWarnings("rawtypes")
	protected String buildParameterRequest(MultipartHandler mh, String url) throws ServletException, IOException {
		if (url.indexOf("?") == -1)
			url += "?";
		else
			url += "&";
		url = removeQueryStringExecutePrint(url);

		java.util.Enumeration e = mh.getMultipartRequest().getParameterNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			url += getQueryString(mh, name);
		}
		return url;

	}

	@SuppressWarnings("deprecation")
	private String getQueryString(MultipartHandler mh, String name) {
		String par = "";
		String[] values = mh.getMultipartRequest().getParameterValues(name);
		for (int i = 0; i < values.length; i++) {
			par += URLEncoder.encode(name) + "=" + URLEncoder.encode(values[i]) + "&";
		}
		return par;
	}

	private String removeQueryStringExecutePrint(String url) {
		int index = url.indexOf("ExecutePrint=null&");
		if (index != -1) {
			url = url.substring(0, index);
		}
		return url;
	}

	protected File handleFile(ServletEnvironment se, MultipartHandler mh) {
		try {
			// Il file EDI è obbligatorio
			if (mh.getMultipartFiles().size() == 0) {
				se.addErrorMessage(new ErrorMessage("BAS0000000", "Il file e' obbligatorio"));
				return null;
			}
			// Il file EDI deve esistere e non essere vuoto
			MultipartFile mf = (MultipartFile) mh.getMultipartFiles().get(0);
			if (mf.getSize() == 0) {
				se.addErrorMessage(new ErrorMessage("BAS0000000", "Il file e' obbligatorio"));
				return null;
			}
			if (!(mf.getFileName().endsWith("csv"))) {
				se.addErrorMessage(new ErrorMessage(YVALVO_004, "File con formato errato \n Formati accettati: .xlsx"));
				return null;
			}
			// Crea un file temporaneo in cui parcheggiare il file ricevuto
			File tmpFile = File.createTempFile("YMisureTmp", ".xlsx");
			// tmpFile.deleteOnExit(); questo per cancellare file quando VM finisce, cosa
			// dici?
			mf.save(tmpFile);
			return tmpFile;
		} catch (Exception e) {
			e.printStackTrace(Trace.excStream);
			return null;
		}
	}

	protected String getParameter(ServletEnvironment se, MultipartHandler mh, String name) {
		return mh.getMultipartRequest().getParameter(name);
	}

	protected String getApplicactionPathString(ServletEnvironment se) {
		return se.getRequest().getScheme() + "://" + se.getRequest().getServerName() + ":"
				+ se.getRequest().getServerPort() + "/" + IniFile.getValue("thermfw.ini", "Web", "WebApplicationPath")
				+ "/";
	}

	public String getNameFileFormatted(ServletEnvironment se, String originalFileName) {
		String nameSenzaEstensione = extractFileName(originalFileName);
		String ct = TimeUtils.getCurrentTimestamp().toString();
		ct = Utils.replace(ct, "-", "");
		ct = Utils.replace(ct, ":", "");
		ct = Utils.replace(ct, ".", "");
		ct = Utils.replace(ct, " ", "");
		return Utils.replace(originalFileName, nameSenzaEstensione,
				nameSenzaEstensione + "_" + ct + "_" + se.getUser().getId());
	}

	public static String extractFileName(String filePathName) {
		if (filePathName == null)
			return null;

		int dotPos = filePathName.lastIndexOf('.');
		int slashPos = filePathName.lastIndexOf('\\');
		if (slashPos == -1)
			slashPos = filePathName.lastIndexOf('/');

		if (dotPos > slashPos) {
			return filePathName.substring(slashPos > 0 ? slashPos + 1 : 0, dotPos);
		}
		return filePathName.substring(slashPos > 0 ? slashPos + 1 : 0);
	}

}
