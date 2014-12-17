package edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model.uTrollRootAPI;

@Path("/")
public class uTrollRootAPIResource {
	@GET
	public uTrollRootAPI getRootAPI() {
		uTrollRootAPI api = new uTrollRootAPI();
		return api;
	}
}