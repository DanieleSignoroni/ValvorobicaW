package it.valvorobica.thip.base.generale.ws;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.thera.thermfw.base.TimeUtils;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.persist.CachedStatement;
import com.thera.thermfw.persist.ConnectionDescriptor;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.security.Security;
import com.thera.thermfw.web.SessionEnvironment;

import it.thera.thip.base.articolo.Articolo;
import it.thera.thip.base.cliente.ClienteVendita;
import it.thera.thip.base.ecommerce.Gwmso00f;
import it.thera.thip.base.ecommerce.Gwror00f;
import it.thera.thip.base.ecommerce.Gwtor00f;
import it.thera.thip.base.ecommerce.Gwtor00fTM;
import it.thera.thip.vendite.generaleVE.ws.RicercaPrezzoEcomm;
import it.thera.thip.ws.GenRequestJSON;
import it.valvorobica.thip.base.portal.YCarrelloPortale;

/**
 * <h1>Softre Solutions</h1>
 * <br>
 * @author Daniele Signoroni 31/08/2023
 * <br><br>
 * <b>XXXXX	DSSOF3	31/08/2023</b>	<p>Prima stesura.<br>
 * 										Check-out carrello.<br>
 * 										Passo dagli ordini ECommerce:
 * 										<ul>
 * 											<li>{@link Gwtor00f} --> testata</li>
 * 											<li>{@link Gwror00f} --> righe</li>
 * 										</ul>
 * 										Se la creazione di queste e' andata a buon fine, tolgo l'articolo dal carrello.
 * 									</p>
 */

public class YCheckOutCarrello extends GenRequestJSON{

	protected String idCliente;

	protected Gwmso00f flusso;

	public Gwmso00f getFlusso() {
		return flusso;
	}

	public void setFlusso(Gwmso00f flusso) {
		this.flusso = flusso;
	}

