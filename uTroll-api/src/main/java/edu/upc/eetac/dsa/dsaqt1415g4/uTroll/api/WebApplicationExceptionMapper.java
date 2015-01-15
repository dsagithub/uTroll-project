package edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model.uTrollError;

// Procesa el formato del error recibido
@Provider
public class WebApplicationExceptionMapper implements
		ExceptionMapper<WebApplicationException> {
	@Override
	public Response toResponse(WebApplicationException exception) {
		uTrollError error = new uTrollError(
				exception.getResponse().getStatus(), exception.getMessage());
		return Response.status(error.getStatus()).entity(error)
				.type(MediaType.UTROLL_API_ERROR).build();
	}

}
