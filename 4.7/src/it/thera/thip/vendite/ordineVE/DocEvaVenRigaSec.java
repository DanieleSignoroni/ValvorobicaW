package it.thera.thip.vendite.ordineVE;

import java.math.*;
import java.sql.*;
import java.util.*;

import com.thera.thermfw.base.*;
import com.thera.thermfw.cbs.*;
import com.thera.thermfw.common.*;
import com.thera.thermfw.persist.*;


import it.thera.thip.base.articolo.*;
import it.thera.thip.base.comuniVenAcq.*;
import it.thera.thip.base.documenti.*;
import it.thera.thip.base.generale.ParametroPsn;
import it.thera.thip.cs.*;
import it.thera.thip.magazzino.generalemag.*;
import it.thera.thip.magazzino.saldi.GestoreSaldi;
import it.thera.thip.vendite.documentoVE.*;
import it.thera.thip.vendite.proposteEvasione.*;
import it.thera.thip.atp.PersDatiATP;
/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author Graziano Scartapacchi
 * @version 1.0
 */
/*
 * Date          Author     Description
 * 25/06/2002    GSCARTA    Versione Beta 4
 * 27/06/2003    GSCARTA    tolta il calcolaImportiRiga()
 * 02/07/2002    GSCARTA    Versione Beta 5
 * 07/07/2002    GSCARTA    Versione Beta 6
 * 03/10/2002    GSCARTA    Aggiornamento calcolo e controllo disponibilità
 * 11/11/2003    DBattisto  FIX 1013 Corretto il metodo CreaRiga nel caso in cui
 *                          la riga non sia aggiungiabile
 * 18/03/2004    DB         Fix 1674
 * 15/06/2004    DB         Fix 1932
 * 29/11/04      GSCARTA    Fix 2322
 * 18/01/2005    GScarta    Fix 3134
 * 14/01/2005    LP         Fix 3187: Aggiunto controllo generazione automatica lotti
 * 16/02/2005    ME         Fix 3274: ridefinito vuoto il metodo
 *                          ricalcoloQuantitaRigaInternal
 * 17/02/2005    MN         Fix 3266: ridefinito il metodo getIdArtIntestatarioForGUI
 * 17/02/2005    SL+GSCARTA Fix 03269: Ridefinito il metodo initializeOwnedObjects e aggiunto il
 *                                     metodo setFather(...);
 * 24/02/2005    MN          Fix 03307 : Commentato il metodo getIdArtIntestatarioForGUI
 * 29/11/2004    PM          Fix 4380 Nel metodo save commentata la chiamata al
 *                           medodo copiaInOldRiga. Questa chimata ha come effetto
 *                           collaterale che durante l'evasione di un kit non venissero
 *                           aggiornate correttamente le qta proposta, attesa e spedita
 *                           sulle righe secondarie dell'ordine
 * 28/03/2006    DB          Fix 4996
 * 05566   15/06/2006  MN    Ridefiniti i metodi sistemaQuadraturaLotti(), getUnicoLottoEffettivo().
 *                           Viene aggiornata la qtaDaSpedire con il delta della qta.
 * 02/08/2006    DB          Fix 5500. Nel renderedefinitiva la riga primaria devo settare alla riga secondaria di essere estratta
 * 5798    31/08/2006  MN    Modificato il metodo controllaQtaLotti, deve essere conseiderata la
 *                           qta da spedire (qta eventualmente modificata in fase di evasione) e non
 *                           la qta residua (qta residua dell' ordine).
 *                           Utilizzato il metodo del metodo calcolaGiacenzaNetta.
 * 06110   26/10/2006  ME    Aggiunto codice per proposizione automatica
 *                           delle movimentazioni di storico matricole
 *                           presenti nell'ordine
 * 06294   23/11/2006  ME    Aggiunto metodo getCommentiParteIntestatario
 * 06920   19/03/2007  MN   Modificato il tipo di rritorno del metodo getUnicoLottoEffettivo.
 * 06965  21/03/2007    MN     Modificate le chiamate ai metodi di calcolo
 *                             giacenza/disponibilità su ProposizioneAutLotto.
 * 07220  02/05/2007   DBot  Introdotta gestione blocchi per accantonato e prenotato
 * 11120  24/07/2009   GScarta   Introdotta nuova gestione del controllo giacenza con errori forzabili.
 * 11213   30/07/2009  GScarta   Completamento fix 11120
 * 11084   03/09/2009  PM          Gestione picking e packing
 * 11402   28/09/2009  DB
 * 13001  27/07/2010   PM        Nelle proposte di evasione il controllo disponibiltà non deve essere fatto.
 * 13342  06/10/2010   PM      In evasione di una riga sec gestita a lotti unitari se sull'ordine è indicato un lotto sul documento la quantità totale della
 *                             riga viene caricata tutta sul lotto
 * 14253  04/04/2011   PM      Eliminato problema di ClassCastException
 * 16586  14/01/2013   DBot    Eliminate le righe lotto da ordine in eliminazione righe di proposta evasione
 * 19215  10/02/2014   AYM     Gestione il flag "Quantità attesa entrata disponibilié" nella modello  di "Proposte evasione".
 * 31484  04/09/2020   TJ	   La proposta automatica del lotto deve essere solo si l'articolo della rigaPRM è Kit non gestito a mag
 * 35639  02/05/2022   LTB     Gestione assegnazione dei lotti (con proposizione automatica o manuale dei lotti) 
 * 								che consideri quanto già assegnato nello stesso documento 
 * 38596  05/05/2023   SZ	   Deve fere il controllo di quadratura lotti si il modalita di prelevio dell articolo e manuel.	 
 * */
public class DocEvaVenRigaSec extends DocumentoVenRigaSec implements HasDocEvaVenRiga {

	private boolean isInizializedOwnedObjects = false;//Mod. 03269 SL + GSCARTA

	protected boolean iControlloAccPrn = false; //fix 7220

	/**
	 *
	 */
	private DocEvaVenRiga iDocEvaVenRiga;

	public DocEvaVenRigaSec() {
		this.setSpecializzazioneRiga(RIGA_SECONDARIA_PER_COMPONENTE);
		super.iRigheLotto = new OneToMany(DocEvaVenRigaLottoSec.class, this, 31, true);
		// fix 1674
		//this.iDocEvaVenRiga = new DocEvaVenRiga(this);
		this.iDocEvaVenRiga =  (DocEvaVenRiga)Factory.createObject(DocEvaVenRiga.class);
		this.iDocEvaVenRiga.setRigaDoc(this);
		this.iDocEvaVenRiga.getRigaDoc().setRigaSaldata(DocEvaVenRiga.DEFAULT_RIGA_SALDATA);
		// fine fix 1674
		this.iRigaPrimaria = new Proxy(DocEvaVenRigaPrm.class);

		// fix 11213 >
		this.getLivelloControlloDisp().add(0, new Character(LivelliControlloDisponibilita.NA));
		this.getLivelloControlloDisp().add(1, new BigDecimal(0.00));
		// fix 11213 <

	}

	public DocEvaVenRiga getDocEvaVenRiga() {
		return iDocEvaVenRiga;
	}

	/**
	 * Attributo di servizio
	 * @return
	 */
	public Integer getNumeroRigaDocumentoPrm() {
		Integer num = null;
		if (getRigaPrimaria() != null) {
			num = getRigaPrimaria().getNumeroRigaDocumento();
		}
		return num;
	}

	protected TableManager getTableManager() throws java.sql.SQLException {
		return DocEvaVenRigaSecTM.getInstance();
	}

