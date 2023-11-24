// nome del ClassCD collegato alla griglia dei periodi
var gridClassCD = "Periodi";

/*
giorni di durata del singolo periodo generato nel caso in
cui data inizio e data fine calendario siano diversi da
1-1 e 31-12 (dello stesso anno)
*/
var DURATA_PERIODO = 30;
var curGrid;

/*
disabilita l'editing dei periodi tramite
invisibilità dei bottoni
*/
function disabilitaEditingPeriodi() {
	document.getElementById("NewRow_" + gridClassCD).style.visibility = "hidden";
	document.getElementById("CopyRow_" + gridClassCD).style.visibility = "hidden";
	document.getElementById("DeleteRow_" + gridClassCD).style.visibility = "hidden";
	document.getElementById("OkRow_" + gridClassCD).style.visibility = "hidden";
	document.getElementById("CancelRow_" + gridClassCD).style.visibility = "hidden";
	document.getElementById("GeneraPeriodi").disabled = true;
}

/*
disabilita il campo della data di inizio calendario settando
l'attributo readOnly a true e cambiando il colore in grigino.
Non si usa il disabled perchè toglie l'attributo dalla submit.
*/
function disabilitaDataInizio() {
	eval("document.forms[0]." + idFromName["DataInizio"]).readOnly = true;
	eval("document.forms[0]." + idFromName["DataInizio"]).style.color = "#808080";
}

/*
disabilita il campo della data di fine calendario settando
l'attributo readOnly a true e cambiando il colore in grigino.
Non si usa il disabled perchè toglie l'attributo dalla submit.
*/
function disabilitaDataFine() {
	eval("document.forms[0]." + idFromName["DataFine"]).readOnly = true;
	eval("document.forms[0]." + idFromName["DataFine"]).style.color = "#808080";
}


/*
chiamata dalla funzione generaPeriodiOnClick()
inizializza il riferimento alla griglia dei periodi
*/
function initializeGridReference() {
	curGrid = eval(editGrid[gridClassCD]);
}

/*
mi visualizza l'alert recuperando il messaggio messo
come variabile javascript dal formModifier della gestione
*/
function showAlert(messageIndex) {
	alert(eval("msg"+padout(messageIndex)));
}

/*
mi visualizza la confirm tornando il relativo valore 
recuperando il messaggio messo come variabile javascript 
dal formModifier della gestione
*/
function showConfirm(msg) {
	return confirm(eval("msg"+padout(msg)));
}

/*
presa una data in formato array YMD
restituisce una stringa come da locale
*/
function getDateString(dateArray) {
	var posD = DateTimeType.findComponentDateTimePosition(dateMask, 'D');
	var posM = DateTimeType.findComponentDateTimePosition(dateMask, 'M');
	var posY = DateTimeType.findComponentDateTimePosition(dateMask, 'Y');

	var tmpDateArray = new Array(3);
	tmpDateArray[posY] = dateArray[0];
	tmpDateArray[posM] = dateArray[1];
	tmpDateArray[posD] = dateArray[2];

	var strDate = padout(tmpDateArray[0]) + datSep +  padout(tmpDateArray[1]) + datSep + padout(tmpDateArray[2]);
	return strDate;	
}

/*
presa una data in formato stringa come da locale
ritorna un array con tre elementi interi: anno mese giorno
*/
function getDateArray(dt) {

	var posD = DateTimeType.findComponentDateTimePosition(dateMask, 'D');
	var posM = DateTimeType.findComponentDateTimePosition(dateMask, 'M');
	var posY = DateTimeType.findComponentDateTimePosition(dateMask, 'Y');
    var tmp = dt.split(datSep);

	var tmpD = clearParseInt(tmp[posD]);
	var tmpM = clearParseInt(tmp[posM]);
	var tmpY = clearParseInt(tmp[posY]);
	
	return new Array(tmpY, tmpM, tmpD);
}

