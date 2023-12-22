package it.thera.thip.base.articolo;

import java.util.*;
import java.math.BigDecimal;

import com.thera.thermfw.persist.*;
import com.thera.thermfw.type.DecimalType;
import com.thera.thermfw.cbs.*;//MOD: 02853 SL

import it.thera.thip.base.interfca.RiferimentoVociCA;
import it.thera.thip.base.generale.*;
import it.thera.thip.base.azienda.*;
import it.thera.thip.base.cliente.*;
import it.thera.thip.base.comuniVenAcq.*;
import it.thera.thip.base.comuniVenAcq.web.*;
import it.thera.thip.base.documenti.DocumentoBaseRiga;
import it.thera.thip.datiTecnici.configuratore.*;
import it.thera.thip.base.risorse.*;

import java.sql.*;

import it.thera.thip.base.interfca.GruppoContiSpcCA;
import it.thera.thip.nicim.interfacce.InterfacciaStdNICIM;
import it.thera.thip.tessile.base.articolo.SchemaProdottoLotto;
import it.thera.thip.vendite.documentoVE.DocumentoVenditaRigaTM;
import it.valvorobica.thip.base.articolo.YArticoloBase;
import it.thera.thip.nicim.gestori.GestoreInterfacciaNICIM;
import it.thera.thip.nicim.gestori.GestoreInterfacciaArticoli;
import it.thera.thip.cs.ThipException;

import com.thera.thermfw.common.BaseComponentsCollection;
import com.thera.thermfw.common.ErrorMessage;

// fix 10955 >
import com.thera.thermfw.base.*;
import java.io.PrintStream;
// fix 10955 <

/**
 * Articolo.
 *
 * <br><br><b>Copyright (C): Thera SpA</b>
 * @author Debora Zanardini   10/01/2002
 */
/* Revisions:
 * Date         Owner       Description
 * 01/08/2002   DZ          Aggiunto iFromCodificatore e test su iFromCodificatore in saveOwnedObjects.
 * 23/10/2002   DZ          Aggiunto metodo getVersioneAtDate.
 * 24/10/2002   DZ          Aggiunti metodi per la proxy Configurazione su ArticoloDatiProduz.
 * 24/10/2002   DZ          Aggiunti metodi getUMDefault, getUMPrimaria, getUMSecondarie da ArticoloDatiVendita.
 * 28/11/2002   DZ          Aggiunti metodi getUMDefault, getUMPrimaria, getUMSecondarie da ArticoloDatiAcquisto.
 * 03/12/2002   DZ          Aggiunti metodi getImportoStdVendita, getImportoStdAcquisto.
 * 25/10/2002   DZ          Aggiunta proxy verso ArtidoloDatiEstensioni.
 * 23/04/2003   DZ          Modificata saveOwnedObjects perchè in copia il LivelloMinimo e
 *                          LivelloMinimoLavoro dei DatiTecnici siano imostati a zero anzichè copiati.
 * 21/05/2003   DZ          Scambiate proxy LineaCommerciale e LineaProdotto su ArticoloBase e ArticoloDatiIdent.
 * 16/06/2003   DZ          Tolti metodi get/set relativi a UMAltezza, UMLarghezza, UMLunghezza di DatiTecnici
 *                          (sostituiti da UMDimensioni) per adeguamento invio da Tunisi del 06/06/03.
 * 02/07/2003   PJ          Modificata la gestione dell'apertura automatica scheda saldi magazzino da copia di articolo e salvataggio sezione magazzino/logistica
 * 16/09/2003   DZ          Aggiunti metodi convertiUM (tolto da ArticoloBase) e getUMMagConvertitore.
 * 01/10/2003   DZ          Metodo save: Aggiunto test su errorcode prima di eseguire l'apertura saldi su sezione magaz.
 * 08/10/2003   DZ          Corretto getUMMagConvertitore (avevo invertito gli operatori di conversione).
 * 14/10/2003   DZ          In tutti i metodi getSezioneArticoloXXX aggiunta chiamata a setArticolo.
 * 17/10/2003   DZ          Corretti i metodi getSezioneXXX -spostata chiamata a setArticolo fuori dall'if(retrieve)
 * 20/10/2003   DZ          CollegamentoThipLogis (save - NON deleteOwnedObjects perchè viene già cancellato in ArticoloBase).
 * 21/10/2003   DZ          Corretta save: agisco direttamente sulle istanze delle varie sezioni anzichè
 *                          chiamare getSezioneXXX -problemi in caricamento di massa: il getSezioneXXX svuota
 *                          la sezione dopo che era stata valorizzata- (impostando sulle istanze onDB true,
 *                          visto che non viene settato in automatico quando la retrieve va a buon fine)
 * 22/10/2003   DZ          Modificata save: settato a false iAggiornaDìSattoDatiIdent.
 * 01279  16/01/2004  DZ     convertiUM: adeguamento a modifica 1279 su ArticoloDatiMagaz -
 *                           utilizzare per la conversione OperConv e FattConv su ArticoloDatiMagaz se,
 *                           oltre ad essere le due UM quelle Prm e Sec di magazzino,
 *                           l'operConver su DatiMagaz è diverso da NON_SIGNIFICATIVO
 * 01294  20/01/2004  DZ     getUMMagConvertitore: aggiunta scale al fattoreConver dell'um fittizia
 * 01545  01/02/2004  DZ     convertiUM: corrette parentesi nell'if (baco)
 * 02508  24/09/2004  SR     modificati getLegislazione e setLegislazione
 * 02585  08/10/2004  DZ     Aggiunto metodo salvaVersioniPerSaldi.
 * 02792  08/11/2004  DZ     Aggiunti metodi get/set per IdSchemaPrzVen, IdArticoloMatVen, IdSchemaPrzAcq,
 *                           IdArticoloMatAcq, IdFamigliaPgm, IdArticoloCicloColl, Procedura che sostituisce Legislazione.
 * 02853  23/11/2004  SL     Modifica fatta per gestire gli attributi estendibili con il
 *                           caricamento di massa degli articoli;
 *                           Corretta anche la copia dell'articolo con attributi estendibili.
 * 03785  17/05/2005  MN     Ottimizzato il metodo convertiUM().
 * 04088  08/07/2005  DZ     saveOO: in copia non deve essere copiato il LivelloMinimoModello (datiTecnici)
 * 04670  02/12/2005  MN     Gestione Unità Misura con flag Quantità intera.
 * 04792  19/12/2005  DZ     Aggiunto attributo PrezzoStdVen di ArticoloDatiVendita.
 * 04842  03/01/2006  MN     L'arrotondamento delle UM gestite a qta intera deve essere
 *                           eseguita anche quando le unità di misura sono ugiali.
 * 05042  20/02/2006  DZ     saveOO: in copia non deve essere copiata la configurazione std.
 * 05117  14/03/2006  GN     In calcolaQuantitaArrotondate un UM viene considerata gestita a qta intera, non solo quando
 *                           è attivo il relativo flag sull'UM stessa, ma anche quando l'UM è di magazzino e sui
 *                           dati di magazzino dell'articolo il flag 'Movimentazione a qta intera' è impostato a true.
 * 07522  27/06/2007  DB     aggiunta gestione Um logistica
 * 08159  13/12/2007  EP     Modificato il meccanismo di retrieve per le singole sezione.
 *                           Ogni sezione utilizza LockType() della sezione base
 * 08436  14/12/2007  OV     Correzione copia dati contabili.
 *
 * 08243  03/12/2007  MS     Tenere sempre conto dell'eventual fattore di conversione specifico nella sezione "Dati magazzino".
 * 08646  04/02/2008  MN     Modificato l'algoritmo di arrontondamento delle qta intere.
 * 08650  04/02/2008  GM     aggiunto getIdGruppoProdotto
 * 08770  24/03/2008  Bac    Gestione interfacce tempo reale (Nicim)
 * 09117  24/04/2008  DBot   Corretta gestione tempo reale (Gestore sbagliato in delete)
 * 09608  28/07/2008  FR     Commentato e spostato nella classe genitore ArticoloBase il metodo getUnitaMisuraList()
 * 09922  16/10/2008  DBot   Aggiunto metodo per versione tecnica
 * 10955  08/06/2009  GSca   Nuovo metodo convertiUM
 * 11120  24/07/2009  GSca   Aggiornato metodo controllaConversioneUM
 * 11302  14/09/2009  DB     Sto cercando di permettere la copia delle versioni quando l'articolo hasVersioneEstesa e sono in copia
 * 11818  18/12/2009  GN     Corretto metodo controllaConversioneUM ed aggiunta una nuovi signature rispettivamente
 *                           per il metodo convertiUM e per convertiUMArrotondate
 * 11517  21/10/2009  GSca   Aggiornato metodo controllaConversioneUM
 * 11622  20/11/2009  DBot   Aggiunti metodi statici per arrotondamento UM
 * 11779  16/12/2009  GSca   Aggiornamento gestione Numero Imballo
 * 11951  15/01/2010  GSca   Riporto in 3.0.8 della fix 11779. Riallineamento alla fix 11622
 * 12508  20/04/2010  DBot   Metodi di utilità per pesi e volumi su ordini e documenti
 * 13011  29/07/2010  DBot   Limitati i valori inseribili su righe e testate per pesi e volumi
 * 12639  26/05/2010  GScarta  Nuova gesitone numero imballo
 * 14738  29/06/2011  DBot   Integrazione a standard fix 12639
 * 14931  30/08/2011  DBot   Modificata gestione pesi/volume x ceramiche
 * 14782  16/02/2012  BW     Aggiunto metodo getIsNew per poterlo chiamare nelle sezioni figlie
 * 18088  10/09/2013  RA     Annula valore di ScortaMinima in caso di copia dell'articolo.
 * 18087  13/10/2014  FM     Copia articoli costi
 * 20517  10/12/2014  LTB    Aggiungere i metodi di GestioneLogisticaLight
 * 21735  12/06/2015  OCH    Copia dati saldi -1 per articoli Magaz e annula la copia dei articoli Costi :
 *                           Annullamento delle fix 18087 e 18088
 * 22528  16/11/2015  LTB    Aggiunto attributo iUMUbicazioniBarcode
 * 22583  01/12/2015  AYM    Salva 'ArticoloDatiVersioni' in caso di intercompany.
 * 26116  12/07/2017  LP     Fix tecnica per personalizzazioni 
 * 22170  08/09/2015  Mz     Gestione tessile 
 * 22255  29/092015   Mz     Ripeimento prodotto in altre aziende
 * 29177  11/04/2019  LTB    Rendere il metodo roundQuantita() da protected a public 
 * 28684  24/04/2019  RH     aggiunto metodi per gestione WorkFlow
 * 30478  10/01/2019  TJ	 Verifica se QuantitaInUMRif, QuantitaInUMPrm, QuantitaInUMSec sono diverse da null
 * 30372  21/01/2020  HED    aggiunto metodi per ottenere la versione tra due date
 * 30871  09/03/2020  SZ	 6 Decimali.
 * 33959  13/07/2021  YBA    Aggiunto metodi getGruppoProdotto, getNomenclaturaDog
 * 70977  01/03/2023  TBSOF3 Modificata l'estensione da ArticoloBase a YArticoloBase
 * 39402  24/07/2023  SZ     Scale errato se il database ha le quantità a 6 decimali.
 */

public class Articolo extends YArticoloBase implements BaseObject{
	
  protected char iSezioneArticoloWF ='-'; 

  protected ArticoloDatiIdent iArticoloDatiIdent;

  protected ArticoloDatiTecnici iArticoloDatiTecnici;        // sezione 2;
  protected ArticoloDatiVendita iArticoloDatiVendita;        // sezione 3;
  protected ArticoloDatiAcquisto iArticoloDatiAcquisto;      // sezione 4;
  protected ArticoloDatiPianif iArticoloDatiPianif;          // sezione 5;
  protected ArticoloDatiProduz iArticoloDatiProduz;          // sezione 6;
  protected ArticoloDatiMagaz iArticoloDatiMagaz;            // sezione 7;
  protected ArticoloDatiContab iArticoloDatiContab;          // sezione 8;
  protected ArticoloDatiCosto iArticoloDatiCosto;            // sezione 9;
  protected ArticoloDatiQualita iArticoloDatiQualita;        // sezione 10;
  protected ArticoloDatiStabil iArticoloDatiStabil;          // sezione 11;
  protected ArticoloDatiVersioni iArticoloDatiVersioni;      // sezione 12;
  protected ArticoloDatiEstensioni iArticoloDatiEstensioni;  // sezione 13;

  protected boolean iIsNew = false;
  //Fix 14782 ini
  public boolean getIsNew() {
    return iIsNew;
  }
  //Fix 14782 fine


  private static Articolo cInstance;
  // Inizio 3785
  private static BigDecimal ZERO_DEC = new BigDecimal(0);
  // Fine 3785

  // Inizio 4670
  public static final char TIPO_RIGA = 'R';
  public static final char TIPO_LOTTO = 'L';

  // Sigla UM Origine
  public static final char DOMINIO_VEN = 'V';
  public static final char DOMINIO_ACQ = 'A';
  public static final char UM_RIF = 'R';
  public static final char UM_PRM = 'P';
  public static final char UM_SEC = 'S';

  // Step di calcolo
  public static final char STEP_0 = '0';
  public static final char STEP_1 = '1';
  public static final char STEP_2 = '2';
  public static final char STEP_3 = '3';
  public static final char STEP_4 = '4';
  

  // Fine 4670

  // fix 10955 >
  private List iErrorsMessagges = new ArrayList();

  public List getErrorsMessagges() {
    return this.iErrorsMessagges;
  }
  public void setErrorsMessagges(List errors) {
    this.iErrorsMessagges.clear();
    this.iErrorsMessagges.addAll(errors);
  }
  public void addErrorMessage(ErrorMessage em) {
    this.iErrorsMessagges.add(em);
  }
  public void removeErrorsMessagges() {
    this.iErrorsMessagges.clear();
  }
  // fix 10955 <


  /**
   * Restituisce la sezione corrispondente all'id passato come parametro.
   * Sarà successivamente necessario effettuare un cast dell'oggetto SezioneBaseArticolo.
   * Usato dal Codificatore.
   */
  public SezioneBaseArticolo getSezione(char idSezione){
    SezioneBaseArticolo sezioneArticolo = null;
    switch (idSezione) {
      case SezioneBaseArticolo.DATI_DI_ACQUISTO:
        sezioneArticolo = getArticoloDatiAcquisto();
        break;
      case SezioneBaseArticolo.DATI_DI_CONTABILI:
        sezioneArticolo = getArticoloDatiContab();
        break;
      case SezioneBaseArticolo.DATI_DI_CONTROLLO:
        sezioneArticolo = getArticoloDatiQualita();
        break;
      case SezioneBaseArticolo.DATI_DI_COSTO:
        sezioneArticolo = getArticoloDatiCosto();
        break;
      case SezioneBaseArticolo.DATI_DI_MAGAZZINO:
        sezioneArticolo = getArticoloDatiMagaz();
        break;
      case SezioneBaseArticolo.DATI_DI_PIANIFICAZIONE:
        sezioneArticolo = getArticoloDatiPianif();
        break;
      case SezioneBaseArticolo.DATI_DI_PRODUZIONE:
        sezioneArticolo = getArticoloDatiProduz();
        break;
      case SezioneBaseArticolo.DATI_DI_STABILIMENTO:
        sezioneArticolo = getArticoloDatiStabil();
        break;
      case SezioneBaseArticolo.DATI_DI_VENDITA:
        sezioneArticolo = getArticoloDatiVendita();
        break;
      case SezioneBaseArticolo.DATI_IDENTIFICATIVI:
        sezioneArticolo = getArticoloDatiIdent();
        break;
      case SezioneBaseArticolo.DATI_TECNICI:
        sezioneArticolo = getArticoloDatiTecnici();
    }
    return sezioneArticolo;
  }

//******************************************************************************
//*********************************** Proxy ************************************
//******************************************************************************

  public void setIdAzienda(String idAzienda) {
    super.setIdAzienda(idAzienda);
//    if (!iFromCodificatore){
      if (iArticoloDatiIdent != null)
        iArticoloDatiIdent.setIdAzienda(idAzienda);
      if (iArticoloDatiTecnici != null)
        iArticoloDatiTecnici.setIdAzienda(idAzienda);
      if (iArticoloDatiVendita != null)
        iArticoloDatiVendita.setIdAzienda(idAzienda);
      if (iArticoloDatiAcquisto != null)
        iArticoloDatiAcquisto.setIdAzienda(idAzienda);
      if (iArticoloDatiPianif != null)
        iArticoloDatiPianif.setIdAzienda(idAzienda);
      if (iArticoloDatiProduz != null)
        iArticoloDatiProduz.setIdAzienda(idAzienda);
      if (iArticoloDatiMagaz != null)
        iArticoloDatiMagaz.setIdAzienda(idAzienda);
      if (iArticoloDatiContab != null)
        iArticoloDatiContab.setIdAzienda(idAzienda);
      if (iArticoloDatiCosto != null)
        iArticoloDatiCosto.setIdAzienda(idAzienda);
      if (iArticoloDatiQualita != null)
        iArticoloDatiQualita.setIdAzienda(idAzienda);
      if (iArticoloDatiStabil != null)
        iArticoloDatiStabil.setIdAzienda(idAzienda);
      if (iArticoloDatiVersioni != null)
        iArticoloDatiVersioni.setIdAzienda(idAzienda);
//Mod. 02853 SL - Gestione attributi estendibili (x CM)
      if (iArticoloDatiEstensioni != null)
        iArticoloDatiEstensioni.setIdAzienda(idAzienda);
  }

  public void setIdArticolo(String idArticolo) {
    super.setIdArticolo(idArticolo);
//    if (!iFromCodificatore){
      if (iArticoloDatiIdent != null)
        iArticoloDatiIdent.setIdArticolo(idArticolo);
      if (iArticoloDatiTecnici != null)
        iArticoloDatiTecnici.setIdArticolo(idArticolo);
      if (iArticoloDatiVendita != null)
        iArticoloDatiVendita.setIdArticolo(idArticolo);
      if (iArticoloDatiAcquisto != null)
        iArticoloDatiAcquisto.setIdArticolo(idArticolo);
      if (iArticoloDatiPianif != null)
        iArticoloDatiPianif.setIdArticolo(idArticolo);
      if (iArticoloDatiProduz != null)
        iArticoloDatiProduz.setIdArticolo(idArticolo);
      if (iArticoloDatiMagaz != null)
        iArticoloDatiMagaz.setIdArticolo(idArticolo);
      if (iArticoloDatiContab != null)
        iArticoloDatiContab.setIdArticolo(idArticolo);
      if (iArticoloDatiCosto != null)
        iArticoloDatiCosto.setIdArticolo(idArticolo);
      if (iArticoloDatiQualita != null)
        iArticoloDatiQualita.setIdArticolo(idArticolo);
       if (iArticoloDatiStabil != null)
        iArticoloDatiStabil.setIdArticolo(idArticolo);
       if (iArticoloDatiVersioni != null)
        iArticoloDatiVersioni.setIdArticolo(idArticolo);
//Mod. 02853 SL - Gestione attributi estendibili (x CM)
       if (iArticoloDatiEstensioni != null)
        iArticoloDatiEstensioni.setIdArticolo(idArticolo);
       //}
  }

//...................................................... proxy ArticoloDatiIdent

  /**
   * Se iArticoloDatiIdent è null, viene tentata una prima retrieve.
   * Se essa non ha dato esito positivo (perchè ancora la chiave non era stata impostata
   * correttamente), quando il metodo viene richiamato la retrieve viene ritentata.
   */
  public ArticoloDatiIdent getArticoloDatiIdent() {
    try{
      if (iArticoloDatiIdent == null){
        iArticoloDatiIdent = (ArticoloDatiIdent)Factory.createObject(ArticoloDatiIdent.class);
        iArticoloDatiIdent.setKey(this.getKey());
        iArticoloDatiIdent.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        //if (iArticoloDatiIdent.retrieve(PersistentObject.OPTIMISTIC_LOCK)) // Fix 8159
        if (iArticoloDatiIdent.retrieve(this.getLockType())) // Fix 8159
          iArticoloDatiIdent.setInitialized(true);
        iArticoloDatiIdent.setArticolo(this);
      }
      //...se la prima volta la retrieve non è andata a buon fine, l'oggetto viene riletto.
      else if (!iArticoloDatiIdent.isInitialized()){
        iArticoloDatiIdent.setKey(this.getKey());
        iArticoloDatiIdent.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        //if (iArticoloDatiIdent.retrieve(PersistentObject.OPTIMISTIC_LOCK)) // Fix 8159
        if (iArticoloDatiIdent.retrieve(this.getLockType())) // Fix 8159
          iArticoloDatiIdent.setInitialized(true);
        iArticoloDatiIdent.setArticolo(this);
      }
    }
    catch(SQLException e){
      e.printStackTrace();
    }
    return iArticoloDatiIdent;
  }

//.................................................... proxy ArticoloDatiTecnici

  public ArticoloDatiTecnici getArticoloDatiTecnici()
   {
     try{
      if (iArticoloDatiTecnici == null){
        iArticoloDatiTecnici = (ArticoloDatiTecnici)Factory.createObject(ArticoloDatiTecnici.class);
        iArticoloDatiTecnici.setKey(this.getKey());
        iArticoloDatiTecnici.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        //if (iArticoloDatiTecnici.retrieve(PersistentObject.OPTIMISTIC_LOCK)) // Fix 8159
        if (iArticoloDatiTecnici.retrieve(this.getLockType())) // Fix 8159
          iArticoloDatiTecnici.setInitialized(true);
        iArticoloDatiTecnici.setArticolo(this);
      }
      else if (!iArticoloDatiTecnici.isInitialized()){
        iArticoloDatiTecnici.setKey(this.getKey());
        iArticoloDatiTecnici.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        //if (iArticoloDatiTecnici.retrieve(PersistentObject.OPTIMISTIC_LOCK)) // Fix 8159
        if (iArticoloDatiTecnici.retrieve(this.getLockType())) // Fix 8159
          iArticoloDatiTecnici.setInitialized(true);
        iArticoloDatiTecnici.setArticolo(this);
      }
    }
    catch(SQLException e){
      e.printStackTrace();
    }
    return iArticoloDatiTecnici;
  }

//.................................................... proxy ArticoloDatiVendita

