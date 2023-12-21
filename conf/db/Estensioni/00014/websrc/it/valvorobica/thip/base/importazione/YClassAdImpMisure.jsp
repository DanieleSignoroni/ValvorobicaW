<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN"
                      "file:///W:\PthDev\Projects\Panthera\Valvorobica\WebContent\dtd/xhtml1-transitional.dtd">
<html>
<!-- WIZGEN Therm 2.0.0 as Form riga indipendente - multiBrowserGen = true -->
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
  BODataCollector YClassAdImpMisureBODC = null; 
  List errors = new ArrayList(); 
  WebJSTypeList jsList = new WebJSTypeList(); 
  WebFormForIndipendentRowForm YClassAdImpMisureForm =  
     new com.thera.thermfw.web.WebFormForIndipendentRowForm(request, response, "YClassAdImpMisureForm", "YClassAdImpMisure", null, "com.thera.thermfw.web.servlet.FormActionAdapter", false, false, true, true, true, true, null, 0, false, null); 
  YClassAdImpMisureForm.setServletEnvironment(se); 
  YClassAdImpMisureForm.setJSTypeList(jsList); 
  YClassAdImpMisureForm.setHeader("it.thera.thip.cs.PantheraHeader.jsp"); 
  YClassAdImpMisureForm.setFooter("com.thera.thermfw.common.Footer.jsp"); 
  YClassAdImpMisureForm.setDeniedAttributeModeStr("hideNone"); 
  int mode = YClassAdImpMisureForm.getMode(); 
  String key = YClassAdImpMisureForm.getKey(); 
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
        YClassAdImpMisureForm.outTraceInfo(getClass().getName()); 
        String collectorName = YClassAdImpMisureForm.findBODataCollectorName(); 
	     YClassAdImpMisureBODC = (BODataCollector)Factory.createObject(collectorName); 
        if (YClassAdImpMisureBODC instanceof WebDataCollector) 
            ((WebDataCollector)YClassAdImpMisureBODC).setServletEnvironment(se); 
        YClassAdImpMisureBODC.initialize("YClassAdImpMisure", true, 0); 
        YClassAdImpMisureForm.setBODataCollector(YClassAdImpMisureBODC); 
        int rcBODC = YClassAdImpMisureForm.initSecurityServices(); 
        mode = YClassAdImpMisureForm.getMode(); 
        if (rcBODC == BODataCollector.OK) 
        { 
           requestIsValid = true; 
           YClassAdImpMisureForm.write(out); 
           if(mode != WebForm.NEW) 
              rcBODC = YClassAdImpMisureBODC.retrieve(key); 
           if(rcBODC == BODataCollector.OK) 
           { 
              YClassAdImpMisureForm.writeHeadElements(out); 
           // fine blocco XXX  
           // a completamento blocco di codice YYY a fine body con catch e gestione errori 
%> 
<% 
  WebMenuBar menuBar = new com.thera.thermfw.web.WebMenuBar("HM_Array1", "150", "#000000","#000000","#A5B6CE","#E4EAEF","#FFFFFF","#000000"); 
  menuBar.setParent(YClassAdImpMisureForm); 
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
  myToolBarTB.setParent(YClassAdImpMisureForm); 
   request.setAttribute("toolBar", myToolBarTB); 
%> 
<jsp:include page="/it/thera/thip/cs/defObjMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="toolBar"/> 
</jsp:include> 
<% 
   myToolBarTB.write(out); 
%> 
</head>
  <body onbeforeunload="<%=YClassAdImpMisureForm.getBodyOnBeforeUnload()%>" onload="<%=YClassAdImpMisureForm.getBodyOnLoad()%>" onunload="<%=YClassAdImpMisureForm.getBodyOnUnload()%>" style="margin: 0px; overflow: hidden;"><%
   YClassAdImpMisureForm.writeBodyStartElements(out); 
%> 

    <table width="100%" height="100%" cellspacing="0" cellpadding="0">
<tr>
<td style="height:0" valign="top">
<% String hdr = YClassAdImpMisureForm.getCompleteHeader();
 if (hdr != null) { 
   request.setAttribute("dataCollector", YClassAdImpMisureBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= hdr %>" flush="true"/> 
<% } %> 
</td>
</tr>

<tr>
<td valign="top" height="100%">
<form action="<%=YClassAdImpMisureForm.getServlet()%>" method="post" name="YClassAdImpMisureForm" style="height:100%"><%
  YClassAdImpMisureForm.writeFormStartElements(out); 
%>

      <table cellpadding="0" cellspacing="0" height="100%" id="emptyborder" width="100%">
        <tr>
          <td style="height:0">
            <% menuBar.writeElements(out); %> 

          </td>
        </tr>
        <tr>
          <td style="height:0">
            <% myToolBarTB.writeChildren(out); %> 

          </td>
        </tr>
        <tr>
          <td>
            <% 
  WebTextInput YClassAdImpMisureIdAzienda =  
     new com.thera.thermfw.web.WebTextInput("YClassAdImpMisure", "IdAzienda"); 
  YClassAdImpMisureIdAzienda.setParent(YClassAdImpMisureForm); 
%>
<input class="<%=YClassAdImpMisureIdAzienda.getClassType()%>" id="<%=YClassAdImpMisureIdAzienda.getId()%>" maxlength="<%=YClassAdImpMisureIdAzienda.getMaxLength()%>" name="<%=YClassAdImpMisureIdAzienda.getName()%>" size="<%=YClassAdImpMisureIdAzienda.getSize()%>" type="hidden"><% 
  YClassAdImpMisureIdAzienda.write(out); 
%>

          </td>
        </tr>
        <tr>
          <td>
            <% 
  WebTextInput YClassAdImpMisureIdFornitore =  
     new com.thera.thermfw.web.WebTextInput("YClassAdImpMisure", "IdFornitore"); 
  YClassAdImpMisureIdFornitore.setParent(YClassAdImpMisureForm); 
%>
<input class="<%=YClassAdImpMisureIdFornitore.getClassType()%>" id="<%=YClassAdImpMisureIdFornitore.getId()%>" maxlength="<%=YClassAdImpMisureIdFornitore.getMaxLength()%>" name="<%=YClassAdImpMisureIdFornitore.getName()%>" size="<%=YClassAdImpMisureIdFornitore.getSize()%>" type="hidden"><% 
  YClassAdImpMisureIdFornitore.write(out); 
%>

          </td>
        </tr>
        <tr>
          <td height="100%">
            <!--<span class="tabbed" id="mytabbed">-->
<table width="100%" height="100%" cellpadding="0" cellspacing="0" style="padding-right:1px">
   <tr valign="top">
     <td><% 
  WebTabbed mytabbed = new com.thera.thermfw.web.WebTabbed("mytabbed", "100%", "100%"); 
  mytabbed.setParent(YClassAdImpMisureForm); 
 mytabbed.addTab("tab1", "it.valvorobica.thip.base.importazione.resources.YClassAdImpMisure", "tab1", "YClassAdImpMisure", null, null, null, null); 
  mytabbed.write(out); 
%>

     </td>
   </tr>
   <tr>
     <td height="100%"><div class="tabbed_pagine" id="tabbedPagine" style="position: relative; width: 100%; height: 100%;">
              <div class="tabbed_page" id="<%=mytabbed.getTabPageId("tab1")%>" style="width:100%;height:100%;overflow:auto;"><% mytabbed.startTab("tab1"); %>
                <table style="width: 100%;">
                  <tr>
                    <td valign="top">
                      <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YClassAdImpMisure", "TitoloColonna", null); 
   label.setParent(YClassAdImpMisureForm); 
%><label class="<%=label.getClassType()%>" for="TitoloColonna"><%label.write(out);%></label><%}%>
                    </td>
                    <td valign="top">
                      <% 
  WebTextInput YClassAdImpMisureTitoloColonna =  
     new com.thera.thermfw.web.WebTextInput("YClassAdImpMisure", "TitoloColonna"); 
  YClassAdImpMisureTitoloColonna.setParent(YClassAdImpMisureForm); 
%>
<input class="<%=YClassAdImpMisureTitoloColonna.getClassType()%>" id="<%=YClassAdImpMisureTitoloColonna.getId()%>" maxlength="<%=YClassAdImpMisureTitoloColonna.getMaxLength()%>" name="<%=YClassAdImpMisureTitoloColonna.getName()%>" size="<%=YClassAdImpMisureTitoloColonna.getSize()%>"><% 
  YClassAdImpMisureTitoloColonna.write(out); 
%>

                    </td>
                  </tr>
                  <tr>
                    <td valign="top">
                      <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YClassAdImpMisure", "BoAttribute", null); 
   label.setParent(YClassAdImpMisureForm); 
%><label class="<%=label.getClassType()%>" for="BoAttribute"><%label.write(out);%></label><%}%>
                    </td>
                    <td valign="top">
                      <% 
  WebTextInput YClassAdImpMisureBoAttribute =  
     new com.thera.thermfw.web.WebTextArea("YClassAdImpMisure", "BoAttribute"); 
  YClassAdImpMisureBoAttribute.setParent(YClassAdImpMisureForm); 
%>
<textarea class="<%=YClassAdImpMisureBoAttribute.getClassType()%>" cols="60" id="<%=YClassAdImpMisureBoAttribute.getId()%>" maxlength="<%=YClassAdImpMisureBoAttribute.getMaxLength()%>" name="<%=YClassAdImpMisureBoAttribute.getName()%>" rows="5" size="<%=YClassAdImpMisureBoAttribute.getSize()%>"></textarea><% 
  YClassAdImpMisureBoAttribute.write(out); 
%>

                    </td>
                  </tr>
                  <tr>
                    <td valign="top">
                      <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YClassAdImpMisure", "ValoreDefault", null); 
   label.setParent(YClassAdImpMisureForm); 
%><label class="<%=label.getClassType()%>" for="ValoreDefault"><%label.write(out);%></label><%}%>
                    </td>
                    <td valign="top">
                      <% 
  WebTextInput YClassAdImpMisureValoreDefault =  
     new com.thera.thermfw.web.WebTextArea("YClassAdImpMisure", "ValoreDefault"); 
  YClassAdImpMisureValoreDefault.setParent(YClassAdImpMisureForm); 
