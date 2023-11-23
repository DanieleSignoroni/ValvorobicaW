package it.valvorobica.thip.base.generale.ws.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import com.thera.thermfw.base.Trace;
import com.thera.thermfw.common.ErrorMessage;
import com.thera.thermfw.edi.EDITranscoder;
import com.thera.thermfw.persist.ConnectionDescriptor;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.pref.Preferences;
import com.thera.thermfw.security.HashManager;
import com.thera.thermfw.security.Security;
import com.thera.thermfw.web.SessionEnvironment;

import it.thera.thip.base.azienda.AziendaTM;
import it.thera.thip.base.generale.ParametroPsn;
import it.thera.thip.base.profilo.UtenteAzienda;
import it.thera.thip.ws.GenericQuery;
import it.thera.thip.ws.WebServiceUtils;
import it.valvorobica.thip.base.portal.YAziendeUserPortalTM;
import it.valvorobica.thip.base.portal.YUserPortalSession;
import it.valvorobica.thip.base.portal.YUserPortalTM;

/**
 * <h1>Softre Solutions</h1>
 * 
 * @author Andrea Gatta 24/03/2023 <br>
 *         <br>
 *         <b>71018 AGSOF3 24/03/2023</b>
 *         <p>
 *         Riportati metodi di controllo validità password da
 *         <code>UtenteAzienda</code>, chiamati poi dalla nuova anagrafica
 *         YUserPortal nell'inserimento di un nuovo utente
 *         </p>
 *         <b>71162 DSSOF3 04/07/2023</b>
 *         <p>
 *         Aggiunta funzione per permettere al fornitore di compilare l'offerta.
 *         </p>
 */

public class YUtilsPortal {

	public static final int FUNZIONE_CLIENTI = 0;

	public static final int FUNZIONE_ANAGAFICA = 1;

	public static final int FUNZIONE_MANUALI_DICH = 2;

	public static final int FUNZIONE_OFFERTE = 3;

	public static final int FUNZIONE_ORDINI = 4;

	public static final int FUNZIONE_DDT = 5;

	public static final int FUNZIONE_FATTURE = 6;

	public static final int FUNZIONE_INEVASO = 7;

	public static final int FUNZIONE_CERTIFICATI = 8;

	public static final int FUNZIONE_PROSPECT_CLIENTI = 9;

	public static final int FUNZIONE_CAMBIO_PASSWORD = 10;

	public static final int FUNZIONE_LOGOUT = 11;

	public static final int FUNZIONE_CAMBIO_AZIENDA = 12;

	public static final int FUNZIONE_COMPILAZIONE_OFFERTA_SUPPLIER = 13; // 71162

	public static final int FUNZIONE_CATALOGO = 14;

	public static final int FUNZIONE_CARRELLO = 15;

	// Le tre variabili statiche qui sotto indicano l'utnete che deve esistere a
	// sistema per poter effettuare la prima chiamata ws
	public static final String GENERIC_USER_PORTAL = "PORTALUSER";

	public static final String GENERIC_PWD_PORTAL = "SOFTRE999";

	public static final String GENERIC_COMPANY_PORTAL = "001";

