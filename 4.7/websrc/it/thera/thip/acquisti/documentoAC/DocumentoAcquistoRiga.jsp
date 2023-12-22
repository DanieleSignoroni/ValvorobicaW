<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN"
                      "file:///C:/pthsvil/deps/T_Thip_40/Thip/4.0/websrcsvil/dtd/xhtml1-transitional.dtd">
<!-- 34124 -->
<!-- 34326  -->
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
  BODataCollector DocumentoAcquistoRigaPrmBODC = null; 
  List errors = new ArrayList(); 
  WebJSTypeList jsList = new WebJSTypeList(); 
  WebForm DocumentoAcquistoRigaPrmForm =  
     new com.thera.thermfw.web.WebForm(request, response, "DocumentoAcquistoRigaPrmForm", "DocumentoAcquistoRigaPrm", null, "it.thera.thip.acquisti.documentoAC.web.DocumentoAcquistoRigaPrmCompletaFormActionAdapter", false, false, true, true, true, true, "it.thera.thip.acquisti.documentoAC.web.DocumentoAcquistoRigaPrmDataCollector", 1, true, "it/thera/thip/acquisti/documentoAC/DocumentoAcquistoRiga.js"); 
  DocumentoAcquistoRigaPrmForm.setServletEnvironment(se); 
  DocumentoAcquistoRigaPrmForm.setJSTypeList(jsList); 
  DocumentoAcquistoRigaPrmForm.setHeader("it.thera.thip.cs.Header.jsp"); 
  DocumentoAcquistoRigaPrmForm.setFooter("it.thera.thip.cs.Footer.jsp"); 
  DocumentoAcquistoRigaPrmForm.setWebFormModifierClass("it.thera.thip.acquisti.documentoAC.web.DocumentoAcqRigaCompletaFormModifier"); 
  DocumentoAcquistoRigaPrmForm.setDeniedAttributeModeStr("hideNone"); 
  int mode = DocumentoAcquistoRigaPrmForm.getMode(); 
  String key = DocumentoAcquistoRigaPrmForm.getKey(); 
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
        DocumentoAcquistoRigaPrmForm.outTraceInfo(getClass().getName()); 
        String collectorName = DocumentoAcquistoRigaPrmForm.findBODataCollectorName(); 
                DocumentoAcquistoRigaPrmBODC = (BODataCollector)Factory.createObject(collectorName); 
        if (DocumentoAcquistoRigaPrmBODC instanceof WebDataCollector) 
            ((WebDataCollector)DocumentoAcquistoRigaPrmBODC).setServletEnvironment(se); 
        DocumentoAcquistoRigaPrmBODC.initialize("DocumentoAcquistoRigaPrm", true, 1); 
        DocumentoAcquistoRigaPrmForm.setBODataCollector(DocumentoAcquistoRigaPrmBODC); 
        int rcBODC = DocumentoAcquistoRigaPrmForm.initSecurityServices(); 
        mode = DocumentoAcquistoRigaPrmForm.getMode(); 
        if (rcBODC == BODataCollector.OK) 
        { 
           requestIsValid = true; 
           DocumentoAcquistoRigaPrmForm.write(out); 
           if(mode != WebForm.NEW) 
              rcBODC = DocumentoAcquistoRigaPrmBODC.retrieve(key); 
           if(rcBODC == BODataCollector.OK) 
           { 
              DocumentoAcquistoRigaPrmForm.writeHeadElements(out); 
           // fine blocco XXX  
           // a completamento blocco di codice YYY a fine body con catch e gestione errori 
%> 

	<title>Documento Acquisto Riga Primaria</title>
<STYLE type="text/css">
<!--  .thLabel{   font-family : Arial;   font-size : 9pt;   color: black;  }  .myLabel{   font-family : Arial;   font-size : 9pt;   color: black;   font-weight : bold;  } -->
</STYLE>

<% 
  WebMenuBar menuBar = new com.thera.thermfw.web.WebMenuBar("HM_Array1", "150", "#000000","#000000","#A5B6CE","#E4EAEF","#FFFFFF","#000000"); 
  menuBar.setParent(DocumentoAcquistoRigaPrmForm); 
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
  myToolBarTB.setParent(DocumentoAcquistoRigaPrmForm); 
   request.setAttribute("toolBar", myToolBarTB); 
%> 
<jsp:include page="/it/thera/thip/cs/defObjMenu.jsp" flush="true"> 
<jsp:param name="partRequest" value="toolBar"/> 
</jsp:include> 
<% 
   myToolBarTB.write(out); 
%> 
</head>

<% 
  WebScript script_0 =  
   new com.thera.thermfw.web.WebScript(); 
 script_0.setRequest(request); 
 script_0.setSrcAttribute("it/thera/thip/acquisti/documentoAC/ComuneAlleRighe.js"); 
 script_0.setLanguageAttribute("JavaScript1.2"); 
  script_0.write(out); 
%>
<!--<script language="JavaScript1.2" type="text/javascript" src="it/thera/thip/acquisti/documentoAC/ComuneAlleRighe.js"></script>-->
<% 
  WebScript script_1 =  
   new com.thera.thermfw.web.WebScript(); 
 script_1.setRequest(request); 
 script_1.setSrcAttribute("it/thera/thip/base/comuniVenAcq/AggiornaLotti.js"); 
 script_1.setLanguageAttribute("JavaScript1.2"); 
  script_1.write(out); 
%>
<!--<script language="JavaScript1.2" type="text/javascript" src="it/thera/thip/base/comuniVenAcq/AggiornaLotti.js"></script>-->

<body leftmargin="0" rightmargin="0" topmargin="0" bottommargin="0" onload="<%=DocumentoAcquistoRigaPrmForm.getBodyOnLoad()%>" onunload="<%=DocumentoAcquistoRigaPrmForm.getBodyOnUnload()%>" onbeforeunload="<%=DocumentoAcquistoRigaPrmForm.getBodyOnBeforeUnload()%>"><%
   DocumentoAcquistoRigaPrmForm.writeBodyStartElements(out); 
%> 



	<table width="100%" height="100%" cellspacing="0" cellpadding="0">
<tr>
<td style="height:0" valign="top">
<% String hdr = DocumentoAcquistoRigaPrmForm.getCompleteHeader();
 if (hdr != null) { 
   request.setAttribute("dataCollector", DocumentoAcquistoRigaPrmBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= hdr %>" flush="true"/> 
<% } %> 
</td>
</tr>

<tr>
<td valign="top" height="100%">
<form name="DocumentoAcquistoRigaForm" action="<%=DocumentoAcquistoRigaPrmForm.getServlet()%>" method="post" style="height:100%"><%
  DocumentoAcquistoRigaPrmForm.writeFormStartElements(out); 
%>

		<table cellspacing="1" cellpadding="1" height="100%" width="100%">

			<!-- **************************************************************************************************** -->
			<!-- Menubar -->
			<tr>
				<td style="height:0">
					<% menuBar.writeElements(out); %> 

				</td>
			</tr>

			<!-- **************************************************************************************************** -->
			<!-- Toolbar -->
			<tr>
				<td style="height:0">
					<% myToolBarTB.writeChildren(out); %> 

				</td>
			</tr>

			<!-- **************************************************************************************************** -->
			<!-- Pannello tabulare principale -->
			<tr>
				<td height="100%">
					<!--<span class="tabbed" id="MainTabbed">-->
<table width="100%" height="100%" cellpadding="0" cellspacing="0" style="padding-right:1px">
   <tr valign="top">
     <td><% 
  WebTabbed MainTabbed = new com.thera.thermfw.web.WebTabbed("MainTabbed", "100%", "100%"); 
  MainTabbed.setParent(DocumentoAcquistoRigaPrmForm); 
 MainTabbed.addTab("GeneraleTab", "it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "GeneraleTab", "DocumentoAcquistoRigaPrm", null, null, null, null); 
 MainTabbed.addTab("PrezziScontiTab", "it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "PrezziScontiTab", "DocumentoAcquistoRigaPrm", null, null, null, "verificaCondizioniRecuperoDatiAcquisto()"); 
 MainTabbed.addTab("CondEvasioneTab", "it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "CondEvasioneTab", "DocumentoAcquistoRigaPrm", null, null, null, null); 
 MainTabbed.addTab("ContabilitaTab", "it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "ContabilitaTab", "DocumentoAcquistoRigaPrm", null, null, null, null); 
 MainTabbed.addTab("CommentiMultimediaTab", "it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "CommentiMultimediaTab", "DocumentoAcquistoRigaPrm", null, null, null, null); 
 MainTabbed.addTab("LottiTab", "it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "LottiTab", "DocumentoAcquistoRigaPrm", null, null, null, null); 
 MainTabbed.addTab("SpeditoTab", "it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "SpeditoTab", "DocumentoAcquistoRigaPrm", null, null, null, null); 
 MainTabbed.addTab("ContenitoriTab", "it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "ContenitoriTab", "DocumentoAcquistoRigaPrm", null, null, null, null); 
 MainTabbed.addTab("RiepilogoTab", "it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "RiepilogoTab", "DocumentoAcquistoRigaPrm", null, null, null, null); 
  MainTabbed.write(out); 
%>

     </td>
   </tr>
   <tr>
     <td height="100%"><div class="tabbed_pagine" id="tabbedPagine" style="position: relative; width: 100%; height: 100%;">
						<!-- **************************************************************************************** -->
						<!-- Cartella Generale -->
						<div class="tabbed_page" id="<%=MainTabbed.getTabPageId("GeneraleTab")%>" style="width:100%;height:100%;overflow:auto;"><% MainTabbed.startTab("GeneraleTab"); %>
							<!-- Fix 39398 - inizio modifiche grafiche -->
							<!-- <table border="0" cellspacing="1" cellpadding="1"> -->
							<table cellspacing="2" cellpadding="2" style="padding: 3px">
								<!-- Fix 2844: inizio -->
								<!--       ME: se non li metto in questa posizione non vengono sentiti -->
								<!--           nella DOList del contenitore                            -->
								<tr>
									<td colspan="2">
										<% 
  WebTextInput DocumentoAcquistoRigaPrmIdConfigurazione =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "IdConfigurazione"); 
  DocumentoAcquistoRigaPrmIdConfigurazione.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmIdConfigurazione.getSize()%>" id="<%=DocumentoAcquistoRigaPrmIdConfigurazione.getId()%>" name="<%=DocumentoAcquistoRigaPrmIdConfigurazione.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmIdConfigurazione.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmIdConfigurazione.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmIdConfigurazione.write(out); 
%>

									</td>
									<td colspan="3">
										<% 
  WebTextInput DocumentoAcquistoRigaPrmIdIntestatario =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "IdIntestatario"); 
  DocumentoAcquistoRigaPrmIdIntestatario.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmIdIntestatario.getSize()%>" id="<%=DocumentoAcquistoRigaPrmIdIntestatario.getId()%>" name="<%=DocumentoAcquistoRigaPrmIdIntestatario.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmIdIntestatario.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmIdIntestatario.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmIdIntestatario.write(out); 
%>

									</td>
									<td colspan="1">
										<% 
  WebTextInput DocumentoAcquistoRigaPrmLavorazioneEsterna =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "LavorazioneEsterna"); 
  DocumentoAcquistoRigaPrmLavorazioneEsterna.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmLavorazioneEsterna.getSize()%>" id="<%=DocumentoAcquistoRigaPrmLavorazioneEsterna.getId()%>" name="<%=DocumentoAcquistoRigaPrmLavorazioneEsterna.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmLavorazioneEsterna.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmLavorazioneEsterna.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmLavorazioneEsterna.write(out); 
%>

									</td>
									<td colspan="1">
										<input type="hidden" id="IdNumeratoreArtIntestField" name="IdNumeratoreArtIntestField">
									</td>
								</tr>
								<!-- Fix 2844 fine -->
								<tr>
									<!-- Sequenza Riga -->
								 	<td nowrap style="width: 105px" colspan="2"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "SequenzaRiga", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="SequenzaField" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
									<!-- Fix 3147 inizio -->
									<!-- <td></td> -->
									<!-- Fix 3147 fine -->
			  						<td colspan="3"><% 
  WebTextInput DocumentoAcquistoRigaPrmSequenzaRiga =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "SequenzaRiga"); 
  DocumentoAcquistoRigaPrmSequenzaRiga.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="10" id="<%=DocumentoAcquistoRigaPrmSequenzaRiga.getId()%>" name="<%=DocumentoAcquistoRigaPrmSequenzaRiga.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmSequenzaRiga.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmSequenzaRiga.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmSequenzaRiga.write(out); 
%>
</td>

									<!-- Combo Stato Avanzamento -->
									<td nowrap style="width: 150px"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "StatoAvanzamento", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="StatoAvanzamentoCombo" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
									<td colspan="7"><% 
  WebComboBox DocumentoAcquistoRigaPrmStatoAvanzamento =  
     new com.thera.thermfw.web.WebComboBox("DocumentoAcquistoRigaPrm", "StatoAvanzamento", null); 
  DocumentoAcquistoRigaPrmStatoAvanzamento.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmStatoAvanzamento.setOnChange("recuperaQuantita(true)"); 
%>
<select name="<%=DocumentoAcquistoRigaPrmStatoAvanzamento.getName()%>" id="<%=DocumentoAcquistoRigaPrmStatoAvanzamento.getId()%>"><% 
  DocumentoAcquistoRigaPrmStatoAvanzamento.write(out); 
%> 
</select></td>
								</tr>
								<tr>
									<!-- Proxy Causale -->
									<td nowrap colspan="2"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "IdCausaleRigaAcquisto", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="CausaleProxy" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
									<!-- Fix 3147 inizio -->
									<!-- <td></td> -->
									<!-- Fix 3147 fine -->
									<td nowrap colspan="3"><% 
  WebMultiSearchForm DocumentoAcquistoRigaPrmRCausaleRigaAcquisto =  
     new com.thera.thermfw.web.WebMultiSearchForm("DocumentoAcquistoRigaPrm", "RCausaleRigaAcquisto", false, false, true, 1, null, null); 
  DocumentoAcquistoRigaPrmRCausaleRigaAcquisto.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmRCausaleRigaAcquisto.write(out); 
%>
<!--<span class="multisearchform" id="CausaleProxy"></span>--></td>
									<!-- Check Non Fatturare -->
									<td>
										<% 
  WebCheckBox DocumentoAcquistoRigaPrmNonFatturare =  
     new com.thera.thermfw.web.WebCheckBox("DocumentoAcquistoRigaPrm", "NonFatturare"); 
  DocumentoAcquistoRigaPrmNonFatturare.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmNonFatturare.setOnClick("variazioneFlagNonFatturare()"); 
%>
<input type="checkbox" name="<%=DocumentoAcquistoRigaPrmNonFatturare.getName()%>" id="<%=DocumentoAcquistoRigaPrmNonFatturare.getId()%>" value="Y"><%
  DocumentoAcquistoRigaPrmNonFatturare.write(out); 
%>

									</td>

									<!-- Proxy Spesa -->
									<!--          <td><label for="SpesaProxy">Spesa</label></td>          <td nowrap="true"><span class="multisearchform" id ="SpesaProxy"></span></td>          <td>&nbsp;</td>          -->
								</tr>
								<!-- Fix 16972 inizio -->
								<tr id="OrdineSrvTR" name="OrdineSrvTR" style="display:none">
									<!-- Proxy Ordine Servizio -->
									<td nowrap colspan="2"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "IdNumeroOrdineSrv", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="OrdineServizioProxy" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
									<!-- <td></td> -->
									<td nowrap colspan="5"><% 
  WebMultiSearchForm DocumentoAcquistoRigaPrmOrdineServizio =  
     new com.thera.thermfw.web.WebMultiSearchForm("DocumentoAcquistoRigaPrm", "OrdineServizio", false, false, true, 2, null, null); 
  DocumentoAcquistoRigaPrmOrdineServizio.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmOrdineServizio.write(out); 
%>
<!--<span class="multisearchform" id="OrdineServizioProxy"></span>--></td>
								</tr>
								<tr id="AttivitaTR" name="AttivitaTR" style="display:none">
									<!-- Proxy Attivita -->
									<td nowrap colspan="2"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "IdAttivita", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="AttivitaProxy" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
									<!-- <td></td> -->
									<td nowrap colspan="5"><% 
  WebMultiSearchForm DocumentoAcquistoRigaPrmAttivita =  
     new com.thera.thermfw.web.WebMultiSearchForm("DocumentoAcquistoRigaPrm", "Attivita", false, false, true, 1, null, null); 
  DocumentoAcquistoRigaPrmAttivita.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmAttivita.write(out); 
%>
<!--<span class="multisearchform" id="AttivitaProxy"></span>--></td>
								</tr>
								<tr id="BeneTR" name="BeneTR" style="display:none">
									<!-- Proxy Bene -->
									<td nowrap colspan="2"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "IdBene", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="BeneProxy" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
									<!-- <td></td> -->
									<td nowrap colspan="5"><% 
  WebMultiSearchForm DocumentoAcquistoRigaPrmBene =  
     new com.thera.thermfw.web.WebMultiSearchForm("DocumentoAcquistoRigaPrm", "Bene", false, false, true, 1, null, null); 
  DocumentoAcquistoRigaPrmBene.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmBene.write(out); 
%>
<!--<span class="multisearchform" id="BeneProxy"></span>--></td>
								</tr>
								<!-- Fix 16972 fine -->

								<tr>
									<!-- Proxy Magazzino -->
									<td nowrap colspan="2"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "IdMagazzino", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="MagazzinoProxy" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
									<!-- Fix 3147 inizio -->
									<!-- <td></td> -->
									<!-- Fix 3147 fine -->
									<td nowrap colspan="5"><% 
  WebMultiSearchForm DocumentoAcquistoRigaPrmRMagazzino =  
     new com.thera.thermfw.web.WebMultiSearchForm("DocumentoAcquistoRigaPrm", "RMagazzino", false, false, true, 1, null, null); 
  DocumentoAcquistoRigaPrmRMagazzino.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmRMagazzino.setOnKeyChange("gestioneOrdineCliente()"); 
  DocumentoAcquistoRigaPrmRMagazzino.write(out); 
%>
<!--<span class="multisearchform" id="MagazzinoProxy"></span>--></td>
								</tr>
								<tr id="SezMagazzinoLavEsterna" name="SezMagazzinoLavEsterna" style="display:none">
									<!-- Proxy Magazzino Lavorazione Esterna -->
									<td nowrap colspan="2"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "IdMagazzinoLavEsterna", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="MagazzinoLavEsternaProxy" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
									<!-- Fix 3147 inizio -->
									<!-- <td></td> -->
									<!-- Fix 3147 fine -->
									<td nowrap colspan="5"><% 
  WebMultiSearchForm DocumentoAcquistoRigaPrmRMagazzinoLavEsterna =  
     new com.thera.thermfw.web.WebMultiSearchForm("DocumentoAcquistoRigaPrm", "RMagazzinoLavEsterna", false, false, true, 1, null, null); 
  DocumentoAcquistoRigaPrmRMagazzinoLavEsterna.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmRMagazzinoLavEsterna.write(out); 
%>
<!--<span class="multisearchform" id="MagazzinoLavEsternaProxy"></span>--></td>
								</tr>
								<!-- Fix 1918 inizio -->
								<tr id="SezCatalogoEsterno" name="SezCatalogoEsterno" style="display:none">
									<!-- Proxy Catalogo Esterno -->
									<td nowrap colspan="2">
										<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "IdArticoloFornitoreCatal", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="CatalogoEsternoProxy" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%>
									</td>
									<!-- Fix 3147 inizio -->
									<!-- <td></td> -->
									<!-- Fix 3147 fine -->
									<td nowrap colspan="6">
										<% 
  WebMultiSearchForm DocumentoAcquistoRigaPrmRCatalogoEsterno =  
     new com.thera.thermfw.web.WebMultiSearchForm("DocumentoAcquistoRigaPrm", "RCatalogoEsterno", false, false, true, 2, "15", null); 
  DocumentoAcquistoRigaPrmRCatalogoEsterno.setExtraRelatedClassAD("IdCatalogo"); 
  DocumentoAcquistoRigaPrmRCatalogoEsterno.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmRCatalogoEsterno.setOnKeyFocus("memorizzaCodiciCatalogoEsterno()"); 
  DocumentoAcquistoRigaPrmRCatalogoEsterno.setOnKeyBlur("confrontaCodiciCatalogoEsterno()"); 
  DocumentoAcquistoRigaPrmRCatalogoEsterno.setOnSearchBack("recuperaDatiCatalogoEsterno()"); 
  DocumentoAcquistoRigaPrmRCatalogoEsterno.write(out); 
%>
<!--<span class="multisearchform" id="CatalogoEsternoProxy"></span>-->
									</td>
								</tr>
								<!-- Fix 1918 fine -->
								<tr>
									<!-- Fix 3147 inizio -->
									<!-- Proxy Articolo Intestatario Label -->
									<td nowrap id="SezArtIntLabel" style="display:none">
										<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "IdArticoloIntestatario", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="ArticoloIntestatarioProxy" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%>
									</td>

									<!-- Proxy Articolo Label -->
									<td nowrap id="SezArticoloLabel">
										<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "IdArticolo", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="ArticoloProxy" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%>
									</td>
									<!-- Check Visualizza Articolo Intestatario -->
									<td id="SezCheckVisulArtInt" style="display:none; text-align: end; vertical-align: middle;">
										<% 
  WebCheckBox DocumentoAcquistoRigaPrmVisualizzaArtInt =  
     new com.thera.thermfw.web.WebCheckBox("DocumentoAcquistoRigaPrm", "VisualizzaArtInt"); 
  DocumentoAcquistoRigaPrmVisualizzaArtInt.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="checkbox" name="<%=DocumentoAcquistoRigaPrmVisualizzaArtInt.getName()%>" id="<%=DocumentoAcquistoRigaPrmVisualizzaArtInt.getId()%>" value="Y"><%
  DocumentoAcquistoRigaPrmVisualizzaArtInt.write(out); 
%>

									</td>
									<td>
										<input type="checkbox" id="VisualizzaArtInt2" name="VisualizzaArtInt2" onclick="visualizzaArticoloIntestatarioOnClick()">
									</td>
									<!-- Proxy Articolo Intestatario Multisearch -->
									<td nowrap id="SezArtIntProxy" style="display:none" colspan="3">
										<% 
  WebMultiSearchForm DocumentoAcquistoRigaPrmRArticoloIntestatario =  
     new com.thera.thermfw.web.WebMultiSearchForm("DocumentoAcquistoRigaPrm", "RArticoloIntestatario", false, false, true, 1, "15", null); 
  DocumentoAcquistoRigaPrmRArticoloIntestatario.setExtraRelatedClassAD("IdArticolo,IdConfigurazione,IdArticoloFornitore"); 
  DocumentoAcquistoRigaPrmRArticoloIntestatario.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmRArticoloIntestatario.setOnKeyFocus("memorizzaCodiceArticoloIntestatario()"); 
  DocumentoAcquistoRigaPrmRArticoloIntestatario.setOnKeyBlur("confrontaCodiceArticoloIntestatario()"); 
  DocumentoAcquistoRigaPrmRArticoloIntestatario.setOnKeyChange("ripulisciNumeratoreArticoloIntestatario()"); 
  DocumentoAcquistoRigaPrmRArticoloIntestatario.setOnSearchBack("recuperaDatiArticoloIntestatario()"); 
  DocumentoAcquistoRigaPrmRArticoloIntestatario.setAdditionalRestrictConditions("LavorazioneEsterna,LavEsterna"); 
  DocumentoAcquistoRigaPrmRArticoloIntestatario.write(out); 
%>
<!--<span class="multisearchform" id="ArticoloIntestatarioProxy"></span>-->
									</td>

									<!-- Proxy Articolo Multisearch -->
									<td nowrap id="SezArticoloProxy" colspan="3">
										<% 
  WebMultiSearchForm DocumentoAcquistoRigaPrmRArticolo =  
     new it.thera.thip.base.articolo.web.ArticoloMultiSearchForm("DocumentoAcquistoRigaPrm", "RArticolo", false, false, true, 1, "15", null); 
  DocumentoAcquistoRigaPrmRArticolo.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmRArticolo.setOnKeyFocus("leggoIlContenuto()"); 
  DocumentoAcquistoRigaPrmRArticolo.setOnKeyBlur("chiaveCambiata()"); 
  DocumentoAcquistoRigaPrmRArticolo.setOnSearchClick("ricercaArticolo()"); 
  DocumentoAcquistoRigaPrmRArticolo.setOnSearchBack("recuperaUMArticolo()"); 
  DocumentoAcquistoRigaPrmRArticolo.setFixedRestrictConditions("DatiComuniEstesi.Stato,V"); 
  ((it.thera.thip.base.articolo.web.ArticoloMultiSearchForm)DocumentoAcquistoRigaPrmRArticolo).setArticoliAlternButton(true); 
  DocumentoAcquistoRigaPrmRArticolo.write(out); 
%>
<!--<span class="articolomultisearchform" id="ArticoloProxy"></span>-->
									</td>
									<!-- Fix 3147 fine -->
									<!-- Campo Descrizione Articolo -->
									<td nowrap colspan="2" id="SezArticoloDescr">
										<% 
  WebTextInput DocumentoAcquistoRigaPrmDescrizioneArticolo =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "DescrizioneArticolo"); 
  DocumentoAcquistoRigaPrmDescrizioneArticolo.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="40" style="width: 290px" id="<%=DocumentoAcquistoRigaPrmDescrizioneArticolo.getId()%>" name="<%=DocumentoAcquistoRigaPrmDescrizioneArticolo.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmDescrizioneArticolo.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmDescrizioneArticolo.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmDescrizioneArticolo.write(out); 
%>

									</td>
								</tr>
								<tr style="height: 22px;">
									<!-- Proxy Versione -->
									<td nowrap colspan="2"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "IdVersioneRichiesta", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="VersioneRichiestaProxy" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
									<!-- Fix 3147 inizio -->
									<!-- <td></td> -->
									<!-- Fix 3147 fine -->
									<td nowrap colspan="3"><% 
  WebMultiSearchForm DocumentoAcquistoRigaPrmRVersioneRichiesta =  
     new com.thera.thermfw.web.WebMultiSearchForm("DocumentoAcquistoRigaPrm", "RVersioneRichiesta", false, false, true, 1, null, null); 
  DocumentoAcquistoRigaPrmRVersioneRichiesta.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmRVersioneRichiesta.setOnKeyFocus("memorizzaCodiceVersione()"); 
  DocumentoAcquistoRigaPrmRVersioneRichiesta.setOnKeyBlur("confrontaCodiceVersione()"); 
  DocumentoAcquistoRigaPrmRVersioneRichiesta.setOnSearchBack("recuperaCostoUnitario()"); 
  DocumentoAcquistoRigaPrmRVersioneRichiesta.write(out); 
%>
<!--<span class="multisearchform" id="VersioneRichiestaProxy"></span>--></td>

									<!-- Operazione -->
									<td style="display:none">
										<table>
											<tr id="SezOperazione" name="SezOperazione" style="display:none">
												<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "Operazione", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="OperazioneField" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
												<!-- Fix 3147 inizio -->
												<td></td>
												<!-- Fix 3147 fine -->
												<td><% 
  WebTextInput DocumentoAcquistoRigaPrmOperazione =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "Operazione"); 
  DocumentoAcquistoRigaPrmOperazione.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="10" id="<%=DocumentoAcquistoRigaPrmOperazione.getId()%>" name="<%=DocumentoAcquistoRigaPrmOperazione.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmOperazione.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmOperazione.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmOperazione.write(out); 
%>
</td>
											</tr>
										</table>
									</td>
									<!-- Fix14727 Inizio RA -->
									<td rowspan="3" colspan="2" id="SezDescrExtArticolo">
									<% 
  WebTextInput DocumentoAcquistoRigaPrmDescrizioneExtArticolo =  
     new com.thera.thermfw.web.WebTextArea("DocumentoAcquistoRigaPrm", "DescrizioneExtArticolo"); 
  DocumentoAcquistoRigaPrmDescrizioneExtArticolo.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<textarea style="width: 290px" rows="3" size="<%=DocumentoAcquistoRigaPrmDescrizioneExtArticolo.getSize()%>" id="<%=DocumentoAcquistoRigaPrmDescrizioneExtArticolo.getId()%>" name="<%=DocumentoAcquistoRigaPrmDescrizioneExtArticolo.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmDescrizioneExtArticolo.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmDescrizioneExtArticolo.getClassType()%>"></textarea><% 
  DocumentoAcquistoRigaPrmDescrizioneExtArticolo.write(out); 
%>

									</td>
									<!-- Fix14727 Fine RA -->
								</tr>
								<tr style="height: 22px;">
									<!-- Proxy Configurazione -->
									<td nowrap colspan="2"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "IdEsternoConfig", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="ConfigurazioneProxy" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
									<!-- Fix 3147 inizio -->
									<!-- <td></td> -->
									<!-- Fix 3147 fine -->
									<td colspan="3"><div id="confdiv">
										<% 
  WebMultiSearchForm DocumentoAcquistoRigaPrmRConfigurazione =  
     new it.thera.thip.datiTecnici.configuratore.web.ConfigurazioneMultiSearchForm("DocumentoAcquistoRigaPrm", "RConfigurazione", false, false, true, 1, null, null); 
  DocumentoAcquistoRigaPrmRConfigurazione.setExtraRelatedClassAD("IdAzienda,IdConfigurazione"); 
  DocumentoAcquistoRigaPrmRConfigurazione.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmRConfigurazione.setOnKeyFocus("memorizzaCodiceConfigurazione()"); 
  DocumentoAcquistoRigaPrmRConfigurazione.setOnKeyBlur("confrontaCodiceConfigurazione()"); 
  DocumentoAcquistoRigaPrmRConfigurazione.setOnSearchBack("recuperaCostoUnitario()"); 
  DocumentoAcquistoRigaPrmRConfigurazione.setAdditionalRestrictConditions("IdArticolo,IdArticolo"); 
  DocumentoAcquistoRigaPrmRConfigurazione.write(out); 
%>
<!--<span class="configurazionemultisearchform" id="ConfigurazioneProxy"></span>-->
									</div>
									</td>

									<td>&nbsp;</td>
									<!-- Numero Ordine Produzione -->
									<td>
									&nbsp;
									<!-- fix 5757 modificati rif a produzione           <table>            <tr id="SezNumOrdProd" name="SezNumOrdProd" style="display:none">             <td nowrap="true">              <label for="NumeroOrdProduzioneField">               Numero Ordine Produzione              </label>             </td>             <td>              <input type ="text" id="NumeroOrdProduzioneField" name="NumeroOrdProduzioneField" size="10"/>             </td>            </tr>           </table>           -->
									</td>
								</tr>
								<tr><td colspan="5"></td></tr>

                                                                <!-- Fix 13681 Inizio -->
                                                                <tr name="trCliente" id="trCliente" style="display:none">
                                                                  <!-- Proxy Cliente -->
                                                                  <td nowrap colspan="2"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "IdCliente", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="Cliente" id="labelCliente" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
                                                                  <!-- <td>&nbsp;</td> -->
                                                                  <td nowrap colspan="5"><% 
  WebMultiSearchForm DocumentoAcquistoRigaPrmCliente =  
     new com.thera.thermfw.web.WebMultiSearchForm("DocumentoAcquistoRigaPrm", "Cliente", false, false, true, 1, null, null); 
  DocumentoAcquistoRigaPrmCliente.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmCliente.write(out); 
%>
<!--<span class="multisearchform" id="Cliente"></span>--></td>
                                                                </tr>
                                                                <tr name="trOrdineVendita" id="trOrdineVendita" style="display:none">
                                                                  <!-- Proxy OrdineVendita -->
                                                                  <td nowrap colspan="2"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "NumeroOrdineCliente", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="OrdineVendita" id="labelOrdineVendita" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
                                                                  <!-- <td>&nbsp;</td> -->
                                                                  <td nowrap colspan="4"><% 
  WebMultiSearchForm DocumentoAcquistoRigaPrmOrdineVendita =  
     new com.thera.thermfw.web.WebMultiSearchForm("DocumentoAcquistoRigaPrm", "OrdineVendita", true, false, true, 2, null, null); 
  DocumentoAcquistoRigaPrmOrdineVendita.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmOrdineVendita.setAdditionalRestrictConditions("IdCliente,Cliente"); 
  DocumentoAcquistoRigaPrmOrdineVendita.write(out); 
%>
<!--<span class="multisearchform" id="OrdineVendita"></span>-->
                                                                    <!-- Proxy OrdineVenditaRiga -->
                                                                    <%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "RigaOrdineCliente", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="OrdineVenditaRiga" id="labelOrdineVenditaRiga" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%>
                                                                    <% 
  WebMultiSearchForm DocumentoAcquistoRigaPrmOrdineVenditaRiga =  
     new com.thera.thermfw.web.WebMultiSearchForm("DocumentoAcquistoRigaPrm", "OrdineVenditaRiga", false, false, true, 1, null, null); 
  DocumentoAcquistoRigaPrmOrdineVenditaRiga.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmOrdineVenditaRiga.setAdditionalRestrictConditions("AnnoOrdineCliente,AnnoOrdine; NumeroOrdineCliente,NumeroOrdine"); 
  DocumentoAcquistoRigaPrmOrdineVenditaRiga.write(out); 
%>
<!--<span class="multisearchform" id="OrdineVenditaRiga"></span>-->
                                                                  </td>
                                                                </tr>
                                                                <!-- Fix 13681 Fine -->

								<tr id="SezNumOrdProd" name="SezNumOrdProd" style="display:none">
									<!-- Proxy a attivitaEsecutiva -->
									<td nowrap colspan="2">
										<label id="AttEsecProxyLbl" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "RifOrdPrdLbl", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label>
									</td>
									<!-- <td></td> -->
									<td nowrap colspan="4">
											<% 
  WebMultiSearchForm DocumentoAcquistoRigaPrmAttivitaEsecutivaRel =  
     new com.thera.thermfw.web.WebMultiSearchForm("DocumentoAcquistoRigaPrm", "AttivitaEsecutivaRel", false, false, true, 3, null, null); 
  DocumentoAcquistoRigaPrmAttivitaEsecutivaRel.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmAttivitaEsecutivaRel.write(out); 
%>
<!--<span class="multisearchform" id="AttEsecProxy"></span>-->
									</td>
									<td style="display: none;">
										<% 
  WebTextInput DocumentoAcquistoRigaPrmDettRigaOrdProduzione =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "DettRigaOrdProduzione"); 
  DocumentoAcquistoRigaPrmDettRigaOrdProduzione.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="1" id="<%=DocumentoAcquistoRigaPrmDettRigaOrdProduzione.getId()%>" name="<%=DocumentoAcquistoRigaPrmDettRigaOrdProduzione.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmDettRigaOrdProduzione.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmDettRigaOrdProduzione.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmDettRigaOrdProduzione.write(out); 
%>

									</td>
								</tr>
								<!-- Rif doc prd CL su Fase -->
								<tr id="SezNumDocProd" name="SezNumDocProd" style="display:none">
									<td style="display: none;">
										<% 
  WebTextInput DocumentoAcquistoRigaPrmAnnoDocProduzione =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "AnnoDocProduzione"); 
  DocumentoAcquistoRigaPrmAnnoDocProduzione.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="1" id="<%=DocumentoAcquistoRigaPrmAnnoDocProduzione.getId()%>" name="<%=DocumentoAcquistoRigaPrmAnnoDocProduzione.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmAnnoDocProduzione.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmAnnoDocProduzione.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmAnnoDocProduzione.write(out); 
%>

									</td>
									<td style="display: none;">
										<% 
  WebTextInput DocumentoAcquistoRigaPrmNumeroDocProduzione =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "NumeroDocProduzione"); 
  DocumentoAcquistoRigaPrmNumeroDocProduzione.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="1" id="<%=DocumentoAcquistoRigaPrmNumeroDocProduzione.getId()%>" name="<%=DocumentoAcquistoRigaPrmNumeroDocProduzione.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmNumeroDocProduzione.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmNumeroDocProduzione.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmNumeroDocProduzione.write(out); 
%>

									</td>
									<td style="display: none;">
										<% 
  WebTextInput DocumentoAcquistoRigaPrmRigaDocPrdProdotto =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "RigaDocPrdProdotto"); 
  DocumentoAcquistoRigaPrmRigaDocPrdProdotto.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="1" id="<%=DocumentoAcquistoRigaPrmRigaDocPrdProdotto.getId()%>" name="<%=DocumentoAcquistoRigaPrmRigaDocPrdProdotto.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmRigaDocPrdProdotto.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmRigaDocPrdProdotto.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmRigaDocPrdProdotto.write(out); 
%>

									</td>
									<td style="display: none;">
										<% 
  WebTextInput DocumentoAcquistoRigaPrmRigaDocPrdRisorsa =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "RigaDocPrdRisorsa"); 
  DocumentoAcquistoRigaPrmRigaDocPrdRisorsa.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="1" id="<%=DocumentoAcquistoRigaPrmRigaDocPrdRisorsa.getId()%>" name="<%=DocumentoAcquistoRigaPrmRigaDocPrdRisorsa.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmRigaDocPrdRisorsa.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmRigaDocPrdRisorsa.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmRigaDocPrdRisorsa.write(out); 
%>

									</td>
								</tr>

								<tr>
									<!-- Fix 3147 inizio -->
									<td></td>
									<td></td>
									<!-- Fix 3147 fine -->
									<!-- Check Saldo manuale -->
									<td colspan="2">
										<% 
  WebCheckBox DocumentoAcquistoRigaPrmRigaSaldata =  
     new com.thera.thermfw.web.WebCheckBox("DocumentoAcquistoRigaPrm", "RigaSaldata"); 
  DocumentoAcquistoRigaPrmRigaSaldata.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="checkbox" name="<%=DocumentoAcquistoRigaPrmRigaSaldata.getName()%>" id="<%=DocumentoAcquistoRigaPrmRigaSaldata.getId()%>" value="Y"><%
  DocumentoAcquistoRigaPrmRigaSaldata.write(out); 
%>

									</td>
								</tr>
								<tr>
									<td colspan="6">
										<table cellspacing="1" cellpadding="1">
											<td style="width: 450px" align="left" valign="top">
												<fieldset>
													<legend><label id="Quantita" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "Quantita", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label><label id="TipoQuantitaLbl"> Tipo</label></legend>
													<table cellspacing="1" cellpadding="1">
														<tr>
															<!-- Campo Qta Vendita -->
															<td nowrap style="width: 102px"> 	<label id="QtaInUMAcqLbl" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "UMAcquisto", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></td>
															<td style="width: 80px;"><% 
  WebTextInput DocumentoAcquistoRigaPrmQtaInUMAcq =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "QtaInUMAcq"); 
  DocumentoAcquistoRigaPrmQtaInUMAcq.setOnChange("variazioneQuantAcquistoRiga(false)"); 
  DocumentoAcquistoRigaPrmQtaInUMAcq.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="<%=DocumentoAcquistoRigaPrmQtaInUMAcq.getSize()%>" id="<%=DocumentoAcquistoRigaPrmQtaInUMAcq.getId()%>" name="<%=DocumentoAcquistoRigaPrmQtaInUMAcq.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmQtaInUMAcq.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmQtaInUMAcq.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmQtaInUMAcq.write(out); 