	public void setEqual(Copyable obj) throws CopyException {
		super.setEqual(obj);
	}

	/**
	 *
	 * @param doc
	 * @param rigaOrdine
	 * @return
	 */
	protected DocEvaVenRigaLottoSec creaRigaLottoSec(OrdineVenditaRigaLotto rigaOrdineLotto) {
		return DocEvaVenRigaLottoSec.creaRiga(this, rigaOrdineLotto);
	}

	//ini fix 2322
	public static DocEvaVenRigaSec creaRiga(DocEvaVenRigaPrm rigaPrm, OrdineVenditaRigaSec rigaOrdine) {
		return creaRiga(rigaPrm, rigaOrdine, true);
	}
	//fine fix 2322

	/**
	 * Crea una riga documento secondaria da una riga ordine secondaria.
	 * @param rigaPrm
	 * @param rigaOrdineSec
	 * @return
	 */
	public static DocEvaVenRigaSec creaRiga(DocEvaVenRigaPrm rigaPrm, OrdineVenditaRigaSec rigaOrdine, boolean consQtaResidua) { //fix 2322
		DocEvaVenRigaSec riga = null;
		try {
			GestoreEvasioneVendita.get().println("--- Inizio creazione nuova riga sec su riga ordine sec: '" +
					rigaOrdine.getKey() + "' ---");
			riga = (DocEvaVenRigaSec) Factory.createObject(DocEvaVenRigaSec.class);
			//fix 2322
			riga.getDocEvaVenRiga().setConsideraQtaResiduaRigaOrdine(consQtaResidua);

			// testata
			riga.setTestata(rigaPrm.getTestata());

			// key
			riga.setIdAzienda(rigaPrm.getIdAzienda());
			riga.setFatherKey(rigaPrm.getKey());
			riga.setNumeroRigaDocumento(rigaPrm.getNumeroRigaDocumento());
			riga.setDettaglioRigaDocumento(rigaPrm.getNumeroRigaDocumentoSecUtile());

			riga.setRigaPrimaria(rigaPrm);
			rigaPrm.getRigheSecondarie().add(riga);

			// attributi di riga presi dalla riga doc primaria
			riga.getDocEvaVenRiga().copiaDatiDaRigaPrm(rigaPrm);

			// attributi di riga presi dalla riga ordine secondaria
			riga.getDocEvaVenRiga().aggiornaAttributiDaRigaOrdine(rigaOrdine);

			riga.setControlloAccPrn(rigaPrm.hasControlloAccPrn()); //fix 7220
			//ini fix 2322
			// modifica qta se abilitato controllo disponibilità
			// fix 11402
			//char cdisp = riga.getTestata() != null ? ((DocEvaVen)riga.getTestata()).getControlloDisp() :
			//    TipiControlloDisponibilita.NESSUNO;
			//riga.correggiQtaSuControlloDisp(cdisp);
			char tipoControllo = riga.getTestata() != null ? ((DocEvaVen)riga.getTestata()).getControlloDisp() :
				TipiControlloDisponibilita.NESSUNO;
			if (!(PersDatiATP.getCurrentPersDatiATP().getAttivazAccantPrenot() &&
					PersDatiATP.getLivelloControlloAccPrnMan() == PersDatiATP.LC_ERRORE)){
				//if (riga.getArticolo() != null && !riga.getArticolo().isArticLotto()) { //Fix 13001 PM
				if (((DocEvaVen)riga.getTestata()).getTipoBloccoLSP() == TipoBloccoLSP.NESSUNO && riga.getArticolo() != null && !riga.getArticolo().isArticLotto()) {//Fix 13001 PM
					tipoControllo = TipiControlloDisponibilita.GIACENZA;
					if (riga.getMagazzino() != null &&
							riga.getMagazzino().getTpControlloGiacRil() ==
							PersDatiMagazzino.TP_CTL_GIAC_DISP_NO) {
						tipoControllo = TipiControlloDisponibilita.NESSUNO;
					}
				}
			}
			riga.correggiQtaSuControlloDisp(tipoControllo);
			// fine fix 11402
			//fine fix 2322

			// aggiungi alla riga primaria
			if (riga.getDocEvaVenRiga().isAddable(riga.getDocEvaVenRiga(), rigaOrdine)) {
				// Righe lotto
				List righeLottoOrdine = rigaOrdine.getRigheLotto();
				if (righeLottoOrdine != null) {
					Iterator iterLO = righeLottoOrdine.iterator();
					int numRiga = riga.getDocEvaVenRiga().getNumeroRigaDocumentoLottoUtile().intValue();
					while (iterLO.hasNext()) {
						riga.getDocEvaVenRiga().setNumeroRigaDocumentoLottoUtile(new Integer(numRiga));
						OrdineVenditaRigaLotto obj = (OrdineVenditaRigaLotto) iterLO.next();
						DocEvaVenRigaLottoSec rigaLotto = riga.creaRigaLottoSec(obj);
						if (rigaLotto != null) {
							/*
              GestoreEvasioneVendita.get().println("Creata riga lotto doc sec :'" + rigaLotto.getKey() +
                  "' da riga lotto ord sec :'" + obj.getKey() + "'");
              rigaLotto.getDocEvaVenRigaLotto().logQta("su riga ordine lotto sec: " + obj.getKey());
							 */
							numRiga++;
						}
					}
				}
				// Collegamento alla riga ordine
				riga.setRigaOrdine(rigaOrdine);
				riga.setRRigaOrd(rigaOrdine.getNumeroRigaDocumento());
			}
			else {
				// FIX 1013
				rigaPrm.getRigheSecondarie().remove(riga);
				// Fine FIX 1013
				riga = null;
			}
		}
		catch (Throwable t) {
			Trace.excStream.print("Errore di generazione di riga secondaria su riga ordine sec: '" +
					rigaOrdine.getKey() + "'");
			t.printStackTrace(Trace.excStream);
		}
		return riga;
	}

	//ini fix 2322
	/**
	 *
	 * @param tipoControllo
	 */
	protected void correggiQtaSuControlloDisp(char tipoControllo) {
		int roundingMode = BigDecimal.ROUND_HALF_EVEN;
		if (tipoControllo != TipiControlloDisponibilita.NESSUNO) {
			// fix 11213 >
			//List livDisp = this.getLivelloControlloDisp(true);
			this.assegnaLivelloControlloGiaDisp();
			List livDisp = this.getLivelloControlloDisp();
			// fix 11213 <
			this.getDocEvaVenRiga().correggiQtaSuControlloDisp(livDisp);
			if (this.getDocEvaVenRiga().getQtaDaSpedire().isZero()) {
				DocEvaVenRigaLottoSec lotto = (DocEvaVenRigaLottoSec)this.getDocEvaVenRiga().getLottoDummy();
				if (lotto != null) {
					lotto.getDocEvaVenRigaLotto().assegnaQtaDaSpedire(this.
							getDocEvaVenRiga().getQtaDaSpedire());
				}
			}
		}
	}
	//fine fix 2322

	protected DocumentoVenRigaLotto getLottoDummy() {
		DocumentoVenRigaLotto lottoD = (DocEvaVenRigaLottoSec) Factory.createObject(DocEvaVenRigaLottoSec.class);
		return lottoD;
	}

	/**
	 * Ridefinito perchè non è necessario salvare la riga primaria
	 * @param rc
	 * @return
	 * @throws SQLException
	 */
	protected int salvaRigaPrimaria(int rc) throws SQLException {
		return rc;
	}

