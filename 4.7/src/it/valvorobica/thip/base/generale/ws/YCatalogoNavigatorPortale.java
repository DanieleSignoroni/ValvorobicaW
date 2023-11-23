package it.valvorobica.thip.base.generale.ws;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.thera.thermfw.ad.ClassAD;
import com.thera.thermfw.base.SystemParam;
import com.thera.thermfw.common.BusinessObjectAdapter;
import com.thera.thermfw.common.ErrorMessage;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.Database;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.persist.Proxy;
import com.thera.thermfw.security.Security;
import com.thera.thermfw.setting.OrderByItem;
import com.thera.thermfw.setting.Setting;

import it.thera.thip.base.catalogo.CatalogoNavigatoreNodo;
import it.thera.thip.base.catalogo.CatalogoVista;
import it.thera.thip.base.listini.ListinoAcquisto;
import it.thera.thip.base.listini.ListinoVendita;
import it.thera.thip.base.profilo.Profilo;
import it.thera.thip.base.profilo.ThipUser;
import it.thera.thip.base.profilo.UtenteAzienda;

/**
 * <h1>Softre Solutions</h1>
 * <br>
 * @author Daniele Signoroni 31/08/2023
 * <br><br>
 * <b>XXXXX	DSSOF3	31/08/2023</b>	<p>Prima stesura.<br>
 * 										Copiata dallo STD, rimosse tutte le Azienda.getAziendaCorrente(),
 * 										dato che siamo nel portale e non viene settata.
 * 									</p>
 */

public class YCatalogoNavigatorPortale extends BusinessObjectAdapter
{

	public static final String SCHEMA = SystemParam.getSchema("THIP");   // Fix PC

	public static final String QueryArticolo1 = "SELECT s.DESCRIZIONE,c.ID_UNSPSC_SEG,f.DESCRIZIONE,c.ID_UNSPSC_FAM,cl.DESCRIZIONE,c.ID_UNSPSC_CLS,"
			+ " m.DESCRIZIONE,c.ID_UNSPSC_MRC,c.ID_AZIENDA"
			+ " FROM(SELECT DISTINCT ID_AZIENDA,ID_UNSPSC_SEG,ID_UNSPSC_FAM,ID_UNSPSC_CLS,ID_UNSPSC_MRC"
			+ " FROM " + SCHEMA + "CATALOGO_ART_V02 WHERE ID_AZIENDA = ?";


	public static final String QueryArticolo2 = ")c"
			+ " LEFT OUTER JOIN " + SCHEMA + "UNSPSC_SEG s ON c.ID_UNSPSC_SEG = s.ID_UNSPSC_SEG"
			+ " LEFT OUTER JOIN " + SCHEMA + "UNSPSC_FAM f ON c.ID_UNSPSC_SEG = f.ID_UNSPSC_SEG AND c.ID_UNSPSC_FAM = f.ID_UNSPSC_FAM"
			+ " LEFT OUTER JOIN " + SCHEMA + "UNSPSC_CLS cl ON c.ID_UNSPSC_SEG = cl.ID_UNSPSC_SEG AND c.ID_UNSPSC_FAM = cl.ID_UNSPSC_FAM AND c.ID_UNSPSC_CLS = cl.ID_UNSPSC_CLS"
			+ " LEFT OUTER JOIN " + SCHEMA + "UNSPSC_MRC m ON c.ID_UNSPSC_SEG = m.ID_UNSPSC_SEG AND c.ID_UNSPSC_FAM = m.ID_UNSPSC_FAM AND c.ID_UNSPSC_CLS = m.ID_UNSPSC_CLS"
			+ " AND c.ID_UNSPSC_MRC = m.ID_UNSPSC_MRC"
			+ " ORDER BY s.DESCRIZIONE, f.DESCRIZIONE,cl.DESCRIZIONE,m.DESCRIZIONE";



	public static final String QueryLisVen1 =  "SELECT s.DESCRIZIONE,c.ID_UNSPSC_SEG,f.DESCRIZIONE,c.ID_UNSPSC_FAM,cl.DESCRIZIONE,c.ID_UNSPSC_CLS,"
			+ " m.DESCRIZIONE,c.ID_UNSPSC_MRC,c.ID_AZIENDA"
			+ " FROM(SELECT DISTINCT ID_AZIENDA,ID_UNSPSC_SEG,ID_UNSPSC_FAM,ID_UNSPSC_CLS,ID_UNSPSC_MRC"
			+ " FROM " + SCHEMA + "CATAL_LST_VEN_V02 WHERE ID_AZIENDA = ? AND ID_LISTINO = ?";

	public static final String QueryLisVen2 =  ")c"
			+ " LEFT OUTER JOIN " + SCHEMA + "UNSPSC_SEG s ON c.ID_UNSPSC_SEG = s.ID_UNSPSC_SEG"
			+ " LEFT OUTER JOIN " + SCHEMA + "UNSPSC_FAM f ON c.ID_UNSPSC_SEG = f.ID_UNSPSC_SEG AND c.ID_UNSPSC_FAM = f.ID_UNSPSC_FAM"
			+ " LEFT OUTER JOIN " + SCHEMA + "UNSPSC_CLS cl ON c.ID_UNSPSC_SEG = cl.ID_UNSPSC_SEG AND c.ID_UNSPSC_FAM = cl.ID_UNSPSC_FAM AND c.ID_UNSPSC_CLS = cl.ID_UNSPSC_CLS"
			+ " LEFT OUTER JOIN " + SCHEMA + "UNSPSC_MRC m ON c.ID_UNSPSC_SEG = m.ID_UNSPSC_SEG AND c.ID_UNSPSC_FAM = m.ID_UNSPSC_FAM AND c.ID_UNSPSC_CLS = m.ID_UNSPSC_CLS"
			+ " AND c.ID_UNSPSC_MRC = m.ID_UNSPSC_MRC"
			+ " ORDER BY s.DESCRIZIONE, f.DESCRIZIONE,cl.DESCRIZIONE,m.DESCRIZIONE";



	public static final String QueryLisAcq1 = "SELECT s.DESCRIZIONE,c.ID_UNSPSC_SEG,f.DESCRIZIONE,c.ID_UNSPSC_FAM,cl.DESCRIZIONE,c.ID_UNSPSC_CLS,"
			+ " m.DESCRIZIONE,c.ID_UNSPSC_MRC,c.ID_AZIENDA"
			+ " FROM(SELECT DISTINCT ID_AZIENDA,ID_UNSPSC_SEG,ID_UNSPSC_FAM,ID_UNSPSC_CLS,ID_UNSPSC_MRC"
			+ " FROM " + SCHEMA + "CATAL_LST_ACQ_V02 WHERE ID_AZIENDA = ? AND ID_LISTINO = ?";

