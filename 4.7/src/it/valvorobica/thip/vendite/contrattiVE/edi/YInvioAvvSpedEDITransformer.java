package it.valvorobica.thip.vendite.contrattiVE.edi;

import com.thera.thermfw.xml.LWElement;

import it.thera.thip.vendite.contrattiVE.edi.InvioAvvSpedEDITransformer;
import it.thera.thip.vendite.documentoVE.DocumentoVenRigaPrm;
import it.thera.thip.vendite.documentoVE.DocumentoVendita;

/**
 * 
 * <h1>Softre Solutions</h1> <br>
 * 
 * @version 1.0
 * @author Daniele Signoroni 03/11/2023 <br>
 *         <br>
 *         <b>XXXXX DSSOF3 03/11/2023</b>
 *         <p>
 *         Settare nuovi campi.
 *         </p>
 */

public class YInvioAvvSpedEDITransformer extends InvioAvvSpedEDITransformer {

	// Attributi DocumentoSpedizione
	public static final String ATT_DOC_SPED_MOD_SPE = "modalitaSpedizione";
	public static final String ATT_DOC_SPED_MOD_CONS = "modalitaConsegna";
	public static final String ATT_DOC_SPED_TARGA_MEZZO = "targaMezzo";
	public static final String ATT_DOC_SPED_VETT_1 = "vettore1";
	public static final String ATT_DOC_SPED_DESC_VETT_1 = "descrVettore1";
	public static final String ATT_DOC_SPED_MAGAZ = "magazzino";
	public static final String ATT_DOC_SPED_MAGAZ_TRA = "magazzinoTrasferimento";
	public static final String ATT_DOC_SPED_NR_COLLI = "nrColli";
	public static final String ATT_DOC_SPED_PESO_LORDO = "pesoLordo";
	public static final String ATT_DOC_SPED_PESO_NETTO = "pesoNetto";
	public static final String ATT_DOC_SPED_TOTALE_QTA = "totaleQta";
	public static final String ATT_DOC_SPED_ID_SPEDIZIONE = "idSpedizione";

	// Attributi riga
	public static final String ATT_RIGA_STRINGA_RIS_UTE_2 = "posizioneRiga";
	public static final String ATT_RIGA_STRINGA_RIS_UTE_1 = "articoloCliente";
	public static final String ATT_RIGA_PESO_LORDO = "pesoLordo";
	public static final String ATT_RIGA_PESO_NETTO = "pesoNetto";

	/**
	 * Appendo pie finale a doc sped
	 */
	@Override
	public LWElement transformDDTToXML(DocumentoVendita docVen) {
		LWElement elStandard = super.transformDDTToXML(docVen);
		LWElement pieFinale = getPieFinale(docVen);
		elStandard.appendChild(pieFinale);
		return elStandard;
	}

	/**
	 * Pie finale dove totalizzare le righe
	 * 
	 * @param docVen
	 * @return
	 */
	public LWElement getPieFinale(DocumentoVendita docVen) {
		LWElement pie = new LWElement("PieFinale");
		pie.setAttribute(ATT_DATI_FINE_IDMSG, "");
		return pie;
	}

	@Override
	public LWElement getElementRigaArticolo(DocumentoVenRigaPrm rigaDoc) {
		LWElement elStandard = super.getElementRigaArticolo(rigaDoc);
		elStandard.setAttribute(ATT_RIGA_STRINGA_RIS_UTE_1, getValueElement(rigaDoc.getAlfanumRiservatoUtente1()));
		elStandard.setAttribute(ATT_RIGA_STRINGA_RIS_UTE_2, getValueElement(rigaDoc.getAlfanumRiservatoUtente2()));
		elStandard.setAttribute(ATT_RIGA_PESO_LORDO, getValueElement(rigaDoc.getPesoLordo()));
		elStandard.setAttribute(ATT_RIGA_PESO_NETTO, getValueElement(rigaDoc.getPesoNetto()));
		elStandard.setAttribute("dataConsegna", getValueElement(rigaDoc.getDataConsegnaConfermata()));
		return elStandard;
	}

	@Override
	public LWElement getElementDocumentoSpedizione(DocumentoVendita docVen) {
		LWElement elStandard = super.getElementDocumentoSpedizione(docVen);
		elStandard.setAttribute(ATT_DOC_SPED_MOD_SPE, getValueElement(docVen.getIdModSpedizione()));
		elStandard.setAttribute(ATT_DOC_SPED_MOD_CONS, getValueElement(docVen.getIdModConsegna()));
		elStandard.setAttribute(ATT_DOC_SPED_TARGA_MEZZO, getValueElement(docVen.getTargaMezzo()));
		elStandard.setAttribute(ATT_DOC_SPED_VETT_1,
				getValueElement(docVen.getVettore1() != null ? docVen.getVettore1().getRagioneSociale() : "ssssssss"));
		elStandard.setAttribute(ATT_DOC_SPED_DESC_VETT_1, getValueElement(docVen.getDescrVettore1()));
		elStandard.setAttribute(ATT_DOC_SPED_MAGAZ, getValueElement(docVen.getIdMagazzino()));
		elStandard.setAttribute(ATT_DOC_SPED_MAGAZ_TRA, getValueElement(docVen.getIdMagazzinoTra()));
		elStandard.setAttribute(ATT_DOC_SPED_NR_COLLI, getValueElement(docVen.getNumeroColli()));
		elStandard.setAttribute(ATT_DOC_SPED_PESO_LORDO, getValueElement(docVen.getPesoLordo()));
		elStandard.setAttribute(ATT_DOC_SPED_PESO_NETTO, getValueElement(docVen.getPesoNetto()));
		elStandard.setAttribute(ATT_DOC_SPED_ID_SPEDIZIONE, getValueElement(docVen.getIdModSpedizione()));
		elStandard.setAttribute(ATT_DOC_SPED_TOTALE_QTA, getValueElement("")); // somma pz righe?, in caso di um != pz?
		return elStandard;
	}
}
