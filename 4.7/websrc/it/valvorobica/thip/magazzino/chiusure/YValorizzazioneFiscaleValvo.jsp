<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN"
                      "file:///K:/Thip/4.7.0/websrcsvil/dtd/xhtml1-transitional.dtd">
<html>
<!-- WIZGEN Therm 2.0.0 as Batch form - multiBrowserGen = true -->
<%=WebGenerator.writeRuntimeInfo()%>
<head>
<%@ page contentType="text/html; charset=Cp1252"%>
<%@ page import= " 
  java.sql.*, 
  java.util.*, 
  java.lang.reflect.*, 
  javax.naming.*, 
  com.thera.thermfw.common.*, 
  com.thera.thermfw.type.*, 
  com.thera.thermfw.web.*, 
  com.thera.thermfw.security.*, 
  com.thera.thermfw.base.*, 
  com.thera.thermfw.ad.*, 
  com.thera.thermfw.persist.*, 
  com.thera.thermfw.gui.cnr.*, 
  com.thera.thermfw.setting.*, 
  com.thera.thermfw.collector.*, 
  com.thera.thermfw.batch.web.*, 
  com.thera.thermfw.batch.*, 
  com.thera.thermfw.pref.* 
"%> 
<%
  ServletEnvironment se = (ServletEnvironment)Factory.createObject("com.thera.thermfw.web.ServletEnvironment"); 
  BODataCollector YValorizzazioneFiscaleValBODC = null; 
  List errors = new ArrayList(); 
  WebJSTypeList jsList = new WebJSTypeList(); 
  WebForm YValorizzazioneFiscaleValForm =  
     new com.thera.thermfw.web.WebFormForBatchForm(request, response, "YValorizzazioneFiscaleValForm", "YValorizzazioneFiscaleVal", "Arial,10", "com.thera.thermfw.batch.web.BatchFormActionAdapter", false, false, true, true, true, true, null, 1, false, null); 
  YValorizzazioneFiscaleValForm.setServletEnvironment(se); 
  YValorizzazioneFiscaleValForm.setJSTypeList(jsList); 
  YValorizzazioneFiscaleValForm.setHeader("it.thera.thip.cs.Header.jsp"); 
  YValorizzazioneFiscaleValForm.setFooter("it.thera.thip.cs.Footer.jsp"); 
  ((WebFormForBatchForm)  YValorizzazioneFiscaleValForm).setGenerateSSDEnabled(true); 
  YValorizzazioneFiscaleValForm.setWebFormModifierClass("it.valvorobica.thip.magazzino.chiusure.web.YValorizzazioneFiscaleValvoFormModifier"); 
  YValorizzazioneFiscaleValForm.setDeniedAttributeModeStr("hideNone"); 
  int mode = YValorizzazioneFiscaleValForm.getMode(); 
  String key = YValorizzazioneFiscaleValForm.getKey(); 
  String errorMessage; 
  boolean requestIsValid = false; 
  boolean leftIsKey = false; 
  boolean conflitPresent = false; 
  String leftClass = ""; 
  try 
  {
     se.initialize(request, response); 
     if(se.begin()) 
     { 
        YValorizzazioneFiscaleValForm.outTraceInfo(getClass().getName()); 
        String collectorName = YValorizzazioneFiscaleValForm.findBODataCollectorName(); 
				 YValorizzazioneFiscaleValBODC = (BODataCollector)Factory.createObject(collectorName); 
        if (YValorizzazioneFiscaleValBODC instanceof WebDataCollector) 
            ((WebDataCollector)YValorizzazioneFiscaleValBODC).setServletEnvironment(se); 
        YValorizzazioneFiscaleValBODC.initialize("YValorizzazioneFiscaleVal", true, 1); 
        int rcBODC; 
        if (YValorizzazioneFiscaleValBODC.getBo() instanceof BatchRunnable) 
          rcBODC = YValorizzazioneFiscaleValBODC.initSecurityServices("RUN", mode, true, false, true); 
        else 
          rcBODC = YValorizzazioneFiscaleValBODC.initSecurityServices(mode, true, true, true); 
        if (rcBODC == BODataCollector.OK) 
        { 
           requestIsValid = true; 
           YValorizzazioneFiscaleValForm.write(out); 
           if(mode != WebForm.NEW) 
              rcBODC = YValorizzazioneFiscaleValBODC.retrieve(key); 
           if(rcBODC == BODataCollector.OK) 
           { 
              YValorizzazioneFiscaleValForm.setBODataCollector(YValorizzazioneFiscaleValBODC); 
              YValorizzazioneFiscaleValForm.writeHeadElements(out); 
           // fine blocco XXX  
           // a completamento blocco di codice YYY a fine body con catch e gestione errori 
%> 

<title>Valorizzazione fiscale batch</title>
<% 
  WebMenuBar menuBar = new com.thera.thermfw.web.WebMenuBar("HM_Array1", "150", "#000000","#000000","#A5B6CE","#E4EAEF","#FFFFFF","#000000"); 
  menuBar.setParent(YValorizzazioneFiscaleValForm); 
   request.setAttribute("menuBar", menuBar); 
%> 
<jsp:include page="/com/thera/thermfw/batch/PrintRunnableMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="menuBar"/> 
</jsp:include> 
<% 
  menuBar.write(out); 
  menuBar.writeChildren(out); 
%> 
<% 
  WebToolBar myToolBarTB = new com.thera.thermfw.web.WebToolBar("myToolBar", "24", "24", "16", "16", "#f7fbfd","#C8D6E1"); 
  myToolBarTB.setParent(YValorizzazioneFiscaleValForm); 
   request.setAttribute("toolBar", myToolBarTB); 
%> 
<jsp:include page="/com/thera/thermfw/batch/PrintRunnableMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="toolBar"/> 
</jsp:include> 
<% 
   myToolBarTB.write(out); 
%> 
</head>


<body bottommargin="0" leftmargin="0" onbeforeunload="<%=YValorizzazioneFiscaleValForm.getBodyOnBeforeUnload()%>" onload="<%=YValorizzazioneFiscaleValForm.getBodyOnLoad()%>" onunload="<%=YValorizzazioneFiscaleValForm.getBodyOnUnload()%>" rightmargin="0" topmargin="0"><%
   YValorizzazioneFiscaleValForm.writeBodyStartElements(out); 
%> 




	<table width="100%" height="100%" cellspacing="0" cellpadding="0">
<tr>
<td style="height:0" valign="top">
<% String hdr = YValorizzazioneFiscaleValForm.getCompleteHeader();
 if (hdr != null) { 
   request.setAttribute("dataCollector", YValorizzazioneFiscaleValBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= hdr %>" flush="true"/> 
<% } %> 
</td>
</tr>

<tr>
<td valign="top" height="100%">
<form action="<%=YValorizzazioneFiscaleValForm.getServlet()%>" method="post" name="myForm" style="height:100%"><%
  YValorizzazioneFiscaleValForm.writeFormStartElements(out); 
%>

		<table cellpadding="2" cellspacing="0" height="100%" id="emptyborder" width="100%">
			<tr>
				<td><% menuBar.writeElements(out); %> 
</td>
			</tr>
			<tr>
				<td><% myToolBarTB.writeChildren(out); %> 
</td>
			</tr>
			<tr>
				<td>
					<table border="0" cellpadding="0" cellspacing="15">
						<tr>
							<td><% 
  WebTextInput YValorizzazioneFiscaleValIdAzienda =  
     new com.thera.thermfw.web.WebTextInput("YValorizzazioneFiscaleVal", "IdAzienda"); 
  YValorizzazioneFiscaleValIdAzienda.setParent(YValorizzazioneFiscaleValForm); 
%>
<input class="<%=YValorizzazioneFiscaleValIdAzienda.getClassType()%>" id="<%=YValorizzazioneFiscaleValIdAzienda.getId()%>" maxlength="<%=YValorizzazioneFiscaleValIdAzienda.getMaxLength()%>" name="<%=YValorizzazioneFiscaleValIdAzienda.getName()%>" size="<%=YValorizzazioneFiscaleValIdAzienda.getSize()%>" type="Hidden"><% 
  YValorizzazioneFiscaleValIdAzienda.write(out); 
%>
</td>
							<td><input id="thReportId" name="thReportId" type="Hidden"></td>

						</tr>
						<tr>
							<td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YValorizzazioneFiscaleVal", "IdAnnoFiscale", null); 
   label.setParent(YValorizzazioneFiscaleValForm); 
%><label class="<%=label.getClassType()%>" for="AnnoFsc"><%label.write(out);%></label><%}%></td>
							<td><% 
  WebMultiSearchForm YValorizzazioneFiscaleValAnnoFiscale =  
     new com.thera.thermfw.web.WebMultiSearchForm("YValorizzazioneFiscaleVal", "AnnoFiscale", false, false, true, 1, null, null); 
  YValorizzazioneFiscaleValAnnoFiscale.setParent(YValorizzazioneFiscaleValForm); 
  YValorizzazioneFiscaleValAnnoFiscale.setFixedRestrictConditions("StatoAnnoFiscale,A"); 
  YValorizzazioneFiscaleValAnnoFiscale.write(out); 
%>
<!--<span class="multisearchform" id="AnnoFsc" name="AnnoFsc"></span>--></td>
						</tr>
						<tr>
							<td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YValorizzazioneFiscaleVal", "DataUltimaChiusura", null); 
   label.setParent(YValorizzazioneFiscaleValForm); 
%><label class="<%=label.getClassType()%>" for="DataUltimaChiusura"><%label.write(out);%></label><%}%></td>
							<td><% 
  WebTextInput YValorizzazioneFiscaleValDataUltimaChiusura =  
     new com.thera.thermfw.web.WebTextInput("YValorizzazioneFiscaleVal", "DataUltimaChiusura"); 
  YValorizzazioneFiscaleValDataUltimaChiusura.setParent(YValorizzazioneFiscaleValForm); 
%>
<input class="<%=YValorizzazioneFiscaleValDataUltimaChiusura.getClassType()%>" id="<%=YValorizzazioneFiscaleValDataUltimaChiusura.getId()%>" maxlength="<%=YValorizzazioneFiscaleValDataUltimaChiusura.getMaxLength()%>" name="<%=YValorizzazioneFiscaleValDataUltimaChiusura.getName()%>" size="<%=YValorizzazioneFiscaleValDataUltimaChiusura.getSize()%>"><% 
  YValorizzazioneFiscaleValDataUltimaChiusura.write(out); 
%>
</td>
						</tr>
						<tr>
							<td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YValorizzazioneFiscaleVal", "StatoChiusuraMag", null); 
   label.setParent(YValorizzazioneFiscaleValForm); 
%><label class="<%=label.getClassType()%>" for="StatoChiusuraMag"><%label.write(out);%></label><%}%></td>
							<td><% 
  WebComboBox YValorizzazioneFiscaleValStatoChiusuraMag =  
     new com.thera.thermfw.web.WebComboBox("YValorizzazioneFiscaleVal", "StatoChiusuraMag", null); 
  YValorizzazioneFiscaleValStatoChiusuraMag.setParent(YValorizzazioneFiscaleValForm); 
%>
<select id="<%=YValorizzazioneFiscaleValStatoChiusuraMag.getId()%>" name="<%=YValorizzazioneFiscaleValStatoChiusuraMag.getName()%>"><% 
  YValorizzazioneFiscaleValStatoChiusuraMag.write(out); 
%> 

									
							</select></td>
						</tr>
						<tr>
							<td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YValorizzazioneFiscaleVal", "PrcIncrementoMatProd", null); 
   label.setParent(YValorizzazioneFiscaleValForm); 
%><label class="<%=label.getClassType()%>" for="PrcIncrementoMatProd"><%label.write(out);%></label><%}%></td>
							<td><% 
  WebTextInput YValorizzazioneFiscaleValPrcIncrementoMatProd =  
     new com.thera.thermfw.web.WebTextInput("YValorizzazioneFiscaleVal", "PrcIncrementoMatProd"); 
  YValorizzazioneFiscaleValPrcIncrementoMatProd.setParent(YValorizzazioneFiscaleValForm); 
%>
<input class="<%=YValorizzazioneFiscaleValPrcIncrementoMatProd.getClassType()%>" id="<%=YValorizzazioneFiscaleValPrcIncrementoMatProd.getId()%>" maxlength="<%=YValorizzazioneFiscaleValPrcIncrementoMatProd.getMaxLength()%>" name="<%=YValorizzazioneFiscaleValPrcIncrementoMatProd.getName()%>" size="<%=YValorizzazioneFiscaleValPrcIncrementoMatProd.getSize()%>"><% 
  YValorizzazioneFiscaleValPrcIncrementoMatProd.write(out); 
%>
</td>
						</tr>
						<tr>
							<td><% 
  WebCheckBox YValorizzazioneFiscaleValCalcoloCmp =  
     new com.thera.thermfw.web.WebCheckBox("YValorizzazioneFiscaleVal", "CalcoloCmp"); 
  YValorizzazioneFiscaleValCalcoloCmp.setParent(YValorizzazioneFiscaleValForm); 
%>
<input id="<%=YValorizzazioneFiscaleValCalcoloCmp.getId()%>" name="<%=YValorizzazioneFiscaleValCalcoloCmp.getName()%>" type="checkbox" value="Y"><%
  YValorizzazioneFiscaleValCalcoloCmp.write(out); 
%>
</td>
						</tr>
						<tr>
							<td><% 
  WebCheckBox YValorizzazioneFiscaleValStampaCMP =  
     new com.thera.thermfw.web.WebCheckBox("YValorizzazioneFiscaleVal", "StampaCMP"); 
  YValorizzazioneFiscaleValStampaCMP.setParent(YValorizzazioneFiscaleValForm); 
%>
<input id="<%=YValorizzazioneFiscaleValStampaCMP.getId()%>" name="<%=YValorizzazioneFiscaleValStampaCMP.getName()%>" type="checkbox" value="Y"><%
  YValorizzazioneFiscaleValStampaCMP.write(out); 
%>
</td>
						</tr>
						<tr>
							<td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YValorizzazioneFiscaleVal", "AzzeraCostiManuali", null); 
   label.setParent(YValorizzazioneFiscaleValForm); 
%><label class="<%=label.getClassType()%>" for="AzzeraCostiManuali"><%label.write(out);%></label><%}%></td>
							<td><% 
  WebComboBox YValorizzazioneFiscaleValAzzeraCostiManuali =  
     new com.thera.thermfw.web.WebComboBox("YValorizzazioneFiscaleVal", "AzzeraCostiManuali", null); 
  YValorizzazioneFiscaleValAzzeraCostiManuali.setParent(YValorizzazioneFiscaleValForm); 
