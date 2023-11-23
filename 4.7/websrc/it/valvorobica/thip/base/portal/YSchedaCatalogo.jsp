<%@page import="java.util.HashMap"%>
<%@page import="it.valvorobica.thip.base.portal.YCarrelloPortaleTM"%>
<%@page import="java.util.ArrayList"%>
<%@page import="it.thera.thip.ws.GenericQuery"%>
<%@page import="it.thera.thip.base.catalogo.CatalogoNavigatoreNodo"%>
<%@page import="it.valvorobica.thip.base.generale.ws.YCatalogoPortale"%>
<%@page import="com.thera.thermfw.persist.KeyHelper"%>
<%@page import="it.valvorobica.thip.base.portal.YUserPortalSession"%>
<%@page import="com.thera.thermfw.base.IniFile"%>
<%@page import="it.valvorobica.thip.base.generale.ws.dati.YClientePortale.YIndirizziClientePortale"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.thera.thermfw.persist.ConnectionManager"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.thera.thermfw.web.SessionEnvironment"%>
<%@page import="com.thera.thermfw.persist.ConnectionDescriptor"%>
<%@page import="com.thera.thermfw.security.Security"%>
<%@page import="com.thera.thermfw.base.Trace"%>
<%@page import="it.valvorobica.thip.base.generale.ws.dati.YDatiClientePortale"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="it.valvorobica.thip.base.generale.ws.YAnagrafica"%>
<%@page import="com.thera.thermfw.persist.Factory"%>
<%@page import="com.thera.thermfw.web.ServletEnvironment"%>

<%
YUserPortalSession userPortalSession = (YUserPortalSession) request.getSession().getAttribute("YUserPortal");
String webAppPath = IniFile.getValue("thermfw.ini", "Web", "WebApplicationPath");
String select = "SELECT COUNT(*) FROM " + YCarrelloPortaleTM.TABLE_NAME + " C ";
String where = "WHERE C." + YCarrelloPortaleTM.ID_AZIENDA + " = '" + userPortalSession.getIdAzienda() + "'" + " AND C."
		+ YCarrelloPortaleTM.R_UTENTE_PORTALE + " = '" + userPortalSession.getIdUtente() + "' ";
Map values = null;
boolean isopen = false;
Object[] info = SessionEnvironment.getDBInfoFromIniFile();
String dbName = (String) info[0];
Integer items = 0;
// if (userPortalSession.getJsonCatalogo() == null) {
	try {
		if (!Security.isCurrentDatabaseSetted()) {
	Security.setCurrentDatabase(dbName, null);
		}
		Security.openDefaultConnection();
		isopen = true;
		ConnectionDescriptor cd = ConnectionManager.getCurrentConnectionDescriptor();
		GenericQuery gq = new GenericQuery();
		gq.getAppParams().put("query", select + where);
		gq.setUseAuthentication(false);
		gq.setUseAuthorization(false);
		gq.setUseLicence(false);
		gq.setConnectionDescriptor(cd);
		values = gq.send();
		ArrayList records = (ArrayList) values.get("records");
		if (records != null && records.size() > 0) {
	ArrayList valuesRecords = (ArrayList) records.get(0);
	items = Integer.valueOf(valuesRecords.get(0).toString().trim());
		}
		YCatalogoPortale cat = new YCatalogoPortale();
		cat.setUseAuthentication(false);
		cat.setUseAuthorization(false);
		cat.setUseLicence(false);
		cat.setConnectionDescriptor(cd);
		cat.setUserSession(userPortalSession);
		values = cat.send();
		ArrayList errors = (ArrayList) values.get("errors");
		if (errors.isEmpty()) {
			userPortalSession.setJsonCatalogo((String) values.get("catalogo"));
		}
		items = (Integer) values.get("nr_items");
	} catch (Throwable t) {
		t.printStackTrace(Trace.excStream);
	} finally {
		if (isopen) {
	Security.closeDefaultConnection();
		}
	}
// }else{
// 	items = null;
// }
%>

<html>
<head>
<meta charset="utf-8">
<meta content="width=device-width, initial-scale=1.0" name="viewport">

<title>Scheda Anagrafica</title>

