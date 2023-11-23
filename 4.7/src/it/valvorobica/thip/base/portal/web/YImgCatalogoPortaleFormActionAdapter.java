package it.valvorobica.thip.base.portal.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;

import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.web.MultipartFile;
import com.thera.thermfw.web.MultipartHandler;
import com.thera.thermfw.web.ServletEnvironment;
import com.thera.thermfw.web.WebElement;
import com.thera.thermfw.web.servlet.FormActionAdapter;

import it.valvorobica.thip.base.portal.YImgCatalogoPortale;

public class YImgCatalogoPortaleFormActionAdapter extends FormActionAdapter {

	private static final long serialVersionUID = 1L;

	public static final String CARICA = "CARICA";

	public void processAction(ServletEnvironment se) throws ServletException, IOException {
		String action = se.getRequest().getParameter("thAction");
		if (action == null || (action != null && action.equals(CARICA))) {
			MultipartHandler mh = new MultipartHandler(se);
			mh.initialize();
			carica(se, mh);
		} else
			super.processAction(se);
	}

	protected void carica(ServletEnvironment se, MultipartHandler mh) throws IOException, ServletException {
		PrintWriter out = se.getResponse().getWriter();
		out.println("<script type=\"text/javascript\">");
		out.println("parent.disableMultipart();");
		out.println("parent.fireActionCompleted();");
		if (!mh.getMultipartFiles().isEmpty()) {
			Map<String, MultipartFile> mapFileCorrel = mh.getMultipartFilesMap();
			for (Entry<String, MultipartFile> fileInfo : mapFileCorrel.entrySet()) {
				File xslFile = saveFile(se, mh, fileInfo.getValue());
				if (xslFile != null) {
					String name = xslFile.getAbsolutePath();
					out.println("parent.valorizzaNomeImmagine('" + WebElement.formatStringForHTML(name) + "');");
				}
			}
		}
		if (se.isErrorListEmpity()) {
			out.println(
					"parent.runActionDirect('SAVE', 'action_submit', parent.document.getElementById('thClassName').value, '', 'errorsFrame', 'no');");
		} else {
			se.sendRequest(getServletContext(), "com/thera/thermfw/common/ErrorListHandler.jsp", true);
		}
		out.println("</script>");
	}

	protected File saveFile(ServletEnvironment se, MultipartHandler mh, MultipartFile mf) {
		File imgFile = null;
		String rootPath = se.getSession().getServletContext().getRealPath("") + "\\" + "ImmaginiPortale";
		MultipartFile fileMH = (MultipartFile) mh.getMultipartFiles().get(0);
		try {
			String locFileName = null;
			String objectKey = mh.getMultipartRequest().getParameter(KEY);
			if (objectKey != null && !objectKey.isEmpty()) {
				try {
					YImgCatalogoPortale immagine = YImgCatalogoPortale.elementWithKey(objectKey,
							PersistentObject.OPTIMISTIC_LOCK);
					if (immagine != null && immagine.retrieve()) {
						locFileName = immagine.getTipoClassificazione() + "-" + immagine.getIdClassificazione()
								+ getExtensionFromName(fileMH.getFileName());
						String nomeFile = "" + immagine.getTipoClassificazione() + "-" + immagine.getIdClassificazione()
								+ getExtensionFromName(fileMH.getFileName());
						File file = new File(nomeFile);
						if (file.exists()) {
							file.delete();
							imgFile = new File(rootPath, locFileName);
							mf.save(locFileName);
						} else {
							imgFile = new File(rootPath, locFileName);
							mf.save(imgFile);
						}
						String urlImg = "";
						if (urlImg != null && !urlImg.isEmpty()) {
							File img = new File(rootPath, locFileName);
							if (img.exists())
								locFileName = img.getName();
						}
					}
				} catch (SQLException ignore) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imgFile;
	}

	protected String getExtensionFromName(String fileName) {
		return fileName.substring(fileName.lastIndexOf("."), fileName.length());
	}

}
