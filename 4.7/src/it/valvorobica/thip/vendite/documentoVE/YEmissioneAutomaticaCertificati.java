package it.valvorobica.thip.vendite.documentoVE;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.thera.thermfw.base.TimeUtils;
import com.thera.thermfw.batch.BatchJob;
import com.thera.thermfw.batch.BatchOptions;
import com.thera.thermfw.batch.BatchRunnable;
import com.thera.thermfw.batch.BatchService;
import com.thera.thermfw.persist.CachedStatement;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;

import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.base.comuniVenAcq.DdtTes;
import it.thera.thip.base.comuniVenAcq.DdtTesTM;
import it.thera.thip.base.comuniVenAcq.ReportDdtBollaTestata;
import it.thera.thip.base.documentoDgt.DescrittoreStampaDgt;
import it.thera.thip.base.documentoDgt.DescrittoreStampaDgtTM;
import it.thera.thip.base.generale.ParametroPsn;
import it.valvorobica.thip.base.cliente.YClienteVenditaTM;
import it.valvorobica.thip.vendite.generaleVE.YEmissioneAutomaticaCertificatiEnum;

/**
 * <h1>Softre Solutions</h1>
 * 
 * @author Daniele Signoroni 22/04/2023 <br>
 *         <br>
 *         <b>71053 DSSOF3 22/04/2023</b>
 *         <p>
 *         Batch per l'emissione automatica dei certificati.<br>
 *         Prende tutti i DDT data una data, questi DDT devono avere la
 *         colonna:<br>
 *         Y_NOTE_CERT_DDT_V01.PRES_CERT = 'Y' e la colonna:<br>
 *         Y_NOTE_CERT_DDT_V01.MANUALE = 'N'.<br>
 *         Ovvero richiedono i certificati ma non in modo manuale, e quindi
 *         automatico. Per ognuno di questi documenti viene lanciato il batch
 *         {@linkplain YReportDdtBollaBatch} con tipo rpt = "StampaCert31VA",
 *         ovver la stampa dei certificati.<br>
 *         La stampa visto che e' DDT viene sottomessa in modalita'
 *         {@linkplain ReportDdtBollaTestata#RISTAMPA}<br>
 *         Una volta sottomessa la stampa finche' non finisce non itero il
 *         prossimo docs, appena terminata la stampa, setto
 *         {@link DdtTesTM#FLAG_RIS_UTE_5} = 'Y' per evidenziare che per quel
 *         DDT i certificati sono stati emessi. <br>
 *         <br>
 *         <b>NB : </b>Ovviamente attivo SSD e spedizione digitale.<br>
 *         </p>
 *         <b>71124 AGSOF3 07/06/2023</b>
 *         <p>
 *         Parto dal 01/06/2023.
 *         </p>
 *         <b>71127 AGSOF3 08/06/2023</b>
 *         <p>
 *         Data bolla di partenza presa da un parametro personalizzato.
 *         </p>
 *         <b>71139 DSSOF3 14/06/2023</b>
 *         <p>
 *         Per la generazione dogDgt e docSSD prendo i flag dal
 *         {@link DescrittoreStampaDgt}.<br>
 *         La cui chiave e' {
 *         {@value DescrittoreStampaDgtTM#ID_AZIENDA},{@value DescrittoreStampaDgtTM#ID_MODELLO_RPT}
 *         }.
 *         </p>
 *         <b>71240 AGSOF3 28/09/2023</b>
 *         <p>
 *         Aggiunta gestione casistica
 *         {@link YEmissioneAutomaticaCertificatiEnum#DA_VALUTARE}, per cui se
 *         per alcune righe mancano i certificati updato il flag a 'Da
 *         valutare'.
 *         </p>
 *         <b> DSSOF3 06/10/2023</b>
 *         <p>
 *         Correggere update delle righe, non veniva mai messo automatico per
 *         cui quelli in stato 'Da valutare', non venivano mai messi in
 *         'Automatico'.<br>
 *         Se la riga ha superato le prime due condizioni ed e' 'Da valutare',
 *         nel caso in cui debba diventare 'Automatica' eseguo un ulteriore
 *         controllo per assicurarmi che sull'anagrafica cliente vi sia il flag
 *         di Emissione Automatica = 'Automatica'.
 *         </p>
 */

