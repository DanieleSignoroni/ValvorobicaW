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
  BODataCollector YHdrImpMisureBODC = null; 
  List errors = new ArrayList(); 
  WebJSTypeList jsList = new WebJSTypeList(); 
  WebForm YHdrImpMisureForm =  
     new com.thera.thermfw.web.WebForm(request, response, "YHdrImpMisureForm", "YHdrImpMisure", null, "com.thera.thermfw.web.servlet.FormActionAdapter", false, false, true, true, true, true, null, 0, true, "it/valvorobica/thip/base/importazione/YHdrImpMisure.js"); 
  YHdrImpMisureForm.setServletEnvironment(se); 
  YHdrImpMisureForm.setJSTypeList(jsList); 
  YHdrImpMisureForm.setHeader("it.thera.thip.cs.PantheraHeader.jsp"); 
  YHdrImpMisureForm.setFooter("com.thera.thermfw.common.Footer.jsp"); 
  YHdrImpMisureForm.setDeniedAttributeModeStr("hideNone"); 
  int mode = YHdrImpMisureForm.getMode(); 
  String key = YHdrImpMisureForm.getKey(); 
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
        YHdrImpMisureForm.outTraceInfo(getClass().getName()); 
        String collectorName = YHdrImpMisureForm.findBODataCollectorName(); 
                YHdrImpMisureBODC = (BODataCollector)Factory.createObject(collectorName); 
        if (YHdrImpMisureBODC instanceof WebDataCollector) 
            ((WebDataCollector)YHdrImpMisureBODC).setServletEnvironment(se); 
        YHdrImpMisureBODC.initialize("YHdrImpMisure", true, 0); 
        YHdrImpMisureForm.setBODataCollector(YHdrImpMisureBODC); 
        int rcBODC = YHdrImpMisureForm.initSecurityServices(); 
        mode = YHdrImpMisureForm.getMode(); 
        if (rcBODC == BODataCollector.OK) 
        { 
           requestIsValid = true; 
           YHdrImpMisureForm.write(out); 
           if(mode != WebForm.NEW) 
              rcBODC = YHdrImpMisureBODC.retrieve(key); 
           if(rcBODC == BODataCollector.OK) 
           { 
              YHdrImpMisureForm.writeHeadElements(out); 
           // fine blocco XXX  
           // a completamento blocco di codice YYY a fine body con catch e gestione errori 
%> 
<% 
  WebMenuBar menuBar = new com.thera.thermfw.web.WebMenuBar("HM_Array1", "150", "#000000","#000000","#A5B6CE","#E4EAEF","#FFFFFF","#000000"); 
  menuBar.setParent(YHdrImpMisureForm); 
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
  myToolBarTB.setParent(YHdrImpMisureForm); 
   request.setAttribute("toolBar", myToolBarTB); 
%> 
<jsp:include page="/it/thera/thip/cs/defObjMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="toolBar"/> 
</jsp:include> 
<% 
   myToolBarTB.write(out); 
%> 
</head>
  <body onbeforeunload="<%=YHdrImpMisureForm.getBodyOnBeforeUnload()%>" onload="<%=YHdrImpMisureForm.getBodyOnLoad()%>" onunload="<%=YHdrImpMisureForm.getBodyOnUnload()%>" style="margin: 0px; overflow: hidden;"><%
   YHdrImpMisureForm.writeBodyStartElements(out); 
%> 

    <table width="100%" height="100%" cellspacing="0" cellpadding="0">
<tr>
<td style="height:0" valign="top">
<% String hdr = YHdrImpMisureForm.getCompleteHeader();
 if (hdr != null) { 
   request.setAttribute("dataCollector", YHdrImpMisureBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= hdr %>" flush="true"/> 
<% } %> 
</td>
</tr>

<tr>
<td valign="top" height="100%">
<form action="<%=YHdrImpMisureForm.getServlet()%>" method="post" name="YHdrImpMisureForm" style="height:100%"><%
  YHdrImpMisureForm.writeFormStartElements(out); 
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
          <td height="100%">
            <!--<span class="tabbed" id="mytabbed">-->
<table width="100%" height="100%" cellpadding="0" cellspacing="0" style="padding-right:1px">
   <tr valign="top">
     <td><% 
  WebTabbed mytabbed = new com.thera.thermfw.web.WebTabbed("mytabbed", "100%", "100%"); 
  mytabbed.setParent(YHdrImpMisureForm); 
 mytabbed.addTab("tab1", "it.valvorobica.thip.base.importazione.resources.YHdrImpMisure", "tab1", "YHdrImpMisure", null, null, null, null); 
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
                      <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YHdrImpMisure", "IdFornitore", null); 
   label.setParent(YHdrImpMisureForm); 
%><label class="<%=label.getClassType()%>" for="Fornitore"><%label.write(out);%></label><%}%>
                    </td>
                    <td valign="top">
                      <% 
  WebMultiSearchForm YHdrImpMisureFornitore =  
     new com.thera.thermfw.web.WebMultiSearchForm("YHdrImpMisure", "Fornitore", false, false, true, 1, null, null); 
  YHdrImpMisureFornitore.setParent(YHdrImpMisureForm); 
  YHdrImpMisureFornitore.write(out); 
%>
<!--<span class="multisearchform" id="Fornitore"></span>-->
                    </td>
                  </tr>
                  <tr>
                    <td colspan="2" valign="top">
                      <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YHdrImpMisure", null, "YClassAdImpMisure"); 
   label.setParent(YHdrImpMisureForm); 
%><label class="<%=label.getClassType()%>" for="YClassAdImpMisure"><%label.write(out);%></label><%}%>
                    </td>
                  </tr>
                  <tr>
                    <td colspan="2" valign="top">
                      <!--<span class="editgrid" id="YClassAdImpMisure">--><% 
  WebEditGrid YHdrImpMisureYClassAdImpMisure =  
     new com.thera.thermfw.web.WebEditGrid("YHdrImpMisure", "YClassAdImpMisure", 8, new String[]{"TitoloColonna", "IdFornitore", "IdAzienda", "BoAttribute", "ValoreDefault", "Escludi", "Azienda.Descrizione"}, 3, null, null,false,"com.thera.thermfw.web.servlet.GridActionAdapterForIndependentRow"); 
 YHdrImpMisureYClassAdImpMisure.setParent(YHdrImpMisureForm); 
 YHdrImpMisureYClassAdImpMisure.setNoControlRowKeys(false); 
 YHdrImpMisureYClassAdImpMisure.addHideAsDefault("Azienda.Descrizione"); 
 YHdrImpMisureYClassAdImpMisure.write(out); 
%>
<!--</span>-->
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
  errorList.setParent(YHdrImpMisureForm); 
  errorList.write(out); 
%>
<!--<span class="errorlist"></span>-->
          </td>
        </tr>
      </table>
    <%
  YHdrImpMisureForm.writeFormEndElements(out); 
%>
</form></td>
</tr>

<tr>
<td style="height:0">
<% String ftr = YHdrImpMisureForm.getCompleteFooter();
 if (ftr != null) { 
   request.setAttribute("dataCollector", YHdrImpMisureBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= ftr %>" flush="true"/> 
<% } %> 
</td>
</tr>
</table>


  <%
           // blocco YYY  
           // a completamento blocco di codice XXX in head 
              YHdrImpMisureForm.writeBodyEndElements(out); 
           } 
           else 
              errors.addAll(0, YHdrImpMisureBODC.getErrorList().getErrors()); 
        } 
        else 
           errors.addAll(0, YHdrImpMisureBODC.getErrorList().getErrors()); 
           if(YHdrImpMisureBODC.getConflict() != null) 
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
     if(YHdrImpMisureBODC != null && !YHdrImpMisureBODC.close(false)) 
        errors.addAll(0, YHdrImpMisureBODC.getErrorList().getErrors()); 
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
     String errorPage = YHdrImpMisureForm.getErrorPage(); 
%> 
     <jsp:include page="<%=errorPage%>" flush="true"/> 
<% 
  } 
  else 
  { 
     request.setAttribute("ConflictMessages", YHdrImpMisureBODC.getConflict()); 
     request.setAttribute("ErrorMessages", errors); 
     String conflictPage = YHdrImpMisureForm.getConflictPage(); 
%> 
     <jsp:include page="<%=conflictPage%>" flush="true"/> 
<% 
   } 
   } 
%> 
</body>
</html>
