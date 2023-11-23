<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN"
                      "file:///K:/Thip/4.7.0/websrcsvil/dtd/xhtml1-transitional.dtd">
<html>
<!-- WIZGEN Therm 2.0.0 as Form - multiBrowserGen = true -->
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
  BODataCollector YImgCatalogoPortaleBODC = null; 
  List errors = new ArrayList(); 
  WebJSTypeList jsList = new WebJSTypeList(); 
  WebForm YImgCatalogoPortaleForm =  
     new com.thera.thermfw.web.WebForm(request, response, "YImgCatalogoPortaleForm", "YImgCatalogoPortale", null, "it.valvorobica.thip.base.portal.web.YImgCatalogoPortaleFormActionAdapter", false, false, true, true, true, true, null, 0, true, "it/valvorobica/thip/base/portal/YImgCatalogoPortale.js"); 
  YImgCatalogoPortaleForm.setServletEnvironment(se); 
  YImgCatalogoPortaleForm.setJSTypeList(jsList); 
  YImgCatalogoPortaleForm.setHeader("it.thera.thip.cs.PantheraHeader.jsp"); 
  YImgCatalogoPortaleForm.setFooter("com.thera.thermfw.common.Footer.jsp"); 
  YImgCatalogoPortaleForm.setWebFormModifierClass("it.valvorobica.thip.base.portal.web.YImgCatalogoPortaleFormModifier"); 
  YImgCatalogoPortaleForm.setDeniedAttributeModeStr("hideNone"); 
  int mode = YImgCatalogoPortaleForm.getMode(); 
  String key = YImgCatalogoPortaleForm.getKey(); 
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
        YImgCatalogoPortaleForm.outTraceInfo(getClass().getName()); 
        String collectorName = YImgCatalogoPortaleForm.findBODataCollectorName(); 
                YImgCatalogoPortaleBODC = (BODataCollector)Factory.createObject(collectorName); 
        if (YImgCatalogoPortaleBODC instanceof WebDataCollector) 
            ((WebDataCollector)YImgCatalogoPortaleBODC).setServletEnvironment(se); 
        YImgCatalogoPortaleBODC.initialize("YImgCatalogoPortale", true, 0); 
        YImgCatalogoPortaleForm.setBODataCollector(YImgCatalogoPortaleBODC); 
        int rcBODC = YImgCatalogoPortaleForm.initSecurityServices(); 
        mode = YImgCatalogoPortaleForm.getMode(); 
        if (rcBODC == BODataCollector.OK) 
        { 
           requestIsValid = true; 
           YImgCatalogoPortaleForm.write(out); 
           if(mode != WebForm.NEW) 
              rcBODC = YImgCatalogoPortaleBODC.retrieve(key); 
           if(rcBODC == BODataCollector.OK) 
           { 
              YImgCatalogoPortaleForm.writeHeadElements(out); 
           // fine blocco XXX  
           // a completamento blocco di codice YYY a fine body con catch e gestione errori 
%> 
<% 
  WebMenuBar menuBar = new com.thera.thermfw.web.WebMenuBar("HM_Array1", "150", "#000000","#000000","#A5B6CE","#E4EAEF","#FFFFFF","#000000"); 
  menuBar.setParent(YImgCatalogoPortaleForm); 
   request.setAttribute("menuBar", menuBar); 
%> 
<jsp:include page="/it/thera/thip/cs/defObjMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="menuBar"/> 
</jsp:include> 
<% 
  menuBar.write(out); 
  menuBar.writeChildren(out); 
%> 
<% 
  WebToolBar myToolBarTB = new com.thera.thermfw.web.WebToolBar("myToolBar", "24", "24", "16", "16", "#f7fbfd","#C8D6E1"); 
  myToolBarTB.setParent(YImgCatalogoPortaleForm); 
   request.setAttribute("toolBar", myToolBarTB); 
%> 
<jsp:include page="/it/thera/thip/cs/defObjMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="toolBar"/> 
</jsp:include> 
<% 
   myToolBarTB.write(out); 
%> 
</head>
<body onbeforeunload="<%=YImgCatalogoPortaleForm.getBodyOnBeforeUnload()%>" onload="<%=YImgCatalogoPortaleForm.getBodyOnLoad()%>" onunload="<%=YImgCatalogoPortaleForm.getBodyOnUnload()%>" style="margin: 0px; overflow: hidden;"><%
   YImgCatalogoPortaleForm.writeBodyStartElements(out); 
%> 

	<table width="100%" height="100%" cellspacing="0" cellpadding="0">
<tr>
<td style="height:0" valign="top">
<% String hdr = YImgCatalogoPortaleForm.getCompleteHeader();
 if (hdr != null) { 
   request.setAttribute("dataCollector", YImgCatalogoPortaleBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= hdr %>" flush="true"/> 
<% } %> 
</td>
</tr>

<tr>
<td valign="top" height="100%">
<form action="<%=YImgCatalogoPortaleForm.getServlet()%>" method="post" name="YImgCatalogoPortaleForm" style="height:100%"><%
  YImgCatalogoPortaleForm.writeFormStartElements(out); 
%>

		<table cellpadding="0" cellspacing="0" height="100%" id="emptyborder" width="100%">
			<tr>
				<td style="height: 0"><% menuBar.writeElements(out); %> 
</td>
			</tr>
			<tr>
				<td style="height: 0"><% myToolBarTB.writeChildren(out); %> 
</td>
			</tr>
			<tr>
				<td><% 
  WebTextInput YImgCatalogoPortaleIdAzienda =  
     new com.thera.thermfw.web.WebTextInput("YImgCatalogoPortale", "IdAzienda"); 
  YImgCatalogoPortaleIdAzienda.setParent(YImgCatalogoPortaleForm); 
%>
<input class="<%=YImgCatalogoPortaleIdAzienda.getClassType()%>" id="<%=YImgCatalogoPortaleIdAzienda.getId()%>" maxlength="<%=YImgCatalogoPortaleIdAzienda.getMaxLength()%>" name="<%=YImgCatalogoPortaleIdAzienda.getName()%>" size="<%=YImgCatalogoPortaleIdAzienda.getSize()%>" type="hidden"><% 
  YImgCatalogoPortaleIdAzienda.write(out); 
%>
</td>
			</tr>
			<tr>
				<td height="100%"><!--<span class="tabbed" id="mytabbed">-->
<table width="100%" height="100%" cellpadding="0" cellspacing="0" style="padding-right:1px">
   <tr valign="top">
     <td><% 
  WebTabbed mytabbed = new com.thera.thermfw.web.WebTabbed("mytabbed", "100%", "100%"); 
  mytabbed.setParent(YImgCatalogoPortaleForm); 
 mytabbed.addTab("tab1", "it.valvorobica.thip.base.portal.resources.YImgCatalogoPortale", "tab1", "YImgCatalogoPortale", null, null, null, null); 
  mytabbed.write(out); 
%>

     </td>
   </tr>
   <tr>
     <td height="100%"><div class="tabbed_pagine" id="tabbedPagine" style="position: relative; width: 100%; height: 100%;"> <div class="tabbed_page" id="<%=mytabbed.getTabPageId("tab1")%>" style="width:100%;height:100%;overflow:auto;"><% mytabbed.startTab("tab1"); %>
							<table style="width: 100%;">
								<tr style="display: none">
									<td valign="top"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YImgCatalogoPortale", "TipoClassificazione", null); 
   label.setParent(YImgCatalogoPortaleForm); 
%><label class="<%=label.getClassType()%>" for="TipoClassificazione"><%label.write(out);%></label><%}%>
									</td>
									<td valign="top"><% 
  WebTextInput YImgCatalogoPortaleTipoClassificazione =  
     new com.thera.thermfw.web.WebTextInput("YImgCatalogoPortale", "TipoClassificazione"); 
  YImgCatalogoPortaleTipoClassificazione.setParent(YImgCatalogoPortaleForm); 
%>
<input class="<%=YImgCatalogoPortaleTipoClassificazione.getClassType()%>" id="<%=YImgCatalogoPortaleTipoClassificazione.getId()%>" maxlength="<%=YImgCatalogoPortaleTipoClassificazione.getMaxLength()%>" name="<%=YImgCatalogoPortaleTipoClassificazione.getName()%>" size="<%=YImgCatalogoPortaleTipoClassificazione.getSize()%>"><% 
  YImgCatalogoPortaleTipoClassificazione.write(out); 
%>
</td>
								</tr>
								<tr style="display: none">
									<td valign="top"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YImgCatalogoPortale", "IdClassificazione", null); 
   label.setParent(YImgCatalogoPortaleForm); 
%><label class="<%=label.getClassType()%>" for="IdClassificazione"><%label.write(out);%></label><%}%></td>
									<td valign="top"><% 
  WebTextInput YImgCatalogoPortaleIdClassificazione =  
     new com.thera.thermfw.web.WebTextInput("YImgCatalogoPortale", "IdClassificazione"); 
  YImgCatalogoPortaleIdClassificazione.setParent(YImgCatalogoPortaleForm); 
%>
<input class="<%=YImgCatalogoPortaleIdClassificazione.getClassType()%>" id="<%=YImgCatalogoPortaleIdClassificazione.getId()%>" maxlength="<%=YImgCatalogoPortaleIdClassificazione.getMaxLength()%>" name="<%=YImgCatalogoPortaleIdClassificazione.getName()%>" size="<%=YImgCatalogoPortaleIdClassificazione.getSize()%>"><% 
  YImgCatalogoPortaleIdClassificazione.write(out); 
%>
</td>
								</tr>
								<tr>
									<td><label for="BORelation">Tipo classificazione</label></td>
									<td><select id="BORelation" name="BORelation"></select></td>
								</tr>
								<tr>
									<td><label for="KeyValue">Valore</label></td>
									<td><select id="KeyValue" name="KeyValue"></select></td>
								</tr>
								<tr>
									<td valign="top"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YImgCatalogoPortale", "Colonne", null); 
   label.setParent(YImgCatalogoPortaleForm); 
%><label class="<%=label.getClassType()%>" for="Colonne"><%label.write(out);%></label><%}%>
									</td>
									<td valign="top"><% 
  WebTextInput YImgCatalogoPortaleColonne =  
     new com.thera.thermfw.web.WebTextInput("YImgCatalogoPortale", "Colonne"); 
  YImgCatalogoPortaleColonne.setParent(YImgCatalogoPortaleForm); 
