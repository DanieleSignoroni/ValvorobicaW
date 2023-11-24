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
  BODataCollector YCalendarioFiscaleValvoBODC = null; 
  List errors = new ArrayList(); 
  WebJSTypeList jsList = new WebJSTypeList(); 
  WebForm YCalendarioFiscaleValvoForm =  
     new com.thera.thermfw.web.WebForm(request, response, "YCalendarioFiscaleValvoForm", "YCalendarioFiscaleValvo", null, "it.valvorobica.thip.magazzino.chiusure.YCalendarioFiscaleFormActionAdapter", false, false, true, true, true, true, null, 1, false, "it/valvorobica/thip/magazzino/chiusure/YCalendarioFiscale.js"); 
  YCalendarioFiscaleValvoForm.setServletEnvironment(se); 
  YCalendarioFiscaleValvoForm.setJSTypeList(jsList); 
  YCalendarioFiscaleValvoForm.setHeader("it.thera.thip.cs.Header.jsp"); 
  YCalendarioFiscaleValvoForm.setFooter("it.thera.thip.cs.Footer.jsp"); 
  YCalendarioFiscaleValvoForm.setWebFormModifierClass("it.valvorobica.thip.magazzino.chiusure.YCalendarioFiscaleFormModifier"); 
  YCalendarioFiscaleValvoForm.setDeniedAttributeModeStr("hideNone"); 
  int mode = YCalendarioFiscaleValvoForm.getMode(); 
  String key = YCalendarioFiscaleValvoForm.getKey(); 
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
        YCalendarioFiscaleValvoForm.outTraceInfo(getClass().getName()); 
        String collectorName = YCalendarioFiscaleValvoForm.findBODataCollectorName(); 
                YCalendarioFiscaleValvoBODC = (BODataCollector)Factory.createObject(collectorName); 
        if (YCalendarioFiscaleValvoBODC instanceof WebDataCollector) 
            ((WebDataCollector)YCalendarioFiscaleValvoBODC).setServletEnvironment(se); 
        YCalendarioFiscaleValvoBODC.initialize("YCalendarioFiscaleValvo", true, 1); 
        YCalendarioFiscaleValvoForm.setBODataCollector(YCalendarioFiscaleValvoBODC); 
        int rcBODC = YCalendarioFiscaleValvoForm.initSecurityServices(); 
        mode = YCalendarioFiscaleValvoForm.getMode(); 
        if (rcBODC == BODataCollector.OK) 
        { 
           requestIsValid = true; 
           YCalendarioFiscaleValvoForm.write(out); 
           if(mode != WebForm.NEW) 
              rcBODC = YCalendarioFiscaleValvoBODC.retrieve(key); 
           if(rcBODC == BODataCollector.OK) 
           { 
              YCalendarioFiscaleValvoForm.writeHeadElements(out); 
           // fine blocco XXX  
           // a completamento blocco di codice YYY a fine body con catch e gestione errori 
%> 

<% 
  WebMenuBar menuBar = new com.thera.thermfw.web.WebMenuBar("HM_Array1", "150", "#000000","#000000","#A5B6CE","#E4EAEF","#FFFFFF","#000000"); 
  menuBar.setParent(YCalendarioFiscaleValvoForm); 
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
  myToolBarTB.setParent(YCalendarioFiscaleValvoForm); 
   request.setAttribute("toolBar", myToolBarTB); 
%> 
<jsp:include page="/it/thera/thip/cs/defObjMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="toolBar"/> 
</jsp:include> 
<% 
   myToolBarTB.write(out); 
%> 
</head>

<body bottommargin="0" leftmargin="0" onbeforeunload="<%=YCalendarioFiscaleValvoForm.getBodyOnBeforeUnload()%>" onload="<%=YCalendarioFiscaleValvoForm.getBodyOnLoad()%>" onunload="<%=YCalendarioFiscaleValvoForm.getBodyOnUnload()%>" rightmargin="0" topmargin="0"><%
   YCalendarioFiscaleValvoForm.writeBodyStartElements(out); 
%> 




	<table width="100%" height="100%" cellspacing="0" cellpadding="0">
<tr>
<td style="height:0" valign="top">
<% String hdr = YCalendarioFiscaleValvoForm.getCompleteHeader();
 if (hdr != null) { 
   request.setAttribute("dataCollector", YCalendarioFiscaleValvoBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= hdr %>" flush="true"/> 
<% } %> 
</td>
</tr>

<tr>
<td valign="top" height="100%">
<form action="<%=YCalendarioFiscaleValvoForm.getServlet()%>" method="post" name="form" style="height:100%"><%
  YCalendarioFiscaleValvoForm.writeFormStartElements(out); 
%>


		<table cellpadding="2" cellspacing="0" height="100%" id="emptyborder" width="100%">
			<tr>
				<td style="height: 0"><% menuBar.writeElements(out); %> 

				</td>
			</tr>
			<tr>
				<td style="height: 0"><% myToolBarTB.writeChildren(out); %> 

				</td>
			</tr>
			<tr>
				<td height="100%"><!--<span class="tabbed" id="myTabbed">-->
<table width="100%" height="100%" cellpadding="0" cellspacing="0" style="padding-right:1px">
   <tr valign="top">
     <td><% 
  WebTabbed myTabbed = new com.thera.thermfw.web.WebTabbed("myTabbed", "100%", "100%"); 
  myTabbed.setParent(YCalendarioFiscaleValvoForm); 
 myTabbed.addTab("tabAnnoFiscale", "it.thera.thip.magazzino.chiusure.resources.Chiusure", "CalenFiscAnnoFiscale", "YCalendarioFiscaleValvo", null, null, null, null); 
 myTabbed.addTab("tabNLS", "it.thera.thip.magazzino.chiusure.resources.Chiusure", "CalenFiscDescrNLS", "YCalendarioFiscaleValvo", null, null, null, null); 
 myTabbed.addTab("tabPeriodi", "it.thera.thip.magazzino.chiusure.resources.Chiusure", "CalenFiscPeriodi", "YCalendarioFiscaleValvo", null, null, null, null); 
  myTabbed.write(out); 
%>

     </td>
   </tr>
   <tr>
     <td height="100%"><div class="tabbed_pagine" id="tabbedPagine" style="position: relative; width: 100%; height: 100%;"> <div class="tabbed_page" id="<%=myTabbed.getTabPageId("tabAnnoFiscale")%>" style="width:100%;height:100%;overflow:auto;"><% myTabbed.startTab("tabAnnoFiscale"); %>
							<table cellpadding="2" cellspacing="2" width="60%">
								<tr>
									<td colspan="2"><% 
  WebTextInput YCalendarioFiscaleValvoCodiceAzienda =  
     new com.thera.thermfw.web.WebTextInput("YCalendarioFiscaleValvo", "CodiceAzienda"); 
  YCalendarioFiscaleValvoCodiceAzienda.setParent(YCalendarioFiscaleValvoForm); 
%>
<input class="<%=YCalendarioFiscaleValvoCodiceAzienda.getClassType()%>" id="<%=YCalendarioFiscaleValvoCodiceAzienda.getId()%>" maxlength="<%=YCalendarioFiscaleValvoCodiceAzienda.getMaxLength()%>" name="<%=YCalendarioFiscaleValvoCodiceAzienda.getName()%>" size="<%=YCalendarioFiscaleValvoCodiceAzienda.getSize()%>" style="display: none" type="text"><% 
  YCalendarioFiscaleValvoCodiceAzienda.write(out); 
%>
</td>
								</tr>
								<tr>
									<td width="35%"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YCalendarioFiscaleValvo", "CodiceAnnoFiscale", null); 
   label.setParent(YCalendarioFiscaleValvoForm); 
%><label class="<%=label.getClassType()%>" for="CodiceAnnoFiscale"><%label.write(out);%></label><%}%></td>
									<td width="64%"><% 
  WebTextInput YCalendarioFiscaleValvoCodiceAnnoFiscale =  
     new com.thera.thermfw.web.WebTextInput("YCalendarioFiscaleValvo", "CodiceAnnoFiscale"); 
  YCalendarioFiscaleValvoCodiceAnnoFiscale.setParent(YCalendarioFiscaleValvoForm); 
%>
<input class="<%=YCalendarioFiscaleValvoCodiceAnnoFiscale.getClassType()%>" id="<%=YCalendarioFiscaleValvoCodiceAnnoFiscale.getId()%>" maxlength="<%=YCalendarioFiscaleValvoCodiceAnnoFiscale.getMaxLength()%>" name="<%=YCalendarioFiscaleValvoCodiceAnnoFiscale.getName()%>" size="<%=YCalendarioFiscaleValvoCodiceAnnoFiscale.getSize()%>" type="text"><% 
  YCalendarioFiscaleValvoCodiceAnnoFiscale.write(out); 
%>
</td>
								</tr>
								<% 
   request.setAttribute("parentForm", YCalendarioFiscaleValvoForm); 
   String CDForDescrizione$it$thera$thip$cs$Descrizione$jsp = "Descrizione"; 
%>
<jsp:include page="/it/thera/thip/cs/Descrizione.jsp" flush="true"> 
<jsp:param name="CDName" value="<%=CDForDescrizione$it$thera$thip$cs$Descrizione$jsp%>"/> 
</jsp:include> 
<!--<span class="subform" id="Descrizione" name="Descrizione"></span>-->
							</table>

							<table cellpadding="2" cellspacing="2" width="60%">
								<tr>
									<td>
										<fieldset name="validita">
											<legend align="top">
												<label class="thLabel" id="Validita" name="Validita">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.magazzino.chiusure.resources.Chiusure", "Validita", null, null, null, null); 
 label.setParent(YCalendarioFiscaleValvoForm); 
label.write(out); }%> 
</label>
											</legend>
											<table width="100%">
												<tr>
													<td width="35%"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YCalendarioFiscaleValvo", "DataInizio", null); 
   label.setParent(YCalendarioFiscaleValvoForm); 
%><label class="<%=label.getClassType()%>" for="DataInizio"><%label.write(out);%></label><%}%>
													</td>
													<td width="35%"><% 
  WebTextInput YCalendarioFiscaleValvoDataInizio =  
     new com.thera.thermfw.web.WebTextInput("YCalendarioFiscaleValvo", "DataInizio"); 
  YCalendarioFiscaleValvoDataInizio.setParent(YCalendarioFiscaleValvoForm); 
