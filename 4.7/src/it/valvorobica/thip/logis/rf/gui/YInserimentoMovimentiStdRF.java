package it.valvorobica.thip.logis.rf.gui;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.thera.thermfw.base.ResourceLoader;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.base.Utils;
import com.thera.thermfw.persist.CachedStatement;
import com.thera.thermfw.rf.driver.Driver;
import com.thera.thermfw.rf.driver.FormCreator;
import com.thera.thermfw.rf.tc.TForm;
import com.thera.thermfw.rf.tc.TList;

import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.logis.fis.InserimentoMovimenti;
import it.thera.thip.logis.fis.OperazioneMovimento;
import it.thera.thip.logis.fis.Saldo;
import it.thera.thip.logis.rf.gui.InserimentoMovimentiStdRF;
import it.valvorobica.thip.logis.bas.YCostantiValvo;

/**
 * 
 * <h1>Softre Solutions</h1> <br>
 * 
 * @author Daniele Signoroni 19/12/2023 <br>
 *         <br>
 *         <b>71357 DSSOF3 19/12/2023</b>
 *         <p>
 *         Modificare schermata per funzione 10.<br>
 *         </br>
 *         Lotto e colata spostati e formattati su due righe:<br>
 *         </br>
 *         L: Lotto1 <br>
 *         C: Colata1 <br>
 *         </br>
 *         Ridefinizione {@link #settaLottiPgMovimento(TForm)} per portare anche
 *         nella funzione 22, il formato sopra descritto.<br>
 *         In questa funzione non abbiamo una {@link TList} ma una
 *         {@link TForm}, dunque e' stato necessario aggiugere la form
 *         personalizzata nell'xml.<br>
 *         Nel costruttore qui sotto la variabile --> formPaginaMovimento e'
 *         stata settata a quella personalizzata.
 *         </p>
 */

public class YInserimentoMovimentiStdRF extends InserimentoMovimentiStdRF {

	public static final String PREF_LOTTO = "L";

	public static final String PREF_COLATA = "C";

