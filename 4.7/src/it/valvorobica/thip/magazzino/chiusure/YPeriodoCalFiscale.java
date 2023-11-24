package it.valvorobica.thip.magazzino.chiusure;

import java.sql.Date;
import java.util.Vector;

import com.thera.thermfw.common.BaseComponentsCollection;
import com.thera.thermfw.common.ErrorMessage;

/**
 * <h1>Softre Solutions</h1> <br>
 * 
 * @author Daniele Signoroni 24/11/2023 <br>
 *         <br>
 *         <b>71310 DSSOF3 24/11/2023</b>
 *         <p>
 *         </p>
 */

@SuppressWarnings("rawtypes")
public class YPeriodoCalFiscale extends YPeriodoCalFiscalePO implements Comparable {

	public Vector checkAll(BaseComponentsCollection components) {
		Vector errors = new Vector();
		components.runAllChecks(errors);
		return errors;
	}

	public int compareTo(Object object) {
		Date dataInizio = ((YPeriodoCalFiscale) object).getDataInizio();
		return getDataInizio().compareTo(dataInizio);
	}

	public ErrorMessage checkDate() {
		if (getDataFine().before(getDataInizio()))
			return new ErrorMessage("BAS0000014");
		else
			return null;
	}

	public YPeriodoCalFiscale() {
		setEsecAttivPerFiscale(NESSUNA);
		setStatoChiusuraMag(NON_CHIUSO);
	}

}