  public ArticoloDatiVendita getArticoloDatiVendita()
   {
     try{
      if (iArticoloDatiVendita == null){
        iArticoloDatiVendita = (ArticoloDatiVendita)Factory.createObject(ArticoloDatiVendita.class);
        iArticoloDatiVendita.setKey(this.getKey());
        iArticoloDatiVendita.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        //if (iArticoloDatiVendita.retrieve(PersistentObject.OPTIMISTIC_LOCK)) // Fix 8159
        if (iArticoloDatiVendita.retrieve(this.getLockType())) // Fix 8159
          iArticoloDatiVendita.setInitialized(true);
        iArticoloDatiVendita.setArticolo(this);
      }
      else if (!iArticoloDatiVendita.isInitialized()){
        iArticoloDatiVendita.setKey(this.getKey());
        iArticoloDatiVendita.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        //if (iArticoloDatiVendita.retrieve(PersistentObject.OPTIMISTIC_LOCK)) // Fix 8159
        if (iArticoloDatiVendita.retrieve(this.getLockType())) // Fix 8159
          iArticoloDatiVendita.setInitialized(true);
        iArticoloDatiVendita.setArticolo(this);
      }
    }
    catch(SQLException e){
      e.printStackTrace();
    }
    return iArticoloDatiVendita;
  }

//................................................... proxy ArticoloDatiAcquisto

  public ArticoloDatiAcquisto getArticoloDatiAcquisto()
   {
     try{
      if (iArticoloDatiAcquisto == null){
        iArticoloDatiAcquisto = (ArticoloDatiAcquisto)Factory.createObject(ArticoloDatiAcquisto.class);
        iArticoloDatiAcquisto.setKey(this.getKey());
        iArticoloDatiAcquisto.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        //if (iArticoloDatiAcquisto.retrieve(PersistentObject.OPTIMISTIC_LOCK)) // Fix 8159
        if (iArticoloDatiAcquisto.retrieve(this.getLockType())) // Fix 8159
          iArticoloDatiAcquisto.setInitialized(true);
        iArticoloDatiAcquisto.setArticolo(this);
      }
      else if (!iArticoloDatiAcquisto.isInitialized()){
        iArticoloDatiAcquisto.setKey(this.getKey());
        iArticoloDatiAcquisto.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        //if (iArticoloDatiAcquisto.retrieve(PersistentObject.OPTIMISTIC_LOCK)) // Fix 8159
        if (iArticoloDatiAcquisto.retrieve(this.getLockType())) // Fix 8159
          iArticoloDatiAcquisto.setInitialized(true);
        iArticoloDatiAcquisto.setArticolo(this);
      }
    }
    catch(SQLException e){
      e.printStackTrace();
    }
    return iArticoloDatiAcquisto;
  }

//..................................................... proxy ArticoloDatiPianif

  public ArticoloDatiPianif getArticoloDatiPianif()
   {
     try{
      if (iArticoloDatiPianif == null){
        iArticoloDatiPianif = (ArticoloDatiPianif)Factory.createObject(ArticoloDatiPianif.class);
        iArticoloDatiPianif.setKey(this.getKey());
        iArticoloDatiPianif.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        //if (iArticoloDatiPianif.retrieve(PersistentObject.OPTIMISTIC_LOCK)) // Fix 8159
        if (iArticoloDatiPianif.retrieve(this.getLockType())) // Fix 8159
          iArticoloDatiPianif.setInitialized(true);
        iArticoloDatiPianif.setArticolo(this);
      }
      else if (!iArticoloDatiPianif.isInitialized()){
        iArticoloDatiPianif.setKey(this.getKey());
        iArticoloDatiPianif.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        //if (iArticoloDatiPianif.retrieve(PersistentObject.OPTIMISTIC_LOCK)) // Fix 8159
        if (iArticoloDatiPianif.retrieve(this.getLockType())) // Fix 8159
          iArticoloDatiPianif.setInitialized(true);
        iArticoloDatiPianif.setArticolo(this);
      }
    }
    catch(SQLException e){
      e.printStackTrace();
    }
    return iArticoloDatiPianif;
  }

//..................................................... proxy ArticoloDatiProduz

  public ArticoloDatiProduz getArticoloDatiProduz()
   {
     try{
      if (iArticoloDatiProduz == null){
        iArticoloDatiProduz = (ArticoloDatiProduz)Factory.createObject(ArticoloDatiProduz.class);
        iArticoloDatiProduz.setKey(this.getKey());
        iArticoloDatiProduz.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        //if (iArticoloDatiProduz.retrieve(PersistentObject.OPTIMISTIC_LOCK)) // Fix 8159
        if (iArticoloDatiProduz.retrieve(this.getLockType())) // Fix 8159
          iArticoloDatiProduz.setInitialized(true);
        iArticoloDatiProduz.setArticolo(this);
      }
      else if (!iArticoloDatiProduz.isInitialized()){
        iArticoloDatiProduz.setKey(this.getKey());
        iArticoloDatiProduz.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        //if (iArticoloDatiProduz.retrieve(PersistentObject.OPTIMISTIC_LOCK)) // Fix 8159
        if (iArticoloDatiProduz.retrieve(this.getLockType())) // Fix 8159
          iArticoloDatiProduz.setInitialized(true);
        iArticoloDatiProduz.setArticolo(this);
      }
    }
    catch(SQLException e){
      e.printStackTrace();
    }
    return iArticoloDatiProduz;
  }

//...................................................... proxy ArticoloDatiMagaz

  public ArticoloDatiMagaz getArticoloDatiMagaz()
   {
     try{
      if (iArticoloDatiMagaz == null){
        iArticoloDatiMagaz = (ArticoloDatiMagaz)Factory.createObject(ArticoloDatiMagaz.class);
        iArticoloDatiMagaz.setKey(this.getKey());
        iArticoloDatiMagaz.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        //if (iArticoloDatiMagaz.retrieve(PersistentObject.OPTIMISTIC_LOCK)) // Fix 8159
        if (iArticoloDatiMagaz.retrieve(this.getLockType())) // Fix 8159
          iArticoloDatiMagaz.setInitialized(true);
        iArticoloDatiMagaz.setArticolo(this);
      }
      else if (!iArticoloDatiMagaz.isInitialized()){
        iArticoloDatiMagaz.setKey(this.getKey());
        iArticoloDatiMagaz.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        //if (iArticoloDatiMagaz.retrieve(PersistentObject.OPTIMISTIC_LOCK)) // Fix 8159
        if (iArticoloDatiMagaz.retrieve(this.getLockType())) // Fix 8159
          iArticoloDatiMagaz.setInitialized(true);
        iArticoloDatiMagaz.setArticolo(this);
      }
    }
    catch(SQLException e){
      e.printStackTrace();
    }
    return iArticoloDatiMagaz;
  }

//..................................................... proxy ArticoloDatiContab

  public ArticoloDatiContab getArticoloDatiContab()
   {
     try{
      if (iArticoloDatiContab == null){
        iArticoloDatiContab = (ArticoloDatiContab)Factory.createObject(ArticoloDatiContab.class);
        iArticoloDatiContab.setKey(this.getKey());
        iArticoloDatiContab.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        //if (iArticoloDatiContab.retrieve(PersistentObject.OPTIMISTIC_LOCK)) // Fix 8159
        if (iArticoloDatiContab.retrieve(this.getLockType())) // Fix 8159
          iArticoloDatiContab.setInitialized(true);
        iArticoloDatiContab.setArticolo(this);
      }
      else if (!iArticoloDatiContab.isInitialized()){
        iArticoloDatiContab.setKey(this.getKey());
        iArticoloDatiContab.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        //if (iArticoloDatiContab.retrieve(PersistentObject.OPTIMISTIC_LOCK)) // Fix 8159
        if (iArticoloDatiContab.retrieve(this.getLockType())) // Fix 8159
          iArticoloDatiContab.setInitialized(true);
        iArticoloDatiContab.setArticolo(this);
      }
    }
    catch(SQLException e){
      e.printStackTrace();
    }
    return iArticoloDatiContab;
  }

//...................................................... proxy ArticoloDatiCosto

  public ArticoloDatiCosto getArticoloDatiCosto()
   {
     try{
      if (iArticoloDatiCosto == null){
        iArticoloDatiCosto = (ArticoloDatiCosto)Factory.createObject(ArticoloDatiCosto.class);
        iArticoloDatiCosto.setKey(this.getKey());
        iArticoloDatiCosto.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        //if (iArticoloDatiCosto.retrieve(PersistentObject.OPTIMISTIC_LOCK)) // Fix 8159
        if (iArticoloDatiCosto.retrieve(this.getLockType())) // Fix 8159
          iArticoloDatiCosto.setInitialized(true);
        iArticoloDatiCosto.setArticolo(this);
      }
      else if (!iArticoloDatiCosto.isInitialized()){
        iArticoloDatiCosto.setKey(this.getKey());
        iArticoloDatiCosto.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        //if (iArticoloDatiCosto.retrieve(PersistentObject.OPTIMISTIC_LOCK)) // Fix 8159
        if (iArticoloDatiCosto.retrieve(this.getLockType())) // Fix 8159
          iArticoloDatiCosto.setInitialized(true);
        iArticoloDatiCosto.setArticolo(this);
      }
    }
    catch(SQLException e){
      e.printStackTrace();
    }
    return iArticoloDatiCosto;
  }

//.................................................... proxy ArticoloDatiQualita

  public ArticoloDatiQualita getArticoloDatiQualita()
   {
     try{
      if (iArticoloDatiQualita == null){
        iArticoloDatiQualita = (ArticoloDatiQualita)Factory.createObject(ArticoloDatiQualita.class);
        iArticoloDatiQualita.setKey(this.getKey());
        iArticoloDatiQualita.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        //if (iArticoloDatiQualita.retrieve(PersistentObject.OPTIMISTIC_LOCK)) // Fix 8159
        if (iArticoloDatiQualita.retrieve(this.getLockType())) // Fix 8159
          iArticoloDatiQualita.setInitialized(true);
        iArticoloDatiQualita.setArticolo(this);
      }
      else if (!iArticoloDatiQualita.isInitialized()){
        iArticoloDatiQualita.setKey(this.getKey());
        iArticoloDatiQualita.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        //if (iArticoloDatiQualita.retrieve(PersistentObject.OPTIMISTIC_LOCK)) // Fix 8159
        if (iArticoloDatiQualita.retrieve(this.getLockType())) // Fix 8159
          iArticoloDatiQualita.setInitialized(true);
        iArticoloDatiQualita.setArticolo(this);
      }
    }
    catch(SQLException e){
      e.printStackTrace();
    }
    return iArticoloDatiQualita;
  }

//..................................................... proxy ArticoloDatiStabil

  public ArticoloDatiStabil getArticoloDatiStabil()
   {
     try{
      if (iArticoloDatiStabil == null){
        iArticoloDatiStabil = (ArticoloDatiStabil)Factory.createObject(ArticoloDatiStabil.class);
        iArticoloDatiStabil.setKey(this.getKey());
        iArticoloDatiStabil.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        //if (iArticoloDatiStabil.retrieve(PersistentObject.OPTIMISTIC_LOCK)) // Fix 8159
        if (iArticoloDatiStabil.retrieve(this.getLockType())) // Fix 8159
          iArticoloDatiStabil.setInitialized(true);
        iArticoloDatiStabil.setArticolo(this);
      }
      else if (!iArticoloDatiStabil.isInitialized()){
        iArticoloDatiStabil.setKey(this.getKey());
        iArticoloDatiStabil.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        //if (iArticoloDatiStabil.retrieve(PersistentObject.OPTIMISTIC_LOCK)) // Fix 8159
        if (iArticoloDatiStabil.retrieve(this.getLockType())) // Fix 8159
          iArticoloDatiStabil.setInitialized(true);
        iArticoloDatiStabil.setArticolo(this);
      }
    }
    catch(SQLException e){
      e.printStackTrace();
    }
    return iArticoloDatiStabil;
  }

//................................................... proxy ArticoloDatiVersioni

 public ArticoloDatiVersioni getArticoloDatiVersioni() {
    try{
      if (iArticoloDatiVersioni == null){
        iArticoloDatiVersioni = (ArticoloDatiVersioni)Factory.createObject(ArticoloDatiVersioni.class);
        iArticoloDatiVersioni.setKey(this.getKey());
        iArticoloDatiVersioni.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        //if (iArticoloDatiVersioni.retrieve(PersistentObject.OPTIMISTIC_LOCK)) // Fix 8159
        if (iArticoloDatiVersioni.retrieve(this.getLockType())) // Fix 8159
          iArticoloDatiVersioni.setInitialized(true);
        iArticoloDatiVersioni.setArticolo(this);
      }
      else if (!iArticoloDatiVersioni.isInitialized()){
        iArticoloDatiVersioni.setKey(this.getKey());
        iArticoloDatiVersioni.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        //if (iArticoloDatiVersioni.retrieve(PersistentObject.OPTIMISTIC_LOCK)) // Fix 8159
        if (iArticoloDatiVersioni.retrieve(this.getLockType())) // Fix 8159
          iArticoloDatiVersioni.setInitialized(true);
        iArticoloDatiVersioni.setArticolo(this);
      }
    }
    catch(SQLException e){
      e.printStackTrace();
    }
    return iArticoloDatiVersioni;
  }

//................................................. proxy ArticoloDatiEstensioni

  public ArticoloDatiEstensioni getArticoloDatiEstensioni(){
     try{
      if (iArticoloDatiEstensioni == null){
        iArticoloDatiEstensioni = (ArticoloDatiEstensioni)Factory.createObject(ArticoloDatiEstensioni.class);
        iArticoloDatiEstensioni.setKey(this.getKey());
        iArticoloDatiEstensioni.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        if (iArticoloDatiEstensioni.retrieve(PersistentObject.NO_LOCK))
          iArticoloDatiEstensioni.setInitialized(true);
        iArticoloDatiEstensioni.setArticolo(this);
      }
      else if (!iArticoloDatiEstensioni.isInitialized()){
        iArticoloDatiEstensioni.setKey(this.getKey());
        iArticoloDatiEstensioni.setDeepRetrieveEnabled(this.isDeepRetrieveEnabled());
        if (iArticoloDatiEstensioni.retrieve(PersistentObject.NO_LOCK))
          iArticoloDatiEstensioni.setInitialized(true);
        iArticoloDatiEstensioni.setArticolo(this);
      }
    }
    catch(SQLException e){
      e.printStackTrace();
    }
    return iArticoloDatiEstensioni;
  }

//******************************************************************************
//***************************** Altri attributi*********************************
//******************************************************************************

  public void setDatiSezione0(ArticoloBase sezione0) throws CopyException{
    super.setEqual(sezione0);
  }

  protected TableManager getTableManager() throws SQLException{
    return ArticoloTM.getInstance();
  }

  public static Articolo elementWithKey(String key, int lockType) throws SQLException{
    return (Articolo) PersistentObject.elementWithKey(Articolo.class, key, lockType);
  }

  public static java.util.Vector retrieveList(String where, String orderBy, boolean optimistic) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException{
    if (cInstance == null)
      cInstance = (Articolo) Factory.createObject(Articolo.class);
    return PersistentObject.retrieveList(cInstance, where, orderBy, optimistic);
  }

//******************************************************************************
//********************************** Metodi ************************************
//******************************************************************************

  /**
   * Esegue la conversione di un valore da una unita di misura all'altra.
   * Se le due unita di misura sono le UM primaria e secondaria di magazzino,
   * bisogna che la conversione venga effettuata tenendo conto di fattore e operatore
   * di conversione impostati su ArticoloDatiMagaz, anzichè quelli della lista UM
   * associata all'articolo
   * @param valore BigDecimal
   * @param source UnitaMisura
   * @param target UnitaMisura
   * @return BigDecimal
   */
  /* Revisions:
   * Number Date        Owner  Description
   * 01279  16/01/2004  DZ     Adeguamento a modifica 1279 su ArticoloDatiMagaz: utilizzare per la conversione
   *                           OperConv e FattConv su ArticoloDatiMagaz se, oltre ad essere le due UM
   *                           quelle Prm e Sec di magazzino, l'operConver su DatiMagaz è diverso da NON_SIGNIFICATIVO
   * 01545  01/02/2004  DZ     Corrette parentesi nell'if sulle umMag
   */
//  public BigDecimal convertiUM(BigDecimal valore, UnitaMisura source, UnitaMisura target){
//    ArticoloDatiMagaz datiMagaz = getArticoloDatiMagaz();
//    UnitaMisura umPrmMag = datiMagaz.getUMPrmMag();
//    UnitaMisura umSecMag = datiMagaz.getUMSecMag();
//    if ((umPrmMag != null && umSecMag != null) &&
//        ((source.equals(umPrmMag) && target.equals(umSecMag)) ||
//        (source.equals(umSecMag) && target.equals(umPrmMag))) &&
//        datiMagaz.getOperConverUM() != datiMagaz.NON_SIGNIFICATIVO)
//      return ConvertitoreUM.converti(valore, source, target, getUMMagConvertitore());
//    if (getCategoriaUM() != null)
//      return getCategoriaUM().convertiUM(valore, source, target);
//    return ConvertitoreUM.converti(valore, source, target, getUMSpecifiche());
//  }

  // fix 10955 >

  /**
   * @deprecated
   * Metodo sostituito da convertiUM(BigDecimal valore, UnitaMisura source, UnitaMisura target, ArticoloVersione versione)
   * La logica del metodo è stata tutta riportata nel metodo privato :
   * convertiUMinternal(BigDecimal valore, UnitaMisura source, UnitaMisura target)
   * Se vi sono errori, questi vengono archiviati e sono disponibili tramite il metodo: getErrorsMessagges() e viene
   * eseguito il printStackTrace()
   */
  public BigDecimal convertiUM(BigDecimal valore, UnitaMisura source, UnitaMisura target){
    return convertiUM(valore, source, target, false); //Fix 11818
  }

  /**
   * @deprecated
   * Metodo sostituito da convertiUM(BigDecimal valore, UnitaMisura source, UnitaMisura target, ArticoloVersione versione)
   * La logica del metodo è stata tutta riportata nel metodo privato :
   * convertiUMinternal(BigDecimal valore, UnitaMisura source, UnitaMisura target)
   * Se vi sono errori, questi vengono archiviati e sono disponibili tramite il metodo: getErrorsMessagges() e viene
   * eseguito il printStackTrace()
   */
  //Fix 11818 aggiunto parametro controllaConversione
  public BigDecimal convertiUM(BigDecimal valore, UnitaMisura source, UnitaMisura target,  boolean controllaConversione){
    BigDecimal val = convertiUMinternal(valore, source, target);
    if (controllaConversione) { //Fix 11818
    List errors = controllaConversioneUM(valore, source, target, null, val);
    if (!errors.isEmpty()) {
      this.getErrorsMessagges().addAll(errors);
      ErrorMessage err = (ErrorMessage)errors.get(0);
      String msg = err.getText() + " " + err.getLongText();
      Exception t = new Exception(msg);
      t.printStackTrace(Trace.excStream);
    }
    }
    return val;
  }

  /**
   * Permette la conversione delle UM dell'articolo: se l'articolo è gestito a versione estesa, recupera la logica di
   * conversione dalla rispettiva classe ArticoloVersione.
   * Se vi sono errori, questi vengono archiviati e sono disponibili tramite il metodo: getErrorsMessagges()
   *
   * @param valore BigDecimal
   * @param source UnitaMisura
   * @param target UnitaMisura
   * @param versione ArticoloVersione
   * @return BigDecimal
   */
  public BigDecimal convertiUM(BigDecimal valore, UnitaMisura source, UnitaMisura target, ArticoloVersione versione){
    BigDecimal val = convertiUM(valore, source, target, versione, true); //Fix 11818
    return val;
  }

  /**
   * Permette la conversione delle UM dell'articolo: se l'articolo è gestito a versione estesa, recupera la logica di
   * conversione dalla rispettiva classe ArticoloVersione.
   * Se vi sono errori, questi vengono archiviati e sono disponibili tramite il metodo: getErrorsMessagges()
   *
   * @param valore BigDecimal
   * @param source UnitaMisura
   * @param target UnitaMisura
   * @param versione ArticoloVersione
   * @param controllaConversione boolean
   * @return BigDecimal
   */
  //Fix 11818 aggiunto parametro controllaConversione
  public BigDecimal convertiUM(BigDecimal valore, UnitaMisura source, UnitaMisura target, ArticoloVersione versione, boolean controllaConversione){
    BigDecimal val = ZERO_DEC;
    List errors = new ArrayList();
    if (this.hasVersioneEstesa()) {
      if (versione != null) {
        val = versione.convertiUM(valore, source, target);
      }
    }
    else {
      val = convertiUMinternal(valore, source, target);
    }
    if (controllaConversione) { //Fix 11818
    errors = controllaConversioneUM(valore, source, target, versione, val);
    if (!errors.isEmpty()) {
      this.getErrorsMessagges().addAll(errors);
    }
    }
    return val;
  }

  public List controllaConversioneUM(BigDecimal valore, UnitaMisura source, UnitaMisura target, ArticoloVersione versione, BigDecimal risultato) {
    List errors = new ArrayList();
    ErrorMessage err = null;

    boolean isValoreZero = false;
    if (valore == null || (valore != null && valore.compareTo(ZERO_DEC) == 0)) {
      isValoreZero = true;
    }

    boolean isRisultatoZero = false;
    if (risultato == null || (risultato != null && risultato.compareTo(ZERO_DEC) == 0)) {
      isRisultatoZero = true;
      // 11120 >
      if (risultato != null) {
        //risultato = risultato.setScale(2);//Fix 30871
		risultato = Q6Calc.get().setScale(risultato,2);//Fix 30871
      }
      // 11120 <
    }
    boolean isUMUguali = false;
    if (target != null && source != null && target.getKey().equals(source.getKey())) {
      isUMUguali = true;
    }
    if ((!isValoreZero && !isUMUguali) || (!isValoreZero && isRisultatoZero && isUMUguali)) {  // fix 11517

      if (this.hasVersioneEstesa() && versione == null || (versione != null && versione.getIdVersione() == null)) {
        String key = this.getIdAzienda().trim() + "/" +
            this.getIdArticolo().trim() + "/-";
        err = new ErrorMessage("THIP300209", new String[] {
                               key,
                               risultato != null ? risultato.toString() : "", //Fix 11818
                               valore != null ? valore.toString() : "", //Fix 11818
                               source.getIdUnitaMisura(),
                               target.getIdUnitaMisura()});
        if (err != null) {
          errors.add(err);
          String msg = err.getId() + ": " + err.getText() + " " + err.getLongText();
          Exception t = new Exception(msg);
          t.printStackTrace(Trace.excStream);
          this.addErrorMessage(err);
        }
      }

      if (isRisultatoZero && err == null) {
        String ver = versione == null || (versione != null && versione.getIdVersione() == null) ? "-" : versione.getIdVersione().toString();
        String key = this.getIdAzienda().trim() + "/" +
            this.getIdArticolo().trim() + "/" + ver.trim();
        err = new ErrorMessage("THIP300207", new String[] {
                               key,
                               risultato != null ? risultato.toString() : "", //Fix 11818
                               valore != null ? valore.toString() : "", //Fix 11818
                               source.getIdUnitaMisura(),
                               target.getIdUnitaMisura()});

        if (err != null) {
          errors.add(err);
          String msg = err.getId() + ": " + err.getText() + " " + err.getLongText();
          Exception t = new Exception(msg);
          t.printStackTrace(Trace.excStream);
          this.addErrorMessage(err);
        }
      }
    }
    return errors;
  }