function clearParseInt(str) {
//Fix 11094 PM Inizio
/*	if (str == "08")
		return 8;
	if (str == "09")
		return 9;
	return parseInt(str);
*/
	return parseInt(str,10);
//Fix 11094 PM Fine
}
/*
funzione collegata al click del bottone di generazione periodi,
fatti i necessari controlli chiama la relativa funzione di
generazione (sia essa di anno solare o di anno Atipico)
*/
function generaPeriodiOnClick() {
	initializeGridReference();
	
	var totRows = 0;
	for (var idx = 0; idx < curGrid.rows.length; idx++)
		if (curGrid.rows[idx].status != "DELETE")
			totRows++;

	if (totRows > 0) {
		showAlert(1);
		return false;
	}
	
	var objectDataInizio = eval("document.forms[0]." + idFromName["DataInizio"]);
	var objectDataFine = eval("document.forms[0]." + idFromName["DataFine"]);
	var dataInizio = objectDataInizio.value;
	var dataFine = objectDataFine.value;
	var dateTypeDataInizio = new DateType(objectDataInizio, false);
	var dateTypeDataFine = new DateType(objectDataFine, false);

	if (!dateTypeDataInizio.validate(dataInizio)) {
		showAlert(3);
		return false;
	}

	if (!dateTypeDataFine.validate(dataFine)) {
		showAlert(4);
		return false;
	}

	if (dataInizio == "" || dataFine == "") {
		showAlert(5);
		return false;
	}
	
    var arr = getDateArray(dataInizio);

	
	var annoDI = arr[0];
	var meseDI = arr[1];
	var giornoDI = arr[2];

    arr = getDateArray(dataFine);

	var annoDF = arr[0];
	var meseDF = arr[1];
	var giornoDF = arr[2];
	

	if (giornoDI == 1 && meseDI == 1 && giornoDF == 31 && meseDF == 12 && annoDI == annoDF) {
		if (!showConfirm(6))
			return false;
		if (generaPeriodiAnnoSolare(annoDI)) {
			showAlert(2);
			return true;
		}
	}
	else {
		if (!showConfirm(6))
			return false;
		if (generaPeriodiAnnoAtipico(annoDI, meseDI, giornoDI, annoDF, meseDF, giornoDF)) {
			showAlert(2);
			return true;
		}
	}
}
//RA: modificare la formattazione del data  seguendo la lingua
//Fix 10872 inizio
function creaStrDate(day,month,year)
{
	var tmp = new Array(3);
	var y = DateTimeType.findComponentDateTimePosition(dateMask, "Y");
	var m = DateTimeType.findComponentDateTimePosition(dateMask, "M");
	var d = DateTimeType.findComponentDateTimePosition(dateMask, "D");
	tmp[y] = formatYear(year);
	tmp[m] = addZeroCharacter(month, 2, true);
	tmp[d] = addZeroCharacter(day, 2, true);
	return tmp[0] + datSep + tmp[1] + datSep + tmp[2];
}
//Fix 10872 fine
/*
funzione che si occupa di generare e inserire nella relativa
editGrid i periodi nel caso l'intervallo del calendario fiscale
sia dal primo gennaio al trentuno dicembre.
vengono generati dodici periodi uno per ogni mese solare.
*/
function generaPeriodiAnnoSolare(anno) {

	var dIni = new Array(12);
	//Fix 10872 inizio
	/*dIni[0] = "01/01/";
	dIni[1] = "01/02/";
	dIni[2] = "01/03/";
	dIni[3] = "01/04/";
	dIni[4] = "01/05/";
	dIni[5] = "01/06/";
	dIni[6] = "01/07/";
	dIni[7] = "01/08/";
	dIni[8] = "01/09/";
	dIni[9] = "01/10/";
	dIni[10] = "01/11/";
	dIni[11] = "01/12/";*/
	dIni[0] = creaStrDate("01","01",anno);
	dIni[1] = creaStrDate("01","02",anno);
	dIni[2] = creaStrDate("01","03",anno);
	dIni[3] = creaStrDate("01","04",anno);
	dIni[4] = creaStrDate("01","05",anno);
	dIni[5] = creaStrDate("01","06",anno);
	dIni[6] = creaStrDate("01","07",anno);
	dIni[7] = creaStrDate("01","08",anno);
	dIni[8] = creaStrDate("01","09",anno);
	dIni[9] = creaStrDate("01","10",anno);
	dIni[10] = creaStrDate("01","11",anno);
	dIni[11] = creaStrDate("01","12",anno);
	
	var dFin = new Array(12);
	/*dFin[0] = "31/01/";
	dFin[1] = dayInFebruary(anno) + "/02/";
	dFin[2] = "31/03/";
	dFin[3] = "30/04/";
	dFin[4] = "31/05/";
	dFin[5] = "30/06/";
	dFin[6] = "31/07/";
	dFin[7] = "31/08/";
	dFin[8] = "30/09/";
	dFin[9] = "31/10/";
	dFin[10] = "30/11/";
	dFin[11] = "31/12/";*/
	dFin[0] = creaStrDate("31","01",anno);
	dFin[1] = creaStrDate(dayInFebruary(anno),"02",anno);
	dFin[2] = creaStrDate("31","03",anno);
	dFin[3] = creaStrDate("30","04",anno);
	dFin[4] = creaStrDate("31","05",anno);
	dFin[5] = creaStrDate("30","06",anno);
	dFin[6] = creaStrDate("31","07",anno);
	dFin[7] = creaStrDate("31","08",anno);
	dFin[8] = creaStrDate("30","09",anno);
	dFin[9] = creaStrDate("31","10",anno);
	dFin[10] = creaStrDate("30","11",anno);
	dFin[11] = creaStrDate("31","12",anno);
	//Fix 10872 Fine

	for (var i = 0; i<12; i++) {
		//Fix 10872 inizio
		//var inizio = dIni[i] + anno;
		//var fine = dFin[i] + anno;
		var inizio = dIni[i];
		var fine = dFin[i];
		//Fix 10872 fine
		addPeriodo(inizio, fine);
	}

	return true;
	
}

