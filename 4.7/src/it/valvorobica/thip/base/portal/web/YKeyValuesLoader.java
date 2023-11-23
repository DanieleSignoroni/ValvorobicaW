package it.valvorobica.thip.base.portal.web;

import java.io.PrintWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.web.ServletEnvironment;
import com.thera.thermfw.web.servlet.BaseServlet;

import it.thera.thip.base.azienda.Azienda;

public class YKeyValuesLoader extends BaseServlet{

	private static final long serialVersionUID = 1L;

	protected void processAction(ServletEnvironment se) throws Exception {
		String classHdrName = getStringParameter(se.getRequest(), "ClassName");
		if (classHdrName != null && !classHdrName.equals("")) {
			fillValues(se.getResponse().getWriter(),classHdrName);
		}
	}

	@SuppressWarnings("rawtypes")
	public void fillValues(Writer writer,String className)
			throws SQLException {
		PrintWriter out = new PrintWriter(writer);
		out.println("<script language='JavaScript1.2'>");
		Iterator it = getListDataObjects(className).iterator();
		out.println("parent.addBoAtt('-', '-','BORelation');");
		while (it.hasNext()) {
			PersistentObject ogg = (PersistentObject) it.next();
			out.println("parent.addBoAtt('" + KeyHelper.getTokenObjectKey(ogg.getKey(),2) + "', '"+ KeyHelper.getTokenObjectKey(ogg.getKey(),2) + "','KeyValue');");
		}
		out.println("parent.document.getElementById('IdClassificazione').value = parent.document.getElementById('KeyValue').selectedOptions[0].innerHTML.trim();");
		out.println("</script>");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<PersistentObject> getListDataObjects(String className) {
		List contatti = new ArrayList<PersistentObject>();
		try {
			if(className.equals("Vista catalogo")) {
				className = "it.thera.thip.base.catalogo.CatalogoVista";
			}
			PersistentObject dataObject = (PersistentObject) Factory.createObject(className);
			contatti = PersistentObject.retrieveList(dataObject, " ID_AZIENDA = '"+Azienda.getAziendaCorrente()+"' ", "", false);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return contatti;
	}

}
