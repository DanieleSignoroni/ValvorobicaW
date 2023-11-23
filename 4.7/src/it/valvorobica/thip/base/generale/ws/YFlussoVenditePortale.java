package it.valvorobica.thip.base.generale.ws;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.thera.thermfw.base.SystemParam;
import com.thera.thermfw.persist.CachedStatement;
import com.thera.thermfw.persist.Column;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.Database;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;

import it.thera.thip.acquisti.ordineAC.OrdineAcquistoRigaTM;
import it.thera.thip.base.comuniVenAcq.DdtRigTM;
import it.thera.thip.base.comuniVenAcq.DdtTes;
import it.thera.thip.produzione.ordese.OrdineEsecutivoTM;
import it.thera.thip.vendite.documentoVE.DocumentoVenditaRigaTM;
import it.thera.thip.vendite.documentoVE.DocumentoVenditaTM;
import it.thera.thip.vendite.documentoVE.FatturaVenditaTM;
import it.thera.thip.vendite.documentoVE.FatvenDocvenTM;
import it.thera.thip.vendite.offerteCliente.OffertaClienteRigaTM;
import it.thera.thip.vendite.ordineVE.OrdineVenditaRigaTM;
import it.thera.thip.ws.GenRequestJSON;
import it.valvorobica.thip.vendite.ordineVE.YOrdineVendita;
import it.valvorobica.thip.vendite.ordineVE.YOrdineVenditaRigaPrm;

/**
 * <h1>Softre Solutions</h1> <br>
 * <h2>ID WS = 'YFLVP'</h2>
 * 
 * @version 1.0
 * @author Daniele Signoroni 10/11/2023 <br>
 *         <br>
 *         <b></b>
 *         <p>
 *         Replica del flusso vendite nel portale.<br>
 *         Per ora replicato solo il flusso Ordine Vendita --> DDT.
 *         </p>
 */

public class YFlussoVenditePortale extends GenRequestJSON {

	protected static final String OFF_VEN_RIG = OffertaClienteRigaTM.TABLE_NAME;
	protected static final String ORD_VEN_RIG = OrdineVenditaRigaTM.TABLE_NAME;
	protected static final String DOC_VEN_RIG = DocumentoVenditaRigaTM.TABLE_NAME_PRINCIPALE;
	protected static final String DOC_VEN = DocumentoVenditaTM.TABLE_NAME;
	protected static final String FATVEN = FatturaVenditaTM.TABLE_NAME;
	protected static final String ORDESE = OrdineEsecutivoTM.TABLE_NAME;
	protected static final String RIGACQ = OrdineAcquistoRigaTM.TABLE_NAME;
	protected static final String DDT_RIG = DdtRigTM.TABLE_NAME;
	protected static final String FATVEN_DOCVEN = FatvenDocvenTM.TABLE_NAME;

	protected static final String SELECT_RIGHE_OFF_DA_OFF = "SELECT " + OFF_VEN_RIG + ".ID_RIGA_OFF" + " FROM "
			+ OFF_VEN_RIG + "" + " WHERE " + OFF_VEN_RIG + ".ID_AZIENDA = ? AND" + " " + OFF_VEN_RIG
			+ ".ID_ANNO_OFF = ? AND" + " " + OFF_VEN_RIG + ".ID_NUMERO_OFF = ? AND" + " " + OFF_VEN_RIG
			+ ".ID_DET_RIGA_OFF = 0";
	protected static CachedStatement cRigheOffDaOff = new CachedStatement(SELECT_RIGHE_OFF_DA_OFF);

	protected static final String SELECT_RIGHE_ORD_DA_ORD = "SELECT " + ORD_VEN_RIG + ".ID_RIGA_ORD" + " FROM "
			+ ORD_VEN_RIG + "" + " WHERE " + ORD_VEN_RIG + ".ID_AZIENDA = ? AND" + " " + ORD_VEN_RIG
			+ ".ID_ANNO_ORD = ? AND" + " " + ORD_VEN_RIG + ".ID_NUMERO_ORD = ? AND" + " " + ORD_VEN_RIG
			+ ".ID_DET_RIGA_ORD = 0";
	protected static CachedStatement cRigheOrdDaOrd = new CachedStatement(SELECT_RIGHE_ORD_DA_ORD);

