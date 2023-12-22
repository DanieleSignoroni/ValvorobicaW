<!-- WIZGEN Therm 2.0.0 as Form riga interna - multiBrowserGen = true -->

<%
if (false) {
%>
<head>
<%
}
%>

<%@ page contentType="text/html; charset=Cp1252"%>
<%@ page
	import=" 
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
ServletEnvironment se = (ServletEnvironment) Factory.createObject("com.thera.thermfw.web.ServletEnvironment");
BODataCollector DocumentoAcqRigaLottoPrmBODC = null;
WebFormForInternalRowForm DocumentoAcqRigaLottoPrmForm = new com.thera.thermfw.web.WebFormForInternalRowForm(request,
		response, "DocumentoAcqRigaLottoPrmForm", "DocumentoAcqRigaLottoPrm", "Arial,10",
		"com.thera.thermfw.web.servlet.FormActionAdapter", false, false, true, true, true, true, null, 1);
DocumentoAcqRigaLottoPrmForm
		.setWebFormModifierClass("it.thera.thip.acquisti.documentoAC.web.DocumentoAcqRigaLottoFormModifier");
DocumentoAcqRigaLottoPrmForm.setOKNewButtForInternalRowForm(true);
int mode = DocumentoAcqRigaLottoPrmForm.getMode();
String key = DocumentoAcqRigaLottoPrmForm.getKey();
String errorMessage;
boolean requestIsValid = false;
boolean leftIsKey = false;
String leftClass = "";
try {
	se.initialize(request, response);
	if (se.begin()) {
		DocumentoAcqRigaLottoPrmForm.outTraceInfo(getClass().getName());
		ClassADCollection globCadc = DocumentoAcqRigaLottoPrmForm.getClassADCollection();
		requestIsValid = true;
		DocumentoAcqRigaLottoPrmForm.write(out);
		String collectorName = DocumentoAcqRigaLottoPrmForm.findBODataCollectorName();
		DocumentoAcqRigaLottoPrmBODC = (BODataCollector) Factory.createObject(collectorName);
		DocumentoAcqRigaLottoPrmBODC.initialize("DocumentoAcqRigaLottoPrm", true, 1);
		DocumentoAcqRigaLottoPrmForm.setBODataCollector(DocumentoAcqRigaLottoPrmBODC);
		WebForm parentForm = (WebForm) request.getAttribute("parentForm");
		DocumentoAcqRigaLottoPrmForm.setJSTypeList(parentForm.getOwnerForm().getJSTypeList());
		DocumentoAcqRigaLottoPrmForm.setParent(parentForm);
		DocumentoAcqRigaLottoPrmForm.writeHeadElements(out);
	}
} catch (NamingException e) {
	errorMessage = e.getMessage();
} catch (SQLException e) {
	errorMessage = e.getMessage();
} finally {
	try {
		se.end();
	} catch (IllegalArgumentException e) {
		e.printStackTrace();
	} catch (SQLException e) {
		e.printStackTrace();
	}
}
%>

<title>Ordine - Lotto</title>
<%
if (false) {
%>
</head>
<%
}
%>