<!-- CSS Files -->
<link href="css/bootstrap.min.css" rel="stylesheet">
<link href="css/frames.css" rel="stylesheet">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.1/dist/css/bootstrap.min.css">
<link
	href="https://maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="/<%=webAppPath%>/it/valvorobica/thip/base/portal/css/cart.css" />
<link rel="stylesheet"
	href="/<%=webAppPath%>/it/valvorobica/thip/base/portal/css/jquery-ui.css" />
<script
	src="/<%=webAppPath%>/it/valvorobica/thip/base/portal/js/jquery.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
<script src="https://code.jquery.com/ui/1.13.0/jquery-ui.min.js"></script>
<script src="js/main_1.js"></script>
<link
	href="/<%=webAppPath%>/it/valvorobica/thip/assets/DataTables/datatables.css"
	rel="stylesheet">
<script
	src="/<%=webAppPath%>/it/valvorobica/thip/assets/DataTables/datatables.js"></script>
<script
	src="/<%=webAppPath%>/it/valvorobica/thip/base/portal/js/modal_utils.js"></script>
<script
	src="/<%=webAppPath%>/it/valvorobica/thip/assets/DataTables/Select/js/dataTables.select.js"></script>
<script
	src="/<%=webAppPath%>/it/valvorobica/thip/assets/DataTables/Buttons/js/dataTables.buttons.js"></script>
<style type="text/css">
.card-img-overlaybrixia {
	position: absolute;
	right: 0;
	bottom: 0;
	left: 0;
	/* padding: 1rem; */
	border-radius: calc(0.25rem - 1px);
}
.card-title {
	 background-color: rgb(255, 255, 255, 40%);
	/* width: fit-content; */
	/* width: 100%; */
	/* cursor: pointer; */
	/* user-select: none; */
	/* padding: 0.5rem 0.2rem; */
}

