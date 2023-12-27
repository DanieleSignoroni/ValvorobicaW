package it.valvorobica.thip.base.importazione;

import java.sql.SQLException;

import com.thera.thermfw.base.SystemParam;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.TableManager;

import it.thera.thip.cs.DatiComuniEstesiTTM;

/**
 * <h1>Softre Solutions</h1>
 * <br>
 * @author Daniele Signoroni 22/12/2023
 * <br><br>
 * <b>71366	DSSOF3	22/12/2023</b>    <p>Descrittore di attributo importazione</p>
 */

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
	 * Attributo CLASS_HDR
	 */
	public static final String CLASS_HDR = "CLASS_HDR";

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
	 * Attributo VALORE_DEFAULT
	 */
	public static final String VALORE_DEFAULT = "VALORE_DEFAULT";

	/**
	 * Attributo ESCLUDI
	 */
	public static final String ESCLUDI = "ESCLUDI";

	/**
	 * Attributo CLASS_AD
	 */
	public static final String CLASS_AD = "CLASS_AD";

	/**
	 * TABLE_NAME
	 */
	public static final String TABLE_NAME = SystemParam.getSchema("THIPPERS") + "YCLASS_AD_IMP_MISURE";

	/**
	 * instance
	 */
	private static TableManager cInstance;

	/**
	 * CLASS_NAME
	 */
	private static final String CLASS_NAME = it.valvorobica.thip.base.importazione.YClassAdImpMisure.class.getName();

	/**
	 * getInstance
	 * 
	 * @return TableManager
	 * @throws SQLException
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 CodeGen Codice generato da
	 * CodeGenerator
	 *
	 */
	public synchronized static TableManager getInstance() throws SQLException {
		if (cInstance == null) {
			cInstance = (TableManager) Factory.createObject(YClassAdImpMisureTM.class);
		}
		return cInstance;
	}

	/**
	 * YClassAdImpMisureTM
	 * 
	 * @throws SQLException
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 CodeGen Codice generato da
	 * CodeGenerator
	 *
	 */
	public YClassAdImpMisureTM() throws SQLException {
		super();
	}

	/**
	 * initialize
	 * 
	 * @throws SQLException
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 CodeGen Codice generato da
	 * CodeGenerator
	 *
	 */
	protected void initialize() throws SQLException {
		setTableName(TABLE_NAME);
		setObjClassName(CLASS_NAME);
		init();
	}

	/**
	 * initializeRelation
	 * 
	 * @throws SQLException
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	protected void initializeRelation() throws SQLException {
		super.initializeRelation();
		addAttribute("TitoloColonna", TITOLO_COLONNA);
		addAttribute("ValoreDefault", VALORE_DEFAULT);
		addAttribute("Escludi", ESCLUDI);
		addAttribute("IdAzienda", ID_AZIENDA);
		addAttribute("IdFornitore", ID_FORNITORE);
		addAttribute("ClassHdr", CLASS_HDR);
		addAttribute("ClassAd", CLASS_AD);

		addComponent("DatiComuniEstesi", DatiComuniEstesiTTM.class);
		setKeys(ID_AZIENDA + "," + ID_FORNITORE + "," + CLASS_HDR + "," + TITOLO_COLONNA);

		setTimestampColumn("TIMESTAMP_AGG");
		((it.thera.thip.cs.DatiComuniEstesiTTM) getTransientTableManager("DatiComuniEstesi")).setExcludedColums();
	}

	/**
	 * init
	 * 
	 * @throws SQLException
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	private void init() throws SQLException {
		configure(TITOLO_COLONNA + ", " + VALORE_DEFAULT + ", " + ESCLUDI + ", " + ID_AZIENDA + ", " + ID_FORNITORE
				+ ", " + CLASS_HDR + ", " + CLASS_AD + ", " + STATO + ", " + R_UTENTE_CRZ + ", " + TIMESTAMP_CRZ + ", "
				+ R_UTENTE_AGG + ", " + TIMESTAMP_AGG);
	}

}