%>
<input class="<%=YCalendarioFiscaleValvoDataInizio.getClassType()%>" id="<%=YCalendarioFiscaleValvoDataInizio.getId()%>" maxlength="<%=YCalendarioFiscaleValvoDataInizio.getMaxLength()%>" name="<%=YCalendarioFiscaleValvoDataInizio.getName()%>" size="<%=YCalendarioFiscaleValvoDataInizio.getSize()%>" type="text"><% 
  YCalendarioFiscaleValvoDataInizio.write(out); 
%>
</td>
													<td rowspan="2" width="30">
														<button id="GeneraPeriodi" name="GeneraPeriodi" onclick="generaPeriodiOnClick()" type="button"><%= ResourceLoader.getString("it.thera.thip.magazzino.chiusure.resources.Chiusure", "GeneraPeriodiButton")%></button>
													</td>
												</tr>
												<tr>
													<td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YCalendarioFiscaleValvo", "DataFine", null); 
   label.setParent(YCalendarioFiscaleValvoForm); 
%><label class="<%=label.getClassType()%>" for="DataFine"><%label.write(out);%></label><%}%></td>
													<td><% 
  WebTextInput YCalendarioFiscaleValvoDataFine =  
     new com.thera.thermfw.web.WebTextInput("YCalendarioFiscaleValvo", "DataFine"); 
  YCalendarioFiscaleValvoDataFine.setParent(YCalendarioFiscaleValvoForm); 
