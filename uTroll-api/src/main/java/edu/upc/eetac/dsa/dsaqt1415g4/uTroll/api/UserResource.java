package edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.codec.digest.DigestUtils;

import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model.User;

@Path("/users")
public class UserResource {
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();

	private final static String GET_USER_BY_USERNAME_QUERY = "select * from users where username=?";

	// Método obtención usuario - cacheable
	@GET
	@Path("/{username}")
	@Produces(MediaType.UTROLL_API_USER)
	public Response getUser(@PathParam("username") String username,
			@Context Request request) {
		CacheControl cc = new CacheControl();

		User user = getUserFromDatabase(username, false);

		// Como en este caso no tenemos un last modified que pueda variar,
		// podemos hacer un md5 entre el usuario (invariable) y el email
		// (variable) para ver si el recurso sigue siendo válido
		String eTagDigest = DigestUtils
				.md5Hex(user.getName() + user.getEmail() + user.getAge() + user.getGroupid() + user.getPoints() + user.getPoints_max());

		EntityTag eTag = new EntityTag(eTagDigest);

		// Verificar si coincide con el etag de la peticion http
		Response.ResponseBuilder rb = request.evaluatePreconditions(eTag);

		if (rb != null) {
			return rb.cacheControl(cc).tag(eTag).build();
		}
		rb = Response.ok(user).cacheControl(cc).tag(eTag); // ok = status 200OK

		return rb.build();
	}

	// El bool password define si se quiere recuperar o no el pwd de la BD
	private User getUserFromDatabase(String username, boolean password) {
		User user = new User();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_USER_BY_USERNAME_QUERY);
			stmt.setString(1, username);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				user.setUsername(rs.getString("username"));
				if (password)
					user.setPassword(rs.getString("userpass"));
				user.setEmail(rs.getString("email"));
				user.setName(rs.getString("name"));
				user.setAge(rs.getInt("age"));
				user.setGroupid(rs.getInt("groupid"));
				user.setPoints(rs.getInt("points"));
				user.setPoints_max(rs.getInt("points_max"));
			} else
				throw new NotFoundException(username + " not found.");
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return user;
	}

	// Para trabajar con parámetros de seguridad
	@Context
	private SecurityContext security;
}