	/**
	 *
	 */
	protected void creaOldRiga() {
		iOldRiga = (DocEvaVenRigaSec) Factory.createObject(DocEvaVenRigaSec.class);
	}

	/**
	 *
	 * @return
	 * @throws SQLException
	 */
	public int delete() throws SQLException {
		int res = ErrorCodes.OK;
		if (this.isOnDB()) {
			if (this.getOldRiga() != null) {
				this.setQtaAttesaEvasione(this.getOldRiga().getQtaAttesaEvasione());
				this.setQtaPropostaEvasione(this.getOldRiga().getQtaPropostaEvasione());
			}
		}
		res = super.delete();
		return res;
	}

	//ini fix 2322
	protected void copiaValoriInOldRiga() {
		if (this.getOldRiga() == null) {
			creaOldRiga();
		}
		super.copiaValoriInOldRiga();
	}
	//fine fix 2322

	/**
	 *
	 * @return
	 * @throws SQLException
	 */
	public int save() throws SQLException {
		// fix 2844
		if (!isOnDB()){
			this.creazioneAutomaticaRigaContenitore();
		}
		// fine fix 2844
		int res = ErrorCodes.GENERIC_ERROR;
		ThipException ex = null;
		/** @todo DA CONTROLLARE */
		boolean oldIsOnDB = isOnDB();
		Timestamp oldTimestamp = this.getTimestamp();
		try {
			//ini fix 2322
			boolean salvaRiga = this.getDocEvaVenRiga().isRigaEstratta() ||
					((DocEvaVen)this.getTestata()).isForzaSalvataggioRighe();
			if (salvaRiga) { //fine fix 2322
				int numRiga = getNumeroRigaDocumento().intValue();
				if (numRiga < 0) {
					setNumeroRigaDocumento(new Integer(0 - numRiga));
				}
				GestoreEvasioneVendita.get().println("tento la save() di riga sec :'" + this.getKey() + " su articolo: " +
						this.getArticoloKey() + " timestamp = " + oldTimestamp);

				//Fix 6110 - inizio
				Integer vecchioDett = getDettaglioRigaDocumento();
				//Fix 6110 - fine

				res = super.save();

				//Fix 6110 - inizio
				if (res >= 0) {
					DocEvaVen testata = (DocEvaVen)getTestata();
					if (!testata.getMatricoleOrdine().isEmpty()) {
						testata.aggiornaMatricoleOrdineNumRigaSec(
								vecchioDett, getDettaglioRigaDocumento(), getNumeroRigaDocumento()
								);
					}
				}
				//Fix 6110 - fine
			}
			else {
				res = ErrorCodes.OK;
			}
		}
		catch (Throwable t) {
			t.printStackTrace(Trace.excStream);
			ex = new ThipException(t.getMessage());
			ex.setErrorMessage(new ErrorMessage("THIP_BS000"));
		}
		finally {
			if (this.getDocEvaVenRiga().isRigaEstratta()) {
				String tipoRiga = "primaria";
				if (this.getSpecializzazioneRiga() != RIGA_PRIMARIA) {
					tipoRiga = "secondaria";
				}
				GestoreEvasioneVendita.get().println("save() di riga " + tipoRiga + ":'" + this.getKey() + "' -> " + res +
						" timestamp = " + this.getTimestamp());
			}
			if (res < 0 || ex != null) {
				setOnDB(oldIsOnDB);
				this.setTimestamp(oldTimestamp);
				if (ex != null) {
					throw ex;
				}
			}
			//Fix 4380 Inizio PM
			//ini fix 2322
			//      else {
			//        copiaValoriInOldRiga();
			//      }
			//fine fix 2322
			//Fix 4380 Fine PM
		}
		return res;
	}

	/**
	 * Ridefinisce il metodo perchè non è necessario recuperare i dati già presenti
	 * @return
	 */
	protected boolean recuperoDatiVenditaSave() {
		return false;
	}

	/**
	 * Fix 03269 SL + GSCARTA - Ridefinita la initializeOwnedObjects
	 * @param result
	 * @return
	 */
	public boolean initializeOwnedObjects(boolean result) {
		isInizializedOwnedObjects = true;
		GestoreEvasioneVendita.get().println("initializeOwnedObjects riga secondaria");
		//Fix 4996
		if (this.isOnDB())
			aggiornaDatiDaStorico();
		// Fine fix 4996
		boolean bo = super.initializeOwnedObjects(result);
		GestoreEvasioneVendita.get().println("'" + this.getKey() + "' - secondaria");
		return bo;
	}

	/**
	 * Fix 03269 SL + GSCARTA - Aggiunto metodo setFather ridefinendolo;
	 * @param result
	 * @return
	 */
	public void setFather(PersistentObject father) {
		super.setFather(father);
		if (isInizializedOwnedObjects) {
			boolean rigaEstratta = this.getDocEvaVenRiga().isRigaEstratta() || this.isOnDB();
			this.getDocEvaVenRiga().setRigaEstratta(rigaEstratta);
			this.getDocEvaVenRiga().inizializzaQtaRiga();
			this.getDocEvaVenRiga().logQta("inizializzate le qta di '" + this.getKey() +"' isOnDB = '" + this.isOnDB() + "'" );
			isInizializedOwnedObjects = false;
		}
	}

	/**
	 * Instanzia un lotto dummy di classe erede di DocEvaVenRigaLotto
	 * @return
	 */
	protected DocumentoRigaLotto creaLottoDummy() {
		DocEvaVenRigaLottoSec lottoD;
		lottoD = (DocEvaVenRigaLottoSec)Factory.createObject(DocEvaVenRigaLottoSec.class);
		lottoD.setFather(this);
		lottoD.setIdAzienda(getIdAzienda());
		lottoD.setIdLotto(LOTTO_DUMMY);
		lottoD.setIdArticolo(getIdArticolo());
		// controlla presenza lotto
		try {
			String keyLotto = KeyHelper.buildObjectKey(new String[] {
					getIdAzienda(), getIdArticolo(), Lotto.LOTTO_DUMMY});
			Lotto lotto = Lotto.elementWithKey(keyLotto, PersistentObject.NO_LOCK);
			if (lotto == null) {
				Lotto newLotto = (Lotto) Factory.createObject(Lotto.class);
				newLotto.setKey(keyLotto);
				newLotto.save();
			}
		}
		catch (SQLException ex) {
			ex.printStackTrace(Trace.excStream);
		}
		return lottoD;
	}

	/**
	 * Forza lo stato di avanzamento sulla riga e sui lotti
	 * aggiornando le qta.
	 * @param stato nuovo stato avanzamento
	 * @return Lista di ErrorMessage
	 */
	public List forzaStatoAvanzamento(char stato) {
		List errors = new ArrayList();
		try {
			this.aggiornaQtaEvasione(stato, true);
			super.setStatoAvanzamento(stato);
			ErrorMessage em = this.getDocEvaVenRiga().checkQuadraturaLotti();
			if (em != null) {
				errors.add(em);
			}
		}
		catch (Throwable t) {
		}
		return errors;
	}

