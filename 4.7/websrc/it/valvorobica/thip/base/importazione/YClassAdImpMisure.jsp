<!-- WIZGEN Therm 2.0.0 as Form riga interna - multiBrowserGen = true -->

<% 
  if(false) 
  { 
%> 
<head><% 
  } 
%> 

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
  WebFormForInternalRowForm YClassAdImpMisureForm =  
     new com.thera.thermfw.web.WebFormForInternalRowForm(request, response, "YClassAdImpMisureForm", "YClassAdImpMisure", null, "com.thera.thermfw.web.servlet.FormActionAdapter", false, false, true, true, true, true, null, 0); 
  int mode = YClassAdImpMisureForm.getMode(); 
  String key = YClassAdImpMisureForm.getKey(); 
  String errorMessage; 
  boolean requestIsValid = false; 
  boolean leftIsKey = false; 
  String leftClass = ""; 
  try 
  {
     se.initialize(request, response); 
     if(se.begin()) 
     { 
        YClassAdImpMisureForm.outTraceInfo(getClass().getName()); 
        ClassADCollection globCadc = YClassAdImpMisureForm.getClassADCollection(); 
        requestIsValid = true; 
        YClassAdImpMisureForm.write(out); 
        String collectorName = YClassAdImpMisureForm.findBODataCollectorName(); 
				 YClassAdImpMisureBODC = (BODataCollector)Factory.createObject(collectorName); 
        YClassAdImpMisureBODC.initialize("YClassAdImpMisure", true, 0); 
        YClassAdImpMisureForm.setBODataCollector(YClassAdImpMisureBODC); 
        WebForm parentForm = (WebForm)request.getAttribute("parentForm"); 
        YClassAdImpMisureForm.setJSTypeList(parentForm.getOwnerForm().getJSTypeList()); 
        YClassAdImpMisureForm.setParent(parentForm); 
        YClassAdImpMisureForm.writeHeadElements(out); 
     }
  }
  catch(NamingException e) { 
    errorMessage = e.getMessage(); 
  } 
  catch(SQLException e) { 
     errorMessage = e.getMessage(); 
  } 
  finally 
  { 
     try 
     { 
        se.end(); 
     } 
     catch(IllegalArgumentException e) { 
        e.printStackTrace(); 
     } 
     catch(SQLException e) { 
        e.printStackTrace(); 
     } 
  } 
%> 
<% 
  if(false) 
  { 
%> 
</head><% 
  } 
%> 


<% 
  if(false) 
  { 
%> 
<body style="margin: 0px; overflow: hidden;"><% 
  } 
%> 
<%
   YClassAdImpMisureForm.writeBodyStartElements(out); 
%> 

  <% 
  if(false) 
  { 
%> 
<form name="YClassAdImpMisureForm"><% 
  } 
%> 
<%
   YClassAdImpMisureForm.writeFormStartElements(out); 
%> 

    <table cellpadding="0" cellspacing="0" height="100%" id="emptyborder" width="100%">
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
        <td>
          <% 
  WebTextInput YClassAdImpMisureClassHdr =  
     new com.thera.thermfw.web.WebTextInput("YClassAdImpMisure", "ClassHdr"); 
  YClassAdImpMisureClassHdr.setParent(YClassAdImpMisureForm); 
%>
<input class="<%=YClassAdImpMisureClassHdr.getClassType()%>" id="<%=YClassAdImpMisureClassHdr.getId()%>" maxlength="<%=YClassAdImpMisureClassHdr.getMaxLength()%>" name="<%=YClassAdImpMisureClassHdr.getName()%>" size="<%=YClassAdImpMisureClassHdr.getSize()%>" type="hidden"><% 
  YClassAdImpMisureClassHdr.write(out); 
%>

        </td>
      </tr>
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
                      <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YClassAdImpMisure", "ClassAd", null); 
   label.setParent(YClassAdImpMisureForm); 
%><label class="<%=label.getClassType()%>" for="ClassAD"><%label.write(out);%></label><%}%>
                    </td>
                    <td valign="top">
                      <% 
  WebMultiSearchForm YClassAdImpMisureClassAD =  
     new com.thera.thermfw.web.WebMultiSearchForm("YClassAdImpMisure", "ClassAD", false, false, true, 1, null, null); 
  YClassAdImpMisureClassAD.setParent(YClassAdImpMisureForm); 
  YClassAdImpMisureClassAD.write(out); 
%>
<!--<span class="multisearchform" id="ClassAD"></span>-->
                    </td>
                  </tr>
      <tr style="display:none;">
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
  <%
  YClassAdImpMisureForm.writeFormEndElements(out); 
%>
<% 
  if(false) 
  { 
%> 
</form><% 
  } 
%> 

<%
   YClassAdImpMisureForm.writeBodyEndElements(out); 
%> 
<% 
  if(false) 
  { 
%> 
</body><% 
  } 
%> 


