/*
 * @(#)YStoricoCmpLancioTM.java
 */

/**
 * YStoricoCmpLancioTM
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
import com.thera.thermfw.common.*;
import java.sql.*;
import com.thera.thermfw.base.*;
import it.thera.thip.cs.*;

public class YStoricoCmpLancioTM extends TableManager {

  
  /**
   * Attributo ID_AZIENDA
   */
  public static final String ID_AZIENDA = "ID_AZIENDA";

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
   * Attributo ID_ANNO_FISCALE
   */
  public static final String ID_ANNO_FISCALE = "ID_ANNO_FISCALE";

  /**
   * Attributo ID_PER_ANNO_FSC
   */
  public static final String ID_PER_ANNO_FSC = "ID_PER_ANNO_FSC";

  /**
   * Attributo DATA
   */
  public static final String DATA = "DATA";

  /**
   *  TABLE_NAME
   */
  public static final String TABLE_NAME = SystemParam.getSchema("THIPPERS") + "YSTORICO_CMP_LANCIO";

  /**
   *  instance
   */
  private static TableManager cInstance;

  /**
   *  CLASS_NAME
   */
  private static final String CLASS_NAME = it.valvorobica.thip.magazzino.chiusure.YStoricoCmpLancio.class.getName();

  
  /**
   *  getInstance
   * @return TableManager
   * @throws SQLException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/11/2023    CodeGen     Codice generato da CodeGenerator
   *
   */
  public synchronized static TableManager getInstance() throws SQLException {
    if (cInstance == null) {
      cInstance = (TableManager)Factory.createObject(YStoricoCmpLancioTM.class);
    }
    return cInstance;
  }

  /**
   *  YStoricoCmpLancioTM
   * @throws SQLException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/11/2023    CodeGen     Codice generato da CodeGenerator
   *
   */
  public YStoricoCmpLancioTM() throws SQLException {
    super();
  }

  /**
   *  initialize
   * @throws SQLException
   */
  /*
   * Revisions:
   * Date          Owner      Description
   * 30/11/2023    CodeGen     Codice generato da CodeGenerator
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
   * 30/11/2023    Wizard     Codice generato da Wizard
   *
   */
  protected void initializeRelation() throws SQLException {
    super.initializeRelation();
    addAttribute("Data", DATA);
    addAttribute("IdAzienda", ID_AZIENDA);
    addAttribute("IdAnnoFiscale", ID_ANNO_FISCALE);
    addAttribute("CodicePeriodo", ID_PER_ANNO_FSC, "getIntegerObject");
    
    addComponent("DatiComuniEstesi", DatiComuniEstesiTTM.class);
    setKeys(ID_AZIENDA + "," + ID_ANNO_FISCALE + "," + ID_PER_ANNO_FSC);

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
   * 30/11/2023    Wizard     Codice generato da Wizard
   *
   */
  private void init() throws SQLException {
    configure(DATA + ", " + ID_AZIENDA + ", " + ID_ANNO_FISCALE + ", " + ID_PER_ANNO_FSC
         + ", " + STATO + ", " + R_UTENTE_CRZ + ", " + TIMESTAMP_CRZ + ", " + R_UTENTE_AGG
         + ", " + TIMESTAMP_AGG);
  }

}

