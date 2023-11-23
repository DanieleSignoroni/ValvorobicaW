package it.valvorobica.thip.base.portal;

import com.thera.thermfw.persist.*;
import java.sql.*;
import java.util.*;
import it.thera.thip.base.articolo.Articolo;
import it.thera.thip.base.cliente.ClienteVendita;

import java.math.*;

import it.thera.thip.cs.*;

import com.thera.thermfw.common.*;

import it.thera.thip.base.azienda.Azienda;

import com.thera.thermfw.security.*;

public abstract class YCarrelloPortalePO extends EntitaAzienda implements BusinessObject, Authorizable, Deletable, Conflictable {

	private static YCarrelloPortale cInstance;

	protected BigDecimal iQuantita;

	protected Integer iProgressivo;

	protected Proxy iArticolo = new Proxy(it.thera.thip.base.articolo.Articolo.class);

	protected Proxy iCliente = new Proxy(it.thera.thip.base.cliente.ClienteVendita.class);

	protected Proxy iUtenteportale = new Proxy(it.valvorobica.thip.base.portal.YUserPortal.class);

	@SuppressWarnings("rawtypes")
	public static Vector retrieveList(String where, String orderBy, boolean optimistic) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (cInstance == null)
			cInstance = (YCarrelloPortale)Factory.createObject(YCarrelloPortale.class);
		return PersistentObject.retrieveList(cInstance, where, orderBy, optimistic);
	}

	public static YCarrelloPortale elementWithKey(String key, int lockType) throws SQLException {
		return (YCarrelloPortale)PersistentObject.elementWithKey(YCarrelloPortale.class, key, lockType);
	}

	public YCarrelloPortalePO() {
		setProgressivo(new Integer(0));
		setIdAzienda(Azienda.getAziendaCorrente());
	}

	public void setQuantita(BigDecimal quantita) {
		this.iQuantita = quantita;
		setDirty();
	}

	public BigDecimal getQuantita() {
		return iQuantita;
	}

	public void setProgressivo(Integer progressivo) {
		this.iProgressivo = progressivo;
		setDirty();
		setOnDB(false);
	}

	public Integer getProgressivo() {
		return iProgressivo;
	}

	public void setArticolo(Articolo articolo) {
		String oldObjectKey = getKey();
		String idAzienda = getIdAzienda();
		if (articolo != null) {
			idAzienda = KeyHelper.getTokenObjectKey(articolo.getKey(), 1);
		}
		setIdAziendaInternal(idAzienda);
		this.iArticolo.setObject(articolo);
		setDirty();
		if (!KeyHelper.areEqual(oldObjectKey, getKey())) {
			setOnDB(false);
		}
	}

	public Articolo getArticolo() {
		return (Articolo)iArticolo.getObject();
	}

	public void setArticoloKey(String key) {
		String oldObjectKey = getKey();
		iArticolo.setKey(key);
		String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
		setIdAziendaInternal(idAzienda);
		setDirty();
		if (!KeyHelper.areEqual(oldObjectKey, getKey())) {
			setOnDB(false);
		}
	}

	public String getArticoloKey() {
		return iArticolo.getKey();
	}

	public void setRArticolo(String rArticolo) {
		String key = iArticolo.getKey();
		iArticolo.setKey(KeyHelper.replaceTokenObjectKey(key , 2, rArticolo));
		setDirty();
	}

	public String getRArticolo() {
		String key = iArticolo.getKey();
		String objRArticolo = KeyHelper.getTokenObjectKey(key,2);
		return objRArticolo;
	}

	public void setCliente(ClienteVendita cliente) {
		String oldObjectKey = getKey();
		String idAzienda = getIdAzienda();
		if (cliente != null) {
			idAzienda = KeyHelper.getTokenObjectKey(cliente.getKey(), 1);
		}
		setIdAziendaInternal(idAzienda);
		this.iCliente.setObject(cliente);
		setDirty();
		if (!KeyHelper.areEqual(oldObjectKey, getKey())) {
			setOnDB(false);
		}
	}

	public ClienteVendita getCliente() {
		return (ClienteVendita)iCliente.getObject();
	}

	public void setClienteKey(String key) {
		String oldObjectKey = getKey();
		iCliente.setKey(key);
		String idAzienda = KeyHelper.getTokenObjectKey(key, 1);
		setIdAziendaInternal(idAzienda);
		setDirty();
		if (!KeyHelper.areEqual(oldObjectKey, getKey())) {
			setOnDB(false);
		}
	}

	public String getClienteKey() {
		return iCliente.getKey();
	}

	public void setRCliente(String rCliente) {
		String key = iCliente.getKey();
		iCliente.setKey(KeyHelper.replaceTokenObjectKey(key , 2, rCliente));
		setDirty();
	}

	public String getRCliente() {
		String key = iCliente.getKey();
		String objRCliente = KeyHelper.getTokenObjectKey(key,2);
		return objRCliente;
	}

	public void setIdAzienda(String idAzienda) {
		setIdAziendaInternal(idAzienda);
		setDirty();
		setOnDB(false);
	}

	public String getIdAzienda() {
		String key = iArticolo.getKey();
		String objIdAzienda = KeyHelper.getTokenObjectKey(key,1);
		return objIdAzienda;

	}

	public void setUtenteportale(YUserPortal utenteportale) {
		this.iUtenteportale.setObject(utenteportale);
		setDirty();
		setOnDB(false);
	}

	public YUserPortal getUtenteportale() {
		return (YUserPortal)iUtenteportale.getObject();
	}

	public void setUtenteportaleKey(String key) {
		iUtenteportale.setKey(key);
		setDirty();
		setOnDB(false);
	}

	public String getUtenteportaleKey() {
		return iUtenteportale.getKey();
	}

	public void setRUtentePortale(String rUtentePortale) {
		iUtenteportale.setKey(rUtentePortale);
		setDirty();
		setOnDB(false);
	}

	public String getRUtentePortale() {
		String key = iUtenteportale.getKey();
		return key;
	}

	public void setEqual(Copyable obj) throws CopyException {
		super.setEqual(obj);
		YCarrelloPortalePO yCarrelloPortalePO = (YCarrelloPortalePO)obj;
		iArticolo.setEqual(yCarrelloPortalePO.iArticolo);
		iCliente.setEqual(yCarrelloPortalePO.iCliente);
		iUtenteportale.setEqual(yCarrelloPortalePO.iUtenteportale);
	}

	@SuppressWarnings("rawtypes")
	public Vector checkAll(BaseComponentsCollection components) {
		Vector errors = new Vector();
		if (!isOnDB()) {
			setProgressivo(new Integer(0));
		}
		components.runAllChecks(errors);
		return errors;
	}

	public void setKey(String key) {
		setIdAzienda(KeyHelper.getTokenObjectKey(key, 1));
		setRUtentePortale(KeyHelper.getTokenObjectKey(key, 2));
		setProgressivo(KeyHelper.stringToIntegerObj(KeyHelper.getTokenObjectKey(key, 3)));
	}

	public String getKey() {
		String idAzienda = getIdAzienda();
		String rUtentePortale = getRUtentePortale();
		Integer progressivo = getProgressivo();
		Object[] keyParts = {idAzienda, rUtentePortale, progressivo};
		return KeyHelper.buildObjectKey(keyParts);
	}

	public boolean isDeletable() {
		return checkDelete() == null;
	}

	public String toString() {
		return getClass().getName() + " [" + KeyHelper.formatKeyString(getKey()) + "]";
	}

	protected TableManager getTableManager() throws SQLException {
		return YCarrelloPortaleTM.getInstance();
	}

	protected void setIdAziendaInternal(String idAzienda) {
		String key1 = iArticolo.getKey();
		iArticolo.setKey(KeyHelper.replaceTokenObjectKey(key1, 1, idAzienda));
		String key2 = iCliente.getKey();
		iCliente.setKey(KeyHelper.replaceTokenObjectKey(key2, 1, idAzienda));
		iAzienda.setKey(idAzienda);
	}

}