	protected static final String SELECT_RIGHE_DOC_DA_DOC = "SELECT " + DOC_VEN_RIG + ".ID_RIGA_DOC" + " FROM "
			+ DOC_VEN_RIG + "" + " WHERE " + DOC_VEN_RIG + ".ID_AZIENDA = ? AND" + " " + DOC_VEN_RIG
			+ ".ID_ANNO_DOC = ? AND" + " " + DOC_VEN_RIG + ".ID_NUMERO_DOC = ? AND" + " " + DOC_VEN_RIG
			+ ".ID_DET_RIGA_DOC = 0";
	protected static CachedStatement cRigheDocDaDoc = new CachedStatement(SELECT_RIGHE_DOC_DA_DOC);

	protected static final String SELECT_RIGHE_OFF_DA_RIGA_ORD = "SELECT " + ORD_VEN_RIG + ".R_ANNO_BOZZA, "
			+ ORD_VEN_RIG + ".R_NUMERO_BOZZA, " + ORD_VEN_RIG + ".R_RIGA_BOZZA" + " FROM " + ORD_VEN_RIG + " WHERE "
			+ ORD_VEN_RIG + ".ID_AZIENDA = ? AND" + " " + ORD_VEN_RIG + ".ID_ANNO_ORD = ? AND" + " " + ORD_VEN_RIG
			+ ".ID_NUMERO_ORD = ? AND" + " " + ORD_VEN_RIG + ".ID_RIGA_ORD = ? AND" + " " + ORD_VEN_RIG
			+ ".ID_DET_RIGA_ORD = 0 AND" + " " + ORD_VEN_RIG + ".R_NUMERO_BOZZA IS NOT NULL";
	protected static CachedStatement cRigheOffDaRigaOrd = new CachedStatement(SELECT_RIGHE_OFF_DA_RIGA_ORD);

	protected static final String SELECT_RIGHE_ORD_DA_RIGA_OFF = "SELECT " + ORD_VEN_RIG + ".ID_ANNO_ORD, "
			+ ORD_VEN_RIG + ".ID_NUMERO_ORD, " + ORD_VEN_RIG + ".ID_RIGA_ORD" + " FROM " + ORD_VEN_RIG + " WHERE "
			+ ORD_VEN_RIG + ".ID_AZIENDA = ? AND" + " " + ORD_VEN_RIG + ".R_ANNO_BOZZA = ? AND" + " " + ORD_VEN_RIG
			+ ".R_NUMERO_BOZZA = ? AND" + " " + ORD_VEN_RIG + ".R_RIGA_BOZZA = ? AND" + " " + ORD_VEN_RIG
			+ ".R_DET_RIGA_BOZZA = 0";
	protected static CachedStatement cRigheOrdDaRigaOff = new CachedStatement(SELECT_RIGHE_ORD_DA_RIGA_OFF);

	protected static final String SELECT_RIGHE_DOC_DA_RIGA_ORD = "SELECT " + DOC_VEN_RIG + ".ID_ANNO_DOC, "
			+ DOC_VEN_RIG + ".ID_NUMERO_DOC, " + DOC_VEN_RIG + ".ID_RIGA_DOC" + " FROM " + DOC_VEN_RIG + " WHERE "
			+ DOC_VEN_RIG + ".ID_AZIENDA = ? AND" + " " + DOC_VEN_RIG + ".R_ANNO_ORD = ? AND" + " " + DOC_VEN_RIG
			+ ".R_NUMERO_ORD = ? AND" + " " + DOC_VEN_RIG + ".R_RIGA_ORD = ? AND" + " " + DOC_VEN_RIG
			+ ".R_DET_RIGA_ORD = 0";
	protected static CachedStatement cRigheDocDaRigaOrd = new CachedStatement(SELECT_RIGHE_DOC_DA_RIGA_ORD);