public class YEmissioneAutomaticaCertificati extends BatchRunnable {

	protected Date iDataDDT;

	public Date getDataDDT() {
		return iDataDDT;
	}

	public void setDataDDT(Date iDataDDT) {
		this.iDataDDT = iDataDDT;
	}

	public YEmissioneAutomaticaCertificati() {
		setDataDDT(TimeUtils.getCurrentDate());
	}

	@Override
	protected boolean run() {
		writeLog("*** INIZIATA L'EMISSIONE DEI CERTIFICATI AUTOMATICI IN DATA : " + this.getDataDDT().toString()
				+ " ***");
		writeLog("");
		boolean isOk = false;
		List<String> listaDocsDDTOdierni = getListaDDTOdierni(true);
		if (listaDocsDDTOdierni.size() > 0) {
			// AGSOF3 INI
			controllaRigheDDTAutom(listaDocsDDTOdierni);
			// AGSOF3 FIN
			listaDocsDDTOdierni = getListaDDTOdierni(false);
			isOk = emissioneCertificati(listaDocsDDTOdierni);
		} else {
			isOk = true; // 71139 per evitare che termini con errore, perche' errore non e'
		}
		settaManualiDocumentiNonProcessatiUltimi15GG();
		writeLog("*** TERMINATA L'EMISSIONE DEI CERTIFICATI AUTOMATICI IN DATA : " + this.getDataDDT().toString()
				+ " ***");
		return isOk;
	}
	