  // Inizio 3785
  private BigDecimal convertiUMinternal(BigDecimal valore, UnitaMisura source, UnitaMisura target){
    if (valore == null || valore.compareTo(ZERO_DEC) == 0)// Fix 4962
      return ZERO_DEC;
    if (source.equals(target))
      return valore;

    ArticoloDatiMagaz datiMagaz = getArticoloDatiMagaz();
    String idUmPrmMag = datiMagaz.getIdUMPrmMag();
    String idUmSecMag = datiMagaz.getIdUMSecMag();
    String idSource = source.getIdUnitaMisura();
    String idTarget = target.getIdUnitaMisura();

    if ((idUmPrmMag != null && idUmSecMag != null) &&
        ((idSource.equals(idUmPrmMag) && idTarget.equals(idUmSecMag)) ||
        (idSource.equals(idUmSecMag) && idTarget.equals(idUmPrmMag))) &&
        datiMagaz.getOperConverUM() != datiMagaz.NON_SIGNIFICATIVO)
      return ConvertitoreUM.converti(valore, source, target, getUMMagConvertitore());
    //Fix 8243 PM Inizio
    if (datiMagaz.getOperConverUM() != datiMagaz.NON_SIGNIFICATIVO && (idTarget.equals(idUmSecMag) || idTarget.equals(idUmPrmMag)))
    {
        if (getCategoriaUM() != null)
        {
            UnitaMisura umRif = null;
            Iterator i = getCategoriaUM().getUMAssociate().iterator();
            while (i.hasNext())
            {
                CategoriaUMUM cum = (CategoriaUMUM)i.next();
                if (cum.getUMIsPrimaria())
                {
                    umRif = cum.getUnitaMisura();
                    break;
                }
            }
            String idUMRif = umRif.getIdUnitaMisura();
            if (!idUMRif.equals(idTarget) && (idUMRif.equals(idUmPrmMag) || idUMRif.equals(idUmSecMag)))
            {
                List umList = gestioneFattConversioneDatiMagCategoriaUM(idUMRif, idUmPrmMag, idUmSecMag, idSource, idTarget);
                return ConvertitoreUM.converti(valore, source, target, umList);
            }
        }
        else
        {
            UnitaMisura umRif = null;
            Iterator i = getUMSpecifiche().iterator();
            while (i.hasNext())
            {
                ArticoloUnitaMisura cum = (ArticoloUnitaMisura)i.next();
                if (cum.getRiferimento())
                {
                    umRif = cum.getUnitaMisura();
                    break;
                }
            }
            String idUMRif = umRif.getIdUnitaMisura();
            if (!idUMRif.equals(idTarget) && (idUMRif.equals(idUmPrmMag) || idUMRif.equals(idUmSecMag)))
            {
                List umList = gestioneFattConversioneDatiMagUMSpecifiche(idUMRif, idUmPrmMag, idUmSecMag, idSource, idTarget);
                return ConvertitoreUM.converti(valore, source, target, umList);
            }
        }
    }
    //Fix 8243 PM Fine
    if (getCategoriaUM() != null)
      return getCategoriaUM().convertiUM(valore, source, target);
    return ConvertitoreUM.converti(valore, source, target, getUMSpecifiche());
  }
  // Fine 3785
  // fix 10955 <


  //Fix 8243 PM Inizio

  protected List gestioneFattConversioneDatiMagUMSpecifiche(String idUMRif, String idUmPrmMag, String idUmSecMag, String idSource, String idTarget)
  {
      List umList = getUMSpecifiche();
      ArticoloDatiMagaz datiMagaz = getArticoloDatiMagaz();
      Iterator i = umList.iterator();
      List temp = new ArrayList();
      while (i.hasNext())
      {
          ArticoloUnitaMisura origine = (ArticoloUnitaMisura)i.next();
          ArticoloUnitaMisura clone = new ArticoloUnitaMisura();
          try
          {
              clone.setEqual(origine);
          }
          catch(CopyException e)
          {}
          temp.add(clone);
      }
      umList = temp;
      if (idUMRif.equals(idUmSecMag) && idTarget.equals(idUmPrmMag))
      {
          i = umList.iterator();
          while (i.hasNext())
          {
              ArticoloUnitaMisura cum = (ArticoloUnitaMisura)i.next();
              if (cum.getIdUnitaMisura().equals(idUmPrmMag))
              {
                  cum.setFattoreConverUM(datiMagaz.getFttConverUM());
                  if (datiMagaz.getOperConverUM() == ArticoloDatiMagaz.MOLTILPLICARE)
                      cum.setOperConverUM(ArticoloDatiMagaz.DIVIDERE);
                  else
                      cum.setOperConverUM(ArticoloDatiMagaz.MOLTILPLICARE);
                  break;
              }
          }
      }
      else
      {
          i = umList.iterator();
          while (i.hasNext())
          {
              ArticoloUnitaMisura cum = (ArticoloUnitaMisura)i.next();
              if (cum.getIdUnitaMisura().equals(idUmSecMag))
              {
                  cum.setFattoreConverUM(datiMagaz.getFttConverUM());
                  cum.setOperConverUM(datiMagaz.getOperConverUM());
                  break;
              }
          }
      }
      return umList;
  }

  protected List gestioneFattConversioneDatiMagCategoriaUM(String idUMRif, String idUmPrmMag, String idUmSecMag, String idSource, String idTarget)
  {
      List umList = getCategoriaUM().getUMAssociate();
      ArticoloDatiMagaz datiMagaz = getArticoloDatiMagaz();
      Iterator i = umList.iterator();
      List temp = new ArrayList();
      while (i.hasNext())
      {
          CategoriaUMUM origine = (CategoriaUMUM)i.next();
          CategoriaUMUM clone = new CategoriaUMUM();
          try
          {
              clone.setEqual(origine);
          }
          catch(CopyException e)
          {}
          temp.add(clone);
      }
      umList = temp;
      if (idUMRif.equals(idUmSecMag) && idTarget.equals(idUmPrmMag))
      {
          i = umList.iterator();
          while (i.hasNext())
          {
              CategoriaUMUM cum = (CategoriaUMUM)i.next();
              if (cum.getIdUnitaMisura().equals(idUmPrmMag))
              {
                  cum.setFattoreConverUM(datiMagaz.getFttConverUM());
                  if (datiMagaz.getOperConverUM() == ArticoloDatiMagaz.MOLTILPLICARE)
                      cum.setOperConverUM(ArticoloDatiMagaz.DIVIDERE);
                  else
                      cum.setOperConverUM(ArticoloDatiMagaz.MOLTILPLICARE);
                  break;
              }
          }
      }
      else
      {
          i = umList.iterator();
          while (i.hasNext())
          {
              CategoriaUMUM cum = (CategoriaUMUM)i.next();
              if (cum.getIdUnitaMisura().equals(idUmSecMag))
              {
                  cum.setFattoreConverUM(datiMagaz.getFttConverUM());
                  cum.setOperConverUM(datiMagaz.getOperConverUM());
                  break;
              }
          }
      }
      return umList;
  }

  //Fix 8243 PM Fine

  /**
   * Costruisce la lista di UM costituita dalle due UM impostate su ArticoloDatiMagaz,
   * necessaria affinchè venga passata al metodo che esegue la conversione.
   * Operatore e fattore di conversione vengono letti da ArtidoloDatiMagaz e ricostruiti supponendo
   * come unita di misura di riferimento umPrmMag.
   * @return List
   */
  /* Revisions:
   * Number Date        Owner  Description
   * 01294  20/01/2004  DZ     Aggiunta scale al fattore di conversione dell'um fittizia di riferimento
   *                           (uguale a quella del fattore di conversione dell'umSecondaria)
   *                           per evitare problemi di arrotondmento.
   */
  protected List getUMMagConvertitore(){
    ArticoloDatiMagaz datiMagaz = getArticoloDatiMagaz();
    UnitaMisura umPrmMag = datiMagaz.getUMPrmMag();
    UnitaMisura umSecMag = datiMagaz.getUMSecMag();

    List umList = new ArrayList();

    ArticoloUnitaMisura artUmPrmMag = new ArticoloUnitaMisura();
    artUmPrmMag.setUnitaMisura(umPrmMag);
    artUmPrmMag.setFattoreConverUM(new BigDecimal("1").setScale(datiMagaz.getFttConverUM().scale()));
    artUmPrmMag.setOperConverUM(datiMagaz.getOperConverUM());
    artUmPrmMag.setRiferimento(true);
    umList.add(artUmPrmMag);

    ArticoloUnitaMisura artUmSecMag = new ArticoloUnitaMisura();
    artUmSecMag.setUnitaMisura(umSecMag);
    artUmSecMag.setFattoreConverUM(datiMagaz.getFttConverUM());
    artUmSecMag.setOperConverUM(datiMagaz.getOperConverUM());
    umList.add(artUmSecMag);

    return umList;
  }

  //Fix 09608 FR Inizio : spostato nella classe genitore ArticoloBase.java
//  /**
//   * Restituisce la collezione di unità di misura dell'articolo, sia che queste siano
//   * le unità di misura specifiche dell'articolo, sia che siano quelle della categoriaUM
//   * associata all'articolo.
//   */
//  public List getUnitaMisuraList(){
//    List um = new ArrayList();
//    if (getCategoriaUM() != null){
//      List UMAssociate = getCategoriaUM().getUMAssociate();
//      for (int i = 0; i < UMAssociate.size(); i++){
//        um.add(((CategoriaUMUM)UMAssociate.get(i)).getUnitaMisura());
//      }
//      return um;
//    }
//    List UMSpecifiche = getUMSpecifiche();
//    for (int i = 0; i < UMSpecifiche.size(); i++){
//      um.add(((ArticoloUnitaMisura)UMSpecifiche.get(i)).getUnitaMisura());
//    }
//    return um;
//  }
  //Fix 09608 FR Fine

  protected void checkLivelloAziendale() throws SQLException {
  }

  /**
   * Save
   * @return int
   * @throws SQLException
   */
  /* Revisions:
   * Date         Owner   Description
   * 01/10/2003   DZ      Aggiunto test su errorcode prima di eseguire l'apertura saldi su sezione magaz.
   * 22/10/2003   DZ      Settato a false iAggiornaStatoDatiIdent (durante il caricam di massa e la copia dell'articolo
   *                      non si deve modificare lo statoSez1, probabilmente già valorizzato di suo)
   */
  public int save() throws SQLException {
	//inizio 28684
	  
	if(iSezioneArticoloWF != '-'){
		if ((getPropagazioneHandler().getPropagatore() == null)/* && (!hasFatherGroup() || (hasFatherGroup() && isAziendaIndipendente())) */)
	    { 
	        getPropagazioneHandler().setPropagazioneAttiva(onDB);
	        int rcWf = saveSezionePerWf(); 
	        if(rcWf < 0)
				return rcWf;
	    }
	}
	//fine 28684
	iIsNew = !onDB;
    iAggiornaStatoDatiIdent = false;

    int retSave = super.save();
    
   	//retSave = salvaArticoliCosti(retSave); // 18087 // Fix 21735

//	    /*
//	    Brescia 02/07/2003
//	    PJ: gestione dell'apertura saldo automatica. Questa gestione è di norma gestita
//	    dalla sezione magazzino/logistica tranne nel caso della copia, perché in questo
//	    caso al salvataggio della sezione non è ancora presente la versione 1, necessaria
//	    alla creazione della scheda di magazzino.
//	    */
//	    if (retSave >= ErrorCodes.OK){
//	      iArticoloDatiMagaz.setAperturaSaldiAnnullata(false);
//	      ErrorMessage erroreAperturaSaldo = iArticoloDatiMagaz.aperturaSaldo();
//	      if (erroreAperturaSaldo != null)
//	        throw new ThipException(erroreAperturaSaldo);
//	    }

    retSave = this.saveArticoloLogis(retSave, this);

    return retSave;
  }

  public int saveSezionePerWf() {
	  int rc = 0;
	  try {
	    	switch (iSezioneArticoloWF) {
	         case SezioneBaseArticolo.DATI_DI_MAGAZZINO:
	        	 rc = this.getArticoloDatiMagazWf().save();
	        	 break;
	        case SezioneBaseArticolo.DATI_DI_CONTABILI:
	            rc = this.getArticoloDatiContab().save();
	            break;
	         case SezioneBaseArticolo.DATI_DI_CONTROLLO:
	           rc = this.getArticoloDatiQualita().save();
	           break;
	         case SezioneBaseArticolo.DATI_DI_COSTO:
	           rc = this.getArticoloDatiCosto().save();
	           break;
	         case SezioneBaseArticolo.DATI_DI_PIANIFICAZIONE:
	           rc = this.getArticoloDatiPianif().save();
	           break;
	         case SezioneBaseArticolo.DATI_DI_PRODUZIONE:
	           rc = this.getArticoloDatiProduz().save();
	           break;
	         case SezioneBaseArticolo.DATI_DI_STABILIMENTO:
	           rc = this.getArticoloDatiStabil().save();
	           break;
	         case SezioneBaseArticolo.DATI_DI_VENDITA:
	           rc = this.getArticoloDatiVendita().save();
	           break;
	         case SezioneBaseArticolo.DATI_IDENTIFICATIVI:
	           rc = this.getArticoloDatiIdent().save();
	           break;
	         case SezioneBaseArticolo.DATI_DI_ACQUISTO:
		           rc = this.getArticoloDatiAcquisto().save();
		           break;
	         case SezioneBaseArticolo.DATI_DI_ESTENSIONI:
	        	 rc = this.getArticoloDatiEstensioniWf().save();
	        	 break;
	         case SezioneBaseArticolo.DATI_DI_VERSIONI:
	        	 rc = this.getArticoloDatiVersioniWf().save();
	        	 break;
	         case SezioneBaseArticolo.DATI_TECNICI:
	           rc = this.getArticoloDatiTecnici().save();
	    	}
	    	if(rc <0 ) return rc;
	   		int rcwf = aggiornaWf();
	   		if(rcwf <0)
	   			 return rcwf;
       	 	
    	} catch (SQLException e) {
			e.printStackTrace(Trace.excStream);
		}
	  	return rc;
	  }
/**
   * saveOwnedObjects
   * @param rc int
   * @return int
   * @throws SQLException
   */
  /**
   * Revisions
   * Date         Owner     Description
   * 20/10/2003   DZ        Aggiunta chiamata a setArticoloLogisDisabilitato
   */
  public int saveOwnedObjects(int rc) throws SQLException{
    super.saveOwnedObjects(rc);

    //...se la save viene chiamata dal codificatore, non deve essere eseguita la save delle singole sezioni,
    //...perchè viene chiamata la save sui bodc di ciascuna sezione dopo che son stati settati anche
    //...gli attributi codificati
    //...non devono essere eseguite le operazioni di aggiornamento dell'articolo Logis
    //...dalle singole sezioni, l'operazione viene eseguita una sola volta nella save di Articolo

    if (!iFromCodificatore){
      if (iArticoloDatiIdent != null){
        iArticoloDatiIdent.setOnDB(true);
        iArticoloDatiIdent.setAggiornamentoDatiGlobali(false);
        iArticoloDatiIdent.setArticoloLogisDisabilitato(true);
      }
      if (iArticoloDatiTecnici != null){
        iArticoloDatiTecnici.setOnDB(true);
        iArticoloDatiTecnici.setAggiornamentoDatiGlobali(false);
        iArticoloDatiTecnici.setArticoloLogisDisabilitato(true);
      }
      if (iArticoloDatiVendita != null){
        iArticoloDatiVendita.setOnDB(true);
        iArticoloDatiVendita.setAggiornamentoDatiGlobali(false);
        iArticoloDatiVendita.setArticoloLogisDisabilitato(true);
      }
      if (iArticoloDatiProduz != null){
        iArticoloDatiProduz.setOnDB(true);
        iArticoloDatiProduz.setAggiornamentoDatiGlobali(false);
        iArticoloDatiProduz.setArticoloLogisDisabilitato(true);
      }
      if (iArticoloDatiMagaz != null){
        iArticoloDatiMagaz.setOnDB(true);
        iArticoloDatiMagaz.setAggiornamentoDatiGlobali(false);
        iArticoloDatiMagaz.setArticoloLogisDisabilitato(true);
      }

      if (iArticoloDatiAcquisto != null){
        iArticoloDatiAcquisto.setAggiornamentoDatiGlobali(false);
        iArticoloDatiAcquisto.setOnDB(true);
      }
      if (iArticoloDatiPianif != null){
        iArticoloDatiPianif.setAggiornamentoDatiGlobali(false);
        iArticoloDatiPianif.setOnDB(true);
      }
      if (iArticoloDatiContab != null){
        iArticoloDatiContab.setAggiornamentoDatiGlobali(false);
        iArticoloDatiContab.setOnDB(true);
      }
      if (iArticoloDatiCosto != null){
        iArticoloDatiCosto.setAggiornamentoDatiGlobali(false);
        iArticoloDatiCosto.setOnDB(true);
      }
      if (iArticoloDatiQualita != null){
        iArticoloDatiQualita.setAggiornamentoDatiGlobali(false);
        iArticoloDatiQualita.setOnDB(true);
      }
      if (iArticoloDatiStabil != null){
        iArticoloDatiStabil.setAggiornamentoDatiGlobali(false);
        iArticoloDatiStabil.setOnDB(true);
      }
      if (iArticoloDatiVersioni != null){
        iArticoloDatiVersioni.setAggiornamentoDatiGlobali(false);
        iArticoloDatiVersioni.setOnDB(true);
      }
//Mod. 02853 SL - Gestione attributi estendibili (x CM)
      if (iArticoloDatiEstensioni != null){
        iArticoloDatiEstensioni.setOnDB(true);
      }

      rc = iArticoloDatiIdent != null ? iArticoloDatiIdent.save(rc) : rc;

      if (iArticoloDatiTecnici != null){
        //...se siamo in COPIA, il livello minimo non deve essere copiato, ma impostato a zero
        if (iIsNew){
          iArticoloDatiTecnici.setLivelloMinimo(new Integer(0));
          iArticoloDatiTecnici.setLivelloMinimoLav(new Integer(0));
          iArticoloDatiTecnici.setLivelloMinimoModello(new Integer(0)); //...FIX04088 - DZ
        }
        rc = iArticoloDatiTecnici.save(rc);
      }

      rc = iArticoloDatiVendita != null ? iArticoloDatiVendita.save(rc) : rc;
      rc = iArticoloDatiAcquisto != null ? iArticoloDatiAcquisto.save(rc) : rc;
      rc = iArticoloDatiPianif != null ? iArticoloDatiPianif.save(rc) : rc;

      if (iArticoloDatiProduz != null){ //...FIX05042 - DZ
        //...se siamo in COPIA, la cfg standard non deve essere copiata, ma impostata a null
        if (iIsNew)
          iArticoloDatiProduz.setIdConfigurazioneStd(null);
        rc = iArticoloDatiProduz.save(rc);
      }

      rc = iArticoloDatiEstensioni != null ? iArticoloDatiEstensioni.save(rc) : rc;//Mod. 02853 SL

      /*
      Brescia 02/07/2003
      PJ: annullo l'apertura saldi automatica della sezione di magazzino/logistica
      perché in copia se ne occupa direttamente il metodo save() dell'articolo.
      */
      if (iArticoloDatiMagaz != null) {
          iArticoloDatiMagaz.setAperturaSaldiAnnullata(true);
          //annulaDatiSaldiPerCopia();//Fix 18088 // Fix 21735 
          iArticoloDatiMagaz.save(rc);
      }
      /*
      PJ - FINE
      */

     // Fix 8436 - Inizio
      //rc = iArticoloDatiContab != null ? iArticoloDatiContab.save(rc) : rc;
      if (iArticoloDatiContab != null ) {
          //...se siamo in COPIA deve essere creato un nuovo gruppo conti specifico con gli stessi valori di quello originale
          if (iIsNew){
              GruppoContiSpcCA gruppoContiSpecificoOld = iArticoloDatiContab.getRiferimVociCA().getInternoGrpCntCA();
              GruppoContiSpcCA gruppoContiSpecificoNew = (GruppoContiSpcCA)Factory.createObject(GruppoContiSpcCA.class);
              try {
                  gruppoContiSpecificoNew.setEqual(gruppoContiSpecificoOld);
                  gruppoContiSpecificoNew.setIdInternoGrpCntCA(null); // così al salvataggio mi viene assegnato un nuovo id
                  iArticoloDatiContab.getRiferimVociCA().setInternoGrpCntCA(gruppoContiSpecificoNew);
              }
              catch(CopyException e){
                  e.printStackTrace();
              }
          }
          iArticoloDatiContab.save(rc);
      }
      // Fix 8436 - Fine
    
      rc = iArticoloDatiCosto != null ? iArticoloDatiCosto.save(rc) : rc;
    
     
      rc = iArticoloDatiQualita != null ? iArticoloDatiQualita.save(rc) : rc;
      rc = iArticoloDatiStabil != null ? iArticoloDatiStabil.save(rc) : rc;

      //...1-!iIsNew = in caso di COPY non deve esser fatta la save delle versioni dell'articolo da copiare
      //...(ma solo la creazione della prima versione nella save di ArticoloBase)
      //...2-|| (iIsNew &&... ) = in caso di new o copy devono essere salvate le versioni se l'azienda
      //...appartiene ad un gruppo (tipo azienda A o G e livello getione entità G)
      //...(e quindi non viene creata la prima versione nella save di ArticoloBase)

      if (!iIsNew || (iIsNew /*&& getAziendaAndModGest() == 'G'*/ && hasFatherGroup() && getModGestioneEntita() == GRUPPO))
        rc= iArticoloDatiVersioni != null ? iArticoloDatiVersioni.save(rc) : rc;
      // fix 11302
      //else if (this.getIdArticoloInCopia()!=null && this.hasVersioneEstesa()){//Fix 22583
      else if (isInCopiaDaIC()|| ( this.getIdArticoloInCopia()!=null && this.hasVersioneEstesa())){//Fix 22583        
        rc= iArticoloDatiVersioni != null ? iArticoloDatiVersioni.save(rc) : rc;
      }

    }
    return rc;

  }