	protected static final String SELECT_RIGHE_ORD_DA_RIGA_DOC = "SELECT " + DOC_VEN_RIG + ".R_ANNO_ORD, " + DOC_VEN_RIG
			+ ".R_NUMERO_ORD, " + DOC_VEN_RIG + ".R_RIGA_ORD" + " FROM " + DOC_VEN_RIG + " WHERE " + DOC_VEN_RIG
			+ ".ID_AZIENDA = ? AND" + " " + DOC_VEN_RIG + ".ID_ANNO_DOC = ? AND" + " " + DOC_VEN_RIG
			+ ".ID_NUMERO_DOC = ? AND" + " " + DOC_VEN_RIG + ".ID_RIGA_DOC = ? AND" + " " + DOC_VEN_RIG
			+ ".ID_DET_RIGA_DOC = 0 AND" + " " + DOC_VEN_RIG + ".R_NUMERO_ORD IS NOT NULL"; // Fix 15715
	protected static CachedStatement cRigheOrdDaRigaDoc = new CachedStatement(SELECT_RIGHE_ORD_DA_RIGA_DOC);

	protected static final String SELECT_FAT_DA_DOC = "SELECT DISTINCT ANNO_FAT, NUMERO_FATTURA " + " FROM "
			+ SystemParam.getSchema("THIP") + "DOC_VEN_TES_V10" + " WHERE ID_AZIENDA = ? AND" + " ID_ANNO_DOC = ? AND"
			+ " ID_NUMERO_DOC = ? AND" + " NUMERO_FATTURA IS NOT NULL";
	protected static CachedStatement cFatDaDoc = new CachedStatement(SELECT_FAT_DA_DOC);

	protected static final String SELECT_RIGHE_DOC_DA_FAT = "SELECT " + DOC_VEN_RIG + ".ID_ANNO_DOC, " + DOC_VEN_RIG
			+ ".ID_NUMERO_DOC, " + DOC_VEN_RIG + ".ID_RIGA_DOC" + " FROM " + DOC_VEN_RIG + " INNER JOIN "
			+ SystemParam.getSchema("THIP") + "DOC_VEN_TES_V10 V" + " ON " + DOC_VEN_RIG + ".ID_AZIENDA = "
			+ "V.ID_AZIENDA AND" + " " + DOC_VEN_RIG + ".ID_ANNO_DOC = " + "V.ID_ANNO_DOC AND" + " " + DOC_VEN_RIG
			+ ".ID_NUMERO_DOC = " + "V.ID_NUMERO_DOC" + " WHERE " + "V.ID_AZIENDA = ? AND" + " " + "V.ANNO_FAT = ? AND"
			+ " " + "V.NUMERO_FATTURA = ?";
	protected static CachedStatement cRigheDocDaFat = new CachedStatement(SELECT_RIGHE_DOC_DA_FAT);

	protected static final String SELECT_ORDESE_DA_RIGA_ORD = "SELECT " + ORDESE + ".ID_ANNO_ORD, " + ORDESE
			+ ".ID_NUMERO_ORD " + " FROM " + ORDESE + " WHERE " + ORDESE + ".ID_AZIENDA = ? AND" + " " + ORDESE
			+ ".R_ANNO_ORD_CLI = ? AND" + " " + ORDESE + ".R_NUMERO_ORD_CLI = ? AND" + " " + ORDESE
			+ ".R_RIGA_ORD_CLI = ? AND" + " " + ORDESE + ".R_DET_RIGA_ORD = 0";
	protected static CachedStatement cOrdeseDaRigaOrd = new CachedStatement(SELECT_ORDESE_DA_RIGA_ORD);

	protected static final String SELECT_RIGACQ_DA_RIGA_ORD = "SELECT " + RIGACQ + ".ID_ANNO_ORD, " + RIGACQ
			+ ".ID_NUMERO_ORD, " + RIGACQ + ".ID_RIGA_ORD" + " FROM " + RIGACQ + " WHERE " + RIGACQ
			+ ".ID_AZIENDA = ? AND" + " " + RIGACQ + ".R_ANNO_ORDC = ? AND" + " " + RIGACQ + ".R_NUMERO_ORDC = ? AND"
			+ " " + RIGACQ + ".R_RIGA_ORDC = ? AND" + " " + RIGACQ + ".R_DET_RIGA_ORDC = 0";
	protected static CachedStatement cRigAcqDaRigaOrd = new CachedStatement(SELECT_RIGACQ_DA_RIGA_ORD);

