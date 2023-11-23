package it.valvorobica.thip.base.portal;

import com.thera.thermfw.persist.*;

import java.sql.*;
import com.thera.thermfw.base.*;
import it.thera.thip.cs.*;

public class YCarrelloPortaleTM extends TableManager {

	public static final String ID_AZIENDA = "ID_AZIENDA";

	public static final String R_UTENTE_PORTALE = "R_UTENTE_PORTALE";

	public static final String R_ARTICOLO = "R_ARTICOLO";

	public static final String QUANTITA = "QUANTITA";

	public static final String R_CLIENTE = "R_CLIENTE";

	public static final String STATO = "STATO";

	public static final String R_UTENTE_CRZ = "R_UTENTE_CRZ";

	public static final String TIMESTAMP_CRZ = "TIMESTAMP_CRZ";

	public static final String R_UTENTE_AGG = "R_UTENTE_AGG";

	public static final String TIMESTAMP_AGG = "TIMESTAMP_AGG";

	public static final String PROGRESSIVO = "PROGRESSIVO";

	public static final String TABLE_NAME = SystemParam.getSchema("THIPPERS") + "YCARRELLO_PORTALE";

	private static TableManager cInstance;

	private static final String CLASS_NAME = it.valvorobica.thip.base.portal.YCarrelloPortale.class.getName();

	public synchronized static TableManager getInstance() throws SQLException {
		if (cInstance == null) {
			cInstance = (TableManager)Factory.createObject(YCarrelloPortaleTM.class);
		}
		return cInstance;
	}

	public YCarrelloPortaleTM() throws SQLException {
		super();
	}

	protected void initialize() throws SQLException {
		setTableName(TABLE_NAME);
		setObjClassName(CLASS_NAME);
		init();
	}

	protected void initializeRelation() throws SQLException {
		super.initializeRelation();
		addAttribute("Quantita", QUANTITA);
		addAttribute("Progressivo", PROGRESSIVO, "getIntegerObject");
		addAttribute("IdAzienda", ID_AZIENDA);
		addAttribute("RArticolo", R_ARTICOLO);
		addAttribute("RCliente", R_CLIENTE);
		addAttribute("RUtentePortale", R_UTENTE_PORTALE);

		addComponent("DatiComuniEstesi", DatiComuniEstesiTTM.class);
		setKeys(ID_AZIENDA + "," + R_UTENTE_PORTALE + "," + PROGRESSIVO);

		setTimestampColumn("TIMESTAMP_AGG");
		((it.thera.thip.cs.DatiComuniEstesiTTM)getTransientTableManager("DatiComuniEstesi")).setExcludedColums();
	}

	private void init() throws SQLException {
		configure(QUANTITA + ", " + PROGRESSIVO + ", " + ID_AZIENDA + ", " + R_ARTICOLO
				+ ", " + R_CLIENTE + ", " + R_UTENTE_PORTALE + ", " + STATO + ", " + R_UTENTE_CRZ
				+ ", " + TIMESTAMP_CRZ + ", " + R_UTENTE_AGG + ", " + TIMESTAMP_AGG);
	}

}