  //Fix 18088 inizio
  private void annulaDatiSaldiPerCopia() {
  	iArticoloDatiMagaz.setScortaMinima(null);
  	iArticoloDatiMagaz.setLivelloServizio(null);
  	iArticoloDatiMagaz.setGiorniCopertura(null);
  	iArticoloDatiMagaz.setQtaPuntoRiordino(null);
  	iArticoloDatiMagaz.setLottoEconomico(null);
  	iArticoloDatiMagaz.setNumMaxGGNonMovim(null);
  	iArticoloDatiMagaz.setNumMovInventario(null);
  	iArticoloDatiMagaz.setIdClasseABCInv(null);
  	iArticoloDatiMagaz.setTipoInventario(ArticoloDatiMagaz.NON_SIGNIFICATIVO);
  	if(iArticoloDatiMagaz.getTmpApprovPrd() != null){
	  	iArticoloDatiMagaz.getTmpApprovPrd().setTmpApprovStd(null);
	  	iArticoloDatiMagaz.getTmpApprovPrd().setTmpApprovPrp(null);
	  	iArticoloDatiMagaz.getTmpApprovPrd().setTmpApprovMed(null);
	  	iArticoloDatiMagaz.getTmpApprovPrd().setTmpApprovRtf(null);
  	}
  	
  	if(iArticoloDatiMagaz.getTmpApprovPrd() != null){
	  	iArticoloDatiMagaz.getTmpApprovAcq().setTmpApprovStd(null);
	  	iArticoloDatiMagaz.getTmpApprovAcq().setTmpApprovPrp(null);
	  	iArticoloDatiMagaz.getTmpApprovAcq().setTmpApprovMed(null);
	  	iArticoloDatiMagaz.getTmpApprovAcq().setTmpApprovRtf(null);
  	}

  	if(iArticoloDatiMagaz.getTmpApprovPrd() != null){
	  	iArticoloDatiMagaz.getTmpApprovClav().setTmpApprovStd(null);
	  	iArticoloDatiMagaz.getTmpApprovClav().setTmpApprovPrp(null);
	  	iArticoloDatiMagaz.getTmpApprovClav().setTmpApprovMed(null);
	  	iArticoloDatiMagaz.getTmpApprovClav().setTmpApprovRtf(null);
  	}
  	
  	iArticoloDatiMagaz.setTmpApprovCumPrd(null);
  	iArticoloDatiMagaz.setTmpApprovCumAcq(null);
  	iArticoloDatiMagaz.setTmpApprovCumClav(null);		
	}
  //Fix 18088 fine
  
	public boolean initializeOwnedObjects(boolean ret){
    ret = super.initializeOwnedObjects(ret);
    if (isDeepRetrieveEnabled()){
      getArticoloDatiIdent();
      getArticoloDatiTecnici();
      getArticoloDatiVendita();
      getArticoloDatiAcquisto();
      getArticoloDatiPianif();
      getArticoloDatiProduz();
      getArticoloDatiMagaz();
      getArticoloDatiContab();
      getArticoloDatiCosto();
      getArticoloDatiQualita();
      getArticoloDatiStabil();
      getArticoloDatiVersioni();
      getArticoloDatiEstensioni();
    }
    return ret;
  }

  public int deleteOwnedObjects() throws SQLException{
    int ret = super.deleteOwnedObjects();
    this.setDeepRetrieveEnabled(true);
    ret = getArticoloDatiVersioni().delete(ret);
    return ret;
  }

//******************************************************************************
//*********************** Attributi ArticoloDatiIdent **************************
//******************************************************************************

//....................................................... statoArticoloDatiIdent
  public void setStatoArticoloDatiIdent(char iStatoArticoloDatiIdent){
    getArticoloDatiIdent().setStatoArticoloDatiIdent(iStatoArticoloDatiIdent);
  }

  public char getStatoArticoloDatiIdent(){
    return getArticoloDatiIdent().getStatoArticoloDatiIdent();
  }

//.................................................................... tipoParte
  public void setTipoParte(char iTipoParte){
    getArticoloDatiIdent().setTipoParte(iTipoParte);
  }

  public char getTipoParte(){
    return getArticoloDatiIdent().getTipoParte();
  }

//.............................................................. articoloTecnico
  public void setIdArticoloTecnico(String iIdArticoloTecnico){
    getArticoloDatiIdent().setIdArticoloTecnico(iIdArticoloTecnico);
  }

  public String getIdArticoloTecnico(){
    return getArticoloDatiIdent().getIdArticoloTecnico();
  }

//.............................................................. vecchioArticolo
  public void setVecchioArticolo(String iVecchioArticolo){
    getArticoloDatiIdent().setVecchioArticolo(iVecchioArticolo);
  }

  public String getVecchioArticolo(){
    return getArticoloDatiIdent().getVecchioArticolo();
  }

//........................................................... vecchiaDescrizione
  public void setVecchiaDescrizione(String iVecchiaDescrizione){
    getArticoloDatiIdent().setVecchiaDescrizione(iVecchiaDescrizione);
  }

  public String getVecchiaDescrizione(){
    return getArticoloDatiIdent().getVecchiaDescrizione();
  }

//.............................................................. dataValNuovoArt
  public void setDataValNuovoArt(java.sql.Date iDataValNuovoArt){
    getArticoloDatiIdent().setDataValNuovoArt(iDataValNuovoArt);
  }

  public java.sql.Date getDataValNuovoArt(){
    return getArticoloDatiIdent().getDataValNuovoArt();
  }

//................................................................ Proxy ClasseC
  public void setClasseC(ClasseC classeC) {
    getArticoloDatiIdent().setClasseC(classeC);
  }

  public ClasseC getClasseC() {
    return getArticoloDatiIdent().getClasseC();
  }

  public void setClasseCKey(String key) {
    getArticoloDatiIdent().setClasseCKey(key);
  }

  public String getClasseCKey() {
    return getArticoloDatiIdent().getClasseCKey();
  }

  public void setIdClasseC(String idClasseC) {
    getArticoloDatiIdent().setIdClasseC(idClasseC);
  }

  public String getIdClasseC() {
    return getArticoloDatiIdent().getIdClasseC();
  }

//................................................................ Proxy ClasseD
  public void setClasseD(ClasseD classeD) {
    getArticoloDatiIdent().setClasseD(classeD);
  }

  public ClasseD getClasseD() {
    return getArticoloDatiIdent().getClasseD();
  }

  public void setClasseDKey(String key) {
    getArticoloDatiIdent().setClasseDKey(key);
  }

  public String getClasseDKey() {
    return getArticoloDatiIdent().getClasseDKey();
  }

  public void setIdClasseD(String idClasseD) {
    getArticoloDatiIdent().setIdClasseD(idClasseD);
  }

  public String getIdClasseD() {
    return getArticoloDatiIdent().getIdClasseD();
  }

//................................................................ Proxy ClasseE
  public void setClasseE(ClasseE classeE) {
    getArticoloDatiIdent().setClasseE(classeE);
  }

  public ClasseE getClasseE() {
    return getArticoloDatiIdent().getClasseE();
  }

  public void setClasseEKey(String key) {
    getArticoloDatiIdent().setClasseEKey(key);
  }

  public String getClasseEKey() {
    return getArticoloDatiIdent().getClasseEKey();
  }

  public void setIdClasseE(String idClasseE) {
    getArticoloDatiIdent().setIdClasseE(idClasseE);
  }

  public String getIdClasseE() {
    return getArticoloDatiIdent().getIdClasseE();
  }

//....................................................... Proxy LineaCommerciale
  public void setLineaCommerciale(LineaCommerciale lineaCommerciale) {
    getArticoloDatiIdent().setLineaCommerciale(lineaCommerciale);
  }

  public LineaCommerciale getLineaCommerciale() {
    return getArticoloDatiIdent().getLineaCommerciale();
  }

  public void setLineaCommercialeKey(String key) {
    getArticoloDatiIdent().setLineaCommercialeKey(key);
  }

  public String getLineaCommercialeKey() {
    return getArticoloDatiIdent().getLineaCommercialeKey();
  }

  public void setIdLineaCommerciale(String idLineaCommerciale) {
    getArticoloDatiIdent().setIdLineaCommerciale(idLineaCommerciale);
  }

  public String getIdLineaCommerciale() {
    return getArticoloDatiIdent().getIdLineaCommerciale();
  }

//........................................................ Proxy ClasseMateriale
  public void setClasseMateriale(ClasseMateriale classeMateriale) {
    getArticoloDatiIdent().setClasseMateriale(classeMateriale);
  }

  public ClasseMateriale getClasseMateriale() {
    return getArticoloDatiIdent().getClasseMateriale();
  }

  public void setClasseMaterialeKey(String key) {
    getArticoloDatiIdent().setClasseMaterialeKey(key);
  }

  public String getClasseMaterialeKey() {
    return getArticoloDatiIdent().getClasseMaterialeKey();
  }

  public void setIdClasseMateriale(String idClasseMateriale) {
    getArticoloDatiIdent().setIdClasseMateriale(idClasseMateriale);
  }

  public String getIdClasseMateriale() {
    return getArticoloDatiIdent().getIdClasseMateriale();
  }

//.......................................................... Proxy ClasseFiscale
  public void setClasseFiscale(ClasseFiscale classeFiscale) {
    getArticoloDatiIdent().setClasseFiscale(classeFiscale);
  }

  public ClasseFiscale getClasseFiscale() {
    return getArticoloDatiIdent().getClasseFiscale();
  }

  public void setClasseFiscaleKey(String key) {
    getArticoloDatiIdent().setClasseFiscaleKey(key);
  }

  public String getClasseFiscaleKey() {
    return getArticoloDatiIdent().getClasseFiscaleKey();
  }

  public void setIdClasseFiscale(String idClasseFiscale) {
    getArticoloDatiIdent().setIdClasseFiscale(idClasseFiscale);
  }

  public String getIdClasseFiscale() {
    return getArticoloDatiIdent().getIdClasseFiscale();
  }

//....................................................... OneToMany UMSpecifiche
  public List getArticoloEsclusioni(){
    return getArticoloDatiIdent().getArticoloEsclusioni();
  }

  public List getEsclusioniDisponibili() throws SQLException{
    return ArticoloEsclusione.getEsclusioniDisponibili(getArticoloDatiIdent());
  }

//******************************************************************************
//*********************** Attributi ArticoloDatiTecnic *************************
//******************************************************************************

//...................................................................... Disegno
  public String getDisegno()  {
     return getArticoloDatiTecnici().getDisegno();
  }
  public void setDisegno(String disegno)  {
     getArticoloDatiTecnici().setDisegno(disegno);
  }

//.................................................................. CodiceBarre
  public String getCodiceBarre()  {
      return getArticoloDatiTecnici().getCodiceBarre();
  }
  public void setCodiceBarre(String codiceBarre)  {
     getArticoloDatiTecnici().setCodiceBarre(codiceBarre);
  }

//................................................................ LivelloMinimo
  public Integer getLivelloMinimo()  {
    return getArticoloDatiTecnici().getLivelloMinimo();
  }
  public void setLivelloMinimo(Integer livelloMinimo)  {
    getArticoloDatiTecnici().setLivelloMinimo(livelloMinimo);
  }

//............................................................. LivelloMinimoLav
  public Integer getLivelloMinimoLav()  {
    return getArticoloDatiTecnici().getLivelloMinimoLav();
  }
  public void setLivelloMinimoLav(Integer livelloMinimoLav)  {
    getArticoloDatiTecnici().setLivelloMinimoLav(livelloMinimoLav);
  }

//............................................................. livelloMinimoModello
  public Integer getLivelloMinimoModello()  {
    return getArticoloDatiTecnici().getLivelloMinimoModello();
  }
  public void setLivelloMinimoModello(Integer livelloMinimoModello)  {
    getArticoloDatiTecnici().setLivelloMinimoModello(livelloMinimoModello);
  }

//............................................................. DomandaSenzaCfg
  public boolean isDomandaSenzaCfg()  {
    return getArticoloDatiTecnici().isDomandaSenzaCfg();
  }
  public void setDomandaSenzaCfg(boolean domandaSenzaCfg)  {
    getArticoloDatiTecnici().setDomandaSenzaCfg(domandaSenzaCfg);
  }


//...................................................................... Altezza
  public BigDecimal getAltezza()  {
    return getArticoloDatiTecnici().getAltezza();
  }
  public void setAltezza(BigDecimal altezza)  {
   getArticoloDatiTecnici().setAltezza(altezza);
  }

//.................................................................... Lunghezza
  public BigDecimal getLunghezza()  {
    return getArticoloDatiTecnici().getLunghezza();
  }
  public void setLunghezza(BigDecimal lunghezza)  {
    getArticoloDatiTecnici().setLunghezza(lunghezza);
  }

//.................................................................... Larghezza
  public BigDecimal getLarghezza()  {
    return getArticoloDatiTecnici().getLarghezza();
  }
  public void setLarghezza(BigDecimal larghezza)  {
    getArticoloDatiTecnici().setLarghezza(larghezza);
  }

//......................................................................... Peso
  public BigDecimal getPeso()  {
    return getArticoloDatiTecnici().getPeso();
  }
  public void setPeso(BigDecimal peso)  {
    getArticoloDatiTecnici().setPeso(peso);
  }

//......................................................................... PesoNetto
  public BigDecimal getPesoNetto()  {
    return getArticoloDatiTecnici().getPesoNetto();
  }
  public void setPesoNetto(BigDecimal pesoNetto)  {
    getArticoloDatiTecnici().setPesoNetto(pesoNetto);
  }

//....................................................................... Volume
  public BigDecimal getVolume()  {
    return getArticoloDatiTecnici().getVolume();
  }
  public void setVolume(BigDecimal volume)  {
    getArticoloDatiTecnici().setVolume(volume);
  }

//.............................................. GruppoDistinta proxy managament
  public ArticoloBase getGruppoDistinta()  {
    return getArticoloDatiTecnici().getGruppoDistinta();
  }

  public void setGruppoDistinta(ArticoloBase gruppoDistinta)  {
    getArticoloDatiTecnici().setGruppoDistinta(gruppoDistinta);
  }

  public String getGruppoDistintaKey()  {
    return getArticoloDatiTecnici().getGruppoDistintaKey();
  }

  public void setGruppoDistintaKey(String gruppoDistintaKey)  {
    getArticoloDatiTecnici().setGruppoDistintaKey(gruppoDistintaKey);
  }

  public String getIdGruppoDistinta()  {
    return getArticoloDatiTecnici().getIdGruppoDistinta();
  }

  public void setIdGruppoDistinta(String idGruppoDistinta)  {
    getArticoloDatiTecnici().setIdGruppoDistinta(idGruppoDistinta);
  }

//................................................. GruppoCiclo proxy managament
//  public ArticoloBase getGruppoCiclo()  {
//    return getArticoloDatiTecnici().getGruppoCiclo();
//  }
//
//  public void setGruppoCiclo(ArticoloBase gruppoCiclo)  {
//    getArticoloDatiTecnici().setGruppoCiclo(gruppoCiclo);
//  }
//
//  public String getGruppoCicloKey()  {
//    return getArticoloDatiTecnici().getGruppoCicloKey();
//  }
//
//  public void setGruppoCicloKey(String gruppoCicloKey)  {
//    getArticoloDatiTecnici().setGruppoCicloKey(gruppoCicloKey);
//  }
//
//  public String getIdGruppoCiclo()  {
//    return getArticoloDatiTecnici().getIdGruppoCiclo();
//  }
//
//  public void setIdGruppoCiclo(String idGruppoCiclo)  {
//    getArticoloDatiTecnici().setIdGruppoCiclo(idGruppoCiclo);
//  }

//............................................... GruppoModello proxy managament
  public ArticoloBase getGruppoModello()  {
    return getArticoloDatiTecnici().getGruppoModello();
  }

  public void setGruppoModello(ArticoloBase gruppoModello)  {
    getArticoloDatiTecnici().setGruppoModello(gruppoModello);
  }

  public String getGruppoModelloKey()  {
    return getArticoloDatiTecnici().getGruppoModelloKey();
  }

  public void setGruppoModelloKey(String gruppoModelloKey)  {
    getArticoloDatiTecnici().setGruppoModelloKey(gruppoModelloKey);
  }

  public String getIdGruppoModello()  {
    return getArticoloDatiTecnici().getIdGruppoModello();
  }

  public void setIdGruppoModello(String idGruppoModello)  {
    getArticoloDatiTecnici().setIdGruppoModello(idGruppoModello);
  }

//................................................... UMTecnica proxy managament
  public UnitaMisura getUMTecnica()  {
    return getArticoloDatiTecnici().getUMTecnica();
  }

  public void setUMTecnica(UnitaMisura UMTecnica)  {
    getArticoloDatiTecnici().setUMTecnica(UMTecnica);
  }

  public String getUMTecnicaKey()  {
    return getArticoloDatiTecnici().getUMTecnicaKey();
  }

  public void setUMTecnicaKey(String UMtecnicaKey)  {
    getArticoloDatiTecnici().setUMTecnicaKey(UMtecnicaKey);
  }

  public String getIdUMTecnica()  {
    return getArticoloDatiTecnici().getIdUMTecnica();
  }

  public void setIdUMTecnica(String idUMTecnica)  {
    getArticoloDatiTecnici().setIdUMTecnica(idUMTecnica);
  }

//................................................ UMDimensioni proxy managament
  public UnitaMisura getUMDimensioni()  {
    return getArticoloDatiTecnici().getUMDimensioni();
  }

  public void setUMDimensioni(UnitaMisura UMAltezza)  {
    getArticoloDatiTecnici().setUMDimensioni(UMAltezza);
  }

  public String getUMDimensioniKey()  {
    return getArticoloDatiTecnici().getUMDimensioniKey();
  }

  public void setUMDimensioniKey(String UMAltezzaKey)  {
    getArticoloDatiTecnici().setUMDimensioniKey(UMAltezzaKey);
  }

  public String getIdUMDimensioni()  {
    return getArticoloDatiTecnici().getIdUMDimensioni();
  }

  public void setIdUMDimensioni(String idUMAltezza)  {
    getArticoloDatiTecnici().setIdUMDimensioni(idUMAltezza);
  }

//.....................................................  UMPeso proxy managament
  public UnitaMisura getUMPeso()  {
    return getArticoloDatiTecnici().getUMPeso();
  }

  public void setUMPeso(UnitaMisura UMPeso)  {
    getArticoloDatiTecnici().setUMPeso(UMPeso);
  }

  public String getUMPesoKey()  {
    return getArticoloDatiTecnici().getUMPesoKey();
  }

  public void setUMPesoKey(String UMPesoKey)  {
    getArticoloDatiTecnici().setUMPesoKey(UMPesoKey);
  }

  public String getIdUMPeso()  {
    return getArticoloDatiTecnici().getIdUMPeso();
  }

  public void setIdUMPeso(String idUMPeso)  {
    getArticoloDatiTecnici().setIdUMPeso(idUMPeso);
  }

//.....................................................UMVolume proxy managament
  public UnitaMisura getUMVolume()  {
    return getArticoloDatiTecnici().getUMVolume();
  }

  public void setUMVolume(UnitaMisura UMVolume)  {
    getArticoloDatiTecnici().setUMVolume(UMVolume);
  }

  public String getUMVolumeKey()  {
    return getArticoloDatiTecnici().getUMVolumeKey();
  }

  public void setUMVolumeKey(String UMVolumeKey)  {
    getArticoloDatiTecnici().setUMVolumeKey(UMVolumeKey);
  }

  public String getIdUMVolume()  {
    return getArticoloDatiTecnici().getIdUMVolume();
  }

  public void setIdUMVolume(String idUMVolume)  {
    getArticoloDatiTecnici().setIdUMVolume(idUMVolume);
  }

//............................................. iArticoloScelte OneToMany managament
  public List getArticoloScelte()  {
    return getArticoloDatiTecnici().getArticoloScelte();
  }

//******************************************************************************
//********************* Attributi ArticoloDatiVendita **************************
//******************************************************************************

//............................................................... IdSchemaPrzVen
  public void setIdSchemaPrzVen(String idSchemaPrzVen)  {
    getArticoloDatiVendita().setIdSchemaPrzVen(idSchemaPrzVen);
  }

  public String getIdSchemaPrzVen()  {
    return getArticoloDatiVendita().getIdSchemaPrzVen();
  }

//............................................................. IdArticoloMatVen
  public void setIdArticoloMatVen(String idArticoloMatVen)  {
    getArticoloDatiVendita().setIdArticoloMatVen(idArticoloMatVen);
  }

  public String getIdArticoloMatVen()  {
    return getArticoloDatiVendita().getIdArticoloMatVen();
  }

//.................................................................. Provvigione
  public void setProvvigione(BigDecimal provvigione)  {
     getArticoloDatiVendita().setProvvigione(provvigione);
  }

  public BigDecimal getProvvigione()  {
    return getArticoloDatiVendita().getProvvigione();
  }

//............................................................... CodCommerciale
  public void setCodCommerciale(String codCommerciale)  {
    getArticoloDatiVendita().setCodCommerciale(codCommerciale);
  }

  public String getCodCommerciale()  {
    return getArticoloDatiVendita().getCodCommerciale();
  }

//................................................................ TipoAccantVen
  public void setTipoAccantVen(char tipoAccantVen)  {
    getArticoloDatiVendita().setTipoAccantVen(tipoAccantVen);
  }

  public char getTipoAccantVen()  {
    return getArticoloDatiVendita().getTipoAccantVen();
  }

//................................................................  ModAccantVen
  public void setModAccantVen(char modAccantVen)  {
    getArticoloDatiVendita().setModAccantVen(modAccantVen);
  }
  public char getModAccantVen()  {
    return getArticoloDatiVendita().getModAccantVen();
  }

//................................................................  PrezzoStdVen
                                                              //...FIX04792 - DZ
  public void setPrezzoStdVen(BigDecimal prezzoStd)  {
    getArticoloDatiVendita().setPrezzoStdVen(prezzoStd);
  }

  public BigDecimal getPrezzoStdVen()  {
    return getArticoloDatiVendita().getPrezzoStdVen();
  }

//............................................................. QtaMinimaVendita

  public void setQtaMinimaVendita(BigDecimal qtaMinimaVendita)  {
    getArticoloDatiVendita().setQtaMinimaVendita(qtaMinimaVendita);
  }

  public BigDecimal getQtaMinimaVendita()  {
    return getArticoloDatiVendita().getQtaMinimaVendita();
  }

//........................................................... QtaMultiplaVendita
  public void setQtaMultiplaVendita(BigDecimal qtaMultiplaVendita)  {
    getArticoloDatiVendita().setQtaMultiplaVendita(qtaMultiplaVendita);
  }

  public BigDecimal getQtaMultiplaVendita()  {
    return getArticoloDatiVendita().getQtaMultiplaVendita();
  }

//............................................................. TempoMinEvasione
  public void setTempoMinEvasione(Integer tempoMinEvasione)  {
    getArticoloDatiVendita().setTempoMinEvasione(tempoMinEvasione);
  }

  public Integer getTempoMinEvasione()  {
    return getArticoloDatiVendita().getTempoMinEvasione();
  }

//............................................................. TempoStdEvasione
  public void setTempoStdEvasione(Integer tempoStdEvasione)  {
   getArticoloDatiVendita().setTempoStdEvasione(tempoStdEvasione);
  }

  public Integer getTempoStdEvasione()  {
    return getArticoloDatiVendita().getTempoStdEvasione();
  }

//............................................................. TempoMaxEvasione
  public void setTempoMaxEvasione(Integer tempoMaxEvasione)  {
    getArticoloDatiVendita().setTempoMaxEvasione(tempoMaxEvasione);
  }

