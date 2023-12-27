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
 * <b>71366	DSSOF3	22/12/2023</b>    <p>Descrittore di importazione</p>
 */

public class YHdrImpMisureTM extends TableManager {

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
	 * Attributo ID_FORNITORE
	 */
	public static final String ID_FORNITORE = "ID_FORNITORE";

	/**
	 * Attributo CLASS_HDR
	 */
	public static final String CLASS_HDR = "CLASS_HDR";

	/**
	 * TABLE_NAME
	 */
	public static final String TABLE_NAME = SystemParam.getSchema("THIPPERS") + "YHDR_IMP_MISURE";

	/**
	 * instance
	 */
	private static TableManager cInstance;

	/**
	 * CLASS_NAME
	 */
	private static final String CLASS_NAME = it.valvorobica.thip.base.importazione.YHdrImpMisure.class.getName();

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
			cInstance = (TableManager) Factory.createObject(YHdrImpMisureTM.class);
		}
		return cInstance;
	}

	/**
	 * YHdrImpMisureTM
	 * 
	 * @throws SQLException
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 CodeGen Codice generato da
	 * CodeGenerator
	 *
	 */
	public YHdrImpMisureTM() throws SQLException {
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
		addAttribute("IdAzienda", ID_AZIENDA);
		addAttribute("IdFornitore", ID_FORNITORE);
		addAttribute("ClassHdr", CLASS_HDR);

		addComponent("DatiComuniEstesi", DatiComuniEstesiTTM.class);
		setKeys(ID_AZIENDA + "," + ID_FORNITORE + "," + CLASS_HDR);

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
		configure(ID_AZIENDA + ", " + ID_FORNITORE + ", " + CLASS_HDR + ", " + STATO + ", " + R_UTENTE_CRZ + ", "
				+ TIMESTAMP_CRZ + ", " + R_UTENTE_AGG + ", " + TIMESTAMP_AGG);
	}

}
