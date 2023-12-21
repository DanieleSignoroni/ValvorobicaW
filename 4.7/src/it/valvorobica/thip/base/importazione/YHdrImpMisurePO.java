package it.valvorobica.thip.base.importazione;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import com.thera.thermfw.common.BaseComponentsCollection;
import com.thera.thermfw.common.BusinessObject;
import com.thera.thermfw.common.Deletable;
import com.thera.thermfw.persist.CopyException;
import com.thera.thermfw.persist.Copyable;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.OneToMany;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.persist.Proxy;
import com.thera.thermfw.persist.TableManager;
import com.thera.thermfw.security.Authorizable;
import com.thera.thermfw.security.Conflictable;
import com.thera.thermfw.util.PersistentClassHdr;

import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.base.fornitore.FornitoreAcquisto;
import it.thera.thip.cs.EntitaAzienda;

public abstract class YHdrImpMisurePO extends EntitaAzienda
		implements BusinessObject, Authorizable, Deletable, Conflictable {

	/**
	 * instance
	 */
	private static YHdrImpMisure cInstance;

	/**
	 * Attributo iFornitore
	 */
	protected Proxy iFornitore = new Proxy(it.thera.thip.base.fornitore.FornitoreAcquisto.class);

	/**
	 * Attributo iDescrittore
	 */
	protected Proxy iDescrittore = new Proxy(com.thera.thermfw.util.PersistentClassHdr.class);

	/**
	 * Attributo iYClassAdImpMisure
	 */
	protected OneToMany iYClassAdImpMisure = new OneToMany(
			it.valvorobica.thip.base.importazione.YClassAdImpMisure.class, this, 7, false);

	/**
	 * retrieveList
	 * 
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
	 * Revisions: Date Owner Description 21/12/2023 CodeGen Codice generato da
	 * CodeGenerator
	 *
	 */
	@SuppressWarnings("rawtypes")
	public static Vector retrieveList(String where, String orderBy, boolean optimistic)
			throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (cInstance == null)
			cInstance = (YHdrImpMisure) Factory.createObject(YHdrImpMisure.class);
		return PersistentObject.retrieveList(cInstance, where, orderBy, optimistic);
	}

	/**
	 * elementWithKey
	 * 
	 * @param key
	 * @param lockType
	 * @return YHdrImpMisure
	 * @throws SQLException
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 CodeGen Codice generato da
	 * CodeGenerator
	 *
	 */
	public static YHdrImpMisure elementWithKey(String key, int lockType) throws SQLException {
		return (YHdrImpMisure) PersistentObject.elementWithKey(YHdrImpMisure.class, key, lockType);
	}

	/**
	 * YHdrImpMisurePO
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public YHdrImpMisurePO() {
		setIdAzienda(Azienda.getAziendaCorrente());
		setClassHdr("YMisure");
	}

	/**
	 * Valorizza l'attributo.
	 * 
	 * @param fornitore
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public void setFornitore(FornitoreAcquisto fornitore) {
		String idAzienda = getIdAzienda();
		if (fornitore != null) {
			idAzienda = KeyHelper.getTokenObjectKey(fornitore.getKey(), 1);
		}
		setIdAziendaInternal(idAzienda);
		this.iFornitore.setObject(fornitore);
		setDirty();
		setOnDB(false);
		iYClassAdImpMisure.setFatherKeyChanged();
	}

	/**
	 * Restituisce l'attributo.
	 * 
	 * @return FornitoreAcquisto
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public FornitoreAcquisto getFornitore() {
		return (FornitoreAcquisto) iFornitore.getObject();
	}

	/**
	 * setFornitoreKey
	 * 
	 * @param key
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public void setFornitoreKey(String key) {
		iFornitore.setKey(key);
		String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
		setIdAziendaInternal(idAzienda);
		setDirty();
		setOnDB(false);
		iYClassAdImpMisure.setFatherKeyChanged();
	}

	/**
	 * getFornitoreKey
	 * 
	 * @return String
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public String getFornitoreKey() {
		return iFornitore.getKey();
	}

	/**
	 * Valorizza l'attributo.
	 * 
	 * @param idAzienda
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public void setIdAzienda(String idAzienda) {
		setIdAziendaInternal(idAzienda);
		setDirty();
		setOnDB(false);
		iYClassAdImpMisure.setFatherKeyChanged();
	}

	/**
	 * Restituisce l'attributo.
	 * 
	 * @return String
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public String getIdAzienda() {
		String key = iAzienda.getKey();
		return key;
	}

	/**
	 * Valorizza l'attributo.
	 * 
	 * @param idFornitore
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public void setIdFornitore(String idFornitore) {
		String key = iFornitore.getKey();
		iFornitore.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idFornitore));
		setDirty();
		setOnDB(false);
		iYClassAdImpMisure.setFatherKeyChanged();
	}

	/**
	 * Restituisce l'attributo.
	 * 
	 * @return String
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public String getIdFornitore() {
		String key = iFornitore.getKey();
		String objIdFornitore = KeyHelper.getTokenObjectKey(key, 2);
		return objIdFornitore;
	}

	/**
	 * Valorizza l'attributo.
	 * 
	 * @param descrittore
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public void setDescrittore(PersistentClassHdr descrittore) {
		this.iDescrittore.setObject(descrittore);
		setDirty();
		setOnDB(false);
		iYClassAdImpMisure.setFatherKeyChanged();
	}

	/**
	 * Restituisce l'attributo.
	 * 
	 * @return PersistentClassHdr
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public PersistentClassHdr getDescrittore() {
		return (PersistentClassHdr) iDescrittore.getObject();
	}

	/**
	 * setDescrittoreKey
	 * 
	 * @param key
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public void setDescrittoreKey(String key) {
		iDescrittore.setKey(key);
		setDirty();
		setOnDB(false);
		iYClassAdImpMisure.setFatherKeyChanged();
	}

	/**
	 * getDescrittoreKey
	 * 
	 * @return String
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public String getDescrittoreKey() {
		return iDescrittore.getKey();
	}

	/**
	 * Valorizza l'attributo.
	 * 
	 * @param classHdr
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public void setClassHdr(String classHdr) {
		iDescrittore.setKey(classHdr);
		setDirty();
		setOnDB(false);
		iYClassAdImpMisure.setFatherKeyChanged();
	}

	/**
	 * Restituisce l'attributo.
	 * 
	 * @return String
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public String getClassHdr() {
		String key = iDescrittore.getKey();
		return key;
	}

	/**
	 * getYClassAdImpMisure
	 * 
	 * @return List
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	@SuppressWarnings("rawtypes")
	public List getYClassAdImpMisure() {
		return getYClassAdImpMisureInternal();
	}

	/**
	 * setEqual
	 * 
	 * @param obj
	 * @throws CopyException
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public void setEqual(Copyable obj) throws CopyException {
		super.setEqual(obj);
		YHdrImpMisurePO yHdrImpMisurePO = (YHdrImpMisurePO) obj;
		iFornitore.setEqual(yHdrImpMisurePO.iFornitore);
		iDescrittore.setEqual(yHdrImpMisurePO.iDescrittore);
		iYClassAdImpMisure.setEqual(yHdrImpMisurePO.iYClassAdImpMisure);
	}

	/**
	 * checkAll
	 * 
	 * @param components
	 * @return Vector
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	@SuppressWarnings("rawtypes")
	public Vector checkAll(BaseComponentsCollection components) {
		Vector errors = new Vector();
		components.runAllChecks(errors);
		return errors;
	}

	/**
	 * setKey
	 * 
	 * @param key
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public void setKey(String key) {
		setIdAzienda(KeyHelper.getTokenObjectKey(key, 1));
		setIdFornitore(KeyHelper.getTokenObjectKey(key, 2));
		setClassHdr(KeyHelper.getTokenObjectKey(key, 3));
	}

	/**
	 * getKey
	 * 
	 * @return String
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public String getKey() {
		String idAzienda = getIdAzienda();
		String idFornitore = getIdFornitore();
		String classHdr = getClassHdr();
		Object[] keyParts = { idAzienda, idFornitore, classHdr };
		return KeyHelper.buildObjectKey(keyParts);
	}

	/**
	 * isDeletable
	 * 
	 * @return boolean
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public boolean isDeletable() {
		return checkDelete() == null;
	}

	/**
	 * saveOwnedObjects
	 * 
	 * @param rc
	 * @return int
	 * @throws SQLException
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public int saveOwnedObjects(int rc) throws SQLException {
		rc = iYClassAdImpMisure.save(rc);
		return rc;
	}

	/**
	 * deleteOwnedObjects
	 * 
	 * @return int
	 * @throws SQLException
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public int deleteOwnedObjects() throws SQLException {
		return getYClassAdImpMisureInternal().delete();
	}

	/**
	 * initializeOwnedObjects
	 * 
	 * @param result
	 * @return boolean
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public boolean initializeOwnedObjects(boolean result) {
		result = iYClassAdImpMisure.initialize(result);
		return result;
	}

	/**
	 * toString
	 * 
	 * @return String
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public String toString() {
		return getClass().getName() + " [" + KeyHelper.formatKeyString(getKey()) + "]";
	}

	/**
	 * getTableManager
	 * 
	 * @return TableManager
	 * @throws SQLException
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 CodeGen Codice generato da
	 * CodeGenerator
	 *
	 */
	protected TableManager getTableManager() throws SQLException {
		return YHdrImpMisureTM.getInstance();
	}

	/**
	 * getYClassAdImpMisureInternal
	 * 
	 * @return OneToMany
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	protected OneToMany getYClassAdImpMisureInternal() {
		if (iYClassAdImpMisure.isNew())
			iYClassAdImpMisure.retrieve();
		return iYClassAdImpMisure;
	}

	/**
	 * setIdAziendaInternal
	 * 
	 * @param idAzienda
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	protected void setIdAziendaInternal(String idAzienda) {
		iAzienda.setKey(idAzienda);
		String key2 = iFornitore.getKey();
		iFornitore.setKey(KeyHelper.replaceTokenObjectKey(key2, 1, idAzienda));
	}

}