  public Integer getTempoMaxEvasione()  {
    return getArticoloDatiVendita().getTempoMaxEvasione();
  }

//............................................................... TipoCalcPrzKit
  public void setTipoCalcPrzKit(char tipoCalcPrzKit)  {
    getArticoloDatiVendita().setTipoCalcPrzKit(tipoCalcPrzKit);
  }

  public char getTipoCalcPrzKit()  {
    return getArticoloDatiVendita().getTipoCalcPrzKit();
  }

//.................................................................... MarkupKit
  public void setMarkupKit(BigDecimal markupKit)  {
    getArticoloDatiVendita().setMarkupKit(markupKit);
  }

  public BigDecimal getMarkupKit()  {
    return getArticoloDatiVendita().getMarkupKit();
  }

//........................................................... TipoStatisticheKit
  public void setTipoStatisticheKit(char tipoStatisticheKit)  {
    getArticoloDatiVendita().setTipoStatisticheKit(tipoStatisticheKit);
  }

  public char getTipoStatisticheKit()  {
    return getArticoloDatiVendita().getTipoStatisticheKit();
  }

//............................................. CategoriaPrezzo proxy managament
  public void setCategoriaPrezzo(CatPrezzo catPrezzo)  {
    getArticoloDatiVendita().setCategoriaPrezzo(catPrezzo);
  }

  public CatPrezzo getCategoriaPrezzo()  {
    return getArticoloDatiVendita().getCategoriaPrezzo();
  }

  public void setCategoriaPrezzoKey(String key)  {
    getArticoloDatiVendita().setCategoriaPrezzoKey(key);
  }

  public String getCategoriaPrezzoKey()  {
    return getArticoloDatiVendita().getCategoriaPrezzoKey();
  }

  public void setIdCategoriaPrezzo(String key)  {
    getArticoloDatiVendita().setIdCategoriaPrezzo(key);
  }

  public String getIdCategoriaPrezzo()  {
    return getArticoloDatiVendita().getIdCategoriaPrezzo();
  }

//.......................................... ArticAlternVendita proxy managament
  public void setArticAlternVendita(ArticoloBase articAlternVendita)  {
     getArticoloDatiVendita().setArticAlternVendita(articAlternVendita);
  }

  public ArticoloBase getArticAlternVendita()  {
    return getArticoloDatiVendita().getArticAlternVendita();
  }

  public void setArticAlternVenditaKey(String key)  {
    getArticoloDatiVendita().setArticAlternVenditaKey(key);
  }

  public String getArticAlternVenditaKey()  {
    return getArticoloDatiVendita().getArticAlternVenditaKey();
  }

  public void setIdArticAlternVendita(String key)  {
    getArticoloDatiVendita().setIdArticAlternVendita(key);
  }

  public String getIdArticAlternVendita()  {
    return getArticoloDatiVendita().getIdArticAlternVendita();
  }

//.......................................... AssoggettamentoIVA proxy managament
  public void setAssoggettamentoIVA(AssoggettamentoIVA assoggettamentoIVA)  {
    getArticoloDatiVendita().setAssoggettamentoIVA(assoggettamentoIVA);
  }

  public AssoggettamentoIVA getAssoggettamentoIVA()  {
    return getArticoloDatiVendita().getAssoggettamentoIVA();
  }

  public void setAssoggettamentoIVAKey(String key)  {
    getArticoloDatiVendita().setAssoggettamentoIVAKey(key);
  }

  public String getAssoggettamentoIVAKey()  {
    return getArticoloDatiVendita().getAssoggettamentoIVAKey();
  }

  public void setIdAssoggettamentoIVA(String key)  {
    getArticoloDatiVendita().setIdAssoggettamentoIVA(key);
  }

  public String getIdAssoggettamentoIVA()  {
    return getArticoloDatiVendita().getIdAssoggettamentoIVA();
  }

//............................................. iConfezione OneToMany managament
  public List getConfezione()  {
    return getArticoloDatiVendita().getConfezione();
  }

//.................................................................... UMDefault
  public UnitaMisura getUMDefaultVendita(){
    return getArticoloDatiVendita().getUMDefault();
  }

//................................................................... UMPrimaria
  public UnitaMisura getUMPrimariaVendita(){
    return getArticoloDatiVendita().getUMPrimaria();
  }

//................................................................. UMSecondarie
  public List getUMSecondarieVendita() {
    return getArticoloDatiVendita().getUMSecondarie();
  }

//............................................................ ImportoStdVendita
//  public ImportoInValuta getImportoStdVendita(){
//    return getArticoloDatiVendita().getImportoStd();
//  }

//******************************************************************************
//********************* Attributi ArticoloDatiAcquisto *************************
//******************************************************************************

//............................................................... IdSchemaPrzAcq
  public void setIdSchemaPrzAcq(String idSchemaPrzAcq)  {
    getArticoloDatiAcquisto().setIdSchemaPrzAcq(idSchemaPrzAcq);
  }

  public String getIdSchemaPrzAcq()  {
    return getArticoloDatiAcquisto().getIdSchemaPrzAcq();
  }

//............................................................. IdArticoloMatAcq
  public void setIdArticoloMatAcq(String idArticoloMatAcq)  {
    getArticoloDatiAcquisto().setIdArticoloMatAcq(idArticoloMatAcq);
  }

  public String getIdArticoloMatAcq()  {
    return getArticoloDatiAcquisto().getIdArticoloMatAcq();
  }

//............................................................... TolRicevimento
  public BigDecimal getTolRicevimento()  {
    return getArticoloDatiAcquisto().getTolRicevimento();
  }
  public void setTolRicevimento(BigDecimal tolRicevimento)  {
    getArticoloDatiAcquisto().setTolRicevimento(tolRicevimento);
  }

//.................................................................. CostoStdAcq
  public void setCostoStdAcq(BigDecimal costoStdAcq)
  {
    getArticoloDatiAcquisto().setCostoStdAcq(costoStdAcq);
  }

  public BigDecimal getCostoStdAcq()
  {
    return getArticoloDatiAcquisto().getCostoStdAcq();
  }
//.................................................................. CostoStdLav
  public void setCostoStdLav(BigDecimal costoStdLav)
  {
    getArticoloDatiAcquisto().setCostoStdLav(costoStdLav);
  }

  public BigDecimal getCostoStdLav()
  {
    return getArticoloDatiAcquisto().getCostoStdLav();
  }
//............................................. FornPrefLav OneToMany managament
  public List getArticoloFornitPrfLav()
  {
    return getArticoloDatiAcquisto().getArticoloFornitPrfLav();
  }

//............................................. FornPrefAcq OneToMany managament
  public List getArticoloFornitPrfAcq()
  {
    return getArticoloDatiAcquisto().getArticoloFornitPrfAcq();
  }

//.................................................................... UMDefault
  public UnitaMisura getUMDefaultAcquisto(){
    return getArticoloDatiAcquisto().getUMDefault();
  }

//................................................................... UMPrimaria
  public UnitaMisura getUMPrimariaAcquisto(){
    return getArticoloDatiAcquisto().getUMPrimaria();
  }

//................................................................. UMSecondarie
  public List getUMSecondarieAcquisto() {
    return getArticoloDatiAcquisto().getUMSecondarie();
  }

//........................................................... ImportoStdAcquisto
//  public ImportoInValuta getImportoStdAcquisto(){
//    return getArticoloDatiAcquisto().getImportoStd();
//  }

//******************************************************************************
//*********************** Attributi ArticoloDatiPianif *************************
//******************************************************************************

//.................................................................. LottoMinimo
  public void setLottoMinimo(BigDecimal iLottoMinimo){
    getArticoloDatiPianif().setLottoMinimo(iLottoMinimo);
  }
  public BigDecimal getLottoMinimo(){
    return  getArticoloDatiPianif().getLottoMinimo();
  }

//................................................................. LottoMassimo
  public void setLottoMassimo(BigDecimal iLottoMassimo){
     getArticoloDatiPianif().setLottoMassimo(iLottoMassimo);
  }
  public BigDecimal getLottoMassimo(){
    return  getArticoloDatiPianif().getLottoMassimo();
  }

//................................................................ LottoMultiplo
  public void setLottoMultiplo(BigDecimal iLottoMultiplo){
     getArticoloDatiPianif().setLottoMultiplo(iLottoMultiplo);
  }
  public BigDecimal getLottoMultiplo(){
    return  getArticoloDatiPianif().getLottoMultiplo();
  }

//.................................................................... PercenStk
  public void setPercenStk(BigDecimal iPercenStk){
     getArticoloDatiPianif().setPercenStk(iPercenStk);
  }

  public BigDecimal getPercenStk(){
    return  getArticoloDatiPianif().getPercenStk();
  }

//.......................................................... GiorniFrazionamento
//  public void setGiorniFrazionamento(Integer iGiorniFrazionamento){
//     getArticoloDatiPianif().setGiorniFrazionamento(iGiorniFrazionamento);
//  }
//
//  public Integer getGiorniFrazionamento(){
//    return  getArticoloDatiPianif().getGiorniFrazionamento();
//  }

//..................................................................... NumPezzi
  public void setNumPezzi(Integer iNumPezzi){
     getArticoloDatiPianif().setNumPezzi(iNumPezzi);
  }
  public Integer getNumPezzi(){
    return  getArticoloDatiPianif().getNumPezzi();
  }

//................................................................... NumPeriodi
  public void setNumPeriodi(Integer iNumPeriodi){
     getArticoloDatiPianif().setNumPeriodi(iNumPeriodi);
  }
  public Integer getNumPeriodi(){
    return  getArticoloDatiPianif().getNumPeriodi();
  }

//................................................................ GiorniPeriodo
  public void setGiorniPeriodo(Integer iGiorniPeriodo){
    getArticoloDatiPianif().setGiorniPeriodo(iGiorniPeriodo);
  }
  public Integer getGiorniPeriodo(){
    return  getArticoloDatiPianif().getGiorniPeriodo();
  }

//.............................................................. FabbisPeriodici
  public void setFabbisPeriodici(boolean iFabbisPeriodici){
    getArticoloDatiPianif().setFabbisPeriodici(iFabbisPeriodici);
  }
  public boolean getFabbisPeriodici(){
    return getArticoloDatiPianif().getFabbisPeriodici();
  }

//.............................................................. IndiceCaricoMPS

  public void setIndiceCaricoMPS(BigDecimal indiceCaricoMPS)
  {
    getArticoloDatiPianif().setIndiceCaricoMPS(indiceCaricoMPS);
  }

  public BigDecimal getIndiceCaricoMPS()
  {
    return getArticoloDatiPianif().getIndiceCaricoMPS();
  }
//................................................. PoliticaPGP proxy managament
//  public void setPoliticaPGP(PoliticaPGP politicaPGP) {
//     getArticoloDatiPianif().setPoliticaPGP(politicaPGP);
//  }
//
//  public PoliticaPGP getPoliticaPGP() {
//    return  getArticoloDatiPianif().getPoliticaPGP();
//  }
//
//  public void setPoliticaPGPKey(String key) {
//     getArticoloDatiPianif().setPoliticaPGPKey(key);
//  }
//
//  public String getPoliticaPGPKey() {
//    return  getArticoloDatiPianif().getPoliticaPGPKey();
//  }
//
//  public void setIdPoliticaPGP(String idPoliticaPGP) {
//     getArticoloDatiPianif().setIdPoliticaPGP(idPoliticaPGP);
//  }
//
//  public String getIdPoliticaPGP() {
//    return  getArticoloDatiPianif().getIdPoliticaPGP();
//  }

//.............................................. GrigliaPeriodi proxy managament
  public void setGrigliaPeriodi(GrigliaPeriodi grigliaPeriodi) {
     getArticoloDatiPianif().setGrigliaPeriodi(grigliaPeriodi);
  }

  public GrigliaPeriodi getGrigliaPeriodi() {
    return  getArticoloDatiPianif().getGrigliaPeriodi();
  }

  public void setGrigliaPeriodiKey(String key) {
     getArticoloDatiPianif().setGrigliaPeriodiKey(key);
  }

  public String getGrigliaPeriodiKey() {
    return  getArticoloDatiPianif().getGrigliaPeriodiKey();
  }

  public void setIdGrigliaPeriodi(String idGrigliaPeriodi) {
    getArticoloDatiPianif().setIdGrigliaPeriodi(idGrigliaPeriodi);
  }

  public String getIdGrigliaPeriodi() {
    return  getArticoloDatiPianif().getIdGrigliaPeriodi();
  }

//............................................ ResponsRlsOrdini proxy managament
  public void setResponRlsOrdini(ResponsabileRlsOrdini responsabileRlsOrdini) {
     getArticoloDatiPianif().setResponRlsOrdini(responsabileRlsOrdini);
  }

  public ResponsabileRlsOrdini getResponRlsOrdini() {
    return  getArticoloDatiPianif().getResponRlsOrdini();
  }

  public void setResponRlsOrdiniKey(String key) {
     getArticoloDatiPianif().setResponRlsOrdiniKey(key);
  }

  public String getResponRlsOrdiniKey() {
    return  getArticoloDatiPianif().getResponRlsOrdiniKey();
  }

  public void setIdResponRlsOrdini(String idResponRlsOrdini) {
     getArticoloDatiPianif().setIdResponRlsOrdini(idResponRlsOrdini);
  }

  public String getIdResponRlsOrdini() {
    return  getArticoloDatiPianif().getIdResponRlsOrdini();
  }

//..............................................  TecnicaPropag proxy managament
  public void setTecnicaPropag(TecnicaPropagazione tecnicaPropagazione) {
     getArticoloDatiPianif().setTecnicaPropag(tecnicaPropagazione);
  }

  public TecnicaPropagazione getTecnicaPropag() {
    return  getArticoloDatiPianif().getTecnicaPropag();
  }

  public void setTecnicaPropagKey(String key) {
     getArticoloDatiPianif().setTecnicaPropagKey(key);
  }

  public String getTecnicaPropagKey() {
    return  getArticoloDatiPianif().getTecnicaPropagKey();
  }

  public void setIdTecnicaPropag(String idTecnicaPropag) {
    getArticoloDatiPianif().setIdTecnicaPropag(idTecnicaPropag);
  }

  public String getIdTecnicaPropag() {
    return  getArticoloDatiPianif().getIdTecnicaPropag();
  }

//............................................... Pianificatore proxy managament
  public void setPianificatore(Pianificatore pianificatore) {
    getArticoloDatiPianif().setPianificatore(pianificatore);
  }

  public Pianificatore getPianificatore() {
    return  getArticoloDatiPianif().getPianificatore();
  }

  public void setPianificatoreKey(String key) {
    getArticoloDatiPianif().setPianificatoreKey(key);
  }

  public String getPianificatoreKey() {
    return  getArticoloDatiPianif().getPianificatoreKey();
  }

  public void setIdPianificatore(String idPianificatore) {
     getArticoloDatiPianif().setIdPianificatore(idPianificatore);
  }

  public String getIdPianificatore() {
    return  getArticoloDatiPianif().getIdPianificatore();
  }

//............................................ PoliticaRiordino proxy managament
  public void setPoliticaRiordino(PoliticaRiordino politicaRiordino) {
    getArticoloDatiPianif().setPoliticaRiordino(politicaRiordino);
  }

  public PoliticaRiordino getPoliticaRiordino() {
    return  getArticoloDatiPianif().getPoliticaRiordino();
  }

  public void setPoliticaRiordinoKey(String key) {
     getArticoloDatiPianif().setPoliticaRiordinoKey(key);
  }

  public String getPoliticaRiordinoKey() {
    return  getArticoloDatiPianif().getPoliticaRiordinoKey();
  }

  public void setIdPoliticaRiordino(String idPoliticaRiordino) {
    getArticoloDatiPianif().setIdPoliticaRiordino(idPoliticaRiordino);
  }

  public String getIdPoliticaRiordino() {
    return  getArticoloDatiPianif().getIdPoliticaRiordino();
  }

//............................................ PoliticaMPS proxy managament
  public void setPoliticaMPS(PoliticaMPS politicaMPS)
  {
    getArticoloDatiPianif().setPoliticaMPS(politicaMPS);
  }

  public PoliticaMPS getPoliticaMPS()
  {
    return getArticoloDatiPianif().getPoliticaMPS();
  }

  public void setPoliticaMPSKey(String key)
  {
     getArticoloDatiPianif().setPoliticaMPSKey(key);
  }

  public String getPoliticaMPSKey()
  {
    return getArticoloDatiPianif().getPoliticaMPSKey();
  }

  public void setIdPoliticaMPS(String idPoliticaMPS)
  {
    getArticoloDatiPianif().setIdPoliticaMPS(idPoliticaMPS);
  }

  public String getIdPoliticaMPS()
  {
    return getArticoloDatiPianif().getIdPoliticaMPS();
  }

//............................................ ArticoloMPS proxy managament
  public void setArticoloMPS(Articolo articoloMPS)
  {
    getArticoloDatiPianif().setArticoloMPS(articoloMPS);
  }

  public Articolo getArticoloMPS()
  {
    return getArticoloDatiPianif().getArticoloMPS();
  }

  public void setArticoloMPSKey(String articoloMPSKey)
  {
    getArticoloDatiPianif().setArticoloMPSKey(articoloMPSKey);
  }

  public String getArticoloMPSKey()
  {
    return getArticoloDatiPianif().getArticoloMPSKey();
  }

  public void setIdArticoloMPS(String idArticoloMPS)
  {
    getArticoloDatiPianif().setIdArticoloMPS(idArticoloMPS);
  }

  public String getIdArticoloMPS()
  {
    return getArticoloDatiPianif().getIdArticoloMPS();
  }

//............................................ UMCaricoMPS proxy managament
  public void setUMCaricoMPS(UnitaMisura umCaricoMPS)
  {
    getArticoloDatiPianif().setUMCaricoMPS(umCaricoMPS);
  }

  public UnitaMisura getUMCaricoMPS()
  {
    return getArticoloDatiPianif().getUMCaricoMPS();
  }

  public void setUMCaricoMPSKey(String umCaricoMPSKey)
  {
    getArticoloDatiPianif().setUMCaricoMPSKey(umCaricoMPSKey);
  }

  public String getUMCaricoMPSKey()
  {
    return getArticoloDatiPianif().getUMCaricoMPSKey();
  }

  public void setIdUMCaricoMPS(String idUMCaricoMPS)
  {
    getArticoloDatiPianif().setIdUMCaricoMPS(idUMCaricoMPS);
  }

  public String getIdUMCaricoMPS()
  {
    return getArticoloDatiPianif().getIdUMCaricoMPS();
  }

//............................................ RisorsaMPS proxy managament
  public void setRisorsaMPS(Risorsa risorsaMPS)
  {
    getArticoloDatiPianif().setRisorsaMPS(risorsaMPS);
  }

  public Risorsa getRisorsaMPS()
  {
    return getArticoloDatiPianif().getRisorsaMPS();
  }

  public void setRisorsaMPSKey(String risorsaMPSKey)
  {
    getArticoloDatiPianif().setRisorsaMPSKey(risorsaMPSKey);
  }

  public String getRisorsaMPSKey()
  {
    return getArticoloDatiPianif().getRisorsaMPSKey();
  }

  public void setIdRisorsaMPS(String idRisorsaMPS)
  {
    getArticoloDatiPianif().setIdRisorsaMPS(idRisorsaMPS);
  }

  public String getIdRisorsaMPS()
  {
    return getArticoloDatiPianif().getIdRisorsaMPS();
  }

  public void setTipoRisorsaMPS(char tipoRisorsaMPS)
  {
    getArticoloDatiPianif().setTipoRisorsaMPS(tipoRisorsaMPS);
  }

  public char getTipoRisorsaMPS()
  {
    return getArticoloDatiPianif().getTipoRisorsaMPS();
  }

  public void setLivelloRisorsaMPS(char livelloRisorsaMPS)
  {
    getArticoloDatiPianif().setLivelloRisorsaMPS(livelloRisorsaMPS);
  }

  public char getLivelloRisorsaMPS()
  {
    return getArticoloDatiPianif().getLivelloRisorsaMPS();
  }

  //............................................. iPoliticheMPS OneToMany managament
  public List getPoliticheMPS()  {
    return getArticoloDatiPianif().getPoliticheMPS();
  }

//******************************************************************************
//********************** Attributi ArticoloDatiProduz **************************
//******************************************************************************

//................................................................ IdFamigliaPgm
  public void setIdFamigliaPgm(String idFamigliaPgm)  {
    getArticoloDatiProduz().setIdFamigliaPgm(idFamigliaPgm);
  }

  public String getIdFamigliaPgm()  {
    return getArticoloDatiProduz().getIdFamigliaPgm();
  }

//.......................................................... IdConfigurazioneStd
  public void setConfigurazioneStd(Configurazione configurazione){
    getArticoloDatiProduz().setConfigurazioneStd(configurazione);
  }

  public Configurazione getConfigurazioneStd() {
    return getArticoloDatiProduz().getConfigurazioneStd();
  }

  public void setConfigurazioneStdKey(String key) {
    getArticoloDatiProduz().setConfigurazioneStdKey(key);
  }

  public String getConfigurazioneStdKey() {
    return getArticoloDatiProduz().getConfigurazioneStdKey();
  }

  public void setIdConfigurazioneStd(Integer idConfigurazioneStd){
    getArticoloDatiProduz().setIdConfigurazioneStd(idConfigurazioneStd);
  }

  public Integer getIdConfigurazioneStd(){
    return  getArticoloDatiProduz(). getIdConfigurazioneStd();
  }

//............................................ Eredita configurazione dailivelli
  public void setEreditaConfig(boolean ereditaCfg){
    getArticoloDatiProduz().setEreditaConfig(ereditaCfg);
  }

  public boolean isEreditaConfig(){
    return getArticoloDatiProduz().isEreditaConfig();
  }

//............................................................ Fattore di scarto
     public void setPercenScarto(BigDecimal percenScarto){
       getArticoloDatiProduz().setPercenScarto(percenScarto);
      }

      public BigDecimal getPercenScarto(){
        return getArticoloDatiProduz().getPercenScarto();
      }

//................................................................Lotto standard
     public void setLottoStandard(BigDecimal lottoStd){
         getArticoloDatiProduz().setLottoStandard(lottoStd);
      }

      public BigDecimal getLottoStandard(){
        return getArticoloDatiProduz().getLottoStandard();
      }

//......................................................... Lotto di attrezzagio
     public void setLottoAttrezzagio(BigDecimal lottoAtz){
        getArticoloDatiProduz().setLottoAttrezzagio(lottoAtz);
      }

      public BigDecimal getLottoAttrezzagio(){
        return getArticoloDatiProduz().getLottoAttrezzagio();
      }

//......................................................... Gestione esaurimento
     public void setGesEsaurimento(boolean gesEsauri){
       getArticoloDatiProduz().setGesEsaurimento(gesEsauri);
      }

