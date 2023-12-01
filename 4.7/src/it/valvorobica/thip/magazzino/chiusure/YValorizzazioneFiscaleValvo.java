package it.valvorobica.thip.magazzino.chiusure;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.thera.thermfw.base.Trace;
import com.thera.thermfw.batch.AvailableReport;
import com.thera.thermfw.batch.CrystalReportsInterface;
import com.thera.thermfw.batch.ElaboratePrintRunnable;
import com.thera.thermfw.batch.PrintingToolInterface;
import com.thera.thermfw.batch.ReportModel;
import com.thera.thermfw.common.BusinessObject;
import com.thera.thermfw.common.ErrorMessage;
import com.thera.thermfw.persist.CachedStatement;
import com.thera.thermfw.persist.Column;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.Database;
import com.thera.thermfw.persist.ErrorCodes;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.persist.Proxy;

import it.thera.thip.acquisti.documentoAC.DocumentoAcqRigaPrm;
import it.thera.thip.acquisti.documentoAC.DocumentoAcqRigaSec;
import it.thera.thip.base.articolo.Articolo;
import it.thera.thip.base.articolo.ArticoloCosto;
import it.thera.thip.base.articolo.ArticoloCostoTM;
import it.thera.thip.base.articolo.TipoCosto;
import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.cs.ThipException;
import it.thera.thip.magazzino.chiusure.CalendarioFiscale;
import it.thera.thip.magazzino.chiusure.PeriodoCalFiscale;
import it.thera.thip.magazzino.chiusure.PeriodoCalFiscaleTM;
import it.thera.thip.magazzino.chiusure.RptStoricoCmpArticolo;
import it.thera.thip.magazzino.chiusure.RptStoricoCmpArticoloTM;
import it.thera.thip.produzione.ordese.AttivitaEsecMateriale;
import it.thera.thip.produzione.ordese.AttivitaEsecutiva;
import it.thera.thip.produzione.ordese.OrdineEsecutivo;
import it.thera.thip.vendite.proposteEvasione.CreaMessaggioErrore;

/**
 * <h1>Softre Solutions</h1> <br>
 * 
 * @author Daniele Signoroni 24/11/2023 <br>
 *         <br>
 *         <b>71310 DSSOF3 24/11/2023</b>
 *         <p>
 *         Ripresa stesura di AGOSF3.<br>
 *         </br>
 *         Aggiunto {@link #setIdAziendaInternal(String)} per Proxy.<br>
 *         Aggiunto {@link #storicizzaCostoArticolo()} per storicizzare il costo
 *         CMP nel {@link #iTipoCosto} passatomi dall'utente.<br>
 *         Aggiunta {@link #iDataUltimaChiusura}, {@link #iStatoChiusuraMag}
 *         presi dall'ultima chiusura di magazzino.<br>
 *         Aggiunti booleani {@link #iStampaCMP}, {@link #iCalcoloCmp}
 *         rispettivamente per scegliere di effettuare solo stampa, solo calcolo
 *         o entrambe le operativita'.<br>
 *         Aggiunto {@link #getKeyArticoloCosto(String)} per prendere eventuale
 *         costo gia' esistente e cancellarlo.<br>
 *         </p>
 */

public class YValorizzazioneFiscaleValvo extends ElaboratePrintRunnable implements BusinessObject {

	SimpleDateFormat dateFormatSQL = new SimpleDateFormat("yyyyMMdd");

	protected String iIdAzienda = null;

	protected String iIdAnnoFiscale = null;

	protected Proxy iAzienda = new Proxy(Azienda.class);

	protected Proxy iCalendarioFiscale = new Proxy(CalendarioFiscale.class);

	protected java.sql.Date iDataUltimaChiusura;

	protected char iStatoChiusuraMag;

	protected String iIdCosto;

	protected boolean iCalcoloCmp;

	protected boolean iStampaCMP;

	protected char iAzzeraCostiManuali;

	protected Proxy iTipoCosto = new Proxy(TipoCosto.class);

	protected AvailableReport availableRepCMP;

	protected int rigaJobIdValCMP = 1;

	protected BigDecimal iPrcIncrementoMatProd = new BigDecimal(0);

	protected final static String ID_PER_ANNO_FISCALE = "1";

	protected static final String SQL_KEY_STOR_CMP_ART = "SELECT ID_PER_ANNO_FSC, ID_RAG_FSC_MAG, ID_ARTICOLO, ID_CONFIGURAZIONE "
			+ "FROM THIPPERS.YSTOR_CMP_ARTICOLO WHERE ID_AZIENDA = ? AND ID_ANNO_FISCALE= ?";// AND ID_PER_ANNO_FSC =
	// ?";
	protected static CachedStatement cKeyStorCmpArt = new CachedStatement(SQL_KEY_STOR_CMP_ART);

	public ArrayList<String> iAnomalie = new ArrayList<String>();

	public YValorizzazioneFiscaleValvo() {
		super();
		setIdAzienda(Azienda.getAziendaCorrente());
		setStatoChiusuraMag('N');
	}

	public java.sql.Date getDataUltimaChiusura() {
		return iDataUltimaChiusura;
	}

	public String getDataUltimaChiusuraString() {
		java.sql.Date sqldate = getDataUltimaChiusura();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String data = sdf.format(sqldate);
		return data;
	}

	public void setDataUltimaChiusura(java.sql.Date iDataUltimaChiusura) {
		this.iDataUltimaChiusura = iDataUltimaChiusura;
	}

	public void setAzienda(Azienda azienda) {
		this.iAzienda.setObject(azienda);
	}

	public Azienda getAzienda() {
		return (Azienda) iAzienda.getObject();
	}

	public void setAziendaKey(String key) {
		iAzienda.setKey(key);
	}

	public String getAziendaKey() {
		return iAzienda.getKey();
	}

	public void setIdAzienda(String idAzienda) {
		iAzienda.setKey(idAzienda);
		setIdAziendaInternal(idAzienda);
	}

	protected void setIdAziendaInternal(String idAzienda) {
		iTipoCosto.setKey(KeyHelper.replaceTokenObjectKey(iTipoCosto.getKey(), 1, idAzienda));
		iCalendarioFiscale.setKey(KeyHelper.replaceTokenObjectKey(iCalendarioFiscale.getKey(), 1, idAzienda));

	}

	public String getIdAzienda() {
		String key = iAzienda.getKey();
		return key;
	}

	public String getIdAnnoFiscale() {
		return iIdAnnoFiscale;
	}

	public void setIdAnnoFiscale(String iIdAnnoFiscale) {
		this.iIdAnnoFiscale = iIdAnnoFiscale;
	}

	public void setCalendarioFiscale(CalendarioFiscale calendarioFiscale) {
		this.iCalendarioFiscale.setObject(calendarioFiscale);
	}

	public CalendarioFiscale getCalendarioFiscale() {
		return (CalendarioFiscale) iCalendarioFiscale.getObject();
	}

	public void setCalendarioFiscaleKey(String key) {
		iCalendarioFiscale.setKey(key);
	}

	public String getCalendarioFiscaleKey() {
		return iCalendarioFiscale.getKey();
	}

