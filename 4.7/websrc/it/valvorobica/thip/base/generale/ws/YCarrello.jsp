<%@page import="it.valvorobica.thip.base.generale.ws.YCarrello"%>
<%@page import="it.thera.thip.ws.*"%>
<%@page import="com.thera.thermfw.persist.Factory"%>
<%
	String query = request.getQueryString();
	String pageJsp = "/servlet/it.thera.thip.ws.servlet.WebServiceExecutor";
	String classInstance = YCarrello.class.getName();
	classInstance = Factory.getName(classInstance, Factory.CLASS);
	query = query == null ? "" : "&" + query;
	String url = pageJsp + "?classInstance=" + classInstance + query;
%>
<jsp:forward page="<%=url%>"/> 