      public boolean isGesEsaurimento(){
        return getArticoloDatiProduz().isGesEsaurimento();
      }

//...................................................... prelivelo quanta intera
//     public void setQtaPrelievoIntera(boolean qtaPrlIntera){
//        getArticoloDatiProduz().setQtaPrelievoIntera(qtaPrlIntera);
//      }
//
//      public boolean isQtaPrelievoIntera(){
//        return getArticoloDatiProduz().isQtaPrelievoIntera();
//      }

//............................................................ modalita prolievo
     public void setModalitaPrelievo(char modPrelievo)
     {
        getArticoloDatiProduz().setModalitaPrelievo(modPrelievo);
     }

      public char getModalitaPrelievo(){
        return getArticoloDatiProduz().getModalitaPrelievo();
      }

//.......................................................... Tipo accantonamento
     public void setTipoAccantProd(char tipoAccantPrd){
       getArticoloDatiProduz().setTipoAccantProd(tipoAccantPrd);
      }

      public char getTipoAccantProd(){
        return  getArticoloDatiProduz().getTipoAccantProd();
      }

//...................................................... Modalità accantonamento
     public void setModAccantProd(char modAccantPrd){
        getArticoloDatiProduz().setModAccantProd(modAccantPrd);
      }

      public char getModAccantProd(){
        return getArticoloDatiProduz().getModAccantProd();
      }


//......................................... Classe Merceologica proxy managament
  public void setClasseMerclg(ClasseMerceologica classeMerceologica){
    getArticoloDatiProduz().setClasseMerclg(classeMerceologica);
  }

  public ClasseMerceologica getClasseMerclg() {
    return getArticoloDatiProduz().getClasseMerclg();
  }

  public void setClasseMerclgKey(String key) {
   getArticoloDatiProduz().setClasseMerclgKey(key);
  }

  public String getClasseMerclgKey() {
    return getArticoloDatiProduz().getClasseMerclgKey();
  }

  public void setIdClasseMerclg(String idClasseMerclg) {
    getArticoloDatiProduz().setIdClasseMerclg(idClasseMerclg);
  }

  public String getIdClasseMerclg() {
    return getArticoloDatiProduz().getIdClasseMerclg();
  }

//........................................ Tipo ciclo aziendale proxy managament
//  public void setTipoCicloStd(TipoCicloAziendale tipoCicloStd){
//   getArticoloDatiProduz().setTipoCicloStd(tipoCicloStd);
//  }
//
//  public TipoCicloAziendale getTipoCicloStd() {
//    return  getArticoloDatiProduz().getTipoCicloStd();
//  }
//
//  public void setTipoCicloStdKey(String key) {
//    getArticoloDatiProduz().setTipoCicloStdKey(key);
//  }
//
//  public String getTipoCicloStdKey() {
//    return getArticoloDatiProduz().getTipoCicloStdKey();
//  }
//
//  public void setIdTipoCicloStd(String idTipoCicloStd) {
//  getArticoloDatiProduz().setIdTipoCicloStd(idTipoCicloStd);
//  }
//
//  public String getIdTipoCicloStd() {
//    return  getArticoloDatiProduz().getIdTipoCicloStd();
//  }

//................................ TIPO TICLO AZIENDALE  MANY TO MANY managament
//  public List getTipiCiclo()
//  {
//   return getArticoloDatiProduz().getTipiCicloInternal();
//  }
//
//  public Vector getTipiCicloKeys()
//  {
//   return getArticoloDatiProduz().getTipiCicloKeys();
//  }
//
//  public void setTipiCicloKeys(Vector keys)
//  {
//   getArticoloDatiProduz().setTipiCicloKeys(keys);
//  }

//......................................................... Alternativi Distinta
  public List getAlternativiDistinta()
  {
     return getArticoloDatiProduz().getAlternativiDistinta();
  }

//******************************************************************************
//*********************** Attributi ArticoloDatiMagaz **************************
//******************************************************************************

//................................................................. OperConverUM
  public char getOperConverUM()
  {
    return getArticoloDatiMagaz().getOperConverUM();
  }

  public void setOperConverUM(char operConverUM)
  {
     getArticoloDatiMagaz().setOperConverUM(operConverUM);
  }

//................................................................ PropostaLotto
  public char getPropostaLotto()
  {
    return  getArticoloDatiMagaz().getPropostaLotto();
  }

  public void setPropostaLotto(char propostaLotto)
  {
    getArticoloDatiMagaz().setPropostaLotto(propostaLotto);
  }

//............................................................. ModPrelievoLotto
  public char getModPrelievoLotto()
  {
    return getArticoloDatiMagaz().getModPrelievoLotto();
  }

  public void setModPrelievoLotto(char modPrelievoLotto)
  {
    getArticoloDatiMagaz().setModPrelievoLotto(modPrelievoLotto);
  }

//........................................................... CriterioOrdinLotti
  public char getCriterioOrdinLotti()
  {
    return getArticoloDatiMagaz().getCriterioOrdinLotti();
  }

  public void setCriterioOrdinLotti(char criterioOrdinLotti)
  {
    getArticoloDatiMagaz().setCriterioOrdinLotti(criterioOrdinLotti);
  }

//............................................................... TipoInventario
  public char getTipoInventario()
  {
    return getArticoloDatiMagaz().getTipoInventario();
  }

  public void setTipoInventario(char tipoInventario)
  {
    getArticoloDatiMagaz().setTipoInventario(tipoInventario);
  }

//................................................................... ArticLotto
  public boolean isArticLotto()
  {
    return getArticoloDatiMagaz().isArticLotto();
  }

  public void setArticLotto(boolean articLotto)
  {
    getArticoloDatiMagaz().setArticLotto(articLotto);
  }

//............................................................ ArticoloSicurezza
  public boolean isArticoloSicurezza()
  {
    return getArticoloDatiMagaz().isArticoloSicurezza();
  }

  public void setArticoloSicurezza(boolean articoloSicurezza)
  {
    getArticoloDatiMagaz().setArticoloSicurezza(articoloSicurezza);
  }

//............................................................... ArticoloMatric
  public boolean isArticoloMatric()
  {
    return getArticoloDatiMagaz().isArticoloMatric();
  }

  public void setArticoloMatric(boolean articoloMatric)
  {
    getArticoloDatiMagaz().setArticoloMatric(articoloMatric);
  }

//............................................................. GesScadenzaLotto
  public boolean isGesScadenzaLotto()
  {
     return getArticoloDatiMagaz().isGesScadenzaLotto();
  }

  public void setGesScadenzaLotto(boolean gesScadenzaLotto)
  {
    getArticoloDatiMagaz().setGesScadenzaLotto(gesScadenzaLotto);
  }

//............................................................. QtaIntera
  public boolean isQtaIntera()
  {
    return getArticoloDatiMagaz().isQtaIntera();
  }

  public void setQtaIntera(boolean qtaIntera)
  {
    getArticoloDatiMagaz().setQtaIntera(qtaIntera);
  }

//............................................................. GesSaldiCommessa
  public boolean isGesSaldiCommessa()
  {
    return getArticoloDatiMagaz().isGesSaldiCommessa();
  }

  public void setGesSaldiCommessa(boolean gesSaldiCommessa)
  {
    getArticoloDatiMagaz().setGesSaldiCommessa(gesSaldiCommessa);
  }

//.................................................................. FttConverUM
  public BigDecimal getFttConverUM()
  {
    return getArticoloDatiMagaz().getFttConverUM();
  }

  public void setFttConverUM(BigDecimal fttConverUM)
  {
    getArticoloDatiMagaz().setFttConverUM(fttConverUM);
  }

//................................................................. ScortaMinima
  public BigDecimal getScortaMinima()
  {
  return getArticoloDatiMagaz().getScortaMinima();
  }

  public void setScortaMinima(BigDecimal scortaMinima)
  {
    getArticoloDatiMagaz().setScortaMinima(scortaMinima);
  }

//.............................................................. LivelloServizio
  public BigDecimal getLivelloServizio()
  {
    return getArticoloDatiMagaz().getLivelloServizio();
  }

  public void setLivelloServizio(BigDecimal livelloServizio)
  {
    getArticoloDatiMagaz().setLivelloServizio(livelloServizio);
  }

//............................................................... LottoEconomico
  public BigDecimal getLottoEconomico()
  {
    return getArticoloDatiMagaz().getLottoEconomico();
  }

  public void setLottoEconomico(BigDecimal lottoEconomico)
  {
    getArticoloDatiMagaz().setLottoEconomico(lottoEconomico);
  }

//............................................................. QtaPuntoRiordino
  public BigDecimal getQtaPuntoRiordino()
  {
    return getArticoloDatiMagaz().getQtaPuntoRiordino();
  }

  public void setQtaPuntoRiordino(BigDecimal qtaPuntoRiordino)
  {
    getArticoloDatiMagaz().setQtaPuntoRiordino(qtaPuntoRiordino);
  }

//............................................................... GiorniValidita
  public Integer getGiorniValidita()
  {
    return getArticoloDatiMagaz().getGiorniValidita();
  }

  public void setGiorniValidita(Integer giorniValidita)
  {
    getArticoloDatiMagaz().setGiorniValidita(giorniValidita);
  }

//.............................................................. GiorniCopertura
  public Integer getGiorniCopertura()
  {
    return getArticoloDatiMagaz().getGiorniCopertura();
  }

  public void setGiorniCopertura(Integer giorniCopertura)
  {
    getArticoloDatiMagaz().setGiorniCopertura(giorniCopertura);
  }

//............................................................. NumMaxGGNonMovim
  public Integer getNumMaxGGNonMovim()
  {
    return getArticoloDatiMagaz().getNumMaxGGNonMovim();
  }

  public void setNumMaxGGNonMovim(Integer numMaxGGNonMovim)
  {
    getArticoloDatiMagaz().setNumMaxGGNonMovim(numMaxGGNonMovim);
  }

//............................................................. NumMovInventario
  public Integer getNumMovInventario()
  {
    return getArticoloDatiMagaz().getNumMovInventario();
  }

  public void setNumMovInventario(Integer numMovInventario)
  {
    getArticoloDatiMagaz().setNumMovInventario(numMovInventario);
  }

//.............................................................. TmpApprovCumPrd
  public Integer getTmpApprovCumPrd()
  {
    return getArticoloDatiMagaz().getTmpApprovCumPrd();
  }

  public void setTmpApprovCumPrd(Integer tmpApprovCumPrd)
  {
    getArticoloDatiMagaz().setTmpApprovCumPrd(tmpApprovCumPrd);
  }

//.............................................................. TmpApprovCumAcq
  public Integer getTmpApprovCumAcq()
  {
    return getArticoloDatiMagaz().getTmpApprovCumAcq();
  }

  public void setTmpApprovCumAcq(Integer tmpApprovCumAcq)
  {
    getArticoloDatiMagaz().setTmpApprovCumAcq(tmpApprovCumAcq);
  }

//............................................................. TmpApprovCumClav
  public Integer getTmpApprovCumClav()
  {
    return getArticoloDatiMagaz().getTmpApprovCumClav();
  }

  public void setTmpApprovCumClav(Integer tmpApprovCumClav)
  {
    getArticoloDatiMagaz().setTmpApprovCumClav(tmpApprovCumClav);
  }

//................................................................ TmpApprovClav
  public TempiApprov getTmpApprovClav()
  {
    return getArticoloDatiMagaz().getTmpApprovClav();
  }

//................................................................. TmpApprovPrd
  public TempiApprov getTmpApprovPrd()
  {
    return getArticoloDatiMagaz().getTmpApprovPrd();
  }

//................................................................. TmpApprovAcq
  public TempiApprov getTmpApprovAcq()
  {
    return getArticoloDatiMagaz().getTmpApprovAcq();
  }

//................................................................. PrezzoStdMag
  public BigDecimal getPrezzoStdMag()
  {
    return getArticoloDatiMagaz().getPrezzoStdMag();
  }

  public void setPrezzoStdMag(BigDecimal prezzoStdMag)
  {
    getArticoloDatiMagaz().setPrezzoStdMag(prezzoStdMag);
  }

//.................................................................. CostoStdMag
  public BigDecimal getCostoStdMag()
  {
    return getArticoloDatiMagaz().getCostoStdMag();
  }

  public void setCostoStdMag(BigDecimal costoStdMag)
  {
    getArticoloDatiMagaz().setCostoStdMag(costoStdMag);
  }

//.............................................. GrpVisteDispon proxy managament
  public GruppoVistaDisponibilita getGrpVisteDispon()
  {
    return getArticoloDatiMagaz().getGrpVisteDispon();
  }

  public void setGrpVisteDispon(GruppoVistaDisponibilita grpVisteDispon)
  {
    getArticoloDatiMagaz().setGrpVisteDispon(grpVisteDispon);
  }

  public String getGrpVisteDisponKey()
  {
    return getArticoloDatiMagaz().getGrpVisteDisponKey();
  }

  public void setGrpVisteDisponKey(String grpVisteDisponKey)
  {
    getArticoloDatiMagaz().setGrpVisteDisponKey(grpVisteDisponKey);
  }

  public String getIdGrpVisteDispon()
  {
    return getArticoloDatiMagaz().getIdGrpVisteDispon();
  }

  public void setIdGrpVisteDispon(String idGrpVisteDispon)
  {
    getArticoloDatiMagaz().setIdGrpVisteDispon(idGrpVisteDispon);
  }

//................................................ ClasseABCInv proxy managament
  public ClasseABCInv getClasseABCInv()
  {
    return getArticoloDatiMagaz().getClasseABCInv();
  }

  public void setClasseABCInv(ClasseABCInv classeABCInv)
  {
    getArticoloDatiMagaz().setClasseABCInv(classeABCInv);
  }

  public String getClasseABCInvKey()
  {
    return getArticoloDatiMagaz().getClasseABCInvKey();
  }

  public void setClasseABCInvKey(String classeABCInvKey)
  {
    getArticoloDatiMagaz().setClasseABCInvKey(classeABCInvKey);
  }

  public String getIdClasseABCInv()
  {
    return getArticoloDatiMagaz().getIdClasseABCInv();
  }

  public void setIdClasseABCInv(String idClasseABCInv)
  {
    getArticoloDatiMagaz().setIdClasseABCInv(idClasseABCInv);
  }

//................................................ ClasseABCMag proxy managament
  public ClasseABCMag getClasseABCMag()
  {
    return getArticoloDatiMagaz().getClasseABCMag();
  }

  public void setClasseABCMag(ClasseABCMag classeABCMag)
  {
    getArticoloDatiMagaz().setClasseABCMag(classeABCMag);
  }

  public String getClasseABCMagKey()
  {
    return getArticoloDatiMagaz().getClasseABCMagKey();
  }

  public void setClasseABCMagKey(String classeABCMagKey)
  {
    getArticoloDatiMagaz().setClasseABCMagKey(classeABCMagKey);
  }

  public String getIdClasseABCMag()
  {
    return getArticoloDatiMagaz().getIdClasseABCMag();
  }

  public void setIdClasseABCMag(String idClasseABCMag)
  {
    getArticoloDatiMagaz().setIdClasseABCMag(idClasseABCMag);
  }

//.................................................... UMPrmMag proxy managament
  public UnitaMisura getUMPrmMag()
  {
    return getArticoloDatiMagaz().getUMPrmMag();
  }

  public void setUMPrmMag(UnitaMisura uMPrmMag)
  {
    getArticoloDatiMagaz().setUMPrmMag(uMPrmMag);
  }

  public String getUMPrmMagKey()
  {
    return getArticoloDatiMagaz().getUMPrmMagKey();
  }

  public void setUMPrmMagKey(String uMPrmMagKey)
  {
    getArticoloDatiMagaz().setUMPrmMagKey(uMPrmMagKey);
  }

  public String getIdUMPrmMag()
  {
    return getArticoloDatiMagaz().getIdUMPrmMag();
  }

  public void setIdUMPrmMag(String idUMPrmMag)
  {
    getArticoloDatiMagaz().setIdUMPrmMag(idUMPrmMag);
  }

//.................................................... UMSecMag proxy managament
  public UnitaMisura getUMSecMag()
  {
    return getArticoloDatiMagaz().getUMSecMag();
  }

  public void setUMSecMag(UnitaMisura uMSecMag)
  {
    getArticoloDatiMagaz().setUMSecMag(uMSecMag);
  }

  public String getUMSecMagKey()
  {
    return getArticoloDatiMagaz().getUMSecMagKey();
  }

  public void setUMSecMagKey(String uMSecMagKey)
  {
    getArticoloDatiMagaz().setUMSecMagKey(uMSecMagKey);
  }

  public String getIdUMSecMag()
  {
    return getArticoloDatiMagaz().getIdUMSecMag();
  }

  public void setIdUMSecMag(String idUMSecMag)
  {
    getArticoloDatiMagaz().setIdUMSecMag(idUMSecMag);
  }

//******************************************************************************
//*********************** Attributi ArticoloDatiContab *************************
//******************************************************************************

//................................................. CatContiVen proxy managament
  public CatContiCG getCatContiVen()
  {
    return getArticoloDatiContab().getCatContiVen();
  }

  public void setCatContiVen(CatContiCG catContiVen)
  {
     getArticoloDatiContab().setCatContiVen(catContiVen);
  }

  public String getCatContiVenKey()
  {
    return getArticoloDatiContab().getCatContiVenKey();
  }

  public void setCatContiVenKey(String key)
  {
     getArticoloDatiContab().setCatContiVenKey(key);
  }

  public String getIdCatContiVen()
  {
    return getArticoloDatiContab().getIdCatContiVen();
  }

  public void setIdCatContiVen(String idCatContiVen)
  {
    getArticoloDatiContab().setIdCatContiVen(idCatContiVen);
  }

//................................................  CatContiAcq proxy managament
  public CatContiCG getCatContiAcq()
  {
    return getArticoloDatiContab().getCatContiAcq();
  }

  public void setCatContiAcq(CatContiCG catContiAcq)
  {
    getArticoloDatiContab().setCatContiAcq(catContiAcq);
  }

  public String getCatContiAcqKey()
  {
    return getArticoloDatiContab().getCatContiAcqKey();
  }

  public void setCatContiAcqKey(String key)
  {
     getArticoloDatiContab().setCatContiAcqKey(key);
  }

  public String getIdCatContiAcq()
  {
    return getArticoloDatiContab().getIdCatContiAcq();
  }

  public void setIdCatContiAcq(String idCatContiAcq)
  {
    getArticoloDatiContab().setIdCatContiAcq(idCatContiAcq);
  }

//................................................ CatContiClav proxy managament
  public CatContiCG getCatContiClav()
  {
    return getArticoloDatiContab().getCatContiClav();
  }

  public void setCatContiClav(CatContiCG catContiClav)
  {
     getArticoloDatiContab().setCatContiClav(catContiClav);
  }

  public String getCatContiClavKey()
  {
    return getArticoloDatiContab().getCatContiClavKey();
  }

  public void setCatContiClavKey(String key)
  {
     getArticoloDatiContab().setCatContiClavKey(key);
  }

  public String getIdCatContiClav()
  {
    return getArticoloDatiContab().getIdCatContiClav();
  }

  public void setIdCatContiClav(String idCatContiClav)
  {
    getArticoloDatiContab().setIdCatContiClav(idCatContiClav);
  }

//...................................................Component RiferimentoVociCA
  public RiferimentoVociCA getRiferimVociCA()
  {
    return getArticoloDatiContab().getRiferimVociCA();
  }

//******************************************************************************
//********************* Attributi ArticoloDatiQualita **************************
//******************************************************************************

//.......................................................... IdArticoloCicloColl
  public void setIdArticoloCicloColl(String idArticoloCicloColl)  {
    getArticoloDatiQualita().setIdArticoloCicloColl(idArticoloCicloColl);
  }

  public String getIdArticoloCicloColl()  {
    return getArticoloDatiQualita().getIdArticoloCicloColl();
  }

//............................................................. ControlloQualita
  public void setControlloQualita(boolean ControlloQualita)
  {
   getArticoloDatiQualita().setControlloQualita(ControlloQualita);
  }

  public boolean getControlloQualita()
  {
    return getArticoloDatiQualita().getControlloQualita();
  }

//................................................................. Legislazione
  /**
   * FIX02792 - DZ
   * @deprecated sostituito da setProcedura.
   * @param Legislazione String
   */
  public void setLegislazione(String Legislazione)
  {
    // fix 2508
    getArticoloDatiQualita().setProcedura(Legislazione);
    // fix 2508
  }

  /**
   * FIX02792 - DZ
   * @deprecated sostituito da getProcedura.
   * @return String
   */
  public String getLegislazione()
  {
    // fix 2508
    return getArticoloDatiQualita().getProcedura();
    // fix 2508
  }

//.................................................................... Procedura
  public void setProcedura(String procedura){
    getArticoloDatiQualita().setProcedura(procedura);
  }

  public String getProcedura(){
    return getArticoloDatiQualita().getProcedura();
  }

//.................................................................... Normativa
  public void setNormativa(String Normativa)
  {
    getArticoloDatiQualita().setNormativa(Normativa);
  }

  public String getNormativa()
  {
    return  getArticoloDatiQualita().getNormativa();
  }

//................................................................ CicloCollaudo
  public void setCicloCollaudo(String CicloCollaudo)
  {
    getArticoloDatiQualita().setCicloCollaudo(CicloCollaudo);
  }

  public String getCicloCollaudo()
  {
    return getArticoloDatiQualita().getCicloCollaudo();
  }

//................................................................. TipoCollaudo
  public void setTipoCollaudo(char TipoCollaudo)
  {
    getArticoloDatiQualita().setTipoCollaudo(TipoCollaudo);
  }

  public char getTipoCollaudo()
  {
    return getArticoloDatiQualita().getTipoCollaudo();
  }

//............................................................... PercenCollaudo
  public void setPercenCollaudo(BigDecimal PercenCollaudo)
  {
    getArticoloDatiQualita().setPercenCollaudo(PercenCollaudo);
  }

  public BigDecimal getPercenCollaudo()
  {
    return getArticoloDatiQualita().getPercenCollaudo();
  }

//............................................................. CriticitaRitardo
  public void setCriticitaRitardo(BigDecimal CriticitaRitardo)
  {
    getArticoloDatiQualita().setCriticitaRitardo(CriticitaRitardo);
  }

  public BigDecimal getCriticitaRitardo()
  {
    return getArticoloDatiQualita().getCriticitaRitardo();
  }


//******************************************************************************
//*********************** Attributi ArticoloDatiCosto **************************
//******************************************************************************

//...........................................CostiArticolo OneToMany Managemenet
  public List getArticoloCosto(){
    return getArticoloDatiCosto().getArticoloCosto();
  }

//............................................. StabilimentoPrf proxy managament
  public void setStabilimentoPrf(Stabilimento stabilimentoPrf)
  {
    getArticoloDatiCosto().setStabilimentoPrf(stabilimentoPrf);
  }

  public Stabilimento getStabilimentoPrf()
  {
    return getArticoloDatiCosto().getStabilimentoPrf();
  }

  public void setStabilimentoPrfKey(String stabilimentoPrfKey)
  {
    getArticoloDatiCosto().setStabilimentoPrfKey(stabilimentoPrfKey);
  }