	protected static final String SELECT_RIGHE_DOC_DA_RIGA_DDT = "SELECT " + DDT_RIG + ".ID_ANNO_DOC_VEN, " + DDT_RIG
			+ ".ID_NUMERO_DOC_VEN, " + DDT_RIG + ".ID_RIGA_DOC_VEN" + " FROM " + DDT_RIG + " WHERE " + DDT_RIG
			+ ".ID_AZIENDA = ? AND" + " " + DDT_RIG + ".ID_ANNO_DDT = ? AND" + " " + DDT_RIG + ".ID_NUMERO_DDT = ? AND"
			+ " " + DDT_RIG + ".TIPO_DDT = ? AND" + " " + DDT_RIG + ".ID_RIGA_DDT = ? AND" + " " + DDT_RIG
			+ ".ID_DET_RIGA_DDT = 0 AND" + " " + DDT_RIG + ".ID_NUMERO_DOC_VEN IS NOT NULL";
	protected static CachedStatement cRigheDocDaRigaDDT = new CachedStatement(SELECT_RIGHE_DOC_DA_RIGA_DDT);

	protected static final String SELECT_RIGHE_DDT_DA_RIGA_DOC = "SELECT " + DDT_RIG + ".ID_ANNO_DDT, " + DDT_RIG
			+ ".ID_NUMERO_DDT, " + DDT_RIG + ".TIPO_DDT, " + DDT_RIG + ".ID_RIGA_DDT" + " FROM " + DDT_RIG + " WHERE "
			+ DDT_RIG + ".ID_AZIENDA = ? AND " + DDT_RIG + ".ID_ANNO_DOC_VEN = ? AND " + DDT_RIG
			+ ".ID_NUMERO_DOC_VEN = ? AND " + DDT_RIG + ".ID_RIGA_DOC_VEN = ? AND " + DDT_RIG
			+ ".ID_DET_RIGA_DOCVEN = 0 AND " + DDT_RIG + ".TIPO_DDT <> '" + DdtTes.TIPO_DDT_ACQ + "'  AND " + DDT_RIG
			+ ".ID_NUMERO_DDT IS NOT NULL ";
	protected static CachedStatement cRigheDDTDaRigaDoc = new CachedStatement(SELECT_RIGHE_DDT_DA_RIGA_DOC);

	protected static final String SELECT_RIGHE_DDT_DA_DDT = "SELECT " + DdtRigTM.ID_RIGA_DDT + " FROM " + DDT_RIG
			+ " WHERE " + DdtRigTM.ID_AZIENDA + " = ? AND " + DdtRigTM.ID_ANNO_DDT + " = ? AND "
			+ DdtRigTM.ID_NUMERO_DDT + " = ? AND " + DdtRigTM.TIPO_DDT + " = ? AND " + DdtRigTM.ID_DET_RIGA_DDT
			+ " = 0";
	protected static CachedStatement cRigheDDTDaDDT = new CachedStatement(SELECT_RIGHE_DDT_DA_DDT);

	protected static final String SELECT_FAT_DA_DDT = "SELECT DISTINCT ANNO_FAT, NUMERO_FATTURA " + " FROM "
			+ SystemParam.getSchema("THIP") + "DOC_VEN_TES_V10" + " WHERE ID_AZIENDA = ? AND" + " ID_ANNO_DOC = ? AND"
			+ " ID_NUMERO_DOC = ? AND" + " NUMERO_FATTURA IS NOT NULL";
	protected static CachedStatement cFatDaDDT = new CachedStatement(SELECT_FAT_DA_DDT);

	protected static final String SELECT_RIGHE_DDT_DA_RIGA_FAT = "SELECT DDT.ID_ANNO_DDT, DDT.ID_NUMERO_DDT, DDT.TIPO_DDT, DDT.ID_RIGA_DDT, DDT.ID_DET_RIGA_DDT"
			+ " FROM " + DDT_RIG + " DDT " + " LEFT OUTER JOIN " + FATVEN_DOCVEN + " FAT ON "
			+ " FAT.ID_AZIENDA = DDT.ID_AZIENDA AND " + " FAT.R_ANNO_DOC = DDT.ID_ANNO_DOC_VEN AND "
			+ " FAT.R_NUMERO_DOC = DDT.ID_NUMERO_DOC_VEN " + " WHERE FAT.ID_AZIENDA = ? AND "
			+ " FAT.ID_ANNO_FAT = ? AND " + " FAT.ID_NUMERO_FAT = ? AND " + " DDT.ID_DET_RIGA_DDT = 0 AND "
			+ " DDT.TIPO_DDT <> '3'";

