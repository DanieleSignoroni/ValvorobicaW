package it.valvorobica.thip.logis.rf.gui;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Vector;

import com.thera.thermfw.base.ResourceLoader;
import com.thera.thermfw.base.TimeUtils;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.batch.BatchOptions;
import com.thera.thermfw.batch.BatchService;
import com.thera.thermfw.common.ErrorMessage;
import com.thera.thermfw.persist.CachedStatement;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.CopyException;
import com.thera.thermfw.persist.ErrorCodes;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.rf.driver.Driver;
import com.thera.thermfw.rf.driver.DriverVT100;
import com.thera.thermfw.rf.driver.FormCreator;
import com.thera.thermfw.rf.tc.TField;
import com.thera.thermfw.rf.tc.TForm;
import com.thera.thermfw.rf.tc.TList;
import com.thera.thermfw.server.ConnectionException;

import it.softre.thip.acquisti.documentoAC.YDocumentoAcquisto;
import it.thera.thip.acquisti.documentoAC.DocumentoAcqRigaLottoPrm;
import it.thera.thip.acquisti.documentoAC.DocumentoAcqRigaPrm;
import it.thera.thip.acquisti.documentoAC.DocumentoAcquisto;
import it.thera.thip.base.articolo.Articolo;
import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.base.comuniVenAcq.AzioneMagazzino;
import it.thera.thip.base.comuniVenAcq.TipoRiga;
import it.thera.thip.base.generale.IntegrazioneThipLogis;
import it.thera.thip.base.generale.ParametroPsn;
import it.thera.thip.base.generale.UnitaMisura;
import it.thera.thip.base.partner.Valuta;
import it.thera.thip.base.profilo.UtenteAzienda;
import it.thera.thip.logis.bas.Attributo;
import it.thera.thip.logis.bas.EventoApplicativo;
import it.thera.thip.logis.bas.Numeratore;
import it.thera.thip.logis.bas.NumeratoreMaxException;
import it.thera.thip.logis.bas.NumeratoreMaxProgrException;
import it.thera.thip.logis.bas.NumeratoreNotFoundException;
import it.thera.thip.logis.bas.NumeratoreNotValidException;
import it.thera.thip.logis.bas.StampaRptEticUds;
import it.thera.thip.logis.fis.DistribuzioneFisicaPrelievo;
import it.thera.thip.logis.fis.EsecuzioneMissioni;
import it.thera.thip.logis.fis.Missione;
import it.thera.thip.logis.fis.Saldo;
import it.thera.thip.logis.fis.StrategiaPrelievo;
import it.thera.thip.logis.fis.TestataUds;
import it.thera.thip.logis.fis.TipoUds;
import it.thera.thip.logis.fis.Ubicazione;
import it.thera.thip.logis.lgb.OperazioneTipoLista;
import it.thera.thip.logis.lgb.RigaLista;
import it.thera.thip.logis.lgb.TestataLista;
import it.thera.thip.logis.prd.CampoTrascodificaGruppo;
import it.thera.thip.logis.rf.gui.ProcessaListeCompattoRF;
import it.thera.thip.magazzino.generalemag.Lotto;
import it.thera.thip.vendite.documentoVE.DocumentoVenRigaLottoPrm;
import it.thera.thip.vendite.documentoVE.DocumentoVenRigaPrm;
import it.thera.thip.vendite.documentoVE.DocumentoVendita;
import it.thera.thip.vendite.generaleVE.CondizioniDiVendita;
import it.thera.thip.vendite.generaleVE.RicercaCondizioniDiVendita;
import it.thera.thip.vendite.ordineVE.OrdineVenditaRigaPrm;
import it.valvorobica.thip.acquisti.documentoAC.YReportDdtBollaACBatch;
import it.valvorobica.thip.base.articolo.YArticoloDatiMagaz;
import it.valvorobica.thip.base.cliente.YClienteVendita;
import it.valvorobica.thip.base.fornitore.YFornitoreAcquisto;
import it.valvorobica.thip.logis.bas.YCostantiValvo;
import it.valvorobica.thip.logis.configurazione.YStampaDdtAcqTerminalino;
import it.valvorobica.thip.vendite.documentoVE.YDocumentoVenRigaPrm;
import it.valvorobica.thip.vendite.documentoVE.YDocumentoVendita;
import it.valvorobica.thip.vendite.documentoVE.YReportDdtBollaBatch;
import it.valvorobica.thip.vendite.documentoVE.YStampaUdsBatch;

/**
 * <h1>Softre Solutions</h1>
 * 
 * @author Thomas Brescianini 16/11/2022 <br>
 *         <br>
 *         <b>70753 TBSOF3 16/11/2022</b> <br>
 *         Modifiche grafiche alla lista di prelievo e modifica al procedimento
 *         di prelievo di una missione.<br>
 *         <b>70810 TBSOF3 21/12/2022</b> <br>
 *         Correzione errori. Aggiunto ordinamento delle missioni sulla
 *         commessa.<br>
 *         <b>70812 TBSOF3 22/12/2022</b> <br>
 *         Rimuovo dall'elenco delle missioni quelle già eseguite. Aggiunta la
 *         richiesta della stampa del ddt dopo aver chiuso tutte le uds.<br>
 *         <b>70825 TBSOF3 03/01/2022</b> <br>
 *         Creazione di una riga di spesa imballo quando il documento di vendita
 *         passa in stato definitivo. Il prezzo è preso dall'articolo collegato
 *         al tipo UDS nella tabella THIPPERS.YUDSARTICOLO. Per prendere il
 *         prezzo usato il metodo standard. Creo la riga se prezzo > 0 Messo
 *         nella videata della missione il lotto fornitore.<br>
 *         <b>70839 TBSOF3 09/01/2023</b> <br>
 *         Rimessa eccezione sull'addizione nel calcolo della spesa di
 *         imballo.<br>
 *         <b>70849 TBSOF3 10/01/2023</b> <br>
 *         Modifica al tasto F2 del terminalino. Descrizione presa dalla vista
 *         SOFTRE.Y_LMISSIONE_DESCR_V01.<br>
 *         <b>70851 TBSOF3 11/01/2023</b> <br>
 *         Modifica alla pagina note testata/riga del terminalino. Reso
 *         possibile scorrere la pagina con il commento.<br>
 *         <b>70855 TBSOF3 11/01/2023</b> <br>
 *         Aggiunta la riga per il campo UDC nella lista delle missioni.<br>
 *         <b>70862 TBSOF3 13/01/2023</b> <br>
 *         Modificato ordinamento delle missioni in base a commessa, articolo e
 *         fifo.<br>
 *         <b>70874 DSSOF3 20/01/2023</b> <br>
 *         Gestione spese di imballo, cancello e ricreo ogni volta la riga
 *         invece di aggiornarla.<br>
 *         <b>70880 DSSOF3 25/01/2023</b> <br>
 *         Aggiunta descirzione nella spesa imballo nel metodo
 *         <code>controlloChiusura()</code>.<br>
 *         <b>70885 TBSOF3 27/01/2023</b> <br>
 *         Aggiunto il campo sequenza nell'elenco delle missioni.<br>
 *         <b>70897 TBSOF3 31/01/2023</b> <br>
 *         Modificata la videata delle giacenze accessibili dalla missione.
 *         Rimossa riga disponibilità e giacenza in visualizza missione.<br>
 *         <b>70912 TBSOF3 06/02/2023</b> <br>
 *         Aggiunti campi nella stampa del ddt per evitare che si svuoti la
 *         testata del documento durante la stampa.<br>
 *         <b>70915 TBSOF3 07/02/2023</b> <br>
 *         Aggiunta stampa packing list.<br>
 *         <b>70940 TBSOF3 14/02/2023</b> <br>
 *         Aggiunto delay di 2 secondi prima della stampa da terminalino.<br>
 *         <b>70942 TBSOF3 15/02/2023</b> <br>
 *         Aggiunto prefisso "Kit" sugli articoli KIT nell'elenco delle
 *         missioni.<br>
 *         <b>70951 TBSOF3 16/02/2023</b> <br>
 *         Aggiunto stato avanzamento articolo se l'articolo è KIT ed esiste la
 *         riga collegata all'ordine di vendita nella lista delle missioni sul
 *         palmare.<br>
 *         <b>70960 TBSOF3 21/02/2023</b> <br>
 *         Modificata la richiesta di stampa packingList/DDT.<br>
 *         <b>70967 AGSOF3 23/02/2023</b> <br>
 *         Sposto stampa packing list automatica nella classe batch stampa bolla
 *         (ovviamente qui rimane la richiesta se non sto stampando la
 *         bolla).<br>
 *         <b>70970 TBSOF3 27/02/2023</b> <br>
 *         Abilitata la gestione delle cataste.<br>
 *         <b>70973 DSSOF3 28/02/2023</b> <br>
 *         Remmata la stmapa del packing list, sostituita con la stampa
 *         dell'UDS.<br>
 *         <b>70975 TBSOF3 01/03/2023</b> <br>
 *         Omissione missioni tubing<br>
 *         <b>70983 TBSOF3 06/03/2023</b> <br>
 *         Modifica maschera della gestione cataste da terminalino. Mostrata a
 *         video la qta da prelevare e non quella disponibile<br>
 *         <b>70984 TBSOF3 06/03/2023</b> <br>
 *         Nuova modifica alla maschera della gestione cataste. Controllo sulla
 *         qta disponibile e quella da prelevare. Modifica alla abilitazione
 *         delle cataste<br>
 *         <b>70996 TBSOF3 14/03/2023</b> <br>
 *         Correzioni al prelievo gestito a cataste e cambiata la gestione della
 *         omissione delle missioni tubing<br>
 *         <b>70998 TBSOF3 15/03/2023</b> <br>
 *         Modifica schermata elenco saldi (portata su due righe). Reso
 *         impossibile assegnare una quantità maggiore di quella disponibile sul
 *         saldo. Velocizzata procedura pagina giacenze<br>
 *         <b>71002 AGSOF3 16/03/2023</b> <br>
 *         Stampa DDT, con piu copie se ho dati parametri.<br>
 *         <b>71015 TBSOF3 22/03/2023</b> <br>
 *         Salto collo<br>
 *         <b>71036 TBSOF3 03/04/2023</b> <br>
 *         Implementata gestione dei commenti da terminalino sulle liste di
 *         prelievo per i documenti di acquisto<br>
 *         <b>71076 DSSOF3 10/05/2023</b> <br>
 *         Stampa DDT - forzo la formattazione della descrizione vettore 1, nel
 *         caso in cui la stringa sia formattata solo con \n aggiungo \r\n cosi
 *         che la stampa DDT non vada in errore.<br>
 *         <b>71089 AGSOF3 20/05/2023</b> <br>
 *         Nella stampa DDT, - se azienda aellebi e ddt intestato a Valvorobica
 *         (cliente 000308) allora stampo solo 2 copie. - se azeinda valvorobica
 *         e vettore DHL allora solo 2 copie<br>
 *         <b>71095 TBSOF3 22/05/2023</b> <br>
 *         Lancio procedura per riportare i lotti dei materiali sulle righe
 *         secondarie di un ordine/documento di vendita sulla chiusura di una
 *         lista di prelievo<br>
 *         <b>71114 TBSOF3 31/05/2023</b> <br>
 *         Aggiunto l'utente nell'esecuzione della procedura Gestione KGM da
 *         terminalino<br>
 *         <b>71159 TBSOF3 03/07/2023</b> <br>
 *         Aggiunta retrive sulle missioni per andare ad aggiornare lo stato che
 *         rimaneva in stato E post prelievo totale<br>
 *         <b>71176 TBSOF3 14/07/2023</b> <br>
 *         Corretta gestione dell'ultimo prelievo da terminalino<br>
 *         <b>71186 DSSOF3 31/07/2023</b>
 *         <p>
 *         Revisione codice.<br>
 *         Saltare richiesta stampa DDT se viene ritornato almeno un record
 *         dalla vista SOFTRE.Y_LOGIS_LISTE_POS_APERTE_V01.
 *         </p>
 *         <b>71195 TBSOF3 08/08/2023</b> <br>
 *         Gestione peso variabile prima stesura<br>
 *         <b>71197 TBSOF3 10/08/2023</b> <br>
 *         Implementazione tasto F4 nel prelievo della missione<br>
 *         <b>71207 TBSOF3 06/09/2023</b> <br>
 *         Gestione peso variabile gestito sulle righe secondarie del dcoumenti
 *         di conto lavoro <br>
 *         <b>71241 DSSOF3 30/09/2023</b> <br>
 *         Gestione peso variabile, corretto caso di prelievo totale, vado a
 *         committare la qta 5 solo nel caso in cui, la conferma missione sia
 *         avvenuta con successo.</br>
 *         <b>71268 DSSOF3 20/10/2023</b>
 *         <p>
 *         Aggiunta stampa documenti acquisto uscita conto lavoro.<br>
 *         Abilito/Disabilito la richiesta di stampa di DDT - ACQ/VEN in
 *         funzione di:<br>
 *         <ul>
 *         <li>{@link YFornitoreAcquisto#isStampaAutomTerminalinoDDT()}</li>
 *         <li>{@link YClienteVendita#isStampaAutomTerminalinoDDT()}</li>
 *         </ul>
 *         </p>
 *         <b>71277 DSSOF3 26/10/2023</b>
 *         <p>
 *         Ottimizzazione drastica del codice.
 *         </p>
 *         <b>71286 TBSOF3 02/11/2023</b>
 *         <p>
 *         Abilitata la gestione delle cataste. Modificata la videata delle
 *         missioni, mopstriamo la qta inferiore tra disponibile e richiesta
 *         </p>
 *         <b>71357 DSSOF3 19/12/2023</b>
 *         <p>
 *         Sistemare peso 0 in {@link #caricaListaMissioni(TForm)} .<br>
 *         Rimuovere scrollUp in elenco missioni.<br>
 *         </p>
 */

public class YProcessaListeCompattoRF extends ProcessaListeCompattoRF {

	protected boolean comment = false;

	protected static int DAELENCO = 0;

	protected final static int ELENCO_MISSIONI = 14;

	protected final static int VISUALIZZA_MISSIONE = 15;

	protected final static int PAGINA_GIACENZA = 16; // 70897

	protected final static int PAGINA_SALDI_COMPATIBILI = 17; // 71015

	protected final static int PAGINA_SELEZIONE_COM_CONT = 18; // 71197

	protected final static int PAGINA_CONTEGGIO = 19; // 71197

	protected int itemPos = 0;

	protected boolean reverse = false;

	protected int numMisAttPreCanc = 0;

	protected boolean creazioneUds = false;

	protected String codiceUdsInChiusura;

	protected String qtaDaPrl = ""; // 70983

	protected Saldo saldoPreCatasta = null; // 70996

	protected Missione missionePost = null; // 71195

	protected BigDecimal qtaEvasa5; // 71195

	protected BigDecimal qtaTotaleUds; // 71197

	public ErrorMessage esegui() {
		ErrorMessage err = null;
		boolean interrompi = false;
		pagina = PRESENTAZIONE;
		while (!interrompi) {
			switch (pagina) {
			case MENU:
				interrompi = true;
				break;
			case PRESENTAZIONE:
				esecuzioneMissioni = (EsecuzioneMissioni) Factory.createObject(EsecuzioneMissioni.class);
				paginaPresentazione();
				break;
			case RANDOM_LISTA:
				paginaRandomLista();
				break;
			case SELEZIONE:
				paginaSelezione();
				break;
			case ELENCO:
				paginaElenco();
				break;
			case TIPO_UDS:
				paginaTipoUds();
				break;
			case UDS:
				paginaUds();
				break;
			case MISSIONE:
				paginaMissione();
				break;
			case CANCELLA: {
				numMisAttPreCanc = getMissioniAttive();
				paginaCancella();
				break;
			}
			case ESITO: {
				if (numMisAttPreCanc == 0)
					numMisAttPreCanc = getMissioniAttive();
				paginaEsito();
				break;
			}
			case CHIUDI_UDS: {
				if (esecuzioneMissioni.getNumUdsAperte() > 0) {
					paginaChiudiUds();
				} else if (creazioneUds) {
					controlloChiusura();
					pagina = SELEZIONE;
				} else
					pagina = SELEZIONE;
				break;
			}
			case CHIUDI_TIPO_UDS:
				paginaChiudiTipoUds();
				break;
			case DIMENSIONI_UDS:
				paginaDimensioniUds();
				creazioneUds = true;
				if (esecuzioneMissioni.getNumUdsAperte() > 0)
					controlloChiusura();
				break;
			case ESITO_UDS:
				paginaEsito();
				break;
			case ELENCO_MISSIONI: {
				if (getMissioniAttive() > 0)
					paginaElencoMissioni();
				else
					pagina = ESITO;
				break;
			}
			case VISUALIZZA_MISSIONE:
				pagina = visualizzaMissione();
				break;
			case PAGINA_GIACENZA:
				pagina = paginaGiacenza();
				break;
			case PAGINA_SALDI_COMPATIBILI:
				paginaSaldiCompatibili();
				break;
			case PAGINA_SELEZIONE_COM_CONT:
				pagina = paginaSelezioneComCont();
				break;
			case PAGINA_CONTEGGIO:
				pagina = paginaConteggio();
				break;
			default:
				try {
					messaggio(true,
							ResourceLoader.getString(RESOURCES, "msg0039", new String[] { String.valueOf(pagina) }));
					interrompi = true;
				} catch (Exception e) {
					e.printStackTrace(Trace.excStream);
					interrompi = true;
				}
				break;
			}
		}
		return err;
	}