  public String getStabilimentoPrfKey()
  {
    return getArticoloDatiCosto().getStabilimentoPrfKey();
  }

  public void setIdStabilimentoPrf(String idStabilimentoPrf)
  {
    getArticoloDatiCosto().setIdStabilimentoPrf(idStabilimentoPrf);
  }

  public String getIdStabilimentoPrf()
  {
    return getArticoloDatiCosto().getIdStabilimentoPrf();
  }
//******************************************************************************
//********************** Attributi ArticoloDatiStabil **************************
//******************************************************************************

//....................................StabilimentiArticolo OneToMany Managemenet
 public List getStabilimentiArticolo()
  {
    return getArticoloDatiStabil().getStabilimentiArticolo();
  }

//******************************************************************************
//********************* Attributi ArticoloDatiVersioni *************************
//******************************************************************************

//........................................................... OneToMany Versioni
  public List getVersioni(){
    return getArticoloDatiVersioni().getVersioni();
  }

//........................................................... versione alla data
  /**
   * Metodo che restituisce la versone corrente ad una certa data.
   */
  public ArticoloVersione getVersioneAtDate(java.sql.Date data){
    return getArticoloDatiVersioni().getVersioneAtDate(data);
  }

//MOD. 02853 SL - Aggiunto metodo di get su Attributo ExtensibleAttribute
//                per Caricamenti di massa
//******************************************************************************
//************************ Attributo ExtensibleAttribute ***********************
//******************************************************************************
  public ExtensibleAttribute getExtensibleAttribute(){
    return this.getArticoloDatiEstensioni().getExtensibleAttribute();
  }


  /**
   * FIX02585 - DZ
   * In ArticoloBase non fa nulla.
   * Ridefinito in Articolo perchè salvi le versioni prima che vengano creati i SaldiBase.
   * @return int
   * @throws SQLException
   */
  protected int salvaVersioniPerSaldi() throws SQLException{
    return getArticoloDatiVersioni().save();
  }

  public static void main(String[] args)throws Exception{
    ConnectionManager.openMainConnection("THIPSVIL","server","visual", new DB2Database());

    Articolo art = (Articolo)Articolo.elementWithKey("it.thera.thip.base.articolo.Articolo",KeyHelper.buildObjectKey(new String[]{"SFT","_DEB"}),PersistentObject.NO_LOCK);
    List um = art.getUnitaMisuraList();
    for (int i = 0; i < um.size(); i++)
      System.out.println("UM: "+((UnitaMisura)um.get(i)).getIdUnitaMisura());
    UnitaMisura target = UnitaMisura.elementWithKey(KeyHelper.buildObjectKey(new String[]{"SFT","KG"}),PersistentObject.NO_LOCK);
    UnitaMisura source = UnitaMisura.elementWithKey(KeyHelper.buildObjectKey(new String[]{"SFT","NR"}),PersistentObject.NO_LOCK);
    BigDecimal bd = art.convertiUM(new BigDecimal("150"), source, target);
    System.out.println("bd " + bd);
  }

//  public static void main(String[] args)throws Exception{
//    ConnectionManager.openMainConnection("THIPLOC4","server","visual", new DB2Database());
//
//    Articolo art = (Articolo)Articolo.elementWithKey(KeyHelper.buildObjectKey(new String[]{"SFT","001"}),PersistentObject.NO_LOCK);
//    Calendar calendar = new GregorianCalendar();
//    calendar.set(2002, 9, 24);
//    ArticoloVersione vers = art.getVersioneAtDate(new java.sql.Date(calendar.getTime().getTime()));
//    System.out.println("getIdVersione: "+vers.getIdVersione());
//
//  }

  // Inizio 4670

  // fix 10955 >
  /**
   * @deprecated
   */
  public BigDecimal convertiUMArrotondate(BigDecimal valore, UnitaMisura source, UnitaMisura target){
    return convertiUMArrotondate(valore, source, target, null);
  }

  public BigDecimal convertiUMArrotondate(BigDecimal valore, UnitaMisura source, UnitaMisura target, ArticoloVersione versione){
    return convertiUMArrotondate(valore, source, target, versione, true); //Fix 11818
  }

  //Fix 11818 aggiunto parametro controllaConversione
  public BigDecimal convertiUMArrotondate(BigDecimal valore, UnitaMisura source, UnitaMisura target, ArticoloVersione versione, boolean controllaConversione){
    BigDecimal qtaConvertita = null;
    if (valore != null) {
      if (valore.compareTo(ZERO_DEC) == 0) {
        qtaConvertita = ZERO_DEC;
      }
      valore = roundQuantita(valore, source);
      qtaConvertita = convertiUM(valore, source, target, versione, controllaConversione); //Fix 11818
      qtaConvertita = roundQuantita(qtaConvertita, target);
    }
    return qtaConvertita;
  }

  /**
   * @deprecated
   */
  public CalcoloQuantitaWeb calcolaQuantitaArrotondateForGUI(BigDecimal quantOrigine,
      UnitaMisura umRif, UnitaMisura umPrm, UnitaMisura umSec,
      char tipoRiga, char idUMDigitata, char dominio){
    return calcolaQuantitaArrotondateForGUI(quantOrigine, umRif, umPrm, umSec, null, tipoRiga, idUMDigitata, dominio);
  }

  public CalcoloQuantitaWeb calcolaQuantitaArrotondateForGUI(BigDecimal quantOrigine,
      UnitaMisura umRif, UnitaMisura umPrm, UnitaMisura umSec, ArticoloVersione versione,
      char tipoRiga, char idUMDigitata, char dominio){
    QuantitaInUMRif qta = calcolaQuantitaArrotondate(quantOrigine, umRif,
        umPrm, umSec, versione, idUMDigitata);
    // fix 11779 >
    //CalcoloQuantitaWeb cqw = buildCalcoloQuantitaWeb(qta, umRif, umPrm, umSec, tipoRiga, dominio);
    CalcoloQuantitaWeb cqw = buildCalcoloQuantitaWeb(qta, umRif, umPrm, umSec, tipoRiga, dominio, versione);
    // fix 11779 <
    return cqw;
  }


  /**
   * @deprecated
   */
  public QuantitaInUMRif calcolaQuantitaArrotondate(BigDecimal quantOrigine,
      UnitaMisura umRif, UnitaMisura umPrm, UnitaMisura umSec,
      char idUMDigitata){
    return calcolaQuantitaArrotondate(quantOrigine, umRif, umPrm, umSec, null, idUMDigitata);
  }

  public QuantitaInUMRif calcolaQuantitaArrotondate(BigDecimal quantOrigine,
      UnitaMisura umRif, UnitaMisura umPrm, UnitaMisura umSec, ArticoloVersione versione,
      char idUMDigitata){

    QuantitaInUMRif qtaCalcolata = new QuantitaInUMRif();
    char step = STEP_0;
    switch(idUMDigitata){
      case UM_PRM:
        step = STEP_2;
        qtaCalcolata.setQuantitaInUMPrm(roundQuantita(quantOrigine, umPrm));
        break;
      case UM_SEC:
        step = STEP_4;
        qtaCalcolata.setQuantitaInUMSec(roundQuantita(quantOrigine, umSec));
        break;
                        //Fix 5117 inizio
      default:
        step = STEP_1;
        qtaCalcolata.setQuantitaInUMRif(roundQuantita(quantOrigine, umRif));
        break;
                        //Fix 5117 fine
    }
    BigDecimal qta = quantOrigine;
    boolean finito = false;
    boolean okQtaRif = false;
    boolean okQtaPrm = false;
    boolean okQtaSec = false;
    boolean primaVolta = true;//Fix 5117

    QuantitaInUMRif qtaCalcolataTemp = new QuantitaInUMRif(qtaCalcolata.getQuantitaInUMPrm(), qtaCalcolata.getQuantitaInUMSec(), qtaCalcolata.getQuantitaInUMRif());
    while (!finito){
      switch (step){
        case STEP_1:
          qta = convertiUMArrotondate(qta, umRif, umPrm, versione);
          qtaCalcolataTemp.setQuantitaInUMPrm(qta);//Fix 5117
          // **
          if (!gestisceUMQtaIntera(umPrm)) {//Fix 5117
            okQtaPrm = true;
            qtaCalcolata.setQuantitaInUMPrm(qta);//Fix 5117
          }
          // **
          okQtaRif = true;
          break;

        case STEP_2:
          // Inizio 8646: la conversione è da UMPrm verso UMRif quindi
        	// il test sulla gestione a qta intera della UM deve essere fatto su UMRif
          //if (primaVolta || gestisceUMQtaIntera(umPrm)) {//Fix 5117
          if (primaVolta || gestisceUMQtaIntera(umRif)) {
            // Fine 8646
          	qta = convertiUMArrotondate(qta, umPrm, umRif, versione);
            qtaCalcolataTemp.setQuantitaInUMRif(qta);//Fix 5117
          }
          else
            qta = qtaCalcolata.getQuantitaInUMRif();//Fix 5117
          // **
          if (!gestisceUMQtaIntera(umRif)) {//Fix 5117
            okQtaRif = true;
            qtaCalcolata.setQuantitaInUMRif(qta);//Fix 5117
          }
          // **
          if (umSec == null){
            step = STEP_0;
            okQtaSec = true;
          }
          okQtaPrm = true;
          break;

        case STEP_3:
          qta = convertiUMArrotondate(qta, umRif, umSec, versione);
          qtaCalcolataTemp.setQuantitaInUMSec(qta);//Fix 5117
          // **
          if (!gestisceUMQtaIntera(umSec)) {//Fix 5117
            okQtaSec = true;
            qtaCalcolata.setQuantitaInUMSec(qta);//Fix 5117
          }
          // **
          okQtaRif = true;
          break;

        case STEP_4:
          if (primaVolta || gestisceUMQtaIntera(umSec)) {//Fix 5117
            qta = convertiUMArrotondate(qta, umSec, umRif, versione);
            qtaCalcolataTemp.setQuantitaInUMRif(qta);
          }
          else
            qta = qtaCalcolata.getQuantitaInUMRif();//Fix 5117
          if (!gestisceUMQtaIntera(umRif))//Fix 5117
            qtaCalcolata.setQuantitaInUMRif(qta);//Fix 5117
          okQtaSec = true;
          break;
      }
      if (qtaCalcolataTemp.compareTo(qtaCalcolata) != 0){
        okQtaRif = false;
        okQtaPrm = false;
        okQtaSec = false;
      }
      finito =  (okQtaRif && okQtaPrm && okQtaSec);
      qtaCalcolata.setQuantitaInUMRif(qtaCalcolataTemp.getQuantitaInUMRif());
      qtaCalcolata.setQuantitaInUMPrm(qtaCalcolataTemp.getQuantitaInUMPrm());
      qtaCalcolata.setQuantitaInUMSec(qtaCalcolataTemp.getQuantitaInUMSec());
      //System.out.println("STEP :"+step+" Quantità: "+qtaCalcolata);
      if (step == STEP_4)
        step = STEP_0;
      primaVolta = false;//Fix 5117
      step++;
    }

    if (qtaCalcolata != null) { // Fix 26116
    	
    	//Fix 5117 inizio
    	if(qtaCalcolata.getQuantitaInUMRif() != null)//Fix 30478
    		//qtaCalcolata.setQuantitaInUMRif(qtaCalcolata.getQuantitaInUMRif().setScale(2,BigDecimal.ROUND_HALF_UP));//Fix 30871
			qtaCalcolata.setQuantitaInUMRif(Q6Calc.get().setScale(qtaCalcolata.getQuantitaInUMRif(),2,BigDecimal.ROUND_HALF_UP));//Fix 30871
    	if(qtaCalcolata.getQuantitaInUMPrm() != null)//Fix 30478
    		//qtaCalcolata.setQuantitaInUMPrm(qtaCalcolata.getQuantitaInUMPrm().setScale(2,BigDecimal.ROUND_HALF_UP));//Fix 30871
			qtaCalcolata.setQuantitaInUMPrm(Q6Calc.get().setScale(qtaCalcolata.getQuantitaInUMPrm(),2,BigDecimal.ROUND_HALF_UP));//Fix 30871
    	if(qtaCalcolata.getQuantitaInUMSec() != null)//Fix 30478
    		//qtaCalcolata.setQuantitaInUMSec(qtaCalcolata.getQuantitaInUMSec().setScale(2,BigDecimal.ROUND_HALF_UP));//Fix 30871
			qtaCalcolata.setQuantitaInUMSec(Q6Calc.get().setScale(qtaCalcolata.getQuantitaInUMSec(),2,BigDecimal.ROUND_HALF_UP));//Fix 30871
    	//Fix 5117 fine

    }

    
    return qtaCalcolata;
  }

  //Fix 5117 inizio
  protected boolean gestisceUMQtaIntera(UnitaMisura um) {
    if (um != null) {
      if (um.getQtaIntera())
        return true;
      if (isQtaIntera() && um.getKey().equals(getUMPrmMagKey()))
        return true;
    }
    return false;
  }
  //Fix 5117 fine

  // fix 11779 >
  /**
   * @deprecated
   */
  public CalcoloQuantitaWeb buildCalcoloQuantitaWeb(QuantitaInUMRif qtaCalcolata, UnitaMisura umRif, UnitaMisura umPrm, UnitaMisura umSec,
      char tipoRiga, char dominio){
    CalcoloQuantitaWeb cqw = new CalcoloQuantitaWeb();
    DecimalType decType = new DecimalType();
    //decType.setScale(2);//Fix 30871
	decType.setScale(Q6Calc.get().scale(2));//Fix 30871
    cqw.setDominio(dominio);
    switch (tipoRiga){
      case TIPO_RIGA:
        cqw.setQuantRigaUMRif(decType.objectToString(qtaCalcolata.getQuantitaInUMRif()));
        cqw.setQuantRigaUMPrmMag(decType.objectToString(qtaCalcolata.getQuantitaInUMPrm()));
        cqw.setQuantRigaUMSecMag(decType.objectToString(qtaCalcolata.getQuantitaInUMSec()));
        break;
      case TIPO_LOTTO:
        cqw.setQuantRigaLottoUMRif(decType.objectToString(qtaCalcolata.getQuantitaInUMRif()));
        cqw.setQuantRigaLottoUMPrmMag(decType.objectToString(qtaCalcolata.getQuantitaInUMPrm()));
        cqw.setQuantRigaLottoUMSecMag(decType.objectToString(qtaCalcolata.getQuantitaInUMSec()));
        break;
    }
    cqw.setIdUMRif(umRif.getIdUnitaMisura());
    if (umPrm != null)
      cqw.setIdUMPrm(umPrm.getIdUnitaMisura());
    if (umSec != null)
      cqw.setIdUMSec(umSec.getIdUnitaMisura());

    return cqw;
  }

  public CalcoloQuantitaWeb buildCalcoloQuantitaWeb(QuantitaInUMRif qtaCalcolata, UnitaMisura umRif, UnitaMisura umPrm, UnitaMisura umSec,
      char tipoRiga, char dominio, ArticoloVersione versione){
    CalcoloQuantitaWeb cqw = new CalcoloQuantitaWeb();
    DecimalType decType = new DecimalType();
    //decType.setScale(2);//Fix 30871
	decType.setScale(Q6Calc.get().scale(2));//Fix 30871
    cqw.setDominio(dominio);
    switch (tipoRiga){
      case TIPO_RIGA:
        cqw.setQuantRigaUMRif(decType.objectToString(qtaCalcolata.getQuantitaInUMRif()));
        cqw.setQuantRigaUMPrmMag(decType.objectToString(qtaCalcolata.getQuantitaInUMPrm()));
        cqw.setQuantRigaUMSecMag(decType.objectToString(qtaCalcolata.getQuantitaInUMSec()));
        break;
      case TIPO_LOTTO:
        cqw.setQuantRigaLottoUMRif(decType.objectToString(qtaCalcolata.getQuantitaInUMRif()));
        cqw.setQuantRigaLottoUMPrmMag(decType.objectToString(qtaCalcolata.getQuantitaInUMPrm()));
        cqw.setQuantRigaLottoUMSecMag(decType.objectToString(qtaCalcolata.getQuantitaInUMSec()));
        break;
    }
    cqw.setIdUMRif(umRif.getIdUnitaMisura());
    if (umPrm != null)
      cqw.setIdUMPrm(umPrm.getIdUnitaMisura());
    if (umSec != null)
      cqw.setIdUMSec(umSec.getIdUnitaMisura());

    // fix 12639 >
    BigDecimal numImballo = null;
    if (versione != null) {
      numImballo = versione.calcolaNumeroImballo(qtaCalcolata.getQuantitaInUMPrm(), umPrm);
      cqw.setNumeroImballo(decType.objectToString(numImballo));
      if (versione.getArticolo().hasVersioneEstesa() && versione.getFattoreConvNI() != null && versione.getFattoreConvNI().compareTo(new BigDecimal(0)) == 0) {
        String key = this.getIdAzienda().trim() + "/" +
         this.getIdArticolo().trim() + "/" + versione.getIdVersione();
         ErrorMessage err = new ErrorMessage("THIP300207", new String[] {
            key,
            numImballo != null ? numImballo.toString() : "",
            qtaCalcolata.getQuantitaInUMPrm() != null ? qtaCalcolata.getQuantitaInUMPrm().toString() : "",
               umPrm.getIdUnitaMisura(),
               umPrm.getIdUnitaMisura()});
        this.addErrorMessage(err);
      }
    }
    // fix 12639 <

    return cqw;
  }
  // fix 11779


  /**
   * Effettua un arrotondamento secondo l'unità di misura.
   * @param qta
   * @param um
   * @return
   */
  protected BigDecimal roundQuantita(BigDecimal qta, UnitaMisura um){ //Fix 14738 passato a protected
     //Fix 14738 inizio
     //la chiamata non ha effetto se chiamata con qta.getScale() ma permette modifiche attraverso metodo
     //roundQuantita(BigDecimal qta, UnitaMisura um, int scale

  	 // Fix 26116 ini
     /*
  	 return roundQuantita(qta, um, qta.scale());
     */
   	 int scale = 0;
   	 if (qta != null) {
  		 //scale = qta.scale();//Fix 39402
   		 scale = Q6Calc.get().scale(2);//Fix 39402
  	 }
     return roundQuantita(qta, um, scale); 
     // Fix 26116 fin

     /*
    //Fix 5117 inizio
    if (qta == null)
      return null;
    //Fix 5117 fine
    BigDecimal qtaArrot = qta;
    if (um != null && um.getQtaIntera()){
      switch(um.getTipoArrotondamento()){
        case UnitaMisura.TP_ARROT_DIFETTO:
          qtaArrot = qta.setScale(0,BigDecimal.ROUND_DOWN);
          break;
        case UnitaMisura.TP_ARROT_ECCESSO:
          qtaArrot = qta.setScale(0,BigDecimal.ROUND_UP);
          break;
        case UnitaMisura.TP_ARROT_DIFETT_ECCESSO:
          qtaArrot = qta.setScale(0,BigDecimal.ROUND_HALF_UP);
          break;
      }
    }
    //Fix 5117 inizio
    else if (isQtaIntera() && um.getKey().equals(getUMPrmMagKey()))
      qtaArrot = qta.setScale(0,BigDecimal.ROUND_UP);
    //Fix 5117 fine

    return qtaArrot;
    */
    //Fix 14738 fine
  }

  //Fix 14738 inizio
  /**
   * Effettua un arrotondamento secondo l'unità di misura.
   * GSCARTA: non sarebbe meglo a 2 decimali ?
   * @param qta
   * @param um
   * @return
   */
  //protected BigDecimal roundQuantita(BigDecimal qta, UnitaMisura um, int scale){ // fix 13084 //29177
  public BigDecimal roundQuantita(BigDecimal qta, UnitaMisura um, int scale){ // fix 13084  //29177
    //Fix 5117 inizio
    if (qta == null)
      return null;
    //Fix 5117 fine
    BigDecimal qtaArrot = qta;
    if (um != null && um.getQtaIntera()){
      qta = qta.setScale(scale, BigDecimal.ROUND_DOWN); // fix 13084
      switch(um.getTipoArrotondamento()){
        case UnitaMisura.TP_ARROT_DIFETTO:
          qtaArrot = qta.setScale(0,BigDecimal.ROUND_DOWN);
          break;
        case UnitaMisura.TP_ARROT_ECCESSO:
          qtaArrot = qta.setScale(0,BigDecimal.ROUND_UP);
          break;
        case UnitaMisura.TP_ARROT_DIFETT_ECCESSO:
          qtaArrot = qta.setScale(0,BigDecimal.ROUND_HALF_UP);
          break;
      }
    }
    //Fix 5117 inizio
    else if (isQtaIntera() && um != null && um.getKey().equals(getUMPrmMagKey())) {
      qta = qta.setScale(scale, BigDecimal.ROUND_DOWN); // fix 13084
      qtaArrot = qta.setScale(0,BigDecimal.ROUND_UP);
    }
    //Fix 5117 fine

    return qtaArrot;
  }
  //Fix 14738 fine

  // Fine 4670
  //Fix 11622 inizio
  public static BigDecimal arrotondaQuantita(BigDecimal qta, UnitaMisura um, Articolo art)
  {
     if(art == null || um == null)
        return qta;

     return art.roundQuantita(qta, um);
   }

  public static boolean isAQuantitaIntera(UnitaMisura um, Articolo art)
  {
     if(art == null || um == null)
        return false;

     boolean aQtaIntera = false;
     if(um != null && um.getQtaIntera())
        aQtaIntera = true;
     else if(art.isQtaIntera() && um.getKey().equals(art.getUMPrmMagKey()))
        aQtaIntera = true;

     return aQtaIntera;
   }
  //Fix 11622 fine

// fix 7522
  public boolean isRapportoVariabileUM() {
    return getArticoloDatiMagaz().isRapportoVariabileUM();
  }

  public void setRapportoVariabileUM(boolean annullata) {
    getArticoloDatiMagaz().setRapportoVariabileUM(annullata);
  }

  public UnitaMisura getUMLogistica() {
    return getArticoloDatiMagaz().getUMLogistica();
  }

  public void setUMLogistica(UnitaMisura u) {
    getArticoloDatiMagaz().setUMLogistica(u);
  }

  public String getUMLogisticaKey() {
    return getArticoloDatiMagaz().getUMLogisticaKey();
  }

  public void setUMLogisticaKey(String u) {
    getArticoloDatiMagaz().setUMLogisticaKey(u);
  }

  public String getIdUMLogistica() {
    return getArticoloDatiMagaz().getIdUMLogistica();
  }

  public void setIdUMLogistica(String u) {
    getArticoloDatiMagaz().setIdUMLogistica(u);
  }
  // fine fix 7522

  // fix 8650 - inizio
  public String getIdGruppoProdotto() {
    return getArticoloDatiIdent().getIdGruppoProdotto();
  }
  // fix 8650 - fine

  //Fix 33959 inizio
  