%>
<select id="<%=YValorizzazioneFiscaleValAzzeraCostiManuali.getId()%>" name="<%=YValorizzazioneFiscaleValAzzeraCostiManuali.getName()%>"><% 
  YValorizzazioneFiscaleValAzzeraCostiManuali.write(out); 
%> 

									
							</select></td>
						</tr>
						<tr>
							<td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YValorizzazioneFiscaleVal", "IdCosto", null); 
   label.setParent(YValorizzazioneFiscaleValForm); 
%><label class="<%=label.getClassType()%>" for="TipoCosto"><%label.write(out);%></label><%}%></td>
							<td><% 
  WebMultiSearchForm YValorizzazioneFiscaleValTipoCosto =  
     new com.thera.thermfw.web.WebMultiSearchForm("YValorizzazioneFiscaleVal", "TipoCosto", false, false, true, 1, null, null); 
  YValorizzazioneFiscaleValTipoCosto.setParent(YValorizzazioneFiscaleValForm); 
  YValorizzazioneFiscaleValTipoCosto.write(out); 
%>
<!--<span class="multisearchform" id="TipoCosto" name="TipoCosto"></span>--></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td height="100%"></td>
			</tr>
			<tr>
				<td><% 
  WebErrorList errorList = new com.thera.thermfw.web.WebErrorList(); 
  errorList.setParent(YValorizzazioneFiscaleValForm); 
  errorList.write(out); 