%>
<textarea class="<%=YClassAdImpMisureValoreDefault.getClassType()%>" cols="60" id="<%=YClassAdImpMisureValoreDefault.getId()%>" maxlength="<%=YClassAdImpMisureValoreDefault.getMaxLength()%>" name="<%=YClassAdImpMisureValoreDefault.getName()%>" rows="5" size="<%=YClassAdImpMisureValoreDefault.getSize()%>"></textarea><% 
  YClassAdImpMisureValoreDefault.write(out); 
%>

                    </td>
                  </tr>
                  <tr>
                    <td valign="top">
                      <% 
  WebCheckBox YClassAdImpMisureEscludi =  
     new com.thera.thermfw.web.WebCheckBox("YClassAdImpMisure", "Escludi"); 
  YClassAdImpMisureEscludi.setParent(YClassAdImpMisureForm); 
%>
<input id="<%=YClassAdImpMisureEscludi.getId()%>" name="<%=YClassAdImpMisureEscludi.getName()%>" type="checkbox" value="Y"><%
  YClassAdImpMisureEscludi.write(out); 
%>

                    </td>
                    <td valign="top">
                    </td>
                  </tr>
                </table>
              <% mytabbed.endTab(); %> 
</div>
            </div><% mytabbed.endTabbed();%> 

     </td>
   </tr>
