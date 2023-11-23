package it.valvorobica.thip.base.generale.ws;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.thera.thermfw.base.Trace;
import com.thera.thermfw.persist.ConnectionDescriptor;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.security.Security;
import com.thera.thermfw.web.SessionEnvironment;

import it.thera.thip.base.documenti.StatoAvanzamento;
import it.thera.thip.ws.GenRequestJSON;
import it.thera.thip.ws.GenericQuery;
import it.valvorobica.thip.base.generale.ws.YManualiDich.DocumentoDgtOggettoPortale;
import it.valvorobica.thip.base.generale.ws.YManualiDich.DocumentoDigitalePortale;
import it.valvorobica.thip.base.generale.ws.dati.YDocumentoBase;
import it.valvorobica.thip.base.generale.ws.dati.YRigaPortaleBase;
import it.valvorobica.thip.base.portal.YUserPortalSession;

/**
 * <h1>Softre Solutions</h1>
 * 
 * @author Daniele Signoroni 09/02/2023 <br>
 *         </br>
 *         <b>70921 DSSOF3 09/02/2023</b> Nuova gestione portale, tutti i dati
 *         vengono reperiti tramtite ws.<br>
 *         <b>70937 DSSOF3 14/02/2023</b> Corretto errore reperimento documento
 *         digitale, prima veniva cercato con tipo doc OFF_VEN cambiato in
 *         DDT_VEN.<br>
 *         <b>70976 DSSOF3 01/03/2023</b> Concatenati gli sconti nelle righe.
 */

public class YDDTSpecifico extends GenRequestJSON {

	public String keyDDT;

	public String dataDDT;

	public String getDataDDT() {
		return dataDDT;
	}

	public void setDataDDT(String dataDDT) {
		this.dataDDT = dataDDT;
	}

	public String getKeyDDT() {
		return keyDDT;
	}