%>
<input class="<%=YImgCatalogoPortaleColonne.getClassType()%>" id="<%=YImgCatalogoPortaleColonne.getId()%>" maxlength="<%=YImgCatalogoPortaleColonne.getMaxLength()%>" name="<%=YImgCatalogoPortaleColonne.getName()%>" size="<%=YImgCatalogoPortaleColonne.getSize()%>"><% 
  YImgCatalogoPortaleColonne.write(out); 
%>
</td>
								</tr>
								<tr>
									<td valign="top"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YImgCatalogoPortale", "UrlImg", null); 
   label.setParent(YImgCatalogoPortaleForm); 
%><label class="<%=label.getClassType()%>" for="UrlImg"><%label.write(out);%></label><%}%></td>
									<td valign="top"><% 
  WebTextInput YImgCatalogoPortaleUrlImg =  
     new com.thera.thermfw.web.WebTextArea("YImgCatalogoPortale", "UrlImg"); 
  YImgCatalogoPortaleUrlImg.setParent(YImgCatalogoPortaleForm); 
%>
<textarea class="<%=YImgCatalogoPortaleUrlImg.getClassType()%>" cols="60" id="<%=YImgCatalogoPortaleUrlImg.getId()%>" maxlength="<%=YImgCatalogoPortaleUrlImg.getMaxLength()%>" name="<%=YImgCatalogoPortaleUrlImg.getName()%>" rows="5" size="<%=YImgCatalogoPortaleUrlImg.getSize()%>"></textarea><% 
  YImgCatalogoPortaleUrlImg.write(out); 
