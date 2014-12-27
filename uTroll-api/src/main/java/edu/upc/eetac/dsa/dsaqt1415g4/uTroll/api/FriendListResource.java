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

import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model.FriendList;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model.FriendListCollection;

@Path("/friends")
public class FriendListResource {

	private DataSource ds = DataSourceSPA.getInstance().getDataSource();

	private final static String GET_FRIENDS_BY_USER_QUERY = "select friend1, friend2, state from friend_list where (friend1=? or friend2=?) and state = 'accepted'";

	@GET
	@Path("/{username}")
	@Produces(MediaType.UTROLL_API_FRIENDLIST_COLLECTION)
	public FriendListCollection getFriends(
			@PathParam("username") String username, @Context Request request) {

		FriendListCollection friends = new FriendListCollection();
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_FRIENDS_BY_USER_QUERY);
			stmt.setString(1, username);
			stmt.setString(2, username);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				FriendList friendlist = new FriendList();
				if (rs.getString("friend1").equals(username)) {
					friendlist.setFriend2(rs.getString("friend2"));
				} else {
					friendlist.setFriend2(rs.getString("friend1"));
				}
				friendlist.setState(rs.getString("state"));
				friends.addFriend(friendlist);
			}
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
		return friends;
	}

	// Para trabajar con parámetros de seguridad
	@Context
	private SecurityContext security;

}
