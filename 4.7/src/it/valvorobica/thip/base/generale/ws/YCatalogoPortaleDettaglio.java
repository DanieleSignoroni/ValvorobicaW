package it.valvorobica.thip.base.generale.ws;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.thera.thermfw.ad.ClassAD;
import com.thera.thermfw.ad.ClassADCollection;
import com.thera.thermfw.ad.ClassADCollectionManager;
import com.thera.thermfw.base.TimeUtils;
import com.thera.thermfw.gui.cnr.DOList;
import com.thera.thermfw.gui.cnr.DisplayObject;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.setting.Criterion;
import com.thera.thermfw.setting.LiteralCriterion;
import com.thera.thermfw.setting.NumericLikeCriterion;
import com.thera.thermfw.setting.Setting;
import com.thera.thermfw.type.ComparisonOperator;
import com.thera.thermfw.type.EqualOperator;
import com.thera.thermfw.type.LikeOperator;
import com.thera.thermfw.type.NumberType;
import com.thera.thermfw.type.Type;
import com.thera.thermfw.web.ServletEnvironment;

import it.thera.thip.base.articolo.Articolo;
import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.base.cliente.ClienteVendita;
import it.thera.thip.base.partner.Valuta;
import it.thera.thip.magazzino.saldi.SaldoMag;
import it.thera.thip.magazzino.saldi.SaldoMagTM;
import it.thera.thip.vendite.generaleVE.CondizioniDiVendita;
import it.thera.thip.vendite.generaleVE.RicercaCondizioniDiVendita;
import it.thera.thip.ws.GenRequestJSON;
import it.valvorobica.thip.base.portal.YUserPortalSession;

public class YCatalogoPortaleDettaglio extends GenRequestJSON {