	/**
	 * Aggiorna le QTA di riga e dei suoi componenti in base allo stato avanzamento
	 * ma non aggiorna lo stato avanzamento.
	 * @param stato
	 */
	protected void aggiornaQtaEvasione(char stato, boolean forzaStato) {
		char statoIniziale = this.getStatoAvanzamento();
		if (statoIniziale != stato || forzaStato) {
			List righeEliminabili = new ArrayList();
			this.getDocEvaVenRiga().aggiornaQtaEvasione(stato, forzaStato);
			Iterator iterLotti = this.getRigheLotto().iterator();
			QuantitaInUMRif qtaDaSpedireCorrente = new QuantitaInUMRif();
			QuantitaInUMRif qtaResiduaLottoCorrente = new QuantitaInUMRif();
			try {
				qtaDaSpedireCorrente.setEqual(getDocEvaVenRiga().getQtaDaSpedire());
			}
			catch (CopyException ce) {
			}
			while (iterLotti.hasNext()) {
				DocEvaVenRigaLottoSec lotto = (DocEvaVenRigaLottoSec) iterLotti.next();
				boolean isChanged = false;
				if (this.getOldRiga() !=null ){
					isChanged = this.getOldRiga().getServizioQta().compareTo(qtaDaSpedireCorrente) != 0;
				}
				if (!lotto.getIdLotto().equals(Lotto.LOTTO_DUMMY) && !qtaDaSpedireCorrente.isZero()) {
					if (!this.isOnDB() || isChanged) {
						int comp = lotto.getDocEvaVenRigaLotto().getQtaResidua().compareTo(qtaDaSpedireCorrente);
						try {
							if (comp == 0) {
								qtaResiduaLottoCorrente.setEqual(qtaDaSpedireCorrente);
								qtaDaSpedireCorrente.azzera();
							}
							else if (comp > 0) {
								qtaResiduaLottoCorrente.setEqual(qtaDaSpedireCorrente);
								qtaDaSpedireCorrente.azzera();
							}
							else {
								String msg = "Residuo sul lotto sec '" + lotto.getKey() + "' [" + qtaResiduaLottoCorrente + "] < [" +
										qtaDaSpedireCorrente + "]";
								((DocEvaVen)this.getTestata()).getRigheWarnings().put(this.getRigaOrdine(), msg);
								GestoreEvasioneVendita.get().println(msg);
								qtaResiduaLottoCorrente.setEqual(lotto.getDocEvaVenRigaLotto().getQtaResidua());
								qtaDaSpedireCorrente = qtaDaSpedireCorrente.subtract(qtaResiduaLottoCorrente);
							}
						}
						catch (CopyException cx) {
						}
					}
					else {
						try {
							qtaResiduaLottoCorrente.setEqual(qtaDaSpedireCorrente);
						}
						catch (CopyException cx) {
						}
					}
				}
				else {
					qtaResiduaLottoCorrente.azzera();
				}
				lotto.getDocEvaVenRigaLotto().setAggiornamentoInOrdine(this.getDocEvaVenRiga().isAggiornamentoInOrdine());
				lotto.getDocEvaVenRigaLotto().aggiornaQtaEvasione(stato, forzaStato, qtaResiduaLottoCorrente);
				if (qtaResiduaLottoCorrente.isZero()) {
					righeEliminabili.add(lotto);
				}
			}
			if (!righeEliminabili.isEmpty()) {
				Iterator iterEliminabili = righeEliminabili.iterator();
				while (iterEliminabili.hasNext()) {
					this.getRigheLotto().remove(iterEliminabili.next());
				}
			}
		}
	}

	/**
	 * Restituisce il livello di controllo disponibilità in base al tipo, giacenza
	 * o disponibilità
	 * @return List a due valori, get(0) = Character indicante il livello, get(1) = BigDecimal
	 * indicante il valore di giacenza media o disponibilità
	 */
	// fix 11120 >
	/*
  public List getLivelloControlloDisp() {
    return getLivelloControlloDisp(false);
  }
	 */
	// fix 11120 <

	//ini fix 2322
	public boolean isDisponibileControlloDisp() {
		boolean isOk = false;
		isOk = this.isAbilitatoAggiornamentoSaldi() || this.isAbilitatoMovimentiMagazzino();
		return isOk;
	}

	/**
	 * Restituisce il livello di controllo disponibilità in base al tipo, giacenza
	 * o disponibilità
	 * @return List a due valori, get(0) = Character indicante il livello, get(1) = BigDecimal
	 * indicante il valore di giacenza media o disponibilità
	 */
	// fix 11120 >
	//public List getLivelloControlloDisp(boolean addWarnings) {
	public List assegnaLivelloControlloGiaDisp() {
		boolean addWarnings = true;
		// fix 11120 <
		List valori = new ArrayList();
		char livello = LivelliControlloDisponibilita.DEFAULT;
		BigDecimal qtaRifUMPrm = GestoreEvasioneVendita.get().getBigDecimalZero();
		char ctrlDisp = ( (DocEvaVen) getTestata()).getControlloDisp();
		// fix 11402
		if (!(PersDatiATP.getCurrentPersDatiATP().getAttivazAccantPrenot() &&
				PersDatiATP.getLivelloControlloAccPrnMan() == PersDatiATP.LC_ERRORE)){
			if (this.getArticolo() != null && !this.getArticolo().isArticLotto()) {
				ctrlDisp = TipiControlloDisponibilita.GIACENZA;
				if (this.getMagazzino() != null &&
						this.getMagazzino().getTpControlloGiacRil() ==
						PersDatiMagazzino.TP_CTL_GIAC_DISP_NO) {
					ctrlDisp = TipiControlloDisponibilita.NESSUNO;
				}
			}
		}
		// fine fix 11402
		if (!isDisponibileControlloDisp()) {
			ctrlDisp = TipiControlloDisponibilita.NESSUNO;
		}
		switch (ctrlDisp) {
		case TipiControlloDisponibilita.GIACENZA: {
			if (!this.getDocEvaVenRiga().isDocumentoDiReso()) {
				//fix 7220 inizio
				if(hasControlloAccPrn()) {
					livello = individuaLivelloDispAccPrn(addWarnings);
				}
				else
				{
					//fix 7220 fine
					BigDecimal qtaDaSpedireInUMPrm = getDocEvaVenRiga().getQtaDaSpedireInUMPrm();//GestoreEvasioneVendita.get().getBigDecimalZero();
					BigDecimal qtaResiduaInUMPrm = this.getQtaResiduaInUMPrm();
					BigDecimal giacenzaNettaInUMPrm = this.getGiacenzaNetta();
					livello = LivelliControlloDisponibilita.ROSSO;
					String msg = "";
					String gestKGM = ParametroPsn.getValoreParametroPsn("YGestioneKGM", "Attivazione gestione KGM"); //71151 DSSOF3
					if (this.getRigaPrimaria().getArticolo().getTipoParte() == ArticoloDatiIdent.KIT_NON_GEST) {
						List livDisp = CalcolaDisponibilita.get().assegnaLivelloDisp(qtaDaSpedireInUMPrm,
								qtaResiduaInUMPrm, giacenzaNettaInUMPrm);
						livello = ((Character)livDisp.get(0)).charValue();
						qtaRifUMPrm = (BigDecimal)livDisp.get(1);
						//DSSOF3 71220 13/09/2023	Inizio
						//Se l'articolo della riga primaria e' un KIT gestito a magazzino
						//E ho attivo il KGM, mancava il settaggio della variabile qtaRifUMPrm
						//Percio' la riga si creava con quantita evas 0 e non veniva portata sul documento
					}else if(this.getRigaPrimaria().getArticolo().getTipoParte() == ArticoloDatiIdent.KIT_GEST
							&& (gestKGM != null && gestKGM.equals("Y"))) {
						List livDisp = CalcolaDisponibilita.get().assegnaLivelloDisp(qtaDaSpedireInUMPrm,
								qtaResiduaInUMPrm, giacenzaNettaInUMPrm);
						livello = ((Character)livDisp.get(0)).charValue();
						qtaRifUMPrm = (BigDecimal)livDisp.get(1);
						//DSSOF3 71220	13/09/2023	Fine
					}else if(gestKGM == null || !gestKGM.equals("Y")) { //71175 DSSOF3 Se il parametro e' vuoto o N, STD
						// nulla
						livello = LivelliControlloDisponibilita.VERDE;
					}
					if (livello == LivelliControlloDisponibilita.ROSSO) {
						msg += " [Componente '" + this.getDocEvaVenRiga().getRifRigaOrdineFormattato() +
								"' Articolo '" + this.getIdArticolo() + "'" +
								" errore di giacenza, qta  = " + giacenzaNettaInUMPrm +
								" su richiesta = " + qtaDaSpedireInUMPrm + "] ";
						if (this.getDocEvaVenRiga().isAbilitaWarnings() && addWarnings) {
							DocumentoOrdineRiga rigaOrdPrm = this.getRigaPrimaria().getRigaOrdine();
							String s = (String) ( (DocEvaVen)this.getTestata()).getRigheWarnings().
									get(rigaOrdPrm);
							if (s == null) {
								s = "";
							}
							msg += s;
							((DocEvaVen)this.getTestata()).getRigheWarnings().put(rigaOrdPrm, msg);
						}
						GestoreEvasioneVendita.get().println(msg);
					}
				}
			}
		}
		break;
		case TipiControlloDisponibilita.DISPONIBILITA: {
			livello = LivelliControlloDisponibilita.GIALLO;
		}
		break;
		case TipiControlloDisponibilita.NESSUNO: {
			// fix 11402
			//livello = LivelliControlloDisponibilita.VERDE;
			livello = LivelliControlloDisponibilita.NA;
			// fine fix 11402
		}
		}
		valori.add(0, new Character(livello));
		valori.add(1, qtaRifUMPrm);

		// fix 11120 >
		this.getLivelloControlloDisp().add(0, valori.get(0));
		this.getLivelloControlloDisp().add(1, valori.get(1));
		// fix 11120 <

		return valori;
	}