	public YProcessaListeCompattoRF(Driver drv, String xml) {
		super(drv, xml);
		try {
			FormCreator.print(YCostantiValvo.getFileXmlValvo(), this);
		} catch (Exception e) {
			e.printStackTrace(Trace.excStream);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void paginaSelezione() {
		ErrorMessage errore = null;
		gestUdsInCorso();
		TForm form = getTForm(formPaginaSelezione);
		try {
			Vector lista = caricaElencoListe();
			if (lista.size() == 0) {
				errore = new ErrorMessage("LOGIST0106");
				messaggio(true, errore.getLongText());
				pagina = PRESENTAZIONE;
				return;
			}
			if (filtro != null && !filtro.equals("")) {
				applicaFiltro(lista, filtro);
				if (elencoFiltrato.size() > 1) {
					pagina = ELENCO;
					return;
				} else {
					if (sendMessaggioNoFiltro)
						messaggio(false, ResourceLoader.getString(RESOURCES, "msg0041", new String[] { filtro }));
					filtro = null;
					tipo = ' ';
				}
			}
			int pgNext = filtraListaSocieta(lista);
			if (pgNext > 0) {
				pagina = pgNext;
				return;
			}
			form.getTField("CodiceFiltroTxt").setValue("");
			form.getTField("TastoF3").setValue("");
			caricaLista(form, lista);
			sendForm(form);
			TForm risposta = readInput();
			if (risposta.getKeyPressed().equals(TForm.KEY_ESC) || risposta.getKeyPressed().equals(TForm.KEY_CTL_X)) {
				pagina = MENU;
				return;
			}
			if (risposta.getKeyPressed().equals(TForm.KEY_F5)) {
				pagina = PRESENTAZIONE;
				return;
			}
			if (risposta.getKeyPressed().equals(TForm.KEY_F1) || risposta.getKeyPressed().equals(TForm.KEY_F3)
					|| risposta.getKeyPressed().equals(TForm.KEY_F4) || risposta.getKeyPressed().equals(TForm.KEY_F6)
					|| risposta.getKeyPressed().equals(TForm.KEY_F9) || risposta.getKeyPressed().equals(TForm.KEY_F10))
				return;
			filtro = form.getTField("CodiceFiltroTxt").getValue();
			if (filtro != null && !filtro.equals("")) {
				applicaFiltro(lista, filtro);
				if (elencoFiltrato.size() == 0) {
					errore = new ErrorMessage("LOGIST0107", filtro);
					messaggio(true, errore.getLongText());
					filtro = null; // Resetto il vecchio filtro.
					tipo = ' ';
					pagina = SELEZIONE;
					return;
				}
				if (elencoFiltrato.size() == 1) {
					TestataLista lis = (TestataLista) elencoFiltrato.elementAt(0);
					esecuzioneMissioni.getElencoTestate().removeAllElements();
					esecuzioneMissioni.getElencoTestate().addElement(lis);
					// Verifica se la lista scelta può essere gestita con UdS in picking.
					String commento = getCommento();
					getMessaggioCommento(false, commento);
					if (esecuzioneMissioni.isAbilPrepUds())
						esecuzioneMissioni.setAbilPrepUds(checkGestioneUds(lis));
					gestioneUds = esecuzioneMissioni.isAbilPrepUds();
					esecuzioneMissioni.setAbilPrepUds(false);
					// Allestisce la testata selezionata.
					if (esecuzioneMissioni.isAbilPrepUds()) {
						esecuzioneMissioni.allestisciListe();
						paginaElencoColliGenerati(); // Visualizzo i colli già generati.
						gestioneUdsInCorso = true;
					} else {
						esecuzioneMissioni.setTestataUds(null);
						gestioneUdsInCorso = false;
						if (gestioneUds)
							paginaElencoColliGenerati();
					}
					errore = trattaLista();
					if (errore == null) {
						if (esecuzioneMissioni.isAbilPrepUds())
							pagina = TIPO_UDS;
						else
							pagina = ELENCO_MISSIONI;
					} else {
						messaggio(true, errore.getLongText());
						pagina = SELEZIONE;
					}
				} else
					pagina = ELENCO;
				return;
			}
			TList list = risposta.getTList("ListaMissioni");
			itemPos = list.getCurrentSelectedItem();
			list.getListSelectedItem().removeElement(new Integer(itemPos));
			list.selectCurrent();
			errore = caricaListeInEsecuzione(lista, list.getListSelectedItem());
			gestioneUds = esecuzioneMissioni.isAbilPrepUds();
			esecuzioneMissioni.setAbilPrepUds(false);
			if (errore == null) {
				String commento = getCommento();
				getMessaggioCommento(false, commento);
				if (esecuzioneMissioni.isAbilPrepUds()) {
					int numAll = esecuzioneMissioni.allestisciListe();
					if (numAll > 1) {
						if (!conferma(false, ResourceLoader.getString(RESOURCES, "cnf0008"))) {
							pagina = SELEZIONE;
							return;
						}
					}
					paginaElencoColliGenerati();
					gestioneUdsInCorso = true;
				} else {
					esecuzioneMissioni.setTestataUds(null);
					gestioneUdsInCorso = false;
					if (gestioneUds) {
						paginaElencoColliGenerati();
					}
				}
				errore = trattaLista();
			}
			if (errore == null) {
				if (esecuzioneMissioni.isAbilPrepUds())
					pagina = TIPO_UDS;
				else {
					pagina = ELENCO_MISSIONI; // mostro l'elenco delle missioni in modo da porter scegliere prima quale
					// eseguire
				}
			} else {
				messaggio(true, errore.getLongText());
				pagina = SELEZIONE;
			}
		} catch (Exception e) {
			e.printStackTrace(Trace.excStream);
			pagina = MENU;
		}
		return;
	}

	protected void paginaElenco() {
		ErrorMessage errore = null;
		TForm form = getTForm(formPaginaElenco);
		try {
			caricaLista(form, elencoFiltrato);
			String testo = ResourceLoader.getString(RESOURCES, "gen0039") + " ";
			switch (tipo) {
			case COD_LISTA:
				testo = ResourceLoader.getString(RESOURCES, "gen0029") + " ";
				break;
			case COD_DEST:
				testo = ResourceLoader.getString(RESOURCES, "gen0030") + " ";
				break;
			case DESC_DEST:
				testo = ResourceLoader.getString(RESOURCES, "gen0030") + " ";
				break;
			case COD_ORDINE:
				testo = ResourceLoader.getString(RESOURCES, "gen0031") + " ";
				break;
			case COD_VETTORE:
				testo = ResourceLoader.getString(RESOURCES, "gen0032") + " ";
				break;
			case COD_GIRO:
				testo = ResourceLoader.getString(RESOURCES, "gen0033") + " ";
				break;
			case COD_PIANO:
				testo = ResourceLoader.getString(RESOURCES, "gen0034") + " ";
				break;
			case COD_SPED:
				testo = ResourceLoader.getString(RESOURCES, "gen0035") + " ";
				break;
			case COD_ZONA:
				testo = ResourceLoader.getString(RESOURCES, "gen0036") + " ";
				break;
			case RIF_PARTNER:
				testo = ResourceLoader.getString(RESOURCES, "gen0037") + " ";
				break;
			case ' ':
				testo = ResourceLoader.getString(RESOURCES, "gen0038");
				filtro = "";
				break;
			default:
			}
			form.getTField("FiltroLbl").setValue(testo + filtro);
			sendForm(form);
			TForm risposta = readInput();
			if (risposta.getKeyPressed().equals(TForm.KEY_ESC) || risposta.getKeyPressed().equals(TForm.KEY_CTL_X)) {
				pagina = MENU;
				return;
			}
			if (risposta.getKeyPressed().equals(TForm.KEY_F5)) {
				pagina = SELEZIONE;
				filtro = null;
				tipo = ' ';
				return;
			}
			if (risposta.getKeyPressed().equals(TForm.KEY_F1) || risposta.getKeyPressed().equals(TForm.KEY_F3)
					|| risposta.getKeyPressed().equals(TForm.KEY_F4) || risposta.getKeyPressed().equals(TForm.KEY_F6)
					|| risposta.getKeyPressed().equals(TForm.KEY_F9) || risposta.getKeyPressed().equals(TForm.KEY_F10))
				return;
			TList list = risposta.getTList("ListaMissioni");
			int itemPos = list.getCurrentSelectedItem();
			list.getListSelectedItem().removeElement(new Integer(itemPos));
			list.selectCurrent();
			errore = caricaListeInEsecuzione(elencoFiltrato, list.getListSelectedItem());
			gestioneUds = esecuzioneMissioni.isAbilPrepUds();
			esecuzioneMissioni.setAbilPrepUds(false);
			if (errore == null) {
				if (esecuzioneMissioni.isAbilPrepUds()) {
					int numAll = esecuzioneMissioni.allestisciListe();
					if (numAll > 1) { // Warning se ho più allestimenti.
						if (!conferma(false, ResourceLoader.getString(RESOURCES, "cnf0008"))) {
							pagina = SELEZIONE;
							return;
						}
					}
					paginaElencoColliGenerati(); // Visualizzo i colli già generati.
					gestioneUdsInCorso = true;
				} else {
					esecuzioneMissioni.setTestataUds(null);
					gestioneUdsInCorso = false;
				}
				String commento = getCommento(); // mostro il commento della riga
				getMessaggioCommento(false, commento);
				errore = trattaLista();
			}
			if (errore == null) {
				if (esecuzioneMissioni.isAbilPrepUds()) {
					pagina = TIPO_UDS;
				} else {
					pagina = ELENCO_MISSIONI;
				}
			} else {
				messaggio(true, errore.getLongText());
				pagina = SELEZIONE;
			}
		} catch (Exception e) {
			e.printStackTrace(Trace.excStream);
			pagina = MENU;
		}
	}

	@SuppressWarnings("rawtypes")
	protected String getCommento() {
		String commento = new String();
		ResultSet rs = null;
		CachedStatement cs = null;
		try {
			int i = 0;
			Vector elencoTestate = esecuzioneMissioni.getElencoTestate();
			TestataLista lista = (TestataLista) elencoTestate.get(0);
			String acqVen = "";
			if (lista != null) {
				acqVen = lista.getCodice();
				acqVen = acqVen.substring(0, 1);
			}
			DocumentoVendita docVen = null;
			DocumentoAcquisto docAcq = null;
			if (acqVen.equals("V"))
				docVen = getDocumentoVendita();
			else if (acqVen.equals("A"))
				docAcq = getDocumentoAcquisto(); // 71036 Aggiunta gestione sui docAcq
			if (docVen != null) {
				String select = "";
				commento += "Peso totale spedizione : "
						+ (docVen.getPesoLordo() != null ? docVen.getPesoLordo() : BigDecimal.ZERO) + "--";
				select = "SELECT NLS_COMMENT_TEXT FROM SOFTRE.Y_NOTE_MAG_TD_V01 " + "WHERE ID_AZIENDA='"
						+ Azienda.getAziendaCorrente() + "' " + "AND ID_ANNO_DOC='" + docVen.getAnnoDocumento() + "' "
						+ "AND ID_NUMERO_DOC = '" + docVen.getNumeroDocumento() + "' " + "AND TIPO_LISTA = '" + acqVen
						+ "'";
				cs = new CachedStatement(select);
				rs = cs.executeQuery();
				while (rs.next()) {
					i++;
					String commenti = rs.getString("NLS_COMMENT_TEXT");
					commento += i + ")" + commenti + "-";
				}

			} else if (docAcq != null) {
				String select = "";
				commento += "Peso totale spedizione : "
						+ (docAcq.getPesoLordo() != null ? docAcq.getPesoLordo() : BigDecimal.ZERO) + "--";
				select = "SELECT NLS_COMMENT_TEXT FROM SOFTRE.Y_NOTE_MAG_TD_V01 " + "WHERE ID_AZIENDA='"
						+ Azienda.getAziendaCorrente() + "' " + "AND ID_ANNO_DOC='" + docAcq.getAnnoDocumento() + "' "
						+ "AND ID_NUMERO_DOC = '" + docAcq.getNumeroDocumento() + "' " + "AND TIPO_LISTA = '" + acqVen
						+ "'";
				cs = new CachedStatement(select);
				rs = cs.executeQuery();
				while (rs.next()) {
					i++;
					String commenti = rs.getString("NLS_COMMENT_TEXT");
					commento += i + ")" + commenti + "-";
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.free();
				}
			} catch (Throwable t) {
				t.printStackTrace(Trace.excStream);
			}
		}
		return commento;
	}

	@SuppressWarnings("rawtypes")
	protected DocumentoVendita getDocumentoVendita() {
		DocumentoVendita docVen = null;
		try {
			Vector elencoTestate = esecuzioneMissioni.getElencoTestate();
			TestataLista lista = (TestataLista) elencoTestate.get(0);
			String anno = lista.getCodice().substring(1, 5);
			String numero = lista.getCodice().substring(5);
			String key = KeyHelper.buildObjectKey(new String[] { Azienda.getAziendaCorrente(), anno, numero });
			docVen = (DocumentoVendita) DocumentoVendita.elementWithKey(DocumentoVendita.class, key, 0);
			if (docVen != null)
				return docVen;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return docVen;
	}

	@SuppressWarnings("rawtypes")
	protected DocumentoAcquisto getDocumentoAcquisto() { // 71036 Aggiunta gestione sui docAcq
		DocumentoAcquisto docAcq = null;
		try {
			Vector elencoTestate = esecuzioneMissioni.getElencoTestate();
			TestataLista lista = (TestataLista) elencoTestate.get(0);
			String anno = lista.getCodice().substring(1, 5);
			String numero = lista.getCodice().substring(5);
			String key = KeyHelper.buildObjectKey(new String[] { Azienda.getAziendaCorrente(), anno, numero });
			docAcq = (DocumentoAcquisto) DocumentoAcquisto.elementWithKey(DocumentoAcquisto.class, key, 0);
			if (docAcq != null)
				return docAcq;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return docAcq;
	}

	@SuppressWarnings({ "unused", "rawtypes" })
	protected void getMessaggioCommento(boolean errore, String testo) throws Exception {
		if (testo == null || testo.equals(""))
			return;
		testo = testo.replace('\n', ' ');
		testo = testo.replace('à', 'a');
		testo = testo.replace('è', 'e');
		testo = testo.replace('é', 'e');
		testo = testo.replace('ì', 'i');
		testo = testo.replace('ò', 'o');
		testo = testo.replace('ù', 'u');
		while (true) {
			TForm form = getTForm("YpaginaCommento");
			int h = 21;
			int u = 0;
			TList list = form.getTList("MeggaggioCommento");
			list.resetSelected();
			Vector elencoVecchio = list.getItems();
			while (elencoVecchio.size() > 1) {
				elencoVecchio.removeElementAt(1);
			}
			String item = "";
			while (u < testo.length()) {
				while (u < testo.length() && u < h) {
					if (!testo.substring(u, u + 1).equals("-")) {
						item = item + testo.substring(u, u + 1);
						u++;
					} else {
						u++;
						break;
					}
				}
				list.addItem(item);
				item = "";
				h = u + 21;
			}
			sendForm(form);
			TForm risposta = readInput();
			break;
		}
	}

	@SuppressWarnings({ "rawtypes", "static-access", "unchecked" })
	protected int visualizzaMissione() {
		ErrorMessage errore = null;
		esecuzioneMissioni.setScannerInput(null);
		Missione m = esecuzioneMissioni.getMissInConferma();
		saldoPreCatasta = m.getSaldo(); // 70996 mi salvo il saldo iniziale, perchè se dovessi prelevare tutto il saldo
		// gestito a cataste andrebbe in errore, quindi dovrei sostituirlo con questo
		// chè ha giacenza
		if (m != null) {
			try {
				boolean check = checkMissioniOmesse(m); // 70996 aggiunto check sull'omissione tubing prima di mostrare
				// la missione. Rimuovo la missione dall'elenco solo quando vado
				// a mostrare l'elenco
				if (!check) {
					return ELENCO_MISSIONI;
				}
				if (m.isOnDB()) {
					m.retrieve(PersistentObject.NO_LOCK);
					if (m.getStatoMissione() != Missione.ESECUZIONE)
						return CONFERMA;
					if ((m.getRigaLista() != null && m.getRigaLista().getStatoRigaLista() == RigaLista.CHIUSO)
							|| m.getSaldo() == null) {
						Vector errori = m.confermaMissione(new BigDecimal(0));
						if (errori.size() != 0 && errori.elementAt(0) instanceof ErrorMessage) {
							messaggio(true, ((ErrorMessage) errori.elementAt(0)).getLongText());
							return VISUALIZZA_MISSIONE;
						} else
							return ELENCO_MISSIONI;
					} else
						m.getSaldo().retrieve(PersistentObject.NO_LOCK);
				}
				if (gestioneUdsInCorso) {
					String compRes = esecuzioneMissioni.controlloCompatibilitaUds(null, m.getRigaLista());
					if (compRes != null) {
						if (!compRes.equals(esecuzioneMissioni.UDS_CHIUSA))
							messaggio(true, ResourceLoader.getString(RESOURCES, "msg0042", new String[] { compRes }));
						return NUOVA_UDS;
					}
				}
				TForm form = getTForm("YvisualizzaMissioneUds"); // messo la mia
				form.getTField("BarcodeTxt").setSize(50);
				settaDatiForm(form, m);
				form.getTField("QtaGiacienzaLbl").setValue("");
				form.getTField("QtaGiacienzaVal").setValue("");
				form.getTField("QtaDisponibileLbl").setValue("");
				form.getTField("QtaDisponibileVal").setValue("");
				sendForm(form);
				TForm risposta = readInput();
				boolean chkTastoFunc = this.chkTastoFunc(risposta);
				if (risposta.getKeyPressed().equals(TForm.KEY_ESC)
						|| risposta.getKeyPressed().equals(TForm.KEY_CTL_X)) {
					if (chkTastoFunc)
						messaggio(false, ResourceLoader.getString(RESOURCES, "msg0025"));
					elencoMatricole.clear();
					return ELENCO_MISSIONI;
				}
				if (risposta.getKeyPressed().equals(TForm.KEY_F9) && flagAvanti) {
					return VISUALIZZA_MISSIONE;
				}
				if (risposta.getKeyPressed().equals(TForm.KEY_F4)) { // 71197 se è presente un commento chiedo quale
					// funzione eseguire, sennò apro la pagina di
					// conteggio
					if (comment) {
						return PAGINA_SELEZIONE_COM_CONT;
					} else {
						return PAGINA_CONTEGGIO;
					}
				}
				if (risposta.getKeyPressed().equals(TForm.KEY_F2)) {
					messaggio(false, getDescrizioneEstesa());
					return VISUALIZZA_MISSIONE;
				}
				if (risposta.getKeyPressed().equals(TForm.KEY_F7)) {
					// return PAGINA_GIACENZA; 71015 TBSOF3 sostituita la funzione del stato f7
					return PAGINA_SALDI_COMPATIBILI;
				}
				if (gestioneNote && risposta.getKeyPressed().equals(TForm.KEY_F5)) {
					gestioneInfoMissione(m);
					return ELENCO_MISSIONI;
				}
				if (risposta.getKeyPressed().equals(TForm.KEY_F1) || (risposta.getKeyPressed().equals(TForm.KEY_F3))
						|| flagConfermaConSpunta) {
					String barcode = risposta.getTField("BarcodeTxt").getValue();
					String qtaConfermata = risposta.getTField("QtaConfermataTxt").getValue();
					qtaDaPrl = qtaConfermata; // 70983 verrà usata per la videaata della gestione delle cataste
					// 71015 TBSOF3 Remmate le due righe successive perchè disabilitata la gestione
					// delle cataste
					// boolean gestioneCataste = gestioneCataste(m); //70984 vado a controllare se è
					// abilitata la gestione delle cataste
					// esecuzioneMissioni.setflagGestCatasta(gestioneCataste); //70970 TBSOF3
					// Abilitata la gestione delle cataste
					esecuzioneMissioni.setflagGestCatasta(false);
					qtaDaPrl = qtaConfermata;
					// 71286 attivata al gestione delle cataste che era stata disabilitata
					boolean gestioneCataste = gestioneCataste(m); // 70984 vado a controllare se è abilitata la gestione
					// delle cataste
					esecuzioneMissioni.setflagGestCatasta(gestioneCataste); // 70970 TBSOF3 Abilitata la gestione delle
					// cataste
					errore = testBarcode(barcode);
					if (qtaConfermata.equals("") || qtaConfermata.equals("0")) {
						if (esecuzioneMissioni.getQtaConfermata() != null
								&& esecuzioneMissioni.getQtaConfermata().compareTo(new BigDecimal(0)) > 0)
							qtaConfermata = esecuzioneMissioni.getQtaConfermata().toString();
					}
					if (errore != null) {
						messaggio(true, errore.getLongText());
						return VISUALIZZA_MISSIONE;
					}
					m = (Missione) esecuzioneMissioni.getMissInConferma();
					Missione mi = null;
					if (m.isOnDB()) {
						String keyMiss = m.getKey();
						try {
							mi = Missione.elementWithKey(keyMiss, PersistentObject.OPTIMISTIC_LOCK);
						} catch (SQLException ex) {
							ex.printStackTrace(Trace.excStream);
							messaggio(true, ResourceLoader.getString(RESOURCES, "msg0026"));
							return VISUALIZZA_MISSIONE;
						}
					} else
						mi = m;
					if (mi == null) {
						messaggio(true, ResourceLoader.getString(RESOURCES, "errChiuMiss") + " "
								+ ResourceLoader.getString(RESOURCES, "vediPC"));
						return ERR_GRAVE;
					}
					BigDecimal qta = new BigDecimal(0);
					if (esecuzioneMissioni.getflagGestCatasta() && esecuzioneMissioni.getElencoSaldiCom().size() > 0) {
						boolean ripeti = false;
						ripeti = gestioneCatasta(mi, qtaConfermata);
						if (ripeti)
							return ELENCO_MISSIONI;
						qta = mi.getQta1Evasa();
					} else {
						if (flagConfermaConSpunta) {
							int stTmp = funzioneCalcolatrice(mi);
							if (stTmp != OK)
								return stTmp;
							qta = mi.getQta1Evasa();
						} else {
							try {
								qta = new BigDecimal(qtaConfermata);
							} catch (Exception ex) {
								ex.printStackTrace(Trace.excStream);
								messaggio(true, ResourceLoader.getString(RESOURCES, "qtaNumero") + " "
										+ ResourceLoader.getString(RESOURCES, "qtaOk"));
								return VISUALIZZA_MISSIONE;
							}
							qta = qta.multiply(esecuzioneMissioni.getCoefMovim());
						}
					}
					if (qta == null || qta.compareTo(new BigDecimal(0)) < 0) {
						messaggio(true, ResourceLoader.getString(RESOURCES, "qtaNoNegativa") + " "
								+ ResourceLoader.getString(RESOURCES, "qtaOk"));
						return VISUALIZZA_MISSIONE;
					}
					BigDecimal qtaPrm = new BigDecimal(0);
					BigDecimal qtaSec = new BigDecimal(0);

					if (qta.compareTo(new BigDecimal(0)) == 0)
						qtaPrm = new BigDecimal(0);
					else
						qtaPrm = mi.getArticolo().gestioneQta2(qta);
					while (qtaPrm == null) {
						String res = paginaQtaUM(mi, qta);
						if (res.equals(ESCI + ""))
							return CLR;
						if (res.equals(INDIETRO + ""))
							return ELENCO_MISSIONI;
						try {
							qtaPrm = new BigDecimal(res);
						} catch (Exception ex) {
							ex.printStackTrace(Trace.excStream);
							messaggio(true, ResourceLoader.getString(RESOURCES, "qtaNumero") + " "
									+ ResourceLoader.getString(RESOURCES, "qtaOk"));
							qtaPrm = null;
						}
					}
					if (qta.compareTo(new BigDecimal(0)) == 0)
						qtaSec = new BigDecimal(0);
					else
						qtaSec = mi.getArticolo().gestioneQta3(qta);
					while (qtaSec == null) {
						String res = paginaQtaUM(mi, qta);
						if (res.equals(ESCI + ""))
							return CLR;
						if (res.equals(INDIETRO + ""))
							return CANCELLA;
						try {
							qtaSec = new BigDecimal(res);
						} catch (Exception ex) {
							ex.printStackTrace(Trace.excStream);
							messaggio(true, ResourceLoader.getString(RESOURCES, "qtaNumero") + " "
									+ ResourceLoader.getString(RESOURCES, "qtaOk"));
							qtaSec = null;
						}
					}
					if (mi.getSaldo() == null) {
						messaggio(true, ResourceLoader.getString(RESOURCES, "msg0043"));
						return VISUALIZZA_MISSIONE;
					}
					if (mi.getTipoMissione() == Missione.SPOSTAMENTO && mi.getCodiceMappaUdcInv() != null
							&& !mi.getCodiceMappaUdcInv().equals("")) {
						if (mi.getMappaUdcInv() != null) {
							try {
								if (!mi.getMappaUdcInv().retrieve(PersistentObject.NO_LOCK))
									mi.setMappaUdcInv(mi.getMappaUdc());
							} catch (SQLException ex) {
								ex.printStackTrace(Trace.excStream);
							}
						} else
							mi.setMappaUdcInv(mi.getMappaUdc());
					}
					String uds = form.getTField("UDSTxt").getValue();
					esecuzioneMissioni.getElencoUds().add(uds);
					Vector err = new Vector();

					boolean confermato = true;
					boolean prelievoParziale = (qta.compareTo(mi.getQta1Richiesta()) < 0);
					if (prelievoParziale) {
						// if (qta.compareTo(mi.getQta1Richiesta().min(mi.getSaldo().getQta1())) != 0) {
						// 70996 remmato il controllo (sarà poi standard) perchè se faccio il prelievo
						// totale di un saldo non preleva nulla
						boolean conf = false;
						try {
							conf = conferma(false, ResourceLoader.getString(RESOURCES, "cnf0005"));
						} catch (Exception ex) {
							ex.printStackTrace(Trace.excStream);
						}
						if (!conf)
							err.add(new ErrorMessage("LOGIST0039"));
						else {
							if (gestioneMatricola) {
								try {
									elencoMatricole = paginaMatricola(mi.getArticolo(), mi.getLotto1(), mi.getLotto2(),
											qta, elencoMatricole, mi.getCodiceMagLogico(), esecuzioneMissioni);
								} catch (Exception e) {
									e.printStackTrace();
									err.add(new ErrorMessage("LOGIS01054", e.getMessage()));
									return VISUALIZZA_MISSIONE;
								}
								if (elencoMatricole.size() < qta.intValue()) {
									return ELENCO_MISSIONI;
								}
							}
							// 71195 inizio
							String keyArt = KeyHelper.buildObjectKey(
									new String[] { Azienda.getAziendaCorrente(), mi.getCodiceArticolo() });
							Articolo art = (Articolo) Articolo.elementWithKey(Articolo.class, keyArt,
									PersistentObject.NO_LOCK);
							if (art != null) {
								YArticoloDatiMagaz artDatiMag = (YArticoloDatiMagaz) art.getArticoloDatiMagaz();
								if (artDatiMag != null && artDatiMag.isGestionePesoVariabile()) {
									RigaLista rigaLista = mi.getRigaLista();
									if (rigaLista != null && rigaLista.getUmBase().equals("nr")
											&& !rigaLista.getUmBase().equals(rigaLista.getUmDocumento()))
										confermato = paginaPesoVariabile(mi);
									else { // 71207
										DocumentoAcqRigaPrm docRig = getDocumentoAcquistoRiga(mi);
										if (docRig != null
												&& !docRig.getUMPrm().getIdUnitaMisura()
														.equals(docRig.getUMRif().getIdUnitaMisura())
												&& docRig.getUMPrm().getIdUnitaMisura().equals("nr"))
											confermato = paginaPesoVariabile(mi);
									}
								}
							}
							// 71195 fine
							if (confermato) {
								missionePost = null;
								err = spezzaMissionePers(mi, qta, qtaPrm, qtaSec);
								if (missionePost != null) {
									if (qtaEvasa5 != null) {
										missionePost.setQta5Evasa(missionePost.getQta5Evasa().add(qtaEvasa5));
										if (missionePost.save() >= 0) {
											ConnectionManager.commit();
										}
										missionePost = null;
										qtaEvasa5 = null;
									}
								}
							}
						}
					} else {
						// 71195 inizio
						String keyArt = KeyHelper
								.buildObjectKey(new String[] { Azienda.getAziendaCorrente(), mi.getCodiceArticolo() });
						Articolo art = (Articolo) Articolo.elementWithKey(Articolo.class, keyArt,
								PersistentObject.NO_LOCK);
						if (art != null) {
							YArticoloDatiMagaz artDatiMag = (YArticoloDatiMagaz) art.getArticoloDatiMagaz();
							if (artDatiMag != null && artDatiMag.isGestionePesoVariabile()) {
								RigaLista rigaLista = mi.getRigaLista();
								if (rigaLista != null && rigaLista.getUmBase().equals("nr")
										&& !rigaLista.getUmBase().equals(rigaLista.getUmDocumento()))
									confermato = paginaPesoVariabile(mi);
								else { // 71207
									DocumentoAcqRigaPrm docRig = getDocumentoAcquistoRiga(mi);
									if (docRig != null
											&& !docRig.getUMPrm().getIdUnitaMisura()
													.equals(docRig.getUMRif().getIdUnitaMisura())
											&& docRig.getUMPrm().getIdUnitaMisura().equals("nr"))
										confermato = paginaPesoVariabile(mi);
								}
								if (qtaEvasa5 != null) {
									mi.setQta5Evasa(mi.getQta5Evasa().add(qtaEvasa5));
									// 71241 DSSOF3 Remmato inizio
									// if(mi.save() >= 0)
									// ConnectionManager.commit();
									// qtaEvasa5 = null;
									// 71241 DSSOF3 Remmato fine
								}
							}
						}
						// 71195 fine
						if (confermato)
							err = trattaMissione(mi, qta, qtaPrm, qtaSec);
						// 71241 Inizio
						// Committo la qta5 solo nel caso in cui la missione e' stata confermata con
						// successo
						if (err.size() == 0) {
							if (mi.save() >= 0) {
								ConnectionManager.commit();
								qtaEvasa5 = null;
							}
						}
						// 71241 Fine
					}
					if (err.size() == 0 || (err.size() == 1 && err.elementAt(0) instanceof String)) {
						if (confermato) {
							elencoMatricole.clear();
							if (!prelievoParziale) {
								esecuzioneMissioni.setTlAbbassamento(null);
								m.setStatoMissione(Missione.TERMINATA);
							}
							if (err.size() == 1) {
								messaggio(false, new ErrorMessage((String) err.elementAt(0)).getLongText());
							}
							form.getTField("UDSTxt").setValue("");
							if (risposta.getKeyPressed().equals(TForm.KEY_F3)) {
								paginaChiudiTipoUds();
								if (esecuzioneMissioni.getTipoUds() != null)
									esecuzioneMissioni.getTestataUds().setTipoUds(esecuzioneMissioni.getTipoUds());
								paginaDimensioniUds();
							}
							numMisAttPreCanc = getMissioniAttive();
							if (prelievoParziale)
								return VISUALIZZA_MISSIONE;
							else
								return ELENCO_MISSIONI;
						}
					} else {
						messaggio(true, ((ErrorMessage) err.elementAt(0)).getLongText());
						form.getTField("UDSTxt").setValue("");
						ConnectionManager.rollback();
						return VISUALIZZA_MISSIONE;
					}
				}
			} catch (Exception e) {
				e.printStackTrace(Trace.excStream);
				return ERR_GRAVE;
			}
		}
		return VISUALIZZA_MISSIONE;
	}

	/**
	 * TBSOF3 71197 Pagina per la selezione dell'operazione da fare sul pulsante F4
	 * in VisualizzaMissione()
	 * 
	 * @return
	 */
	protected int paginaSelezioneComCont() {
		TForm form = getTForm("YPagineSelezioneComCont");
		form.getTField("TitoloLbl").setValue("Cosa vuoi visualizzare?");
		caricaListaScelteComCont(form);
		sendForm(form);
		try {
			TForm risposta = readInput();
			if (risposta.getKeyPressed().equals(TForm.KEY_ESC) || risposta.getKeyPressed().equals(TForm.KEY_CTL_X)) {
				return VISUALIZZA_MISSIONE;
			} else if (risposta.getKeyPressed().equals(TForm.KEY_F2) || risposta.getKeyPressed().equals(TForm.KEY_F3)
					|| risposta.getKeyPressed().equals(TForm.KEY_F4) || risposta.getKeyPressed().equals(TForm.KEY_F6)
					|| risposta.getKeyPressed().equals(TForm.KEY_F7) || risposta.getKeyPressed().equals(TForm.KEY_F8)
					|| risposta.getKeyPressed().equals(TForm.KEY_F9) || risposta.getKeyPressed().equals(TForm.KEY_F10))
				return PAGINA_SELEZIONE_COM_CONT;
			else if (risposta.getKeyPressed().equals(TForm.KEY_F5)) {
				return VISUALIZZA_MISSIONE;
			}
			TList list = risposta.getTList("ElencoScelte");
			int item = list.getCurrentSelectedItem();
			if (item == 1) {
				return PAGINA_CONTEGGIO;
			} else if (item == 2) {
				Missione m = esecuzioneMissioni.getMissInConferma();
				if (m != null) {
					String commento = getCommentoRiga(m);
					if (commento == null || commento.equals("")) {
						getMessaggioCommento(true, "Non è stato trovato nessun commento!");
					} else
						getMessaggioCommento(false, getCommentoRiga(m));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return VISUALIZZA_MISSIONE;
	}

	@SuppressWarnings("rawtypes")
	protected void caricaListaScelteComCont(TForm form) {
		TList list = form.getTList("ElencoScelte");
		list.resetSelected();
		Vector elencoVecchio = list.getItems();
		while (elencoVecchio.size() > 1) {
			elencoVecchio.removeElementAt(1);
		}
		String item = "1) Conteggio";
		if (item.length() >= 22)
			item = item.substring(0, 21);
		list.addItem(item);
		item = "2) Commento";
		if (item.length() >= 22)
			item = item.substring(0, 21);
		list.addItem(item);
	}

	/**
	 * TBSOF3 71197 Mostra le UDS collegate a quella missione che sono già state
	 * prelevate
	 * 
	 * @return
	 */
	protected int paginaConteggio() {
		Missione m = esecuzioneMissioni.getMissInConferma();
		TForm form = getTForm("YPaginaConteggio");
		caricaListaUdsPrelevate(form);
		form.getTField("TitoloLbl").setValue("Riepilogo Prelievi");
		form.getTField("LblArticolo").setValue("Art:" + m.getCodiceArticolo());
		form.getTField("LblQtaPrelevata")
				.setValue("Qta Prelevata:" + qtaTotaleUds.setScale(0, 0) + " " + m.getArticolo().getUmBase());
		String desc = m.getArticolo().getDescrizione();
		if (desc.length() >= 22)
			desc = desc.substring(0, 21);
		form.getTField("LblDescrizione").setValue(desc);
		sendForm(form);
		try {
			TForm risposta = readInput();
			if (risposta.getKeyPressed().equals(TForm.KEY_ESC) || risposta.getKeyPressed().equals(TForm.KEY_CTL_X)) {
				return VISUALIZZA_MISSIONE;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return PAGINA_CONTEGGIO;
	}

	@SuppressWarnings({ "rawtypes", "unused", "unlikely-arg-type" })
	protected void caricaListaUdsPrelevate(TForm form) {
		Vector lista = new Vector();
		Vector elencoVecchio = form.getTList("ElencoUds").getItems(); // Svuota.
		while (elencoVecchio.size() > 1) {
			elencoVecchio.removeElementAt(1);
		}
		List elencoUds = getElencoUdsPrelevate();
		Iterator iter = elencoUds.iterator();
		while (iter.hasNext()) {
			YOggettinoUdsPrelevate ogg = (YOggettinoUdsPrelevate) iter.next();
			if (ogg.getCodUds() != null && !ogg.getCodUds().equals("") && !ogg.equals("-")) {
				String item = ogg.getCodUds() + ": " + ogg.getQta();
				form.getTList("ElencoUds").addItem(item);
			}
		}
	}

	protected List<YOggettinoUdsPrelevate> getElencoUdsPrelevate() {
		qtaTotaleUds = BigDecimal.ZERO;
		List<YOggettinoUdsPrelevate> elenco = new ArrayList<YOggettinoUdsPrelevate>();
		Missione m = esecuzioneMissioni.getMissInConferma();
		if (m != null) {
			ResultSet rs = null;
			CachedStatement cs = null;
			try {
				String select = "SELECT COD_UDS,QTA_PRE_UM_PRM FROM SOFTRE.Y_RIEPILOGO_PREL_UDS_V01 WHERE "
						+ "COD_MAG_FISICO = '" + m.getCodiceMagFisico() + "' AND CODICE = '" + m.getCodice() + "'";
				cs = new CachedStatement(select);
				rs = cs.executeQuery();
				while (rs.next()) {
					YOggettinoUdsPrelevate ogg = new YOggettinoUdsPrelevate();
					String uds = rs.getString("COD_UDS") != null ? rs.getString("COD_UDS").trim() : "";
					String qta = rs.getString("QTA_PRE_UM_PRM") != null ? rs.getString("QTA_PRE_UM_PRM").trim()
							: BigDecimal.ZERO.toString();
					qtaTotaleUds = qtaTotaleUds.add(new BigDecimal(qta));
					BigDecimal quantita = new BigDecimal(qta).setScale(0, 0);
					ogg.setQta(quantita.toString());
					ogg.setCodUds(uds);
					elenco.add(ogg);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
					if (cs != null) {
						cs.free();
					}
				} catch (Throwable t) {
					t.printStackTrace(Trace.excStream);
				}
			}
		}
		return elenco;
	}

	/**
	 * <h1>Gestione peso variabile</h1> <br>
	 * Thomas Brescianini 08/08/2023 <br>
	 * <p>
	 * Viene proposta una videata in cui è possibile andare ad inserire un altro
	 * peso in base alla UM proposta. Questo dato verrà poi salvato nella colonna
	 * QTA_EVASIONE_5 della tabella LOGIS.LMISSIONE. Questa popolazione servirà per
	 * andare a modificare in fase di chiusura della lista, la qtaInUnVen/Acq.
	 * </p>
	 */
	protected boolean paginaPesoVariabile(Missione m) {
		boolean ok = false;
		try {
			while (!ok) {
				TForm form = getTForm("YFormPaginaPesoVariabile");
				form.getTField("Rilevazione").setValue("Rilevazione");
				form.getTField("ValoreInput").setValue("0");
				String value = "Qta teorica= ";
				String um = "";
				DocumentoVenRigaPrm docVen = getDocumentoVenditaRiga(m, m.getRigaLista());
				if (docVen != null) {
					BigDecimal qtaRif = docVen.getQtaPropostaEvasione().getQuantitaInUMRif();
					BigDecimal qtaPrm = docVen.getQtaPropostaEvasione().getQuantitaInUMPrm();
					if (qtaRif.compareTo(BigDecimal.ZERO) <= 0 || qtaPrm.compareTo(BigDecimal.ZERO) <= 0) {
						qtaRif = docVen.getQtaAttesaEvasione().getQuantitaInUMRif();
						qtaPrm = docVen.getQtaAttesaEvasione().getQuantitaInUMPrm();
					}
					BigDecimal qta = qtaRif.divide(qtaPrm, 0);
					qta = qta.multiply(new BigDecimal(qtaDaPrl));
					qta = qta.setScale(2, 0);
					value += qta.toString();
					um = docVen.getUMRif().getIdUnitaMisura();
				} else {
					DocumentoAcqRigaPrm docAcq = getDocumentoAcquistoRiga(m);
					if (docAcq != null) {
						BigDecimal qtaRif = docAcq.getQtaPropostaEvasione().getQuantitaInUMRif();
						BigDecimal qtaPrm = docAcq.getQtaPropostaEvasione().getQuantitaInUMPrm();
						if (qtaRif.compareTo(BigDecimal.ZERO) <= 0 || qtaPrm.compareTo(BigDecimal.ZERO) <= 0) {
							qtaRif = docAcq.getQtaAttesaEvasione().getQuantitaInUMRif();
							qtaPrm = docAcq.getQtaAttesaEvasione().getQuantitaInUMPrm();
						}
						BigDecimal qta = qtaRif.divide(qtaPrm, 0);
						qta = qta.multiply(new BigDecimal(qtaDaPrl));
						qta = qta.setScale(2, 0);
						value += qta.toString();
						um = docAcq.getUMRif().getIdUnitaMisura();
					}
				}
				form.getTField("UmVariabile").setValue("Um Variabile : " + um);
				form.getTField("QtaTeorica").setValue(value);
				sendForm(form);
				TForm risposta = readInput();
				if (risposta.getKeyPressed().equals(TForm.KEY_ESC) || risposta.getKeyPressed().equals(TForm.KEY_CTL_X))
					return false;
				else if (risposta.getKeyPressed().equals(TForm.KEY_F1)) {
					if (form.getTField("ValoreInput").getValue() != null) {
						String valore = form.getTField("ValoreInput").getValue();
						if (valore != null && !valore.equals("") && !valore.equals("0")) {
							try {
								BigDecimal qta = new BigDecimal(valore);
								if (qta.compareTo(BigDecimal.ZERO) > 0) {
									// m.setQta5Evasa(m.getQta5Evasa().add(qta));
									// if(m.save() >= 0) {
									// ConnectionManager.commit();
									ok = true;
									qtaEvasa5 = qta;
									// }
								} else {
									messaggio(true, "Inserire in valore superiore a zero");
								}
							} catch (Exception ex) {
								ex.printStackTrace(Trace.excStream);
								messaggio(true, ResourceLoader.getString(RESOURCES, "qtaNumero") + " "
										+ ResourceLoader.getString(RESOURCES, "qtaOk"));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	protected void settaDatiForm(TForm form, Missione m) {
		if (m == null) {
			return;
		}
		OperazioneTipoLista otl = (OperazioneTipoLista) m.getRigaLista().getTestataLista().getTipoLista()
				.getOperazioni().get(0);
		gestioneMatricola = m.getArticolo().getGestioneMatricola()
				&& otl.getOperazioneMovimento().getGestioneMatricola();
		if (gestioneMatricola) {
			form.getTField("ArticoloMatricolato").setVisible(true);
			form.getTField("ArticoloMatricolato").setValue(DriverVT100.REVERSE_ON
					+ ResourceLoader.getString(RESOURCES, "visualizzaMissioneUds.ArtMatr") + DriverVT100.NORMAL_VIDEO);
		} else {
			form.getTField("ArticoloMatricolato").setVisible(false);
			form.getTField("ArticoloMatricolato").setValue("");
		}
		form.getTField("TitoloLbl").setValue(formatoPosizione());
		form.getTField("UMLbl").setValue(ResourceLoader.getString(RESOURCES, "visualizzaMissioneUds.UM.label"));
		form.getTField("ArticoloLbl").setValue(ResourceLoader.getString(RESOURCES, "visualizzaMissioneUds.Art.label"));
		form.getTField("VersioneLbl")
				.setValue(ResourceLoader.getString(RESOURCES, "visualizzaMissioneUds.Versione.label"));
		form.getTField("Lotto1Lbl")
				.setValue(ResourceLoader.getString(RESOURCES, "visualizzaMissioneUds.Lottto1.label"));
		form.getTField("Lotto2Lbl")
				.setValue(ResourceLoader.getString(RESOURCES, "visualizzaMissioneUds.Lottto2.label"));
		form.getTField("UbicSaldoLbl")
				.setValue(ResourceLoader.getString(RESOURCES, "visualizzaMissioneUds.UbicSaldo.label"));
		form.getTField("UdcLbl").setValue(ResourceLoader.getString(RESOURCES, "visualizzaMissioneUds.Udc.label"));
		form.getTField("UbicDestLbl")
				.setValue(ResourceLoader.getString(RESOURCES, "visualizzaMissioneUds.UbicDest.label"));
		form.getTField("QtaGiacienzaLbl")
				.setValue(ResourceLoader.getString(RESOURCES, "visualizzaMissioneUds.QtaGia.label"));
		form.getTField("QtaDisponibileLbl")
				.setValue(ResourceLoader.getString(RESOURCES, "visualizzaMissioneUds.QtaDisp.label"));
		form.getTField("BarcodeLbl")
				.setValue(ResourceLoader.getString(RESOURCES, "visualizzaMissioneUds.Barcode.label"));
		form.getTField("QtaRichiestaLbl")
				.setValue(ResourceLoader.getString(RESOURCES, "visualizzaMissioneUds.QtaRich.label"));
		form.getTField("QtaConfermataLbl")
				.setValue(ResourceLoader.getString(RESOURCES, "visualizzaMissioneUds.QtaConf.label"));
		form.getTField("UDSLbl").setValue(ResourceLoader.getString(RESOURCES, "visualizzaMissioneUds.UDS.label"));
		form.getTField("PuntoCaricoLbl")
				.setValue(ResourceLoader.getString(RESOURCES, "visualizzaMissioneUds.PuntoCarico.label"));
		if (!gestioneUds) {
			form.getTField("UDSLbl").setValue("");
			form.getTField("UDSTxt").setValue("");
			form.getTField("UDSTxt").setDisplay(TField.DISPLAY_OUTPUT);
		} else {
			form.getTField("UDSTxt").setDisplay(TField.DISPLAY_INPUT);
		}

		if (m.getArticolo().getGruppo().getAbilVersione())
			form.getTField("VersioneVal").setValue(m.getArticolo().getVersione());
		else
			form.getTField("VersioneLbl").setValue("");

		gestioneLottoPers(form, m); // METTO IL LOTTO FORNITORE DALLA VISTA SOFTRE.Y_LOTTI_LOGIS_V01
		gestioneConfigurazione(form, m);
		gestionePuntoCarico(form, m);

		form.getTField("UMVal").setValue(formato(m.getArticolo().getUmBase(), 2));
		form.getTField("CodiceArticoloVal").setValue(formato(m.getArticolo().getCodice(), 17));
		form.getTField("DescrArticoloVal").setValue(formato(m.getArticolo().getDescrizione(), 21));

		form.getTField("UbicSaldoVal").setValue(formato(m.getSaldo().getCodiceUbicazione(), 17));
		form.getTField("UbicDestVal").setValue(formato(m.getCodiceUbicazioneInv(), 17));
		gestioneUdc(form, m);

		form.getTField("QtaGiacienzaVal").setValue(formattaBigDec(m.getSaldo().getQta1()));
		BigDecimal qtaDisp = m.getSaldo().calcolaDisponibile().add(m.getQta1Richiesta().min(m.getSaldo().getQta1()));

		form.getTField("QtaDisponibileVal").setValue(formattaBigDec(qtaDisp));

		// 71286 mostro la qta inferiore tra quella disponibile e quella richiesta
		if (qtaDisp.compareTo(m.getQta1Richiesta()) < 0)
			form.getTField("QtaRichiestaVal").setValue(formattaBigDec(qtaDisp));
		else
			form.getTField("QtaRichiestaVal").setValue(formattaBigDec(m.getQta1Richiesta()));

		form.getTField("BarcodeTxt").setValue("");
		if (flagConfermaConSpunta) {
			form.getTField("QtaConfermataTxt").setVisible(false);
			form.getTField("QtaConfermataLbl").setVisible(false);
			form.getTField("QtaConfermataTxt").setValue("");
			form.getTField("QtaConfermataLbl").setValue("");
			form.getTField("QtaConfermataTxt").setDisplay(TField.DISPLAY_OUTPUT);
			form.setAcknowledge(TForm.ACK_LAST_FIELD);
		} else {
			form.getTField("QtaConfermataTxt").setVisible(true);
			form.getTField("QtaConfermataLbl").setVisible(true);
			form.getTField("QtaConfermataTxt").setValue(formatoQtaConfermata(m));
			form.getTField("QtaConfermataLbl")
					.setValue(ResourceLoader.getString(RESOURCES, "visualizzaMissioneUds.QtaConf.label"));
			form.getTField("QtaConfermataTxt").setDisplay(TField.DISPLAY_INPUT);
		}
		form.getTField("TastiFunzioneUltimaLbl").setValue(formatoTastiFunzioneBassiPers());
	}

	protected void gestioneLottoPers(TForm form, Missione m) {
		String lotto = null;
		if (m.getArticolo().getGruppo().getLotti().size() > 0) {
			CampoTrascodificaGruppo campo = (CampoTrascodificaGruppo) m.getArticolo().getGruppo().getLotti().get(0);
			if (campo.getAbilitazione() && (m.getLotto1() != null)) {
				lotto = getLottoFornitore(m);
			}
		}
		lotto = formato(lotto, 17);
		form.getTField("Lotto1Val").setValue(lotto);
		boolean lotto1Visible = lotto.length() > 0;
		form.getTField("Lotto1Lbl").setVisible(lotto1Visible);
		form.getTField("Lotto1Val").setVisible(lotto1Visible);
		if (!lotto1Visible) {
			form.getTField("Lotto1Lbl").setValue("");
		}
	}

	protected String getLottoFornitore(Missione mis) {
		String lotto = "";
		ResultSet rs = null;
		CachedStatement cs = null;
		try {
			String select = "SELECT LOTTO_LOGIS FROM SOFTRE.Y_LOTTI_LOGIS_V01 " + "WHERE ID_AZIENDA = '"
					+ Azienda.getAziendaCorrente() + "' AND " + "ID_ARTICOLO = '" + mis.getCodiceArticolo() + "' AND "
					+ "ID_LOTTO = '" + mis.getLotto1() + "'";
			cs = new CachedStatement(select);
			rs = cs.executeQuery();
			if (rs.next()) {
				lotto = rs.getString("LOTTO_LOGIS").trim();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.free();
				}
			} catch (Throwable t) {
				t.printStackTrace(Trace.excStream);
			}
		}
		return lotto;
	}

	protected String formatoTastiFunzioneBassiPers() {
		String tasti = "";
		if (tasti.length() > 0) {
			tasti += Attributo.formatta("", 1);
		}
		tasti += "F2=D";
		if (tasti.length() > 0) {
			tasti += Attributo.formatta("", 1);
		}
		tasti += "F7=G";
		if (tasti.length() > 0) {
			tasti += Attributo.formatta("", 1);
		}
		tasti += "F4=C";
		while (tasti.length() < 14) {
			tasti += " ";
		}
		tasti += Attributo.formatta("", 1);
		if (gestioneUds)
			tasti += "F3=U";
		return tasti;
	}

	@SuppressWarnings("rawtypes")
	protected String getCommentoRiga(Missione missione) {
		comment = false;
		String commentoRiga = new String();
		ResultSet rs = null;
		CachedStatement cs = null;
		try {
			int i = 0;
			Vector elencoTestate = esecuzioneMissioni.getElencoTestate();
			TestataLista lista = (TestataLista) elencoTestate.get(0);
			String acqVen = "";
			if (lista != null) {
				acqVen = lista.getCodice();
				acqVen = acqVen.substring(0, 1);
			}
			DocumentoVenRigaPrm docVenRig = null;
			DocumentoAcqRigaPrm docAcqRig = null;
			RigaLista rigaLista = missione.getRigaLista();
			if (acqVen.equals("V"))
				docVenRig = getDocumentoVenditaRiga(missione, rigaLista);
			else if (acqVen.equals("A"))
				docAcqRig = getDocumentoAcquistoRiga(missione); // 71036 Aggiunta gestione sui docAcqRig

			if (docVenRig != null) {
				String select = "SELECT * FROM SOFTRE.Y_NOTE_MAG_RD_V01 " + "WHERE ID_AZIENDA='"
						+ Azienda.getAziendaCorrente() + "' " + "AND ID_ANNO_DOC='" + docVenRig.getAnnoDocumento()
						+ "' " + "AND ID_NUMERO_DOC = '" + docVenRig.getNumeroDocumento() + "' " + "AND ID_RIGA_DOC='"
						+ docVenRig.getNumeroRigaDocumento() + "' " + "AND TIPO_LISTA = '" + acqVen + "'";
				cs = new CachedStatement(select);
				rs = cs.executeQuery();
				while (rs.next()) {
					i++;
					String commenti = rs.getString("NLS_COMMENT_TEXT");
					commentoRiga += i + ")" + commenti + "-";
					comment = true;
				}
			} else if (docAcqRig != null) {
				String select = "SELECT * FROM SOFTRE.Y_NOTE_MAG_RD_V01 " + "WHERE ID_AZIENDA='"
						+ Azienda.getAziendaCorrente() + "' " + "AND ID_ANNO_DOC='" + docAcqRig.getAnnoDocumento()
						+ "' " + "AND ID_NUMERO_DOC = '" + docAcqRig.getNumeroDocumento() + "' " + "AND ID_RIGA_DOC='"
						+ docAcqRig.getNumeroRigaDocumento() + "' " + "AND TIPO_LISTA = '" + acqVen + "'";
				cs = new CachedStatement(select);
				rs = cs.executeQuery();
				while (rs.next()) {
					i++;
					String commenti = rs.getString("NLS_COMMENT_TEXT");
					commentoRiga += i + ")" + commenti + "-";
					comment = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.free();
				}
			} catch (Throwable t) {
				t.printStackTrace(Trace.excStream);
			}
		}
		return commentoRiga;
	}

	protected DocumentoVenRigaPrm getDocumentoVenditaRiga(Missione missione, RigaLista lisRig) {
		DocumentoVenRigaPrm docVen = null;
		try {
			String anno = lisRig.getCodiceTestataLista().substring(1, 5);
			String numero = lisRig.getCodiceTestataLista().substring(5);
			if (missione != null) {
				Integer rig = lisRig.getNumeroRigaHost();
				String riga = rig.toString();
				String key = KeyHelper
						.buildObjectKey(new String[] { Azienda.getAziendaCorrente(), anno, numero, riga });
				docVen = (DocumentoVenRigaPrm) DocumentoVenRigaPrm.elementWithKey(DocumentoVenRigaPrm.class, key, 0);
				if (docVen != null)
					return docVen;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return docVen;
	}

	protected DocumentoAcqRigaPrm getDocumentoAcquistoRiga(Missione missione) { // 71036 Aggiunta gestione sui docAcqRig
		DocumentoAcqRigaPrm docAcq = null;
		try {
			if (missione != null) {
				String anno = missione.getRigaLista().getCodiceTestataLista().substring(1, 5);
				String numero = missione.getRigaLista().getCodiceTestataLista().substring(5);
				Integer rig = missione.getRigaLista().getNumeroRigaHost();
				String riga = rig.toString();
				String key = KeyHelper
						.buildObjectKey(new String[] { Azienda.getAziendaCorrente(), anno, numero, riga });
				docAcq = (DocumentoAcqRigaPrm) DocumentoAcqRigaPrm.elementWithKey(DocumentoAcqRigaPrm.class, key, 0);
				if (docAcq != null)
					return docAcq;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return docAcq;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Vector trattaMissione(Missione mi, BigDecimal qta, BigDecimal qtaPrm, BigDecimal qtaSec) {
		// Non è necessario controllare le altre qta siccome:
		// - se sono non gestite o costanti, ovvio
		// - se sono variabili, la richiesta calcolata teoricamente difficilmente sarà
		// corretta con la realtà
		if (qta.compareTo(mi.getQta1Richiesta().min(mi.getSaldo().getQta1())) != 0) {
			boolean conf = false;
			if (qta.compareTo(mi.getSaldo().getQta1()) <= 0) { // qta diversa dalla richiesta ma non maggiore del saldo
				try {
					conf = conferma(false, ResourceLoader.getString(RESOURCES, "cnf0005"));
				} catch (Exception ex) {
					ex.printStackTrace(Trace.excStream);
				}
			}
			// chiedo conferma nel caso in cui la qta è maggiore del saldo
			else {
				try {
					conf = conferma(false, ResourceLoader.getString(RESOURCES, "msg0067"));// 27.10.17
				} catch (Exception ex) {
					ex.printStackTrace(Trace.excStream);
				}
				if (conf)
					mi.setControlloGiacenza(false);
			}
			if (!conf) {
				Vector v = new Vector();
				v.addElement(new ErrorMessage("LOGIST0039"));
				return v;
			}
		}
		if (gestioneUds) {
			String codUds = getTForm("YvisualizzaMissioneUds").getTField("UDSTxt").getValue(); // MESSO LA MIA
			String errore = testPaginaUds(codUds, false);
			if (errore != null) {
				Vector v = new Vector();
				v.addElement(new ErrorMessage("LOGIS01054", errore));
				return v;
			}
		}
		if (gestioneUdsInCorso && qta.compareTo(new BigDecimal(0)) > 0 && esecuzioneMissioni.getTestataUds() != null) {
			mi.setTestataUds(esecuzioneMissioni.getTestataUds()); // Scarica su questa UdS
			udsGenerate = true; // il materiale prelevato.
		}
		if (gestioneMatricola && elencoMatricole.size() < qta.intValue()) {// nel caso missione parziale ho già
			// acquisito le matricole quindi non devo
			// entrare
			try {
				elencoMatricole = paginaMatricola(mi.getArticolo(), mi.getLotto1(), mi.getLotto2(), qta,
						elencoMatricole, mi.getCodiceMagLogico(), esecuzioneMissioni);
			} catch (Exception e) {
				e.printStackTrace();
				Vector v = new Vector();
				v.addElement(new ErrorMessage("LOGIS01054", e.getMessage()));
				return v;
			}
			if (elencoMatricole.size() < qta.intValue()) {// uscito con CTLX, le matricole acquisite vanno conservate
				Vector v = new Vector();
				v.addElement(new ErrorMessage("LOGIST0039"));
				return v;
			}
		}
		mi.getElencoMatricole().clear();
		mi.getElencoMatricole().addAll(elencoMatricole);
		return esecuzioneMissioni.confermaMissione(mi, qta, qtaPrm, qtaSec);
	}

	@SuppressWarnings("unused")
	protected void paginaElencoMissioni() {
		TForm form = getTForm("YElencoMissioni");
		DAELENCO = 0;
		try {
			caricaListaMissioni(form);
			// form.getTList("ElencoMissioni").scrollPageUp(); // scrollo sopra
			sendForm(form);
			TForm risposta = readInput();
			if (risposta.getKeyPressed().equals(TForm.KEY_ESC) || risposta.getKeyPressed().equals(TForm.KEY_CTL_X)) {
				DAELENCO = 22;
				pagina = CANCELLA;
			} else if (risposta.getKeyPressed().equals(TForm.KEY_F1) || risposta.getKeyPressed().equals(TForm.KEY_F3)
					|| risposta.getKeyPressed().equals(TForm.KEY_F4) || risposta.getKeyPressed().equals(TForm.KEY_F5)
					|| risposta.getKeyPressed().equals(TForm.KEY_F6) || risposta.getKeyPressed().equals(TForm.KEY_F7)
					|| risposta.getKeyPressed().equals(TForm.KEY_F9)
					|| risposta.getKeyPressed().equals(TForm.KEY_F10)) {
				pagina = ELENCO_MISSIONI;
			} else {
				TList list = risposta.getTList("ElencoMissioni");
				itemPos = list.getCurrentSelectedItem();
				Missione missione = null;
				int i = 0;
				if ((itemPos - 6) == 0) {
					itemPos = itemPos - 6;
					missione = (Missione) esecuzioneMissioni.getElencoMissioni().elementAt(itemPos);
				} else if ((itemPos - 6) < 0) {
					itemPos = itemPos - itemPos;
					missione = (Missione) esecuzioneMissioni.getElencoMissioni().elementAt(itemPos);
				} else {
					if (itemPos % 6 == 0) {
						itemPos = (itemPos / 6) - 1;
						missione = (Missione) esecuzioneMissioni.getElencoMissioni().elementAt(itemPos);
					} else {
						while (itemPos % 6 != 0) {
							itemPos++;
						}
						itemPos = (itemPos / 6) - 1;
						if (itemPos <= esecuzioneMissioni.getElencoMissioni().size()) // 70847 controllo per rimuovere
							// le eccezioni dell'index
							missione = (Missione) esecuzioneMissioni.getElencoMissioni().elementAt(itemPos);
					}
				}
				esecuzioneMissioni.setMissInConferma(missione);
				pagina = VISUALIZZA_MISSIONE;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * <h1>Rimozione missioni omesse</h1> <br>
	 * Daniele Signoroni 26/10/2023 <br>
	 * <p>
	 * Tramite metodo {@link #checkMissioniOmesse(Missione)} rimuovo le missione che
	 * soddisfano le condizioni
	 * </p>
	 * 
	 * @param list
	 */
	@SuppressWarnings("rawtypes")
	public void rimuoviMissioniOmesse(TList list) {
		Vector elencoVecchio = list.getItems();
		while (elencoVecchio.size() > 1) {
			elencoVecchio.removeElementAt(1);
		}
		for (int j = 0; j < esecuzioneMissioni.getElencoMissioni().size(); j++) {
			Missione missione = (Missione) esecuzioneMissioni.getElencoMissioni().get(j);
			try {
				missione.retrieve(PersistentObject.NO_LOCK); // 71159 Faccio la retrieve per andare ad aggiornare la
				// missione (questo perchè la missione, anche se
				// prelevata, rimaneva in stato E e non si aggiornava)
			} catch (SQLException e) {
				e.printStackTrace();
			}
			boolean check = checkMissioniOmesse(missione); // 70975
			if (check) {
				if (missione.getStatoMissione() != Missione.ESECUZIONE) {
					esecuzioneMissioni.getElencoMissioni().remove(j);
					j--;
				}
			} else {
				esecuzioneMissioni.getElencoMissioni().remove(j);
				j--;
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void caricaListaMissioni(TForm form) {
		TList list = form.getTList("ElencoMissioni");
		if (!reverse) {
			Collections.reverse(esecuzioneMissioni.getElencoMissioni());
			reverse = true;
		}
		Collections.sort(esecuzioneMissioni.getElencoMissioni(), new YComparatorMissioni());
		list.resetSelected();
		rimuoviMissioniOmesse(list);
		Iterator listaMissioni = esecuzioneMissioni.getElencoMissioni().iterator();
		int i = 1;
		while (listaMissioni.hasNext()) {
			Missione missione = (Missione) listaMissioni.next();
			if (missione.getStatoMissione() == Missione.ESECUZIONE) {
				RigaLista rigaLista = (RigaLista) missione.getRigaLista();
				DocumentoVenRigaPrm docVenRig = null;
				// DocumentoAcqRigaPrm docAcqRig = null; // per futura necessita
				switch (rigaLista.getTestataLista().getCodice().charAt(0)) {
				case IntegrazioneThipLogis.VENDITA:
					docVenRig = getDocumentoVenditaRiga(missione, rigaLista);
					break;
				case IntegrazioneThipLogis.ACQUISTO:
					// docAcqRig = getDocumentoAcquistoRiga(missione);
					// break;
				}
				String spazio = "";
				boolean kit = isKit(missione, rigaLista);
				BigDecimal qta = missione.getQta1Richiesta().subtract(missione.getQta1Evasa());
				if (qta.compareTo(BigDecimal.ZERO) < 0) // 70996 potrebbe succedere di avere qtaevasa maggiore di qta
					// richiesta (perchè abbiamo spezzato la missione)
					qta = missione.getQta1Richiesta(); // se dovesse succedere allora mostro solo la qta richiesta senza
				// sottrarre quella evasa (perchè è la evasa della missione
				// filia)
				String item = i + ")";
				if (kit)
					item += "KIT ";
				item += missione.getCodiceArticolo();
				if (item.length() >= 22)
					item = item.substring(0, 21);
				list.addItem(item);
				int sequenza = docVenRig != null ? docVenRig.getSequenzaRiga() : 0;
				item = " SEQ." + sequenza + " Q."
						+ Double.valueOf(qta != null ? qta.toString() : BigDecimal.ZERO.toString()); // 70885
				if (item.length() >= 22)
					item = item.substring(0, 21);
				list.addItem(item);
				BigDecimal pesoSingolo = BigDecimal.ZERO;
				// DSSOF3 Inizio, prendo il peso unitario dall'articolo logis, se um peso = g
				// divido per 1000
				if (missione.getArticolo().getPesoUnitario() != null) {
					pesoSingolo = missione.getArticolo().getPesoUnitario();
					if (missione.getArticolo().getUmPeso().equals("g")) {
						pesoSingolo = pesoSingolo.divide(new BigDecimal(100), RoundingMode.DOWN);
					}
					// In seguito lo moltiplico per la quantita richiesta della missione
					pesoSingolo = pesoSingolo.multiply(missione.getQta1Richiesta()).setScale(2, RoundingMode.DOWN);
				}
				item = " U." + missione.getCodiceUbicazione() + " P." + Double.valueOf(pesoSingolo.toString());
				if (item.length() >= 22)
					item = item.substring(0, 21);
				list.addItem(item);
				String udc = missione.getCodiceMappaUdc();
				if (udc != null && !udc.equals(""))
					item = " " + udc;
				else
					item = " SenzaUDC";
				list.addItem(item);
				String commessa = docVenRig != null ? docVenRig.getStringaEDI1() : "";
				if (commessa != null && !commessa.equals(""))
					item = " C." + commessa;
				else
					item = " ";
				list.addItem(item);
				list.addItem(spazio);
				i++;
			}
		}
	}

	protected String getStatoArticoloKit(Missione missione) { // 70951
		String item = "";
		DocumentoVenRigaPrm docVenRigPrm = getDocumentoVenditaRiga(missione, missione.getRigaLista());
		if (docVenRigPrm != null) {
			String key = docVenRigPrm.getRigaOrdineKey();
			if (key != null && !key.equals("")) {
				try {
					OrdineVenditaRigaPrm ordVenRig = (OrdineVenditaRigaPrm) OrdineVenditaRigaPrm
							.elementWithKey(OrdineVenditaRigaPrm.class, key, 0);
					if (ordVenRig != null) {
						if (ordVenRig.getFlagRiservatoUtente1() == '-')
							item = " Non significativo";
						else if (ordVenRig.getFlagRiservatoUtente1() == '0')
							item = " Da assemblare";
						else if (ordVenRig.getFlagRiservatoUtente1() == '1')
							item = " In corso";
						else if (ordVenRig.getFlagRiservatoUtente1() == '2')
							item = " Assemblato";
						else
							item = "";
					} else
						item = "";
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else
				item = "";
		} else
			item = "";

		return item;
	}

	protected boolean isKit(Missione missione, RigaLista lisRig) {
		boolean kit = false;
		if (lisRig != null) {
			String codiceLis = lisRig.getCodiceTestataLista();
			if (codiceLis != null && codiceLis.charAt(0) == 'V') {
				int detRigaHost = 0;
				detRigaHost = lisRig.getDettaglioRigaHost();
				if (detRigaHost != 0)
					kit = true;
			}
		}
		return kit;
	}

	// 70998 modificata completamente la query per renderla 1000 volte più veloce
	// (questo perchè rallentava il server quando venivano lanciate le operazioni
	// su questa funzione
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Vector getGiacenze() {
		Vector giacenze = new Vector();
		ResultSet rs = null;
		CachedStatement cs = null;
		try {
			Missione missione = esecuzioneMissioni.getMissInConferma();
			if (missione != null) {
				Saldo saldo = missione.getSaldo();
				String select = "SELECT COD_UBICAZIONE,LOTTO01,QTA1,COD_MAPPA_UDC FROM LOGIS.LSALDO S " // non serviva
						// fare select *
						+ "LEFT OUTER JOIN LOGIS.LUBICAZIONE U " + "ON S.COD_UBICAZIONE = U.CODICE AND "
						+ "S.COD_SOCIETA = U.COD_MAG_FISICO " + "WHERE S.COD_ARTICOLO='" + saldo.getCodiceArticolo()
						+ "' " + "AND S.COD_MAG_FISICO='" + saldo.getCodiceMagFisico() + "' "
						+ "AND S.COD_MAG_LOGICO = '" + missione.getCodiceMagLogico() + "' " + "AND S.COD_SOCIETA = '"
						+ Azienda.getAziendaCorrente() + "' "
						+ "AND U.COD_ZONA_PRELIEVO NOT IN('SPEDIZIONE','RICEVIMENTO') ";
				try {
					cs = new CachedStatement(select);
					rs = cs.executeQuery();
					while (rs.next()) {
						String ubicazione = rs.getString("COD_UBICAZIONE") != null
								? rs.getString("COD_UBICAZIONE").trim()
								: "";
						String lotto = rs.getString("LOTTO01") != null ? rs.getString("LOTTO01").trim() : "";
						String qta = rs.getString("QTA1") != null ? rs.getString("QTA1").trim() : "";
						String udc = rs.getString("COD_MAPPA_UDC") != null ? rs.getString("COD_MAPPA_UDC").trim() : "-";
						YOggettinoGiacenze ogg = new YOggettinoGiacenze();
						ogg.setLotto(lotto);
						ogg.setQta(qta);
						ogg.setUbicazione(ubicazione);
						ogg.setUdc(udc);
						giacenze.add(ogg);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					try {
						if (rs != null) {
							rs.close();
						}
						if (cs != null) {
							cs.free();
						}
					} catch (Throwable t) {
						t.printStackTrace(Trace.excStream);
					}
				}
			}

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (cs != null) {
					cs.free();
				}
			} catch (Throwable t) {
				t.printStackTrace(Trace.excStream);
			}
		}
		return giacenze;
	}

	// 70849 prendo la descrizione dalla vista SOFTRE.Y_LMISSIONE_DESCR_V01
	protected String getDescrizioneEstesa() {
		String descrizione = "";
		Missione missione = esecuzioneMissioni.getMissInConferma();
		if (missione != null) {
			ResultSet rs = null;
			CachedStatement cs = null;
			try {
				String select = "SELECT DESCRIZIONE FROM SOFTRE.Y_LMISSIONE_DESCR_V01" + " WHERE COD_MISSIONE='"
						+ missione.getCodice() + "' AND " + "COD_MAG_FISICO = '" + missione.getCodiceMagFisico() + "'";
				cs = new CachedStatement(select);
				rs = cs.executeQuery();
				if (rs.next()) {
					descrizione = rs.getString("DESCRIZIONE").trim();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
					if (cs != null) {
						cs.free();
					}
				} catch (Throwable t) {
					t.printStackTrace(Trace.excStream);
				}
			}
		}
		return descrizione;
	}

	protected void paginaCancella() {
		int dim = esecuzioneMissioni.getElencoMissioni().size();
		if (esecuzioneMissioni.getNumMissConfermate() < dim) {
			if (conferma(getTForm(formPaginaCancella))) { // Chiedi conferma.
				if (cancellazione()) { // Cancella ...
					try {
						ConnectionManager.rollback();
						messaggio(true, ResourceLoader.getString(RESOURCES, "operazioneKo") + " "
								+ ResourceLoader.getString(RESOURCES, "vediPC"));
					} catch (Exception ex) {
						ex.printStackTrace(Trace.excStream);
					}
					pagina = MENU;
				} else {
					try {
						ConnectionManager.commit();
					} catch (SQLException ex) {
						ex.printStackTrace(Trace.excStream);
						pagina = CANCELLA;
						return;
					}
					pagina = ESITO; // ... ed esce.
				}
			} else { // Non cancella e torna alle missioni.
				flagIndietro = false;
				esecuzioneMissioni.setPosMiss(-1);
				cercaMissioneAvanti();
				if (DAELENCO == 22) {
					pagina = ELENCO_MISSIONI;
				} else
					pagina = VISUALIZZA_MISSIONE;

			}
		} else {
			pagina = ESITO; // Passerebbe direttamente alla pagina conclusiva ma
			ErrorMessage err = trattaLista(); // riprova a fare il prelievo rilanciando il processo.
			dim = 0;
			if (err == null)
				dim = esecuzioneMissioni.getElencoMissioni().size();
			if (dim > 0)
				pagina = VISUALIZZA_MISSIONE;
		}
	}

	@SuppressWarnings("rawtypes")
	protected int getMissioniAttive() {
		int numeroAttive = 0;
		Iterator listaMissioni = esecuzioneMissioni.getElencoMissioni().iterator();
		while (listaMissioni.hasNext()) {
			Missione missione = (Missione) listaMissioni.next();
			try {
				missione.retrieve(); // 71176
			} catch (SQLException e) {
			}
			if (missione.getStatoMissione() == Missione.ESECUZIONE) {
				numeroAttive++;
			}
		}
		return numeroAttive;
	}

	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	protected Vector spezzaMissionePers(Missione m, BigDecimal qtaConfermata, BigDecimal qta2Confermata,
			BigDecimal qta3Confermata) throws Exception {
		Vector errori = new Vector();
		Missione missFiglia = (Missione) Factory.createObject(Missione.class); // Missione figlia
		missFiglia.setCodiceMagFisico(m.getCodiceMagFisico());
		missFiglia.setCodiceOperazioneMovimento(m.getCodiceOperazioneMovimento());
		missFiglia.setTipoMissione(m.getTipoMissione());
		missFiglia.setChiaveRigaLista(m.getChiaveRigaLista());
		missFiglia.setArticolo(m.getArticolo());
		missFiglia.setSaldo(m.getSaldo());
		missFiglia.setChiavePostazione(m.getChiavePostazione());
		missFiglia.setChiaveOperatore(m.getChiaveOperatore());
		for (int i = 0; i < m.getLotto().length; i++)
			missFiglia.setLotto(m.getLotto(i), i);
		missFiglia.setDataScadenza(m.getDataScadenza());
		missFiglia.setUmBase(m.getUmBase());
		missFiglia.setUmBase1(m.getUmBase1());
		missFiglia.setUmBase2(m.getUmBase2());
		missFiglia.setSegno(m.getSegno());
		missFiglia.setCodiceStatistico(m.getCodiceStatistico());
		missFiglia.setConfezione(m.getConfezione());
		missFiglia.setMagLogico(m.getMagLogico());
		missFiglia.setSocieta(m.getSocieta());
		missFiglia.setPianificazioneLista(m.getPianificazioneLista());
		missFiglia.setAllestimentoDoc(m.getAllestimentoDoc());
		missFiglia.setDestinatario(m.getDestinatario());
		missFiglia.setMagLogicoInv(m.getMagLogicoInv());
		missFiglia.setCodiceUdm(m.getCodiceUdm());
		missFiglia.setCodiceUdmInverso(m.getCodiceUdmInverso());
		missFiglia.setScomparto(m.getScomparto());
		missFiglia.setSottoUbicazione(m.getSottoUbicazione());
		missFiglia.setMappaUdc(m.getMappaUdc());
		missFiglia.setChiaveUbicazione(m.getChiaveUbicazione());
		missFiglia.setChiaveUbicazioneInv(m.getChiaveUbicazioneInv());
		missFiglia.setQta1Richiesta(qtaConfermata);
		missFiglia.setQta2Richiesta(qta2Confermata);
		missFiglia.setQta3Richiesta(qta3Confermata);
		missFiglia.setParzializzazione(m.getParzializzazione());
		missFiglia.setNumUdm(m.getNumUdm());
		missFiglia.setSvuotamentoUdc(m.getSvuotamentoUdc());
		missFiglia.setNumEsecuzione(m.getNumEsecuzione());
		missFiglia.setSequenza(m.getSequenza());
		missFiglia.setChiaveAreaLavoro(m.getChiaveAreaLavoro());
		missFiglia.setTipoMacchina(m.getTipoMacchina());
		missFiglia.setTsEsecuzione(m.getTsEsecuzione());
		missFiglia.setStatoMissione(m.getStatoMissione());
		missFiglia.setNote(ResourceLoader.getString(RESOURCES, "txt0004"));
		try {
			Numeratore mioNum = null; // Genera codice della missione.
			missFiglia.setCodice(mioNum.getProgr(m.getMagFisico().getTipoNumeratoreMissione().getCodice()));
		} catch (SQLException ex) {
			ex.printStackTrace(Trace.excStream);
			errori.addElement(new ErrorMessage("LOGIST0117", ex.getMessage()));
			return errori;
		} catch (NumeratoreMaxProgrException ex) {
			ex.printStackTrace(Trace.excStream);
			errori.addElement(new ErrorMessage("LOGIST0117", ex.getMessage()));
			return errori;
		} catch (NumeratoreNotFoundException ex) {
			ex.printStackTrace(Trace.excStream);
			errori.addElement(new ErrorMessage("LOGIST0117", ex.getMessage()));
			return errori;
		} catch (InstantiationException ex) {
			ex.printStackTrace(Trace.excStream);
			errori.addElement(new ErrorMessage("LOGIST0117", ex.getMessage()));
			return errori;
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace(Trace.excStream);
			errori.addElement(new ErrorMessage("LOGIST0117", ex.getMessage()));
			return errori;
		} catch (IllegalAccessException ex) {
			ex.printStackTrace(Trace.excStream);
			errori.addElement(new ErrorMessage("LOGIST0117", ex.getMessage()));
			return errori;
		} catch (NumeratoreNotValidException ex) {
			ex.printStackTrace(Trace.excStream);
			errori.addElement(new ErrorMessage("LOGIST0117", ex.getMessage()));
			return errori;
		} catch (NumeratoreMaxException ex) {
			ex.printStackTrace(Trace.excStream);
			errori.addElement(new ErrorMessage("LOGIST0117", ex.getMessage()));
			return errori;
		}
		m.setQta1Richiesta(m.getQta1Richiesta().subtract(qtaConfermata));
		m.setQta2Richiesta(m.getQta2Richiesta().subtract(qta2Confermata));
		m.setQta3Richiesta(m.getQta3Richiesta().subtract(qta3Confermata));
		if (m.getQtaSaldo().compareTo(BigDecimal.ZERO) <= 0) // 70996 se la qta sul saldo è 0 o meno, vado ad assegnare
			// un altro saldo con giacenza (quello iniziale)
			m.setSaldo(saldoPreCatasta); // questa cosa succede se l'articolo è gestito a cataste
		int r = ErrorCodes.NO_ROWS_UPDATED;
		try {
			r = m.save(); // Commit fatta dalla conferma missione figlia.
		} catch (SQLException e) {
			e.printStackTrace(Trace.excStream);
			errori.addElement(new ErrorMessage("LOGIST0118"));
			return errori;
		}
		if (r < ErrorCodes.NO_ROWS_UPDATED) { // Errore nella save della miss. madre.
			try {
				ConnectionManager.rollback();
			} catch (SQLException e) {
				e.printStackTrace(Trace.excStream);
				errori.addElement(new ErrorMessage("LOGIST0118"));
				return errori;
			}
			errori.addElement(new ErrorMessage("LOGIST0118"));
			return errori;
		}
		errori = trattaMissione(missFiglia, qtaConfermata, qta2Confermata, qta3Confermata);
		if (errori.size() == 0) { // Sostiuisco la missione originaria con quella aggiornata.
			esecuzioneMissioni.getElencoMissioni().removeElementAt(itemPos);
			esecuzioneMissioni.getElencoMissioni().add(itemPos, m);
			missionePost = missFiglia; // 71195 tengo in pancia la missione per la gestione del peso variabile
		}
		return errori;
	}

	/**
	 * <h1>Controllo chiusura</h1> <br>
	 * Daniele Signoroni 31/07/2023 <br>
	 * <p>
	 * Prima stesura eseguita da Thomas Brescianini.<br>
	 * All'interno abbiamo varie azioni:<br>
	 * <list>
	 * <li>Gestione KGM : {@link #gestioneKGM(TestataLista)}</li>
	 * <li>Gestione spese IMB : {@link #gestioneSpeseImballo(TestataLista)}</li>
	 * <li>Stampa DDT : {@link #stampaDocumentoVendita()}</li>
	 * <li>Stampa UDS : {@link #stampaUds()}</li>
	 * <li>Gestione peso variabile : {@link #gestionePesoVariabile()}</li> </list>
	 * </p>
	 */
	protected void controlloChiusura() {
		try {
			if (esecuzioneMissioni.getElencoTestate().size() > 0) {
				TestataLista tlNew = (TestataLista) esecuzioneMissioni.getElencoTestate().get(0);
				boolean daDDT = false; // 70960
				if (tlNew.getStatoLista() == TestataLista.CHIUSO || numMisAttPreCanc == 0) {
					String gestKGM = ParametroPsn.getValoreParametroPsn("YGestioneKGM", "Attivazione gestione KGM");
					if (gestKGM != null && gestKGM.equals("Y")) {
						gestioneKGM(tlNew);
					}
					String attivaPersonalizzazione = ""; // serve per capire se è attiva o meno la personalizzazione
					// sull'azienda
					if (Azienda.getAziendaCorrente().equals("001"))
						attivaPersonalizzazione = ParametroPsn.getValoreParametroPsn("YAddImballoDefault001",
								"SpeseImballo001");
					else if (Azienda.getAziendaCorrente().equals("002"))
						attivaPersonalizzazione = ParametroPsn.getValoreParametroPsn("YAddImballoDefault002",
								"SpeseImballo002");
					if (attivaPersonalizzazione.equals("Y")) {
						gestioneSpeseImballo(tlNew);
					}
					gestionePesoVariabile(tlNew); // 71195
				}
				if (esecuzioneMissioni.getNumUdsAperte() == 0 && esecuzioneMissioni.getElencoUds().size() > 0
						&& numMisAttPreCanc == 0) {
					String parametro = ParametroPsn.getValoreParametroPsn("YStampaDDT", "StampaDDT");
					if (parametro.equals("S")) {
						if (!presentiPerListaPosizioniAperte(tlNew.getCodice())) {
							// 71268 Inizio
							char tipoDoc = tlNew.getCodice().substring(0, 1).charAt(0);
							switch (tipoDoc) {
							case IntegrazioneThipLogis.VENDITA:
								if (getDocumentoVendita() != null
										&& ((YClienteVendita) getDocumentoVendita().getCliente())
												.isStampaAutomTerminalinoDDT()) { // 71186
									boolean stampa = conferma(false, "Proseguire con la stampa del DDT?");
									if (stampa) {
										YReportDdtBollaBatch ddt = stampaDocumentoVendita();
										if (ddt.save() >= 0) {
											ConnectionManager.commit();
											BatchService.submitJob(ddt.getBatchJob());
											daDDT = true; // 70960
										}
									}
								}
								break;
							case IntegrazioneThipLogis.ACQUISTO:
								YDocumentoAcquisto docAcq = (YDocumentoAcquisto) getDocumentoAcquisto();
								if (docAcq != null
										&& ((YFornitoreAcquisto) docAcq.getFornitore()).isStampaAutomTerminalinoDDT()
										&& docAcq.isDocumentoDiContoLavoro() // solo se e' C/LAV
										&& docAcq.getCausale().getAzioneMagazzino() == AzioneMagazzino.USCITA
										&& YStampaDdtAcqTerminalino.getCurrentPersDatiStampaDDTAcquistoTerminalino()
												.getAbilitata()) { // e solo se e' uscita
									boolean stampa = conferma(false, "Proseguire con la stampa del DDT?");
									if (stampa) {
										YReportDdtBollaACBatch ddt = stampaDocumentoAcquisto(docAcq);
										if (ddt.save() >= 0) {
											ConnectionManager.commit();
											BatchService.submitJob(ddt.getBatchJob());
											daDDT = true; // 70960
										}
									}
								}
								break;
							default:
								break;
							}
							// 71268 Fine
						}
					}
				}
				boolean stampa = false;
				if (!daDDT)
					stampa = conferma(false, "Proseguire con la stampa dell' UDS?");
				if (stampa) {
					YStampaUdsBatch stampaUds = stampaUds();
					Thread.sleep(1000);
					if (stampaUds.save() >= 0) {
						ConnectionManager.commit();
						BatchService.submitJob(stampaUds.getBatchJob());
					}
				}
				// 70915 fine
			}
		} catch (SQLException ex) {
			ex.printStackTrace(Trace.excStream);
			writeLog("Errore nel metodo controlloChiusura() di YProcessaListeCompattoRF");
		} catch (ConnectionException e) {
			e.printStackTrace();
			writeLog("Errore nel metodo controlloChiusura() di YProcessaListeCompattoRF");
		} catch (Exception e) {
			e.printStackTrace();
			writeLog("Errore nel metodo controlloChiusura() di YProcessaListeCompattoRF");
		}
	}

	/**
	 * <h1>Stampa documento acquisto - terminalino</h1> <br>
	 * Daniele Signoroni 20/10/2023 <br>
	 * <p>
	 * <b>71268 DSSOF3 20/10/2023</b><br>
	 * Stampa documento acquisto quando la lista si chiude.<br>
	 * Il report lo prendo da {@link YStampaDdtAcqTerminalino#getRReport()} <br>
	 * Il resto e' speculare alla stampa documento vendita.
	 * </p>
	 * 
	 * @param docAcq
	 * @return
	 * @throws Exception
	 */
	protected static YReportDdtBollaACBatch stampaDocumentoAcquisto(YDocumentoAcquisto docAcq) throws Exception {
		YReportDdtBollaACBatch ddt = (YReportDdtBollaACBatch) Factory.createObject(YReportDdtBollaACBatch.class);
		BatchOptions batchOptions = (BatchOptions) Factory.createObject(BatchOptions.class);
		batchOptions.initDefaultValues(YReportDdtBollaACBatch.class, "STAMPA_BOLLE", "RUN");
		ddt.setBatchJob(batchOptions.getBatchJob());
		ddt.setScheduledJob(batchOptions.getScheduledJob());
		ddt.setReportId(YStampaDdtAcqTerminalino.getCurrentPersDatiStampaDDTAcquistoTerminalino().getRReport());
		ddt.setExecutePrint(true);
		ddt.setNumeroDocumento(docAcq.getKey());
		ddt.getBatchJob().setDescription("Stampa DDT da terminalino");
		ddt.getBatchJob().setUserDescription("Stampa DDT da terminalino");
		ddt.getBatchJob().setBatchQueueId("DefQueue");
		ddt.setDocDgtEnabled(true);
		ddt.setSSDEnabled(true);
		ddt.setVettore1(docAcq.getVettore1());
		ddt.setCausaleTrasporto(docAcq.getCausaleTrasporto());
		ddt.setLocalitaDestinatario(docAcq.getLocalitaDestinatario());
		ddt.setModalitaSpedizione(docAcq.getModalitaSpedizione());
		ddt.setAspettoEsteriore(docAcq.getAspettoEsteriore());
		ddt.setVolume(docAcq.getVolume());
		ddt.setDescrModalitaSpedizione(docAcq.getDescrModalitaSpedizione());
		ddt.setPesoNetto(docAcq.getPesoNetto());
		ddt.setPesoLordo(docAcq.getPesoLordo());
		ddt.setRicalcoloPesi(docAcq.isRicalcolaPesiEVolume());
		// ddt.setStampaInLingua(docAcq.isstamp());
		// 71076 DSSOF3 Inizio
		String descVet1 = docAcq.getDescrVettore1() != null ? docAcq.getDescrVettore1() : "";
		if (!descVet1.isEmpty()) {
			if (descVet1.contains("\n") && !descVet1.contains("\r\n")) {
				descVet1 = descVet1.replace("\n", "\r\n");
			}
		}
		// 71076 DSSOF3 Fine
		ddt.setDescrVettore3(docAcq.getDescrVettore3());
		ddt.setDescrVettore2(docAcq.getDescrVettore2());
		ddt.setDescrVettore1(descVet1);
		ddt.setTipoDestinatario(docAcq.getTipoDestinatario());
		// ddt.setIdSequenzaInd(docAcq.getIdSequenzaInd());//70930
		ddt.setIdSequenzaIndDenFornitore(docAcq.getIdSequenzaIndDenFornitore());
		ddt.setIdSequenzaIndCliForDest(docAcq.getIdSequenzaIndCliForDest());
		ddt.setDescrModalitaConsegna(docAcq.getDescrModalitaConsegna());
		ddt.setDataInizioTrasporto(docAcq.getDataInizioTrasporto());
		ddt.setNazione(docAcq.getNazione());
		ddt.setCAPDestinatario(docAcq.getCAPDestinatario());
		ddt.setClienteDestinatario(docAcq.getClienteDestinatario());
		ddt.setRagioneSocaleDest(docAcq.getRagioneSocaleDest());
		ddt.setNumeroColli(docAcq.getNumeroColli());
		ddt.setModalitaConsegna(docAcq.getModalitaConsegna());
		ddt.setProvincia(docAcq.getProvincia());
		ddt.setIndirizzoDestinatario(docAcq.getIndirizzoDestinatario());// 71034
		ddt.setIdAnagrafico(docAcq.getIdAnagrafico());
		ddt.setOraInizioTrasporto(docAcq.getOraInizioTrasporto());
		ddt.setVettore3(docAcq.getVettore3());
		ddt.setVettore2(docAcq.getVettore2());// 70930
		ddt.setTargaMezzo(docAcq.getTargaMezzo());
		ddt.setDaTerminalino(true);
		// 70967 ini, mi passo la chiave del doc ven
		ddt.getBatchJob().setLog(docAcq.getKey());
		// 70967 fin
		// 71002 Inizio
		if (Azienda.getAziendaCorrente().equals("001")) {// se valvorobica
			boolean isCopie2 = false;
			String idModSpe = docAcq.getIdModSpedizione();
			if (idModSpe != null) {
				if (idModSpe.equals("MI") || idModSpe.equals("DE") || idModSpe.equals("AGE"))
					isCopie2 = true;
			}
			if (docAcq.getIdVettore1() != null && docAcq.getIdVettore1().equals("003989"))
				isCopie2 = true;
			if (docAcq.getIdVettore1() != null && docAcq.getIdVettore1().equals("000990"))// 71089
				isCopie2 = true;
			if (isCopie2)
				ddt.setCopyNumber(3);
		} else if (Azienda.getAziendaCorrente().equals("002")) {// 71089
			if (docAcq.getIdCliente().equals("000308"))
				ddt.setCopyNumber(3);
		}
		// 71002 Fine
		return ddt;
	}

	/**
	 * <h1>Gestione peso variabile</h1> <br>
	 * Thomas Brescianini 08/08/2023 <br>
	 * <p>
	 * Il metodo va a controllare che la lista in chiusura abbia al suo interno
	 * almeno una missione che ha avuto una gestione del peso variabile. Se contiene
	 * almeno una riga, allora va a modificare le qta sul documento di acquisto o di
	 * vendita. Vado a settare la qtaInUmVen/Acq come quella della qta variata, però
	 * si mantiente uguale la qtaInUmPrmMag. Sul salvataggio andranno poi a
	 * modificarsi anche le qta del lotto
	 * </p>
	 */
	@SuppressWarnings("rawtypes")
	protected void gestionePesoVariabile(TestataLista tlNew) {
		List<YOggettinoPesoVariabile> elencoDoc = getElencoDocumenti(tlNew);
		try {
			Iterator iter = elencoDoc.iterator();
			while (iter.hasNext()) {
				YOggettinoPesoVariabile ogg = (YOggettinoPesoVariabile) iter.next();
				if (ogg.getTipoDoc().equals("V")) {
					DocumentoVenRigaPrm docRig = (DocumentoVenRigaPrm) DocumentoVenRigaPrm
							.elementWithKey(DocumentoVenRigaPrm.class, ogg.getKey(), PersistentObject.NO_LOCK);
					if (docRig != null) {
						BigDecimal qta = new BigDecimal(ogg.getQta());
						docRig.setRicalcoloQtaFattoreConv(false);
						docRig.setQtaInUMVen(qta);
						docRig.setQtaInUMPrm(docRig.getQtaAttesaEvasione().getQuantitaInUMPrm());
						if (docRig.save() >= 0) {
							ConnectionManager.commit();
							modificaGestioneLottiVendite(docRig, tlNew);
						}
					}
				} else if (ogg.getTipoDoc().equals("A")) {
					DocumentoAcqRigaPrm docRig = (DocumentoAcqRigaPrm) DocumentoAcqRigaPrm
							.elementWithKey(DocumentoAcqRigaPrm.class, ogg.getKey(), PersistentObject.NO_LOCK);
					if (docRig != null) {
						BigDecimal qta = new BigDecimal(ogg.getQta());
						docRig.setRicalcoloQtaFattoreConv(false);
						docRig.setQtaInUMAcq(qta);
						docRig.setQtaInUMPrm(docRig.getQtaAttesaEvasione().getQuantitaInUMPrm());
						if (docRig.save() >= 0) {
							ConnectionManager.commit();
							modificaGestioneLottiAcquisti(docRig, tlNew);
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	protected void modificaGestioneLottiVendite(DocumentoVenRigaPrm docRig, TestataLista tlNew) {
		ResultSet rs = null;
		CachedStatement cs = null;
		List<YOggettinoLottoPeso> elenco = new ArrayList<YOggettinoLottoPeso>();
		BigDecimal qtaAttesa = BigDecimal.ZERO;
		try {
			String select = "SELECT ID_LOTTO,QTA_UM_VARIABILE FROM SOFTRE.Y_QTA_UM_VAR_LOT_V01 WHERE ID_ANNO_DOC = '"
					+ docRig.getAnnoDocumento() + "' AND ID_NUMERO_DOC = '" + docRig.getNumeroDocumento() + "' "
					+ "AND ID_RIGA_DOC = '" + docRig.getNumeroRigaDocumento() + "' AND COD_LISTA = '"
					+ tlNew.getCodice() + "' AND COD_SOCIETA = '" + Azienda.getAziendaCorrente() + "'";
			cs = new CachedStatement(select);
			rs = cs.executeQuery();
			boolean ok = true;
			while (rs.next()) {
				String idLotto = rs.getString("ID_LOTTO") != null ? rs.getString("ID_LOTTO").trim() : "";
				String qta = rs.getString("QTA_UM_VARIABILE") != null ? rs.getString("QTA_UM_VARIABILE").trim() : "";
				YOggettinoLottoPeso ogg = new YOggettinoLottoPeso();
				ogg.setIdLotto(idLotto);
				ogg.setQta(qta);
				elenco.add(ogg);
			}
			rs.close();
			Iterator iter = elenco.iterator();
			while (iter.hasNext()) {
				YOggettinoLottoPeso ogg = (YOggettinoLottoPeso) iter.next();
				String key = KeyHelper.buildObjectKey(new String[] { Azienda.getAziendaCorrente(),
						docRig.getAnnoDocumento(), docRig.getNumeroDocumento(),
						docRig.getNumeroRigaDocumento().toString(), docRig.getIdArticolo(), ogg.getIdLotto() });
				DocumentoVenRigaLottoPrm docRigLot = (DocumentoVenRigaLottoPrm) DocumentoVenRigaLottoPrm
						.elementWithKey(DocumentoVenRigaLottoPrm.class, key, PersistentObject.NO_LOCK);
				if (docRigLot != null) {
					docRigLot.getQtaAttesaEvasione().setQuantitaInUMRif(new BigDecimal(ogg.getQta()));
					if (docRigLot.save() < 0)
						ok = false;
					qtaAttesa = qtaAttesa.add(new BigDecimal(ogg.getQta()));
				}
			}
			if (qtaAttesa.compareTo(docRig.getQtaInUMVen()) == 0) {
				Iterator iterator = docRig.getRigheLotto().iterator();
				while (iterator.hasNext()) {
					DocumentoVenRigaLottoPrm docLot = (DocumentoVenRigaLottoPrm) iterator.next();
					if (docLot.getIdLotto().equals(Lotto.LOTTO_DUMMY)) {
						if (docLot.delete() < 0)
							ok = false;
						break;
					}
				}
			}
			if (ok)
				ConnectionManager.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	protected void modificaGestioneLottiAcquisti(DocumentoAcqRigaPrm docRig, TestataLista tlNew) {
		ResultSet rs = null;
		CachedStatement cs = null;
		List<YOggettinoLottoPeso> elenco = new ArrayList<YOggettinoLottoPeso>();
		BigDecimal qtaAttesa = BigDecimal.ZERO;
		try {
			String select = "SELECT ID_LOTTO,QTA_UM_VARIABILE FROM SOFTRE.Y_QTA_UM_VAR_LOT_V01 WHERE ID_ANNO_DOC = '"
					+ docRig.getAnnoDocumento() + "' AND ID_NUMERO_DOC = '" + docRig.getNumeroDocumento() + "' "
					+ "AND ID_RIGA_DOC = '" + docRig.getNumeroRigaDocumento() + "' AND COD_LISTA = '"
					+ tlNew.getCodice() + "' AND COD_SOCIETA = '" + Azienda.getAziendaCorrente() + "'";
			cs = new CachedStatement(select);
			rs = cs.executeQuery();
			boolean ok = true;
			while (rs.next()) {
				String idLotto = rs.getString("ID_LOTTO") != null ? rs.getString("ID_LOTTO").trim() : "";
				String qta = rs.getString("QTA_UM_VARIABILE") != null ? rs.getString("QTA_UM_VARIABILE").trim() : "";
				YOggettinoLottoPeso ogg = new YOggettinoLottoPeso();
				ogg.setIdLotto(idLotto);
				ogg.setQta(qta);
				elenco.add(ogg);
			}
			rs.close();
			Iterator iter = elenco.iterator();
			while (iter.hasNext()) {
				YOggettinoLottoPeso ogg = (YOggettinoLottoPeso) iter.next();
				String key = KeyHelper.buildObjectKey(new String[] { Azienda.getAziendaCorrente(),
						docRig.getAnnoDocumento(), docRig.getNumeroDocumento(),
						docRig.getNumeroRigaDocumento().toString(), docRig.getIdArticolo(), ogg.getIdLotto() });
				DocumentoAcqRigaLottoPrm docRigLot = (DocumentoAcqRigaLottoPrm) DocumentoAcqRigaLottoPrm
						.elementWithKey(DocumentoAcqRigaLottoPrm.class, key, PersistentObject.NO_LOCK);
				if (docRigLot != null) {
					docRigLot.getQtaAttesaEvasione().setQuantitaInUMRif(new BigDecimal(ogg.getQta()));
					if (docRigLot.save() < 0)
						ok = false;
					qtaAttesa = qtaAttesa.add(new BigDecimal(ogg.getQta()));
				}
			}
			if (qtaAttesa.compareTo(docRig.getQtaInUMAcq()) == 0) {
				Iterator iterator = docRig.getRigheLotto().iterator();
				while (iterator.hasNext()) {
					DocumentoAcqRigaLottoPrm docLot = (DocumentoAcqRigaLottoPrm) iterator.next();
					if (docLot.getIdLotto().equals(Lotto.LOTTO_DUMMY)) {
						if (docLot.delete() < 0)
							ok = false;
						break;
					}
				}
			}
			if (ok)
				ConnectionManager.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	protected List<YOggettinoPesoVariabile> getElencoDocumenti(TestataLista tlNew) {
		ResultSet rs = null;
		String stmt = null;
		CachedStatement cs = null;
		List<YOggettinoPesoVariabile> elencoDoc = new ArrayList<YOggettinoPesoVariabile>();
		try {
			stmt = "SELECT QTA_UM_VARIABILE,TIPO_DOC,ID_NUMERO_DOC,ID_RIGA_DOC " + "FROM SOFTRE.Y_QTA_UM_VAR_RIG_V01 "
					+ "WHERE COD_LISTA LIKE '%" + tlNew.getCodice() + "%' AND ID_AZIENDA = '"
					+ Azienda.getAziendaCorrente() + "' AND QTA_UM_VARIABILE != '0'";
			cs = new CachedStatement(stmt);
			rs = cs.executeQuery();
			while (rs.next()) {
				YOggettinoPesoVariabile ogg = new YOggettinoPesoVariabile();
				String qta = rs.getString("QTA_UM_VARIABILE") != null ? rs.getString("QTA_UM_VARIABILE").trim() : "";
				String tipoDoc = rs.getString("TIPO_DOC") != null ? rs.getString("TIPO_DOC").trim() : "";
				String annoDoc = rs.getString("ID_ANNO_DOC") != null ? rs.getString("ID_ANNO_DOC").trim() : "";
				String numeroDoc = rs.getString("ID_NUMERO_DOC") != null ? rs.getString("ID_NUMERO_DOC").trim() : "";
				String rigaDoc = rs.getString("ID_RIGA_DOC") != null ? rs.getString("ID_RIGA_DOC").trim() : "";
				String key = KeyHelper
						.buildObjectKey(new String[] { Azienda.getAziendaCorrente(), annoDoc, numeroDoc, rigaDoc });
				ogg.setKey(key);
				ogg.setTipoDoc(tipoDoc);
				ogg.setQta(qta);
				elencoDoc.add(ogg);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		return elencoDoc;
	}

	/**
	 * <h1>Verifica presenza posizioni aperte per una determinata lista</h1> <br>
	 * Daniele Signoroni 31/07/2023 <br>
	 * <p>
	 * Tramite il codice lista, vado sulla vista SOFTRE.Y_LOGIS_LISTE_POS_APERTE_V01
	 * creata da Arturo, per verificare se, per una determinata lista, sono presenti
	 * posizioni aperte.<br>
	 * </p>
	 * 
	 * @param codiceLista
	 * @return true se sono presenti posizioni aperte, false altrimenti
	 */
	protected static boolean presentiPerListaPosizioniAperte(String codiceLista) {
		ResultSet rs = null;
		String stmt = null;
		CachedStatement cs = null;
		try {
			stmt = "SELECT * " + "FROM SOFTRE.Y_LOGIS_LISTE_POS_APERTE_V01 " + "WHERE COD_LISTA LIKE '%" + codiceLista
					+ "%' AND ID_AZIENDA = '" + Azienda.getAziendaCorrente() + "'";
			cs = new CachedStatement(stmt);
			rs = cs.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * <h1>Gestione creazione riga spesa di imballo</h1> <br>
	 * Daniele Signoroni 31/07/2023 <br>
	 * <p>
	 * Spostata la creazione in un metodo per ordinare il codice.<br>
	 * Il metodo fa la throws di tutto quello che potrebbe accadere, mi voglio
	 * fermare anche in singolo caso di exc nei catch contenuti qui -->
	 * {@linkplain #controlloChiusura()}.
	 * </p>
	 * 
	 * @param tlNew
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 */
	@SuppressWarnings({ "rawtypes", "static-access", "unchecked" })
	protected void gestioneSpeseImballo(TestataLista tlNew)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
		String causale = ParametroPsn.getValoreParametroPsn("YcauRigSpeseImballo", "CausaleSpesaImballo"); // causale di
		// riga
		// della
		// spesa di
		// imballo
		BigDecimal totale = BigDecimal.ZERO;
		DocumentoVendita docVen = getDocumentoVendita();
		if (docVen != null) {
			boolean checkCausali = checkCausali(docVen, causale); // controllo che la causale di riga della spesa di
			// imballo sia compatibile con quella di testata
			// (uso tabella THIP.CAU_DOCVEN_CRA)
			if (checkCausali) {
				YClienteVendita cliVen = (YClienteVendita) docVen.getCliente();
				if (!cliVen.isEscludiSpeseImb()) {
					if (((YDocumentoVendita) docVen).isCalcoloRigheSpeseTrasportoAbilitato()) {
						Vector elencoUds = tlNew.getElencoUds(tlNew);
						for (int i = 0; i < elencoUds.size(); i++) {
							String codUds = (String) elencoUds.get(i);
							TestataUds udsTes = (TestataUds) TestataUds.elementWithKey(TestataUds.class, codUds, 0);
							if (udsTes != null) {
								TipoUds tipoUds = udsTes.getTipoUds();
								if (tipoUds != null) {
									Articolo articolo = getArticolo(tipoUds); // prendo l'articolo collegato al tipoUDS
									// in THIPPERS.YUDSARTICOLO
									if (articolo != null) {
										CondizioniDiVendita cdv = recuperaCondizioniVendita(articolo,
												(YDocumentoVendita) docVen, (YClienteVendita) docVen.getCliente());
										if (cdv != null) {
											BigDecimal price = cdv.getPrezzo();
											if (price != null && price.compareTo(BigDecimal.ZERO) > 0)
												totale = totale.add(price);
										}
									}
								}
							}
						}
					}
				}
				String where = "ID_AZIENDA = '" + Azienda.getAziendaCorrente() + "' AND ID_ANNO_DOC = '"
						+ docVen.getAnnoDocumento() + "'" + " AND ID_NUMERO_DOC = '" + docVen.getNumeroDocumento()
						+ "' " + " AND R_CAU_RIG_DOCVEN = '" + causale + "'";
				List<YDocumentoVenRigaPrm> lst = YDocumentoVenRigaPrm.retrieveList(YDocumentoVenRigaPrm.class, where,
						"", false);
				if (lst.size() > 0) {
					YDocumentoVenRigaPrm rigaSpesa = lst.get(0);
					if (rigaSpesa.delete() > 0) {
						ConnectionManager.commit();
					}
				}
				if (totale.compareTo(BigDecimal.ZERO) > 0) {
					YDocumentoVenRigaPrm docVenRig = creaRigaSpesaImballo(causale, (YDocumentoVendita) docVen, totale);
					if (docVenRig.save() >= 0) {
						ConnectionManager.commit();
					}
				}
			}
		}
	}

	/**
	 * <h1>Ricerca Condizioni di Vendita</h1> <br>
	 * Daniele Signoroni 31/07/2023 <br>
	 * <p>
	 * Spostata la ricerca in metodo nuovo.<br>
	 * Il metodo fa la throws di tutto quello che potrebbe accadere, mi voglio
	 * fermare anche in singolo caso di exc nei catch contenuti qui -->
	 * {@linkplain #controlloChiusura()}.
	 * </p>
	 * 
	 * @param articolo
	 * @param docVen
	 * @param cliente
	 * @return
	 * @throws SQLException
	 */
	protected CondizioniDiVendita recuperaCondizioniVendita(Articolo articolo, YDocumentoVendita docVen,
			YClienteVendita cliente) throws SQLException {
		RicercaCondizioniDiVendita rcdv = new RicercaCondizioniDiVendita();
		CondizioniDiVendita cdv = null;
		cdv = rcdv.ricercaCondizioniDiVendita( // recupero le condizioni di vendita per avere il prezzo del contenitore
				Azienda.getAziendaCorrente(), // String idAzienda
				docVen.getListinoPrezzi(), // ListinoVendita listino 70874 DSSOF3 Prendo il listino dalla testata.
				cliente, // ClienteVendita cliente
				articolo, // Articolo articolo
				(UnitaMisura) UnitaMisura.elementWithKey(UnitaMisura.class,
						KeyHelper.buildObjectKey(new String[] { Azienda.getAziendaCorrente(), "nr" }), 0), // UnitaMisura
				// unita
				BigDecimal.ONE, // BigDecimal quantita
				null, // BigDecimal importo
				null, // ModalitaPagamento modalita
				TimeUtils.getCurrentDate(), // java.sql.Date dataValidita
				null, // Agente agente
				null, // Agente subagente
				null, // UnitaMisura unitaMag
				null, // BigDecimal quantitaMag
				(Valuta) Valuta.elementWithKey(Valuta.class, "EUR", 0), // Valuta valuta
				null, // UnitaMisura umSecMag
				null);
		return cdv;
	}

	/**
	 * <h1>Crea riga spesa di imballo</h1> <br>
	 * Daniele Signoroni 31/07/2023 <br>
	 * <p>
	 * Creazione di una riga spesa imballo.<br>
	 * Il metodo fa la throws di tutto quello che potrebbe accadere, mi voglio
	 * fermare anche in singolo caso di exc nei catch contenuti qui -->
	 * {@linkplain #controlloChiusura()}.
	 * </p>
	 * 
	 * @param idCausale
	 * @param docVen
	 * @param importo
	 * @return
	 * @throws SQLException
	 */
	protected YDocumentoVenRigaPrm creaRigaSpesaImballo(String idCausale, YDocumentoVendita docVen, BigDecimal importo)
			throws SQLException {
		Articolo speseTrasporto = (Articolo) Articolo.elementWithKey(Articolo.class,
				KeyHelper.buildObjectKey(new String[] { Azienda.getAziendaCorrente(), "SPESE_IMB" }), 0);
		YDocumentoVenRigaPrm docVenRig = (YDocumentoVenRigaPrm) Factory.createObject(YDocumentoVenRigaPrm.class);
		docVenRig.setAnnoDocumento(docVen.getAnnoDocumento());
		docVenRig.setNumeroDocumento(docVen.getNumeroDocumento());
		docVenRig.setSequenzaRiga(9998);
		docVenRig.setIdCauRig(idCausale);
		docVenRig.setIdSpesa("IMB");
		docVenRig.setIdAssogIVA("22");
		docVenRig.setIdArticolo("SPESE_IMB");
		docVenRig.setTipoRiga(TipoRiga.SPESE_MOV_VALORE);
		docVenRig.setImportoPercentualeSpesa(importo);
		docVenRig.setTestata(docVen);
		docVenRig.setQtaInUMVen(new BigDecimal(1));
		docVenRig.setIdUMRif("nr");
		docVenRig.setDescrizioneArticolo(speseTrasporto.getDescrizioneArticoloNLS().getDescrizione());
		docVenRig.setDataConsegnaRichiesta(docVen.getDataConsegnaRichiesta());
		docVenRig.setStatoAvanzamento(DocumentoVenRigaPrm.DEFINITIVO);
		return docVenRig;
	}

	/**
	 * <h1>Inizializzazione batch {@link YStampaUdsBatch}</h1> <br>
	 * Daniele Signoroni 31/07/2023 <br>
	 * <p>
	 * Il metodo fa la throws di tutto quello che potrebbe accadere, mi voglio
	 * fermare anche in singolo caso di exc nei catch contenuti qui -->
	 * {@linkplain #controlloChiusura()}.
	 * </p>
	 * 
	 * @return
	 * @throws Exception
	 */
	protected YStampaUdsBatch stampaUds() throws Exception {
		YStampaUdsBatch stampaUds = (YStampaUdsBatch) Factory.createObject(YStampaUdsBatch.class);
		BatchOptions batchOptions = (BatchOptions) Factory.createObject(BatchOptions.class);
		batchOptions.initDefaultValues(YStampaUdsBatch.class, "YStampaUDS", "RUN");
		stampaUds.setBatchJob(batchOptions.getBatchJob());
		stampaUds.setScheduledJob(batchOptions.getScheduledJob());
		String report = "";
		if (Azienda.getAziendaCorrente().equals("001"))
			report = ParametroPsn.getValoreParametroPsn("YStampaUds001", "IdReport");
		else
			report = ParametroPsn.getValoreParametroPsn("YStampaUds002", "IdReport");
		stampaUds.setReportId(report);
		stampaUds.setExecutePrint(true);
		stampaUds.getBatchJob().setDescription("St. UDS da ddt term.ino");
		stampaUds.getBatchJob().setUserDescription("St. UDS ddt term.ino");
		stampaUds.getBatchJob().setBatchQueueId("DefQueue");
		stampaUds.setCodiceUds(codiceUdsInChiusura);
		return stampaUds;
	}

	/**
	 * <h1>Inizializzazione batch {@link YReportDdtBollaBatch}</h1> <br>
	 * Daniele Signoroni 31/07/2023 <br>
	 * <p>
	 * Il metodo fa la throws di tutto quello che potrebbe accadere, mi voglio
	 * fermare anche in singolo caso di exc nei catch contenuti qui -->
	 * {@linkplain #controlloChiusura()}.
	 * </p>
	 * 
	 * @return
	 * @throws Exception
	 */
	protected YReportDdtBollaBatch stampaDocumentoVendita() throws Exception {
		YReportDdtBollaBatch ddt = (YReportDdtBollaBatch) Factory.createObject(YReportDdtBollaBatch.class);
		BatchOptions batchOptions = (BatchOptions) Factory.createObject(BatchOptions.class);
		batchOptions.initDefaultValues(YReportDdtBollaBatch.class, "STAMPA_BOLLE", "RUN");
		ddt.setBatchJob(batchOptions.getBatchJob());
		ddt.setScheduledJob(batchOptions.getScheduledJob());
		String report = ""; // 70809 TBSOF3
		if (Azienda.getAziendaCorrente().equals("001"))
			report = ParametroPsn.getValoreParametroPsn("YStampaBolla001", "StampaBollaVA1");
		else if (Azienda.getAziendaCorrente().equals("002"))
			report = ParametroPsn.getValoreParametroPsn("YStampaBolla002", "StampaBollaVA2");
		ddt.setReportId(report);
		ddt.setExecutePrint(true);
		DocumentoVendita docVen = getDocumentoVendita();
		ddt.setNumeroDocumento(docVen.getKey());
		ddt.getBatchJob().setDescription("Stampa DDT da terminalino");
		ddt.getBatchJob().setUserDescription("Stampa DDT da terminalino");
		ddt.getBatchJob().setBatchQueueId("DefQueue");
		ddt.setDocDgtEnabled(true);
		ddt.setSSDEnabled(true);
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
		// 71076 DSSOF3 Inizio
		String descVet1 = docVen.getDescrVettore1() != null ? docVen.getDescrVettore1() : "";
		if (!descVet1.isEmpty()) {
			if (descVet1.contains("\n") && !descVet1.contains("\r\n")) {
				descVet1 = descVet1.replace("\n", "\r\n");
			}
		}
		// 71076 DSSOF3 Fine
		ddt.setDescrVettore3(docVen.getDescrVettore3());
		ddt.setDescrVettore2(docVen.getDescrVettore2());
		ddt.setDescrVettore1(descVet1);
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
		ddt.setDaTerminalino(true);
		ddt.setCodiceUds(codiceUdsInChiusura); // 70973
		// 70967 ini, mi passo la chiave del doc ven
		ddt.getBatchJob().setLog(docVen.getKey());
		// 70967 fin
		// 71002 Inizio
		if (Azienda.getAziendaCorrente().equals("001")) {// se valvorobica
			boolean isCopie2 = false;
			String idModSpe = docVen.getIdModSpedizione();
			if (idModSpe != null) {
				if (idModSpe.equals("MI") || idModSpe.equals("DE") || idModSpe.equals("AGE"))
					isCopie2 = true;
			}
			if (docVen.getIdVettore1() != null && docVen.getIdVettore1().equals("003989"))
				isCopie2 = true;
			if (docVen.getIdVettore1() != null && docVen.getIdVettore1().equals("000990"))// 71089
				isCopie2 = true;
			if (isCopie2)
				ddt.setCopyNumber(2);
		} else if (Azienda.getAziendaCorrente().equals("002")) {// 71089
			if (docVen.getIdCliente().equals("000308"))
				ddt.setCopyNumber(2);
		}
		// 71002 Fine
		return ddt;
	}

	public void writeLog(String log) {
		System.out.println(log);
	}

	protected Articolo getArticolo(TipoUds tipoUds) {
		Articolo articolo = null;
		ResultSet rs = null;
		CachedStatement cs = null;
		try {
			String select = "SELECT CODICE_ARTICOLO FROM THIPPERS.YUDS_ARTICOLO " + "WHERE ID_AZIENDA = '"
					+ Azienda.getAziendaCorrente() + "' AND TIPO_UDS = '" + tipoUds.getCodice() + "'";
			cs = new CachedStatement(select);
			rs = cs.executeQuery();
			String codiceArt = "";
			if (rs.next()) {
				codiceArt = rs.getString("CODICE_ARTICOLO").trim();
			}
			if (codiceArt != null) {
				String key = KeyHelper.buildObjectKey(new String[] { Azienda.getAziendaCorrente(), codiceArt });
				articolo = (Articolo) Articolo.elementWithKey(Articolo.class, key, 0);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Throwable t) {
				t.printStackTrace(Trace.excStream);
			}
		}
		return articolo;
	}

	protected boolean checkCausali(DocumentoVendita docVen, String causale) {
		boolean check = false;
		ResultSet rs = null;
		CachedStatement cs = null;
		try {
			String select = "SELECT * FROM THIP.CAU_DOCVEN_CRA " + "WHERE ID_AZIENDA = '" + Azienda.getAziendaCorrente()
					+ "' AND " + "ID_CAU_DOC_VEN = '" + docVen.getIdCau() + "' AND " + "ID_CAU_RIG_DOCVEN = '" + causale
					+ "'";
			cs = new CachedStatement(select);
			rs = cs.executeQuery();
			if (rs.next())
				check = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Throwable t) {
				t.printStackTrace(Trace.excStream);
			}
		}
		return check;
	}

	/**
	 * 
	 * @return 70897 TBSOF3 Aggiunta pagina per mostrare tutte le giacenze
	 *         dell'articolo della missione in esecuzione
	 */
	protected int paginaGiacenza() {
		TForm form = getTForm("YPaginaGiacenze");
		DAELENCO = 0;
		try {
			caricaListaGiacenze(form);
			sendForm(form);
			TForm risposta = readInput();
			if (!risposta.getKeyPressed().equals(TForm.KEY_ESC) && !risposta.getKeyPressed().equals(TForm.KEY_CTL_X)
					&& !risposta.getKeyPressed().equals(TForm.KEY_F1) && !risposta.getKeyPressed().equals(TForm.KEY_F2)
					&& !risposta.getKeyPressed().equals(TForm.KEY_F3) && !risposta.getKeyPressed().equals(TForm.KEY_F4)
					&& !risposta.getKeyPressed().equals(TForm.KEY_F5) && !risposta.getKeyPressed().equals(TForm.KEY_F6)
					&& !risposta.getKeyPressed().equals(TForm.KEY_F7) && !risposta.getKeyPressed().equals(TForm.KEY_F8)
					&& !risposta.getKeyPressed().equals(TForm.KEY_F9)
					&& !risposta.getKeyPressed().equals(TForm.KEY_F10))
				pagina = VISUALIZZA_MISSIONE;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pagina;
	}

	@SuppressWarnings("rawtypes")
	protected void caricaListaGiacenze(TForm form) {
		TList list = form.getTList("MessaggioCommento");
		list.resetSelected();
		Vector elencoVecchio = list.getItems();
		while (elencoVecchio.size() > 1) {
			elencoVecchio.removeElementAt(1);
		}
		Vector listaGiacenze = getGiacenze();
		String item = "";
		for (int i = 0; i < listaGiacenze.size(); i++) {
			YOggettinoGiacenze ogg = (YOggettinoGiacenze) listaGiacenze.get(i);
			if (ogg != null) {
				item = i + 1 + ")L." + ogg.getLotto();
				list.addItem(item);
				item = "U." + ogg.getUbicazione() + " Q." + ogg.getQta();
				list.addItem(item);
				item = ogg.getUdc();
				list.addItem(item);
			}
		}

	}

	protected int paginaUds(boolean chiusuraUds) {
		TForm form = getTForm("paginaUds");
		form.getTField("FieldUds").setValue("");
		String udsGenAut = null;
		if (chiusuraUds) {
			form.getTField("LblIntestazione").setValue(">>>> " + ResourceLoader.getString(RESOURCES, "val0037"));
			form.getTField("LblTipoUds").setValue("");
			form.getTField("LblTipoUds").setVisible(false);
			form.getTField("TastoF5").setValue("");
			form.getTField("TastoF5").setVisible(false);
		} else {
			form.getTField("LblIntestazione").setValue(">>> " + ResourceLoader.getString(RESOURCES, "val0038"));
			form.getTField("TastoF5").setVisible(true);
			if (esecuzioneMissioni.getTipoUds() != null) {
				form.getTField("LblTipoUds").setValue(formatoLblTipoUds(esecuzioneMissioni.getTipoUds()));
				form.getTField("LblTipoUds").setVisible(true);
				udsGenAut = esecuzioneMissioni.generaCodiceUds();
				form.getTField("FieldUds").setValue(udsGenAut);
			} else {
				form.getTField("LblTipoUds").setValue("");
				form.getTField("LblTipoUds").setVisible(false);
			}
		}
		try {
			sendForm(form);
			TForm risposta = readInput();
			if (risposta.getKeyPressed().equals(TForm.KEY_F1) || risposta.getKeyPressed().equals(TForm.KEY_F2)
					|| risposta.getKeyPressed().equals(TForm.KEY_F3) || risposta.getKeyPressed().equals(TForm.KEY_F4)
					|| risposta.getKeyPressed().equals(TForm.KEY_F6) || risposta.getKeyPressed().equals(TForm.KEY_F7)
					|| risposta.getKeyPressed().equals(TForm.KEY_F8) || risposta.getKeyPressed().equals(TForm.KEY_F9)
					|| risposta.getKeyPressed().equals(TForm.KEY_F10)) // Ripeti
				return pagina;
			if (risposta.getKeyPressed().equals(TForm.KEY_CTL_X) || risposta.getKeyPressed().equals(TForm.KEY_ESC)) {
				if (!chiusuraUds) {
					if (udsObligatoria)
						// messaggio(false, "Indica l'UdS in cui caricare la merce in prelievo.");
						messaggio(false, ResourceLoader.getString(RESOURCES, "msg0031"));
					else {
						esecuzioneMissioni.setTestataUds(null);
						gestioneUdsInCorso = false;
						pagina = MISSIONE;
					}
					return pagina;
				} else {
					pagina = ESITO_UDS;
					return pagina;
				}
			}
			if (risposta.getKeyPressed().equals(TForm.KEY_F5)) {
				if (!chiusuraUds)
					pagina = TIPO_UDS;
				else
					pagina = UDS; // Ripeti la schermata.
				return pagina;
			}
			String uds = form.getTField("FieldUds").getValue();
			codiceUdsInChiusura = uds; // DSSOF3 70973 Quando l'utente chiude la singola uds mi salvo il codice in una
			// variabile globale
			// verra' poi utilizata per la stampa dell'uds
			if (uds == null || uds.equals("")) {
				// messaggio(true, "Indica un codice valido per l'UdS.");
				messaggio(true, ResourceLoader.getString(RESOURCES, "msg0032"));
				pagina = UDS;
				return pagina;
			}
			String errore = testPaginaUds(uds, chiusuraUds);
			if (errore != null) {
				messaggio(true, errore);
				pagina = UDS;
				return pagina;
			}
			pagina = MISSIONE;
			// se l'utente non ha cambiato uds genero l'evento di stampa ETIC_UDS
			if (uds != null && uds.equals(udsGenAut)) {
				TestataUds tu = (TestataUds) Factory.createObject(TestataUds.class);
				tu.setCodice(uds);
				tu.setTipoUds(tipoUds);
				EventoApplicativo.creaEvento(tu, TestataUds.ETIC_UDS);
				EventoApplicativo.creaEvento(tu, StampaRptEticUds.STAMPA_ETIC_UDS);
			}
		} catch (Exception e) {
			e.printStackTrace(Trace.excStream);
			pagina = MENU;
		}
		return pagina;
	}

	protected boolean checkMissioniOmesse(Missione missione) {
		boolean check = true;
		ResultSet rs = null;
		CachedStatement cs = null;
		try {
			RigaLista lisRig = missione.getRigaLista();
			Integer rig = lisRig.getNumeroRigaHost();
			String riga = rig.toString();
			String select = "SELECT * FROM SOFTRE.Y_OMETTI_MISSIONI_V01 " + "WHERE ID_AZIENDA = '"
					+ Azienda.getAziendaCorrente() + "' AND " + "COD_LISTA = '" + lisRig.getCodiceTestataLista()
					+ "' AND " + "ID_RIGA_DOC = '" + riga + "' AND ID_DET_RIGA_DOC = '" + lisRig.getDettaglioRigaHost()
					+ "'";
			cs = new CachedStatement(select);
			rs = cs.executeQuery();
			if (rs.next()) {
				check = false;
			}
			if (!check) {
				String delete = "DELETE FROM LOGIS.LMISSIONE WHERE CODICE = '" + missione.getCodice() + "' AND "
						+ "COD_MAG_FISICO = '" + Azienda.getAziendaCorrente() + "' AND " + "STATO_MISSIONE = 'E'";
				CachedStatement cs1 = new CachedStatement(delete);
				int rx = cs1.executeUpdate();
				if (rx > 0)
					ConnectionManager.commit();
				cs1.free();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return check;
	}

	/**
	 * 70983 Modificata la videata, mostro la qta da prelevare e non quella
	 * disponibile
	 */
	protected String paginaAssegnaQta(String qtaConf, Saldo sal) throws Exception {
		String errore = null;
		TForm form = getTForm("paginaAssegnaQtaCatasta");
		Missione m = esecuzioneMissioni.getMissInConferma();
		form.getTField("LblArticolo").setValue(formatoArticoloTit(m.getArticolo()));
		boolean isConf = (m.getConfezione() != null);
		BigDecimal qtaDisp = new BigDecimal(0);
		// Se il saldo è quello della missione devo aggiungere la qtà richiesta
		if (sal.getKey().equals(m.getSaldo().getKey())) {
			qtaDisp = sal.calcolaDisponibile().add(m.getQta1Richiesta().min(m.getSaldo().getQta1()));
			if (isConf)
				qtaDisp = m.getSaldo().getQtaConfDisponibile()
						.add(m.getQta1Richiesta().min(m.getSaldo().getQtaConfezione()));
		} else {
			qtaDisp = sal.calcolaDisponibile();
			if (isConf)
				qtaDisp = m.getSaldo().getQtaConfDisponibile();
		}
		if (qtaDaPrl.charAt(0) == '0')
			qtaDaPrl = qtaDaPrl.substring(1);
		BigDecimal qtaPrl = new BigDecimal(qtaDaPrl);
		if (qtaDisp.compareTo(qtaPrl) < 0) { // 70984 se la qta che ho disponibile sul saldo è minore di quella che
			// vuole prelevare l'utente, vado ad avvisare l'utente e mostra la
			// qtadisp
			messaggio(false,
					"Attenzione: è stato selezionato un saldo con disponibilità inferiore di quella richiesta. La qtà di prelievo è stata aggiornata");
			form.getTField("LblQtaMax").setValue("QTA PREL: " + qtaDisp);
			form.getTField("FieldMultifunzione").setValue(String.valueOf(qtaDisp));
		} else {
			form.getTField("LblQtaMax").setValue("QTA PREL: " + qtaDaPrl);
			form.getTField("FieldMultifunzione").setValue(qtaDaPrl);
		}

		while (true) {
			sendForm(form);
			TForm risposta = readInput();
			if (risposta.getKeyPressed().equals(TForm.KEY_ESC) || risposta.getKeyPressed().equals(TForm.KEY_CTL_X)
					|| risposta.getKeyPressed().equals(TForm.KEY_F1) || risposta.getKeyPressed().equals(TForm.KEY_F2)
					|| risposta.getKeyPressed().equals(TForm.KEY_F3) || risposta.getKeyPressed().equals(TForm.KEY_F4)
					|| risposta.getKeyPressed().equals(TForm.KEY_F6) || risposta.getKeyPressed().equals(TForm.KEY_F7)
					|| risposta.getKeyPressed().equals(TForm.KEY_F8) || risposta.getKeyPressed().equals(TForm.KEY_F9)
					|| risposta.getKeyPressed().equals(TForm.KEY_F10))
				continue;
			if (risposta.getKeyPressed().equals(TForm.KEY_F5))
				return "";
			BigDecimal qtaInserita = new BigDecimal(risposta.getTField("FieldMultifunzione").getValue());
			if (qtaInserita.compareTo(qtaDisp) > 0) {
				// 70998 reso impossibile prelevare una quantità maggiore di quella disponibile
				// sul saldo selezionato
				messaggio(true, "La quantita indicata è superiore alla disponibilità di questo saldo!");
			} else {
				errore = testPaginaAssegnaQta(risposta.getTField("FieldMultifunzione").getValue());
				if (errore != null) {
					messaggio(true, errore);
				} else {
					return risposta.getTField("FieldMultifunzione").getValue();
				}
			}
		}
	}

	/**
	 *
	 * 70984 vado a controllare la gestione delle cataste
	 * 
	 */
	protected boolean gestioneCataste(Missione m) {
		boolean flag = false;
		CachedStatement cs = null;
		ResultSet rs = null;
		String strategia = getStrategia(m);
		try {
			if (strategia != null && !strategia.equals("")) {
				String select = "SELECT FLAG_CATASTA FROM LOGIS.LELE_ZONA_PRE " + "WHERE COD_MAG_FISICO = '"
						+ Azienda.getAziendaCorrente() + "' " + "AND COD_STRATEGIA_PRE = '" + strategia + "' "
						+ "AND COD_ZONA_PRELIEVO = '" + m.getUbicazione().getCodiceZonaPrelievo() + "'";
				cs = new CachedStatement(select);
				rs = cs.executeQuery();
				if (rs.next()) {
					String abilitato = rs.getString("FLAG_CATASTA").trim();
					if (abilitato != null && abilitato.equals("Y"))
						flag = true;
				}
				cs.free();
			}
		} catch (SQLException e) {
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
		return flag;
	}

	protected String getStrategia(Missione m) {
		String strategia = "";
		String[] keys = { m.getCodiceSocieta(), m.getCodiceMagLogico(), m.getGruppoArticolo(),
				m.getArticolo().getLogica2(), m.getCodiceMagFisico() };
		DistribuzioneFisicaPrelievo dfp = null;
		try {
			dfp = (DistribuzioneFisicaPrelievo) DistribuzioneFisicaPrelievo
					.readOnlyElementWithKey(DistribuzioneFisicaPrelievo.class, KeyHelper.buildObjectKey(keys)); // Fix
			// 15947
		} catch (SQLException ex) {
			ex.printStackTrace(Trace.excStream);
		}
		if (dfp != null) {
			StrategiaPrelievo sp = dammiStrategiaPrelievo(dfp, m);
			strategia = sp.getCodice();
		}
		return strategia;
	}

	public StrategiaPrelievo dammiStrategiaPrelievo(DistribuzioneFisicaPrelievo dfp, Missione m) {
		if (m.getRigaLista() != null) {
			StrategiaPrelievo strategiaPrelievoSpecifica = m.getRigaLista().dammiStrategiaPrelievoSpecifica(dfp);
			if (strategiaPrelievoSpecifica != null) {
				return strategiaPrelievoSpecifica;
			}
		}
		StrategiaPrelievo sp = dfp.getStrategiaPrelievo(); // Cerco la strategia di prelievo della RL.
		if (sp == null) // in prima istanza sulla dist. fis. di prel.
			sp = m.getMagFisico().getStrategiaPrelievo(); // ed eventualmente in seconda sul magFisico.
		return sp;
	}

	// 70998 portate le informazioni su due righe per evitare la substring dei dati

	@SuppressWarnings({ "unused", "rawtypes" })
	protected Saldo paginaElencoSaldiComp() throws Exception {
		TForm pointerForm4 = getTForm(formPaginaElencoSaldiComp);
		Missione m = esecuzioneMissioni.getMissInConferma();
		pointerForm4.getTField("Intestazione").setValue(">>>> " + ResourceLoader.getString(RESOURCES, "val0013"));
		pointerForm4.getTField("LblArticolo").setValue("");
		if (m.getArticolo() != null)
			pointerForm4.getTField("LblArticolo")
					.setValue(ResourceLoader.getString(RESOURCES, "val0014") + ":" + formatoArticolo(m));
		if (m.getTipoMissione() == Missione.SPOSTAMENTO) {
			pointerForm4.getTField("LblDestinazione").setVisible(true);
			pointerForm4.getTField("LblDestinazione").setValue(formatoUbicazioneDestinazione(m));
		} else {
			pointerForm4.getTField("LblDestinazione").setVisible(false);
			pointerForm4.getTField("LblDestinazione").setValue("");
		}
		pointerForm4.getTField("FieldMultifunzione").setValue("");
		Saldo saldoScelto = null;
		Vector elencoSaldiComp = esecuzioneMissioni.getElencoSaldiCom();
		switch (elencoSaldiComp.size()) {
		case 0:
			messaggio(true, ResourceLoader.getString(RESOURCES, "msg0028"));
			break;
		default:
			caricaElencoSaldi(pointerForm4, elencoSaldiComp);
			sendForm(pointerForm4);
			TForm risposta = readInput();
			if (risposta.getKeyPressed().equals(TForm.KEY_F5)) {
				saldoScelto = new Saldo();
				break;
			}
			if (risposta.getKeyPressed().equals(TForm.KEY_ESC) || risposta.getKeyPressed().equals(TForm.KEY_CTL_X)
					|| risposta.getKeyPressed().equals(TForm.KEY_F1) || risposta.getKeyPressed().equals(TForm.KEY_F3)
					|| risposta.getKeyPressed().equals(TForm.KEY_F4) || risposta.getKeyPressed().equals(TForm.KEY_F6)
					|| risposta.getKeyPressed().equals(TForm.KEY_F7) || risposta.getKeyPressed().equals(TForm.KEY_F9)
					|| risposta.getKeyPressed().equals(TForm.KEY_F10))
				break;
			String codUbic = pointerForm4.getTField("FieldMultifunzione").getValue();
			if (codUbic != null && !codUbic.equals("")) {
				for (int i = 0; i < elencoSaldiComp.size() && saldoScelto == null; i++) {
					Object obj = elencoSaldiComp.elementAt(i);
					if (obj instanceof Saldo) {
						if (((Saldo) obj).getCodiceUbicazione().equals(codUbic)
								|| ((Saldo) obj).getMappaUdc().getCodice().equals(codUbic))
							saldoScelto = (Saldo) obj;
					}
				}
				if (saldoScelto == null) {
					messaggio(true, ResourceLoader.getString(RESOURCES, "msg0029", new String[] { codUbic }));
					break;
				}
			} else {
				TList list = risposta.getTList("ElencoSaldi");
				int itemPos = list.getCurrentSelectedItem();
				int i = 0;
				if ((itemPos - 2) == 0) {
					itemPos = itemPos - 2;
					saldoScelto = (Saldo) elencoSaldiComp.elementAt(itemPos);
				} else if ((itemPos - 2) < 0) {
					itemPos = itemPos - itemPos;
					saldoScelto = (Saldo) elencoSaldiComp.elementAt(itemPos);
				} else {
					if (itemPos % 2 == 0) {
						itemPos = (itemPos / 2) - 1;
						saldoScelto = (Saldo) elencoSaldiComp.elementAt(itemPos);
					} else {
						while (itemPos % 2 != 0) {
							itemPos++;
						}
						itemPos = (itemPos / 2) - 1;
						if (itemPos <= elencoSaldiComp.size())
							saldoScelto = (Saldo) elencoSaldiComp.elementAt(itemPos);
					}
				}
			}
			break;
		}
		return saldoScelto;
	}

	// 70998 inserisco un singolo saldo su due righe
	@SuppressWarnings({ "rawtypes", "unused" })
	protected void caricaElencoSaldi(TForm form, Vector elenco) {
		Vector lista = new Vector();
		Vector elencoVecchio = form.getTList("ElencoSaldi").getItems(); // Svuota.
		while (elencoVecchio.size() > 1) {
			elencoVecchio.removeElementAt(1);
		}
		int j = 1;
		for (int i = 0; i < elenco.size(); i++) { // Riempie
			Saldo s = (Saldo) elenco.elementAt(i);
			// String item = formatoSaldo(s, (i + j));
			String item = i + j + ")" + s.getCodiceMappaUdc();
			form.getTList("ElencoSaldi").addItem(item);
			item = "  " + s.getCodiceUbicazione() + "-" + formattaBigDec(s.getQta1());
			form.getTList("ElencoSaldi").addItem(item);
		}
		form.getTList("ElencoSaldi").setCurrentSelectedItem(1);
		form.getTList("ElencoSaldi").setCurrentTopItem(1);
	}

	/**
	 * 71015 TBSOF3 Creata nuova pagina che mostra tutti i saldi compatibili con la
	 * missione in corso. Per vedere quali saldi sono compatibili viene usata la
	 * vista SOFTRE.Y_SALTO_COLLO_V01
	 */
	@SuppressWarnings("rawtypes")
	protected void paginaSaldiCompatibili() {
		TForm pointerForm4 = getTForm("YPaginaSaldiCompatibili");
		Missione m = esecuzioneMissioni.getMissInConferma();
		try {
			if (m != null) {
				Vector elencoSaldiComp = getElencoSaldiCompatibili(m); // 71015 prendo i saldi compatibili e creo degli
				// oggetti che mi torneranno utili dopo
				switch (elencoSaldiComp.size()) {
				case 0:
					messaggio(true, ResourceLoader.getString(RESOURCES, "msg0028"));
					pagina = VISUALIZZA_MISSIONE;
					return;
				default:
					caricaElencoSaldiCompatibili(pointerForm4, elencoSaldiComp);
					sendForm(pointerForm4);
					TForm risposta = readInput();
					if (risposta.getKeyPressed().equals(TForm.KEY_ESC)
							|| risposta.getKeyPressed().equals(TForm.KEY_CTL_X)) {
						pagina = VISUALIZZA_MISSIONE;
						return;
					} else if (risposta.getKeyPressed().equals(TForm.KEY_F1)
							|| risposta.getKeyPressed().equals(TForm.KEY_F2)
							|| risposta.getKeyPressed().equals(TForm.KEY_F3)
							|| risposta.getKeyPressed().equals(TForm.KEY_F4)
							|| risposta.getKeyPressed().equals(TForm.KEY_F5)
							|| risposta.getKeyPressed().equals(TForm.KEY_F6)
							|| risposta.getKeyPressed().equals(TForm.KEY_F7)
							|| risposta.getKeyPressed().equals(TForm.KEY_F8)
							|| risposta.getKeyPressed().equals(TForm.KEY_F9)
							|| risposta.getKeyPressed().equals(TForm.KEY_F10)) {
						return;
					} else {
						TList list = risposta.getTList("ElencoSaldiCompatibili");
						YOggettoSaldiCompatibili ogg = new YOggettoSaldiCompatibili();
						int itemPos = list.getCurrentSelectedItem();
						int sottrarre = 4;
						if ((itemPos - sottrarre) == 0) {
							itemPos = itemPos - sottrarre;
							ogg = (YOggettoSaldiCompatibili) elencoSaldiComp.elementAt(itemPos);
						} else if ((itemPos - sottrarre) < 0) {
							itemPos = itemPos - itemPos;
							ogg = (YOggettoSaldiCompatibili) elencoSaldiComp.elementAt(itemPos);
						} else {
							if (itemPos % sottrarre == 0) {
								itemPos = (itemPos / sottrarre) - 1;
								ogg = (YOggettoSaldiCompatibili) elencoSaldiComp.elementAt(itemPos);
							} else {
								while (itemPos % sottrarre != 0) {
									itemPos++;
								}
								itemPos = (itemPos / sottrarre) - 1;
								if (itemPos <= elencoSaldiComp.size())
									ogg = (YOggettoSaldiCompatibili) elencoSaldiComp.elementAt(itemPos);
							}
						}
						if (ogg.getTipoSalto().equals("A")) { // saldo uguale a quello che ha già la missione
							updateMissione(m, ogg);
							messaggio(false, "Il saldo selezionato corrisponde a quello attuale");
							pagina = VISUALIZZA_MISSIONE;
							return;
						} else if (ogg.getTipoSalto().equals("T")) { // saldo che potrebbe coprire completamente la
							// missione
							updateMissione(m, ogg); // vado ad assegnare alla missione il nuovo saldo, ubicazione, lotto
							// e mappa udc
							pagina = VISUALIZZA_MISSIONE;
							return;
						} else if (ogg.getTipoSalto().equals("P")) { // saldo parziale che NON potrebbe coprire
							// completamente la missione
							boolean proseguire = conferma(false, "Il saldo non copre tutto il prelievo. "
									+ "La qtà residua verrà lasciata sul saldo originale. Proseguire?");
							if (proseguire)
								operazioniPerParziale(m, ogg);
							else
								return;
							pagina = VISUALIZZA_MISSIONE;
							return;
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (MissingResourceException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	protected void operazioniPerParziale(Missione m, YOggettoSaldiCompatibili ogg) {
		try {
			m.setQta1Richiesta(ogg.getQtaRes());
			m.setQta2Richiesta(ogg.getQtaRes());
			if (m.save() >= 0)
				ConnectionManager.commit();
			Missione nuovaMissione = (Missione) Factory.createObject(Missione.class);
			nuovaMissione.setEqual(m);
			int codice = Numeratore.getNumProgr("MISS01").getLastProgr();
			nuovaMissione.setCodice(codice);
			updateMissione(nuovaMissione, ogg);
			nuovaMissione.setQta1Richiesta(ogg.getQtaDisp());
			nuovaMissione.setQta2Richiesta(ogg.getQtaDisp());
			if (nuovaMissione.save() >= 0) {
				ConnectionManager.commit();
				esecuzioneMissioni.getElencoMissioni().add(nuovaMissione);
				esecuzioneMissioni.setMissInConferma(nuovaMissione);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (CopyException e) {
			e.printStackTrace();
		} catch (NumeratoreMaxProgrException e) {
			e.printStackTrace();
		} catch (NumeratoreNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NumeratoreNotValidException e) {
			e.printStackTrace();
		} catch (NumeratoreMaxException e) {
			e.printStackTrace();
		}
	}

	protected void updateMissione(Missione m, YOggettoSaldiCompatibili ogg) {
		try {
			String key = KeyHelper.buildObjectKey(new String[] { Azienda.getAziendaCorrente(), ogg.getCodSaldo() });
			Saldo saldo = (Saldo) Saldo.elementWithKey(Saldo.class, key, 0);
			if (saldo != null) {
				m.setSaldo(saldo);
				m.setLotto1(ogg.getLotto1());
				m.setCodiceMappaUdc(ogg.getCodMappaUdc());
				key = KeyHelper.buildObjectKey(new String[] { Azienda.getAziendaCorrente(), ogg.getCodUbicazione() });
				Ubicazione ubicazione = (Ubicazione) Ubicazione.elementWithKey(Ubicazione.class, key, 0);
				if (ubicazione != null) {
					m.setUbicazione(ubicazione);
					if (m.save() >= 0) {
						ConnectionManager.commit();
					} else {
						messaggio(true, "Errore durante il cambio del saldo");
						ConnectionManager.rollback();
					}
				}
			} else {
				messaggio(true, "Non è stato trovato nessun saldo con codice : " + ogg.getCodSaldo());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Vector getElencoSaldiCompatibili(Missione m) {
		Vector elencoSaldiCompatibili = new Vector();
		ResultSet rs = null;
		try {
			String select = "SELECT * FROM SOFTRE.Y_SALTO_COLLO_V01 WHERE COD_MAG_FISICO = '"
					+ Azienda.getAziendaCorrente() + "' " + "AND COD_MISSIONE = '" + m.getCodice() + "' ";
			CachedStatement cs = new CachedStatement(select);
			rs = cs.executeQuery();
			while (rs.next()) {
				YOggettoSaldiCompatibili ogg = new YOggettoSaldiCompatibili();
				String codSaldo = rs.getString("COD_SALDO") != null ? rs.getString("COD_SALDO").trim() : "";
				String lottoLogis = rs.getString("LOTTO_LOGIS") != null ? rs.getString("LOTTO_LOGIS").trim() : "";
				String codUbicazione = rs.getString("COD_UBICAZIONE") != null ? rs.getString("COD_UBICAZIONE").trim()
						: "";
				BigDecimal qtaGiac = rs.getBigDecimal("QTA_GIAC") != null ? rs.getBigDecimal("QTA_GIAC")
						: BigDecimal.ZERO; // 71197 Da qtaDisp a QtaGiac
				String codMappaUdc = rs.getString("COD_MAPPA_UDC") != null ? rs.getString("COD_MAPPA_UDC").trim() : "-";
				String tipoSalto = rs.getString("TIPO_SALTO") != null ? rs.getString("TIPO_SALTO").trim() : "";
				String lotto01 = rs.getString("LOTTO01") != null ? rs.getString("LOTTO01").trim() : "";
				String codSaldoRes = rs.getString("COD_SALDO_RES") != null ? rs.getString("COD_SALDO_RES").trim() : "";
				BigDecimal qtaRes = rs.getBigDecimal("QTA_RES") != null ? rs.getBigDecimal("QTA_RES") : BigDecimal.ZERO;
				ogg.setCodSaldo(codSaldo);
				ogg.setLottoLogis(lottoLogis);
				ogg.setCodUbicazione(codUbicazione);
				ogg.setQtaDisp(qtaGiac);
				ogg.setCodMappaUdc(codMappaUdc);
				ogg.setTipoSalto(tipoSalto);
				ogg.setLotto1(lotto01);
				ogg.setQtaRes(qtaRes);
				ogg.setCodSaldoRes(codSaldoRes);
				elencoSaldiCompatibili.add(ogg);
			}
			cs.free();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return elencoSaldiCompatibili;
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	protected void caricaElencoSaldiCompatibili(TForm form, Vector elenco) {
		Vector lista = new Vector();
		Vector elencoVecchio = form.getTList("ElencoSaldiCompatibili").getItems(); // Svuota.
		while (elencoVecchio.size() > 1) {
			elencoVecchio.removeElementAt(1);
		}
		String mostroQta = ParametroPsn.getValoreParametroPsn("YMostraQtaSaltoColloRF", "Mostra Quantita RF");
		for (int i = 0; i < elenco.size(); i++) { // Riem pie
			YOggettoSaldiCompatibili ogg = (YOggettoSaldiCompatibili) elenco.elementAt(i);
			String item = i + 1 + ")L." + ogg.getLottoLogis() + " (" + ogg.getTipoSalto() + ")";
			form.getTList("ElencoSaldiCompatibili").addItem(item);
			item = "  U." + ogg.getCodUbicazione();
			if (mostroQta != null && mostroQta.equals("Y"))
				item += " Q." + ogg.getQtaDisp().intValue(); // cambiato da disp a giac
			form.getTList("ElencoSaldiCompatibili").addItem(item);
			item = "  " + ogg.getCodMappaUdc();
			form.getTList("ElencoSaldiCompatibili").addItem(item);
			item = "  ";
			form.getTList("ElencoSaldiCompatibili").addItem(item);
		}
		form.getTList("ElencoSaldiCompatibili").setCurrentSelectedItem(1);
		form.getTList("ElencoSaldiCompatibili").setCurrentTopItem(1);
	}

	@SuppressWarnings("rawtypes")
	protected void gestioneKGM(TestataLista tesLis) {
		if (tesLis.getCodice().startsWith("V")) {
			List righe = tesLis.getRigheLista();
			boolean presente = false;
			for (int i = 0; i < righe.size(); i++) {
				RigaLista riga = (RigaLista) righe.get(i);
				if (riga != null) {
					if (riga.getTipoRigaLista() == RigaLista.KIT_MAG) {
						presente = true;
					}
				}
				if (presente)
					break;
			}
			if (presente) {
				CachedStatement cs = null;
				int rs = 0;
				try {
					String execute = "EXEC SOFTRE.Y_GESTIONE_KGM_P01 '" + Azienda.getAziendaCorrente() + "' , '"
							+ tesLis.getCodice() + "' , '" + UtenteAzienda.getUtenteAziendaConnesso().getId() + "'"; // 71114
					// aggiunto
					// il
					// codice
					// utente
					cs = new CachedStatement(execute);
					rs = cs.executeUpdate();
					if (rs > 0)
						ConnectionManager.commit();
					else
						ConnectionManager.rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					try {
						if (cs != null)
							cs.free();
						if (rs != 0)
							rs = 0;
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 71197 Modificato il peso assegnato all'uds: TARA+PESO_NETTO
	 */
	@Override
	public boolean assegnaDimensioni(TestataUds uds, TForm form) throws Exception {
		boolean ok = super.assegnaDimensioni(uds, form);
		TipoUds tipoUds = uds.getTipoUds();
		BigDecimal tara = BigDecimal.ZERO;
		if (tipoUds != null) {
			tara = tipoUds.getTara() != null && tipoUds.getTara().compareTo(BigDecimal.ONE) != 0 ? tipoUds.getTara()
					: BigDecimal.ZERO;
		}
		BigDecimal qtaNetta = getQtaNetta(uds);
		qtaNetta = qtaNetta.add(tara);
		form.getTField("pesoLordoField").setValue(qtaNetta.setScale(2, 0).toString());

		return ok;
	}

	protected BigDecimal getQtaNetta(TestataUds uds) {
		BigDecimal qta = BigDecimal.ZERO;
		ResultSet rs = null;
		CachedStatement cs = null;
		try {
			String select = "SELECT PESO_NETTO_CALC FROM SOFTRE.Y_UDS_PESO_NETTO_CALC_V01 WHERE COD_UDS = '"
					+ uds.getCodice() + "'";
			cs = new CachedStatement(select);
			rs = cs.executeQuery();
			if (rs.next()) {
				String quantita = rs.getString("PESO_NETTO_CALC") != null ? rs.getString("PESO_NETTO_CALC").trim()
						: "0";
				qta = new BigDecimal(quantita);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Throwable t) {
				t.printStackTrace(Trace.excStream);
			}
		}
		return qta;
	}

}