%>
</td>
															<td colspan="2">
																<% 
  WebSearchComboBox DocumentoAcquistoRigaPrmRUMRif =  
     new com.thera.thermfw.web.WebSearchComboBox("DocumentoAcquistoRigaPrm", "RUMRif", null, 2, "20", false, "getListaUMRiferimento"); 
  DocumentoAcquistoRigaPrmRUMRif.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmRUMRif.setOnChange("variazioneUnitaMisura()"); 
  DocumentoAcquistoRigaPrmRUMRif.write(out); 
%>
<!--<span class="searchcombobox" id="UMAcqCombo" name="UMAcqCombo"></span>-->
															</td>
															<!--                <td nowrap="true" colspan="2">                 <select name="UMAcqCombo" id="UMAcqCombo" onChange="impostaUMAcquisto()">                  <option></option>                 </select>                </td>                -->
															<td>&nbsp;</td>
														</tr>
														<tr>
															<!-- Campo Qta Primaria -->
															<td nowrap><label id="QtaInUMPrmLbl" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "UMPrm", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></td>
															<td><% 
  WebTextInput DocumentoAcquistoRigaPrmQtaInUMPrmMag =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "QtaInUMPrmMag"); 
  DocumentoAcquistoRigaPrmQtaInUMPrmMag.setOnChange("variazioneQuantPrimariaMagazzinoRiga(false)"); 
  DocumentoAcquistoRigaPrmQtaInUMPrmMag.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="<%=DocumentoAcquistoRigaPrmQtaInUMPrmMag.getSize()%>" id="<%=DocumentoAcquistoRigaPrmQtaInUMPrmMag.getId()%>" name="<%=DocumentoAcquistoRigaPrmQtaInUMPrmMag.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmQtaInUMPrmMag.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmQtaInUMPrmMag.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmQtaInUMPrmMag.write(out); 
