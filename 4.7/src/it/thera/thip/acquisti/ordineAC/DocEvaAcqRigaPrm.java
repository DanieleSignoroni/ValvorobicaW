package it.thera.thip.acquisti.ordineAC;

import java.math.*;
import java.sql.*;
import java.util.*;

import com.thera.thermfw.base.*;
import com.thera.thermfw.cbs.*;
import com.thera.thermfw.common.*;
import com.thera.thermfw.persist.*;
import com.thera.thermfw.security.Entity; // fix 11473

import it.softre.thip.acquisti.documentoAC.YDocumentoAcqRigaPrm;
import it.thera.thip.acquisti.documentoAC.*;
import it.thera.thip.acquisti.generaleAC.*;
import it.thera.thip.base.articolo.*;
import it.thera.thip.base.azienda.Magazzino;
import it.thera.thip.base.comuniVenAcq.*;
import it.thera.thip.base.documenti.*;
import it.thera.thip.base.generale.ParametroPsn;
import it.thera.thip.base.generale.UnitaMisura;
import it.thera.thip.cs.*;
import it.thera.thip.datiTecnici.configuratore.*;
import it.thera.thip.magazzino.generalemag.*;
import it.thera.thip.magazzino.matricole.StoricoMatricola;
import it.thera.thip.vendite.ordineVE.GestoreEvasioneVendita;
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
 * 25/06/2003    GSCARTA    Versione Beta 4
 * 27/06/2003    GSCARTA    tolta il calcolaImportiRiga() ed inserito il controllo di check quadratura lotti
 *                          in creazione solo se il doc non è di reso
 * 02/07/2003    GSCARTA    Versione Beta 5
 * 07/07/2003    GSCARTA    Versione Beta 6
 * 08/07/2003    GSCARTA    Aggiornato il comportamento per le qta Rif e Prm variate e ricalcolare in seguito
 *                          ad annullamento in griglia
 * 10/07/2003    GSCARTA    forzaStatoAvanzamento()
 * 08/08/2003    GSCARTA    Attivato il controllo di concorrenza di estrazione sulla riga ordine
 * 13/08/2003    GSCARTA    Attivata la gestione del controllo delle causali
 * 04/09/2003    GSCARTA    Controllato il isCollegataAMagazzino sia in save sia in delete
 * 24/09/2003    GSCARTA    Aggiornati i metodi di check gruppo e getLivelloControlloDisp(boolean addWarnings)
 * 03/10/2002    GSCARTA    Aggiornamento calcolo e controllo disponibilità
 * 06/07/2003    GSCARTA    Introdotto il controllo della tolleranza rivevimento (checkPrcTolleranzaRicevimento)
 * 07/10/2003    GSCARTA    Il controllo di tolleranza viene effettuato solamente per articoli che hanno
 *                          identica unità di misura di acquisto e primaria di magazzino.
 *                          Il controllo avviene solamente se la riga non ha percentuale perdita residuo o è zero
 * 02/02/2004    GSCARTA    Modificato il metodo checkPrcTolleranzaRicevimento e introdotto controllo che se una
 *                          riga ordine sec ha causale con tipo dist. lav. est. uguale a comp=prod e non può essere
 *                          estratta viene automaticamente non estratta la riga prm. FIX 01363
 * 24/02/2004    DB         fix 1502
 * 18/03/2004    DB         fix 1674
 * 25/03/2004    GScarta    Fix 1684
 * 02/04/2004    GScarta    Fix 1699 : introdotto controllo sulla riga modificata
 * 10/05/2004    DB         Fix 1932
 * 19/05/2004    GSCARTA    Fix 2006
 * 24/05/2004    GSCARTA    Fix 2028
 * 31/05/2004    GSCARTA    Fix 2053
 * 25/06/2004    DB         fix 2161
 * 07/07/2004    GScarta    Fix 2218
 * 10/12/2004    GSCARTA    Fix 2322
 * 07/12/2004    ME         Fix 2844
 * 16/12/2004    DB         Fix 3003
 * 14/01/2005    LP         Fix 3099: Aggiunto controllo generazione automatica lotti
 * 10/02/2005    SL         Fix 3262: Aggiunto controllo su descrArtRigaDoc e gestito
 *                                    il messaggio di errore; Questo nel metodo
 *                                    checkRigaModificata(...);
 * 16/02/2005    ME         Fix 3274: ridefinito vuoto il metodo
 *                          ricalcoloQuantitaRigaInternal
 * 17/02/2005    MN         Fix 3266: Ridefinito il metodo getIdArtIntestatarioForGUI().
 * Number  Date         Author   Description
 * 03292   18/02/2005   DZ       checkCondCompEvas: aggiunto test su ABILITA_CONTROLLO_COND_COMP_EVAS.
 * 03307   24/02/2005   MN       Commentato il metodo getIdArtIntestatarioForGUI
 * 03309   24/02/2005   FCrosa   Modifica nel metodo checkRigaModificata()
 * 03315   28/02/2005   PM       Fix 3315
 * 03700   03/05/2005   ME       Introdotto controllo delle generazione delle righe
 *                               secondarie nel metodo creaRigaSec
 * 03785   19/05/2005   MN
 * 03820   24/05/2005   PJ
 * 03864   30/05/2005   MN
 * 03942   20/06/2005   MN
 * 04004   23/06/2005   MN
 * 04090   08/07/2005   PJ/MN    corretta gestione quantità di magazzino in assenza di ricalcolo
 * 03331   22/04/2005   BP       Fix 3331 problema loop!
 * 04720   30/11/2005   MN       Modificata l'impostazione dell'attributo nonFrazionabile:
 *                               la riga è non frazionabile quando:
 *                               - la riga d'ordine è non frazionabile
 *                               - il documento è di spedizione comp.
 * 04670   02/12/2005   MN        Gestione Unità Misura con flag Quantità intera.
 * 04706   30/11/2005   DZ       Aggiunto checkRigheSecondarie (ridefinito).
 * 04790   19/12/2005   MN       Gestione del flag TipoEvasioneOrdine = SALDO_AUTOMATICO_IN_SPEDIZIONE
 *                               in questo caso la riga deve essere forzata a saldo, ma le quantità
 *                               devono essere editabili.
 * 05179   13/03/2006   ME       Righe secondarie con modalità produzione
 *                               FLOOR_STOCK escluse dalle righe secondarie del
 *                               documento
 * 05117   14/03/2006   GN       Correzione nella gestione delle unità di misura con flag Quantità intera
 * 05348   26/04/2006   ME       Ridefinizione del motodo checkArticoloEsclusione
 * 05395   18/05/2006   MN       Modificato il metodo nulla(), deve ritornare true
 *                               solo se la qta è null e non anche quando è 0.
 * 05303   11/04/2006   DB
 * 05566   15/06/2006   MN       Ridefiniti i metodi sistemaQuadraturaLotti(), getUnicoLottoEffettivo().
 *                               Viene aggiornata la qtaDaSpedire con il delta della qta.
 * 05590   21/06/2006   MN       In caso di proposizione automatica deve essere controllata
 *                               la giacenza del lotto, e quindi settato lo stato avanzamento in base
 *                               a questa.
 * 05759   27/07/2006   ME       Spento il flag di salvataggio delle righe
 *                               ordine per evitare l'OPTIMISTIC_LOCK in fase di
 *                               salvataggio
 * 05876   11/09/2006   ME       Eliminate modifiche della fix 5759
 * 06110   24/10/2006   ME       Aggiunto codice per proposizione automatica
 *                               delle movimentazioni di storico matricole
 *                               presenti nell'ordine
 * 06253   16/11/2006   ME       Modificata inizializzazione dello stato
 *                               avanzamento in fase di creazione della riga del
 *                               documento
 * 06294   23/11/2006   ME       Aggiunto metodo getCommentiParteIntestatario
 * 06583   24/01/2007   ME       Aggiunto controllo dichiarazione matricole in
 *                               creazione delle righe del documento
 * 06652   05/02/2007   MN       Se la causale del documento indica che il prezzo è obbligatorio, in fase
 *                               di estrazione delle righe ordine , devono essere segnalate come righe non estratte
 *                               quelle che hanno un prezzo zero o null.
 * 06920   20/03/2007   MN       Modificata signature del metodo getUnicoLottoEffettivo()
 * 07187   11/06/2007   DBot     Modifiche per accantonato prenotato
 * 08467   21/12/2007   MN       Modificato il metodo controllaQuadraturaQtaLotti(...), le qta righe del
 *                               lotto non devono essere aggiornate , questa operazione viene eseguita
 *                               dalla DocumentoDocRiga (se non c'e' quadratura).
 * 08805   12/03/2008   LP       Aggiunto hook per personalizzazioni
 * 08707   19/03/2008   ME       Ridefinito checkDichiarazioneMatricole
 * 08929   14/04/2008   MM       Modificato il metodo creaRiga per copiare solamente le righe valide
 * 09283   23/05/2008   ME       Eliminata funzionalità del metodo checkDichiarazioneMatricole
 *                               in quanto poteva causare la visualizzazione di messaggi
 *                               di errore non coerenti
 * 09224   13/05/2008   DBot     Alzato flag per attivare controllo assegnazioni in salvataggio
 * 09450   23/06/2008   LP       Aggiunto hook per personalizzazioni
 * 09671   25/08/2008   PM       Con le righe di conto lavoro non viene ricalcolata
 *                               correttamente la quantità delle righe secondarie.
 * 09745   10/09/2008   PM	     Fix completamento della fix 9671: usato metodo  ricalcoloQuantita(DocumentoOrdineRiga rigaPrm, QuantitaInUMRif qtaRigaPrimaria)
 * 10955   17/06/2009  Gscarta   modificate chiamate a convertiUM dell'articolo per passare la versione
 * 11415   02/10/2009   DB
 * 11606   13/112009   GScarta
 * 11614   19/11/2009  GScarta   In fase di riassegnazione configurazione è possibile che siano generate righe ordine e
 *                               righe documento sulle nuove configurazioni. Corretto anche la gestione errori.
 * 11659   27/11/2009  GScarta   Implemento sul CL della 11614.
 * 11822   23/12/2009  GScarta   correzione 11614
 * 11840   29/12/2009  GScarta   Interventi:
 *                               1) non viene mai inizializzato il docEvaAcqConfiguratore con le configurazioni
 *                               2) Per CL non viene mai spezzata la riga ordine, ma viene riassegnata la configurazione
 *                               3) Per CL in ricevimento componenti attivato controllo sull'impossibilità di ricevere una configurazione neutra
 * 11473   08/01/2010  GScarta   Impostato nuovo controllo Giacenza/Disponibilità sul Magazzino per gli articoli non gestiti a lotti
 *                               1) introdotto attributo riga selezionabile e forzabile.
 *                               2) introdotto attributo 'LivelloControlloDisp' che contine il livello e la qta disponibile.
 * 11951   15/01/2010  GScarta   Riporto in 3.0.8 delle fix 11822, 11840 e 11473 e correzione al metodo 'checkRigaModificata()'
 *                               per controllare la presenza della riga ordine
 * 12062   28/01/2010  GScarta  Introdotti controlli per configurazione neutra
 * 12181   11/02/2010  GScarta  Correzioni su forzabilità e controllo giacenza
 * 13342  06/10/2010  PM     In evasione di una riga sec gestita a lotti unitari se sull'ordine è indicato un lotto sul documento la quantità totale della
 *                           riga viene caricata tutta sul lotto
 * 13057   08/09/2010  ElOuni   Introdotti controlli per qtaDaSpedireInUMPrm
 *                     Ichrak
 * 14253   04/04/2011  PM      Eliminato problema di ClassCastException
 * 12384   18/03/2010  GScarta  correzioni su multitono
 * 12686   01/06/2010  GScarta  COrrezione per nuova gestione numero imballo.
 * 13073   12/08/2010  GScarta  Correzione multitono
 * 13130   08/09/2010  GScarta  Correzioni multitono CL: introdotto ricalcolo su qta sec per personalizzazioni
 * 14738   29/06/2011  DBot     Integrazione a standard fix 12384 12686 13073 13130
 * 16860   26/09/2012  Linda    Modifica il metodo checkQtaDaSpedireInUMPrm().
 * 17924   23/05/2013  Linda    Corretto il calcola della  quantità del lotto del componente nelle righe documento.
 * 17927   23/05/2013  AYM      Correzione di quantia QtaPropostaEvasione nel caso di cambio automatica di stato documento da "Definitivo" à "Provvisorio".
 * 19840   23/05/2014  AYM      Correzione il aggiorna di quantita lotti nel caso di articolo con unita di misura intero.
 * 21468   13/05/2015  Linda    Correzione metodo riassegnaErroreGiaDisp().
 * 21503   08/09/2015  OCH      Impostato il valore RegistrazioneFattura a NON_RICHIESTO e il flag non fatturare in base alla causale di riga documento nel caso di evasione ordine di conto lavoro per la spedizione componenti
 * 21510   18/09/2015  AYM      Aggiungere condizione compatibiltà evasione.
 * 22545   01/04/2016  OCH      Ridefinizione checkArticolStatoTecnico
 * 23523   18/05/2016  Linda    Caso spedizione Componente:Se utente non abilitato deve esser proposta la quantità massima evadibile senza che mandi in negativo la disponibilità componenti.
 * 23955   21/07/2016  Linda    Gestione il caso di trasferimento tra magazzino.
 * 24384   20/10/2016  OCH      Elimina NullPointerException nel metodo isRigaConAssegnazione 
 * 29790   03/09/2019  LTB      Elimina il confronto tra le sconti riga doc eva (che possono essere creata manualmente) e le sconti della riga ordine
 * 29440   24/09/2019  LTB      E’ nata un’eccezione in caso di evasione di un ordine matricolato dove le matricole erano definite a livello di ordine, 
															  In questo caso la creazione delle righe documento lotto porti al class cast exception tra DocumentoAcqRigaLottoPrm e DocEvaAcqRigaLottoPrm 

 * 30871   06/03/2020  SZ		6 Decimale.
 * 31786   27/08/2020  LTB      ClassCastException in evasione ord acq (checkGruppoConsegna)
 * 32416   07/12/2020  LTB       Evasione conto lavoro, spedizione componenti, nel caso un componente (sec) gestito a lotti con prelievo automatico 
 * 								 trova il lotto ed assegna le quantità, ma lo stato avanzamento deve essere lo stato avanzamento scelto dal utente 
 * 								 nel GUI di lancio del evasione.
 * 32571  15/01/2021   MBH       In caso di causale C=P il controllo di giacenza deve essere fatto con il mag di prelievo (mag della riga sec).
 * 33748   11/06/2021  LTB       Una seconda evasione in spedizione delle componenti con stato avanzamento provvisorio
 * 								 nessuna riga estratta, non viene proposto nulla
 * 34827   13/12/2021  LTB       Modifica il nome della metodo setRigaConQtaInteroCambiato 
 * 34783   03/12/2021  SZ		 In evasione acquisto aggiungere ilcontrolllo forzabile sull'articolo escluso dalle acquisto
 * 35433   10/03/2022  YBA       Aggiunto il metodo resetVersioneSal
 * 35681   15/04/2022  YBA       Eliminato problema di ClassCastException
 * 35639  02/05/2022   LTB       Gestione assegnazione dei lotti (con proposizione automatica o manuale dei lotti) 
 * 								 che consideri quanto già assegnato nello stesso documento  	 *
 * 36157   29/06/2022  MR        Se abilitato il cambio magazzino, abbiamo il bisogno di mettere il magazzino disabilitata nella griglia evasione
								 per ogni riga da sola.
 * 36394   03/08/2022  SZ 		 Se acceso permettere evasione di rientro prodotti (acquisto o conto lavoro) nel caso l'utente inserisca 0 e flag saldo. In questo caso la riga è saldata a zero.
 * 36869   26/10/2022  SZ	     Evitare il nullPointerException nel caso di magazino == null.
 * 39402   24/07/2023  SZ        Scale errato se il database ha le quantità a 6 decimali
*/