/*
funzione che si occupa di generare e inserire nella relativa
editGrid i periodi nel caso l'intervallo del calendario fiscale
sia atipico, ovvero non sia dal primo gennaio al trentuno dicembre.
vengono generati n periodi da trenta giorni (con n che va da 0)
più un periodo a conguaglio per i giorni eccedenti.
*/
function generaPeriodiAnnoAtipico(annoI, meseI, giornoI, annoF, meseF, giornoF) {

	var dataI = new Date(annoI, meseI - 1, giornoI);
	var dataF = new Date(annoF, meseF - 1, giornoF);
	
	if (dataF < dataI) {
		showAlert(7);
		return false;
	}

	finito = false;
	
	while (!finito) {
		
		// Federico Crosa fix 2597: modificata la rappresentazione dell'anno.		
		var YI = dataI.getFullYear();
		
		var MI = dataI.getMonth() + 1;
		var DI = dataI.getDate();
	
		var arrInc = addDays(YI,MI,DI,DURATA_PERIODO-1);
	
		var incY = arrInc[0];
		var incM = arrInc[1];
		var incD = arrInc[2];
	
		var dataIncrementata = new Date(incY, incM - 1, incD);
		
		if (dataIncrementata > dataF) {
			finito = true;
			
			//ora inserisco il tappo.

			// Federico Crosa fix 2597: modificata la rappresentazione dell'anno.
			var YF = dataF.getFullYear();
			
			var MF = dataF.getMonth() + 1;
			var DF = dataF.getDate();
			
			var strDataInizio = getDateString(new Array(YI,MI,DI));
			var strDataFine = getDateString(new Array(YF,MF,DF));
			addPeriodo(strDataInizio, strDataFine);
			
		}
		else {
			var strDataInizio = getDateString(new Array(YI,MI,DI));
			var strDataFine = getDateString(arrInc);
			addPeriodo(strDataInizio, strDataFine);
			var arrNewDataI = addDays(incY, incM, incD, 1);
			dataI = new Date(arrNewDataI[0], arrNewDataI[1] - 1, arrNewDataI[2]);
		}
		
	}

    
	curGrid.firstRow = 0;
	editGridLoadTable(gridClassCD);
   	editGridSelectRow(gridClassCD, -1, -1);
	
	return true;
		
}

/*
funzione che aggiunge un periodo alla griglia
(con data inizio e data fine)
*/
function addPeriodo(dataInizio, dataFine) {
	if (curGrid.isInEdit)
		return;
	if (curGrid.gridType == "EDIT")
		editGridCloseEditComponent(gridClassCD, true, true);
			
	if (curGrid.gridType != "IND") {
		var copyData = new Array();
		for(i = 0; i < curGrid.columns.length; i++)
			copyData[i] = "";
		editGridInsertFatherValues(gridClassCD, copyData);
		
		curGrid.columns[1].doSubmit = true;
		curGrid.columns[2].doSubmit = true;
		curGrid.columns[9].doSubmit = true;
		curGrid.columns[12].doSubmit = true;

		copyData[1] = dataInizio;
		copyData[2] = dataFine;
		copyData[9] = "N";
		copyData[12] = "0";
		
		var newRow = new gridRow(copyData);
		if (curGrid.gridType == "EDIT")
			newRow.status = "NEW";
		else	
			newRow.status = "NEWING";
		curGrid.rows[curGrid.rows.length] = newRow;
		editGridShowLastRow(gridClassCD);
	}

}


/*
Copiati da internet. verificati solamente per un utilizzo
non troppo particolare - PJ
*/

/*
insieme di metodi di utilità che permettono di sommare e sottrarre
giorni ad una data.
Necessari per la generazione dei periodi legati all'anno Atipico.
*/

function padout(number) { 
	return (number < 10) ? '0' + number : number;
}

function leapYear(year) {
    if ((year/4)   != Math.floor(year/4))   return false;
    if ((year/100) != Math.floor(year/100)) return true;
    if ((year/400) != Math.floor(year/400)) return false;
    return true;
}

function daysInYear(year) { 
	if (leapYear(year)) return 366; else return 365; 
}

function addDays(year,month,day,addition) {
	var accumulate = new Array(0, 0, 31, 59, 90,120,151,181,212,243,273,304,334);
	var accumulateLY = new Array(0, 0, 31, 60, 91,121,152,182,213,244,274,305,335);

    if (leapYear(year)) 
    	var number = day + accumulateLY[month] + addition;
    else                
    	var number = day + accumulate[month] + addition;

    var days = daysInYear(year);
   
    while (number > days) {
        number -= days;
        days = daysInYear(++year);
    }

    while (number < 1) {
        days = daysInYear(--year);
        number += days;
    }

    month = 1;

    if (leapYear(year)) {
        while (number > accumulateLY[month]) { month++; }
        day = number - accumulateLY[--month];
    }
    else {
        while (number > accumulate[month]) { month++; }
        day = number - accumulate[--month];
    }

	return new Array(year, month, day);
}
