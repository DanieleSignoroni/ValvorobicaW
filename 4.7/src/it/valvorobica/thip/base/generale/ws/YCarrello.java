package it.valvorobica.thip.base.generale.ws;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.thera.thermfw.base.Trace;
import com.thera.thermfw.persist.ConnectionDescriptor;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.security.Security;
import com.thera.thermfw.web.SessionEnvironment;

import it.thera.thip.base.articolo.ArticoloTM;
import it.thera.thip.vendite.generaleVE.ws.RicercaPrezzoEcomm;
import it.thera.thip.ws.GenRequestJSON;
import it.thera.thip.ws.GenericQuery;
import it.valvorobica.thip.base.portal.YCarrelloPortaleTM;
import it.valvorobica.thip.base.portal.YUserPortalSession;

/**
 * <h1>Softre Solutions</h1>
 * <br>
 * @author Daniele Signoroni 31/08/2023
 * <br><br>
 * <b>XXXXX	DSSOF3	31/08/2023</b>	<p>Prima stesura.<br>
 * 									   WebService per la retrieve e la visualizzazione del carrello di un utente di tipo cliente.<br>
 * 									   Retrieve su tabella {@value YCarrelloPortaleTM#TABLE_NAME}, recupero alcuni dati dalla tabella articoli,
 * 									   e il prezzo dal listino.
 * 									</p>
 */

public class YCarrello extends GenRequestJSON{

	protected YUserPortalSession user;

	public YUserPortalSession getUserPortalSession() {
		return user;
	}

