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

import com.mysql.jdbc.Statement;

import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model.FriendList;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model.FriendListCollection;

@Path("/friends")
public class FriendListResource {

	private DataSource ds = DataSourceSPA.getInstance().getDataSource();

	private final static String GET_FRIENDS_BY_USER_QUERY = "select * from friend_list where (friend1=? or friend2=?) and state = 'accepted'";
	private final static String GET_FRIENDS_PENDING_BY_USER_QUERY = "select * from friend_list where (friend1=? or friend2=?) and state = 'pending'";
	private final static String GET_FRIEND_BY_FRIENDSHIPID_QUERY = "select * from friend_list where friendshipid=?";
	private final static String INSERT_FRIENDS_PENDING = "insert into friend_list values(NULL, ?, ?, 'pending')";
	private final static String UPDATE_FRIENDS_TO_ACCEPT = "update friend_list set state ='accepted' where friendshipid = ?";

	// devuelve todos los amigos aceptados
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
				friendlist.setFriendshipid(rs.getInt("friendshipid"));
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

	// devuelve las peticiones de amigo que tiene el "username" que debe aceptar
	// o no
	@GET
	@Path("/pending/{username}/")
	@Produces(MediaType.UTROLL_API_FRIENDLIST_COLLECTION)
	public FriendListCollection getFriendsPending(
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
			stmt = conn.prepareStatement(GET_FRIENDS_PENDING_BY_USER_QUERY);
			stmt.setString(1, username);
			stmt.setString(2, username);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				FriendList friendlist = new FriendList();
				friendlist.setFriendshipid(rs.getInt("friendshipid"));
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

	// hacer una peticion de amistad y ponerla en "pending"
	@POST
	@Consumes(MediaType.UTROLL_API_FRIENDLIST)
	@Produces(MediaType.UTROLL_API_FRIENDLIST)
	public FriendList createFriendPending(FriendList friendlist) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(INSERT_FRIENDS_PENDING,
					Statement.RETURN_GENERATED_KEYS);

			// stmt.setString(1, security.getUserPrincipal().getName());
			stmt.setString(1, friendlist.getFriend1());
			stmt.setString(2, friendlist.getFriend2());

			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int friendshipid = rs.getInt("friendshipid");
				friendlist = getFriendFromDatabase(friendshipid);
			} else {
				// Something has failed...
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

		return friendlist;

	}

	// poner estado de amistad en aceptado
	@PUT
	@Path("/{friendshipid")
	@Consumes(MediaType.UTROLL_API_FRIENDLIST)
	@Produces(MediaType.UTROLL_API_FRIENDLIST)
	public FriendList updateFriendList(
			@PathParam("friendshipid") int friendshipid, FriendList friendlist) {
		
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(UPDATE_FRIENDS_TO_ACCEPT);
			stmt.setInt(1, friendlist.getFriendshipid());

			int rows = stmt.executeUpdate();
			if (rows == 1)
				friendlist = getFriendFromDatabase(friendshipid);
			else {
				throw new NotFoundException("Comment not found");
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
		return null;
	}

	private FriendList getFriendFromDatabase(int friendshipid) {
		FriendList friendlist = new FriendList();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_FRIEND_BY_FRIENDSHIPID_QUERY);
			stmt.setInt(1, friendshipid);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				friendlist.setFriendshipid(rs.getInt("friendshipid"));
				friendlist.setFriend1(rs.getString("friend1"));
				friendlist.setFriend2(rs.getString("friend2"));
				friendlist.setState(rs.getString("state"));
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

		return friendlist;
	}

	
	// Para trabajar con parámetros de seguridad
	@Context
	private SecurityContext security;

}