	/**
	 * Effettua la chiamata ad un WebService esterno
	 * 
	 * @param url        del webservices da chiamare
	 * @param method     di chiamata
	 * @param params,    se isSendJson true questa stringa deve essere un json
	 *                   altrimenti devono essere dei queryParams
	 * @param isSendJson indica se la chiamata va fatta passando un json in raw body
	 * @return una mappa con status,response e error
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, String> chiamaWsEsterno(String url, String method, String params, boolean isSendJson) {
		Map<String, String> out = new HashMap<String, String>();
		try {
			String[] res = null;
			if (isSendJson) {
				res = WebServiceUtils.getInstance().sendJSON(method, url, params, true);
			} else {
				if (method.equals("POST")) {
					res = WebServiceUtils.getInstance().sendPost(url, params == null ? "" : params, new HashMap());
				} else if (method.equals("GET")) {
					res = WebServiceUtils.getInstance().sendGet(url, params == null ? "" : params, new HashMap());
				}
			}
			out.put("status", res[0]);
			out.put("response", res[1]);
			out.put("error", res[2]);
		} catch (Throwable t) {
			t.printStackTrace(Trace.excStream);
			out = null;
		}
		return out;
	}

	/**
	 * 
	 * @param data da formattare
	 * @return stringa formattata yyyyMMdd
	 * @throws ParseException
	 */
	public static String formattaDataPerWs(java.util.Date data) throws ParseException {
		SimpleDateFormat formatoWs = new SimpleDateFormat("yyyyMMdd");
		String ultimaDataOk = formatoWs.format(data);
		return ultimaDataOk;
	}