	public void setUserPortalSession(YUserPortalSession user) {
		this.user = user;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Map execute(Map m) {
		BigDecimal totale = BigDecimal.ZERO;
		ArrayList<YCarrello.ItemCarrello> items = new ArrayList<YCarrello.ItemCarrello>();
		String select = "SELECT "
				+ " C."+YCarrelloPortaleTM.PROGRESSIVO+", "
				+ " C."+YCarrelloPortaleTM.R_ARTICOLO+", "
				+ " A."+ArticoloTM.DESC_ESTESA+", "
				+ " UPPER(A.R_UM_PRM_VEN) , "
				+ " C."+YCarrelloPortaleTM.QUANTITA+", "
				+ " C."+YCarrelloPortaleTM.R_CLIENTE+", "
				+ " C."+YCarrelloPortaleTM.TIMESTAMP_CRZ+", "
				+ " C."+YCarrelloPortaleTM.TIMESTAMP_AGG+","
				+ " SB.QTA_GIAC_PRM AS DISPONIBILITA "
				+ " FROM "+YCarrelloPortaleTM.TABLE_NAME+" C "
				+ " INNER JOIN "+ArticoloTM.TABLE_NAME+" A "
				+ " ON A."+ArticoloTM.ID_AZIENDA+" = C."+YCarrelloPortaleTM.ID_AZIENDA+" "
				+ " AND A."+ArticoloTM.ID_ARTICOLO+" = C."+YCarrelloPortaleTM.R_ARTICOLO+" "
				+ " INNER JOIN SOFTRE.Y_SALDI_BASE_V03 SB ON SB.ID_AZIENDA = A.ID_AZIENDA  AND SB.ID_ARTICOLO = A.ID_ARTICOLO ";
		String where = "WHERE C."+YCarrelloPortaleTM.ID_AZIENDA+" = '"+getUserPortalSession().getIdAzienda()+"'"
				+ " AND C."+YCarrelloPortaleTM.R_UTENTE_PORTALE+" = '"+getUserPortalSession().getIdUtente()+"' ";
		Map values = null;
		boolean isopen = false;		
		Object[] info = SessionEnvironment.getDBInfoFromIniFile();
		String dbName = (String)info[0];
		try {		
			if(!Security.isCurrentDatabaseSetted()) {
				Security.setCurrentDatabase(dbName, null);
			}
			Security.openDefaultConnection();
			isopen = true;
			ConnectionDescriptor cd = ConnectionManager.getCurrentConnectionDescriptor();
			GenericQuery gq = new GenericQuery();
			gq.getAppParams().put("query", select+where);
			gq.setUseAuthentication(false);
			gq.setUseAuthorization(false);
			gq.setUseLicence(false);
			gq.setConnectionDescriptor(cd);
			values = gq.send();		
			ArrayList records = (ArrayList) values.get("records");
			for (int i = 0; i < records.size(); i++) {
				ArrayList valuesRecords = (ArrayList) records.get(i);
				ItemCarrello item = new ItemCarrello();
				String idProgressivo = valuesRecords.get(0).toString().trim();
				String idArticolo = valuesRecords.get(1).toString().trim();
				String descExtArticolo =  valuesRecords.get(2).toString().trim();
				String umDefVen = valuesRecords.get(3).toString().trim();
				String quantita = valuesRecords.get(4).toString().trim();
				BigDecimal qta = new BigDecimal(quantita);
				String idCliente = valuesRecords.get(5).toString().trim();
				item.setIdProgressivo(idProgressivo);
				item.setIdArticolo(idArticolo);
				item.setIdCliente(idCliente);
				item.setQuantita(qta.setScale(4,RoundingMode.DOWN).toString());
				item.setDescrExtArticolo(descExtArticolo);
				item.setUmDefVen(umDefVen);
				items.add(item);
				String key = KeyHelper.buildObjectKey(new String[]{
					getUserPortalSession().getIdAzienda(),
					getUserPortalSession().getIdUtente(),
					idProgressivo
				});
				BigDecimal disp = new BigDecimal(valuesRecords.get(8).toString().trim());
				item.setDisponibilita(disp.setScale(4,RoundingMode.DOWN).toString());
				item.setKey(key);
				Map parametriRcPrz = new HashMap();
				parametriRcPrz.put("codArticolo", item.getIdArticolo());
				parametriRcPrz.put("codCliente", item.getIdCliente().trim());
				parametriRcPrz.put("tipoUMVendita", "V");
				parametriRcPrz.put("company", getUserPortalSession().getIdAzienda());
				parametriRcPrz.put("qtaRichiesta", item.getQuantita());
				parametriRcPrz.put("codListino", "VEN");
				RicercaPrezzoEcomm rcPrz = (RicercaPrezzoEcomm) Factory.createObject(RicercaPrezzoEcomm.class); //ricerco prezzo tramite ws std
				rcPrz.setUseAuthentication(false);
				rcPrz.setUseAuthorization(false);
				rcPrz.setUseLicence(false);
				rcPrz.setConnectionDescriptor(cd);
				rcPrz.setAppParams(parametriRcPrz);
				rcPrz.setQtaVenditaRich(new BigDecimal(item.getQuantita()));
				values = rcPrz.send();
				BigDecimal prezzo = BigDecimal.ZERO;
				if(values.get("prezzo") != null) {
					prezzo = (BigDecimal) values.get("prezzo");
				}
				totale = totale.add(prezzo.multiply(qta));
				item.setPrezzo(prezzo.setScale(4, RoundingMode.DOWN).toString());
			}
		}
		catch (Throwable t) {
			t.printStackTrace(Trace.excStream);
		}
		finally {
			if (isopen) {
				Security.closeDefaultConnection();
			}
		}
		m.put("total",totale);
		m.put("items", items);
		return m;
	}

	public class ItemCarrello {

		protected String idProgressivo;

		protected String idArticolo;

		protected String quantita;

		protected String idCliente;
		
		protected String descrExtArticolo;
		
		protected String umDefVen;
		
		protected String key;
		
		protected String prezzo;
		
		protected String disponibilita;

		public String getIdProgressivo() {
			return idProgressivo;
		}

		public void setIdProgressivo(String idProgressivo) {
			this.idProgressivo = idProgressivo;
		}

		public String getIdArticolo() {
			return idArticolo;
		}

		public void setIdArticolo(String idArticolo) {
			this.idArticolo = idArticolo;
		}

		public String getQuantita() {
			return quantita;
		}

		public void setQuantita(String quantita) {
			this.quantita = quantita;
		}

		public String getIdCliente() {
			return idCliente;
		}

		public void setIdCliente(String idCliente) {
			this.idCliente = idCliente;
		}

		public String getDescrExtArticolo() {
			return descrExtArticolo;
		}

		public void setDescrExtArticolo(String descrExtArticolo) {
			this.descrExtArticolo = descrExtArticolo;
		}

		public String getUmDefVen() {
			return umDefVen;
		}

		public void setUmDefVen(String umDefVen) {
			this.umDefVen = umDefVen;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getPrezzo() {
			return prezzo;
		}

		public void setPrezzo(String prezzo) {
			this.prezzo = prezzo;
		}

		public String getDisponibilita() {
			return disponibilita;
		}

		public void setDisponibilita(String disponibilita) {
			this.disponibilita = disponibilita;
		}
		
		
	}
}