	//fix 7220 inizio
	public char individuaLivelloDispAccPrn(boolean addWarnings)
	{
		BigDecimal qtaDaSpedireInUMPrm = getDocEvaVenRiga().getQtaDaSpedireInUMPrm();
		BigDecimal qtaResiduaInUMPrm = this.getQtaResiduaInUMPrm();
		//simulo disponibilità piena
		char livello = LivelliControlloDisponibilita.DEFAULT;
		if(this.getRigaPrimaria().getArticolo().getTipoParte() == ArticoloDatiIdent.KIT_NON_GEST)
		{
			List livDisp = CalcolaDisponibilita.get().assegnaLivelloDisp(qtaDaSpedireInUMPrm, qtaResiduaInUMPrm, qtaDaSpedireInUMPrm);
			livello = ((Character)livDisp.get(0)).charValue();
		}
		else
			livello = LivelliControlloDisponibilita.VERDE;

		if(livello == LivelliControlloDisponibilita.ROSSO)
		{
			String msg = " [Componente '" + this.getDocEvaVenRiga().getRifRigaOrdineFormattato() +
					"' Articolo '" + this.getIdArticolo() + "'" +
					" quantità in giacenza non sufficiente su  su residua = " + qtaResiduaInUMPrm + "] ";

			GestoreEvasioneVendita.get().println(msg);

			if(this.getDocEvaVenRiga().isAbilitaWarnings() && addWarnings)
			{
				DocumentoOrdineRiga rigaOrdPrm = this.getRigaPrimaria().getRigaOrdine();
				Map righeWarn = ((DocEvaVen)this.getTestata()).getRigheWarnings();
				String s = (String)righeWarn.get(rigaOrdPrm);
				if(s != null)
					msg += s;
				righeWarn.put(rigaOrdPrm, msg);
			}
		}
		return livello;
	}
	//fix 7220 fine

	protected boolean isQtaOrdinataUgualeZero() {
		boolean isOk = false;
		if (this.getRigaPrimaria() != null) {
			this.getDocEvaVenRiga().setAbilitaControlloQtaZero(
					((DocEvaVenRigaPrm)this.getRigaPrimaria()).getDocEvaVenRiga().isAbilitaControlloQtaZero());
			isOk = this.getDocEvaVenRiga().isQtaOrdinataUgualeZero();
		}
		return isOk;
	}

	public ErrorMessage checkRigaOrdineNoFrazionabile() {
		ErrorMessage em = null;
		if (this.getDocEvaVenRiga().isAbilitaControlloQtaZero()) {
			em = super.checkRigaOrdineNoFrazionabile();
		}
		return em;
	}
	//fine fix 2322

	// per la UI ---v
	public QuantitaInUMRif getQtaOrdinata() {
		return this.getDocEvaVenRiga().getQtaOrdinata();
	}

	public QuantitaInUMRif getQtaResidua() {
		return this.getDocEvaVenRiga().getQtaResidua();
	}

	public boolean isRigaEstratta() {
		return this.getDocEvaVenRiga().isRigaEstratta();
	}

	public void setRigaEstratta(boolean isOk) {
		this.getDocEvaVenRiga().setRigaEstratta(isOk);
	}

	public boolean isStatoAvanzamentoDef() {
		return this.getDocEvaVenRiga().isStatoAvanzamentoDef();
	}

	public void setStatoAvanzamentoDef(boolean statoDefinitivo) {
		this.getDocEvaVenRiga().setStatoAvanzamentoDef(statoDefinitivo);
	}

	// DA SPEDIRE
	public void setQtaDaSpedireInUMRif(BigDecimal qta) {
		this.getDocEvaVenRiga().getQtaDaSpedire().setQuantitaInUMRif(qta);
		// Fix 4996
		this.getDocEvaVenRiga().setQtaDaSpedireInUMRif(qta);
		// fine fix 4996
	}

	public void setQtaDaSpedireInUMPrm(BigDecimal qta) {
		this.getDocEvaVenRiga().getQtaDaSpedire().setQuantitaInUMPrm(qta);
		// Fix 4996
		this.getDocEvaVenRiga().setQtaDaSpedireInUMPrm(qta);
		// fine fix 4996
	}
	public void setQtaDaSpedireInUMSec(BigDecimal qta) {
		this.getDocEvaVenRiga().getQtaDaSpedire().setQuantitaInUMSec(qta);
		// Fix 4996
		this.getDocEvaVenRiga().setQtaDaSpedireInUMSec(qta);
		// fine fix 4996
	}

	public BigDecimal getQtaDaSpedireInUMRif() {
		return this.getDocEvaVenRiga().getQtaDaSpedire().getQuantitaInUMRif();
	}

	public BigDecimal getQtaDaSpedireInUMPrm() {
		return this.getDocEvaVenRiga().getQtaDaSpedire().getQuantitaInUMPrm();
	}

	public BigDecimal getQtaDaSpedireInUMSec() {
		return this.getDocEvaVenRiga().getQtaDaSpedire().getQuantitaInUMSec();
	}

	public BigDecimal getQtaResiduaInUMRif() {
		return this.getDocEvaVenRiga().getQtaResidua().getQuantitaInUMRif();
	}

	public BigDecimal getQtaResiduaInUMPrm() {
		return this.getDocEvaVenRiga().getQtaResidua().getQuantitaInUMPrm();
	}

	public BigDecimal getQtaResiduaInUMSec() {
		return this.getDocEvaVenRiga().getQtaResidua().getQuantitaInUMSec();
	}
	// per la UI ---^
	public ErrorMessage checkValute() {
		return null;
	}
	// Fix 1932
	protected CommentHandler getCommentiIntestatario()
	{
		return this.getRigaOrdine().getCommentHandler();
	}
	// Fine fix 1932