	protected static CachedStatement cRigheDDTDaRigaFat = new CachedStatement(SELECT_RIGHE_DDT_DA_RIGA_FAT);

	protected static final String SELECT_FATVEN_DA_DDT = "SELECT DISTINCT FAT.ID_AZIENDA, FAT.ID_ANNO_FAT, FAT.ID_NUMERO_FAT"
			+ " FROM " + FATVEN_DOCVEN + " FAT " + " LEFT OUTER JOIN " + DDT_RIG + " DDT ON "
			+ " FAT.ID_AZIENDA = DDT.ID_AZIENDA AND " + " FAT.R_ANNO_DOC = DDT.ID_ANNO_DOC_VEN AND "
			+ " FAT.R_NUMERO_DOC = DDT.ID_NUMERO_DOC_VEN " + " WHERE DDT.ID_AZIENDA = ? AND "
			+ " DDT.ID_ANNO_DDT = ? AND " + " DDT.ID_NUMERO_DDT = ? AND " + " DDT.TIPO_DDT = ? ";

	protected static CachedStatement cfatVenDaRigaDDT = new CachedStatement(SELECT_FATVEN_DA_DDT);

	protected static final String SELECT_FATVEN_DA_TRANCONTAB = "SELECT  " + " FAT." + FatturaVenditaTM.ID_AZIENDA
			+ ", FAT." + FatturaVenditaTM.ID_ANNO_FAT + ", " + " FAT." + FatturaVenditaTM.ID_NUMERO_FAT + " FROM "
			+ FATVEN + " FAT " + " WHERE FAT." + FatturaVenditaTM.ID_AZIENDA + " = ? AND " + " FAT."
			+ FatturaVenditaTM.T09CD + " = ? AND " + " FAT." + FatturaVenditaTM.TRANREGG + " = ? ";

	protected static CachedStatement cFatVenDaTransContab = new CachedStatement(SELECT_FATVEN_DA_TRANCONTAB);

	protected static final String SELECT_TRANCONTAB_DA_FATVEN = "SELECT FAT.T09CD, FAT.TRANREGG" + " FROM " + FATVEN
			+ " FAT " + " WHERE FAT." + FatturaVenditaTM.ID_AZIENDA + " = ? AND " + " FAT."
			+ FatturaVenditaTM.ID_ANNO_FAT + " = ? AND " + " FAT." + FatturaVenditaTM.ID_NUMERO_FAT + " = ? AND "
			+ " FAT.TRANREGG != 0";

