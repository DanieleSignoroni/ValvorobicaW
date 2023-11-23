<%@page import="it.thera.thip.base.azienda.AziendaTM"%>
<%@page import="it.valvorobica.thip.base.portal.YAziendeUserPortalTM"%>
<%@page import="it.valvorobica.thip.base.portal.YUserPortalTM"%>
<%@page import="java.util.ArrayList"%>
<%@page import="it.thera.thip.ws.GenericQuery"%>
<%@page import="it.valvorobica.thip.base.portal.YUserPortalSession"%>
<%@page import="it.valvorobica.thip.base.generale.ws.utils.YUserToken"%>
<%@page import="com.thera.thermfw.base.Trace"%>
<%@page import="com.thera.thermfw.persist.ConnectionManager"%>
<%@page import="com.thera.thermfw.persist.ConnectionDescriptor"%>
<%@page import="com.thera.thermfw.security.Security"%>
<%@page import="com.thera.thermfw.web.SessionEnvironment"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.thera.thermfw.base.IniFile"%>
<%@page import="it.valvorobica.thip.base.generale.ws.utils.YUtilsPortal"%>
<%@page import="it.valvorobica.thip.base.generale.ws.dati.YDatiClientePortale"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="it.valvorobica.thip.base.generale.ws.YAnagrafica"%>
<%@page import="com.thera.thermfw.persist.Factory"%>
<%@page import="com.thera.thermfw.web.ServletEnvironment"%>
<%

