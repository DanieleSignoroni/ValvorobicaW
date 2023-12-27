package it.valvorobica.thip.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.thera.thermfw.ad.ClassADCollection;
import com.thera.thermfw.ad.ClassADCollectionManager;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.batch.BatchRunnable;
import com.thera.thermfw.collector.BODataCollector;
import com.thera.thermfw.collector.BaseBOComponentManager;
import com.thera.thermfw.common.BaseComponentsCollection;
import com.thera.thermfw.common.BusinessObject;
import com.thera.thermfw.common.ErrorMessage;
import com.thera.thermfw.gui.cnr.OpenType;
import com.thera.thermfw.persist.CachedStatement;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.persist.Proxy;
import com.thera.thermfw.rs.errors.ErrorUtils;
import com.thera.thermfw.type.DecimalType;

import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.base.fornitore.FornitoreAcquisto;
import it.valvorobica.thip.base.importazione.YClassAdImpMisure;
import it.valvorobica.thip.base.importazione.YClassAdImpMisureTM;
import it.valvorobica.thip.base.importazione.YHdrImpMisure;

/**
 * <h1>Softre Solutions</h1>
 * <br>
 * @author Daniele Signoroni 22/12/2023
 * <br><br>
 * <b>71366	DSSOF3	22/12/2023</b>    <p>Importazione misure da .csv prima stesura.<br></br>
 * 										 Tramite il descrittore di importazione {@link YHdrImpMisure} e i suoi
 * 										 {@link YClassAdImpMisure} creo l'associazione: <br><b> Titolo colonna <--> {@link BaseBOComponentManager} </b>.<br>
 * 										 Una volta creata l'associazione vado a valorizzare il {@link BusinessObject} tramite {@link BODataCollector}.
 * 									  </p>
 */

public class YImportazioneMisureBatch extends BatchRunnable {

	protected String iIdAzienda;

	protected Proxy iFornitore = new Proxy(it.thera.thip.base.fornitore.FornitoreAcquisto.class);

	protected String iTemporaryFileName;

	protected File file = null;

	protected YHdrImpMisure descrittoreImportazione = null;

	public String getTemporaryFileName() {
		return iTemporaryFileName;
	}

	public void setTemporaryFileName(String iTemporaryFileName) {
		this.iTemporaryFileName = iTemporaryFileName;
	}

	public YImportazioneMisureBatch() {
		setIdAzienda(Azienda.getAziendaCorrente());
	}

	protected void setIdAziendaInternal(String idAzienda) {
		iFornitore.setKey(KeyHelper.replaceTokenObjectKey(iFornitore.getKey(), 1, idAzienda));
	}

	public String getIdAzienda() {
		return iIdAzienda;
	}

	public void setIdAzienda(String iIdAzienda) {
		this.iIdAzienda = iIdAzienda;
		setIdAziendaInternal(iIdAzienda);
	}

	public void setFornitore(FornitoreAcquisto Fornitore) {
		this.iFornitore.setObject(Fornitore);
	}

	public FornitoreAcquisto getFornitore() {
		return (FornitoreAcquisto) iFornitore.getObject();
	}

	public void setFornitoreKey(String key) {
		iFornitore.setKey(key);
	}

	public String getFornitoreKey() {
		return iFornitore.getKey();
	}

	public void setIdFornitore(String rFornitore) {
		String key = iFornitore.getKey();
		iFornitore.setKey(KeyHelper.replaceTokenObjectKey(key, 2, rFornitore));
	}

