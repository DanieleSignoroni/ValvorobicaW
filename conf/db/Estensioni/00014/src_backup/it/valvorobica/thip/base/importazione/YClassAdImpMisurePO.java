/*
 * @(#)YClassAdImpMisurePO.java
 */

/**
 * null
 *
 * <br></br><b>Copyright (C) : Thera SpA</b>
 * @author Wizard 21/12/2023 at 11:26:49
 */
/*
 * Revisions:
 * Date          Owner      Description
 * 21/12/2023    Wizard     Codice generato da Wizard
 *
 */
package it.valvorobica.thip.base.importazione;
import com.thera.thermfw.persist.*;
import java.sql.*;
import java.util.*;
import it.thera.thip.base.azienda.AziendaEstesa;
import it.thera.thip.cs.*;
import com.thera.thermfw.common.*;
import it.thera.thip.base.azienda.Azienda;
import com.thera.thermfw.security.*;

public abstract class YClassAdImpMisurePO extends EntitaAzienda implements BusinessObject, Authorizable, Deletable, Child, Conflictable {

  
  /**
   *  instance
   */
  private static YClassAdImpMisure cInstance;

  /**
   * Attributo iTitoloColonna
   */
  protected String iTitoloColonna;

  /**
   * Attributo iBoAttribute
   */
  protected String iBoAttribute;

  /**
   * Attributo iValoreDefault
   */
  protected String iValoreDefault;

  /**
   * Attributo iEscludi
   */
  protected boolean iEscludi = false;

