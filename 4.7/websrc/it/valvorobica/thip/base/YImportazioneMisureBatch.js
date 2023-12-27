var enctypeDefault;
var encodingDefault;
var enctypeMultipart = "multipart/form-data";

function YImportMisureOL() {
	window.resizeTo(700,500);
	enctypeDefault = document.forms[0].enctype;
	encodingDefault = document.forms[0].encoding;
	document.getElementById('NomeFile').style.background = mCo; //mettiamolo giallo va'
}

function enableMultipart() {
	document.forms[0].enctype = enctypeMultipart;
	document.forms[0].encoding = enctypeMultipart;
}
function disableMultipart() {
	document.forms[0].enctype = enctypeDefault;
	document.forms[0].encoding = encodingDefault;
}

var oldRunActionDirect = runActionDirect;
runActionDirect = function(action, type, classhdr, key, target, toolbar) {
	if (action != 'RUN_BATCH' && action != 'RUN_AND_NEW_BATCH' && action != 'PRINT_BATCH' && action != 'PREVIEW_BATCH')
		disableMultipart();
	else
		enableMultipart();
	oldRunActionDirect(action, type, classhdr, key, target, toolbar);
}