	public void setIdCalendarioFiscale(String idCalendarioFiscale) {
		String key = iCalendarioFiscale.getKey();
		iCalendarioFiscale.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idCalendarioFiscale));
	}

	public String getIdCalendarioFiscale() {
		String key = iCalendarioFiscale.getKey();
		String objCalendarioFiscale = KeyHelper.getTokenObjectKey(key, 2);
		return objCalendarioFiscale;
	}

	public BigDecimal getPrcIncrementoMatProd() {
		return iPrcIncrementoMatProd;
	}

	public void setPrcIncrementoMatProd(BigDecimal iPrcIncrementoMatProd) {
		this.iPrcIncrementoMatProd = iPrcIncrementoMatProd;
	}

	public void setTipoCosto(TipoCosto tipoCosto) {
		this.iTipoCosto.setObject(tipoCosto);
		setDirty();
	}

	public TipoCosto getTipoCosto() {
		return (TipoCosto) iTipoCosto.getObject();
	}

	public void setTipoCostoKey(String key) {
		iTipoCosto.setKey(key);
		setDirty();
	}

	public String getTipoCostoKey() {
		return iTipoCosto.getKey();
	}

	public void setIdCosto(String idTipoCosto) {
		if (idTipoCosto != null) {
			String key = iTipoCosto.getKey();
			iTipoCosto.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idTipoCosto));
		}
	}

	public String getIdCosto() {
		String key = iTipoCosto.getKey();
		String objIdTipoCosto = KeyHelper.getTokenObjectKey(key, 2);
		return objIdTipoCosto;
	}

	public boolean isCalcoloCMP() {
		return iCalcoloCmp;
	}

	public void setCalcoloCMP(boolean iCalcoloCmp) {
		this.iCalcoloCmp = iCalcoloCmp;
	}

	public boolean isStampaCMP() {
		return iStampaCMP;
	}

	public void setStampaCMP(boolean iStampaCMP) {
		this.iStampaCMP = iStampaCMP;
	}

	public char getStatoChiusuraMag() {
		return iStatoChiusuraMag;
	}

	public void setStatoChiusuraMag(char iStatoChiusuraMag) {
		this.iStatoChiusuraMag = iStatoChiusuraMag;
	}

	public char getAzzeraCostiManuali() {
		return iAzzeraCostiManuali;
	}

	public void setAzzeraCostiManuali(char iAzzeraCostiManuali) {
		this.iAzzeraCostiManuali = iAzzeraCostiManuali;
	}

	@Override
	public boolean createReport() {
		boolean ok = true;
		setDataUltimaChiusura(recuperaDataUltimaChiusuraMagazzinoDef());
		writeLog("Data ultima chiusura trovata: " + getDataUltimaChiusura());
		try {
			if (isCalcoloCMP()) {
				storicizzaUltimoLancio();
				cancellaStoricoSePeriodoMinore();
				creaStoricoZero();
				if (getAzzeraCostiManuali() == YAzzeraCostiManualiCMP.SI) {
					// azzero costi manuali di tutti i record ystoricocmpart con anno == quello
					// nella form di lancio
					azzeraCostiManualiAnno(this.getIdAnnoFiscale());
				}
				storicizzazioneCostoMedioAnno(true, null);
				aggiornaCostoMovimenti();
				storicizzaCostoArticolo();
			}
			// storicizzazioneCostoMedioAnno(false);//non lo faccio più generico ma lo farò
			// per ogni articolo processato da aggiornaCostoMovimenti
			if (isStampaCMP()) {
				setPrintToolInterface((PrintingToolInterface) Factory.createObject(CrystalReportsInterface.class));
				ReportModel rptModel = new ReportModel();
				rptModel.setReportModelId(getReportId().trim());
				rptModel.retrieve();
				int rc = 0;
				availableRepCMP = createNewReport(getReportId());
				availableRepCMP.setWhereCondition(printToolInterface.generateDefaultWhereCondition(availableRepCMP,
						RptStoricoCmpArticoloTM.TABLE_NAME));
				rc += availableRepCMP.save();
				if (rc >= ErrorCodes.OK) {
					salvaRptStoricoCmpArticoloNoAggiornamento();
				}
				for (String anomalia : iAnomalie) {
					writeLog(anomalia);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			writeLog(e.getMessage());
			ok = false;
		}
		return ok;
	}

	protected void cancellaStoricoSePeriodoMinore() {
		boolean cancellare = false;
		PeriodoCalFiscale periodoUltimaChiusura = getPeriodoCalFiscaleUltimaChiusura();
		PeriodoCalFiscale periodoUltimoLancio = getPeriodoCalFiscaleUltimoLancio();
		if (periodoUltimaChiusura != null && periodoUltimoLancio != null) {
			int annoUltimaChiusura = Integer.valueOf(periodoUltimaChiusura.getCodiceAnnoFiscale().trim());
			int annoUltimaLancio = Integer.valueOf(periodoUltimoLancio.getCodiceAnnoFiscale().trim());
			if (annoUltimaChiusura < annoUltimaLancio) {
				cancellare = true;
			} else if (periodoUltimaChiusura.getCodicePeriodo() < periodoUltimoLancio.getCodicePeriodo()) {
				cancellare = true;
			}
//			} else if (periodoUltimaChiusura.getDataFine().compareTo(periodoUltimoLancio.getDataFine()) < 0) {
//				cancellare = true;
//			}
		}
		if (cancellare) {
			String q = " DELETE " + YStoricoCmpArticoloTM.TABLE_NAME + " WHERE " + YStoricoCmpArticoloTM.ID_AZIENDA
					+ " = '" + Azienda.getAziendaCorrente() + "'" + " AND " + YStoricoCmpArticoloTM.ID_ANNO_FISCALE
					+ " = '" + this.getIdAnnoFiscale() + "'  ";
			CachedStatement cs = null;
			cs = new CachedStatement(q);
			try {
				int ok = cs.executeUpdate();
				if (ok > 0) {
					ConnectionManager.commit();
				} else {
					ConnectionManager.rollback();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (cs != null) {
						cs.free();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected PeriodoCalFiscale getPeriodoCalFiscaleUltimoLancio() {
		ResultSet rs = null;
		CachedStatement cs = null;
		try {
			String query = "SELECT Y.ID_AZIENDA ,Y.ID_ANNO_FISCALE ,Y.ID_PER_ANNO_FSC FROM THIPPERS.YSTORICO_CMP_LANCIO Y"
					+ "					 INNER JOIN THIP.PER_CALEN_FSC P   ON Y.ID_AZIENDA = P.ID_AZIENDA "
					+ "					 AND Y.ID_ANNO_FISCALE = P.ID_ANNO_FISCALE "
					+ "					 AND Y.ID_PER_ANNO_FSC = P.ID_PER_ANNO_FSC    ORDER BY P.DATA_FINE_PER DESC";
			cs = new CachedStatement(query);
			rs = cs.executeQuery();
			if (rs.next()) {
				return (PeriodoCalFiscale) PeriodoCalFiscale.elementWithKey(PeriodoCalFiscale.class,
						KeyHelper.buildObjectKey(new String[] { rs.getString(YStoricoCmpLancioTM.ID_AZIENDA),
								rs.getString(YStoricoCmpLancioTM.ID_ANNO_FISCALE),
								rs.getString(YStoricoCmpLancioTM.ID_PER_ANNO_FSC) }),
						PersistentObject.NO_LOCK);
			}
		} catch (SQLException e) {
			writeLog(e.getMessage());
			e.printStackTrace(Trace.excStream);
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Setta {@link YStoricoCmpArticoloTM#COSTO_MED_POND_MANUALE} == 0. In base
	 * all'anno passato e l'azienda corrente.
	 * 
	 * @param idAnnoFiscale
	 */
	protected static void azzeraCostiManualiAnno(String idAnnoFiscale) {
		String q = " UPDATE " + YStoricoCmpArticoloTM.TABLE_NAME + " SET "
				+ YStoricoCmpArticoloTM.COSTO_MED_POND_MANUALE + " = 0 " + " WHERE " + YStoricoCmpArticoloTM.ID_AZIENDA
				+ " = '" + Azienda.getAziendaCorrente() + "'" + " AND " + YStoricoCmpArticoloTM.ID_ANNO_FISCALE + " = '"
				+ idAnnoFiscale + "'  ";
		CachedStatement cs = null;
		cs = new CachedStatement(q);
		try {
			int ok = cs.executeUpdate();
			if (ok > 0) {
				ConnectionManager.commit();
			} else {
				ConnectionManager.rollback();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (cs != null) {
					cs.free();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Storicizzazione ultimo lancio in tabella personalizzata.<br>
	 * Servira' dopo.
	 * 
	 * @throws ThipException
	 */
	protected void storicizzaUltimoLancio() throws ThipException {
		PeriodoCalFiscale periodo = getPeriodoCalFiscaleUltimaChiusura();
		if (periodo != null) {
			YStoricoCmpLancio lancio = (YStoricoCmpLancio) Factory.createObject(YStoricoCmpLancio.class);
			lancio.setPeriodocalfiscale(periodo);
			try {
				int rc = lancio.save();
				if (rc >= 0) {
					ConnectionManager.commit();
				} else {
					ConnectionManager.rollback();
					ErrorMessage em = CreaMessaggioErrore.daRcAErrorMessage(rc, null);
					throw new ThipException("** Impossibile salvare storico ultimo lancio, controllare ** --> " + em);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			throw new ThipException("Non e' stato trovato nessun periodo fiscale");
		}

	}

	protected void storicizzaCostoArticolo() throws SQLException {
		ArrayList<YOggettinoLetturaValFis> lista = new ArrayList<YOggettinoLetturaValFis>();
		String statement = "SELECT " + "	ID_AZIENDA AS R_AZIENDA , " + "	ID_ANNO_FISCALE AS R_ANNO_FISCALE , "
				+ "	ID_PER_ANNO_FSC , " + "	ID_ARTICOLO AS R_ARTICOLO , " + "	COSTO_MED_POND , "
				+ "	COSTO_MED_POND_MANUALE " + "FROM " + "	THIPPERS.YSTOR_CMP_ARTICOLO " + "WHERE "
				+ "	ID_PER_ANNO_FSC = ( " + "	SELECT " + "		MAX(ID_PER_ANNO_FSC) " + "	FROM "
				+ "		THIPPERS.YSTOR_CMP_ARTICOLO " + "	WHERE " + "		ID_ANNO_FISCALE = '"
				+ this.getIdAnnoFiscale() + "' " + "      AND (COSTO_MED_POND != 0 OR COSTO_MED_POND_MANUALE != 0) "
				+ "    ) " + "    AND (COSTO_MED_POND != 0 OR COSTO_MED_POND_MANUALE != 0);";
		ResultSet rs = null;
		CachedStatement cs = new CachedStatement(statement);
		rs = cs.executeQuery();
		YStoricoCmpCostiIterator iteratore = new YStoricoCmpCostiIterator(rs);
		while (iteratore.hasNext()) {
			YOggettinoLetturaValFis ogg = (YOggettinoLetturaValFis) iteratore.next();
			lista.add(ogg);
		}
		try {
			if (rs != null)
				rs.close();
			iteratore.closeCursor();
		} catch (SQLException ex) {
			ex.printStackTrace(Trace.excStream);
		}
		for (Iterator<YOggettinoLetturaValFis> iterator = lista.iterator(); iterator.hasNext();) {
			YOggettinoLetturaValFis oggino = (YOggettinoLetturaValFis) iterator.next();
			// aggiornare articolo costo
			if (oggino.R_ARTICOLO != null && this.getIdCosto() != null) {
				ArticoloCosto costo = (ArticoloCosto) Factory.createObject(ArticoloCosto.class);
				String keyC = getKeyArticoloCosto(oggino.R_ARTICOLO);
				BigDecimal prezzo = BigDecimal.ZERO;
				if (oggino.COSTO_MED_POND_MANUALE.compareTo(BigDecimal.ZERO) > 0) {
					prezzo = oggino.COSTO_MED_POND_MANUALE;
				} else {
					prezzo = oggino.COSTO_MED_POND;
				}
				if (prezzo != null && prezzo.compareTo(BigDecimal.ZERO) > 0) {
					if (keyC != null) {
						ArticoloCosto costoEsistente = (ArticoloCosto) Factory.createObject(ArticoloCosto.class);
						costoEsistente.setKey(keyC);
						if (costoEsistente.retrieve()) {
							if (costoEsistente.delete() < 0) {
								writeLog("Errore nella cancellazione dell'articolo costo: " + costoEsistente.getKey()
										+ ", procedo...");
								continue;
							}
						}
					}
					costo.setIdVersione(1);
					costo.setIdArticolo(oggino.R_ARTICOLO);
					costo.setIdTipoCosto(this.getIdCosto());
					costo.setDataCosto(getDataUltimaChiusura());
					costo.setCosto(prezzo);
					if (costo.save() < 0) {
						writeLog("Errore nel salvataggio dell'articolo costo: " + costo.getKey()
								+ ", procedo con il resto.");
					}
				}
			}
		}
		ConnectionManager.commit();
	}

	/**
	 * Processo tutti gli articoli (non esclusi da val fiscale) movimentati
	 * nell'anno fiscale scelto, se non esiste il record con periodo 0 nella tabella
	 * dello storico, lo creo: - se esiste un record storico precedente allora copio
	 * giacenza/valore finale e CMP - se non esiste uno storico precedente allora
	 * creo record 0 con tutto a 0
	 * 
	 * @throws SQLException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	protected void creaStoricoZero()
			throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		ArrayList<String> idArticoli = new ArrayList<String>();
		String statement = "SELECT R_ARTICOLO \r\n" + "FROM THIP.MOVIM_MAGAZ mm \r\n"
				+ "LEFT OUTER JOIN THIP.ARTICOLI art \r\n"
				+ "ON mm.R_AZIENDA = art.ID_AZIENDA AND mm.R_ARTICOLO = art.ID_ARTICOLO \r\n"
				+ "LEFT OUTER JOIN THIP.CLASSI_FISCALI cf \r\n"
				+ "ON cf.ID_AZIENDA = art.ID_AZIENDA AND cf.ID_CLASSE_FISCALE = art.R_CLASSE_FISCALE\r\n"
				+ "WHERE R_AZIENDA = '" + Azienda.getAziendaCorrente() + "'\r\n" + "AND YEAR(DTA_REGISTRAZIONE) = '"
				+ getIdAnnoFiscale() + "'\r\n" + "AND DTA_REGISTRAZIONE <= '" + getDataUltimaChiusuraString() + "'\r\n"
				+ "AND (cf.ESCLUS_STP_FSC IS NULL OR cf.ESCLUS_STP_FSC <> 'Y')\r\n" + "AND NOT EXISTS (\r\n"
				+ "	SELECT 1\r\n" + "	FROM THIPPERS.YSTOR_CMP_ARTICOLO yca \r\n"
				+ "	WHERE yca.ID_AZIENDA = mm.R_AZIENDA \r\n"
				+ "	AND yca.ID_ANNO_FISCALE = YEAR(mm.DTA_REGISTRAZIONE) \r\n"
				+ "	AND yca.ID_ARTICOLO = mm.R_ARTICOLO \r\n" + "	AND yca.ID_PER_ANNO_FSC = 0\r\n" + ")"
				+ "GROUP BY R_ARTICOLO ";
		CachedStatement cs = new CachedStatement(statement);
		ResultSet rs = cs.executeQuery();
		while (rs.next()) {
			idArticoli.add(rs.getString("R_ARTICOLO"));
		}
		try {
			if (rs != null) {
				rs.close();
			}
			if (cs != null) {
				cs.free();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for (String idArticolo : idArticoli) {
			YStoricoCmpArticolo storicoZero = (YStoricoCmpArticolo) Factory.createObject(YStoricoCmpArticolo.class);
			storicoZero.setIdAzienda(Azienda.getAziendaCorrente());
			storicoZero.setIdAnnoFiscale(getIdAnnoFiscale());
			storicoZero.setIdPerAnnoFiscale(0);
			storicoZero.setIdArticolo(idArticolo);
			storicoZero.setIdConfigurazione(0);
			storicoZero.setIdRaggrFiscMag("RF1");
			YStoricoCmpArticolo storicoPrecedente = YStoricoCmpArticolo
					.cercaStoricoPrecedenteStatico(Azienda.getAziendaCorrente(), getIdAnnoFiscale(), idArticolo);
			if (storicoPrecedente != null) {
				storicoZero.setGiacenzaFinale(storicoPrecedente.getGiacenzaFinale());
				storicoZero.setValoreFinale(storicoPrecedente.getValoreFinale());
				storicoZero.setCostoMedioPonderato(storicoPrecedente.getCostoMedioPonderato());
			}
			storicoZero.save();
		}
		ConnectionManager.commit();
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	public void salvaRptStoricoCmpArticoloNoAggiornamento() throws Exception {
		Iterator iter = getChiaviStoricoCmpArticolo(getIdAnnoFiscale()).iterator();
		Integer periodoLancio = Integer.parseInt(ID_PER_ANNO_FISCALE);
		RptStoricoCmpArticolo rptStorCmpArt = null;

		while (iter.hasNext()) {
			String key = (String) iter.next();
			YStoricoCmpArticolo storCmp = (YStoricoCmpArticolo) PersistentObject
					.elementWithKey(YStoricoCmpArticolo.class, key, PersistentObject.NO_LOCK);
			rptStorCmpArt = buildRptStoricoCmpArticolo(storCmp, periodoLancio);
			if (rptStorCmpArt != null) {
				int ret = rptStorCmpArt.save();
			}
		}
		ConnectionManager.commit();
	}

	public RptStoricoCmpArticolo buildRptStoricoCmpArticolo(YStoricoCmpArticolo storicoCmpArt, Integer periodoLancio)
			throws SQLException {
		RptStoricoCmpArticolo rptStoricoCmpArt = (RptStoricoCmpArticolo) Factory
				.createObject(RptStoricoCmpArticolo.class);
		rptStoricoCmpArt.setBatchJobId(getBatchJob().getBatchJobId());
		rptStoricoCmpArt.setReportNr(availableRepCMP.getReportNr());
		rptStoricoCmpArt.setRigaJobId(rigaJobIdValCMP++);
		rptStoricoCmpArt.setIdAzienda(storicoCmpArt.getIdAzienda());
		rptStoricoCmpArt.setIdAnnoFiscale(storicoCmpArt.getIdAnnoFiscale());
		rptStoricoCmpArt.setIdPerAnnoFsc(storicoCmpArt.getIdPerAnnoFiscale());
		rptStoricoCmpArt.setIdRagFscMag(storicoCmpArt.getIdRaggrFiscMag());
		rptStoricoCmpArt.setIdArticolo(storicoCmpArt.getIdArticolo());
		rptStoricoCmpArt.setDescArticolo(storicoCmpArt.getArticolo().getDescrizioneArticoloNLS().getDescrizione());
		rptStoricoCmpArt.setIdConfigurazione(storicoCmpArt.getIdConfigurazione());
		rptStoricoCmpArt.setQtaCarichi(storicoCmpArt.getQtaCarichi());
		rptStoricoCmpArt.setQtaScarichi(storicoCmpArt.getQtaScarichi());
		rptStoricoCmpArt.setValoreCarichi(storicoCmpArt.getValoreCarichi());
		rptStoricoCmpArt.setValoreScarichi(storicoCmpArt.getValoreScarichi());
		rptStoricoCmpArt.setGiacenzaFinale(storicoCmpArt.getGiacenzaFinale());
		rptStoricoCmpArt.setValoreFinale(storicoCmpArt.getValoreFinale());
		rptStoricoCmpArt.setCostoMedPond(storicoCmpArt.getCostoMedioPonderato());
		rptStoricoCmpArt.setIdUnitaMisura(storicoCmpArt.getIdUnitaMisura());
		// Inizio 9200
		if (storicoCmpArt.getIdPerAnnoFiscale().compareTo(periodoLancio) == 0)
			rptStoricoCmpArt.setTotaleValorePer(storicoCmpArt.getValoreFinale());
		// Fine 9200
		return rptStoricoCmpArt;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static synchronized List getChiaviStoricoCmpArticolo(String idAnnoFiscale) throws SQLException {
		List ret = new ArrayList();
		PreparedStatement ps = cKeyStorCmpArt.getStatement();
		String idAzienda = Azienda.getAziendaCorrente();
		Database db = ConnectionManager.getCurrentDatabase();
		db.setString(ps, 1, idAzienda);
		db.setString(ps, 2, idAnnoFiscale);
		// db.setString(ps, 3, ID_PER_ANNO_FISCALE);
		ResultSet rs = cKeyStorCmpArt.executeQuery();
		while (rs.next()) {
			Integer idPerAnnoFiscale = Column.getIntegerFromColumn(rs, 1);
			String idRaggrFiscMag = rs.getString(2).trim();
			String idArticolo = rs.getString(3).trim();
			Integer idConfigurazione = Column.getIntegerFromColumn(rs, 4);
			Object[] keyParts = { idAzienda, idAnnoFiscale, idPerAnnoFiscale, idRaggrFiscMag, idArticolo,
					idConfigurazione };
			ret.add(KeyHelper.buildObjectKey(keyParts));
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	protected void aggiornaCostoMovimenti() throws Exception {
		ArrayList<YOggettinoAggMovim> movimentiDaAggiornare = caricaMovimentiDaAggiornare();
		String idArticoloPrec = null;
		if (!movimentiDaAggiornare.isEmpty()) {
			idArticoloPrec = ((YOggettinoAggMovim) movimentiDaAggiornare.get(0)).iIdArticolo;
		}
		for (YOggettinoAggMovim ogg : movimentiDaAggiornare) {
			String idArticoloCurr = ogg.iIdArticolo;
			Articolo articolo = (Articolo) Articolo.elementWithKey(Articolo.class,
					KeyHelper.buildObjectKey(new String[] { Azienda.getAziendaCorrente(), ogg.iIdArticolo.trim() }),
					PersistentObject.NO_LOCK);
			if (articolo != null) {
				if (ogg.iTpMovimMagaz.equals("2") && ogg.iTipoDocumento.startsWith("L")) { // startWith è un controllo
																							// extra
					// nel senso che la query l'ha
					// già fatto
					// acquisto conto lavoro
					String keyDocAcqRigaPrm = ogg.costruisciKeyDocAcqRigPrm();
					DocumentoAcqRigaPrm rigaPrm = (DocumentoAcqRigaPrm) DocumentoAcqRigaPrm
							.elementWithKey(DocumentoAcqRigaPrm.class, keyDocAcqRigaPrm, 0);
					if (rigaPrm != null) {// should always not be null
						BigDecimal prezzo = rigaPrm.getPrezzoNettoFattura() != null ? rigaPrm.getPrezzoNettoFattura()
								: rigaPrm.getPrezzoNetto();
						List<DocumentoAcqRigaSec> righeSec = rigaPrm.getRigheSecondarie();
						for (DocumentoAcqRigaSec rigaSec : righeSec) {
							BigDecimal cmpMateriale = YStoricoCmpArticolo.cercaUltimoCMP(rigaSec.getIdArticolo());
							if (cmpMateriale != null && cmpMateriale.compareTo(BigDecimal.ZERO) == 1) {
								BigDecimal cmpRigaSec = cmpMateriale.multiply(rigaSec.getCoefficienteImpiego())
										.stripTrailingZeros();
								prezzo = prezzo.add(cmpRigaSec);
							} else {
								String anomalia = "(nr.reg: " + ogg.iNumRegistrazione
										+ "). CMP non trovato per materiale: " + rigaSec.getIdArticolo()
										+ ", riga secondaria con chiave: " + rigaSec.getKey();
								iAnomalie.add(anomalia);
							}
						}
						aggiornaCostoMovimMagaz(ogg, prezzo);
					}
				} else if (ogg.iTpMovimMagaz.equals("8")) {
					// produzione
					BigDecimal prezzo = BigDecimal.ZERO;
					String keyOrdEse = ogg.costruisciKeyOrdEse();
					OrdineEsecutivo oe = (OrdineEsecutivo) OrdineEsecutivo.elementWithKey(OrdineEsecutivo.class,
							keyOrdEse, 0);
					if (oe != null) {
						List<AttivitaEsecutiva> atvs = oe.getAttivitaEsecutive();
						for (AttivitaEsecutiva atv : atvs) {
							List<AttivitaEsecMateriale> materiali = atv.getMateriali();
							for (AttivitaEsecMateriale materiale : materiali) {
								BigDecimal cmpMateriale = YStoricoCmpArticolo.cercaUltimoCMP(materiale.getIdArticolo());
								if (cmpMateriale != null && cmpMateriale.compareTo(BigDecimal.ZERO) == 1) {
									prezzo = prezzo.add(cmpMateriale);
								} else {
									String anomalia = "(nr.reg: " + ogg.iNumRegistrazione
											+ "). CMP non trovato per materiale: " + materiale.getIdArticolo()
											+ ", riga materiale: " + materiale.getKey();
									iAnomalie.add(anomalia);
								}
							}
						}
						// adesso aumento il costo del 20%
						BigDecimal maggiorazione = getPrcIncrementoMatProd();
						maggiorazione = maggiorazione.divide(new BigDecimal(100), BigDecimal.ROUND_UP);
						BigDecimal incremento = prezzo.multiply(maggiorazione);
						prezzo = prezzo.add(incremento);
						aggiornaCostoMovimMagaz(ogg, prezzo);
					}
				} else if (ogg.iTpMovimMagaz.equals("1")) {
					// generico cambio codice
					Object[] ris = null;
					ris = cercaArticoloPadreCambioCodice(ogg);
					if (ris[0] != null) {
						String idArticoloPadre = ris[0].toString().trim();
						BigDecimal qtaPadre = (BigDecimal) ris[1];
						// String idArticoloFiglio = ris[2].toString().trim();
						BigDecimal qtaFiglio = (BigDecimal) ris[3];
						if (idArticoloPadre != null) {
							BigDecimal costoPadre = YStoricoCmpArticolo.cercaUltimoCMP(idArticoloPadre);
							if (costoPadre != null) {
								if (costoPadre.compareTo(BigDecimal.ZERO) > 0) {
									try {
										BigDecimal res = qtaPadre.stripTrailingZeros()
												.multiply(costoPadre.stripTrailingZeros())
												.divide(qtaFiglio.stripTrailingZeros(), RoundingMode.HALF_UP);
										costoPadre = res;
									} catch (Exception e) {
										iAnomalie.add("per articolo padre : " + idArticoloPadre + " eccezzione");
										e.printStackTrace();
									}
								}
								aggiornaCostoMovimMagaz(ogg, costoPadre);
								// inserisci articolo costo

							} else {
								iAnomalie.add("(nr.reg: " + ogg.iNumRegistrazione + "). Per l'articolo: "
										+ ogg.iIdArticolo + ", il padre è: " + idArticoloPadre
										+ ", ma non è stato trovato il costo.");
							}
						} else {
							iAnomalie.add("Errore nella ricerca dell'articolo padre per il cambio codice di: "
									+ ogg.iIdArticolo + ". (num.registrazione:" + ogg.iNumRegistrazione + ")");
						}
					}
				}

				if (!idArticoloCurr.equals(idArticoloPrec)) {
					// qui devo ricalcolare il cmp per questo articolo
					storicizzazioneCostoMedioAnno(false, ogg.iIdArticolo);
				}
				// Update the previousIdarticolo for the next iteration
				idArticoloPrec = idArticoloCurr;
			}
		}
	}

	/**
	 * Cerco tramite il numero del movimento di magazzino panthera qual è il codice
	 * padre da cui deriva l'articolo in questione
	 * 
	 * @param ogg
	 * @return
	 * @throws SQLException
	 */
	protected Object[] cercaArticoloPadreCambioCodice(YOggettinoAggMovim ogg) throws SQLException {
		String idArtPadre = null;
		BigDecimal qtaPadre = null;
		String idArtFiglio = null;
		BigDecimal qtaFiglio = null;
		String statement = "SELECT lt.COD_ARTICOLO\r\n,lt.QTA1" + " FROM LOGIS.LMOVIM_RIGA lt\r\n"
				+ "LEFT JOIN LOGIS.LMOVIM_TESTA lt2 \r\n" + "on lt.COD_SOCIETA = lt2.COD_SOCIETA \r\n"
				+ "AND lt.COD_MOVIM = lt2.CODICE \r\n" + "WHERE lt2.NOTE = (\r\n"
				+ "SELECT COALESCE (lt.NOTE,lt2.NOTE) \r\n" + "FROM LOGIS.LDOCHOST_TESTA AS DHT\r\n"
				+ "LEFT JOIN LOGIS.LDOCHOST_RIGA AS DHR\r\n" + "ON DHT.CODICE = DHR.CODICE_DOCHOST\r\n"
				+ "LEFT JOIN LOGIS.LMOVIM_RIGA lt \r\n" + "ON DHR.COD_SOCIETA = lt.COD_SOCIETA \r\n"
				+ "AND DHR.COD_MOVIM = lt.COD_MOVIM \r\n" + "LEFT JOIN LOGIS.LMOVIM_TESTA lt2 \r\n"
				+ "on lt.COD_SOCIETA = lt2.COD_SOCIETA \r\n" + "AND lt.COD_MOVIM = lt2.CODICE \r\n"
				+ "LEFT OUTER JOIN THIP.MOVIM_MAGAZ mm \r\n" + "ON mm.R_AZIENDA = DHT.ID_AZIENDA \r\n"
				+ "AND mm.R_ANNO_FISCALE = DHT.ID_ANNO_DOC \r\n" + "AND mm.NUMERATORE_DOC = DHT.ID_NUMERO_DOC \r\n"
				+ "AND mm.NUM_RIGA_DOC = DHR.ID_RIGA_DOC \r\n" + "WHERE mm.NUM_REGISTRAZIONE = ('"
				+ ogg.iNumRegistrazione + "')\r\n" + ")\r\n" + "AND lt.COD_ARTICOLO <> '" + ogg.iIdArticolo + "' ";
		CachedStatement cs = new CachedStatement(statement);
		ResultSet rs = cs.executeQuery();
		if (rs.next()) {
			idArtPadre = rs.getString("COD_ARTICOLO");
			qtaPadre = rs.getBigDecimal("QTA1");
		}
		statement = "SELECT lt.COD_ARTICOLO\r\n,lt.QTA1" + " FROM LOGIS.LMOVIM_RIGA lt\r\n"
				+ "LEFT JOIN LOGIS.LMOVIM_TESTA lt2 \r\n" + "on lt.COD_SOCIETA = lt2.COD_SOCIETA \r\n"
				+ "AND lt.COD_MOVIM = lt2.CODICE \r\n" + "WHERE lt2.NOTE = (\r\n"
				+ "SELECT COALESCE (lt.NOTE,lt2.NOTE) \r\n" + "FROM LOGIS.LDOCHOST_TESTA AS DHT\r\n"
				+ "LEFT JOIN LOGIS.LDOCHOST_RIGA AS DHR\r\n" + "ON DHT.CODICE = DHR.CODICE_DOCHOST\r\n"
				+ "LEFT JOIN LOGIS.LMOVIM_RIGA lt \r\n" + "ON DHR.COD_SOCIETA = lt.COD_SOCIETA \r\n"
				+ "AND DHR.COD_MOVIM = lt.COD_MOVIM \r\n" + "LEFT JOIN LOGIS.LMOVIM_TESTA lt2 \r\n"
				+ "on lt.COD_SOCIETA = lt2.COD_SOCIETA \r\n" + "AND lt.COD_MOVIM = lt2.CODICE \r\n"
				+ "LEFT OUTER JOIN THIP.MOVIM_MAGAZ mm \r\n" + "ON mm.R_AZIENDA = DHT.ID_AZIENDA \r\n"
				+ "AND mm.R_ANNO_FISCALE = DHT.ID_ANNO_DOC \r\n" + "AND mm.NUMERATORE_DOC = DHT.ID_NUMERO_DOC \r\n"
				+ "AND mm.NUM_RIGA_DOC = DHR.ID_RIGA_DOC \r\n" + "WHERE mm.NUM_REGISTRAZIONE = ('"
				+ ogg.iNumRegistrazione + "')\r\n" + ")\r\n" + "AND lt.COD_ARTICOLO = '" + ogg.iIdArticolo + "' ";
		cs = new CachedStatement(statement);
		rs = cs.executeQuery();
		if (rs.next()) {
			idArtFiglio = rs.getString("COD_ARTICOLO");
			qtaFiglio = rs.getBigDecimal("QTA1");
		}
		try {
			if (rs != null) {
				rs.close();
			}
			// if (cs != null) {
			// cs.free();
			// }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new Object[] { idArtPadre, qtaPadre, idArtFiglio, qtaFiglio };
	}

	/**
	 * Aggiorno il costo sul movimento di magazzino con i parametri in entrata
	 * 
	 * @param ogg
	 * @param prezzo
	 * @param articolo
	 * @throws Exception
	 * @throws SQLException
	 */
	protected void aggiornaCostoMovimMagaz(YOggettinoAggMovim ogg, BigDecimal prezzo) throws Exception {
		if (prezzo.compareTo(BigDecimal.ZERO) == 0) {
			writeLog("Prezzo 0 per articolo : " + ogg.iIdArticolo + ", movimento : " + ogg.iNumRegistrazione);
		}
		CachedStatement cs = null;
		try {
			String query = "UPDATE THIP.MOVIM_MAGAZ  " + "SET COS_PRZ_EFF_PRM = '" + prezzo.toString() + "' "
					+ "WHERE R_AZIENDA = '" + ogg.iIdAzienda + "' AND NUM_REGISTRAZIONE = '" + ogg.iNumRegistrazione
					+ "' ";
			cs = new CachedStatement(query);
			int rs = cs.executeUpdate();
			if (rs == 1)
				ConnectionManager.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Exception();
		} finally {
			if (cs != null)
				cs.free();
		}
	}

	public String getKeyArticoloCosto(String idArticolo) {
		ResultSet rs = null;
		CachedStatement cs = null;
		try {
			String query = " SELECT " + ArticoloCostoTM.PROG_INT + " FROM " + ArticoloCostoTM.TABLE_NAME + " C"
					+ " WHERE C." + ArticoloCostoTM.ID_AZIENDA + " = '" + Azienda.getAziendaCorrente() + "' AND C."
					+ ArticoloCostoTM.ID_ARTICOLO + " = '" + idArticolo + "' " + " AND C."
					+ ArticoloCostoTM.R_TIPO_COSTO + " = '" + this.getIdCosto() + "' ";
			query += " AND " + ArticoloCostoTM.DATA_COSTO + " = '" + dateFormatSQL.format(getDataUltimaChiusura())
					+ "' "; // Aggiunto controllo su data chiusura in modo da inserire piu costi per diversi
							// periodi e cancellare solo quelli del periodo corrente, ricreandoli
			cs = new CachedStatement(query);
			rs = cs.executeQuery();
			if (rs.next()) {
				String key = KeyHelper.buildObjectKey(new Object[] { Azienda.getAziendaCorrente(), idArticolo,
						rs.getString(ArticoloCostoTM.PROG_INT) });
				return key;
			}
		} catch (SQLException e) {
			writeLog(e.getMessage());
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
		return null;
	}

	/**
	 * Prendo tutti i movimenti di magazzino dell'azienda per l'anno fiscale scelto
	 * e fino alla data di ultima chiusura (definitiva o provvisoria), che
	 * contribuiscano al costo medio e che siano,in OR: di acquisto ma conto lavoro
	 * (TP_MOV_MAGAZ = '2' AND TIPO_DOCUMENTO LIKE 'L%') di produzione (TP_MOV_MAGAZ
	 * = '8') generici ma cambi codice, quindi che il movimento di magazzino abbia
	 * un movimento logis collegato e che il movimento logis abbia NOTE LIKE 'CA%',
	 * che sta per cambio articolo
	 * 
	 * @return
	 * @throws SQLException
	 */
	protected ArrayList<YOggettinoAggMovim> caricaMovimentiDaAggiornare() throws SQLException {
		ArrayList<YOggettinoAggMovim> lista = new ArrayList<YOggettinoAggMovim>();
		String statement = "SELECT mm.R_AZIENDA, mm.NUM_REGISTRAZIONE, mm.TP_MOV_MAGAZ ,mm.TIPO_DOCUMENTO,  mm.R_ANNO_FISCALE , mm.NUMERATORE_DOC , mm.NUM_RIGA_DOC,\r\n"
				+ "mm.R_ANNO_FISCALE_PRD AS ANNO_ORD_PRD, mm.NUM_ORDINE_PRD AS NUM_ORD_PRD, mm.R_ARTICOLO  \r\n"
				+ "FROM THIP.MOVIM_MAGAZ mm\r\n" + "LEFT OUTER JOIN THIP.CAU_MOV_MAGAZ caumov \r\n"
				+ "ON mm.R_AZIENDA = caumov.ID_AZIENDA AND mm.R_CAU_MOV_MAG = caumov.ID_CAU_MOV_MAG\r\n"
				+ "LEFT OUTER JOIN THIP.CAU_FISCALE caufis \r\n"
				+ "ON caumov.ID_AZIENDA = caufis.ID_AZIENDA AND caumov.R_CAUSALE_FISCALE = caufis.ID_CAU_FISCALE AND mm.R_CAU_MOV_MAG = caumov.ID_CAU_MOV_MAG\r\n"
				+ "LEFT OUTER JOIN THIP.MAGAZZINI mag \r\n"
				+ "ON mag.ID_AZIENDA = mm.R_AZIENDA AND mag.ID_MAGAZZINO = mm.R_MAGAZZINO\r\n"
				+ "LEFT OUTER JOIN THIP.ARTICOLI art\r\n" + "ON mm.R_AZIENDA = art.ID_AZIENDA \r\n"
				+ "AND mm.R_ARTICOLO = art.ID_ARTICOLO\r\n" + "LEFT OUTER JOIN THIP.CLASSI_FISCALI cf \r\n"
				+ "ON cf.ID_AZIENDA = art.ID_AZIENDA \r\n" + "AND cf.ID_CLASSE_FISCALE = art.R_CLASSE_FISCALE \r\n"
				+ "LEFT OUTER JOIN THIP.ORDESE_ATV_PRD oap\r\n" + "ON oap.ID_AZIENDA = mm.R_AZIENDA \r\n"
				+ "AND oap.ID_ANNO_ORD = mm.R_ANNO_FISCALE_PRD\r\n" + "AND oap.ID_NUMERO_ORD = mm.NUM_ORDINE_PRD\r\n"
				+ "AND oap.ID_RIGA_ATTIVITA = mm.NUM_RIG_ORD_PRD \r\n"
				+ "AND oap.ID_RIGA_PRODOTTO = mm.NUM_DET_RIG_ORDPRD \r\n" + "WHERE\r\n" + "R_AZIENDA = '"
				+ this.getIdAzienda() + "'\r\n" + " AND mag.RILEV_FISCALE = 'Y' AND DTA_REGISTRAZIONE <= '"
				+ this.getDataUltimaChiusuraString() + "'\r\n" + "AND caufis.AZIONE_COS_MED  = 'Y'\r\n"
				+ "AND YEAR(DTA_REGISTRAZIONE) = '" + this.getIdAnnoFiscale() + "'\r\n"
				+ "AND (cf.ESCLUS_STP_FSC IS NULL OR cf.ESCLUS_STP_FSC <> 'Y')\r\n"
				+ "AND (oap.TIPO_PRODOTTO IS NULL OR oap.TIPO_PRODOTTO IN ('0','1'))\r\n" + "AND (\r\n"
				+ "(mm.TP_MOV_MAGAZ = '2' AND TIPO_DOCUMENTO LIKE 'L%') --acquisto conto lavoro\r\n" + "OR \r\n"
				+ "mm.TP_MOV_MAGAZ = '8' --produzione\r\n" + "OR\r\n"
				+ "mm.TP_MOV_MAGAZ = '1' AND EXISTS ( --GENERICI MA SOLO QUELLI CHE SONO CAMBI CODICE\r\n"
				+ "		SELECT *\r\n" + "		FROM LOGIS.LDOCHOST_TESTA AS DHT\r\n"
				+ "		LEFT JOIN LOGIS.LDOCHOST_RIGA AS DHR\r\n" + "		ON DHT.CODICE = DHR.CODICE_DOCHOST\r\n"
				+ "		LEFT JOIN LOGIS.LMOVIM_TESTA lt \r\n" + "		ON DHR.COD_SOCIETA = lt.COD_SOCIETA \r\n"
				+ "		AND DHR.COD_MOVIM = lt.CODICE\r\n"
				+ "		WHERE lt.NOTE like 'CA%' AND CAU_RIGA_HOST = 'CC+' \r\n"
				+ "		AND mm.R_AZIENDA = DHT.ID_AZIENDA \r\n" + "		AND mm.R_ANNO_FISCALE = DHT.ID_ANNO_DOC \r\n"
				+ "		AND mm.NUMERATORE_DOC = DHT.ID_NUMERO_DOC \r\n"
				+ "		AND mm.NUM_RIGA_DOC = DHR.ID_RIGA_DOC \r\n" + "	) \r\n" + ")\r\n"
				+ "ORDER BY art.LIVELLO_MIN_MODEL DESC,art.ID_ARTICOLO";
		ResultSet rs = null;
		CachedStatement cs = new CachedStatement(statement);
		writeLog("NR DA CERCARE:" + statement);
		rs = cs.executeQuery();
		YMovimentiDaAggiornareIterator iteratore = getIteratoreMovim(rs);
		while (iteratore.hasNext()) {
			YOggettinoAggMovim ogg = (YOggettinoAggMovim) iteratore.next();
			lista.add(ogg);
		}
		try {
			if (rs != null)
				rs.close();
			iteratore.closeCursor();
		} catch (SQLException ex) {
			ex.printStackTrace(Trace.excStream);
		}
		return lista;
	}

	protected PeriodoCalFiscale getPeriodoCalFiscaleUltimaChiusura() {
		ResultSet rs = null;
		CachedStatement cs = null;
		try {
			String query = "SELECT p." + PeriodoCalFiscaleTM.ID_AZIENDA + ",p." + PeriodoCalFiscaleTM.ID_ANNO_FISCALE
					+ ",p." + PeriodoCalFiscaleTM.ID_PER_ANNO_FSC + " " + "FROM THIP.PER_CALEN_FSC p "
					+ "WHERE ID_AZIENDA = '" + Azienda.getAziendaCorrente() + "'  "
					+ "AND STATO_CHIUS_MAG IN ('D','P') " + "ORDER BY p.DATA_FINE_PER DESC";
			cs = new CachedStatement(query);
			rs = cs.executeQuery();
			if (rs.next()) {
				return (PeriodoCalFiscale) PeriodoCalFiscale.elementWithKey(PeriodoCalFiscale.class,
						KeyHelper.buildObjectKey(new String[] { rs.getString(PeriodoCalFiscaleTM.ID_AZIENDA),
								rs.getString(PeriodoCalFiscaleTM.ID_ANNO_FISCALE),
								rs.getString(PeriodoCalFiscaleTM.ID_PER_ANNO_FSC) }),
						PersistentObject.NO_LOCK);
			}
		} catch (SQLException e) {
			writeLog(e.getMessage());
			e.printStackTrace(Trace.excStream);
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	protected Date recuperaDataUltimaChiusuraMagazzinoDef() {
		java.sql.Date d = null;
		ResultSet rs = null;
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
				String anno = rs.getString(PeriodoCalFiscaleTM.ID_ANNO_FISCALE);
				char esito = rs.getString(PeriodoCalFiscaleTM.STATO_CHIUS_MAG).charAt(0);
				String key = KeyHelper.buildObjectKey(new Object[] { Azienda.getAziendaCorrente(), anno });
				YCalendarioFiscale cal = (YCalendarioFiscale) YCalendarioFiscale
						.elementWithKey(YCalendarioFiscale.class, key, PersistentObject.NO_LOCK);
				if (cal != null) {
					cal.setDataUltimaChiusura(d);
					cal.setStatoChiusuraMag(esito);
					if (cal.save() >= 0) {
						ConnectionManager.commit();
					} else {
						ConnectionManager.rollback();
					}
				}
			}
		} catch (SQLException e) {
			writeLog(e.getMessage());
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
		return d;
	}

	/**
	 * Leggo tutti i movimenti di magazzino dell'anno scelto fino alla data di
	 * ultima chiusura di magazzino se il movimento è di acquisto e contribuisce al
	 * cmp faccio + carichi e + valori se non è acquisto ma entrata +scarichi+valori
	 * scarichi se non è acquisto ma uscita metto uno scarico della qta*-1
	 * 
	 * @throws SQLException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	protected void storicizzazioneCostoMedioAnno(boolean consideroAcq, String idArticolo)
			throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		ArrayList<YOggettinoLetturaValFis> listaRecordLetti = processaMovimMagaz(consideroAcq, idArticolo);
		for (YOggettinoLetturaValFis oggettoLetto : listaRecordLetti) {
			// per ogni oggetto letto cerco innanzitutto se esiste già un record con periodo
			// = this.ID_PER_ANNO_FISCALE (significa che è stato creato con la procedura)
			// se esiste allora lo tengo e vado in aggiornamento, se invece non esiste
			// allora lo creerò
			String[] c = new String[] { oggettoLetto.R_AZIENDA, oggettoLetto.R_ANNO_FISCALE.trim(), ID_PER_ANNO_FISCALE,
					oggettoLetto.R_RAG_FSC_MAG, oggettoLetto.R_ARTICOLO, oggettoLetto.R_CONFIG };
			String key = KeyHelper.buildObjectKey(c);
			YStoricoCmpArticolo storicoValvo = (YStoricoCmpArticolo) Factory.createObject(YStoricoCmpArticolo.class);
			storicoValvo.setKey(key);
			if (storicoValvo.getIdRaggrFiscMag() == null) {
				storicoValvo.setIdRaggrFiscMag("RF1");
			}
			storicoValvo.retrieve();// faccio la retrieve così se il record esisteva gli aggiornerò solo dei valori
			// altrimenti in save verrà creato il nuovo record
			storicoValvo.setQtaCarichi(oggettoLetto.CARICHI);
			storicoValvo.setQtaScarichi(oggettoLetto.SCARICHI);
			storicoValvo.setValoreCarichi(oggettoLetto.VAL_CARICHI);
			storicoValvo.setValoreScarichi(oggettoLetto.VAL_SCARICHI);

			// YStoricoCmpArticolo storicoPrecedente =
			// storicoValvo.cercaStoricoPrecedente(); //should always not be null
			// alla fine utilizzo il cerca storico precedene con query perchè ci mette quasi
			// la metà, 8-12 ms rispetto a minimo 19ms del carca storico con retrieveList
			YStoricoCmpArticolo storicoPrecedente = storicoValvo.cercaStoricoPrecedenteQuery(); // should always not be
			// null

			// Calcolo giacenza finale dello storico (copiata std pth): (giacenza finale
			// storico precedene + carichi ) - scarichi
			BigDecimal giacenzaFinale = getBigDecimalValue(storicoPrecedente.getGiacenzaFinale())
					.add(getBigDecimalValue(storicoValvo.getQtaCarichi()))
					.subtract(getBigDecimalValue(storicoValvo.getQtaScarichi()));
			// Calcolo del costo medio ponderato
			BigDecimal costoMedioPond = BigDecimal.ZERO;
			// Calcolo del dividendo per il costo medio ponderato
			BigDecimal costoMedioPondDiv = getBigDecimalValue(storicoPrecedente.getGiacenzaFinale())
					.add(getBigDecimalValue(storicoValvo.getQtaCarichi()));
			if (costoMedioPondDiv.compareTo(BigDecimal.ZERO) > 0) {
				costoMedioPond = (getBigDecimalValue(storicoPrecedente.getValoreFinale())
						.add(storicoValvo.getValoreCarichi())).divide(costoMedioPondDiv, 6, BigDecimal.ROUND_HALF_UP);
			}

			// CAlcolo del valore finale
			BigDecimal valoreFinale = giacenzaFinale.multiply(costoMedioPond);
			valoreFinale = valoreFinale.setScale(2, BigDecimal.ROUND_HALF_UP);

			storicoValvo.setGiacenzaFinale(giacenzaFinale);
			storicoValvo.setIdUnitaMisura(oggettoLetto.R_UNITA_MISURA); // aggiunto dssof3
			if (costoMedioPond.equals(BigDecimal.ZERO)) {
				costoMedioPond = storicoPrecedente.getCostoMedioPonderato();
			} else { // se il costo medio e' stato calcolato e ha valore e l'utente azzera costi
						// calcolati tolgo il manuale
				if (getAzzeraCostiManuali() == YAzzeraCostiManualiCMP.COSTI_CALCOLATI) {
					storicoValvo.setCostoMedioPonderatoMan(BigDecimal.ZERO);
				}
			}
			storicoValvo.setCostoMedioPonderato(costoMedioPond);
			storicoValvo.setValoreFinale(valoreFinale);
			if (storicoValvo.save() < 0)
				writeLog("Errore nel salvataggio del record in THIPPERS.YSTOR_CMP_ARTICOLO: " + storicoValvo.getKey()
						+ ", procedo con il resto.");
		}
		ConnectionManager.commit();
	}

	/**
	 * Leggo tutti i movimenti di magazzino dell'anno fiscale scelto e fino alla
	 * data di ultima chiusura (provvisoria o definitiva), raggruppati per R_AZIENDA
	 * R_ANNO_FISCALE R_RAG_FSC_MAG R_ARTICOLO R_CONFIG if(consideroAcquisto) se il
	 * movimento è di acquisto e contribuisce al cmp ed è entrata allora faccio +
	 * nei CARICHI (e VAL_CARICHI) se il movimento è di acquisto e contribuisce al
	 * cmp ed è uscita allora faccio - nei CARICHI (e VAL_CARICHI) se il movimento
	 * NON è di acquisto o NON contribuisce al cmp ed è entrata allora faccio +
	 * negli SCARICHI (e VAL_SCARICHI) se il movimento NON è di acquisto o NON
	 * contribuisce al cmp ed è uscita allora faccio - negli SCARICHI (e
	 * VAL_SCARICHI) else se il movimento contribuisce al cmp ed è entrata allora
	 * faccio + nei CARICHI (e VAL_CARICHI) se il movimento contribuisce al cmp ed è
	 * uscita allora faccio - nei CARICHI (e VAL_CARICHI) se il movimento NON
	 * contribuisce al cmp ed è entrata allora faccio + negli SCARICHI (e
	 * VAL_SCARICHI) se il movimento NON contribuisce al cmp ed è uscita allora
	 * faccio - negli SCARICHI (e VAL_SCARICHI)
	 * 
	 * @return
	 * @throws SQLException
	 */
	protected ArrayList<YOggettinoLetturaValFis> processaMovimMagaz(boolean consideroAcquisto, String idArticolo)
			throws SQLException {
		ArrayList<YOggettinoLetturaValFis> lista = new ArrayList<YOggettinoLetturaValFis>();
		String like = "", notLike = "";
		String whereArticolo = "";
		if (consideroAcquisto) {
			like = " AND TIPO_DOCUMENTO LIKE 'A%' ";
			notLike = " OR TIPO_DOCUMENTO NOT LIKE 'A%' ";
		} else {
			whereArticolo = " AND mm.R_ARTICOLO = '" + idArticolo + "' \r\n";
		}
		String statement = "SELECT\r\n" + "        R_AZIENDA,\r\n"
				+ "        YEAR(DTA_REGISTRAZIONE) AS R_ANNO_FISCALE,\r\n" + "        mag.R_RAG_FSC_MAG,\r\n"
				+ "        R_ARTICOLO,\r\n" + "        R_CONFIG,mm.R_UNITA_MISURA,\r\n" + "        --CARICHI\r\n"
				+ "        SUM(CASE \r\n" + "        		WHEN caufis.AZIONE_COS_MED='Y' " + like
				+ " AND mm.QTA_GIAC='E' THEN QTA_MOVIMENTO \r\n" + "    			WHEN caufis.AZIONE_COS_MED='Y' "
				+ like + " AND mm.QTA_GIAC='U' THEN QTA_MOVIMENTO * -1\r\n" + "    			ELSE 0 END) AS CARICHI,\r\n"
				+ "		--SCARICHI\r\n" + "    	SUM(CASE \r\n" + "    			WHEN (caufis.AZIONE_COS_MED = 'N' "
				+ notLike + ") AND mm.QTA_GIAC ='U' THEN QTA_MOVIMENTO \r\n"
				+ "    			WHEN (caufis.AZIONE_COS_MED = 'N' " + notLike
				+ ") AND mm.QTA_GIAC ='E' THEN QTA_MOVIMENTO * -1\r\n" + "    			ELSE 0 END\r\n"
				+ "    	) AS SCARICHI,\r\n" + "         --VALORI CARICHI\r\n" + "        SUM(CASE \r\n"
				+ "        		WHEN caufis.AZIONE_COS_MED='Y' " + like
				+ " AND mm.QTA_GIAC='E' THEN (QTA_MOVIMENTO * mm.COS_PRZ_EFF_PRM) \r\n"
				+ "    			WHEN caufis.AZIONE_COS_MED='Y' " + like
				+ " AND mm.QTA_GIAC='U' THEN (QTA_MOVIMENTO * mm.COS_PRZ_EFF_PRM) * -1\r\n"
				+ "    			ELSE 0 END) AS VAL_CARICHI,\r\n" + "		--VALORI SCARICHI\r\n"
				+ "    	SUM(CASE \r\n" + "    			WHEN (caufis.AZIONE_COS_MED = 'N' " + notLike
				+ ") AND mm.QTA_GIAC ='U' THEN (QTA_MOVIMENTO * mm.COS_PRZ_EFF_PRM) \r\n"
				+ "    			WHEN (caufis.AZIONE_COS_MED = 'N' " + notLike
				+ ") AND mm.QTA_GIAC ='E' THEN (QTA_MOVIMENTO * mm.COS_PRZ_EFF_PRM) * -1\r\n"
				+ "    			ELSE 0 END\r\n" + "    	) AS VAL_SCARICHI        \r\n" + "    FROM\r\n"
				+ "        THIP.MOVIM_MAGAZ mm\r\n" + "    LEFT OUTER JOIN\r\n"
				+ "        THIP.CAU_MOV_MAGAZ cauacq ON mm.R_AZIENDA = cauacq.ID_AZIENDA AND mm.R_CAU_MOV_MAG = cauacq.ID_CAU_MOV_MAG\r\n"
				+ "    LEFT OUTER JOIN\r\n"
				+ "        THIP.CAU_FISCALE caufis ON cauacq.ID_AZIENDA = caufis.ID_AZIENDA AND cauacq.R_CAUSALE_FISCALE = caufis.ID_CAU_FISCALE AND mm.R_CAU_MOV_MAG = cauacq.ID_CAU_MOV_MAG\r\n"
				+ "    LEFT OUTER JOIN\r\n"
				+ "        THIP.MAGAZZINI mag ON mag.ID_AZIENDA = mm.R_AZIENDA AND mag.ID_MAGAZZINO = mm.R_MAGAZZINO\r\n"
				+ "		LEFT OUTER JOIN \r\n"
				+ "			THIP.ARTICOLI art ON mag.ID_AZIENDA = art.ID_AZIENDA AND mm.R_ARTICOLO = art.ID_ARTICOLO \r\n"
				+ "		LEFT OUTER JOIN \r\n"
				+ "			THIP.CLASSI_FISCALI cf ON cf.ID_AZIENDA = art.ID_AZIENDA AND cf.ID_CLASSE_FISCALE = art.R_CLASSE_FISCALE\r\n "
				+ "    WHERE\r\n" + "        R_AZIENDA = '" + Azienda.getAziendaCorrente() + "'\r\n"
				+ "     AND mag.RILEV_FISCALE = 'Y'    AND DTA_REGISTRAZIONE <= '" + this.getDataUltimaChiusuraString()
				+ "'\r\n  AND YEAR(DTA_REGISTRAZIONE) = '" + this.getIdAnnoFiscale() + "'\r\n"
				+ " 		AND (cf.ESCLUS_STP_FSC IS NULL OR cf.ESCLUS_STP_FSC <> 'Y')\r\n " + whereArticolo
				+ "    GROUP BY\r\n" + "        R_AZIENDA,\r\n" + "        YEAR(DTA_REGISTRAZIONE),\r\n"
				+ "        mag.R_RAG_FSC_MAG,\r\n" + "        R_ARTICOLO,\r\n"
				+ "        R_CONFIG,mm.R_UNITA_MISURA\r\n" + "	ORDER BY R_ARTICOLO ";
		statement = "SELECT\r\n" + "	R_AZIENDA,\r\n" + "	YEAR(DTA_REGISTRAZIONE) AS R_ANNO_FISCALE,\r\n"
				+ "	mag.R_RAG_FSC_MAG,\r\n" + "	R_ARTICOLO,\r\n" + "	R_CONFIG,\r\n" + "	mm.R_UNITA_MISURA,\r\n"
				+ "	SUM(CASE WHEN (caufis.AZIONE_COS_MED = 'Y' " + like + " AND mm.QTA_GIAC IN ('U', 'E') )\r\n"
				+ "			THEN (\r\n" + "				CASE caufis.AZIONE_MAGAZ WHEN 'E' THEN QTA_MOVIMENTO\r\n"
				+ "				WHEN 'U' THEN QTA_MOVIMENTO * -1 \r\n" + "				ELSE 0 END\r\n"
				+ "			)\r\n" + "			WHEN (caufis.AZIONE_COS_MED = 'Y' " + like
				+ " AND mm.QTA_GIAC IN ('-') )\r\n" + "			THEN QTA_MOVIMENTO\r\n" + "		ELSE 0 END\r\n"
				+ "		) AS CARICHI,\r\n" + "	SUM(CASE WHEN (caufis.AZIONE_COS_MED = 'N' " + notLike
				+ " AND mm.QTA_GIAC IN ('U', 'E') )\r\n" + "			THEN (\r\n"
				+ "				CASE caufis.AZIONE_MAGAZ WHEN 'E' THEN QTA_MOVIMENTO * -1\r\n"
				+ "				WHEN 'U' THEN QTA_MOVIMENTO \r\n" + "				ELSE 0 END\r\n" + "			)\r\n"
				+ "    		WHEN (caufis.AZIONE_COS_MED = 'Y' " + notLike + " AND mm.QTA_GIAC IN ('-') )\r\n"
				+ "			THEN QTA_MOVIMENTO\r\n" + "		ELSE 0 END\r\n" + "    	) AS SCARICHI,\r\n"
				+ "	SUM(CASE WHEN (caufis.AZIONE_COS_MED = 'Y' " + like + " AND mm.QTA_GIAC IN ('U', 'E') )\r\n"
				+ "			THEN (\r\n"
				+ "				CASE caufis.AZIONE_MAGAZ WHEN 'E' THEN (QTA_MOVIMENTO * mm.COS_PRZ_EFF_PRM)\r\n"
				+ "				WHEN 'U' THEN (QTA_MOVIMENTO * mm.COS_PRZ_EFF_PRM) * -1 \r\n"
				+ "				ELSE 0 END\r\n" + "			)\r\n" + "			WHEN (caufis.AZIONE_COS_MED = 'Y' "
				+ like + " AND mm.QTA_GIAC IN ('-') )\r\n" + "			THEN (QTA_MOVIMENTO * mm.COS_PRZ_EFF_PRM)\r\n"
				+ "		ELSE 0 END\r\n" + "		) AS VAL_CARICHI,\r\n" + "	SUM(CASE WHEN (caufis.AZIONE_COS_MED = 'N' "
				+ notLike + " AND mm.QTA_GIAC IN ('U', 'E') )\r\n" + "			THEN (\r\n"
				+ "				CASE caufis.AZIONE_MAGAZ WHEN 'E' THEN (QTA_MOVIMENTO * mm.COS_PRZ_EFF_PRM) * -1\r\n"
				+ "				WHEN 'U' THEN (QTA_MOVIMENTO * mm.COS_PRZ_EFF_PRM)  \r\n"
				+ "				ELSE 0 END\r\n" + "			)\r\n" + "    		WHEN (caufis.AZIONE_COS_MED = 'Y' "
				+ notLike + " AND mm.QTA_GIAC IN ('-') )\r\n"
				+ "			THEN (QTA_MOVIMENTO * mm.COS_PRZ_EFF_PRM)\r\n" + "		ELSE 0 END\r\n"
				+ "    	) AS VAL_SCARICHI\r\n" + "FROM\r\n" + "	THIP.MOVIM_MAGAZ mm\r\n" + "LEFT OUTER JOIN\r\n"
				+ "        THIP.CAU_MOV_MAGAZ cauacq ON\r\n" + "	mm.R_AZIENDA = cauacq.ID_AZIENDA\r\n"
				+ "	AND mm.R_CAU_MOV_MAG = cauacq.ID_CAU_MOV_MAG\r\n" + "LEFT OUTER JOIN\r\n"
				+ "        THIP.CAU_FISCALE caufis ON\r\n" + "	cauacq.ID_AZIENDA = caufis.ID_AZIENDA\r\n"
				+ "	AND cauacq.R_CAUSALE_FISCALE = caufis.ID_CAU_FISCALE\r\n"
				+ "	AND mm.R_CAU_MOV_MAG = cauacq.ID_CAU_MOV_MAG\r\n" + "LEFT OUTER JOIN\r\n"
				+ "        THIP.MAGAZZINI mag ON\r\n" + "	mag.ID_AZIENDA = mm.R_AZIENDA\r\n"
				+ "	AND mag.ID_MAGAZZINO = mm.R_MAGAZZINO\r\n" + "LEFT OUTER JOIN \r\n"
				+ "			THIP.ARTICOLI art ON\r\n" + "	mag.ID_AZIENDA = art.ID_AZIENDA\r\n"
				+ "	AND mm.R_ARTICOLO = art.ID_ARTICOLO\r\n" + "LEFT OUTER JOIN \r\n"
				+ "			THIP.CLASSI_FISCALI cf ON\r\n" + "	cf.ID_AZIENDA = art.ID_AZIENDA\r\n"
				+ "	AND cf.ID_CLASSE_FISCALE = art.R_CLASSE_FISCALE";
		statement += "    WHERE\r\n" + "        R_AZIENDA = '" + Azienda.getAziendaCorrente() + "'\r\n"
				+ "     AND mag.RILEV_FISCALE = 'Y'    AND DTA_REGISTRAZIONE <= '" + this.getDataUltimaChiusuraString()
				+ "'\r\n  AND YEAR(DTA_REGISTRAZIONE) = '" + this.getIdAnnoFiscale() + "'\r\n"
				+ " 		AND (cf.ESCLUS_STP_FSC IS NULL OR cf.ESCLUS_STP_FSC <> 'Y')\r\n " + whereArticolo
				+ "    GROUP BY\r\n" + "        R_AZIENDA,\r\n" + "        YEAR(DTA_REGISTRAZIONE),\r\n"
				+ "        mag.R_RAG_FSC_MAG,\r\n" + "        R_ARTICOLO,\r\n"
				+ "        R_CONFIG,mm.R_UNITA_MISURA\r\n" + "	ORDER BY R_ARTICOLO ";
		ResultSet rs = null;
		CachedStatement cs = new CachedStatement(statement);
		rs = cs.executeQuery();
		YStoricCMPValvoIterator iteratore = getIteratore(rs);
		while (iteratore.hasNext()) {
			YOggettinoLetturaValFis ogg = (YOggettinoLetturaValFis) iteratore.next();
			lista.add(ogg);
		}
		try {
			if (rs != null)
				rs.close();
			iteratore.closeCursor();
		} catch (SQLException ex) {
			ex.printStackTrace(Trace.excStream);
		}
		return lista;
	}

	protected YStoricCMPValvoIterator getIteratore(ResultSet rs) throws SQLException {
		YStoricCMPValvoIterator iteratore = new YStoricCMPValvoIterator(rs);
		return iteratore;
	}

	protected YMovimentiDaAggiornareIterator getIteratoreMovim(ResultSet rs) throws SQLException {
		YMovimentiDaAggiornareIterator iteratore = new YMovimentiDaAggiornareIterator(rs);
		return iteratore;
	}

	@Override
	protected String getClassAdCollectionName() {
		return "YValorizzazioneFiscaleVal";
	}

	public void writeLog(String log) {
		System.out.println(log);
		getOutput().println(log);
	}

	protected BigDecimal getBigDecimalValue(BigDecimal qta) {
		if (qta == null)
			return BigDecimal.ZERO;
		else
			return qta;
	}
}