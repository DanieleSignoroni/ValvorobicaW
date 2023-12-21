/*
 * @(#)YClassAdImpMisurePO.java
 */

/**
 * null
 *
 * <br></br><b>Copyright (C) : Thera SpA</b>
 * @author Wizard 21/12/2023 at 11:59:01
 */
/*
 * Revisions:
 * Date          Owner      Description
 * 21/12/2023    Wizard     Codice generato da Wizard
 *
 */
package it.valvorobica.thip.base.importazione;

import java.sql.SQLException;
import java.util.Vector;

import com.thera.thermfw.common.BaseComponentsCollection;
import com.thera.thermfw.common.BusinessObject;
import com.thera.thermfw.common.Deletable;
import com.thera.thermfw.persist.Child;
import com.thera.thermfw.persist.CopyException;
import com.thera.thermfw.persist.Copyable;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.persist.Proxy;
import com.thera.thermfw.persist.TableManager;
import com.thera.thermfw.security.Authorizable;
import com.thera.thermfw.security.Conflictable;
import com.thera.thermfw.util.PersistentClassAd;

import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.cs.EntitaAzienda;

public abstract class YClassAdImpMisurePO extends EntitaAzienda
		implements BusinessObject, Authorizable, Deletable, Child, Conflictable {

	/**
	 * instance
	 */
	private static YClassAdImpMisure cInstance;

	/**
	 * Attributo iTitoloColonna
	 */
	protected String iTitoloColonna;

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
	 * Attributo iClassad
	 */
	protected Proxy iClassad = new Proxy(com.thera.thermfw.util.PersistentClassAd.class);

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
			cInstance = (YClassAdImpMisure) Factory.createObject(YClassAdImpMisure.class);
		return PersistentObject.retrieveList(cInstance, where, orderBy, optimistic);
	}

	/**
	 * elementWithKey
	 * 
	 * @param key
	 * @param lockType
	 * @return YClassAdImpMisure
	 * @throws SQLException
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 CodeGen Codice generato da
	 * CodeGenerator
	 *
	 */
	public static YClassAdImpMisure elementWithKey(String key, int lockType) throws SQLException {
		return (YClassAdImpMisure) PersistentObject.elementWithKey(YClassAdImpMisure.class, key, lockType);
	}

	/**
	 * YClassAdImpMisurePO
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public YClassAdImpMisurePO() {
		setEscludi(false);
		setIdAzienda(Azienda.getAziendaCorrente());
	}

	/**
	 * Valorizza l'attributo.
	 * 
	 * @param titoloColonna
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public void setTitoloColonna(String titoloColonna) {
		this.iTitoloColonna = titoloColonna;
		setDirty();
		setOnDB(false);
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
	public String getTitoloColonna() {
		return iTitoloColonna;
	}

	/**
	 * Valorizza l'attributo.
	 * 
	 * @param valoreDefault
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public void setValoreDefault(String valoreDefault) {
		this.iValoreDefault = valoreDefault;
		setDirty();
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
	public String getValoreDefault() {
		return iValoreDefault;
	}

	/**
	 * Valorizza l'attributo.
	 * 
	 * @param escludi
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public void setEscludi(boolean escludi) {
		this.iEscludi = escludi;
		setDirty();
	}

	/**
	 * Restituisce l'attributo.
	 * 
	 * @return boolean
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public boolean getEscludi() {
		return iEscludi;
	}

	/**
	 * Valorizza l'attributo.
	 * 
	 * @param parent
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public void setParent(YHdrImpMisure parent) {
		String idAzienda = getIdAzienda();
		if (parent != null) {
			idAzienda = KeyHelper.getTokenObjectKey(parent.getKey(), 1);
		}
		setIdAziendaInternal(idAzienda);
		String classHdr = getClassHdr();
		if (parent != null) {
			classHdr = KeyHelper.getTokenObjectKey(parent.getKey(), 3);
		}
		setClassHdrInternal(classHdr);
		this.iParent.setObject(parent);
		setDirty();
		setOnDB(false);
	}

	/**
	 * Restituisce l'attributo.
	 * 
	 * @return YHdrImpMisure
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public YHdrImpMisure getParent() {
		return (YHdrImpMisure) iParent.getObject();
	}

	/**
	 * setParentKey
	 * 
	 * @param key
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public void setParentKey(String key) {
		iParent.setKey(key);
		String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
		setIdAziendaInternal(idAzienda);
		String classHdr = KeyHelper.getTokenObjectKey(key, 3);
		setClassHdrInternal(classHdr);
		setDirty();
		setOnDB(false);
	}

	/**
	 * getParentKey
	 * 
	 * @return String
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public String getParentKey() {
		return iParent.getKey();
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
		String key = iParent.getKey();
		iParent.setKey(KeyHelper.replaceTokenObjectKey(key, 2, idFornitore));
		setDirty();
		setOnDB(false);
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
		String key = iParent.getKey();
		String objIdFornitore = KeyHelper.getTokenObjectKey(key, 2);
		return objIdFornitore;

	}

	/**
	 * Valorizza l'attributo.
	 * 
	 * @param classad
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public void setClassad(PersistentClassAd classad) {
		String oldObjectKey = getKey();
		String classHdr = getClassHdr();
		if (classad != null) {
			classHdr = KeyHelper.getTokenObjectKey(classad.getKey(), 1);
		}
		setClassHdrInternal(classHdr);
		this.iClassad.setObject(classad);
		setDirty();
		if (!KeyHelper.areEqual(oldObjectKey, getKey())) {
			setOnDB(false);
		}
	}

	/**
	 * Restituisce l'attributo.
	 * 
	 * @return PersistentClassAd
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public PersistentClassAd getClassad() {
		return (PersistentClassAd) iClassad.getObject();
	}

	/**
	 * setClassadKey
	 * 
	 * @param key
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public void setClassadKey(String key) {
		String oldObjectKey = getKey();
		iClassad.setKey(key);
		String classHdr = KeyHelper.getTokenObjectKey(key, 1);
		setClassHdrInternal(classHdr);
		setDirty();
		if (!KeyHelper.areEqual(oldObjectKey, getKey())) {
			setOnDB(false);
		}
	}

	/**
	 * getClassadKey
	 * 
	 * @return String
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public String getClassadKey() {
		return iClassad.getKey();
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
		setClassHdrInternal(classHdr);
		setDirty();
		setOnDB(false);
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
		String key = iParent.getKey();
		String objClassHdr = KeyHelper.getTokenObjectKey(key, 3);
		return objClassHdr;
	}

	/**
	 * Valorizza l'attributo.
	 * 
	 * @param classAd
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public void setClassAd(String classAd) {
		String key = iClassad.getKey();
		iClassad.setKey(KeyHelper.replaceTokenObjectKey(key, 2, classAd));
		setDirty();
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
	public String getClassAd() {
		String key = iClassad.getKey();
		String objClassAd = KeyHelper.getTokenObjectKey(key, 2);
		return objClassAd;
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
		YClassAdImpMisurePO yClassAdImpMisurePO = (YClassAdImpMisurePO) obj;
		iParent.setEqual(yClassAdImpMisurePO.iParent);
		iClassad.setEqual(yClassAdImpMisurePO.iClassad);
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
		setTitoloColonna(KeyHelper.getTokenObjectKey(key, 4));
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
		String titoloColonna = getTitoloColonna();
		Object[] keyParts = { idAzienda, idFornitore, classHdr, titoloColonna };
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
	 * getFatherKey
	 * 
	 * @return String
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public String getFatherKey() {
		return getParentKey();
	}

	/**
	 * setFatherKey
	 * 
	 * @param key
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public void setFatherKey(String key) {
		setParentKey(key);
	}

	/**
	 * setFather
	 * 
	 * @param father
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public void setFather(PersistentObject father) {
		iParent.setObject(father);
	}

	/**
	 * getOrderByClause
	 * 
	 * @return String
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	public String getOrderByClause() {
		return "";
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
		return YClassAdImpMisureTM.getInstance();
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
		String key2 = iParent.getKey();
		iParent.setKey(KeyHelper.replaceTokenObjectKey(key2, 1, idAzienda));
	}

	/**
	 * setClassHdrInternal
	 * 
	 * @param classHdr
	 */
	/*
	 * Revisions: Date Owner Description 21/12/2023 Wizard Codice generato da Wizard
	 *
	 */
	protected void setClassHdrInternal(String classHdr) {
		String key1 = iParent.getKey();
		iParent.setKey(KeyHelper.replaceTokenObjectKey(key1, 3, classHdr));
		String key2 = iClassad.getKey();
		iClassad.setKey(KeyHelper.replaceTokenObjectKey(key2, 1, classHdr));
	}

}
