
function YStoricoCMPArticoloOL() {
	let idAnnoFiscale = document.getElementById('IdAnnoFiscale').value;
	let idPeriodoFiscale = document.getElementById('IdPerAnnoFiscale').value;
	const currentDate = new Date();
	const currentYear = currentDate.getFullYear();
	if(currentYear >= idAnnoFiscale && idPeriodoFiscale == "1"){ //da 99 a 1
		 document.getElementById('labelCMPMan').style.display = "revert";
	}else{
		document.getElementById('CostoMedioPonderatoMan').readOnly = "true";
		document.getElementById('CostoMedioPonderatoMan').style.backgroundColor = "rgb(232, 232, 232)";
	}
	
}

function calcolaValFinale(){
	let giacFinale = (document.getElementById('GiacenzaFinale').value).replace(',', '.');
	let cmpManuale = (document.getElementById('CostoMedioPonderatoMan').value).replace(',', '.');
	var floatCmpManuale = parseFloat(cmpManuale);
	if(isNaN(floatCmpManuale) || floatCmpManuale === 0){
		floatCmpManuale = (document.getElementById('CostoMedioPonderato').value).replace(',', '.');
	}
	floatCmpManuale = parseFloat(floatCmpManuale);
	var valFinale = floatCmpManuale * giacFinale;
	var valFinaleString = valFinale.toLocaleString(undefined, { minimumFractionDigits: 2 });
	document.getElementById('ValoreFinale').value = valFinaleString;
}