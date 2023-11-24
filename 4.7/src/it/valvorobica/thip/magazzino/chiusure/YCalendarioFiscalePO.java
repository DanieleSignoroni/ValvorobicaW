package it.valvorobica.thip.magazzino.chiusure;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.thera.thermfw.base.SystemParam;
import com.thera.thermfw.base.TimeUtils;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.common.BaseComponentsCollection;
import com.thera.thermfw.common.BusinessObject;
import com.thera.thermfw.common.Deletable;
import com.thera.thermfw.common.ErrorMessage;
import com.thera.thermfw.persist.CachedStatement;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.CopyException;
import com.thera.thermfw.persist.Copyable;
import com.thera.thermfw.persist.Database;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.OneToMany;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.persist.TableManager;
import com.thera.thermfw.security.Authorizable;

import it.thera.thip.cs.EntitaDescrAzienda;

/**
 * <h1>Softre Solutions</h1> <br>
 * 
 * @author Daniele Signoroni 24/11/2023 <br>
 *         <br>
 *         <b>71310 DSSOF3 24/11/2023</b>
 *         <p>
 *         Aggiunti attributi {@link #iDataUltimaChiusura},
 *         {@link #iStatoChiusuraMag}
 *         </p>
 */

public class YCalendarioFiscalePO extends EntitaDescrAzienda implements BusinessObject, Authorizable, Deletable {

	public static String TABLE_NLS_NAME = SystemParam.getSchema("THIPPERS") + "YCALEN_FISCALE_L";

	// Enum EsecAttivAnnoFiscale
	public static final char NESSUNA = '0';
	public static final char CHISURA = '1';
	public static final char REGRESSIONE = '2';

	// Enum StatoAnnoFiscale
	public static final char INCOMPLETO = 'I';
	public static final char SOSPESO = 'S';
	public static final char ATTIVO = 'A';

	private static final String SELECT_YEAR = "SELECT " + YCalendarioFiscaleTM.ID_ANNO_FISCALE + " FROM "
			+ YCalendarioFiscaleTM.TABLE_NAME + " WHERE " + YCalendarioFiscaleTM.ID_ANNO_FISCALE + " LIKE ? " + " AND "
			+ YCalendarioFiscaleTM.ID_AZIENDA + " = ? " + " ORDER BY " + YCalendarioFiscaleTM.ID_ANNO_FISCALE + " DESC";

	private static CachedStatement selectYear = new CachedStatement(SELECT_YEAR);

	// Federico Crosa fix 2597 aggiunte le due righe successive
	// Inizio 4609
	private static final String SELECT_REFERENZIATO = "SELECT NUM_REGISTRAZIONE FROM " + SystemParam.getSchema("THIP")
			+ "MOVIM_MAGAZ WHERE R_ANNO_FISCALE = ? AND R_AZIENDA = ? ";
	// Fine 4609
	private static CachedStatement selectReferenziato = new CachedStatement(SELECT_REFERENZIATO);

	private static YCalendarioFiscale cInstance;

	protected String iCodiceAnnoFiscale;

	protected Date iDataInizio;

	protected Date iDataFine;

	protected int iNumMesiCostoMedioLIFO = 0;

	protected char iEsecAttivAnnoFiscale = NESSUNA;

	protected char iStatoAnnoFiscale = INCOMPLETO;

	protected char iStatoChiusuraMag;

	protected Date iDataUltimaChiusura;

	public char getStatoChiusuraMag() {
		return iStatoChiusuraMag;
	}

	public void setStatoChiusuraMag(char iStatoChiusuraMag) {
		this.iStatoChiusuraMag = iStatoChiusuraMag;
		setDirty();
	}

	public Date getDataUltimaChiusura() {
		return iDataUltimaChiusura;
	}

	public void setDataUltimaChiusura(Date iDataUltimaChiusura) {
		this.iDataUltimaChiusura = iDataUltimaChiusura;
		setDirty();
	}

	protected char iVecchioStatoAnnoFiscale;

	protected OneToMany iPeriodi = new OneToMany(YPeriodoCalFiscale.class, this, 3, true);