%>
<input class="<%=YCalendarioFiscaleValvoDataFine.getClassType()%>" id="<%=YCalendarioFiscaleValvoDataFine.getId()%>" maxlength="<%=YCalendarioFiscaleValvoDataFine.getMaxLength()%>" name="<%=YCalendarioFiscaleValvoDataFine.getName()%>" size="<%=YCalendarioFiscaleValvoDataFine.getSize()%>" type="text"><% 
  YCalendarioFiscaleValvoDataFine.write(out); 
%>

													</td>
												</tr>
											</table>
										</fieldset>
									</td>
								</tr>
								<tr>
									<td>
										<fieldset name="x">
											<table width="100%">
												<tr>
													<td width="35%"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YCalendarioFiscaleValvo", "EsecAttivAnnoFiscale", null); 
   label.setParent(YCalendarioFiscaleValvoForm); 
%><label class="<%=label.getClassType()%>" for="EsecAttivAnnoFiscale"><%label.write(out);%></label><%}%>
													</td>
													<td width="65%"><% 
  WebComboBox YCalendarioFiscaleValvoEsecAttivAnnoFiscale =  
     new com.thera.thermfw.web.WebComboBox("YCalendarioFiscaleValvo", "EsecAttivAnnoFiscale", null); 
  YCalendarioFiscaleValvoEsecAttivAnnoFiscale.setParent(YCalendarioFiscaleValvoForm); 
