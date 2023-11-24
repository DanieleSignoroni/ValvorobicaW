package it.valvorobica.thip.magazzino.chiusure;

import java.sql.SQLException;

import com.thera.thermfw.base.SystemParam;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.TableManager;

import it.thera.thip.cs.DatiComuniEstesiTTM;
import it.thera.thip.cs.DescrizioneTTM;

/**
 * <h1>Softre Solutions</h1> <br>
 * 
 * @author Daniele Signoroni 24/11/2023 <br>
 *         <br>
 *         <b>71310 DSSOF3 24/11/2023</b>
 *         <p>
 *         Aggiunte colonne {@value #DATA_ULTIMA_CHIUSURA},
 *         {@value #ESITO_ULTIMA_CHIUSURA}
 *         </p>
 */

public class YCalendarioFiscaleTM extends TableManager {

	public static final String ID_AZIENDA = "ID_AZIENDA";

	public static final String ID_ANNO_FISCALE = "ID_ANNO_FISCALE";

	public static final String DESCRIZIONE = "DESCRIZIONE";

	public static final String DESCR_RIDOTTA = "DESCR_RIDOTTA";

	public static final String DATA_INIZIO = "DATA_INIZIO";

	public static final String DATA_FINE = "DATA_FINE";

	public static final String NUM_MESI_LIFO = "NUM_MESI_LIFO";

	public static final String ESEC_ATV_ANN_FSC = "ESEC_ATV_ANN_FSC";

	public static final String STATO_ANNO_FSC = "STATO_ANNO_FSC";

	public static final String STATO = "STATO";

	public static final String R_UTENTE_CRZ = "R_UTENTE_CRZ";

	public static final String R_UTENTE_AGG = "R_UTENTE_AGG";

	public static final String TIMESTAMP_CRZ = "TIMESTAMP_CRZ";

	public static final String TIMESTAMP_AGG = "TIMESTAMP_AGG";

	public static final String DATA_ULTIMA_CHIUSURA = "DATA_ULTIMA_CHIUSURA";

	public static final String ESITO_ULTIMA_CHIUSURA = "ESITO_ULTIMA_CHIUSURA";

	public static final String TABLE_NAME = SystemParam.getSchema("THIPPERS") + "YCALEN_FISCALE";

	private static final String CLASS_NAME = it.valvorobica.thip.magazzino.chiusure.YCalendarioFiscale.class.getName();

	private static TableManager cInstance;

	public synchronized static TableManager getInstance() throws SQLException {
		if (cInstance == null) {
			cInstance = (TableManager) Factory.createObject(YCalendarioFiscaleTM.class);
		}
		return cInstance;
	}

	public YCalendarioFiscaleTM() throws SQLException {
		super();
	}

	protected void initialize() throws SQLException {
		setTableName(TABLE_NAME);
		setObjClassName(CLASS_NAME);
		configure();
	}

	protected void initializeRelation() throws SQLException {
		super.initializeRelation();

		addAttribute("CodiceAzienda", ID_AZIENDA);
		addAttribute("CodiceAnnoFiscale", ID_ANNO_FISCALE);

		addAttribute("DataInizio", DATA_INIZIO);
		addAttribute("DataFine", DATA_FINE);
		addAttribute("NumMesiCostoMedioLIFO", NUM_MESI_LIFO);
		addAttribute("EsecAttivAnnoFiscale", ESEC_ATV_ANN_FSC);
		addAttribute("StatoAnnoFiscale", STATO_ANNO_FSC);
		addAttribute("DataUltimaChiusura", DATA_ULTIMA_CHIUSURA);
		addAttribute("StatoChiusuraMag", ESITO_ULTIMA_CHIUSURA);

		addComponent("Descrizione", DescrizioneTTM.class);
		addComponent("DatiComuniEstesi", DatiComuniEstesiTTM.class);

		setTimestampColumn(TIMESTAMP_AGG);
		((DatiComuniEstesiTTM) getTransientTableManager("DatiComuniEstesi")).setExcludedColums();

		setKeys(ID_AZIENDA + "," + ID_ANNO_FISCALE);
	}

}
