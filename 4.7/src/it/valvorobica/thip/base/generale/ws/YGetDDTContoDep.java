package it.valvorobica.thip.base.generale.ws;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.gson.Gson;
import com.thera.thermfw.base.TimeUtils;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.common.ErrorMessage;
import com.thera.thermfw.persist.CachedStatement;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;

import it.thera.thip.base.articolo.Articolo;
import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.base.comuniVenAcq.GestoreDocumenti;
import it.thera.thip.base.documenti.StatoAttivita;
import it.thera.thip.base.documenti.StatoAvanzamento;
import it.thera.thip.base.generale.UnitaMisura;
import it.thera.thip.base.partner.Valuta;
import it.thera.thip.magazzino.generalemag.Lotto;
import it.thera.thip.magazzino.generalemag.LottoTM;
import it.thera.thip.magazzino.saldi.DatiSaldoTTM;
import it.thera.thip.magazzino.saldi.SaldoMagLottoCliente;
import it.thera.thip.magazzino.saldi.SaldoMagLottoClienteTM;
import it.thera.thip.vendite.documentoVE.DocumentoVenRigaLottoPrm;
import it.thera.thip.vendite.documentoVE.DocumentoVenRigaPrm;
import it.thera.thip.vendite.documentoVE.DocumentoVendita;
import it.thera.thip.vendite.documentoVE.DocumentoVenditaTM;
import it.thera.thip.vendite.generaleVE.CondizioniDiVendita;
import it.thera.thip.vendite.generaleVE.RicercaCondizioniDiVendita;
import it.thera.thip.ws.GenRequestJSON;
import it.valvorobica.thip.base.generale.ws.dati.YContoDepDDTDettaglio;
import it.valvorobica.thip.base.generale.ws.dati.YContoDepDDTTestata;
import it.valvorobica.thip.base.generale.ws.dati.YContoDepDDTTestate;
import it.valvorobica.thip.base.generale.ws.utils.YOggettoJSONReqParams;
import it.valvorobica.thip.base.generale.ws.utils.YUtilsPortal;
import it.valvorobica.thip.vendite.documentoVE.YDocumentoVendita;

/**
 * <h1>Softre Solutions</h1>
 * 
 * @author Andrea Gatta 01/12/2022
 * <br></br>
 * <b>70877	23/01/2023	DSSOF3</b>	<br>Commentata la riga dove setto la STRINGA_RIS_UTE_1 al valore del json di entrata.
 *									<br>Questo perche' l'articolo cliente che ci passano non equivale a quello codificato in panthera.<br>
 * <b>70880	25/01/2023	DSSOF3</b>  <br>CondizioniDiVendita sulle righe documento.<br>
 * <b>70958	20/02/2023	DSSOF3</b>	<br>UM presa dall'articolo pth e non dal json.<br>
 * <b>70979	01/03/2023	DSSOF3</b>	<br>Introdotta la gestione del saldo cliente e non piu il saldo base.<br>
 * <b>71003	17/03/2023	DSSOF3</b>	<br>Nella creazione doc ven non setto piu numRifInt e dataRifInt ma numOrdCLI e dataOrdCLI.
 * 									<br>Settate alle righe anche le provvigioni.<br>
 * <b>71037	03/04/2023	DSSOF3</b>	<br>Correzione - con la fix precedente e' stato cambiato il set dei riferimenti, ma non e' stato aggiornato
 * 										il modo in cui viene updatato il parametro contenente l'ultima data ok di processo.<br>
 * 										Visto che prima era valorizzata con la data rif int, ora e' stata cambiata con la data ord int. (cioe' quella dell'ordine cliente.)<br>
 * <b>71066	03/05/2023	AGSOF3</b>	<br>Modifica al reperimento in funzione della data, adesso vengono processate tutte le date a partire da oggi - 15gg fino ad oggi, scartando 
 * 										però tutti i documenti che esistono già, facendo il controllo con la chiave id_ddt<br>
 * <b>71067	04/05/2023	DSSOF3</b>	<br>Nella ricerca delle condizioni vendita {@link #recuperaCondizioniDiVendita(DocumentoVendita, DocumentoVenRigaPrm, String)}<br>
 * 										assumo che la quantita' magazzino passata alla classe che recupera le condizioni sia uguale a {@link BigDecimal@#ONE} .<br>
 * <b>71119	06/06/2023	DSSOF3</b>	<br>Nella lettura, creo un file di log di supporto per capire quali documenti vanno in errore.<br>
 * <b>71119	07/06/2023	AGSOF3</b>	<br>Non leggo.<br>
 * <b>71148	21/06/2023	DSSOF3</b>	<br>Convalido il doc anche se ho creato il lotto generico<br>
 * <b>71156	28/06/2023	DSSOF3</b>	<br>Sistemare la generazione dei lotti quando creo la riga documento vendita.<br>
 * <b>71225	19/09/2023	DSSOF3</b>	<br>Aggiunto metodo {@link #lanciaCovalidaNonRiusciti(Map, PrintWriter)} per poter ri-lanciare la convalida di tutti quei documenti
 * 										che non sono stati convalidati al primo giro.</br>
 */