	/**
	 * 
	 * @param xmlò
	 * 
	 *             70862 TBSOF3 nella lista dei saldi è stata aggiunta una riga a
	 *             saldo per mostrare in modo completo il lotto (preso il lotto
	 *             fornitore dalla vista SOFTRE.Y_LOTTI_LOGIS_V01
	 * 
	 */
	public YInserimentoMovimentiStdRF(Driver drv, String xml) {
		super(drv, xml);
		formPaginaMovimento = "YPaginaMovimento";
		try {
			FormCreator.print(YCostantiValvo.getFileXmlValvo(), this);
		} catch (Exception e) {
			e.printStackTrace(Trace.excStream);
		}
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	protected void caricaLista(TForm form, Vector elenco, boolean altraUbic) {
		Vector lista = new Vector();
		Vector elencoVecchio = form.getTList("ListaUbicazioni").getItems(); // Svuota.
		while (elencoVecchio.size() > 1) {
			elencoVecchio.removeElementAt(1);
		}
		int j = 1;
		if (altraUbic) {
			form.getTList("ListaUbicazioni").addItem(ResourceLoader.getString(RESOURCES, "gen0092"));
			j++;
		}
		for (int i = 0; i < elenco.size(); i++) { // Riempie
			Saldo s = (Saldo) elenco.elementAt(i);
			String item = formatoSaldoPers(s, (i + j), altraUbic);
			form.getTList("ListaUbicazioni").addItem(item);
			String lotto = s.getLotto1();
			if (lotto != null) {
				item = "   " + PREF_LOTTO + ":" + lotto; // Spazi per intendare
			}
			form.getTList("ListaUbicazioni").addItem(item);
			String colata = getLotto(s);
			if (colata != null) {
				item = "   " + PREF_COLATA + ":" + getLotto(s); // Spazi per intendare
			}
			if (item.length() > 20)
				item = item.substring(0, 20);
			form.getTList("ListaUbicazioni").addItem(item);
		}
		form.getTList("ListaUbicazioni").setCurrentSelectedItem(1);
		form.getTList("ListaUbicazioni").setCurrentTopItem(1);// 31.08.04
	}

	protected String getLotto(Saldo s) {
		String lotto = "";
		ResultSet rs = null;
		CachedStatement cs = null;
		try {
			String select = "SELECT LOTTO_LOGIS FROM SOFTRE.Y_LOTTI_LOGIS_V01 " + "WHERE ID_AZIENDA = '"
					+ Azienda.getAziendaCorrente() + "' AND " + "ID_ARTICOLO = '" + s.getCodiceArticolo() + "' AND "
					+ "ID_LOTTO = '" + s.getLotto1() + "'";
			cs = new CachedStatement(select);
			rs = cs.executeQuery();
			if (rs.next()) {
				lotto = rs.getString("LOTTO_LOGIS").trim();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Throwable t) {
				t.printStackTrace(Trace.excStream);
			}
		}
		return lotto;
	}

	@Override
	protected void settaLottiPgMovimento(TForm form) {
		super.settaLottiPgMovimento(form);
		String colata = getLotto(inserimentoMovimenti.getSaldoPrimario());
		String lotto = inserimentoMovimenti.getLotto(0);
		if (lotto != null) {
			form.getTField("LblLotto").setValue(" " + PREF_LOTTO + ":" + lotto);
		}
		if (colata != null) {
			form.getTField("LblColata").setValue(" " + PREF_COLATA + ":" + colata);
		}

	}

	protected String formatoSaldoPers(Saldo s, int pos, boolean primSec) {
		if (primSec || inserimentoMovimenti.getArticolo() != null)
			return formatoSaldo(s, pos);
		String res = pos + ")" + s.getCodiceArticolo() + "-" + formattaBigDec(s.getQta1());
		if (s.getMappaUdc() != null)
			res = formatoUdc(res, s.getCodiceMappaUdc());
		if (res.length() > 20)
			res = res.substring(0, 20);
		return res;
	}

	@Override
	public String formatoSaldo(Saldo s, int pos) {
		// return super.formatoSaldo(s, pos);
		String res = pos + ")" + s.getCodiceUbicazione() + "-" + formattaBigDec(s.getQta1());// 25.03.04
		if (s.getLotto1() != null)
			// res += "-" + s.getLotto1();
			if (s.getMappaUdc() != null)
				// res += "-" + s.getCodiceMappaUdc();
				res = formatoUdc(res, s.getCodiceMappaUdc());
		if (res.length() > 20) {
			// res = res.substring(0, 20);
		}
		return res;
	}

	@SuppressWarnings("rawtypes")
	protected void paginaUbicScarico() {
		TForm pointerForm4 = getTForm(formPaginaUbicScarico);
		inserimentoMovimenti.setSaldoPrimario(null);
		aggiornaFieldQta = true;
		String valoreInserito = trovaArticoloDaCampoVariabile(inserimentoMovimenti.getCampoVariabile());
		String codArticolo = inserimentoMovimenti.getCodiceArticolo();
		if (!Utils.areEqual(valoreInserito, codArticolo))
			inserimentoMovimenti.setCodiceArticolo(null);
		if (tipologiaMovimento == InserimentoMovimenti.CARICO)
			pointerForm4.getTField("Intestazione").setValue(">>>> " + ResourceLoader.getString(RESOURCES, "val0026"));
		else
			pointerForm4.getTField("Intestazione").setValue(">>>> " + ResourceLoader.getString(RESOURCES, "val0027"));
		pointerForm4.getTField("LblMultifunzione").setValue("");
		if (inserimentoMovimenti.getArticolo() != null)
			pointerForm4.getTField("LblMultifunzione")
					.setValue(ResourceLoader.getString(RESOURCES, "val0014") + ":" + formatLblArticolo());
		pointerForm4.getTField("FieldMultifunzione").setValue("");
		Vector elencoSaldiScarico = null;
		if (aggiornaSaldo) {
			inserimentoMovimenti.aggiornaSaldiPrimaVideata();
			elencoSaldiScarico = inserimentoMovimenti.getSaldiPrimaVideata();
		} else
			elencoSaldiScarico = inserimentoMovimenti.calcolaSaldiPrimaVideata();
		aggiornaSaldo = false;
		Saldo saldoScelto = null;
		try {
			switch (elencoSaldiScarico.size()) {
			case 0:
				if (tipologiaMovimento != InserimentoMovimenti.CARICO) {
					pagina = saldiNonPresenti();
				} else {
					paginaChiamanteCarico = BARCODE;
					paginaChiamanteLottoAtt = BARCODE;
					codUbicazioneSuggerita = null;
					pagina = SECONDARIA; // Direttamente al movimento.
				}
				break;
			case 1:
				if (tipologiaMovimento != InserimentoMovimenti.CARICO) {
					saldoScelto = (Saldo) elencoSaldiScarico.elementAt(0); // Un solo saldo: via subito a pg_carico.
					if (inserimentoMovimenti.getArticolo() == null)
						inserimentoMovimenti.setArticolo(saldoScelto.getArticolo());
					if (inserimentoMovimenti.getConfezione() == null)
						inserimentoMovimenti.setConfezione(saldoScelto.getConfezione());
					paginaChiamanteCarico = BARCODE;
					paginaChiamanteLottoAtt = BARCODE;
					pagina = SECONDARIA;
					break;
				}
			default:
				if (tipologiaMovimento != InserimentoMovimenti.CARICO || tipoBarcode == OperazioneMovimento.UBICAZIONE)
					caricaLista(pointerForm4, elencoSaldiScarico, false);
				else // Considera tutte le ubicazioni.
					caricaLista(pointerForm4, elencoSaldiScarico, true);
				sendForm(pointerForm4);
				TForm risposta = readInput();
				if (risposta.getKeyPressed().equals(TForm.KEY_ESC)
						|| risposta.getKeyPressed().equals(TForm.KEY_CTL_X)) {
					pagina = MENU;
					break;
				}
				if (risposta.getKeyPressed().equals(TForm.KEY_F5)) {
					pagina = BARCODE;
					break;
				}
				if (risposta.getKeyPressed().equals(TForm.KEY_F1) || risposta.getKeyPressed().equals(TForm.KEY_F4)
						|| risposta.getKeyPressed().equals(TForm.KEY_F6)
						|| risposta.getKeyPressed().equals(TForm.KEY_F7)
						|| risposta.getKeyPressed().equals(TForm.KEY_F9)
						|| risposta.getKeyPressed().equals(TForm.KEY_F10))
					break;
				String codScelta = pointerForm4.getTField("FieldMultifunzione").getValue();
				if (codScelta != null && !codScelta.equals("")) {
					for (int i = 0; i < elencoSaldiScarico.size() && saldoScelto == null; i++) {
						Object obj = elencoSaldiScarico.elementAt(i);
						if (obj instanceof Saldo) {
							if ((tipoBarcode == OperazioneMovimento.ARTICOLO
									&& ((Saldo) obj).getCodiceUbicazione().equals(codScelta))
									|| (tipoBarcode == OperazioneMovimento.UBICAZIONE
											&& (((Saldo) obj).getCodiceArticolo().equals(codScelta))
											|| hasBarcode(((Saldo) obj).getArticolo(), codScelta)))
								saldoScelto = (Saldo) obj;
							else if (((Saldo) obj).getCodiceMappaUdc() != null
									&& !((Saldo) obj).getCodiceMappaUdc().equals("")
									&& ((Saldo) obj).getCodiceMappaUdc().equals(codScelta))
								saldoScelto = (Saldo) obj;
						}
					}
					if (saldoScelto == null) {
						messaggio(true, ResourceLoader.getString(RESOURCES, "msg0029", new String[] { codScelta }));
						break;
					}
				} else {
					TList list = risposta.getTList("ListaUbicazioni");
					int itemPos = list.getCurrentSelectedItem();
					if (tipologiaMovimento == InserimentoMovimenti.CARICO
							&& tipoBarcode != OperazioneMovimento.UBICAZIONE) {
						if ((itemPos - 2) == 0) {
							itemPos = itemPos - 2;
							saldoScelto = (Saldo) elencoSaldiScarico.elementAt(itemPos);
						}

						else if ((itemPos - 2) < 0) {
							itemPos = itemPos - itemPos;
							saldoScelto = (Saldo) elencoSaldiScarico.elementAt(itemPos);
						} else {
							if (itemPos % 2 == 0) {
								itemPos = (itemPos / 2) - 1;
								saldoScelto = (Saldo) elencoSaldiScarico.elementAt(itemPos);

							} else {
								while (itemPos % 2 != 0) {
									itemPos++;
								}
								itemPos = (itemPos / 2) - 1;
								saldoScelto = (Saldo) elencoSaldiScarico.elementAt(itemPos);
							}
						}
					} else {
						if ((itemPos - 2) == 0) {
							itemPos = itemPos - 2;
							saldoScelto = (Saldo) elencoSaldiScarico.elementAt(itemPos);
						}

						else if ((itemPos - 2) < 0) {
							itemPos = itemPos - itemPos;
							saldoScelto = (Saldo) elencoSaldiScarico.elementAt(itemPos);
						} else {
							if (itemPos % 2 == 0) {
								itemPos = (itemPos / 2) - 1;
								saldoScelto = (Saldo) elencoSaldiScarico.elementAt(itemPos);

							} else {
								while (itemPos % 2 != 0) {
									itemPos++;
								}
								itemPos = (itemPos / 2) - 1;
								saldoScelto = (Saldo) elencoSaldiScarico.elementAt(itemPos);
							}
						}
					}
				}
				paginaChiamanteCarico = PRIMARIA;
				paginaChiamanteLottoAtt = PRIMARIA;
				if (risposta.getKeyPressed().equals(TForm.KEY_F3) && saldoScelto != null) {
					pagina = DETAIL;
					saldoSelezionato = saldoScelto;
					paginaChiamanteDetail = PRIMARIA;
				} else
					pagina = SECONDARIA;
				break;
			}
			if (tipologiaMovimento == InserimentoMovimenti.CARICO)
				inserimentoMovimenti.setSaldoSecondario(saldoScelto);
			else
				inserimentoMovimenti.setSaldoPrimario(saldoScelto);
			if (saldoScelto != null) {
				inserimentoMovimenti.setLotto((String[]) saldoScelto.getLotto().clone());
			}
		} catch (Exception e) {
			e.printStackTrace(Trace.excStream);
			pagina = MENU;
		}
	}

}