ServletEnvironment se = (ServletEnvironment)Factory.createObject("com.thera.thermfw.web.ServletEnvironment"); 
se.initialize(request, response);
String fun = request.getParameter("fun");
//String token = request.getParameter("token");
String webAppPath = IniFile.getValue("thermfw.ini", "Web", "WebApplicationPath");
se.getRequest().setAttribute("webAppPath", webAppPath);
String url = "it/valvorobica/thip/base/portal/YSchedaFiltri.jsp";
String payload = null;
YUserPortalSession userPortalSession = (YUserPortalSession) request.getSession().getAttribute("YUserPortal");
String token = userPortalSession.getTokecUID();
int funzione = Integer.parseInt(fun);
switch(funzione){
case YUtilsPortal.FUNZIONE_CLIENTI:
	se.sendRequest(getServletContext(), "it/valvorobica/thip/base/portal/YSchedaClientiAgente.jsp", false);
	break;
case YUtilsPortal.FUNZIONE_ANAGAFICA:
	//YAnagrafica anag = (YAnagrafica) Factory.createObject(YAnagrafica.class);
	//anag.setToken(token);
	//payload = anag.toOutputFormat(response); //qui avviene la sottomissione del ws
	//Gson gson = new Gson();
	//YDatiClientePortale datiCliente = gson.fromJson(payload, YDatiClientePortale.class);
	//request.setAttribute("daticli", datiCliente);
	Map values = new LinkedHashMap();
	boolean isopen = false;
	Object[] info = SessionEnvironment.getDBInfoFromIniFile();
	String dbName = (String)info[0];
	String newsHtml = null;
	try {		
		if(!Security.isCurrentDatabaseSetted()) {
			Security.setCurrentDatabase(dbName, null);
		}
		Security.openDefaultConnection();
		isopen = true;
		ConnectionDescriptor cd = ConnectionManager.getCurrentConnectionDescriptor();
		YAnagrafica ws = new YAnagrafica();
		ws.setUseAuthentication(false);
		ws.setUseAuthorization(false);
		ws.setUseLicence(false);
		ws.setConnectionDescriptor(cd);
		//ws.setToken(token);
		ws.setUserSession(userPortalSession);
		ws.setKeyCliente(userPortalSession.getIdCliente());
		values = ws.send();	
		//request.setAttribute("daticli", values.get("daticli"));
		url = "/" + webAppPath + "/it/valvorobica/thip/base/portal/YSchedaAnagafica.jsp?token="+token;
		//se.sendRequest(getServletContext(), "it/valvorobica/thip/base/portal/YSchedaAnagafica.jsp", false);
		%>
		<script type="text/javascript">window.location = "<%=url%>";</script>
		<% 
	}
	catch (Throwable t) {
		t.printStackTrace(Trace.excStream);
	}
	finally {
		if (isopen) {
			Security.closeDefaultConnection();
		}
	}
	break;
case YUtilsPortal.FUNZIONE_MANUALI_DICH:
	se.sendRequest(getServletContext(), "it/valvorobica/thip/base/portal/YSchedaManualiDich.jsp", false);
	break;
case YUtilsPortal.FUNZIONE_OFFERTE:
	url += "?jspPage=YSchedaOfferte.jsp&token="+token;
	se.sendRequest(getServletContext(), url, false);
	break;
case YUtilsPortal.FUNZIONE_ORDINI:
	url += "?jspPage=YSchedaOrdini.jsp&token="+token;
	se.sendRequest(getServletContext(), url, false);
	break;
case YUtilsPortal.FUNZIONE_DDT:
	url += "?jspPage=YSchedaDDT.jsp&token="+token;
	se.sendRequest(getServletContext(), url, false);
	break;
case YUtilsPortal.FUNZIONE_FATTURE:
	url += "?jspPage=YSchedaFatture.jsp&token="+token;
	se.sendRequest(getServletContext(), url, false);
	break;
case YUtilsPortal.FUNZIONE_INEVASO:
	String clienteAttivo = (String)se.getSession().getAttribute("YClienteAttivo");
	//YUserToken user = YUserToken.getUserJWTFromToken(token);
	url = "it/valvorobica/thip/base/portal/YSchedaInevaso.jsp?token="+token;
	if(clienteAttivo != null)
		url += "&clienteAttivo=" + clienteAttivo;
	else if(clienteAttivo == null && (userPortalSession.getTipoUser().equals(YUserPortalSession.AGENTE)
			|| userPortalSession.getTipoUser().equals(YUserPortalSession.DIPENDENTE))){ //anche i dipendenti funzionano allo stesso modo
		%>
		<script>
			window.alert('Cliente non attivo');
			parent.document.getElementById('preloader').style.display = "none";
		</script>		
		<%
		payload = "CLIENTE NON ATTIVO, PER POTER VISUALIZZARE GLI INEVASI ATTIVARE UN CLIENTE \n "
				  + " ANDARE QUINDI NELLA VOCE CLIENTI, POSIZIONARSI SULLA RIGA DESIDERATA E CLICCARE IL  BOTTONE 'SELEZIONA'";
		break;
	}
	se.sendRequest(getServletContext(), url, false);
	break;
case YUtilsPortal.FUNZIONE_CERTIFICATI:
	url = "it/valvorobica/thip/base/portal/YSchedaFiltriCertificati.jsp";
	url += "?jspPage=YSchedaCertificati.jsp&token="+token;
	se.sendRequest(getServletContext(), url, false);
	break;
case YUtilsPortal.FUNZIONE_PROSPECT_CLIENTI:
	url = "it/valvorobica/thip/base/portal/YSchedaProspect.jsp?token="+token;
	se.sendRequest(getServletContext(), url, false);
	break;
case YUtilsPortal.FUNZIONE_CAMBIO_PASSWORD:
	url = "it/valvorobica/thip/base/portal/YSchedaCambioPwd.jsp?token="+token+"&exit=no";
	se.sendRequest(getServletContext(), url, false);
	break;
case YUtilsPortal.FUNZIONE_LOGOUT:
%>
	<script>
		window.parent.location = "/<%= YUserPortalSession.WEB_APP_PATH %>/customersportal";
	</script>
<%
// 	url = "/customersportal";
// 	se.sendRequest(getServletContext(), url, false);
	payload = "PROCESSANDO IL LOG OUT.....";
	request.getSession().invalidate();
	break;
//Nuova funzione cambio azienda, al click lancio la jsp della scelta azienda, passando in chiaro l'utente
case YUtilsPortal.FUNZIONE_CAMBIO_AZIENDA:
	String jspPage ="/"+webAppPath+"/it/valvorobica/thip/base/portal/YSceltaAziendePortal.jsp?idUtente="+userPortalSession.getIdUtente();
	se.getSession().removeAttribute("YClienteAttivo"); //importante, altrimenti  vedono altri clienti nell'altra azienda
	%>
	<script>
<%-- 		window.parent.location = "<%= YUserPortalSession.WEB_APP_PATH +  "/"+ srvlPth + "/it.valvorobica.thip.base.portal.web.YCambioAziendaPortal" %>"; --%>
	window.parent.location = "<%=jspPage%>";
	</script>
	<%
	break;
case YUtilsPortal.FUNZIONE_COMPILAZIONE_OFFERTA_SUPPLIER:
	url = "it/valvorobica/thip/base/portal/YSchedaOffertaFornitoreSpecifica.jsp";
	se.sendRequest(getServletContext(), url, false);
	break;
case YUtilsPortal.FUNZIONE_CATALOGO:
	url = "it/valvorobica/thip/base/portal/YSchedaCatalogo.jsp";
	se.sendRequest(getServletContext(), url, false);
	break;
case YUtilsPortal.FUNZIONE_CARRELLO:
	url = "it/valvorobica/thip/base/portal/YSchedaCarrello.jsp";
	se.sendRequest(getServletContext(), url, false);
	break;
default:
	payload = "Funzione non ancora gestita";
}
%>

<%=payload%>

