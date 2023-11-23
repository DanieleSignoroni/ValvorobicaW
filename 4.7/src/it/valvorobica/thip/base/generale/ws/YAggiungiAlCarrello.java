package it.valvorobica.thip.base.generale.ws;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import com.thera.thermfw.base.Trace;
import com.thera.thermfw.persist.ConnectionDescriptor;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.security.Security;
import com.thera.thermfw.web.SessionEnvironment;

import it.thera.thip.ws.GenRequestJSON;
import it.thera.thip.ws.GenericQuery;
import it.valvorobica.thip.base.portal.YCarrelloPortale;
import it.valvorobica.thip.base.portal.YCarrelloPortaleTM;

/**
 * <h1>Softre Solutions</h1> <br>
 * 
 * @author Daniele Signoroni 31/08/2023 <br>
 *         <br>
 *         <b>XXXXX DSSOF3 31/08/2023</b>
 *         <p>
 *         Prima stesura.<br>
 *         WebService usato per inserire un'articolo nel carrello: <br>
 *         </br>
 *         {@value YCarrelloPortaleTM#TABLE_NAME} <br>
 *         </p>
 */

public class YAggiungiAlCarrello extends GenRequestJSON {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Map execute(Map m) {
		String idArticolo = (String) this.getAppParams().get("articolo");
		if (idArticolo == null || idArticolo.isEmpty())
			m.put("errors", "Articolo vuoto");
		String quantita = (String) this.getAppParams().get("quantita");
		if (quantita == null)
			m.put("errors", "Quantita vuota");
		String idUtente = (String) this.getAppParams().get("idUtente");
		if (idUtente == null)
			m.put("errors", "Problemi con l'utente");
		String idAzienda = (String) this.getAppParams().get("idAzienda");
		String idCliente = (String) this.getAppParams().get("idCliente");
		if (idCliente == null)
			m.put("errors", "Cliente non selezionato, riloggarsi");
		if (m.get("errors") == null)
			addToCart(idArticolo, idAzienda, idUtente, quantita, idCliente, m);
		int nrItemsCarrello = getNumeroItemsCarrelloUtente(idAzienda, idUtente);
		m.put("nrItemsCart", nrItemsCarrello);
		return m;
	}

	@SuppressWarnings({ "rawtypes" })
	protected void addToCart(String idArticolo, String idAzienda, String idUtente, String quantita, String idCliente,
			Map m) {
		boolean isopen = false;
		Object[] info = SessionEnvironment.getDBInfoFromIniFile();
		String dbName = (String) info[0];
		try {
			if (!Security.isCurrentDatabaseSetted()) {
				Security.setCurrentDatabase(dbName, null);
			}
			Security.openDefaultConnection();
			isopen = true;
			ConnectionDescriptor cd = ConnectionManager.getCurrentConnectionDescriptor();
			YCarrelloPortale item = (YCarrelloPortale) Factory.createObject(YCarrelloPortale.class);
			item.setIdAzienda(idAzienda);
			item.setRUtentePortale(idUtente);
			item.setRCliente(idCliente);
			item.setQuantita(new BigDecimal(quantita));
			item.setRArticolo(idArticolo);
			if (item.save() >= 0) {
				cd.commit();
			} else {
				cd.rollback();
			}
		} catch (Throwable t) {
			t.printStackTrace(Trace.excStream);
		} finally {
			if (isopen) {
				Security.closeDefaultConnection();
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int getNumeroItemsCarrelloUtente(String idAzienda, String idUtente) {
		String select = "SELECT COUNT(*) FROM " + YCarrelloPortaleTM.TABLE_NAME + " C ";
		String where = "WHERE C." + YCarrelloPortaleTM.ID_AZIENDA + " = '" + idAzienda + "'" + " AND C."
				+ YCarrelloPortaleTM.R_UTENTE_PORTALE + " = '" + idUtente + "' ";
		Map values = null;
		boolean isopen = false;
		Object[] info = SessionEnvironment.getDBInfoFromIniFile();
		String dbName = (String) info[0];
		try {
			if (!Security.isCurrentDatabaseSetted()) {
				Security.setCurrentDatabase(dbName, null);
			}
			Security.openDefaultConnection();
			isopen = true;
			ConnectionDescriptor cd = ConnectionManager.getCurrentConnectionDescriptor();
			GenericQuery gq = new GenericQuery();
			gq.getAppParams().put("query", select + where);
			gq.setUseAuthentication(false);
			gq.setUseAuthorization(false);
			gq.setUseLicence(false);
			gq.setConnectionDescriptor(cd);
			values = gq.send();
			ArrayList records = (ArrayList) values.get("records");
			ArrayList valuesRecords = (ArrayList) records.get(0);
			return Integer.valueOf(valuesRecords.get(0).toString().trim());
		} catch (Throwable t) {
			t.printStackTrace(Trace.excStream);
		} finally {
			if (isopen) {
				Security.closeDefaultConnection();
			}
		}
		return 0;
	}

}
