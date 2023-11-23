package it.test.portal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.thera.thermfw.base.TimeUtils;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.persist.CachedStatement;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.rs.BaseResource;
import com.thera.thermfw.rs.errors.ExceptionTransporterException;
import com.thera.thermfw.rs.errors.PantheraApiException;
import com.thera.thermfw.rs.security.AuthenticationSettings;
import com.thera.thermfw.rs.security.ThermJwtClaimsManager;
import com.thera.thermfw.security.Security;
import com.thera.thermfw.security.User;
import com.thera.thermfw.security.UserNotFoundException;
import com.thera.thermfw.security.UserTM;
import com.thera.thermfw.security.WrongPasswordException;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/*
 * Revisions:
 * Fix     Date          Owner      Description
 * 35311   24/02/2022    PJ         Rilascio infrastruttura webservice su JAX-RS
 * 35852   08/09/2022    PJ+FG      Nuovi servizi per nuova WPU
 * 36953   04/11/2022    PJ         Corretta la gestione dell'apertura della sessione su thread corrente
 * 37046   16/11/2022    PJ         Gestione claims
 * 37145   24/11/2022    PJ         Token JWT collegato alla sessione, ad uso esclusivo UI         
 * 37957   13/03/2023    PJ         Produces, Consumes: JSON
 */