	public static final String QueryLisAcq2 = ")c"
			+ " LEFT OUTER JOIN " + SCHEMA + "UNSPSC_SEG s ON c.ID_UNSPSC_SEG = s.ID_UNSPSC_SEG"
			+ " LEFT OUTER JOIN " + SCHEMA + "UNSPSC_FAM f ON c.ID_UNSPSC_SEG = f.ID_UNSPSC_SEG AND c.ID_UNSPSC_FAM = f.ID_UNSPSC_FAM"
			+ " LEFT OUTER JOIN " + SCHEMA + "UNSPSC_CLS cl ON c.ID_UNSPSC_SEG = cl.ID_UNSPSC_SEG AND c.ID_UNSPSC_FAM = cl.ID_UNSPSC_FAM AND c.ID_UNSPSC_CLS = cl.ID_UNSPSC_CLS"
			+ " LEFT OUTER JOIN " + SCHEMA + "UNSPSC_MRC m ON c.ID_UNSPSC_SEG = m.ID_UNSPSC_SEG AND c.ID_UNSPSC_FAM = m.ID_UNSPSC_FAM AND c.ID_UNSPSC_CLS = m.ID_UNSPSC_CLS"
			+ " AND c.ID_UNSPSC_MRC = m.ID_UNSPSC_MRC"
			+ " ORDER BY s.DESCRIZIONE, f.DESCRIZIONE,cl.DESCRIZIONE,m.DESCRIZIONE";




	public static final String QueryCatalEsterno1 = "SELECT s.DESCRIZIONE,c.ID_UNSPSC_SEG,f.DESCRIZIONE,c.ID_UNSPSC_FAM,cl.DESCRIZIONE,c.ID_UNSPSC_CLS,"
			+ " m.DESCRIZIONE,c.ID_UNSPSC_MRC,c.ID_AZIENDA"
			+ " FROM(SELECT DISTINCT ID_AZIENDA,ID_UNSPSC_SEG,ID_UNSPSC_FAM,ID_UNSPSC_CLS,ID_UNSPSC_MRC"
			+ " FROM " + SCHEMA + "CATAL_ESTERNO_V02 WHERE ID_AZIENDA = ?";


	public static final String QueryCatalEsterno2 = ")c"
			+ " LEFT OUTER JOIN " + SCHEMA + "UNSPSC_SEG s ON c.ID_UNSPSC_SEG = s.ID_UNSPSC_SEG"
			+ " LEFT OUTER JOIN " + SCHEMA + "UNSPSC_FAM f ON c.ID_UNSPSC_SEG = f.ID_UNSPSC_SEG AND c.ID_UNSPSC_FAM = f.ID_UNSPSC_FAM"
			+ " LEFT OUTER JOIN " + SCHEMA + "UNSPSC_CLS cl ON c.ID_UNSPSC_SEG = cl.ID_UNSPSC_SEG AND c.ID_UNSPSC_FAM = cl.ID_UNSPSC_FAM AND c.ID_UNSPSC_CLS = cl.ID_UNSPSC_CLS"
			+ " LEFT OUTER JOIN " + SCHEMA + "UNSPSC_MRC m ON c.ID_UNSPSC_SEG = m.ID_UNSPSC_SEG AND c.ID_UNSPSC_FAM = m.ID_UNSPSC_FAM AND c.ID_UNSPSC_CLS = m.ID_UNSPSC_CLS"
			+ " AND c.ID_UNSPSC_MRC = m.ID_UNSPSC_MRC"
			+ " ORDER BY s.DESCRIZIONE, f.DESCRIZIONE,cl.DESCRIZIONE,m.DESCRIZIONE";



	public static final String QueryLisEsterno1 = "SELECT s.DESCRIZIONE,c.ID_UNSPSC_SEG,f.DESCRIZIONE,c.ID_UNSPSC_FAM,cl.DESCRIZIONE,c.ID_UNSPSC_CLS,"
			+ " m.DESCRIZIONE,c.ID_UNSPSC_MRC,c.ID_AZIENDA"
			+ " FROM(SELECT DISTINCT ID_AZIENDA,ID_UNSPSC_SEG,ID_UNSPSC_FAM,ID_UNSPSC_CLS,ID_UNSPSC_MRC"
			+ " FROM " + SCHEMA + "CATAL_LST_ESN_V02 WHERE ID_AZIENDA = ?";

	public static final String QueryLisEsterno2 = ")c"
			+ " LEFT OUTER JOIN " + SCHEMA + "UNSPSC_SEG s ON c.ID_UNSPSC_SEG = s.ID_UNSPSC_SEG"
			+ " LEFT OUTER JOIN " + SCHEMA + "UNSPSC_FAM f ON c.ID_UNSPSC_SEG = f.ID_UNSPSC_SEG AND c.ID_UNSPSC_FAM = f.ID_UNSPSC_FAM"
			+ " LEFT OUTER JOIN " + SCHEMA + "UNSPSC_CLS cl ON c.ID_UNSPSC_SEG = cl.ID_UNSPSC_SEG AND c.ID_UNSPSC_FAM = cl.ID_UNSPSC_FAM AND c.ID_UNSPSC_CLS = cl.ID_UNSPSC_CLS"
			+ " LEFT OUTER JOIN " + SCHEMA + "UNSPSC_MRC m ON c.ID_UNSPSC_SEG = m.ID_UNSPSC_SEG AND c.ID_UNSPSC_FAM = m.ID_UNSPSC_FAM AND c.ID_UNSPSC_CLS = m.ID_UNSPSC_CLS"
			+ " AND c.ID_UNSPSC_MRC = m.ID_UNSPSC_MRC"
			+ " ORDER BY s.DESCRIZIONE, f.DESCRIZIONE,cl.DESCRIZIONE,m.DESCRIZIONE";





	public static final String SEPARATOR = KeyHelper.KEY_SEPARATOR; //String.valueOf((char)22);

	public static  PreparedStatement cpstmtArt = null;
	public static  PreparedStatement cpstmtVen = null;
	public static  PreparedStatement cpstmtAcq = null;

	public static  PreparedStatement cpstmtCatalEst = null;
	public static  PreparedStatement cpstmtListEst = null;

	@SuppressWarnings("rawtypes")
	Map attributeInfomap  = new HashMap();


	protected Proxy iCatalogoVista = new Proxy(it.thera.thip.base.catalogo.CatalogoVista.class);
	protected Proxy iListinoVen = new Proxy(ListinoVendita.class);
	protected Proxy iListinoAcq = new Proxy(ListinoAcquisto.class);
	CatalogoNavigatoreNodo iNodoRadice = new CatalogoNavigatoreNodo();
	protected java.sql.Date iData ;//= new java.sql.Date();


