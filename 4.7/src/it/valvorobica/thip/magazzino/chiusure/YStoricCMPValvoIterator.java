package it.valvorobica.thip.magazzino.chiusure;

import java.sql.ResultSet;
import java.sql.SQLException;

import it.thera.thip.cs.ResultSetIterator;
import it.thera.thip.magazzino.movimenti.MovimentoMagazzinoTM;

/**
 * <h1>Softre Solutions</h1> <br>
 * 
 * @author Daniele Signoroni 24/11/2023 <br>
 *         <br>
 *         <b>71310 DSSOF3 24/11/2023</b>
 *         <p>
 *         </p>
 */

public class YStoricCMPValvoIterator extends ResultSetIterator {

	public YStoricCMPValvoIterator(ResultSet rs) {
		super(rs);
	}

	@Override
	protected Object createObject() throws SQLException {
		YOggettinoLetturaValFis oggettino = new YOggettinoLetturaValFis();
		oggettino.R_AZIENDA = cursor.getString(MovimentoMagazzinoTM.R_AZIENDA);
		oggettino.R_ANNO_FISCALE = cursor.getString(MovimentoMagazzinoTM.R_ANNO_FISCALE).trim();
		oggettino.R_RAG_FSC_MAG = cursor.getString("R_RAG_FSC_MAG");
		oggettino.R_ARTICOLO = cursor.getString(MovimentoMagazzinoTM.R_ARTICOLO);
		oggettino.R_CONFIG = cursor.getString(MovimentoMagazzinoTM.R_CONFIG);
		oggettino.R_UNITA_MISURA = cursor.getString(MovimentoMagazzinoTM.R_UNITA_MISURA);
		oggettino.CARICHI = cursor.getBigDecimal("CARICHI");
		oggettino.VAL_CARICHI = cursor.getBigDecimal("VAL_CARICHI");
		oggettino.SCARICHI = cursor.getBigDecimal("SCARICHI");
		oggettino.VAL_SCARICHI = cursor.getBigDecimal("VAL_SCARICHI");
		return oggettino;
	}

}