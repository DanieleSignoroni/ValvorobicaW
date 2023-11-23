package it.valvorobica.thip.base.portal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.thera.thermfw.base.IniFile;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.persist.ConnectionDescriptor;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.security.Security;
import com.thera.thermfw.web.SessionEnvironment;

import it.thera.thip.ws.GenericQuery;

/**
 * 
 * @author sviluppo1
 *
 *         Oggetto di appoggio con tutte le info necessarie relative all'utente
 *         che si è loggato. Questo oggetto verrà messo in sessione dopo il
 *         login
 */

public class YUserPortalSession {

	public static final String CLIENTE = "C";

	public static final String WEB_APP_PATH = IniFile.getValue("thermfw.ini", "Web", "WebApplicationPath");

	public static final String AGENTE = "A";

	public static final String DIPENDENTE = "D";

	public static final String FORNITORE = "F"; // 71162

	protected String iIdAzienda;

	protected String iTipoUser;

	protected String iIdAgente;

	protected String iIdCliente;

	protected String iIdDipendente;

	protected String iIdUtente;

	protected String iTokecUID;

	protected boolean iMultipleEnv; // aggiunto att per capire se l'utente e' multi-azienda

	protected String iSupplierOfferKey;

	protected String iIdFornitore;

	// json catalogo

	protected String jsonCatalogo = null;

	public HashMap<String, String> dettagli_caricati = new HashMap<String, String>(); // dettagli catalogo caricati,
																						// cosi da non fare retrieve
																						// ogni volta!!

	public String getJsonCatalogo() {
		return jsonCatalogo;
	}

	public void setJsonCatalogo(String jsonCatalogo) {
		this.jsonCatalogo = jsonCatalogo;
	}

	public YUserPortalSession(String tipoUser, String idAgente, String idCliente, String idDipendente, String idUtente,
			String token, String azienda) {
		this.setTipoUser(tipoUser);
		this.setIdAgente(idAgente);
		this.setIdCliente(idCliente);
		this.setIdDipendente(idDipendente);
		this.setIdUtente(idUtente);
		this.setTokecUID(token);
		this.setIdAzienda(azienda);
	}

	public String getIdAzienda() {
		return iIdAzienda;
	}

	public void setIdAzienda(String iIdAzienda) {
		this.iIdAzienda = iIdAzienda;
	}

	public String getTipoUser() {
		return iTipoUser;
	}

	public void setTipoUser(String iTipoUser) {
		this.iTipoUser = iTipoUser;
	}

	public String getIdAgente() {
		return iIdAgente;
	}

	public void setIdAgente(String iIdAgente) {
		this.iIdAgente = iIdAgente;
	}

	public String getIdCliente() {
		return iIdCliente;
	}

	public void setIdCliente(String iIdCliente) {
		this.iIdCliente = iIdCliente;
	}

	public String getIdDipendente() {
		return iIdDipendente;
	}

	public void setIdDipendente(String iIdDipendente) {
		this.iIdDipendente = iIdDipendente;
	}

	public String getIdUtente() {
		return iIdUtente;
	}

	public void setIdUtente(String iIdUtente) {
		this.iIdUtente = iIdUtente;
	}

	public String getTokecUID() {
		return iTokecUID;
	}

	public void setTokecUID(String iTokecUID) {
		this.iTokecUID = iTokecUID;
	}

	/*
	 * -------------------------------- 71078 Inizio, booleano per capire se ci sono
	 * piu aziende --------------------------------
	 */

	public boolean isMultipleEnv() {
		return iMultipleEnv;
	}

	public void setMultipleEnv(boolean iMultipleEnv) {
		this.iMultipleEnv = iMultipleEnv;
	}

	/*
	 * -------------------------------- 71078 Fine --------------------------------
	 */

	public String getSupplierOfferKey() {
		return iSupplierOfferKey;
	}

	public void setSupplierOfferKey(String iSupplierOfferKey) {
		this.iSupplierOfferKey = iSupplierOfferKey;
	}

	public String getIdFornitore() {
		return iIdFornitore;
	}

	public void setIdFornitore(String iIdFornitore) {
		this.iIdFornitore = iIdFornitore;
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static BigDecimal getQtaOrdinataPerArticolo(String idAzienda, String idUtente, String idArticolo) {
		String select = "SELECT SUM(" + YCarrelloPortaleTM.QUANTITA + ") FROM " + YCarrelloPortaleTM.TABLE_NAME + " C ";
		String where = "WHERE C." + YCarrelloPortaleTM.ID_AZIENDA + " = '" + idAzienda + "'" + " AND C."
				+ YCarrelloPortaleTM.R_UTENTE_PORTALE + " = '" + idUtente + "' AND " + YCarrelloPortaleTM.R_ARTICOLO
				+ " = '" + idArticolo + "' ";
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
			return new BigDecimal(valuesRecords.get(0).toString().trim());
		} catch (Throwable t) {
			t.printStackTrace(Trace.excStream);
		} finally {
			if (isopen) {
				Security.closeDefaultConnection();
			}
		}
		return BigDecimal.ZERO;
	}

}