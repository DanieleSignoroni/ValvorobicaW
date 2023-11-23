package it.valvorobica.thip.base.portal.web;

import java.io.PrintWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.thera.thermfw.persist.CachedStatement;
import com.thera.thermfw.web.ServletEnvironment;
import com.thera.thermfw.web.servlet.BaseServlet;

import it.thera.thip.base.generale.ParametroPsn;

public class YRelationsLoader extends BaseServlet{

	private static final long serialVersionUID = 1L;

	public static String BOClassName = "CatalogoArticolo";

	public String[] relationsToExclude;

	protected void processAction(ServletEnvironment se) throws Exception {
		fillAttNames(se.getResponse().getWriter());
	}

	@SuppressWarnings("rawtypes")
	public void fillAttNames(Writer writer)
			throws SQLException {
		try {
			String relazioniDaEscludere = ParametroPsn.getValoreParametroPsn("YImmaginiCatalogo", "RelazioniEscludere");
			if(relazioniDaEscludere != null) {
				relationsToExclude = relazioniDaEscludere.split(";");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		PrintWriter out = new PrintWriter(writer);
		out.println("<script language='JavaScript1.2'>");
		Iterator it = getRelationsList().iterator();
		out.println("parent.addBoAtt('-', '-','BORelation');");
		out.println("parent.addBoAtt('Vista catalogo', 'Vista catalogo','BORelation');");
		while (it.hasNext()) {
			YOggettoRs ogg = (YOggettoRs) it.next();
			if(relationsToExclude != null) {
				if(!isDaEscludere(ogg)) {
					out.println("parent.addBoAtt('" + ogg.businessClass + "', '"+ ogg.description + "','BORelation');");
				}
			}

		}
		out.println("</script>");
	}

	protected boolean isDaEscludere(YOggettoRs ogg) {
		for (int i = 0; i < relationsToExclude.length; i++) {
			String relationName = relationsToExclude[i];
			if(relationName.equals(ogg.relationName)) {
				return true;
			}
		}
		return false;
	}

	protected List<YOggettoRs> getRelationsList(){
		List<YOggettoRs> ret = new ArrayList<YRelationsLoader.YOggettoRs>();
		ResultSet rs = null;
		CachedStatement cs = null;
		String stmt = "SELECT  "
				+ "R.RELATION_NAME ,H.BO_CLASS_NAME,H.DESCRIPTION  "
				+ "FROM THERA.CLASSRD R "
				+ "INNER JOIN THERA.CLASS_HDR H "
				+ "ON R.CORR_CLASS_NAME = H.CLASS_NAME  "
				+ "WHERE R.CLASS_NAME = '"+BOClassName+"'";
		try {
			cs = new CachedStatement(stmt);
			rs = cs.executeQuery();
			while(rs.next()) {
				YOggettoRs ogg = new YOggettoRs();
				ogg.relationName = rs.getString(1);
				ogg.businessClass = rs.getString(2);
				ogg.description= rs.getString(3);
				ret.add(ogg);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				if(rs != null) {
					rs.close();
				}
			}catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	public class YOggettoRs{
		public String relationName;
		public String businessClass;
		public String description;
	}
}