public class YGetDDTContoDep extends GenRequestJSON{

	protected final String endpointGetDDT = "http://valvorobica.contodeposito.com/API/GetDDT?";

	protected final String methodRequest = "POST";

	protected boolean isLottoGenCreato = false;

	@SuppressWarnings("rawtypes")
	public Map risultatoWs = new HashMap();

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Map execute(Map m) {
		//71119	Inizio
		String fileName = "log_"+TimeUtils.getCurrentDate().toString()+".txt";
		File log = new File("D:\\Panthera\\Panth01\\LogGetDDT\\"+fileName);
		try {
			if (log.createNewFile()) {
				System.out.println("Creo log nella YGetDDTContoDep");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//71119	FIne
		try {
			//71066 ini remmata
			//			String ultimaDataOkStringa = YUtilsPortal.getParametroPersonalizzazione("YValvorobicaContoDeposito", "UltimaDataProcessataDDT").getValore();
			//			java.util.Date ultimaDataOk = new SimpleDateFormat("yyyy-MM-dd").parse(ultimaDataOkStringa);
			//			ultimaDataOk = TimeUtils.addDays(ultimaDataOk, 1);
			//71066 fine remmata
			FileWriter fileWriter = new FileWriter(log.getAbsolutePath()); //71119
			PrintWriter printWriter = new PrintWriter(fileWriter); //71119
			printWriter.println("INIZIO RICEZIONE DATI CONTO DEPOSITO "+TimeUtils.getCurrentTimestamp().toString() + "------------------------->"); //71119
			java.util.Date oggi = TimeUtils.getCurrentDateTime();
			java.util.Date ultimaDataOk = TimeUtils.addDays(oggi, -15);//71066 la data da cui partire a processare è oggi - 15gg
			Map<String,YDocumentoVendita> docsNonConvalidati= new HashMap<String, YDocumentoVendita>(); //71225
			while(ultimaDataOk.before(oggi)) {//processo tutti i giorni dall'ultima data ok fino ad oggi compreso
				String dataDaProcessare = YUtilsPortal.formattaDataPerWs(ultimaDataOk); //formatto in yyyyMMdd
				printWriter.println("-------------------------------------------------------------"); //71119
				printWriter.println("Chiamata all'endpoint con data: "+dataDaProcessare); //71119
				YOggettoJSONReqParams oggettoReq = new YOggettoJSONReqParams("VALVODEP","VALVOPWD",dataDaProcessare,"");
				String reqParams = oggettoReq.ottieniJSONParams();
				Map<String, String> res = YUtilsPortal.chiamaWsEsterno(endpointGetDDT, methodRequest, reqParams, true); //qui chiamo il ws esterno
				String status = res.get("status");
				printWriter.println("Stato chiamata: "+status); //71119
				if(status != null && status.equals("200")) {//status 200 = ok
					String json = res.get("response"); 
					json = "{ \"Testate\": " + json + "}";//normalizzo per avere un oggetto con una lista di oggetti					
					Gson gson = new Gson();
					YContoDepDDTTestate testate = gson.fromJson(json, YContoDepDDTTestate.class);
					Iterator<YContoDepDDTTestata> iterTestate = testate.Testate.iterator();
					ArrayList<String> chiaviDocSalvati = new ArrayList<String>();
					printWriter.println("In questa data ho trovato: "+testate.Testate.size()+ " testate, le scorro e provo a creare i documenti"); //71119
					while(iterTestate.hasNext()) {
						isLottoGenCreato = false;
						YContoDepDDTTestata testata = iterTestate.next();
						if(testata.Dettagli.size() > 0) {//se non ho almeno una riga non faccio niente
							printWriter.println("---------"); //71119
							DocumentoVendita docven = creaDocVenDaTestataJson(testata);
							if(docven != null) { //71119
								printWriter.println("Creato il documento di vendita: "+docven.getKey());
								if(docven != null) {
									//Se ho creato il lotto generico evito la convalida dato che andrebbe in errore per i saldi
									//if(!isLottoGenCreato) { REMMATO IN DATA 21/06/2023 71148
									printWriter.println("Provo a convalidare il documento dopo che ho sistemato i lotti"); //71119/
									GestoreDocumenti ges = GestoreDocumenti.getInstance();
									List errori = ges.convalida(docven);
									//71225 Inizio
									if(docven.retrieve()) { //per essere sicuro di avere l'oggetto aggiornato
										if(docven.getCollegatoAMagazzino() != StatoAttivita.ESEGUITO) {
											docsNonConvalidati.put(docven.getKey(), (YDocumentoVendita) docven);
										}
									}
									//71225 Fine
									ArrayList<String> erroriWS = (ArrayList<String>) risultatoWs.get("errori") != null ? (ArrayList<String>) risultatoWs.get("errori") : new ArrayList<String>();
									for(int i = 0 ; i < errori.size() ; i++) {
										ErrorMessage em = (ErrorMessage) errori.get(i);
										erroriWS.add(em.getText());
									}
									printWriter.println("Documento "+docven.getNumeroDocumento()+" convalidato con i seugenti errori \n"+erroriWS.toString()); //71119
									risultatoWs.put("errori", erroriWS);
									//} REMMATO IN DATA 21/06/2023 71148
									String docSalvato = docven.getKey().replace(KeyHelper.KEY_SEPARATOR, "/");
									docSalvato += " - Testata: " + docven.getNumeroOrdineIntestatario();
									docSalvato += " - Data: " + docven.getDataOrdineIntestatario();
									chiaviDocSalvati.add(docSalvato);
								}
							} //71119
							printWriter.println("---------"); //71119
						}
					}
					ArrayList<String> docSalvati = (ArrayList<String>) risultatoWs.get("DocumentiSalvati");
					if(docSalvati == null)
						docSalvati = new ArrayList<String>();
					docSalvati.addAll(chiaviDocSalvati);
					risultatoWs.put("DocumentiSalvati", docSalvati);					
				}
				risultatoWs.clear(); //pulisco ad ogni giorno 71119
				//incremento la data di un giorno
				ultimaDataOk = TimeUtils.addDays(ultimaDataOk, 1);
				printWriter.println("Termine lettura dell'endpoint con data: "+dataDaProcessare); //71119
				printWriter.println("-------------------------------------------------------------"); //71119
			}
			//Ci sono dei documenti da ri-convalidare
			if(docsNonConvalidati.size() > 0) {
				lanciaCovalidaNonRiusciti(docsNonConvalidati, printWriter);
			}
			printWriter.println("<----------------------------- TERMINATA RICEZIONE DATI CONTO DEPOSITO "+TimeUtils.getCurrentTimestamp().toString()); //71119
			printWriter.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return risultatoWs;
	}

	/**
	 * <h1>Lancia convalida per i documenti non convalidati</h1>
	 * <br>
	 * Daniele Signoroni 19/09/2023
	 * <br></br>
	 * <b>71225	DSSOF3	19/09/2023</b>
	 * <p>
	 * Capitava spesso che la procedura non convalidasse alcuni documenti che a mano venivano convalidati senza cambiare dati.<br>
	 * E' stato deciso di implementare una ri-convalida per quelli non riusciti.
	 * </p>
	 * @param docsNonConvalidati
	 * @param printWriter
	 */
	@SuppressWarnings("rawtypes")
	protected void lanciaCovalidaNonRiusciti(Map<String, YDocumentoVendita> docsNonConvalidati,PrintWriter printWriter) {
		printWriter.println("Sono presenti documenti non convalidati al primo giro, provo a rilanciare la convalida");
		for (Map.Entry<String, YDocumentoVendita> entry : docsNonConvalidati.entrySet()) {
			String key = entry.getKey();
			YDocumentoVendita docVen = entry.getValue();
			GestoreDocumenti ges;
			try {
				ges = GestoreDocumenti.getInstance();
				List errori = ges.convalida(docVen);
				ArrayList<String> erroriWS = new ArrayList<String>();
				for(int i = 0 ; i < errori.size() ; i++) {
					ErrorMessage em = (ErrorMessage) errori.get(i);
					erroriWS.add(em.getText());
				}
				printWriter.println("Documento "+key+" convalidato con i seugenti errori \n"+erroriWS.toString()); 
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		printWriter.println("Ho terminato di convalidare i documenti mancanti");
	}

	@SuppressWarnings("unchecked")
	protected DocumentoVendita creaDocVenDaTestataJson(YContoDepDDTTestata testata) {
		DocumentoVendita docVen = null;
		try {
			boolean esisteGiaDoc = esisteGiaDocVen(testata.getID_DDTTestata());
			if(!esisteGiaDoc) { // se non esiste lo creo
				docVen = (DocumentoVendita) Factory.createObject(DocumentoVendita.class);
				docVen.setIdAzienda(Azienda.getAziendaCorrente());
				docVen.setIdCau("VDP");
				String idCliente = ((YContoDepDDTDettaglio)testata.Dettagli.get(0)).getCOD_Cliente();
				docVen.setIdCliente(idCliente != null ? idCliente.replace("C", "0"): null);
				//docVen.setNumeroRifIntestatario(testata.getID_DDTTestata()); remmato 71003
				java.sql.Date data = YUtilsPortal.getDataDaStringa(testata.getData(), "yyyyMMdd");
				docVen.setNumeroOrdineIntestatario(testata.getID_DDTTestata()); //DSSOF3	71003	Setto numero ord cli
				docVen.setDataOrdineIntestatario(data); //DSSOF3 	71003	Setto data ord cli
				docVen.getNumeratoreHandler().setIdSerie("IT");
				//docVen.setDataRifIntestatario(data); remmato 71003
				docVen.completaBO();
				docVen.setDataConsegnaRichiesta(data);//agsof3 setto data doc = data presa portale
				docVen.setDataConsegnaConfermata(data); //agsof3 setto data doc = data presa portale
				if(docVen.save() >= 0) { //salvo ma non committo così ho la chiave e posso aggiungere le righe
					Iterator<YContoDepDDTDettaglio> iterRighe = testata.Dettagli.iterator();
					while(iterRighe.hasNext()) {
						YContoDepDDTDettaglio dettaglio = iterRighe.next();
						int rc = creaDocVenRigDaDettaglioJson(docVen,dettaglio);
						if(rc < 0) {
							ArrayList<String> errori = (ArrayList<String>) risultatoWs.get("errori");
							if(errori == null)
								errori = new ArrayList<String>();						
							String msg = rc == -1 ? " articolo non esistente " : "";
							if(rc == -1) {
								errori.add("Problema nel salvataggio riga art: " + dettaglio.getCOD_Articolo() + " ("+msg+") per la testata: " + testata.getID_DDTTestata());
							}else if(rc == -2) {
								msg = " lotto vuoto ";
								errori.add("Problema nel salvataggio riga art : " + dettaglio.getCOD_Articolo() + " ("+msg+") per la testata : " + testata.getID_DDTTestata());
							}
							risultatoWs.put("errori", errori);
						}
					}
					ConnectionManager.commit();
					//YUtilsPortal.updateValoreParametro(docVen.getDataRifIntestatario().toString(), "YValvorobicaContoDeposito", "UltimaDataProcessataDDT"); 71037
					YUtilsPortal.updateValoreParametro(docVen.getDataOrdineIntestatario().toString(), "YValvorobicaContoDeposito", "UltimaDataProcessataDDT");
				}
			}
		}catch(Exception e) {
			risultatoWs.put("errori", e.getMessage());
			e.printStackTrace();
			return null;
		}
		return docVen;
	}

	/**
	 * 
	 * @param id_DDTTestata
	 * @return true se esiste già un documento con id_DDTTestata nel nro ordine intestatario
	 */
	protected boolean esisteGiaDocVen(String id_DDTTestata) {
		boolean esiste = false;
		ResultSet rs = null;
		CachedStatement cs = null;
		try {
			String select = "SELECT 1 FROM " + DocumentoVenditaTM.TABLE_NAME + " " + 
					"WHERE " + DocumentoVenditaTM.ID_AZIENDA + " = '"+Azienda.getAziendaCorrente()+"' " + 
					"AND " + DocumentoVenditaTM.NUM_ORD_CLIENTE + " = '"+id_DDTTestata+"' ";
			//aggiunto dssof3
			select = select += " AND "+DocumentoVenditaTM.R_CAU_DOC_VEN+" = 'VDP' ";
			//controllo su vdp, potrebbero esserci altri doc con causale diversa
			//che usano lo stesso rif cliente
			cs = new CachedStatement(select);
			rs = cs.executeQuery();
			if(rs.next())
				esiste = true;
		}catch(SQLException e) {
			e.printStackTrace(Trace.excStream);
			e.printStackTrace();
		}
		return esiste;
	}

	@SuppressWarnings("unchecked")
	protected int creaDocVenRigDaDettaglioJson(DocumentoVendita docVen, YContoDepDDTDettaglio dettaglio) throws SQLException {
		int rc = -1;
		DocumentoVenRigaPrm  docVenRig = (DocumentoVenRigaPrm ) Factory.createObject(DocumentoVenRigaPrm .class);
		docVenRig.setIdAzienda(Azienda.getAziendaCorrente());
		docVenRig.setTestata(docVen);
		docVenRig.setIdCauRig(docVen.getCausale().getIdCausaleRigaDocumVen()); 
		docVenRig.setStatoAvanzamento(StatoAvanzamento.DEFINITIVO);
		docVenRig.completaBO();
		docVenRig.setIdArticolo(dettaglio.getCOD_Articolo());
		Articolo articolo = getArticolo(dettaglio.getCOD_Articolo());
		if(articolo != null) {
			//docVenRig.setAlfanumRiservatoUtente1(dettaglio.getCOD_ArticoloCliente()); DSSOF3 remmato perche' l'articolo che ci arriva non e' quello codificato in panthera.
			docVenRig.getQtaAttesaEvasione().setQuantitaInUMRif(new BigDecimal(dettaglio.getQuantita()));
			//String UMContoDep = dettaglio.getUnitaMisura();
			//String UMPth = YUtilsPortal.trascodificaUMContoDep(UMContoDep);
			//docVenRig.setIdUMRif(UMPth);
			//docVenRig.setIdUMPrm(UMPth);
			docVenRig.setIdUMRif(articolo.getIdUMRiferimento());
			docVenRig.setIdUMPrm(articolo.getUMDefaultVendita().getIdUnitaMisura());
			docVenRig.ricalcoloQuantitaRiga(); //ricalcolare le qta
			if(docVenRig.getArticolo() != null) {
				CondizioniDiVendita cdv = recuperaCondizioniDiVendita(docVen, docVenRig,/*UMPth*/articolo.getIdUMRiferimento());
				if(cdv != null) {
					docVenRig.setScontoArticolo1(cdv.getScontoArticolo1());
					docVenRig.setScontoArticolo2(cdv.getScontoArticolo2());
					docVenRig.setMaggiorazione(cdv.getMaggiorazione());
					docVenRig.setPrezzo(cdv.getPrezzo());
					docVenRig.setPrezzoExtra(cdv.getPrezzoExtra());
					docVenRig.setSconto(cdv.getSconto());
					docVenRig.setRiferimentoUMPrezzo(cdv.getUMPrezzo());
					docVenRig.setNumeroImballo(cdv.getNumeroImballo());
					docVenRig.setProvenienzaPrezzo(cdv.getTipoTestata());
					docVenRig.setProvvigione1Agente(cdv.getProvvigioneAgente1()); //71003
					docVenRig.setProvvigione1Subagente(cdv.getProvvigioneSubagente1()); //71003
					docVenRig.setProvvigione2Agente(cdv.getProvvigioneAgente2()); //71003
					docVenRig.setProvvigione2Subagente(cdv.getProvvigioneSubagente2()); //71003
				}
				docVenRig.setIdAssogIVA(docVenRig.getArticolo().getIdAssoggettamentoIVA());
				sistemaLotti(docVenRig.getIdArticolo(), docVenRig);
				docVen.getRighe().add(docVenRig);
				rc = docVenRig.save();
				if(rc >= 0) {
					Iterator<DocumentoVenRigaLottoPrm> iterRigheLotto = docVenRig.getRigheLotto().iterator();
					while(iterRigheLotto.hasNext()) {
						DocumentoVenRigaLottoPrm rigaLotto = iterRigheLotto.next();
						if(rigaLotto.getIdLotto().equals(Lotto.LOTTO_DUMMY))
							rigaLotto.delete();
					}
				}
			}
		}
		return rc;
	}	

	/**
	 * <b>71156	28/06/2023	DSSOF3</b>	<p>Ridisegnato, ordino per QTA_GIAC_PRM DESC in modo da prendere prima quelli con piu giacenza,<br>
	 * 									   In seguito sistemata la logica.</p>
	 * @param idArticolo
	 * @param docVenRig
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void sistemaLotti(String idArticolo,DocumentoVenRigaPrm docVenRig) {
		String where = " "+SaldoMagLottoClienteTM.ID_AZIENDA+" = '" + Azienda.getAziendaCorrente() +"' "
				+ " AND "+SaldoMagLottoClienteTM.ID_ARTICOLO+" = '" + docVenRig.getIdArticolo() + "' "
				+ " AND "+SaldoMagLottoClienteTM.ID_MAGAZZINO+" = '"+docVenRig.getIdMagazzino()+"' "
				+ " AND "+SaldoMagLottoClienteTM.ID_CONFIG+" = '" + (docVenRig.getIdConfigurazione() != null ? docVenRig.getIdConfigurazione() : 0) + "'"
				+ " AND "+SaldoMagLottoClienteTM.ID_CLIENTE+" = '"+((YDocumentoVendita)docVenRig.getTestata()).getIdCliente()+"' "
				+ " AND "+SaldoMagLottoClienteTM.ID_LOTTO+" != 'LOTTO_GEN' "
				+ " AND "+SaldoMagLottoClienteTM.ID_LOTTO+" <> '-' ";
		String orderBy = " "+DatiSaldoTTM.QTA_GIAC_PRM+" DESC ";
		BigDecimal quantitaRiga = docVenRig.getQtaAttesaEvasione().getQuantitaInUMRif();
		try {
			Vector saldiLotti = SaldoMagLottoCliente.retrieveList(SaldoMagLottoCliente.class,where, orderBy, false);
			if(saldiLotti.size() > 0) {
				Iterator iterLotti = saldiLotti.iterator();
				BigDecimal quantitaScalata = null;
				BigDecimal qtaDaScalare = quantitaRiga;
				while(iterLotti.hasNext()) {
					SaldoMagLottoCliente saldoLotto = (SaldoMagLottoCliente) iterLotti.next();
					BigDecimal quantitaLotto = saldoLotto.getDatiSaldo().getQtaGiacenzaUMPrim();
					if(qtaDaScalare != null && qtaDaScalare.compareTo(BigDecimal.ZERO) == 0) {
						return;
					}
					if(quantitaLotto.compareTo(BigDecimal.ZERO) == 0
							|| quantitaLotto.compareTo(BigDecimal.ZERO) < 0)
						continue;
					if(quantitaRiga.compareTo(quantitaLotto) > 0) {
						DocumentoVenRigaLottoPrm rigaLotto = (DocumentoVenRigaLottoPrm) Factory.createObject(DocumentoVenRigaLottoPrm.class);
						rigaLotto.setDocumentoBaseRiga(docVenRig);
						rigaLotto.setIdArticolo(docVenRig.getIdArticolo());
						rigaLotto.setIdLotto(saldoLotto.getCodiceLotto());
						rigaLotto.getQtaAttesaEvasione().setQuantitaInUMRif(quantitaLotto);
						rigaLotto.getQtaAttesaEvasione().setQuantitaInUMPrm(quantitaLotto);
						docVenRig.getRigheLotto().add(rigaLotto);
						quantitaScalata = quantitaLotto;
					}else {
						DocumentoVenRigaLottoPrm rigaLotto = (DocumentoVenRigaLottoPrm) Factory.createObject(DocumentoVenRigaLottoPrm.class);
						rigaLotto.setDocumentoBaseRiga(docVenRig);
						rigaLotto.setIdArticolo(docVenRig.getIdArticolo());
						rigaLotto.setIdLotto(saldoLotto.getCodiceLotto());
						rigaLotto.getQtaAttesaEvasione().setQuantitaInUMRif(docVenRig.getQtaAttesaEvasione().getQuantitaInUMRif());
						rigaLotto.getQtaAttesaEvasione().setQuantitaInUMPrm(docVenRig.getQtaAttesaEvasione().getQuantitaInUMRif());
						docVenRig.getRigheLotto().add(rigaLotto);
						quantitaScalata = docVenRig.getQtaAttesaEvasione().getQuantitaInUMRif();
					}
					qtaDaScalare = qtaDaScalare.subtract(quantitaScalata != null ? quantitaScalata : BigDecimal.ZERO);
				}
				if(qtaDaScalare.compareTo(BigDecimal.ZERO) > 0) { //se ho ancora un residuo, butto tutto su lotto GEN
					Lotto lottoGenerico = getLottoGenerico(idArticolo);
					if(lottoGenerico == null) {
						lottoGenerico = creaLotto(qtaDaScalare, idArticolo, "LOTTO_GEN");
						DocumentoVenRigaLottoPrm rigaLotto = (DocumentoVenRigaLottoPrm) Factory.createObject(DocumentoVenRigaLottoPrm.class);
						rigaLotto.setDocumentoBaseRiga(docVenRig);
						rigaLotto.setIdArticolo(docVenRig.getIdArticolo());
						rigaLotto.setLotto(lottoGenerico);
						rigaLotto.getQtaAttesaEvasione().setQuantitaInUMRif(qtaDaScalare);
						rigaLotto.getQtaAttesaEvasione().setQuantitaInUMPrm(qtaDaScalare);
						docVenRig.getRigheLotto().add(rigaLotto);
					}else {
						DocumentoVenRigaLottoPrm rigaLotto = (DocumentoVenRigaLottoPrm) Factory.createObject(DocumentoVenRigaLottoPrm.class);
						rigaLotto.setDocumentoBaseRiga(docVenRig);
						rigaLotto.setIdArticolo(docVenRig.getIdArticolo());
						rigaLotto.setLotto(lottoGenerico);
						rigaLotto.getQtaAttesaEvasione().setQuantitaInUMRif(qtaDaScalare);
						rigaLotto.getQtaAttesaEvasione().setQuantitaInUMPrm(qtaDaScalare);
						docVenRig.getRigheLotto().add(rigaLotto);
					}
					isLottoGenCreato = true;
				}
			}
		}catch (Exception ex) {
			ex.printStackTrace(Trace.excStream);
		}
	}

	@SuppressWarnings("rawtypes")
	private Lotto getLottoGenerico(String idArticolo) {
		Lotto lotto = null;
		String where = LottoTM.ID_AZIENDA + "='" + Azienda.getAziendaCorrente() + "'" + "AND  " + LottoTM.ID_ARTICOLO + "= '" + idArticolo + "'"
				+ " AND "+LottoTM.ID_LOTTO+" = 'LOTTO_GEN' ";
		Vector lotti;
		try {
			lotti = Lotto.retrieveList(where, "", false);
			if(lotti.size() > 0) {
				return (Lotto) lotti.get(0);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lotto;
	}

	//	@SuppressWarnings("unchecked")
	//	private SaldoMagLotto getSaldoMagLotto(Lotto lotto, DocumentoVenRigaPrm docVenRig) {
	//		String where = "ID_AZIENDA = '" + Azienda.getAziendaCorrente() +
	//				"' AND ID_ARTICOLO = '" + docVenRig.getIdArticolo() + "' AND ID_MAGAZZINO = '"+docVenRig.getIdMagazzino()+"' "
	//				+ " AND ID_CONFIG = '" + (docVenRig.getIdConfigurazione() != null ? docVenRig.getIdConfigurazione() : 0) + "' AND ID_LOTTO = '" + lotto.getCodiceLotto() + "' ";
	//		try {
	//			List<SaldoMagLotto> lista = SaldoMagLotto.retrieveList(SaldoMagLotto.class, where, "", false);
	//			if(lista.size() > 0) {
	//				return lista.get(0);
	//			}
	//		} catch (ClassNotFoundException e) {
	//			e.printStackTrace();
	//		} catch (InstantiationException e) {
	//			e.printStackTrace();
	//		} catch (IllegalAccessException e) {
	//			e.printStackTrace();
	//		} catch (SQLException e) {
	//			e.printStackTrace();
	//		}
	//
	//		return null;
	//	}

	//	@SuppressWarnings("unchecked")
	//	private SaldoMagLottoCliente getSaldoPerCliente(Lotto lotto, DocumentoVenRigaPrm docVenRig) {
	//		YDocumentoVendita docVen = (YDocumentoVendita) docVenRig.getTestata();
	//		String where = "ID_AZIENDA = '" + Azienda.getAziendaCorrente() +
	//				"' AND ID_ARTICOLO = '" + docVenRig.getIdArticolo() + "' AND ID_MAGAZZINO = '"+docVenRig.getIdMagazzino()+"' "
	//				+ " AND ID_CONFIG = '" + (docVenRig.getIdConfigurazione() != null ? docVenRig.getIdConfigurazione() : 0) + "'"
	//				+ " AND ID_LOTTO = '" + lotto.getCodiceLotto() + "' "
	//				+ " AND ID_CLIENTE = '"+docVen.getIdCliente()+"'";
	//		String orderBy = " "+DatiSaldoTTM.QTA_GIAC_PRM+" DESC ";
	//		try {
	//			List<SaldoMagLottoCliente> lista = SaldoMagLottoCliente.retrieveList(SaldoMagLottoCliente.class, where, orderBy, false);
	//			if(lista.size() > 0) {
	//				return lista.get(0);
	//			}
	//		} catch (ClassNotFoundException e) {
	//			e.printStackTrace();
	//		} catch (InstantiationException e) {
	//			e.printStackTrace();
	//		} catch (IllegalAccessException e) {
	//			e.printStackTrace();
	//		} catch (SQLException e) {
	//			e.printStackTrace();
	//		}
	//
	//		return null;
	//	}

	@SuppressWarnings("rawtypes")
	public Lotto getLottoWithIdLottoArticolo(String idArticolo, String idLotto) {
		String where = LottoTM.ID_AZIENDA + "='" + Azienda.getAziendaCorrente() + "'" + "AND  " + LottoTM.ID_ARTICOLO + "= '" + idArticolo + "'" + "AND  " + LottoTM.ID_LOTTO + "= '" + idLotto + "'";
		try {
			Vector lotti = Lotto.retrieveList(where, "", false);
			if (!lotti.isEmpty()) {
				return (Lotto) lotti.get(0);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace(Trace.excStream);
		}
		return null;
	}

	protected Lotto creaLotto(BigDecimal qta, String codiceArticolo, String codiceLotto) {
		Lotto lotto = null;
		try {
			lotto = (Lotto) Factory.createObject(Lotto.class);
			lotto.setCodiceArticolo(codiceArticolo);
			lotto.setCodiceLotto(codiceLotto);
			lotto.setCodiceAzienda(Azienda.getAziendaCorrente());
			lotto.setQtaGiacPrm(qta);

			if(lotto.save() >= 0)
				ConnectionManager.commit();
			else
				ConnectionManager.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lotto;
	}

	public CondizioniDiVendita recuperaCondizioniDiVendita(DocumentoVendita docVen, DocumentoVenRigaPrm docVenRig, String uMPth) {
		RicercaCondizioniDiVendita rcdv = new RicercaCondizioniDiVendita();
		CondizioniDiVendita cdv = null;
		String key = KeyHelper.buildObjectKey(new String[] {Azienda.getAziendaCorrente(), uMPth});
		try {
			UnitaMisura um = (UnitaMisura) UnitaMisura.elementWithKey(UnitaMisura.class, key, 0);
			cdv = rcdv.ricercaCondizioniDiVendita(//recupero le condizioni di vendita per avere il prezzo del contenitore
					Azienda.getAziendaCorrente(),//String idAzienda
					docVen.getListinoPrezzi(),//ListinoVendita listino
					docVen.getCliente(),//ClienteVendita cliente
					docVenRig.getArticolo(),//Articolo articolo
					um,//UnitaMisura unita
					BigDecimal.ONE,//BigDecimal quantita
					null,//BigDecimal importo
					null,//ModalitaPagamento modalita
					TimeUtils.getCurrentDate(),//java.sql.Date dataValidita
					docVen.getAgente(),//Agente agente DSSOF3 71003 
					docVen.getSubagente(),//Agente subagente DSSOF3 71003
					null,//UnitaMisura unitaMag
					BigDecimal.ONE,//BigDecimal quantitaMag DSSOF3	71067
					(Valuta) Valuta.elementWithKey(Valuta.class, "EUR", 0),//Valuta valuta
					null,//UnitaMisura umSecMag
					null);
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return cdv;
	}

	private Articolo getArticolo(String cod_Articolo) {
		Articolo art = null;
		try {
			art = (Articolo) Articolo.elementWithKey(Articolo.class, 
					KeyHelper.buildObjectKey(new String[] {Azienda.getAziendaCorrente(),cod_Articolo}), 0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return art;
	}

}