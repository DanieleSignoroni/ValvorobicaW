package it.thera.thip.cs.web;


import com.thera.thermfw.security.*;
import com.thera.thermfw.web.*;
import com.thera.thermfw.ad.ClassAD;
import com.thera.thermfw.base.SystemParam;

import it.thera.thip.cs.TicklerExtTM;

/**
 * <p>Title: TicklerDoList</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2014</p>
 *
 * <p>Company: </p>
 *
 * @author Elouni Ichrak
 * @version 1.0
 */
/**
 * Revisions:
 * Number  Date          Owner          Description
 * 20255   20/08/2014    Ichrak         Prima versione
 * 25791   26/10/2017    Rached         Modifica nel metodo setRestrictCondition().
 * 39523   28/08/2023    FG             Aggiunta condizione di riservatezza scadenza
 */

public class TicklerExtDoList extends WebDOList {

	protected static final String OBJ_TCK_LST = "OBJ_TCK_LST";

	public void setRestrictCondition(ClassAD[] attributes, String[] values) {
		String azione = se.getRequest().getParameter("thAction");

		super.setRestrictCondition(attributes, values);
		//DSSOF3	70747	Remmato STD, omessa la WHERE su utente corrente
		if(azione != null && azione.equals(OBJ_TCK_LST)){
			//      specificWhereClause += " AND ((" + TicklerExtTM.TABLE_NAME + "." + TicklerExtTM.CREATION_USER + "='" + Security.getCurrentUser().getId() + "')" +
			//                             " OR (" + TicklerExtTM.TABLE_NAME + "." + TicklerExtTM.ASSIGN_USER + "='" + Security.getCurrentUser().getId() + "')" +
			//                             //" OR (" + TicklerExtTM.TABLE_NAME + "." + TicklerExtTM.ASSIGN_GROUP + " IN ( SELECT GROUP_ID FROM THERA.USER_GROUP WHERE USER_ID = '" +//Fix 25791
			//                             " OR (" + TicklerExtTM.TABLE_NAME + "." + TicklerExtTM.ASSIGN_GROUP + " IN ( SELECT GROUP_ID FROM " + SystemParam.getFrameworkSchema() + "USER_GROUP WHERE USER_ID = '" +//Fix 25791
			//                             Security.getCurrentUser().getId() + "')))";
		}
		//39523 - inizio
		else {
			if(this.vedeScadenzeAziendali()) {
				//Se un utente può vedere le scadenze aziendali allora vede quelle aziendali e quelle riservate create da lui o assegnate a lui (o al suo gruppo)
				String riservato = TicklerExtTM.TABLE_NAME_EXT + "." + TicklerExtTM.RISERVATO;
				this.addSpecificWhereClause("(" + riservato + " = 'N' OR ( " + riservato + "= 'Y' AND (" + this.getTicklerUserWhereCondition() + ")))");
			}else {
				//Altrimenti vede solo quelle che sono state create da lui o assegnate a lui (o al suo gruppo)
				this.addSpecificWhereClause("(" + this.getTicklerUserWhereCondition() + ")");
			}
		}
		//39523 - fine
	}

	//39523 - inizio

	private String getTicklerUserWhereCondition() {
		String result = "(" + TicklerExtTM.TABLE_NAME + "." + TicklerExtTM.CREATION_USER + "='" + Security.getCurrentUser().getId() + "')"
				+ " OR (" + TicklerExtTM.TABLE_NAME + "." + TicklerExtTM.ASSIGN_USER + "='" + Security.getCurrentUser().getId() + "')"
				+ " OR (" + TicklerExtTM.TABLE_NAME + "." + TicklerExtTM.ASSIGN_GROUP + " IN ( SELECT GROUP_ID FROM " + SystemParam.getFrameworkSchema() + "USER_GROUP WHERE USER_ID = '" 
				+  Security.getCurrentUser().getId() + "'))";
		return result;
	}

	protected boolean vedeScadenzeAziendali() {
		return true;
	}

	//39523 - fine

}