%>
</td>
															<!-- Campo Servizio UM Primaria -->
															<td nowrap colspan="2"><% 
  WebTextInput DocumentoAcquistoRigaPrmServizioUMPrmMag =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "ServizioUMPrmMag"); 
  DocumentoAcquistoRigaPrmServizioUMPrmMag.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="20" id="<%=DocumentoAcquistoRigaPrmServizioUMPrmMag.getId()%>" name="<%=DocumentoAcquistoRigaPrmServizioUMPrmMag.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmServizioUMPrmMag.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmServizioUMPrmMag.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmServizioUMPrmMag.write(out); 
%>
</td>
															<td>&nbsp;</td>
														</tr>
														<tr id="SezUMSecondaria" name="SezUMSecondaria" style="display:none">
															<!-- Campo Qta Secondaria -->
															<td nowrap><label id="QtaInUMSecLbl" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "UMSec", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></td>
															<td><% 
  WebTextInput DocumentoAcquistoRigaPrmQtaInUMSecMag =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "QtaInUMSecMag"); 
  DocumentoAcquistoRigaPrmQtaInUMSecMag.setOnChange("variazioneQuantSecondariaMagazzinoRiga(false)"); 
  DocumentoAcquistoRigaPrmQtaInUMSecMag.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="<%=DocumentoAcquistoRigaPrmQtaInUMSecMag.getSize()%>" id="<%=DocumentoAcquistoRigaPrmQtaInUMSecMag.getId()%>" name="<%=DocumentoAcquistoRigaPrmQtaInUMSecMag.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmQtaInUMSecMag.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmQtaInUMSecMag.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmQtaInUMSecMag.write(out); 
%>
</td>
															<!-- Campo Servizio UM Secondaria -->
															<td nowrap colspan="2"><% 
  WebTextInput DocumentoAcquistoRigaPrmServizioUMSecMag =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "ServizioUMSecMag"); 
  DocumentoAcquistoRigaPrmServizioUMSecMag.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="20" id="<%=DocumentoAcquistoRigaPrmServizioUMSecMag.getId()%>" name="<%=DocumentoAcquistoRigaPrmServizioUMSecMag.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmServizioUMSecMag.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmServizioUMSecMag.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmServizioUMSecMag.write(out); 
%>
</td>
															<td>&nbsp;</td>
														</tr>

														<!-- fix 12572 > -->
														<tr id="SezNumeroImballo" name="SezNumeroImballo" style="display:none">
															<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "NumeroImballo", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="NumeroImballoField" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
															<td><% 
  WebTextInput DocumentoAcquistoRigaPrmNumeroImballo =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "NumeroImballo"); 
  DocumentoAcquistoRigaPrmNumeroImballo.setOnChange("numeroImballoManuale()"); 
  DocumentoAcquistoRigaPrmNumeroImballo.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="<%=DocumentoAcquistoRigaPrmNumeroImballo.getSize()%>" id="<%=DocumentoAcquistoRigaPrmNumeroImballo.getId()%>" name="<%=DocumentoAcquistoRigaPrmNumeroImballo.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmNumeroImballo.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmNumeroImballo.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmNumeroImballo.write(out); 
%>
</td>
															<td><input type="hidden" id="UMRifFattoreConvNI" name="UMRifFattoreConvNI"></td>
														</tr>
														<!-- fix 12572 < -->

														<!-- Fix 3246 inizio -->
														<!-- Check Ricalcolo Quantita Fattore Conversione -->
														<tr>
															<td>&nbsp;</td>
															<td colspan="2">
																<% 
  WebCheckBox DocumentoAcquistoRigaPrmRicalcoloQtaFattoreConv =  
     new com.thera.thermfw.web.WebCheckBox("DocumentoAcquistoRigaPrm", "RicalcoloQtaFattoreConv"); 
  DocumentoAcquistoRigaPrmRicalcoloQtaFattoreConv.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="checkbox" name="<%=DocumentoAcquistoRigaPrmRicalcoloQtaFattoreConv.getName()%>" id="<%=DocumentoAcquistoRigaPrmRicalcoloQtaFattoreConv.getId()%>" value="Y"><%
  DocumentoAcquistoRigaPrmRicalcoloQtaFattoreConv.write(out); 
%>

															</td>
															<td nowrap hidden="true" id="SezBottoneRicalcolaSec">
																<button style="width: 130px" name="RicalcolaCoefficientePulsante" id="RicalcolaCoefficientePulsante" onclick="ricalcolaCoefficiente()" type="button"><!--12094-->
																	<label id="RicalcolaCoefficienteLabel">Ricalcola secondarie</label>
																</button>
															</td>
														</tr>
														<!-- Fix 3246 fine -->
													</table>
												</fieldset>
											</td>
											<td style="width: 10px;"></td>
											<td style="width: 400px" valign="top">
												<fieldset>
													<legend><label id="Date" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "Date", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></legend>
													<table cellspacing="1" cellpadding="1">
														<tr>
															<!-- Campo Data Consegna Richiesta -->
															<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "DataConsegnaRichiesta", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="DataConsegnaRichiestaField" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
															<td style="width: 15px;"></td>
															<td><% 
  WebTextInput DocumentoAcquistoRigaPrmDataConsegnaRichiesta =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "DataConsegnaRichiesta"); 
  DocumentoAcquistoRigaPrmDataConsegnaRichiesta.setOnChange("gestDataConsegnaRichiesta()"); 
  DocumentoAcquistoRigaPrmDataConsegnaRichiesta.setShowCalendarBtn(true); 
  DocumentoAcquistoRigaPrmDataConsegnaRichiesta.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="12" id="<%=DocumentoAcquistoRigaPrmDataConsegnaRichiesta.getId()%>" name="<%=DocumentoAcquistoRigaPrmDataConsegnaRichiesta.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmDataConsegnaRichiesta.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmDataConsegnaRichiesta.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmDataConsegnaRichiesta.write(out); 
%>
</td>
															<td style="width: 30px;"></td>
															<!-- Campo Settimana Consegna Richiesta -->
															<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "SettConsegnaRichiesta", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="SettConsegnaRichiestaField" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
															<td style="width: 15px;"></td>
															<td><% 
  WebTextInput DocumentoAcquistoRigaPrmSettConsegnaRichiesta =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "SettConsegnaRichiesta"); 
  DocumentoAcquistoRigaPrmSettConsegnaRichiesta.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="5" id="<%=DocumentoAcquistoRigaPrmSettConsegnaRichiesta.getId()%>" name="<%=DocumentoAcquistoRigaPrmSettConsegnaRichiesta.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmSettConsegnaRichiesta.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmSettConsegnaRichiesta.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmSettConsegnaRichiesta.write(out); 
%>
</td>
														</tr>
														<tr>
															<!-- Campo Data Consegna Confermata -->
															<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "DataConsegnaConfermata", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="DataConsegnaConfermataField" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
															<td style="width: 15px;"></td>
															<td><% 
  WebTextInput DocumentoAcquistoRigaPrmDataConsegnaConfermata =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "DataConsegnaConfermata"); 
  DocumentoAcquistoRigaPrmDataConsegnaConfermata.setOnChange("gestDataConsegnaConfermata()"); 
  DocumentoAcquistoRigaPrmDataConsegnaConfermata.setShowCalendarBtn(true); 
  DocumentoAcquistoRigaPrmDataConsegnaConfermata.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="12" id="<%=DocumentoAcquistoRigaPrmDataConsegnaConfermata.getId()%>" name="<%=DocumentoAcquistoRigaPrmDataConsegnaConfermata.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmDataConsegnaConfermata.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmDataConsegnaConfermata.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmDataConsegnaConfermata.write(out); 
%>
</td>
															<td style="width: 30px;"></td>															<!-- Campo Settimana Consegna Confermata -->
															<td><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "SettConsegnaConfermata", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="SettConsegnaConfermataField" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
															<td style="width: 15px;"></td>
															<td><% 
  WebTextInput DocumentoAcquistoRigaPrmSettConsegnaConfermata =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "SettConsegnaConfermata"); 
  DocumentoAcquistoRigaPrmSettConsegnaConfermata.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="5" id="<%=DocumentoAcquistoRigaPrmSettConsegnaConfermata.getId()%>" name="<%=DocumentoAcquistoRigaPrmSettConsegnaConfermata.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmSettConsegnaConfermata.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmSettConsegnaConfermata.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmSettConsegnaConfermata.write(out); 
%>
</td>
														</tr>
													</table>
												</fieldset>
											</td>
										</table>
									</td>
								</tr>
								<!-- Fix 3147 inizio - modificato layout -->
								<tr>
									<td colspan="7">
										<table>
											<tr>
												<!-- Dati Comuni Estesi - Stato -->
												<td nowrap>
													<table>
														<tr>
															<td style="width: 101px"><!-- Fix 39398 - fine -->
																<% 
   request.setAttribute("parentForm", DocumentoAcquistoRigaPrmForm); 
   String CDForDatiComuniEstesi$it$thera$thip$cs$DatiComuniEstesi$jsp = "DatiComuniEstesi"; 
%>
<jsp:include page="/it/thera/thip/cs/DatiComuniEstesi.jsp" flush="true"> 
<jsp:param name="CDName" value="<%=CDForDatiComuniEstesi$it$thera$thip$cs$DatiComuniEstesi$jsp%>"/> 
</jsp:include> 
<!--<span class="subform" name="T3" id="T3"></span>-->
															</td>
														</tr>
													</table>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<!-- Fix 3147 fine -->

							</table>
						<% MainTabbed.endTab(); %> 
</div>
						<!-- **************************************************************************************** -->
						<!-- Cartella Prezzi/Sconti -->
						<div class="tabbed_page" id="<%=MainTabbed.getTabPageId("PrezziScontiTab")%>" style="width:100%;height:100%;overflow:auto;"><% MainTabbed.startTab("PrezziScontiTab"); %>
							<!-- Fix 39398 - inizio modifiche grafiche -->
							<!-- <table cellspacing="1" cellpadding="1"> -->
							<table cellspacing="2" cellpadding="2" style="padding: 3px">
								<tr>
									<!-- Proxy Listino -->
									<td style="width: 150px;">
										<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "IdListinoPrezzi", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="ListinoProxy" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%>
									</td>
									<td nowrap colspan="3">
										<% 
  WebMultiSearchForm DocumentoAcquistoRigaPrmRListino =  
     new com.thera.thermfw.web.WebMultiSearchForm("DocumentoAcquistoRigaPrm", "RListino", false, false, true, 1, null, null); 
  DocumentoAcquistoRigaPrmRListino.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmRListino.setOnKeyChange("variazioneListino()"); 
  DocumentoAcquistoRigaPrmRListino.setAdditionalRestrictConditions("ServizioLavEstListino,LavorazioneEsterna"); 
  DocumentoAcquistoRigaPrmRListino.write(out); 
%>
<!--<span class="multisearchform" id="ListinoProxy"></span>-->
									</td>
									<!-- Pulsante Ricalcola dati vendita -->
									<!-- Fix 2333 aggiunto l'ID al bottone-->
									<td nowrap colspan="6" id="SezBottoneRicalcola">
									<!-- Fine Fix 2333 -->
										<button style="width: 130px;" name="RicalcolaDatiAcquistoPulsante" id="RicalcolaDatiAcquistoPulsante" type="button" onclick="ricalcolaDatiAcquisto()"><%= ResourceLoader.getString("it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "RicalcolaDatiAcquisto")%></button>
									</td>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<!-- Campo Prezzo -->
									<td>
										<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "Prezzo", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="PrezzoField" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%>
									</td>
									<td style="width: 250px;">
										<% 
  WebTextInput DocumentoAcquistoRigaPrmPrezzo =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "Prezzo"); 
  DocumentoAcquistoRigaPrmPrezzo.setOnChange("settaggioManualeProvenienzaPrezzo()"); 
  DocumentoAcquistoRigaPrmPrezzo.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="12" id="<%=DocumentoAcquistoRigaPrmPrezzo.getId()%>" name="<%=DocumentoAcquistoRigaPrmPrezzo.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmPrezzo.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmPrezzo.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmPrezzo.write(out); 
%>

										<label id="LabelValuta"></label>
									</td>
									<!-- Fix 05398  SL ini... -->
									<% { 
   WebFormCustomization fc = new com.thera.thermfw.web.WebFormCustomization("FORM", "FormCustomizeLivelloInterno1"); 
  fc.setParent(DocumentoAcquistoRigaPrmForm); 
  fc.customize(); } 
%>
<!--<span class="formCustomization" id="campiFormLivelloInterno1"></span>-->
									<!-- Fix 05398  SL fine... -->
									<!--          <td>&nbsp;</td>          <td>&nbsp;</td>          -->
									<!-- Campo Prezzo Extra -->
									<td style="width: 110px;">
										<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "PrezzoExtra", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="PrezzoExtraField" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%>
									</td>
									<td style="width: 170px;">
										<% 
  WebTextInput DocumentoAcquistoRigaPrmPrezzoExtra =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "PrezzoExtra"); 
  DocumentoAcquistoRigaPrmPrezzoExtra.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="12" id="<%=DocumentoAcquistoRigaPrmPrezzoExtra.getId()%>" name="<%=DocumentoAcquistoRigaPrmPrezzoExtra.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmPrezzoExtra.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmPrezzoExtra.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmPrezzoExtra.write(out); 
%>

									</td>
									<!-- Fix 2333 -->
									<td nowrap id="SezDettaglioPrezzi" style="display:none" colspan="2">
										<button style="width: 90px" name="DettaglioPrezzi" id="DettaglioPrezzi" type="button" onclick="apriDettaglioPrezzi('RecuperaPrezziExtraDocAcq','true', 'Y')">
											<label id="LabelDettaglioPrezzi" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.base.prezziExtra.resources.DocOrdRiga", "DettaglioPrezzi", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label>
										</button>
									</td>
									<!-- Fine Fix 2333 -->

									<!-- Fix 1495 -->
									<td id="SezDataInizioValiditaLstLbl" style="display:none; width: 90px;">
										<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "DataInizioValiditaLst", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="DataInizioValiditaLstField" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%>
									</td>
									<td id="SezDataInizioValiditaLst" style="display:none">
										<% 
  WebTextInput DocumentoAcquistoRigaPrmDataInizioValiditaLst =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "DataInizioValiditaLst"); 
  DocumentoAcquistoRigaPrmDataInizioValiditaLst.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="12" id="<%=DocumentoAcquistoRigaPrmDataInizioValiditaLst.getId()%>" name="<%=DocumentoAcquistoRigaPrmDataInizioValiditaLst.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmDataInizioValiditaLst.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmDataInizioValiditaLst.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmDataInizioValiditaLst.write(out); 
%>

									</td>
									<!-- Fine fix 1495 -->


								</tr>
								<tr>
									<!-- Campo Sconto 1 Articolo -->
									<td>
										<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "ScontoArticolo1", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="ScontoArticolo1Field" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%>
									</td>
									<td>
										<% 
  WebTextInput DocumentoAcquistoRigaPrmScontoArticolo1 =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "ScontoArticolo1"); 
  DocumentoAcquistoRigaPrmScontoArticolo1.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="7" id="<%=DocumentoAcquistoRigaPrmScontoArticolo1.getId()%>" name="<%=DocumentoAcquistoRigaPrmScontoArticolo1.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmScontoArticolo1.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmScontoArticolo1.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmScontoArticolo1.write(out); 
%>

									</td>
									<!--<td>&nbsp;</td>-->
									<!-- Campo Sconto 2 Articolo -->
									<td>
										<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "ScontoArticolo2", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="ScontoArticolo2Field" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%>
									</td>
									<td>
										<% 
  WebTextInput DocumentoAcquistoRigaPrmScontoArticolo2 =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "ScontoArticolo2"); 
  DocumentoAcquistoRigaPrmScontoArticolo2.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="7" id="<%=DocumentoAcquistoRigaPrmScontoArticolo2.getId()%>" name="<%=DocumentoAcquistoRigaPrmScontoArticolo2.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmScontoArticolo2.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmScontoArticolo2.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmScontoArticolo2.write(out); 
%>

									</td>
									<!--<td>&nbsp;</td>-->
									<!-- Campo Maggiorazione -->
									<td style="width: 90px">
										<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "Maggiorazione", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="MaggiorazioneField" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%>
									</td>
									<td>
										<% 
  WebTextInput DocumentoAcquistoRigaPrmMaggiorazione =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "Maggiorazione"); 
  DocumentoAcquistoRigaPrmMaggiorazione.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="7" id="<%=DocumentoAcquistoRigaPrmMaggiorazione.getId()%>" name="<%=DocumentoAcquistoRigaPrmMaggiorazione.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmMaggiorazione.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmMaggiorazione.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmMaggiorazione.write(out); 
%>

									</td>
								</tr>
								<tr>
									<!-- Proxy Sconto -->
									<td nowrap>
										<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "IdSconto", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="ScontoProxy" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%>
									</td>
									<td nowrap colspan="3">
										<% 
  WebMultiSearchForm DocumentoAcquistoRigaPrmRSconto =  
     new com.thera.thermfw.web.WebMultiSearchForm("DocumentoAcquistoRigaPrm", "RSconto", false, false, true, 1, null, null); 
  DocumentoAcquistoRigaPrmRSconto.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmRSconto.write(out); 
%>
<!--<span class="multisearchform" id="ScontoProxy"></span>-->
									</td>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<!-- Campo Sconto Cliente -->
									<td>
										<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "PrcScontoIntestatario", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="ScontoClienteField" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%>
									</td>
									<td>
										<% 
  WebTextInput DocumentoAcquistoRigaPrmPrcScontoIntestatario =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "PrcScontoIntestatario"); 
  DocumentoAcquistoRigaPrmPrcScontoIntestatario.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="7" id="<%=DocumentoAcquistoRigaPrmPrcScontoIntestatario.getId()%>" name="<%=DocumentoAcquistoRigaPrmPrcScontoIntestatario.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmPrcScontoIntestatario.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmPrcScontoIntestatario.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmPrcScontoIntestatario.write(out); 
%>

									</td>
									<!--<td>&nbsp;</td>-->
									<!-- Campo Sconto Modal. -->
									<td>
										<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "PrcScontoModalita", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="ScontoModalitaField" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%>
									</td>
									<td>
										<% 
  WebTextInput DocumentoAcquistoRigaPrmPrcScontoModalita =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "PrcScontoModalita"); 
  DocumentoAcquistoRigaPrmPrcScontoModalita.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="7" id="<%=DocumentoAcquistoRigaPrmPrcScontoModalita.getId()%>" name="<%=DocumentoAcquistoRigaPrmPrcScontoModalita.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmPrcScontoModalita.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmPrcScontoModalita.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmPrcScontoModalita.write(out); 
%>

									</td>
								</tr>
								<tr>
									<!-- Proxy Sconto Modal -->
									<td>
										<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "IdScontoModalita", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="ScontoModalitaProxy" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%>
									</td>
									<td nowrap colspan="3">
										<% 
  WebMultiSearchForm DocumentoAcquistoRigaPrmRScontoModalita =  
     new com.thera.thermfw.web.WebMultiSearchForm("DocumentoAcquistoRigaPrm", "RScontoModalita", false, false, true, 1, null, null); 
  DocumentoAcquistoRigaPrmRScontoModalita.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmRScontoModalita.write(out); 
%>
<!--<span class="multisearchform" id="ScontoModalitaProxy"></span>-->
									</td>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
								</tr>
								<tr rowspan="2">
									<!-- Proxy Assoggettamento IVA -->
									<td>
										<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "IdAssoggettamentoIVA", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="AssoggettamentoIVAProxy" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%>
									</td>
									<td nowrap colspan="3">
										<% 
  WebMultiSearchForm DocumentoAcquistoRigaPrmRAssoggettamentoIVA =  
     new com.thera.thermfw.web.WebMultiSearchForm("DocumentoAcquistoRigaPrm", "RAssoggettamentoIVA", false, false, true, 1, null, null); 
  DocumentoAcquistoRigaPrmRAssoggettamentoIVA.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmRAssoggettamentoIVA.write(out); 
%>
<!--<span class="multisearchform" id="AssoggettamentoIVAProxy"></span>-->
									</td>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
								</tr>
								<tr style="display: none;"><!-- Fix 39398 - fine -->
									<td>&nbsp;</td>
								</tr>
								<tr>
									<!-- Combo Riferimento Prezzo -->
									<td>
 										<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "RiferimentoUMPrezzo", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="RiferimentoPrezzoCombo" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%>
									</td>
									<td>
										<% 
  WebComboBox DocumentoAcquistoRigaPrmRiferimentoUMPrezzo =  
     new com.thera.thermfw.web.WebComboBox("DocumentoAcquistoRigaPrm", "RiferimentoUMPrezzo", null); 
  DocumentoAcquistoRigaPrmRiferimentoUMPrezzo.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<select name="<%=DocumentoAcquistoRigaPrmRiferimentoUMPrezzo.getName()%>" id="<%=DocumentoAcquistoRigaPrmRiferimentoUMPrezzo.getId()%>"><% 
  DocumentoAcquistoRigaPrmRiferimentoUMPrezzo.write(out); 
%> 

											
										</select>
									</td>
								</tr>
								<tr>
									<!-- Combo Tipo Prezzo -->
									<td>
 										<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "TipoPrezzo", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="TipoPrezzoCombo" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%>
									</td>
									<td colspan="2">
										<% 
  WebComboBox DocumentoAcquistoRigaPrmTipoPrezzo =  
     new com.thera.thermfw.web.WebComboBox("DocumentoAcquistoRigaPrm", "TipoPrezzo", null); 
  DocumentoAcquistoRigaPrmTipoPrezzo.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<select name="<%=DocumentoAcquistoRigaPrmTipoPrezzo.getName()%>" id="<%=DocumentoAcquistoRigaPrmTipoPrezzo.getId()%>"><% 
  DocumentoAcquistoRigaPrmTipoPrezzo.write(out); 
