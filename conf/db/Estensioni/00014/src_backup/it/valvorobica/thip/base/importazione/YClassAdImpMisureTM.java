/*
 * @(#)YClassAdImpMisureTM.java
 */

/**
 * YClassAdImpMisureTM
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
import com.thera.thermfw.common.*;
import java.sql.*;
import com.thera.thermfw.base.*;
import it.thera.thip.cs.*;

public class YClassAdImpMisureTM extends TableManager {

  
  /**
   * Attributo ID_AZIENDA
   */
  public static final String ID_AZIENDA = "ID_AZIENDA";

  /**
   * Attributo ID_FORNITORE
   */
  public static final String ID_FORNITORE = "ID_FORNITORE";

  /**
   * Attributo STATO
   */
  public static final String STATO = "STATO";

  /**
   * Attributo R_UTENTE_CRZ
   */
  public static final String R_UTENTE_CRZ = "R_UTENTE_CRZ";

  /**
   * Attributo TIMESTAMP_CRZ
   */
  public static final String TIMESTAMP_CRZ = "TIMESTAMP_CRZ";

  /**
   * Attributo R_UTENTE_AGG
   */
  public static final String R_UTENTE_AGG = "R_UTENTE_AGG";

  /**
   * Attributo TIMESTAMP_AGG
   */
  public static final String TIMESTAMP_AGG = "TIMESTAMP_AGG";

  /**
   * Attributo TITOLO_COLONNA
   */
  public static final String TITOLO_COLONNA = "TITOLO_COLONNA";

  /**
   * Attributo BO_ATTRIBUTE
   */
  public static final String BO_ATTRIBUTE = "BO_ATTRIBUTE";

  /**
   * Attributo VALORE_DEFAULT
   */
  public static final String VALORE_DEFAULT = "VALORE_DEFAULT";

  /**
   * Attributo ESCLUDI
   */
  public static final String ESCLUDI = "ESCLUDI";

  /**
   *  TABLE_NAME
   */
  public static final String TABLE_NAME = SystemParam.getSchema("THIPPERS") + "YCLASS_AD_IMP_MISURE";

  /**
   *  instance
   */
  private static TableManager cInstance;

  /**
   *  CLASS_NAME
   */
  private static final String CLASS_NAME = it.valvorobica.thip.base.importazione.YClassAdImpMisure.class.getName();

  
  /**
   *  getInstance
   * @return TableManager
   * @throws SQLException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    CodeGen     Codice generato da CodeGenerator
   *
   */
  public synchronized static TableManager getInstance() throws SQLException {
    if (cInstance == null) {
      cInstance = (TableManager)Factory.createObject(YClassAdImpMisureTM.class);
    }
    return cInstance;
  }

  /**
   *  YClassAdImpMisureTM
   * @throws SQLException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    CodeGen     Codice generato da CodeGenerator
   *
   */
  public YClassAdImpMisureTM() throws SQLException {
    super();
  }

  /**
   *  initialize
   * @throws SQLException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    CodeGen     Codice generato da CodeGenerator
   *
   */
  protected void initialize() throws SQLException {
    setTableName(TABLE_NAME);
    setObjClassName(CLASS_NAME);
    init();
  }

  /**
   *  initializeRelation
   * @throws SQLException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  protected void initializeRelation() throws SQLException {
    super.initializeRelation();
    addAttribute("TitoloColonna", TITOLO_COLONNA);
    addAttribute("BoAttribute", BO_ATTRIBUTE);
    addAttribute("ValoreDefault", VALORE_DEFAULT);
    addAttribute("Escludi", ESCLUDI);
    addAttribute("IdAzienda", ID_AZIENDA);
    addAttribute("IdFornitore", ID_FORNITORE);
    
    addComponent("DatiComuniEstesi", DatiComuniEstesiTTM.class);
    setKeys(ID_AZIENDA + "," + ID_FORNITORE + "," + TITOLO_COLONNA);

    setTimestampColumn("TIMESTAMP_AGG");
    ((it.thera.thip.cs.DatiComuniEstesiTTM)getTransientTableManager("DatiComuniEstesi")).setExcludedColums();
  }

  /**
   *  init
   * @throws SQLException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 21/12/2023    Wizard     Codice generato da Wizard
   *
   */
  private void init() throws SQLException {
    configure(TITOLO_COLONNA + ", " + BO_ATTRIBUTE + ", " + VALORE_DEFAULT + ", " + ESCLUDI
         + ", " + ID_AZIENDA + ", " + ID_FORNITORE + ", " + STATO + ", " + R_UTENTE_CRZ
         + ", " + TIMESTAMP_CRZ + ", " + R_UTENTE_AGG + ", " + TIMESTAMP_AGG);
  }

}

