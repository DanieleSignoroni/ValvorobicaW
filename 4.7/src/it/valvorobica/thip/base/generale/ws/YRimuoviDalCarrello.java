package it.valvorobica.thip.base.generale.ws;

import java.util.Map;

import com.google.gson.Gson;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.persist.ConnectionDescriptor;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.security.Security;
import com.thera.thermfw.web.SessionEnvironment;

import it.thera.thip.ws.GenRequestJSON;
import it.valvorobica.thip.base.portal.YCarrelloPortale;

/**
 * <h1>Softre Solutions</h1>
 * <br>
 * @author Daniele Signoroni 31/08/2023
 * <br><br>
 * <b>XXXXX	DSSOF3	31/08/2023</b>	<p>Prima stesura.<br>
 * 										Data una lista di chiavi di item da rimuovere, li tolgo dalla tabella carrello.
 * 									</p>
 */

public class YRimuoviDalCarrello extends GenRequestJSON{

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Map execute(Map m) {
		boolean isopen = false;		
		Object[] info = SessionEnvironment.getDBInfoFromIniFile();
		String dbName = (String)info[0];
		try {		
			if(!Security.isCurrentDatabaseSetted()) {
				Security.setCurrentDatabase(dbName, null);
			}
			Security.openDefaultConnection();
			isopen = true;
			ConnectionDescriptor cd = ConnectionManager.getCurrentConnectionDescriptor();
			Gson gson = new Gson();
			Item[] items = gson.fromJson((String) this.getAppParams().get("keys"), Item[].class);
			if(items != null) {
				for (int i = 0; i < items.length; i++) {
					YRimuoviDalCarrello.Item itemDaCancellare = items[i];
					YCarrelloPortale item = (YCarrelloPortale)
							YCarrelloPortale.elementWithKey(YCarrelloPortale.class, itemDaCancellare.value, PersistentObject.NO_LOCK);
					if(item != null) {
						if(item.delete() > 0) {
							cd.commit();
						}else {
							cd.rollback();
						}
					}else {
						m.put("errors", "Non e' stato trovato nessun articolo nel carrello");
					}
				}
			}else {
				m.put("errors", "Nessuna chiave passata");
			}
		}catch (Throwable t) {
			t.printStackTrace(Trace.excStream);
		}
		finally {
			if (isopen) {
				Security.closeDefaultConnection();
			}
		}
		return m;
	}

	public class Item{

		protected int id;

		protected String value;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}
}