%> 

											
										</select>
									</td>
									<td>&nbsp;</td>
								</tr>
								<tr>
									<!-- Combo Provenienza Prezzo -->
									<td>
 										<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "ProvenienzaPrezzo", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="ProvenienzaPrezzoCombo" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%>
									</td>
									<td>
										<% 
  WebComboBox DocumentoAcquistoRigaPrmProvenienzaPrezzo =  
     new com.thera.thermfw.web.WebComboBox("DocumentoAcquistoRigaPrm", "ProvenienzaPrezzo", null); 
  DocumentoAcquistoRigaPrmProvenienzaPrezzo.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<select name="<%=DocumentoAcquistoRigaPrmProvenienzaPrezzo.getName()%>" id="<%=DocumentoAcquistoRigaPrmProvenienzaPrezzo.getId()%>"><% 
  DocumentoAcquistoRigaPrmProvenienzaPrezzo.write(out); 
%> 

											
										</select>
									</td>
								</tr>
							</table>

						<% MainTabbed.endTab(); %> 
</div>

						<!-- **************************************************************************************** -->
						<!-- **************************************************************************************** -->
						<!-- Cartella Cond. evasione -->
						<div class="tabbed_page" id="<%=MainTabbed.getTabPageId("CondEvasioneTab")%>" style="width:100%;height:100%;overflow:auto;"><% MainTabbed.startTab("CondEvasioneTab"); %>
							<!-- Fix 39398 - inizio modifiche grafiche -->
							<!-- <table width="100%" border="0" cellspacing="2" cellpadding="2" align="center"> -->
							<table cellspacing="2" cellpadding="2" style="padding: 3px; width: 100%;">
							    <tr>

								<td colspan="2">
									<fieldset>
									<legend><label id="OrdineRiga" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "OrdineRiga", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></legend>
									<table width="100%" align="center">
									<tr>
										<td nowrap style="width: 148px"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "NumeroOrdineFormattato", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="NumeroOrdineFormattatoField" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
							  			<td style="width: 160px"><% 
  WebTextInput DocumentoAcquistoRigaPrmNumeroOrdineFormattato =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "NumeroOrdineFormattato"); 
  DocumentoAcquistoRigaPrmNumeroOrdineFormattato.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="15" id="<%=DocumentoAcquistoRigaPrmNumeroOrdineFormattato.getId()%>" name="<%=DocumentoAcquistoRigaPrmNumeroOrdineFormattato.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmNumeroOrdineFormattato.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmNumeroOrdineFormattato.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmNumeroOrdineFormattato.write(out); 
%>
</td>
										<td nowrap style="width: 80px;"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "DataOrdine", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="DataOrdineField" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
							  			<td style="width: 110px"><% 
  WebTextInput DocumentoAcquistoRigaPrmDataOrdine =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "DataOrdine"); 
  DocumentoAcquistoRigaPrmDataOrdine.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="10" id="<%=DocumentoAcquistoRigaPrmDataOrdine.getId()%>" name="<%=DocumentoAcquistoRigaPrmDataOrdine.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmDataOrdine.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmDataOrdine.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmDataOrdine.write(out); 
%>
</td>
										<td nowrap style="width: 80px"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "RigaOrdine", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="RigaOrdineField" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
							  			<td><% 
  WebTextInput DocumentoAcquistoRigaPrmRigaOrdine =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "RigaOrdine"); 
  DocumentoAcquistoRigaPrmRigaOrdine.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="10" id="<%=DocumentoAcquistoRigaPrmRigaOrdine.getId()%>" name="<%=DocumentoAcquistoRigaPrmRigaOrdine.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmRigaOrdine.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmRigaOrdine.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmRigaOrdine.write(out); 
%>
</td>
									</tr>
									</table>
									</fieldset>
								</td>
								</tr>
								<tr>
									<!-- Campo Priorita -->
									<td style="width: 150px"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "Priorita", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="PrioritaField" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
									<td><% 
  WebTextInput DocumentoAcquistoRigaPrmPriorita =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "Priorita"); 
  DocumentoAcquistoRigaPrmPriorita.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="5" id="<%=DocumentoAcquistoRigaPrmPriorita.getId()%>" name="<%=DocumentoAcquistoRigaPrmPriorita.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmPriorita.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmPriorita.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmPriorita.write(out); 
%>
</td>
								</tr>
								<tr>
									<!-- Proxy Responsabile Acquisti -->
									<td nowrap>
										<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "IdResponsabileAcquisti", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="ResponsabileAcquistiProxy" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%>
									</td>
									<td nowrap>
										<% 
  WebMultiSearchForm DocumentoAcquistoRigaPrmRResponsabileAcquisti =  
     new com.thera.thermfw.web.WebMultiSearchForm("DocumentoAcquistoRigaPrm", "RResponsabileAcquisti", false, false, true, 1, null, null); 
  DocumentoAcquistoRigaPrmRResponsabileAcquisti.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmRResponsabileAcquisti.write(out); 
%>
<!--<span class="multisearchform" id="ResponsabileAcquistiProxy"></span>-->
									</td>
								</tr>




						    <tr id="SezBolla" name="SezBolla" style="display:none">
							<td colspan="2">
							<fieldset>
								<legend><label id="DatiBolla" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquisto", "DatiBolla", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></legend>
								<table>
								<tr>
									<!-- Numero bolla -->
									<td nowrap style="width: 148px"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "NumeroBolla", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="NumeroBollaField" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
							  		<td style="width: 160px"><% 
  WebTextInput DocumentoAcquistoRigaPrmNumeroBolla =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "NumeroBolla"); 
  DocumentoAcquistoRigaPrmNumeroBolla.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="10" id="<%=DocumentoAcquistoRigaPrmNumeroBolla.getId()%>" name="<%=DocumentoAcquistoRigaPrmNumeroBolla.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmNumeroBolla.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmNumeroBolla.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmNumeroBolla.write(out); 
%>
</td>
									<!-- Data bolla -->
									<td nowrap style="width: 80px"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "DataBolla", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="DataBollaField" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
							  		<td><% 
  WebTextInput DocumentoAcquistoRigaPrmDataBolla =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "DataBolla"); 
  DocumentoAcquistoRigaPrmDataBolla.setShowCalendarBtn(true); 
  DocumentoAcquistoRigaPrmDataBolla.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="7" id="<%=DocumentoAcquistoRigaPrmDataBolla.getId()%>" name="<%=DocumentoAcquistoRigaPrmDataBolla.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmDataBolla.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmDataBolla.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmDataBolla.write(out); 
%>
</td>
								</tr>
								</table>
							</fieldset>
							</td>
							</tr>

							<tr>
								<td colspan="2">
								<table cellspacing="1" cellpadding="1" width="100%">
									<tr>
										<td width="100%" align="left" valign="top">
											<fieldset>
											<legend><label id="DatiDiTrasporto" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "DatiDiTrasporto", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></legend>
											<table cellspacing="1" cellpadding="1">
												<tr>
													<td nowrap style="width: 148px">
														<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "PesoLordo", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="PesoLordo" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%>
													</td>
													<td style="width: 161px">
														<% 
  WebTextInput DocumentoAcquistoRigaPrmPesoLordo =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "PesoLordo"); 
  DocumentoAcquistoRigaPrmPesoLordo.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="<%=DocumentoAcquistoRigaPrmPesoLordo.getSize()%>" id="<%=DocumentoAcquistoRigaPrmPesoLordo.getId()%>" name="<%=DocumentoAcquistoRigaPrmPesoLordo.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmPesoLordo.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmPesoLordo.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmPesoLordo.write(out); 
%>

													</td>
													<td nowrap style="width: 81px">
														<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "PesoNetto", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="PesoNetto" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%>
													</td>
													<td style="width: 111px">
														<% 
  WebTextInput DocumentoAcquistoRigaPrmPesoNetto =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "PesoNetto"); 
  DocumentoAcquistoRigaPrmPesoNetto.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="<%=DocumentoAcquistoRigaPrmPesoNetto.getSize()%>" id="<%=DocumentoAcquistoRigaPrmPesoNetto.getId()%>" name="<%=DocumentoAcquistoRigaPrmPesoNetto.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmPesoNetto.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmPesoNetto.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmPesoNetto.write(out); 
%>

													</td>
													<td nowrap style="width: 81px">
														<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "Volume", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="Volume" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%>
													</td>
													<td style="width: 120px"><!-- Fix 39398 - fine -->
														<% 
  WebTextInput DocumentoAcquistoRigaPrmVolume =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "Volume"); 
  DocumentoAcquistoRigaPrmVolume.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="<%=DocumentoAcquistoRigaPrmVolume.getSize()%>" id="<%=DocumentoAcquistoRigaPrmVolume.getId()%>" name="<%=DocumentoAcquistoRigaPrmVolume.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmVolume.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmVolume.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmVolume.write(out); 
%>

													</td>
													<td>
														<% 
  WebCheckBox DocumentoAcquistoRigaPrmRicalcolaPesiEVolume =  
     new com.thera.thermfw.web.WebCheckBox("DocumentoAcquistoRigaPrm", "RicalcolaPesiEVolume"); 
  DocumentoAcquistoRigaPrmRicalcolaPesiEVolume.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="checkbox" name="<%=DocumentoAcquistoRigaPrmRicalcolaPesiEVolume.getName()%>" id="<%=DocumentoAcquistoRigaPrmRicalcolaPesiEVolume.getId()%>" value="Y"><%
  DocumentoAcquistoRigaPrmRicalcolaPesiEVolume.write(out); 
%>

													</td>
												</tr>
											</table>
											</fieldset>
										</td>
									</tr>
								</table>
							</td>
						</tr>


							</table>
						<% MainTabbed.endTab(); %> 
</div>
						<div class="tabbed_page" id="<%=MainTabbed.getTabPageId("ContabilitaTab")%>" style="width:100%;height:100%;overflow:auto;"><% MainTabbed.startTab("ContabilitaTab"); %>
							<!-- Fix 39398 - inizio modifiche grafiche -->
							<!-- <table width="100%" align="center"> -->
							<table cellspacing="2" cellpadding="2" style="padding: 3px; width: 100%">
							<tr>
									<!-- Proxy Commessa -->
									<td nowrap style="width: 151px"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "IdCommessa", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="CommessaProxy" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
									<td nowrap colspan="2"><% 
  WebMultiSearchForm DocumentoAcquistoRigaPrmRCommessa =  
     new com.thera.thermfw.web.WebMultiSearchForm("DocumentoAcquistoRigaPrm", "RCommessa", false, false, true, 1, null, null); 
  DocumentoAcquistoRigaPrmRCommessa.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmRCommessa.write(out); 
%>
<!--<span class="multisearchform" id="CommessaProxy"></span>--></td>
							</tr>
							<tr>
									<!-- Proxy Centro Ricavo -->
									<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "IdCentroRicavo", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="CentroRicavoProxy" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
									<td nowrap colspan="2"><% 
  WebMultiSearchForm DocumentoAcquistoRigaPrmRCentroCosto =  
     new com.thera.thermfw.web.WebMultiSearchForm("DocumentoAcquistoRigaPrm", "RCentroCosto", false, false, true, 1, null, null); 
  DocumentoAcquistoRigaPrmRCentroCosto.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmRCentroCosto.write(out); 
%>
<!--<span class="multisearchform" id="CentroRicavoProxy"></span>--></td>

							</tr>
							<tr>
									<!-- Proxy Gruppo contabilita analitica -->
									<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "IdGrpCntCa", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="GruppoContiCAProxy" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
									<td nowrap style="width: 550px;"><% 
  WebMultiSearchForm DocumentoAcquistoRigaPrmRGruppoContiCA =  
     new com.thera.thermfw.web.WebMultiSearchForm("DocumentoAcquistoRigaPrm", "RGruppoContiCA", false, false, true, 1, null, null); 
  DocumentoAcquistoRigaPrmRGruppoContiCA.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmRGruppoContiCA.write(out); 
%>
<!--<span class="multisearchform" id="GruppoContiCAProxy"></span>--></td>

							<!-- 8286 - Inizio -->
              <td nowrap>
							<button id="bottoneRicalcolaDatiCA" name="bottoneRicalcolaDatiCA" type="button" onclick="recuperaDatiCA()">
							<label id="RicalcolaDatiCA" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquisto", "RicalcolaDatiCA", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label>
							</button>
              </td>
						  <!-- 8286 - Fine -->

							</tr>
							<% 
   request.setAttribute("parentForm", DocumentoAcquistoRigaPrmForm); 
   String CDForDatiCA$it$thera$thip$acquisti$generaleAC$DatiCA$jsp = "DatiCA"; 
%>
<jsp:include page="/it/thera/thip/acquisti/generaleAC/DatiCA.jsp" flush="true"> 
<jsp:param name="CDName" value="<%=CDForDatiCA$it$thera$thip$acquisti$generaleAC$DatiCA$jsp%>"/> 
</jsp:include> 
<!--<span class="subform" id="DatiCA"></span>-->

						    <tr id="SezFattura" name="SezFattura" style="display:none">
								<td colspan="6">
								<fieldset>
									<legend><label id="DatiFattura" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquisto", "DatiFattura", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></legend>
									<table>
									<tr>
									<!-- Numero fattura -->
									<td nowrap style="width: 147px"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "NumeroFattura", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="NumeroFatturaField" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
							  		<td style="width: 150px"><!-- Fix 32962 <input type ="text" id="NumeroFatturaField" name="NumeroFatturaField" size="10"/> -->
							  		<!-- Fix 39398 - fine -->
									<!-- Fix 32962 - Inizio -->
							  		<% 
  WebTextInput DocumentoAcquistoRigaPrmNumeroFattura =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "NumeroFattura"); 
  DocumentoAcquistoRigaPrmNumeroFattura.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="<%=DocumentoAcquistoRigaPrmNumeroFattura.getSize()%>" id="<%=DocumentoAcquistoRigaPrmNumeroFattura.getId()%>" name="<%=DocumentoAcquistoRigaPrmNumeroFattura.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmNumeroFattura.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmNumeroFattura.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmNumeroFattura.write(out); 
%>

							  		<% 
  WebTextInput DocumentoAcquistoRigaPrmNumeroFattElet =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "NumeroFattElet"); 
  DocumentoAcquistoRigaPrmNumeroFattElet.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="<%=DocumentoAcquistoRigaPrmNumeroFattElet.getSize()%>" id="<%=DocumentoAcquistoRigaPrmNumeroFattElet.getId()%>" name="<%=DocumentoAcquistoRigaPrmNumeroFattElet.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmNumeroFattElet.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmNumeroFattElet.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmNumeroFattElet.write(out); 
%>

									<!-- Fix 32962 - Fine -->
							  		</td>
									<!-- Data fattura -->
									<td nowrap style="width: 110px"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "DataFattura", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="DataFatturaField" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
							  		<td><% 
  WebTextInput DocumentoAcquistoRigaPrmDataFattura =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "DataFattura"); 
  DocumentoAcquistoRigaPrmDataFattura.setShowCalendarBtn(true); 
  DocumentoAcquistoRigaPrmDataFattura.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="7" id="<%=DocumentoAcquistoRigaPrmDataFattura.getId()%>" name="<%=DocumentoAcquistoRigaPrmDataFattura.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmDataFattura.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmDataFattura.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmDataFattura.write(out); 
%>
</td>
									</tr>
									</table>
								</fieldset>
								</td>
							</tr>
							</table>
						<% MainTabbed.endTab(); %> 