	/**
	 * 
	 * <h1>Title</h1>
	 * <br>
	 * Daniele Signoroni 03/11/2023
	 * <br>
	 * <p>
	 * </p>
	 */
	protected void settaManualiDocumentiNonProcessatiUltimi15GG() {
		List<String> lst = new ArrayList<String>();
		SimpleDateFormat formatSQL = new SimpleDateFormat("yyyyMMdd");
		Date data = this.getDataDDT() != null ? this.getDataDDT() : TimeUtils.getCurrentDate();
		String dataPartenza = ParametroPsn.getValoreParametroPsn("YEmissioneAutomCert", "DataDiPartenza"); // 71127
		String daValutare = "( C.MANUALE = 'N' OR (C.MANUALE = 'V' AND DATA_BOLLA < DATEADD("+formatSQL.format(data)+", -16, GETDATE() ) ))";
		String where = " SELECT "
				+ "T."+YDocumentoVenditaTM.ID_AZIENDA+" ,"
				+ "T."+YDocumentoVenditaTM.ID_ANNO_DOC+" ,"
				+ "T."+YDocumentoVenditaTM.ID_NUMERO_DOC+"  FROM "+YDocumentoVenditaTM.TABLE_NAME+" T "
				+ "LEFT OUTER JOIN SOFTRE.Y_NOTE_CERT_DDT_V01 C  "
				+ "ON T."+YDocumentoVenditaTM.ID_AZIENDA+" = C.ID_AZIENDA  "
				+ "AND T."+YDocumentoVenditaTM.NUMERO_BOLLA+"  = C.NUMERO_BOLLA  "
				+ "AND T."+YDocumentoVenditaTM.ANNO_BOLLA+"  = C.ANNO_BOLLA  "
				+ "LEFT OUTER JOIN "+DdtTesTM.TABLE_NAME+" D  "
				+ "ON D."+DdtTesTM.ID_AZIENDA+" = C.ID_AZIENDA "
				+ "AND D."+DdtTesTM.ID_NUMERO_DDT+" = C.NUMERO_BOLLA  "
				+ "AND D."+DdtTesTM.ID_ANNO_DDT+" = C.ANNO_BOLLA "
				+ "WHERE D."+DdtTesTM.FLAG_RIS_UTE_5+" != 'Y' "
				+ "AND C.PRES_CERT = 'Y' "
				+ "AND " + daValutare + " "
				+ "AND D."+DdtTesTM.ID_AZIENDA+" = '"+Azienda.getAziendaCorrente()+"' " //aggiunta 16/05/2023
				+ "AND T."+YDocumentoVenditaTM.DATA_BOLLA+" > '"+dataPartenza.replace("-", "")+"'";//la generazione automatica la facciamo partire dal 1 giugno 23
		ResultSet rs = null;
		CachedStatement cs = null;
		try {
			cs = new CachedStatement(where);
			writeLog("Statement per la ricerca :\n");
			writeLog(where);
			rs = cs.executeQuery();
			while (rs.next()) {
				String c = KeyHelper.buildObjectKey(new String[] { rs.getString(1), rs.getString(2), rs.getString(3) });
				lst.add(c);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				if(rs != null) {
					rs.close();
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (Iterator<String> iterator = lst.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			try {
				CachedStatement cs1 = null;
				YDocumentoVendita docVen = (YDocumentoVendita) YDocumentoVendita.elementWithKey(YDocumentoVendita.class, key, PersistentObject.NO_LOCK);
				if(docVen != null) {
					String update = " UPDATE "+YDocumentoVenRigaPrmTM.TABLE_NAME_EXT+" R SET R."+YDocumentoVenRigaPrmTM.EMISSIONE_AUTOM_CERT+" = '"+YEmissioneAutomaticaCertificatiEnum.MANUALE+"' "
						+ "WHERE R."+YDocumentoVenRigaPrmTM.ID_AZIENDA+" = '"+docVen.getIdAzienda()+"' "
						+ "AND R."+YDocumentoVenRigaPrmTM.ID_ANNO_DOC+" = '"+docVen.getAnnoDocumento()+"' "
						+ "AND R."+YDocumentoVenRigaPrmTM.ID_NUMERO_DOC+" = '"+docVen.getNumeroDocumento()+"' ";
					cs1 = new CachedStatement(update);
					if(cs1.executeUpdate() > 0) {
						ConnectionManager.commit();
					}else {
						ConnectionManager.commit();
					}
					cs1.free();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * In questo metodo controllo per ogni ddt se esiste almeno una riga che ha,
	 * nella vista Y_DOC_VEN_TP_CERT_V01, un record con TIPO_CERTIFICATO IN
	 * ('ORIGINALEL','ORIGINALE','NO_CERT'), allora metto in tutte le righe la
	 * colonna THIPPERS.YDOCVEN_RIG_PRM_V01.EMISSIONE_AUTOM_CERT = '1'
	 * 
	 * @param listaDocsDDTOdierni
	 */
	protected void controllaRigheDDTAutom(List<String> listaDocsDDTOdierni) {
		Iterator<String> iterDocs = listaDocsDDTOdierni.iterator();
		while (iterDocs.hasNext()) {
			CachedStatement cs = null;
			try {
				String[] keys = KeyHelper.unpackObjectKey(iterDocs.next());
				if (keys != null) {
					String update = "UPDATE YRIGA " + "SET YRIGA." + YDocumentoVenRigaPrmTM.EMISSIONE_AUTOM_CERT + " = "
							+ "    CASE WHEN EXISTS (" + "            SELECT * "
							+ "            FROM SOFTRE.Y_DOC_VEN_TP_CERT_V01 DCERT "
							+ "            WHERE DCERT.ID_AZIENDA = YRIGA." + YDocumentoVenRigaPrmTM.ID_AZIENDA + " "
							+ "            AND DCERT.ID_ANNO_DOC = YRIGA." + YDocumentoVenRigaPrmTM.ID_ANNO_DOC + " "
							+ "            AND DCERT.ID_NUMERO_DOC = YRIGA." + YDocumentoVenRigaPrmTM.ID_NUMERO_DOC
							+ " "
							// + " AND DCERT.ID_RIGA_DOC = YRIGA."+YDocumentoVenRigaPrmTM.ID_RIGA_DOC+" "
							+ "            AND TIPO_CERTIFICATO IN ('ORIGINALEL','ORIGINALE') " + "        )"
							+ "    THEN '" + YEmissioneAutomaticaCertificatiEnum.MANUALE + "' " + " WHEN EXISTS ( "// 71240
							// AGGIUNTO
							// QUESTO
							// CASE
							// PER
							// SETTARE
							// IL
							// VALORE
							// DA
							// VALUTARE
							+ "            SELECT *  " + "            FROM SOFTRE.Y_DOC_VEN_TP_CERT_V01 DCERT  "
							+ "            WHERE DCERT.ID_AZIENDA = YRIGA.ID_AZIENDA   "
							+ "            AND DCERT.ID_ANNO_DOC = YRIGA.ID_ANNO_DOC "
							+ "            AND DCERT.ID_NUMERO_DOC = YRIGA.ID_NUMERO_DOC   "
							+ "            AND TIPO_CERTIFICATO IN ('NO_CERT')  " + "        )  " + "	THEN '"
							+ YEmissioneAutomaticaCertificatiEnum.DA_VALUTARE + "' " // se il tipo certificato è no_cert
							// setto sulla riga doc ven
							// nell'emissione automatica
							// certificati = da valutare
							// Aggiunta 06/10/2023
							+ "   WHEN YRIGA." + YDocumentoVenRigaPrmTM.EMISSIONE_AUTOM_CERT + " = '"
							+ YEmissioneAutomaticaCertificatiEnum.DA_VALUTARE + "'" + "		AND EXISTS ("
							+ "		SELECT" + "				1" + "		FROM" + "				"
							+ YClienteVenditaTM.TABLE_NAME_EXT + " CV" + "		INNER JOIN "
							+ YDocumentoVenditaTM.TABLE_NAME + " T" + "			ON" + "				T."
							+ YDocumentoVenditaTM.ID_AZIENDA + " = YRIGA." + YDocumentoVenRigaPrmTM.ID_AZIENDA + " "
							+ "			AND T." + YDocumentoVenditaTM.ID_ANNO_DOC + " = YRIGA."
							+ YDocumentoVenRigaPrmTM.ID_ANNO_DOC + " " + "			AND T."
							+ YDocumentoVenditaTM.ID_NUMERO_DOC + " = YRIGA." + YDocumentoVenRigaPrmTM.ID_NUMERO_DOC
							+ " " + "		WHERE" + "				CV." + YClienteVenditaTM.ID_AZIENDA + " = T."
							+ YDocumentoVenditaTM.ID_AZIENDA + " " + "			AND CV." + YClienteVenditaTM.ID_CLIENTE
							+ " = T." + YDocumentoVenditaTM.R_CLIENTE + " " + "			AND CV."
							+ YClienteVenditaTM.EMISSIONE_AUTOM_CERT + " = '"
							+ YEmissioneAutomaticaCertificatiEnum.AUTOMATICA + "'" + "		" + "			) THEN '"
							+ YEmissioneAutomaticaCertificatiEnum.AUTOMATICA + "'" + "		ELSE YRIGA."
							+ YDocumentoVenRigaPrmTM.EMISSIONE_AUTOM_CERT + " END "
							// Fine Aggiunta 06/10/2023
							+ "FROM " + YDocumentoVenRigaPrmTM.TABLE_NAME_EXT + " YRIGA " + "WHERE YRIGA."
							+ YDocumentoVenRigaPrmTM.ID_AZIENDA + " = '" + keys[0] + "' " + "AND YRIGA."
							+ YDocumentoVenRigaPrmTM.ID_ANNO_DOC + " = '" + keys[1] + "' " + "AND YRIGA."
							+ YDocumentoVenRigaPrmTM.ID_NUMERO_DOC + " = '" + keys[2] + "'";// 71115 <=
					cs = new CachedStatement(update);
					int risUpdate = cs.executeUpdate();
					if (risUpdate > 0) {
						ConnectionManager.commit();
					} else {
						ConnectionManager.rollback();
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (cs != null) {
						cs.free();
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}

	}

	@SuppressWarnings({ "unused", "unchecked" })
	protected boolean emissioneCertificati(List<String> listaDocsDDTOdierni) {
		boolean isOk = true;
		Iterator<String> iterDocs = listaDocsDDTOdierni.iterator();
		while (iterDocs.hasNext()) {
			try {
				YDocumentoVendita docVen = (YDocumentoVendita) YDocumentoVendita.elementWithKey(YDocumentoVendita.class,
						iterDocs.next(), PersistentObject.NO_LOCK);
				// stampa certificati
				YReportDdtBollaBatch ddt = (YReportDdtBollaBatch) Factory.createObject(YReportDdtBollaBatch.class);
				BatchOptions batchOptions = (BatchOptions) Factory.createObject(BatchOptions.class);
				boolean ok = batchOptions.initDefaultValues(YReportDdtBollaBatch.class, "STAMPA_BOLLE", "RUN");
				ddt.setBatchJob(batchOptions.getBatchJob());
				ddt.setScheduledJob(batchOptions.getScheduledJob());
				String report = "StampaCert31VA";
				// 71139 Ini
				DescrittoreStampaDgt descrStampa = (DescrittoreStampaDgt) DescrittoreStampaDgt.elementWithKey(
						DescrittoreStampaDgt.class,
						KeyHelper.buildObjectKey(new String[] { Azienda.getAziendaCorrente(), report }),
						PersistentObject.NO_LOCK);
				if (descrStampa == null) {
					writeLog("NON ESISTE IL DESCRITTORE DI STAMPA DIGITALE PER IL REPORT: " + report + " PER L'AZIENDA:"
							+ Azienda.getAziendaCorrente());
					return false;
				}
				// 71139 Fin
				ddt.setReportId(report);
				// ddt.setExecutePrint(true);
				ddt.setNumeroDocumento(docVen.getKey());
				ddt.setChiaviSelezionati(docVen.getKey());
				ddt.getBatchJob().setDescription("Stampa automatica certificati");
				ddt.getBatchJob().setUserDescription("Stampa automatica certificati");
				ddt.getBatchJob().setBatchQueueId("DefQueue");
				// ddt.setDocDgtEnabled(true);
				// ddt.setSSDEnabled(true);
				ddt.setDocDgtEnabled(descrStampa.isAttivaGenDocDgt()); // 71139
				ddt.setSSDEnabled(descrStampa.isAttivaGenSSD()); // 71139
				ddt.setVettore1(docVen.getVettore1());
				ddt.setCausaleTrasporto(docVen.getCausaleTrasporto());
				ddt.setLocalitaDestinatario(docVen.getLocalitaDestinatario());
				ddt.setModalitaSpedizione(docVen.getModalitaSpedizione());
				ddt.setAspettoEsteriore(docVen.getAspettoEsteriore());
				ddt.setVolume(docVen.getVolume());
				ddt.setDescrModalitaSpedizione(docVen.getDescrModalitaSpedizione());
				ddt.setPesoNetto(docVen.getPesoNetto());
				ddt.setPesoLordo(docVen.getPesoLordo());
				ddt.setRicalcoloPesi(docVen.isRicalcolaPesiEVolume());
				ddt.setStampaInLingua(docVen.isStampaFatturaInLingua());
				ddt.setDescrVettore3(docVen.getDescrVettore3());
				ddt.setDescrVettore2(docVen.getDescrVettore2());
				ddt.setDescrVettore1(docVen.getDescrVettore1());
				ddt.setTipoDestinatario(docVen.getTipoDestinatario());
				ddt.setIdSequenzaInd(docVen.getIdSequenzaInd());// 70930
				ddt.setDescrModalitaConsegna(docVen.getDescrModalitaConsegna());
				ddt.setDataInizioTrasporto(docVen.getDataInizioTrasporto());
				ddt.setNazione(docVen.getNazione());
				ddt.setCAPDestinatario(docVen.getCAPDestinatario());
				ddt.setClienteDestinatario(docVen.getClienteDestinatario());
				ddt.setRagioneSocaleDest(docVen.getRagioneSocaleDest());
				ddt.setNumeroColli(docVen.getNumeroColli());
				ddt.setModalitaConsegna(docVen.getModalitaConsegna());
				ddt.setProvincia(docVen.getProvincia());
				ddt.setIndirizzoDestinatario(docVen.getIndirizzoDestinatario());// 71034
				ddt.setIdAnagrafico(docVen.getIdAnagrafico());
				ddt.setOraInizioTrasporto(docVen.getOraInizioTrasporto());
				ddt.setVettore3(docVen.getVettore3());
				ddt.setVettore2(docVen.getVettore2());// 70930
				ddt.setTargaMezzo(docVen.getTargaMezzo());
				ddt.getBatchJob().setLog(docVen.getKey());
				ddt.setAzione(ReportDdtBollaTestata.RIS_COL); // cambiato in RIS_COL 16/05/2023
				if (ddt.save() >= 0) {
					ConnectionManager.commit();
					BatchService.submitJob(ddt.getBatchJob());
					writeLog("SOTTOMESSA LA STAMPA CERTIFICATI DEL DOC : " + docVen.getKey());
					long timeout = 60; // secondi per il timeout
					long start = System.currentTimeMillis();
					boolean isStop = false;
					boolean isStopWithWarning = false;
					boolean isStopWithError = false;
					while (!isStop) {
						BatchJob job = ddt.getBatchJob();
						if (job.retrieve()) {
							if (job.getStatus() == BatchJob.COMPLETED || job.getStatus() == BatchJob.COMPLETED_ERROR) {
								isStop = true;
								if (job.getStatus() == BatchJob.COMPLETED_ERROR) {
									isStopWithError = true;
								}
								if (job.getApplStatus() == BatchJob.WITH_WARNING) {
									isStopWithWarning = true;
								}
							}
							Thread.sleep(1000); // attendo un secondo prima di rieseguire la retrieve (sennò spamma...)
						} else {
							isStop = true;
						}
						// verifico il timeout
						if (!isStop) {
							long stop = System.currentTimeMillis();
							long delta = (stop - start) / 1000;
							if (delta > timeout) {
							}
						}
					}
					SimpleDateFormat formatSQL = new SimpleDateFormat("yyyyMMdd");
					// Se finito e non con errore devo compilare FLAG_RISE_UTE_5 di THIP.DDT_VEN a
					// 'Y'
					if (isStop && !isStopWithError) {
						String where = " " + DdtTesTM.ID_AZIENDA + " = '" + docVen.getIdAzienda() + "' " + " AND "
								+ DdtTesTM.ID_ANNO_DDT + " = '" + docVen.getAnnoBolla() + "' " + " AND "
								+ DdtTesTM.ID_NUMERO_DDT + "  = '" + docVen.getNumeroBolla() + "' " + " AND "
								+ DdtTesTM.DATA_DDT + " = '" + formatSQL.format(docVen.getDataBolla()) + "' ";
						List<DdtTes> lstDdt = DdtTes.retrieveList(DdtTes.class, where, "", false);
						if (lstDdt.size() > 0) {
							DdtTes ddtTes = lstDdt.get(0);
							ddtTes.setFlagRisUte5('Y');
							if (ddtTes.save() >= 0) {
								writeLog("");
								writeLog("STAMPA TERMINATA CORRETTAMENTE, CERTIFICATI EMESSI");
								ConnectionManager.commit();
							} else {
								ConnectionManager.rollback();
							}

						}
					}
				}
			} catch (Exception e) {
				isOk = false;
				writeLog(e.getMessage() != null ? e.getMessage() : "");
				e.printStackTrace();
			}
		}
		return isOk;
	}

	protected List<String> getListaDDTOdierni(boolean consideroDaValutare) {
		String msgDaValutare = " e non processo i da valutare";
		if (consideroDaValutare)
			msgDaValutare = "e processo i da valuare per verificare se sono stati aggiornati i certificati e ora sono presenti tutti. ";
		writeLog(
				"-----------------------------------------------------------------------------------------------------------");
		writeLog(
				"RICERCO I DDT ODIERNI CHE NON HANNO I CERTIFICATI EMESSI,FLAG_RIS_UTE_5 != 'Y' E CHE PREVEDANO I CERTIFICATI ("
						+ msgDaValutare + ") ");
		writeLog("");
		List<String> lst = new ArrayList<String>();
		SimpleDateFormat formatSQL = new SimpleDateFormat("yyyyMMdd");
		Date data = this.getDataDDT() != null ? this.getDataDDT() : TimeUtils.getCurrentDate();
		String dataPartenza = ParametroPsn.getValoreParametroPsn("YEmissioneAutomCert", "DataDiPartenza"); // 71127
		String daValutare = " C.MANUALE = 'N' ";
		if (consideroDaValutare)
			daValutare = "( C.MANUALE = 'N' OR (C.MANUALE = 'V' AND DATA_BOLLA > DATEADD(day, -15, GETDATE() ) ))";
		String where = " SELECT "
				+ "T."+YDocumentoVenditaTM.ID_AZIENDA+" ,"
				+ "T."+YDocumentoVenditaTM.ID_ANNO_DOC+" ,"
				+ "T."+YDocumentoVenditaTM.ID_NUMERO_DOC+"  FROM "+YDocumentoVenditaTM.TABLE_NAME+" T "
				+ "LEFT OUTER JOIN SOFTRE.Y_NOTE_CERT_DDT_V01 C  "
				+ "ON T."+YDocumentoVenditaTM.ID_AZIENDA+" = C.ID_AZIENDA  "
				+ "AND T."+YDocumentoVenditaTM.NUMERO_BOLLA+"  = C.NUMERO_BOLLA  "
				+ "AND T."+YDocumentoVenditaTM.ANNO_BOLLA+"  = C.ANNO_BOLLA  "
				+ "LEFT OUTER JOIN "+DdtTesTM.TABLE_NAME+" D  "
				+ "ON D."+DdtTesTM.ID_AZIENDA+" = C.ID_AZIENDA  "
				+ "AND D."+DdtTesTM.ID_NUMERO_DDT+" = C.NUMERO_BOLLA  "
				+ "AND D."+DdtTesTM.ID_ANNO_DDT+" = C.ANNO_BOLLA "
				+ "WHERE D."+DdtTesTM.FLAG_RIS_UTE_5+" != 'Y' "
				+ "AND C.PRES_CERT = 'Y' "
				+ "AND " + daValutare + " "
				+ "AND D."+DdtTesTM.ID_AZIENDA+" = '"+Azienda.getAziendaCorrente()+"' " //aggiunta 16/05/2023
				+ "AND T."+YDocumentoVenditaTM.DATA_BOLLA+" <= '"+formatSQL.format(data)+"' "
				+ "AND T."+YDocumentoVenditaTM.DATA_BOLLA+" > '"+dataPartenza.replace("-", "")+"'";//la generazione automatica la facciamo partire dal 1 giugno 23
		ResultSet rs = null;
		CachedStatement cs = null;
		try {
			cs = new CachedStatement(where);
			writeLog("Statement per la ricerca :\n");
			writeLog(where);
			rs = cs.executeQuery();
			while (rs.next()) {
				String c = KeyHelper.buildObjectKey(new String[] { rs.getString(1), rs.getString(2), rs.getString(3) });
				lst.add(c);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		writeLog("");
		writeLog("TROVATI : " + lst.size() + " DOCUMENTI");
		writeLog(
				"-----------------------------------------------------------------------------------------------------------");
		return lst;
	}

	protected void writeLog(String text) {
		System.out.println(text);
		getOutput().println(text);
	}

	@Override
	protected String getClassAdCollectionName() {
		return "YEmissAutomCert";
	}

}