<%
if (false) {
%>
<body>
	<%
	}
	%>
	<%
	DocumentoAcqRigaLottoPrmForm.writeBodyStartElements(out);
	%>


	<%
	if (false) {
	%>
	<form name="DocumentoAcqRigaLottoForm">
		<%
		}
		%>
		<%
		DocumentoAcqRigaLottoPrmForm.writeFormStartElements(out);
		%>

		<table>

			<!-- **************************************************************************************************** -->
			<!-- Pannello tabulare principale -->
			<tr>
				<td height="100%">
					<!--<span class="tabbed" id="LottoTabbed">-->
					<table width="100%" height="162px" cellpadding="0" cellspacing="0"
						style="padding-right: 1px">
						<tr valign="top">
							<td>
								<%
							WebTabbed LottoTabbed = new com.thera.thermfw.web.WebTabbed("LottoTabbed", "99%", "130"); 
								LottoTabbed.setParent(DocumentoAcqRigaLottoPrmForm);
								LottoTabbed.addTab("GeneraleLottoTab", "it.thera.thip.vendite.ordineVE.resources.OrdineVenditaLotto", "GeneraleTab",
										"DocumentoAcqRigaLottoPrm", null, null, null, null);
								LottoTabbed.addTab("SpeditoTab", "it.thera.thip.vendite.documentoVE.resources.DocumentoVenditaRiga", "SpeditoTab",
										"DocumentoAcqRigaLottoPrm", null, null, null, null);
								LottoTabbed.write(out);
								%>

							</td>
						</tr>
						<tr>
							<td height="100%"><div class="tabbed_pagine"
									id="tabbedPagine"
									style="position: relative; width: 100%; height: 100%;">
									<!-- **************************************************************************************** -->
									<!-- Cartella Generale -->
									<div class="tabbed_page"
										id="<%=LottoTabbed.getTabPageId("GeneraleLottoTab")%>"
										style="width: 700; height: 130; overflow: auto;">
										<%
										LottoTabbed.startTab("GeneraleLottoTab");
										%>
										<table>
											<tr>

												<!-- Proxy Lotto -->
												<td nowrap>
													<%
													{
														WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcqRigaLottoPrm",
														"IdLotto", null);
														label.setParent(DocumentoAcqRigaLottoPrmForm);
													%><label for="Lotto" class="<%=label.getClassType()%>">
														<%
														label.write(out);
														%>
												</label> <%
													}
													%>
												</td>
												<!-- DSSOF3 Aggiunto l'input della colata, fatto a STD per poter far funzionare l'internalRowForm e 
												salvare il valore nel campo LOTTO_ACQUISTO di THIP.LOTTI
												 -->
												<td>
													<%
													WebTextInput colata = new com.thera.thermfw.web.WebTextInput("YDocumentoAcqRigaLottoPrm", "ColataRifLottoForn");
													colata.setParent(DocumentoAcqRigaLottoPrmForm);
													%> <input type="text" size="12" id="<%=colata.getId()%>"
													name="<%=colata.getName()%>"
													maxlength="<%=colata.getMaxLength()%>"
													class="<%=colata.getClassType()%>"> <%
													colata.write(out);
													%>
												</td>
												<td style="display:none;">
													<%
													WebTextInput sigla = new com.thera.thermfw.web.WebTextInput("YDocumentoAcqRigaLottoPrm", "YSigla");
													sigla.setParent(DocumentoAcqRigaLottoPrmForm);
													%><input type="text" size="12" id="<%=sigla.getId()%>"
													name="<%=sigla.getName()%>"
													maxlength="<%=sigla.getMaxLength()%>"
													class="<%=sigla.getClassType()%>"> <%
													sigla.write(out);
													%>
												</td>
												<!-- DSSOF3 Fine aggiunta campo RifLottoAcq -->
												<td nowrap colspan="3">
													<%
													WebMultiSearchForm DocumentoAcqRigaLottoPrmRLotto = new it.thera.thip.magazzino.generalemag.web.LottoMultiSearchForm(
															"DocumentoAcqRigaLottoPrm", "RLotto", false, false, true, 1, null, null);
													DocumentoAcqRigaLottoPrmRLotto.setExtraRelatedClassAD("DataApertura, DataScadenza");
													DocumentoAcqRigaLottoPrmRLotto.setParent(DocumentoAcqRigaLottoPrmForm);
													DocumentoAcqRigaLottoPrmRLotto.setOnKeyChange("controllaIdLotto()");
													DocumentoAcqRigaLottoPrmRLotto.setOnSearchBack("impostaDesc()");
													DocumentoAcqRigaLottoPrmRLotto.setSpecificDOList("it.thera.thip.magazzino.generalemag.web.LottoSearchDOList");
													DocumentoAcqRigaLottoPrmRLotto.write(out);
													%> <!--<span class="lottomultisearchform" id="Lotto"></span>-->
												</td>
												<!--Fix 15936 inizio-->
												<td>
													<button id="thBeniRicercaBut" type="button"
														class="thShowSBut" onclick="azioneRicercaBeni()">
														<img width="16" border="0" height="16"
															src="thermweb/image/gui/Search.gif">
													</button>
												</td>
												<td><button id="thBeniModificaBut" type="button"
														class="thShowSBut" onclick="azioneModificaBeni()">
														<img width="16" border="0" height="16"
															src="thermweb/image/gui/Edit.gif">
													</button></td>
												<!--Fix 15936 fine-->
											</tr>
											<tr>
												<td>&nbsp;</td>
												<!-- DSSOF3 Rimosso align center sulla cella -->
												<td><label id="UMAcqLabel"
													class="thLabel"> <%
 {
 	WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple(
 	"it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquisto", "UMAcqLabel", null, null, null, null);
 	label.setParent(DocumentoAcqRigaLottoPrmForm);
 	label.write(out);
 }
 %>
												</label></td>
												<!-- DSSOF3 Rimosso align center sulla cella -->
												<td><label id="UMPrmLabel"
													class="thLabel"> <%
 {
 	WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple(
 	"it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquisto", "UMPrmLabel", null, null, null, null);
 	label.setParent(DocumentoAcqRigaLottoPrmForm);
 	label.write(out);
 }
 %>
												</label></td>
												<td id="SezUMSecLblLotto" name="SezUMSecLblLotto"
													align="center"><label id="UMSecLabel" class="thLabel">
														<%
														{
															WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple(
															"it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquisto", "UMSecLabel", null, null, null, null);
															label.setParent(DocumentoAcqRigaLottoPrmForm);
															label.write(out);
														}
														%>
												</label></td>
											</tr>
											<tr>
												<td nowrap><label id="QuantitaLotto" class="thLabel">
														<%
														{
															WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple(
															"it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquisto", "QuantitaLotto", null, null, null, null);
															label.setParent(DocumentoAcqRigaLottoPrmForm);
															label.write(out);
														}
														%>
												</label> <label id="TipoQuantitaLottoLbl" class="thLabel"> <%
 {
 	WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple(
 	"it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquisto", "TipoQuantitaLottoLbl", null, null, null,
 	null);
 	label.setParent(DocumentoAcqRigaLottoPrmForm);
 	label.write(out);
 }
 %>
												</label></td>
												<!-- Campo Qta Vendita -->
												<td>
													<%
													WebTextInput DocumentoAcqRigaLottoPrmQtaInUMAcq = new com.thera.thermfw.web.WebTextInput("DocumentoAcqRigaLottoPrm",
															"QtaInUMAcq");
													DocumentoAcqRigaLottoPrmQtaInUMAcq.setOnChange("variazioneQuantAcquistoLotto(false)");
													DocumentoAcqRigaLottoPrmQtaInUMAcq.setParent(DocumentoAcqRigaLottoPrmForm);
													%> <input type="text" size="12"
													id="<%=DocumentoAcqRigaLottoPrmQtaInUMAcq.getId()%>"
													name="<%=DocumentoAcqRigaLottoPrmQtaInUMAcq.getName()%>"
													maxlength="<%=DocumentoAcqRigaLottoPrmQtaInUMAcq.getMaxLength()%>"
													class="<%=DocumentoAcqRigaLottoPrmQtaInUMAcq.getClassType()%>">
													<%
													DocumentoAcqRigaLottoPrmQtaInUMAcq.write(out);
													%>

												</td>
												<!-- Campo Qta Primaria -->
												<td>
													<%
													WebTextInput DocumentoAcqRigaLottoPrmQtaInUMPrmMag = new com.thera.thermfw.web.WebTextInput("DocumentoAcqRigaLottoPrm",
															"QtaInUMPrmMag");
													DocumentoAcqRigaLottoPrmQtaInUMPrmMag.setOnChange("variazioneQuantPrimariaMagazzinoLotto(false)");
													DocumentoAcqRigaLottoPrmQtaInUMPrmMag.setParent(DocumentoAcqRigaLottoPrmForm);
													%> <input type="text" size="12"
													id="<%=DocumentoAcqRigaLottoPrmQtaInUMPrmMag.getId()%>"
													name="<%=DocumentoAcqRigaLottoPrmQtaInUMPrmMag.getName()%>"
													maxlength="<%=DocumentoAcqRigaLottoPrmQtaInUMPrmMag.getMaxLength()%>"
													class="<%=DocumentoAcqRigaLottoPrmQtaInUMPrmMag.getClassType()%>">
													<%
													DocumentoAcqRigaLottoPrmQtaInUMPrmMag.write(out);
													%>

												</td>
												<!-- Campo Qta Secondaria -->
												<td id="SezUMSecondariaLotto" name="SezUMSecondariaLotto">
													<%
													WebTextInput DocumentoAcqRigaLottoPrmQtaInUMSecMag = new com.thera.thermfw.web.WebTextInput("DocumentoAcqRigaLottoPrm",
															"QtaInUMSecMag");
													DocumentoAcqRigaLottoPrmQtaInUMSecMag.setOnChange("variazioneQuantSecondariaMagazzinoLotto(false)");
													DocumentoAcqRigaLottoPrmQtaInUMSecMag.setParent(DocumentoAcqRigaLottoPrmForm);
													%> <input type="text" size="12"
													id="<%=DocumentoAcqRigaLottoPrmQtaInUMSecMag.getId()%>"
													name="<%=DocumentoAcqRigaLottoPrmQtaInUMSecMag.getName()%>"
													maxlength="<%=DocumentoAcqRigaLottoPrmQtaInUMSecMag.getMaxLength()%>"
													class="<%=DocumentoAcqRigaLottoPrmQtaInUMSecMag.getClassType()%>">
													<%
													DocumentoAcqRigaLottoPrmQtaInUMSecMag.write(out);
													%>

												</td>
											</tr>
											<tr>
												<!-- Combo Stato Evasione -->
												<td nowrap>
													<%
													{
														WebLabelCompound label = new com.thera.thermfw.web.WebLabelCompound(null, null, "DocumentoAcqRigaLottoPrm",
														"StatoRigaLotto", null);
														label.setParent(DocumentoAcqRigaLottoPrmForm);
													%><label for="StatoRigaLotto"
													class="<%=label.getClassType()%>"> <%
														label.write(out);
														%>
												</label> <%
													}
													%>
												</td>
												<td>
													<%
													WebComboBox DocumentoAcqRigaLottoPrmStatoRigaLotto = new com.thera.thermfw.web.WebComboBox("DocumentoAcqRigaLottoPrm",
															"StatoRigaLotto", null);
													DocumentoAcqRigaLottoPrmStatoRigaLotto.setParent(DocumentoAcqRigaLottoPrmForm);
													%> <select
													name="<%=DocumentoAcqRigaLottoPrmStatoRigaLotto.getName()%>"
													id="<%=DocumentoAcqRigaLottoPrmStatoRigaLotto.getId()%>">
														<%
														DocumentoAcqRigaLottoPrmStatoRigaLotto.write(out);
														%>
												</select>
												</td>
											</tr>
										</table>
										<%
										LottoTabbed.endTab();
										%>
									</div>

									<!-- ************************************************************************************************ -->
									<!-- Cartella Spedito  -->
									<div class="tabbed_page"
										id="<%=LottoTabbed.getTabPageId("SpeditoTab")%>"
										style="width: 700; height: 130; overflow: auto;">
										<%
										LottoTabbed.startTab("SpeditoTab");
										%>
										<table border="0" cellspacing="2" cellpadding="0">
											<tr>
												<td>&nbsp;</td>
												<td>
													<table width="100%" height="100%">
														<tr>
															<td width="33%"><label id="UMAcqField"
																class="thLabel"> <%
 {
 	WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple(
 	"it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "UMAcquisto", null, null, null, null);
 	label.setParent(DocumentoAcqRigaLottoPrmForm);
 	label.write(out);
 }
 %>
															</label></td>
															<td width="33%"><label id="UMPrmField"
																class="thLabel"> <%
 {
 	WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple(
 	"it.thera.thip.base.comuniVenAcq.resources.DocumentoRiga", "UMPrm", null, null, null, null);
 	label.setParent(DocumentoAcqRigaLottoPrmForm);
 	label.write(out);
 }
 %>
															</label></td>
															<td width="33%"><label id="UMSecField"
																class="thLabel"> <%
 {
 	WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple(
 	"it.thera.thip.base.comuniVenAcq.resources.DocumentoRiga", "UMSec", null, null, null, null);
 	label.setParent(DocumentoAcqRigaLottoPrmForm);
 	label.write(out);
 }
 %>
															</label></td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td nowrap><label id="QtaPropostaEvasioneLottoLbl"
													class="thLabel"> <%
 {
 	WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple(
 	"it.thera.thip.base.comuniVenAcq.resources.DocumentoRiga", "QtaPropostaEvasione", null, null, null, null);
 	label.setParent(DocumentoAcqRigaLottoPrmForm);
 	label.write(out);
 }
 %>
												</label></td>
												<td>
													<%
													request.setAttribute("parentForm", DocumentoAcqRigaLottoPrmForm);
													String CDForQtaPropostaEvasione$it$thera$thip$base$comuniVenAcq$QuantitaInUMRif$jsp = "QtaPropostaEvasione";
													%> <jsp:include
														page="/it/thera/thip/base/comuniVenAcq/QuantitaInUMRif.jsp"
														flush="true">
														<jsp:param name="CDName"
															value="<%=CDForQtaPropostaEvasione$it$thera$thip$base$comuniVenAcq$QuantitaInUMRif$jsp%>" />
													</jsp:include> <!--<span class="subform" name="QtaPropostaEvasioneField" id="QtaPropostaEvasioneField"></span>-->
												</td>
											</tr>
											<tr>
												<td nowrap><label id="QtaAttesaEvasioneLottoLbl"
													class="thLabel"> <%
 {
 	WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple(
 	"it.thera.thip.base.comuniVenAcq.resources.DocumentoRiga", "QtaAttesaEvasione", null, null, null, null);
 	label.setParent(DocumentoAcqRigaLottoPrmForm);
 	label.write(out);
 }
 %>
												</label></td>
												<td>
													<%
													request.setAttribute("parentForm", DocumentoAcqRigaLottoPrmForm);
													String CDForQtaAttesaEvasione$it$thera$thip$base$comuniVenAcq$QuantitaInUMRif$jsp = "QtaAttesaEvasione";
													%> <jsp:include
														page="/it/thera/thip/base/comuniVenAcq/QuantitaInUMRif.jsp"
														flush="true">
														<jsp:param name="CDName"
															value="<%=CDForQtaAttesaEvasione$it$thera$thip$base$comuniVenAcq$QuantitaInUMRif$jsp%>" />
													</jsp:include> <!--<span class="subform" name="QtaAttesaEvasioneField" id="QtaAttesaEvasioneField"></span>-->
												</td>
											</tr>
											<tr>
												<td nowrap><label id="QtaRicevutaLottoLbl"
													class="thLabel"> <%
 {
 	WebLabelSimple label = new com.thera.thermfw.web.WebLabelSimple(
 	"it.thera.thip.acquisti.documentoAC.resources.DocumentoAcquistoRiga", "QtaRicevuta", null, null, null,
 	null);
 	label.setParent(DocumentoAcqRigaLottoPrmForm);
 	label.write(out);
 }
 %>
												</label></td>
												<td>
													<%
													request.setAttribute("parentForm", DocumentoAcqRigaLottoPrmForm);
													String CDForQtaRicevuta$it$thera$thip$base$comuniVenAcq$QuantitaInUMRif$jsp = "QtaRicevuta";
													%> <jsp:include
														page="/it/thera/thip/base/comuniVenAcq/QuantitaInUMRif.jsp"
														flush="true">
														<jsp:param name="CDName"
															value="<%=CDForQtaRicevuta$it$thera$thip$base$comuniVenAcq$QuantitaInUMRif$jsp%>" />
													</jsp:include> <!--<span class="subform" name="QtaRicevutaField" id="QtaRicevutaField"></span>-->
												</td>
											</tr>
										</table>
										<%
										LottoTabbed.endTab();
										%>
									</div>
								</div> <%
								LottoTabbed.endTabbed();
								%></td>
						</tr>
					</table> <!--</span>-->
				</td>
			</tr>
			<tr>
				<td>
					<%
					WebTextInput DocumentoAcqRigaLottoPrmIdAzienda = new com.thera.thermfw.web.WebTextInput("DocumentoAcqRigaLottoPrm",
							"IdAzienda");
					DocumentoAcqRigaLottoPrmIdAzienda.setParent(DocumentoAcqRigaLottoPrmForm);
					%> <input type="hidden"
					size="<%=DocumentoAcqRigaLottoPrmIdAzienda.getSize()%>"
					id="<%=DocumentoAcqRigaLottoPrmIdAzienda.getId()%>"
					name="<%=DocumentoAcqRigaLottoPrmIdAzienda.getName()%>"
					maxlength="<%=DocumentoAcqRigaLottoPrmIdAzienda.getMaxLength()%>"
					class="<%=DocumentoAcqRigaLottoPrmIdAzienda.getClassType()%>">
					<%
					DocumentoAcqRigaLottoPrmIdAzienda.write(out);
					%>
				</td>
			</tr>
			<tr>
				<td>
					<%
					WebTextInput DocumentoAcqRigaLottoPrmAnnoDocumento = new com.thera.thermfw.web.WebTextInput("DocumentoAcqRigaLottoPrm",
							"AnnoDocumento");
					DocumentoAcqRigaLottoPrmAnnoDocumento.setParent(DocumentoAcqRigaLottoPrmForm);
					%> <input type="hidden"
					size="<%=DocumentoAcqRigaLottoPrmAnnoDocumento.getSize()%>"
					id="<%=DocumentoAcqRigaLottoPrmAnnoDocumento.getId()%>"
					name="<%=DocumentoAcqRigaLottoPrmAnnoDocumento.getName()%>"
					maxlength="<%=DocumentoAcqRigaLottoPrmAnnoDocumento.getMaxLength()%>"
					class="<%=DocumentoAcqRigaLottoPrmAnnoDocumento.getClassType()%>">
					<%
					DocumentoAcqRigaLottoPrmAnnoDocumento.write(out);
					%>
				</td>
			</tr>
			<tr>
				<td>
					<%
					WebTextInput DocumentoAcqRigaLottoPrmNumeroDocumento = new com.thera.thermfw.web.WebTextInput(
							"DocumentoAcqRigaLottoPrm", "NumeroDocumento");
					DocumentoAcqRigaLottoPrmNumeroDocumento.setParent(DocumentoAcqRigaLottoPrmForm);
					%> <input type="hidden"
					size="<%=DocumentoAcqRigaLottoPrmNumeroDocumento.getSize()%>"
					id="<%=DocumentoAcqRigaLottoPrmNumeroDocumento.getId()%>"
					name="<%=DocumentoAcqRigaLottoPrmNumeroDocumento.getName()%>"
					maxlength="<%=DocumentoAcqRigaLottoPrmNumeroDocumento.getMaxLength()%>"
					class="<%=DocumentoAcqRigaLottoPrmNumeroDocumento.getClassType()%>">
					<%
					DocumentoAcqRigaLottoPrmNumeroDocumento.write(out);
					%>
				</td>
			</tr>
			<tr>
				<td>
					<%
					WebTextInput DocumentoAcqRigaLottoPrmNumeroRigaDocumento = new com.thera.thermfw.web.WebTextInput(
							"DocumentoAcqRigaLottoPrm", "NumeroRigaDocumento");
					DocumentoAcqRigaLottoPrmNumeroRigaDocumento.setParent(DocumentoAcqRigaLottoPrmForm);
					%> <input type="hidden"
					size="<%=DocumentoAcqRigaLottoPrmNumeroRigaDocumento.getSize()%>"
					id="<%=DocumentoAcqRigaLottoPrmNumeroRigaDocumento.getId()%>"
					name="<%=DocumentoAcqRigaLottoPrmNumeroRigaDocumento.getName()%>"
					maxlength="<%=DocumentoAcqRigaLottoPrmNumeroRigaDocumento.getMaxLength()%>"
					class="<%=DocumentoAcqRigaLottoPrmNumeroRigaDocumento.getClassType()%>">
					<% 
  DocumentoAcqRigaLottoPrmNumeroRigaDocumento.write(out); 
%>
				</td>
			</tr>
			<tr>
				<td>
					<% 
  WebTextInput DocumentoAcqRigaLottoPrmDettaglioRigaDocumento =  
     new com.thera.thermfw.web.WebTextInput("DocumentoAcqRigaLottoPrm", "DettaglioRigaDocumento"); 
  DocumentoAcqRigaLottoPrmDettaglioRigaDocumento.setParent(DocumentoAcqRigaLottoPrmForm); 
%> <input type="hidden"
					size="<%=DocumentoAcqRigaLottoPrmDettaglioRigaDocumento.getSize()%>"
					id="<%=DocumentoAcqRigaLottoPrmDettaglioRigaDocumento.getId()%>"
					name="<%=DocumentoAcqRigaLottoPrmDettaglioRigaDocumento.getName()%>"
					maxlength="<%=DocumentoAcqRigaLottoPrmDettaglioRigaDocumento.getMaxLength()%>"
					class="<%=DocumentoAcqRigaLottoPrmDettaglioRigaDocumento.getClassType()%>">
					<% 
  DocumentoAcqRigaLottoPrmDettaglioRigaDocumento.write(out); 
%>
				</td>
			</tr>
		</table>
		<%
  DocumentoAcqRigaLottoPrmForm.writeFormEndElements(out); 
%>
		<% 
  if(false) 
  { 
%>
	</form>
	<% 
  } 
%>


	<%
   DocumentoAcqRigaLottoPrmForm.writeBodyEndElements(out); 
%>
	<% 
  if(false) 
  { 
%>
</body>
<% 
  } 
%>

