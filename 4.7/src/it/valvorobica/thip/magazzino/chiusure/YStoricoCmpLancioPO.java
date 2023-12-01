/*
 * @(#)YStoricoCmpLancioPO.java
 */

/**
 * null
 *
 * <br></br><b>Copyright (C) : Thera SpA</b>
 * @author Wizard 30/11/2023 at 09:18:25
 */
/*
 * Revisions:
 * Date          Owner      Description
 * 30/11/2023    Wizard     Codice generato da Wizard
 *
 */
package it.valvorobica.thip.magazzino.chiusure;
import com.thera.thermfw.persist.*;
import java.sql.*;
import java.util.*;
import it.thera.thip.base.azienda.AziendaEstesa;
import it.thera.thip.magazzino.chiusure.PeriodoCalFiscale;
import it.thera.thip.cs.*;
import com.thera.thermfw.common.*;
import it.thera.thip.base.azienda.Azienda;
import com.thera.thermfw.security.*;

public abstract class YStoricoCmpLancioPO extends EntitaAzienda implements BusinessObject, Authorizable, Deletable, Conflictable {

  
  /**
   *  instance
   */
  private static YStoricoCmpLancio cInstance;

  /**
   * Attributo iData
   */
  protected java.sql.Date iData;

  /**
   * Attributo iPeriodocalfiscale
   */
  protected Proxy iPeriodocalfiscale = new Proxy(it.thera.thip.magazzino.chiusure.PeriodoCalFiscale.class);

  
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
   * 30/11/2023    CodeGen     Codice generato da CodeGenerator
   *
   */
  public static Vector retrieveList(String where, String orderBy, boolean optimistic) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    if (cInstance == null)
      cInstance = (YStoricoCmpLancio)Factory.createObject(YStoricoCmpLancio.class);
    return PersistentObject.retrieveList(cInstance, where, orderBy, optimistic);
  }

  /**
   *  elementWithKey
   * @param key
   * @param lockType
   * @return YStoricoCmpLancio
   * @throws SQLException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/11/2023    CodeGen     Codice generato da CodeGenerator
   *
   */
  public static YStoricoCmpLancio elementWithKey(String key, int lockType) throws SQLException {
    return (YStoricoCmpLancio)PersistentObject.elementWithKey(YStoricoCmpLancio.class, key, lockType);
  }

  /**
   * YStoricoCmpLancioPO
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/11/2023    Wizard     Codice generato da Wizard
   *
   */
  public YStoricoCmpLancioPO() {
    setIdAzienda(Azienda.getAziendaCorrente());
  }

  /**
   * Valorizza l'attributo. 
   * @param data
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/11/2023    Wizard     Codice generato da Wizard
   *
   */
  public void setData(java.sql.Date data) {
    this.iData = data;
    setDirty();
  }

  /**
   * Restituisce l'attributo. 
   * @return java.sql.Date
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/11/2023    Wizard     Codice generato da Wizard
   *
   */
  public java.sql.Date getData() {
    return iData;
  }

  /**
   * Valorizza l'attributo. 
   * @param periodocalfiscale
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/11/2023    Wizard     Codice generato da Wizard
   *
   */
  public void setPeriodocalfiscale(PeriodoCalFiscale periodocalfiscale) {
    String idAzienda = getIdAzienda();
    if (periodocalfiscale != null) {
      idAzienda = KeyHelper.getTokenObjectKey(periodocalfiscale.getKey(), 1);
    }
    setIdAziendaInternal(idAzienda);
    this.iPeriodocalfiscale.setObject(periodocalfiscale);
    setDirty();
    setOnDB(false);
  }

  /**
   * Restituisce l'attributo. 
   * @return PeriodoCalFiscale
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/11/2023    Wizard     Codice generato da Wizard
   *
   */
  public PeriodoCalFiscale getPeriodocalfiscale() {
    return (PeriodoCalFiscale)iPeriodocalfiscale.getObject();
  }

  /**
   * setPeriodocalfiscaleKey
   * @param key
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/11/2023    Wizard     Codice generato da Wizard
   *
   */
  public void setPeriodocalfiscaleKey(String key) {
    iPeriodocalfiscale.setKey(key);
    String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
    setIdAziendaInternal(idAzienda);
    setDirty();
    setOnDB(false);
  }

  /**
   * getPeriodocalfiscaleKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/11/2023    Wizard     Codice generato da Wizard
   *
   */
  public String getPeriodocalfiscaleKey() {
    return iPeriodocalfiscale.getKey();
  }

  /**
   * Valorizza l'attributo. 
   * @param idAzienda
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/11/2023    Wizard     Codice generato da Wizard
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
   * 30/11/2023    Wizard     Codice generato da Wizard
   *
   */
  public String getIdAzienda() {
    String key = iAzienda.getKey();
    return key;
  }

  /**
   * Valorizza l'attributo. 
   * @param idAnnoFiscale
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/11/2023    Wizard     Codice generato da Wizard
   *
   */
  public void setIdAnnoFiscale(String idAnnoFiscale) {
    String key = iPeriodocalfiscale.getKey();
    iPeriodocalfiscale.setKey(KeyHelper.replaceTokenObjectKey(key , 2, idAnnoFiscale));
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
   * 30/11/2023    Wizard     Codice generato da Wizard
   *
   */
  public String getIdAnnoFiscale() {
    String key = iPeriodocalfiscale.getKey();
    String objIdAnnoFiscale = KeyHelper.getTokenObjectKey(key,2);
    return objIdAnnoFiscale;
    
  }

  /**
   * Valorizza l'attributo. 
   * @param codicePeriodo
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/11/2023    Wizard     Codice generato da Wizard
   *
   */
  public void setCodicePeriodo(Integer codicePeriodo) {
    String key = iPeriodocalfiscale.getKey();
    iPeriodocalfiscale.setKey(KeyHelper.replaceTokenObjectKey(key , 3, codicePeriodo));
    setDirty();
    setOnDB(false);
  }

  /**
   * Restituisce l'attributo. 
   * @return Integer
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/11/2023    Wizard     Codice generato da Wizard
   *
   */
  public Integer getCodicePeriodo() {
    String key = iPeriodocalfiscale.getKey();
    String objCodicePeriodo = KeyHelper.getTokenObjectKey(key,3);
    return KeyHelper.stringToIntegerObj(objCodicePeriodo);
  }

  /**
   * setEqual
   * @param obj
   * @throws CopyException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/11/2023    Wizard     Codice generato da Wizard
   *
   */
  public void setEqual(Copyable obj) throws CopyException {
    super.setEqual(obj);
    YStoricoCmpLancioPO yStoricoCmpLancioPO = (YStoricoCmpLancioPO)obj;
    if (yStoricoCmpLancioPO.iData != null)
        iData = (java.sql.Date)yStoricoCmpLancioPO.iData.clone();
    iPeriodocalfiscale.setEqual(yStoricoCmpLancioPO.iPeriodocalfiscale);
  }

  /**
   * checkAll
   * @param components
   * @return Vector
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/11/2023    Wizard     Codice generato da Wizard
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
   * 30/11/2023    Wizard     Codice generato da Wizard
   *
   */
  public void setKey(String key) {
    setIdAzienda(KeyHelper.getTokenObjectKey(key, 1));
    setIdAnnoFiscale(KeyHelper.getTokenObjectKey(key, 2));
    setCodicePeriodo(KeyHelper.stringToIntegerObj(KeyHelper.getTokenObjectKey(key, 3)));
  }

  /**
   *  getKey
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/11/2023    Wizard     Codice generato da Wizard
   *
   */
  public String getKey() {
    String idAzienda = getIdAzienda();
    String idAnnoFiscale = getIdAnnoFiscale();
    Integer codicePeriodo = getCodicePeriodo();
    Object[] keyParts = {idAzienda, idAnnoFiscale, codicePeriodo};
    return KeyHelper.buildObjectKey(keyParts);
  }

  /**
   * isDeletable
   * @return boolean
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/11/2023    Wizard     Codice generato da Wizard
   *
   */
  public boolean isDeletable() {
    return checkDelete() == null;
  }

  /**
   * toString
   * @return String
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/11/2023    Wizard     Codice generato da Wizard
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
   * 30/11/2023    CodeGen     Codice generato da CodeGenerator
   *
   */
  protected TableManager getTableManager() throws SQLException {
    return YStoricoCmpLancioTM.getInstance();
  }

  /**
   * setIdAziendaInternal
   * @param idAzienda
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/11/2023    Wizard     Codice generato da Wizard
   *
   */
  protected void setIdAziendaInternal(String idAzienda) {
    iAzienda.setKey(idAzienda);
        String key2 = iPeriodocalfiscale.getKey();
    iPeriodocalfiscale.setKey(KeyHelper.replaceTokenObjectKey(key2, 1, idAzienda));
  }

}

