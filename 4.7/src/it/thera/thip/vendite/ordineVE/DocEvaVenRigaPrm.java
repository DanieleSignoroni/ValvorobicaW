package it.thera.thip.vendite.ordineVE;

import java.math.*;
import java.sql.*;
import java.util.*;

import com.thera.thermfw.base.*;
import com.thera.thermfw.cbs.*;
import com.thera.thermfw.common.*;
import com.thera.thermfw.persist.*;

import it.thera.thip.base.articolo.*;
import it.thera.thip.base.azienda.Magazzino;
import it.thera.thip.base.comuniVenAcq.*;
import it.thera.thip.base.documenti.*;
import it.thera.thip.base.documenti.TipoGestione;
import it.thera.thip.base.generale.ParametroPsn;
import it.thera.thip.base.generale.UnitaMisura;
import it.thera.thip.cs.*;
import it.thera.thip.datiTecnici.configuratore.*;
import it.thera.thip.magazzino.generalemag.*;
import it.thera.thip.magazzino.matricole.LottoMatricola;
import it.thera.thip.magazzino.matricole.StoricoMatricola;
import it.thera.thip.magazzino.saldi.GestoreSaldi;
import it.thera.thip.vendite.contrattiVE.ContrattoVendita;
import it.thera.thip.vendite.documentoVE.*;
import it.thera.thip.vendite.generaleVE.*;
import it.thera.thip.vendite.proposteEvasione.LspFabbisognoTM;
import it.valvorobica.thip.vendite.documentoVE.YDocumentoVenRigaPrm;
import it.thera.thip.atp.PersDatiATP;

// fix 11120 >
import com.thera.thermfw.security.Entity;
import com.thera.thermfw.type.DecimalType;
// fix 11120 <


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
 * 13/08/2003    GSCARTA    Attivato il controllo per il raggruppamento ordini in bolla, attivata la gestione del controllo delle causali
 * 04/09/2003    GSCARTA    Controllato il isCollegataAMagazzino sia in save sia in delete
 * 24/09/2003    GSCARTA    Aggiornati i metodi di check gruppo e getLivelloControlloDisp(boolean addWarnings)
 * 03/10/2002    GSCARTA    Aggiornamento calcolo e controllo disponibilità
 * 27/10/2003    DB         FIX 935. Implementato vuoto il metodo creaRigaOmaggio();
 * 23/02/2004    DB         Fix 1502
 * 18/03/2004    DB         Fix 1674
 * 25/03/2004    GScarta    Fix 1684
 * 02/04/2004    GScarta    Fix 1699 : introdotto controllo sulla riga modificata
 * 15/04/2004    GScarta    Fix 1842 : Non venivano salvate righe sec in evasione ordini per modifiche al comportamento sul documento.
 * 30/04/2004    GScarta    Fix 1922
 * 07/05/2004    DB         Fix 1932
 * 19/05/2004    GSCARTA    Fix 2006
 * 24/05/2004    GSCARTA    Fix 2028
 * 25/06/2004    DB         fix 2161
 * 07/07/2004    GScarta    Fix 2218
 * 09/09/2004    DB         Fix 2402
 * 10/12/2004    GSCARTA    Fix 2322
 * 10/12/2004    ME         Fix 2844
 * 13/01/2005    FCrosa     Fix 3132: aggiunta variabile daEvasione
 * 18/01/2005    GScarta    Fix 3134: corretta 3132
 * 20/01/2005    ME         Fix 3177: modificato metodo aggiornaRiga
 * 14/01/2005    LP         Fix 3187: Aggiunto controllo generazione automatica lotti
 * 03/02/2005    GScarta    Fix 3242
 * 16/02/2005    ME         Fix 3274: ridefinito vuoto il metodo
 *                          ricalcoloQuantitaRigaInternal
 * 17/02/2005    MN         Fix 3266: ridefinito il metodo getIdArtIntestatarioForGUI
 * 24/02/2005    MN         Fix 03307 : Commentato il metodo getIdArtIntestatarioForGUI
 * 24/02/2005    FCrosa     Fix 3309: modifica nel metodo checkRigaModificata()
 * 28/02/2005    PM         Fix 3315
 * 14/03/2005    BP         Fix 3383 le righe ordine assumono il tipoBlocco della testata
 * 06/04/2005    PM         Fix 3549:La creazione da evasione di un documento in stato provvisorio
 *                          non fuziona se l'articolo della riga ordine è gestito a lotti,
 *                          sull'articolo è abilitata la proposta di prelievo dei lotti  e
 *                          non esistono lotti per quell'articolo
 * 19/05/2005    PJ         fix 3741
 * 17/05/2005    MN         Fix 3785: Modificato il metodo save().
 * 24/05/2005    PJ         fix 3820: aggiunti commenti nel metodo aggiornaRiga
 * 15/06/2005    MN         Fix 3921
 * 23/06/2005    MN         Fix 4004
 * 08/07/2005    PJ/MN      Fix 4090 corretta gestione quantità di magazzino in assenza di ricalcolo
 * 22/04/2005    BP         Fix 3331 correzione loop metodo isCollegataAMagazzino();
 * 16/09/2005    EP         Fix 04324 Gestione saldo automatico
 * Number  Date        Author  Description
 * 04669   22/11/2005  DZ      Aggiunto checkRigheSecondarie (ridefinito).
 * 04670   02/12/2005  MN        Gestione Unità Misura con flag Quantità intera.
 * 04768   13/12/2005  ME      Ridefinito metodo calcolaPrezzoDaRigheSecondarie()
 *                             vuoto per evitare che si perda alcuni dati (sconti,
 *                             maggiorazione) settati nelle righe dell'ordine
 *                             con articoli di tipoParte 'kit' e tipoCalcoloPrezzo
 *                             'da componenti'
 * 04718   19/12/2005  EP      Modificato ritorno errore nella finally della save()
 * 04825   27/12/2005  ME      Sistemato problema descrizione articolo con articolo-cliente
 * 07/10/2005    GScarta    Fix 4445
 * 05117   14/03/2006  GN      Correzione nella gestione delle unità di misura con flag Quantità intera
 * 28/03/2006    DB         Fix 4996
 * 05238   31/03/2006  EP      Modifiche controllo fido
 * 05348   26/04/2006  ME      Ridefinizione del motodo checkArticoloEsclusione
 * 05395   18/05/2006  MN    Modificato il metodo nulla(), deve ritornare true solo se
 *                           la qta è nulla e non anche se è 0.
 * 11/04/2006    DB         Fix 5303
 * 18/04/2006    DB         Fix 5331: Non sembrerebbe data la modifica, ma il problema è stato scoperto
 *                          perchè non si riusciva a fare il saldo automatico di una riga saldata a quantità zero.
 * 18/04/2006    DB         Fix 5277
 * 05566   15/06/2006  MN   Ridefiniti i metodi sistemaQuadraturaLotti(), getUnicoLottoEffettivo().
 *                          Viene aggiornata la qtaDaSpedire con il delta della qta.
 * 25/07/2006    DB         Fix 5500: Della serie predicare bene e razzolare male!
 * 05798   03/08/2006  MN   Modifica varie sul controllo della qta lotti nel caso in cui
 *                          sia attiva l'identificazione automatica del lotto.
 * 05855   06/09/2006  MN   In fase di evasione vengono applicati i movimenti contenuti nella
 *                          lista iMovimPortafoglioEvaDaCancellare. Questi movimenti vengono generati
 *                          dalla delete della RigaLottoOrdine.
 * 06034   11/10/2006  DB   Riportata modifica persa della 5500.
 * 06110   26/10/2006  ME   Aggiunto codice per proposizione automatica
 *                          delle movimentazioni di storico matricole
 *                          presenti nell'ordine
 * 06253   16/11/2006  ME   Modificata inizializzazione dello stato
 *                          avanzamento in fase di creazione della riga del
 *                          documento
 * 06294   23/11/2006  ME   Aggiunto metodo getCommentiParteIntestatario
 * 06583   24/01/2007  ME   Aggiunto controllo dichiarazione matricole in
 *                          creazione delle righe del documento
 * 06473   19/01/2007  MN   Se la causale del documento indica che il prezzo è obbligatorio, in fase
 *                          di estrazione delle righe ordine , devono essere segnalate come righe nopn estratte
 *                          quelle che hanno un prezzo zero o null.
 * 06716   14/02/2007  MN   Creazione automatica dei contenitori nel caso di ordine aperto.
 * 06663   09/02/2007  DB
 * 06920   19/03/2007  MN   Modificato il tipo di rritorno del metodo getUnicoLottoEffettivo.
 * 06944   19/03/2007  MN   Al salvataggio del documento il sistema verifica se sussistono le seguenti
 *                          condizioni:
 *                          1- Non è attiva l'identificazione automatica del lotto
 *                          2- Esiste un solo lotto effettivo
 *                          3- Tipo documento = Vendita
 *                          4- Non c'e' giacenza per il lotto in questione.
 *                          5- La riga documento si trova in stato DEFINITIVO
 *                          Se si verificano le suddette condizioni lo stato av.
 *                          della riga documento deve essere impostata a provvisorio.
 * 06965  21/03/2007   MN   Modificate le chiamate ai metodi di calcolo
 *                          giacenza/disponibilità su ProposizioneAutLotto.
 * 07220  02/05/2007   DBot Introdotta gestione blocchi per accantonato e prenotato
 * 07727  10/08/2007   GScarta
 * 08175  05/11/2007   PM   L'evasione ordini va in loop se la causale riga doc.
 *                          è di tipo "merce a valore" con azione a magazzino "uscita"
 *                          ed ha associata una causale di movimento di magazzione
 *                          che non prevede azione sulla giacenza.
 * 08805  12/03/2008   LP   Aggiunto hook per personalizzazioni
 * 08929  14/04/2008   MM   Modificato il metodo creaRiga per copiare solamente le righe valide
 * 09181  08/05/2008   DBot Allentati controlli di accPrn quando causale non mov. magazzino
 * 09251  19/05/2008   DBot Resa possibile evasione provvisoria senza copertura con controlllo acc/prn = ERRORE
 * 09450   23/06/2008  LP   Aggiunto hook per personalizzazioni
 * 09671  25/08/2008   PM   Se una riga sec ha lo stesso articolo della riga prm e
 *                          il coefficente è 1 allora la sua quantita deve
 *                          essere uguale a quella della riga prm.
 * 09745  10/09/2008   PM	   Fix completamento della fix 9671: usato metodo  ricalcoloQuantita(DocumentoOrdineRiga rigaPrm, QuantitaInUMRif qtaRigaPrimaria)
 * 09612  08/10/2008   GScarta Introdotto metodo che blocca in evasione ordini vendita l'eventuale eliminazione della
 *                             riga documento lotto DUMMY se presente in riga ordine lotto.
 * 10081  17/11/2008   DB   Le descrizioni FEDERICO
 * 09867  17/11/2008   DB   Descrizione con PAOLA
 * 10556  16/03/2009   DB   Risolto il problema seconda evasione di documento con righe non referenziate da una riga ordine
 * 10955   17/06/2009  Gscarta   modificate chiamate a convertiUM dell'articolo per passare la versione
 * 11120  24/07/2009   GScarta   Introdotta nuova gestione del controllo giacenza con errori forzabili.
 *                               Modificata la signature del metodo ckeckControlloDisp()
 * 11213  30/07/2009   GScarta   Completamento fix 11120
 * 11084  03/09/2009   PM        Gestione picking e packing
 * 11402  28/09/2009   DB
 * 11779  14/12/2009   GScarta   Corretta 10955 e migliorata gestione NumeroImballo
 * 11951  15/01/2010   GScarta   Riporto in 3.0.8 della fix 11779 con correzione al metodo 'checkRigaModificata()'
 *                               per controllare la presenza della riga ordine
 * 13001  27/07/2010   PM        Nelle proposte di evasione il controllo disponibiltà non deve essere fatto.
 * 13342  06/10/2010  PM     In evasione di una riga sec gestita a lotti unitari se sull'ordine è indicato un lotto sul documento la quantità totale della
 *                           riga viene caricata tutta sul lotto
 * 13057   08/09/2010  ElOuni   Introdotti controlli per qtaDaSpedireInUMPrm
 *                     Ichrak
 * 13495   18/11/2010  AYM      Elimia controllo "checkRigaModificata()" nel caso RigaOrdine il valore è null
 * 13893  24/01/2011   PM       Chiuso il rubinetto verso l'intercompany.
 * 14253   04/04/2011  PM      Eliminato problema di ClassCastException
 * 14133   24/03/2011  AYM      corretto l'aggiornamento di riga lotto dummy in caso di " Stato avanzamento" PROVVISORIO
 * 14336  25/05/2011   HBT     Agguinto un nuovo messaggio d'errore
 * 12825  18/06/2010   GScarta corretto metodo verificaForzabilitaErrorMessage
 * 14738  29/06/2011   DBot    Integrazione a standard fix 12825
 * 17245  14/12/2012   PM      In evasione ordini non deve essere fatto il controllo giacenza sulle righe merce a valore.
 * 16586  14/01/2013   DBot    Eliminate le righe lotto da ordine in eliminazione righe di proposta evasione
 * 17485  21/02/2013   Linda   revocando la fix 12825
 * 17924  23/05/2013   Linda   Corretto il calcola della quantità del lotto del componente nelle righe documento.
 * 19215  10/02/2014   AYM     Gestione il flag "Quantità attesa entrata disponibilié" nella modello  di "Proposte evasione".
 * 19426  15/02/2014   AYM     Corretto il caso di Aggiornamento della quantità ordinata per articoli gestiti a lotti nel caso cancellaizone parziale delle proposte.
 * 20952  26/01/2015   MBH     corretto metodo riassegnaErroreGiaDisp
 * 21144  19/03/2015   AYM     Agguinto il Gestione Tipo bene .  
 * 22471  19/11/2015   AYM     Ripristinata il valore di  il falg di "proppsta saldo manuale" nlla rigenerazione della proposta
 * 22618  01/12/2015   OCH     Aggiunto il metodo checkDichiarazioneMatricole 
 * 23151  09/03/2016   AYM     il controllo di "checkRigaModificata()" non deve essere effettuato si l'evasione è da proposta .
 * 22545  01/04/2016   OCH     Ridefinizione checkArticolStatoTecnico 
 * 29288  10/05/2019   Jihene  Agguingere sistemaQuantitaDopoVariazioneStatoAv(StatoAvanzamento.PROVVISORIO).
 * 30567  23/01/2020   SZ	   Mettendo il valore del flag escluso come da causale durante l'evasione dell'ordine di vendita. 
 * 30871  06/03/2020   SZ	   6 Decimali.	
 * 31850  14/09/2020   MBH     Modificare il calcolo del livello controllo in caso KIT_NON_GEST, se esiste almeno una riga sec con BloccoRicalcoloQtaComp.
 * 32165  24/11/2020   MBH     Il semaforo non è corretto in caso di AccPre attivo e la riga non è frazionabile
 * 32807  22/01/2021   Jackal  Fix tecnica per personalizzazioni
 * 32769  18/01/2021   DB      Irrobustito il codice della classe DocEvaVenRigaPrm in fase di recupero se è una riga collegata a magazzino.
 * 33874  25/06/2021   SZ	   le righe secondarie dei kit non gestiti a magazzino non vengono propagate sul documento di vendita se generato da proposta d'evasione.
 * 34038  23/07/2021   SZ	   	Rimuovere il controllo di giacenza in caso di accprn attivo se la causale riga di vendita è una causale di reso (azione magazzino = entrata)
 * 33992  20/09/2021   SZ		Imposta il flag GestOriginePref nella articolo.
 * 34464  19/10/2021   YBA     Aggiungi un controllo su aggiornaRigaOff
 * 34827  13/12/2021   LTB     Aggiungere il metodo setQtaCambiata, serve nel change della qtà
 * 34821  17/12/2021   Jackal  Fix tecnica per personalizzazioni
 * 34783  03/12/2021   SZ	   In evasione vendite aggiungere ilcontrolllo forzabile sull'articolo escluso dalle vendite	
 * 35178  03/02/2022   SZ	   Se l' articolo secondario è sospeso o annullato il documento viene chiuso al posto di dare errore.
 * 35639  02/05/2022   LTB     Gestione assegnazione dei lotti (con proposizione automatica o manuale dei lotti) 
 * 							   che consideri quanto già assegnato nello stesso documento  		
 * 36048  17/06/2022   YBA     Imposta il la nazione nella articolo.
 * 36157  29/06/2022   MR      Se abilitato il cambio magazzino, abbiamo il bisogno di mettere il magazzino disabilitata nella griglia evasione
							   per ogni riga da sola.
 * 36762  13/10/2022   SZ	   Viene sostituti il messagi di worning inveci di aggiungere un nuovo.	 
 * 36869  26/10/2022   SZ	   Evitare il nullPointerException nel caso di magazino == null.
 * 38596  05/05/2023   SZ	   In caso di isInDettaglio la qta è già sistemato nel metodo setStatoAvanzamento appena chiamata
 * 38724  17/05/2023   SZ	   Mettendo il valore del cambo UtilizzaContoAnticipi come da causale durante l'evasione dell'ordine di vendita. 			  		
 * 38908  05/06/2023  LTB       In alcune condizioni di la proposizione atomatica dei lotti occupa in modo abnorme la memoria 
 * 39402   24/07/2023  SZ      Scale errato se il database ha le quantità a 6 decimali
 */