	public BigDecimal getGiacenzaNetta() {
		return CalcolaDisponibilita.get().getGiacenzaNettaDaSaldi(this.getOldRiga(), this, this.getDocEvaVenRiga().hasControlloDisp()); //this.getDocEvaVenRiga().getGiacenzaNettaDaSaldi();
	}

	//...FIX 3187 inizio

	/**
	 * controllaLottiSec
	 * @return boolean
	 */
	public boolean controllaLottiSec() {

		boolean statoDef = getStatoAvanzamento() == StatoAvanzamento.DEFINITIVO ? true : false;
		//...Eseguo il controllo solo per le righe che non sono onDB
		//...e che hanno un articolo gestito a lotti
		if(!isOnDB() && getArticolo().getArticoloDatiMagaz().isArticLotto()) {

			//...Controllo se sono in un documento non di reso
			char tipoDoc = ((DocumentoVendita)this.getTestata()).getTipoDocVenPerGestMM();
			if(tipoDoc == DocumentoVendita.TD_VENDITA || tipoDoc == DocumentoVendita.TD_SPE_CTO_TRASF) {
				//...Controllo che la creazione automatica sia impostata
				boolean ok = identificaLotto();
				if(ok)
					statoDef = controllaQtaLotti(PersDatiMagazzino.TIPO_VEN, ProposizioneAutLotto.CREA_DA_DOCUMENTO, getIdMagazzino());
				//Fix 38596 Inizio
				else
				{
					if (getRigheLotto() == null || getRigheLotto().isEmpty() || checkPresenzaLottoDummy() != null)
						statoDef = false;
				}
				//Fix 38596 Fine
			}
		}
		return statoDef;
	}

	/**
	 * controllaQtaLotti
	 * @param tipo char
	 * @param ambito char
	 */
	public boolean controllaQtaLotti(char tipo, char ambito, String idMagazzino) {
		boolean okQta = false;
		List lottiOrig = new ArrayList();
		List lottiOrdine = new ArrayList();
		/*QuantitaInUMRif qtaAttEva = null;
    QuantitaInUMRif qtaPrpEva = null;
    QuantitaInUMRif qtaOrdinata = null;*/
		BigDecimal qta = new BigDecimal("0");
		boolean esisteLottoDummy = false;
		if(getRigaOrdine() != null) {
			List lottiRig = getRigaOrdine().getRigheLotto();
			for(int i = 0; i < lottiRig.size(); i++) {
				OrdineVenditaRigaLotto lt = (OrdineVenditaRigaLotto)lottiRig.get(i);
				if(!lt.getIdLotto().equals(Lotto.LOTTO_DUMMY)) {
					lottiOrig.add(lt.getLotto());
					//lottiOrdine.add(lt); //35639
				}
				else {
					// Inizio 5798
					//qta = lt.getQuantitaResiduoConSegno().getQuantitaInUMPrm();
					qta = getQtaDaSpedireInUMPrm();
					// Fine 5798
					esisteLottoDummy = true;
				}
			}
			lottiOrdine = getImpegniLottiOrdine(false); //35639
		}

		if(qta.compareTo(new BigDecimal(0)) == 0 && !esisteLottoDummy){
			//qta = getQtaResidua().getQuantitaInUMPrm();
			qta = getQtaDaSpedireInUMPrm();
		}

		if(qta.compareTo(new BigDecimal(0)) > 0) {

			//35639 inizio
			/*ProposizioneAutLotto pal = ProposizioneAutLotto.creaProposizioneAutLotto(tipo,*/
			ProposizioneAutLotto pal = getProposizioneAutLotto();
			List lottiAuto = new ArrayList();

			if(pal == null) {
				pal = creaProposizioneAutLotto();
				//35639

				pal.inizializzaProposizioneAutLotto(tipo, 
						//35639 fine      
						getNumeroDocumento(),
						getAnnoDocumento(),
						getTestata().getDataDocumento(),
						getNumeroRigaDocumento(),
						null,
						getIdArticolo(),
						getIdVersioneSal(),
						getIdEsternoConfig(),
						idMagazzino,
						getIdCommessa(),
						getIdFornitore(),
						//PersDatiMagazzino.CREA_DA_DOCUMENTO,
						ambito,
						lottiOrig,
						lottiOrdine,
						null, //...Se alla data passo null allora considero la data corrente
						qta);
				pal.setQtaAttesaEntrataDisp(isQtaAttesaEntrataDisp()); //Fix 19215 AYM
				caricaLottiGiaAssegnati(pal); //35639
				lottiAuto = pal.proponiLottiAutomatici();
				pal.setSaldiLottiProposati(lottiAuto); //35639
			}	 
			//35639 inizio
			else {
				lottiAuto = pal.getSaldiLottiProposati();
			}
			//35639 fine

			//BigDecimal qtaLotti = new BigDecimal("0");//35639
			BigDecimal qtaLotti = pal.getQtaLottiProposate();  //35639

			if(qtaLotti == null) { //35639
				qtaLotti = new BigDecimal("0"); //35639

				//...Se è stato creato un lotto automatico genero una riga lotto con quel lotto
				if(lottiAuto != null && !lottiAuto.isEmpty()) {
					BigDecimal qtaRichiesta = qta;  //35639
					for(int j = 0; j < lottiAuto.size(); j++) {
						LottiSaldi lt = (LottiSaldi)lottiAuto.get(j);
						// Inizio 5798
						//qtaLotti = qtaLotti.add(pal.calcolaQtaDisponibileLotto(tipo, lt, !lottiOrig.isEmpty(), lottiOrdine));
						// Inizio 6965
						//qtaLotti = qtaLotti.add(ProposizioneAutLotto.getInstance().calcolaQtaGiacenzaNetta(tipo, lt, !lottiOrig.isEmpty(), lottiOrdine)); //35639
						// Fine 6965
						// Fine 5798
						//35639 inizio
						BigDecimal qtaLotto = pal.calcolaQtaGiacenzaNetta(tipo, lt, !lottiOrig.isEmpty(), lottiOrdine);
						BigDecimal qtaLottoAssegnato = getQtaLottoGiaAssegnato(lt);
						qtaLotto = qtaLotto.subtract(qtaLottoAssegnato);

						if(qtaRichiesta.compareTo(qtaLotto) >= 0) {
							qtaRichiesta = qtaRichiesta.subtract(qtaLotto);    		        
							assegnaQtaLotto(lt, qtaLotto);
							qtaLotti = qtaLotti.add(qtaLotto);
						}
						else {
							assegnaQtaLotto(lt, qtaRichiesta);
							qtaLotti = qtaLotti.add(qtaRichiesta);

						}
						//35639 fine  
					}
					pal.setQtaLottiProposate(qtaLotti); //35639
				}
			}

			if(qtaLotti.compareTo(qta) >= 0)
				okQta = true;

			if(lottiAuto.isEmpty())
				okQta = false;
		}
		else
			okQta = true;

		return okQta;
	}

	//...FIX 3187 fine


	//Fix 3274 - inizio
	/**
	 * Ridefinizione
	 */
	protected void ricalcoloQuantitaRigaInternal() {

	}
	//Fix 3274 - fine
	// Inizio 3266
	// Inizio 3307
	//  public String getIdArtIntestatarioForGUI() {
	//    return this.getIdArticoloIntestatario();
	//  }
	// Fine 3307
	// Fine 3266