%>
<select id="<%=YCalendarioFiscaleValvoEsecAttivAnnoFiscale.getId()%>" name="<%=YCalendarioFiscaleValvoEsecAttivAnnoFiscale.getName()%>" style="width: 100%"><% 
  YCalendarioFiscaleValvoEsecAttivAnnoFiscale.write(out); 
%> 

															
													</select></td>
												</tr>
												<tr>
													<td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YCalendarioFiscaleValvo", "StatoAnnoFiscale", null); 
   label.setParent(YCalendarioFiscaleValvoForm); 
%><label class="<%=label.getClassType()%>" for="StatoAnnoFiscale"><%label.write(out);%></label><%}%></td>
													<td><% 
  WebComboBox YCalendarioFiscaleValvoStatoAnnoFiscale =  
     new com.thera.thermfw.web.WebComboBox("YCalendarioFiscaleValvo", "StatoAnnoFiscale", null); 
  YCalendarioFiscaleValvoStatoAnnoFiscale.setParent(YCalendarioFiscaleValvoForm); 
%>
<select id="<%=YCalendarioFiscaleValvoStatoAnnoFiscale.getId()%>" name="<%=YCalendarioFiscaleValvoStatoAnnoFiscale.getName()%>" style="width: 100%"><% 
  YCalendarioFiscaleValvoStatoAnnoFiscale.write(out); 
%> 

															
													</select></td>
												</tr>
												<tr>
													<td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YCalendarioFiscaleValvo", "NumMesiCostoMedioLIFO", null); 
   label.setParent(YCalendarioFiscaleValvoForm); 
%><label class="<%=label.getClassType()%>" for="NumMesiCostoMedioLIFO"><%label.write(out);%></label><%}%>
													</td>
													<td><% 
  WebTextInput YCalendarioFiscaleValvoNumMesiCostoMedioLIFO =  
     new com.thera.thermfw.web.WebTextInput("YCalendarioFiscaleValvo", "NumMesiCostoMedioLIFO"); 
  YCalendarioFiscaleValvoNumMesiCostoMedioLIFO.setParent(YCalendarioFiscaleValvoForm); 
%>
<input class="<%=YCalendarioFiscaleValvoNumMesiCostoMedioLIFO.getClassType()%>" id="<%=YCalendarioFiscaleValvoNumMesiCostoMedioLIFO.getId()%>" maxlength="<%=YCalendarioFiscaleValvoNumMesiCostoMedioLIFO.getMaxLength()%>" name="<%=YCalendarioFiscaleValvoNumMesiCostoMedioLIFO.getName()%>" size="3" type="text"><% 
  YCalendarioFiscaleValvoNumMesiCostoMedioLIFO.write(out); 
