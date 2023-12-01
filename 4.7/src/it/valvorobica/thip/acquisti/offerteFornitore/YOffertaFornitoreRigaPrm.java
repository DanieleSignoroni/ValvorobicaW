package it.valvorobica.thip.acquisti.offerteFornitore;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Iterator;

import com.thera.thermfw.persist.CopyException;
import com.thera.thermfw.persist.Copyable;

import it.thera.thip.acquisti.offerteFornitore.OffertaFornitore;
import it.thera.thip.acquisti.offerteFornitore.OffertaFornitoreRigaPrm;
import it.thera.thip.base.articolo.Articolo;
import it.thera.thip.base.articolo.ArticoloUnitaMisura;
import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.base.generale.UnitaMisura;

/**
 * <h1>Softre Solutions</h1> <br>
 * 
 * @author Daniele Signoroni 04/07/2023 <br>
 *         <br>
 *         <b>71162 DSSOF3 04/07/2023</b>
 *         <p>
 *         Aggiunto l'attributo peso, valorizzato inizialmente con il perso
 *         dell'anagrafica articolo.
 *         </p>
 *         <b>71190 DSSOF3 03/08/2023</b>
 *         <p>
 *         In copia riga, non prendere il peso dall'articolo.<br>
 *         Prendere il peso netto dall'articolo.
 *         </p>
 */

public class YOffertaFornitoreRigaPrm extends OffertaFornitoreRigaPrm {

	protected BigDecimal iPeso;

	public YOffertaFornitoreRigaPrm() {
		setIdAzienda(Azienda.getAziendaCorrente());
	}

	public void setPeso(BigDecimal peso) {
		this.iPeso = peso;
		setDirty();
	}

	public BigDecimal getPeso() {
		return iPeso;
	}

	public void setEqual(Copyable obj) throws CopyException {
		super.setEqual(obj);
	}

	@Override
	public int salvaTestata(int rc) throws SQLException {
		OffertaFornitore offTes = (OffertaFornitore) this.getTestata();
		Object[] pesi = getPesITotalRigheOffertaFornitore(offTes);
		return super.salvaTestata(rc);
	}

	@Override
	public int save() throws SQLException {
		if (!this.isOnDB() && !this.isInCopiaRiga) { // aggiunto !inCopia 03/08/2023
			this.setPeso(
					this.getArticolo().getPesoNetto() != null ? this.getArticolo().getPesoNetto() : BigDecimal.ZERO);
		}
		return super.save();
	}

	@SuppressWarnings("unchecked")
	public static Object[] getPesITotalRigheOffertaFornitore(OffertaFornitore offTes) {
		Object[] pesi = new Object[2];
		BigDecimal totNetto = BigDecimal.ZERO;
		BigDecimal totLordo = BigDecimal.ZERO;
		Iterator<YOffertaFornitoreRigaPrm> iterRows = offTes.getRighe().iterator();
		while (iterRows.hasNext()) {
			YOffertaFornitoreRigaPrm row = iterRows.next();
			BigDecimal pesoLordoRiga = BigDecimal.ZERO;
			BigDecimal pesoNettoRiga = BigDecimal.ZERO;
			BigDecimal pesoNettoArt = row.getArticolo().getPesoNetto() != null ? row.getArticolo().getPesoNetto()
					: BigDecimal.ZERO;
			BigDecimal pesoLordoArt = row.getArticolo().getPeso() != null ? row.getArticolo().getPeso()
					: BigDecimal.ZERO; // questo e' peso lordo
			BigDecimal fttCnv = getFttConversionePeso(row.getArticolo().getUMTecnica(), row.getArticolo());
			pesoNettoRiga = row.getQuantitaOffertaAcq().getQuantitaInUMPrm().multiply(fttCnv).multiply(pesoNettoArt);
			pesoLordoRiga = row.getQuantitaOffertaAcq().getQuantitaInUMPrm().multiply(fttCnv).multiply(pesoLordoArt);
			totNetto = totNetto.add(pesoNettoRiga);
			totLordo = totLordo.add(pesoLordoRiga);
		}
		pesi[0] = totNetto;
		pesi[1] = totLordo;
		return pesi;
	}

	@SuppressWarnings("rawtypes")
	public static BigDecimal getFttConversionePeso(UnitaMisura um, Articolo art) {
		Iterator unita = art.getUMSpecifiche().iterator();
		while (unita.hasNext()) {
			ArticoloUnitaMisura artUniMis = (ArticoloUnitaMisura) unita.next();
			if (artUniMis.getUnitaMisura().getIdUnitaMisura().equals(um.getIdUnitaMisura())) {
				return artUniMis.getFattoreConverUM();
			}
		}
		return null;
	}

}