  /**
   * Attributo iParent
   */
  protected Proxy iParent = new Proxy(it.valvorobica.thip.base.importazione.YHdrImpMisure.class);

  
  /**
   *  retrieveList
   * @param where
   * @param orderBy
   * @param optimistic
   * @return Vector
   * @throws SQLException
   * @throws ClassNotFoundException
   * @throws InstantiationException
   * @throws IllegalAccessException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    CodeGen     Codice generato da CodeGenerator
   *
   */
  public static Vector retrieveList(String where, String orderBy, boolean optimistic) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    if (cInstance == null)
      cInstance = (YClassAdImpMisure)Factory.createObject(YClassAdImpMisure.class);
    return PersistentObject.retrieveList(cInstance, where, orderBy, optimistic);
  }

  /**
   *  elementWithKey
   * @param key
   * @param lockType
   * @return YClassAdImpMisure
   * @throws SQLException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    CodeGen     Codice generato da CodeGenerator
   *
   */
  public static YClassAdImpMisure elementWithKey(String key, int lockType) throws SQLException {
    return (YClassAdImpMisure)PersistentObject.elementWithKey(YClassAdImpMisure.class, key, lockType);
  }

  /**
   * YClassAdImpMisurePO
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public YClassAdImpMisurePO() {
    setEscludi(false);
    setIdAzienda(Azienda.getAziendaCorrente());
  }

  /**
   * Valorizza l'attributo. 
   * @param titoloColonna
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public void setTitoloColonna(String titoloColonna) {
    this.iTitoloColonna = titoloColonna;
    setDirty();
    setOnDB(false);
  }

  /**
   * Restituisce l'attributo. 
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public String getTitoloColonna() {
    return iTitoloColonna;
  }

  /**
   * Valorizza l'attributo. 
   * @param boAttribute
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public void setBoAttribute(String boAttribute) {
    this.iBoAttribute = boAttribute;
    setDirty();
  }

  /**
   * Restituisce l'attributo. 
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public String getBoAttribute() {
    return iBoAttribute;
  }

  /**
   * Valorizza l'attributo. 
   * @param valoreDefault
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public void setValoreDefault(String valoreDefault) {
    this.iValoreDefault = valoreDefault;
    setDirty();
  }

  /**
   * Restituisce l'attributo. 
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public String getValoreDefault() {
    return iValoreDefault;
  }

  /**
   * Valorizza l'attributo. 
   * @param escludi
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public void setEscludi(boolean escludi) {
    this.iEscludi = escludi;
    setDirty();
  }

  /**
   * Restituisce l'attributo. 
   * @return boolean
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public boolean getEscludi() {
    return iEscludi;
  }

  /**
   * Valorizza l'attributo. 
   * @param parent
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public void setParent(YHdrImpMisure parent) {
    String idAzienda = getIdAzienda();
    if (parent != null) {
      idAzienda = KeyHelper.getTokenObjectKey(parent.getKey(), 1);
    }
    setIdAziendaInternal(idAzienda);
    this.iParent.setObject(parent);
    setDirty();
    setOnDB(false);
  }

  /**
   * Restituisce l'attributo. 
   * @return YHdrImpMisure
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public YHdrImpMisure getParent() {
    return (YHdrImpMisure)iParent.getObject();
  }

  /**
   * setParentKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public void setParentKey(String key) {
    iParent.setKey(key);
    String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
    setIdAziendaInternal(idAzienda);
    setDirty();
    setOnDB(false);
  }

  /**
   * getParentKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public String getParentKey() {
    return iParent.getKey();
  }

  /**
   * Valorizza l'attributo. 
   * @param idAzienda
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public void setIdAzienda(String idAzienda) {
    setIdAziendaInternal(idAzienda);
    setDirty();
    setOnDB(false);
  }

  /**
   * Restituisce l'attributo. 
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public String getIdAzienda() {
    String key = iAzienda.getKey();
    return key;
  }

  /**
   * Valorizza l'attributo. 
   * @param idFornitore
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public void setIdFornitore(String idFornitore) {
    String key = iParent.getKey();
    iParent.setKey(KeyHelper.replaceTokenObjectKey(key , 2, idFornitore));
    setDirty();
    setOnDB(false);
  }

  /**
   * Restituisce l'attributo. 
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public String getIdFornitore() {
    String key = iParent.getKey();
    String objIdFornitore = KeyHelper.getTokenObjectKey(key,2);
    return objIdFornitore;
  }

  /**
   * setEqual
   * @param obj
   * @throws CopyException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public void setEqual(Copyable obj) throws CopyException {
    super.setEqual(obj);
    YClassAdImpMisurePO yClassAdImpMisurePO = (YClassAdImpMisurePO)obj;
    iParent.setEqual(yClassAdImpMisurePO.iParent);
  }

  /**
   * checkAll
   * @param components
   * @return Vector
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public Vector checkAll(BaseComponentsCollection components) {
    Vector errors = new Vector();
    components.runAllChecks(errors);
    return errors;
  }

  /**
   *  setKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public void setKey(String key) {
    setIdAzienda(KeyHelper.getTokenObjectKey(key, 1));
    setIdFornitore(KeyHelper.getTokenObjectKey(key, 2));
    setTitoloColonna(KeyHelper.getTokenObjectKey(key, 3));
  }

  /**
   *  getKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public String getKey() {
    String idAzienda = getIdAzienda();
    String idFornitore = getIdFornitore();
    String titoloColonna = getTitoloColonna();
    Object[] keyParts = {idAzienda, idFornitore, titoloColonna};
    return KeyHelper.buildObjectKey(keyParts);
  }

  /**
   * isDeletable
   * @return boolean
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public boolean isDeletable() {
    return checkDelete() == null;
  }

  /**
   * getFatherKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public String getFatherKey() {
    return getParentKey();
  }

  /**
   * setFatherKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public void setFatherKey(String key) {
    setParentKey(key);
  }

  /**
   * setFather
   * @param father
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public void setFather(PersistentObject father) {
    iParent.setObject(father);
  }

  /**
   * getOrderByClause
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public String getOrderByClause() {
    return "";
  }

  /**
   * toString
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  public String toString() {
    return getClass().getName() + " [" + KeyHelper.formatKeyString(getKey()) + "]";
  }

  /**
   *  getTableManager
   * @return TableManager
   * @throws SQLException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    CodeGen     Codice generato da CodeGenerator
   *
   */
  protected TableManager getTableManager() throws SQLException {
    return YClassAdImpMisureTM.getInstance();
  }

  /**
   * setIdAziendaInternal
   * @param idAzienda
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  protected void setIdAziendaInternal(String idAzienda) {
    iAzienda.setKey(idAzienda);
        String key2 = iParent.getKey();
    iParent.setKey(KeyHelper.replaceTokenObjectKey(key2, 1, idAzienda));
  }

}

