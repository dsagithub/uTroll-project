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
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model.User;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model.UserCollection;

@Path("/friends")
public class FriendListResource {

	private DataSource ds = DataSourceSPA.getInstance().getDataSource();

	private final static String GET_FRIENDS_BY_USER_QUERY = "select * from friend_list where (friend1=? or friend2=?) and state = 'accepted'";
	private final static String GET_PENDING_FRIENDS_QUERY = "select * from friend_list where friend2=? and state = 'pending' and request = true";
	private final static String GET_FRIENDS_PENDING_BY_USER_QUERY = "select * from friend_list where (friend1=? or friend2=?) and state = 'pending'";
	private final static String GET_FRIEND_BY_FRIENDSHIPID_QUERY = "select * from friend_list where friendshipid=?";
	private final static String GET_FRIEND_STATE = "select state, request from friend_list where friend1 = ? and friend2 = ?";
	private final static String INSERT_FRIENDSHIP = "insert into friend_list (friend1, friend2, state, request) values(?, ?, ?, ?)";
	private final static String UPDATE_FRIENDSHIP = "update friend_list set state = ? where friend1 = ? and friend2 = ?";
	private final static String DELETE_FRIENDSHIP = "delete from friend_list where friend1 = ? and friend2 = ?";
	private final static String UPDATE_FRIENDS_TO_ACCEPT = "update friend_list set state ='accepted' where friendshipid = ?";

	private final static String GET_FRIENDS_BY_USER_QUERY_SINGLE = "select * from friend_list where friend1=? and state = 'accepted'";
	private final static String GET_FRIENDS_PENDING_BY_USER_QUERY_SINGLE = "select * from friend_list where friend1=? and state = 'pending'";

	// Devuelve todos los amigos aceptados
	@GET
	@Produces(MediaType.UTROLL_API_USER_COLLECTION)
	public UserCollection getFriends() {
		UserCollection users = new UserCollection();

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
			stmt.setString(1, security.getUserPrincipal().getName());
			stmt.setString(2, security.getUserPrincipal().getName());

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				User user = new User();
				if (rs.getString("friend1").equals(
						security.getUserPrincipal().getName())) {
					user.setUsername(rs.getString("friend2"));
				} else {
					user.setUsername(rs.getString("friend1"));
				}
				users.addUser(user);
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
		return users;
	}

