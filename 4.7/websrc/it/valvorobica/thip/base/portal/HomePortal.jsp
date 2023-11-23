<%@page import="it.valvorobica.thip.base.generale.ws.utils.YUtilsPortal"%>
<%@page import="it.valvorobica.thip.base.portal.YUserPortalSession"%>
<%@page import="com.thera.thermfw.base.IniFile"%>
<%@page import="com.thera.thermfw.persist.KeyHelper"%>
<%@page import="it.thera.thip.base.cliente.Cliente"%>
<%@page import="it.valvorobica.thip.base.generale.ws.utils.YUserToken"%>
<%
String token = response.getHeader("authorization");
YUserPortalSession userPortalSession = (YUserPortalSession) request.getSession().getAttribute("YUserPortal");
String idClienteAttivo = (String) session.getAttribute("YClienteAttivo"); //dssof3 cliente attivo
String idClienteAttivoRagSoc = (String) session.getAttribute("YClienteAttivoRagSoc");
%>
<html>
<head>
<title>Portale</title>
<link
	href="/<%=YUserPortalSession.WEB_APP_PATH%>/it/valvorobica/thip/base/portal/img/logoSoftre.png"
	rel="icon">
<meta content="width=device-width, initial-scale=1.0" name="viewport">
<link
	href="/<%=YUserPortalSession.WEB_APP_PATH%>/it/valvorobica/thip/base/portal/css/home.css"
	rel="stylesheet">
<link
	href="/<%=YUserPortalSession.WEB_APP_PATH%>/it/valvorobica/thip/base/portal/css/modal_home.css"
	rel="stylesheet">
<link
	href="/<%=YUserPortalSession.WEB_APP_PATH%>/it/valvorobica/thip/base/portal/css/preloader.css"
	rel="stylesheet">
<link
	href="/<%=YUserPortalSession.WEB_APP_PATH%>/it/valvorobica/thip/base/portal/css/bootstrap.min.css"
	rel="stylesheet">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

<script
	src="/<%=YUserPortalSession.WEB_APP_PATH%>/it/valvorobica/thip/base/portal/js/home.js"></script>
<script
	src="/<%=YUserPortalSession.WEB_APP_PATH%>/it/valvorobica/thip/base/portal/js/main.js?v=1.02"></script>