</div>
						<!-- Scheda Commenti/Multimedia -->
						<div class="tabbed_page" id="<%=MainTabbed.getTabPageId("CommentiMultimediaTab")%>" style="width:100%;height:100%;overflow:auto;"><% MainTabbed.startTab("CommentiMultimediaTab"); %>
							<!-- Fix 39398 - inizio modifiche grafiche -->
							<!-- <table width="100%" align="center"> -->
							<table cellspacing="2" cellpadding="2" style="padding: 3px; width: 100%">
								<tr>
								<!-- Commenti -->
		     					<td colspan="2">
									<table width="100%">
										<tr>
											<td><% 
   request.setAttribute("parentForm", DocumentoAcquistoRigaPrmForm); 
   String CDForCommentHandler$com$thera$thermfw$cbs$CommentHandler$jsp = "CommentHandler"; 
%>
<jsp:include page="/com/thera/thermfw/cbs/CommentHandler.jsp" flush="true"> 
<jsp:param name="CDName" value="<%=CDForCommentHandler$com$thera$thermfw$cbs$CommentHandler$jsp%>"/> 
</jsp:include> 
<!--<span class="subform" id="Commenti"></span>--></td>
										</tr>
									</table>
								</td>
								</tr>
								<tr>
									<!-- Documento Multimediale -->
									<td nowrap style="width: 150px"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "IdDocumentoMM", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="DocumentoMMField" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
									<td nowrap><% 
  WebMultiSearchForm DocumentoAcquistoRigaPrmRDocumentoMM =  
     new com.thera.thermfw.web.WebMultiSearchForm("DocumentoAcquistoRigaPrm", "RDocumentoMM", false, false, true, 1, null, null); 
  DocumentoAcquistoRigaPrmRDocumentoMM.setParent(DocumentoAcquistoRigaPrmForm); 
  DocumentoAcquistoRigaPrmRDocumentoMM.write(out); 
%>
<!--<span class="multisearchform" id="DocumentoMMField"></span>--></td>
								</tr><!-- Fix 39398 - fine -->
								<tr>
									<!-- Note -->
									<td nowrap valign="top"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "Nota", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="NoteField" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
									<td><% 
  WebTextInput DocumentoAcquistoRigaPrmNota =  
     new com.thera.thermfw.web.WebTextArea("DocumentoAcquistoRigaPrm", "Nota"); 
  DocumentoAcquistoRigaPrmNota.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<textarea cols="75" rows="2" size="<%=DocumentoAcquistoRigaPrmNota.getSize()%>" id="<%=DocumentoAcquistoRigaPrmNota.getId()%>" name="<%=DocumentoAcquistoRigaPrmNota.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmNota.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmNota.getClassType()%>"></textarea><% 
  DocumentoAcquistoRigaPrmNota.write(out); 
%>
</td>
								</tr>
							</table>
						<% MainTabbed.endTab(); %> 
</div>
						<!-- ************************************************************************************************ -->
						<!-- Cartella DettaglioLotti  -->
						<div class="tabbed_page" id="<%=MainTabbed.getTabPageId("LottiTab")%>" style="width:100%;height:100%;overflow:auto;"><% MainTabbed.startTab("LottiTab"); %>
							<!-- Fix 39398 - inizio modifiche grafiche -->
							<!-- <table width="100%" border="0" cellspacing="2" cellpadding="2" align="center"> -->
							<table cellspacing="2" cellpadding="2" style="padding: 3px; width: 100%">
							    <tr>
								<td>&nbsp;</td>
								</tr>
								<tr>
									<td nowrap colspan="1" id="SezAggiornaLotti">
										<button style="width: 75px" name="AggiornaLotti" id="AggiornaLotti" type="button" onclick="aggiornaTuttiILotti()"><%= ResourceLoader.getString("it.thera.thip.base.comuniVenAcq.resources.DocumentoRiga", "AggiornaLotti")%></button>
									</td><!-- Fix 39398 - fine -->
									<td style="display:none">
										<label name="thCoerente" id="thCoerente" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.base.comuniVenAcq.resources.DocumentoRiga", "MsgCoerente", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label>
									</td>
									<td style="display:none">
										<label name="thCambiaStato" id="thCambiaStato" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.base.comuniVenAcq.resources.DocumentoRiga", "MsgCambiaStato", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label>
									</td>
								</tr>
								<tr>
									<td colspan="3"><!--<span id="T5" class="editgrid">--><% 
  WebEditGrid DocumentoAcquistoRigaPrmLotti =  
     new com.thera.thermfw.web.WebEditGrid("DocumentoAcquistoRigaPrm", "Lotti", 6, new String[]{"IdArticolo", "IdLotto","YColata", "QtaPropostaEvasione.QuantitaInUMRif", "QtaPropostaEvasione.QuantitaInUMPrm", "QtaPropostaEvasione.QuantitaInUMSec", "QtaAttesaEvasione.QuantitaInUMRif", "QtaAttesaEvasione.QuantitaInUMPrm", "QtaAttesaEvasione.QuantitaInUMSec", "QtaRicevuta.QuantitaInUMRif", "StatoRigaLotto"}, 1, null, null,false,"com.thera.thermfw.web.servlet.GridActionAdapterForIndependentRow"); 
 DocumentoAcquistoRigaPrmLotti.setParent(DocumentoAcquistoRigaPrmForm); 
 DocumentoAcquistoRigaPrmLotti.setNoControlRowKeys(false); 
 DocumentoAcquistoRigaPrmLotti.addHideAsDefault("QtaPropostaEvasione.QuantitaInUMSec"); 
 DocumentoAcquistoRigaPrmLotti.addHideAsDefault("QtaAttesaEvasione.QuantitaInUMPrm"); 
 DocumentoAcquistoRigaPrmLotti.addHideAsDefault("QtaPropostaEvasione.QuantitaInUMPrm"); 
 DocumentoAcquistoRigaPrmLotti.addHideAsDefault("QtaAttesaEvasione.QuantitaInUMSec"); 
 DocumentoAcquistoRigaPrmLotti.includeAction("DeleteRow",new WebMenuItem("GestioneMatricole", "action_submit", "new", "no", "it.thera.thip.magazzino.matricole.resources.LottoMatricola", "GestioneMatricoleBtn", "gestioneMatricole()", "none", true)); 
 DocumentoAcquistoRigaPrmLotti.write(out); 
%>
 <BR><% 
   request.setAttribute("parentForm", DocumentoAcquistoRigaPrmForm); 
   String CDForLotti = "Lotti"; 
%>
<jsp:include page="/it/thera/thip/acquisti/documentoAC/DocumentoAcqRigaLotto.jsp" flush="true"> 
<jsp:param name="EditGridCDName" value="<%=CDForLotti%>"/> 
<jsp:param name="Mode" value="NEW"/> 
</jsp:include> 
<!--</span>--></td>
								</tr>
							</table>
						<% MainTabbed.endTab(); %> 
</div>

						<!-- ************************************************************************************************ -->
						<!-- Cartella Spedito  -->
						<div class="tabbed_page" id="<%=MainTabbed.getTabPageId("SpeditoTab")%>" style="width:100%;height:100%;overflow:auto;"><% MainTabbed.startTab("SpeditoTab"); %>
							<!-- Fix 39398 - inizio modifiche grafiche -->
							<!-- <table width="100%" border="0" cellspacing="7" cellpadding="7" align="center"> -->
							<table cellspacing="2" cellpadding="2" style="padding: 3px; width: 100%">
							    <tr>
									<td style="width: 150px">&nbsp;</td>
									<td>
									<table width="100%" height="100%">
									<tr>
									<td style="width: 150px"><label id="UMAcqLabel" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "UMAcquistoLabel", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></td>
									<td style="width: 150px"><label id="UMPrmLabel" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "UMPrmLabel", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></td>
									<td><label id="UMSecLabel" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "UMSecLabel", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></td>
									</tr><!-- Fix 39398 - fine -->
									</table>
									</td>
								</tr>
								<tr>
									<td nowrap><label id="QtaPropostaEvasioneLbl" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "QtaPropostaEvasione", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></td>
									<td><% 
   request.setAttribute("parentForm", DocumentoAcquistoRigaPrmForm); 
   String CDForQtaPropostaEvasione$it$thera$thip$base$comuniVenAcq$QuantitaInUMRif$jsp = "QtaPropostaEvasione"; 
%>
<jsp:include page="/it/thera/thip/base/comuniVenAcq/QuantitaInUMRif.jsp" flush="true"> 
<jsp:param name="CDName" value="<%=CDForQtaPropostaEvasione$it$thera$thip$base$comuniVenAcq$QuantitaInUMRif$jsp%>"/> 
</jsp:include> 
<!--<span class="subform" name="QtaPropostaEvasioneField" id="QtaPropostaEvasioneField"></span>--></td>
								</tr>
								<tr>
									<td nowrap><label id="QtaAttesaEvasioneLbl" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "QtaAttesaEvasione", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></td>
									<td><% 
   request.setAttribute("parentForm", DocumentoAcquistoRigaPrmForm); 
   String CDForQtaAttesaEvasione$it$thera$thip$base$comuniVenAcq$QuantitaInUMRif$jsp = "QtaAttesaEvasione"; 
%>
<jsp:include page="/it/thera/thip/base/comuniVenAcq/QuantitaInUMRif.jsp" flush="true"> 
<jsp:param name="CDName" value="<%=CDForQtaAttesaEvasione$it$thera$thip$base$comuniVenAcq$QuantitaInUMRif$jsp%>"/> 
</jsp:include> 
<!--<span class="subform" name="QtaAttesaEvasioneField" id="QtaAttesaEvasioneField"></span>--></td>
								</tr>
								<tr>
									<td nowrap><label id="QtaRicevutaLbl" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "QtaRicevuta", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></td>
									<td><% 
   request.setAttribute("parentForm", DocumentoAcquistoRigaPrmForm); 
   String CDForQtaRicevuta$it$thera$thip$base$comuniVenAcq$QuantitaInUMRif$jsp = "QtaRicevuta"; 
%>
<jsp:include page="/it/thera/thip/base/comuniVenAcq/QuantitaInUMRif.jsp" flush="true"> 
<jsp:param name="CDName" value="<%=CDForQtaRicevuta$it$thera$thip$base$comuniVenAcq$QuantitaInUMRif$jsp%>"/> 
</jsp:include> 
<!--<span class="subform" name="QtaRicevutaField" id="QtaRicevutaField"></span>--></td>
								</tr>
<!--         <tr>          <td nowrap="true"><label id="QtaPrevistaLavEsternaLabel">Qta Prevista Lavorazione Esterna</label></td>          <span class="subform" name="QuantitaPrevistaLavEsternaField" id="QuantitaPrevistaLavEsternaField"></span>         </tr>         <tr>          <td nowrap="true"><label id="QtaSpeditaLavEsternaLabel">Qta Spedita Lavorazione Esterna</label></td>          <span class="subform" name="QuantitaSpeditaLavEsternaField" id="QuantitaSpeditaLavEsternaField"></span>         </tr>         <tr>          <td nowrap="true"><label id="QtaImpiegataLavEsternaLabel">Qta Impiegata Lavorazione Esterna</label></td>          <span class="subform" name="QuantitaImpiegLavEsternaField" id="QuantitaImpiegLavEsternaField"></span>         </tr> -->
						    </table>
						<% MainTabbed.endTab(); %> 
</div>

						<!-- Fix 2844: inizio -->
						<!-- ************************************************************************************************ -->
						<!-- Cartella Contenitori  -->
						<div class="tabbed_page" id="<%=MainTabbed.getTabPageId("ContenitoriTab")%>" style="width:100%;height:100%;overflow:auto;"><% MainTabbed.startTab("ContenitoriTab"); %>
							<!-- <table width="100%" border="0" cellspacing="2" cellpadding="2" align="center"> -->
							<table cellspacing="2" cellpadding="2" style="padding: 3px; width: 100%"> <!-- Fix 39398 -->
								<tr>
									<td><!--<span id="T4" class="editgrid">--><% 
  WebEditGrid DocumentoAcquistoRigaPrmContenitori =  
     new com.thera.thermfw.web.WebEditGrid("DocumentoAcquistoRigaPrm", "Contenitori", 6, new String[]{"IdArticolo", "IdMagazzino", "QtaArticoloXContenitore", "QtaContenitoreUMPrm"}, 1, null, null,false,"com.thera.thermfw.web.servlet.GridActionAdapterForIndependentRow"); 
 DocumentoAcquistoRigaPrmContenitori.setParent(DocumentoAcquistoRigaPrmForm); 
 DocumentoAcquistoRigaPrmContenitori.setNoControlRowKeys(true); 
 DocumentoAcquistoRigaPrmContenitori.write(out); 
%>
 <BR><% 
   request.setAttribute("parentForm", DocumentoAcquistoRigaPrmForm); 
   String CDForContenitori = "Contenitori"; 
%>
<jsp:include page="/it/thera/thip/acquisti/documentoAC/ContenitoreAcqRiga.jsp" flush="true"> 
<jsp:param name="EditGridCDName" value="<%=CDForContenitori%>"/> 
<jsp:param name="Mode" value="NEW"/> 
</jsp:include> 
<!--</span>--></td>
								</tr>
							</table>
						<% MainTabbed.endTab(); %> 
</div>
						<!-- Fix 2844: fine -->

						<!-- ************************************************************************************************ -->
						<!-- Cartella Riepilogo  -->
						<div class="tabbed_page" id="<%=MainTabbed.getTabPageId("RiepilogoTab")%>" style="width:100%;height:100%;overflow:auto;"><% MainTabbed.startTab("RiepilogoTab"); %>
							<!-- Fix 39398 - inizio modifiche grafiche -->
							<!-- <table width="100%" border="0" cellspacing="2" cellpadding="2" align="center"> -->
							<table cellspacing="2" cellpadding="2" style="padding: 3px">
								<tr>
									<td>

							<table width="100%" border="0" cellspacing="2" cellpadding="2" align="center">
								<tr>
									<td style="width: 350px">
										<fieldset>
										<legend><label id="ValoreDocumento" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.base.comuniVenAcq.resources.DocumentoRiga", "ValoreDocumento", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></legend>
											<table>
                                                <tr>
												<td style="width: 173px"></td>
												<td>
												<label id="LabelValutaVA" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.base.comuniVenAcq.resources.DocumentoRiga", "ValutaAziendale", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label>
												</td>
												</tr>
												<tr>
													<!-- Valore in valuta -->
													<!--<td nowrap="true"><label id="ValutaAziendaleField">Valore Documento</label></td>-->
												    <td><label id="ValoreLbl" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.base.comuniVenAcq.resources.DocumentoRiga", "Valore", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></td>
													<td><% 
  WebTextInput DocumentoAcquistoRigaPrmValoreRigaVA =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "ValoreRigaVA"); 
  DocumentoAcquistoRigaPrmValoreRigaVA.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="15" id="<%=DocumentoAcquistoRigaPrmValoreRigaVA.getId()%>" name="<%=DocumentoAcquistoRigaPrmValoreRigaVA.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmValoreRigaVA.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmValoreRigaVA.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmValoreRigaVA.write(out); 
%>
</td>
												</tr>
												<tr>
												    <td><label id="CostoLbl" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.base.comuniVenAcq.resources.DocumentoRiga", "Costo", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></td>
													<td><% 
  WebTextInput DocumentoAcquistoRigaPrmCostoRigaVA =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "CostoRigaVA"); 
  DocumentoAcquistoRigaPrmCostoRigaVA.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="15" id="<%=DocumentoAcquistoRigaPrmCostoRigaVA.getId()%>" name="<%=DocumentoAcquistoRigaPrmCostoRigaVA.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmCostoRigaVA.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmCostoRigaVA.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmCostoRigaVA.write(out); 
