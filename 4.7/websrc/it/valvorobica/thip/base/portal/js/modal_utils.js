/**
 * <h2>Open modal to show warning</h2>
 * @author	Daniele Signoroni	03/09/2023
 * @param paragraphId deve essere l'id del paragrafo contenuto nel modal che visualizza il messaggio
 * @param elementToClick e' l' <a href> che apre il modal
 * @param text e' il testo che si vuole visualizzare
 * @description Il modal viene cercato sia nel DOM da dove viene chiamato, sia nel parent.
 */
function openModal(paragraphId,elementToClick,text){
	if($('#'+paragraphId).data() == null || $('#'+paragraphId).data() == undefined){
		if($('#'+paragraphId,parent.document).data() == null || $('#'+paragraphId,parent.document).data() == undefined){
			$('#'+paragraphId,parent.parent.document).html(text);
		}else{
			$('#'+paragraphId,parent.document).html(text);	
		}
	}else{
		$('#'+paragraphId).html(text);	
	}
	elementToClick.click();	
}
