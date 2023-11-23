package it.valvorobica.thip.base.portal;

import java.sql.*;

import com.thera.thermfw.common.*;
import com.thera.thermfw.base.*;

public class YCarrelloPortale extends YCarrelloPortalePO {

	public ErrorMessage checkDelete() {
		return null;
	}

	public int save() throws SQLException {
		if (!isOnDB()) {
			try {
				setProgressivo(new Integer(Numerator.getNextInt("YCarrelloPortale")));
			}
			catch(NumeratorException e) {e.printStackTrace(Trace.excStream);}
		}
		return super.save();
	}

}