	public YCatalogoNavigatorPortale()
	{
		fillStaticTable();
	}
	public void setData(java.sql.Date Data)
	{
		this.iData = Data;
	}

	public java.sql.Date getData()
	{
		return iData;
	}


	public String getIdAzienda() {
		String key = iCatalogoVista.getKey();
		String objIdAzienda = KeyHelper.getTokenObjectKey(key,1);
		return objIdAzienda;
	}

	//Proxy iCatalogoVista

	public void setCatalogoVista(CatalogoVista catalogoVista) {
		this.iCatalogoVista.setObject(catalogoVista);

	}


	public CatalogoVista getCatalogoVista() {
		return (CatalogoVista)iCatalogoVista.getObject();
	}

	public void setCatalogoVistaKey(String key) {
		iCatalogoVista.setKey(key);

	}

	public String getCatalogoVistaKey() {
		return iCatalogoVista.getKey();
	}


	public void setIdCatalogoVista(String IdCatalogoVista) {
		String key = iCatalogoVista.getKey();
		iCatalogoVista.setKey(KeyHelper.replaceTokenObjectKey(key , 2, IdCatalogoVista));

	}

	public String getIdCatalogoVista() {
		String key = iCatalogoVista.getKey();
		String objIdCatalogoVista = KeyHelper.getTokenObjectKey(key,2);
		return objIdCatalogoVista;
	}


	//Proxy iListinoVen

	public void setListinoVen(ListinoVendita listinoVendita)
	{
		iListinoVen.setObject(listinoVendita);

	}

	public ListinoVendita getListinoVen()
	{
		return (ListinoVendita)iListinoVen.getObject();
	}

	public void setListinoVenKey(String newKey)
	{
		iListinoVen.setKey(newKey);
	}

	public String getListinoVenKey()
	{
		return iListinoVen.getKey();
	}


	public void setIdListinoVen(String idListinoVen)
	{
		setListinoVenKey(KeyHelper.replaceTokenObjectKey(getListinoVenKey(), 2, idListinoVen ));
	}

	public String getIdListinoVen()
	{
		return KeyHelper.getTokenObjectKey(getListinoVenKey(), 2);
	}


	//Proxy iListinoAcq


	public void setListinoAcq(ListinoAcquisto listinoAcquisto)
	{
		iListinoAcq.setObject(listinoAcquisto);

	}

	public ListinoAcquisto getListinoAcq()
	{
		return (ListinoAcquisto)iListinoAcq.getObject();
	}

	public void setListinoAcqKey(String newKey)
	{
		iListinoAcq.setKey(newKey);
	}

	public String getListinoAcqKey()
	{
		return iListinoAcq.getKey();
	}


	public void setIdListinoAcq(String idListinoAcq)
	{
		setListinoAcqKey(KeyHelper.replaceTokenObjectKey(getListinoAcqKey(), 2, idListinoAcq ));
	}

	public String getIdListinoAcq()
	{
		return KeyHelper.getTokenObjectKey(getListinoAcqKey(), 2);
	}




	//Proxy iNodoRadice

	public void setIdNodoRadice(int idNodoRadice)
	{
		this.iNodoRadice.setIdNodo(idNodoRadice);
	}

	public int getIdNodoRadice()
	{
		return this.iNodoRadice.getIdNodo();
	}

	public CatalogoNavigatoreNodo getNodoRadice()
	{
		return this.iNodoRadice;
	}

	public void setNodoRadice(CatalogoNavigatoreNodo root)
	{
		iNodoRadice = root;
	}

	public void setIdAzienda(String idAzienda)
	{
		String catalogoVistaKey = iCatalogoVista.getKey();
		String listinoVenKey = iListinoVen.getKey();
		String listinoAcqKey = iListinoAcq.getKey();
		iCatalogoVista.setKey(KeyHelper.replaceTokenObjectKey(catalogoVistaKey , 1, idAzienda));
		iListinoAcq.setKey(KeyHelper.replaceTokenObjectKey(listinoAcqKey , 1, idAzienda));
		iListinoVen.setKey(KeyHelper.replaceTokenObjectKey(listinoVenKey , 1, idAzienda));
	}


	//CreaAlbero