%>

													</td>
												</tr>
												<% 
   request.setAttribute("parentForm", YCalendarioFiscaleValvoForm); 
   String CDForDatiComuniEstesi$it$thera$thip$cs$DatiComuniEstesi$jsp = "DatiComuniEstesi"; 
%>
<jsp:include page="/it/thera/thip/cs/DatiComuniEstesi.jsp" flush="true"> 
<jsp:param name="CDName" value="<%=CDForDatiComuniEstesi$it$thera$thip$cs$DatiComuniEstesi$jsp%>"/> 
</jsp:include> 
<!--<span class="subform" id="DatiComuniEstesi" name="DatiComuniEstesi"></span>-->
											</table>
										</fieldset>
									</td>
								</tr>
								<tr>
									<td>
										<fieldset name="x">
											<table width="100%">
												<tr>
													<td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YCalendarioFiscaleValvo", "DataUltimaChiusura", null); 
   label.setParent(YCalendarioFiscaleValvoForm); 
%><label class="<%=label.getClassType()%>" for="DataUltimaChiusura"><%label.write(out);%></label><%}%></td>
													<td><% 
  WebTextInput YCalendarioFiscaleValvoDataUltimaChiusura =  
     new com.thera.thermfw.web.WebTextInput("YCalendarioFiscaleValvo", "DataUltimaChiusura"); 
  YCalendarioFiscaleValvoDataUltimaChiusura.setParent(YCalendarioFiscaleValvoForm); 
%>
<input class="<%=YCalendarioFiscaleValvoDataUltimaChiusura.getClassType()%>" id="<%=YCalendarioFiscaleValvoDataUltimaChiusura.getId()%>" maxlength="<%=YCalendarioFiscaleValvoDataUltimaChiusura.getMaxLength()%>" name="<%=YCalendarioFiscaleValvoDataUltimaChiusura.getName()%>" size="<%=YCalendarioFiscaleValvoDataUltimaChiusura.getSize()%>" type="text"><% 
  YCalendarioFiscaleValvoDataUltimaChiusura.write(out); 
%>

													</td>
												</tr>
												<tr>
													<td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "YCalendarioFiscaleValvo", "StatoChiusuraMag", null); 
   label.setParent(YCalendarioFiscaleValvoForm); 
%><label class="<%=label.getClassType()%>" for="StatoChiusuraMag"><%label.write(out);%></label><%}%></td>
													<td><% 
  WebComboBox YCalendarioFiscaleValvoStatoChiusuraMag =  
     new com.thera.thermfw.web.WebComboBox("YCalendarioFiscaleValvo", "StatoChiusuraMag", null); 
  YCalendarioFiscaleValvoStatoChiusuraMag.setParent(YCalendarioFiscaleValvoForm); 
%>
<select id="<%=YCalendarioFiscaleValvoStatoChiusuraMag.getId()%>" name="<%=YCalendarioFiscaleValvoStatoChiusuraMag.getName()%>" style="width: 100%"><% 
  YCalendarioFiscaleValvoStatoChiusuraMag.write(out); 
%> 

															
													</select></td>
												</tr>
											</table>
										</fieldset>
									</td>
								</tr>
							</table>
					<% myTabbed.endTab(); %> 
</div> <div class="tabbed_page" id="<%=myTabbed.getTabPageId("tabNLS")%>" style="width:100%;height:100%;overflow:auto;"><% myTabbed.startTab("tabNLS"); %>
							<table width="100%">
								<tr>
									<td valign="top" width="100%"><% 
   request.setAttribute("parentForm", YCalendarioFiscaleValvoForm); 
   String CDForDescrizione$it$thera$thip$cs$DescrizioneInLingua$jsp = "Descrizione"; 
%>
<jsp:include page="/it/thera/thip/cs/DescrizioneInLingua.jsp" flush="true"> 
<jsp:param name="CDName" value="<%=CDForDescrizione$it$thera$thip$cs$DescrizioneInLingua$jsp%>"/> 
</jsp:include> 
<!--<span class="subform" id="DescrizioneNLS" name="DescrizioneNLS"></span>--></td>
								</tr>
							</table>
					<% myTabbed.endTab(); %> 