</head>
<%
if (!userPortalSession.getTipoUser().equals(YUserPortalSession.FORNITORE)) {
%>
<body onload="apriSchedaAnagrafica()">
	<%
	} else {
	%>

<body onload="apriSupplierOffer()">
	<%
	}
	%>

	<!-- DSSOF3 Sull'onload del body apro l'anagrafica, 1° schermata -->

	<div id="preloader" class="preloader" style="display: none">
		<div class="spinner"></div>
	</div>

	<%
	String webAppPath = IniFile.getValue("thermfw.ini", "Web", "WebApplicationPath");
	%>
	<input type="hidden" id="webAppPath" value="<%=webAppPath%>">

	<div class="wrapper d-flex align-items-stretch">

		<div id="sidebar"
			class="d-flex flex-column flex-shrink-0 p-3 text-white bg-dark h-100 sidenav"
			style="width: 280px; min-height: 100vh">
			<a href="#" class="nav-link text-white pl-2" id="10"
				aria-current="page" onclick="submitJFun(this)"><img
				src="/<%=YUserPortalSession.WEB_APP_PATH%>/it/valvorobica/thip/base/portal/img/logoImpostazioni.png"
				style="height: 26px;"></a>
			<hr>

			<ul class="nav nav-pills flex-column mb-auto">
				<%
				if (!userPortalSession.getTipoUser().equals(YUserPortalSession.FORNITORE)) {
					if (userPortalSession.getTipoUser().equals(YUserPortalSession.AGENTE)
					|| userPortalSession.getTipoUser().equals(YUserPortalSession.DIPENDENTE)) {
				%>
				<li><a href="#" class="nav-link text-white pl-2" id="0"
					aria-current="page" onclick="submitJFun(this)"> Clienti </a></li>
				<%
				} else if (userPortalSession.getTipoUser().equals(YUserPortalSession.CLIENTE)) {
				%>
				<li><a href="#" class="nav-link text-white pl-2" id="1"
					aria-current="page" onclick="submitJFun(this)"> Anagrafica </a></li>
				<%
				}
				%>
				<li><a href="#" class="nav-link text-white pl-2" id="2"
					onclick="submitJFun(this)"> Manuali/Dich. Conformità </a></li>
				<li><a href="#" class="nav-link text-white pl-2" id="3"
					onclick="submitJFun(this)"> Offerte </a></li>
				<li><a href="#" class="nav-link text-white pl-2" id="4"
					onclick="submitJFun(this)"> Ordini </a></li>
				<li><a href="#" class="nav-link text-white pl-2" id="5"
					onclick="submitJFun(this)"> Ddt </a></li>
				<li><a href="#" class="nav-link text-white pl-2" id="6"
					onclick="submitJFun(this)"> Fatture/Note cr. </a></li>
				<li><a href="#" class="nav-link text-white pl-2" id="7"
					onclick="submitJFun(this)"> Inevaso </a></li>
				<li><a href="#" class="nav-link text-white pl-2" id="8"
					onclick="submitJFun(this)"> Certificati </a></li>
				<%
				if (userPortalSession.getTipoUser().equals(YUserPortalSession.CLIENTE)) {
				%>
				<li><a href="#" class="nav-link text-white pl-2" id="14"
					onclick="submitJFun(this)"> Catalogo </a></li>
					<li><a href="#" class="nav-link text-white pl-2" id="15"
					onclick="submitJFun(this)"> Carrello </a></li>
				<%
				}
				%>
			</ul>
			<hr>
			<%
			}
			if (userPortalSession.isMultipleEnv()) { //se l'utente ha piu di un azienda, mostro il bottone 'Cambia azienda'
			%>
			<div class="row">
				<div class="col-6">
					<a href="#" class="nav-link text-white pl-2" id="11"
						aria-current="page" onclick="submitJFun(this)">Logout <img
						src="/<%=YUserPortalSession.WEB_APP_PATH%>/it/valvorobica/thip/base/portal/img/logoLogout.png"
						style="height: 24px;"></a>
				</div>
				<div class="col-6">
					<a href="#" class="nav-link text-white pl-2" id="12"
						aria-current="page" onclick="submitJFun(this)">Cambia azienda</a>
				</div>
			</div>

			<%
			} else { //altrimenti potra' solo sloggarsi
			%>
			<a href="#" class="nav-link text-white pl-2" id="11"
				aria-current="page" onclick="submitJFun(this)"> Logout <img
				src="/<%=YUserPortalSession.WEB_APP_PATH%>/it/valvorobica/thip/base/portal/img/logoLogout.png"
				style="height: 24px;"></a>
			<%
			}
			if (userPortalSession.getTipoUser().equals(YUserPortalSession.FORNITORE)) {
			%>
			<div class="col-6" style="display: none;">
				<a href="#" class="nav-link text-white pl-2" id="13"
					aria-current="page" onclick="submitJFun(this)"></a>
			</div>
			<%
			}
			%>
		</div>



		<!-- Page Content  -->
		<div id="content">
			<div class="row" style="-bs-gutter-x: 0;">
				<!-- Toggler -->
				<div class="col-4" style="width: fit-content;">
					<button type="button" id="sidebarCollapse"
						class="btn burgerSoftre p-1 m-1"
						style="height: fit-content; border: 1px solid black; border-radius: 10px;">
						<!-- 				<img src="it/valvorobica/thip/base/portal/img/logoSoftre.png" -->
						<!-- 					style="height: 64px;"> -->
						<img
							src="/<%=YUserPortalSession.WEB_APP_PATH%>/it/valvorobica/thip/base/portal/img/logo<%=userPortalSession.getIdAzienda()%>.png"
							style="height: 64px;">
					</button>
				</div>
				<!-- DSSOF3 Aggiunto titolo con valore del cliente attivato in sessione, funziona come filtro su ogni voce -->
				<%
				if (idClienteAttivo != null && (userPortalSession.getTipoUser().equals(YUserPortalSession.AGENTE)
						|| userPortalSession.getTipoUser().equals(YUserPortalSession.DIPENDENTE))) {
				%>
				<div class="col-4 mt-auto">
					<h1
						style="display: inline; font-size: 1rem; font-weight: bold; color: #DC3545; text-decoration: underline;"><%=idClienteAttivo + " - " + idClienteAttivoRagSoc%></h1>
					<span style="cursor: pointer;" onclick="disattivaClienteAttivo()"
						title="Rimuovi cliente attivo">&#10060;</span>
				</div>
				<%
				} else {
				session.removeAttribute("YClienteAttivo");
				session.removeAttribute("YClienteAttivoRagSoc");
				}
				%>
			</div>
			<div class="cont">
				<iframe style="height: 88%; width: 100%; padding: 0; margin: 0;"
					id="frameContent" class="px-4 py-3"> </iframe>
			</div>
			<div style="display: none" class="text-center">
				<a href="#modalTokenExpired" id="tokenExpiredClick"
					class="trigger-btn" data-toggle="modal"></a>
			</div>
			<div id="modalTokenExpired" class="modal fade">
				<div class="modal-dialog modal-confirm">
					<div class="modal-content">
						<div class="modal-header">
							<h4 class="modal-title">Attenzione!</h4>
						</div>
						<div class="modal-body">
							<p class="text-center">Token expired, ripetere il login</p>
						</div>
						<div class="modal-footer" style="flex-wrap: unset;">
							<button class="btn btn-block"
								onClick="submitJFun(document.getElementById('11'))"
								data-dismiss="modal">OK</button>
						</div>
					</div>
				</div>
			</div>
			<div style="display: none" class="text-center">
				<a href="#modalWarning" id="modalWarningClick"
					class="trigger-btn" data-toggle="modal"></a>
			</div>
			<div id="modalWarning" class="modal fade">
				<div class="modal-dialog modal-confirm">
					<div class="modal-content">
						<div class="modal-header">
							<h4 class="modal-title">Attenzione!</h4>
						</div>
						<div class="modal-body">
							<p id="txtWarning" class="text-center"></p>
						</div>
						<div class="modal-footer" style="flex-wrap: unset;">
							<button class="btn btn-block"
								onClick="$('#modalWarning').modal('toggle');"
								data-dismiss="modal">OK</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<script>