	public static java.sql.Date getDataDaStringa(String dataStringa, String formatoData) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(formatoData);
		java.sql.Date data = new java.sql.Date(sdf.parse(dataStringa).getTime());
		return data;
	}

	public static String trascodificaUMContoDep(String UMContoDep) throws SQLException {
		String UMPth = null;
		EDITranscoder transcoder = EDITranscoder.elementWithKey("UMPortaleContoD", 0);
		if (transcoder != null)
			UMPth = transcoder.transcodeToXML(UMContoDep);
		return UMPth;
	}

	/**
	 * DSSOF3 28/12/2022 Update del valore di un parametro personalizzato data
	 * funzione, parametro e valore.
	 * 
	 * @param valore
	 * @param funzione
	 * @param parametro
	 */
	public static void updateValoreParametro(String valore, String funzione, String parametro) {
		ParametroPsn param = getParametroPersonalizzazione(funzione, parametro);
		try {
			if (param != null) {
				param.setValore(valore);
				if (param.save() >= 0) {
					ConnectionManager.commit();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static ParametroPsn getParametroPersonalizzazione(String funzione, String parametro) {
		String where = "ID_FUNZIONE = '" + funzione + "' AND ID_PARAMETRO = '" + parametro + "'";
		try {
			List<ParametroPsn> lista = ParametroPsn.retrieveList(ParametroPsn.class, where, "", false);
			if (lista.size() > 0) {
				return lista.get(0);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Copiato da classe <code>UtenteAzienda</code>
	 */
	public static ErrorMessage checkPassword(String pwdNoCrypto) {
		boolean upperCaseMandatory = Preferences.getInstance().isUpperCaseMandatory();
		boolean lowerCaseMandatory = Preferences.getInstance().isLowerCaseMandatory();
		boolean numberMandatory = Preferences.getInstance().isNumberMandatory();
		boolean specialCharMandatory = Preferences.getInstance().isSpecialCharMandatory();

		List<String> regexps = new ArrayList<String>();
		List<String> excludedRegexps = new ArrayList<String>();
		ErrorMessage errMessagePwd = null;
		String errorText = "";
		String resourceFileName = UtenteAzienda.RES_FILE;

		errorText = errorText.concat("\n- ")
				+ com.thera.thermfw.base.ResourceLoader.getString(resourceFileName, "messg10");

		if (upperCaseMandatory) {
			errorText = errorText.concat("\n")
					.concat("- " + com.thera.thermfw.base.ResourceLoader.getString(resourceFileName, "messg6"));
			regexps.add(UtenteAzienda.UP_CASE_REGEX);
		}

		if (lowerCaseMandatory) {
			errorText = errorText.concat("\n")
					.concat("- " + com.thera.thermfw.base.ResourceLoader.getString(resourceFileName, "messg7"));
			regexps.add(UtenteAzienda.LOWER_CASE_REGEX);
		}

		if (numberMandatory) {
			errorText = errorText.concat("\n")
					.concat("- " + com.thera.thermfw.base.ResourceLoader.getString(resourceFileName, "messg8"));
			regexps.add(UtenteAzienda.DEGIT_REGEX);
		}

		if (specialCharMandatory) {
			errorText = errorText.concat("\n")
					.concat("- " + com.thera.thermfw.base.ResourceLoader.getString(resourceFileName, "messg9"));
			regexps.add(UtenteAzienda.SPECIAL_CHAR_REGEX);
		}

		int pwdLg = Preferences.getInstance().getMinPwdLength();
		if (pwdLg != 0) {
			errorText = errorText.concat("\n").concat("- " + new ErrorMessage("THIP_TN310", String.valueOf(pwdLg)));
		}
		boolean excludedChars = !excludedRegexps.isEmpty() && containsExpression(excludedRegexps, pwdNoCrypto);
		boolean includedChars = !regexps.isEmpty() && !containsExpression(regexps, pwdNoCrypto);
		boolean passedPWDLength = (pwdLg != 0) && (pwdNoCrypto.length() < pwdLg);
		if (excludedChars || includedChars || !isPasswordPolicy(pwdNoCrypto) || passedPWDLength) {
			errorText = errorText.concat("\n");
			errMessagePwd = new ErrorMessage("THIP_TN819", errorText);
			errMessagePwd.setAttOrGroupLabel("Password");
		}

		return errMessagePwd;
	}

	/**
	 * Copiato da classe <code>UtenteAzienda</code>
	 */
	public static boolean containsExpression(String regex, String word) {
		Pattern pattern = Pattern.compile(regex, Pattern.UNICODE_CASE);
		Matcher matcher = pattern.matcher(word);
		return matcher.find();
	}

	/**
	 * Copiato da classe <code>UtenteAzienda</code>
	 */
	public static boolean containsExpression(List<String> regexps, String word) {
		for (String regex : regexps) {
			if (!containsExpression(regex, word))
				return false;
		}
		return true;
	}

	/**
	 * 
	 * @param pwdNoCrypto
	 * @return true sempre perchè deciso che non faccio controlli di policy
	 */
	public static boolean isPasswordPolicy(String pwdNoCrypto) {
		return true;
	}

	/**
	 * 
	 * @param password da criptare
	 * @return password criptata
	 */
	public static String criptaPassword(String password) {
		return getHashedPassword(password);
	}

	/**
	 * Copiato da {@link HashManager#getHashedPassword(String)}
	 * 
	 * @param passwordToHash
	 * @return
	 */
	public static String getHashedPassword(String passwordToHash) {
		if (passwordToHash != null && passwordToHash.length() == 32) // Fix 36736
			return passwordToHash; // Fix 36736
		String generatedPassword = null;
		try {
			// Create MessageDigest instance for MD5
			MessageDigest md = MessageDigest.getInstance("MD5");
			// Add password bytes to digest
			md.update(getSalt(passwordToHash).getBytes());
			// Get the hash's bytes
			byte[] bytes = md.digest(passwordToHash.getBytes());
			// This bytes[] has bytes in decimal format;
			// Convert it to hexadecimal format
			// Get complete hashed password in hex format
			generatedPassword = DatatypeConverter.printHexBinary(bytes).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return generatedPassword;
	}

	/**
	 * Copiato da {@link HashManager#getSalt(String)}
	 * 
	 * @param passwordToHash
	 * @return
	 */
	public static String getSalt(String password) {
		String salt = "";
		if (password.length() > 0) {
			salt = String.valueOf(new char[] { password.charAt(0), password.charAt(password.length() - 1) });
			String binary = Integer.toBinaryString(password.length());
			salt += "@!?*";
			for (int i = 0; (i < binary.length()) && (i < password.length()); i++) {
				if (binary.charAt(i) == '1') {
					salt += password.charAt(i);
				}
			}
		}
		return salt;
	}

	/**
	 * Metodo statico che prende in entrata una query la sottomette a db tramite ws
	 * GSQ e ritorna una mappa dei risultati
	 * 
	 * @param query che vogliamo sottomettere
	 * @return Map del risultato della query
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map sottomettiGSQ(String query) {
		Map values = null;
		boolean isopen = false;
		Object[] info = SessionEnvironment.getDBInfoFromIniFile();
		String dbName = (String) info[0];
		try {
			if (!Security.isCurrentDatabaseSetted()) {
				Security.setCurrentDatabase(dbName, null);
			}
			Security.openDefaultConnection();
			isopen = true;
			ConnectionDescriptor cd = ConnectionManager.getCurrentConnectionDescriptor();
			GenericQuery gq = new GenericQuery();
			gq.getAppParams().put("query", query);
			gq.setUseAuthentication(false);
			gq.setUseAuthorization(false);
			gq.setUseLicence(false);
			gq.setConnectionDescriptor(cd);
			values = gq.send();
		} catch (Throwable t) {
			t.printStackTrace(Trace.excStream);
		} finally {
			if (isopen) {
				Security.closeDefaultConnection();
			}
		}
		return values;
	}

	/**
	 * Per il cambio azienda, visto che sono gia loggato non voglio mettere in where
	 * la PWD.
	 * 
	 * @param user
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ArrayList getListaAziende(YUserPortalSession user) {
		ArrayList records = new ArrayList();
		Map values = null;
		boolean isopen = false;
		Object[] info = SessionEnvironment.getDBInfoFromIniFile();
		String dbName = (String) info[0];
		String query = "SELECT yaz.*,azi.DESCRIZIONE " + "FROM " + YUserPortalTM.TABLE_NAME + " yuser  " + "INNER JOIN "
				+ YAziendeUserPortalTM.TABLE_NAME + " yaz " + "ON yuser." + YUserPortalTM.ID_UTENTE + " = yaz."
				+ YUserPortalTM.ID_UTENTE + " " + "LEFT OUTER JOIN " + AziendaTM.TABLE_NAME + " azi "
				+ "ON azi.IDAZIENDA = yaz." + YAziendeUserPortalTM.ID_AZIENDA + " " + "WHERE yuser."
				+ YUserPortalTM.ID_UTENTE + " = '" + user.getIdUtente() + "' " + "AND yuser." + YUserPortalTM.STATO
				+ " = 'V' ";
		try {
			if (!Security.isCurrentDatabaseSetted()) {
				Security.setCurrentDatabase(dbName, null);
			}
			Security.openDefaultConnection();
			isopen = true;
			ConnectionDescriptor cd = ConnectionManager.getCurrentConnectionDescriptor();
			GenericQuery gq = new GenericQuery();
			gq.getAppParams().put("query", query);
			gq.setUseAuthentication(false);
			gq.setUseAuthorization(false);
			gq.setUseLicence(false);
			gq.setConnectionDescriptor(cd);
			values = gq.send();
			records = (ArrayList) values.get("records");
			records = formattaRecords(records);
		} catch (Throwable t) {
			t.printStackTrace(Trace.excStream);
		} finally {
			if (isopen) {
				Security.closeDefaultConnection();
			}
		}
		return records;
	}

	/**
	 * formatto i valori all'interno di records in stringhe stile chiavi pth
	 * trimmate
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ArrayList formattaRecords(ArrayList records) {
		ArrayList<String> newRecords = new ArrayList<String>();
		for (int i = 0; i < records.size(); i++) {
			ArrayList<String> valori = (ArrayList<String>) records.get(i);
			String idAzienda = valori.get(1).trim();
			String desc = valori.get(valori.size() - 1).trim();
			String tipoUser = valori.get(2).trim();
			String agente = valori.get(3).trim();
			String cliente = valori.get(4).trim();
			String dipendente = valori.get(5).trim();
			String utentePTH = valori.get(6).trim();
			String key = KeyHelper
					.buildObjectKey(new String[] { idAzienda, desc, tipoUser, agente, cliente, dipendente, utentePTH });
			newRecords.add(key);
		}

		return newRecords;
	}

}