%>
</td>
												</tr>
     											<tr>
												    <td><label id="ScostamentoLbl" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.base.comuniVenAcq.resources.DocumentoRiga", "Scostamento", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></td>
  												    <td><% 
  WebTextInput DocumentoAcquistoRigaPrmScostamentoVA =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "ScostamentoVA"); 
  DocumentoAcquistoRigaPrmScostamentoVA.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="15" id="<%=DocumentoAcquistoRigaPrmScostamentoVA.getId()%>" name="<%=DocumentoAcquistoRigaPrmScostamentoVA.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmScostamentoVA.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmScostamentoVA.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmScostamentoVA.write(out); 
%>
</td>
												</tr>
											</table>
										</fieldset>
									<td id="SezValoreValutaAzienda" name="SezValoreValutaAzienda" style="display:none; width: 350px">
										<fieldset>
										<legend><label id="ValoreDocumento" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.base.comuniVenAcq.resources.DocumentoRiga", "ValoreDocumento", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></legend>
											<table>
                                                <tr>
												<td style="width: 80px"></td>
												<td>
												<label id="LabelValutaVO" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.base.comuniVenAcq.resources.DocumentoRiga", "ValutaDocumento", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label>
												</td>
												</tr>
												<tr>
													<!-- Valore Ordinato -->
												    <td><label id="ValoreLbl" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.base.comuniVenAcq.resources.DocumentoRiga", "Valore", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></td>
													<td><% 
  WebTextInput DocumentoAcquistoRigaPrmValoreRiga =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "ValoreRiga"); 
  DocumentoAcquistoRigaPrmValoreRiga.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="15" id="<%=DocumentoAcquistoRigaPrmValoreRiga.getId()%>" name="<%=DocumentoAcquistoRigaPrmValoreRiga.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmValoreRiga.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmValoreRiga.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmValoreRiga.write(out); 
%>
</td>
												</tr>
												<tr>
												    <td><label id="CostoLbl" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.base.comuniVenAcq.resources.DocumentoRiga", "Costo", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></td>
  												    <td><% 
  WebTextInput DocumentoAcquistoRigaPrmCostoRiga =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "CostoRiga"); 
  DocumentoAcquistoRigaPrmCostoRiga.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="15" id="<%=DocumentoAcquistoRigaPrmCostoRiga.getId()%>" name="<%=DocumentoAcquistoRigaPrmCostoRiga.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmCostoRiga.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmCostoRiga.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmCostoRiga.write(out); 
%>
</td>
												</tr>
												<tr>
													<!-- Valore in valuta -->
												    <td><label id="FatturaLbl" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.base.comuniVenAcq.resources.DocumentoRiga", "Fattura", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></td>
													<td><% 
  WebTextInput DocumentoAcquistoRigaPrmImportoNettoFattura =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "ImportoNettoFattura"); 
  DocumentoAcquistoRigaPrmImportoNettoFattura.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="15" id="<%=DocumentoAcquistoRigaPrmImportoNettoFattura.getId()%>" name="<%=DocumentoAcquistoRigaPrmImportoNettoFattura.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmImportoNettoFattura.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmImportoNettoFattura.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmImportoNettoFattura.write(out); 
%>
</td>
												</tr>

											</table>
										</fieldset>
									</td>
									<td id="SezValoreFatturato" name="SezValoreFatturato" style="display:none; width: 350px">
										<fieldset>
										<legend><label id="ValoreFattura" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.base.comuniVenAcq.resources.DocumentoRiga", "ValoreFattura", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></legend>
											<table>
                                                <tr>
												<td style="width: 100px"></td>
												<td>
												<label id="LabelValutaCA" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.base.comuniVenAcq.resources.DocumentoRiga", "ValutaAziendale", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label>
												</td>
												</tr>
												<tr>
													<!-- Valore in valuta -->
													<!--<td nowrap="true"><label id="ValutaAziendaleField">Valore Documento</label></td>-->
												    <td><label id="ValoreLbl" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.base.comuniVenAcq.resources.DocumentoRiga", "Valore", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></td>
													<td><% 
  WebTextInput DocumentoAcquistoRigaPrmImportoNettoFatturaVA =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "ImportoNettoFatturaVA"); 
  DocumentoAcquistoRigaPrmImportoNettoFatturaVA.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="15" id="<%=DocumentoAcquistoRigaPrmImportoNettoFatturaVA.getId()%>" name="<%=DocumentoAcquistoRigaPrmImportoNettoFatturaVA.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmImportoNettoFatturaVA.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmImportoNettoFatturaVA.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmImportoNettoFatturaVA.write(out); 
%>
</td>
												</tr>
												<tr>
												    <td><label id="CostoLbl" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.base.comuniVenAcq.resources.DocumentoRiga", "Costo", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></td>
													<td><% 
  WebTextInput DocumentoAcquistoRigaPrmCostoRigaFatturaVA =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "CostoRigaFatturaVA"); 
  DocumentoAcquistoRigaPrmCostoRigaFatturaVA.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="15" id="<%=DocumentoAcquistoRigaPrmCostoRigaFatturaVA.getId()%>" name="<%=DocumentoAcquistoRigaPrmCostoRigaFatturaVA.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmCostoRigaFatturaVA.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmCostoRigaFatturaVA.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmCostoRigaFatturaVA.write(out); 
%>
</td>
												</tr>
												<tr>
												    <td><label id="ScostamentoLbl" class="thLabel">
 <% { WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple("it.thera.thip.base.comuniVenAcq.resources.DocumentoRiga", "Scostamento", null, null, null, null); 
 label.setParent(DocumentoAcquistoRigaPrmForm); 
label.write(out); }%> 
</label></td>
  												    <td><% 
  WebTextInput DocumentoAcquistoRigaPrmScostamentoFatturaVA =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "ScostamentoFatturaVA"); 
  DocumentoAcquistoRigaPrmScostamentoFatturaVA.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="15" id="<%=DocumentoAcquistoRigaPrmScostamentoFatturaVA.getId()%>" name="<%=DocumentoAcquistoRigaPrmScostamentoFatturaVA.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmScostamentoFatturaVA.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmScostamentoFatturaVA.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmScostamentoFatturaVA.write(out); 
%>
</td>
												</tr>
											</table>
										</fieldset>
									</td>
									</td>
								</tr>
								</table>
								</td>
								</tr>
					            <tr>
								<td>
								<table width="100%" border="0" cellspacing="2" cellpadding="2" align="center">
		 			            		<tr>
									<td nowrap style="width: 175px"><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "CostoUnitario", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="CostoUnitarioField" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td><!-- Fix 39398 - fine -->
									<td><% 
  WebTextInput DocumentoAcquistoRigaPrmCostoUnitario =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "CostoUnitario"); 
  DocumentoAcquistoRigaPrmCostoUnitario.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="20" id="<%=DocumentoAcquistoRigaPrmCostoUnitario.getId()%>" name="<%=DocumentoAcquistoRigaPrmCostoUnitario.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmCostoUnitario.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmCostoUnitario.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmCostoUnitario.write(out); 
%>
</td>
									<td nowrap id="SezPrezzoFatturaLbl" name="SezPrezzoFatturaLbl" style="display:none">
										<%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "PrezzoNettoFattura", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="PrezzoNettoFatturaField" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%>
									</td>
									<td id="SezPrezzoFattura" name="SezPrezzoFattura" style="display:none">
										<% 
  WebTextInput DocumentoAcquistoRigaPrmPrezzoNettoFattura =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "PrezzoNettoFattura"); 
  DocumentoAcquistoRigaPrmPrezzoNettoFattura.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" size="20" id="<%=DocumentoAcquistoRigaPrmPrezzoNettoFattura.getId()%>" name="<%=DocumentoAcquistoRigaPrmPrezzoNettoFattura.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmPrezzoNettoFattura.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmPrezzoNettoFattura.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmPrezzoNettoFattura.write(out); 
%>

									</td>
                                		    		</tr>
								<tr><td>&nbsp;</td></tr>
								<tr>
									<!-- Combo Stato Invio EDI -->
									<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "StatoInvioEDI", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="StatoInvioEDICombo" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
									<td><% 
  WebComboBox DocumentoAcquistoRigaPrmStatoInvioEDI =  
     new com.thera.thermfw.web.WebComboBox("DocumentoAcquistoRigaPrm", "StatoInvioEDI", null); 
  DocumentoAcquistoRigaPrmStatoInvioEDI.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<select name="<%=DocumentoAcquistoRigaPrmStatoInvioEDI.getName()%>" id="<%=DocumentoAcquistoRigaPrmStatoInvioEDI.getId()%>"><% 
  DocumentoAcquistoRigaPrmStatoInvioEDI.write(out); 
%> 
</select></td>
								</tr>
								<tr>
									<!-- Combo Stato Invio Data Warehouse -->
									<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "StatoInvioDatawarehouse", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="StatoInvioDatawarehouseCombo" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
									<td><% 
  WebComboBox DocumentoAcquistoRigaPrmStatoInvioDatawarehouse =  
     new com.thera.thermfw.web.WebComboBox("DocumentoAcquistoRigaPrm", "StatoInvioDatawarehouse", null); 
  DocumentoAcquistoRigaPrmStatoInvioDatawarehouse.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<select name="<%=DocumentoAcquistoRigaPrmStatoInvioDatawarehouse.getName()%>" id="<%=DocumentoAcquistoRigaPrmStatoInvioDatawarehouse.getId()%>"><% 
  DocumentoAcquistoRigaPrmStatoInvioDatawarehouse.write(out); 
%> 
</select></td>
								</tr>
								<tr>
									<!-- Combo Stato Invio Logistica -->
									<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "StatoInvioLogistica", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="StatoInvioLogisticaCombo" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
									<td><% 
  WebComboBox DocumentoAcquistoRigaPrmStatoInvioLogistica =  
     new com.thera.thermfw.web.WebComboBox("DocumentoAcquistoRigaPrm", "StatoInvioLogistica", null); 
  DocumentoAcquistoRigaPrmStatoInvioLogistica.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<select name="<%=DocumentoAcquistoRigaPrmStatoInvioLogistica.getName()%>" id="<%=DocumentoAcquistoRigaPrmStatoInvioLogistica.getId()%>"><% 
  DocumentoAcquistoRigaPrmStatoInvioLogistica.write(out); 
%> 
</select></td>
								</tr>
								<tr>
									<!-- Combo Stato Invio Contabil. Analitica -->
									<td nowrap><%{  WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcquistoRigaPrm", "StatoInvioContAnalitica", null); 
   label.setParent(DocumentoAcquistoRigaPrmForm); 
%><label for="StatoInvioContabAnaliticaCombo" class="<%=label.getClassType()%>"><%label.write(out);%></label><%}%></td>
									<td><% 
  WebComboBox DocumentoAcquistoRigaPrmStatoInvioContAnalitica =  
     new com.thera.thermfw.web.WebComboBox("DocumentoAcquistoRigaPrm", "StatoInvioContAnalitica", null); 
  DocumentoAcquistoRigaPrmStatoInvioContAnalitica.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<select name="<%=DocumentoAcquistoRigaPrmStatoInvioContAnalitica.getName()%>" id="<%=DocumentoAcquistoRigaPrmStatoInvioContAnalitica.getId()%>"><% 
  DocumentoAcquistoRigaPrmStatoInvioContAnalitica.write(out); 
%> 
</select></td>
								</tr>
								</table>
								</td>
								</tr>
							</table>
						<% MainTabbed.endTab(); %> 
</div>
					</div><% MainTabbed.endTabbed();%> 

     </td>
   </tr>
</table><!--</span>-->
				 </td>
			</tr>
      <tr style="display:none">
        <td>


          <!-- **************************************************************************************************** -->
          <!-- Campi nascosti -->
          <table cellspacing="0" cellpadding="0">
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmIdAzienda =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "IdAzienda"); 
  DocumentoAcquistoRigaPrmIdAzienda.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" style="display:none" size="<%=DocumentoAcquistoRigaPrmIdAzienda.getSize()%>" id="<%=DocumentoAcquistoRigaPrmIdAzienda.getId()%>" name="<%=DocumentoAcquistoRigaPrmIdAzienda.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmIdAzienda.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmIdAzienda.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmIdAzienda.write(out); 
%>

              </td>
            </tr>
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmAnnoDocumento =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "AnnoDocumento"); 
  DocumentoAcquistoRigaPrmAnnoDocumento.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" style="display:none" size="<%=DocumentoAcquistoRigaPrmAnnoDocumento.getSize()%>" id="<%=DocumentoAcquistoRigaPrmAnnoDocumento.getId()%>" name="<%=DocumentoAcquistoRigaPrmAnnoDocumento.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmAnnoDocumento.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmAnnoDocumento.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmAnnoDocumento.write(out); 
%>

              </td>
            </tr>
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmNumeroDocumento =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "NumeroDocumento"); 
  DocumentoAcquistoRigaPrmNumeroDocumento.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" style="display:none" size="<%=DocumentoAcquistoRigaPrmNumeroDocumento.getSize()%>" id="<%=DocumentoAcquistoRigaPrmNumeroDocumento.getId()%>" name="<%=DocumentoAcquistoRigaPrmNumeroDocumento.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmNumeroDocumento.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmNumeroDocumento.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmNumeroDocumento.write(out); 
%>

              </td>
            </tr>

            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmRigaDocumento =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "RigaDocumento"); 
  DocumentoAcquistoRigaPrmRigaDocumento.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" style="display:none" size="<%=DocumentoAcquistoRigaPrmRigaDocumento.getSize()%>" id="<%=DocumentoAcquistoRigaPrmRigaDocumento.getId()%>" name="<%=DocumentoAcquistoRigaPrmRigaDocumento.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmRigaDocumento.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmRigaDocumento.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmRigaDocumento.write(out); 
%>

              </td>
            </tr>
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmDettaglioRigaDocumento =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "DettaglioRigaDocumento"); 
  DocumentoAcquistoRigaPrmDettaglioRigaDocumento.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" style="display:none" size="<%=DocumentoAcquistoRigaPrmDettaglioRigaDocumento.getSize()%>" id="<%=DocumentoAcquistoRigaPrmDettaglioRigaDocumento.getId()%>" name="<%=DocumentoAcquistoRigaPrmDettaglioRigaDocumento.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmDettaglioRigaDocumento.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmDettaglioRigaDocumento.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmDettaglioRigaDocumento.write(out); 
%>

              </td>
            </tr>


            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmTipoRiga =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "TipoRiga"); 
  DocumentoAcquistoRigaPrmTipoRiga.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmTipoRiga.getSize()%>" id="<%=DocumentoAcquistoRigaPrmTipoRiga.getId()%>" name="<%=DocumentoAcquistoRigaPrmTipoRiga.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmTipoRiga.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmTipoRiga.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmTipoRiga.write(out); 
%>

              </td>
            </tr>

            <!-- Fix 8286 Inizio -->
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmIdSpesa =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "IdSpesa"); 
  DocumentoAcquistoRigaPrmIdSpesa.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmIdSpesa.getSize()%>" id="<%=DocumentoAcquistoRigaPrmIdSpesa.getId()%>" name="<%=DocumentoAcquistoRigaPrmIdSpesa.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmIdSpesa.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmIdSpesa.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmIdSpesa.write(out); 
%>

              </td>
            </tr>
            <!-- Fix 8286 Fine -->

            <!--             <tr>               <td>                 <input type="hidden" id="UnitaMisuraRiferimentoField" name="UnitaMisuraRiferimentoField"/>               </td>             </tr>             -->
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmIdUMPrm =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "IdUMPrm"); 
  DocumentoAcquistoRigaPrmIdUMPrm.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmIdUMPrm.getSize()%>" id="<%=DocumentoAcquistoRigaPrmIdUMPrm.getId()%>" name="<%=DocumentoAcquistoRigaPrmIdUMPrm.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmIdUMPrm.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmIdUMPrm.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmIdUMPrm.write(out); 
