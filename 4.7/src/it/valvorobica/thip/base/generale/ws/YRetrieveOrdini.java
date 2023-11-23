package it.valvorobica.thip.base.generale.ws;

import java.util.ArrayList;
import java.util.Map;

import com.thera.thermfw.base.Trace;
import com.thera.thermfw.persist.ConnectionDescriptor;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.security.Security;
import com.thera.thermfw.web.SessionEnvironment;

import it.thera.thip.ws.GenericQuery;
import it.valvorobica.thip.base.generale.ws.dati.YDocumentoBase;
import it.valvorobica.thip.base.generale.ws.dati.YFiltri;
import it.valvorobica.thip.base.portal.YUserPortalSession;

/**
 * <h1>Softre Solutions</h1>
 * 
 * @author Daniele Signoroni 09/02/2023 <br>
 *         </br>
 *         <b>70921 DSSOF3 09/02/2023</b> Nuova gestione portale, tutti i dati
 *         vengono reperiti tramtite ws. <b>71003 DSSOF3 17/03/2023</b> <br>
 *         Visto che le anagrafiche non sono condivise tra aziende, per ora e'
 *         stata parametrizzata l'azienda 001 in where.<br>
 */

public class YRetrieveOrdini extends YRetrieve {

	public ArrayList<YDocumentoBase> iOrdini = new ArrayList<YDocumentoBase>();

	// @SuppressWarnings("rawtypes")
	// @Override
	// protected String buildOutputFormat(Map m) {
	// String res = null;
	//
	// try {
	// YDatiOrdini dati = new YDatiOrdini(getFiltri());
	// Gson gson = new Gson();
	// res = gson.toJson(dati);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// return res;
	// }

	protected YUserPortalSession userPortalSession; // aggiunto con nuova gestione portale

	public YUserPortalSession getUserPortalSession() {
		return userPortalSession;
	}

	public void setUserPortalSession(YUserPortalSession userPortalSession) {
		this.userPortalSession = userPortalSession;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Map execute(Map m) {
		this.popolaListaOrdini(this.getFiltri());
		m.put("datiOrd", this);
		return m;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void popolaListaOrdini(YFiltri filtri) {
		Map values = null;
		boolean isopen = false;
		Object[] info = SessionEnvironment.getDBInfoFromIniFile();
		String dbName = (String) info[0];
		String select = "SELECT ID_NUMERO_ORD, FORMAT(DATA_ORDINE,'yyyy-MM-dd') AS DATA_ORDINE, "
				+ "'SEDE DI', COALESCE(NUM_ORD_CLIENTE,'-') AS NUM_ORD_CLIENTE, "
				+ "COALESCE(LOCALITA_DEST,'-') AS LOCALITA_DEST, VLR_TOT_ORD, ID_AZIENDA, ID_ANNO_ORDINE, A.ANARASOC	";
		String from = "FROM THIP.ORDVEN_RIG_VISTA_V01 LEFT OUTER JOIN FINANCE.BBANAPT A"
				+ " ON A.ANACD = THIP.ORDVEN_RIG_VISTA_V01.R_ANAGRAFICO ";
		String where = filtri.costruisciWhereBase();
		if (filtri.getAnno() != null && !filtri.getAnno().equals(""))
			where += " AND ID_ANNO_ORDINE LIKE '%" + filtri.getAnno() + "%' ";
		if (filtri.getMese() != null && !filtri.getMese().equals(""))
			where += " AND MONTH(DATA_ORDINE) LIKE '%" + filtri.getMese() + "%' ";
		if (filtri.getGiorno() != null && !filtri.getGiorno().equals(""))
			where += " AND DAY(DATA_ORDINE) LIKE '%" + filtri.getGiorno() + "%' ";
		if (filtri.getNumeroInterno() != null && !filtri.getNumeroInterno().equals(""))
			where += " AND ID_NUMERO_ORD LIKE '%" + filtri.getNumeroInterno() + "%' ";
		if (filtri.getNumeroEsterno() != null && !filtri.getNumeroEsterno().equals(""))
			where += " AND NUM_ORD_CLIENTE LIKE '%" + filtri.getNumeroEsterno() + "%' ";
		if (filtri.getIdArticolo() != null && !filtri.getIdArticolo().equals(""))
			where += " AND R_ARTICOLO LIKE '%" + filtri.getIdArticolo() + "%' ";
		String groupBy = " GROUP BY ID_AZIENDA,ID_ANNO_ORDINE,ID_NUMERO_ORD,DATA_ORDINE,NUM_ORD_CLIENTE,"
				+ "LOCALITA_DEST,VLR_TOT_ORD,A.ANARASOC ";

		// where += " AND ID_AZIENDA = '001' "; //DSSOF3 Necessaria, in quanto le
		// anagrafiche non sono condivise tra aziende, per ora prametrizzata

		where += " AND ID_AZIENDA = '" + this.getUserPortalSession().getIdAzienda() + "'"; // nuova gestione Multi azi

		String query = select + from + where + groupBy;
		try {
			if (!Security.isCurrentDatabaseSetted()) {
				Security.setCurrentDatabase(dbName, null);
			}
			Security.openDefaultConnection();
			isopen = true;
			ConnectionDescriptor cd = ConnectionManager.getCurrentConnectionDescriptor();
			GenericQuery gq = new GenericQuery();
			gq.getAppParams().put("query", query);
			gq.setUseAuthentication(false);
			gq.setUseAuthorization(false);
			gq.setUseLicence(false);
			gq.setConnectionDescriptor(cd);
			values = gq.send();
			ArrayList records = (ArrayList) values.get("records");
			for (int i = 0; i < records.size(); i++) {
				YDocumentoBase ord = new YDocumentoBase();
				ArrayList valuesRecords = (ArrayList) records.get(i);
				ord.setNro(valuesRecords.get(0).toString().trim());
				ord.setData(valuesRecords.get(1).toString().trim());
				ord.setStabilimento(valuesRecords.get(2).toString().trim());
				ord.setRifVsOff(valuesRecords.get(3).toString().trim());
				ord.setConsegna(valuesRecords.get(4).toString().trim());
				ord.setImponibile(valuesRecords.get(5).toString().trim());
				String key = valuesRecords.get(6).toString().trim() + KeyHelper.KEY_SEPARATOR
						+ valuesRecords.get(7).toString().trim() + KeyHelper.KEY_SEPARATOR + ord.getNro();
				ord.setKey(key);
				ord.setRagioneSociale(valuesRecords.get(8).toString().trim());
				iOrdini.add(ord);
			}
		} catch (Throwable t) {
			t.printStackTrace(Trace.excStream);
		} finally {
			if (isopen) {
				Security.closeDefaultConnection();
			}
		}
	}

}