public class DocEvaAcqRigaPrm
    extends YDocumentoAcqRigaPrm implements HasDocEvaAcqRiga {

  //Fix 34783 inizio	
  public static List iErroriForceable = new ArrayList();
  public static String ERR_FORZABILE_ART_ESCLUSIONE  = "THIP_BS209";
  static {
	  iErroriForceable.add(ERR_FORZABILE_ART_ESCLUSIONE);
  }
  
  //Fix 34783 fine
  //ini FIX 1699
  private boolean isAbilitaCheckAllCondizionata = true;
  //fine FIX 1699
  private boolean iRigaConQtaInteroCambiato = false;//Fix 19840
  /**
   *
   */
  private DocEvaAcqRiga iDocEvaAcqRiga;

  /**
   *
   */
  private boolean isInDettaglio = false;

  /**
   *
   */
  private boolean isRigaForzata = false;

  /**
   * Mantiene il numero di riga da assegnare ad una nuova riga secondaria
   */
  private Integer iNumeroRigaDocumentoSecUtile = new Integer(1);

  //Attributi
  //protected OneToMany iRigheSecondarie;

  // fix 11415
  private DocEvaAcqConfiguratore iConfiguratore = null;
  private boolean iRicalcola = true;
  // fine fix 11451

  // fix 11822 >
  private boolean isNonVisibile = false;
  // fix 11822 <

  
  // 29868 inizio
  private boolean iDaClipboardFactory = false;
  // 29868 fine
  /**
   *
   */
  public DocEvaAcqRigaPrm() {
    super.iTestata = new Proxy(DocEvaAcq.class);
    this.setSpecializzazioneRiga(RIGA_PRIMARIA);
    super.iRigheLotto = new OneToMany(DocEvaAcqRigaLottoPrm.class, this, 15, true);
    this.iRigheSecondarie = new OneToMany(it.thera.thip.acquisti.ordineAC.
                                          DocEvaAcqRigaSec.class, this, 15, true);
    // Fix 1674
    //this.iDocEvaAcqRiga = new DocEvaAcqRiga(this);
    this.iDocEvaAcqRiga = (DocEvaAcqRiga) Factory.createObject(DocEvaAcqRiga.class);
    this.iDocEvaAcqRiga.setRigaDoc(this);
    this.iDocEvaAcqRiga.getRigaDoc().setRigaSaldata(DocEvaAcqRiga.
        DEFAULT_RIGA_ESTRATTA);
    // Fine fix 1674
    this.setInDettaglio(false);

    // fix 11473 >
    this.getLivelloControlloDisp().add(0, new Character(LivelliControlloDisponibilita.NA));
    this.getLivelloControlloDisp().add(1, new BigDecimal(0.00));
    // fix 11473 <

  }

  public DocEvaAcqRiga getDocEvaAcqRiga() {
    return iDocEvaAcqRiga;
  }

  public void setInDettaglio(boolean isDettaglio) {
    this.isInDettaglio = isDettaglio;
  }

  public boolean isInDettaglio() {
    return this.isInDettaglio;
  }

  public void setRigaForzata(boolean isForzata) {
    this.isRigaForzata = isForzata;
  }

  public boolean isRigaForzata() {
    return this.isRigaForzata;
  }

  //29868 inizio
  public void setDaClipboardFactory(boolean isDaClipboard) {
    this.iDaClipboardFactory = isDaClipboard;
  }

  public boolean isDaClipboardFactory() {
    return this.iDaClipboardFactory;
  }
  //29868 fine
  
  //Fix 34783
  public List getIdErroriForceable(){
		 return iErroriForceable; 	
  }

  //Fix 34783

  /**
   *
   * @return
   * @throws java.sql.SQLException
   */
  protected TableManager getTableManager() throws java.sql.SQLException {
    return DocEvaAcqRigaPrmTM.getInstance();
  }

  /**
   * Valorizza l'attributo AnnoDocumento.
   */
  public void setAnnoDocumento(String annoDocumento) {
    super.setAnnoDocumento(annoDocumento);
    iRigheSecondarie.setFatherKeyChanged();
  }

  /**
   * Valorizza l'attributo NumeroDocumento.
   */
  public void setNumeroDocumento(String numeroDocumento) {
    super.setNumeroDocumento(numeroDocumento);
    iRigheSecondarie.setFatherKeyChanged();
  }

  /**
   * Valorizza l'attributo NumeroRigaDocumento.
   */
  public void setNumeroRigaDocumento(Integer numeroRigaDocumento) {
    super.setNumeroRigaDocumento(numeroRigaDocumento);
    // fix 11614 >
    int nriga = getNumeroRigaDocumento().intValue();
    if (nriga < 0) {
      nriga = -nriga;
    }
    setSequenzaRiga(nriga);
    //setSequenzaRiga(getNumeroRigaDocumento().intValue()); //fix 2322
    // fix 11614 <
    iRigheSecondarie.setFatherKeyChanged();
  }

  /**
   * Valorizza l'attributo DettaglioRigaDocumento.
   */
  public void setDettaglioRigaDocumento(Integer dettaglioRigaDocumento) {
    super.setDettaglioRigaDocumento(dettaglioRigaDocumento);
    iRigheSecondarie.setFatherKeyChanged();
  }

  //Implentazione RigaPrimaria
  public List getRigheSecondarie() {
    return getRigheSecondarieInternal();
  }

  public boolean isSalvaTestata() {
  	if(isDaClipboardFactory()) //29868
  		return super.isSalvaTestata(); //29868
    return false;
  }

  public void setSalvaTestata(boolean salvaTestata) {
  }

  protected OneToMany getRigheSecondarieInternal() {
    if (iRigheSecondarie.isNew()) {
      iRigheSecondarie.retrieve();
    }
    return iRigheSecondarie;
  }

  /**
   *
   * @param result
   * @return
   */
  public boolean initializeOwnedObjects(boolean result) {
    boolean bo = super.initializeOwnedObjects(result);

    // fix 11473 >
    /*
    boolean rigaEstratta = this.getDocEvaVenRiga().isRigaEstratta() ||
        this.isOnDB();
    */
    boolean rigaEstratta = this.getDocEvaAcqRiga().isRigaEstratta();
    // fix 11473 <

    this.getDocEvaAcqRiga().setRigaEstratta(rigaEstratta);
    this.setRigaForzata(rigaEstratta);
    this.getDocEvaAcqRiga().inizializzaQtaRiga();
    this.getDocEvaAcqRiga().setRigaEstratta(rigaEstratta);
    //this.getDocEvaAcqRiga().logQta("initializeOwnedObjects di '" + this.getKey() +"' isOnDB = '" + this.isOnDB() + "'" );
    if (GestoreEvasioneAcquisto.get().isAbilitaControlloConcorrenza()) {
      DocumentoOrdineRiga rigaOrd = this.getRigaOrdine();
      if (rigaOrd != null) {
        //ini Fix 1922
        //rigaOrd.abilitaTask(DocEvaAcq.TASK_ID_ESTRAZIONE);
        rigaOrd.getTestata().abilitaTask(DocEvaAcq.TASK_ID_ESTRAZIONE);
        //fine Fix 1922
      }
    }
    return bo;
  }

  /**
   *
   */
  protected void creaOldRiga() {
    iOldRiga = (DocEvaAcqRigaPrm) Factory.createObject(DocEvaAcqRigaPrm.class);
  }

  /**
   * setEqual
   * @param obj
   * @throws CopyException
   */
  public void setEqual(Copyable obj) throws CopyException {
    super.setEqual(obj);
    DocEvaAcqRigaPrm doc = (DocEvaAcqRigaPrm) obj;
    iRigheSecondarie.setEqual(doc.iRigheSecondarie);
  }

  public Integer getNumeroRigaDocumentoSecUtile() {
    return iNumeroRigaDocumentoSecUtile;
  }

  private void setNumeroRigaDocumentoSecUtile(Integer i) {
    iNumeroRigaDocumentoSecUtile = i;
  }

  /**
   * Crea una riga documento da una riga ordine.
   * @param rigaOrdine
   * @return la riga generata, null se no è stato possibile generarla perchè non
   * è stata trovata una causale riga documento appropriata
   */
  public static DocEvaAcqRigaPrm creaRiga(DocEvaAcq doc,
                                          OrdineAcquistoRigaPrm rigaOrdine) {
    DocEvaAcqRigaPrm riga = null;
    CausaleDocumentoRigaAcq cauRigaDoc = doc.trovaCausaleRigaDocAcq(rigaOrdine);
    if (cauRigaDoc == null) {
      doc.getRigheNonEstratte().put(rigaOrdine,
                                    " causale riga ord '" +
                                    rigaOrdine.getCausaleRigaKey() +
                                    "' non referenziata");
    }
    // Inizio 6652
    else if (!controllaPrezzoSecondoCausale(rigaOrdine, cauRigaDoc)){
      doc.getRigheNonEstratte().put(rigaOrdine,
          " La causale riga doc '" +cauRigaDoc.getIdCausale()+
          "' prevede un prezzo significativo per la riga ordine");
    }
    // Fine 6652
    else {
      riga = (DocEvaAcqRigaPrm) Factory.createObject(DocEvaAcqRigaPrm.class);

      // testata
      riga.setTestata(doc);

      // key
      riga.setIdAzienda(doc.getIdAzienda());
      riga.setFatherKey(doc.getKey());
      riga.setNumeroRigaDocumento(new Integer(doc.getNumeroNuovaRiga()));
      /** @todo NUM RIGA NEG */
      riga.setNumeroRigaDocumento(new Integer(0 - doc.getNumeroNuovaRiga()));

      // attributi valorizzati dalla testata del documento
      riga.setCausaleRiga(cauRigaDoc);
      //Fix 6253 - inizio
      /*
       La seguente riga è stata commentata perchè fa sì che se una sola riga
       viene messa in stato PROVVISORIO, la check mette in stato PROVVISORIO
       anche la testata e quindi tutte le righe seguenti sono messe in stato
       PROVVISORIO
       */
      //riga.setStatoAvanzamento(doc.getStatoAvanzamento());
      riga.setStatoAvanzamento(doc.getStatoAvanzamentoUt());
      //Fix 6253 - fine

      // attributi di riga

      // Aggiornamento dati da riga ordine
      riga.getDocEvaAcqRiga().aggiornaAttributiDaRigaOrdine(rigaOrdine);
      riga.getDocEvaAcqRiga().aggiornaAttributiDaCausale(doc); // Fix 21503
      /*
       GestoreEvasioneAcquisto.get().println("Creata riga doc prm :'" + riga.getKey() +
            "' da riga ord prm :'" + rigaOrdine.getKey() + "' stato av. = '" +
            riga.getStatoAvanzamento() + "'");
       */
      // Righe lotto
      // verifica se è creabile
      if (riga.getDocEvaAcqRiga().isAddable(riga.getDocEvaAcqRiga(), rigaOrdine)) {
        // fix 11415
        if (riga.getArticolo()!=null && riga.getArticolo().hasVersioneEstesa() &&
            riga.getArticolo().getSchemaCfg()!=null){
          riga.settaConfiguratore();
        }
        // fine fix 11415
        List righeLottoOrdine = rigaOrdine.getRigheLotto();
        if (righeLottoOrdine != null) {
          Iterator iterLO = righeLottoOrdine.iterator();
          int numRiga = riga.getDocEvaAcqRiga().
              getNumeroRigaDocumentoLottoUtile().intValue();
          while (iterLO.hasNext()) {
            riga.getDocEvaAcqRiga().setNumeroRigaDocumentoLottoUtile(new
                Integer(numRiga));
            OrdineAcquistoRigaLotto obj = (OrdineAcquistoRigaLotto) iterLO.next();
            DocEvaAcqRigaLottoPrm rigaLotto = riga.creaRigaLottoPrm(obj);
            if (rigaLotto != null) {
              /*
                             GestoreEvasioneAcquisto.get().println("Creata riga lotto doc prm :'" + rigaLotto.getKey() +
               "' da riga lotto ord prm :'" + obj.getKey() + "' stato av. = '" +
                  rigaLotto.getStatoAvanzamento() + "'");
               */
              numRiga++;
            }
          }
        }
        char oldStatoAvanzamento = riga.getStatoAvanzamento();
        if (!doc.isDocumentoDiReso()) {
          ErrorMessage emLottiPrm = null;
          char statoForzatura = StatoAvanzamento.PROVVISORIO;
          riga.forzaStatoAvanzamento(statoForzatura, false);
          emLottiPrm = riga.getDocEvaAcqRiga().checkQuadraturaLotti();
          if (emLottiPrm == null) {
            if (emLottiPrm == null &&
                (oldStatoAvanzamento == StatoAvanzamento.DEFINITIVO)) {
              statoForzatura = oldStatoAvanzamento;
              riga.forzaStatoAvanzamento(statoForzatura, false);
              emLottiPrm = riga.getDocEvaAcqRiga().checkQuadraturaLotti();
              if (emLottiPrm != null) {
                riga.forzaStatoAvanzamento(StatoAvanzamento.PROVVISORIO, false);
                //...FIX 3099 inizio
                if (riga.controllaLottiPrm()) {
                  riga.forzaStatoAvanzamento(StatoAvanzamento.DEFINITIVO, false);
                  emLottiPrm = null;
                }
              }
            }
          }
          if (emLottiPrm != null) {
            String msg =
                "Errore di quadratura lotti su riga prm riferita alla riga ordine prm:'" +
                riga.getDocEvaAcqRiga().getRifRigaOrdineFormattato() +
                "' in stato av.:'" +
                statoForzatura + "' -> " + emLottiPrm.getLongText();
            doc.getRigheWarnings().put(riga.getRigaOrdine(), msg);
            GestoreEvasioneAcquisto.get().println(msg);
          }
        }

        //Controllo dichiarazione matricole su riga primaria
        //Fix 6583 - inizio
        ErrorMessage emDichMatr = riga.getDocEvaAcqRiga().checkDichiarazioneMatricole();
        if (emDichMatr != null &&
                        doc.getCausale().getAzioneMagazzino() == AzioneMagazzino.ENTRATA) {		//Fix 9283
          String msg =
            "Errore di quadratura dichiarazione matricole su riga prm riferita alla riga ordine prm :'" +
            riga.getDocEvaAcqRiga().getRifRigaOrdineFormattato() + "': -> " +
            emDichMatr.getLongText();
          doc.getRigheWarnings().put(riga.getRigaOrdine(), msg);
          //GestoreEvasioneVendita.get().println(msg);//Fix 34783
          GestoreEvasioneAcquisto.get().println(msg);//Fix 34783
          riga.sistemaQuantitaDopoVariazioneStatoAv(StatoAvanzamento.PROVVISORIO);
        }
        //Fix 6583 - fine

        //Fix 34783 Inizio
        ErrorMessage emArticoloEsculsion = riga.checkArticoloEsclusione();
        if(emArticoloEsculsion != null) {
        	String msg =
                    riga.getDocEvaAcqRiga().getRifRigaOrdineFormattato() + " : -> " +
                    emArticoloEsculsion.getLongText();
                  doc.getRigheWarnings().put(riga.getRigaOrdine(), msg);
                  GestoreEvasioneAcquisto.get().println(msg);
        }
        //Fix 34783 Fine
        
        // Righe secondarie
        /** @todo CONTROLLARE PER impostaCausale !!! */
        List righeSecondarieOrdine = rigaOrdine.getRigheSecondarie();
        if (righeSecondarieOrdine != null) {
          Iterator iterSE = righeSecondarieOrdine.iterator();
          int numRiga = riga.getNumeroRigaDocumentoSecUtile().intValue();
          while (iterSE.hasNext()) {
            riga.setNumeroRigaDocumentoSecUtile(new Integer(numRiga));
            OrdineAcquistoRigaSec obj = (OrdineAcquistoRigaSec) iterSE.next();
            // Fix 08929 inizio
            if (obj.getDatiComuniEstesi().getStato() != DatiComuniEstesi.VALIDO)
              continue;
            // Fix 08929 fine
            if (generaRigaSecDocOk(doc, obj)) {  //Fix 5179
            DocEvaAcqRigaSec rigaSec = riga.creaRigaSec(obj);
            if (rigaSec != null) {
              /*
                             GestoreEvasioneAcquisto.get().println("Creata riga doc sec :'" + rigaSec.getKey() +
                  "' da riga ord sec :'" + obj.getKey() +  "' stato av. = '" +
                  rigaSec.getStatoAvanzamento() + "'");
               */
              numRiga++;
              /** **/
              if (!doc.isDocumentoDiReso()) {
                ErrorMessage emLottiSec = null;
                String longTextError = "";
                char oldStatoAvanzamentoSec = rigaSec.getStatoAvanzamento();
                char statoForzatura = StatoAvanzamento.PROVVISORIO;
                rigaSec.forzaStatoAvanzamento(statoForzatura);
                emLottiSec = rigaSec.getDocEvaAcqRiga().checkQuadraturaLotti();
                boolean okControlloLotti = true;
                if (emLottiSec == null) {
                  if (emLottiSec == null &&
                      (oldStatoAvanzamentoSec == StatoAvanzamento.DEFINITIVO)) {
                    statoForzatura = oldStatoAvanzamentoSec;
                    rigaSec.forzaStatoAvanzamento(oldStatoAvanzamentoSec);
                    emLottiSec = rigaSec.getDocEvaAcqRiga().
                        checkQuadraturaLotti();
                    if (emLottiSec != null) {
                      rigaSec.forzaStatoAvanzamento(StatoAvanzamento.
                          PROVVISORIO);
                      //...FIX 3099 inizio
//                      if(rigaSec.controllaLottiSec()) {
//                        rigaSec.forzaStatoAvanzamento(StatoAvanzamento.DEFINITIVO);
//                        emLottiSec = null;
//                      }
                    }
                    // Inizio 3864
                    okControlloLotti = rigaSec.controllaLottiSec();
                    if (okControlloLotti) {
                      rigaSec.forzaStatoAvanzamento(StatoAvanzamento.DEFINITIVO);
                      emLottiSec = null;
                    }
                    else {
                      rigaSec.forzaStatoAvanzamento(StatoAvanzamento.
                          PROVVISORIO);
                      longTextError = "Giacenza dei lotti non disponibile";
                    }
                    // Fine 3864
                  }
                }
                if (emLottiSec != null || !okControlloLotti) {
                  if (emLottiSec != null)
                    longTextError = emLottiSec.getLongText();
                  String msg =
                      "Errore di quadratura lotti su riga sec riferita alla riga ordine sec:'" +
                      rigaSec.getDocEvaAcqRiga().getRifRigaOrdineFormattato() +
                      "' in stato av.:'" +
                      statoForzatura + "' -> " + longTextError;
                  doc.getRigheWarnings().put(rigaSec.getRigaOrdine(), msg);
                  if (oldStatoAvanzamento == StatoAvanzamento.DEFINITIVO &&
                      rigaSec.getStatoAvanzamento() ==
                      StatoAvanzamento.PROVVISORIO) {
                    riga.forzaStatoAvanzamento(StatoAvanzamento.PROVVISORIO, false);
                  }
                }
              }

              //Controllo dichiarazione matricole su righe secondarie
              //Fix 6583 - inizio
              emDichMatr = rigaSec.getDocEvaAcqRiga().checkDichiarazioneMatricole();
              if (emDichMatr != null &&
                              doc.getCausale().getAzioneMagazzino() == AzioneMagazzino.USCITA) {		//Fix 9283
                String msg =
                  "Errore di quadratura dichiarazione matricole su riga sec riferita alla riga ordine sec :'" +
                  rigaSec.getDocEvaAcqRiga().getRifRigaOrdineFormattato() + "': -> " +
                  emDichMatr.getLongText();
                doc.getRigheWarnings().put(rigaSec.getRigaOrdine(), msg);
                GestoreEvasioneVendita.get().println(msg);
                rigaSec.sistemaQuantitaDopoVariazioneStatoAv(StatoAvanzamento.PROVVISORIO);
              }
              //Fix 6583 - fine
            }
            else {
              // FIX 01363
              if ( ( (OrdineAcquistoRiga) riga.getRigaOrdine()).getCausaleRiga().
                  getTipoDistintaLavEsterna()
                  == TipoDistintaLavEsterna.COMP_PROD) {
                String msg =
                    " Riga senza componenti con tipo distinta lav. est. = '" +
                    TipoDistintaLavEsterna.COMP_PROD + "'";
                String mTmp = (String) ( (DocEvaAcq) riga.getTestata()).
                    getRigheNonEstratte().get(riga.getRigaOrdine());
                if (mTmp != null) {
                  msg += mTmp;
                }
                ( (DocEvaAcq) riga.getTestata()).getRigheNonEstratte().put(riga.
                    getRigaOrdine(), msg);
                riga = null;
                // fix 3003
                break;
                // fine fix 3003
              }
            }
          }
        }
        }
        if (riga != null && riga.isSoloGestioneRigheSecondarie()) {

          if (!rigaOrdine.isPropostaSaldoManuale() &&
              !rigaOrdine.isSaldoManuale()) {
            if (riga.getRigheSecondarie() == null ||
                riga.getRigheSecondarie().isEmpty()) {
              String msg = " spedizione componenti, riga prm senza componenti ";
              String mTmp = (String) ( (DocEvaAcq) riga.getTestata()).
                  getRigheNonEstratte().get(rigaOrdine);
              if (mTmp != null) {
                msg += mTmp;
              }
              ( (DocEvaAcq) riga.getTestata()).getRigheNonEstratte().put(
                  rigaOrdine, msg);
              riga = null;
            }
          }
        }
      }
      else {
        riga = null;
      }
    }
    return riga;
  }

  //ini FIX 1699
  /**
   *
   * @param components
   * @return
   */
  public java.util.Vector checkAll(BaseComponentsCollection components) {
    Vector errors = new Vector();
    boolean check = this.isAbilitaCheckAllCondizionata() ? this.isRigaEstratta() : true;
    if (check) {
      //Fix 9224 inizio
      boolean isAbilitataCheckAssegnazioni = isAbilitaCheckAssegnazioni();
      setAbilitaCheckAssegnazioni(true);
      //Fix 9224 fine
      errors = super.checkAll(components);
      setAbilitaCheckAssegnazioni(isAbilitataCheckAssegnazioni); //Fix 9224
      // fix 11473 >
      //ErrorMessage em = this.ckeckControlloDisp();
      ErrorMessage em = this.ckeckControlloDisp(errors);
      // fix 11473 <
      if (em != null) {
        errors.add(em);
      }
      //ini Fix 2218
      List ems = this.checkCondCompEvas();
      boolean addAll = errors.addAll(ems);
      /* ?? non viene chiamato implicitamente ?? */
      em = this.checkStatoAvanzamento();
      if (em != null) {
        errors.add(em);
      }
      em = this.checkRigaModificata();
      if (em != null) {
        errors.add(em);
      }

      // fix 12062 >
      if (this.isRigaEstratta()) {
        ErrorMessage err = this.controllaConfNeutra();
        if (err != null) {
          errors.add(err);
        }
      }
      // fix 12062 <

      //Fix 34783 inizio
      em = checkAltriErroriForzabile(errors);
      if (em != null) {
          errors.add(em);
      }
      //Fix 34783 fine
    }
    return errors;
  }

  //fine FIX 1699


  //ini FIX 1699
  public void setAbilitaCheckAllCondizionata(boolean abilita) {
    isAbilitaCheckAllCondizionata = abilita;
  }

  public boolean isAbilitaCheckAllCondizionata() {
    return isAbilitaCheckAllCondizionata;
  }

  //ini Fix 1922
  //Fix 19840 inizio
  /**
   * @deprecated sostituito con setQtaCambiata
   * */
  public void setRigaConQtaInteroCambiato(boolean cambiato) {
    iRigaConQtaInteroCambiato = cambiato;
  }
  /**
   * @deprecated sostituito con isQtaCambiata
   * */
  public boolean isRigaConQtaInteroCambiato() {
    return iRigaConQtaInteroCambiato;
  }

  //Fix 19840 fine
  
  //34827 inizio 
  //Modifica del nome metodo setQtaCambiata invece di setRigaConQtaInteroCambiato, perche è usato sia per qta intera o no
  //
  public void setQtaCambiata(boolean cambiato) {
	  setRigaConQtaInteroCambiato(cambiato);
  }

  public boolean isQtaCambiata() {
	  return isRigaConQtaInteroCambiato();
  }
  //34827 fine
  
  public ErrorMessage checkRigaModificata() {
    ErrorMessage em = null;
    String msg = ""; //Fix 03262 SL
    boolean isError = false;
    // Federico Crosa fix 3309: modificata condizione sottostante
    boolean eseguiControllo = this.isRigaEstratta() && !this.isRigaForzata();
    if (eseguiControllo) {
      // descrizione articolo
    	 //String descrArtRigaDoc = this.getDescrizioneArticolo(); //DSSOF3 70761
    	String descrArtRigaDoc = this.getDescrizioneExtArticolo(); //DSSOF3 70761
      String descrArtRigaOrd = descrArtRigaDoc;
      DocumentoOrdineRiga rigaOrd = this.getRigaOrdine();
      //Fix 29790 inizio
      if (rigaOrd == null)
      	return null;
      //Fix 29790 Fine
      
      if (rigaOrd != null) {
    	     //descrArtRigaOrd = rigaOrd.getDescrizioneArticolo(); //DSSOF3 70761
    	  descrArtRigaOrd = rigaOrd.getDescrizioneExtArticolo(); //DSSOF3 70761
      }
      // Fix 2161
      //isError |= !descrArtRigaDoc.equals(descrArtRigaOrd);
      if (descrArtRigaOrd == null) {
        descrArtRigaOrd = "";
      }
      //Fix 03262 SL - Fix analoga alla 03242 fatta per gli ordini vendita;
      if (descrArtRigaDoc == null)
        descrArtRigaDoc = "";

      // Federico Crosa fix 3309: modificata condizione sottostante
      boolean isNok = !descrArtRigaDoc.trim().equals(descrArtRigaOrd.trim());
      isError |= isNok;
      //Fix 03262 SL - Gestito il msg d'errore
      //isError |= !descrArtRigaDoc.trim().equals(descrArtRigaOrd);
      // fine fix 2161
      if (isNok) {
        msg += " - descrizione articolo (" + descrArtRigaDoc + " <> " +
            descrArtRigaOrd + ") - ";
      }
      //Fix 03262 SL - fine

      // controllo sconto articolo 1
      BigDecimal roSC1 =  rigaOrd == null ? null : rigaOrd.getScontoArticolo1(); // fix 11951
      if (roSC1 == null) {
        roSC1 = GestoreEvasioneAcquisto.get().getBigDecimalZero();
      }
      BigDecimal rdSC1 = this.getScontoArticolo1();
      if (rdSC1 == null) {
        rdSC1 = GestoreEvasioneAcquisto.get().getBigDecimalZero();
      }
      //Fix 03262 SL - Gestito il msg d'errore
      //isError |= roSC1.compareTo(rdSC1) != 0;
      isNok = roSC1.compareTo(rdSC1) != 0;
      isError |= isNok;
      if (isNok) {
        msg += " - sconto articolo1 (" + roSC1 + " <> " + rdSC1 + ") - ";
      }
      //Fix 03262 SL - fine

      // controllo sconto articolo 1
      BigDecimal roSC2 =  rigaOrd == null ? null : rigaOrd.getScontoArticolo2(); // fix 11951
      if (roSC2 == null) {
        roSC2 = GestoreEvasioneAcquisto.get().getBigDecimalZero();
      }
      BigDecimal rdSC2 = this.getScontoArticolo2();
      if (rdSC2 == null) {
        rdSC2 = GestoreEvasioneAcquisto.get().getBigDecimalZero();
      }
      //Fix 03262 SL - Gestito il msg d'errore
      //isError |= roSC2.compareTo(rdSC2) != 0;
      isNok = roSC2.compareTo(rdSC2) != 0;
      isError |= isNok;
      if (isNok) {
        msg += " - sconto articolo2 (" + roSC2 + " <> " + rdSC2 + ") - ";
      }
      //Fix 03262 SL - fine

      // prc sconto intestatario
      BigDecimal roPrcSCI =  rigaOrd == null ? null : rigaOrd.getPrcScontoIntestatario(); // fix 11951
      if (roPrcSCI == null) {
        roPrcSCI = GestoreEvasioneAcquisto.get().getBigDecimalZero();
      }
      BigDecimal rdPrcSCI = this.getPrcScontoIntestatario();
      if (rdPrcSCI == null) {
        rdPrcSCI = GestoreEvasioneAcquisto.get().getBigDecimalZero();
      }
      //Fix 03262 SL - Gestito il msg d'errore
      //isError |= roPrcSCI.compareTo(rdPrcSCI) != 0;
      isNok = roPrcSCI.compareTo(rdPrcSCI) != 0;
      isError |= isNok;
      if (isNok) {
        msg += " - prc sconto intestatario (" + roPrcSCI + " <> " + rdPrcSCI +
            ") - ";
      }
      //Fix 03262 SL - fine

      // prc sconto modalita
      BigDecimal roPrcSCM =  rigaOrd == null ? null : rigaOrd.getPrcScontoModalita(); // fix 11951
      if (roPrcSCM == null) {
        roPrcSCM = GestoreEvasioneAcquisto.get().getBigDecimalZero();
      }
      BigDecimal rdPrcSCM = this.getPrcScontoModalita();
      if (rdPrcSCM == null) {
        rdPrcSCM = GestoreEvasioneAcquisto.get().getBigDecimalZero();
      }
      //Fix 03262 SL - Gestito il msg d'errore
      //isError |= roPrcSCM.compareTo(rdPrcSCM) != 0;
      isNok = roPrcSCM.compareTo(rdPrcSCM) != 0;
      isError |= isNok;
      if (isNok) {
        msg += " - prc sconto modalità (" + roPrcSCM + " <> " + rdPrcSCM +
            ") - ";
      }
      //Fix 03262 SL - fine

      // sconto
      String roSC =  rigaOrd == null ? null : rigaOrd.getIdSconto(); // fix 11951
      if (roSC == null) {
        roSC = "";
      }
      String rdSC = this.getIdSconto();
      if (rdSC == null) {
        rdSC = "";
      }
      //Fix 03262 SL - Gestito il msg d'errore
      //isError |= !roSC.equals(rdSC);
      isNok = !roSC.equals(rdSC);
      isError |= isNok;
      if (isNok) {
        msg += " - sconto (" + roSC + " <> " + rdSC + ") - ";
      }
      //Fix 03262 SL - fine

      // sconto modalita
      String roSCM =  rigaOrd == null ? null : rigaOrd.getIdScontoMod(); // fix 11951
      if (roSCM == null) {
        roSCM = "";
      }
      String rdSCM = this.getIdScontoMod();
      if (rdSCM == null) {
        rdSCM = "";
      }
      //Fix 03262 SL - Gestito il msg d'errore
      //isError |= !roSCM.equals(rdSCM);
      isNok = !roSCM.equals(rdSCM);
      isError |= isNok;
      if (isNok) {
        msg += " - sconto modalità  (" + roSCM + " <> " + rdSCM + ") - ";
      }
      //Fix 03262 SL - fine

      if (isError) {
        String params = "'" +
            this.getDocEvaAcqRiga().getRifRigaOrdineFormattato() + "' " + msg;
        em = new ErrorMessage("THIP_BS308", params);
      }
    }
    return em;
  }

  //fine Fix 1922
  //fine FIX 1699

  /**
   * Restituisce il livello di controllo disponibilità in base al tipo, giacenza
   * o disponibilità
   * @return List a due valori, get(0) = Character indicante il livello, get(1) = BigDecimal
   * indicante il valore di giacenza media o disponibilità
   */
  // fix 11473 >
  /*
  public List getLivelloControlloDisp() {
    return getLivelloControlloDisp(false);
  }
  */
  // fix 11473 <

  /**
   * Restituisce il livello di controllo disponibilità in base al tipo, giacenza
   * o disponibilità
   * @return List a due valori, get(0) = Character indicante il livello, get(1) = BigDecimal
   * indicante il valore di giacenza media o disponibilità
   */
  // fix 11473 >
  //public List getLivelloControlloDisp(boolean addWarnings) {
  public void assegnaLivelloControlloGiaDisp() {
    BigDecimal qtaDaSpedireDisponibile = null;//Fix 23523
    boolean addWarnings = true;
    // fix 11473 <
    List valori = new ArrayList();
    char livello = LivelliControlloDisponibilita.DEFAULT;
    BigDecimal qtaRif = GestoreEvasioneAcquisto.get().getBigDecimalZero();
    if (this.getDocEvaAcqRiga().hasControlloDisp()) {
      // fix 11473 >
      char controlloDisp = ((DocEvaAcq) getTestata()).getControlloDisp();
      if (this.getDocEvaAcqRiga().cambioControllo()){
        controlloDisp = TipiControlloDisponibilita.GIACENZA;
      }
      else if (this.getArticolo()!=null && !this.getArticolo().isArticLotto()){
        controlloDisp = TipiControlloDisponibilita.NESSUNO;
      }
      switch (controlloDisp) {
      //switch ( ( (DocEvaAcq) getTestata()).getControlloDisp()) {
      // fix 11473 <
        case TipiControlloDisponibilita.GIACENZA: {
          //if (!this.getDocEvaAcqRiga().isDocumentoDiReso()) { // fix 11473
            BigDecimal giacenzaNettaInUMPrm = GestoreEvasioneAcquisto.get().
                getBigDecimalZero();
            BigDecimal qtaDaSpedireInUMPrm = GestoreEvasioneAcquisto.get().
                getBigDecimalZero();
            livello = LivelliControlloDisponibilita.ROSSO;
            String msg = "";
            OrdineAcquistoRiga rigaOrd = (OrdineAcquistoRiga)this.getRigaOrdine();
            boolean analizzaComponenti = true;
            if (rigaOrd != null &&
                rigaOrd.getCausaleRiga().getTipoDistintaLavEsterna() ==
                TipoDistintaLavEsterna.COMP_PROD) {
              analizzaComponenti = false;
            }
            
            //Fix 32571 MBH : Non ho capito perche viene fatto il controllo giacenza con la riga prm in caso di C=P
            // Secondo me deve essere fatto con le righe sec sempre (Confirmato anche da Davide)
            // Così viene usato correttamente il magaz di prelievo (mag della riga sec) 
            analizzaComponenti = true;//Fix 32571
            
            if ( ( (DocEvaAcq)this.getTestata()).isDocumentoDiContoLavoro() &&
                analizzaComponenti) {
              // calcolo la giacenza sui componenti e
              int totSec = this.getRigheSecondarie().size();
              int totRosso = 0;
              int totGiallo = 0;
              int totVerde = 0;
              Iterator iterComponenti = this.getRigheSecondarie().iterator();
              while (iterComponenti.hasNext()) {
                DocEvaAcqRigaSec rigaSec = (DocEvaAcqRigaSec) iterComponenti.
                    next();
                qtaDaSpedireInUMPrm = rigaSec.getDocEvaAcqRiga().
                    getQtaDaSpedireInUMPrm();
                List livCDisp = rigaSec.getLivelloControlloDisp();
                char livelloTmp = ( (Character) livCDisp.get(0)).charValue();
                giacenzaNettaInUMPrm = (BigDecimal) livCDisp.get(1);
                if (livelloTmp == LivelliControlloDisponibilita.ROSSO || livelloTmp == LivelliControlloDisponibilita.GIALLO) {
                  msg += " Componente '" +
                      rigaSec.getDocEvaAcqRiga().getRifRigaOrdineFormattato() +
                      "' Articolo '" + rigaSec.getIdArticolo();
                  if (livelloTmp == LivelliControlloDisponibilita.ROSSO) {
                    totRosso += 1;
                    msg += " : errore ";
                  }
                  if (livelloTmp == LivelliControlloDisponibilita.GIALLO) {
                    //Fix 23523 inizio
                    if(qtaDaSpedireDisponibile == null)
                      qtaDaSpedireDisponibile = getQtaDaSpedirePrmDaSec(rigaSec,giacenzaNettaInUMPrm);
                    else{
                      BigDecimal qta = getQtaDaSpedirePrmDaSec(rigaSec,giacenzaNettaInUMPrm);
                      if(qta.compareTo(qtaDaSpedireDisponibile)<0)
                        qtaDaSpedireDisponibile = qta;
                    }
                    //Fix 23523 fine
                    totGiallo += 1;
                    msg += " : problemi ";
                  }
                  // fix 11951 >
                  //String qtaDaSpedireInUMPrmStr = qtaDaSpedireInUMPrm != null ? qtaDaSpedireInUMPrm.setScale(2, BigDecimal.ROUND_HALF_EVEN).toString() : "";//Fix 30871
				  String qtaDaSpedireInUMPrmStr = qtaDaSpedireInUMPrm != null ? Q6Calc.get().setScale(qtaDaSpedireInUMPrm,2, BigDecimal.ROUND_HALF_EVEN).toString() : "";//Fix 30871
                  msg += "di giacenza, qta  = " + giacenzaNettaInUMPrm +
                    " su richiesta = " + qtaDaSpedireInUMPrmStr;
                  // fix 11951 <
                }
                else {
                  totVerde += 1;
                }
              }
              //Fix 23523 Inizio
              if (qtaDaSpedireDisponibile!=null && qtaDaSpedireDisponibile.compareTo(getDocEvaAcqRiga().getQtaDaSpedireInUMPrm())<0)
                giacenzaNettaInUMPrm = qtaDaSpedireDisponibile;
              //Fix 23523 Fine

              livello = totSec == totVerde ? LivelliControlloDisponibilita.VERDE : LivelliControlloDisponibilita.GIALLO;
              //if (livello == LivelliControlloDisponibilita.GIALLO && totSec == totRosso) {//Fix 23523
              if (livello == LivelliControlloDisponibilita.GIALLO && totRosso > 0) {//Fix 23523
                livello = LivelliControlloDisponibilita.ROSSO;
                giacenzaNettaInUMPrm = GestoreEvasioneAcquisto.get().getBigDecimalZero();//Fix 23523
              }
            }
            else {
              giacenzaNettaInUMPrm = this.getDocEvaAcqRiga().getGiacenzaNetta();
              qtaDaSpedireInUMPrm = getDocEvaAcqRiga().getQtaDaSpedireInUMPrm();
              if (qtaDaSpedireInUMPrm != null) {
                if (qtaDaSpedireInUMPrm.compareTo(giacenzaNettaInUMPrm) <= 0) {
                  livello = LivelliControlloDisponibilita.VERDE;
                }
                // fix 11473 >
                else if (giacenzaNettaInUMPrm.compareTo(new BigDecimal("0"))>0){
                  livello = LivelliControlloDisponibilita.GIALLO;
                }
                // fix 11473 <
              }
              if (livello == LivelliControlloDisponibilita.ROSSO) {
                giacenzaNettaInUMPrm = GestoreEvasioneAcquisto.get().getBigDecimalZero();//Fix 23955
                msg += " errore di giacenza, qta  = " + giacenzaNettaInUMPrm +
                    " su richiesta = " + qtaDaSpedireInUMPrm;
              }
              if (livello == LivelliControlloDisponibilita.GIALLO) {
                msg += " problemi di giacenza, qta  = " + giacenzaNettaInUMPrm +
                " su richiesta = " + qtaDaSpedireInUMPrm;
              }
            }
            if (this.getDocEvaAcqRiga().isAbilitaWarnings() && addWarnings && !msg.trim().equals("")) { // fix 11473
              ( (DocEvaAcq)this.getTestata()).getRigheWarnings().put(this.
                  getRigaOrdine(), msg);
            }
            GestoreEvasioneAcquisto.get().println(msg);
            qtaRif = giacenzaNettaInUMPrm;
          //} // fix 11473
        }
        break;
        case TipiControlloDisponibilita.DISPONIBILITA: {
          livello = LivelliControlloDisponibilita.GIALLO;
        }
        break;
        // fix 11473 >
        case TipiControlloDisponibilita.NESSUNO: {
          livello = LivelliControlloDisponibilita.NA;
        }
        break;
        // fix 11473 <
      }
      //Fix 23523 inizio
      if(livello == LivelliControlloDisponibilita.GIALLO || livello == LivelliControlloDisponibilita.ROSSO){
        //Fix 23955 inizio
        /*BigDecimal qtaUmPrm = qtaRif;
        BigDecimal qtaUmRif = convertiQuantita(getArticolo(),qtaUmPrm,getUMPrm(),getUMRif(),null);
        BigDecimal qtaUmSec = null;
        if(getUMSec()!= null)
           qtaUmSec = convertiQuantita(getArticolo(),qtaUmPrm,getUMPrm(),getUMSec(),null);*/
        QuantitaInUMRif qtaDaSpedire = convertiQuantitaPrm(qtaRif);
        //Fix 23955 fine
        this.setQtaDaSpedireInUMPrm(qtaDaSpedire.getQuantitaInUMPrm());
        this.setQtaDaSpedireInUMRif(qtaDaSpedire.getQuantitaInUMRif());
        this.setQtaDaSpedireInUMSec(qtaDaSpedire.getQuantitaInUMSec());
      }
      //Fix 23523 fine
      valori.add(0, new Character(livello));
      valori.add(1, qtaRif);
      // fix 11473 >
      this.getLivelloControlloDisp().add(0, valori.get(0));
      this.getLivelloControlloDisp().add(1, valori.get(1));
      abilitaRigaForzabile(livello);
      // fix 11473 <

    }
    //return valori; // fix 11473
  }

  //Fix 23955 inizio
  protected QuantitaInUMRif convertiQuantitaPrm(BigDecimal qtaUmPrm){
    BigDecimal zero = GestoreEvasioneAcquisto.get().getBigDecimalZero();
    QuantitaInUMRif qtaCalcolata = new QuantitaInUMRif(zero,zero,zero);

    if (qtaUmPrm==null || qtaUmPrm.compareTo(GestoreEvasioneAcquisto.get().getBigDecimalZero())==0)
      return qtaCalcolata;

    boolean isAttivaGestioneQtaIntere = UnitaMisura.isPresentUMQtaIntera(getUMRif(), getUMPrm(), getUMSec(), getArticolo());

    if (isAttivaGestioneQtaIntere) {
      qtaCalcolata = calcolaQuantitaArrotondate(getArticolo(), qtaUmPrm, getUMRif(), getUMPrm(), getUMSec(), getArticoloVersRichiesta(), Articolo.UM_PRM);
    }
    else {
      BigDecimal qtaUmRif = convertiQuantita(getArticolo(), qtaUmPrm, getUMPrm(), getUMRif(), null);
      BigDecimal qtaUmSec = null;
      if (getUMSec() != null)
        qtaUmSec = convertiQuantita(getArticolo(), qtaUmPrm, getUMPrm(), getUMSec(), null);

      qtaCalcolata.setQuantitaInUMPrm(qtaUmPrm);
      qtaCalcolata.setQuantitaInUMRif(qtaUmRif);
      qtaCalcolata.setQuantitaInUMSec(qtaUmSec);
    }
    return qtaCalcolata;
  }
  //Fix 23955 fine

  //Fix 23523 inizio
  protected BigDecimal getQtaDaSpedirePrmDaSec(DocEvaAcqRigaSec rigaSec,BigDecimal giacenzaNettaInUMPrm) {
    BigDecimal qtaDaSpedirePrmDaSec = GestoreEvasioneAcquisto.get().getBigDecimalZero();
    BigDecimal coeffImpiego = rigaSec.getCoefficienteImpiego();

    if (coeffImpiego == null || coeffImpiego.compareTo(new BigDecimal("0")) == 0)
      qtaDaSpedirePrmDaSec = giacenzaNettaInUMPrm;
    else
      qtaDaSpedirePrmDaSec = giacenzaNettaInUMPrm.divide(coeffImpiego, coeffImpiego.scale(), BigDecimal.ROUND_HALF_UP);
    return qtaDaSpedirePrmDaSec;
  }
  //Fix 23523 fine

  // fix 11473 <

  /**
   * Esegue il controllo di disponibilità in base al tipo selezionato, per giacenza
   * o per disponibilità indipendentemente se la riga è stata selezionata per l'estrazione
   * Il controllo è eseguito solo se la riga non è forzata in evasione.
   * @return
   */
  // fix 11473 >
  /*
  public final ErrorMessage ckeckControlloDisp() {
    return checkControlloDisp(true);
  }
  */
  // fix 11473 <

  /**
   * Esegue il controllo di disponibilità in base al tipo selezionato, per giacenza
   * o per disponibilità.
   * Il controllo è eseguito solo se la riga non è forzata in evasione.
   * @param soloEstratta true esegue solo se la riga è stata selezionata per l'estrazione,
   * altrimenti esegue sempre il controllo
   * @return
   */
  // fix 11473 >
  //public ErrorMessage ckeckControlloDisp(boolean soloEstratta) {
  public ErrorMessage ckeckControlloDisp(boolean soloEstratta, List errors) {
    // fix 11473 <
    ErrorMessage errMsg = null;
    boolean eseguiControllo = true;
    if (soloEstratta) {
      eseguiControllo = getDocEvaAcqRiga().isRigaEstratta();
    }
    //eseguiControllo &= !this.isRigaForzata();//Fix 21468
    // fix 11473 >
    if (eseguiControllo) {
      errMsg = riassegnaErroreGiaDisp(errors);
    }
    /*

    if (this.getDocEvaAcqRiga().hasControlloDisp() && eseguiControllo) {
      List valori = getLivelloControlloDisp();
      char livello = ( (Character) valori.get(0)).charValue();
      BigDecimal qtaLivello = (BigDecimal) valori.get(1);
      String[] params = new String[] {
          this.getDocEvaAcqRiga().getRifRigaOrdineFormattato(),
          qtaLivello.toString()
      };
      switch ( ( (DocEvaAcq) getTestata()).getControlloDisp()) {
        case TipiControlloDisponibilita.GIACENZA: {
          switch (livello) {
            case LivelliControlloDisponibilita.ROSSO: {
              errMsg = new ErrorMessage("THIP_BS117", params);
            }
            break;
          }
        }
        break;
        case TipiControlloDisponibilita.DISPONIBILITA: {
          switch (livello) {
            case LivelliControlloDisponibilita.ROSSO: {
              errMsg = new ErrorMessage("THIP_BS118", params);
            }
            break;
          }
        }
        break;
        default: {
        }
      }
    }
    */
    // fix 11473 <
    return errMsg;
  }

  /**
   * Esegue il controllo del gruppo consegna evidenziando per la riga documento
   * se è presente nel gruppo consegna ma non è stata estratta. Il controllo è
   * eseguito se non vi è forzatura di evasione
   * @return
   */
  public ErrorMessage checkGruppoConsegna() throws ThipException {
    ErrorMessage errMsg = null;
    if (!this.isRigaForzata()) {
      OrdineAcquistoRigaPrm rigaOrdine = (OrdineAcquistoRigaPrm)this.
          getRigaOrdine();
      if (rigaOrdine != null) {
        Map grpConsegne = ( (DocEvaAcq)this.getTestata()).getGruppiConsegna();
        String key = KeyHelper.buildObjectKey(new String[] {
                                              rigaOrdine.getIdAzienda(),
                                              rigaOrdine.getAnnoDocumento(),
                                              rigaOrdine.getNumeroDocumento(),
                                              rigaOrdine.getGruppoConsegna()
        });
        Map righeOrd = (Map) grpConsegne.get(key);
        if (righeOrd != null) {
          String keyRigaOrd = rigaOrdine.getKey();
          //GestoreEvasioneAcquisto.get().println("Riga Ordine : '" + keyRigaOrd + "' -> Righe Ord : " + righeOrd.toString());
          if (this.isRigaEstratta()) {
            StringBuffer srighe = new StringBuffer();
            Iterator iterRigheOrd = righeOrd.values().iterator();
            while (iterRigheOrd.hasNext()) {
              Object riga = iterRigheOrd.next(); //fix 2322
              if (riga instanceof DocEvaAcqRigaPrm) {
                if ( ( (DocEvaAcqRigaPrm) riga).isRigaEstratta()) {
                  if (righeOrd.get(keyRigaOrd) instanceof DocEvaAcqRigaPrm) {
                    //Iterator irighe = righeOrd.values().iterator();
                	  Iterator irighe = righeOrd.keySet().iterator();
                    while (irighe.hasNext()) {
                    	//31786 inizio
                    	String keyRigaOrdCorr = (String) irighe.next();
                    	//DocEvaAcqRigaPrm rcorr = (DocEvaAcqRigaPrm) irighe.next();
                    	Object rcorr =  righeOrd.get(keyRigaOrdCorr);
                    	if(rcorr instanceof String) {
                    		srighe.append(" [");
                    		srighe.append(keyRigaOrdCorr);
                    		srighe.append("] ");
                    		continue;
                    	}
                    	DocEvaAcqRigaPrm rigaCorr = null;
                    	if (rcorr instanceof DocEvaAcqRigaPrm)
                    		rigaCorr = (DocEvaAcqRigaPrm) rcorr;
                    	//31786 fine                      
                      if (!rigaCorr.isRigaEstratta() &&
                          !rigaCorr.getKey().equals(this.getKey())) {
                        srighe.append(" [");
                        srighe.append(rigaCorr.getDocEvaAcqRiga().
                                      getRifRigaOrdineFormattato());
                        srighe.append("] ");
                      }
                    }
                  }
                }
              }
            }
            if (!srighe.toString().equals("")) {
              errMsg = new ErrorMessage("THIP_BS167",
                                        new String[] {this.getDocEvaAcqRiga().
                                        getRifRigaOrdineFormattato(),
                                        srighe.toString()});
            }
          }
        }
      }
    }
    return errMsg;
  }

  /**
   * Esegue il controllo dell'evadibilità di intero ordine evidenziando per la riga documento
   * se deve essere evara ma non è stata estratta. Il controllo è eseguito se non vi è forzatura di evasione
   * @return
   */
  public ErrorMessage checkGruppoOrdineIntero() throws ThipException {
    ErrorMessage errMsg = null;
    if (!this.isRigaForzata()) {
      OrdineAcquistoRigaPrm rigaOrdine = (OrdineAcquistoRigaPrm)this.
          getRigaOrdine();
      if (rigaOrdine != null) {
        String keyOrdine = rigaOrdine.getTestataKey();
        Map grpOrdineIntero = ( (DocEvaAcq)this.getTestata()).
            getGruppiOrdineIntero();
        Map righeOrd = (Map) grpOrdineIntero.get(keyOrdine);
        if (righeOrd != null) {
          String keyRigaOrd = rigaOrdine.getKey();
          //GestoreEvasioneAcquisto.get().println("Riga Ordine : '" + keyRigaOrd + "' -> Righe Ord : " + righeOrd.toString());
          if (this.isRigaEstratta()) {
            StringBuffer srighe = new StringBuffer();
            Iterator iterRigheOrd = righeOrd.values().iterator();
            while (iterRigheOrd.hasNext()) {
              Object riga = iterRigheOrd.next(); //fix 2322
              if (riga instanceof DocEvaAcqRigaPrm) {
                if ( ( (DocEvaAcqRigaPrm) riga).isRigaEstratta()) {
                  if (righeOrd.get(keyRigaOrd) instanceof DocEvaAcqRigaPrm) {
                    Iterator irighe = righeOrd.values().iterator();
                    while (irighe.hasNext()) {
                      DocEvaAcqRigaPrm rcorr = (DocEvaAcqRigaPrm) irighe.next();
                      if (!rcorr.isRigaEstratta() &&
                          !rcorr.getKey().equals(this.getKey())) {
                        srighe.append(" [");
                        srighe.append(rcorr.getDocEvaAcqRiga().
                                      getRifRigaOrdineFormattato());
                        srighe.append("] ");
                      }
                    }
                  }
                }
              }
            }
            if (!srighe.toString().equals("")) {
              errMsg = new ErrorMessage("THIP_BS211",
                                        new String[] {this.getDocEvaAcqRiga().
                                        getRifRigaOrdineFormattato(),
                                        srighe.toString()});
            }
          }
        }
      }
    }
    return errMsg;
  }

  /**
   * Controlla se la riga estratta può essere evasa nel documento verificando che se ha la testata
   * ordine con il flag 'Raggruppamento ordini in bolla' false e la riga doc di riferimento evasione
   * non appartiene allo stesso ordine allora la riga doc non può essere evasa con il documento corrente
   * @return
   */
  public ErrorMessage checkRaggruppamentoBolle() throws ThipException {
    ErrorMessage errMsg = null;
    if (!this.isRigaForzata() && this.isRigaEstratta()) {
      OrdineAcquistoRigaPrm rigaOrdine = (OrdineAcquistoRigaPrm)this.
          getRigaOrdine();
      if (rigaOrdine != null) {
        /** @todo Esiste analogo alle vendite ? */
      }
    }
    return errMsg;
  }

  /**
   *
   * @return
   */
  public ErrorMessage checkQuadraturaLotti() {
    ErrorMessage errMsg = super.checkQuadraturaLotti();
    if (this.getStatoAvanzamento() == StatoAvanzamento.DEFINITIVO && errMsg != null) {
      this.forzaStatoAvanzamento(StatoAvanzamento.PROVVISORIO, true);
    }
    return errMsg;
  }

  /**
   * Controlla lo stato di avanzamento della riga. Se lo stato è PROVVISORIO e lo stato
   * della testata è DEFINITIVO, allora imposta lo stato della testata a PROVVISORIO.
   * @return
   */
  public ErrorMessage checkStatoAvanzamento() {
          ErrorMessage errMsg = null;
    if (this.getStatoAvanzamento() == StatoAvanzamento.PROVVISORIO) {
      if (this.getTestata().getStatoAvanzamento() ==
          StatoAvanzamento.DEFINITIVO) {
        this.getTestata().setStatoAvanzamento(StatoAvanzamento.PROVVISORIO);
      }
    }
    return errMsg;
  }

  /**
   * Controlla la percentuale di tolleranza di ricevimento dell'articolo se il documento è un documento di
   * ricevimento prodotto. Se la qta è in difetto non viene fatto nulla altrimenti viene emesso un messaggio.
   * Il controllo è forzabile
   * Viene considerata la qta in um di acquisto. Il controllo di tolleranza viene effettuato solamente
   * per articoli che hanno identica unità di misura di acquisto e primaria di magazzino.
   * Il controllo avviene solamente se la riga non ha percentuale perdita residuo
   * @return
   */
  public ErrorMessage checkPrcTolleranzaRicevimento() {
    ErrorMessage errMsg = null;
    boolean isOk = true;
    boolean soloUMRif = false;
    if (this.getUMRifKey().equals(this.getUMPrmKey())) {
      soloUMRif = true;
    }
    boolean presentePrcPerditaResiduo = false;
    OrdineAcquistoRiga rigaOrdine = (OrdineAcquistoRiga)this.getRigaOrdine();
    if (rigaOrdine != null && rigaOrdine.getPrcPerditaResiduo() != null &&
        rigaOrdine.getPrcPerditaResiduo().compareTo(GestoreEvasioneAcquisto.get().
        getBigDecimalZero()) != 0) {
      presentePrcPerditaResiduo = true;
    }
    if (!presentePrcPerditaResiduo && soloUMRif && !this.isRigaForzata() &&
        ( (DocEvaAcq)this.getTestata()).isDocumentoDiRicevimentoProdotto()) {
      BigDecimal qtaRicevuta = this.getQtaDaSpedireInUMRif();
      BigDecimal qtaOrdinata = this.getQtaOrdinata().getQuantitaInUMRif();
      BigDecimal qtaTolleranza = GestoreEvasioneAcquisto.get().
          getBigDecimalZero();
      BigDecimal prcTolRic = this.getArticolo().getTolRicevimento();
      if (prcTolRic != null) {
        if (prcTolRic.compareTo(GestoreEvasioneAcquisto.get().getBigDecimalZero()) !=
            0) {
          //qtaTolleranza = qtaOrdinata.multiply(prcTolRic).divide(new BigDecimal(100), qtaOrdinata.scale(),BigDecimal.ROUND_HALF_EVEN);//Fix 39402
        	qtaTolleranza = Q6Calc.get().divide(qtaOrdinata.multiply(prcTolRic), new BigDecimal(100), 2, BigDecimal.ROUND_HALF_EVEN);//Fix 39402
        }
        BigDecimal qtaDifferenza = qtaOrdinata.add(qtaRicevuta.negate());
        boolean isDaSaldare = false;
        if (qtaDifferenza.compareTo(qtaTolleranza) <= 0) {
          if (qtaTolleranza.compareTo(GestoreEvasioneAcquisto.get().
                                      getBigDecimalZero()) !=
              0) {
            // FIX 01363
            // saldo automatico
            isDaSaldare = true;
            //this.setRigaSaldata(true);
          }
        }
        else if (qtaDifferenza.compareTo(qtaTolleranza) > 0) {
          // errore
          //this.setRigaSaldata(false); // FIX 01363
          String[] params = new String[] {
              this.getDocEvaAcqRiga().getRifRigaOrdineFormattato()};
          errMsg = new ErrorMessage("THIP_BS241", params);
        }
      }
      else {
        // non viene controllata
      }
    }
    return errMsg;
  }

  //ini Fix 2218
  /**
   * Esegue il controllo delle condizioni di compatibilità per evasione in base
   * ai valori definiti dalla testata e dalle condizioni di compatibilità definite
   * sulla testata da CondCompEvas.
   * Il controllo avviene solamente sulle righe che hanno qta da spedire > 0.
   * @return
   */
  public List checkCondCompEvas() {
    List errors = new ArrayList();
    //Fix 21510 inizio
    /*
    if (Boolean.valueOf( (String) ParametriEvasioneOrdiniAcq.get().get(
        ParametriEvasioneOrdiniAcq.ABILITA_CONTROLLO_COND_COMP_EVAS)).
        booleanValue()) { //...FIX03292 - DZ
    	*/
    //Fix 21510 fine

      ErrorMessage errMsg = null;
      boolean isOk = true;
      boolean eseguiControllo = this.isRigaEstratta() & !this.isRigaForzata();
      //Fix 3315 PM Inizio
      //if (eseguiControllo) {
      if (eseguiControllo && this.getRigaOrdine() != null) {
        //Fix 3315 PM Fine
        String rifRigaOrd = this.getDocEvaAcqRiga().getRifRigaOrdineFormattato();
        BigDecimal qtaZero = GestoreEvasioneAcquisto.get().getBigDecimalZero();
        if (getDocEvaAcqRiga().getQtaDaSpedireInUMRif().compareTo(qtaZero) > 0 ||
            getDocEvaAcqRiga().getQtaDaSpedireInUMPrm().compareTo(qtaZero) > 0) {

          DocEvaAcq doc = (DocEvaAcq)super.getTestata();
          doc.assegnaRigaPrmPerCondCompEvas(); //fix 21510
          DocEvaAcqRigaPrm rigaDocRif = doc.getRigaPrmPerCondCompEvas();
          //fix 21510
          if (rigaDocRif == null) {
            if (this.getRigaOrdine() != null) {
              doc.setRigaPrmPerCondCompEvas(this);
            }
          }
          //fine fix 21510
          OrdineAcquisto ordTes = (OrdineAcquisto)this.getRigaOrdine().
              getTestata();
          Object idA = null;
          Object idB = null;
          /*//fix 21510
          if (isOk) {
            idA = doc.getIdModPagamento() == null ? "" : doc.getIdModPagamento();
            idB = ordTes.getIdModPagamento() == null ? "" :
                ordTes.getIdModPagamento();
            isOk = idA.equals(idB);
            if (!isOk) {
              errors.add(new ErrorMessage("THIP_BS340",
                                          new String[] {rifRigaOrd,
                                          "doc: '" + idA + "' <> ord: '" + idB +
                                          "'"}));
            }
          }
         */

          String idCaurigaDocTesOrd = null;
          String idCaurigaTesOrd = null;
          if (doc.getCondCompEvas().isGesCauDocAcq()){
          	if (rigaDocRif != null && rigaDocRif.getRigaOrdine()!=null){
          		idCaurigaDocTesOrd = rigaDocRif.getRigaOrdine().getTestata().getIdCau();
          	}
          	if (this.getRigaOrdine()!=null && this.getRigaOrdine().getTestata()!=null){
          		idCaurigaTesOrd = this.getRigaOrdine().getTestata().getIdCau();
          	}
          }

         List erroriTes = doc.checkCondCompEvasRiga(ordTes, rifRigaOrd, idCaurigaTesOrd, idCaurigaDocTesOrd);
         errors.addAll(erroriTes);

        }
      }
    //}
    return errors;
  }


  //Fix 8707 - inizio
  /**
   * Il controllo viene effettuato solo in seguito a conferma evasione o
   * salvataggio e non in fase di estrazione righe, altrimenti (se sussistono le
   * condizioni da pers. dati magazzino) viene proposto lo stato riga a
   * Provvisorio, ma le matricole potrebbero essere assegnate automaticamente
   * al salvataggio (e quindi all'evasione).
   */
  public ErrorMessage checkDichiarazioneMatricole() {
    ErrorMessage em = null;

          DocEvaAcq testata = (DocEvaAcq)getTestata();
    if (testata.isAttivaCheckDichiarazioneMatricole()) {
            //Fix 9283 - inizio
                    //em = super.checkDichiarazioneMatricole();
            //Fix 9283 - fine
    }
    return em;
  }
  //Fix 8707 - fine


  /**
   *
   * @param doc
   * @param rigaOrdine
   * @return
   */
  protected DocEvaAcqRigaLottoPrm creaRigaLottoPrm(OrdineAcquistoRigaLotto
      rigaOrdineLotto) {
    return DocEvaAcqRigaLottoPrm.creaRiga(this, rigaOrdineLotto);
  }

  /**
   *
   * @param doc
   * @param rigaOrdine
   * @return
   */
  protected DocEvaAcqRigaSec creaRigaSec(OrdineAcquistoRigaSec rigaOrdine) {
    //Fix 3700 - inizio
    BigDecimal zero = new BigDecimal(0.0);
    CausaleDocumentoTestataAcq cauTes = ( (DocumentoAcquisto) getTestata()).
        getCausale();
    if (cauTes.getTipoDocumento() == TipoDocumentoAcq.SPED_LAV_ESN &&
        rigaOrdine.getCoefficienteImpiego().compareTo(zero) < 0) {
      return null;
    }
    else {
      return DocEvaAcqRigaSec.creaRiga(this, rigaOrdine);
    }
    //Fix 3700 - fine
  }

  // Inizio 5395
  //added: 3820
//  private static boolean nulla(BigDecimal qta) {
//    return qta == null || qta.compareTo(new BigDecimal(0)) == 0;
//  }

  private static boolean nulla(BigDecimal qta) {
    return qta == null ;
  }
  //added: 3820
  // Fine 5395
  private static boolean diversi(BigDecimal a, BigDecimal b) {
    if (a == null)
      return b != null;

    if (b == null)
      return true;

    return a.compareTo(b) != 0;
  }

  //added: 3820
  private static boolean uguali(BigDecimal a, BigDecimal b) {
    return!diversi(a, b);
  }

  //Fix 7187 inizio (promosso a pubblico accesso a metodo aggiornaRiga)
  public void aggiornaRigaPubblico(ParamRigaPrmDocEvaAcq pr)
  {
     aggiornaRiga(pr);
  }
  //Fix 7187 fine

  //fixed: 3820
  /**
   * Aggiorna i dati sulla riga con i dati corrispondenti in ParaRiga. Le quantità Rif e Prm se variate
   * vengono mantenute solamente se non vengono annullate. In questo caso vengono ricalcolate applicando
   * il fattore di conversione delle unità di misura.
   * @param pr
   */
  //...Reso protected il metodo in modo da poter essere ridefinito negli eredi
  protected void aggiornaRiga(ParamRigaPrmDocEvaAcq pr) {
    if (isOnDB()) {
      pr.iEstratta = true;
      // fix 11951 >
      if (pr.iDaSpedireV == null) {
        pr.iDaSpedireV = this.getQtaDaSpedireInUMRif();
        pr.iDaSpedireM = this.getQtaDaSpedireInUMPrm();
        pr.iDaSpedireSecM = this.getQtaDaSpedireInUMSec();
        pr.iIdUMRif = this.getIdUMRif();
        if (this.getStatoAvanzamento() != StatoAvanzamento.DEFINITIVO && pr.iDefinitivo) {
          pr.iForzaStatoAvanzamento = true;
        }
      }
      // fix 11951 <
    }
    this.setRigaEstratta(pr.iEstratta);
    // anche sulle sue righe secondarie
    Iterator righeSec = this.getRigheSecondarie().iterator();
    while (righeSec.hasNext()) {
      DocEvaAcqRigaSec rigaSec = (DocEvaAcqRigaSec) righeSec.next();
      rigaSec.setRigaEstratta(pr.iEstratta);
    }
    // Inizio 4790
    //this.setRigaSaldata(pr.iSaldo);
    this.setRigaSaldata(pr.iSaldo || isSaldoAutomatico());
    // Fine 4790

    this.setRigaForzata(pr.iForzaEvasione);
    //fix 2322
    if (! ( (DocEvaAcq)this.getTestata()).getMostraCodiceArtEsterno()) {
      this.setDescrizioneArticolo(pr.iDescrizioneArticolo);
    }

    if (pr.iIdUMRif != null && !pr.iIdUMRif.trim().equals("")) {
      boolean isUMRifVariata = !this.getIdUMRif().equals(pr.iIdUMRif);
      if (isUMRifVariata) {
        this.setIdUMRif(pr.iIdUMRif);
      }
    }

    //PJ fix 3820 INIZIO

    /*
     le quantità:
     se è stato scelto il ricalcolo si ricalcolano tutte le quantità
     tranne una, che è quella di partenza.
     l'unità di partenza è, fra le non nulle, la prima modificata secondo
     questa precedenza: 1) riferimento 2) primaria magazzino 3) secondaria magazzino

     se non è stato scelto il ricalcolo non si effettuano ricalcoli a meno
     dei valori nulli (null oppure zero). se tutte e tre le quantità sono
     nulle invece ci sarà errore a livello di check della riga.
     il ricalcolo della quantità nulla viene fatto così:
     manca il valore in unità riferimento: si ricalcola a partire dalla
     unità primaria di magazzino, in mancanza da quella secondaria
     manca il valore in unità primaria magazzino: si ricalcola a partire
     dalla unità di riferimento
     manca il valore in unità second. magazzino: si ricalcola a partire
     dalla unità di riferimento
     */

    boolean ricalcola = pr.iRicalcola;

    // fix 11415
    this.setRicalcola(ricalcola);
    // fine fix 11415

    BigDecimal oldQtaR = getQtaDaSpedireInUMRif();
    BigDecimal oldQtaP = getQtaDaSpedireInUMPrm();
    BigDecimal oldQtaS = getQtaDaSpedireInUMSec();

    int roundingMode = BigDecimal.ROUND_HALF_EVEN;
    //int scale = oldQtaR.scale();//Fix 39402
    int scale = Q6Calc.get().scale(2);//39402

    BigDecimal newQtaR = null;
    BigDecimal newQtaP = null;
    BigDecimal newQtaS = null;

    String UMR = getIdUMRif();
    if (UMR != null)
      UMR = UMR.intern(); // per confronto con == su stringhe!

    String UMP = getIdUMPrm();
    if (UMP != null)
      UMP = UMP.intern();

    String UMS = getIdUMSec();
    if (UMS != null)
      UMS = UMS.intern();

    OrdineAcquistoRiga rigaOrd = (OrdineAcquistoRiga) getRigaOrdine();
    // Inizio 4720
    //boolean nonFrazionabile = rigaOrd != null && rigaOrd.isRigaNonFrazionabile();
    DocEvaAcq docEvaAcq = (DocEvaAcq)getTestata();
    boolean nonFrazionabile = rigaOrd != null &&
      rigaOrd.isRigaNonFrazionabile() &&
      !docEvaAcq.isDocumentoDiRicevimentoProdotto() &&
      docEvaAcq.isDocumentoDiContoLavoro();
    // Fine 4720

    // Inizio 4670
    boolean isAttivaGestioneQtaIntere = UnitaMisura.isPresentUMQtaIntera(getUMRif(), getUMPrm(), getUMSec(), getArticolo());//Fix 5117
    QuantitaInUMRif qtaCalcolata = new QuantitaInUMRif();
    // Fine 4670

    if (nonFrazionabile) {
      newQtaR = oldQtaR;
      newQtaP = oldQtaP;
      newQtaS = oldQtaS;
    }
    else if (ricalcola) {
      /*
       parto dalla unità di riferimento se
       - è cambiata l'unità di riferimento, ma non è nulla
       OPPURE
       - ogni qtà di magazzino è nulla oppure invariata
       */
      boolean riferimento = !nulla(pr.iDaSpedireV)
          &&
          (diversi(oldQtaR, pr.iDaSpedireV)
           ||
           (
               (nulla(pr.iDaSpedireM) || uguali(oldQtaP, pr.iDaSpedireM))
               &&
               (nulla(pr.iDaSpedireSecM) || uguali(oldQtaS, pr.iDaSpedireSecM))
           )
          )
          ;

      /*
       parto dalla unità primaria di magazzino se
       - non parto da quella di riferimento
       AND
       - è cambiata l'unità primaria, ma non è nulla
       */

      boolean primaria =
          !riferimento && !nulla(pr.iDaSpedireM)
          && (diversi(oldQtaP, pr.iDaSpedireM) || nulla(pr.iDaSpedireV))
          ;

      /*
       parto dalla unità secondaria di magazzino se
       - non parto da quella primaria (e quindi nemmeno da quella di riferimento)
       AND
       - è cambiata l'unità secondaria, ma non è nulla
       */

      boolean secondaria =
          !riferimento && !primaria && !nulla(pr.iDaSpedireSecM)
          && (diversi(oldQtaS, pr.iDaSpedireSecM) || nulla(pr.iDaSpedireM))
          ;

      char RIFERIMENTO = 'R';
      char PRIMARIA = 'P';
      char SECONDARIA = 'S';

      char partenza = secondaria ? SECONDARIA : primaria ? PRIMARIA :
          riferimento ? RIFERIMENTO : ' ';

      if (partenza == RIFERIMENTO) {
        /*
         ricalcolo le qta primaria e secondaria a partire dalla qta di riferimento
         */
        newQtaR = pr.iDaSpedireV;
        if (UMP != null) {
          //calcolo la primaria
          if (UMP == UMR) {
            //se è uguale a quella di riferimento la copio
            newQtaP = newQtaR;
          }
          else {
            //altrimenti la ricalcolo

            //...FIX 8805 inizio
            //newQtaP = getArticolo().convertiUM(newQtaR, getUMRif(), getUMPrm());
            newQtaP = convertiQuantita(getArticolo(), newQtaR, getUMRif(), getUMPrm(), pr);
            //...FIX 8805 fine

            //newQtaP = newQtaP.setScale(scale, roundingMode);//Fix 30871
			newQtaP = Q6Calc.get().setScale(newQtaP,scale, roundingMode);//Fix 30871
          }
        }
        if (UMS != null) {
          //calcolo la secondaria
          if (UMS == UMR) {
            //se è uguale a quella di riferimento la copio
            newQtaS = newQtaR;
          }
          else if (UMS == UMP) {
            //se è uguale a quella primaria la copio
            newQtaS = newQtaP;
          }
          else {
            //altrimenti la ricalcolo

            //...FIX 8805 inizio
            //newQtaS = getArticolo().convertiUM(newQtaR, getUMRif(), getUMSec());
            newQtaS = convertiQuantita(getArticolo(), newQtaR, getUMRif(), getUMSec(), pr);
            //...FIX 8805 fine

            //newQtaS = newQtaS.setScale(scale, roundingMode);//Fix 30871
			newQtaS = Q6Calc.get().setScale(newQtaS,scale, roundingMode);//Fix 30871
          }
        }
        // Inizio 4670
        if (isAttivaGestioneQtaIntere){
          //...FIX 9450 inizio
          //qtaCalcolata = getArticolo().calcolaQuantitaArrotondate(newQtaR, getUMRif(), getUMPrm(), getUMSec(), Articolo.UM_RIF);
          qtaCalcolata = calcolaQuantitaArrotondate(getArticolo(), newQtaR, getUMRif(), getUMPrm(), getUMSec(), this.getArticoloVersRichiesta(), Articolo.UM_RIF); // fix 10955
          //...FIX 9450 fine
          newQtaR = qtaCalcolata.getQuantitaInUMRif();
          newQtaP = qtaCalcolata.getQuantitaInUMPrm();
          if (getUMSec() != null)
            newQtaS = qtaCalcolata.getQuantitaInUMSec();
        }
        // Fine 4670
      }
      else if (partenza == PRIMARIA) {
        /*
         ricalcolo le qta riferimento e secondaria a partire dalla qta primaria
         */
        newQtaP = pr.iDaSpedireM;

        // ricalcolo quella di riferimento

        //...FIX 8805 inizio
        //newQtaR = getArticolo().convertiUM(newQtaP, getUMPrm(), getUMRif());
        newQtaR = convertiQuantita(getArticolo(), newQtaP, getUMPrm(), getUMRif(), pr);
        //...FIX 8805 fine

        //newQtaR = newQtaR.setScale(scale, roundingMode);//Fix 30871
		newQtaR = Q6Calc.get().setScale(newQtaR,scale, roundingMode);//Fix 30871

        if (UMS != null) {
          //calcolo la secondaria
          if (UMS == UMR) {
            //se è uguale a quella di riferimento la copio
            newQtaS = newQtaR;
          }
          else if (UMS == UMP) {
            //se è uguale a quella primaria la copio
            newQtaS = newQtaP;
          }
          else {
            //altrimenti la ricalcolo

            //...FIX 8805 inizio
            //newQtaS = getArticolo().convertiUM(newQtaP, getUMPrm(), getUMSec());
            newQtaS = convertiQuantita(getArticolo(), newQtaP, getUMPrm(), getUMSec(), pr);
            //...FIX 8805 fine

            //newQtaS = newQtaS.setScale(scale, roundingMode);//Fix 30871
			newQtaS = Q6Calc.get().setScale(newQtaS,scale, roundingMode);//Fix 30871
          }
        }
        // Inizio 4670
        if (isAttivaGestioneQtaIntere){
          // fix 5303
          //qtaCalcolata = getArticolo().calcolaQuantitaArrotondate(newQtaR, getUMRif(), getUMPrm(), getUMSec(), Articolo.UM_PRM);
          //...FIX 9450 inizio
          //qtaCalcolata = getArticolo().calcolaQuantitaArrotondate(newQtaP, getUMRif(), getUMPrm(), getUMSec(), Articolo.UM_PRM);
          qtaCalcolata = calcolaQuantitaArrotondate(getArticolo(), newQtaP, getUMRif(), getUMPrm(), getUMSec(), this.getArticoloVersRichiesta(), Articolo.UM_PRM); // fix 10955
          //...FIX 9450 fine
          // fine fix 5303
          newQtaR = qtaCalcolata.getQuantitaInUMRif();
          newQtaP = qtaCalcolata.getQuantitaInUMPrm();
          if (getUMSec() != null)
            newQtaS = qtaCalcolata.getQuantitaInUMSec();
      }
        // Fine 4670
      }
      else if (partenza == SECONDARIA) {
        // è cambiata la qta secondaria.
        // ricalcolo le altre due a partire da questa
        newQtaS = pr.iDaSpedireSecM;

        // ricalcolo quella di riferimento

        //...FIX 8805 inizio
        //newQtaR = getArticolo().convertiUM(newQtaS, getUMSec(), getUMRif());
        newQtaR = convertiQuantita(getArticolo(), newQtaS, getUMSec(), getUMRif(), pr);
        //...FIX 8805 fine

        //newQtaR = newQtaR.setScale(scale, roundingMode);//Fix 30871
		newQtaR = Q6Calc.get().setScale(newQtaR,scale, roundingMode);//Fix 30871

        if (UMP != null) {
          //calcolo la primaria
          if (UMP == UMR) {
            //se è uguale a quella di riferimento la copio
            newQtaP = newQtaR;
          }
          else {
            //altrimenti la ricalcolo

            //...FIX 8805 inizio
            //newQtaP = getArticolo().convertiUM(newQtaS, getUMSec(), getUMPrm());
            newQtaP = convertiQuantita(getArticolo(), newQtaS, getUMSec(), getUMPrm(), pr);
            //...FIX 8805 fine

            //newQtaP = newQtaP.setScale(scale, roundingMode);//Fix 30871
			newQtaP = Q6Calc.get().setScale(newQtaP,scale, roundingMode);//Fix 30871
          }
        }
        // Inizio 4670
        if (isAttivaGestioneQtaIntere){
          //...FIX 9450 inizio
          //qtaCalcolata = getArticolo().calcolaQuantitaArrotondate(newQtaS, getUMRif(), getUMPrm(), getUMSec(), Articolo.UM_SEC);
          qtaCalcolata = calcolaQuantitaArrotondate(getArticolo(), newQtaS, getUMRif(), getUMPrm(), getUMSec(), this.getArticoloVersRichiesta(), Articolo.UM_SEC); // fix 10955
          //...FIX 9450 fine
          newQtaR = qtaCalcolata.getQuantitaInUMRif();
          newQtaP = qtaCalcolata.getQuantitaInUMPrm();
          if (getUMSec() != null)
            newQtaS = qtaCalcolata.getQuantitaInUMSec();
        }
        // Fine 4670
      }
    }
    else if (!nulla(pr.iDaSpedireV) || !nulla(pr.iDaSpedireM) ||
             !nulla(pr.iDaSpedireSecM)) {

      //quantità in UM riferimento
      if (nulla(pr.iDaSpedireV)) {
        //la devo ricalcolare
        if (UMP != null && !nulla(pr.iDaSpedireM)) {
          //ricalcolo in base alla primaria di magazzino
          // Inizio 4670
          if (isAttivaGestioneQtaIntere){
            //...FIX 9450 inizio
            //qtaCalcolata = getArticolo().calcolaQuantitaArrotondate(pr.iDaSpedireM, getUMRif(), getUMPrm(), getUMSec(), Articolo.UM_PRM);
            qtaCalcolata = calcolaQuantitaArrotondate(getArticolo(), pr.iDaSpedireM, getUMRif(), getUMPrm(), getUMSec(), this.getArticoloVersRichiesta(), Articolo.UM_PRM); // fix 10955
            //...FIX 9450 fine
            newQtaR = qtaCalcolata.getQuantitaInUMRif();
            newQtaP = qtaCalcolata.getQuantitaInUMPrm();
            if (getUMSec() != null)
              newQtaS = qtaCalcolata.getQuantitaInUMSec();
          }
          else{

          //...FIX 8805 inizio
          //newQtaR = getArticolo().convertiUM(pr.iDaSpedireM, getUMPrm(), getUMRif());
          newQtaR = convertiQuantita(getArticolo(), pr.iDaSpedireM, getUMPrm(), getUMRif(), pr);
          //...FIX 8805 fine

          //newQtaR = newQtaR.setScale(scale, roundingMode);//Fix 30871
		  newQtaR = Q6Calc.get().setScale(newQtaR,scale, roundingMode);//Fix 30871
        }
          // Fine 4670
        }
        else {
          //ricalcolo in base alla secondaria di magazzino
          if (isAttivaGestioneQtaIntere){
            //...FIX 9450 inizio
            //qtaCalcolata = getArticolo().calcolaQuantitaArrotondate(pr.iDaSpedireSecM, getUMRif(), getUMPrm(), getUMSec(), Articolo.UM_SEC);
            qtaCalcolata = calcolaQuantitaArrotondate(getArticolo(), pr.iDaSpedireSecM, getUMRif(), getUMPrm(), getUMSec(), this.getArticoloVersRichiesta(), Articolo.UM_SEC); // fix 10955
            //...FIX 9450 fine
            newQtaR = qtaCalcolata.getQuantitaInUMRif();
            newQtaP = qtaCalcolata.getQuantitaInUMPrm();
            if (getUMSec() != null)
              newQtaS = qtaCalcolata.getQuantitaInUMSec();
          }
          else{

          //...FIX 8805 inizio
          //newQtaR = getArticolo().convertiUM(pr.iDaSpedireSecM, getUMSec(), getUMRif());
          newQtaR = convertiQuantita(getArticolo(), pr.iDaSpedireSecM, getUMSec(), getUMRif(), pr);
          //...FIX 8805 fine

          //newQtaR = newQtaR.setScale(scale, roundingMode);//Fix 30871
		  newQtaR = Q6Calc.get().setScale(newQtaR,scale, roundingMode);//Fix 30871
        }
          // Fine 4670
        }
      }
      else {
        newQtaR = pr.iDaSpedireV;
      }

      //quantità in UM primaria magazzino
      //fix 4090
      //if (nulla(pr.iDaSpedireM) && UMP != null) {
      if (UMP != null) {
        if (UMP == UMR) {
          newQtaP = newQtaR;
        }
        else if (nulla(pr.iDaSpedireM)) {
          //la devo ricalcolare in base all'UM riferimento
          // Inizio 4670
          if (isAttivaGestioneQtaIntere){
            //...FIX 9450 inizio
            //qtaCalcolata = getArticolo().calcolaQuantitaArrotondate(newQtaR, getUMRif(), getUMPrm(), getUMSec(), Articolo.UM_RIF);
            qtaCalcolata = calcolaQuantitaArrotondate(getArticolo(), newQtaR, getUMRif(), getUMPrm(), getUMSec(), this.getArticoloVersRichiesta(), Articolo.UM_RIF); // fix 10955
            //...FIX 9450 fine
            newQtaR = qtaCalcolata.getQuantitaInUMRif();
            newQtaP = qtaCalcolata.getQuantitaInUMPrm();
            if (getUMSec() != null)
              newQtaS = qtaCalcolata.getQuantitaInUMSec();
          }
          else{

          //...FIX 8805 inizio
          //newQtaP = getArticolo().convertiUM(newQtaR, getUMRif(), getUMPrm());
          newQtaP = convertiQuantita(getArticolo(), newQtaR, getUMRif(), getUMPrm(), pr);
          //...FIX 8805 fine

          //newQtaP = newQtaP.setScale(scale, roundingMode);//Fix 30871
		  newQtaP = Q6Calc.get().setScale(newQtaP,scale, roundingMode);//Fix 30871
        }
          // Fine 4670
        }
        else {
          newQtaP = pr.iDaSpedireM;
        }
      }
      else {
        newQtaP = pr.iDaSpedireM;
      }

      //quantità in UM secondaria magazzino
      //fix 4090
      //if (nulla(pr.iDaSpedireSecM) && UMS != null) {
      if (UMS != null) {
        if (UMS == UMR) {
          newQtaS = newQtaR;
        }
        else if (UMS == UMP) {
          newQtaS = newQtaP;
        }
        else if (nulla(pr.iDaSpedireSecM)) {
          //la devo ricalcolare in base all'UM riferimento
          // Inizio 4670
          if (isAttivaGestioneQtaIntere){
            //...FIX 9450 inizio
            //qtaCalcolata = getArticolo().calcolaQuantitaArrotondate(newQtaR, getUMRif(), getUMPrm(), getUMSec(), Articolo.UM_RIF);
            qtaCalcolata = calcolaQuantitaArrotondate(getArticolo(), newQtaR, getUMRif(), getUMPrm(), getUMSec(), this.getArticoloVersRichiesta(), Articolo.UM_RIF); // fix 10955
            //...FIX 9450 fine
            newQtaR = qtaCalcolata.getQuantitaInUMRif();
            newQtaP = qtaCalcolata.getQuantitaInUMPrm();
            if (getUMSec() != null)
              newQtaS = qtaCalcolata.getQuantitaInUMSec();
          }
          else{

          //...FIX 8805 inizio
          //newQtaS = getArticolo().convertiUM(newQtaR, getUMRif(), getUMSec());
          newQtaS = convertiQuantita(getArticolo(), newQtaR, getUMRif(), getUMSec(), pr);
          //...FIX 8805 fine

          //newQtaS = newQtaS.setScale(scale, roundingMode);//Fix 30871
		  newQtaS = Q6Calc.get().setScale(newQtaS,scale, roundingMode);//Fix 30871
        }
          // Fine 4670
        }
        else {
          newQtaS = pr.iDaSpedireSecM;
        }
      }
      else {
        newQtaS = pr.iDaSpedireSecM;
      }
    }

    if (newQtaR == null)
      newQtaR = GestoreEvasioneAcquisto.get().getBigDecimalZero();

    if (newQtaP == null)
      newQtaP = GestoreEvasioneAcquisto.get().getBigDecimalZero();

    if (newQtaS == null)
      newQtaS = GestoreEvasioneAcquisto.get().getBigDecimalZero();

    getDocEvaAcqRiga().setQtaDaSpedire(new QuantitaInUMRif(newQtaP, newQtaS,
        newQtaR));

    if (diversi(oldQtaR, newQtaR) || diversi(oldQtaP, newQtaP) ||
        diversi(oldQtaS, newQtaS)||
        isRigaConQtaInteroCambiato())//Fix 19840
    {
      // aggiorna le qta componenti solo se vi è stata variazione di qta
      aggiornaQtaEvasione(getStatoAvanzamento(), true);
    }

    //PJ fix 3820 FINE


    if (pr.iForzaStatoAvanzamento) {
      char statoAT = this.getTestata().getStatoAvanzamento();
      if (statoAT == StatoAvanzamento.DEFINITIVO) {
        statoAT = StatoAvanzamento.PROVVISORIO;
      }
      else if (statoAT == StatoAvanzamento.PROVVISORIO) {
        statoAT = StatoAvanzamento.DEFINITIVO;
      }
      pr.iDefinitivo = statoAT == StatoAvanzamento.DEFINITIVO;
      this.setStatoAvanzamentoDef(pr.iDefinitivo);
    }
    // Inizio 4004
    setRicalcoloQtaFattoreConv(ricalcola);
    // Fine 4004
    this.getDocEvaAcqRiga().logDeepQta("Aggiornamento da griglia");
	//Fix 36394 - inizio
    
    BigDecimal zero = GestoreEvasione.getBigDecimalZero();
    if (isRigaSaldata() && abilitaSaldoAZeroEvasioneOrdini() && 
        ( (pr.iDaSpedireV != null && (pr.iDaSpedireV.compareTo(zero) == 0)) ||
         (pr.iDaSpedireM != null && (pr.iDaSpedireM.compareTo(zero) == 0) ))) {
      getDocEvaAcqRiga().setAbilitaControlloQtaZero(false);
    }
    //Fix 36394 - fine
  }

  /**
   * Forza lo stato di avanzamento sulla riga, sui lotti e sulle sue righe secondarie
   * aggiornamendo le qta.
   * @param stato nuovo stato avanzamento
   * @return Lista di ErrorMessage
   */
  public List forzaStatoAvanzamento(char stato, boolean soloEstratte) {
    List errors = new ArrayList();
    if (this.getStatoAvanzamento() != stato) {
      try {
        boolean forza = soloEstratte ? this.isRigaEstratta() : true;
        if (forza) {
          this.aggiornaQtaEvasione(stato, true);
          super.setStatoAvanzamento(stato);
          ErrorMessage em = this.getDocEvaAcqRiga().checkQuadraturaLotti();
          if (em != null) {
            errors.add(em);
          }
        }
      }
      catch (Throwable t) {
      }
      if (errors.isEmpty()) {
        Iterator righeSec = this.getRigheSecondarie().iterator();
        while (righeSec.hasNext()) {
          DocEvaAcqRigaSec rigaSec = (DocEvaAcqRigaSec) righeSec.next();
          errors.addAll(rigaSec.forzaStatoAvanzamento(stato));
        }
      }
    }
	//33748 inizio
    Iterator iterLotti = this.getRigheLotto().iterator();    
    while (iterLotti.hasNext()) {
      //DocEvaAcqRigaLottoPrm lotto = (DocEvaAcqRigaLottoPrm) iterLotti.next();//Fix 35681
    	DocumentoOrdineRigaLotto lotto = (DocumentoOrdineRigaLotto) iterLotti.next();//Fix 35681

      if (lotto.getIdLotto().equals(Lotto.LOTTO_DUMMY))
    	  iterLotti.remove();
    }
    //33748 Fine
    return errors;
  }

  /**
   * Aggiorna le qta evasione in base allo stato avanzamento corrente.
   */
  public void aggiornaQtaEvasione() {
    this.aggiornaQtaEvasione(this.getStatoAvanzamento(), true);
  }

  /**
   * Aggiorna le QTA di riga e dei suoi componenti in base allo stato avanzamento ma
   * non imposta il nuovo stato alla riga
   * @param stato
   * @param forzaStato true non controlla lo stato passato con quello in corso, false
   * controlla lo stato passato e aggiorna solo se variato
   */
  protected void aggiornaQtaEvasione(char stato, boolean forzaStato) {
    int roundingMode = BigDecimal.ROUND_HALF_EVEN;
    char statoIniziale = this.getStatoAvanzamento();
    if (statoIniziale != stato || forzaStato) {
      List righeEliminabili = new ArrayList();
      this.getDocEvaAcqRiga().aggiornaQtaEvasione(stato, forzaStato);
      Iterator iterLotti = this.getRigheLotto().iterator();
      QuantitaInUMRif qtaDaSpedireCorrente = new QuantitaInUMRif();
      QuantitaInUMRif qtaResiduaLottoCorrente = new QuantitaInUMRif();
      try {
        qtaDaSpedireCorrente.setEqual(getDocEvaAcqRiga().getQtaDaSpedire());
      }
      catch (CopyException ce) {
      }
      while (iterLotti.hasNext()) {
        DocEvaAcqRigaLottoPrm lotto = (DocEvaAcqRigaLottoPrm) iterLotti.next();
        boolean isChanged = false;
        if (this.getOldRiga() != null) {
          isChanged = this.getOldRiga().getServizioQta().compareTo(
              qtaDaSpedireCorrente) != 0;
        }
        if (!lotto.getIdLotto().equals(Lotto.LOTTO_DUMMY) &&
            !qtaDaSpedireCorrente.isZero()) {
          if (!this.isOnDB() || isChanged) {
            int comp = lotto.getDocEvaAcqRigaLotto().getQtaResidua().compareTo(
                qtaDaSpedireCorrente);
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
                /*
                                String msg = "Residuo sul lotto prm '" + lotto.getKey() + "' [" + qtaResiduaLottoCorrente + "] < [" +
                             qtaDaSpedireCorrente + "]";
                                 ((DocEvaAcq)this.getTestata()).getRigheWarnings().put(this.getRigaOrdine(), msg);
                                 GestoreEvasioneAcquisto.get().println(msg);
                 qtaResiduaLottoCorrente.setEqual(lotto.getDocEvaAcqRigaLotto().getQtaResidua());
                 qtaDaSpedireCorrente = qtaDaSpedireCorrente.subtract(qtaResiduaLottoCorrente);
                 */
                String msg = "Residuo sul lotto prm '" + lotto.getKey() + "' [" +
                    qtaResiduaLottoCorrente + "] < [" +
                    qtaDaSpedireCorrente + "]";
                //((DocEvaAcq)this.getTestata()).getRigheWarnings().put(this.getRigaOrdine(), msg);
                GestoreEvasioneAcquisto.get().println(msg);
                qtaResiduaLottoCorrente.setEqual(lotto.getDocEvaAcqRigaLotto().
                                                 getQtaResidua());
                qtaDaSpedireCorrente = qtaDaSpedireCorrente.subtract(
                    qtaResiduaLottoCorrente);
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
        lotto.getDocEvaAcqRigaLotto().setAggiornamentoInOrdine(this.
            getDocEvaAcqRiga().isAggiornamentoInOrdine());
        lotto.getDocEvaAcqRigaLotto().aggiornaQtaEvasione(stato, forzaStato,
            qtaResiduaLottoCorrente);
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
      Iterator iterRighe = this.getRigheSecondarie().iterator();
      while (iterRighe.hasNext()) {
        DocEvaAcqRigaSec rigaSec = (DocEvaAcqRigaSec) iterRighe.next();
        //Fix 9745 PM Inizio
        //rigaSec.ricalcoloQuantita(this.getQtaDaSpedireInUMPrm()); //Fix 9671 PM
        //rigaSec.ricalcoloQuantita(this); //Fix 9671 PM
        rigaSec.ricalcoloQuantita(this, getDocEvaAcqRiga().getQtaDaSpedire());
        //Fix 9745 PM Fine
        //int scale = this.getQtaDaSpedireInUMPrm().scale();//Fix 39402
        int scale = Q6Calc.get().scale(2);//39402
        //Fix 9745 PM Inizio
        //rigaSec.ricalcoloQuantita(this.getQtaDaSpedireInUMPrm()); //Fix 9671 PM
        //rigaSec.ricalcoloQuantita(this); //Fix 9671 PM
        rigaSec.ricalcoloQuantita(this, getDocEvaAcqRiga().getQtaDaSpedire());
        //Fix 9745 PM Fine
        BigDecimal qta = rigaSec.getQtaInUMAcq();
        if (qta == null) {
          qta = GestoreEvasioneAcquisto.get().getBigDecimalZero();
        }
        //qta = qta.setScale(scale, roundingMode);//Fix 30871
		qta = Q6Calc.get().setScale(qta,scale, roundingMode);//Fix 30871
        rigaSec.setQtaDaSpedireInUMRif(qta);
        qta = rigaSec.getQtaInUMPrm();
        if (qta == null) {
          qta = GestoreEvasioneAcquisto.get().getBigDecimalZero();
        }
        //qta = qta.setScale(scale, roundingMode);//Fix 30871
		qta = Q6Calc.get().setScale(qta,scale, roundingMode);//Fix 30871
        rigaSec.setQtaDaSpedireInUMPrm(qta);
        qta = rigaSec.getQtaInUMSec();
        if (qta == null) {
          qta = GestoreEvasioneAcquisto.get().getBigDecimalZero();
        }
        //qta = qta.setScale(scale, roundingMode);//Fix 30871
		qta = Q6Calc.get().setScale(qta,scale, roundingMode);//Fix 30871
        rigaSec.setQtaDaSpedireInUMSec(qta);
        rigaSec.getDocEvaAcqRiga().setAggiornamentoInOrdine(this.
            getDocEvaAcqRiga().isAggiornamentoInOrdine());
        rigaSec.aggiornaQtaEvasione(stato, forzaStato);

        rigaSec.aggiornaNumeroImballo(); // fix 13073
      }
    }
  }

  /**
   *
   * @return
   * @throws SQLException
   */
  public int delete() throws SQLException {
    int res = ErrorCodes.OK;
    boolean isCollegataAMagazzino = this.isCollegataAMagazzino();
    if (this.isRigaEstratta() && this.isOnDB() && !isCollegataAMagazzino) {
      if (this.getOldRiga() != null) {
        this.setQtaAttesaEvasione(this.getOldRiga().getQtaAttesaEvasione());
        this.setQtaPropostaEvasione(this.getOldRiga().getQtaPropostaEvasione());
      }
      res = super.delete();
      if (res >= 0) {
        if (GestoreEvasioneAcquisto.get().isAbilitaControlloConcorrenza()) {
          if (this.getRigaOrdine() != null) {
            //ini Fix 1922
            this.getRigaOrdine().getTestata().disabilitaTask(DocEvaAcq.
                TASK_ID_ESTRAZIONE);
            //fine Fix 1922
          }
        }
      }
    }
    else {
      /** @todo CODIFICARE MESSAGGIO */
      if (isCollegataAMagazzino) {
        String message = new String(
            "riga non eliminabile perchè collegata a magazzino");
        GestoreEvasioneAcquisto.get().println(message);
        if (this.getRigaOrdine() != null) {
          ( (DocEvaAcq)this.getTestata()).getRigheWarnings().put(this.
              getRigaOrdine(), message);
        }
      }
    }
    return res;
  }

  //ini FIX 1699
  /**
   *
   * @return
   * @throws SQLException
   */
  public int save() throws SQLException {
    //Fix 2844 - inizio
    if (!isOnDB()) {
      this.creazioneAutomaticaRigaContenitore();
    }
    //Fix 2844 - inizio
    int res = ErrorCodes.GENERIC_ERROR;
    ThipException ex = null;
    boolean oldIsOnDB = isOnDB();
    Timestamp oldTimestamp = this.getTimestamp();
    Integer oldNum = this.getNumeroRigaDocumento();
    boolean salvaSoloRigaOrd = false;//Fix 36394
    // fix 11614 >
    BigDecimal qtaDaSpedireOriRif = this.getQtaDaSpedireInUMRif();
    BigDecimal qtaDaSpedireOriPrm = this.getQtaDaSpedireInUMPrm();
    BigDecimal qtaDaSpedireOriSec = this.getQtaDaSpedireInUMSec();
    if (this.getDocEvaAcqRiga().isRigaEstratta() && this.getConfiguratore()!=null){
      ErrorMessage err = creaRigheConfigurazione();
      if (err != null) {
        throw new ThipException(err);
      }
      else {
        res = ErrorCodes.OK;
      }
    }
    // fix 11614 <
    boolean salvaRiga = this.getDocEvaAcqRiga().isRigaEstratta();
    try {
      if (salvaRiga) {
        ErrorMessage em = this.checkRigaModificata();
        if (em != null) {
          salvaRiga = false;
          throw new ThipException(em);
        }
      }
      if ( ( (DocEvaAcq)this.getTestata()).getRigaDaSalvareSingolarmente() != null) {
        salvaRiga = ( (DocEvaAcq)this.getTestata()).
            getRigaDaSalvareSingolarmente().getKey().equals(this.getKey());
      }
      if (salvaRiga && !this.isCollegataAMagazzino()) {
        //DocumentoOrdineRiga rigaOrd = this.getRigaOrdine();//Fix 36394
  		OrdineAcquistoRiga rigaOrd = (OrdineAcquistoRiga)this.getRigaOrdine();//Fix 36394
        if (rigaOrd != null) {
          // Inizio 3785
          boolean isOk = true; //rigaOrd.getTestata().retrieve(rigaOrd.getLockType());
          // Fine 3785
          GestoreEvasioneAcquisto.get().println(
              "Retrieve della testata ordine : '" + rigaOrd.getTestata().getKey() +
              "' res = " + isOk);
        }
        int numRiga = getNumeroRigaDocumento().intValue();
        /** @todo NUM RIGA NEG */
        if (numRiga < 0) {
          oldNum = new Integer(0 - numRiga);
          setNumeroRigaDocumento(oldNum);
        }
        //Fix 36394 Inizio
        if(this.getQtaDaSpedireInUMRif().compareTo(GestoreEvasioneVendita.get().
                getBigDecimalZero()) != 0 || !this.isRigaSaldata())
        {
        //Fix 36394 Fine
        GestoreEvasioneAcquisto.get().println("tento la save() di riga prm :'" +
                                              this.getKey() + " su articolo: " +
                                              this.getArticoloKey() +
                                              " timestamp = " + oldTimestamp);
        //Fix 5759 - inizio
        //Fix 5876 elimina fix 5759
        //setSalvaRigaOrdCollegata(false);
        //Fix 5759 - fine

        //Fix 6110 - inizio
        DocEvaAcq testata = (DocEvaAcq)getTestata();
        if (!testata.getMatricoleOrdine().isEmpty()) {
                testata.aggiornaMatricoleOrdineNumRigaPrm(
                        new Integer(numRiga), getNumeroRigaDocumento()
                );
        }
        //Fix 6110 - fine

        //Fix 7187 inizio generazione automatica assegnazioni
        boolean conAssegnazione =
           PersDatiATP.getCurrentPersDatiATP().getAttivazAccantPrenot() &&
           PersDatiATP.getLivelloControlloAccPrnMan() == PersDatiATP.LC_ERRORE;
        if(!isOnDB() && conAssegnazione)
        {
           List righeAssegnazione = getRigheAssegnazione();
           if(righeAssegnazione.isEmpty())
              generaRigheAssegnazioneDefault(getDocEvaAcqRiga().getQtaDaSpedire());
        }
        //Fix 7187 fine

        res = super.save();
        // fix 11614 >
        if (res < 0) {
          this.setQtaDaSpedireInUMRif(qtaDaSpedireOriRif);
          this.setQtaDaSpedireInUMPrm(qtaDaSpedireOriPrm);
          this.setQtaDaSpedireInUMSec(qtaDaSpedireOriSec);
         // fix 13073 >
         this.setQtaInUMAcq(this.getQtaDaSpedireInUMRif());
         this.setQtaInUMPrm(this.getQtaDaSpedireInUMPrm());
         this.setQtaInUMSec(this.getQtaDaSpedireInUMSec());
         this.aggiornaNumeroImballo();
         // fix 13073 <
          this.settaConfiguratore();
        }
        // fix 11614 <
        aggiornaAssegnazioniLotti(); //35639
	      }//Fix 36394 Inizio
        else {
                // In caso di qta zero e il flag salda riga ordine è true
                // non deve salvare la riga documento ma deve solo saldare la riga ordine
                salvaSoloRigaOrd = true;
                this.setRigaSaldata(true);
                rigaOrd.setSaldoManuale(true);
                res = this.saveRigaOrdine(rigaOrd);
              
        }
        //Fix 36394 Fine
      }
      else {
        if (this.isCollegataAMagazzino()) {
          /** @todo CODIFICARE ERRORE */
          ex = new ThipException();
          ex.setErrorMessage(new ErrorMessage("THIP_BS000"));
        }
        else {
          res = ErrorCodes.OK;
        }
      }
    }
    catch (Throwable t) {
      t.printStackTrace(Trace.excStream);
      // fix 11614 >
      if (t instanceof ThipException) {
        ex = (ThipException)t;
      }
      else {
        ex = new ThipException(t.getMessage());
        ex.setErrorMessage(new ErrorMessage("THIP_BS000"));
      }
      // fix 11614 <
    }
    finally {
      ( (DocEvaAcq)this.getTestata()).setRigaDaSalvareSingolarmente(null);
      if (salvaRiga) {
        String tipoRiga = "primaria";
        if (this.getSpecializzazioneRiga() != RIGA_PRIMARIA) {
          tipoRiga = "secondaria";
        }
        GestoreEvasioneAcquisto.get().println("save() di riga " + tipoRiga +
                                              ":'" +
                                              this.getKey() + "' -> " + res +
                                              " timestamp = " +
                                              this.getTimestamp());
      }
      boolean isAbilitaCommitRiga = ( (DocEvaAcq)this.getTestata()).
          isAbilitaCommitRiga();
      if (res < 0 || ex != null) {
        setOnDB(oldIsOnDB);
        this.setTimestamp(oldTimestamp);
        setNumeroRigaDocumento(oldNum);
        if (isAbilitaCommitRiga) {
          DocumentoOrdineRiga rigaOrd = this.getRigaOrdine();
          String rifRigaOrd = "";
          if (rigaOrd != null) {
            rifRigaOrd = " RifRigaOrd = '" + this.getRifRigaOrdineFormattato() +
                "' hash = " + rigaOrd.getTestata().hashCode();
          }
          String message = ex != null ? ex.getMessage() :
              new String(" Articolo = '" + this.getArticoloKey() +
                         rifRigaOrd + " Error Codes : " + res);
          GestoreEvasioneAcquisto.get().println(message);
          ( (DocEvaAcq)this.getTestata()).getRigheNonSalvate().put(rigaOrd,
              message);
          // ini FIX 1684
          this.setRigaEstratta(false);
          // fine FIX 1684
          res = ErrorCodes.NO_ROWS_UPDATED;
          ConnectionManager.rollback();
        }
        else {
          if (ex != null) {
            throw ex;
          }
        }
      }
      else {
        if (isAbilitaCommitRiga) {
          this.getDocEvaAcqRiga().setAggiornamentoInOrdine(false);
          ConnectionManager.commit();
        }
      }
    }
    return res;
  }

  //fine FIX 1699
  
  // 35639 inizio
  public void aggiornaAssegnazioniLotti() {
	  DocEvaAcq docAcq = (DocEvaAcq)this.getTestata();
	  if(docAcq.getGestoreAssegnazioneLotti() != null) {
		  List righeSec = getRigheSecondarie();
		  for (int i = 0; i < righeSec.size(); i++) {
			  DocEvaAcqRigaSec rigaSec = (DocEvaAcqRigaSec) righeSec.get(i);
            
            Magazzino magazzinoSec = docAcq.getMagazzinoPerAssegnazioneLotti(rigaSec);
            //Fix 35639 inizio
            if(magazzinoSec == null)
            	continue;
            //Fix 35639 fine
            boolean qtaUscitaGiaInGiacenzaSec = magazzinoSec.getDspQtaPrpSpeRil();
            boolean daStornoQtaLottiRigaSec = true;

            if (rigaSec.getStatoAvanzamento() == StatoAvanzamento.PROVVISORIO && 
            		!qtaUscitaGiaInGiacenzaSec)
            	daStornoQtaLottiRigaSec =  false;
            
            //if(!rigaSec.isAbilitatoAggiornamentoSaldiRigaSec())
            if(!rigaSec.isAbilitatoAggiornamentoSaldi())
            	daStornoQtaLottiRigaSec =  false;
            
			if(daStornoQtaLottiRigaSec) {
	 			 Iterator lottiRigSec = rigaSec.getRigheLotto().iterator();
	 			 while (lottiRigSec.hasNext()) {
	 				 DocumentoAcqRigaLottoSec lottoSec = (DocumentoAcqRigaLottoSec)lottiRigSec.next();
					 String keySaldo = docAcq.getKeySaldo(lottoSec, magazzinoSec.getIdMagazzino());
			        	   
					 BigDecimal qtaPrm = lottoSec.getServizioQta().getQuantitaInUMPrm();
					 if(keySaldo != null) {
						 docAcq.getGestoreAssegnazioneLotti().stornoQtaLotto(keySaldo, qtaPrm);
						 docAcq.getGestoreAssegnazioneLotti().stornoQtaLottoEstratta(keySaldo, qtaPrm);
					 }
				 } 
			}
            
          }
      }
  }
  

  // 35639 fine

  /**
   * Ridefinisce il metodo per evitare la gestione delle righe omaggio perchè sono
   * già gestite dalla riga ordine
   * @param testata
   * @param rc
   * @return
   * @throws SQLException
   */
  protected int gestioneRigheOmaggio(DocumentoAcquisto testata, int rc) throws
      SQLException {
    // nulla da fare
    return ErrorCodes.OK;
  }

  /**
   * Ridefinisce il metodo per evitare la gestione kit perchè sono già gestite dalla
   * riga ordine
   */
  protected void gestioneKit() throws SQLException {
    // nulla da fare
  }

  /**
   * Ridefinisce il metodo perchè non è necessario recuperare i dati già presenti
   * @return
   */
  protected boolean recuperoDatiVenditaSave() {
    return false;
  }

  /**
   * Instanzia un lotto dummy di classe erede di DocEvaAcqRigaLotto
   * @return
   */
  protected DocumentoRigaLotto creaLottoDummy() {
    DocumentoAcqRigaLotto lottoD;
    lottoD = (DocEvaAcqRigaLottoPrm) Factory.createObject(DocEvaAcqRigaLottoPrm.class);
    lottoD.setFather(this);
    lottoD.setIdAzienda(getIdAzienda());
    lottoD.setIdLotto(Lotto.LOTTO_DUMMY);
    lottoD.setIdArticolo(getIdArticolo());
    // controlla presenza lotto
    try {
      String keyLotto = KeyHelper.buildObjectKey(new String[] {
                                                 getIdAzienda(), getIdArticolo(),
                                                 Lotto.LOTTO_DUMMY});
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
   * Ridefinito perchè se sono in dettaglio di riga una variazione di stato
   * avanzamento si ripercuote anche sulle righe. Il metodo è condizionato al
   * isInDettaglio perchè la forzatura non deve essere lanciata durante la fase
   * di creazione o retrieve della riga.
   * @param statoAvanzamento
   */
  public void setStatoAvanzamento(char statoAvanzamento) {
    if (isInDettaglio()) {
      this.forzaStatoAvanzamento(statoAvanzamento, false);
    }
    super.setStatoAvanzamento(statoAvanzamento);
  }

  /**
   *
   */
  public void finalize() {
    if (GestoreEvasioneAcquisto.get().isAbilitaControlloConcorrenza()) {
      if (this.getRigaOrdine() != null) {
        if (GestoreEvasioneAcquisto.get().isAbilitaControlloConcorrenza()) {
          //ini Fix 1922
          //this.getRigaOrdine().disabilitaTask(DocEvaAcq.TASK_ID_ESTRAZIONE);
          //fine Fix 1922
        }
      }
    }
  }

  /**
   * Ridefinito
   * @param tipoLavEsterna
   * @param ricercaGerarchica
   * @throws SQLException
   */
  protected void verificaGenerazioneRigheSecondarie(char tipoLavEsterna,
      boolean ricercaGerarchica) throws SQLException {
    // non deve fare nulla
  }

  /**
   * Ridefinito
   * @param riga
   * @return
   */
  public boolean isCollegataAMagazzino() {
    boolean isOk = false;
    ThipException thipException = null;
    String strError = "";
    isOk = super.isCollegataAMagazzino();
    if (this.isOnDB() && !isOk) {
      //Fix 3331 BP ini...
      String select = "SELECT * FROM " + DocumentoAcqRigaPrmTM.TABLE_NAME;
      String where = " WHERE " +
          " " + DocumentoAcqRigaPrmTM.ID_AZIENDA + " = '" + this.getIdAzienda() +
          "' " +
          " AND " + DocumentoAcqRigaPrmTM.ID_ANNO_DOC + " = '" +
          this.getAnnoDocumento() + "' " +
          " AND " + DocumentoAcqRigaPrmTM.ID_NUMERO_DOC + " = '" +
          this.getNumeroDocumento() + "' " +
          " AND " + DocumentoAcqRigaPrmTM.ID_RIGA_DOC + " = " +
          this.getNumeroRigaDocumento() + " ";
      String statment = select + where;
      CachedStatement cs = new CachedStatement(statment);
      ResultSet rs = null;
      try {
        rs = cs.executeQuery();
        while (rs.next()) {
          String strColMag = rs.getString("COL_MAGAZZINO");
          if (strColMag != null) {
            this.setCollegatoAMagazzino(strColMag.charAt(0));
            isOk = super.isCollegataAMagazzino();
          }
        }
      }
      catch (SQLException sql1) {
        sql1.printStackTrace(Trace.excStream);
      }
      finally {
        try {
          if (rs != null) {
            rs.close();
          }
          cs.free();
        }
        catch (SQLException sql2) {
          sql2.printStackTrace(Trace.excStream);
        }
      }
      /* il blocco precedente è stato sostituito con il seguente...
       DocumentoAcqRigaPrm rigaTmp = (DocumentoAcqRigaPrm) Factory.createObject(
          DocumentoAcqRigaPrm.class);
             rigaTmp.setKey(this.getKey());
             try {
        isOk = rigaTmp.retrieve();
        if (isOk) {
          isOk = rigaTmp.isCollegataAMagazzino();
          if (isOk) {
            this.setCollegatoAMagazzino(rigaTmp.getCollegatoAMagazzino());
          }
        }
             }
             catch (Throwable t) {
        t.printStackTrace(Trace.excStream);
             }
       */
      //Fix 3331 BP fine.
    }
    return isOk;
  }

  /**
   * Ridefinito
   * @return
   */
  // Inizio 4720
/*
  public ErrorMessage checkRigaOrdineNoFrazionabile() {
    ErrorMessage em = null;
    if (! ( (DocEvaAcq)this.getTestata()).isDocumentoDiRicevimentoProdotto()) {
      em = super.checkRigaOrdineNoFrazionabile();
    }
    return em;
  }
*/
  public ErrorMessage checkRigaOrdineNoFrazionabile() {
    ErrorMessage em = null;
    if (this.getDocEvaAcqRiga().isAbilitaControlloQtaZero()) {//Fix 36394
    DocEvaAcq docEvaAcq = (DocEvaAcq)getTestata();
    if (! docEvaAcq.isDocumentoDiRicevimentoProdotto() && docEvaAcq.isDocumentoDiContoLavoro() ) {
      em = super.checkRigaOrdineNoFrazionabile();
    }
    }//Fix 36394
    return em;
  }
 //Fix 36394 Inizio
  protected boolean isQtaOrdinataUgualeZero() {
	    return this.getDocEvaAcqRiga().isQtaOrdinataUgualeZero();
  }
//Fix 36394 Fine
  // Fine 4720


  // per la UI ---v
  public String getRifRigaOrdineFormattato() {
    return this.getDocEvaAcqRiga().getRifRigaOrdineFormattato();
  }

  public QuantitaInUMRif getQtaOrdinata() {
    return this.getDocEvaAcqRiga().getQtaOrdinata();
  }

  public QuantitaInUMRif getQtaResidua() {
    return this.getDocEvaAcqRiga().getQtaResidua();
  }

  public boolean isRigaEstratta() {
    return this.getDocEvaAcqRiga().isRigaEstratta() || this.isOnDB();
  }

  public void setRigaEstratta(boolean isOk) {
    this.getDocEvaAcqRiga().setRigaEstratta(isOk);
  }

  public boolean isStatoAvanzamentoDef() {
    return this.getDocEvaAcqRiga().isStatoAvanzamentoDef();
  }

  public void setStatoAvanzamentoDef(boolean statoDefinitivo) {
    this.getDocEvaAcqRiga().setStatoAvanzamentoDef(statoDefinitivo);
  }

  // DA SPEDIRE
  public void setQtaDaSpedireInUMRif(BigDecimal qta) {
    this.getDocEvaAcqRiga().getQtaDaSpedire().setQuantitaInUMRif(qta);
  }

  public void setQtaDaSpedireInUMPrm(BigDecimal qta) {
    this.getDocEvaAcqRiga().getQtaDaSpedire().setQuantitaInUMPrm(qta);
  }

  public void setQtaDaSpedireInUMSec(BigDecimal qta) {
    this.getDocEvaAcqRiga().getQtaDaSpedire().setQuantitaInUMSec(qta);
  }

  public BigDecimal getQtaDaSpedireInUMRif() {
    return this.getDocEvaAcqRiga().getQtaDaSpedire().getQuantitaInUMRif();
  }

  public BigDecimal getQtaDaSpedireInUMPrm() {
    return this.getDocEvaAcqRiga().getQtaDaSpedire().getQuantitaInUMPrm();
  }

  public BigDecimal getQtaDaSpedireInUMSec() {
    return this.getDocEvaAcqRiga().getQtaDaSpedire().getQuantitaInUMSec();
  }

  public BigDecimal getQtaResiduaInUMRif() {
    return this.getDocEvaAcqRiga().getQtaResidua().getQuantitaInUMRif();
  }

  public BigDecimal getQtaResiduaInUMPrm() {
    return this.getDocEvaAcqRiga().getQtaResidua().getQuantitaInUMPrm();
  }

  public BigDecimal getQtaResiduaInUMSec() {
    return this.getDocEvaAcqRiga().getQtaResidua().getQuantitaInUMSec();
  }

  // per la UI ---^

  public ErrorMessage checkValute() {
    return null;
  }

  // Fix 1932
  protected CommentHandler getCommentiIntestatario() {
    return this.getRigaOrdine().getCommentHandler();
  }

  // Fine fix 1932

  //ini Fix 2006
  protected boolean recuperoDatiVenAcqOnSave() {
    return false;
  }

  //fine Fix 2006

  //ini Fix 2028
  public java.sql.Date getDataConsegnaConfermataRO() {
    return super.getDataConsegnaConfermata();
  }

  public String getIdEsternoConfig() {
    String s = null;
    Configurazione config = getConfigurazione();
    if (config != null) {
      s = config.getIdEsternoConfig();
    }
    return s;
  }

  public String getDescrizioneConfig() {
    String s = "";
    Configurazione config = getConfigurazione();
    if (config != null) {
      DescrizioneInLingua dNLS = config.getDescrizione();
      if (dNLS != null) {
        s = dNLS.getDescrizione();
      }
    }
    return s;
  }

  //fine Fix 2028

  //...FIX 3099 inizio
  public boolean controllaLottiPrm() {

    boolean statoDef = getStatoAvanzamento() == StatoAvanzamento.DEFINITIVO ? true : false;
    //...Eseguo il controllo solo per le righe che non sono onDB
    //...e che hanno un articolo gestito a lotti
    if (!isOnDB() && getArticolo().getArticoloDatiMagaz().isArticLotto()) {

      //...Controllo se sono un documento di entrata degli acquisti
      if (!getLavEsterna() && !isRigaReso()) {
        //...Controllo che la creazione automatica sia impostata
        //...e nel caso sia impostata lo stato sarà definitivo
        boolean ok = proponiLotto(PersDatiMagazzino.TIPO_ACQ);
        if (ok)
          statoDef = true;
      }

      //...Controllo se sono in un documento di entrata da conto lavoro
      if (isSoloGestioneRigaPrimaria() && !isRigaReso()) {
        //...Controllo che la creazione automatica sia impostata
        boolean ok = proponiLotto(PersDatiMagazzino.TIPO_CL);
        //...e nel caso sia impostata lo stato sarà definitivo
        if (ok)
          statoDef = true;
      }

      //...Controllo tutte le righe secondarie
      List righeSec = getRigheSecondarie();
      boolean allRigheSecOk = true;
      for (int i = 0; i < righeSec.size(); i++) {
        DocEvaAcqRigaSec docEvaRigaSec = (DocEvaAcqRigaSec) righeSec.get(i);
        boolean rigaSecOk = docEvaRigaSec.controllaLottiSec();
        // Inizio 5590
        //32416 inizio commentato
        /*if (!rigaSecOk) {  
          allRigheSecOk = false;
        }*/
        //32416 fine commentato
        // Fine 5590
        //if (rigaSecOk) {  //32416
        if (rigaSecOk && ((DocEvaAcq)getTestata()).getStatoAvanzamentoUt() == StatoAvanzamento.DEFINITIVO) {  //32416          
                  docEvaRigaSec.sistemaQuantitaDopoVariazioneStatoAv(StatoAvanzamento.DEFINITIVO);
        }
        else {
        	docEvaRigaSec.sistemaQuantitaDopoVariazioneStatoAv(StatoAvanzamento.PROVVISORIO);
        	allRigheSecOk = false; //32416
                
        }
      }
      /*
      if (!allRigheSecOk)
        statoDef = false;
      */

      // Inizio 5590
      if (!allRigheSecOk){
              statoDef = false;
              if (!isSpedizioneComponenti() && getStatoAvanzamento() == StatoAvanzamento.DEFINITIVO){
                      setStatoAvanzamento(StatoAvanzamento.PROVVISORIO);
                     //Fix 17927 inizio
                      if (!isInDettaglio())//32416
                      {
                      try{
                              getQtaPropostaEvasione().setEqual(getQtaAttesaEvasione());
                      }catch(CopyException ex){
                              ex.printStackTrace(Trace.excStream);
                      }
                      }
                      // //Fix 17927 fine
              }

      }
      // Fine 5590


      //...FIX 3099 fine
    }
    return statoDef;
  }

  //...FIX 3099 fine


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
// Fine Inizio 3307
// Fine 3266

  // Inizio 3942
  protected boolean controllaQuadraturaQtaLotti(QuantitaInUMRif
                                                totaleQtaPropostaLotti,
                                                QuantitaInUMRif
                                                totaleQtaAttesaLotti) {
    boolean quadratura = true;
    if (getStatoAvanzamento() == StatoAvanzamento.PROVVISORIO &&
        getQtaPropostaEvasione() != null) {
      if (totaleQtaPropostaLotti.absRif().compareTo(getQtaPropostaEvasione().
          absRif()) < 0)
        quadratura = false;
      // Inizio 3942
      // Inizio 8467
      /*
      if (!quadratura)
        quadratura = aggiornaRigheLotto(totaleQtaPropostaLotti.absRif());
      */
      // Fine 8467
      // Fine 3942
    }
    if (getStatoAvanzamento() == StatoAvanzamento.DEFINITIVO &&
        getQtaAttesaEvasione() != null) {
      if (totaleQtaAttesaLotti.absRif().compareTo(getQtaAttesaEvasione().absRif()) <
          0)
        quadratura = false;
    }
    return quadratura;
  }
  // Fine 3942

  /**
   * FIX04706 - DZ.
   * Ridefinito affinchè non scatti il controllo di esistenza righe secondarie
   * (inutile in fase di evasione se sono già presenti le righe secondarie sull'ordine).
   * @return ErrorMessage
   */
  protected ErrorMessage checkRigheSecondarie(){
    if (getRigheSecondarie().isEmpty())
      return super.checkRigheSecondarie();
    return null;
  }

  // Inizio 4790
  public boolean isSaldoAutomatico() {
    boolean saldoAutomatico = false;
    if (getDocEvaAcqRiga() != null &&
        getDocEvaAcqRiga().getRigaDoc() != null &&
        getDocEvaAcqRiga().getRigaDoc().getRigaOrdine() != null) {
       OrdineAcquistoTestata ordAcq = (OrdineAcquistoTestata)getDocEvaAcqRiga().getRigaDoc().getRigaOrdine().getTestata();
       if (ordAcq != null && ordAcq.getTipoEvasioneOrdine() == OrdineTestata.SALDO_AUTOMATICO) {
          saldoAutomatico = true;
       }
    }
    return saldoAutomatico;
  }
  // Fine 4790


  //Fix 5179 - inizio
  /**
   * Verifica se sussistono le condizioni per creare la riga secondaria del
   * documento.
   * La riga sec. NON viene generata se sussistono TUTTE le seguenti condizioni:
   * 1) l'articolo della riga sec. dell'ordine ha come modalità di prelievo
   *    FLOOR_STOCK_NON_CONTROLLATO
   * 2) il documento ha SPEDIZIONE come tipo di causale
   */
  private static boolean generaRigaSecDocOk(DocEvaAcq docEva, OrdineAcquistoRigaSec ordRigaSec) {
          boolean ret = true;
          Articolo artRigaSec = ordRigaSec.getArticolo();
          if (artRigaSec != null) {
                  if (artRigaSec.getModalitaPrelievo() == ArticoloDatiProduz.SINGOLA) {
                    CausaleDocumentoTestataAcq cauDoc = docEva.getCausale();
                    if (cauDoc != null) {
                            char tipoDoc = cauDoc.getTipoDocumento();
                            if (tipoDoc == TipoDocumentoAcq.SPED_LAV_ESN) {
            ret = false;
//MG FIX 6342 inizio
            if (ordRigaSec.getCausaleRiga().getTipoDistintaLavEsterna() == TipoDistintaLavEsterna.COMP_PROD) {
              String ordRigaSecArt = ordRigaSec.getIdArticolo();
              String ordRigaPrmArt = ordRigaSec.getRigaPrimaria().getIdArticolo();
              if (ordRigaSecArt.equals(ordRigaPrmArt))
                ret = true;
            }
//MG FIX 6342 fine
                            }
                    }
                  }
          }
          return ret;
  }

  //Fix 5170 - fine

  //Fix 5348 - inizio
  /**
   * Ridefinizione
   */
  public ErrorMessage checkArticoloEsclusione() {
	  //Fix 34783 inizio
	  DocEvaAcq testata = (DocEvaAcq)getTestata();
	    if (testata.isAttivaCheckDichiarazioneMatricole()) {
	    	return super.checkArticoloEsclusione();
	    }
	  //Fix 34783 fine
          return null;
  }
  //Fix 5348 - fine

  // Inizio 5395
  /**
   * Verifica che la qtà di una riga non sia 0 (zero)
   */
  public ErrorMessage checkQtaOrdinataDiversaDaZero()
  {
          boolean isAttivaCheckQtaOrdinata = ((DocEvaAcq)getTestata()).getAttivaCheckQtaOrdinata();
          if (isAttivaCheckQtaOrdinata)
                  return super.checkQtaOrdinataDiversaDaZero();
          return null;
  }
  // Fine 5395

  // Inizio 5566
  public void sistemaQuadraturaLotti()
  {
    //DocEvaAcqRigaLottoPrm docRigaLotto = ((DocEvaAcqRigaLottoPrm)getUnicoLottoEffettivo());  //29440
  	DocumentoOrdineRigaLotto docRigaLotto = (DocumentoOrdineRigaLotto)getUnicoLottoEffettivo();  //29440
  	
    QuantitaInUMRif zeroQta = new QuantitaInUMRif();
    zeroQta.azzera();
    QuantitaInUMRif deltaQta = new QuantitaInUMRif();
    if (docRigaLotto == null){
      super.sistemaQuadraturaLotti();//Fix 17924
      return;
    }
    //Fix 13342 PM >
    if (getArticolo().getArticoloDatiMagaz().isLottoUnitario()){
      super.sistemaQuadraturaLotti();//Fix 17924
      return;
    }
    //Fix 13342 PM <

    sistemoLeQuantita();
    
    if(docRigaLotto instanceof DocEvaAcqRigaLottoPrm) { //29440 In caso di generazione automatica di lotto (ND per esempio) nel documento viene creato una instanceof DocumentoOrdineRigaLotto
    	DocEvaAcqRigaLottoPrm docEvaRigaLotto = ((DocEvaAcqRigaLottoPrm)getUnicoLottoEffettivo()); //29440
    	if (getStatoAvanzamento()== StatoAvanzamento.PROVVISORIO){
    		deltaQta = getQtaPropostaEvasione().subtract(docEvaRigaLotto.getQtaPropostaEvasione());
    		if (deltaQta.compareTo(zeroQta) != 0){
    			docEvaRigaLotto.getDocEvaAcqRigaLotto().setQtaDaSpedire(docEvaRigaLotto.getQtaPropostaEvasione().add(deltaQta));
    		}
    	}
    	else if (getStatoAvanzamento() == StatoAvanzamento.DEFINITIVO){
    		deltaQta = getQtaAttesaEvasione().subtract(docEvaRigaLotto.getQtaAttesaEvasione());
    		if (deltaQta.compareTo(zeroQta) != 0){
    			docEvaRigaLotto.getDocEvaAcqRigaLotto().setQtaDaSpedire(docEvaRigaLotto.getQtaAttesaEvasione().add(deltaQta));
    		}
    	}
    }
    super.sistemaQuadraturaLotti();
  }

  public DocumentoOrdineRigaLotto getUnicoLottoEffettivo(){ // Fix 6920
          //DocEvaAcqRigaLottoPrm rigalotto = null; //Fix 14253 PM
    DocumentoOrdineRigaLotto rigalotto = null;   //Fix 14253 PM
    ArrayList listaLotti = (ArrayList)getRigheLotto();
    if (listaLotti.size() == 1){
            //DocEvaAcqRigaLottoPrm rigalottoTmp  = (DocEvaAcqRigaLottoPrm)listaLotti.get(0); //Fix 14253 PM
      DocumentoOrdineRigaLotto rigalottoTmp  = (DocumentoOrdineRigaLotto)listaLotti.get(0); //Fix 14253 PM
      if (!rigalottoTmp.getIdLotto().equals(LOTTO_DUMMY))
        rigalotto = rigalottoTmp;
    }
    return rigalotto;
  }
  // Fine 5566


  //Fix 6110 - inizio
  /**
   * Ridefinizione.
   * Serve per aggiornare le HashMap che contengono i dati delle matricole
   * in seguito al possibile cambiamento di numero riga che potrebbe verificarsi
   * dopo la chiamata a questo metodo.
   */
  protected void componiChiave() {
          Integer vecchioNumRiga = getNumeroRigaDocumento();
    super.componiChiave();
    ((DocEvaAcq)getTestata()).aggiornaMatricoleOrdineNumRigaPrm(vecchioNumRiga, getNumeroRigaDocumento());
  }
  //Fix 6110 - fine


  //Fix 6294 - inizio
  /**
   * Ridefinizione
   */
  protected CommentHandler getCommentiParteIntestatario() {
          return null;
  }
  //Fix 6294 - fine

  // Inizio 6652
  public static boolean controllaPrezzoSecondoCausale(OrdineAcquistoRigaPrm rigaOrd, CausaleDocumentoRigaAcq cauRigaDoc){
    boolean isRigaEstratta = true;
    BigDecimal prezzoRigaOrd = rigaOrd.getPrezzo();
    boolean prezzoNonSignificativo = (prezzoRigaOrd == null || prezzoRigaOrd.compareTo(new BigDecimal(0)) == 0);
    if (cauRigaDoc != null){
      if (cauRigaDoc.getTipiGestione().getTPGestionePrezzo().getTipoGestione() == TipoGestione.GESTITO_OBBLIG &&
          prezzoNonSignificativo){
        isRigaEstratta = false;
      }
    }
    return isRigaEstratta;
  }
  // Fine 6652

   //Fix 7187 inizio
   public boolean isRigaConAccPrn()
   {
      return PersDatiATP.getCurrentPersDatiATP().getAttivazAccantPrenot() &&
             PersDatiATP.getLivelloControlloAccPrnMan() != PersDatiATP.LC_NIENTE;
   }

   public boolean isRigaConAssegnazione()
   {
      boolean conAssegnazione =
         PersDatiATP.getCurrentPersDatiATP().getAttivazAccantPrenot() &&
         PersDatiATP.getLivelloControlloAccPrnMan() == PersDatiATP.LC_ERRORE;

      if(conAssegnazione &&
         (!isOnDB() || isOnDB() && !isCollegataAMagazzino()))
      {
         OrdineAcquistoRigaPrm rigaOrdine = (OrdineAcquistoRigaPrm)getRigaOrdine();
         // Fix 24384 inizio
         if(rigaOrdine != null)
           conAssegnazione = rigaOrdine.isAccPrnPresente();
         else
           conAssegnazione = false;
         // Fix 24384 fine
      }
      return conAssegnazione;
   }

   public boolean isCambioCfgAbilitato()
   {
      boolean cambioCfgAbilitato = !isOnDB() && PersDatiATP.getCurrentPersDatiATP().isCambioCfgEvasioneAcquisto();
      if(cambioCfgAbilitato)
         cambioCfgAbilitato = getArticolo().getIdSchemaCfg() != null;
      return cambioCfgAbilitato;
   }

   public ErrorMessage checkAssegnazioni()
   {
      if(!isAbilitaCheckAssegnazioni())
         return null;

      ErrorMessage err = null;

      BigDecimal qtaDaSped = getDocEvaAcqRiga().getQtaDaSpedireInUMPrm();
      BigDecimal qtaResRiga = getQtaResiduaInUMPrm();
      qtaResRiga = qtaResRiga.subtract(qtaDaSped);
      if(isRigaSaldata())
         qtaResRiga = ZERO_DEC;

      BigDecimal residuoPrenotazioni = ZERO_DEC;
      BigDecimal totaleAssegnazioni = ZERO_DEC;
      Iterator iter = getRigheAssegnazione().iterator();
      while(iter.hasNext())
      {
         DocumentoAcqRigaAssPrm rigaAss = (DocumentoAcqRigaAssPrm)iter.next();
         residuoPrenotazioni = residuoPrenotazioni.add(rigaAss.getQtaResiduaPrenotazionePrm());
         totaleAssegnazioni = totaleAssegnazioni.add(rigaAss.getQtaAssegnataUMPrm());
      }

      //esistono delle prenotazioni non soddisfatte
      if(totaleAssegnazioni.compareTo(qtaDaSped) > 0)
      {
         err = new ErrorMessage("THIP200405");
      }
      else if(residuoPrenotazioni.compareTo(ZERO_DEC) > 0)
      {
         if(qtaResRiga.compareTo(residuoPrenotazioni) < 0)
            err = new ErrorMessage("THIP200406");
      }

      return err;
   }
   //Fix 7187 fine

   //...FIX 8805 inizio

   /**
    * convertiQuantita
    * @param articolo Articolo
    * @param valore BigDecimal
    * @param source UnitaMisura
    * @param target UnitaMisura
    * @param pr ParamRigaPrmDocEvaAcq
    * @return BigDecimal
    */
   protected BigDecimal convertiQuantita(Articolo articolo, BigDecimal valore, UnitaMisura source, UnitaMisura target, ParamRigaPrmDocEvaAcq pr) {
     return articolo.convertiUM(valore, source, target, getArticoloVersRichiesta()); // fix 10955
   }

   //...FIX 8805 fine

   //...FIX 9450 inizio

   // fix 10955 >
   /**
    * @deprecated
    */
   /*
   public QuantitaInUMRif calcolaQuantitaArrotondate(Articolo articolo, BigDecimal quantOrigine, UnitaMisura umRif, UnitaMisura umPrm, UnitaMisura umSec, char idUMDigitata){
     return calcolaQuantitaArrotondate(articolo, quantOrigine, umRif, umPrm, umSec, null, idUMDigitata);
   }
   */
   public QuantitaInUMRif calcolaQuantitaArrotondate(Articolo articolo, BigDecimal quantOrigine, UnitaMisura umRif, UnitaMisura umPrm, UnitaMisura umSec, ArticoloVersione versione, char idUMDigitata){
     return articolo.calcolaQuantitaArrotondate(quantOrigine, umRif, umPrm, umSec, versione, idUMDigitata);
   }

   // fix 10955 <

   //...FIX 9450 fine

   // fix 11415
   public void setConfiguratore(DocEvaAcqConfiguratore conf){
     iConfiguratore = conf;
   }
   public DocEvaAcqConfiguratore getConfiguratore(){
     return iConfiguratore;
   }

   public void setRicalcola(boolean b){
     iRicalcola = b;
   }
   public boolean isRicalcola(){
     return iRicalcola;
   }

   // fix 11606 >
   public void settaConfiguratore(){
     DocEvaAcqConfiguratore config = (DocEvaAcqConfiguratore)Factory.createObject(DocEvaAcqConfiguratore.class);
     config.setRigaOrdine(this.getRigaOrdine());
     config.setIdAzienda(this.getIdAzienda());
     config.setIdArticolo(this.getIdArticolo());
     config.setIdVersioneRcs(this.getIdVersioneRcs());
     config.setDataConsegnaConfermata(this.getDataConsegnaConfermata());

     Configurazione conf = this.getRigaOrdine().getConfigurazione();
     BigDecimal qtaPrm = this.getQtaDaSpedireInUMPrm();
     BigDecimal qtaSec = this.getQtaDaSpedireInUMSec();
     BigDecimal qtaRif = this.getQtaDaSpedireInUMRif();
     config.setQtaDaSpedirePrm(qtaPrm);
     config.setQtaDaSpedireRif(qtaRif);
     config.setQtaDaSpedireSec(qtaSec);
     //fix 11840 >
     /*
     DocEvaAcqConfigurazione confa = (DocEvaAcqConfigurazione)Factory.createObject(DocEvaAcqConfigurazione.class);
     confa.setConfiguratore(config);
     confa.setArticolo(this.getArticolo());
     confa.setConfigurazione(conf);
     confa.setIdAzienda(this.getIdAzienda());
     confa.setQtaInUMPrm(qtaPrm);
     confa.setQtaInUMRif(qtaRif);
     confa.setQtaInUMSec(qtaSec);
     boolean isConfAttNeutra = this.getConfigurazione().getIdEsternoConfig().equals(Configurazione.CONFIGURAZIONE_NEUTRA);
     if (!isConfAttNeutra) {
       config.getDocEvaAcqConfig().add(confa);
     }
     */
     // fix 11840 <
     this.setConfiguratore(config);
   }
   // fix 11606 <

   // fix 11614 >
   public ErrorMessage creaRigheConfigurazione() {
     ErrorMessage err = null;
     String rifRigaOrd = this.getDocEvaAcqRiga().getRifRigaOrdineFormattato();
     boolean analizzaConfigurazioni = false; // fix 11840
     boolean isQtaTotalmenteAssegnata = false; // fix 13073
     boolean rigaCorrenteEstratta = true;
     try {
       int rit = 0;
       analizzaConfigurazioni = this.getConfiguratore() != null && !this.getConfiguratore().getDocEvaAcqConfig().isEmpty(); // fix 11840
       if (analizzaConfigurazioni) { // fix 11840
         ((DocEvaAcq)this.getTestata()).setInCreaRigheConfigurazione(true);
         List listaCfg = new ArrayList(this.getConfiguratore().getDocEvaAcqConfig());
         OrdineAcquistoRigaPrm rigaOrd = (OrdineAcquistoRigaPrm)this.getRigaOrdine();
         this.setIdVersioneRcs(this.getConfiguratore().getIdVersioneRcs());

         BigDecimal qtaOrdAggRif = GestoreEvasioneAcquisto.get().getBigDecimalZero();
         BigDecimal qtaOrdAggPrm = GestoreEvasioneAcquisto.get().getBigDecimalZero();
         BigDecimal qtaOrdAggSec = GestoreEvasioneAcquisto.get().getBigDecimalZero();

           // fix 13073 >
           //BigDecimal qtaDaSpedireOriRif = this.getQtaDaSpedireInUMRif(); // fix 11659
           BigDecimal qtaDaSpedireOriRif = this.getQtaResiduaInUMRif();
           isQtaTotalmenteAssegnata = this.getConfiguratore().isQtaTotalmenteAssegnata(qtaDaSpedireOriRif); // fix 13073
           // fix 13073 <

         boolean isConfAttNeutra = this.getConfigurazione().getIdEsternoConfig().equals(Configurazione.CONFIGURAZIONE_NEUTRA);
         boolean isAssegnaResiduo = false;
         boolean isRiassegnaCfg = false;
         boolean isAggiornaQtaOrd = false;

         DocEvaAcqConfigurazione  confaNew = confStessaConfigurazione(listaCfg);
         if (confaNew!=null) {
           this.setQtaInUMAcq(confaNew.getQtaInUMRif());
           this.setQtaInUMPrm(confaNew.getQtaInUMPrm());
           this.setQtaInUMSec(confaNew.getQtaInUMSec());
           this.setQtaDaSpedireInUMRif(confaNew.getQtaInUMRif());
           this.setQtaDaSpedireInUMPrm(confaNew.getQtaInUMPrm());
           this.setQtaDaSpedireInUMSec(confaNew.getQtaInUMSec());
             // fix 13073 >
             this.aggiornaNumeroImballo();
             // fix 13073 <

           isAssegnaResiduo = true;
           qtaOrdAggRif = rigaOrd.getQtaInUMRif();
           qtaOrdAggPrm = rigaOrd.getQtaInUMPrmMag();
           qtaOrdAggSec = rigaOrd.getQtaInUMSecMag();
         }
         else {
           rigaCorrenteEstratta = false;
           confaNew = (DocEvaAcqConfigurazione)listaCfg.get(0);
           // fix 11659 >
           boolean isRiassegnaConfAttNeutra = false;
           if (qtaDaSpedireOriRif.compareTo(confaNew.getQtaInUMRif()) == 0 || this.getCausaleRiga().isLavEsterna()) { // fix 11840
             isRiassegnaConfAttNeutra = true;
           }
           // fix 11659 >
           this.setQtaInUMAcq(confaNew.getQtaInUMRif());
           this.setQtaInUMPrm(confaNew.getQtaInUMPrm());
           this.setQtaInUMSec(confaNew.getQtaInUMSec());
           this.setQtaDaSpedireInUMRif(confaNew.getQtaInUMRif());
           this.setQtaDaSpedireInUMPrm(confaNew.getQtaInUMPrm());
           this.setQtaDaSpedireInUMSec(confaNew.getQtaInUMSec());
             // fix 13073 >
             this.aggiornaNumeroImballo();
             // fix 13073 <

             if (isConfAttNeutra && !isRiassegnaConfAttNeutra || (this.isRigaSaldata() && !((DocumentoAcquisto)this.getTestata()).isDocumentoDiRicevimentoProdotto())) { // fix 11659 // fix 11822
             isAssegnaResiduo = true;
             qtaOrdAggRif = rigaOrd.getQtaInUMRif();
             qtaOrdAggPrm = rigaOrd.getQtaInUMPrmMag();
             qtaOrdAggSec = rigaOrd.getQtaInUMSecMag();
           }
           else {
             isRiassegnaCfg = true;
             this.setIdConfigurazione(confaNew.getIdConfigurazione());
             listaCfg.remove(0);
             // fix 11659 >
             if (isRiassegnaConfAttNeutra) {
               rigaCorrenteEstratta = true;
             }
             // fix 11659 <
           }
         }
           // fix 13073 >
           if (isQtaTotalmenteAssegnata) {
             isAssegnaResiduo = false;
           }
           // fix 13073 <
         Iterator iter = listaCfg.iterator();
         while (iter.hasNext()) {
           DocEvaAcqConfigurazione confa = (DocEvaAcqConfigurazione) iter.next();
           DocEvaAcqRigaPrm rigaNew = null;
             // fix 13073 >
             if (!iter.hasNext() && isQtaTotalmenteAssegnata) {
                rigaNew = this;
             }
             else {
                try {
                   rigaNew = this.creaRigaDocumentoConfigurata();
                }
                catch (ThipException tx) {
                   err = tx.getErrorMessage();
                }
             }
              // fix 13073 <
           if (rigaNew == null) {
             break;
           }
           else {
             BigDecimal confaQtaInUMRif = confa.getQtaInUMRif() == null ? new BigDecimal(0) : confa.getQtaInUMRif();
             BigDecimal confaQtaInUMPrm = confa.getQtaInUMPrm() == null ? new BigDecimal(0) : confa.getQtaInUMPrm();
             BigDecimal confaQtaInUMSec = confa.getQtaInUMSec() == null ? new BigDecimal(0) : confa.getQtaInUMSec();
             if (isAssegnaResiduo) {
               qtaOrdAggRif = qtaOrdAggRif.subtract(confaQtaInUMRif);
               qtaOrdAggPrm = qtaOrdAggPrm.subtract(confaQtaInUMPrm);
               qtaOrdAggSec = qtaOrdAggSec.subtract(confaQtaInUMSec);
             }
             rigaNew.setQtaInUMAcq(confaQtaInUMRif);
             rigaNew.setQtaInUMPrm(confaQtaInUMPrm);
             rigaNew.setQtaInUMSec(confaQtaInUMSec);
                // fix 13073 >
                rigaNew.aggiornaNumeroImballo();
                // fix 13073 <

             rigaNew.setIdConfigurazione(confa.getIdConfigurazione());
             rigaNew.setTestata(this.getTestata());
             rigaNew.setConfiguratore(null);

             err = creaRigaOrdineConfigurata(rigaNew);
             if (err == null) {
               err = aggiornaRigaOrdineConfigurata(rigaNew);
               if (err == null) {
                 ( (DocEvaAcq)this.getTestata()).setAbilitaCommitRiga(false);
                 this.setSalvaTestata(false);
                 // fix 11659 >
                 List righeSec = rigaNew.getRigheSecondarie();
                 Iterator iterS = righeSec.iterator();
                 int i = 0;
                 while (iterS.hasNext()) {
                   DocEvaAcqRigaSec secTmp = (DocEvaAcqRigaSec)iterS.next();
                   secTmp.setRigaPrimaria(rigaNew);
                   secTmp.setTestata(rigaNew.getTestata());
                   String key = KeyHelper.buildObjectKey(new String[] {rigaNew.getKey(), String.valueOf(++i)});
                   secTmp.setKey(key);
                   secTmp.setOnDB(false);
                 }
                 // fix 11659 <
                     // fix 13073 >
                     if (rigaNew != this) {
                   int ritR = rigaNew.save();
                   ( (DocEvaAcq)this.getTestata()).setAbilitaCommitRiga(true);
                   this.setSalvaTestata(true);
                   if (ritR<0){
                     err = new ErrorMessage("THIP300274", new String[]{rifRigaOrd, "problema salvataggio riga ordine in assegnazione qta residua o configurazione: " + ritR});
                     rit = ritR;
                   }
                   else {
                     rit = rit + ritR;
                   }
                     }
                         // fix 13073 <
               }
             }
           }
         }
         if (err == null) {
           if (isAssegnaResiduo || isRiassegnaCfg || isAggiornaQtaOrd) {
             boolean isOk = rigaOrd.retrieve();
             if (isAssegnaResiduo || isAggiornaQtaOrd) {
               rigaOrd.setQtaInUMRif(qtaOrdAggRif);
               rigaOrd.setQtaInUMPrmMag(qtaOrdAggPrm);
               rigaOrd.setQtaInUMSecMag(qtaOrdAggSec);
               rigaOrd.getQuantitaOrdinata().setQuantitaInUMRif(qtaOrdAggRif);
               rigaOrd.getQuantitaOrdinata().setQuantitaInUMPrm(qtaOrdAggPrm);
               rigaOrd.getQuantitaOrdinata().setQuantitaInUMSec(qtaOrdAggSec);
                   rigaOrd.aggiornaNumeroImballo(); // fix 13073
               // fix 11822 >
               rigaOrd.setPropostaSaldoManuale(this.isRigaSaldata());
               if (isConfAttNeutra && this.isRigaSaldata() && !rigaCorrenteEstratta ) {
                 rigaOrd.setPropostaSaldoManuale(false);
                 rigaOrd.setSaldoManuale(true);
               }
               // fix 11822 <
             }
             if (isRiassegnaCfg) {
               rigaOrd.setIdConfigurazione(this.getIdConfigurazione());
             }
             rigaOrd.setSalvaTestata(false);
             rigaOrd.setApplicaMovimentiSuiSaldi(false);
             rigaOrd.setProvenienzaPrezzo(TipoRigaRicerca.MANUALE); // fix 11659
             if (isOk) {
                     // fix 11822 >
               if (!rigaCorrenteEstratta) {
                 rigaOrd.setApplicaMovimentiSuiSaldi(true);
               }
                     // fix 11822 <
               int ritO = rigaOrd.save();
               rigaOrd.setApplicaMovimentiSuiSaldi(true);
               if (ritO<0) {
                 err = new ErrorMessage("THIP300274", new String[]{rifRigaOrd, "problema salvataggio riga ordine in assegnazione qta residua o configurazione: " + ritO});
                 rit = ritO;
               }
               else {
                 isOk = rigaOrd.getTestata().retrieve();
                 if (!isOk) {
                   err = new ErrorMessage("THIP300274", new String[]{rifRigaOrd, "problema retrieve testata su salvataggio riga ordine in assegnazione qta residua o configurazione"});
                   rit = ErrorCodes.GENERIC_ERROR;
                 }
                 else {
                   ((OrdineAcquisto)rigaOrd.getTestata()).calcolaCostiValoriOrdine(); // fix 11659
                   if (!rigaCorrenteEstratta) {
                     QuantitaInUMRif residuo = this.getDocEvaAcqRiga().getQtaResidua(rigaOrd);
                     this.setQtaDaSpedireInUMRif(residuo.getQuantitaInUMRif());
                     this.setQtaDaSpedireInUMPrm(residuo.getQuantitaInUMPrm());
                     this.setQtaDaSpedireInUMSec(residuo.getQuantitaInUMSec());
                     this.aggiornaQtaEvasione();
                            // fix 13073 >
                            this.setQtaInUMAcq(this.getQtaDaSpedireInUMRif());
                            this.setQtaInUMPrm(this.getQtaDaSpedireInUMPrm());
                            this.setQtaInUMSec(this.getQtaDaSpedireInUMSec());
                            this.aggiornaNumeroImballo();
                            // fix 13073 <
                   }
                 }
               }
             }
           }
         }
       }
     }
     catch (Throwable t) {
       t.printStackTrace(Trace.excStream);
       if (t instanceof ThipException) {
         err = ((ThipException)t).getErrorMessage();
       }
       else {
         err = new ErrorMessage("THIP300274", new String[]{rifRigaOrd, "problema generico in assegnazione configurazioni: " + t.getMessage()});
       }
     }
     if (!isQtaTotalmenteAssegnata && analizzaConfigurazioni && err == null) { // fix 11840
       this.getDocEvaAcqRiga().setRigaEstratta(rigaCorrenteEstratta);
       // fix 11822 >
       boolean nonVisibile = !rigaCorrenteEstratta && this.isRigaSaldata();
       this.setNonVisibile(nonVisibile);
       // fix 11822 <
     }
     err = this.controllaConfNeutra(); // fix 11840
     return err;
   }

   public DocEvaAcqConfigurazione confStessaConfigurazione(List configurazioni){
     DocEvaAcqConfigurazione confa = null;
     Integer confAtt = this.getIdConfigurazione();
     boolean isOk = false;
     Iterator iter = configurazioni.iterator();
     while (iter.hasNext()) {
       confa = (DocEvaAcqConfigurazione) iter.next();
       if (confAtt.compareTo(confa.getIdConfigurazione()) == 0) {
         iter.remove();
         isOk = true;
         break;
       }
     }
     if (!isOk) {
       confa = null;
     }
     return confa;
   }

   protected ErrorMessage aggiornaRigaOrdineConfigurata(DocEvaAcqRigaPrm rigaDoc) {
     String rifRigaOrd = this.getDocEvaAcqRiga().getRifRigaOrdineFormattato();
     ErrorMessage err = null;
     try {
       OrdineAcquistoRigaPrm rigaOrd = (OrdineAcquistoRigaPrm)rigaDoc.getRigaOrdine();
       rigaOrd.setQtaInUMRif(rigaDoc.getQtaInUMAcq());
       rigaOrd.setQtaInUMPrmMag(rigaDoc.getQtaInUMPrm());
       rigaOrd.setQtaInUMSecMag(rigaDoc.getQtaInUMSec());
       // fix 13073 >
       rigaOrd.aggiornaNumeroImballo();
       boolean usaRigaCurr = rigaDoc == this;
       if (!usaRigaCurr) {
          int res = rigaOrd.save();
          if (res < 0) {
          err = new ErrorMessage("THIP300274", new String[]{rifRigaOrd, "problema salvataggio nuova riga ordine in assegnazione qta residua o configurazione: " + res});
          }
       }
       // fix 13073 <
     }
     catch(Throwable t){
       t.printStackTrace(Trace.excStream);
       err = new ErrorMessage("THIP300274", new String[]{rifRigaOrd, "problema salvataggio nuova riga ordine in assegnazione qta residua o configurazione"});
     }
     return err;
   }

  protected DocEvaAcqRigaPrm creaRigaDocumentoConfigurata() throws ThipException {
     String rifRigaOrd = this.getDocEvaAcqRiga().getRifRigaOrdineFormattato();
     DocEvaAcqRigaPrm rigaNew = null;
     try {
       rigaNew = (DocEvaAcqRigaPrm)Factory.createObject(DocEvaAcqRigaPrm.class);
       rigaNew.setEqual(this);
       // fix 11659 >
       List righeSec = rigaNew.getRigheSecondarie();
       Iterator iter = righeSec.iterator();
       while (iter.hasNext()) {
         DocEvaAcqRigaSec secTmp = (DocEvaAcqRigaSec)iter.next();
         secTmp.setKey(rigaNew.getKey());
         secTmp.setTestata(rigaNew.getTestata());
       }
       rigaNew.setProvenienzaPrezzo(TipoRigaRicerca.MANUALE);
       // fix 11659 <
       rigaNew.getDocEvaAcqRiga().setRigaEstratta(true);
       int numRiga = this.getNumeroRigaDocumento().intValue() - ((DocEvaAcq)this.getTestata()).getNumeroNuovaRiga();
       rigaNew.setNumeroRigaDocumento(new Integer(numRiga));
     }
     catch(CopyException ex){
       ex.printStackTrace(Trace.excStream);
       ErrorMessage err = new ErrorMessage("THIP300274", new String[]{rifRigaOrd, "problema creazione nuova riga documento evasione configurata"});
       ThipException e = new ThipException();
       e.setErrorMessage(err);
     }
     return rigaNew;
   }

   protected ErrorMessage creaRigaOrdineConfigurata(DocEvaAcqRigaPrm rigaDoc) {
     ErrorMessage err = null;
     String rifRigaOrd = this.getDocEvaAcqRiga().getRifRigaOrdineFormattato();
     try {
       OrdineAcquistoRigaPrm rigaOrdCurr = (OrdineAcquistoRigaPrm)rigaDoc.getRigaOrdine();
       // fix 13073 >
       boolean usaRigaCurr = rigaDoc == this;
       OrdineAcquistoRigaPrm rigaOrd = null;
       if (usaRigaCurr) {
          rigaOrd = rigaOrdCurr;
       }
       else {
          rigaOrd = (OrdineAcquistoRigaPrm)Factory.createObject(OrdineAcquistoRigaPrm.class);
         rigaOrd.setEqual(rigaOrdCurr);
         rigaOrd.getRigheLotto().clear();
         //rigaOrd.getRigheSecondarie().clear(); // fix 11659
         rigaOrd.setGeneraRigheSecondarie(false); // fix 11840
         rigaOrd.setOnDB(false);
         // fix 11659 >
         rigaOrd.setNumeroRigaDocumento(null);
          List righeSec = rigaOrd.getRigheSecondarie();
          Iterator iterS = righeSec.iterator();
          int i = 0;
          while (iterS.hasNext()) {
           OrdineAcquistoRigaSec secTmp = (OrdineAcquistoRigaSec)iterS.next();
           secTmp.setRigaPrimaria(rigaOrd);
           secTmp.setTestata(rigaOrd.getTestata());
           String key = KeyHelper.buildObjectKey(new String[] {rigaOrd.getKey(), String.valueOf(++i)});
           secTmp.setKey(key);
           secTmp.setOnDB(false);
           secTmp.setAbilitaCopiaCommenti(true);
           secTmp.copiaCommenti();
          }
          rigaOrd.setProvenienzaPrezzo(TipoRigaRicerca.MANUALE);
         // fix 11659 <
         rigaOrd.setAbilitaCopiaCommenti(true);
         rigaOrd.copiaCommenti();
          //rigaOrd.setIdConfigurazione(rigaDoc.getIdConfigurazione());
          rigaOrd.setTestata(this.getRigaOrdine().getTestata());
       }
       rigaOrd.setIdConfigurazione(rigaDoc.getIdConfigurazione());
       // fix 13073 <

       BigDecimal qtaRif = rigaDoc.getServizioQta().getQuantitaInUMRif();
       BigDecimal qtaPrm = rigaDoc.getServizioQta().getQuantitaInUMPrm();
       BigDecimal qtaSec = rigaDoc.getServizioQta().getQuantitaInUMSec();

       rigaOrd.setQtaInUMRif(qtaRif);
       rigaOrd.setQtaInUMPrmMag(qtaPrm);
       rigaOrd.setQtaInUMSecMag(qtaSec);
       // fix 13073 >
       rigaOrd.aggiornaNumeroImballo();
       // fix 13073 <

       rigaOrd.setSequenzaRiga(rigaOrd.getSequenzaRiga() + 1);
       rigaOrd.setSalvaTestata(false);
       int res = rigaOrd.save();
       if (res >= 0) {
          // fix 13073 >
          boolean isOk = true;
          String msg = "riga";
         if (usaRigaCurr) {
          isOk = rigaOrd.retrieve();
          if (isOk) {
             isOk = rigaOrd.getTestata().retrieve();
             msg = "testata";
          }
         }
         if (isOk) {
             rigaDoc.setRRigaOrd(rigaOrd.getNumeroRigaDocumento());
             ((OrdineAcquisto)rigaOrd.getTestata()).calcolaCostiValoriOrdine();
            rigaOrd.setSalvaTestata(true);
         }
         else {
          err = new ErrorMessage("THIP300274", new String[]{rifRigaOrd, "problema aggiornamento " + msg + " ordine evasione configurata: " + res});
         }
         // fix 13073 <
       }
       else {
       err = new ErrorMessage("THIP300274", new String[]{rifRigaOrd, "problema creazione nuova riga ordine evasione configurata: " + res});
       }
     }
     catch(Throwable t){
       t.printStackTrace(Trace.excStream);
       err = new ErrorMessage("THIP300274", new String[]{rifRigaOrd, "problema creazione nuova riga ordine evasione configurata: " + t.getMessage()});
     }

     return err;
   }

   public boolean isAbilitataCreazioneRigheConfigurate() {
     boolean isOk = this.getArticolo()!=null && this.getArticolo().hasVersioneEstesa() && this.getArticolo().getSchemaCfg()!=null;
     isOk &= !this.getDocEvaAcqRiga().isDocumentoSpedizioneComponenti(); // fix 11822
     return isOk;
   }
   // fix 11614 <

  // fix 11822 >
  public void setNonVisibile(boolean isNonVisibile) {
   this.isNonVisibile = isNonVisibile;
  }

  public boolean isNonVisibile() {
    return isNonVisibile;
   }
  // fix 11822 <

  // fix 11840 >
  // fix 12062 >
   // fix 12384 >
   public ErrorMessage controllaConfNeutra() {
      ErrorMessage em = null;
      if (!isAbilitataCreazioneRigheConfigurate()) {
         em = super.controllaConfNeutra();
      }
      else if (!hasRigheconfigurate()) {
         em = super.controllaConfNeutra();
      }
      return em;
   }

   public boolean hasRigheconfigurate() {
      boolean isOk = false;
      if (this.getConfiguratore() != null) {
         isOk = !this.getConfiguratore().getDocEvaAcqConfig().isEmpty();
      }
      return isOk;
   }
   // fix 12384 <
  // fix 12062 <
  // fix 11840 <

  // fix 11473 >
  private boolean isSelezionabile = true;
  private boolean isForzabile = true;
  private List iLivelloControlloDisp = new ArrayList(2);

  public List getLivelloControlloDisp() {
     return this.iLivelloControlloDisp;
  }

  public void setSelezionabile(boolean isOk) {
    isSelezionabile = isOk;
  }

  public boolean isSelezionabile() {
    return isSelezionabile;
  }

  public void setForzabile(boolean isOk) {
    isForzabile = isOk;
  }

  public boolean isForzabile() {
    return isForzabile;
  }

  protected boolean verificaForzabilitaErrorMessage(ErrorMessage errMsg, String taskId) {
    boolean isOk = false;
    if (errMsg != null && taskId != null) {
      try {
        Entity entity = Entity.findEntity(this);
        if (entity != null) {
          isOk = true;
          List errors = new ArrayList();
          errors.add(errMsg);
          entity.extractForceableErrors(errors, taskId);
       }
     }
     catch (Throwable t) {
        t.printStackTrace(Trace.excStream);
      }
    }
    return isOk;
  }

  protected void abilitaRigaForzabile(char livelloDisp) {
    this.setSelezionabile(true);
    this.setForzabile(true);
    //if (livelloDisp != LivelliControlloDisponibilita.VERDE && livelloDisp != LivelliControlloDisponibilita.NA) { // fix 12181 // fix 13130//Fix23523
    if (livelloDisp == LivelliControlloDisponibilita.ROSSO) {//Fix23523
      if (this.getMagazzino().getTpControlloGiacRil() ==
          PersDatiMagazzino.TP_CTL_GIAC_DISP_ERRORE) {
        String[] parametri = new String[] {};
        ErrorMessage em = new ErrorMessage(CalcoloGiacenzaDisponibilita.
                                           ERR_FORZABILE_GIA, parametri);
        boolean isOk = this.verificaForzabilitaErrorMessage(em, "NEW");
        if (isOk) {
          if (em.getForceable()) {
            this.setSelezionabile(true);
            this.setForzabile(true);
          }
          else {
            this.setSelezionabile(false);
            this.setForzabile(false);
          }
        }
      }
    }
  }

  public ErrorMessage ckeckControlloDisp(List errors) {
    return ckeckControlloDisp(true, errors);
  }

  protected ErrorMessage riassegnaErroreGiaDisp(List errors) {
    ErrorMessage errMsg = null;
    if (errors != null) {
      Iterator i = errors.iterator();
      while (i.hasNext()) {
        ErrorMessage e = (ErrorMessage)i.next();
        if (e.getId().equals(CalcoloGiacenzaDisponibilita.ERR_FORZABILE_GIA)) {
          i.remove();
          //Fix 21468 inizio
          /*boolean isOk = !this.isRigaForzata() && verificaForzabilitaErrorMessage(e, "NEW");
          if (isOk) {
            String[] ps = new String[] {
                this.getRifRigaOrdineFormattato() + ": " + e.getLongText()};
            errMsg = new ErrorMessage("THIP300219", ps);
          }*/
          boolean isOk = verificaForzabilitaErrorMessage(e, "NEW");
          if (!this.isRigaForzata())
          {
            if (e.getForceable()) {
              String[] ps = new String[] {this.getRifRigaOrdineFormattato() + ": " + e.getLongText()};
              errMsg = new ErrorMessage("THIP300219", ps);
            }
            else errMsg = e;
          }
          else
          {
            this.riassegnaQtaRigheSec(true);
            if (!e.getForceable()) errMsg = e;
          }
          // fix 13130 >
          /*else {
            this.riassegnaQtaRigheSec(true);
          }*/
          // fix 13130 <
          //Fix 21468 fine
        }
      }
    }
    return errMsg;
  }

  public List checkControlloGiacDisp() {
    List errors = null;
    char tipoControllo = ((DocEvaAcq)this.getTestata()).getTipoAnalisiGiaDisp();
    boolean eseguiControllo = tipoControllo == TipiControlloDisponibilita.GIACENZA || tipoControllo == TipiControlloDisponibilita.DISPONIBILITA;
    if (eseguiControllo) {
      //Fix 23955 inizio
      if(getTestata() != null && getRigaOrdine() != null && getRigaOrdine().getTestata() != null)
        ((DocEvaAcq)this.getTestata()).setIdMagazzinoTra(((OrdineAcquisto) ((DocEvaAcqRigaPrm) this).getRigaOrdine().getTestata()).getIdMagazzinoTra());
      //Fix 23955 fine
      errors = super.checkControlloGiacDisp();
    }
    return errors;
  }

  // fix 11473 <

  // 12181 >
  public boolean effettuareIlControllo(List lista){
    boolean isOk = false;
    //if (!this.isRigaForzata())  {//Fix 21468
      isOk = super.effettuareIlControllo(lista);
    //}//Fix 21468
    return isOk;
  }

  public boolean isDaAggiornare() {
    return true;
  }
  // 12181 <

  //Fix 13057 Inizio

  public ErrorMessage checkQtaDaSpedireInUMPrm() {

    if (((DocEvaAcq)getTestata()).isDisabilitaCheckInEstrazioneRighe()) return null; //Fix 16860

    if (Articolo.isAQuantitaIntera(getUMPrm(), getArticolo()) && !isQtaUMPrmInt()) {
      return new ErrorMessage("THIP40T005");
    }
    return null;
  }

  public boolean isQtaUMPrmInt() {
    float rest = getQtaDaSpedireInUMPrm().floatValue() - getQtaDaSpedireInUMPrm().intValue();
    if (new Float(rest).compareTo(new Float(0)) != 0)
      return false;
    return true;
  }

  //Fix 13057 Fine

  // fix 13130 >
  public void riassegnaQtaRigheSec(boolean forzaCtrlSaldoCL) {
   Iterator iter = this.getRigheSecondarie().iterator();
   while (iter.hasNext()) {
      DocEvaAcqRigaSec rigaSec = (DocEvaAcqRigaSec)iter.next();
      rigaSec.ricalcoloQuantita(this, this.getServizioQta(), forzaCtrlSaldoCL);
   }
  }
  // fix 13130 <
  // Fix 22545 inizio
//Fix 22545 inzio
 public ErrorMessage checkArticolStatoTecnico() {
   return null;
 }
  // Fix 22545 fine

 //Fix 34783 inizio
 public ErrorMessage checkAltriErroriForzabile(List errors){
	     ErrorMessage errMsg = null;
	     if(getTipoRiga()== TipoRiga.SPESE_MOV_VALORE) return null;
	     if (errors != null) {
	       Iterator i = errors.iterator();
	       while (i.hasNext()) {
	         ErrorMessage e = (ErrorMessage)i.next();
	         if (getIdErroriForceable().contains(e.getId())) {
	           i.remove();
	           boolean isOk = verificaForzabilitaErrorMessage(e, "NEW");
	           if (!this.isRigaForzata())
	           {
	        	   if (e.getForceable()) {
	        		   String[] ps = new String[] {
	        				   this.getRifRigaOrdineFormattato() + ": " + e.getLongText()};
	        		   errMsg = new ErrorMessage("THIP300219", ps);
	        	   }
	        	   else errMsg = e;
	           }else if (!e.getForceable()) errMsg = e;
	         }
	       }
	     }
	     return errMsg; 
} 
//Fix 34783 fine 

 
   //Fix 35433 inizio
	public void resetVersioneSal()
	{
	}
	//Fix 35433 fine

	// 36157 inizio
	public boolean isCambioMagazzinoAbilitato() {
		boolean cambioMagAbilitato = true;

		PersDatiAcq pdv = PersDatiAcq.getCurrentPersDatiAcq();
		if (!pdv.isCambioMagEvasione())
			cambioMagAbilitato = false;

		else if (isOnDB() && !((DocEvaAcq) getTestata()).isAccodamento())
			cambioMagAbilitato = false;

		return cambioMagAbilitato;
	}
	// 36157 fine
 //Fix 36394 inizio
 public boolean abilitaSaldoAZeroEvasioneOrdini() {
	 DocEvaAcq docEvaAcq = (DocEvaAcq)getTestata();
	 String saldoAZeroEvasioneOrdini = ParametroPsn.getValoreParametroPsn("std.acquisti", "SaldoAZeroEvasioneOrdini");
	 if(saldoAZeroEvasioneOrdini != null && saldoAZeroEvasioneOrdini.equals("Y") && ( docEvaAcq.getCausale().getTipoDocumento() == TipoDocumentoAcq.ACQUISTO 
			 ||  docEvaAcq.getCausale().getTipoDocumento() == TipoDocumentoAcq.ENTRATA_GENERICA ))
		 return true;
	 
	 return false;
 }
 public int saveRigaOrdine(OrdineAcquistoRiga rigaOrd) throws SQLException {
   int res = ErrorCodes.OK;
   if (rigaOrd != null) {
     try {
       // viene settato questo flag in quanto se attiva la gestione intercompany
       // deve partire in questa fase di salvataggio della riga ordine
       // in quanto sono in una fase di evasione della stessa
       // le modifiche che questa operazione potrebbe comportare non
       // sono determinanti al fine dell'intercompany
       rigaOrd.setForzaDisattivaGestioneIntercompany(true);

       // per evitare il lok ottimistico sulla testata
       // d'altra parte mi sembra superfluo salvare la testata.
       boolean aggiornaRigaOff = rigaOrd.isAggiornaRigaOfferta();
       rigaOrd.setAggiornaRigaOfferta(false);
       res = rigaOrd.save();
       rigaOrd.setAggiornaRigaOfferta(aggiornaRigaOff);
       if (rigaOrd.iMovimPortafoglioEvaDaCancellare != null){
         iGestoreSaldi.applicaMovimentiPortafoglio(rigaOrd.iMovimPortafoglioEvaDaCancellare);
       }
       GestoreEvasioneVendita.get().println(
           "tento la save() di riga ordine prm :'" +
           rigaOrd.getKey() + "'");

     }
     catch (Throwable t) {
       t.printStackTrace(Trace.excStream);
       res = -100;
     }
     finally
     {
       if (rigaOrd != null)
         rigaOrd.setForzaDisattivaGestioneIntercompany(false);
     }
   }
   return res;
 }
//Fix 36394 Fine
}