  public GruppoProdotto getGruppoProdotto() {
	    return getArticoloDatiIdent().getGruppoProdotto();
	  }
  //Fix 33959 Fine

  //Fix 9922 inzio
  public String getIdVersioneTecnica()
  {
     return getArticoloDatiTecnici().getIdVersioneTecnica();
  }
  //Fix 9922 fine


  //Fix 08770 inizio
  public int delete() throws SQLException {
    int delRes = super.delete();
    if (delRes > 0) {
      //Fix 9117 inizio
      GestoreInterfacciaNICIM ges = (GestoreInterfacciaNICIM) Factory.createObject(GestoreInterfacciaArticoli.class);
      //GestoreInterfacciaNICIM ges = (GestoreInterfacciaNICIM) Factory.createObject(GestoreInterfacciaReparti.class);
      //Fix 9117 fine
      char tipoAgg = InterfacciaStdNICIM.CANCELLAZIONE;
      List errs = ges.aggiornaInterfacciaTempoReale(getKey(), this, tipoAgg);
      if ((errs != null) && !errs.isEmpty()) {
        throw new ThipException((ErrorMessage) errs.get(0));
      }
    }
    return delRes;
  }
  //Fix 08770 fine

  //Fix 14931 I metodi seguenti non sono più utilizzati e sono sostituiti con la classe
  //it.thera.thip.base.comuniVenAcq.CalcolatorePesiVolume per la gestione anche delle ceramiche
  //Fix 12508 inizio
  //-------------------------------------------------------------------
  /* Metodi di utilità per il calcolo dei Pesi e Volumi delle righe ordine
   * e documenti
   * Il metodi riceve il massimo possibile delle informazioni
   *       Articolo, qtaUmRif, qtaUmPrm, qtaUmSec, UmRif, UmPrm, UmSec
   *
   * Il ritorno è un array di valori costituito come
   *       [PesoNettoRiga, PesoLordoRiga, VolumeRiga]
   */
  //-------------------------------------------------------------------
  public static BigDecimal[] getPesiEVolumeTotali(Articolo articolo,
                                                 BigDecimal qtaUMRif, BigDecimal qtaUMPrm, BigDecimal qtaUMSec,
                                                 UnitaMisura umRif, UnitaMisura umPrm, UnitaMisura umSec)
  {
     BigDecimal[] ret = new BigDecimal[]{ZERO_DEC, ZERO_DEC, ZERO_DEC};
     //Fix 14931 inizio
     return ret;
     /*
     UnitaMisura[] listaUM = new UnitaMisura[] {umRif, umPrm, umSec};
     BigDecimal[] valoriUM = new BigDecimal[]{qtaUMRif, qtaUMPrm, qtaUMSec};
     ArticoloDatiTecnici artTec = articolo.getArticoloDatiTecnici();

     //Individuazione dei pesi
     CategoriaUM categoriaPesi = PersDatiGen.getCurrentPersDatiGen().getCategoriaUMPeso();
     UnitaMisura umDefPesi = getUMPrimariaCategoria(categoriaPesi);
     int indicePesi = individuaUMConvertibile(listaUM, categoriaPesi);
     if(indicePesi >= 0)
     {
        ret[0] = ConvertitoreUM.converti(valoriUM[indicePesi], listaUM[indicePesi], umDefPesi, categoriaPesi.getUMAssociate());
        ret[1] = ret[0];
     }
     else
     {
        if(artTec.getUMPeso() != null)
        {
           if(artTec.getPesoNetto() != null)
           {
              BigDecimal pesoNettoArt = ConvertitoreUM.converti(artTec.getPesoNetto(), artTec.getUMPeso(), umDefPesi, categoriaPesi.getUMAssociate());
              if(pesoNettoArt != null && qtaUMPrm != null)
                 ret[0] = pesoNettoArt.multiply(qtaUMPrm);
           }
           if(artTec.getPeso() != null)
           {
              BigDecimal pesoLordoArt = ConvertitoreUM.converti(artTec.getPeso(), artTec.getUMPeso(), umDefPesi, categoriaPesi.getUMAssociate());
              if(pesoLordoArt != null && qtaUMPrm != null)
                 ret[1] = pesoLordoArt.multiply(qtaUMPrm);
           }

           //valorizzo entrambi uguali se uno non è valorizzato
           boolean nettoVal = isValorizzato(ret[0]);
           boolean lordoVal = isValorizzato(ret[1]);

           if(nettoVal && !lordoVal)
              ret[1] = ret[0];
           else if(!nettoVal && lordoVal)
              ret[0] = ret[1];
        }
     }

     //Individuazione del volume
     CategoriaUM categoriaVolumi = PersDatiGen.getCurrentPersDatiGen().getCategoriaUMVol();
     UnitaMisura umDefVolumi = getUMPrimariaCategoria(categoriaVolumi);
     int indiceVolumi = individuaUMConvertibile(listaUM, categoriaVolumi);
     if(indiceVolumi >= 0)
     {
        ret[2] = ConvertitoreUM.converti(valoriUM[indiceVolumi], listaUM[indiceVolumi], umDefVolumi, categoriaVolumi.getUMAssociate());
     }
     else
     {
        if(artTec.getVolume() != null && artTec.getUMVolume() != null)
        {
           BigDecimal volumeArt = ConvertitoreUM.converti(artTec.getVolume(), artTec.getUMVolume(), umDefVolumi, categoriaVolumi.getUMAssociate());
           if(volumeArt != null && qtaUMPrm != null)
              ret[2] = volumeArt.multiply(qtaUMPrm);
        }
     }

     ret = sistemaScalePesiEVolumePerRighe(ret);
     return ret;
     */
     //Fix 14931 fine
  }

  public static BigDecimal[] sistemaScalePesiEVolume(BigDecimal[] pesiEVolumi)
  {
     //Fix 13011 inizio
     BigDecimal minPeso = new BigDecimal("0.00");
     BigDecimal maxPeso = new BigDecimal("99999.99");
     BigDecimal minVol = new BigDecimal("0.000000");
     BigDecimal maxVol = new BigDecimal("999999.999999");
     BigDecimal[] ret = new BigDecimal[]{ZERO_DEC, ZERO_DEC, ZERO_DEC};
     if(pesiEVolumi[0] != null)
     {
        //ret[0] = pesiEVolumi[0].setScale(2, BigDecimal.ROUND_HALF_UP);//Fix 30871
		ret[0] = Q6Calc.get().setScale(pesiEVolumi[0],2, BigDecimal.ROUND_HALF_UP);//Fix 30871
        if(ret[0].compareTo(maxPeso) > 0)
           ret[0] = maxPeso;
        else if(ret[0].compareTo(minPeso) < 0)
           ret[0] = minPeso;
     }
     if(pesiEVolumi[1] != null)
     {
        //ret[1] = pesiEVolumi[1].setScale(2, BigDecimal.ROUND_HALF_UP);//Fix 30871
		ret[1] = Q6Calc.get().setScale(pesiEVolumi[1],2, BigDecimal.ROUND_HALF_UP);//Fix 30871
        if(ret[1].compareTo(maxPeso) > 0)
           ret[1] = maxPeso;
        else if(ret[1].compareTo(minPeso) < 0)
           ret[1] = minPeso;
     }
     if(pesiEVolumi[2] != null)
     {
        ret[2] = pesiEVolumi[2].setScale(6, BigDecimal.ROUND_HALF_UP);
        if(ret[2].compareTo(maxVol) > 0)
           ret[2] = maxVol;
        else if(ret[2].compareTo(minVol) < 0)
           ret[2] = minVol;
     }
     return ret;
     /*
     BigDecimal[] ret = new BigDecimal[]{ZERO_DEC, ZERO_DEC, ZERO_DEC};
     if(pesiEVolumi[0] != null)
        ret[0] = pesiEVolumi[0].setScale(2, BigDecimal.ROUND_HALF_UP);
     if(pesiEVolumi[1] != null)
        ret[1] = pesiEVolumi[1].setScale(2, BigDecimal.ROUND_HALF_UP);
     if(pesiEVolumi[2] != null)
        ret[2] = pesiEVolumi[2].setScale(6, BigDecimal.ROUND_HALF_UP);
     return ret;
     */
     //Fix 13011 fine
  }

  public static BigDecimal[] sistemaScalePesiEVolumePerRighe(BigDecimal[] pesiEVolumi)
  {
     //Fix 13011 inizio
     BigDecimal minPeso = new BigDecimal("0.00");
     BigDecimal maxPeso = new BigDecimal("999999.999");
     BigDecimal minVol = new BigDecimal("0.000000");
     BigDecimal maxVol = new BigDecimal("999999.999999");
     BigDecimal[] ret = new BigDecimal[]{ZERO_DEC, ZERO_DEC, ZERO_DEC};
     if(pesiEVolumi[0] != null)
     {
        //ret[0] = pesiEVolumi[0].setScale(3, BigDecimal.ROUND_HALF_UP);//Fix 30871
		ret[0] = Q6Calc.get().setScale(pesiEVolumi[0],3, BigDecimal.ROUND_HALF_UP);//Fix 30871
        if(ret[0].compareTo(maxPeso) > 0)
           ret[0] = maxPeso;
        else if(ret[0].compareTo(minPeso) < 0)
           ret[0] = minPeso;
     }
     if(pesiEVolumi[1] != null)
     {
        //ret[1] = pesiEVolumi[1].setScale(3, BigDecimal.ROUND_HALF_UP);//Fix 30871
		ret[1] = Q6Calc.get().setScale(pesiEVolumi[1],3, BigDecimal.ROUND_HALF_UP);//Fix 30871
        if(ret[1].compareTo(maxPeso) > 0)
           ret[1] = maxPeso;
        else if(ret[1].compareTo(minPeso) < 0)
           ret[1] = minPeso;
     }
     if(pesiEVolumi[2] != null)
     {
        ret[2] = pesiEVolumi[2].setScale(6, BigDecimal.ROUND_HALF_UP);
        if(ret[2].compareTo(maxVol) > 0)
           ret[2] = maxVol;
        else if(ret[2].compareTo(minVol) < 0)
           ret[2] = minVol;
     }
     return ret;

     /*
     BigDecimal[] ret = new BigDecimal[]{ZERO_DEC, ZERO_DEC, ZERO_DEC};
     if(pesiEVolumi[0] != null)
        ret[0] = pesiEVolumi[0].setScale(3, BigDecimal.ROUND_HALF_UP);
     if(pesiEVolumi[1] != null)
        ret[1] = pesiEVolumi[1].setScale(3, BigDecimal.ROUND_HALF_UP);
     if(pesiEVolumi[2] != null)
        ret[2] = pesiEVolumi[2].setScale(6, BigDecimal.ROUND_HALF_UP);
     return ret;
     */
     //Fix 13011 fine
  }

  //Controlla se nella lista di UM passate esiste una unita di misura appartenente alla catUM passata
  //se trovata ritorna indice corrispondente
  protected static int individuaUMConvertibile(UnitaMisura[] umList, CategoriaUM catUM)
  {
     int ret = -1;

     if(catUM == null)
        return ret;

     for(int i = 0; ret < 0 && i < 3; i++)
     {
        if(umList[i] != null)
        {
           List listaUMAssociate = catUM.getUMAssociate();
           Iterator iter = listaUMAssociate.iterator();
           while(iter.hasNext() && ret < 0)
           {
              Convertibile conv = (Convertibile)iter.next();
              if(conv.getUnitaMisura().getKey().equals(umList[i].getKey()))
                 ret = i;
           }
        }
     }
     return ret;
  }

  //Individua la um di riferimento della catUM
  protected static UnitaMisura getUMPrimariaCategoria(CategoriaUM catUM)
  {
     UnitaMisura ret = null;
     if(catUM != null)
     {
        List listaUMAssociate = catUM.getUMAssociate();
        Iterator iter = listaUMAssociate.iterator();
        while(iter.hasNext() && ret == null)
        {
           CategoriaUMUM umCat = (CategoriaUMUM)iter.next();
           if(umCat.getUMIsPrimaria())
              ret = umCat.getUnitaMisura();
        }
     }
     return ret;
  }

  protected static boolean isValorizzato(BigDecimal val)
  {
     boolean valorizzato = false;
     if(val != null && val.compareTo(ZERO_DEC) != 0)
        valorizzato = true;

     return valorizzato;
  }

  //Fix 12508 fine
	// Fix 18087 inizio
	public int salvaArticoliCosti(int rc) {
		if (iInCopia) {
			try {
				if (iArticoloDatiCosto != null) {
					if (iIsNew) {
						List articoliCosti = iArticoloDatiCosto.getArticoloCosto();
						Iterator ite = articoliCosti.iterator();
						while (ite.hasNext()) {
							ArticoloCosto artCosto = (ArticoloCosto) ite.next();
							artCosto.setOnDB(false);
						}
					}
				}
				rc = iArticoloDatiCosto.saveOwnedObjects(rc);
			} catch (SQLException e) {
				e.printStackTrace(Trace.excStream);
			}
		}
		return rc;

	}
	// Fix 18087 fine
	
	// Fix 20517 inizio
  public boolean isGestioneLogisticaLight() {
    return getArticoloDatiMagaz().getGestioneLogisticaLight();
  }

  public void setGestioneLogisticaLight(boolean gestLogisLight) {
    getArticoloDatiMagaz().setGestioneLogisticaLight(gestLogisLight);
  }	
	// Fix 20517 fine
  
  //Fix 22528 inizio
  public char getUMUbicazioniBarcode() {
  	return getArticoloDatiMagaz().getUMUbicazioniBarcode();
  }

  public void setUMUbicazioniBarcode(char umUB) {
  	getArticoloDatiMagaz().setUMUbicazioniBarcode(umUB);
  }
	//Fix 22528 fine
 

  
  // Fix 21016 //22170
	//22255 inizio
	public static Articolo getArticoloWithIdProdotto(String idAzienda, String idProdotto) {
    String where = ArticoloTM.ID_AZIENDA + " = '" + idAzienda + "' AND  " + ArticoloTM.ID_PRODOTTO + " = '" + idProdotto + "'";
    try {
      Vector articoloList = Articolo.retrieveList(where, "", false);
      if (!articoloList.isEmpty()) {
        return (Articolo) articoloList.get(0);
      }
    }
    catch (Exception ex) {
      ex.printStackTrace(Trace.excStream);
    }
    return null;
	  
	}
	
	public static Articolo getArticoloWithIdProdotto(String idProdotto) {
    return getArticoloWithIdProdotto(Azienda.getAziendaCorrente(), idProdotto);
  }
	//22255 fine
  // Fix 21016 //22170
	

	
	//Fix 28684
		
	public ArticoloDatiMagaz getArticoloDatiMagazWf()
	{
		return iArticoloDatiMagaz;
	}
	
	public void setArticoloDatiMagazWf(ArticoloDatiMagaz articoloDatiMagaz)
	{
		iArticoloDatiMagaz = articoloDatiMagaz;
		iArticoloDatiMagaz.setInitialized(true);
	}
	
	public ArticoloDatiIdent getArticoloDatiIdentWf()
	{
		return iArticoloDatiIdent;
	}
	
	public void setArticoloDatiIdentWf(ArticoloDatiIdent articoloDatiIdent)
	{
		iArticoloDatiIdent = articoloDatiIdent;
		iArticoloDatiIdent.setInitialized(true);
	}
	
	public ArticoloDatiTecnici getArticoloDatiTecniciWf()
	{
		return iArticoloDatiTecnici;
	}
	
	public void setArticoloDatiTecniciWf(ArticoloDatiTecnici articoloDatiTecnici)
	{
		iArticoloDatiTecnici = articoloDatiTecnici;
		iArticoloDatiTecnici.setInitialized(true);
	}
	
	public ArticoloDatiVendita getArticoloDatiVenditaWf()
	{
		return iArticoloDatiVendita;
	}
	
	public void setArticoloDatiVenditaWf(ArticoloDatiVendita articoloDatiVendita)
	{
		iArticoloDatiVendita = articoloDatiVendita;
		iArticoloDatiVendita.setInitialized(true);
	}
	
	public ArticoloDatiAcquisto getArticoloDatiAcquistoWf()
	{
		return iArticoloDatiAcquisto;
	}
	
	public void setArticoloDatiAcquistoWf(ArticoloDatiAcquisto articoloDatiAcquisto)
	{
		iArticoloDatiAcquisto = articoloDatiAcquisto;
		iArticoloDatiAcquisto.setInitialized(true);
	}
	
	public ArticoloDatiPianif getArticoloDatiPianifWf()
	{
		return iArticoloDatiPianif;
	}
	
	public void setArticoloDatiPianifWf(ArticoloDatiPianif articoloDatiPianif)
	{
		iArticoloDatiPianif = articoloDatiPianif;
		iArticoloDatiPianif.setInitialized(true);
	}
	
	public ArticoloDatiProduz getArticoloDatiProduzWf()
	{
		return iArticoloDatiProduz;
	}
	
	public void setArticoloDatiProduzWf(ArticoloDatiProduz articoloDatiProduz)
	{
		iArticoloDatiProduz = articoloDatiProduz;
		iArticoloDatiProduz.setInitialized(true);
	}
	
	public ArticoloDatiContab getArticoloDatiContabWf()
	{
		return iArticoloDatiContab;
	}
	
	public void setArticoloDatiContabWf(ArticoloDatiContab articoloDatiContab)
	{
		iArticoloDatiContab = articoloDatiContab;
		iArticoloDatiContab.setInitialized(true);
	}
	
	public ArticoloDatiCosto getArticoloDatiCostoWf()
	{
		return iArticoloDatiCosto;
	}
	
	public void setArticoloDatiCostoWf(ArticoloDatiCosto articoloDatiCosto)
	{
		iArticoloDatiCosto = articoloDatiCosto;
		iArticoloDatiCosto.setInitialized(true);
	}
	
	public ArticoloDatiQualita getArticoloDatiQualitaWf()
	{
		return iArticoloDatiQualita;
	}
	
	public void setArticoloDatiQualitaWf(ArticoloDatiQualita articoloDatiQualita)
	{
		iArticoloDatiQualita = articoloDatiQualita;
		iArticoloDatiQualita.setInitialized(true);
	}
	
	public ArticoloDatiStabil getArticoloDatiStabilWf()
	{
		return iArticoloDatiStabil;
	}
	
	public void setArticoloDatiStabilWf(ArticoloDatiStabil articoloDatiStabil)
	{
		iArticoloDatiStabil = articoloDatiStabil;
		iArticoloDatiStabil.setInitialized(true);
	}
	
	public ArticoloDatiEstensioni getArticoloDatiEstensioniWf()
	{
		return iArticoloDatiEstensioni;
	}
	
	public void setArticoloDatiEstensioniWf(ArticoloDatiEstensioni articoloDatiEstensioni)
	{
		iArticoloDatiEstensioni = articoloDatiEstensioni;
		iArticoloDatiEstensioni.setInitialized(true);
	}
	
	public ArticoloDatiVersioni getArticoloDatiVersioniWf()
	{
		return iArticoloDatiVersioni;
	}
	
	public void setArticoloDatiVersioniWf(ArticoloDatiVersioni articoloDatiVersioni)
	{
		iArticoloDatiVersioni = articoloDatiVersioni;
		iArticoloDatiVersioni.setInitialized(true);
	}
	 
	//30372 inizio
	public List<ArticoloVersione> getVersioni(java.sql.Date dataInizio, java.sql.Date dataFine){
		List<ArticoloVersione> articoloVersioniTraLeDate = new ArrayList<ArticoloVersione>();
		List<ArticoloVersione> articoloVersioni = getArticoloDatiVersioni().getVersioni();
		Iterator<ArticoloVersione> it = articoloVersioni.iterator();
		while(it.hasNext()) {
			ArticoloVersione articoloVersione = it.next();
			boolean dopoOUgualeDataInizio = TimeUtils.differenceInDays(articoloVersione.getDataInizio(), dataInizio) >= 0;
			boolean primaOUgualeDataFine = TimeUtils.differenceInDays(articoloVersione.getDataInizio(), dataFine) <= 0;
			if(dopoOUgualeDataInizio && primaOUgualeDataFine) {
				articoloVersioniTraLeDate.add(articoloVersione);
			}
		}
		return articoloVersioniTraLeDate;
	}
	//30372 fine
	
	
	//Fix 33959 inizio
	protected static final String SELECT_NOMENCLATURA_DOG_ART  = "SELECT T13CD FROM " + SystemParam.getSchema("PRIMROSE") + " THARTPT WHERE T01CD = ? AND ID_ARTICOLO = ?";
	public static final CachedStatement cSelectNomenclaturaDogArt = new CachedStatement(SELECT_NOMENCLATURA_DOG_ART);
	
	protected static final String SELECT_NOMENCLATURA_DOG_ClassMat  = "SELECT T13CD  FROM " + SystemParam.getSchema("PRIMROSE") + " THCLMPT  WHERE T01CD = ? AND ID_CLS_MATERIALE = ?";
	public static final CachedStatement cSelectNomenclaturaDogClassMat = new CachedStatement(SELECT_NOMENCLATURA_DOG_ClassMat);

	public String getNomenclaturaDogArt()
	{
	      String nomenclaturaDog = null;
	      ResultSet rs = null;
	      try {
	    	  PreparedStatement ps = cSelectNomenclaturaDogArt.getStatement();
	    	  Database db = ConnectionManager.getCurrentDatabase();

	    	  db.setString(ps,1, getIdAzienda());
	    	  db.setString(ps,2, getIdArticolo());

	    	  rs = ps.executeQuery();
	    	  if (rs.next())
	    		  nomenclaturaDog = rs.getString(1);
	      }catch (SQLException e) {
	    	  e.printStackTrace(Trace.logStream);
	      }finally {
	    	  if (rs != null ) try{rs.close();}catch(SQLException ex){}
	      }
	      return nomenclaturaDog;
	}

	public String getNomenclaturaDogClassMat()
	{
	      String nomenclaturaDog = null;
	      if (getIdClasseMateriale()==null)
	    	  return null;
	      
	      ResultSet rs = null;
	      try {
	    	  PreparedStatement ps = cSelectNomenclaturaDogClassMat.getStatement();
	    	  Database db = ConnectionManager.getCurrentDatabase();

	    	  db.setString(ps,1, getIdAzienda());
	    	  db.setString(ps,2, getIdClasseMateriale());

	    	  rs = ps.executeQuery();
	    	  if (rs.next())
	    		  nomenclaturaDog = rs.getString(1);
	      }catch (SQLException e) {
	    	  e.printStackTrace(Trace.logStream);
	      }finally {
	    	  if (rs != null ) try{rs.close();}catch(SQLException ex){}
	      }
	      return nomenclaturaDog;
	}
	
	
	public String getNomenclaturaDog()
	{
		String nomenclaturaDog = getNomenclaturaDogArt();
		if (nomenclaturaDog != null)
			return nomenclaturaDog;
		return getNomenclaturaDogClassMat();
	}
	
	//Fix 33959 inizio			  
	
}