	public void setCodiceAnnoFiscale(String codiceAnnoFiscale) {
		this.iCodiceAnnoFiscale = codiceAnnoFiscale;
		iDescrizione.getHandler().setFatherKeyChanged();
		iPeriodi.setFatherKeyChanged();
		setDirty();
		setOnDB(false);
	}

	/**
	 * Restituisce l'attributo.
	 * 
	 * @return String
	 */
	/*
	 * Revisions: Date Owner Description 15/10/2002 PJ Initial revision
	 *
	 */
	public String getCodiceAnnoFiscale() {
		return iCodiceAnnoFiscale;
	}

	/**
	 * Valorizza l'attributo.
	 * 
	 * @param dataInizio
	 */
	/*
	 * Revisions: Date Owner Description 15/10/2002 PJ Initial revision
	 *
	 */
	public void setDataInizio(Date dataInizio) {
		this.iDataInizio = dataInizio;
		setDirty();
	}

	/**
	 * Restituisce l'attributo.
	 * 
	 * @return Date
	 */
	/*
	 * Revisions: Date Owner Description 15/10/2002 PJ Initial revision
	 *
	 */
	public Date getDataInizio() {
		return iDataInizio;
	}

	/**
	 * Valorizza l'attributo.
	 * 
	 * @param dataFine
	 */
	/*
	 * Revisions: Date Owner Description 15/10/2002 PJ Initial revision
	 *
	 */
	public void setDataFine(Date dataFine) {
		this.iDataFine = dataFine;
		setDirty();
	}

	/**
	 * Restituisce l'attributo.
	 * 
	 * @return Date
	 */
	/*
	 * Revisions: Date Owner Description 15/10/2002 PJ Initial revision
	 *
	 */
	public Date getDataFine() {
		return iDataFine;
	}

	/**
	 * Valorizza l'attributo.
	 * 
	 * @param numMesiCostoMedioLIFO
	 */
	/*
	 * Revisions: Date Owner Description 15/10/2002 PJ Initial revision
	 *
	 */
	public void setNumMesiCostoMedioLIFO(int numMesi) {
		this.iNumMesiCostoMedioLIFO = numMesi;
		setDirty();
	}

	/**
	 * Restituisce l'attributo.
	 * 
	 * @return int
	 */
	/*
	 * Revisions: Date Owner Description 15/10/2002 PJ Initial revision
	 *
	 */
	public int getNumMesiCostoMedioLIFO() {
		return iNumMesiCostoMedioLIFO;
	}

	/**
	 * Valorizza l'attributo.
	 * 
	 * @param esecAttivAnnoFiscale
	 */
	/*
	 * Revisions: Date Owner Description 15/10/2002 PJ Initial revision
	 *
	 */
	public void setEsecAttivAnnoFiscale(char esecAttivAnnoFiscale) {
		this.iEsecAttivAnnoFiscale = esecAttivAnnoFiscale;
		setDirty();
	}

	/**
	 * Restituisce l'attributo.
	 * 
	 * @return int
	 */
	/*
	 * Revisions: Date Owner Description 15/10/2002 PJ Initial revision
	 *
	 */
	public char getEsecAttivAnnoFiscale() {
		return iEsecAttivAnnoFiscale;
	}

	/**
	 * Valorizza l'attributo.
	 * 
	 * @param statoAnnoFiscale
	 */
	/*
	 * Revisions: Date Owner Description 15/10/2002 PJ Initial revision
	 *
	 */
	public void setStatoAnnoFiscale(char statoAnnoFiscale) {
		this.iStatoAnnoFiscale = statoAnnoFiscale;
		setDirty();
	}

	/**
	 * Restituisce l'attributo.
	 * 
	 * @return int
	 */
	/*
	 * Revisions: Date Owner Description 15/10/2002 PJ Initial revision
	 *
	 */
	public char getStatoAnnoFiscale() {
		return iStatoAnnoFiscale;
	}