</style>
</head>
<body class="noscrollbar"
	onload="rimuoviSpinner();parent.collapseSidebar();">
	<input type="hidden" name="urlWS" id="urlWS" value="">
	<input type="hidden" name="webAppPath" id="webAppPath"
		value="<%=webAppPath%>">
	<input type="hidden" name="Azienda" id="Azienda"
		value="<%=userPortalSession.getIdAzienda()%>">
	<input type="hidden" name="token" id="token"
		value="<%=userPortalSession.getTokecUID()%>">
	<form>
		<div class="container-fluid">
			<div class="row mt-3">
				<div class="col-8" id="title-container">
					<h5 class="card-title" id="card-title">Catalogo</h5>
				</div>
						<div id="cart" class="col-sm-2 cart" title="Apri carrello" data-totalitems="0" style="cursor:pointer;" onclick="openCart()">
					<i class="fa fa-shopping-cart"></i>
				</div>
			</div>
			<div class="row mt-5" id="root-container">
				<!-- Content will be added here -->
			</div>
			<div class="container-fluid mt-3" id="dati"></div>
		</div>
	</form>
	<script>
		<%
		if(items != null){
			%>
			var cart = $('#cart',document);
			cart.addClass('shake').attr('data-totalitems', <%=items%>);
			setTimeout(function(){
		        cart.removeClass('shake');
		      },1000)			
			<%
		}
		%>
		var json =<%=userPortalSession.getJsonCatalogo()%>;
		 var currentLevel = json;
		 var parentStack = [];
		function renderCard(item, container) {
				var card = document.createElement('div');
				card.className = 'col-2 mb-4';
				var cardContent = '<div class="card" id="'+item.text+'"><img class="img-fluid" onclick="toggleChildren(this)" src="'+item.img+'"></img><div class="card-img-overlaybrixia"><h5 class="card-title">'+item.text+'</h5></div></div>';
				card.innerHTML = cardContent;
				return card;
		}
		
		 function toggleChildren(event) {
	            var img = event;
	            var card = img.closest('.card');
	            document.getElementById('root-container').innerHTML = '';
	            parentStack.push(card.getAttribute('id'));
	                var children = getChildren(currentLevel, card.getAttribute('id'));
	                if (children) {
	                	 var rootContainer = document.getElementById('root-container');
	                	 children.forEach(function(item) {
	     	                var card = renderCard(item,rootContainer);
	     	               rootContainer.appendChild(card);
	     	                var cardImage = card.querySelector('.card-img-top');
	     	            });
	                }
	                currentLevel = children;
	                renderBreadcrumb(false);
	        }

		 function renderData(data, container) {
	            data.forEach(function(item) {
	                var card = renderCard(item,container);
	                container.appendChild(card);

	                var cardImage = card.querySelector('.card-img-top');
	            });
	        }
	        
		 function getTitlePath(itemTitle) {
	            var parentPath = '';
	            if (parentStack.length > 0) {
	                parentPath = parentStack.join('/');
	                if(parentStack.length > 1){
	                if (parentPath !== '') {
// 	                    parentPath += '/';
	                }
	                }
	            }
	            return parentPath;
	        }

	        function renderBreadcrumb(start) {
	        	if(!start){
	            var breadcrumbContainer = document.getElementById('title-container');
	            var path = getTitlePath();
	            var parts = path.split('/');
	            var breadcrumbHtml = '';

	            for (var i = 0; i < parts.length; i++) {
	                var linkText = parts[i];
	                    breadcrumbHtml += "<span id='title' style='display:none;'>"+linkText+"</span>";
	                    breadcrumbHtml += "<a id='navigator' href='#' onclick='navigateToLevel("+i+",\"" + linkText + "\")'>"+linkText+"</a>";
	                    breadcrumbHtml += ' / ';
	            }
	            breadcrumbContainer.innerHTML = breadcrumbHtml;
	        	}else{
	        		var breadcrumbContainer = document.getElementById('title-container');
	        		var breadcrumbHtml = '';
	        		breadcrumbHtml += '<a id="navigator" href="#" onclick="navigateToLevel(0)">Catalogo</a>';
                    breadcrumbHtml += ' / ';	
                    breadcrumbContainer.innerHTML = breadcrumbHtml;
	        	}
	        }
	        
	        function openCart(){
				mostraSpinner();
				var frameContent = parent.document.getElementById('frameContent');	
				var url = "/<%= YUserPortalSession.WEB_APP_PATH %>/it/valvorobica/thip/base/portal/YSchedaCarrello.jsp?";
				url += "&webAppPath="+ parent.document.getElementById('webAppPath').value;
				frameContent.src = url;
			}
	        
	        function getChildren(level, itemTitle) {
				var foundItem = find(level,itemTitle);
				if(itemTitle == 'Catalogo'){
					return foundItem; //ritorno il json padre e non i figli
				}
				if(!foundItem.children){
					$("#title-container").css("display", "none");
					parent.mostraSpinner();
					$.ajax({
						url : encodeURI($('#urlWS').val()+"?id=YCATD&token="+$('#token').val()+"&company="+$('#Azienda').val()+"&idUserPortal=<%=userPortalSession.getIdUtente()%>&idCliente=<%=userPortalSession.getIdCliente()%>&idVista="+parentStack.join('/')+"&where="+foundItem.where),
						method : "GET",
						data : {},
						success : function(data) {
							let table = "<table id='table' class='table table-striped table-hover table-borderless'><thead class='thead-dark'></thead><tbody id='tbody'></tbody></table>";
							$('#dati').append(table);
							let record = data.records;
							let headers = data.headers;
							tableHead = $("table thead");
							const jsonArray = JSON.parse(headers);
							let header = "<tr>";
							jsonArray.forEach(function(id){
								header += "<th>"+id.id+"</th>";
							});
							header += "</tr>";
							tableHead.append(header);
							const jsonRecords = JSON.parse(record);
							 jsonRecords.forEach(function(value){
								  let markup = "<tr>";
								  markup += "<input type='hidden' name='IdArticoloPth' value='"+value.IdArticoloPth+"'></input>";
								  markup += "<td name='articolo'>"+value.IdArticolo+"</td>";
								  markup += "<td>"+value.DescrEstesa+"</td>";
								  markup += "<td name='disp'>"+parseFloat(value.Giacenza).toFixed(3)+"</td>";
								  markup += "<td name='prezzo'>"+parseFloat(value.Prezzo).toFixed(3)+"</td>";
								  markup += "<td><input class='form-control rcrPrz' type='number' name='quantita' onchange='ricercaPrezzo(this)'></input></td><td><div class='buttons'> <button type='button' onclick='addToCart(this)' class='cart-button'> <span class='add-to-cart'>Aggiungi</span> <span class='added'>Added</span> <i class='fa fa-shopping-cart'></i> <i class='fa fa-square'></i> </button> </div></td>";
								  tableBody = $("table tbody");
					              tableBody.append(markup);
							  });

							 setTimeout(table = new DataTable('#table'),{
								  "autoWidth": false, // might need this
								 aoColumns : [
								      { "sWidth": "20%"},
								      { "sWidth": "50%"},
								      { "sWidth": "10%"},
								      { "sWidth": "5%"},
								      { "sWidth": "10%"},
								      { "sWidth": "5%"},
								    ],
							 },1000);
							 
							 parent.rimuoviSpinner();
							 
							 $("#title-container").css("display", "revert");
						},
						error: function(xhr, status, error) {
							xhr.responseJSON.errors.forEach(function(obj) {
								if(obj[0].includes('token expired')){
									parent.document.getElementById('tokenExpiredClick').click();
								}
							});
							$("#title-container").css("display", "revert");
						    parent.rimuoviSpinner();
						  }
					});
				}
	            return foundItem ? foundItem.children : null; //qui invece voglio ritornare sempre i figli, perche'
	            //saro' sempre a livello -1
	        }
	        	
	        function find(source, id){
	        	if(id == 'Catalogo'){
	        		return json;
	        	}
	            for (key in source)
	            {
	                var item = source[key];
	                if (item.text == id)
	                    return item;
	                if (item.children)
	                {
	                    var subresult = find(item.children, id);
	                    if (subresult)
	                        return subresult;
	                }
	            }
	            return null;
	        }

	        function navigateToLevel(levelIndex,linkText) {
	        	var table = $('#table').DataTable();
	        	table.destroy();
	        	document.getElementById('dati').innerHTML = '';
	                currentLevel = getChildren(json, linkText);
	                var index = parentStack.indexOf(linkText);
	    	        parentStack = parentStack.slice(0,index+1);
	            document.getElementById('root-container').innerHTML = '';
	            renderData(currentLevel, document.getElementById('root-container'));
	            renderBreadcrumb();
	        }
	        
	        parentStack.push('Catalogo');
	        renderBreadcrumb(true);
	        var rootContainer = document.getElementById('root-container');
	        renderData(json, rootContainer);
	        compilaURLWS();
	        
	        function ricercaPrezzo(input){
				input.value = parseFloat(input.value).toFixed(4);
				parent.mostraSpinner();
				var qta = parseFloat(input.value);
				var disp = input.parentNode.parentNode.querySelector('[name="disp"]').innerHTML;
				var idArticolo = input.parentNode.parentNode.querySelector('[name="IdArticoloPth"]').value;
				var idCliente = '<%=userPortalSession.getIdCliente()%>';
				var url = $('#urlWS').val();
				var token = $('#token').val();
				var idAzienda = '<%=userPortalSession.getIdAzienda()%>';
			if (qta > disp) {
				var txt = "Non e' possibile ordinare piu di quanto disponibile in giacenza";
				openModal('txtWarning', $('#modalWarningClick',
						parent.parent.document)[0], txt);
				input.value = '';
				parent.rimuoviSpinner();
				return;
			}
			if (qta <= 0) {
				var txt = "Inserire una quantita' positiva";
				openModal('txtWarning', $('#modalWarningClick',
						parent.parent.document)[0], txt);
				input.value= '';
				parent.rimuoviSpinner();
				return;
			}
			if (isNaN(qta) || qta == undefined) {
				input.parentNode.nextElementSibling.innerHTML = '';
				parent.rimuoviSpinner();
			} else {
				getPrezzo(url, token, idCliente, idArticolo, qta, idAzienda)
						.done(
								function(response) {
									var responseBody = response;
									input.parentNode.parentNode.querySelector('[name="prezzo"]').innerHTML = parseFloat(
											response['prezzo']).toFixed(4);
									parent.rimuoviSpinner();
								}).fail(
								function(jqXHR, textStatus, errorThrown) {
									xhr.responseJSON.errors.forEach(function(
											obj) {
										if (obj[0].includes('token expired')) {
											parent.document.getElementById(
													'tokenExpiredClick')
													.click();
										}
									});
									parent.rimuoviSpinner();
								});
			}
		}

		function getPrezzo(url, token, cliente, articolo, qta, azi) {
			return $.ajax({
				url : encodeURI(url + '?id=RPEC&token=' + token + '&company='
						+ azi + '&tipoUMVendita=V&codCliente=' + cliente
						+ '&codArticolo=' + articolo + '&qtaRichiesta=' + qta), // Replace with the actual URL
				method : 'GET',
				dataType : 'json'
			});
		}
		
		function addToCart(btn){
			parent.mostraSpinner();
			var tr = btn.parentNode.parentNode.parentNode;
			var art = tr.querySelector('[name=IdArticoloPth]').value;
			if(tr.querySelector('[name=disp]') == null){
				var txt = "L'articolo non e' disponibile";
				openModal('txtWarning',$('#modalWarningClick',parent.parent.document)[0],txt);
				parent.rimuoviSpinner();
				return;
			}
			var qta = tr.querySelector('[name=quantita]').value;
			if(parseFloat(qta) > parseFloat(tr.querySelector('[name=disp]').innerHTML)){
				var txt = "Non e' possibile ordinare piu' di quanto disponibile \n Sistemare le quantita'";
				openModal('txtWarning',$('#modalWarningClick',parent.parent.document)[0],txt);
				parent.rimuoviSpinner();
				return;
			}
			var qtaInput = tr.querySelector('[name=quantita]');
			var przInput = tr.querySelector('[name=prezzo]');
			var idUtente = "<%=userPortalSession.getIdUtente()%>";
			var idAzienda = "<%=userPortalSession.getIdAzienda()%>";
			var idCliente = "<%=userPortalSession.getIdCliente()%>";
			var disp = tr.querySelector('[name=disp]');
			if(isNaN(qta) || qta == undefined || qta <= 0){
				var txt = "Inserire una quantita' positiva";
				openModal('txtWarning',$('#modalWarningClick',parent.parent.document)[0],txt);
				parent.rimuoviSpinner();
				return;
			}
			var json = "{articolo : '"+art+"', quantita: '"+qta+"',idUtente: '"+idUtente+"',idAzienda:'"+idAzienda+"',idCliente:'"+idCliente+"'}";
			$.ajax({
				  url: $('#urlWS').val()+'?id=YADDC&token='+$('#token').val(), 
				  method: 'POST', 
				  dataType: 'json', 
				  data:  json,
				  contentType: 'application/json; charset=utf-8',
				  success: function(response) {
					  btn.classList.add('clicked');
					  var items = parseFloat(response.nrItemsCart);
					  setTimeout(function(){
							parent.rimuoviSpinner();
							btn.classList.remove('clicked');
							qtaInput.value = null;
							var cart = $('#cart',parent.document);
							cart.addClass('shake').attr('data-totalitems', items);
							setTimeout(function(){
						        cart.removeClass('shake');
						      },1000)
						},2000);
					  disp.innerHTML = parseFloat((parseFloat(disp.innerHTML)-qtaInput.value)).toFixed(3);
				  },
				  error: function(xhr, status, error) {
						xhr.responseJSON.errors.forEach(function(obj) {
							if(obj[0].includes('token expired')){
								parent.parent.document.getElementById('tokenExpiredClick').click();
							}
						});
					    parent.rimuoviSpinner();
					  }
				});
		}

		function compilaURLWS() {
			var ris;
			var url = window.location.href;
			var wbAppPth = parent.document.getElementById('webAppPath').value;
			var cut = url.indexOf(wbAppPth);
			ris = url.substring(0, cut);
			ris += wbAppPth;
			ris += "/ws";
			document.getElementById('urlWS').value = ris;
		}
	</script>
</body>
</html>