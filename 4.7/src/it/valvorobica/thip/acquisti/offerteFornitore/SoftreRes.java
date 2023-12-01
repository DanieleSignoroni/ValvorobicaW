package it.valvorobica.thip.acquisti.offerteFornitore;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.thera.thermfw.rs.BaseResource;

@Path("/softre")
//@PermitAll
public class SoftreRes extends BaseResource {

	@POST
	@Path("/esti/creaordine")
	public Response test(String body) {
		Response response = null;
		String username = null;
		String bodyRes = "FUNZIONAAA!!";
		response = buildResponse(Status.CREATED, bodyRes);
		return response;
	}
}
