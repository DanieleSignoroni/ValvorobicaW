package com.thera.thermfw.batch;

import java.util.*;

import com.thera.thermfw.base.*;
import com.thera.thermfw.persist.*;
import com.thera.thermfw.ssd.*;

import it.thera.thip.vendite.documentoVE.DocumentoVendita;
import it.valvorobica.thip.vendite.documentoVE.YReportDdtBollaBatch;

import com.thera.thermfw.common.ErrorMessage;
import java.net.SocketException;
import java.sql.SQLException;

/**
 * Classe erede di PrintRunnable che imposta le condizioni per eseguire la stampa.
 * Da questa classe devono ereditare le classi che si occupano delle stampe
 * in batch che necessitano di fare particolari operazioni sui dati delle tabelle.
 * Con questa classe VENGONO GENERATI AvailableReport.
 * <br></br><b>Copyright (C) : Thera s.p.a.</b>
 * @author Laura Pezzolini 05/07/2002
 */
/*
 * Revision:
 * Number     Date        Owner      Description
 * 00290      05/07/2002  LP         Prima versione.
 * 00409      30/10/2002  LP         Modificato per nuova gestione di parametri ed enum.
 * 02158      30/08/2004  IT         Modifica per SSD
 * 02749      30/10/2004  MM         Modifica per uso di più tool di stampa
 * 03313      25/02/2005  IT         Spostata attivazione SSD in metodo public
 * 03543      06/04/2005  DF         Revisione supporto a diversi tool di stampa
 *                                   Ripulito il codice dalle stratificazioni di modifiche accumulatesi nel tempo
 * 05326      14/04/2006  DF         Implementata la gestione della lingua legata al singolo AvailableReport in modo da poter generare da un solo lavoro batch più stampe in lingua diversa.
 * 07070      05/04/2007  DF         Scrittura progressiva log dei lavori batch. Implementi al meccanismo di arresto dei lavori batch.
 * 16266      18/04/2012  ES         Generare un messaggio chiaro quando il server di stampa è spento.
 * 20096      06/05/2014  TF         Stampare l’eccezione ‘catchata’
 * 39625      01/09/2023  YA         Cambio stato stampe e/o avanzamento batch
 */

public class ElaboratePrintRunnable extends PrintRunnable {

	/**
	 * File di risorse.
	 */
	protected static final String BATCH_RES = "com.thera.thermfw.batch.resources.Batch";