%>
<!--<span class="errorlist"></span>--></td>
			</tr>
			<tr>
				<td><iframe height="0" id="ExtraWorkFrame" name="ExtraWorkFrame" style="display: none" width="0"></iframe></td>
			</tr>
		</table>
	<%
  YValorizzazioneFiscaleValForm.writeFormEndElements(out); 
%>
</form></td>
</tr>

<tr>
<td style="height:0">
<% String ftr = YValorizzazioneFiscaleValForm.getCompleteFooter();
 if (ftr != null) { 
   request.setAttribute("dataCollector", YValorizzazioneFiscaleValBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= ftr %>" flush="true"/> 
<% } %> 
</td>
</tr>
</table>



<%
           // blocco YYY  
           // a completamento blocco di codice XXX in head 
              YValorizzazioneFiscaleValForm.writeBodyEndElements(out); 
           } 
           else 
              errors.addAll(0, YValorizzazioneFiscaleValBODC.getErrorList().getErrors()); 
        } 
        else 
           errors.addAll(0, YValorizzazioneFiscaleValBODC.getErrorList().getErrors()); 
           if(YValorizzazioneFiscaleValBODC.getConflict() != null) 
                conflitPresent = true; 
     } 
     else 
        errors.add(new ErrorMessage("BAS0000010")); 
  } 
  catch(NamingException e) { 
     errorMessage = e.getMessage(); 
     errors.add(new ErrorMessage("CBS000025", errorMessage));  } 
  catch(SQLException e) {
     errorMessage = e.getMessage(); 
     errors.add(new ErrorMessage("BAS0000071", errorMessage));  } 
  catch(Throwable e) {
     e.printStackTrace(Trace.excStream);
  }
  finally 
  {
     if(YValorizzazioneFiscaleValBODC != null && !YValorizzazioneFiscaleValBODC.close(false)) 
        errors.addAll(0, YValorizzazioneFiscaleValBODC.getErrorList().getErrors()); 
     try 
     { 
        se.end(); 
     }
     catch(IllegalArgumentException e) { 
        e.printStackTrace(Trace.excStream); 
     } 
     catch(SQLException e) { 
        e.printStackTrace(Trace.excStream); 
     } 
  } 
  if(!errors.isEmpty())
  { 
      if(!conflitPresent)
  { 
     request.setAttribute("ErrorMessages", errors); 
     String errorPage = YValorizzazioneFiscaleValForm.getErrorPage(); 
%> 
     <jsp:include page="<%=errorPage%>" flush="true"/> 
<% 
  } 
  else 
  { 
     request.setAttribute("ConflictMessages", YValorizzazioneFiscaleValBODC.getConflict()); 
     request.setAttribute("ErrorMessages", errors); 
     String conflictPage = YValorizzazioneFiscaleValForm.getConflictPage(); 
%> 
     <jsp:include page="<%=conflictPage%>" flush="true"/> 
<% 
   } 
   } 
%> 
</body>
<% 
  WebScript script_0 =  
   new com.thera.thermfw.web.WebScript(); 
 script_0.setRequest(request); 
 script_0.setSrcAttribute("com/thera/thermfw/batch/PrintBatchRunnable.js"); 
 script_0.setLanguageAttribute("JavaScript1.2"); 
  script_0.write(out); 
%>
<!--<script language="JavaScript1.2" src="com/thera/thermfw/batch/PrintBatchRunnable.js" type="text/javascript"></script>-->
</html>