	//getAttributeListe(): tell for the parameter(idSetting) the list of Attributes column Name defined in the corresponding Setting

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List getAttributesListe()
	{
		List listAtt = new ArrayList();
		try
		{
			if(this.getTipoVista()== CatalogoVista.CLASSIFICAZIONE_UTENTE) //utente
			{
				CatalogoVista catVist = this.getCatalogoVista();
				if(catVist!=null)
				{
					Setting setting = catVist.getSettingVista();
					setting.downloadSetting();
					Vector  listAD = setting.getSelectedAttributes();

					for (int i=0;i<listAD.size();i++)
					{
						listAtt.add(((ClassAD)listAD.get(i)).getColumnName());
					}
				}
			}
			else
			{
				listAtt.add(0,"ID_UNSPSC_SEG");
				listAtt.add(1,"ID_UNSPSC_FAM");
				listAtt.add(2,"ID_UNSPSC_CLS");
				listAtt.add(3,"ID_UNSPSC_MRC");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return listAtt;
	}

	/*
	  return the column descrizione from the static Table attributeInfo;
	 */

	public String getDescriptionColumnName (String columnName)
	{
		return ((AttributeInfo)attributeInfomap.get(columnName)).getDescription();
	}

	/*
	  return the alias from the static Table attributeInfo;
	 */

	public String getAlias (String columnName)
	{
		return ((AttributeInfo)attributeInfomap.get(columnName)).getAlias();
	}

	/*
	  return the TableName from the static Table attributeInfo;
	 */

	public String getTableName (String columnName)
	{
		return ((AttributeInfo)attributeInfomap.get(columnName)).getTableName();
	}

	/*
	  return the IdReferencedTable from the static Table attributeInfo;
	 */

	public String getIdReferencedTable (String columnName)
	{
		return ((AttributeInfo)attributeInfomap.get(columnName)).getIdReferenced();
	}

	/*
	    return the ViewName from the DestinVistaCatal
	 */

	public String getViewName(String idVistaCatal)
	{
		try
		{
			String idAzienda = getIdAzienda();
			String key = KeyHelper.buildObjectKey(new String[]{idAzienda,idVistaCatal});
			CatalogoVista catVist = (CatalogoVista)CatalogoVista.elementWithKey(CatalogoVista.class, key, PersistentObject.NO_LOCK );
			if(catVist!=null)
			{
				if(catVist.getDestinVistaCatal()== CatalogoVista.ARTICOLI)
					return SCHEMA + "CATALOGO_ART_V01";
				if(catVist.getDestinVistaCatal()== CatalogoVista.LISTINO_ACQUISTI)
					return SCHEMA + "CATAL_LST_ACQ_V01";
				if(catVist.getDestinVistaCatal()== CatalogoVista.LISTINO_VENDITA)
					return SCHEMA + "CATAL_LST_VEN_V01";
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/*
	    return the condition of the setting vista of the vistacatal selected.

	 */

	@SuppressWarnings({ "unused", "rawtypes" })
	public String getCondSett(String idVistaCatal)
	{
		List listSettingCrit = new ArrayList();
		try
		{
			CatalogoVista catVist = getCatalogoVista();
			if(catVist!=null)
			{
				Setting setting = catVist.getSettingVista();
				if(setting!=null)
				{
					setting.downloadSetting();
					return setting.getWhereSQLString();
				}
				return null;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;

	}


	/*
	   return the tipoVistaof the vistacatal selected : Utente or UNSPSC.
	 */
	public char getTipoVista()
	{
		CatalogoVista catVist = getCatalogoVista();
		if(catVist!=null)
			return catVist.getTipoVistaCatalogo();
		return '0';
	}

	/*
	    return the Distinazione of the vistacatal selected : article,list_Ven,List_Acq.
	 */
	public char getDistinazione()
	{
		CatalogoVista catVist = getCatalogoVista();
		if(catVist!=null)
			return catVist.getDestinVistaCatal();
		return '0';
	}

	/*
	  return a select query dfined by the list of the attributes
	  and the cooresponding View from where we execute this query.
	 */

	@SuppressWarnings("rawtypes")
	public String getQueryString(List listAttr, String idVistaSetting, String idListino, java.sql.Date data)
	{
		String rigaSelect ="";
		String rigaFrom ="";
		String rigaFrombis ="";
		String rigaJoin ="";
		String rigaOrderBy = "";
		for (int i=0;i<listAttr.size();i++)
		{
			String  attrColmn = listAttr.get(i).toString();
			if(i==0)
			{
				if(listAttr.size()>1) //120702
				{
					rigaSelect = "SELECT " + getAlias(attrColmn) + "." + getDescriptionColumnName(attrColmn) + "," + "c." + attrColmn + "," ;
					rigaFrom = " FROM(SELECT DISTINCT ID_AZIENDA," + attrColmn + ",";
					rigaOrderBy = " ORDER BY " + getAlias(attrColmn) + "." + getDescriptionColumnName(attrColmn)+ "," ;
				}
				else
				{
					rigaSelect = "SELECT " + getAlias(attrColmn) + "." + getDescriptionColumnName(attrColmn) + "," + "c." + attrColmn ;
					rigaFrom = " FROM(SELECT DISTINCT ID_AZIENDA," + attrColmn ;
					rigaOrderBy = " ORDER BY " + getAlias(attrColmn) + "." + getDescriptionColumnName(attrColmn);
				}

			}
			else
			{
				if(i == listAttr.size()-1)
				{
					rigaSelect =  rigaSelect + getAlias(attrColmn) + "." + getDescriptionColumnName(attrColmn) + "," + "c." + attrColmn;
					rigaFrom = rigaFrom + attrColmn;
					rigaOrderBy = rigaOrderBy + getAlias(attrColmn) + "." + getDescriptionColumnName(attrColmn);
				}
				else
				{
					rigaSelect = rigaSelect + getAlias(attrColmn) + "." + getDescriptionColumnName(attrColmn) + "," + "c." + attrColmn + ",";
					rigaFrom = rigaFrom + attrColmn + ",";
					rigaOrderBy = rigaOrderBy + getAlias(attrColmn) + "." + getDescriptionColumnName(attrColmn) + ",";
				}
			}

			rigaJoin = rigaJoin + " LEFT OUTER JOIN " + getTableName(attrColmn) +" "+ getAlias(attrColmn) + " ON c.ID_AZIENDA = " + getAlias(attrColmn) + ".ID_AZIENDA AND c." + attrColmn + "=" + getAlias(attrColmn) + "." + getIdReferencedTable(attrColmn);

		}

		if(getDistinazione()==CatalogoVista.ARTICOLI)
		{
			if(getCondSett(idVistaSetting)!=null && getCondSett(idVistaSetting)!="")
				rigaFrombis = " FROM " + getViewName(idVistaSetting) + " WHERE ID_AZIENDA = '" + getIdAzienda() + "' AND " + getCondSett(idVistaSetting) +")c";

			else
				rigaFrombis = " FROM " + getViewName(idVistaSetting) + " WHERE ID_AZIENDA = '" + getIdAzienda() + "')c";

		}
		else
		{
			if(getData()!=null && getData().toString()!="")
			{
				if(getCondSett(idVistaSetting)!=null && getCondSett(idVistaSetting)!=""){
					// Fix 15858 inizio
					//rigaFrombis = " FROM " + getViewName(idVistaSetting) + " WHERE ID_AZIENDA = '" + getIdAzienda() + "' AND " + getCondSett(idVistaSetting) + " AND ID_LISTINO ='" + idListino + "' AND '" + data + "' BETWEEN DATA_INZ_VALID AND DATA_FIN_VALID)c";
					Database db = ConnectionManager.getCurrentDatabase();
					String dateValue = db.getLiteral(data);
					rigaFrombis = " FROM " + getViewName(idVistaSetting) + " WHERE ID_AZIENDA = '" + getIdAzienda() + "' AND " + getCondSett(idVistaSetting) + " AND ID_LISTINO ='" + idListino + "' AND " + dateValue + " BETWEEN DATA_INZ_VALID AND DATA_FIN_VALID)c";
					// Fix 15858 fine
				}
				else{
					// Fix 15858 inizio
					//rigaFrombis = " FROM " + getViewName(idVistaSetting) + " WHERE ID_AZIENDA = '" + getIdAzienda() + "' AND ID_LISTINO ='" + idListino + "' AND '" + data + "' BETWEEN DATA_INZ_VALID AND DATA_FIN_VALID)c";
					Database db = ConnectionManager.getCurrentDatabase();
					String dateValue = db.getLiteral(data);
					rigaFrombis = " FROM " + getViewName(idVistaSetting) + " WHERE ID_AZIENDA = '" + getIdAzienda() + "' AND ID_LISTINO ='" + idListino + "' AND " + dateValue + " BETWEEN DATA_INZ_VALID AND DATA_FIN_VALID)c";
					// Fix 15858 fine
				}
			}
			else
			{
				if(getCondSett(idVistaSetting)!=null && getCondSett(idVistaSetting)!="")
					rigaFrombis = " FROM " + getViewName(idVistaSetting) + " WHERE ID_AZIENDA = '" + getIdAzienda() + "' AND" + getCondSett(idVistaSetting) + " AND ID_LISTINO ='" + idListino + "')c";

				else
					rigaFrombis = " FROM " + getViewName(idVistaSetting) + " WHERE ID_AZIENDA = '" + getIdAzienda() + "' AND ID_LISTINO ='" + idListino + "')c";
			}

		}

		//Mekki 25/10/02
		if(!getOrderByClause(idVistaSetting).equals(""))
			return rigaSelect  + ",c.ID_AZIENDA" + rigaFrom + rigaFrombis + rigaJoin + getOrderByClause(idVistaSetting);
		else
			return rigaSelect  + ",c.ID_AZIENDA" + rigaFrom + rigaFrombis + rigaJoin + rigaOrderBy;
	}

	/*
	   Excute Query
	 */
	@SuppressWarnings("rawtypes")
	public  ResultSet exexcuteQueryUtente(List listAttr, String idSetting, String idListino,java.sql.Date data)
	{
		ResultSet rst = null;
		try
		{
			String query = getQueryString(listAttr,idSetting,idListino,data);
			//System.out.println("getQueryString==="+query);
			Statement stmt = ConnectionManager.getCurrentConnection().createStatement() ;
			rst = stmt.executeQuery(query);
			return rst;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public synchronized ResultSet exexcuteQueryUNArt(List listAttr, String idSetting)
	{
		try
		{
			//System.out.println("getQueryString==="+getQueryUN(idSetting));
			//if(cpstmtArt == null)
			// {
			cpstmtArt = ConnectionManager.getCurrentConnection().prepareStatement(getQueryUN(idSetting));
			// }
			Database db = ConnectionManager.getCurrentDatabase(); //Fix 6976
			db.setString(cpstmtArt,1,getIdAzienda()); //Fix 6976
			return cpstmtArt.executeQuery();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public synchronized ResultSet exexcuteQueryUNVen(List listAttr, String idSetting, String idListino)
	{
		try
		{
			//System.out.println("getQueryString==="+getQueryUN(idSetting));
			//if(cpstmtVen == null)
			// {
			cpstmtVen = ConnectionManager.getCurrentConnection().prepareStatement(getQueryUN(idSetting));
			// }
			Database db = ConnectionManager.getCurrentDatabase(); //Fix 6976
			db.setString(cpstmtVen,1,getIdAzienda());//Fix 6976
			db.setString(cpstmtVen,2,idListino);//Fix 6976
			return cpstmtVen.executeQuery();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public synchronized ResultSet exexcuteQueryUNAcq(List listAttr, String idSetting, String idListino)
	{
		try
		{
			//System.out.println("getQueryString==="+getQueryUN(idSetting));
			//if(cpstmtAcq == null)
			// {
			cpstmtAcq = ConnectionManager.getCurrentConnection().prepareStatement(getQueryUN(idSetting));
			// }
			Database db = ConnectionManager.getCurrentDatabase(); //Fix 6976
			db.setString(cpstmtAcq, 1,getIdAzienda()); //Fix 6976
			db.setString(cpstmtAcq, 2,idListino); //Fix 6976
			return cpstmtAcq.executeQuery(); //Fix 6976
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}


	@SuppressWarnings("rawtypes")
	public synchronized ResultSet exexcuteQueryUNCatalEst(List listAttr, String idSetting)
	{
		try
		{
			//System.out.println("getQueryString==="+getQueryUN(idSetting));
			// if(cpstmtCatalEst == null)
			// {
			cpstmtCatalEst = ConnectionManager.getCurrentConnection().prepareStatement(getQueryUN(idSetting));
			// }
			Database db = ConnectionManager.getCurrentDatabase(); //Fix 6976
			db.setString(cpstmtCatalEst,1,getIdAzienda()); //Fix 6976
			return cpstmtCatalEst.executeQuery();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public synchronized ResultSet exexcuteQueryUNListEst(List listAttr, String idSetting)
	{
		try
		{
			//System.out.println("getQueryString==="+getQueryUN(idSetting));
			//if(cpstmtListEst == null)
			// {
			cpstmtListEst = ConnectionManager.getCurrentConnection().prepareStatement(getQueryUN(idSetting));
			// }
			Database db = ConnectionManager.getCurrentDatabase(); //Fix 6976
			db.setString(cpstmtListEst,1,getIdAzienda()); //Fix 6976
			return cpstmtListEst.executeQuery();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}


	/*
	  internalCreateTree(): resultset------> Tree
	 */

	/*
	  Ajouter un element(des infos de resultset) dans le father correspondant : cas utente
	 */

	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	public CatalogoNavigatoreNodo ajoutNodoUtente(int idNodo,String descrip,String cond,CatalogoNavigatoreNodo father)
	{
		List nodoFigliList = father.getNodiFigli();
		CatalogoNavigatoreNodo catNavNodExist = null;
		CatalogoNavigatoreNodo catNavNodo = null;
		String condResult = "";
		boolean dejaAjouter = false;
		boolean existe = false;
		if(father.getCondizione()!="")
			condResult = father.getCondizione()+SEPARATOR+cond;
		else
			condResult = cond;


		for(int i=0;i<nodoFigliList.size();i++)
		{
			if(((CatalogoNavigatoreNodo)nodoFigliList.get(i)).getDescrizione().equals(descrip))
			{
				catNavNodExist = (CatalogoNavigatoreNodo)nodoFigliList.get(i);
				existe = true;
			}
		}

		if(existe == false)
		{
			catNavNodo = new CatalogoNavigatoreNodo(idNodo,condResult,descrip);
			father.getNodiFigli().add(catNavNodo);
			return catNavNodo;
		}
		else
			return catNavNodExist;


	}

	/*
	  Ajouter un element(des infos de resultset) dans le father correspondant :Cas Internationnal
	 */

	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	public CatalogoNavigatoreNodo ajoutNodoUN(int idNodo,String descrip,String cond,CatalogoNavigatoreNodo father)
	{
		List nodoFigliList = father.getNodiFigli();
		CatalogoNavigatoreNodo catNavNodExist = null;
		CatalogoNavigatoreNodo catNavNodo = null;
		String condResult = "";
		boolean dejaAjouter = false;
		boolean existe = false;
		String fatherCond = "";
		if(father.getCondizione()!="")
		{
			fatherCond=father.getCondizione().substring(0,father.getCondizione().length()-1);
			condResult = fatherCond+cond+"%";
		}
		else
		{
			condResult = cond+"%";
		}

		for(int i=0;i<nodoFigliList.size();i++)
		{
			if(((CatalogoNavigatoreNodo)nodoFigliList.get(i)).getDescrizione().equals(descrip))
			{
				catNavNodExist = (CatalogoNavigatoreNodo)nodoFigliList.get(i);
				existe = true;
			}
		}

		if(existe == false)
		{
			catNavNodo = new CatalogoNavigatoreNodo(idNodo,condResult,descrip);
			father.getNodiFigli().add(catNavNodo);
			return catNavNodo;
		}
		else
			return catNavNodExist;




	}


	@SuppressWarnings({ "unused", "rawtypes" })
	public CatalogoNavigatoreNodo internalCreateTree(ResultSet rst, List listAttr, String idCatalogVista)
	{
		String descAtt = "";
		String idAtt = "";
		String descAttCol = "";
		String idAttCol = "";
		String cond = "";
		CatalogoNavigatoreNodo rootNodo = new CatalogoNavigatoreNodo(0,"","DescrizioneRoot");
		CatalogoNavigatoreNodo father = new CatalogoNavigatoreNodo();
		try
		{
			if(this.getTipoVista() == CatalogoVista.CLASSIFICAZIONE_UTENTE)//utente
			{
				while(rst.next())
				{
					father = rootNodo;
					int j=0;
					for (int i=1;i<(listAttr.size()*2);i++)
					{
						j++;
						idAttCol =  listAttr.get(i/2).toString();
						descAtt = rst.getString(i);
						i++;
						if(descAtt!=null)
						{
							idAtt = rst.getString(i);
							cond = (idAttCol + SEPARATOR + "=" + SEPARATOR +  idAtt.trim()).trim();
							father = ajoutNodoUtente(j,descAtt,cond,father);
						}
						else
						{
							i=listAttr.size()*2;
						}
					}
				}
			}
			else //UN
			{
				while(rst.next())
				{
					father = rootNodo;
					int j=0;
					for (int i=1;i<(listAttr.size()*2);i++)
					{
						j++;
						descAtt = rst.getString(i);
						i++;
						if(descAtt!=null)
						{
							idAtt = rst.getString(i);
							if(i==2)
							{
								cond = "COD_INTERNAZ"+SEPARATOR+"LIKE"+SEPARATOR+idAtt;
							}
							else
							{
								cond = idAtt;
							}
							father = ajoutNodoUN(j,descAtt,cond,father);
						}
						else
						{
							i=listAttr.size()*2;
						}

					}
				}
			}
			rst.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return rootNodo;
	}


	@SuppressWarnings("rawtypes")
	public CatalogoNavigatoreNodo creaAlbero()
	{
		ResultSet rst = null;
		List listAttr =  getAttributesListe();
		if(getCatalogoVista().getDestinVistaCatal()==CatalogoVista.LISTINO_ACQUISTI)
		{
			if(getCatalogoVista().getTipoVistaCatalogo() == CatalogoVista.CLASSIFICAZIONE_UTENTE)
				rst = this.exexcuteQueryUtente(listAttr,this.getIdCatalogoVista(),this.getIdListinoAcq(),getData());
			else
				rst = this.exexcuteQueryUNAcq(listAttr,this.getIdCatalogoVista(),this.getIdListinoAcq());
		}
		else
		{
			if(getCatalogoVista().getDestinVistaCatal()==CatalogoVista.LISTINO_VENDITA)
			{
				if(getCatalogoVista().getTipoVistaCatalogo() == CatalogoVista.CLASSIFICAZIONE_UTENTE)
					rst = this.exexcuteQueryUtente(listAttr,this.getIdCatalogoVista(),this.getIdListinoVen(),getData());
				else
					rst = this.exexcuteQueryUNVen(listAttr,this.getIdCatalogoVista(),this.getIdListinoVen());
			}
			else
			{
				if(getCatalogoVista().getDestinVistaCatal()==CatalogoVista.ARTICOLI) //040203
				{
					if(getCatalogoVista().getTipoVistaCatalogo() == CatalogoVista.CLASSIFICAZIONE_UTENTE)
						rst = this.exexcuteQueryUtente(listAttr,this.getIdCatalogoVista(),"",getData());
					else
						rst = this.exexcuteQueryUNArt(listAttr,this.getIdCatalogoVista());
				}
				else
				{
					if(getCatalogoVista().getDestinVistaCatal()==CatalogoVista.CATALOGO_ESTERNO) //040203
						rst = this.exexcuteQueryUNCatalEst(listAttr,this.getIdCatalogoVista());
					else  //040203
						rst = this.exexcuteQueryUNListEst(listAttr,this.getIdCatalogoVista());
				}
			}


		}

		CatalogoNavigatoreNodo treeRoot = internalCreateTree(rst,listAttr,getIdCatalogoVista());
		setNodoRadice(treeRoot);
		return treeRoot;
	}


	//Charger la table des informations statiques sur un attribute de la liste.

	@SuppressWarnings("unchecked")
	public void fillStaticTable()
	{
		AttributeInfo attInfo1 = new AttributeInfo("R_LINEA_COMMER",SCHEMA + "LINEE_COMMER","lc","ID_LINEA_COMMER","DESCRIZIONE");
		attributeInfomap.put(attInfo1.getColName(),attInfo1);
		AttributeInfo attInfo2 = new AttributeInfo("R_MACROFAMIGLIA",SCHEMA + "MACROFAMIGLIE","maf","ID_MACROFAMIGLIA","DESCRIZIONE");
		attributeInfomap.put(attInfo2.getColName(),attInfo2);
		AttributeInfo attInfo3 = new AttributeInfo("R_MICROFAMIGLIA",SCHEMA + "MICROFAMIGLIE","mif","ID_MICROFAMIGLIA","DESCRIZIONE");
		attributeInfomap.put(attInfo3.getColName(),attInfo3);
		AttributeInfo attInfo4 = new AttributeInfo("R_SUBFAMIGLIA",SCHEMA + "SUBFAMIGLIE","sf","ID_SUBFAMIGLIA","DESCRIZIONE");
		attributeInfomap.put(attInfo4.getColName(),attInfo4);
		AttributeInfo attInfo5 = new AttributeInfo("R_LINEA_PROD",SCHEMA + "LINEE_PRODOTTO","lp","ID_LINEA_PROD","DESCRIZIONE");
		attributeInfomap.put(attInfo5.getColName(),attInfo5);
		AttributeInfo attInfo6 = new AttributeInfo("R_CAT_PRZ",SCHEMA + "CAT_PREZZI","cp","ID_CAT_PRZ","DESCRIZIONE");
		attributeInfomap.put(attInfo6.getColName(),attInfo6);
		AttributeInfo attInfo7 = new AttributeInfo("R_PIANIFICATORE",SCHEMA + "PIANIFICATORI","pf","ID_PIANIFICATORE","DESCRIZIONE");
		attributeInfomap.put(attInfo7.getColName(),attInfo7);
		AttributeInfo attInfo8 = new AttributeInfo("R_CLASSE_A",SCHEMA + "CLASSI_A","ca","ID_CLASSE_A","DESCRIZIONE");
		attributeInfomap.put(attInfo8.getColName(),attInfo8);
		AttributeInfo attInfo9 = new AttributeInfo("R_CLASSE_B",SCHEMA + "CLASSI_B","cb","ID_CLASSE_B","DESCRIZIONE");
		attributeInfomap.put(attInfo9.getColName(),attInfo9);
		AttributeInfo attInfo10 = new AttributeInfo("R_CLASSE_C",SCHEMA + "CLASSI_C","cc","ID_CLASSE_C","DESCRIZIONE");
		attributeInfomap.put(attInfo10.getColName(),attInfo10);
		AttributeInfo attInfo11 = new AttributeInfo("R_CLASSE_D",SCHEMA + "CLASSI_D","cd","ID_CLASSE_D","DESCRIZIONE");
		attributeInfomap.put(attInfo11.getColName(),attInfo11);
		AttributeInfo attInfo12 = new AttributeInfo("R_CLASSE_E",SCHEMA + "CLASSI_E","ce","ID_CLASSE_E","DESCRIZIONE");
		attributeInfomap.put(attInfo12.getColName(),attInfo12);
		AttributeInfo attInfo13 = new AttributeInfo("R_CLASSE_MERCLG",SCHEMA + "CLASSI_MERCLG","cme","ID_CLASSE_MERCLG","DESCRIZIONE");
		attributeInfomap.put(attInfo13.getColName(),attInfo13);
		AttributeInfo attInfo14 = new AttributeInfo("R_CLS_MATERIALE",SCHEMA + "CLS_MATERIALI","cma","ID_CLS_MATERIALE","DESCRIZIONE");
		attributeInfomap.put(attInfo14.getColName(),attInfo14);
		AttributeInfo attInfo15 = new AttributeInfo("ID_ARTICOLO",SCHEMA + "ARTICOLI","art","ID_ARTICOLO","DESCRIZIONE");
		attributeInfomap.put(attInfo15.getColName(),attInfo15);
	}



	//inner calss

	public class AttributeInfo
	{
		String iColName;
		String iTableName;
		String iAlias;
		String iIdReferenced;
		String iDescription;

		public AttributeInfo(String colName,String tableName,String alias,String idReferenced,String description)
		{
			iColName=colName;
			iTableName=tableName;
			iAlias=alias;
			iIdReferenced=idReferenced;
			iDescription=description;

		}

		public String getColName()
		{
			return iColName;
		}

		public void setColName(String colName)
		{
			this.iColName = colName;
		}

		public String getTableName()
		{
			return iTableName;
		}

		public void setTableName(String tableName)
		{
			this.iTableName = tableName;
		}

		public String getAlias()
		{
			return iAlias;
		}

		public void setAlias(String alias)
		{
			this.iAlias = alias;
		}

		public String getIdReferenced()
		{
			return iIdReferenced;
		}

		public void setIdReferenced(String idReferenced)
		{
			this.iIdReferenced = idReferenced;
		}

		public String getDescription()
		{
			return iDescription;
		}

		public void setDescription(String description)
		{
			this.iDescription = description;
		}

	}

	@SuppressWarnings("rawtypes")
	public List getNodiFigli()
	{
		return getNodoRadice().getNodiFigli();
	}


	//Mekki 25/10/02
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String getOrderByClause(String idVistaCatal)
	{

		List listOrderBy = new ArrayList();
		String OrderByClause = "";
		Integer idSetting = null;
		try
		{
			String idAzienda = getIdAzienda();
			String key = KeyHelper.buildObjectKey(new String[]{idAzienda,idVistaCatal});
			CatalogoVista catVist = (CatalogoVista)CatalogoVista.elementWithKey(CatalogoVista.class, key, PersistentObject.NO_LOCK );
			if(catVist!=null)
			{
				idSetting = catVist.getIdSettingVista();
				Setting setting = Setting.elementWithKey(KeyHelper.objectToString(idSetting),PersistentObject.NO_LOCK);
				setting.downloadSetting();
				Vector listOrderByAD = setting.getOrderByItems();
				for (int i=0;i<listOrderByAD.size();i++)
				{
					listOrderBy.add(((ClassAD)((OrderByItem)listOrderByAD.get(i)).getAttribute()).getColumnName());
				}
				for (int i=0;i<listOrderBy.size();i++)
				{
					String  attrColmn = listOrderBy.get(i).toString();
					if(i==0)
					{
						OrderByClause = " ORDER BY " + getAlias(attrColmn) + "." + getDescriptionColumnName(attrColmn);
					}
					else
					{
						OrderByClause = OrderByClause + ", " + getAlias(attrColmn) + "." + getDescriptionColumnName(attrColmn);
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return OrderByClause;
	}


	public char getTipoProfilo()
	{
		char tipoProfilo = Profilo.UTENTE_ANONIMO;
		UtenteAzienda currentUtenteAzienda = ((ThipUser)Security.getCurrentUser()).getUtenteAzienda();

		if (currentUtenteAzienda != null)
			tipoProfilo = currentUtenteAzienda.getTipoProfilo();

		return tipoProfilo;
	}

	public char getRestrictUsoVistaCatalogo()
	{
		switch (getTipoProfilo())
		{
		case Profilo.UTENTE_AZIENDA_CLIENTE:
		case Profilo.UTENTE_AZIENDA_FORNITORE:
		case Profilo.UTENTE_AZIENDA_GENERICA:
		case Profilo.UTENTE_GENERICA:
		case Profilo.UTENTE_ANONIMO:
			return CatalogoVista.ANCHE_ESTERNO;
		default:
			return CatalogoVista.SOLO_INTERNO;
		}
	}


	@SuppressWarnings("unused")
	public String getQueryUN(String idSetting)
	{
		String QueryArticolo = null;
		String QueryLisVen = null;
		String QueryLisAcq = null;
		String QueryCatalEsterno = null;
		String QueryLisEsterno = null;

		if(getDistinazione()==CatalogoVista.ARTICOLI)
		{
			if(getCondSett(idSetting)!=null && getCondSett(idSetting)!="")
			{
				return QueryArticolo = QueryArticolo1 + " AND " + getCondSett(idSetting) + QueryArticolo2;
			}
			else
			{
				return QueryArticolo = QueryArticolo1 + QueryArticolo2;
			}
		}
		else
		{

			if(getDistinazione()==CatalogoVista.CATALOGO_ESTERNO) //040203
			{
				if(getCondSett(idSetting)!=null && getCondSett(idSetting)!="")
				{
					return QueryCatalEsterno = QueryCatalEsterno1 + " AND " + getCondSett(idSetting) + QueryCatalEsterno2;
				}
				else
				{
					return QueryCatalEsterno = QueryCatalEsterno1 + QueryCatalEsterno2;
				}

			}
			else  //040203
			{

				if(getData()!=null && getData().toString()!="")
				{
					if(getCondSett(idSetting)!=null && getCondSett(idSetting)!="")
					{
						if(getDistinazione()==CatalogoVista.LISTINO_VENDITA){
							// Fix 15858 inizio
							//return QueryLisVen = QueryLisVen1 + " AND " + getCondSett(idSetting) + " AND '" + getData() + "' BETWEEN DATA_INZ_VALID AND DATA_FIN_VALID" +QueryLisVen2;
							Database db = ConnectionManager.getCurrentDatabase();
							String dateValue = db.getLiteral(getData());
							return QueryLisVen = QueryLisVen1 + " AND " + getCondSett(idSetting) + " AND " + dateValue + " BETWEEN DATA_INZ_VALID AND DATA_FIN_VALID" +QueryLisVen2;
							// Fix 15858 fine
						}
						else
							if(getDistinazione()==CatalogoVista.LISTINO_ACQUISTI) {//040203
								// Fix 15858 inizio
								//return QueryLisAcq = QueryLisAcq1 + " AND " + getCondSett(idSetting) + " AND '" + getData() + "' BETWEEN DATA_INZ_VALID AND DATA_FIN_VALID" + QueryLisAcq2;
								Database db = ConnectionManager.getCurrentDatabase();
								String dateValue = db.getLiteral(getData());
								return QueryLisAcq = QueryLisAcq1 + " AND " + getCondSett(idSetting) + " AND " + dateValue + " BETWEEN DATA_INZ_VALID AND DATA_FIN_VALID" + QueryLisAcq2;
								// Fix 15858 fine
							}
							else {//040203
								// Fix 15858 inizio
								//return QueryLisEsterno = QueryLisEsterno1 + " AND " + getCondSett(idSetting) + " AND '" + getData() + "' BETWEEN DATA_INZ_VALID AND DATA_FINE_VALID" + QueryLisEsterno2;
								Database db = ConnectionManager.getCurrentDatabase();
								String dateValue = db.getLiteral(getData());
								return QueryLisEsterno = QueryLisEsterno1 + " AND " + getCondSett(idSetting) + " AND " + dateValue + " BETWEEN DATA_INZ_VALID AND DATA_FINE_VALID" + QueryLisEsterno2;
								// Fix 15858 fine
							}
					}
					else
					{
						if(getDistinazione()==CatalogoVista.LISTINO_VENDITA) {
							// Fix 15858 inizio
							//return QueryLisVen = QueryLisVen1 + " AND '" + getData() + "' BETWEEN DATA_INZ_VALID AND DATA_FIN_VALID" + QueryLisVen2;
							Database db = ConnectionManager.getCurrentDatabase();
							String dateValue = db.getLiteral(getData());
							return QueryLisVen = QueryLisVen1 + " AND " + dateValue + " BETWEEN DATA_INZ_VALID AND DATA_FIN_VALID" + QueryLisVen2;
							// Fix 15858 fine
						}
						else
							if(getDistinazione()==CatalogoVista.LISTINO_ACQUISTI) { //040203
								// Fix 15858 inizio
								//return QueryLisAcq = QueryLisAcq1 + " AND '" + getData() + "' BETWEEN DATA_INZ_VALID AND DATA_FIN_VALID" + QueryLisAcq2;
								Database db = ConnectionManager.getCurrentDatabase();
								String dateValue = db.getLiteral(getData());
								return QueryLisAcq = QueryLisAcq1 + " AND " + dateValue + " BETWEEN DATA_INZ_VALID AND DATA_FIN_VALID" + QueryLisAcq2;
								// Fix 15858 fine
							}
							else {//040203
								// Fix 15858 inizio
								//return QueryLisEsterno = QueryLisEsterno1 + " AND '" + getData() + "' BETWEEN DATA_INZ_VALID AND DATA_FINE_VALID" + QueryLisEsterno2;
								Database db = ConnectionManager.getCurrentDatabase();
								String dateValue = db.getLiteral(getData());
								return QueryLisEsterno = QueryLisEsterno1 + " AND " + dateValue + " BETWEEN DATA_INZ_VALID AND DATA_FINE_VALID" + QueryLisEsterno2;
								// Fix 15858 fine
							}
					}
				}
				else
				{
					if(getCondSett(idSetting)!=null && getCondSett(idSetting)!="")
					{
						if(getDistinazione()==CatalogoVista.LISTINO_VENDITA)
							return QueryLisVen = QueryLisVen1 + " AND " + getCondSett(idSetting) + QueryLisVen2;
						else
							if(getDistinazione()==CatalogoVista.LISTINO_ACQUISTI) //040203
								return QueryLisAcq = QueryLisAcq1 + " AND " + getCondSett(idSetting) + QueryLisAcq2;
							else //040203
								return QueryLisEsterno = QueryLisEsterno1 + " AND " + getCondSett(idSetting) + QueryLisEsterno2;
					}
					else
					{
						if(getDistinazione()==CatalogoVista.LISTINO_VENDITA)
							return  QueryLisVen = QueryLisVen1 + QueryLisVen2;
						else
							if(getDistinazione()==CatalogoVista.LISTINO_ACQUISTI) //040203
								return QueryLisAcq = QueryLisAcq1 + QueryLisAcq2;
							else //040203
								return QueryLisEsterno = QueryLisEsterno1 + QueryLisEsterno2;

					}
				}
			}//040203
		}
	}

	public ErrorMessage checkData()
	{
		if(this.getCatalogoVista().getDestinVistaCatal() == CatalogoVista.ARTICOLI || this.getCatalogoVista().getDestinVistaCatal() == CatalogoVista.CATALOGO_ESTERNO)
			if (getData() != null && getData().toString().trim().length() > 0)
				return new ErrorMessage("THIP_TN370");
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ErrorMessage checkIdCatalogoVista()
	{
		if (getIdCatalogoVista() == null)
			return new ErrorMessage("BAS0000000");

		if (getCatalogoVista() == null)
		{
			Vector parameters = new Vector();
			parameters.addElement(KeyHelper.formatKeyString(getCatalogoVistaKey()));
			return new ErrorMessage("BAS0000004", parameters);
		}

		if ((getRestrictUsoVistaCatalogo() == CatalogoVista.ANCHE_ESTERNO) &&(getCatalogoVista().getUsoVistaCatalogo() != CatalogoVista.ANCHE_ESTERNO))
			return new ErrorMessage("THIP_TN493");

		return null;
	}
}