	protected static CachedStatement cTransContabDaFatVen = new CachedStatement(SELECT_TRANCONTAB_DA_FATVEN);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Map execute(Map m) {
		String keyOrd = this.getKeyOrdVen();
		if (keyOrd != null) {
			try {
				YOrdineVendita ordVen = (YOrdineVendita) YOrdineVendita.elementWithKey(YOrdineVendita.class, keyOrd,
						PersistentObject.NO_LOCK);
				if (ordVen != null) {
					List<String> keysOrdVenRig = new ArrayList<String>();
					Iterator<YOrdineVenditaRigaPrm> righe = ordVen.getRighe().iterator();
					while (righe.hasNext()) {
						YOrdineVenditaRigaPrm yOrdineVenditaRigaPrm = (YOrdineVenditaRigaPrm) righe.next();
						keysOrdVenRig.add(yOrdineVenditaRigaPrm.getKey());
					}
					List<String> keysDocVenRigClg = selectRigheDocDaRigheOrd(keysOrdVenRig);
					List<String> ddts = caricaDDTDaDocumento(keysDocVenRigClg);
					m.put("keysDDT", ddts);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return m;
	}

	protected String getKeyOrdVen() {
		return (String) this.getAppParams().get("key");
	}

	public List<String> caricaDDTDaDocumento(List<String> docRigKeys) throws SQLException {
		List<String> ret = new ArrayList<String>();
		Iterator<String> iter = selectRigheDDTDaRigheDoc(docRigKeys).iterator();
		String oldDDTKey = null;
		while (iter.hasNext()) {
			String rigKey = (String) iter.next();
			String[] tmp = KeyHelper.unpackObjectKey(rigKey);
			String ddtKey = KeyHelper.buildObjectKey(new String[] { tmp[0], tmp[1], tmp[2], tmp[3] });
			if (oldDDTKey == null || !oldDDTKey.equals(ddtKey)) {
				ret.add(ddtKey);
			}
			oldDDTKey = ddtKey;
		}
		return ret;
	}

	protected static List<String> selectRigheDDTDaRigheDoc(List<String> docRigKeys) throws SQLException {
		List<String> ret = new ArrayList<String>();
		Map<String, String> keyMap = new HashMap<String, String>();
		Database db = ConnectionManager.getCurrentDatabase();
		Iterator<String> iter = docRigKeys.iterator();
		while (iter.hasNext()) {
			synchronized (cRigheDDTDaRigaDoc) {
				String docRigKey = (String) iter.next();
				String[] tmp = KeyHelper.unpackObjectKey(docRigKey);
				String idAzienda = tmp[0];
				String idAnnoDoc = tmp[1];
				String idNumeroDoc = tmp[2];
				String idRigaDoc = tmp[3];
				PreparedStatement ps = cRigheDDTDaRigaDoc.getStatement();
				db.setString(ps, 1, idAzienda);
				db.setString(ps, 2, idAnnoDoc);
				db.setString(ps, 3, idNumeroDoc);
				db.setString(ps, 4, idRigaDoc);
				ResultSet res = ps.executeQuery();
				while (res.next()) {
					String idAnnoDDT = Column.rightTrim(res.getString(1));
					String idNumeroDDT = Column.rightTrim(res.getString(2));
					String tipoDDT = Column.rightTrim(res.getString(3));
					String idRigaDDT = Column.rightTrim(res.getString(4));
					String rigKey = KeyHelper.buildObjectKey(
							new String[] { idAzienda, idAnnoDDT, idNumeroDDT, tipoDDT, idRigaDDT, "0" });
					if (rigKey != null && keyMap != null) {
						if (keyMap.isEmpty() || keyMap.get(rigKey) == null)
							keyMap.put(rigKey, rigKey);
					}
				}
				res.close();
			}
		}
		if (keyMap != null && !keyMap.isEmpty())
			ret = new ArrayList<String>(keyMap.values());
		Collections.sort(ret);
		return ret;
	}

	protected static List<String> selectRigheDocDaRigheOrd(List<String> ordVenRigKeys) throws SQLException {
		List<String> ret = new ArrayList<String>();
		Database db = ConnectionManager.getCurrentDatabase();
		Iterator<String> iter = ordVenRigKeys.iterator();
		while (iter.hasNext()) {
			synchronized (cRigheDocDaRigaOrd) {
				String ordVenRigKey = (String) iter.next();
				String[] tmp = KeyHelper.unpackObjectKey(ordVenRigKey);
				String idAzienda = tmp[0];
				String idAnnoOrd = tmp[1];
				String idNumeroOrd = tmp[2];
				String idRigaOrd = tmp[3];
				PreparedStatement ps = cRigheDocDaRigaOrd.getStatement();
				db.setString(ps, 1, idAzienda);
				db.setString(ps, 2, idAnnoOrd);
				db.setString(ps, 3, idNumeroOrd);
				db.setString(ps, 4, idRigaOrd);
				ResultSet res = ps.executeQuery();
				while (res.next()) {
					String idAnnoDoc = Column.rightTrim(res.getString(1));
					String idNumeroDoc = Column.rightTrim(res.getString(2));
					String idRigaDoc = Column.rightTrim(res.getString(3));
					String rigKey = KeyHelper
							.buildObjectKey(new String[] { idAzienda, idAnnoDoc, idNumeroDoc, idRigaDoc, "0" });
					ret.add(rigKey);
				}
				res.close();
			}
		}
		Collections.sort(ret);
		return ret;
	}
}