	protected YUserPortalSession user = null;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Map execute(Map m) {
		String json = null;
		JsonArray objects = new JsonArray();
		JsonArray headers = new JsonArray();
		YUserPortalSession user = (YUserPortalSession) getRequest().getSession().getAttribute("YUserPortal");
		this.user = user;
		this.user.dettagli_caricati.clear();
		if (!user.dettagli_caricati.containsKey(this.getIdVista().trim())) { // far si che la vista sia il fullPath e
																				// replace
																				// ("/",KeyHelper.KEY_SERPARATOR) in
																				// modo che se ci sono titoli uguali ma
																				// di diverse categorie il programma non
																				// cacha sbagliato
			// es Catalogo/Accessori/Raccordo/BX_10
			// Catalogo/Flangia/Valvola/BX_10
			// in questo caso cacho due item diversi, non controllo solo BX_10
			Vector keys = new Vector();
			List criteria = getNodeCriteria(null);
			try {
				Setting setting = (Setting) Setting.elementWithKey(Setting.class, "4738", PersistentObject.NO_LOCK);
				if (setting != null) {
					DOList doList = (DOList) Factory.createObject(DOList.class);
					doList.setClassADCollection(getClassADCollection(null));
					for (int i = 0; i < criteria.size(); i++) {
						Criterion criterion = (Criterion) criteria.get(i);
						if (!isEmptyCriterion(criterion))
							setting.addCriterion(criterion);
					}
					doList.initSetting(setting);
					doList.openList(-1, true);
					Vector detail = doList.getDetailsDO();
					for (Iterator iterator = detail.iterator(); iterator.hasNext();) {
						DisplayObject obj = (DisplayObject) iterator.next();
						keys.add(obj.getObjectKey());
					}
					doList.closeList();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				try {
					ClienteVendita cliente = (ClienteVendita) ClienteVendita.elementWithKey(ClienteVendita.class,
							KeyHelper.buildObjectKey(new String[] { this.getCompany(), this.getCliente() }),
							PersistentObject.NO_LOCK);
					Articolo art = (Articolo) Articolo.elementWithKey(Articolo.class, key, PersistentObject.NO_LOCK);
					if (art != null) {
						JsonObject jsonDettaglio = new JsonObject();
						jsonDettaglio.addProperty("IdArticoloPth", art.getIdArticolo());
						jsonDettaglio.addProperty("IdArticolo",
								art.getVecchioArticolo() != null ? art.getVecchioArticolo() : art.getIdArticolo());
						jsonDettaglio.addProperty("DescrEstesa",
								art.getDescrizioneArticoloNLS().getDescrizioneEstesa() != null
										? art.getDescrizioneArticoloNLS().getDescrizioneEstesa()
										: art.getDescrizioneArticoloNLS().getDescrizione());
						BigDecimal disponibilita = getDisponibilita(art);
						jsonDettaglio.addProperty("Giacenza", disponibilita.toString());
						CondizioniDiVendita condVen = recuperaCondizioniDiVendita(art, cliente);
						if (condVen != null) {
							jsonDettaglio.addProperty("Prezzo",
									condVen.getPrezzo() != null ? condVen.getPrezzo().toString()
											: BigDecimal.ZERO.toString());
						} else {
							jsonDettaglio.addProperty("Prezzo", BigDecimal.ZERO.toString());
						}
						objects.add(jsonDettaglio);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			json = objects.toString();
			user.dettagli_caricati.put(this.getIdVista().trim(), json);
		} else {
			json = user.dettagli_caricati.get(this.getIdVista().trim());
		}
		JsonObject headArt = new JsonObject();
		headArt.addProperty("id", "Articolo");
		headers.add(headArt);
		JsonObject headDescr = new JsonObject();
		headDescr.addProperty("id", "Descrizione");
		headers.add(headDescr);
		JsonObject headGiac = new JsonObject();
		headGiac.addProperty("id", "Giacenza");
		headers.add(headGiac);
		JsonObject headPrice = new JsonObject();
		headPrice.addProperty("id", "Prezzo");
		headers.add(headPrice);
		JsonObject headQtaIns = new JsonObject();
		headQtaIns.addProperty("id", "Qta. ordinata");
		headers.add(headQtaIns);
		JsonObject carello = new JsonObject();
		carello.addProperty("id", "Carrello");
		headers.add(carello);
		m.put("headers", headers.toString());
		m.put("records", json);
		return m;
	}

	@SuppressWarnings("rawtypes")
	private BigDecimal getDisponibilita(Articolo art) {
		String where = " " + SaldoMagTM.ID_AZIENDA + " = '" + this.getCompany() + "' " + "AND " + SaldoMagTM.ID_ARTICOLO
				+ " = '" + art.getIdArticolo() + "' AND " + SaldoMagTM.ID_MAGAZZINO + " = '001' ";
		try {
			Vector saldi = SaldoMag.retrieveList(SaldoMag.class, where, "", false);
			if (saldi.size() > 0) {
				SaldoMag saldo = (SaldoMag) saldi.get(0);
				BigDecimal zero = new BigDecimal(0);

				BigDecimal giacenza = saldo.getDatiSaldo().getQtaGiacenzaUMPrim() == null ? zero
						: saldo.getDatiSaldo().getQtaGiacenzaUMPrim();
				BigDecimal ordinatoCliente = saldo.getDatiSaldo().getQtaOrdClienteUMPrim() == null ? zero
						: saldo.getDatiSaldo().getQtaOrdClienteUMPrim();
				BigDecimal ordinatoProduzione = saldo.getDatiSaldo().getQtaOrdProduzioneUMPrim() == null ? zero
						: saldo.getDatiSaldo().getQtaOrdProduzioneUMPrim();
				BigDecimal ordinatoCtoLavoro = saldo.getDatiSaldo().getQtaOrdCtoLavoroUMPrim() == null ? zero
						: saldo.getDatiSaldo().getQtaOrdCtoLavoroUMPrim();

				BigDecimal dsp = giacenza.subtract(ordinatoCliente).subtract(ordinatoProduzione)
						.subtract(ordinatoCtoLavoro);
				return dsp;
				// bisogna anche sottrarre eventuali articoli nel carrello dell'utente
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return BigDecimal.ZERO;
	}

	public CondizioniDiVendita recuperaCondizioniDiVendita(Articolo articolo, ClienteVendita cliente) {
		RicercaCondizioniDiVendita rcdv = new RicercaCondizioniDiVendita();
		CondizioniDiVendita cdv = null;
		try {
			cdv = rcdv.ricercaCondizioniDiVendita(Azienda.getAziendaCorrente(), // String idAzienda
					cliente.getListino(), // ListinoVendita listino
					cliente, // ClienteVendita cliente
					articolo, // Articolo articolo
					articolo.getUMDefaultVendita(), // UnitaMisura unita
					BigDecimal.ONE, // BigDecimal quantita
					null, // BigDecimal importo
					null, // ModalitaPagamento modalita
					TimeUtils.getCurrentDate(), // java.sql.Date dataValidita
					cliente.getAgente(), // Agente agente DSSOF3 71003
					cliente.getSubAgente(), // Agente subagente DSSOF3 71003
					null, // UnitaMisura unitaMag
					BigDecimal.ONE, // BigDecimal quantitaMag DSSOF3 71067
					(Valuta) Valuta.elementWithKey(Valuta.class, "EUR", 0), // Valuta valuta
					null, // UnitaMisura umSecMag
					null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cdv;
	}

	public boolean isEmptyCriterion(Criterion criterion) {
		return criterion.getAttributeName().equals("") && criterion.getOperator() == null
				&& criterion.getValue(0, 0).equals("");
	}

	@SuppressWarnings("rawtypes")
	protected List getNodeCriteria(ServletEnvironment se) {
		String criteriaString = this.getCondizione();
		return unpackNodeCriteriaString(se, criteriaString);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected List unpackNodeCriteriaString(ServletEnvironment se, String criteriaString) {
		List criteria = new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(criteriaString, KeyHelper.KEY_SEPARATOR);
		while (tokenizer.hasMoreTokens()) {
			String columnName = tokenizer.nextToken();
			String operatorString = tokenizer.nextToken();
			String value = tokenizer.nextToken();
			boolean isLikeOperator = operatorString.equalsIgnoreCase("like");
			ClassAD cad = getClassADWithColumnName(se, columnName);
			Criterion currentCriterion = createCriterion(cad, value, isLikeOperator);
			if (currentCriterion != null)
				criteria.add(currentCriterion);
		}
		return criteria;
	}

	@SuppressWarnings("rawtypes")
	public ClassAD getClassADWithColumnName(ServletEnvironment se, String colmunName) {
		Hashtable allAttributes = getClassADCollection(se).getAllAttributes();
		Enumeration attributesEnum = allAttributes.elements();

		ClassAD target = null;
		while (attributesEnum.hasMoreElements() && target == null) {
			ClassAD currentClassAD = (ClassAD) attributesEnum.nextElement();
			if (currentClassAD.getColumnName() != null && currentClassAD.getColumnName().equals(colmunName))
				target = currentClassAD;
		}
		return target;
	}

	protected Criterion createEmptyCriterion() {
		LiteralCriterion emptyCriterion = new LiteralCriterion();
		emptyCriterion.setAttributeName("");
		emptyCriterion.setOperator(null);
		emptyCriterion.setValue(0, 0, "");
		return emptyCriterion;
	}

	protected Criterion createCriterion(ClassAD cad, String value, boolean likeOperator) {
		LiteralCriterion crit = null;
		Type type = cad.getType();

		if (likeOperator && type instanceof NumberType)
			crit = new NumericLikeCriterion();
		else {
			crit = new LiteralCriterion();
			crit.setCriterionType(Criterion.LITERAL);
		}
		crit.setAttribute(cad);
		crit.setOperator(likeOperator ? (ComparisonOperator) LikeOperator.getInstance()
				: (ComparisonOperator) EqualOperator.getInstance());
		crit.setValue(0, 0, value);
		return crit;
	}

	protected ClassADCollection getClassADCollection(ServletEnvironment se) {
		ClassADCollection objectCad = null;
		try {
			objectCad = ClassADCollectionManager.collectionWithName("Articolo");
		} catch (NoSuchElementException nsee) {
			System.out.println(nsee);
		} catch (NoSuchFieldException nsfe) {
			System.out.println(nsfe);
		}
		return objectCad;
	}

	public String getCliente() {
		return (String) this.getAppParams().get("idCliente");
	}

	public String getCondizione() {
		return (String) this.getAppParams().get("where");
	}

	public String getIdAzienda() {
		return (String) this.getAppParams().get("company");
	}

	public String getIdUtentePortale() {
		return (String) this.getAppParams().get("idUserPortal");
	}

	public String getIdVista() {
		return (String) this.getAppParams().get("idVista").toString().replace("/", KeyHelper.KEY_SEPARATOR);
	}

}