	// Fix 4996
	protected void aggiornaDatiDaStorico(){
		this.getDocEvaVenRiga().setQtaDaSpedire(this.getServizioQta());
	}

	public QuantitaInUMRif getQtaResiduaLorda(){
		if (this.isOnDB() && this.getRigaOrdine()!=null){
			QuantitaInUMRif qtaRif = this.getDocEvaVenRiga().getQuantitaResiduoConSegno((OrdineVenditaRiga)this.getRigaOrdine());
			//qtaRif = qtaRif.add(this.getServizioQta());
			qtaRif = qtaRif.add(this.getOldRiga().getServizioQta());
			QuantitaInUMRif zero = new QuantitaInUMRif();
			zero.azzera();
			if (qtaRif.compareTo(zero)<=0){
				qtaRif.azzera();
			}
			return qtaRif;
		}
		else {
			return this.getDocEvaVenRiga().getQtaResidua();
		}
	}
	// Fine fix 4996

	public char getStatoProposta() {
		if (this.getRigaPrimaria().getArticolo().getTipoParte() == ArticoloDatiIdent.KIT_NON_GEST) {
			BigDecimal qtaDaSpedireInUMPrm = getDocEvaVenRiga().getQtaDaSpedireInUMPrm();//GestoreEvasioneVendita.get().getBigDecimalZero();
			BigDecimal qtaResiduaInUMPrm = getQtaResiduaLorda().getQuantitaInUMPrm();
			BigDecimal giacenzaNettaInUMPrm = this.getGiacenzaNetta();
			BigDecimal zero = new BigDecimal("0");
			if (giacenzaNettaInUMPrm.compareTo(new BigDecimal("0"))<=0 || giacenzaNettaInUMPrm==null){
				return StatoPropostaLSP.ROSSO;
			}
			else {
				if (qtaDaSpedireInUMPrm!=null && qtaResiduaInUMPrm!=null && qtaDaSpedireInUMPrm.compareTo(qtaResiduaInUMPrm)<0){
					return StatoPropostaLSP.GIALLO;
				}
				else if(qtaDaSpedireInUMPrm!=null && qtaResiduaInUMPrm!=null){
					if (qtaDaSpedireInUMPrm.compareTo(giacenzaNettaInUMPrm)<=0){
						return StatoPropostaLSP.VERDE;
					}
					else {
						return StatoPropostaLSP.GIALLO;
					}
				}
			}
		}
		return StatoPropostaLSP.VERDE;
	}

	// Inizio 5566
	public void sistemaQuadraturaLotti()
	{
		DocEvaVenRigaLottoSec docRigaLotto = ((DocEvaVenRigaLottoSec)getUnicoLottoEffettivo());
		QuantitaInUMRif zeroQta = new QuantitaInUMRif();
		zeroQta.azzera();
		QuantitaInUMRif deltaQta = new QuantitaInUMRif();
		if (docRigaLotto == null)
			return;

		//Fix 13342 PM >
		if (getArticolo().getArticoloDatiMagaz().isLottoUnitario())
			return;
		//Fix 13342 PM <

		sistemoLeQuantita();
		if (getStatoAvanzamento()== StatoAvanzamento.PROVVISORIO){
			deltaQta = getQtaPropostaEvasione().subtract(docRigaLotto.getQtaPropostaEvasione());
			if (deltaQta.compareTo(zeroQta) != 0){
				docRigaLotto.getDocEvaVenRigaLotto().setQtaDaSpedire(docRigaLotto.getQtaPropostaEvasione().add(deltaQta));
			}
		}
		else if (getStatoAvanzamento() == StatoAvanzamento.DEFINITIVO){
			deltaQta = getQtaAttesaEvasione().subtract(docRigaLotto.getQtaAttesaEvasione());
			if (deltaQta.compareTo(zeroQta) != 0){
				docRigaLotto.getDocEvaVenRigaLotto().setQtaDaSpedire(docRigaLotto.getQtaAttesaEvasione().add(deltaQta));
			}
		}
		super.sistemaQuadraturaLotti();
	}

	public DocumentoOrdineRigaLotto getUnicoLottoEffettivo(){ // Fix 6920
		//DocEvaVenRigaLottoSec rigalotto = null; //Fix 14253 PM
		DocumentoOrdineRigaLotto rigalotto = null; //Fix 14253 PM
		ArrayList listaLotti = (ArrayList)getRigheLotto();
		if (listaLotti.size() == 1){
			//DocEvaVenRigaLottoSec rigalottoTmp  = (DocEvaVenRigaLottoSec)listaLotti.get(0);//Fix 14253 PM
			DocumentoOrdineRigaLotto rigalottoTmp  = (DocumentoOrdineRigaLotto)listaLotti.get(0);//Fix 14253 PM
			if (!rigalottoTmp.getIdLotto().equals(LOTTO_DUMMY))
				rigalotto = rigalottoTmp;
		}
		return rigalotto;
	}
	// Fine 5566
	// fix 5500
	public void rendiDefinitivoRiga() throws SQLException{
		super.rendiDefinitivoRiga();
		DocEvaVenRigaPrm rigaP = (DocEvaVenRigaPrm)this.getRigaPrimaria();
		if (rigaP!=null){
			boolean estratta = rigaP.getDocEvaVenRiga().isRigaEstratta();
			this.getDocEvaVenRiga().setRigaEstratta(estratta);
		}
	}
	// fine fix 5500


	//Fix 6294 - inizio
	/**
	 * Ridefinizione
	 */
	protected CommentHandler getCommentiParteIntestatario() {
		return null;
	}
	//Fix 6294 - fine

	//fix 7220 inizio
	public boolean hasControlloAccPrn()
	{
		return iControlloAccPrn;
	}

	public void setControlloAccPrn(boolean controlla)
	{
		iControlloAccPrn = controlla;
	}
	//fix 7220 fine

	// fix 11120 >
	private List iLivelloControlloDisp = new ArrayList(2);

	public List getLivelloControlloDisp() {
		return this.iLivelloControlloDisp;
	}
	// fix 11120 <

	//Fix 11084 PM Inizio
	public ErrorMessage checkTrasmissiomePPL()
	{
		return null;
	}
	//Fix 11084 PM Fine

	//Fix 16586 inizio
	// gestione dell'eliminazione delle righe lotto quando generate da proposte
	protected int eliminaRiga() throws SQLException {
		List lotti = getRigheLotto();
		List lottiList = new ArrayList();
		Iterator iter = lotti.iterator();
		while(iter.hasNext())
			lottiList.add(iter.next());

		int ret = super.eliminaRiga();
		boolean perEvasioneLSP = ((DocEvaVen)this.getTestata()).isEvasioneDaLSP();
		char bloccoLSP = ((DocEvaVen)this.getTestata()).getTipoBloccoLSP();
		if(perEvasioneLSP && bloccoLSP == TipoBloccoLSP.IMPEGNATO)
			rimuoviLottiDaRigaOrdine(lottiList);
		return ret;
	}