	public String getIdFornitore() {
		String key = iFornitore.getKey();
		String objRFornitore = KeyHelper.getTokenObjectKey(key, 2);
		return objRFornitore;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Vector checkAll(BaseComponentsCollection components) {
		Vector errors = super.checkAll(components);
		if (errors.isEmpty()) {
			ErrorMessage em = checkFile();
			if (em != null) {
				errors.add(em);
			}
		}
		return errors;
	}

	protected ErrorMessage checkFile() {
		BufferedReader bufferedReader = null;
		FileReader fileReader = null;
		try {
			File file = new File(this.getTemporaryFileName());
			fileReader = new FileReader(file);
			bufferedReader = new BufferedReader(fileReader);
			String line = bufferedReader.readLine();
			int i = 0;
			Object[] headers = null;
			int keyPosition = 0;
			String titleKey = getTitoloColonnaColata(this.getIdFornitore());
			while (line != null) {
				if (i == 0) { // headers
					headers = getLineParams(line);
					for (int j = 0; j < headers.length; j++) {
						if (headers[j] != null && headers[j].toString().trim().equals(titleKey)) {
							keyPosition = j;
							break;
						}
					}
				} else {
					Object[] values = getLineParams(line);
					String c = KeyHelper.buildObjectKey(new String[] { Azienda.getAziendaCorrente(),
							this.getIdFornitore(), values[keyPosition].toString() });
					YMisure mis = (YMisure) YMisure.elementWithKey(YMisure.class, c, PersistentObject.NO_LOCK);
					if (mis != null) {
						bufferedReader.close();
						return new ErrorMessage("YVALVO_005", true,
								"Misura con chiave: " + c + " gia presente, si vuole sovrascrivere?");
					}
				}
				i++;
				line = bufferedReader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace(Trace.excStream);
		}
		try {
			if (bufferedReader != null)
				bufferedReader.close();
			if (fileReader != null)
				fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getTitoloColonnaColata(String idFornitore) {
		ResultSet rs = null;
		CachedStatement cs = null;
		String stmt = "SELECT " + "TITOLO_COLONNA  " + " FROM THIPPERS.YCLASS_AD_IMP_MISURE  " + "WHERE ID_AZIENDA = '"
				+ Azienda.getAziendaCorrente() + "' " + "AND ID_FORNITORE = '" + idFornitore + "' "
				+ "AND ESCLUDI = 'Y'";
		cs = new CachedStatement(stmt);
		try {
			rs = cs.executeQuery();
			if (rs.next()) {
				return rs.getString(YClassAdImpMisureTM.TITOLO_COLONNA).trim();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	protected String getAttributeForColumn(String title) {
		ResultSet rs = null;
		CachedStatement cs = null;
		String stmt = "SELECT " + YClassAdImpMisureTM.CLASS_AD + " FROM THIPPERS.YCLASS_AD_IMP_MISURE  "
				+ "WHERE ID_AZIENDA = '" + Azienda.getAziendaCorrente() + "' " + "AND ID_FORNITORE = '"
				+ this.getIdFornitore() + "' AND " + YClassAdImpMisureTM.TITOLO_COLONNA + " = '" + title + "' ";
		cs = new CachedStatement(stmt);
		try {
			rs = cs.executeQuery();
			if (rs.next()) {
				return rs.getString(YClassAdImpMisureTM.CLASS_AD).trim();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Object[] getLineParams(String line) {
		if (line == null) {
			return null;
		}
		String[] params = new String[line.split(";").length]; //alla max lunghezza delle columns
		params = line.split(";");
		return params;
	}

	@Override
	protected boolean run() {
		boolean isOk = true;
		output.println("*** Iniziata l'importazione delle misure da file ***");
		output.println("Si considera commit singolare");
		file = new File(this.getTemporaryFileName());
		if (file != null) {
			try {
				descrittoreImportazione = (YHdrImpMisure) YHdrImpMisure.elementWithKey(YHdrImpMisure.class,
						KeyHelper.buildObjectKey(
								new String[] { Azienda.getAziendaCorrente(), this.getIdFornitore(), "YMisure" }),
						PersistentObject.NO_LOCK);
				if (descrittoreImportazione != null) {
					isOk = runImportazione();
				} else {
					output.println(" -- Descrittore di file non trovato per il fornitore =  " + this.getIdFornitore());
				}
			} catch (Exception e) {
				if (e instanceof IllegalArgumentException) {
					output.println(e.getMessage());
				}
				isOk = false;
				e.printStackTrace(Trace.excStream);
			}
		} else {
			output.println("File vuoto o non trovato \n Percorso : " + this.getTemporaryFileName());
		}
		output.println("*** Terminata l'importazione delle misure da file ***");
		return isOk;
	}

	@SuppressWarnings("unchecked")
	protected boolean runImportazione() throws IllegalArgumentException {
		output.println();
		boolean isOk = true;
		BufferedReader bufferedReader = null;
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(file);
			bufferedReader = new BufferedReader(fileReader);
			String line = bufferedReader.readLine();
			int i = 0;
			Object[] headers = null;
			int keyPosition = 0;
			String titleKey = getTitoloColonnaColata(this.getIdFornitore());
			if (titleKey == null) {
				bufferedReader.close();
				throw new IllegalArgumentException(
						"Non e' stato definito nessun attributo chiave per la colonna ["+YMisureTM.R_COLATA+"] della tabella: "+YMisureTM.TABLE_NAME);
			}
			HashMap<Integer, String> associazioneColonneAttributo = new HashMap<Integer, String>();
			while (line != null) {
				BODataCollector boDC = null;
				if (i == 0) { // headers
					//sistemazione di qualche carattere particolare
					if(line.contains("ï»¿")) {
						line = line.replace("ï»¿", "");
					}
					if(line.contains("Â")) {
						line = line.replace("Â", "");
					}
					headers = getLineParams(line);
					for (int j = 0; j < headers.length; j++) {
						String attributeName = null;
						if (headers[j] != null && headers[j].toString().trim().equals(titleKey)) {
							keyPosition = j;
							attributeName = getAttributeForColumn(titleKey);
							if (attributeName != null)
								associazioneColonneAttributo.put(j, attributeName);
							else
								output.println("ATTENZIONE: Non e' stato trovato nessun attributo configurato per la colonna : " +titleKey);
						} else {
							attributeName = getAttributeForColumn(headers[j].toString().trim());
							if (attributeName != null)
								associazioneColonneAttributo.put(j, attributeName);
							else
								output.println("ATTENZIONE: Non e' stato trovato nessun attributo configurato per la colonna : " +headers[j].toString().trim());
						}
					}
				} else {
					output.println(" ------ Processo RIGA CSV NR: " + i);
					// qui importazione vera!!
					Object[] values = getLineParams(line);
					String c = KeyHelper.buildObjectKey(new String[] { Azienda.getAziendaCorrente(),
							this.getIdFornitore(), values[keyPosition].toString() });
					boDC = createDataCollector("YMisure");
					if (boDC.initSecurityServices(OpenType.NEW, false, false, true) == BODataCollector.ERROR) {
						bufferedReader.close();
						throw new Exception("Utente non autorizzato ad effettuare l'operazione NEW su YMisure");
					}
					boDC.set("IdAzienda", Azienda.getAziendaCorrente()); //[0] key
					boDC.set("RFornitore", this.getIdFornitore()); //[1] key
					boDC.retrieve(c); // Provo a fare retrieve
					for (Map.Entry<Integer, String> entry : associazioneColonneAttributo.entrySet()) {
						Integer key = entry.getKey();
						String val = entry.getValue();
						BaseBOComponentManager cmp = boDC.getComponentManager(val);
						// andrebbero gestiti i type qui, dato che dal csv recupero qualsiasi valore
						// sotto
						// forma di stringa
						// quindi andrebbe checckato il type del ComponentManger e in seguito gestito il
						// corretto
						// passaggio di Object castato
						if (cmp.getType() instanceof DecimalType) {
							boDC.set(val, new BigDecimal(values[key].toString()));
						} else {
							boDC.set(val, values[key]);
						}
						// setto i component manager
					}
					int ok = boDC.check(); //carica pure i value del comp manager, tramite setBoOnRecursive
					if (ok == BODataCollector.OK) {
						boDC.setAutoCommit(true);
						int rc = boDC.save(true);
						if (rc == BODataCollector.ERROR) {
							output.println("Riga importata con ERRORI:");
							output.println("Errore nel salvataggio della misura \n" + ErrorUtils.getInstance()
									.toJSON((List<ErrorMessage>) boDC.getErrorList().getErrors()));
						} else {
							output.println("Riga csv importata correttamente");
						}
					} else {
						output.println("Riga importata con ERRORI:");
						output.println("Errore nel salvataggio della misura \n" + ErrorUtils.getInstance()
								.toJSON((List<ErrorMessage>) boDC.getErrorList().getErrors()));
					}
					output.println("Termine processo RIGA CSV NR: " + i + "  ---");
				}
				output.println();
				i++;
				line = bufferedReader.readLine();
			}
		} catch (Exception e) {
			output.println(e.getMessage());
			isOk = false;
			e.printStackTrace(Trace.excStream);
		}
		try {
			if (bufferedReader != null)
				bufferedReader.close();
			if (fileReader != null)
				fileReader.close();
		} catch (IOException e) {
			isOk = false;
			e.printStackTrace();
		}
		return isOk;
	}

	protected BODataCollector createDataCollector(ClassADCollection classDescriptor) {
		BODataCollector dataCollector = null;
		String collectorName = classDescriptor.getBODataCollector();
		if (collectorName != null) {
			dataCollector = (BODataCollector) Factory.createObject(collectorName);
		} else {
			dataCollector = new BODataCollector();
		}
		dataCollector.initialize(classDescriptor.getClassName(), true);
		return dataCollector;
	}

	protected BODataCollector createDataCollector(String classname) {
		try {
			ClassADCollection hdr = ClassADCollectionManager.collectionWithName(classname);
			return createDataCollector(hdr);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected String getClassAdCollectionName() {
		return "YImportMisure";
	}

}
