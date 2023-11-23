var enctypeDefault;
var encodingDefault;
var enctypeMultipart = "multipart/form-data";

function YImgCatalogoPortaleOL() {
	enctypeDefault = document.forms[0].enctype;
	encodingDefault = document.forms[0].encoding;
	loadBoRelations();
	document.getElementById('BORelation').addEventListener('change', function() {
		loadBoValues(this.value);
		document.getElementById('KeyValue').innerHTML = '';
		document.getElementById('TipoClassificazione').value = this.selectedOptions[0].innerHTML.trim();
	});
	document.getElementById('KeyValue').addEventListener('change', function() {
		document.getElementById('IdClassificazione').value = this.selectedOptions[0].innerHTML.trim();
	});
	if (document.forms[0].thMode.value == 'UPDATE') {
		document.getElementById('UrlImg').parentNode.parentNode.style.display = 'revert';
		setTimeout(function() {
			var tipoClassificazone = document.getElementById('TipoClassificazione').value;
			var selectElement = document.getElementById("BORelation");
			for (var i = 0; i < selectElement.options.length; i++) {
				var option = selectElement.options[i];
				if (option.text === tipoClassificazone) {
					selectElement.value = option.value;
					loadBoValues(option.value);
				}
			}
		}, 500);
		document.getElementById('BORelation').disabled=true;
		document.getElementById('KeyValue').disabled=true;
	} else {
		document.getElementById('UrlImg').parentNode.parentNode.style.display = 'none';
	}

}

function loadBoValues(businessClass) {
	var url = "/" + webAppPath + "/" + servletPath + "/it.valvorobica.thip.base.portal.web.YKeyValuesLoader?ClassName=" + businessClass;
	var f = document.getElementById(errorsFrameName).contentWindow;
	setLocationOnWindow(f, url);
}

function loadBoRelations() {
	var url = "/" + webAppPath + "/" + servletPath + "/it.valvorobica.thip.base.portal.web.YRelationsLoader";
	var f = document.getElementById(errorsFrameName).contentWindow;
	setLocationOnWindow(f, url);
}

function addBoAtt(name, description, comboName) {
	var combo = document.getElementById(comboName);
	addOption(combo, description, name);
}

function clearBoAtt(comboName) {
	clearCombobox(comboName);
}

function addOption(field, value, description) {
	var option = new Option(value, description);
	field.options[field.options.length] = option;
}

var arrayBlobUploaded = ['', '', ''];

function caricaImg(dimensione) {
	var idUpload = '';
	var urlImg = '';
	var index;

	if (dimensione == 'img') {
		idUpload = 'img';
		urlImg = 'UrlImg';
		index = 0;
	}

	document.getElementById(idUpload).click();
	document.getElementById(idUpload).onchange = function() {
		var fileScelto = document.getElementById(idUpload).files[0];
		document.getElementById(urlImg).value = fileScelto.name;
		console.log(window.URL.createObjectURL(fileScelto));
		arrayBlobUploaded[index] = window.URL.createObjectURL(fileScelto);
		enableMultipart();
		salvaFile();
	}
}

function enableMultipart() {
	document.forms[0].enctype = enctypeMultipart;
	document.forms[0].encoding = enctypeMultipart;
}

function disableMultipart() {
	document.forms[0].enctype = enctypeDefault;
	document.forms[0].encoding = encodingDefault;
}

function salvaFile() {
	runActionDirect("CARICA", "action_submit", document.getElementById("thClassName").value, document.getElementById("thKey").value, "errorsFrame", "no");
}

function fireActionCompleted() {
	enableFormActions();
	var errorsView = eval(errorsViewName);
	if (errorsView) {
		errorsView.clearDisplay();
	}
}

function valorizzaNomeImmagine(nomeImmagine) {
	document.forms[0].UrlImg.value = nomeImmagine;
}
//Fix 38898 fine