	//Questa operazione rimuove i lotti creati da proposte di evasione durante la proposizione per
	//non avere i lotti su riga ordine, altrimenti ho problemi con disponibilità se cancelo la proposta
	protected void rimuoviLottiDaRigaOrdine(List lottiList)
	{
		boolean isArtLotto = getArticolo().isArticLotto();
		if(!isArtLotto)
			return;

		List righeLottoDoc = lottiList;
		List righeLottoOrd = new ArrayList();
		if(getRigaOrdine() != null)
			righeLottoOrd = getRigaOrdine().getRigheLotto();
		else
			return;

		List movPortafoglio = new ArrayList();
		QuantitaInUMRif daAggiungereALottoDummy = new QuantitaInUMRif(new BigDecimal(0), new BigDecimal(0), new BigDecimal(0));
		Iterator iterDoc = righeLottoDoc.iterator();
		while(iterDoc.hasNext())
		{
			DocEvaVenRigaLottoSec rigaLottoDoc = (DocEvaVenRigaLottoSec)iterDoc.next();
			String lottoDoc = rigaLottoDoc.getIdLotto();
			if(!lottoDoc.equals(Lotto.LOTTO_DUMMY))
			{
				Iterator iterOrd = righeLottoOrd.iterator();
				while(iterOrd.hasNext())
				{
					OrdineVenditaRigaLotto rigaLottoOrd = (OrdineVenditaRigaLotto)iterOrd.next();
					if(rigaLottoOrd.getIdLotto().equals(lottoDoc))
					{
						rigaLottoOrd.setMovimentiPortafoglioList(movPortafoglio);
						rigaLottoOrd.abilitaCalcoloMovimentiPortafoglio();
						QuantitaInUMRif daTogliereOrd = new QuantitaInUMRif(new BigDecimal(0), new BigDecimal(0), new BigDecimal(0));
						//Si suppone che QtaDoc sia la qta della riga doc
						QuantitaInUMRif qtaDoc = rigaLottoDoc.getQtaPropostaEvasione();
						//Si suppone che la riga ord abbia già spostato la qta su ordinato
						QuantitaInUMRif qtaOrd = rigaLottoOrd.getQuantitaOrdinata();

						if(qtaOrd.compareTo(qtaDoc) > 0)
						{
							daTogliereOrd = qtaDoc;
							qtaOrd = qtaOrd.subtract(daTogliereOrd);
							daAggiungereALottoDummy = daAggiungereALottoDummy.add(daTogliereOrd);
							rigaLottoOrd.setQtaInUMRif(qtaOrd.getQuantitaInUMRif());
							rigaLottoOrd.setQtaInUMPrmMag(qtaOrd.getQuantitaInUMPrm());
							rigaLottoOrd.setQtaInUMSecMag(qtaOrd.getQuantitaInUMSec());
						}
						else
						{
							daTogliereOrd = qtaOrd;
							qtaOrd = qtaOrd.subtract(daTogliereOrd);
							daAggiungereALottoDummy = daAggiungereALottoDummy.add(daTogliereOrd);
							if(rigaLottoOrd.getQtaAttesaEvasione().isZero() &&
									rigaLottoOrd.getQtaPropostaEvasione().isZero() &&
									rigaLottoOrd.getQuantitaSpedita().isZero())
								iterOrd.remove();
						}
					}
				}
			}
		}

		if(!daAggiungereALottoDummy.isZero())
		{
			OrdineVenditaRigaLotto rigaLottoDummy = null;
			Iterator iterOrd = righeLottoOrd.iterator();
			while(iterOrd.hasNext() && rigaLottoDummy == null)
			{
				OrdineVenditaRigaLotto rigaLottoOrd = (OrdineVenditaRigaLotto)iterOrd.next();
				if(rigaLottoOrd.getIdLotto().equals(Lotto.LOTTO_DUMMY))
					rigaLottoDummy = rigaLottoOrd;
			}
			if(rigaLottoDummy == null)
			{
				rigaLottoDummy = (OrdineVenditaRigaLotto) Factory.createObject(OrdineVenditaRigaLottoSec.class);
				rigaLottoDummy.setFather(getRigaOrdine());
				rigaLottoDummy.setIdLotto(Lotto.LOTTO_DUMMY);
				rigaLottoDummy.setIdArticolo(getIdArticolo());
				getRigaOrdine().getRigheLotto().add(rigaLottoDummy);
			}
			rigaLottoDummy.setMovimentiPortafoglioList(movPortafoglio);
			rigaLottoDummy.abilitaCalcoloMovimentiPortafoglio();
			QuantitaInUMRif qtaOrdLottoDummy = rigaLottoDummy.getQuantitaOrdinata();
			qtaOrdLottoDummy = qtaOrdLottoDummy.add(daAggiungereALottoDummy);
			rigaLottoDummy.setQtaInUMRif(qtaOrdLottoDummy.getQuantitaInUMRif());
			rigaLottoDummy.setQtaInUMPrmMag(qtaOrdLottoDummy.getQuantitaInUMPrm());
			rigaLottoDummy.setQtaInUMSecMag(qtaOrdLottoDummy.getQuantitaInUMSec());
		}

		try
		{
			OneToMany righeLotto = (OneToMany)righeLottoOrd;
			righeLotto.save();

			GestoreSaldi iGestoreSaldi = (GestoreSaldi)Factory.createObject(GestoreSaldi.class);
			iGestoreSaldi.applicaMovimentiPortafoglio(movPortafoglio);
			movPortafoglio.clear();
		}
		catch(Exception ex)
		{
			ex.printStackTrace(Trace.excStream);
		}
	}
	//Fix 16586 fine

	//Fix 31484 -- inizio
	/**
	 * creaLottiAutomatici (da ridefinire negli eredi)
	 */
	protected void creaLottiAutomatici(){
		//...Controllo se sono in un documento non di reso
		char tipoDoc = ((DocumentoVendita)this.getTestata()).getTipoDocVenPerGestMM();
		if(tipoDoc == DocumentoVendita.TD_VENDITA || tipoDoc == DocumentoVendita.TD_SPE_CTO_TRASF) {
			List lottiOrig = recuperaLottiOrig();

			//...Controllo che la creazione automatica sia impostata
			boolean ok = identificaLotto();
			if(ok) {
				//...Nessuna proposta automatica di lotti in caso di: Articolo della riga primaria è di tipo parte KIT GESTITO A MAGAZZINO e esist lotti in ordineVenditaRigaSec
				//...Infatti, i lotti del ordineVenSec sono recuperati alla convalida di un documento di prd (delle righe materiale).
				if(!(getRigaPrimaria().getArticolo().getTipoParte() == ArticoloDatiIdent.KIT_GEST && !lottiOrig.isEmpty())) {
					proponiLotti(PersDatiMagazzino.TIPO_VEN, ProposizioneAutLotto.CREA_DA_DOCUMENTO, getIdMagazzino());
				}else {
					if (lottiOrig != null && !lottiOrig.isEmpty() && getRigheLotto().isEmpty()) {
						Iterator iterLO = lottiOrig.iterator();
						while (iterLO.hasNext()) {
							OrdineVenditaRigaLotto obj = (OrdineVenditaRigaLotto) iterLO.next();
							DocEvaVenRigaLottoSec rigaLotto = this.creaRigaLottoSec(obj);
						}
					}
				}
			}
		}
		else if(tipoDoc == DocumentoVendita.TD_VENDITA_RESO)
		{
			boolean ok = identificazioneAutomaticaND(getArticolo());
			if(ok){
				creaLottoND();
			}
		}
	}

	protected List recuperaLottiOrig() {
		List lottiOrig = new ArrayList();
		if(getRigaOrdine() != null) {
			List lottiRig = getRigaOrdine().getRigheLotto();
			for (int i = 0; i < lottiRig.size(); i++) {
				OrdineVenditaRigaLotto lt = (OrdineVenditaRigaLotto)lottiRig.get(i);
				if(!lt.getIdLotto().equals(Lotto.LOTTO_DUMMY)) {
					lottiOrig.add(lt);
				}
			}
		}
		return lottiOrig; 
	}
	//Fix 31484 -- fine
}