	/**
	 * Valorizza l'attributo.
	 * 
	 * @param codiceAzienda
	 */
	/*
	 * Revisions: Date Owner Description 15/10/2002 PJ Initial revision
	 *
	 */
	public void setCodiceAzienda(String codiceAzienda) {
		setIdAzienda(codiceAzienda);
		iPeriodi.setFatherKeyChanged();
	}

	/**
	 * Restituisce l'attributo.
	 * 
	 * @return String
	 */
	/*
	 * Revisions: Date Owner Description 15/10/2002 PJ Initial revision
	 *
	 */
	public String getCodiceAzienda() {
		String key = getIdAzienda();
		return key;
	}

	/**
	 * setEqual
	 * 
	 * @param obj
	 * @throws CopyException
	 */
	/*
	 * Revisions: Date Owner Description 15/10/2002 PJ Initial revision
	 *
	 */
	public void setEqual(Copyable obj) throws CopyException {
		super.setEqual(obj);
	}

	/**
	 * setKey
	 * 
	 * @param key
	 */
	/*
	 * Revisions: Date Owner Description 15/10/2002 PJ Initial revision
	 *
	 */
	public void setKey(String key) {
		String objCodiceAzienda = KeyHelper.getTokenObjectKey(key, 1);
		setCodiceAzienda(objCodiceAzienda);
		String objCodiceAnnoFiscale = KeyHelper.getTokenObjectKey(key, 2);
		setCodiceAnnoFiscale(objCodiceAnnoFiscale);
	}

	/**
	 * getKey
	 * 
	 * @return String
	 */
	/*
	 * Revisions: Date Owner Description 15/10/2002 PJ Initial revision
	 *
	 */
	public String getKey() {
		String codiceAzienda = getCodiceAzienda();
		String codiceAnnoFiscale = getCodiceAnnoFiscale();
		Object[] keyParts = { codiceAzienda, codiceAnnoFiscale };
		return KeyHelper.buildObjectKey(keyParts);
	}

	/**
	 * isDeletable
	 * 
	 * @return boolean
	 */
	/*
	 * Revisions: Date Owner Description 15/10/2002 PJ Initial revision
	 *
	 */
	public boolean isDeletable() {
		return checkDelete() == null;
	}

	/**
	 * checkDelete
	 * 
	 * @return ErrorMessage
	 */
	/*
	 * Revisions: Date Owner Description 15/10/2002 PJ Initial revision
	 *
	 */
	public ErrorMessage checkDelete() {
		return null;
	}

	public String getTableNLSName() {
		return TABLE_NLS_NAME;
	}