	public String getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(String idCliente) {
		this.idCliente = idCliente;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void applyParams(Map appParams) {
		super.applyParams(appParams);
		String codCli = (String)appParams.get("codCliente");
		if(codCli != null) {
			this.setIdCliente(codCli);
		}else {
			getErrors().add("codCliente inesistente [" + getCompany() + "/" + getIdCliente() + "]");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Map execute(Map m) {
		if(getErrors().isEmpty()) {
			ArrayList<String> errors = new ArrayList<String>();
			Gson gson = new Gson();
			Item[] items = gson.fromJson((String) this.getAppParams().get("items"), Item[].class);
			String errorQta = checkQuantita(items);
			if(errorQta != null) {
				errors.add(errorQta);
				m.put("errors", errors);
			}else {
				generaOrdineECommerce(m,items);
			}
		}
		return m;
	}

	@SuppressWarnings("rawtypes")
	protected void generaOrdineECommerce(Map m, Item[] items) {
		codificaFlusso();
		boolean isopen = false;		
		Object[] info = SessionEnvironment.getDBInfoFromIniFile();
		String dbName = (String)info[0];
		try {		
			if(!Security.isCurrentDatabaseSetted()) {
				Security.setCurrentDatabase(dbName, null);
			}
			Security.openDefaultConnection();
			isopen = true;
			ConnectionDescriptor cd = ConnectionManager.getCurrentConnectionDescriptor();
			ClienteVendita cliente = (ClienteVendita)
					ClienteVendita.elementWithKey(ClienteVendita.class, 
							KeyHelper.buildObjectKey(new String[] {
									this.getCompany(),
									this.getIdCliente()
							}), PersistentObject.NO_LOCK);
			Gwtor00f testata = generaTestataOrdineECommerce(cliente);
			boolean isAllOk = true;
			if(testata.save() >= 0) {
				if(items != null) {
					for (int i = 0; i < items.length; i++) {
						Item itemCheckOut = items[i];
						YCarrelloPortale item = (YCarrelloPortale)
								YCarrelloPortale.elementWithKey(YCarrelloPortale.class, itemCheckOut.getKey(), PersistentObject.NO_LOCK);
						Gwror00f riga = generaRigaOrdineECommerce(itemCheckOut,item,testata,cliente);
						riga.setRocarr(new BigDecimal(i));
						if(riga.save() < 0) {
							isAllOk = false;
						}else {
							if(item.delete() < 0) { //pulire il carrello da quelle gia estratte
								isAllOk = false;
							}
						}
					}
				}
				if(isAllOk) {
					cd.commit();
				}else {
					cd.rollback();
				}
			}
		}catch (Throwable t) {
			t.printStackTrace(Trace.excStream);
		}
		finally {
			if (isopen) {
				Security.closeDefaultConnection();
			}
		}
	}

	protected void codificaFlusso() {
		try {
			flusso = (Gwmso00f)
					Gwmso00f.elementWithKey(Gwmso00f.class, 
							KeyHelper.buildObjectKey(new String[] {
									this.getCompany(),
									"E-Commerce"
							}), PersistentObject.NO_LOCK);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Gwror00f generaRigaOrdineECommerce(Item itemCheckOut, YCarrelloPortale item, Gwtor00f testata,
			ClienteVendita cliente) throws SQLException {
		Articolo art = (Articolo)
				Articolo.elementWithKey(Articolo.class, KeyHelper.buildObjectKey(new String[] {
						this.getCompany(),
						item.getRArticolo()
				}), PersistentObject.NO_LOCK);
		Gwror00f riga = (Gwror00f) Factory.createObject(Gwror00f.class);
		riga.setTestata(testata);
		riga.setRoidaz(this.getCompany());
		riga.setRocarp(testata.getTocarp());
		riga.setRocard(testata.getTocard());
		riga.setIdArticolo(item.getRArticolo());
		riga.setRofoor(testata.getTofoor());
		riga.setRodear(""); //desc art
		riga.setRocafi(this.getFlusso().getCodCauVenRig()); //causale riga 
		riga.setRostat('1'); //stato
		riga.setRocumm(""); //um rif
		riga.setRocumv(""); // um ven
		riga.setRoumuv(new BigDecimal(1));
		riga.setRoopmv('M');
		riga.setRopzum('V');
		riga.setRotpre('1');
		riga.setRotmov(false);
		riga.setRoasfi(""); // assog iva
		riga.setRotpev('P');
		riga.setStatoErrImp('0');
		riga.setRomagw("001");
		BigDecimal qta = new BigDecimal(itemCheckOut.getQuantita());
		riga.setRoqtor(qta);
		riga.setRoqt2o(qta);
		riga.setIdUMPrm(art.getIdUMPrmMag());
		riga.setIdUMRif(art.getIdUMRiferimento());
		riga.setRoutcr(this.getUserId());
		riga.setRoutag(this.getUserId());
		Map parametriRcPrz = new HashMap();
		parametriRcPrz.put("codArticolo", item.getRArticolo());
		parametriRcPrz.put("codCliente", this.getIdCliente().trim());
		parametriRcPrz.put("tipoUMVendita", "V");
		parametriRcPrz.put("company", this.getCompany());
		parametriRcPrz.put("qtaRichiesta", itemCheckOut.getQuantita());
		parametriRcPrz.put("codListino", "VEN");
		RicercaPrezzoEcomm rcPrz = (RicercaPrezzoEcomm) Factory.createObject(RicercaPrezzoEcomm.class); //ricerco prezzo tramite ws std
		rcPrz.setUseAuthentication(false);
		rcPrz.setUseAuthorization(false);
		rcPrz.setUseLicence(false);
		rcPrz.setConnectionDescriptor(this.getConnectionDescriptor());
		rcPrz.setAppParams(parametriRcPrz);
		rcPrz.setQtaVenditaRich(qta);
		Map values = rcPrz.send();
		BigDecimal prezzo = BigDecimal.ZERO;
		if(values.get("prezzo") != null) {
			prezzo = (BigDecimal) values.get("prezzo");
		}
		riga.setRoprez(prezzo);
		riga.setRotpre('0');
		return riga;
	}

	protected Gwtor00f generaTestataOrdineECommerce(ClienteVendita cliente) {
		Gwtor00f testata = (Gwtor00f) Factory.createObject(Gwtor00f.class);
		testata.setToidaz(this.getCompany());
		testata.setTocarp(String.valueOf(getNextIntGwTor()));
		testata.setTocard(TimeUtils.getCurrentDate());
		testata.setCliente(cliente);
		testata.setIdAnagrafico(cliente.getIdAnagrafico());
		testata.setToclir(true); //e' un cliente codificato
		testata.setToutcr(this.getUserId());
		testata.setToutag(this.getUserId());
		testata.setTodaor(TimeUtils.getCurrentDate());
		testata.setTostdo('1');
		testata.setTofoor("E-Commerce");
		testata.setTocafi(this.getFlusso().getCodCauVenTes());
		return testata;
	}

	protected Integer getNextIntGwTor() {
		ResultSet rs = null;
		CachedStatement cs = null;
		try {	
			String stmt = " SELECT COUNT(*)+1 AS MAX FROM "+Gwtor00fTM.TABLE_NAME+" ";
			cs = new CachedStatement(stmt);
			rs = cs.executeQuery();
			if(rs.next()) {
				return rs.getInt(1);
			}
		}catch (SQLException t) {
			t.printStackTrace(Trace.excStream);
		}
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
			}catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;

	}

	protected String checkQuantita(Item[] items) {
		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			if(item.getQuantita() == null
					|| item.getQuantita().isEmpty()
					|| item.getQuantita().equals("")
					|| item.getQuantita().equals(" ")) {
				return "Sistemare le quantita'";
			}else {
				try {
					new BigDecimal(item.getQuantita());
				}catch (NumberFormatException e) {
					e.printStackTrace();				
					return "Formato quantita errato";
				}
			}
		}
		return null;
	}

	public class Item{

		protected int id;

		protected String key;

		protected String quantita;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getQuantita() {
			return quantita;
		}

		public void setQuantita(String quantita) {
			this.quantita = quantita;
		}

	}
}