</div> <div class="tabbed_page" id="<%=myTabbed.getTabPageId("tabPeriodi")%>" style="width:100%;height:100%;overflow:auto;"><% myTabbed.startTab("tabPeriodi"); %>
							<table width="100%">
								<tr>
									<td valign="top" width="50%"><!--<span class="editgrid" id="Periodi" name="Periodi">--><% 
  WebEditGrid YCalendarioFiscaleValvoPeriodi =  
     new com.thera.thermfw.web.WebEditGrid("YCalendarioFiscaleValvo", "Periodi", 12, new String[]{"CodicePeriodo", "DataInizio", "DataFine"}, 1, null, null,false,"com.thera.thermfw.web.servlet.GridActionAdapterForIndependentRow"); 
 YCalendarioFiscaleValvoPeriodi.setParent(YCalendarioFiscaleValvoForm); 
 YCalendarioFiscaleValvoPeriodi.setNoControlRowKeys(true); 
 YCalendarioFiscaleValvoPeriodi.write(out); 
%>
<!--</span>--></td>
									<td valign="top" width="50%">
										<fieldset id="y">
											<span class="rowform" id="formPeriodi" name="formPeriodi"><BR><% 
   request.setAttribute("parentForm", YCalendarioFiscaleValvoForm); 
   String CDForPeriodi = "Periodi"; 
%>
<jsp:include page="/it/thera/thip/magazzino/chiusure/PeriodoCalFiscale.jsp" flush="true"> 
<jsp:param name="EditGridCDName" value="<%=CDForPeriodi%>"/> 
<jsp:param name="Mode" value="NEW"/> 
</jsp:include> 
</span>
										</fieldset>
									</td>
								</tr>
							</table>
					<% myTabbed.endTab(); %> 
</div>
				</div><% myTabbed.endTabbed();%> 

     </td>
   </tr>
</table><!--</span>--></td>
			</tr>
			<tr>
				<td style="height: 0"><% 
  WebErrorList errorList = new com.thera.thermfw.web.WebErrorList(); 
  errorList.setParent(YCalendarioFiscaleValvoForm); 
  errorList.write(out); 
%>
<!--<span class="errorlist"></span>--></td>
			</tr>
		</table>

	<%
  YCalendarioFiscaleValvoForm.writeFormEndElements(out); 
%>
</form></td>
</tr>

<tr>
<td style="height:0">
<% String ftr = YCalendarioFiscaleValvoForm.getCompleteFooter();
 if (ftr != null) { 
   request.setAttribute("dataCollector", YCalendarioFiscaleValvoBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= ftr %>" flush="true"/> 
<% } %> 
</td>
</tr>
</table>



<%
           // blocco YYY  
           // a completamento blocco di codice XXX in head 
              YCalendarioFiscaleValvoForm.writeBodyEndElements(out); 
           } 
           else 
              errors.addAll(0, YCalendarioFiscaleValvoBODC.getErrorList().getErrors()); 
        } 
        else 
           errors.addAll(0, YCalendarioFiscaleValvoBODC.getErrorList().getErrors()); 
           if(YCalendarioFiscaleValvoBODC.getConflict() != null) 
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
     if(YCalendarioFiscaleValvoBODC != null && !YCalendarioFiscaleValvoBODC.close(false)) 
        errors.addAll(0, YCalendarioFiscaleValvoBODC.getErrorList().getErrors()); 
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
     String errorPage = YCalendarioFiscaleValvoForm.getErrorPage(); 
%> 
     <jsp:include page="<%=errorPage%>" flush="true"/> 
<% 
  } 
  else 
  { 
     request.setAttribute("ConflictMessages", YCalendarioFiscaleValvoBODC.getConflict()); 
     request.setAttribute("ErrorMessages", errors); 
     String conflictPage = YCalendarioFiscaleValvoForm.getConflictPage(); 
%> 
     <jsp:include page="<%=conflictPage%>" flush="true"/> 
<% 
   } 
   } 
%> 
</body>
</html>