</table><!--</span>-->
          </td>
        </tr>
        <tr>
          <td style="height:0">
            <% 
  WebErrorList errorList = new com.thera.thermfw.web.WebErrorList(); 
  errorList.setParent(YClassAdImpMisureForm); 
  errorList.write(out); 
%>
<!--<span class="errorlist"></span>-->
          </td>
        </tr>
      </table>
    <%
  YClassAdImpMisureForm.writeFormEndElements(out); 
%>
</form></td>
</tr>

<tr>
<td style="height:0">
<% String ftr = YClassAdImpMisureForm.getCompleteFooter();
 if (ftr != null) { 
   request.setAttribute("dataCollector", YClassAdImpMisureBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= ftr %>" flush="true"/> 
<% } %> 
</td>
</tr>
</table>


  <%
           // blocco YYY  
           // a completamento blocco di codice XXX in head 
              YClassAdImpMisureForm.writeBodyEndElements(out); 
           } 
           else 
              errors.addAll(0, YClassAdImpMisureBODC.getErrorList().getErrors()); 
        } 
        else 
           errors.addAll(0, YClassAdImpMisureBODC.getErrorList().getErrors()); 
           if(YClassAdImpMisureBODC.getConflict() != null) 
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
     if(YClassAdImpMisureBODC != null && !YClassAdImpMisureBODC.close(false)) 
        errors.addAll(0, YClassAdImpMisureBODC.getErrorList().getErrors()); 
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
     String errorPage = YClassAdImpMisureForm.getErrorPage(); 
%> 
     <jsp:include page="<%=errorPage%>" flush="true"/> 
<% 
  } 
  else 
  { 
     request.setAttribute("ConflictMessages", YClassAdImpMisureBODC.getConflict()); 
     request.setAttribute("ErrorMessages", errors); 
     String conflictPage = YClassAdImpMisureForm.getConflictPage(); 
%> 
     <jsp:include page="<%=conflictPage%>" flush="true"/> 
<% 
   } 
   } 
%> 
</body>
</html>