$(document).ready(function () {
    $('#sidebarCollapse').on('click', function () {
        $('#sidebar').toggleClass('active');
    });
});

		function submitJFun(element){
			if(screen.width < 500)
				document.getElementById('sidebarCollapse').click();
			gestioneSpinner(element);
			//if(!element.innerHTML.includes('Anagrafica'))
			mostraSpinner();
			if(element.innerHTML.includes('Logout')){
				setTimeout(()=>{
					evidenziaBottoneCliccato(element);	
					var frameContent = document.getElementById('frameContent');	
					var url = "/<%=YUserPortalSession.WEB_APP_PATH%>/it/valvorobica/thip/base/portal/JFUN.jsp?fun="+element.id;
					url += "&token=<%=token%>";
					url += "&webAppPath="+ document.getElementById('webAppPath').value;
					frameContent.src = url;
				},5000);
			}else{
				evidenziaBottoneCliccato(element);	
				var frameContent = document.getElementById('frameContent');	
				var url = "/<%=YUserPortalSession.WEB_APP_PATH%>/it/valvorobica/thip/base/portal/JFUN.jsp?fun="+element.id;
				url += "&token=<%=token%>";
				url += "&webAppPath="+ document.getElementById('webAppPath').value;
				frameContent.src = url;
			}
		}
		
		function mostraSpinner(){
			document.getElementById('preloader').style.display = "block";
		}
		
		function gestioneSpinner(element){
			if(element != undefined){
				if(element.innerHTML.includes('Anagrafica')
						|| element.innerHTML.includes('Clienti')
						|| element.innerHTML.includes('Logout')){
					document.getElementById("preloader").childNodes[1].classList.add('spinnerSoftre');
					document.getElementById("preloader").childNodes[1].classList.remove('spinner');
				}else{
					document.getElementById("preloader").childNodes[1].classList.add('spinner');
					document.getElementById("preloader").childNodes[1].classList.remove('spinnerSoftre');
				}
			}
		}
		
		function evidenziaBottoneCliccato(element) {
			var ul = element.parentNode.parentNode;
			var lis = ul.getElementsByTagName("li");
			for (var i = 0; i < lis.length; i++) {
				var li = lis[i];
				li.classList.remove("li-selected");
			}
			element.parentNode.classList.add("li-selected");
		}

		function apriSchedaAnagrafica() {
			document.getElementById("preloader").childNodes[1].classList.add('spinnerSoftre');
			document.getElementById("preloader").childNodes[1].classList.remove('spinner');
			submitJFun(document.getElementsByClassName('nav-link text-white pl-2')[1]);
		}
		
		function apriSupplierOffer(){
			document.getElementById("preloader").childNodes[1].classList.add('spinnerSoftre');
			document.getElementById("preloader").childNodes[1].classList.remove('spinner');
			submitJFun(document.getElementById("<%=YUtilsPortal.FUNZIONE_COMPILAZIONE_OFFERTA_SUPPLIER%>"));
		}
		
		function disattivaClienteAttivo(){
			var frameContent = parent.document.getElementById('frameContent');
			var url = "/<%=YUserPortalSession.WEB_APP_PATH%>/it/valvorobica/thip/base/portal/YRimuoviClienteAttivo.jsp";
			frameContent.src = url;
		}
		</script>
</body>
</html>