	@SuppressWarnings("rawtypes")
	public static Vector retrieveList(String where, String orderBy, boolean optimistic)
			throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (cInstance == null)
			cInstance = (YCalendarioFiscale) Factory.createObject(YCalendarioFiscale.class);
		return PersistentObject.retrieveList(cInstance, where, orderBy, optimistic);
	}

	public static YCalendarioFiscale elementWithKey(String key, int lockType) throws SQLException {
		return (YCalendarioFiscale) PersistentObject.elementWithKey(YCalendarioFiscale.class, key, lockType);
	}

	protected TableManager getTableManager() throws SQLException {
		return YCalendarioFiscaleTM.getInstance();
	}

	protected OneToMany getPeriodiInternal() {
		if (iPeriodi.isNew())
			iPeriodi.retrieve();
		return iPeriodi;
	}

	@SuppressWarnings("rawtypes")
	public List getPeriodi() {
		return getPeriodiInternal();
	}

	public boolean initializeOwnedObjects(boolean result) {
		setDeepRetrieveEnabled(true);

		// Federico Crosa fix 2597: aggiunta riga sottostante
		iVecchioStatoAnnoFiscale = iStatoAnnoFiscale;

		return iPeriodi.initialize(super.initializeOwnedObjects(result));
	}

	public int saveOwnedObjects(int returnCode) throws SQLException {
		// return getPeriodiInternal().save(super.saveOwnedObjects(returnCode));
		return iPeriodi.save(super.saveOwnedObjects(returnCode));
	}

	public int deleteOwnedObjects() throws SQLException {
		int ret = super.deleteOwnedObjects();
		if (ret < 0)
			return ret;
		return getPeriodiInternal().delete();
	}

	public YCalendarioFiscalePO() {
		super(3);
	}

	private String makeCodiceCalendario() throws SQLException {
		int anno = TimeUtils.getValues(getDataFine())[0];
		String codice = String.valueOf(anno);
		String codiceAttuale = getCodiceAnnoFiscale();
		if (isOnDB() && codiceAttuale.startsWith(codice)) {
			return codiceAttuale;
		}

		Database db = ConnectionManager.getCurrentDatabase();
		PreparedStatement smtmSelectYear = selectYear.getStatement();
		db.setString(smtmSelectYear, 1, codice + "%");
		db.setString(smtmSelectYear, 2, getCodiceAzienda());

		ResultSet rsYear = smtmSelectYear.executeQuery();

		if (rsYear.next()) {
			String codiceAnnoFiscale = rsYear.getString(1).trim();
			if (codiceAnnoFiscale.length() == 4) {
				codice = codice + "A";
			} else {
				char lastChar = codiceAnnoFiscale.charAt(4);
				lastChar++;
				codice = codice + lastChar;
			}
		}

		rsYear.close();
		smtmSelectYear.clearParameters();

		return codice;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void progressiviOneToManyPeriodi() {
		List tmpPeriodi = new ArrayList(iPeriodi);
		Collections.sort(tmpPeriodi);
		iPeriodi.reset();
		iPeriodi.setNew(false);
		iPeriodi.addAll(tmpPeriodi);
		int i = 1;
		Iterator it = iPeriodi.iterator();
		while (it.hasNext()) {
			YPeriodoCalFiscale pCal = (YPeriodoCalFiscale) it.next();
			pCal.setCodicePeriodo(new Integer(i));
			i++;
		}
	}

	// Federico Crosa fix 2597: metodo che esegue il preparedStatement per vedere se
	// il calendario in questione è referenziato
	private static synchronized ResultSet rsSelectReferenziato(String anno, String azienda) throws SQLException {
		Database db = ConnectionManager.getCurrentDatabase();
		PreparedStatement stmtSelectReferenziato = selectReferenziato.getStatement();
		db.setString(stmtSelectReferenziato, 1, anno);
		db.setString(stmtSelectReferenziato, 2, azienda); // 6878
		return stmtSelectReferenziato.executeQuery();
	}

	// Federico Crosa fix 2597: controlla il risultato del preparedStatement
	// presente nel metodo soprastante
	public boolean isReferenziato() {

		ResultSet rs = null;
		try {
			rs = rsSelectReferenziato(getCodiceAnnoFiscale(), getIdAzienda());
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace(Trace.excStream);
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
			}
		}
		return true;
	}

	// Federico Crosa fix 2597: creato nuovo metodo
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected int saveInternal() throws SQLException {
		String nuovoCodice = makeCodiceCalendario();
		boolean referenziato = isReferenziato();

		// se sono in modifica è possibile che sia necessario modificare
		// la chiave di padre e quindi dei figli
		if (isOnDB() && !referenziato) {
			// mi copio la onetomany dei periodi in una lista temporanea
			List tmpPeriodi = new ArrayList(getPeriodi());
			int retDelete = delete();
			if (retDelete < 0)
				return retDelete;

			// riempio la onetomany dei periodi dalla lista temporanea
			iPeriodi.addAll(tmpPeriodi);
		}

		if (!nuovoCodice.equals(getCodiceAnnoFiscale()) && !referenziato)
			setCodiceAnnoFiscale(nuovoCodice);

		// sistemo i progressivi della OneToMany dei periodi

		if (!referenziato)
			progressiviOneToManyPeriodi();
		return 0;
	}

	// Federico Crosa fix 2597: chiamato nuovo metodo
	public int save() throws SQLException {
		int ret = saveInternal();
		if (ret < 0)
			return ret;
		return super.save();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Vector checkAll(BaseComponentsCollection components) {
		return null;
	}

}
