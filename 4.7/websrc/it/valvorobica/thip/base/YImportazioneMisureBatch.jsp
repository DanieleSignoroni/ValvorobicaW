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
  BODataCollector YImportMisureBODC = null; 
  List errors = new ArrayList(); 
  WebJSTypeList jsList = new WebJSTypeList(); 
  WebForm YImportMisureForm =  
     new com.thera.thermfw.web.WebFormForBatchForm(request, response, "YImportMisureForm", "YImportMisure", "Arial,10", "it.valvorobica.thip.base.web.YImportazioneMisureBatchFormActionAdapter", false, false, true, true, true, true, null, 0, true, "it/valvorobica/thip/base/YImportazioneMisureBatch.js"); 
  YImportMisureForm.setServletEnvironment(se); 
  YImportMisureForm.setJSTypeList(jsList); 
  YImportMisureForm.setHeader("it.thera.thip.cs.Header.jsp"); 
  YImportMisureForm.setFooter("it.thera.thip.cs.Footer.jsp"); 
  ((WebFormForBatchForm)  YImportMisureForm).setGenerateThReportId(true); 
  ((WebFormForBatchForm)  YImportMisureForm).setGenerateSSDEnabled(true); 
  YImportMisureForm.setDeniedAttributeModeStr("hideNone"); 
  int mode = YImportMisureForm.getMode(); 
  String key = YImportMisureForm.getKey(); 
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
        YImportMisureForm.outTraceInfo(getClass().getName()); 
        String collectorName = YImportMisureForm.findBODataCollectorName(); 
				 YImportMisureBODC = (BODataCollector)Factory.createObject(collectorName); 
        if (YImportMisureBODC instanceof WebDataCollector) 
            ((WebDataCollector)YImportMisureBODC).setServletEnvironment(se); 
        YImportMisureBODC.initialize("YImportMisure", true, 0); 
        int rcBODC; 
        if (YImportMisureBODC.getBo() instanceof BatchRunnable) 
          rcBODC = YImportMisureBODC.initSecurityServices("RUN", mode, true, false, true); 
        else 
          rcBODC = YImportMisureBODC.initSecurityServices(mode, true, true, true); 
        if (rcBODC == BODataCollector.OK) 
        { 
           requestIsValid = true; 
           YImportMisureForm.write(out); 
           if(mode != WebForm.NEW) 
              rcBODC = YImportMisureBODC.retrieve(key); 
           if(rcBODC == BODataCollector.OK) 
           { 
              YImportMisureForm.setBODataCollector(YImportMisureBODC); 
              YImportMisureForm.writeHeadElements(out); 
           // fine blocco XXX  
           // a completamento blocco di codice YYY a fine body con catch e gestione errori 
%> 

<% 
  WebMenuBar menuBar = new com.thera.thermfw.web.WebMenuBar("HM_Array1", "150", "#000000","#000000","#A5B6CE","#E4EAEF","#FFFFFF","#000000"); 
  menuBar.setParent(YImportMisureForm); 
   request.setAttribute("menuBar", menuBar); 
%> 
<jsp:include page="/com/thera/thermfw/batch/BatchRunnableMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="menuBar"/> 
</jsp:include> 
<% 
  menuBar.write(out); 
  menuBar.writeChildren(out); 
%> 
<% 
  WebToolBar myToolBarTB = new com.thera.thermfw.web.WebToolBar("myToolBar", "24", "24", "16", "16", "#f7fbfd","#C8D6E1"); 
  myToolBarTB.setParent(YImportMisureForm); 
   request.setAttribute("toolBar", myToolBarTB); 
%> 
<jsp:include page="/com/thera/thermfw/batch/BatchRunnableMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="toolBar"/> 
</jsp:include> 
<% 
   myToolBarTB.write(out); 
%> 
</head>

<body bottommargin="0" leftmargin="0" onbeforeunload="<%=YImportMisureForm.getBodyOnBeforeUnload()%>" onload="<%=YImportMisureForm.getBodyOnLoad()%>" onunload="<%=YImportMisureForm.getBodyOnUnload()%>" rightmargin="0" topmargin="0"><%
   YImportMisureForm.writeBodyStartElements(out); 
%> 


	<% 
  WebScript script_0 =  
   new com.thera.thermfw.web.WebScript(); 
 script_0.setRequest(request); 
 script_0.setSrcAttribute("it/thera/thip/cs/util.js"); 
 script_0.setLanguageAttribute("JavaScript1.2"); 
  script_0.write(out); 
%>
<!--<script language="JavaScript1.2" src="it/thera/thip/cs/util.js" type="text/javascript"></script>-->
	<table width="100%" height="100%" cellspacing="0" cellpadding="0">
<tr>
<td style="height:0" valign="top">
<% String hdr = YImportMisureForm.getCompleteHeader();
 if (hdr != null) { 
   request.setAttribute("dataCollector", YImportMisureBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= hdr %>" flush="true"/> 
<% } %> 
</td>
</tr>

<tr>
<td valign="top" height="100%">
<form action="<%=YImportMisureForm.getServlet()%>" method="post" name="myForm" style="height:100%"><%
  YImportMisureForm.writeFormStartElements(out); 
%>
<!-- Fix 18758 -->
		<table cellpadding="2" cellspacing="0" height="98%" id="emptyborder" width="100%">
			<tr>
				<td style="height:0"><% menuBar.writeElements(out); %> 
</td>
			</tr>
			<tr>
				<td style="height:0"><% myToolBarTB.writeChildren(out); %> 
</td>
			</tr>
			<!--tr><td-->
			<!--table height="100%" width="100%" border="0" Fix 12105-->
			<tr>
				<td colspan="2" style="height:0"><% 
  WebTextInput YImportMisureIdAzienda =  
     new com.thera.thermfw.web.WebTextInput("YImportMisure", "IdAzienda"); 
  YImportMisureIdAzienda.setParent(YImportMisureForm); 
%>
<input class="<%=YImportMisureIdAzienda.getClassType()%>" id="<%=YImportMisureIdAzienda.getId()%>" maxlength="<%=YImportMisureIdAzienda.getMaxLength()%>" name="<%=YImportMisureIdAzienda.getName()%>" size="<%=YImportMisureIdAzienda.getSize()%>" type="hidden"><% 
  YImportMisureIdAzienda.write(out); 
%>
</td>
			</tr>
			<tr>
				<td height="100%">
					<!--<span class="tabbed" id="myTabbed">-->
<table width="100%" height="100%" cellpadding="0" cellspacing="0" style="padding-right:1px">
   <tr valign="top">
     <td><% 
  WebTabbed myTabbed = new com.thera.thermfw.web.WebTabbed("myTabbed", "100%", "100%"); 
  myTabbed.setParent(YImportMisureForm); 
 myTabbed.addTab("generaleTab", "it.valvorobica.thip.base.resources.YImportazioneMisureBatch", "Generale", "YImportMisure", null, null, null, null); 
  myTabbed.write(out); 
%>

     </td>
   </tr>
   <tr>
     <td height="100%"><div class="tabbed_pagine" id="tabbedPagine" style="position: relative; width: 100%; height: 100%;">
						<div class="tabbed_page" id="<%=myTabbed.getTabPageId("generaleTab")%>" style="width:100%;height:100%;overflow:auto;"><% myTabbed.startTab("generaleTab"); %>
							<table border="0" style="margin: 7 7 7 7;" width="98%">
								<tr>
									<td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YImportMisure", "IdFornitore", null); 
   label.setParent(YImportMisureForm); 
%><label class="<%=label.getClassType()%>" for="Fornitore"><%label.write(out);%></label><%}%></td>
									<td><% 
  WebMultiSearchForm YImportMisureFornitore =  
     new com.thera.thermfw.web.WebMultiSearchForm("YImportMisure", "Fornitore", false, false, true, 1, null, null); 
  YImportMisureFornitore.setParent(YImportMisureForm); 
  YImportMisureFornitore.write(out); 
%>
<!--<span class="multisearchform" id="Fornitore"></span>--></td>
								</tr>
								<tr>
									<td><label id="SceltaFile">File CSV.</label></td>
									<td><input id="NomeFile" name="NomeFile" size="90" style=" width: 300px;height: 100px;" type="file"></td>
								</tr>
							</table>
						<% myTabbed.endTab(); %> 
</div>
					</div><% myTabbed.endTabbed();%> 

     </td>
   </tr>
</table><!--</span>-->
				</td>
			</tr>
			<tr>
				<td style="height:0"><% 
  WebErrorList errorList = new com.thera.thermfw.web.WebErrorList(); 
  errorList.setParent(YImportMisureForm); 
  errorList.write(out); 
%>
<!--<span class="errorlist"></span>--></td>
			</tr>
		</table>
	<%
  YImportMisureForm.writeFormEndElements(out); 
%>
</form></td>
</tr>

<tr>
<td style="height:0">
<% String ftr = YImportMisureForm.getCompleteFooter();
 if (ftr != null) { 
   request.setAttribute("dataCollector", YImportMisureBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= ftr %>" flush="true"/> 
<% } %> 
</td>
</tr>
</table>


<%
           // blocco YYY  
           // a completamento blocco di codice XXX in head 
              YImportMisureForm.writeBodyEndElements(out); 
           } 
           else 
              errors.addAll(0, YImportMisureBODC.getErrorList().getErrors()); 
        } 
        else 
           errors.addAll(0, YImportMisureBODC.getErrorList().getErrors()); 
           if(YImportMisureBODC.getConflict() != null) 
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
     if(YImportMisureBODC != null && !YImportMisureBODC.close(false)) 
        errors.addAll(0, YImportMisureBODC.getErrorList().getErrors()); 
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
     String errorPage = YImportMisureForm.getErrorPage(); 
%> 
     <jsp:include page="<%=errorPage%>" flush="true"/> 
<% 
  } 
  else 
  { 
     request.setAttribute("ConflictMessages", YImportMisureBODC.getConflict()); 
     request.setAttribute("ErrorMessages", errors); 
     String conflictPage = YImportMisureForm.getConflictPage(); 
%> 
     <jsp:include page="<%=conflictPage%>" flush="true"/> 
<% 
   } 
   } 
%> 
</body>
</html>