%>

              </td>
            </tr>
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmIdUMSec =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "IdUMSec"); 
  DocumentoAcquistoRigaPrmIdUMSec.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmIdUMSec.getSize()%>" id="<%=DocumentoAcquistoRigaPrmIdUMSec.getId()%>" name="<%=DocumentoAcquistoRigaPrmIdUMSec.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmIdUMSec.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmIdUMSec.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmIdUMSec.write(out); 
%>

              </td>
            </tr>
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmServizioValutaTestata =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "ServizioValutaTestata"); 
  DocumentoAcquistoRigaPrmServizioValutaTestata.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmServizioValutaTestata.getSize()%>" id="<%=DocumentoAcquistoRigaPrmServizioValutaTestata.getId()%>" name="<%=DocumentoAcquistoRigaPrmServizioValutaTestata.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmServizioValutaTestata.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmServizioValutaTestata.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmServizioValutaTestata.write(out); 
%>

              </td>
            </tr>
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmServizioValutaAzienda =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "ServizioValutaAzienda"); 
  DocumentoAcquistoRigaPrmServizioValutaAzienda.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmServizioValutaAzienda.getSize()%>" id="<%=DocumentoAcquistoRigaPrmServizioValutaAzienda.getId()%>" name="<%=DocumentoAcquistoRigaPrmServizioValutaAzienda.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmServizioValutaAzienda.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmServizioValutaAzienda.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmServizioValutaAzienda.write(out); 
%>

              </td>
            </tr>
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmIdVersioneSaldi =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "IdVersioneSaldi"); 
  DocumentoAcquistoRigaPrmIdVersioneSaldi.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmIdVersioneSaldi.getSize()%>" id="<%=DocumentoAcquistoRigaPrmIdVersioneSaldi.getId()%>" name="<%=DocumentoAcquistoRigaPrmIdVersioneSaldi.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmIdVersioneSaldi.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmIdVersioneSaldi.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmIdVersioneSaldi.write(out); 
%>

              </td>
            </tr>
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmServizioCalcDatiAcquisto =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "ServizioCalcDatiAcquisto"); 
  DocumentoAcquistoRigaPrmServizioCalcDatiAcquisto.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmServizioCalcDatiAcquisto.getSize()%>" id="<%=DocumentoAcquistoRigaPrmServizioCalcDatiAcquisto.getId()%>" name="<%=DocumentoAcquistoRigaPrmServizioCalcDatiAcquisto.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmServizioCalcDatiAcquisto.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmServizioCalcDatiAcquisto.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmServizioCalcDatiAcquisto.write(out); 
%>

              </td>
            </tr>
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmServizioListAcqScaglione =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "ServizioListAcqScaglione"); 
  DocumentoAcquistoRigaPrmServizioListAcqScaglione.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmServizioListAcqScaglione.getSize()%>" id="<%=DocumentoAcquistoRigaPrmServizioListAcqScaglione.getId()%>" name="<%=DocumentoAcquistoRigaPrmServizioListAcqScaglione.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmServizioListAcqScaglione.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmServizioListAcqScaglione.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmServizioListAcqScaglione.write(out); 
%>

              </td>
            </tr>
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmPrezzoListino =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "PrezzoListino"); 
  DocumentoAcquistoRigaPrmPrezzoListino.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmPrezzoListino.getSize()%>" id="<%=DocumentoAcquistoRigaPrmPrezzoListino.getId()%>" name="<%=DocumentoAcquistoRigaPrmPrezzoListino.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmPrezzoListino.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmPrezzoListino.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmPrezzoListino.write(out); 
%>

              </td>
            </tr>
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmPrezzoExtraListino =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "PrezzoExtraListino"); 
  DocumentoAcquistoRigaPrmPrezzoExtraListino.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmPrezzoExtraListino.getSize()%>" id="<%=DocumentoAcquistoRigaPrmPrezzoExtraListino.getId()%>" name="<%=DocumentoAcquistoRigaPrmPrezzoExtraListino.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmPrezzoExtraListino.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmPrezzoExtraListino.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmPrezzoExtraListino.write(out); 
%>

              </td>
            </tr>
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmCollegatoMagazzino =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "CollegatoMagazzino"); 
  DocumentoAcquistoRigaPrmCollegatoMagazzino.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmCollegatoMagazzino.getSize()%>" id="<%=DocumentoAcquistoRigaPrmCollegatoMagazzino.getId()%>" name="<%=DocumentoAcquistoRigaPrmCollegatoMagazzino.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmCollegatoMagazzino.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmCollegatoMagazzino.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmCollegatoMagazzino.write(out); 
%>

              </td>
            </tr>
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmTipoLavorazioneEsterna =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "TipoLavorazioneEsterna"); 
  DocumentoAcquistoRigaPrmTipoLavorazioneEsterna.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmTipoLavorazioneEsterna.getSize()%>" id="<%=DocumentoAcquistoRigaPrmTipoLavorazioneEsterna.getId()%>" name="<%=DocumentoAcquistoRigaPrmTipoLavorazioneEsterna.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmTipoLavorazioneEsterna.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmTipoLavorazioneEsterna.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmTipoLavorazioneEsterna.write(out); 
%>

              </td>
            </tr>
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmTipoCostoRiferimento =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "TipoCostoRiferimento"); 
  DocumentoAcquistoRigaPrmTipoCostoRiferimento.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmTipoCostoRiferimento.getSize()%>" id="<%=DocumentoAcquistoRigaPrmTipoCostoRiferimento.getId()%>" name="<%=DocumentoAcquistoRigaPrmTipoCostoRiferimento.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmTipoCostoRiferimento.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmTipoCostoRiferimento.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmTipoCostoRiferimento.write(out); 
%>

              </td>
            </tr>
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmServizioArticoloConfig =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "ServizioArticoloConfig"); 
  DocumentoAcquistoRigaPrmServizioArticoloConfig.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmServizioArticoloConfig.getSize()%>" id="<%=DocumentoAcquistoRigaPrmServizioArticoloConfig.getId()%>" name="<%=DocumentoAcquistoRigaPrmServizioArticoloConfig.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmServizioArticoloConfig.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmServizioArticoloConfig.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmServizioArticoloConfig.write(out); 
%>

              </td>
            </tr>
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmRegistrazioneFattura =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "RegistrazioneFattura"); 
  DocumentoAcquistoRigaPrmRegistrazioneFattura.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmRegistrazioneFattura.getSize()%>" id="<%=DocumentoAcquistoRigaPrmRegistrazioneFattura.getId()%>" name="<%=DocumentoAcquistoRigaPrmRegistrazioneFattura.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmRegistrazioneFattura.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmRegistrazioneFattura.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmRegistrazioneFattura.write(out); 
%>

              </td>
            </tr>
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmAnnoSolare =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "AnnoSolare"); 
  DocumentoAcquistoRigaPrmAnnoSolare.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" style="display:none" size="<%=DocumentoAcquistoRigaPrmAnnoSolare.getSize()%>" id="<%=DocumentoAcquistoRigaPrmAnnoSolare.getId()%>" name="<%=DocumentoAcquistoRigaPrmAnnoSolare.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmAnnoSolare.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmAnnoSolare.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmAnnoSolare.write(out); 
%>

              </td>
            </tr>
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmIdFornitore =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "IdFornitore"); 
  DocumentoAcquistoRigaPrmIdFornitore.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" style="display:none" size="<%=DocumentoAcquistoRigaPrmIdFornitore.getSize()%>" id="<%=DocumentoAcquistoRigaPrmIdFornitore.getId()%>" name="<%=DocumentoAcquistoRigaPrmIdFornitore.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmIdFornitore.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmIdFornitore.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmIdFornitore.write(out); 
%>

              </td>
            </tr>
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmAzioneMagazzino =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "AzioneMagazzino"); 
  DocumentoAcquistoRigaPrmAzioneMagazzino.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="text" style="display:none" size="<%=DocumentoAcquistoRigaPrmAzioneMagazzino.getSize()%>" id="<%=DocumentoAcquistoRigaPrmAzioneMagazzino.getId()%>" name="<%=DocumentoAcquistoRigaPrmAzioneMagazzino.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmAzioneMagazzino.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmAzioneMagazzino.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmAzioneMagazzino.write(out); 
%>

              </td>
            </tr>
            <!-- Fix 1918 inizio -->
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmServizioCatalEstConfig =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "ServizioCatalEstConfig"); 
  DocumentoAcquistoRigaPrmServizioCatalEstConfig.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmServizioCatalEstConfig.getSize()%>" id="<%=DocumentoAcquistoRigaPrmServizioCatalEstConfig.getId()%>" name="<%=DocumentoAcquistoRigaPrmServizioCatalEstConfig.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmServizioCatalEstConfig.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmServizioCatalEstConfig.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmServizioCatalEstConfig.write(out); 
%>

              </td>
            </tr>
            <!-- Fix 1918 fine -->
            <!-- Fix 2384 inizio -->
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmServizioProvenienzaPrezzo =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "ServizioProvenienzaPrezzo"); 
  DocumentoAcquistoRigaPrmServizioProvenienzaPrezzo.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmServizioProvenienzaPrezzo.getSize()%>" id="<%=DocumentoAcquistoRigaPrmServizioProvenienzaPrezzo.getId()%>" name="<%=DocumentoAcquistoRigaPrmServizioProvenienzaPrezzo.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmServizioProvenienzaPrezzo.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmServizioProvenienzaPrezzo.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmServizioProvenienzaPrezzo.write(out); 
%>

              </td>
            </tr>
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmServizioRiferimUMPrezzo =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "ServizioRiferimUMPrezzo"); 
  DocumentoAcquistoRigaPrmServizioRiferimUMPrezzo.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmServizioRiferimUMPrezzo.getSize()%>" id="<%=DocumentoAcquistoRigaPrmServizioRiferimUMPrezzo.getId()%>" name="<%=DocumentoAcquistoRigaPrmServizioRiferimUMPrezzo.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmServizioRiferimUMPrezzo.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmServizioRiferimUMPrezzo.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmServizioRiferimUMPrezzo.write(out); 
%>

              </td>
            </tr>
            <!-- Fix 2384 fine -->
            <!-- Fix 2333 -->
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmAPrezziExtra =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "APrezziExtra"); 
  DocumentoAcquistoRigaPrmAPrezziExtra.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmAPrezziExtra.getSize()%>" id="<%=DocumentoAcquistoRigaPrmAPrezziExtra.getId()%>" name="<%=DocumentoAcquistoRigaPrmAPrezziExtra.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmAPrezziExtra.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmAPrezziExtra.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmAPrezziExtra.write(out); 
%>

              </td>
            </tr>
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmRigaReso =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "RigaReso"); 
  DocumentoAcquistoRigaPrmRigaReso.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmRigaReso.getSize()%>" id="<%=DocumentoAcquistoRigaPrmRigaReso.getId()%>" name="<%=DocumentoAcquistoRigaPrmRigaReso.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmRigaReso.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmRigaReso.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmRigaReso.write(out); 
%>

              </td>
            </tr>
            <!-- Fine Fix 2333 -->
            <!-- Fix 3770 inizio -->
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmIdCatalogoEsterno =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "IdCatalogoEsterno"); 
  DocumentoAcquistoRigaPrmIdCatalogoEsterno.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmIdCatalogoEsterno.getSize()%>" id="<%=DocumentoAcquistoRigaPrmIdCatalogoEsterno.getId()%>" name="<%=DocumentoAcquistoRigaPrmIdCatalogoEsterno.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmIdCatalogoEsterno.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmIdCatalogoEsterno.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmIdCatalogoEsterno.write(out); 
%>

              </td>
            </tr>
            <!-- Fix 3770 fine -->
            <!-- Fix 3910 inizio -->
            <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmQtaMinListino =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "QtaMinListino"); 
  DocumentoAcquistoRigaPrmQtaMinListino.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmQtaMinListino.getSize()%>" id="<%=DocumentoAcquistoRigaPrmQtaMinListino.getId()%>" name="<%=DocumentoAcquistoRigaPrmQtaMinListino.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmQtaMinListino.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmQtaMinListino.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmQtaMinListino.write(out); 
%>

              </td>
            </tr>
            <!-- Fix 3910 fine -->
            <!-- Fix 10173 inizio -->
            <tr>
              <td>
                <input type="hidden" id="ForzaCambioCausale" name="ForzaCambioCausale">
              </td>
            </tr>
            <!-- Fix 10173 fine -->
            <!--Fix 23345 inizio-->
             <tr>
              <td>
                <% 
  WebTextInput DocumentoAcquistoRigaPrmControlloRicalCondiz =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcquistoRigaPrm", "ControlloRicalCondiz"); 
  DocumentoAcquistoRigaPrmControlloRicalCondiz.setParent(DocumentoAcquistoRigaPrmForm); 
%>
<input type="hidden" size="<%=DocumentoAcquistoRigaPrmControlloRicalCondiz.getSize()%>" id="<%=DocumentoAcquistoRigaPrmControlloRicalCondiz.getId()%>" name="<%=DocumentoAcquistoRigaPrmControlloRicalCondiz.getName()%>" maxlength="<%=DocumentoAcquistoRigaPrmControlloRicalCondiz.getMaxLength()%>" class="<%=DocumentoAcquistoRigaPrmControlloRicalCondiz.getClassType()%>"><% 
  DocumentoAcquistoRigaPrmControlloRicalCondiz.write(out); 
%>

              </td>
            </tr>
            <!--Fix 23345 fine-->
          </table>
        </td>
      </tr>
			<!-- **************************************************************************************************** -->
			<!-- ErrorList -->
			<tr>
    			<td style="height:0">
		    	  <% 
  WebErrorList errorList = new com.thera.thermfw.web.WebErrorList(); 
  errorList.setParent(DocumentoAcquistoRigaPrmForm); 
  errorList.write(out); 
%>
<!--<span class="errorlist"></span>-->
			    </td>
			</tr>
		</table>
	<%
  DocumentoAcquistoRigaPrmForm.writeFormEndElements(out); 
%>
</form></td>
</tr>

<tr>
<td style="height:0">
<% String ftr = DocumentoAcquistoRigaPrmForm.getCompleteFooter();
 if (ftr != null) { 
   request.setAttribute("dataCollector", DocumentoAcquistoRigaPrmBODC); 
   request.setAttribute("servletEnvironment", se); %>
  <jsp:include page="<%= ftr %>" flush="true"/> 
<% } %> 
</td>
</tr>
</table>



<%
           // blocco YYY  
           // a completamento blocco di codice XXX in head 
              DocumentoAcquistoRigaPrmForm.writeBodyEndElements(out); 
           } 
           else 
              errors.addAll(0, DocumentoAcquistoRigaPrmBODC.getErrorList().getErrors()); 
        } 
        else 
           errors.addAll(0, DocumentoAcquistoRigaPrmBODC.getErrorList().getErrors()); 
           if(DocumentoAcquistoRigaPrmBODC.getConflict() != null) 
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
     if(DocumentoAcquistoRigaPrmBODC != null && !DocumentoAcquistoRigaPrmBODC.close(false)) 
        errors.addAll(0, DocumentoAcquistoRigaPrmBODC.getErrorList().getErrors()); 
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
     String errorPage = DocumentoAcquistoRigaPrmForm.getErrorPage(); 
%> 
     <jsp:include page="<%=errorPage%>" flush="true"/> 
<% 
  } 
  else 
  { 
     request.setAttribute("ConflictMessages", DocumentoAcquistoRigaPrmBODC.getConflict()); 
     request.setAttribute("ErrorMessages", errors); 
     String conflictPage = DocumentoAcquistoRigaPrmForm.getConflictPage(); 
%> 
     <jsp:include page="<%=conflictPage%>" flush="true"/> 
<% 
   } 
   } 
%> 
</body>
</html>
