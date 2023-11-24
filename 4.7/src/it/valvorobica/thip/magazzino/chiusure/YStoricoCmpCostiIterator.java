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
 *         Iteratore per storicizzare il costo articolo.
 *         </p>
 */

public class YStoricoCmpCostiIterator extends ResultSetIterator {

	public YStoricoCmpCostiIterator(ResultSet rs) {
		super(rs);
	}

	@Override
	protected Object createObject() throws SQLException {
		YOggettinoLetturaValFis oggettino = new YOggettinoLetturaValFis();
		oggettino.R_AZIENDA = cursor.getString(MovimentoMagazzinoTM.R_AZIENDA);
		oggettino.R_ANNO_FISCALE = cursor.getString(MovimentoMagazzinoTM.R_ANNO_FISCALE).trim();
		oggettino.R_ARTICOLO = cursor.getString(MovimentoMagazzinoTM.R_ARTICOLO);
		oggettino.COSTO_MED_POND = cursor.getBigDecimal("COSTO_MED_POND");
		oggettino.COSTO_MED_POND_MANUALE = cursor.getBigDecimal("COSTO_MED_POND_MANUALE");
		return oggettino;
	}

}