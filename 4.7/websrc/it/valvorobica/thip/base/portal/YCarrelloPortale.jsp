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
  BODataCollector YCarrelloPortaleBODC = null; 
  List errors = new ArrayList(); 
  WebJSTypeList jsList = new WebJSTypeList(); 
  WebForm YCarrelloPortaleForm =  
     new com.thera.thermfw.web.WebForm(request, response, "YCarrelloPortaleForm", "YCarrelloPortale", null, "com.thera.thermfw.web.servlet.FormActionAdapter", false, false, true, true, true, true, null, 0, true, "it/valvorobica/thip/base/portal/YCarrelloPortale.js"); 
  YCarrelloPortaleForm.setServletEnvironment(se); 
  YCarrelloPortaleForm.setJSTypeList(jsList); 
  YCarrelloPortaleForm.setHeader("it.thera.thip.cs.PantheraHeader.jsp"); 
  YCarrelloPortaleForm.setFooter("com.thera.thermfw.common.Footer.jsp"); 
  YCarrelloPortaleForm.setDeniedAttributeModeStr("hideNone"); 
  int mode = YCarrelloPortaleForm.getMode(); 
  String key = YCarrelloPortaleForm.getKey(); 
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
        YCarrelloPortaleForm.outTraceInfo(getClass().getName()); 
        String collectorName = YCarrelloPortaleForm.findBODataCollectorName(); 
                YCarrelloPortaleBODC = (BODataCollector)Factory.createObject(collectorName); 
        if (YCarrelloPortaleBODC instanceof WebDataCollector) 
            ((WebDataCollector)YCarrelloPortaleBODC).setServletEnvironment(se); 
        YCarrelloPortaleBODC.initialize("YCarrelloPortale", true, 0); 
        YCarrelloPortaleForm.setBODataCollector(YCarrelloPortaleBODC); 
        int rcBODC = YCarrelloPortaleForm.initSecurityServices(); 
        mode = YCarrelloPortaleForm.getMode(); 
        if (rcBODC == BODataCollector.OK) 
        { 
           requestIsValid = true; 
           YCarrelloPortaleForm.write(out); 
           if(mode != WebForm.NEW) 
              rcBODC = YCarrelloPortaleBODC.retrieve(key); 
           if(rcBODC == BODataCollector.OK) 
           { 
              YCarrelloPortaleForm.writeHeadElements(out); 
           // fine blocco XXX  
           // a completamento blocco di codice YYY a fine body con catch e gestione errori 
%> 
<% 
  WebMenuBar menuBar = new com.thera.thermfw.web.WebMenuBar("HM_Array1", "150", "#000000","#000000","#A5B6CE","#E4EAEF","#FFFFFF","#000000"); 
  menuBar.setParent(YCarrelloPortaleForm); 
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
  myToolBarTB.setParent(YCarrelloPortaleForm); 
   request.setAttribute("toolBar", myToolBarTB); 
%> 
<jsp:include page="/it/thera/thip/cs/defObjMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="toolBar"/> 
</jsp:include> 
<% 
   myToolBarTB.write(out); 
%> 
</head>
  <body onbeforeunload="<%=YCarrelloPortaleForm.getBodyOnBeforeUnload()%>" onload="<%=YCarrelloPortaleForm.getBodyOnLoad()%>" onunload="<%=YCarrelloPortaleForm.getBodyOnUnload()%>" style="margin: 0px; overflow: hidden;"><%
   YCarrelloPortaleForm.writeBodyStartElements(out); 
%> 

    <table width="100%" height="100%" cellspacing="0" cellpadding="0">
<tr>
<td style="height:0" valign="top">
<% String hdr = YCarrelloPortaleForm.getCompleteHeader();
 if (hdr != null) { 
   request.setAttribute("dataCollector", YCarrelloPortaleBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= hdr %>" flush="true"/> 
<% } %> 
</td>
</tr>

<tr>
<td valign="top" height="100%">
<form action="<%=YCarrelloPortaleForm.getServlet()%>" method="post" name="YCarrelloPortaleForm" style="height:100%"><%
  YCarrelloPortaleForm.writeFormStartElements(out); 
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
  WebTextInput YCarrelloPortaleProgressivo =  
     new com.thera.thermfw.web.WebTextInput("YCarrelloPortale", "Progressivo"); 
  YCarrelloPortaleProgressivo.setParent(YCarrelloPortaleForm); 
%>
<input class="<%=YCarrelloPortaleProgressivo.getClassType()%>" id="<%=YCarrelloPortaleProgressivo.getId()%>" maxlength="<%=YCarrelloPortaleProgressivo.getMaxLength()%>" name="<%=YCarrelloPortaleProgressivo.getName()%>" size="<%=YCarrelloPortaleProgressivo.getSize()%>" type="hidden"><% 
  YCarrelloPortaleProgressivo.write(out); 
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
  mytabbed.setParent(YCarrelloPortaleForm); 
 mytabbed.addTab("tab1", "it.valvorobica.thip.base.portal.resources.YCarrelloPortale", "tab1", "YCarrelloPortale", null, null, null, null); 
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
                      <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YCarrelloPortale", "RUtentePortale", null); 
   label.setParent(YCarrelloPortaleForm); 
%><label class="<%=label.getClassType()%>" for="UtentePortale"><%label.write(out);%></label><%}%>
                    </td>
                    <td valign="top">
                      <% 
  WebMultiSearchForm YCarrelloPortaleUtentePortale =  
     new com.thera.thermfw.web.WebMultiSearchForm("YCarrelloPortale", "UtentePortale", false, false, true, 1, null, null); 
  YCarrelloPortaleUtentePortale.setParent(YCarrelloPortaleForm); 
  YCarrelloPortaleUtentePortale.write(out); 
%>
<!--<span class="multisearchform" id="UtentePortale"></span>-->
                    </td>
                  </tr>
                  <tr>
                    <td valign="top">
                      <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YCarrelloPortale", "RArticolo", null); 
   label.setParent(YCarrelloPortaleForm); 
%><label class="<%=label.getClassType()%>" for="Articolo"><%label.write(out);%></label><%}%>
                    </td>
                    <td valign="top">
                      <% 
  WebMultiSearchForm YCarrelloPortaleArticolo =  
     new com.thera.thermfw.web.WebMultiSearchForm("YCarrelloPortale", "Articolo", false, false, true, 1, null, null); 
  YCarrelloPortaleArticolo.setParent(YCarrelloPortaleForm); 
  YCarrelloPortaleArticolo.write(out); 
%>
<!--<span class="multisearchform" id="Articolo"></span>-->
                    </td>
                  </tr>
                  <tr>
                    <td valign="top">
                      <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YCarrelloPortale", "Quantita", null); 
   label.setParent(YCarrelloPortaleForm); 
%><label class="<%=label.getClassType()%>" for="Quantita"><%label.write(out);%></label><%}%>
                    </td>
                    <td valign="top">
                      <% 
  WebTextInput YCarrelloPortaleQuantita =  
     new com.thera.thermfw.web.WebTextInput("YCarrelloPortale", "Quantita"); 
  YCarrelloPortaleQuantita.setParent(YCarrelloPortaleForm); 
