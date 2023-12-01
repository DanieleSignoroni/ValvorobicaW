<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN"
                      "file:///W:\PthDev\Projects\Panthera\Valvorobica\WebContent\dtd/xhtml1-transitional.dtd">
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
  BODataCollector YStoricoCmpLancioBODC = null; 
  List errors = new ArrayList(); 
  WebJSTypeList jsList = new WebJSTypeList(); 
  WebForm YStoricoCmpLancioForm =  
     new com.thera.thermfw.web.WebForm(request, response, "YStoricoCmpLancioForm", "YStoricoCmpLancio", null, "com.thera.thermfw.web.servlet.FormActionAdapter", false, false, true, true, true, true, null, 0, true, "it/valvorobica/thip/magazzino/chiusure/YStoricoCmpLancio.js"); 
  YStoricoCmpLancioForm.setServletEnvironment(se); 
  YStoricoCmpLancioForm.setJSTypeList(jsList); 
  YStoricoCmpLancioForm.setHeader("it.thera.thip.cs.PantheraHeader.jsp"); 
  YStoricoCmpLancioForm.setFooter("com.thera.thermfw.common.Footer.jsp"); 
  YStoricoCmpLancioForm.setDeniedAttributeModeStr("hideNone"); 
  int mode = YStoricoCmpLancioForm.getMode(); 
  String key = YStoricoCmpLancioForm.getKey(); 
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
        YStoricoCmpLancioForm.outTraceInfo(getClass().getName()); 
        String collectorName = YStoricoCmpLancioForm.findBODataCollectorName(); 
                YStoricoCmpLancioBODC = (BODataCollector)Factory.createObject(collectorName); 
        if (YStoricoCmpLancioBODC instanceof WebDataCollector) 
            ((WebDataCollector)YStoricoCmpLancioBODC).setServletEnvironment(se); 
        YStoricoCmpLancioBODC.initialize("YStoricoCmpLancio", true, 0); 
        YStoricoCmpLancioForm.setBODataCollector(YStoricoCmpLancioBODC); 
        int rcBODC = YStoricoCmpLancioForm.initSecurityServices(); 
        mode = YStoricoCmpLancioForm.getMode(); 
        if (rcBODC == BODataCollector.OK) 
        { 
           requestIsValid = true; 
           YStoricoCmpLancioForm.write(out); 
           if(mode != WebForm.NEW) 
              rcBODC = YStoricoCmpLancioBODC.retrieve(key); 
           if(rcBODC == BODataCollector.OK) 
           { 
              YStoricoCmpLancioForm.writeHeadElements(out); 
           // fine blocco XXX  
           // a completamento blocco di codice YYY a fine body con catch e gestione errori 
%> 
<% 
  WebMenuBar menuBar = new com.thera.thermfw.web.WebMenuBar("HM_Array1", "150", "#000000","#000000","#A5B6CE","#E4EAEF","#FFFFFF","#000000"); 
  menuBar.setParent(YStoricoCmpLancioForm); 
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
  myToolBarTB.setParent(YStoricoCmpLancioForm); 
   request.setAttribute("toolBar", myToolBarTB); 
%> 
<jsp:include page="/it/thera/thip/cs/defObjMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="toolBar"/> 
</jsp:include> 
<% 
   myToolBarTB.write(out); 
%> 
</head>
  <body onbeforeunload="<%=YStoricoCmpLancioForm.getBodyOnBeforeUnload()%>" onload="<%=YStoricoCmpLancioForm.getBodyOnLoad()%>" onunload="<%=YStoricoCmpLancioForm.getBodyOnUnload()%>" style="margin: 0px; overflow: hidden;"><%
   YStoricoCmpLancioForm.writeBodyStartElements(out); 
%> 

    <table width="100%" height="100%" cellspacing="0" cellpadding="0">
<tr>
<td style="height:0" valign="top">
<% String hdr = YStoricoCmpLancioForm.getCompleteHeader();
 if (hdr != null) { 
   request.setAttribute("dataCollector", YStoricoCmpLancioBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= hdr %>" flush="true"/> 
<% } %> 
</td>
</tr>

<tr>
<td valign="top" height="100%">
<form action="<%=YStoricoCmpLancioForm.getServlet()%>" method="post" name="YStoricoCmpLancioForm" style="height:100%"><%
  YStoricoCmpLancioForm.writeFormStartElements(out); 
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
  WebTextInput YStoricoCmpLancioIdAzienda =  
     new com.thera.thermfw.web.WebTextInput("YStoricoCmpLancio", "IdAzienda"); 
  YStoricoCmpLancioIdAzienda.setParent(YStoricoCmpLancioForm); 
%>
<input class="<%=YStoricoCmpLancioIdAzienda.getClassType()%>" id="<%=YStoricoCmpLancioIdAzienda.getId()%>" maxlength="<%=YStoricoCmpLancioIdAzienda.getMaxLength()%>" name="<%=YStoricoCmpLancioIdAzienda.getName()%>" size="<%=YStoricoCmpLancioIdAzienda.getSize()%>" type="hidden"><% 
  YStoricoCmpLancioIdAzienda.write(out); 
%>

          </td>
        </tr>
        <tr>
          <td>
            <% 
  WebTextInput YStoricoCmpLancioIdAnnoFiscale =  
     new com.thera.thermfw.web.WebTextInput("YStoricoCmpLancio", "IdAnnoFiscale"); 
  YStoricoCmpLancioIdAnnoFiscale.setParent(YStoricoCmpLancioForm); 
%>
<input class="<%=YStoricoCmpLancioIdAnnoFiscale.getClassType()%>" id="<%=YStoricoCmpLancioIdAnnoFiscale.getId()%>" maxlength="<%=YStoricoCmpLancioIdAnnoFiscale.getMaxLength()%>" name="<%=YStoricoCmpLancioIdAnnoFiscale.getName()%>" size="<%=YStoricoCmpLancioIdAnnoFiscale.getSize()%>" type="hidden"><% 
  YStoricoCmpLancioIdAnnoFiscale.write(out); 
%>

          </td>
        </tr>
        <tr>
          <td>
            <% 
  WebTextInput YStoricoCmpLancioCodicePeriodo =  
     new com.thera.thermfw.web.WebTextInput("YStoricoCmpLancio", "CodicePeriodo"); 
  YStoricoCmpLancioCodicePeriodo.setParent(YStoricoCmpLancioForm); 
%>
<input class="<%=YStoricoCmpLancioCodicePeriodo.getClassType()%>" id="<%=YStoricoCmpLancioCodicePeriodo.getId()%>" maxlength="<%=YStoricoCmpLancioCodicePeriodo.getMaxLength()%>" name="<%=YStoricoCmpLancioCodicePeriodo.getName()%>" size="<%=YStoricoCmpLancioCodicePeriodo.getSize()%>" type="hidden"><% 
  YStoricoCmpLancioCodicePeriodo.write(out); 
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
  mytabbed.setParent(YStoricoCmpLancioForm); 
  mytabbed.write(out); 
%>

     </td>
   </tr>
   <tr>
     <td height="100%"><div class="tabbed_pagine" id="tabbedPagine" style="position: relative; width: 100%; height: 100%;">
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
  errorList.setParent(YStoricoCmpLancioForm); 
  errorList.write(out); 
%>
<!--<span class="errorlist"></span>-->
          </td>
        </tr>
      </table>
    <%
  YStoricoCmpLancioForm.writeFormEndElements(out); 
%>
</form></td>
</tr>

<tr>
<td style="height:0">
<% String ftr = YStoricoCmpLancioForm.getCompleteFooter();
 if (ftr != null) { 
   request.setAttribute("dataCollector", YStoricoCmpLancioBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= ftr %>" flush="true"/> 
<% } %> 
</td>
</tr>
</table>


  <%
           // blocco YYY  
           // a completamento blocco di codice XXX in head 
              YStoricoCmpLancioForm.writeBodyEndElements(out); 
           } 
           else 
              errors.addAll(0, YStoricoCmpLancioBODC.getErrorList().getErrors()); 
        } 
        else 
           errors.addAll(0, YStoricoCmpLancioBODC.getErrorList().getErrors()); 
           if(YStoricoCmpLancioBODC.getConflict() != null) 
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
     if(YStoricoCmpLancioBODC != null && !YStoricoCmpLancioBODC.close(false)) 
        errors.addAll(0, YStoricoCmpLancioBODC.getErrorList().getErrors()); 
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
     String errorPage = YStoricoCmpLancioForm.getErrorPage(); 
%> 
     <jsp:include page="<%=errorPage%>" flush="true"/> 
<% 
  } 
  else 
  { 
     request.setAttribute("ConflictMessages", YStoricoCmpLancioBODC.getConflict()); 
     request.setAttribute("ErrorMessages", errors); 
     String conflictPage = YStoricoCmpLancioForm.getConflictPage(); 
%> 
     <jsp:include page="<%=conflictPage%>" flush="true"/> 
<% 
   } 
   } 
%> 
</body>
</html>
