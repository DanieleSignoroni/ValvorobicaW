package it.valvorobica.thip.base.portal;

import java.sql.SQLException;
import java.util.Vector;

import com.thera.thermfw.common.BaseComponentsCollection;
import com.thera.thermfw.common.BusinessObject;
import com.thera.thermfw.common.Deletable;
import com.thera.thermfw.persist.CopyException;
import com.thera.thermfw.persist.Copyable;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.persist.TableManager;
import com.thera.thermfw.security.Authorizable;
import com.thera.thermfw.security.Conflictable;

import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.cs.EntitaAzienda;

public abstract class YImgCatalogoPortalePO extends EntitaAzienda
		implements BusinessObject, Authorizable, Deletable, Conflictable {

	private static YImgCatalogoPortale cInstance;

	protected String iTipoClassificazione;

	protected String iIdClassificazione;

	protected String iUrlImg;

	protected Integer iColonne;

	@SuppressWarnings("rawtypes")
	public static Vector retrieveList(String where, String orderBy, boolean optimistic)
			throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (cInstance == null)
			cInstance = (YImgCatalogoPortale) Factory.createObject(YImgCatalogoPortale.class);
		return PersistentObject.retrieveList(cInstance, where, orderBy, optimistic);
	}

	public static YImgCatalogoPortale elementWithKey(String key, int lockType) throws SQLException {
		return (YImgCatalogoPortale) PersistentObject.elementWithKey(YImgCatalogoPortale.class, key, lockType);
	}

	public YImgCatalogoPortalePO() {
		setIdAzienda(Azienda.getAziendaCorrente());
	}

	public void setTipoClassificazione(String tipoClassificazione) {
		this.iTipoClassificazione = tipoClassificazione;
		setDirty();
		setOnDB(false);
	}

	public String getTipoClassificazione() {
		return iTipoClassificazione;
	}

	public void setIdClassificazione(String idClassificazione) {
		this.iIdClassificazione = idClassificazione;
		setDirty();
		setOnDB(false);
	}

	public String getIdClassificazione() {
		return iIdClassificazione;
	}

	public void setUrlImg(String urlImg) {
		this.iUrlImg = urlImg;
		setDirty();
	}

	public String getUrlImg() {
		return iUrlImg;
	}

	public Integer getColonne() {
		return iColonne;
	}

	public void setColonne(Integer iColonne) {
		this.iColonne = iColonne;
	}

	public void setIdAzienda(String idAzienda) {
		iAzienda.setKey(idAzienda);
		setDirty();
		setOnDB(false);
	}

	public String getIdAzienda() {
		String key = iAzienda.getKey();
		return key;
	}

	public void setEqual(Copyable obj) throws CopyException {
		super.setEqual(obj);
	}

	@SuppressWarnings("rawtypes")
	public Vector checkAll(BaseComponentsCollection components) {
		Vector errors = new Vector();
		components.runAllChecks(errors);
		return errors;
	}

	public void setKey(String key) {
		setIdAzienda(KeyHelper.getTokenObjectKey(key, 1));
		setTipoClassificazione(KeyHelper.getTokenObjectKey(key, 2));
		setIdClassificazione(KeyHelper.getTokenObjectKey(key, 3));
	}

	public String getKey() {
		String idAzienda = getIdAzienda();
		String tipoClassificazione = getTipoClassificazione();
		String idClassificazione = getIdClassificazione();
		Object[] keyParts = { idAzienda, tipoClassificazione, idClassificazione };
		return KeyHelper.buildObjectKey(keyParts);
	}

	public boolean isDeletable() {
		return checkDelete() == null;
	}

	public String toString() {
		return getClass().getName() + " [" + KeyHelper.formatKeyString(getKey()) + "]";
	}

	protected TableManager getTableManager() throws SQLException {
		return YImgCatalogoPortaleTM.getInstance();
	}

}
