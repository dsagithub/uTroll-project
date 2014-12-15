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

import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model.Group;

@Path("/groups")
public class GroupResource {
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();

	private final static String GET_GROUP_BY_GROUPID_QUERY = "select * from groups where groupid=?";

	// Método obtención grupo por id
	@GET
	@Path("/{groupid}")
	@Produces(MediaType.UTROLL_API_GROUP)
	public Group getUser(@PathParam("groupid") int groupid) {
		Group group = new Group();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_GROUP_BY_GROUPID_QUERY);
			stmt.setInt(1, groupid);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				group.setCreationTimestamp(rs.getLong("creation_timestamp"));
				group.setEndingTimestamp(rs.getLong("ending_timestamp"));
				group.setGroupid(rs.getInt("groupid"));
				group.setGroupname(rs.getString("groupname"));
				group.setPrice(rs.getInt("price"));
				group.setState(rs.getString("state"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
		return group;
	}

//	// El bool password define si se quiere recuperar o no el pwd de la BD
//	private Group getGroupFromDatabase(int groupid) {
//		Group group = new Group();
//		
//		Connection conn = null;
//		try {
//			conn = ds.getConnection();
//		} catch (SQLException e) {
//			throw new ServerErrorException("Could not connect to the database",
//					Response.Status.SERVICE_UNAVAILABLE);
//		}
//		
//		PreparedStatement stmt = null;
//		try {
//			stmt = conn.prepareStatement(GET_USER_BY_USERNAME_QUERY);
//			stmt.setString(1, username);
//
//			ResultSet rs = stmt.executeQuery();
//			if (rs.next()) {
//				user.setUsername(rs.getString("username"));
//				if (password)
//					user.setPassword(rs.getString("userpass"));
//				user.setEmail(rs.getString("email"));
//				user.setName(rs.getString("name"));
//				user.setAge(rs.getInt("age"));
//				user.setGroupid(rs.getInt("groupid"));
//				user.setPoints(rs.getInt("points"));
//				user.setPoints_max(rs.getInt("points_max"));
//			} else
//				throw new NotFoundException(username + " not found.");
//		} catch (SQLException e) {
//			throw new ServerErrorException(e.getMessage(),
//					Response.Status.INTERNAL_SERVER_ERROR);
//		} finally {
//			try {
//				if (stmt != null)
//					stmt.close();
//				conn.close();
//			} catch (SQLException e) {
//			}
//		}
//
//		return user;
//	}

	// Para trabajar con parámetros de seguridad
	@Context
	private SecurityContext security;
}
