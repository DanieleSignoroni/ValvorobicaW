package it.valvorobica.thip.magazzino.chiusure;

import java.math.BigDecimal;

/**
 * <h1>Softre Solutions</h1> <br>
 * 
 * @author Daniele Signoroni 24/11/2023 <br>
 *         <br>
 *         <b>71310 DSSOF3 24/11/2023</b>
 *         <p>
 *         </p>
 */

public class YOggettinoLetturaValFis {
	public String R_AZIENDA;
	public String R_ANNO_FISCALE;
	public String R_RAG_FSC_MAG;
	public String R_ARTICOLO;
	public String R_CONFIG;
	public String R_UNITA_MISURA; // aggiunta DSSOF3
	public BigDecimal CARICHI;
	public BigDecimal SCARICHI;
	public BigDecimal VAL_CARICHI;
	public BigDecimal VAL_SCARICHI;

	public BigDecimal COSTO_MED_POND;
	public BigDecimal COSTO_MED_POND_MANUALE;
}
