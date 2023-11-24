package it.valvorobica.thip.magazzino.chiusure.web;

import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.jsp.JspWriter;

import com.thera.thermfw.base.Trace;
import com.thera.thermfw.persist.CachedStatement;
import com.thera.thermfw.web.WebFormModifier;

import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.magazzino.chiusure.PeriodoCalFiscaleTM;
import it.valvorobica.thip.magazzino.chiusure.YValorizzazioneFiscaleValvo;

/**
 * <h1>Softre Solutions</h1> <br>
 * 
 * @author Daniele Signoroni 24/11/2023 <br>
 *         <br>
 *         <b>71310 DSSOF3 24/11/2023</b>
 *         <p>
 *         </p>
 */

public class YValorizzazioneFiscaleValvoFormModifier extends WebFormModifier {

	@Override
	public void writeHeadElements(JspWriter out) throws IOException {

	}

	@Override
	public void writeBodyStartElements(JspWriter out) throws IOException {
		YValorizzazioneFiscaleValvo bo = (YValorizzazioneFiscaleValvo) getBODataCollector().getBo();
		Object[] datiUltimaChiu = recuperaDataUltimaChiusuraMagazzinoDef();
		if (datiUltimaChiu != null) {
			if (datiUltimaChiu[0] != null) {
				bo.setDataUltimaChiusura((Date) datiUltimaChiu[0]);
			}
			if (datiUltimaChiu[1] != null) {
				bo.setStatoChiusuraMag((Character) datiUltimaChiu[1]);
			}
		}

	}

	@Override
	public void writeFormStartElements(JspWriter out) throws IOException {

	}

	@Override
	public void writeFormEndElements(JspWriter out) throws IOException {

	}

	@Override
	public void writeBodyEndElements(JspWriter out) throws IOException {

	}

	protected Object[] recuperaDataUltimaChiusuraMagazzinoDef() {
		java.sql.Date d = null;
		ResultSet rs = null;
		char esito = 'N';
		CachedStatement cs = null;
		try {
			String query = "SELECT p.DATA_FINE_PER,p." + PeriodoCalFiscaleTM.ID_ANNO_FISCALE + ",p."
					+ PeriodoCalFiscaleTM.STATO_CHIUS_MAG + " " + "FROM THIP.PER_CALEN_FSC p " + "WHERE ID_AZIENDA = '"
					+ Azienda.getAziendaCorrente() + "'  " + "AND STATO_CHIUS_MAG IN ('D','P') "
					+ "ORDER BY p.DATA_FINE_PER DESC";
			cs = new CachedStatement(query);
			rs = cs.executeQuery();
			if (rs.next()) {
				d = rs.getDate("DATA_FINE_PER");
				esito = rs.getString(PeriodoCalFiscaleTM.STATO_CHIUS_MAG).charAt(0);
			}
		} catch (SQLException e) {
			e.printStackTrace(Trace.excStream);
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (cs != null)
					cs.free();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return new Object[] { d, esito };
	}

}