public class DocEvaVenRigaPrm
extends YDocumentoVenRigaPrm implements HasDocEvaVenRiga {

  public static final String RES_EVA_VEN = "it/thera/thip/vendite/ordineVE/resources/EvasioneOrdine"; //fix 7220
  //Fix 21144 inizio 
  public static String ERR_FORZABILE_TIPO_BENE = "THIP40T368";
  public static String ERR_FORZABILE_ART_ESCLUSIONE  = "THIP200379";//Fix 34783
  public static List iErroriForceable = new ArrayList();
  static {
	  iErroriForceable.add(ERR_FORZABILE_TIPO_BENE);
	  iErroriForceable.add(ERR_FORZABILE_ART_ESCLUSIONE);//Fix 34783
  }
  
  private boolean isAblDaAltroErroreForceable = true ;
  //Fix 21144 fine
  private boolean isAbilitaControlloDisp = false; //fix 2322

  //ini FIX 1699
  private boolean isAbilitaCheckAllCondizionata = true;
  //fine FIX 1699

  /**
   *
   */
  private DocEvaVenRiga iDocEvaVenRiga;

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

  // Inizio 5855
  protected GestoreSaldi  iGestoreSaldi = (GestoreSaldi)Factory.createObject(GestoreSaldi.class);
  // Fine 5855

   //fix 7220 inizio
   protected boolean iControlloAccPrn = false;
   protected boolean iAbilitatoCheckAccPrn = false;
   //fix 7220 fine

   // fix 11402
   protected QuantitaInUM iQuantitaGiaStorno;
   // fine fix 11402

  //Attributi
  //ini FIX 1842
  //protected OneToMany iRigheSecondarie;
  //fine FIX 1842

   protected BigDecimal iQuantitaLotti;//Fix 14336

   protected boolean iEliminataDaEvasioneProposta; //Fix 16586

  /**
   *
   */
  public DocEvaVenRigaPrm() {
    super.iTestata = new Proxy(DocEvaVen.class);
    this.setSpecializzazioneRiga(RIGA_PRIMARIA);
    super.iRigheLotto = new OneToMany(DocEvaVenRigaLottoPrm.class, this, 15, true);
    this.iRigheSecondarie = new OneToMany(it.thera.thip.vendite.ordineVE.
                                          DocEvaVenRigaSec.class, this, 15, true);
    // Fix 1674
    //this.iDocEvaVenRiga = new DocEvaVenRiga(this);
    this.iDocEvaVenRiga = (DocEvaVenRiga) Factory.createObject(DocEvaVenRiga.class);
    this.iDocEvaVenRiga.setRigaDoc(this);
    this.iDocEvaVenRiga.getRigaDoc().setRigaSaldata(DocEvaVenRiga.
        DEFAULT_RIGA_SALDATA);
    // Fine fix 1674
    this.setInDettaglio(false);

    // fix 11213 >
    this.getLivelloControlloDisp().add(0, new Character(LivelliControlloDisponibilita.NA));
    this.getLivelloControlloDisp().add(1, new BigDecimal(0.00));
    // fix 11213 <
  }

  //fix 2322
  public boolean isAbilitaControlloDisp() {
    return isAbilitaControlloDisp;
  }

  public void setAbilitaControlloDisp(boolean abilita) {
    isAbilitaControlloDisp = abilita;
  }

  //fine fix 2322
  //Fix 21144 inizio
  public boolean isAbilitaDaAltroErroreForceable() {
	 return isAblDaAltroErroreForceable;
  }

   public void setAbilitaDaAltroErroreForceable(boolean abilita) {
	   isAblDaAltroErroreForceable = abilita;
  } 
  //Fix 21144 fine
   
  public DocEvaVenRiga getDocEvaVenRiga() {
    return iDocEvaVenRiga;
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

  /**
   *
   * @return
   * @throws java.sql.SQLException
   */
  protected TableManager getTableManager() throws java.sql.SQLException {
    return DocEvaVenRigaPrmTM.getInstance();
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
    setSequenzaRiga(getNumeroRigaDocumento().intValue()); //fix 2322
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
    // PAOLA
    if(((DocEvaVen)this.getTestata()).isEvasioneDaLSP()){
      return super.isSalvaTestata();
    }
    // fine PAOLA
    return false;
  }

  public void setSalvaTestata(boolean salvaTestata) {
    // PAOLA
    if(((DocEvaVen)this.getTestata()).isEvasioneDaLSP()){
      super.setSalvaTestata(salvaTestata);
    }
    // fine PAOLA
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

    // fix 11213 >
    /*
    boolean rigaEstratta = this.getDocEvaVenRiga().isRigaEstratta() ||
        this.isOnDB();
    */
    boolean rigaEstratta = this.getDocEvaVenRiga().isRigaEstratta();
    // fix 11213 <

    this.getDocEvaVenRiga().setRigaEstratta(rigaEstratta);
    this.setRigaForzata(rigaEstratta);
    this.getDocEvaVenRiga().inizializzaQtaRiga();
    //this.getDocEvaVenRiga().logQta("initializeOwnedObjects di '" + this.getKey() +"' isOnDB = '" + this.isOnDB() + "'" );
    if (GestoreEvasioneVendita.get().isAbilitaControlloConcorrenza()) {
      DocumentoOrdineRiga rigaOrd = this.getRigaOrdine();
      if (rigaOrd != null) {
        //ini Fix 1922
        //rigaOrd.abilitaTask(DocEvaVen.TASK_ID_ESTRAZIONE);
        rigaOrd.getTestata().abilitaTask(DocEvaVen.TASK_ID_ESTRAZIONE);
        //fine Fix 1922
      }
    }
    return bo;
  }

  /**
   *
   */
  protected void creaOldRiga() {
    iOldRiga = (DocEvaVenRigaPrm) Factory.createObject(DocEvaVenRigaPrm.class);
  }

  /**
   * setEqual
   * @param obj
   * @throws CopyException
   */
  public void setEqual(Copyable obj) throws CopyException {
    super.setEqual(obj);
    DocEvaVenRigaPrm doc = (DocEvaVenRigaPrm) obj;
    iRigheSecondarie.setEqual(doc.iRigheSecondarie);
  }

  public Integer getNumeroRigaDocumentoSecUtile() {
    return iNumeroRigaDocumentoSecUtile;
  }

  // fix 5500
  //private void setNumeroRigaDocumentoSecUtile(Integer i) {
  public void setNumeroRigaDocumentoSecUtile(Integer i) {
  // fine fix 5500
    iNumeroRigaDocumentoSecUtile = i;
  }

  //ini fix 2322
  public static DocEvaVenRigaPrm creaRiga(DocEvaVen doc,
                                          OrdineVenditaRigaPrm rigaOrdine) {
    return creaRiga(doc, rigaOrdine, true);
  }

  //fine fix 2322

  /**
   * Crea una riga documento da una riga ordine.
   * @param rigaOrdine
   * @return la riga generata, null se no è stato possibile generarla perchè non
   * è stata trovata una causale riga documento appropriata
   */
  public static DocEvaVenRigaPrm creaRiga(DocEvaVen doc,
                                          OrdineVenditaRigaPrm rigaOrdine,
                                          boolean consQtaResidua, DocumentoVenRigaPrm rigaOld) { //fix 2322
    GestoreEvasioneVendita.get().println(
        "--- Inizio creazione nuova riga su riga ordine: '" +
        rigaOrdine.getKey() + "' ---");
    DocEvaVenRigaPrm riga = null;
    CausaleRigaDocVen cauRigaDoc = doc.trovaCausaleRigaDocVen(rigaOrdine);
    if (cauRigaDoc == null) {
      doc.getRigheNonEstratte().put(rigaOrdine,
                                    " causale riga ord '" +
                                    rigaOrdine.getCausaleRigaKey() +
                                    "' non referenziata");
    }
    // Inizio 6473
    else if (!controllaPrezzoSecondoCausale(rigaOrdine, cauRigaDoc)){
      doc.getRigheNonEstratte().put(rigaOrdine,
          " La causale riga doc '" +cauRigaDoc.getIdCausaleRigaDocumentoVen()+
          "' prevede un prezzo significativo per la riga ordine");
    }
    // Fine 6473
    else {
      riga = (DocEvaVenRigaPrm) Factory.createObject(DocEvaVenRigaPrm.class);
      //ini fix 2322
      riga.getDocEvaVenRiga().setConsideraQtaResiduaRigaOrdine(consQtaResidua);
      List righeSecondarieOrdine = rigaOrdine.getRigheSecondarie();
      //fine fix 2322
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
      riga.setEscludiDaDichIntento(cauRigaDoc.isEscludiDaDichIntento());//Fix 30567
      riga.getDatiComuniEstesi().setStato(doc.getDatiComuniEstesi().getStato());
      riga.setUtilizzaContoAnticipi(cauRigaDoc.getUtilizzaContoAnticipi());//Fix 38724
      //Fix 6253 - inizio
      /*
       La seguente riga è stata commentata perchè fa sì che se una sola riga
       viene messa in stato PROVVISORIO, la check mette in stato PROVVISORIO
       anche la testata e quindi tutte le righe seguenti sono messe in stato
       PROVVISORIO
       */
      //riga.setStatoAvanzamento(doc.getStatoAvanzamento());
      riga.setStatoAvanzamento(doc.getStatoAvanzamentoUt());
      //Fix 11084 PM Inizio
      if (doc.getGestionePPL() != CausaleDocumentoVendita.NO)
    	  riga.setStatoAvanzamento(StatoAvanzamento.PROVVISORIO);
      //Fix 11084 PM Fine

      //Fix 6253 - fine

      // attributi di riga

      // Aggiornamento dati da riga ordine
      // fix 6663
      if (rigaOld==null){
        riga.getDocEvaVenRiga().aggiornaAttributiDaRigaOrdine(rigaOrdine);
      }
      else {
        riga.getDocEvaVenRiga().aggiornaAttributiDaRigaDoc(rigaOld);
      }
      // fine fix 6663

      riga.setControlloAccPrn(doc.hasControlloAccPrn());//fix 7220

      //ini fix 2322
      // modifica qta se abilitato controllo disponibilità
      if (righeSecondarieOrdine.isEmpty()) {
        // fix 11273
        //riga.correggiQtaSuControlloDisp(doc.getControlloDisp());
        char tipoControllo = doc.getControlloDisp();
        if (!(PersDatiATP.getCurrentPersDatiATP().getAttivazAccantPrenot() &&
          PersDatiATP.getLivelloControlloAccPrnMan() == PersDatiATP.LC_ERRORE)){

          //if (riga.getArticolo() != null && !riga.getArticolo().isArticLotto()) {//Fix 13001 PM
          if (doc.getTipoBloccoLSP() == TipoBloccoLSP.NESSUNO && riga.getArticolo() != null && !riga.getArticolo().isArticLotto()) {//Fix 13001 PM
            tipoControllo = TipiControlloDisponibilita.GIACENZA;
            if (riga.getMagazzino() != null &&
                riga.getMagazzino().getTpControlloGiacRil() ==
                PersDatiMagazzino.TP_CTL_GIAC_DISP_NO) {
              tipoControllo = TipiControlloDisponibilita.NESSUNO;
            }
          }
        }
        riga.correggiQtaSuControlloDisp(tipoControllo);
        // fine fix 11273
      }
      //Fix 21144 inizio
      riga.verificaGestioneTipoBene();
      //Fix 21144 fine
      //fine fix 2322
      // Righe lotto
      // verifica se è creabile
      if (riga.getDocEvaVenRiga().isAddable(riga.getDocEvaVenRiga(), rigaOrdine)) {
        // fix 5277
        // Questo è un caso che deve essere gestito poichè se rigaOld è dichiarata
        // significa che sono in fase di evasione da proposta evasione, quindi
        // la riga che si sta creando in realtà già esiste e quindi, in fase di creazione
        // dei lotti deve essere tenuto presente che la riga potrebbe avere già dei lotti
        // che devono essere mantenuti.
        List lottiD = null;
        if (rigaOld!=null && rigaOld.getArticolo().isArticLotto()){
          lottiD = rigaOld.getRigheLotto();
        }
        // fine fix 5277
        List righeLottoOrdine = rigaOrdine.getRigheLotto();
        if (righeLottoOrdine != null) {
          Iterator iterLO = righeLottoOrdine.iterator();
          int numRiga = riga.getDocEvaVenRiga().
              getNumeroRigaDocumentoLottoUtile().intValue();
          while (iterLO.hasNext()) {
            riga.getDocEvaVenRiga().setNumeroRigaDocumentoLottoUtile(new
                Integer(numRiga));
            OrdineVenditaRigaLotto obj = (OrdineVenditaRigaLotto) iterLO.next();
            // Fix 5277
            DocEvaVenRigaLottoPrm rigaLotto = null;
            if (lottiD!=null){
              DocumentoVenRigaLottoPrm lottoGiusto = null;
              String codiceL = obj.getIdLotto();
              Iterator lottiDI = lottiD.iterator();
              while (lottiDI.hasNext()){
                DocumentoVenRigaLottoPrm lottoD = (DocumentoVenRigaLottoPrm) lottiDI.next();
                if (lottoD!=null && lottoD.getIdLotto().equals(codiceL)){
                  lottoGiusto = lottoD;
                  break;
                }
              }
              if (lottoGiusto!=null)
                rigaLotto = riga.creaRigaLottoPrm(obj, lottoGiusto);
              else
                rigaLotto = null;
            }
            else{
              //DocEvaVenRigaLottoPrm rigaLotto = riga.creaRigaLottoPrm(obj);
              rigaLotto = riga.creaRigaLottoPrm(obj);
            }
            // fine fix 5277
            if (rigaLotto != null) {
              /*
                             GestoreEvasioneVendita.get().println("Creata riga lotto doc prm :'" + rigaLotto.getKey() +
               "' da riga lotto ord prm :'" + obj.getKey() + "' stato av. = '" +
                  rigaLotto.getStatoAvanzamento() + "'");
               */
              numRiga++;
            }
          }
        }
        char oldStatoAvanzamento = riga.getStatoAvanzamento();
        // Fix 5277
        // Aggiunco il controllo sulla rigaOld perchè in presenza di una riga old
        // significa che mi trovo in evasione di una proposta. ciò vuol dire che non sono
        // nella condizione di fare dei check poichè non ho le quantità della riga principale
        // questi controlli li demanderò quando nella generazione del documento di evasione
        // avrò a disposizione le quantità della riga
        //if (!doc.isDocumentoDiReso()) {
        if (!doc.isDocumentoDiReso() && rigaOld==null) {
        // fine fix 5277
          ErrorMessage emLottiPrm = null;
          char statoForzatura = StatoAvanzamento.PROVVISORIO;
          riga.forzaStatoAvanzamento(statoForzatura, false);
          emLottiPrm = riga.getDocEvaVenRiga().checkQuadraturaLotti();
          if (emLottiPrm == null) {
            if (emLottiPrm == null &&
                (oldStatoAvanzamento == StatoAvanzamento.DEFINITIVO)) {
              statoForzatura = oldStatoAvanzamento;
              riga.forzaStatoAvanzamento(statoForzatura, false);
              emLottiPrm = riga.getDocEvaVenRiga().checkQuadraturaLotti();
              if (emLottiPrm != null || !riga.controllaLottiPrm()) {
                riga.forzaStatoAvanzamento(StatoAvanzamento.PROVVISORIO, false);
                //...FIX 3187 inizio
                // Inizio 5798
//                if (riga.controllaLottiPrm()) {
//                  riga.forzaStatoAvanzamento(StatoAvanzamento.DEFINITIVO, false);
//                  emLottiPrm = null;
//                }
                // Fine 5798
              }
            }
          }
		  riga.setProposizioneAutLotto(null); //38908
          if (emLottiPrm != null) {
            String msg =
                "Errore di quadratura lotti su riga prm riferita alla riga ordine prm :'" +
                riga.getDocEvaVenRiga().getRifRigaOrdineFormattato() +
                "' in stato av.:'" +
                statoForzatura + "' -> " + emLottiPrm.getLongText();
            //Fix 36762 Inizio
            if (doc.getRigheWarnings() != null) {
            	String oldMsg = "";
                if (doc.getRigheWarnings().containsKey(rigaOrdine))
                  oldMsg = (String)doc.getRigheWarnings().get(rigaOrdine) + "\n";
                oldMsg += msg;
                doc.getRigheWarnings().put(rigaOrdine, oldMsg);
              }
            //doc.getRigheWarnings().put(riga.getRigaOrdine(), msg);
            //Fix 36762 Fine
            GestoreEvasioneVendita.get().println(msg);
          }
        }

        //Controllo dichiarazione matricole su riga primaria
        //Fix 6583 - inizio
        ErrorMessage emDichMatr = riga.getDocEvaVenRiga().checkDichiarazioneMatricole();
        if (emDichMatr != null) {
          String msg =
            "Errore di quadratura dichiarazione matricole su riga prm riferita alla riga ordine prm :'" +
            riga.getDocEvaVenRiga().getRifRigaOrdineFormattato() + "': -> " +
            emDichMatr.getLongText();
        //Fix 36762 Inizio
          if (doc.getRigheWarnings() != null) {
          	String oldMsg = "";
              if (doc.getRigheWarnings().containsKey(rigaOrdine))
                oldMsg = (String)doc.getRigheWarnings().get(rigaOrdine) + "\n";
              oldMsg += msg;
              doc.getRigheWarnings().put(rigaOrdine, oldMsg);
            }
          //doc.getRigheWarnings().put(riga.getRigaOrdine(), msg);
          //Fix 36762 Fine
          GestoreEvasioneVendita.get().println(msg);
               riga.sistemaQuantitaDopoVariazioneStatoAv(StatoAvanzamento.PROVVISORIO);
        }
        //Fix 6583 - fine
        //Fix 34783 Inizio
        ErrorMessage emArticoloEsculsion = riga.checkArticoloEsclusione();
        if(emArticoloEsculsion != null) {
        	String msg =
                    riga.getDocEvaVenRiga().getRifRigaOrdineFormattato() + "': -> " +
                    emArticoloEsculsion.getLongText();
        	 //Fix 36762 Inizio
            if (doc.getRigheWarnings() != null) {
            	String oldMsg = "";
                if (doc.getRigheWarnings().containsKey(rigaOrdine))
                  oldMsg = (String)doc.getRigheWarnings().get(rigaOrdine) + "\n";
                oldMsg += msg;
                doc.getRigheWarnings().put(rigaOrdine, oldMsg);
              }
              //doc.getRigheWarnings().put(riga.getRigaOrdine(), msg);
            //Fix 36762 Finie
                  GestoreEvasioneVendita.get().println(msg);
        }
        //Fix 34783 Fine
        // Righe secondarie
        /** @todo CONTROLLARE PER impostaCausale !!! */
        righeSecondarieOrdine = rigaOrdine.getRigheSecondarie();
        if (righeSecondarieOrdine != null) {
          Iterator iterSE = righeSecondarieOrdine.iterator();
          int numRiga = riga.getNumeroRigaDocumentoSecUtile().intValue();
          while (iterSE.hasNext()) {
            riga.setNumeroRigaDocumentoSecUtile(new Integer(numRiga));
            OrdineVenditaRigaSec obj = (OrdineVenditaRigaSec) iterSE.next();
            // Fix 08929 inizio
            if (obj.getDatiComuniEstesi().getStato() != DatiComuniEstesi.VALIDO)
              continue;
            // Fix 08929 fine
            // fix 4996
            //DocEvaVenRigaSec rigaSec = riga.creaRigaSec(obj);
            DocEvaVenRigaSec rigaSec = riga.creaRigaSec(obj, consQtaResidua);
            // fine fix 4996
            if (rigaSec != null) {
              /*
                             GestoreEvasioneVendita.get().println("Creata riga doc sec :'" + rigaSec.getKey() +
                  "' da riga ord sec :'" + obj.getKey() +  "' stato av. = '" +
                  rigaSec.getStatoAvanzamento() + "'");
               */
              numRiga++;
              /** **/
              if (!doc.isDocumentoDiReso()) {
                ErrorMessage emLottiSec = null;
                char oldStatoAvanzamentoSec = rigaSec.getStatoAvanzamento();
                char statoForzatura = StatoAvanzamento.PROVVISORIO;
                rigaSec.forzaStatoAvanzamento(statoForzatura);
                emLottiSec = rigaSec.getDocEvaVenRiga().checkQuadraturaLotti();
                if (emLottiSec == null) {
                  if (emLottiSec == null &&
                      (oldStatoAvanzamentoSec == StatoAvanzamento.DEFINITIVO)) {
                    statoForzatura = oldStatoAvanzamentoSec;
                    rigaSec.forzaStatoAvanzamento(oldStatoAvanzamentoSec);
                    emLottiSec = rigaSec.getDocEvaVenRiga().
                        checkQuadraturaLotti();
                    // Inizio 5798
                    if (emLottiSec != null || !rigaSec.controllaLottiSec()) {
                      rigaSec.forzaStatoAvanzamento(StatoAvanzamento.
                          PROVVISORIO);
                      ((DocEvaVenRigaPrm)rigaSec.getRigaPrimaria()).forzaStatoAvanzamento(StatoAvanzamento.PROVVISORIO, false);
                    }
                    // Fine 5798
                  }
                }
                rigaSec.setProposizioneAutLotto(null); //38908
                if (emLottiSec != null) {
                  String msg =
                      "Errore di quadratura lotti su riga sec riferita alla riga ordine sec :'" +
                      rigaSec.getDocEvaVenRiga().getRifRigaOrdineFormattato() +
                      "' in stato av.:'" +
                      statoForzatura + "' -> " + emLottiSec.getLongText();
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
              emDichMatr = rigaSec.getDocEvaVenRiga().checkDichiarazioneMatricole();
              if (emDichMatr != null) {
                String msg =
                  //"Errore di quadratura dichiarazione matricole su riga prm riferita alla riga ordine prm :'" + //Fix 29288
                  "Errore di quadratura dichiarazione matricole su riga sec riferita alla riga ordine sec :'" + //Fix 29288	  
                  rigaSec.getDocEvaVenRiga().getRifRigaOrdineFormattato() + "': -> " +
                  emDichMatr.getLongText();
                //Fix 36762 Inizio
                if (doc.getRigheWarnings() != null) {
                	String oldMsg = "";
                    if (doc.getRigheWarnings().containsKey(rigaSec.getRigaOrdine()))
                      oldMsg = (String)doc.getRigheWarnings().get(rigaSec.getRigaOrdine()) + "\n";
                    oldMsg += msg;
                    doc.getRigheWarnings().put(rigaSec.getRigaOrdine(), oldMsg);
                  }
                //doc.getRigheWarnings().put(rigaSec.getRigaOrdine(), msg);
                //Fix 36762 Fine
                GestoreEvasioneVendita.get().println(msg);
                rigaSec.sistemaQuantitaDopoVariazioneStatoAv(StatoAvanzamento.PROVVISORIO);
                ((DocEvaVenRigaPrm)rigaSec.getRigaPrimaria()).sistemaQuantitaDopoVariazioneStatoAv(StatoAvanzamento.PROVVISORIO);//Fix 29288
              }
              //Fix 6583 - fine
            }
          }
          //fix 2322

          //non serve falro di nuovo in qunato la collezione righe sec esiste sempre
          //va fatto se ESISTONO righe secondarie e comunque andrebbe scritto per le secondarie in modo opportuno
          if(!righeSecondarieOrdine.isEmpty()) {//fix 7220
            // fix 11402
            //riga.correggiQtaSuControlloDisp(doc.getControlloDisp());
            char tipoControllo = doc.getControlloDisp();
            if (!(PersDatiATP.getCurrentPersDatiATP().getAttivazAccantPrenot() &&
              PersDatiATP.getLivelloControlloAccPrnMan() == PersDatiATP.LC_ERRORE)){
              //if ( riga.getArticolo() != null && !riga.getArticolo().isArticLotto()) { //Fix 13001 PM
              if ( doc.getTipoBloccoLSP() == TipoBloccoLSP.NESSUNO && riga.getArticolo() != null && !riga.getArticolo().isArticLotto()) { //Fix 13001 PM
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
          }
        }
      }
      else {
        riga = null;
      }
    }
    return riga;
  }
  // Inizio 6473
  public static boolean controllaPrezzoSecondoCausale(OrdineVenditaRigaPrm rigaOrd, CausaleRigaDocVen cauRigaDoc){
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
  // Fine 6473



  //ini fix 2322
  public void correggiQtaSuControlloDisp() {
    DocEvaVen doc = (DocEvaVen)this.getTestata();
    if (doc != null) {
      this.correggiQtaSuControlloDisp(doc.getControlloDisp());
    }
  }

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
      this.getDocEvaVenRiga().correggiQtaSuControlloDisp(livDisp); // fix 3134
      if (this.getDocEvaVenRiga().getQtaDaSpedire().isZero()) {
        DocEvaVenRigaLottoPrm lotto = (DocEvaVenRigaLottoPrm)this.
            getDocEvaVenRiga().getLottoDummy();
        if (lotto != null) {
          lotto.getDocEvaVenRigaLotto().assegnaQtaDaSpedire(this.
              getDocEvaVenRiga().getQtaDaSpedire());
        }
      }
    }
  }

  //fine fix 2322

  //ini FIX 1699
  /**
   *
   * @param components
   * @return
   */
  public java.util.Vector checkAll(BaseComponentsCollection components) {
    Vector errors = new Vector();
    boolean check = this.isAbilitaCheckAllCondizionata() ? this.isRigaEstratta() : true;
//Fix 3331 BP ini...
//    if (check){
    if (check && ( (DocEvaVen)this.getTestata()).isAbilitaCheckAll()) {
          /** @todo GSCARTA */
//Fix 3331 BP fine.
      errors = super.checkAll(components);
      // fix 11120 >
      //ErrorMessage em = this.ckeckControlloDisp();
      ErrorMessage em = this.ckeckControlloDisp(errors);
      // fix 11120 <
      if (em != null) {
        errors.add(em);
      }
      //ini Fix 2218
      List ems = this.checkCondCompEvas();
      boolean addAll = errors.addAll(ems);
      em = this.checkRigaModificata();
      if (em != null) {
        errors.add(em);
      }
      //Fix 21144 inizio
      em = checkAltriErroriForzabile(errors);
      if (em != null) {
          errors.add(em);
        }
      //Fix 21144 fine
      //Fix 35178 inizio
      em = checkArticoliRigheSecondarie();
      if(em != null) {
    	  errors.add(em); 
    	  }
      //Fix 35178 fine
    }
    return errors;
  }
  
  //Fix 21144 inizio
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
  //Fix 21144 fine 
   
  //fine FIX 1699

  /** @todo GSCARTA */
  public ErrorMessage checkRigaDuplicata() {
    // nulla
    return null;
  }

  //ini FIX 1699
  public void setAbilitaCheckAllCondizionata(boolean abilita) {
    isAbilitaCheckAllCondizionata = abilita;
  }

  public boolean isAbilitaCheckAllCondizionata() {
    return isAbilitaCheckAllCondizionata;
  }

  //ini Fix 1922 e 2322
  public ErrorMessage checkRigaModificata() {

    ErrorMessage em = null;
    String msg = "";
    boolean isError = false;
    // Federico Crosa fix 3309: modificata condizione sottostante
    boolean eseguiControllo = this.isRigaEstratta() && !this.isRigaForzata()&&(getRigaOrdine()!=null);//Fix 13495
    DocEvaVen doc = (DocEvaVen)this.getTestata();
    if (doc != null) {
      eseguiControllo &= doc.getReperimentoCondEvasione() ==
          TipoReperimento.DA_ORDINE;
      eseguiControllo &= !doc.isEvasioneDaLSP();//Fix 23151
    }
    if (eseguiControllo) {
      // descrizione articolo
      String descrArtRigaDoc = this.getDescrizioneArticolo();
      String descrArtRigaOrd = descrArtRigaDoc;
      DocumentoOrdineRiga rigaOrd = this.getRigaOrdine();
      if (rigaOrd != null) {
        descrArtRigaOrd = rigaOrd.getDescrizioneArticolo();
      }
      // fix 2161
      if (descrArtRigaOrd == null) {
        descrArtRigaOrd = "";
      }
      //ini fix 3242
      if (descrArtRigaDoc == null) {
        descrArtRigaDoc = "";
      }
      //fine fix 3242

      // Federico Crosa fix 3309: modificata condizione sottostante
      boolean isNok = !descrArtRigaDoc.trim().equals(descrArtRigaOrd.trim());
      isError |= isNok;
      // fine fix 2161
      if (isNok) {
        msg += " - descrizione articolo (" + descrArtRigaDoc + " <> " +
            descrArtRigaOrd + ") - ";
      }

      // controllo sconto articolo 1
      BigDecimal roSC1 = rigaOrd == null ? null : rigaOrd.getScontoArticolo1(); // fix 11951
      if (roSC1 == null) {
        roSC1 = GestoreEvasioneVendita.get().getBigDecimalZero();
      }
      BigDecimal rdSC1 = this.getScontoArticolo1();
      if (rdSC1 == null) {
        rdSC1 = GestoreEvasioneVendita.get().getBigDecimalZero();
      }
      isNok = roSC1.compareTo(rdSC1) != 0;
      isError |= isNok;
      if (isNok) {
        msg += " - sconto articolo1 (" + roSC1 + " <> " + rdSC1 + ") - ";
      }

      // controllo sconto articolo 1
      BigDecimal roSC2 = rigaOrd == null ? null : rigaOrd.getScontoArticolo2(); // fix 11951
      if (roSC2 == null) {
        roSC2 = GestoreEvasioneVendita.get().getBigDecimalZero();
      }
      BigDecimal rdSC2 = this.getScontoArticolo2();
      if (rdSC2 == null) {
        rdSC2 = GestoreEvasioneVendita.get().getBigDecimalZero();
      }
      isNok = roSC2.compareTo(rdSC2) != 0;
      isError |= isNok;
      if (isNok) {
        msg += " - sconto articolo2 (" + roSC2 + " <> " + rdSC2 + ") - ";
      }

      // prc sconto intestatario
      BigDecimal roPrcSCI = rigaOrd == null ? null : rigaOrd.getPrcScontoIntestatario(); // fix 11951
      if (roPrcSCI == null) {
        roPrcSCI = GestoreEvasioneVendita.get().getBigDecimalZero();
      }
      BigDecimal rdPrcSCI = this.getPrcScontoIntestatario();
      if (rdPrcSCI == null) {
        rdPrcSCI = GestoreEvasioneVendita.get().getBigDecimalZero();
      }
      isNok = roPrcSCI.compareTo(rdPrcSCI) != 0;
      isError |= isNok;
      if (isNok) {
        msg += " - prc sconto intestatario (" + roPrcSCI + " <> " + rdPrcSCI +
            ") - ";
      }

      // prc sconto modalita
      BigDecimal roPrcSCM = rigaOrd == null ? null : rigaOrd.getPrcScontoModalita(); // fix 11951
      if (roPrcSCM == null) {
        roPrcSCM = GestoreEvasioneVendita.get().getBigDecimalZero();
      }
      BigDecimal rdPrcSCM = this.getPrcScontoModalita();
      if (rdPrcSCM == null) {
        rdPrcSCM = GestoreEvasioneVendita.get().getBigDecimalZero();
      }
      isNok = roPrcSCM.compareTo(rdPrcSCM) != 0;
      isError |= isNok;
      if (isNok) {
        msg += " - prc sconto modalità (" + roPrcSCM + " <> " + rdPrcSCM +
            ") - ";
      }

      // sconto
      String roSC = rigaOrd == null ? null : rigaOrd.getIdSconto(); // fix 11951
      if (roSC == null) {
        roSC = "";
      }
      String rdSC = this.getIdSconto();
      if (rdSC == null) {
        rdSC = "";
      }
      isNok = !roSC.equals(rdSC);
      isError |= isNok;
      if (isNok) {
        msg += " - sconto (" + roSC + " <> " + rdSC + ") - ";
      }

      // sconto modalita
      String roSCM = rigaOrd == null ? null : rigaOrd.getIdScontoMod(); // fix 11951
      if (roSCM == null) {
        roSCM = "";
      }
      String rdSCM = this.getIdScontoMod();
      if (rdSCM == null) {
        rdSCM = "";
      }
      isNok = !roSCM.equals(rdSCM);
      isError |= isNok;
      if (isNok) {
        msg += " - sconto modalità  (" + roSCM + " <> " + rdSCM + ") - ";
      }

      if (isError) {
        String params = "'" +
            this.getDocEvaVenRiga().getRifRigaOrdineFormattato() + "' " + msg;
        em = new ErrorMessage("THIP_BS308", params);
      }
    }
    return em;
  }

  //fine Fix 1922 e 2322
  //fine FIX 1699

  //ini fix 2322
  public ErrorMessage checkRigaOrdineNoFrazionabile() {
    ErrorMessage em = null;
    if (this.getDocEvaVenRiga().isAbilitaControlloQtaZero()) {
      em = super.checkRigaOrdineNoFrazionabile();
    }
    return em;
  }

  protected boolean isQtaOrdinataUgualeZero() {
    return this.getDocEvaVenRiga().isQtaOrdinataUgualeZero();
  }

  //fine fix 2322

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
// Fix 8175 PM Inizio
    /*isOk = this.isAbilitatoAggiornamentoSaldi() ||
        this.isAbilitatoMovimentiMagazzino() &&
        this.isDisponibileControlloDisp();
    */
    isOk = this.isAbilitatoAggiornamentoSaldi() ||
        this.isAbilitatoMovimentiMagazzino();
// Fix 8175 PM Fine

    return isOk;
  }

  /**
   * Restituisce il livello di controllo disponibilità in base al tipo, giacenza
   * o disponibilità
   * @return List a due valori, get(0) = Character indicante il livello, get(1) = BigDecimal
   * indicante il valore di giacenza media o disponibilità
   */
  // fix 11213 >
  //public List getLivelloControlloDisp(boolean addWarnings) {
  public void assegnaLivelloControlloGiaDisp() {
    boolean addWarnings = true;
  // fix 11120 <
    List valori = new ArrayList();
    char livello = LivelliControlloDisponibilita.DEFAULT;
    BigDecimal qtaRifUMPrm = GestoreEvasioneVendita.get().getBigDecimalZero();
    DocEvaVen doc = (DocEvaVen) getTestata();
    char ctrlDisp = doc.getControlloDisp();
    // fix 11273
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
    // fine fix 11273
    boolean isMagPresente = true;
    // if (!this.isDisponibileControlloDisp()) { //Fix 17245 PM
    if (!this.isDisponibileControlloDisp() || ((getCausaleRiga() != null && getCausaleRiga().isRigaMerceAValore())  || (getRigaOrdine() != null && ((OrdineVenditaRiga)getRigaOrdine()).getCausaleRiga().isRigaMerceAValore()   ))) { // Fix 17245 PM
      ctrlDisp = TipiControlloDisponibilita.NESSUNO;
    }
    else {
      GruppoVistaDisponibilita grpVistaDisp = doc.getGruppoVistaDisponibilita();
      if (grpVistaDisp == null) {
        isMagPresente = true;
      }
      else {
        isMagPresente = false;
        Iterator iterVisteDisp = grpVistaDisp.getVisteDisponibilita().iterator();
        while (iterVisteDisp.hasNext()) {
          VistaDisponibilita vistaDisp = (VistaDisponibilita) iterVisteDisp.
              next();
          if (vistaDisp.getMagazziniKeys().contains(this.getMagazzinoKey())) {
            isMagPresente = true;
            break;
          }
        }
      }
    }
	if(this.getArticolo().getTipoParte() == ArticoloDatiIdent.KIT_GEST) {
			String gestKGM = ParametroPsn.getValoreParametroPsn("YGestioneKGM", "Attivazione gestione KGM");
			if(gestKGM != null && gestKGM.equals("Y")) {
				ctrlDisp = TipiControlloDisponibilita.GIACENZA; //71151 DSSOF3 Cambiato a STD in giacenza
			}else {
				ctrlDisp = TipiControlloDisponibilita.DISPONIBILITA;
			}

	}
    //Fix 7220 inizio
    if(isMagPresente)
    {
       if(ctrlDisp == TipiControlloDisponibilita.GIACENZA)
       {
          if(!this.getDocEvaVenRiga().isDocumentoDiReso())
          {
             if(hasControlloAccPrn())
                livello = individuaLivelloDispAccPrn(addWarnings);
             else
             {
               //Fix 34821 - inizio
//               BigDecimal qtaDaSpedireInUMPrm = getDocEvaVenRiga().getQtaDaSpedireInUMPrm();
//               BigDecimal qtaResiduaInUMPrm = this.getQtaResiduaInUMPrm();
//               BigDecimal giacenzaNettaInUMPrm =
//                  CalcolaDisponibilita.get().getGiacenzaNetta(this, this.getDocEvaVenRiga().hasControlloDisp());
               BigDecimal qtaDaSpedireInUMPrm = getQtaDaSpedireUMPrmAssLivCtrlGiaDispNoAccPrn();
               BigDecimal qtaResiduaInUMPrm = getQtaResiduaUMPrmAssLivCtrlGiaDispNoAccPrn();
               BigDecimal giacenzaNettaInUMPrm = getQtaGiacenzaNettaUMPrmAssLivCtrlGiaDispNoAccPrn();
               //Fix 34821 - fine

               livello = LivelliControlloDisponibilita.ROSSO;
               String msg = "";
               List livDisp = new ArrayList();

               //Fix 31850 inizio 
               Character livelloSecondoRigheSec = null;
               if (this.getArticolo().getTipoParte() == ArticoloDatiIdent.KIT_NON_GEST)
            	   //Se esiste almeno una riga sec con BloccoRicalcoloQtaComp, il calcolo del livelloControllo della riga prm deve dipendere del livelloControllo dei righe sec già calcolati            	   
            	   livelloSecondoRigheSec = getLivelloControlloDispRigheSec();
               
               if (livelloSecondoRigheSec==null)
               {
            	 //Fix 31850 Fine
               char tipoBloccoLSP = ((DocEvaVen)this.getDocEvaVenRiga().getRigaDoc().getTestata()).getTipoBloccoLSP();
               if(tipoBloccoLSP == TipoBloccoLSP.IMPEGNATO)
                  livDisp = CalcolaDisponibilita.get().assegnaLivelloDispLsp(qtaDaSpedireInUMPrm, qtaResiduaInUMPrm, giacenzaNettaInUMPrm);
               else
                  livDisp = CalcolaDisponibilita.get().assegnaLivelloDisp(qtaDaSpedireInUMPrm, qtaResiduaInUMPrm, giacenzaNettaInUMPrm);
              //Fix 31850 inizio
               }
               else
               {
            	   livDisp.add(livelloSecondoRigheSec);
            	   livDisp.add(giacenzaNettaInUMPrm);
               }
              //Fix 31850 Fine
               livello = ((Character)livDisp.get(0)).charValue();
               qtaRifUMPrm = (BigDecimal)livDisp.get(1);
               if(livello != LivelliControlloDisponibilita.VERDE)
               {
              	 //Fix 31850 inizio
            	   /*
                   msg += " errore di giacenza, qta  = " + giacenzaNettaInUMPrm +
                           " su richiesta = " + qtaDaSpedireInUMPrm + " per riga ord.'" +
                           this.getRifRigaOrdineFormattato() + "'";
            	   */
            	 if (livelloSecondoRigheSec==null) //Fix 31850  
            	 {
            		 String msg_1 = " " + ResourceLoader.getString(RES_EVA_VEN, "MsgErr_Di_GiCENZA1",new String[]{giacenzaNettaInUMPrm + "",qtaDaSpedireInUMPrm+"",this.getRifRigaOrdineFormattato()});
            		 msg += msg_1;
            	 }
            	 else  
            	 {
            		 String msg_1 = " " + ResourceLoader.getString(RES_EVA_VEN, "MsgErr_Di_GiCENZA2",new String[]{giacenzaNettaInUMPrm + "",this.getRifRigaOrdineFormattato()});
            		 msg += msg_1;
            	 }
            	//Fix 31850 Fine

                 GestoreEvasioneVendita.get().println(msg);

                 if(this.getDocEvaVenRiga().isAbilitaWarnings() && addWarnings)
                 {
                    DocumentoOrdineRiga rigaOrdPrm = this.getRigaOrdine();
                    String s = (String)((DocEvaVen)this.getTestata()).getRigheWarnings().get(rigaOrdPrm);
                    if(s == null)
                       s = "";
                    if (s.indexOf(msg)<0)//Fix 31850	
                    {
                    	msg += s;
                    	((DocEvaVen)this.getTestata()).getRigheWarnings().put(rigaOrdPrm, msg);
                	}
                 }
               }
             }
          }
        }
        else if(ctrlDisp == TipiControlloDisponibilita.DISPONIBILITA)
           livello = LivelliControlloDisponibilita.GIALLO;
        else if(ctrlDisp == TipiControlloDisponibilita.NESSUNO){
          // fix 11273
          //livello = LivelliControlloDisponibilita.VERDE;
          livello = LivelliControlloDisponibilita.NA;
          // fine fix 11273
        }
    }
    else
    {
       livello = LivelliControlloDisponibilita.ROSSO;
    }

    valori.add(0, new Character(livello));
    valori.add(1, qtaRifUMPrm);

    // fix 11120 >
    this.getLivelloControlloDisp().add(0, valori.get(0));
    this.getLivelloControlloDisp().add(1, valori.get(1));
    if(isAbilitaDaAltroErroreForceable()) //Fix 21144
    	abilitaRigaForzabile(livello);
    // fix 11120 <
    /*
    if (isMagPresente) {
      switch (ctrlDisp) {
        case TipiControlloDisponibilita.GIACENZA: {
          if (!this.getDocEvaVenRiga().isDocumentoDiReso()) {
            BigDecimal qtaDaSpedireInUMPrm = getDocEvaVenRiga().
                getQtaDaSpedireInUMPrm(); //GestoreEvasioneVendita.get().getBigDecimalZero();
            BigDecimal qtaResiduaInUMPrm = this.getQtaResiduaInUMPrm();
            //Fix 3331 BP ini...
                         //            if ( ( (DocEvaVen)this.getDocEvaVenRiga().getRigaDoc().getTestata()).
                         //                getTipoBloccoLSP() ==
                         //                TipoBloccoLSP.IMPEGNATO) {
                         //             if(this.isOnDB()==true){
                         // qtaResiduaInUMPrm = qtaResiduaInUMPrm.add(qtaDaSpedireInUMPrm);
                         //             }
                         //            }
            //Fix 3331 BP fine...
            BigDecimal giacenzaNettaInUMPrm = CalcolaDisponibilita.get().
                getGiacenzaNetta(this, this.getDocEvaVenRiga().hasControlloDisp()); //this.getGiacenzaNetta();
            livello = LivelliControlloDisponibilita.ROSSO;
            String msg = "";
            //Fix 3331 BP ini...
                        List livDisp = new ArrayList();
                        if ( ( (DocEvaVen)this.getDocEvaVenRiga().getRigaDoc().getTestata()).
                            getTipoBloccoLSP() ==
                            TipoBloccoLSP.IMPEGNATO) {
                          livDisp = CalcolaDisponibilita.get().assegnaLivelloDispLsp(
                qtaDaSpedireInUMPrm,
                qtaResiduaInUMPrm, giacenzaNettaInUMPrm);
          }
          else {
//Fix 3331 BP fine.
            livDisp = CalcolaDisponibilita.get().assegnaLivelloDisp(
                qtaDaSpedireInUMPrm,
                qtaResiduaInUMPrm, giacenzaNettaInUMPrm);
//Fix 3331 BP ini...
          }
//Fix 3331 BP fine.
            livello = ( (Character) livDisp.get(0)).charValue();
            qtaRifUMPrm = (BigDecimal) livDisp.get(1);
            // @todo GSCARTA fix 4298
            if (livello != LivelliControlloDisponibilita.VERDE) {
              msg += " errore di giacenza, qta  = " + giacenzaNettaInUMPrm +
                  " su richiesta = " +
                  qtaDaSpedireInUMPrm + " per riga ord.'" +
                  this.getRifRigaOrdineFormattato() + "'";
              if (this.getDocEvaVenRiga().isAbilitaWarnings() && addWarnings) {
                DocumentoOrdineRiga rigaOrdPrm = this.getRigaOrdine();
                String s = (String) ( (DocEvaVen)this.getTestata()).
                    getRigheWarnings().
                    get(rigaOrdPrm);
                if (s == null) {
                  s = "";
                }
                msg += s;
                ( (DocEvaVen)this.getTestata()).getRigheWarnings().put(
                    rigaOrdPrm, msg);
              }
              GestoreEvasioneVendita.get().println(msg);
            }
          }
        }
        break;
        case TipiControlloDisponibilita.DISPONIBILITA: {
          livello = LivelliControlloDisponibilita.GIALLO;
        }
        break;
        case TipiControlloDisponibilita.NESSUNO: {
          livello = LivelliControlloDisponibilita.VERDE;
        }
      }
    }
    else {
      livello = LivelliControlloDisponibilita.ROSSO;
    }
    valori.add(0, new Character(livello));
    valori.add(1, qtaRifUMPrm);
    return valori;
*/
  //Fix 7220 fine
  }
  //fine fix 2322
  // fix 11213 <

  //MBH inizio 31850
  /*
   * getLivelloControlloDispRigheSec :
   * Questo metodo viene considerato solo
   * Se esiste almeno una riga sec con BloccoRicalcoloQtaComp ( vale a dire che la qta della riga sec è totale non dipendente della qta della riga prm )
   * in questo caso, il calcolo del livelloControllo della riga prm deve dipendere del livelloControllo dei righe sec già calcolati
   * Altrimente (come prima), dipende della qta gricenza calcolata rispetto alla qta richiesta della riga prm.
   * in questo ultimo caso ritornera Null per non considerare la valore ritornato è il livello sara calcolato dopo come prima.  
   */
  public Character getLivelloControlloDispRigheSec() {
	  boolean esisteBloccoRicalcolo = false;
	  boolean esisteRosso = false;
	  boolean esisteGiallo = false;
	  boolean esisteVerde = false;
	  Character liv = null;
      Iterator iter = ( (RigaPrimaria) this).getRigheSecondarie().iterator();
      while (iter.hasNext()) {
        DocumentoOrdineRiga rigaSec = (DocumentoOrdineRiga) iter.next();
        if (! (rigaSec instanceof DocEvaVenRigaSec)) //E messo solo per securezza (non sono sicuro che sia possibile di avere a questo livello il instance diverso di DocEvaVenRigaSec)  
        	return null;
        
        if(rigaSec.isBloccoRicalcoloQtaComp())
        	esisteBloccoRicalcolo =  true;
        DocEvaVenRigaSec riga = (DocEvaVenRigaSec)rigaSec;
        liv = (Character)riga.getLivelloControlloDisp().get(0);
        if (liv !=null && liv.charValue() == LivelliControlloDisponibilita.ROSSO)
        	esisteRosso = true;
        else if (liv !=null && liv.charValue() == LivelliControlloDisponibilita.GIALLO)
        	esisteGiallo = true;
        else if (liv !=null && liv.charValue() == LivelliControlloDisponibilita.VERDE)
        	esisteVerde = true;
        
      }
      if (!esisteBloccoRicalcolo)
    	  return null;
      
      if (esisteVerde && !esisteRosso && !esisteGiallo)
    	  return new Character(LivelliControlloDisponibilita.VERDE);

      if (!esisteVerde && esisteRosso && !esisteGiallo)
    	  return new Character(LivelliControlloDisponibilita.ROSSO);
      
      return new Character(LivelliControlloDisponibilita.GIALLO);
  }
  //Fine 31850
   //fix 7220 inizio
   public char individuaLivelloDispAccPrn(boolean addWarnings)
   {
      // Fix 17245 PM >
      if (getCausaleRiga() != null && (getCausaleRiga().isRigaMerceAValore()  || (getRigaOrdine() != null && ((OrdineVenditaRiga)getRigaOrdine()).getCausaleRiga().isRigaMerceAValore())))
         return LivelliControlloDisponibilita.VERDE;
      // Fix 17245 PM <
      BigDecimal qtaDaSpedireInUMPrm = getDocEvaVenRiga().getQtaDaSpedireInUMPrm();
      //MBH 32165 inizio
      if (getDocEvaVenRiga().getQtaDaSpedireSecondoGiacenza() != null)
    	  //Questo Solo in caso di riga non frazionabile 
    	  qtaDaSpedireInUMPrm = getDocEvaVenRiga().getQtaDaSpedireSecondoGiacenza().getQuantitaInUMPrm();
      //MBH 32165 Fine
      
      BigDecimal qtaResiduaInUMPrm = this.getQtaResiduaInUMPrm();
      //simulo disponibilità piena
      List livDisp = CalcolaDisponibilita.get().assegnaLivelloDisp(qtaDaSpedireInUMPrm, qtaResiduaInUMPrm, qtaDaSpedireInUMPrm);
      char livello = ((Character)livDisp.get(0)).charValue();
      
      
      if(livello != LivelliControlloDisponibilita.VERDE)
      {
         BigDecimal qtaResiduaInUMRif = this.getQtaResiduaInUMRif();
         String msg = " quantità in giacenza non sufficiente su residua = " + qtaResiduaInUMRif +
                      " per riga ord.'" + this.getRifRigaOrdineFormattato() + "'";
         GestoreEvasioneVendita.get().println(msg);

         if(this.getDocEvaVenRiga().isAbilitaWarnings() && addWarnings)
         {
            DocumentoOrdineRiga rigaOrdPrm = this.getRigaOrdine();
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

  /**
   * Esegue il controllo di disponibilità in base al tipo selezionato, per giacenza
   * o per disponibilità indipendentemente se la riga è stata selezionata per l'estrazione
   * Il controllo è eseguito solo se la riga non è forzata in evasione.
   * @return
   */
  // fix 11120 >
  /*
  public final ErrorMessage ckeckControlloDisp() {
    return ckeckControlloDisp(true);
  }
  */
  // fix 11120 <

  /**
   * Esegue il controllo di disponibilità in base al tipo selezionato, per giacenza
   * o per disponibilità.
   * Il controllo è eseguito solo se la riga non è forzata in evasione.
   * @param soloEstratta true esegue solo se la riga è stata selezionata per l'estrazione,
   * altrimenti esegue sempre il controllo
   * @return
   */
  // fix 11120 >
  //public ErrorMessage ckeckControlloDisp(boolean soloEstratta) {
  public ErrorMessage ckeckControlloDisp(boolean soloEstratta, List errors) {
  // fix 11120 <
    ErrorMessage errMsg = null;
    boolean eseguiControllo = true;
    if (soloEstratta) {
      eseguiControllo = getDocEvaVenRiga().isRigaEstratta();
    }
    //fix 7220  inizio
    if(isAbilitatoCheckAccPrn() && PersDatiATP.getLivelloControlloAccPrnMan() == PersDatiATP.LC_ERRORE )
    		errMsg = checkDisponibilitaAccPrn();
    else
    {
       //fix 7220  fine
       // fix 11213 >
       errMsg = riassegnaErroreGiaDisp(errors);
       /*
       eseguiControllo &= !this.isRigaForzata();
       if (this.getDocEvaVenRiga().hasControlloDisp() && eseguiControllo) {
         List valori = getLivelloControlloDisp();
         char livello = ( (Character) valori.get(0)).charValue();
         BigDecimal qtaLivello = (BigDecimal) valori.get(1);
         String[] params = new String[] {
             getNumeroRigaDocumento().toString(), qtaLivello.toString()
         };
         switch ( ( (DocEvaVen) getTestata()).getControlloDisp()) {
           case TipiControlloDisponibilita.GIACENZA: {
             switch (livello) {
               case LivelliControlloDisponibilita.ROSSO: {
                 errMsg = new ErrorMessage("THIP_BS117", params);
               }
               break;
               // fix 11120 >
               case LivelliControlloDisponibilita.GIALLO: {
                 if (errors != null) {
                   Iterator i = errors.iterator();
                   while (i.hasNext()) {
                     ErrorMessage e = (ErrorMessage)i.next();
                     if (e.getId().equals(CalcoloGiacenzaDisponibilità.ERR_FORZABILE_GIA)) {
                       params = new String[] {
                           this.getRifRigaOrdineFormattato() + ": " + e.getLongText()};
                       errMsg = new ErrorMessage("THIP300219", params);
                       //errMsg = new ErrorMessage("THIP_BS117", params);
                     }
                   }
                 }
               }
               break;
               // fix 11120 <
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
      // fix 11213 <

    }
    return errMsg;
  }

  //fix 7220  inizio
  public ErrorMessage checkDisponibilitaAccPrn()
  {
	 
	//Fix 34038
	if (this.getCausaleRiga().getAzioneMagazzino() != AzioneMagazzino.USCITA)
		  return null;
	//Fix 34038
	 
	  
     ErrorMessage err = null;
     BigDecimal zero = new BigDecimal("0.00");
     BigDecimal qtaNonDisponibileSuRiga = getQtaNonDisponibileConAccPrn();
     if(qtaNonDisponibileSuRiga.compareTo(zero) > 0)
     {
        BigDecimal dispSuRiga = getQtaDaSpedireInUMPrm().subtract(qtaNonDisponibileSuRiga);
        BigDecimal dispSuRigaRif = getArticolo().convertiUM(dispSuRiga, getUMPrm(), getUMRif(), getArticoloVersRichiesta()); // fix 10955
        //dispSuRigaRif = dispSuRigaRif.setScale(2, BigDecimal.ROUND_HALF_UP);//Fix 30871
		dispSuRigaRif = Q6Calc.get().setScale(dispSuRigaRif,2, BigDecimal.ROUND_HALF_UP);//Fix 30871
        String msg_1 = ResourceLoader.getString(RES_EVA_VEN, "MsgErr_THIP200380_1");
        String msg_2 = ResourceLoader.getString(RES_EVA_VEN, "MsgErr_THIP200380_2");
        String param = msg_1 + " '" + this.getRifRigaOrdineFormattato() + "' " + msg_2 + " " + dispSuRigaRif;
        /*
        String msg = " Giacenza non sufficiente su spedizione " + getQtaDaSpedireInUMRif() +
                     " per riga ord.'" + this.getRifRigaOrdineFormattato() + "'  Massimo sped " + dispSuRigaRif;
        */
        err = new ErrorMessage("THIP200380", param);
     }
     return err;
  }

  public BigDecimal getQtaNonDisponibileConAccPrn()
  {
     BigDecimal zero = new BigDecimal("0.00");
     //Fix 9181 inizio
     if(!isAbilitatoMovimentiMagazzino())
        return zero;
     //Fix 9181 fine
     //Fix 9251 inizio
     if(getStatoAvanzamento() != StatoAvanzamento.DEFINITIVO)
        return zero;
     //Fix 9251 fine
     BigDecimal qtaNonDisponibileAccPrn = zero;
     if(this.getArticolo().getTipoParte() == ArticoloDatiIdent.KIT_NON_GEST)
     {
        DocEvaVen doc = (DocEvaVen)getTestata();
        Iterator iterSec = getRigheSecondarie().iterator();
        while(iterSec.hasNext())
        {
           DocEvaVenRigaSec rigaSec = (DocEvaVenRigaSec)iterSec.next();
           BigDecimal coeffImp = rigaSec.getCoefficienteImpiego();
           if(!rigaSec.isBloccoRicalcoloQtaComp() && coeffImp.compareTo(zero) > 0)
           {
              BigDecimal qtaRigaSecDoc = rigaSec.getQtaDaSpedireInUMPrm();
              BigDecimal qtaNonDispSec =
                 doc.getQtaNonDisponibileConAccPrn(rigaSec, rigaSec.getOldRiga(), (OrdineVenditaRiga)rigaSec.getRigaOrdine(), qtaRigaSecDoc);
              if(qtaNonDispSec.compareTo(zero) > 0)
              {
                 //BigDecimal qtaNonDispPrm = qtaNonDispSec.divide(coeffImp, 2, BigDecimal.ROUND_HALF_UP);//Fix 30871
				   BigDecimal qtaNonDispPrm = Q6Calc.get().divide(qtaNonDispSec , coeffImp, 2, BigDecimal.ROUND_HALF_UP);//Fix 30871
                 if(qtaNonDispPrm.compareTo(qtaNonDisponibileAccPrn) > 0)
                    qtaNonDisponibileAccPrn = qtaNonDispPrm;
              }
           }
        }
     }
     else
     {
        DocEvaVen doc = (DocEvaVen)getTestata();
        BigDecimal qtaRigaDoc = getQtaDaSpedireInUMPrm();
        qtaNonDisponibileAccPrn =
           doc.getQtaNonDisponibileConAccPrn(this, this.getOldRiga(), (OrdineVenditaRiga)this.getRigaOrdine(), qtaRigaDoc);
     }
     return qtaNonDisponibileAccPrn;
  }
  //fix 7220  fine

  /**
   * Esegue il controllo del gruppo consegna evidenziando per la riga documento
   * se è presente nel gruppo consegna ma non è stata estratta. Il controllo è
   * eseguito se non vi è forzatura di evasione
   * @return
   */
  public ErrorMessage checkGruppoConsegna() throws ThipException {
    ErrorMessage errMsg = null;
    if (!this.isRigaForzata()) {
      OrdineVenditaRigaPrm rigaOrdine = (OrdineVenditaRigaPrm)this.
          getRigaOrdine();
      if (rigaOrdine != null) {
        String keyGruppoConsegna = rigaOrdine.getTestataKey() +
            KeyHelper.KEY_SEPARATOR + rigaOrdine.getGruppoConsegna();
        Map grpGruppoConsegna = ( (DocEvaVen)this.getTestata()).
            getGruppiConsegna();
        Map righeOrd = (Map) grpGruppoConsegna.get(keyGruppoConsegna);
        if (righeOrd != null) {
          String keyRigaOrd = rigaOrdine.getKey();
          //GestoreEvasioneVendita.get().println("Riga Ordine : '" + keyRigaOrd + "' -> Righe Ord : " + righeOrd.toString());
          if (this.isRigaEstratta()) {
            StringBuffer srighe = new StringBuffer();
            Iterator iterRigheOrd = righeOrd.values().iterator();
            while (iterRigheOrd.hasNext()) {
              Object riga = iterRigheOrd.next(); //fix 2322
              if (riga instanceof DocEvaVenRigaPrm) {
                if ( ( (DocEvaVenRigaPrm) riga).isRigaEstratta()) {
                  if (righeOrd.get(keyRigaOrd) instanceof DocEvaVenRigaPrm) {
                    Iterator irighe = righeOrd.values().iterator();
                    while (irighe.hasNext()) {
                      DocEvaVenRigaPrm rcorr = (DocEvaVenRigaPrm) irighe.next();
                      if (!rcorr.isRigaEstratta() &&
                          !rcorr.getKey().equals(this.getKey())) {
                        srighe.append(" [");
                        srighe.append(rcorr.getDocEvaVenRiga().
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
                                        new String[] {this.getDocEvaVenRiga().
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
      OrdineVenditaRigaPrm rigaOrdine = (OrdineVenditaRigaPrm)this.
          getRigaOrdine();
      if (rigaOrdine != null) {
        String keyOrdine = rigaOrdine.getTestataKey();
        Map grpOrdineIntero = ( (DocEvaVen)this.getTestata()).
            getGruppiOrdineIntero();
        Map righeOrd = (Map) grpOrdineIntero.get(keyOrdine);
        if (righeOrd != null) {
          String keyRigaOrd = rigaOrdine.getKey();
          //GestoreEvasioneVendita.get().println("Riga Ordine : '" + keyRigaOrd + "' -> Righe Ord : " + righeOrd.toString());
          if (this.isRigaEstratta()) {
            StringBuffer srighe = new StringBuffer();
            Iterator iterRigheOrd = righeOrd.values().iterator();
            while (iterRigheOrd.hasNext()) {
              Object riga = iterRigheOrd.next(); //fix 2322
              if (riga instanceof DocEvaVenRigaPrm) {
                if ( ( (DocEvaVenRigaPrm) riga).isRigaEstratta()) {
                  if (righeOrd.get(keyRigaOrd) instanceof DocEvaVenRigaPrm) {
                    Iterator irighe = righeOrd.values().iterator();
                    while (irighe.hasNext()) {
                      DocEvaVenRigaPrm rcorr = (DocEvaVenRigaPrm) irighe.next();
                      if (!rcorr.isRigaEstratta() &&
                          !rcorr.getKey().equals(this.getKey())) {
                        srighe.append(" [");
                        srighe.append(rcorr.getDocEvaVenRiga().
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
                                        new String[] {this.getDocEvaVenRiga().
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
      OrdineVenditaRigaPrm rigaOrdine = (OrdineVenditaRigaPrm)this.
          getRigaOrdine();
      if (rigaOrdine != null) {
        if (! ( (OrdineVenditaTestata) rigaOrdine.getTestata()).
            getRaggruppamentoOrdBolla()) {
          String keyOrdineRif = null;
          DocEvaVenRigaPrm rigaRif = ( (DocEvaVen)this.getTestata()).
              getRigaPrmPerCondCompEvas();
          if (rigaRif != null) {
            keyOrdineRif = rigaRif.getRigaOrdine().getTestataKey();
          }
          String keyOrdine = rigaOrdine.getTestataKey();
          if (keyOrdineRif != null && !keyOrdineRif.equals(keyOrdine)) {
            errMsg = new ErrorMessage("THIP_BS231",
                                      new String[] {this.getDocEvaVenRiga().
                                      getRifRigaOrdineFormattato()});

          }
        }
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
    //Fix 3549 Inizio PM
    //if(!isOnDB()) {
    if (this.getStatoAvanzamento() == StatoAvanzamento.DEFINITIVO && !isOnDB()) {
      //Fix 3549 Fine PM
      char tipoDoc = ( (DocumentoVendita)this.getTestata()).
          getTipoDocVenPerGestMM();
      if (tipoDoc == DocumentoVendita.TD_VENDITA ||
          tipoDoc == DocumentoVendita.TD_SPE_CTO_TRASF) {
        //...Controllo che la creazione automatica sia impostata
        boolean ok = identificaLotto();
        if (ok){
          if (!controllaQtaLotti(PersDatiMagazzino.TIPO_VEN,
                                 ProposizioneAutLotto.CREA_DA_DOCUMENTO,
                                 getIdMagazzino()))
          {//Fix 14336 inizio
              Vector params = new Vector();
              DecimalType decType = new DecimalType();
              //decType.setScale(getQtaDaSpedireInUMPrm().scale());//Fix 30871
              //decType.setScale(Q6Calc.get().scale(getQtaDaSpedireInUMPrm().scale()));//Fix 30871 //Fix 39402
              decType.setScale(Q6Calc.get().scale(2));//Fix 39402
			  params.add(decType.objectToString(getQtaDaSpedireInUMPrm()));
              params.add(decType.objectToString(iQuantitaLotti));
              return new ErrorMessage("THIP40T105",params);
             // return new ErrorMessage("THIP_BS101");
            }//Fix 14336 fine
        }
      }
    }

    return errMsg;
  }

  // Inizio 6944
  public boolean verificaGiacenzaLotto(){
    boolean okGiacenza = true;
    boolean ok = identificaLotto();
    char tipoDoc = ( (DocumentoVendita)this.getTestata()).
      getTipoDocVenPerGestMM();
    if (!ok && getStatoAvanzamento() == StatoAvanzamento.DEFINITIVO &&
        tipoDoc == DocumentoVendita.TD_VENDITA){
      DocEvaVenRigaLottoPrm rigaLotto = (DocEvaVenRigaLottoPrm)getUnicoLottoEffettivo();
      if (rigaLotto != null){
        BigDecimal qtaDisp = controllaDispUnicoLottoEffettivo(rigaLotto);
        okGiacenza = (qtaDisp != null && qtaDisp.compareTo(getQtaDaSpedireInUMPrm())>=0);
      }
    }
    return okGiacenza;
  }
  // Fine 6944

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
    ErrorMessage errMsg = null;
    boolean isOk = true;
    boolean eseguiControllo = this.isRigaEstratta();
    //Fix 3315 PM Inizio
    //if (eseguiControllo) {
    if (eseguiControllo && this.getRigaOrdine() != null) {
      //Fix 3315 PM Fine
      String rifRigaOrd = this.getDocEvaVenRiga().getRifRigaOrdineFormattato();
      BigDecimal qtaZero = GestoreEvasioneVendita.get().getBigDecimalZero();
      if (getDocEvaVenRiga().getQtaDaSpedireInUMRif().compareTo(qtaZero) > 0 ||
          getDocEvaVenRiga().getQtaDaSpedireInUMPrm().compareTo(qtaZero) > 0) {

        DocEvaVen doc = (DocEvaVen)super.getTestata();
        doc.assegnaRigaPrmPerCondCompEvas(); //fix 2322
        DocEvaVenRigaPrm rigaDocRif = doc.getRigaPrmPerCondCompEvas();
        //fix 2322
        if (rigaDocRif == null) {
          if (this.getRigaOrdine() != null) {
            doc.setRigaPrmPerCondCompEvas(this);
          }
        }
        //fine fix 2322
        OrdineVendita ordTes = (OrdineVendita)this.getRigaOrdine().getTestata();
        // fix 4996
        String idCaurigaDocTesOrd = null;
        String idCaurigaTesOrd = null;
        if (doc.getCondCompEvas().isGesCauDocVen()){
          if (rigaDocRif != null && rigaDocRif.getRigaOrdine()!=null){
            idCaurigaDocTesOrd = rigaDocRif.getRigaOrdine().getTestata().getIdCau();
          }
          if (this.getRigaOrdine()!=null && this.getRigaOrdine().getTestata()!=null){
            idCaurigaTesOrd = this.getRigaOrdine().getTestata().getIdCau();
          }
        }
        List erroriTes = doc.checkCondCompEvasRiga(ordTes, rifRigaOrd, idCaurigaTesOrd, idCaurigaDocTesOrd);
        errors.addAll(erroriTes);
        /*
        CondizioniCompatibilitaEvasione cond = doc.getCondCompEvas();
        Object idA = null;
        Object idB = null;
        if (cond == null) {
          isOk = false;
        }
        if (isOk && cond.isGesAgenteSubagente()) {
          idA = doc.getIdAgente() == null ? "" : doc.getIdAgente();
          idB = ordTes.getIdAgente() == null ? "" : ordTes.getIdAgente();
          isOk = idA.equals(idB);
          if (!isOk) {
            errors.add(new ErrorMessage("THIP_BS332", new String[] {rifRigaOrd,
                                        "doc: '" + idA + "' <> ord: '" + idB}));
          }
        }
        if (isOk && cond.isGesAssIVA()) {
          idA = doc.getIdAssogIva() == null ? "" : doc.getIdAssogIva();
          idB = ordTes.getIdAssogIva() == null ? "" : ordTes.getIdAssogIva();
          isOk = idA.equals(idB);
          if (!isOk) {
            errors.add(new ErrorMessage("THIP_BS333", new String[] {rifRigaOrd,
                                        "doc: '" + idA + "' <> ord: '" + idB +
                                        "'"}));
          }
        }
        if (isOk && cond.isGesCauDocVen()) {
          //fix 2322
          if (rigaDocRif != null && rigaDocRif.getRigaOrdine() != null) {
            idA = rigaDocRif.getRigaOrdine().getTestata().getIdCau();
            idB = this.getRigaOrdine() == null ||
                this.getRigaOrdine().getTestata().getIdCau() == null ? "" :
                this.getRigaOrdine().getTestata().getIdCau();
            isOk = idA.equals(idB);
            if (!isOk) {
              errors.add(new ErrorMessage("THIP_BS334",
                                          new String[] {rifRigaOrd,
                                          "doc: '" + idA + "' <> ord: '" + idB +
                                          "'"}));
            }
          }
          //fine fix 2322
        }
        if (isOk && cond.isGesClienteFatturazione()) {
          idA = doc.getIdClienteFat() == null ? "" : doc.getIdClienteFat();
          idB = ordTes.getIdClienteFat() == null ? "" : ordTes.getIdClienteFat();
          isOk = idA.equals(idB);
          if (!isOk) {
            errors.add(new ErrorMessage("THIP_BS335", new String[] {rifRigaOrd,
                                        "doc: '" + idA + "' <> ord: '" + idB +
                                        "'"}));
          }
        }
        if (isOk && cond.isGesDestinatario()) {
          idA = doc.getIdDenAbt() == null ? "" : doc.getIdDenAbt();
          idB = ordTes.getIdDenAbt() == null ? "" : ordTes.getIdDenAbt();
          isOk = idA.equals(idB);
          if (!isOk) {
            errors.add(new ErrorMessage("THIP_BS336", new String[] {rifRigaOrd,
                                        "doc: '" + idA + "' <> ord: '" + idB +
                                        "'"}));
          }
          // fix 2402
          if (isOk) {
            idA = doc.getIdSequenzaInd() == null ? "" :
                doc.getIdSequenzaInd().toString();
            idB = ordTes.getIdSequenzaInd() == null ? "" :
                ordTes.getIdSequenzaInd().toString();
            isOk = idA.equals(idB);
            if (!isOk) {
              errors.add(new ErrorMessage("THIP_BS336",
                                          new String[] {rifRigaOrd,
                                          "doc: '" + idA + "' <> ord: '" + idB +
                                          "'"}));
            }
          }

          if (isOk) {
            idA = doc.getRagioneSocaleDest() == null ? "" :
                doc.getRagioneSocaleDest();
            idB = ordTes.getRagioneSocaleDest() == null ? "" :
                ordTes.getRagioneSocaleDest();
            isOk = idA.equals(idB);
            if (!isOk) {
              errors.add(new ErrorMessage("THIP_BS336",
                                          new String[] {rifRigaOrd,
                                          "doc: '" + idA + "' <> ord: '" + idB +
                                          "'"}));
            }
          }

          if (isOk) {
            idA = doc.getIndirizzoDestinatario() == null ? "" :
                doc.getIndirizzoDestinatario();
            idB = ordTes.getIndirizzoDestinatario() == null ? "" :
                ordTes.getIndirizzoDestinatario();
            isOk = idA.equals(idB);
            if (!isOk) {
              errors.add(new ErrorMessage("THIP_BS336",
                                          new String[] {rifRigaOrd,
                                          "doc: '" + idA + "' <> ord: '" + idB +
                                          "'"}));
            }
          }

          if (isOk) {
            idA = doc.getLocalitaDestinatario() == null ? "" :
                doc.getLocalitaDestinatario();
            idB = ordTes.getLocalitaDestinatario() == null ? "" :
                ordTes.getLocalitaDestinatario();
            isOk = idA.equals(idB);
            if (!isOk) {
              errors.add(new ErrorMessage("THIP_BS336",
                                          new String[] {rifRigaOrd,
                                          "doc: '" + idA + "' <> ord: '" + idB +
                                          "'"}));
            }
          }

          if (isOk) {
            idA = doc.getCAPDestinatario() == null ? "" :
                doc.getCAPDestinatario();
            idB = ordTes.getCAPDestinatario() == null ? "" :
                ordTes.getCAPDestinatario();
            isOk = idA.equals(idB);
            if (!isOk) {
              errors.add(new ErrorMessage("THIP_BS336",
                                          new String[] {rifRigaOrd,
                                          "doc: '" + idA + "' <> ord: '" + idB +
                                          "'"}));
            }
          }
          // fine fix 2402
        }
        if (isOk && cond.isGesFatPeriodica()) {
          isOk = doc.getFatturazionePeriodica() ==
              ordTes.getFatturazionePeriodica();
          if (!isOk) {
            errors.add(new ErrorMessage("THIP_BS337", new String[] {rifRigaOrd,
                                        "doc: '" + doc.getFatturazionePeriodica() +
                                        "' <> ord: '" +
                                        ordTes.getFatturazionePeriodica() + "'"}));
          }
        }
        if (isOk && cond.isGesInizioPagamento()) {
          long l = 0;
          idA = doc.getDataInizioPagamento() == null ? new java.sql.Date(0) :
              doc.getDataInizioPagamento();
          idB = ordTes.getDataInizioPagamento() == null ? new java.sql.Date(0) :
              ordTes.getDataInizioPagamento();
          isOk = idA.equals(idB);
          if (!isOk) {
            errors.add(new ErrorMessage("THIP_BS338", new String[] {rifRigaOrd,
                                        "doc: '" + idA + "' <> ord: '" + idB +
                                        "'"}));
          }
        }
        if (isOk && cond.isGesModalitaConsegna()) {
          idA = doc.getIdModConsegna() == null ? "" : doc.getIdModConsegna(); ;
          idB = ordTes.getIdModConsegna() == null ? "" :
              ordTes.getIdModConsegna(); ;
          isOk = idA.equals(idB);
          if (!isOk) {
            errors.add(new ErrorMessage("THIP_BS339", new String[] {rifRigaOrd,
                                        "doc: '" + idA + "' <> ord: '" + idB +
                                        "'"}));
          }
        }
        if (isOk && cond.isGesModalitaPagamento()) {
          idA = doc.getIdModPagamento() == null ? "" : doc.getIdModPagamento();
          idB = ordTes.getIdModPagamento() == null ? "" :
              ordTes.getIdModPagamento();
          isOk = idA.equals(idB);
          if (!isOk) {
            errors.add(new ErrorMessage("THIP_BS340", new String[] {rifRigaOrd,
                                        "doc: '" + idA + "' <> ord: '" + idB +
                                        "'"}));
          }
        }
        if (isOk && cond.isGesModalitaSpedizione()) {
          idA = doc.getIdModSpedizione() == null ? "" : doc.getIdModSpedizione();
          idB = ordTes.getIdModSpedizione() == null ? "" :
              ordTes.getIdModSpedizione();
          isOk = idA.equals(idB);
          if (!isOk) {
            errors.add(new ErrorMessage("THIP_BS341", new String[] {rifRigaOrd,
                                        "doc: '" + idA + "' <> ord: '" + idB +
                                        "'"}));
          }
        }
        if (isOk && cond.isGesScontoFineFattura()) {
          BigDecimal prcSFFA = doc.getPrcScontoFineFattura();
          BigDecimal prcSFFB = ordTes.getPrcScontoFineFattura();
          if (prcSFFA == null && prcSFFB == null) {
            isOk = true;
          }
          else if ( (prcSFFA == null && prcSFFB != null) ||
                   (prcSFFA != null && prcSFFB == null)) {
            isOk = false;
          }
          else if (prcSFFA != null && prcSFFB != null) {
            isOk = prcSFFA.compareTo(prcSFFB) == 0;
          }
          if (!isOk) {
            errors.add(new ErrorMessage("THIP_BS342", new String[] {rifRigaOrd,
                                        "doc: '" + prcSFFA + "' <> ord: '" +
                                        prcSFFB + "'"}));
          }
        }
        if (cond.isGesValuta()) {
          idA = doc.getIdValuta() == null ? "" : doc.getIdValuta();
          idB = ordTes.getIdValuta() == null ? "" : ordTes.getIdValuta();
          isOk = idA.equals(idB);
          if (!isOk) {
            errors.add(new ErrorMessage("THIP_BS343", new String[] {rifRigaOrd,
                                        "doc: '" + idA + "' <> ord: '" + idB +
                                        "'"}));
          }
        }
        */
        // fine fix 4996
      }
    }
    return errors;
  }

  //fine Fix 2218

  /**
   *
   * @param doc
   * @param rigaOrdine
   * @return
   */
  protected DocEvaVenRigaLottoPrm creaRigaLottoPrm(OrdineVenditaRigaLotto
      rigaOrdineLotto) {
    return DocEvaVenRigaLottoPrm.creaRiga(this, rigaOrdineLotto);
  }

  /**
   *
   * @param doc
   * @param rigaOrdine
   * @return
   */
  protected DocEvaVenRigaSec creaRigaSec(OrdineVenditaRigaSec rigaOrdine) {
    return DocEvaVenRigaSec.creaRiga(this, rigaOrdine);
  }

  // Fix 4996
  protected DocEvaVenRigaSec creaRigaSec(OrdineVenditaRigaSec rigaOrdine, boolean conQtaResidua) {
    return DocEvaVenRigaSec.creaRiga(this, rigaOrdine, conQtaResidua);
  }
  // fine fix 4996
  // Inizio 5395
  //added: 3741
//  private static boolean nulla(BigDecimal qta) {
//    return qta == null || qta.compareTo(new BigDecimal(0)) == 0;
//  }

  private static boolean nulla(BigDecimal qta) {
    return qta == null;
  }
  //added: 3741
  // Fine 5395

  private static boolean diversi(BigDecimal a, BigDecimal b) {
    if (a == null)
      return b != null;

    if (b == null)
      return true;

    return a.compareTo(b) != 0;
  }

  private static boolean uguali(BigDecimal a, BigDecimal b) {
    return!diversi(a, b);
  }

  //fixed: 3741
  /**
   * Aggiorna i dati sulla riga con i dati corrispondenti in ParaRigaPrm. Le quantità Rif e Prm se variate
   * vengono mantenute solamente se non vengono annullate. In questo caso vengono ricalcolate applicando
   * il fattore di conversione delle unità di misura.
   * @param pr
   */
  public void aggiornaRiga(ParamRigaPrmDocEvaVen pr) { //fix 2322
    if (isOnDB()) {
      pr.iEstratta = true;
    }
    this.setRigaEstratta(pr.iEstratta);

    // anche sulle sue righe secondarie
    Iterator righeSec = this.getRigheSecondarie().iterator();
    while (righeSec.hasNext()) {
      DocEvaVenRigaSec rigaSec = (DocEvaVenRigaSec) righeSec.next();
      rigaSec.setRigaEstratta(pr.iEstratta); //fix 2322
    }
    // Fix 04324 EP ini
//    this.setRigaSaldata(pr.iSaldo);
    this.setRigaSaldata(pr.iSaldo || isSaldoAutomatico());
    // Fix 04324 EP fin
    this.setStatoAvanzamentoDef(pr.iDefinitivo); //fix 2322
    this.setRigaForzata(pr.iForzaEvasione);
    //fix 2322
    if (! ( (DocEvaVen)this.getTestata()).getMostraCodiceArtEsterno()) {
      this.setDescrizioneArticolo(pr.iDescrizioneArticolo);
    }
    //Fix 4825 - inizio
    //this.setDescrizioneArticolo(pr.iDescrizioneArticolo);
    //Fix 4825 - fine

    //PJ fix 03741 INIZIO

    /*
     le quantità:
     se è stato scelto il ricalcolo si ricalcolano tutte le quantità
     tranne una, che è quella di partenza.
     l'unità di partenza è, fra le non nulle, la prima modificata secondo
     questa precedenza: 1) riferimento 2) primaria magazzino 3) secondaria magazzino

     se non è stato scelto il ricalcolo, possono essere immessi in GUI anche
     i valori delle unità di magazzino. non si effettuano ricalcoli a meno
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

    BigDecimal oldQtaR = getQtaDaSpedireInUMRif();
    BigDecimal oldQtaP = getQtaDaSpedireInUMPrm();
    BigDecimal oldQtaS = getQtaDaSpedireInUMSec();

    int roundingMode = BigDecimal.ROUND_HALF_EVEN;
    //int scale = oldQtaR.scale();//Fix 39402
    int scale = Q6Calc.get().scale(2);//Fix 39402
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

    OrdineVenditaRiga rigaOrd = (OrdineVenditaRiga) getRigaOrdine();
    boolean nonFrazionabile = rigaOrd != null && rigaOrd.isRigaNonFrazionabile();
    // Inizio 4670
    boolean isAttivaGestioneQtaIntere = UnitaMisura.isPresentUMQtaIntera(getUMRif(), getUMPrm(), getUMSec(), getArticolo());//Fix 5117
    QuantitaInUMRif qtaCalcolata = new QuantitaInUMRif();
    // Fine 4670


    /* Fix 32165 Inizio commentato
    if (nonFrazionabile) {
      newQtaR = oldQtaR;
      newQtaP = oldQtaP;
      newQtaS = oldQtaS;
    }
    else Fix 32165 Fine*/
      if (ricalcola) {
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
        // fix 5331
        // significa che l'ho annullata
        if (pr.iDaSpedireV!=null && pr.iDaSpedireV.compareTo(new BigDecimal("0"))==0 && Utils.areEqual(UMP,UMR)){
          newQtaP = new BigDecimal("0");
          newQtaS = new BigDecimal("0");
          newQtaR = new BigDecimal("0");
        }
        else {
        // fine fix 5331
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
            // Inizio 4670
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
        // fix 5331
        }
        // fine fix 5331
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
          }else{

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
      newQtaR = GestoreEvasioneVendita.get().getBigDecimalZero();

    if (newQtaP == null)
      newQtaP = GestoreEvasioneVendita.get().getBigDecimalZero();

    if (newQtaS == null)
      newQtaS = GestoreEvasioneVendita.get().getBigDecimalZero();

    //32165 inizio
    if (nonFrazionabile) {
    	if (this.hasControlloAccPrn())
    		getDocEvaVenRiga().setQtaDaSpedireSecondoGiacenza(new QuantitaInUMRif(newQtaP, newQtaS,newQtaR));
        newQtaR = oldQtaR;
        newQtaP = oldQtaP;
        newQtaS = oldQtaS;
    }
    //32165 Fine
    
    getDocEvaVenRiga().setQtaDaSpedire(new QuantitaInUMRif(newQtaP, newQtaS,
        newQtaR));

    if (diversi(oldQtaR, newQtaR) || diversi(oldQtaP, newQtaP) ||
    		diversi(oldQtaS, newQtaS) ||
    		isQtaCambiata()) { //Fix 34827 //Aggiungere la condizione isQtaCambiata dal change qta in evasione
    	
      // aggiorna le qta componenti solo se vi è stata variazione di qta
      aggiornaQtaEvasione(getStatoAvanzamento(), true);
    }

    //PJ fix 03741 FINE

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

    this.getDocEvaVenRiga().logDeepQta("Aggiornamento da griglia");
    // Inizio 4004
    setRicalcoloQtaFattoreConv(ricalcola);
    // Fine 4004

    //Fix 3177 - inizio
    BigDecimal zero = GestoreEvasioneVendita.get().getBigDecimalZero();
    if (isRigaSaldata() &&
        ( (pr.iDaSpedireV != null && pr.iDaSpedireV.equals(zero)) ||
         (pr.iDaSpedireM != null && pr.iDaSpedireM.equals(zero)))) {
      getDocEvaVenRiga().setAbilitaControlloQtaZero(false);
    }
    //Fix 3177 - fine
  }

  /**
   * Forza lo stato di avanzamento sulla riga, sui lotti e sulle sue righe secondarie
   * aggiornamendo le qta e controllando la quadratura lotti
   * @param stato nuovo stato avanzamento
   * @return Lista di ErrorMessage
   */
  public List forzaStatoAvanzamento(char stato, boolean soloEstratte) {
    List errors = new ArrayList();
    //Fix 14133 inizio
    BigDecimal qtaPropEvaInUMRif,qtaPropEvaInUMPrm,qtaPropEvaInUMSec = null;
    qtaPropEvaInUMRif=getQtaPropostaEvasione().getQuantitaInUMRif();
    qtaPropEvaInUMPrm=getQtaPropostaEvasione().getQuantitaInUMPrm();
    qtaPropEvaInUMSec=getQtaPropostaEvasione().getQuantitaInUMSec();
    BigDecimal qtaResidEvaInUMRif,qtaResidEvaInUMPrm,qtaResidEvaInUMSec = null;
    qtaResidEvaInUMRif=getQtaResidua().getQuantitaInUMRif();
    qtaResidEvaInUMPrm=getQtaResidua().getQuantitaInUMPrm();
    qtaResidEvaInUMSec=getQtaResidua().getQuantitaInUMSec();
   // if (this.getStatoAvanzamento() != stato) {
    if (this.getStatoAvanzamento() != stato||(diversi(qtaPropEvaInUMRif,qtaResidEvaInUMRif)||diversi(qtaPropEvaInUMPrm,qtaResidEvaInUMPrm)||diversi(qtaPropEvaInUMSec,qtaResidEvaInUMSec))) {//Fix 14133 AYM fine

      try {
        boolean forza = soloEstratte ? this.isRigaEstratta() : true;
        if (forza) {
          this.aggiornaQtaEvasione(stato, true);
          super.setStatoAvanzamento(stato);
          ErrorMessage em = this.getDocEvaVenRiga().checkQuadraturaLotti();
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
          DocEvaVenRigaSec rigaSec = (DocEvaVenRigaSec) righeSec.next();
          errors.addAll(rigaSec.forzaStatoAvanzamento(stato));
        }
      }
    }
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
        DocEvaVenRigaLottoPrm lotto = (DocEvaVenRigaLottoPrm) iterLotti.next();
        boolean isChanged = false;
        if (this.getOldRiga() != null) {
          isChanged = this.getOldRiga().getServizioQta().compareTo(
              qtaDaSpedireCorrente) != 0;
        }
        // fix 09612 >
        boolean isMantieniLottoDummy = isMantieniLottoDummy();
        if (!isMantieniLottoDummy) {
          isMantieniLottoDummy = !lotto.getIdLotto().equals(Lotto.LOTTO_DUMMY);
        }
        if (isMantieniLottoDummy &&
            !qtaDaSpedireCorrente.isZero()) {

        /*
        if (!lotto.getIdLotto().equals(Lotto.LOTTO_DUMMY) &&
            !qtaDaSpedireCorrente.isZero()) {
        */
        // fix 09612 <
          if (!this.isOnDB() || isChanged) {
            int comp = lotto.getDocEvaVenRigaLotto().getQtaResidua().compareTo(
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
                String msg = "Residuo sul lotto prm '" + lotto.getKey() + "' [" +
                    qtaResiduaLottoCorrente + "] < [" +
                    qtaDaSpedireCorrente + "]";
              //Fix 36762 Inizio
                Map map = ( (DocEvaVen)this.getTestata()).getRigheWarnings();
                if (map != null) {
                	String oldMsg = "";
                    if (map.containsKey(this.getRigaOrdine()))
                      oldMsg = (String)map.get(this.getRigaOrdine()) + "\n";
                    if(!oldMsg.contains("Residuo sul lotto prm")) {
                    	oldMsg += msg;
                    	map.put(this.getRigaOrdine(), oldMsg);
                    }
                  }
                /*
                ( (DocEvaVen)this.getTestata()).getRigheWarnings().put(this.
                        getRigaOrdine(), msg);
                */
                //Fix 36762 Fine
                GestoreEvasioneVendita.get().println(msg);
                qtaResiduaLottoCorrente.setEqual(lotto.getDocEvaVenRigaLotto().
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
        lotto.getDocEvaVenRigaLotto().setAggiornamentoInOrdine(this.
            getDocEvaVenRiga().isAggiornamentoInOrdine());
        lotto.getDocEvaVenRigaLotto().aggiornaQtaEvasione(stato, forzaStato,
            qtaResiduaLottoCorrente);

                //Fix 07727 GSCARTA - inizio
        //if (qtaResiduaLottoCorrente.isZero()) {
        if (this.isLottoEliminabilePerQta(qtaResiduaLottoCorrente, lotto)) {
                //Fix 07727 GSCARTA - fine
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
        DocEvaVenRigaSec rigaSec = (DocEvaVenRigaSec) iterRighe.next();
        //Fix 9745 PM Inizio
        //rigaSec.ricalcoloQuantita(this.getQtaDaSpedireInUMPrm());//Fix 9671 PM
        //rigaSec.ricalcoloQuantita(this);//Fix 9671 PM
        rigaSec.ricalcoloQuantita(this, getDocEvaVenRiga().getQtaDaSpedire());
        //Fix 9745 PM Fine
        //int scale = this.getQtaDaSpedireInUMPrm().scale();//Fix 39402
        int scale = Q6Calc.get().scale(2);//Fix 39402
        //Fix 9745 PM Inizio
        //rigaSec.ricalcoloQuantita(this.getQtaDaSpedireInUMPrm());//Fix 9671 PM
        //rigaSec.ricalcoloQuantita(this);//Fix 9671 PM
        rigaSec.ricalcoloQuantita(this, getDocEvaVenRiga().getQtaDaSpedire());
        //Fix 9745 PM Fine
        BigDecimal qta = rigaSec.getQtaInUMVen();
        if (qta == null) {
          qta = GestoreEvasioneVendita.get().getBigDecimalZero();
        }
        //qta = qta.setScale(scale, roundingMode);//Fix 30871
		qta = Q6Calc.get().setScale(qta,scale, roundingMode);//Fix 30871
        rigaSec.setQtaDaSpedireInUMRif(qta);
        qta = rigaSec.getQtaInUMPrm();
        if (qta == null) {
          qta = GestoreEvasioneVendita.get().getBigDecimalZero();
        }
        //qta = qta.setScale(scale, roundingMode);//Fix 30871
		qta = Q6Calc.get().setScale(qta,scale, roundingMode);//Fix 30871
        rigaSec.setQtaDaSpedireInUMPrm(qta);
        qta = rigaSec.getQtaInUMSec();
        if (qta == null) {
          qta = GestoreEvasioneVendita.get().getBigDecimalZero();
        }
        //qta = qta.setScale(scale, roundingMode);//Fix 30871
		qta = Q6Calc.get().setScale(qta,scale, roundingMode);//Fix 30871
        rigaSec.setQtaDaSpedireInUMSec(qta);
        rigaSec.getDocEvaVenRiga().setAggiornamentoInOrdine(this.
            getDocEvaVenRiga().isAggiornamentoInOrdine());
        rigaSec.aggiornaQtaEvasione(stato, forzaStato);
      }
    }
  }


  // fix 7727 >
  protected boolean isLottoEliminabilePerQta(QuantitaInUMRif  qtaResiduaLottoCorrente, DocEvaVenRigaLottoPrm lotto) {
    boolean isOk = qtaResiduaLottoCorrente.isZero();
    return isOk;
  }
  // fix 7727 <

  /**
   *
   * @return
   * @throws SQLException
   */
  public int delete() throws SQLException {
    int res = ErrorCodes.OK;
    // PAOLA
    if (!isCancellabile()){
      return 0;
    }
    // fine PAOLa
    SQLException ex = null;
    boolean hoFattoTutto = false;
    try {
    // fine PAOLA
    boolean isCollegataAMagazzino = this.isCollegataAMagazzino();
       if (this.isRigaEstratta() && this.isOnDB() && !isCollegataAMagazzino) {
      if (this.getOldRiga() != null) {
        this.setQtaAttesaEvasione(this.getOldRiga().getQtaAttesaEvasione());
        this.setQtaPropostaEvasione(this.getOldRiga().getQtaPropostaEvasione());
      }
      //ini fix 2322
      OrdineVenditaRiga rigaOrd = (OrdineVenditaRiga)this.getRigaOrdine();
      if (rigaOrd != null) {
        rigaOrd.setTipoBloccoLSP(TipoBloccoLSP.NESSUNO);
        rigaOrd.setPropostaSaldoManuale(false);//Fix 22471
      }
      // fix 5500/ 6034
      // questo controllo serve perchè potrei trovarmi nella situazione di dover
      // cancellare un DocEvaVen che precedentemente ho salvato.
      // In questo caso le righe che risultano estratte hanno la salvaRigaOrdCollegata
      // a false, ciò comporta che in fase di aggiornamento della riga ordine la
      // riga non venga salvata e quindi non vengono riportati correttamente
      // gli aggiornamenti di qtà. riportando il salvaRigaOrdCollegata a true, la
      // riga documeno cancellando aggiorna la riga ordine e la riga ordine stessa
      // chiama la save su se stessa.
      if (this.isRigaEstratta() && !this.getSalvaRigaOrdCollegata()){
        this.setSalvaRigaOrdCollegata(true);
      }
      // fine fix 5500/ 6034
      //fine fix 2322
      res = super.delete();
      // fix 5277
      // PAOLA
      //if (this.getServizioQta().isZero() && rigaOrd!=null){
      if (this.getServizioQta().isZero() && rigaOrd!=null && res>=0){
      // fine PAOLA
        boolean perEvasioneLSP = ( (DocEvaVen)this.getTestata()).
            isEvasioneDaLSP();
        if (perEvasioneLSP){
          rigaOrd.setForzaDisattivaGestioneIntercompany(true); // fix 13893
          int reo = rigaOrd.save();
          if (reo<0)
            res = reo;
          else
            res = res + reo;
          rigaOrd.setForzaDisattivaGestioneIntercompany(false);  // fx 13893
        }
      }
      // fine fix 5277
      if (res >= 0) {
        if (GestoreEvasioneVendita.get().isAbilitaControlloConcorrenza()) {
          if (this.getRigaOrdine() != null) {
            //ini Fix 1922
            this.getRigaOrdine().getTestata().disabilitaTask(DocEvaVen.
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
        GestoreEvasioneVendita.get().println(message);
        if (this.getRigaOrdine() != null) {
          ( (DocEvaVen)this.getTestata()).getRigheWarnings().put(this.
              getRigaOrdine(), message);
        }
      }
    }
    hoFattoTutto = true;
  }
  catch (SQLException t) {
    t.printStackTrace(Trace.excStream);
    ex = t;
  }
  finally {
    boolean isAbilitaCommitRiga = ((DocEvaVen)this.getTestata()).
        isAbilitaCommitRigaDaProposta();
    boolean rilancioLeccezione = true;
    if (isAbilitaCommitRiga){
      if (res >= 0 && ex ==null && hoFattoTutto) {
        ConnectionManager.commit();
      }
      else {
        aggiornaLaLista(res, ex);
        rilancioLeccezione = false;
        res =0;
        ConnectionManager.rollback();
      }
    }
    if (ex!=null && rilancioLeccezione){
      throw ex;
    }
  }
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

  //ini fix 2218
  public int saveRigaOrdine(OrdineVenditaRiga rigaOrd) throws SQLException {
    int res = ErrorCodes.OK;
    if (rigaOrd != null) {
      try {
        // viene settato questo flag in quanto se attiva la gestione intercompany
        // deve partire in questa fase di salvataggio della riga ordine
        // in quanto sono in una fase di evasione della stessa
        // le modifiche che questa operazione potrebbe comportare non
        // sono determinanti al fine dell'intercompany
        rigaOrd.setForzaDisattivaGestioneIntercompany(true);  // Fix 13893 PM
//Fix 3331 BP ini...
        rigaOrd.setTipoBloccoLSP( ( (DocEvaVen)this.getTestata()).
                                 getTipoBloccoLSP());
//Fix 3331 BP fine.

        // Fix 4445 DB per evitare il lok ottimistico sulla testata
        // d'altra parte mi sembra superfluo salvare la testata.
        //rigaOrd.setSalvaTestata(false);
        // Fine fix 4445 DB
        boolean aggiornaRigaOff = rigaOrd.isAggiornaRigaOfferta();//Fix 34464
        rigaOrd.setAggiornaRigaOfferta(false);//Fix 34464
        res = rigaOrd.save(); // fix 3134
        rigaOrd.setAggiornaRigaOfferta(aggiornaRigaOff);//Fix 34464
        // Inizio 5855
        if (rigaOrd.iMovimPortafoglioEvaDaCancellare != null){
          iGestoreSaldi.applicaMovimentiPortafoglio(rigaOrd.iMovimPortafoglioEvaDaCancellare);
        }
        // Fine 5855
        GestoreEvasioneVendita.get().println(
            "tento la save() di riga ordine prm :'" +
            rigaOrd.getKey() + "'");

      }
      catch (Throwable t) {
        t.printStackTrace(Trace.excStream);
        // FEDERICO
        res = -100;
        // fine FEDERICO
      }
      // Fix 13893 PM
      finally
      {
        if (rigaOrd != null)
          rigaOrd.setForzaDisattivaGestioneIntercompany(false);
      }
      // Fine fix 13893 PM
    }
    return res;
  }

  //fine fix 2218

  //Fix 31850 inizio
  /*
   * eliminaRigheSecConQtaZero :
   * Permete di eliminare le righe sec con qta zero, questa situazione viene solo in caso di riga sec con qta totale (isBloccoRicalcoloQtaComp() == true) e con giacenza in mag zero
   * per evitare di salvare la riga con qta 0
   * Infatti, in questa situazione, non possiamo bloccare l'evasione altrimente l'utente sara bloccato
   */
  public void eliminaRigheSecConQtaZero()
  {
	  
      //Fix 33874 inizio 
      char tpBloc = ((DocEvaVen)this.getTestata()).getTipoBloccoLSP();
      if (tpBloc==TipoBloccoLSP.IMPEGNATO)
            return;

      
      if (isOnDB() && !((DocEvaVen)this.getTestata()).isSalvaDaProposte())
            return;
      //Fix 33874 Fine
      Iterator iter = this.getRigheSecondarie().iterator();
      BigDecimal zero = new BigDecimal("0"); 
      while (iter.hasNext()) {
        DocEvaVenRigaSec rcorr = (DocEvaVenRigaSec)iter.next();
        if ( rcorr.getStatoAvanzamento()== StatoAvanzamento.PROVVISORIO && (rcorr.getQtaPropostaEvasione().getQuantitaInUMPrm() == null || rcorr.getQtaPropostaEvasione().getQuantitaInUMPrm().compareTo(zero) == 0))
        	iter.remove();
        if ( rcorr.getStatoAvanzamento()== StatoAvanzamento.DEFINITIVO && (rcorr.getQtaAttesaEvasione().getQuantitaInUMPrm() == null || rcorr.getQtaAttesaEvasione().getQuantitaInUMPrm().compareTo(zero) == 0))
        	iter.remove();
      }

  }
  //Fix 31850 Fine
  //ini FIX 1699
  /**
   *
   * @return
   * @throws SQLException
   */
  public int save() throws SQLException {
	 //Fix 31850 inizio
	 //if (!isOnDB())//Fix 33874
		 eliminaRigheSecConQtaZero();
	//Fix 31850 Fine
    // fix 11779 >
  	if (this.getArticolo().hasVersioneEstesa() && this.getRigaOrdine() != null) {
	  	BigDecimal numeroImballo = this.getNumeroImballo();
	  	BigDecimal numeroImballoOrd = this.getRigaOrdine().getNumeroImballo();
	  	if (numeroImballo != null && numeroImballoOrd != null && numeroImballo.compareTo(numeroImballoOrd) != 0) {
	  		this.setProvenienzaPrezzo(TipoRigaRicerca.MANUALE);
	  	}
  	}
  	// fix 11779 <

  	// PAOLA
    Timestamp timestampOldTestataOrd = null;
    // fine PAOLA
    //Fix 2844 - inizio
    //Fix 3331 BP ini...
    if (this.isInConvalida()) {
      return super.save();
    }
    //Fix 3331 BP fine.
    if (!isOnDB()) {
      this.creazioneAutomaticaRigaContenitore();
    }
    //Fix 2844 - fine
    int res = ErrorCodes.GENERIC_ERROR;
    ThipException ex = null;
    boolean oldIsOnDB = isOnDB();
    Timestamp oldTimestamp = this.getTimestamp();
    Integer oldNum = this.getNumeroRigaDocumento();
    boolean salvaRiga = this.getDocEvaVenRiga().isRigaEstratta();
    //ini Fix 2218
    boolean salvaSoloRigaOrd = false;
    //fine Fix 2218
    try {
      if (salvaRiga) {
        ErrorMessage em = this.checkRigaModificata();
        if (em != null) {
          salvaRiga = false;
          throw new ThipException(em);
        }
      }
      if ( ( (DocEvaVen)this.getTestata()).getRigaDaSalvareSingolarmente() != null) {
        salvaRiga = ( (DocEvaVen)this.getTestata()).
            getRigaDaSalvareSingolarmente().getKey().equals(this.getKey());
      }
      if (salvaRiga && !this.isCollegataAMagazzino()) {
        OrdineVenditaRiga rigaOrd = (OrdineVenditaRiga)this.getRigaOrdine();
        if (rigaOrd != null) {
          //ini fix 2322
          OrdineVendita ord = (OrdineVendita) rigaOrd.getTestata();
          char oldTipoEvasione = ord.getTipoEvasioneOrdine();
          // Inizio 3785
          boolean isOk = true; //ord.retrieve(rigaOrd.getLockType());
          // Fine 3785
          ord.setTipoEvasioneOrdine(oldTipoEvasione);
          GestoreEvasioneVendita.get().println(
              "Retrieve della testata ordine : '" + rigaOrd.getTestata().getKey() +
              "' res = " + isOk);
          //fine fix 2322
        }
        int numRiga = getNumeroRigaDocumento().intValue();
        /** @todo NUM RIGA NEG */
        if (numRiga < 0) {
          oldNum = new Integer(0 - numRiga);
          setNumeroRigaDocumento(oldNum);
        }

        // Federico Crosa fix 3132: inserita riga sottostante e modificata la condizione di if
        boolean daEvaInterattiva = rigaOrd != null &&
            rigaOrd.getTipoBloccoLSP() == TipoBloccoLSP.NESSUNO;
        // Fine fix 3132
        // fix 5277
        boolean perEvasioneLSP = false;
        if (!daEvaInterattiva){
          perEvasioneLSP = ((DocEvaVen)this.getTestata()).isEvasioneDaLSP();
        }
        // fine fix 5277

        //ini fix 2218 // fin 2322
//        if (this.getQtaDaSpedireInUMRif().compareTo(GestoreEvasioneVendita.get().getBigDecimalZero()) > (daEvasione? 0 : -1)) {
        // fix 5277
        /*
        if ( (this.getQtaDaSpedireInUMRif().compareTo(GestoreEvasioneVendita.
            get().getBigDecimalZero()) > 0 && daEvaInterattiva) ||
            (this.getQtaDaSpedireInUMRif().compareTo(GestoreEvasioneVendita.get().
            getBigDecimalZero()) >= 0 && !daEvaInterattiva)) {
        */
        if ( (this.getQtaDaSpedireInUMRif().compareTo(GestoreEvasioneVendita.
            get().getBigDecimalZero()) > 0 && daEvaInterattiva) ||
            (this.getQtaDaSpedireInUMRif().compareTo(GestoreEvasioneVendita.get().
            getBigDecimalZero()) >= 0 && !daEvaInterattiva && (!perEvasioneLSP || !this.isRigaSaldata())) ||
            (this.getQtaDaSpedireInUMRif().compareTo(GestoreEvasioneVendita.get().
            getBigDecimalZero()) > 0 && !daEvaInterattiva && perEvasioneLSP)) {
        // fine fix 5277
          GestoreEvasioneVendita.get().println("tento la save() di riga prm :'" +
                                               this.getKey() + " su articolo: " +
                                               this.getArticoloKey() +
                                               " timestamp = " + oldTimestamp);
          int resRO = ErrorCodes.OK;
          /** @todo GSCARTA fix 4298 !!!!!! */
          /*
          if (rigaOrd != null) { // fix 3134
            if (rigaOrd.isSaldoManuale()) {
              this.setRigaSaldata(false);
              rigaOrd.setSaldoManuale(false);
            }
            // Inizio 3785
            //resRO = this.saveRigaOrdine(rigaOrd);
          }
          */
          setSalvaRigaOrdCollegata(false);

          //Fix 6110 - inizio
          DocEvaVen testata = (DocEvaVen)getTestata();
          if (!testata.getMatricoleOrdine().isEmpty()) {
             testata.aggiornaMatricoleOrdineNumRigaPrm(
                new Integer(numRiga), getNumeroRigaDocumento()
             );
          }
          //Fix 6110 - fine
          // Inizio 6944
          if (!verificaGiacenzaLotto() && getStatoAvanzamento() == StatoAvanzamento.DEFINITIVO){
            sistemaQuantitaDopoVariazioneStatoAv(StatoAvanzamento.PROVVISORIO);
          }
          // Fine 6944
          //Fix 33992 Inizio
          if(this.getArticolo() != null && this.getArticolo().getArticoloDatiVendita().isGestOriginePref())
        	  this.setGestOriginePref(true);
          //Fix 33992 Fine

         //Fix 36048 inizio
          if(this.getArticolo() != null && this.getArticolo().getArticoloDatiVendita().getNazioneOrgPref() != null)
        	  this.setNazioneOrgPref(this.getArticolo().getArticoloDatiVendita().getNazioneOrgPref());
          //Fix 36048 fine

          res = super.save();
          if (res >= 0) {
            // PAOLA
            // fix 10556
            //timestampOldTestataOrd = rigaOrd.getTestata().getTimestamp();
            if (rigaOrd!=null){
              timestampOldTestataOrd = rigaOrd.getTestata().getTimestamp();
            }
            // fine fix 10556
            // fine PAOLA
            resRO = saveRigaOrdine(rigaOrd);
            // FEDERICO
            if (resRO<0){
              res = resRO;
            }
            else {
              res = res + resRO;
            }
            // fine FEDERICO
          }
          // Fine 3785

        }
        else {
          if (daEvaInterattiva || perEvasioneLSP) { //fix 2322 // fix 3132
            // questa serve perchè deve togliere la proposta saldo
            // nel caso della proposta evasione se sono entrato qui sono proprio in fase
            // di evasione e quindi lo saldo.
            if (perEvasioneLSP)
              rigaOrd.setPropostaSaldoManuale(false);
            salvaSoloRigaOrd = true;
            this.setRigaSaldata(true);
            rigaOrd.setSaldoManuale(true);
            // PAOLA
            //fix 10556
            //timestampOldTestataOrd = rigaOrd.getTestata().getTimestamp();
            if (rigaOrd!=null){
              timestampOldTestataOrd = rigaOrd.getTestata().getTimestamp();
            }
            // fine fix 10556
            // fine PAOLA
            res = this.saveRigaOrdine(rigaOrd);
          }
        }
        //fine fix 2218
        
        aggiornaAssegnazioniLotti(); //35639
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
      if (! (t instanceof ThipException)) {
        ex = new ThipException(t.getMessage());
        ex.setErrorMessage(new ErrorMessage("THIP_BS000"));
      }
      else {
        ex = (ThipException) t;
      }
    }
    finally {
      ( (DocEvaVen)this.getTestata()).setRigaDaSalvareSingolarmente(null);
      if (salvaRiga && !salvaSoloRigaOrd) { //Fix 2218
        String tipoRiga = "primaria";
        if (this.getSpecializzazioneRiga() != RIGA_PRIMARIA) {
          tipoRiga = "secondaria";
        }
        GestoreEvasioneVendita.get().println("save() di riga " + tipoRiga +
                                             ":'" +
                                             this.getKey() + "' -> " + res +
                                             " timestamp = " +
                                             this.getTimestamp());
      }
      boolean isAbilitaCommitRiga = ( (DocEvaVen)this.getTestata()).
          isAbilitaCommitRiga();
      // PAOLA
      boolean isAbilitaCommitRigaDaProposta = ( (DocEvaVen)this.getTestata()).
          isAbilitaCommitRigaDaProposta();
      // fine PAOLA
      if (res < 0 || ex != null) {
        // PAOLA
        if (isAbilitaCommitRigaDaProposta) {
            aggiornaLaLista(res, ex);
            res =0;
            ConnectionManager.rollback();
            if (timestampOldTestataOrd!=null &&
                this.getRigaOrdine()!=null &&
                this.getRigaOrdine().getTestata()!=null){
              Timestamp timestampNew = this.getRigaOrdine().getTestata().getTimestamp();
              if (!timestampOldTestataOrd.equals(timestampNew)){
                OrdineVendita testata = (OrdineVendita)this.getRigaOrdine().getTestata();
                boolean b = testata.retrieve();
              }
            }
        }
        else {
        // fine PAOLA
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
            //ini Fix 2218
            if (salvaSoloRigaOrd) {
              rifRigaOrd +=
                  " - Errore nel salvataggio della sola riga ordine per saldo manuale - ";
            }
            //fine Fix 2218
            GestoreEvasioneVendita.get().println(message);
            ( (DocEvaVen)this.getTestata()).getRigheNonSalvate().put(this.
                getRigaOrdine(), message);
            // ini FIX 1684
            this.setRigaEstratta(false);
            // fine FIX 1684
            res = ErrorCodes.NO_ROWS_UPDATED;
            ConnectionManager.rollback();
          }
          // Fix 4718 ini
          /*
                  else {
                    if (ex != null) {
                      throw ex;
                    }
                  }
           */
          if (ex != null) {
            throw ex;

          }
        // PAOLA
        }
        // fine PAOLA
        // Fix 4718 fin
      }
      else {
        //fix 2322
        copiaValoriInOldRiga();
        // PAOLA
        //if (isAbilitaCommitRiga) {
        if (isAbilitaCommitRiga || isAbilitaCommitRigaDaProposta) {
        // fine PAOLA
          this.getDocEvaVenRiga().setAggiornamentoInOrdine(false);
          DocEvaVen docTes = (DocEvaVen)this.getTestata();
          ConnectionManager.commit();
          docTes.setAlmenoUnaRigaCommittata(true);
          //ini fix 2322
          //fix 7220 inizio
          /*
          il metodo fa accesso ai saldi per fare un ouput non sempre necessario!!!!!!!
          List ctrlDisp = this.getLivelloControlloDisp();
          GestoreEvasioneVendita.get().println(" Dopo commit riga doc: " +
                                               this.getKey() + " ctrlDisp : " +
                                               ctrlDisp.get(0) + ", " +
                                               ctrlDisp.get(1));
          */
          //fix 7220 fine
          //fine fix 2322
        }
      }
    }
    return res;
  }
  
//35639 inizio
 public void aggiornaAssegnazioniLotti() {
	 DocEvaVen docVen = (DocEvaVen)this.getTestata();
	 if(docVen.getGestoreAssegnazioneLotti() != null) {
		 Magazzino magazzinoSec = getMagazzino();
		 
		 //Fix 35639 Inizo
		 if(magazzinoSec != null)
		 {
		 //Fix 35639 Fine
			 boolean qtaUscitaGiaInGiacenza = magazzinoSec.getDspQtaPrpSpeRil();
			 boolean daStornoQtaLottiRiga = true;
	
			 if (getStatoAvanzamento() == StatoAvanzamento.PROVVISORIO && 
					 !qtaUscitaGiaInGiacenza)
				 daStornoQtaLottiRiga =  false;
	      
			 if(!isAbilitatoAggiornamentoSaldi())
				 daStornoQtaLottiRiga =  false;
	      
			 if(daStornoQtaLottiRiga) {
				 Iterator lottiRigSec = getRigheLotto().iterator();
				 while (lottiRigSec.hasNext()) {
					 DocumentoVenRigaLottoPrm lottoPrm = (DocumentoVenRigaLottoPrm)lottiRigSec.next();
	 				 String keySaldo = docVen.getKeySaldo(lottoPrm);
			        	   
	 				 BigDecimal qtaPrm = lottoPrm.getServizioQta().getQuantitaInUMPrm();
	 				 if(keySaldo != null) {
	 					 docVen.getGestoreAssegnazioneLotti().stornoQtaLotto(keySaldo, qtaPrm);
	 					 docVen.getGestoreAssegnazioneLotti().stornoQtaLottoEstratta(keySaldo, qtaPrm);
	 				 }
				 } 
			 }
		 }//Fix 35639 inizio
		 aggiornaAssegnazioniRigheSec();
	 }
 }
 
 public void aggiornaAssegnazioniRigheSec() {
	 DocEvaVen docVen = (DocEvaVen)this.getTestata();
	 if(docVen.getGestoreAssegnazioneLotti() != null) {
		 List righeSec = getRigheSecondarie();
		 for (int i = 0; i < righeSec.size(); i++) {
			 DocEvaVenRigaSec rigaSec = (DocEvaVenRigaSec) righeSec.get(i);
          
			 Magazzino magazzinoSec = rigaSec.getMagazzino();
			 //Fix 36896 inizio
			 if(magazzinoSec == null)
				 continue;
			 //Fix 36896 fine
			 boolean qtaUscitaGiaInGiacenzaSec = magazzinoSec.getDspQtaPrpSpeRil();
			 boolean daStornoQtaLottiRigaSec = true;

			 if (rigaSec.getStatoAvanzamento() == StatoAvanzamento.PROVVISORIO && 
					 !qtaUscitaGiaInGiacenzaSec)
				 daStornoQtaLottiRigaSec =  false;
          
			 if(!rigaSec.isAbilitatoAggiornamentoSaldi())
				 daStornoQtaLottiRigaSec =  false;
          
			 if(daStornoQtaLottiRigaSec) {
				 Iterator lottiRigSec = rigaSec.getRigheLotto().iterator();
				 while (lottiRigSec.hasNext()) {
					 DocumentoVenRigaLottoSec lottoSec = (DocumentoVenRigaLottoSec)lottiRigSec.next();
	 				 String keySaldo = docVen.getKeySaldo(lottoSec);
			        	   
	 				 BigDecimal qtaPrm = lottoSec.getServizioQta().getQuantitaInUMPrm();
	 				 if(keySaldo != null) {
	 					 docVen.getGestoreAssegnazioneLotti().stornoQtaLotto(keySaldo, qtaPrm);
	 					 docVen.getGestoreAssegnazioneLotti().stornoQtaLottoEstratta(keySaldo, qtaPrm);
	 				 }
				 } 
			 }
		 }
	 } 
 }
 // 35639 fine

  //fine FIX 1699
  //Fix 3331 BP ini...
    public boolean isInConvalida() {
      return ( (DocEvaVen)this.getTestata()).isInConvalida();
    }

//Fix 3331 BP fine.

  /**
   * Ridefinisce il metodo per evitare la gestione delle righe omaggio perchè sono
   * già gestite dalla riga ordine
   * @param testata
   * @param rc
   * @return
   * @throws SQLException
   */
  protected int gestioneRigheOmaggio(DocumentoVendita testata, int rc) throws
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
   * Instanzia un lotto dummy di classe erede di DocEvaVenRigaLotto
   * @return
   */
  protected DocumentoRigaLotto creaLottoDummy() {
    DocEvaVenRigaLottoPrm lottoD;
    lottoD = (DocEvaVenRigaLottoPrm) Factory.createObject(DocEvaVenRigaLottoPrm.class);
    lottoD.setFather(this);
    lottoD.setIdAzienda(getIdAzienda());
    lottoD.setIdLotto(LOTTO_DUMMY);
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
    if (GestoreEvasioneVendita.get().isAbilitaControlloConcorrenza()) {
      if (this.getRigaOrdine() != null) {
        if (GestoreEvasioneVendita.get().isAbilitaControlloConcorrenza()) {
          //ini Fix 1922
          //this.getRigaOrdine().disabilitaTask(DocEvaVen.TASK_ID_ESTRAZIONE);
          //fine Fix 1922
        }
      }
    }
  }

  /**
   * Ridefinito
   * @param riga
   * @return
   */
  
  // fix 32769
  public static final String STM_COL_MAG = "SELECT " + DocumentoVenRigaPrmTM.COL_MAGAZZINO + 
		  " FROM " + DocumentoVenRigaPrmTM.TABLE_NAME + 
  			" WHERE " + DocumentoVenRigaPrmTM.ID_AZIENDA + " = ? " +
          " AND " + DocumentoVenRigaPrmTM.ID_ANNO_DOC + " = ? " +
          " AND " + DocumentoVenRigaPrmTM.ID_NUMERO_DOC + " = ? " +
          " AND " + DocumentoVenRigaPrmTM.ID_RIGA_DOC + " = ? ";
  
  public static final CachedStatement CS_COL_MAG = new CachedStatement (STM_COL_MAG);
  
  public static synchronized String dammiColleatoAMagazzino(String idAzienda, String anno, String numero, Integer riga) throws SQLException {
	  ResultSet rs = null;
	  String ret = null;
 	  try {
	      PreparedStatement ps = CS_COL_MAG.getStatement();
	      Database db = ConnectionManager.getCurrentDatabase();
	      db.setString(ps, 1, idAzienda);
	      db.setString(ps, 2, anno);
	      db.setString(ps, 3, numero);
	      ps.setInt(4, riga.intValue());
	      rs = ps.executeQuery();
	      while (rs.next()) {
	    	  ret = rs.getString("COL_MAGAZZINO");
	      }
		  
	  }
	  finally {
                if (rs != null) {
                  rs.close();
                }
		  
	  }
	  return ret;
  }
  // fine fix 32769
  public boolean isCollegataAMagazzino() {
    //Fix 3331 BP ini...
        boolean isOk = false;
        isOk = super.isCollegataAMagazzino();
        if (this.isOnDB() && !isOk) {
        	// fix 32769
        	try {
        	String strColMag = dammiColleatoAMagazzino(this.getIdAzienda(), this.getAnnoDocumento(),this.getNumeroDocumento(), this.getNumeroRigaDocumento());
            if (strColMag != null) {
                this.setCollegatoAMagazzino(strColMag.charAt(0));
                isOk = super.isCollegataAMagazzino();
              }
        	}
            catch (SQLException sql1) {
                sql1.printStackTrace(Trace.excStream);
            }
        	/*
          String select = "SELECT * FROM " + DocumentoVenRigaPrmTM.TABLE_NAME;
          String where = " WHERE " +
              " " + DocumentoVenRigaPrmTM.ID_AZIENDA + " = '" + this.getIdAzienda() +
              "' " +
              " AND " + DocumentoVenRigaPrmTM.ID_ANNO_DOC + " = '" +
              this.getAnnoDocumento() + "' " +
              " AND " + DocumentoVenRigaPrmTM.ID_NUMERO_DOC + " = '" +
              this.getNumeroDocumento() + "' " +
              " AND " + DocumentoVenRigaPrmTM.ID_RIGA_DOC + " = " +
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
          */
        	// fine fix 32769
        }
        return isOk;

        /*
    boolean isOk = false;
    ThipException thipException = null;
    String strError = "";
    isOk = super.isCollegataAMagazzino();
    if (this.isOnDB() && !isOk) {
      DocumentoVenRigaPrm rigaTmp = (DocumentoVenRigaPrm) Factory.createObject(
          DocumentoVenRigaPrm.class);
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
    }
    return isOk;
*/
//Fix 3331 BP fine.
  }

  // per la UI ---v
  public String getRifRigaOrdineFormattato() {
    return this.getDocEvaVenRiga().getRifRigaOrdineFormattato();
  }

  public QuantitaInUMRif getQtaOrdinata() {
    return this.getDocEvaVenRiga().getQtaOrdinata();
  }

  public QuantitaInUMRif getQtaResidua() {
    return this.getDocEvaVenRiga().getQtaResidua();
  }

  public boolean isRigaEstratta() {
    boolean isOk = this.getDocEvaVenRiga().isRigaEstratta();
    DocEvaVen doc = (DocEvaVen)this.getTestata();
    if (doc != null && doc.getTipoBloccoLSP() == TipoBloccoLSP.NESSUNO) {
      isOk = isOk || this.isOnDB();
    }
    return isOk;
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
//Fix 3331 BP ini...
//    return this.getDocEvaVenRiga().getQtaDaSpedire().getQuantitaInUMRif();
    return this.getObjQtaDaSpedireInUMRif().getQuantitaInUMRif();
//Fix 3331 BP fine.
  }
//Fix 3331 BP ini...
  public QuantitaInUMRif getObjQtaDaSpedireInUMRif() {
    return this.getDocEvaVenRiga().getQtaDaSpedire();
  }
//Fix 3331 BP fine.
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

  public void creaRigaOmaggio(DocumentoVendita testata) throws SQLException {
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

  //ini fix 2322
  /*
    public BigDecimal getGiacenzaNetta() {
      BigDecimal giacenzaNettaInUMPrm = GestoreEvasioneVendita.get().getBigDecimalZero();
   if (this.getArticolo().getTipoParte() == ArticoloDatiIdent.KIT_NON_GEST) {
        // calcola quella min in base alla max tra i componenti con coeff. max
        // trova rigasec con giacenza min.
        DocEvaVenRigaSec rigaSecRif = null;
        Iterator iter = this.getRigheSecondarie().iterator();
        while (iter.hasNext()) {
          DocEvaVenRigaSec rcorr = (DocEvaVenRigaSec)iter.next();
          if (rigaSecRif == null) {
            rigaSecRif = rcorr;
          }
          else {
            if (rcorr.getDocEvaVenRiga().getGiacenzaNettaDaSaldi().
   compareTo(rigaSecRif.getDocEvaVenRiga().getGiacenzaNettaDaSaldi()) < 0) {
              rigaSecRif = rcorr;
            }
          }
        }
        giacenzaNettaInUMPrm = calcolaGiacenzaNetta(rigaSecRif);
      }
      else {
   giacenzaNettaInUMPrm = this.getDocEvaVenRiga().getGiacenzaNettaDaSaldi();
      }
      return giacenzaNettaInUMPrm;
    }
   */
  /**
   * Calcola la giacenza in base a quella della riga sec minima.
   * @param rigaSecRif DocEvaVenRigaSec
   * @return BigDecimal
   */
  /*
    private BigDecimal calcolaGiacenzaNetta(DocEvaVenRigaSec rigaSecRif) {
   BigDecimal qtaCalcolata = GestoreEvasioneVendita.get().getBigDecimalZero();
      Iterator iter = this.getRigheSecondarie().iterator();
      while (iter.hasNext()) {
        DocEvaVenRigaSec rcorr = (DocEvaVenRigaSec)iter.next();
        if (rcorr != rigaSecRif) {
          if (rigaSecRif == null) {
            rigaSecRif = rcorr;
          }
          else {
            // @todo VALUTARE SE usare DIV intera
            BigDecimal qtaMinPrpRif = rigaSecRif.getQtaDaSpedireInUMPrm().
                divide(rigaSecRif.getCoefficienteImpiego(), rigaSecRif.getCoefficienteImpiego().scale());
   BigDecimal qtaPrpCorr = qtaMinPrpRif.multiply(rcorr.getCoefficienteImpiego());
            if (qtaPrpCorr.compareTo(rcorr.getGiacenzaNetta()) < 0) {
              qtaCalcolata = calcolaGiacenzaNetta(rcorr);
            }
            else {
              qtaCalcolata = rcorr.getGiacenzaNetta();
            }
          }
        }
      }
      return qtaCalcolata;
    }
   */

  //...FIX 3187 inizio
  // Inizio 5798
/*
  public boolean controllaLottiPrm() {

    boolean statoDef = getStatoAvanzamento() == StatoAvanzamento.DEFINITIVO ? true : false;
    //...Eseguo il controllo solo per le righe che non sono onDB
    //...e che hanno un articolo gestito a lotti
    if (!isOnDB() && getArticolo().getArticoloDatiMagaz().isArticLotto()) {

      //...Controllo se sono in un documento non di reso
      char tipoDoc = ( (DocumentoVendita)this.getTestata()).
          getTipoDocVenPerGestMM();
      if (tipoDoc == DocumentoVendita.TD_VENDITA ||
          tipoDoc == DocumentoVendita.TD_SPE_CTO_TRASF) {
        //...Controllo che la creazione automatica sia impostata
        boolean ok = identificaLotto();
        if (ok)
          statoDef = controllaQtaLotti(PersDatiMagazzino.TIPO_VEN,
                                       ProposizioneAutLotto.CREA_DA_DOCUMENTO,
                                       getIdMagazzino());
      }

      //...Controllo tutte le righe secondarie
      List righeSec = getRigheSecondarie();
      boolean allRigheSecOk = true;
      for (int i = 0; i < righeSec.size(); i++) {
        DocEvaVenRigaSec docEvaRigaSec = (DocEvaVenRigaSec) righeSec.get(i);
        boolean rigaSecOk = docEvaRigaSec.controllaLottiSec();
        if (!rigaSecOk) {
          allRigheSecOk = false;
          break;
        }
      }

      if (!allRigheSecOk)
        statoDef = false;
    }
    return statoDef;
  }
*/
  public boolean controllaLottiPrm() {

     boolean statoDef = getStatoAvanzamento() == StatoAvanzamento.DEFINITIVO ? true : false;
     // Inizio 5798
     // Se sulla riga è stato forzato lo stato Avanzamento a definitivo, eseguo la check
     // con questo stato.
     if (isStatoAvanzamentoDef())
        statoDef = true;
     // Fine 5798
     //...Eseguo il controllo solo per le righe che non sono onDB
     //...e che hanno un articolo gestito a lotti
     if (!isOnDB() && getArticolo().getArticoloDatiMagaz().isArticLotto()) {
        //...Controllo se sono in un documento non di reso
        char tipoDoc = ( (DocumentoVendita)this.getTestata()).
        getTipoDocVenPerGestMM();
        if (tipoDoc == DocumentoVendita.TD_VENDITA ||
              tipoDoc == DocumentoVendita.TD_SPE_CTO_TRASF) {
           //...Controllo che la creazione automatica sia impostata
           boolean ok = identificaLotto();
           if (ok)
              statoDef = controllaQtaLotti(PersDatiMagazzino.TIPO_VEN,
                    ProposizioneAutLotto.CREA_DA_DOCUMENTO,
                    getIdMagazzino());
        }
     }
     // Inizio 5798
     if (statoDef){
        //...Controllo tutte le righe secondarie
        List righeSec = getRigheSecondarie();
        boolean allRigheSecOk = true;
        for (int i = 0; i < righeSec.size(); i++) {
           DocEvaVenRigaSec docEvaRigaSec = (DocEvaVenRigaSec) righeSec.get(i);
           if (!isOnDB() && docEvaRigaSec.getArticolo().getArticoloDatiMagaz().isArticLotto()) {
              boolean rigaSecOk = docEvaRigaSec.controllaLottiSec();
           if (!rigaSecOk) {
             allRigheSecOk = false;
           }
           if (rigaSecOk) {
                docEvaRigaSec.sistemaQuantitaDopoVariazioneStatoAv(StatoAvanzamento.DEFINITIVO);
           }
           else
              docEvaRigaSec.sistemaQuantitaDopoVariazioneStatoAv(StatoAvanzamento.PROVVISORIO);
           }
        }
      if (!allRigheSecOk){
         statoDef = false;
         if (getStatoAvanzamento() == StatoAvanzamento.DEFINITIVO){
            setStatoAvanzamento(StatoAvanzamento.PROVVISORIO);
            try{
            	if (!isInDettaglio()) //Fix 38596 in caso di isInDettaglio la qta è già sistemato nel metodo setStatoAvanzamento appena chiamata
            		getQtaPropostaEvasione().setEqual(getQtaAttesaEvasione());
            }catch(CopyException ex){
               ex.printStackTrace(Trace.excStream);
            }
         }
      }
     }
    return statoDef;
  }
  // Fine 5798

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
    if (getRigaOrdine() != null) {
      List lottiRig = getRigaOrdine().getRigheLotto();
      for (int i = 0; i < lottiRig.size(); i++) {
        OrdineVenditaRigaLotto lt = (OrdineVenditaRigaLotto) lottiRig.get(i);
        if (!lt.getIdLotto().equals(Lotto.LOTTO_DUMMY)) {
          lottiOrig.add(lt.getLotto());
          //lottiOrdine.add(lt); //35639
        }
        else {
          // Inizio 3921
          //qta = lt.getQuantitaResiduoConSegno().getQuantitaInUMPrm();
          qta = getQtaDaSpedireInUMPrm();
          // Fine 3921
          esisteLottoDummy = true;
        }
      }
      lottiOrdine = getImpegniLottiOrdine(false); //35639
    }

    // Inizio 5798
    if (qta.compareTo(new BigDecimal(0)) == 0 && !esisteLottoDummy){
      //qta = getQtaResidua().getQuantitaInUMPrm();
       qta = getQtaDaSpedireInUMPrm();
    }
    // Fine 5798

    if (qta.compareTo(new BigDecimal(0)) > 0) {
    	//35639 inizio
        /*ProposizioneAutLotto pal = ProposizioneAutLotto.creaProposizioneAutLotto(*/
    	ProposizioneAutLotto pal = getProposizioneAutLotto();
    	List lottiAuto = new ArrayList();
    	
    	if(pal == null) {
    		pal = creaProposizioneAutLotto();
    	//35639
    		pal.inizializzaProposizioneAutLotto(
    				tipo,
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
        
      
      //BigDecimal qtaLotti = new BigDecimal("0"); //35639
    	BigDecimal qtaLotti = pal.getQtaLottiProposate();  //35639
    	//List saldiRigaCorrente = getSaldiKeysRigaCorrente(); //35639
    	
    	if(qtaLotti == null) { //35639
    		qtaLotti = new BigDecimal("0");
    		//...Se è stato creato un lotto automatico genero una riga lotto con quel lotto
    		if (lottiAuto != null && !lottiAuto.isEmpty()) {
    			BigDecimal qtaRichiesta = qta;  //35639
    			for (int j = 0; j < lottiAuto.size(); j++) {
    				LottiSaldi lt = (LottiSaldi) lottiAuto.get(j);
    				// Inizio 5798
    				//qtaLotti = qtaLotti.add(pal.calcolaQtaDisponibileLotto(tipo, lt,
    				//    !lottiOrig.isEmpty(), lottiOrdine));
    				// Inizio 6965
    				//35639 inizio
    				/*qtaLotti = qtaLotti.add(ProposizioneAutLotto.getInstance().calcolaQtaGiacenzaNetta(tipo, lt,
    	              !lottiOrig.isEmpty(), lottiOrdine));*/

    				/*BigDecimal qtaLotto = new BigDecimal("0");
    				if(saldiRigaCorrente.contains(lt.getKey())) {
    					qtaLotto = pal.calcolaQtaDisponibileLotto(tipo, lt, !lottiOrig.isEmpty(), lottiOrdine);
    				}
    				else {
        		        qtaLotto = pal.calcolaQtaGiacenzaNetta(tipo, lt, !lottiOrig.isEmpty(), lottiOrdine);

    				}*/
    				
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
    		        
    	          // Fine 6965
    	          // Fine 5798
    			}
    			pal.setQtaLottiProposate(qtaLotti); //35639
    		}	
    	}
    	
      iQuantitaLotti = qtaLotti ;//Fix 14336
      if (qtaLotti.compareTo(qta) >= 0)
        okQta = true;

      if (lottiAuto.isEmpty())
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

  // Fix 04324 EP ini
  public String getAltroRifRigaOrdineFormattato() {
    String s = "";
    return s;
  }

  public boolean isSaldoAutomatico() {
    boolean saldoAutomatico = false;
    if (this.getDocEvaVenRiga() != null &&
        this.getDocEvaVenRiga().getRigaDoc() != null &&
        this.getDocEvaVenRiga().getRigaDoc().getRigaOrdine() != null) {
       OrdineVenditaTestata ordVen = (OrdineVenditaTestata)this.getDocEvaVenRiga().getRigaDoc().getRigaOrdine().getTestata();
       if (ordVen != null && ordVen.getTipoEvasioneOrdine() == OrdineTestata.SALDO_AUTOMATICO) {
          saldoAutomatico = true;
       }
    }
    return saldoAutomatico;
  }

  // Fix 04324 EP fin

  /**
   * FIX04669 - DZ.
   * Ridefinito affinchè non scatti il controllo di esistenza righe secondarie
   * (inutile in fase di evasione se sono già presenti le righe secondarie sull'ordine).
   * @return ErrorMessage
   */
  protected ErrorMessage checkRigheSecondarie(){
    if (getRigheSecondarie().isEmpty())
      return super.checkRigheSecondarie();
    return null;
  }
//Fix 04445 GScarta INI
  public void aggiornaQtaDaSpedire(BigDecimal qtaDaSped) {
	//Fix 32807 - inizio
//	ParamRigaPrmDocEvaVen pr = new ParamRigaPrmDocEvaVen();
    ParamRigaPrmDocEvaVen pr = ParamRigaPrmDocEvaVen.create();
	//Fix 32807 - fine
    //MBH Fix 32165 :  Ho visto che questo metodo viene chiamato (in tutti el projetto) in due posizione 
    // Una in caso di attivazione del AccPre e una in caso di esistensa di piu di una riga del ordine per lo stesso articolo
    //e sempre (per le due chiamate) in caso di non sufficenza della gicenza passando come parametro la nuova qtaDaSpedire (secondo la giacenza) in UMPrmMag
    //quindi è sbagliato di metterla nella qtaVen (pr.iDaSpedireV)
    //evidamente l'errore si verifica in caso di UMVen diversa di UMPrm 
    
    //pr.iDaSpedireV = qtaDaSped;//32165
    pr.iDaSpedireM = qtaDaSped;//32165
    // Fix 4445
    if (qtaDaSped.compareTo(new BigDecimal("0"))==0){
      //pr.iDaSpedireM = qtaDaSped;//32165
    	pr.iDaSpedireV = qtaDaSped;//32165
      pr.iDaSpedireSecM = qtaDaSped;
    }
    else {
      //pr.iDaSpedireM = this.getQtaDaSpedireInUMPrm();//32165
    	pr.iDaSpedireV = this.getQtaDaSpedireInUMRif();//32165
      pr.iDaSpedireSecM = this.getQtaDaSpedireInUMSec();
    }
    //pr.iDaSpedireM = this.getQtaDaSpedireInUMPrm();
    //pr.iDaSpedireSecM = this.getQtaDaSpedireInUMSec();
    // Fine fix 4445
    pr.iDefinitivo = this.getStatoAvanzamento() == StatoAvanzamento.DEFINITIVO;
    pr.iDescrizioneArticolo = this.getDescrizioneArticolo();
    pr.iEstratta = this.isRigaEstratta();
    pr.iForzaEvasione = this.isRigaForzata();
    pr.iRicalcola = this.isRicalcoloQtaFattoreConv();
    pr.iSaldo = this.isRigaSaldata();
    this.aggiornaRiga(pr);
  }
  //Fix 04445 GScarta END
  // Fix 4445

  public void caricamentoLotti(){
    this.iRigheLotto.setNew(true);
  }

  // fine fix 4445

  //Fix 4768 - inizio
  /**
   * Ridefinizione
   */
  public void calcolaPrezzoDaRigheSecondarie() {
  }
  //Fix 4768 - fine

  // Fix 04718 ini
  public void calcolaImportiRiga() {
     super.calcolaImportiRiga();
  }

  // Fix 05238 ini
//  public ErrorMessage controlloFidoRiga() {
//     return null;
//  }
  // Fix 05238 fin
  // Fix 04718 fin


  //Fix 5348 - inizio
  /**
   * Ridefinizione
   */
  //Fix 34783 inizio
 /*
  public ErrorMessage checkArticoloEsclusione() {

     //return null;

  }
  */
  //Fix 34783 fine
  //Fix 5348 - fine


  // fix 5277
  // Questo metodo è necessario in quanto nel caso di evasione di proposte la riga
  // documento che si viene a generare è legata ad una riga documento gà esistente
  public static DocEvaVenRigaPrm creaRiga(DocEvaVen doc,
                                        OrdineVenditaRigaPrm rigaOrdine,
                                        boolean consQtaResidua) {
    return creaRiga(doc, rigaOrdine, consQtaResidua, null);

  }

  protected DocEvaVenRigaLottoPrm creaRigaLottoPrm(OrdineVenditaRigaLotto
      rigaOrdineLotto, DocumentoVenRigaLotto rigaOld) {
    return DocEvaVenRigaLottoPrm.creaRiga(this, rigaOrdineLotto, rigaOld);
  }
  // ridefinito questo metodo perchè in fase di evasione di una proposta non
  // deve se previsto creare i lotti automatici. Ricordo che la riga che viene
  // evasa è una riga che arriva al salvataggio come nuova, ma che in realtà
  // già esiste in quanto presente in un altro documento
  protected void creaLottiAutomatici() {
    if (!((DocEvaVen)this.getTestata()).isEvasioneDaLSP()){
      super.creaLottiAutomatici();
    }
  }
  // fine fix 5277

  // Inizio 5566
  public void sistemaQuadraturaLotti()
  {
    DocEvaVenRigaLottoPrm docRigaLotto = ((DocEvaVenRigaLottoPrm)getUnicoLottoEffettivo());
    QuantitaInUMRif zeroQta = new QuantitaInUMRif();
    zeroQta.azzera();
    QuantitaInUMRif deltaQta = new QuantitaInUMRif();
    if (docRigaLotto == null){
      super.sistemaQuadraturaLotti();//Fix 17924
      return;
    }
    //Fix 13342 PM >
    if (getArticolo().getArticoloDatiMagaz().isLottoUnitario()){
      super.sistemaQuadraturaLotti(); //Fix 17924
      return;
    }
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


  // Inizio 6920
  public DocumentoOrdineRigaLotto getUnicoLottoEffettivo(){
//     DocEvaVenRigaLottoPrm rigalotto = null; //Fix 14253 PM
	  DocumentoOrdineRigaLotto rigalotto = null; //Fix 14253 PM
    ArrayList listaLotti = (ArrayList)getRigheLotto();
    if (listaLotti.size() == 1){
       //DocEvaVenRigaLottoPrm rigalottoTmp  = (DocEvaVenRigaLottoPrm)listaLotti.get(0); //Fix 14253 PM
    	DocumentoOrdineRigaLotto rigalottoTmp  = (DocumentoOrdineRigaLotto)listaLotti.get(0); //Fix 14253 PM
      if (rigalottoTmp.getIdLotto() != null && !rigalottoTmp.getIdLotto().equals(LOTTO_DUMMY)) // Fix 6944
        rigalotto = rigalottoTmp;
    }
    return rigalotto;
  }
  // Fine 6920



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
    ((DocEvaVen)getTestata()).aggiornaMatricoleOrdineNumRigaPrm(vecchioNumRiga, getNumeroRigaDocumento());
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

  // Inizio 6716
  public void creazioneAutomaticaRigaContenitore() throws SQLException{
    ContrattoVendita con = getContrattoVendita();
    if (con != null){
      String idMagazzinoCont = con.getIdMagazzinoCont();
      PersDatiMagazzino pdm = PersDatiMagazzino.getCurrentPersDatiMagazzino();
      boolean cond1 = pdm.getGesContenitori();
      boolean cond2 = (idMagazzinoCont!= null);
      //boolean cond3 = hasCausaleTestataMagazConten();
      if (cond1 && cond2){
        BigDecimal qtaArtPerCont = null;
        BigDecimal qtaRigaDoc = getServizioQtaInUMVen();
        String idArticoloCont = con.getIdArticoloCont1();
        if (idArticoloCont != null){
          qtaArtPerCont = con.getQtaArtCont1();
          if (qtaArtPerCont == null)
            qtaArtPerCont = new BigDecimal(1);
          assegnaDatiRigaContenitoreDaContratto(idArticoloCont, idMagazzinoCont, qtaArtPerCont, qtaRigaDoc, DocumentoVenditaRiga.CONT_PRIMO_LIVELLO, null);
          idArticoloCont = con.getIdArticoloCont2();
          if (idArticoloCont != null){
            BigDecimal qtaArtPerCont2 = con.getQtaArtCont2();
            if (qtaArtPerCont2 == null)
              qtaArtPerCont2 = new BigDecimal(1);
            assegnaDatiRigaContenitoreDaContratto(idArticoloCont, idMagazzinoCont,qtaArtPerCont, qtaRigaDoc, DocumentoVenditaRiga.CONT_SECONDO_LIVELLO,qtaArtPerCont2);
          }
        }
      }
    }
    else{
      super.creazioneAutomaticaRigaContenitore();
    }
  }
  // Fine 6716

   //fix 7220 inizio
   public boolean hasControlloAccPrn()
   {
      return iControlloAccPrn;
   }

   public void setControlloAccPrn(boolean controlla)
   {
      iControlloAccPrn = controlla;
   }

   public boolean isAbilitatoCheckAccPrn()
   {
      return iAbilitatoCheckAccPrn;
   }

   public void setAbilitatoCheckAccPrn(boolean abilita)
   {
      iAbilitatoCheckAccPrn = abilita;
   }
   //fix 7220 fine

   //...FIX 8805 inizio

   /**
    * convertiQuantita
    * @param articolo Articolo
    * @param valore BigDecimal
    * @param source UnitaMisura
    * @param target UnitaMisura
    * @param pr ParamRigaPrmDocEvaVen
    * @return BigDecimal
    */
   protected BigDecimal convertiQuantita(Articolo articolo, BigDecimal valore, UnitaMisura source, UnitaMisura target, ParamRigaPrmDocEvaVen pr) {
     return articolo.convertiUM(valore, source, target, getArticoloVersRichiesta()); // fix 10955
   }

   //...FIX 8805 fine

   // fix 09612 >
   protected boolean isMantieniLottoDummy() {
     return false;
   }
   // fix 09612 <

   //...FIX 9450 inizio

   // fix 11779 >
   // fix 10955 >
   /**
    * @deprecated
    */
   public QuantitaInUMRif calcolaQuantitaArrotondate(Articolo articolo, BigDecimal quantOrigine, UnitaMisura umRif, UnitaMisura umPrm, UnitaMisura umSec, char idUMDigitata){
     return calcolaQuantitaArrotondate(articolo, quantOrigine, umRif, umPrm, umSec, null, idUMDigitata);
   }

   public QuantitaInUMRif calcolaQuantitaArrotondate(Articolo articolo, BigDecimal quantOrigine, UnitaMisura umRif, UnitaMisura umPrm, UnitaMisura umSec, ArticoloVersione versione, char idUMDigitata){
     return articolo.calcolaQuantitaArrotondate(quantOrigine, umRif, umPrm, umSec, versione, idUMDigitata);
   }
   // fix 10955 <
   // fix 11779 <

   //...FIX 9450 fine
   // fix 11120 >
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
            //isOk = true; // fix 12825
            isOk = true; // Fix 17485
            List errors = new ArrayList();
            errors.add(errMsg);
            entity.extractForceableErrors(errors, taskId);
            //isOk = errMsg.getForceable(); // fix 12825 //Fix 17485
        }
      }
      catch (Throwable t) {
         t.printStackTrace(Trace.excStream);
       }
     }
     return isOk;
   }

   protected void abilitaRigaForzabile(char livelloDisp) {
     // fix 11213 >
     this.setSelezionabile(true);
     this.setForzabile(true);
     // fix 11213 <
     if (livelloDisp == LivelliControlloDisponibilita.ROSSO) {
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

   // fix 11120 <

   // fix 11213 >
   protected ErrorMessage riassegnaErroreGiaDisp(List errors) {
     ErrorMessage errMsg = null;
     if (errors != null) {
       Iterator i = errors.iterator();
       while (i.hasNext()) {
         ErrorMessage e = (ErrorMessage)i.next();
         if (e.getId().equals(CalcoloGiacenzaDisponibilita.ERR_FORZABILE_GIA)) {
           i.remove();
           // Fix 20952 Inizio 
           /*
           boolean isOk = !this.isRigaForzata() && verificaForzabilitaErrorMessage(e, "NEW"); 
           if (isOk) {
             String[] ps = new String[] {
                 this.getRifRigaOrdineFormattato() + ": " + e.getLongText()};
             errMsg = new ErrorMessage("THIP300219", ps);
           }
           */
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
           //Fix 20952 Fine
         }
       }
     }
     return errMsg;
   }
   // fix 11213 <

   //Fix 11084 PM Inizio
   public ErrorMessage checkTrasmissiomePPL()
   {
	   return null;
   }
   //Fix 11084 PM Fine

   // fix 11402
   public QuantitaInUM getQuantitaGiaStorno(){
     return iQuantitaGiaStorno;
   }

   public void setQuantitaGiaStorno(QuantitaInUM qta){
     iQuantitaGiaStorno = qta;
   }


   public boolean effettuareIlControllo(List lista){
     boolean ritorno = super.effettuareIlControllo(lista);
     if (ritorno){
       if (this.getQuantitaGiaStorno()!=null){
         Iterator iter = lista.iterator();
         while(iter.hasNext()){
           OggCalcoloGiaDisp ogg = (OggCalcoloGiaDisp)iter.next();
           ogg.setQuantitaGiaStorno(this.getQuantitaGiaStorno());
         }
       }
     }
     return ritorno;
   }
   // fine fix 11402

  //Fix 13057 Inizio

  public ErrorMessage checkQtaDaSpedireInUMPrm() {

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

  //Fix 16586 inizio
  // gestione dell'eliminazione delle righe lotto quando generate da proposte
  public boolean isEliminataDaEvasioneProposta()
  {
     return iEliminataDaEvasioneProposta;
  }

  public void setEliminataDaEvasioneProposta(boolean eliminataDaEvasioneProposta)
  {
     iEliminataDaEvasioneProposta = eliminataDaEvasioneProposta;
  }
 /* //Fix 19426 :questo trattamento è già fatto nella class OrdineVenditaRiga nella metodo "aggiornaOrdLottiDaEvasione".
  protected int eliminaRiga() throws SQLException
  {
     //copia delle righe lotto precedenti
     List lotti = getRigheLotto();
     List lottiList = new ArrayList();
     Iterator iter = lotti.iterator();
     while(iter.hasNext())
        lottiList.add(iter.next());

     //eliminazione riga
     int ret = super.eliminaRiga();

     //eliminazione righe lotto create su righe ordine in caso di cancellazione da proposta
     boolean perEvasioneLSP = ((DocEvaVen)this.getTestata()).isEvasioneDaLSP();
     char bloccoLSP = ((DocEvaVen)this.getTestata()).getTipoBloccoLSP();
     if((perEvasioneLSP && bloccoLSP == TipoBloccoLSP.IMPEGNATO) || isEliminataDaEvasioneProposta())
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
        DocEvaVenRigaLottoPrm rigaLottoDoc = (DocEvaVenRigaLottoPrm)iterDoc.next();

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
           rigaLottoDummy = (OrdineVenditaRigaLotto) Factory.createObject(OrdineVenditaRigaLottoPrm.class);
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
  */ //Fix 19426
  //Fix 21144 inizio
 public void verificaGestioneTipoBene(){
	 ErrorMessage em = checkCongruenzaTipoBene();
	 if(em==null) return ;
	 abilitaRigaForzabile(em);
	 String msg="\n";
     msg += em.getLongText()+"\n";

     if(getDocEvaVenRiga().isAbilitaWarnings())
     {
         DocumentoOrdineRiga rigaOrdPrm = this.getRigaOrdine();
         String s = (String)((DocEvaVen)this.getTestata()).getRigheWarnings().get(rigaOrdPrm);
         if(s == null)
            s = "";
         msg += s;
         ((DocEvaVen)this.getTestata()).getRigheWarnings().put(rigaOrdPrm, msg);
     }	 
 }
 
 public  void  abilitaRigaForzabile(ErrorMessage em){
     boolean isOk = verificaForzabilitaErrorMessage(em, "NEW");
     if (isOk && isSelezionabile() && isForzabile()) {
    	 if(!em.getForceable()){
             this.setSelezionabile(false);
             this.setForzabile(false);
             this.setAbilitaDaAltroErroreForceable(false);
    	 }
     }	  
 }
 
 public List getIdErroriForceable(){
	 return iErroriForceable; 	
 }

 //Fix 21144 fine
 
 // Fix 22618 inizio 
  protected boolean isMatricoleDefinite() {
    DocumentoVenditaRiga riga = this;

    if (!riga.getArticolo().isArticoloMatric())
      return true;

    DocEvaVen testata = (DocEvaVen) getTestata();
    Map matricoleOrdine = testata.getMatricoleOrdine();

    if (matricoleOrdine.isEmpty())
      return false;

    List righeLotto = riga.getRigheLotto();
    Iterator iterRL = righeLotto.iterator();

    while (iterRL.hasNext()) {
      DocumentoVenRigaLotto rigaLotto = (DocumentoVenRigaLotto) iterRL.next();
      if (matricoleOrdine.containsKey(rigaLotto.getKey())) {
	int numMovDaGenerare = getNumMovimStoricoMatricolaDaGenerare(rigaLotto);
	List matricole = (List) matricoleOrdine.get(rigaLotto.getKey());
	int nbMatAMag = getNumeroMatAMag(matricole);
	if (nbMatAMag < numMovDaGenerare)
	  return false;
      }
      else
	return false;
    }

    return true;
  }

  public int getNumeroMatAMag(List mat) {
    Iterator iterLM = mat.iterator();
    int num = 0;
    while (iterLM.hasNext()) {
      LottoMatricola lm = (LottoMatricola) iterLM.next();
      if (lm.getStatoMatricola() == LottoMatricola.STATO_MAT_A_MAGAZZINO)
	num++;
    }
    return num;
  }

  protected int getNumMovimStoricoMatricolaDaGenerare(DocumentoVenRigaLotto rigaLotto) {
    return rigaLotto.getServizioQta().getQuantitaInUMPrm().intValue();
  }

  public ErrorMessage checkDichiarazioneMatricole(char tipoDocStorMat) {
    ErrorMessage em = null;
    Articolo articolo = getArticolo();
    if ((articolo != null && articolo.isArticoloMatric())) {
      char ctrlDichMatr = getLivelloCtrlDichMatricole();
      switch (ctrlDichMatr) {
      case PersDatiMagazzino.MATR_CTRL_DICH_NESSUNO:
	break;
      case PersDatiMagazzino.MATR_CTRL_DICH_WARNING:
	if (!isMatricoleDefinite()) {
	  em = new ErrorMessage("THIP200259");
	}
	break;
      case PersDatiMagazzino.MATR_CTRL_DICH_ERRORE:
	if (!isMatricoleDefinite()) {
	  
	   char  oldStatoAvanzamento =getStatoAvanzamento();
            sistemaQuantitaDopoVariazioneStatoAv(StatoAvanzamento.PROVVISORIO);
            if (oldStatoAvanzamento ==StatoAvanzamento.DEFINITIVO)
              em =getErrorMatricDichiarazione();
            else
	    
	  em = new ErrorMessage("THIP200278");
	}
	break;
      }
    }
    return em;
  }
 // Fix 22618 fine
//Fix 22545 inzio
 public ErrorMessage checkArticolStatoTecnico() {
   return null;
 }
 // Fix 22545 fine

 
 //Fix 34827 inizio
 private boolean iQtaCambiata = false;
 
 public void setQtaCambiata(boolean cambiato) {
	 iQtaCambiata = cambiato;
 }

 public boolean isQtaCambiata() {
   return iQtaCambiata;
 }
//Fix 34827 fine


 //Fix 34821 - inizio
 protected BigDecimal getQtaDaSpedireUMPrmAssLivCtrlGiaDispNoAccPrn() {
	 return getDocEvaVenRiga().getQtaDaSpedireInUMPrm();
 }

 
 protected BigDecimal getQtaResiduaUMPrmAssLivCtrlGiaDispNoAccPrn() {
	 return getQtaResiduaInUMPrm();
 }

 
 protected BigDecimal getQtaGiacenzaNettaUMPrmAssLivCtrlGiaDispNoAccPrn() {
	 return CalcolaDisponibilita.get().getGiacenzaNetta(this, getDocEvaVenRiga().hasControlloDisp());
 }
 //Fix 34821 - fine

 //Fix 35178 inizio
 public ErrorMessage checkArticoliRigheSecondarie() {
	 ErrorMessage em = null;
	 List righeSec = this.getRigheSecondarie();
	 if(righeSec != null && !righeSec.isEmpty()) {
		 Iterator i = righeSec.iterator();
		 while(i.hasNext()) {
			 DocEvaVenRigaSec rigaSec = (DocEvaVenRigaSec) i.next();
			 if(rigaSec != null && rigaSec.getArticolo() != null) {
				 em = rigaSec.checkStatoArticolo();
				 if(em != null)
					 return em;
			 }
		 }
	 }
	 return em;
 }
 //Fix 35178 fine
//36157 inizio
	public boolean isCambioMagazzinoAbilitato() {
		boolean cambioMagAbilitato = true;

		PersDatiVen pdv = PersDatiVen.getCurrentPersDatiVen();
		if (!pdv.isCambioMagEvasione())
			cambioMagAbilitato = false;

		else if (isOnDB() && !((DocEvaVen) getTestata()).isAccodamento())
			cambioMagAbilitato = false;

		return cambioMagAbilitato;
	}
	// 36157 fine

}