%>
</td>
									<td>
										<button id="uploadImg" name="uploadImg" onclick="caricaImg('img')" type="button">Carica</button>
									</td>
									<td style="display: none;"><input id="img" name="img" type="file"></td>
								</tr>
								<tr>
									<td></td>
									<td><img id="imgSuDB" style="height:300px;width:300px;"></td>
								</tr>
							</table>
					<% mytabbed.endTab(); %> 
</div>
				</div><% mytabbed.endTabbed();%> 

     </td>
   </tr>
</table><!--</span>--></td>
			</tr>
			<tr>
				<td style="height: 0"><% 
  WebErrorList errorList = new com.thera.thermfw.web.WebErrorList(); 
  errorList.setParent(YImgCatalogoPortaleForm); 
  errorList.write(out); 
%>
<!--<span class="errorlist"></span>--></td>
			</tr>
		</table>
	<%
  YImgCatalogoPortaleForm.writeFormEndElements(out); 
%>
</form></td>
</tr>

<tr>
<td style="height:0">
<% String ftr = YImgCatalogoPortaleForm.getCompleteFooter();
 if (ftr != null) { 
   request.setAttribute("dataCollector", YImgCatalogoPortaleBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= ftr %>" flush="true"/> 
<% } %> 
</td>
</tr>
</table>


<%
           // blocco YYY  
           // a completamento blocco di codice XXX in head 
              YImgCatalogoPortaleForm.writeBodyEndElements(out); 
           } 
           else 
              errors.addAll(0, YImgCatalogoPortaleBODC.getErrorList().getErrors()); 
        } 
        else 
           errors.addAll(0, YImgCatalogoPortaleBODC.getErrorList().getErrors()); 
           if(YImgCatalogoPortaleBODC.getConflict() != null) 
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
     if(YImgCatalogoPortaleBODC != null && !YImgCatalogoPortaleBODC.close(false)) 
        errors.addAll(0, YImgCatalogoPortaleBODC.getErrorList().getErrors()); 
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
     String errorPage = YImgCatalogoPortaleForm.getErrorPage(); 
%> 
     <jsp:include page="<%=errorPage%>" flush="true"/> 
<% 
  } 
  else 
  { 
     request.setAttribute("ConflictMessages", YImgCatalogoPortaleBODC.getConflict()); 
     request.setAttribute("ErrorMessages", errors); 
     String conflictPage = YImgCatalogoPortaleForm.getConflictPage(); 
%> 
     <jsp:include page="<%=conflictPage%>" flush="true"/> 
<% 
   } 
   } 
%> 
</body>
</html>