	/**
	 * Metodo che esegue la stampa.
	 * @return boolean true se il lavoro batch è terminato correttamente.
	 */
	protected boolean run() {
		try {
			//...Chiamo il metodo implementato nella classe erede che crea i report
			boolean ok = createReport();

			//...Se i report sono stati generati correttamente e devo eseguire la stampa
			if (ok) {
				//...Leggo la lista di report generati e stampo ogni report
				Iterator avReportList = getBatchJob().getAvailableReportColl().iterator();
				if (getExecutePrint()) {
					changeBatchProgressStatus(BatchJob.PRINT_PDF);	//Fix 39625
					output.println(ResourceLoader.getString(BATCH_RES, "OutputReportGenerated"));
					output.println(ResourceLoader.getString(BATCH_RES, "OutputReportNr", new Object[] {"" + getBatchJob().getAvailableReportColl().size()}));
				}

				while (avReportList.hasNext()) {
					AvailableReport avRep = (AvailableReport) avReportList.next();
					//AGSOF3 INI
					ReportModel reportModel = avRep.getReportModel();
					if(reportModel != null && reportModel.getNotes() != null && reportModel.getNotes().contains("SOFTRE")) {
						//adesso controllo nelle viste di PUNSTE, se non esiste il record con questo
						//batch job id allora non genero il documetno digitale
						String view = reportModel.getNotes();
						String nrBolla = "";//71240, gestione stampa massiva bolle
						YReportDdtBollaBatch rep  =((it.valvorobica.thip.vendite.documentoVE.YReportDdtBollaBatch)this);
						if(rep != null) {
							DocumentoVendita docven = rep.getDocumentoVendita();
							if(docven != null)
								nrBolla = docven.getNumeroBolla();
						}
						String select = "SELECT BATCH_JOB_ID "
								+ "FROM " + view + " "
								+ "WHERE BATCH_JOB_ID = '"+this.getBatchJob().getBatchJobId()+"' ";
//								+ "AND REPORT_NR = '"+avRep.getReportNr()+"' "
//								+ "AND ID_NUMERO_DOC = '"+nrBolla+"' ";
						try {
							CachedStatement cs = new CachedStatement(select);
							java.sql.ResultSet rs = cs.executeQuery();
							if(!rs.next())
								continue;
						}catch(java.sql.SQLException e) {
							e.printStackTrace();
						}
					}
					//AGSOF3 FIN
					setPrintToolInterface(avRep.getReportModel().getPrintingToolInterface());

					//05326 - DF
					setCurrentLanguage(avRep.getLanguage());

					//...Riempio i vettori per il file di dati
					printToolInterface.fillUserParams(getParameters());
					printToolInterface.fillEnum(getEnums());
					//05326 - DF
					printToolInterface.setPropertiesFileLanguage(getCurrentLanguage());

					//05326 - DF
					resetCurrentLanguage();

					//...Eseguo la stampa solo se non sono in esecuzione immediata
					if (getExecutePrint() && !getImmediateExecution()) {
						printToolInterface.setShowParamsForPrint(false);
						//...a questo punto stampo il report (con o senza anteprima)
						output.println(ResourceLoader.getString(BATCH_RES, "OutputPrintReport", new Object[] {"" + avRep.getReportNr()}));
						printToolInterface.printAvailableReport(avRep, printPreviewEnabled);
						//...imposto il booleano di stampa effettuata a true e salvo l'AvailableReport
						avRep.setPrinted(true);
						avRep.save();
						ConnectionManager.commit();
					}
					
					if (!generateSSD(avRep))
						return false;
				}
				changeBatchProgressStatus(BatchJob.END);	//Fix 39625
				return true;
			}

			//...Se ci sono stati problemi nella creazione dei report restituisco false.
			else if (!ok) {
				output.println(ResourceLoader.getString(BATCH_RES, "OutputReportError"));
				return ok;
			}
			else
				return ok;
		}
		// 07070 - DF
		catch (BatchStoppedException e) {
			throw e;
		}
		//Mod. 16266 inizio
		catch (SocketException sockEx) {
			output.println("");
			output.println(new ErrorMessage("BATCH036").getText());
			output.println("");
			//Fix 39625 - inizio
			changePrintProgress(BatchJob.STAMPA_IN_ERRORE, DGT);
			changePrintProgress(BatchJob.STAMPA_IN_ERRORE, SSD);
			//Fix 39625 - fine
			return false;
		}
		//fine mod. 16266
		// 07070 - Fine
		catch (Exception e) {
			output.println(ResourceLoader.getString(BATCH_RES, "OutputError"));
			output.println(e.getLocalizedMessage()); //Mod. 16266
			//Mod. 16266//e.printStackTrace(Trace.excStream);
			e.printStackTrace(Trace.excStream);//Fix 19699
			return false;
		}
	}

	/**
	 * Metodo che abilita la generazione SSD.
	 * @return boolean true se il lavoro batch è terminato correttamente.
	 */
	public boolean generateSSD(AvailableReport avRep) throws Exception {
		if (isSSDEnabled()) {
			String whereSSD = SSDDescriptorTM.ENTITY_ID + " = '" + avRep.getReportModel().getEntityId() + "' AND " +
					SSDDescriptorTM.TASK_ID + " = '" + avRep.getReportModel().getTaskId() + "'";
			Vector ssdDescrs = SSDDescriptor.retrieveList(whereSSD, "", false);
			SSDDescriptor ssdDescriptor = null;
			if (ssdDescrs.size() == 1)
				ssdDescriptor = (SSDDescriptor) ssdDescrs.elementAt(0);
			SSDDocumentCarrier ssdDocCarrier = (SSDDocumentCarrier) Factory.createObject(ssdDescriptor.getClassName());
			if (!ssdDocCarrier.run(avRep, printToolInterface, ssdDescriptor)) {
				output.println(ResourceLoader.getString(BATCH_RES, "OutputError"));
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Metodo che esegue la stampa.
	 * Questo metodo viene utilizzato solo in web poichè invece della stampa deve
	 * essere creato un file di tipo pdf.
	 * @return int: codice del lavoro batch corrente.
	 */
	public int interactiveRunForWeb() {
		setImmediateExecution(true);
		if (run())
			return getBatchJob().getBatchJobId();
		else
			return -1;
	}

	/**
	 * METODO DA RIDEFINIRE NELLA CLASSE EREDE.
	 * Restituisce il nome del classAd.
	 * @retun String nome del classAd.
	 */
	protected String getClassAdCollectionName() {
		return null;
	}

	/**
	 * METODO DA RIDEFINIRE NELLA CLASSE EREDE.
	 * @retun boolean se i report sono stati generati correttamente.
	 */
	public boolean createReport() {
		return true;
	}

	/**
	 * Metodo che deve essere implementato se si desidera ricaricare
	 * i parametri del job che è stato eseguito nel caso essi siano stati impostati
	 * dopo che il lavoro è stato sottomesso (e quindi che non sono ancora stati salvati).
	 */
	public void updateJobParameters() {
		getBatchJob().setParameter(getDataCollector().objectToStream(this));
	}
}