%>
<input class="<%=YCarrelloPortaleQuantita.getClassType()%>" id="<%=YCarrelloPortaleQuantita.getId()%>" maxlength="<%=YCarrelloPortaleQuantita.getMaxLength()%>" name="<%=YCarrelloPortaleQuantita.getName()%>" size="<%=YCarrelloPortaleQuantita.getSize()%>"><% 
  YCarrelloPortaleQuantita.write(out); 
%>

                    </td>
                  </tr>
                  <tr>
                    <td valign="top">
                      <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YCarrelloPortale", "RCliente", null); 
   label.setParent(YCarrelloPortaleForm); 
%><label class="<%=label.getClassType()%>" for="Cliente"><%label.write(out);%></label><%}%>
                    </td>
                    <td valign="top">
                      <% 
  WebMultiSearchForm YCarrelloPortaleCliente =  
     new com.thera.thermfw.web.WebMultiSearchForm("YCarrelloPortale", "Cliente", false, false, true, 1, null, null); 
  YCarrelloPortaleCliente.setParent(YCarrelloPortaleForm); 
  YCarrelloPortaleCliente.write(out); 
%>
<!--<span class="multisearchform" id="Cliente"></span>-->
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
  errorList.setParent(YCarrelloPortaleForm); 
  errorList.write(out); 
%>
<!--<span class="errorlist"></span>-->
          </td>
        </tr>
      </table>
    <%
  YCarrelloPortaleForm.writeFormEndElements(out); 
%>
</form></td>
</tr>

<tr>
<td style="height:0">
<% String ftr = YCarrelloPortaleForm.getCompleteFooter();
 if (ftr != null) { 
   request.setAttribute("dataCollector", YCarrelloPortaleBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= ftr %>" flush="true"/> 
<% } %> 
</td>
</tr>
</table>


  <%
           // blocco YYY  
           // a completamento blocco di codice XXX in head 
              YCarrelloPortaleForm.writeBodyEndElements(out); 
           } 
           else 
              errors.addAll(0, YCarrelloPortaleBODC.getErrorList().getErrors()); 
        } 
        else 
           errors.addAll(0, YCarrelloPortaleBODC.getErrorList().getErrors()); 
           if(YCarrelloPortaleBODC.getConflict() != null) 
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
     if(YCarrelloPortaleBODC != null && !YCarrelloPortaleBODC.close(false)) 
        errors.addAll(0, YCarrelloPortaleBODC.getErrorList().getErrors()); 
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
     String errorPage = YCarrelloPortaleForm.getErrorPage(); 
%> 
     <jsp:include page="<%=errorPage%>" flush="true"/> 
<% 
  } 
  else 
  { 
     request.setAttribute("ConflictMessages", YCarrelloPortaleBODC.getConflict()); 
     request.setAttribute("ErrorMessages", errors); 
     String conflictPage = YCarrelloPortaleForm.getConflictPage(); 
%> 
     <jsp:include page="<%=conflictPage%>" flush="true"/> 
<% 
   } 
   } 
%> 
</body>
</html>