@Path("/authenticate")
@PermitAll
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PortalAuthenticationResource extends BaseResource {

	// fix 37145 - inizio
	public static final String SESSION_ID_CLAIM = "sessionId";
	// fix 37145 - fine

	@GET
	public Response authenticate(@QueryParam("user") String user, @QueryParam("password") String password) {
		Response response = null;
		try {

			// 36953 - inizio
			internalOpenSession(user, password);

			String issuedToken = issueToken();

			internalCloseSession();
			// 36953 - fine

			response = buildResponse(Status.OK, envelope(issuedToken));
		} catch (UserNotFoundException e) {
			sendError(Status.UNAUTHORIZED, "BAS0000007", user);
		} catch (WrongPasswordException e) {
			// 35852 - inizio
			sendError(Status.UNAUTHORIZED, e.getError().getId());
			// 35852 - fine
		} catch (PantheraApiException pae) {
			throw pae;
		} catch (Throwable t) {
			sendError(t);
		}
		return response;
	}

	@POST
	@Path("/oauth/token")
	public Response oauthToken(String body) {
		Response response = null;
		String username = null;
		try {
			JSONObject json = new JSONObject(body);
			checkOauthJSON(json);

			// 36953 - inizio
			// internalOpenSession(json);

			OAuthResponse or = buildOAuthResponse();
			Gson gson = new Gson();
			String responseBody = gson.toJson(or);

			internalCloseSession();
			// 36953 - fine
			if (or.access_token != null)
				response = buildResponse(Status.CREATED, responseBody);
			else
				response = buildResponse(Status.UNAUTHORIZED, "Utente o password errati");
//		} catch (UserNotFoundException e) {
//			sendError(Status.UNAUTHORIZED, "BAS0000007", username);
//		} catch (WrongPasswordException e) {
//			sendError(Status.UNAUTHORIZED, "BAS0000009");
		} catch (PantheraApiException pae) {
			throw pae;
		} catch (Throwable t) {
			sendError(t);
		}
		return response;
	}

	protected static final String SELECT_USER_BY_TOKEN = "SELECT " + UserTM.ID + "," + UserTM.HASHED_PWD + " FROM "
			+ UserTM.TABLE_NAME + " WHERE " + UserTM.TOKEN + " = ?";

	protected static CachedStatement cSelectUserByToken = new CachedStatement(SELECT_USER_BY_TOKEN);

	private static synchronized String credentialsByToken(String token) {

		String username = null;
		String password = null;

		try {
			PreparedStatement ps = cSelectUserByToken.getStatement();

			ps.setString(1, token);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				username = rs.getString(UserTM.ID);
				password = rs.getString(UserTM.HASHED_PWD);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace(Trace.excStream);
			throw new ExceptionTransporterException(e);
		}
		if (username == null) {
			return null;
		}

		return KeyHelper.buildObjectKey(new String[] { username.trim(), password.trim() });
	}

	private void internalOpenSession(JSONObject credentials)
			throws SQLException, UserNotFoundException, WrongPasswordException {
		String username = null;
		String password = null;

		String grant_type = credentials.getString("grant_type");
		if (grant_type.equals("password")) {
			username = credentials.getString("username");
			password = credentials.getString("password");
		} else { // grant_type == therm_token
			String therm_token = credentials.getString("therm_token");
			String usernameAndPassword = credentialsByToken(therm_token);
			if (usernameAndPassword == null) {
				sendError(Status.UNAUTHORIZED, "THIP400007", "The therm_token provided is invalid.");
			}
			String[] parts = KeyHelper.unpackObjectKey(usernameAndPassword);
			username = parts[0];
			password = parts[1];
		}

		// 36953 - inizio
		internalOpenSession(username, password);
		// 36953 - fine
	}

	// 36953 - inizio
	private void internalOpenSession(String username, String password)
			throws SQLException, UserNotFoundException, WrongPasswordException {
		if (Security.isSessionOpened()) {
			Trace.excStream
					.println("ERRORE [AuthenticationResource]: sul thread corrente (" + Thread.currentThread().getName()
							+ ") esiste sessione aperta per l'utente " + Security.getSession() + " - la chiudo!");
			Security.closeSession();
		}
		Security.openSession(username, password);
	}

	private void internalCloseSession() {
		if (Security.isSessionOpened()) {
			Security.closeSession();
		}
	}
	// 36953 - fine

	private void checkOauthJSON(JSONObject json) {
		String grantType = json.getString("grant_type");
		if (!(grantType.equals("password") || grantType.equals("therm_token"))) {
			sendError(Status.UNAUTHORIZED, "THIP400007", "Invalid grant_type: accepted values: password|therm_token");
		}
		if (grantType.equals("password")) {
			if (!json.has("username")) {
				sendError(Status.UNAUTHORIZED, "THIP400007", "Field 'username' not provided.");
			}
			if (!json.has("password")) {
				sendError(Status.UNAUTHORIZED, "THIP400007", "Field 'password' not provided.");
			}
		} else {
			if (!json.has("therm_token")) {
				sendError(Status.UNAUTHORIZED, "THIP400007", "Field 'therm_token' not provided.");
			}
		}
	}

	// fix 37145 - inizio
	public OAuthResponse buildOAuthResponse() {
		return buildOAuthResponse(null, null);
	}
	// fix 37145 - fine

	public OAuthResponse buildOAuthResponse(String sessionId, Integer sessionTimeout) {
		int timeOut = AuthenticationSettings.getInstance().getTimeout();

		// fix 37145 - inizio
		if (sessionTimeout != null) {
			timeOut = 86400; // 24H - il token viene comunque rigettato se ha un claim jsessionid e la
								// sessione è cambiata
		}
		// fix 37145 - fine

		String secret = AuthenticationSettings.getInstance().getSecret();
		String issuer = AuthenticationSettings.getInstance().getIssuer();

		Date issueDate = TimeUtils.getCurrentTimestamp();
		Date expirationDate = TimeUtils.addSeconds(issueDate, timeOut);

		User currentUser = Security.getCurrentUser();
//		String userId = currentUser.getId();

		// 37046 - inizio
		ThermJwtClaimsManager claimsManager = (ThermJwtClaimsManager) Factory.createObject(ThermJwtClaimsManager.class);
		Map<String, String> claims = new HashMap<String, String>();// claimsManager.claims();
		// 37046 - fine

		// fix 37145 - inizio
		if (sessionId != null) {
			claims.put(SESSION_ID_CLAIM, sessionId);
		}
		// fix 37145 - fine
		String userId = "ADMIN";
		String token = buildToken(issuer, userId, issueDate, expirationDate, claims, secret);

		OAuthResponse response = new OAuthResponse();
		response.access_token = token;
		response.token_type = "Bearer";
		response.expires_in = timeOut;
		return response;
	}

	private JSONObject envelope(String token) {
		JSONObject json = new JSONObject();
		json.put("token", token);
		return json;
	}

	public String issueToken() {
		return buildOAuthResponse().access_token;
	}

	// fix 37145 - inizio
	public String issueSessionRelatedJwtToken(HttpServletRequest request) {
		String sessionId = null;
		Integer sessionTimeout = null;
		if (request != null) {
			HttpSession session = request.getSession();

			if (session != null) {
				sessionId = session.getId();
				sessionTimeout = session.getMaxInactiveInterval();
			}
		}

		return buildOAuthResponse(sessionId, sessionTimeout).access_token;
	}
	// fix 37145 - fine

	private String buildToken(String issuer, String userId, Date issueDate, Date expirationDate,
			Map<String, String> claims, String secret) {

		JwtBuilder builder = Jwts.builder();
		builder.setId(UUID.randomUUID().toString()).setIssuer(issuer).setSubject(userId);

		for (Map.Entry<String, String> claim : claims.entrySet()) {
			builder.claim(claim.getKey(), claim.getValue());
		}

		builder.setIssuedAt(issueDate).setExpiration(expirationDate).signWith(SignatureAlgorithm.HS256, secret);

		return builder.compact();
	}

	private static class OAuthResponse {
		private String access_token;
		private String token_type;
		private int expires_in;
	}

}