	public void setKeyDDT(String keyDDT) {
		this.keyDDT = keyDDT;
	}

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
		DdtSpecificoPortale ddtSpecifico = new DdtSpecificoPortale();
		compilaDdtSpecifico(ddtSpecifico);
		compilaRigheDdt(ddtSpecifico);
		m.put("ddtSpecifico", ddtSpecifico);
		return m;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void compilaRigheDdt(DdtSpecificoPortale ddtSpecifico) {
		Map values = null;
		boolean isopen = false;
		Object[] info = SessionEnvironment.getDBInfoFromIniFile();
		String dbName = (String) info[0];
		String select = "SELECT  " + "R.SEQUENZA_RIGA ,R.R_ARTICOLO ,R.DES_EXT_ARTICOLO ,R.R_UM_VEN "
				+ ",D.QTA_UM_PRM,R.QTA_SPE_UM_VEN,R.STATO_AVANZAMENTO,R.PREZZO,"
				+ "COALESCE(R.SCONTO_ART1,0),R.VALORE_RIGA ,R.DATA_CONSEG_CFM,"
				+ " COALESCE(R.SCONTO_ART2,0) AS SCONTO_2, COALESCE(R.MAGGIORAZIONE,0) AS MAGGIORAZIONE  "
				+ "FROM THIP.DDT_RIG D " + "LEFT OUTER JOIN THIP.DOC_VEN_RIG R " + "ON R.ID_AZIENDA = D.ID_AZIENDA  "
				+ "AND R.ID_ANNO_DOC = D.ID_ANNO_DOC_VEN  " + "AND R.ID_NUMERO_DOC = D.ID_NUMERO_DOC_VEN  "
				+ "AND R.ID_RIGA_DOC = D.ID_RIGA_DOC_VEN " + "AND R.ID_DET_RIGA_DOC = D.ID_DET_RIGA_DOCVEN ";
		String[] keyOffUnpacked = KeyHelper.unpackObjectKey(this.getKeyDDT());
		String where = " WHERE D.ID_AZIENDA = '" + keyOffUnpacked[0] + "' AND D.ID_ANNO_DDT = '" + keyOffUnpacked[1]
				+ "' " + "AND D.ID_NUMERO_DDT = '" + keyOffUnpacked[2] + "' AND D.TIPO_DDT = '" + keyOffUnpacked[3]
				+ "'" + " AND R.STATO_AVANZAMENTO = '" + StatoAvanzamento.DEFINITIVO + "' ";

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
			for (int i = 0; i < records.size(); i++) {
				YRigaPortaleBase riga = new YRigaPortaleBase();
				ArrayList valuesRecords = (ArrayList) records.get(i);
				riga.setConsegna(valuesRecords.get(10).toString().trim().substring(0, 10));
				riga.setDescArticolo(valuesRecords.get(2).toString().trim());
				BigDecimal evaso = new BigDecimal(valuesRecords.get(5).toString().trim());
				BigDecimal prezzo = new BigDecimal(valuesRecords.get(7).toString().trim());
				BigDecimal qta = new BigDecimal(valuesRecords.get(4).toString().trim());
				BigDecimal sconto = new BigDecimal(valuesRecords.get(8).toString().trim());
				BigDecimal totale = new BigDecimal(valuesRecords.get(9).toString().trim());
				BigDecimal sconto2 = new BigDecimal(valuesRecords.get(11).toString().trim());
				BigDecimal maggiorazione = new BigDecimal(valuesRecords.get(12).toString().trim());
				riga.setEvaso(evaso);
				riga.setIdArticolo(valuesRecords.get(1).toString().trim());
				riga.setPosizione(valuesRecords.get(0).toString().trim());
				riga.setPrezzo(prezzo);
				riga.setQtaOrd(qta);
				riga.setSconto(sconto);
				riga.setStato(valuesRecords.get(6).toString().trim().charAt(0));
				riga.setTotale(totale);
				riga.setUM(valuesRecords.get(3).toString().trim());
				riga.setSconto2(sconto2);
				riga.setMaggiorazione(maggiorazione);
				ddtSpecifico.righePortale.add(riga);
			}
		} catch (Throwable t) {
			t.printStackTrace(Trace.excStream);
		} finally {
			if (isopen) {
				Security.closeDefaultConnection();
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void compilaDdtSpecifico(DdtSpecificoPortale ddtSpecifico) {
		String select = "SELECT DISTINCT D.ID_AZIENDA,D.ID_NUMERO_DDT ,D.ID_ANNO_DDT ,D.TIPO_DDT, "
				+ "FORMAT(D.DATA_DDT ,'yyyy-MM-dd') AS DATA_DDT, MC.DESCRIZIONE AS DESC_MOD_CONS, MS.DESCRIZIONE AS DESC_MOD_SPED,D.R_UTENTE_CRZ,D.RAGIONE_SOC_DEN "
				+ "FROM THIP.DDT_TES D " + "LEFT OUTER JOIN THIP.MOD_CONSEGNA MC  "
				+ "ON MC.ID_AZIENDA = D.ID_AZIENDA  " + "AND MC.ID_MOD_CONSEGNA = D.R_MOD_CONSEGNA  "
				+ "LEFT OUTER JOIN THIP.MOD_SPEDIZIONE MS  " + "ON MS.ID_AZIENDA = D.ID_AZIENDA  "
				+ "AND MS.ID_MOD_SPEDIZIONE = D.R_MOD_SPEDIZIONE  ";
		String[] keyUnpacked = KeyHelper.unpackObjectKey(this.getKeyDDT());
		String where = "WHERE D.ID_AZIENDA = '" + keyUnpacked[0] + "'" + " AND D.ID_NUMERO_DDT = '" + keyUnpacked[2]
				+ "' " + "AND D.ID_ANNO_DDT = '" + keyUnpacked[1] + "' AND D.TIPO_DDT = '" + keyUnpacked[3] + "'";
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
			for (int i = 0; i < records.size(); i++) {
				ArrayList valuesRecords = (ArrayList) records.get(i);
				ddtSpecifico.setNro(valuesRecords.get(1).toString().trim());
				ddtSpecifico.setData(valuesRecords.get(4).toString().trim().substring(0, 10));
				ddtSpecifico.setDescModalitaConsegna(valuesRecords.get(5).toString().trim());
				ddtSpecifico.setDescModalitaSpedizione(valuesRecords.get(6).toString().trim());
				ddtSpecifico.setIdUtenteCre(valuesRecords.get(7).toString().trim());
				ddtSpecifico.setKey(this.getKeyDDT());
				ArrayList<DocumentoDigitalePortale> listaCertificati = listaDocumentiDigitali(ddtSpecifico);
				Iterator<DocumentoDigitalePortale> iterDocDgt = listaCertificati.iterator();
				while (iterDocDgt.hasNext()) {
					DocumentoDigitalePortale doc = iterDocDgt.next();
					ArrayList<DocumentoDgtOggettoPortale> oggettiCertificati = getListaOggettiCertificati(doc);
					ddtSpecifico.listaCertificati.addAll(oggettiCertificati);
				}
				DatiAggiuntiviDDT datiAggiuntivi = getDatiAggiuntiviDDt(ddtSpecifico);
				ddtSpecifico.setNumeroOrdineIntestatario(datiAggiuntivi.getNumeroOrdineIntestatario());
				ddtSpecifico.setDescrAgente(datiAggiuntivi.getDescrizioneAgente());
				ddtSpecifico.setDescrVettore1(datiAggiuntivi.getDescrizioneVettore1());
				ddtSpecifico.setNota(datiAggiuntivi.getNota());
				ddtSpecifico.setConsegna(datiAggiuntivi.getConsegna());
				DatiAggiuntiviDDT valoriDDT = getValoriTotaliDDT(ddtSpecifico);
				ddtSpecifico.setValoreTotaleDocumento(valoriDDT.getValoreTotaleDocumento());
				ddtSpecifico.setValoreImponibile(valoriDDT.getValoreTotaleImponibile());
				ddtSpecifico.setValoreImposta(valoriDDT.getValoreTotaleImposta());
				ddtSpecifico.setValoreTotaleMerce(getTotaleValoreMerce(ddtSpecifico));
				ddtSpecifico.setChiaveDocDgt(getChiaveDocumentoDigitale() != null ? getChiaveDocumentoDigitale() : "");
				ddtSpecifico.setRagioneSociale(valuesRecords.get(8).toString().trim());
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
	private String getChiaveDocumentoDigitale() {
		String key = null;
		Map values = null;
		boolean isopen = false;
		Object[] info = SessionEnvironment.getDBInfoFromIniFile();
		String dbName = (String) info[0];
		String[] keyOffUnpacked = KeyHelper.unpackObjectKey(this.getKeyDDT());
		String select = "SELECT ID_AZIENDA , ID_DOCUMENTO_DGT , ID_VERSIONE  "
				+ "					FROM THIP.DOC_DGT   " + "					WHERE R_TIPO_DOC_DGT  = 'DDT_VEN'  "
				+ "					AND ID_AZIENDA = '" + keyOffUnpacked[0] + "'  "
				+ "					AND R_ANNO_DOC = '" + keyOffUnpacked[1] + "'  "
				+ "					AND R_NUMERO_DOC = '" + keyOffUnpacked[2] + "'  "
				+ "					ORDER BY ID_VERSIONE DESC ";
		try {
			if (!Security.isCurrentDatabaseSetted()) {
				Security.setCurrentDatabase(dbName, null);
			}
			Security.openDefaultConnection();
			isopen = true;
			ConnectionDescriptor cd = ConnectionManager.getCurrentConnectionDescriptor();
			GenericQuery gq = new GenericQuery();
			gq.getAppParams().put("query", select);
			gq.setUseAuthentication(false);
			gq.setUseAuthorization(false);
			gq.setUseLicence(false);
			gq.setConnectionDescriptor(cd);
			values = gq.send();
			ArrayList records = (ArrayList) values.get("records");
			for (int i = 0; i < records.size(); i++) {
				ArrayList valuesRecords = (ArrayList) records.get(i);
				String[] c = { valuesRecords.get(0).toString(), valuesRecords.get(1).toString(),
						valuesRecords.get(2).toString(), "1" };
				key = KeyHelper.buildObjectKey(c);
			}
		} catch (Throwable t) {
			t.printStackTrace(Trace.excStream);
		} finally {
			if (isopen) {
				Security.closeDefaultConnection();
			}
		}
		return key;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ArrayList<DocumentoDgtOggettoPortale> getListaOggettiCertificati(DocumentoDigitalePortale doc) {
		Map values = null;
		YManualiDich man = new YManualiDich();
		List<DocumentoDgtOggettoPortale> ret = new ArrayList<DocumentoDgtOggettoPortale>();
		boolean isopen = false;
		Object[] info = SessionEnvironment.getDBInfoFromIniFile();
		String dbName = (String) info[0];
		String[] keysUnpacked = KeyHelper.unpackObjectKey(doc.getKey());
		String select = "SELECT PROGRESSIVO ,FILENAME,DESCRIZIONE  FROM THIP.DOC_DGT_OGG ";
		String where = "WHERE ID_AZIENDA = '" + keysUnpacked[0] + "' AND ID_DOCUMENTO_DGT = '" + keysUnpacked[1]
				+ "' AND ID_VERSIONE = '" + keysUnpacked[2] + "'";
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
			for (int i = 0; i < records.size(); i++) {
				ArrayList valuesRecords = (ArrayList) records.get(i);
				DocumentoDgtOggettoPortale oggetto = man.new DocumentoDgtOggettoPortale();
				String key = doc.getKey() + KeyHelper.KEY_SEPARATOR + valuesRecords.get(0).toString();
				oggetto.setKey(key);
				oggetto.setNomeFilePdf(valuesRecords.get(1).toString().trim());
				oggetto.setDescrizione(valuesRecords.get(2).toString().trim());
				ret.add(oggetto);
			}
		} catch (Throwable t) {
			t.printStackTrace(Trace.excStream);
		} finally {
			if (isopen) {
				Security.closeDefaultConnection();
			}
		}
		return (ArrayList<DocumentoDgtOggettoPortale>) ret;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String getTotaleValoreMerce(DdtSpecificoPortale ddt) {
		String ret = "";
		String[] keyUnpacked = KeyHelper.unpackObjectKey(ddt.getKey());
		String stmt = "SELECT SUM(R.VALORE_RIGA) FROM THIP.DOC_VEN_RIG R " + "INNER JOIN THIP.DOC_VEN_TES T  "
				+ "ON R.ID_AZIENDA = T.ID_AZIENDA  " + "AND R.ID_ANNO_DOC = T.ID_ANNO_DOC  "
				+ "AND R.ID_NUMERO_DOC = T.ID_NUMERO_DOC  " + "WHERE T.ID_AZIENDA = '" + keyUnpacked[0] + "' "
				+ "AND T.DATA_BOLLA = '" + ddt.getData().replace("-", "") + "' AND T.NUMERO_BOLLA = '" + keyUnpacked[2]
				+ "' AND R.TIPO_RIGA = 1";
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
			gq.getAppParams().put("query", stmt);
			gq.setUseAuthentication(false);
			gq.setUseAuthorization(false);
			gq.setUseLicence(false);
			gq.setConnectionDescriptor(cd);
			values = gq.send();
			ArrayList records = (ArrayList) values.get("records");
			for (int i = 0; i < records.size(); i++) {
				ArrayList valuesRecords = (ArrayList) records.get(i);
				ret = valuesRecords.get(0).toString().trim();
			}
		} catch (Throwable t) {
			t.printStackTrace(Trace.excStream);
		} finally {
			if (isopen) {
				Security.closeDefaultConnection();
			}
		}
		return ret;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private DatiAggiuntiviDDT getValoriTotaliDDT(DdtSpecificoPortale ddtSpecifico) {
		DatiAggiuntiviDDT ret = null;
		String[] keyUnpacked = KeyHelper.unpackObjectKey(ddtSpecifico.getKey());
		String stmt = "SELECT (SUM(T.VLR_TOT_IMP) + SUM(T.VLR_IMPOSTA)) AS VALORE_TOT_DOCUMENTO, SUM(VLR_IMPOSTA) AS VALORE_TOT_IMPOSTA "
				+ ",SUM(VLR_TOT_IMP) AS VALORE_TOTALE_IMPONIBILE " + "FROM THIP.DOC_VEN_TES T "
				+ "WHERE T.ID_AZIENDA = '" + keyUnpacked[0] + "' AND T.DATA_BOLLA = '"
				+ ddtSpecifico.getData().replace("-", "") + "' AND T.NUMERO_BOLLA = '" + keyUnpacked[2] + "' "
				+ "GROUP BY T.ID_AZIENDA ,T.DATA_BOLLA ,T.NUMERO_BOLLA ";
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
			gq.getAppParams().put("query", stmt);
			gq.setUseAuthentication(false);
			gq.setUseAuthorization(false);
			gq.setUseLicence(false);
			gq.setConnectionDescriptor(cd);
			values = gq.send();
			ArrayList records = (ArrayList) values.get("records");
			for (int i = 0; i < records.size(); i++) {
				ArrayList valuesRecords = (ArrayList) records.get(i);
				ret = new DatiAggiuntiviDDT();
				ret.setValoreTotaleDocumento(valuesRecords.get(0).toString().trim());
				ret.setValoreTotaleImposta(valuesRecords.get(1).toString().trim());
				ret.setValoreTotaleImponibile(valuesRecords.get(2).toString().trim());
			}
		} catch (Throwable t) {
			t.printStackTrace(Trace.excStream);
		} finally {
			if (isopen) {
				Security.closeDefaultConnection();
			}
		}
		return ret;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList<DocumentoDigitalePortale> listaDocumentiDigitali(DdtSpecificoPortale ddt) {
		YManualiDich man = new YManualiDich();
		String[] keyUnpacked = KeyHelper.unpackObjectKey(ddt.getKey());
		String select = "SELECT ID_AZIENDA , ID_DOCUMENTO_DGT , ID_VERSIONE " + "FROM THIP.DOC_DGT  "
				+ "WHERE R_TIPO_DOC_DGT LIKE '%CERT%' " + "AND ID_AZIENDA = '" + keyUnpacked[0] + "' "
				+ "AND R_ANNO_DOC = '" + keyUnpacked[1] + "' " + "AND R_NUMERO_DOC = '" + keyUnpacked[2] + "' "
				+ "ORDER BY ID_VERSIONE DESC ";
		Map values = null;
		boolean isopen = false;
		Object[] info = SessionEnvironment.getDBInfoFromIniFile();
		String dbName = (String) info[0];
		ArrayList<DocumentoDigitalePortale> listaDocDig = new ArrayList<DocumentoDigitalePortale>();
		try {
			if (!Security.isCurrentDatabaseSetted()) {
				Security.setCurrentDatabase(dbName, null);
			}
			Security.openDefaultConnection();
			isopen = true;
			ConnectionDescriptor cd = ConnectionManager.getCurrentConnectionDescriptor();
			GenericQuery gq = new GenericQuery();
			gq.getAppParams().put("query", select);
			gq.setUseAuthentication(false);
			gq.setUseAuthorization(false);
			gq.setUseLicence(false);
			gq.setConnectionDescriptor(cd);
			values = gq.send();
			ArrayList records = (ArrayList) values.get("records");
			for (int i = 0; i < records.size(); i++) {
				ArrayList valuesRecords = (ArrayList) records.get(i);
				DocumentoDigitalePortale docDgt = man.new DocumentoDigitalePortale();
				String key = valuesRecords.get(0).toString() + KeyHelper.KEY_SEPARATOR + valuesRecords.get(1).toString()
						+ KeyHelper.KEY_SEPARATOR + valuesRecords.get(2);
				docDgt.setKey(key);
				listaDocDig.add(docDgt);
			}
		} catch (Throwable t) {
			t.printStackTrace(Trace.excStream);
		} finally {
			if (isopen) {
				Security.closeDefaultConnection();
			}
		}
		return listaDocDig;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DatiAggiuntiviDDT getDatiAggiuntiviDDt(DdtSpecificoPortale ddt) {
		DatiAggiuntiviDDT ret = null;
		String[] keyUnpacked = KeyHelper.unpackObjectKey(ddt.getKey());
		String select = "SELECT T.NUM_ORD_CLIENTE  " + ",T.DES_VETTORE1,A.DESCRIZIONE AS DESC_AGENTE,  " + "T.NOTA , "
				+ "CONCAT(TRIM(SV.INDI_D),'-',TRIM(SV.CAP_D),'-',TRIM(SV.LOCA_D),'(',TRIM(SV.PROV_D),')',TRIM(SV.NAZI_D)) AS INDIRIZZO_CONSEGNA "
				+ "FROM THIP.DOC_VEN_TES T  " + "LEFT OUTER JOIN THIP.AGENTI A  " + "ON A.ID_AZIENDA = T.ID_AZIENDA  "
				+ "AND A.ID_AGENTE = T.R_AGENTE  " + "LEFT OUTER JOIN SOFTRE.Y_DOC_VEN_DES_V01 SV  "
				+ "ON SV.AZI = T.ID_AZIENDA  " + "AND SV.ANNO_DOC = T.ID_ANNO_DOC  "
				+ "AND SV.NUM_DOC = T.ID_NUMERO_DOC ";
		String where = "WHERE T.ID_AZIENDA = '" + keyUnpacked[0] + "' AND T.DATA_BOLLA = '"
				+ ddt.getData().replace("-", "") + "' AND T.NUMERO_BOLLA = '" + keyUnpacked[2] + "'";
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
			for (int i = 0; i < records.size(); i++) {
				ArrayList valuesRecords = (ArrayList) records.get(i);
				ret = new DatiAggiuntiviDDT();
				ret.setNumeroOrdineIntestatario(valuesRecords.get(0).toString().trim());
				ret.setDescrizioneVettore1(valuesRecords.get(1).toString().trim());
				ret.setDescrizioneAgente(valuesRecords.get(2).toString().trim());
				ret.setNota(valuesRecords.get(3).toString().trim());
				ret.setConsegna(valuesRecords.get(4).toString().trim());
			}
		} catch (Throwable t) {
			t.printStackTrace(Trace.excStream);
		} finally {
			if (isopen) {
				Security.closeDefaultConnection();
			}
		}
		return ret;
	}

	public class DatiAggiuntiviDDT {
		public String numeroOrdineIntestatario;
		public String descrizioneAgente;
		public String descrizioneVettore1;
		public String nota;
		public String consegna;
		public String valoreTotaleDocumento;
		public String valoreTotaleImponibile;
		public String valoreTotaleImposta;
		public String valoreTotaleMerce;

		public String getConsegna() {
			return consegna;
		}

		public void setConsegna(String consegna) {
			this.consegna = consegna;
		}

		public String getNota() {
			return nota;
		}

		public void setNota(String nota) {
			this.nota = nota;
		}

		public String getValoreTotaleMerce() {
			return valoreTotaleMerce;
		}

		public void setValoreTotaleMerce(String valoreTotaleMerce) {
			this.valoreTotaleMerce = valoreTotaleMerce;
		}

		public String getValoreTotaleDocumento() {
			return valoreTotaleDocumento;
		}

		public void setValoreTotaleDocumento(String valoreTotaleDocumento) {
			this.valoreTotaleDocumento = valoreTotaleDocumento;
		}

		public String getValoreTotaleImponibile() {
			return valoreTotaleImponibile;
		}

		public void setValoreTotaleImponibile(String valoreTotaleImponibile) {
			this.valoreTotaleImponibile = valoreTotaleImponibile;
		}

		public String getValoreTotaleImposta() {
			return valoreTotaleImposta;
		}

		public void setValoreTotaleImposta(String valoreTotaleImposta) {
			this.valoreTotaleImposta = valoreTotaleImposta;
		}

		public String getNumeroOrdineIntestatario() {
			return numeroOrdineIntestatario;
		}

		public void setNumeroOrdineIntestatario(String numeroOrdineIntestatario) {
			this.numeroOrdineIntestatario = numeroOrdineIntestatario;
		}

		public String getDescrizioneAgente() {
			return descrizioneAgente;
		}

		public void setDescrizioneAgente(String descrizioneAgente) {
			this.descrizioneAgente = descrizioneAgente;
		}

		public String getDescrizioneVettore1() {
			return descrizioneVettore1;
		}

		public void setDescrizioneVettore1(String descrizioneVettore1) {
			this.descrizioneVettore1 = descrizioneVettore1;
		}
	}

	public class DdtSpecificoPortale extends YDocumentoBase {
		public String numeroOrdineIntestatario;
		public String descModalitaConsegna;
		public String descModalitaSpedizione;
		public String idUtenteCre;
		public String descrAgente;
		public String descrVettore1;
		public String indirizzoConsegna;
		public String nota;
		public String valoreDocumento;
		public String valoreImponibile;
		public String valoreImposta;
		public String valoreTotaleDocumento;
		public String chiaveDocDgt;
		public String ragioneSociale;
		public ArrayList<YRigaPortaleBase> righePortale = new ArrayList<YRigaPortaleBase>();
		public ArrayList<DocumentoDgtOggettoPortale> listaCertificati = new ArrayList<DocumentoDgtOggettoPortale>();
		public String valoreTotaleMerce;

		public String getValoreTotaleMerce() {
			return valoreTotaleMerce;
		}

		public void setValoreTotaleMerce(String valoreTotaleMerce) {
			this.valoreTotaleMerce = valoreTotaleMerce;
		}

		public String getChiaveDocDgt() {
			return chiaveDocDgt;
		}

		public void setChiaveDocDgt(String chiaveDocDgt) {
			this.chiaveDocDgt = chiaveDocDgt;
		}

		public String getNumeroOrdineIntestatario() {
			return numeroOrdineIntestatario;
		}

		public void setNumeroOrdineIntestatario(String numeroOrdineIntestatario) {
			this.numeroOrdineIntestatario = numeroOrdineIntestatario;
		}

		public String getDescModalitaConsegna() {
			return descModalitaConsegna;
		}

		public void setDescModalitaConsegna(String descModalitaConsegna) {
			this.descModalitaConsegna = descModalitaConsegna;
		}

		public String getDescModalitaSpedizione() {
			return descModalitaSpedizione;
		}

		public void setDescModalitaSpedizione(String descModalitaSpedizione) {
			this.descModalitaSpedizione = descModalitaSpedizione;
		}

		public String getIdUtenteCre() {
			return idUtenteCre;
		}

		public void setIdUtenteCre(String idUtenteCre) {
			this.idUtenteCre = idUtenteCre;
		}

		public String getDescrAgente() {
			return descrAgente;
		}

		public void setDescrAgente(String descrAgente) {
			this.descrAgente = descrAgente;
		}

		public String getDescrVettore1() {
			return descrVettore1;
		}

		public void setDescrVettore1(String descrVettore1) {
			this.descrVettore1 = descrVettore1;
		}

		public String getIndirizzoConsegna() {
			return indirizzoConsegna;
		}

		public void setIndirizzoConsegna(String indirizzoConsegna) {
			this.indirizzoConsegna = indirizzoConsegna;
		}

		public String getNota() {
			return nota;
		}

		public void setNota(String nota) {
			this.nota = nota;
		}

		public String getValoreImponibile() {
			return valoreImponibile;
		}

		public void setValoreImponibile(String valoreImponibile) {
			this.valoreImponibile = valoreImponibile;
		}

		public String getValoreImposta() {
			return valoreImposta;
		}

		public void setValoreImposta(String valoreImposta) {
			this.valoreImposta = valoreImposta;
		}

		public String getValoreDocumento() {
			return valoreDocumento;
		}

		public void setValoreDocumento(String valoreDocumento) {
			this.valoreDocumento = valoreDocumento;
		}

		public String getValoreTotaleDocumento() {
			return valoreTotaleDocumento;
		}

		public void setValoreTotaleDocumento(String valoreTotaleDocumento) {
			this.valoreTotaleDocumento = valoreTotaleDocumento;
		}
	}
}