	// Devuelve todos los amigos aceptados Sin REPETICION
	@GET
	@Path("/getUniqueFriends")
	@Produces(MediaType.UTROLL_API_USER_COLLECTION)
	public UserCollection getUniqueFriends() {
		UserCollection users = new UserCollection();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_FRIENDS_BY_USER_QUERY_SINGLE);
			stmt.setString(1, security.getUserPrincipal().getName());

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				User user = new User();
				if (rs.getString("friend1").equals(
						security.getUserPrincipal().getName())) {
					user.setUsername(rs.getString("friend2"));
				} else {
					user.setUsername(rs.getString("friend1"));
				}
				users.addUser(user);
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
		return users;
	}

	// Devuelve las solicitudes de amistad entrantes pendientes
	@GET
	@Path("/getPendingFriends")
	@Produces(MediaType.UTROLL_API_USER_COLLECTION)
	public UserCollection getPendingFriends() {
		UserCollection users = new UserCollection();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_PENDING_FRIENDS_QUERY);
			stmt.setString(1, security.getUserPrincipal().getName());

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				User user = new User();
				user.setUsername(rs.getString("friend1"));
				users.addUser(user);
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
		return users;
	}

	// Devuelve el estado de la amistad del usuario de "security" con otro
	@GET
	@Path("/{username}")
	@Produces(MediaType.UTROLL_API_FRIENDLIST)
	public FriendList getFriendState(@PathParam("username") String username) {
		FriendList friend = new FriendList();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_FRIEND_STATE);
			stmt.setString(1, security.getUserPrincipal().getName());
			stmt.setString(2, username);

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				friend.setState(rs.getString("state"));
				friend.setRequest(rs.getBoolean("request"));
			} else {
				friend.setState("none");
				friend.setRequest(false);
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
		return friend;
	}

	// Hacer una petición de amistad y ponerla en "pending"
	@POST
	@Path("/addFriend/{username}")
	@Consumes(MediaType.UTROLL_API_FRIENDLIST)
	@Produces(MediaType.UTROLL_API_FRIENDLIST)
	public FriendList addFriend(@PathParam("username") String username) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		FriendList friend = new FriendList();
		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null; // Entrada revertida en la BD
		try {
			stmt = conn.prepareStatement(INSERT_FRIENDSHIP,
					Statement.RETURN_GENERATED_KEYS);
			stmt1 = conn.prepareStatement(INSERT_FRIENDSHIP);

			stmt.setString(1, security.getUserPrincipal().getName());
			stmt.setString(2, username);
			stmt.setString(3, "pending");
			stmt.setBoolean(4, true);
			stmt.executeUpdate();

			stmt1.setString(1, username);
			stmt1.setString(2, security.getUserPrincipal().getName());
			stmt1.setString(3, "pending");
			stmt1.setBoolean(4, false);
			stmt1.executeUpdate();

			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int friendshipid = rs.getInt("friendshipid");
				friend = getFriendFromDatabase(friendshipid);
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
				if (stmt1 != null)
					stmt1.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
		return friend;
	}

	// Aceptar una petición de amistad
	@PUT
	@Path("/acceptFriend/{username}")
	@Consumes(MediaType.UTROLL_API_FRIENDLIST)
	@Produces(MediaType.UTROLL_API_FRIENDLIST)
	public FriendList acceptFriend(@PathParam("username") String username,
			FriendList friend) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null;
		try {
			stmt = conn.prepareStatement(UPDATE_FRIENDSHIP);
			stmt1 = conn.prepareStatement(UPDATE_FRIENDSHIP);

			stmt.setString(1, "accepted");
			stmt.setString(2, security.getUserPrincipal().getName());
			stmt.setString(3, username);

			stmt1.setString(1, "accepted");
			stmt1.setString(3, security.getUserPrincipal().getName());
			stmt1.setString(2, username);

			stmt.executeUpdate();
			stmt1.executeUpdate();
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (stmt1 != null)
					stmt1.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
		return friend;
	}

	// Rechazar una petición de amistad
	@PUT
	@Path("/rejectFriend/{username}")
	@Consumes(MediaType.UTROLL_API_FRIENDLIST)
	@Produces(MediaType.UTROLL_API_FRIENDLIST)
	public FriendList rejectFriend(@PathParam("username") String username,
			FriendList friend) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null;
		try {
			stmt = conn.prepareStatement(DELETE_FRIENDSHIP);
			stmt1 = conn.prepareStatement(DELETE_FRIENDSHIP);

			stmt.setString(1, security.getUserPrincipal().getName());
			stmt.setString(2, username);

			stmt1.setString(2, security.getUserPrincipal().getName());
			stmt1.setString(1, username);

			stmt.executeUpdate();
			stmt1.executeUpdate();
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (stmt1 != null)
					stmt1.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
		FriendList friend1 = new FriendList();
		friend1.setFriendshipid(1);
		return friend1;
	}

	// devuelve las peticiones de amigo que tiene el "username" que debe aceptar
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

	@GET
	@Path("/pendingUnique/{username}/")
	@Produces(MediaType.UTROLL_API_FRIENDLIST_COLLECTION)
	public FriendListCollection getFriendsPendingUnique(
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
			stmt = conn
					.prepareStatement(GET_FRIENDS_PENDING_BY_USER_QUERY_SINGLE);
			stmt.setString(1, username);
			// stmt.setString(2, username);

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

	// poner estado de amistad en aceptado
	@PUT
	@Path("/accept/{friendshipid}")
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
