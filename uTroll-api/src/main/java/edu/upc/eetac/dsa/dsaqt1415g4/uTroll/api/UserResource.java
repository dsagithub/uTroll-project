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
import javax.ws.rs.QueryParam;
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

import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model.Comment;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model.Group;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model.GroupCollection;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model.User;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model.UserCollection;

@Path("/users")
public class UserResource {
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();

	private final static String GET_USER_BY_USERNAME_QUERY = "select * from users where username=?";
	private final static String CREATE_USER_QUERY = "insert into users values (?, MD5(?), ?, ?, ?, 30, 30, false, 0, 0, 'none')";
	private final static String CREATE_USER_ROLE_QUERY = "insert into user_roles values (?, 'registered')";
	private final static String VALIDATE_USERNAME_QUERY = "select username from users where username=?";
	private final static String VALIDATE_GROUP_BELONGING_QUERY = "select groupid from users where username = ?";
	private final static String VALIDATE_GROUP_IS_OPEN_QUERY = "select state from groups where groupid = ?";
	private final static String UPDATE_USER_QUERY = "update users set name = ifnull(?, name), email = ifnull(?, email), age = ifnull(?, age) where username = ?";
	private final static String UPDATE_USER_GROUP_QUERY = "update users set groupid = ? where username = ?";
	private final static String UPDATE_USER_POINTS_QUERY = "update users set points = ?, points_max = greatest(points_max, ?) where username = ?";
	private final static String GET_USERS_IN_A_GROUP_QUERY = "select * from users where groupid = ?";
	private final static String GET_USERS_BY_USERNAME_QUERY = "select * from users where username like ?";
	private final static String GET_POINTS_GROUP_QUERY = "select price from groups where groupid = ?";
	private final static String UPDATE_VOTED_USER_QUERY = "update users set votedBy = (votedBy + 1) where username = ?";
	private final static String UPDATE_VOTER_USER_QUERY = "update users set vote = ? where username = ?";
	private final static String GET_USERS_RANKING_QUERY = "select username, points from users order by points desc";

	// private final static String GET_POINTS_USER_QUERY =
	// "select points from users where username = ?";

	// Método obtención usuario - cacheable
	@GET
	@Path("/byUsername/{username}")
	@Produces(MediaType.UTROLL_API_USER)
	public Response getUser(@PathParam("username") String username,
			@Context Request request) {
		CacheControl cc = new CacheControl();

		User user = getUserFromDatabase(username, false);

		String eTagDigest = DigestUtils.md5Hex(user.getName() + user.getEmail()
				+ user.getAge() + user.getGroupid() + user.getPoints()
				+ user.getPoints_max());

		EntityTag eTag = new EntityTag(eTagDigest);

		Response.ResponseBuilder rb = request.evaluatePreconditions(eTag);

		if (rb != null) {
			return rb.cacheControl(cc).tag(eTag).build();
		}
		rb = Response.ok(user).cacheControl(cc).tag(eTag);

		return rb.build();
	}

	// Obtener usuarios de un grupo
	@GET
	@Path("/usersInGroup/{groupid}")
	@Produces(MediaType.UTROLL_API_USER_COLLECTION)
	public UserCollection getUsersInGroup(@PathParam("groupid") int groupid) {
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
			stmt = conn.prepareStatement(GET_USERS_IN_A_GROUP_QUERY);

			stmt.setInt(1, groupid);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				User user = new User();
				user.setUsername(rs.getString("username"));
				user.setEmail(rs.getString("email"));
				user.setName(rs.getString("name"));
				user.setAge(rs.getInt("age"));
				user.setGroupid(rs.getInt("groupid"));
				user.setPoints(rs.getInt("points"));
				user.setPoints_max(rs.getInt("points_max"));
				user.setTroll(rs.getBoolean("isTroll"));
				user.setVotedBy(rs.getInt("votedBy"));
				user.setVote(rs.getString("vote"));

				users.addUser(user);
			}
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				conn.close();
			} catch (SQLException e) {
			}
		}

		return users;
	}

	@GET
	@Path("/ranking")
	@Produces(MediaType.UTROLL_API_USER_COLLECTION)
	public UserCollection getUsersRanking() {
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
			stmt = conn.prepareStatement(GET_USERS_RANKING_QUERY);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				User user = new User();
				user.setUsername(rs.getString("username"));
				user.setPoints(rs.getInt("points"));

				users.addUser(user);
			}
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				conn.close();
			} catch (SQLException e) {
			}
		}

		return users;
	}

	// Obtener usuarios de un grupo
	@GET
	@Produces(MediaType.UTROLL_API_USER_COLLECTION)
	public UserCollection getUsersByUsername(
			@QueryParam("username") String username) {
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
			stmt = conn.prepareStatement(GET_USERS_BY_USERNAME_QUERY);

			stmt.setString(1, "%" + username + "%");

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				User user = new User();
				user.setUsername(rs.getString("username"));
				user.setEmail(rs.getString("email"));
				user.setName(rs.getString("name"));
				user.setAge(rs.getInt("age"));
				user.setGroupid(rs.getInt("groupid"));
				user.setPoints(rs.getInt("points"));
				user.setPoints_max(rs.getInt("points_max"));
				user.setTroll(rs.getBoolean("isTroll"));
				user.setVotedBy(rs.getInt("votedBy"));
				user.setVote(rs.getString("vote"));

				users.addUser(user);
			}
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				conn.close();
			} catch (SQLException e) {
			}
		}

		return users;
	}

	// Crear un usuario
	@POST
	@Consumes(MediaType.UTROLL_API_USER)
	@Produces(MediaType.UTROLL_API_USER)
	public User createUser(User user) {
		int valid = validateUser(user.getUsername());
		if (valid == 0) { // El usuario ya esta en uso
			User user1 = new User();
			user1.setUsername("exists");
			user1.setPoints(-1);
			return user1;
		}
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
		// System.out.println("");
		PreparedStatement stmtInsertUserIntoUsers = null;
		PreparedStatement stmtInsertUserIntoUserRoles = null;
		try {
			// No autocommit para asegurar adición a las 2 tablas
			conn.setAutoCommit(false);
			stmtInsertUserIntoUsers = conn.prepareStatement(CREATE_USER_QUERY);
			stmtInsertUserIntoUserRoles = conn
					.prepareStatement(CREATE_USER_ROLE_QUERY);

			stmtInsertUserIntoUsers.setString(1, user.getUsername());
			stmtInsertUserIntoUsers.setString(2, user.getPassword());
			stmtInsertUserIntoUsers.setString(3, user.getName());
			stmtInsertUserIntoUsers.setString(4, user.getEmail());
			stmtInsertUserIntoUsers.setInt(5, user.getAge());
			stmtInsertUserIntoUsers.executeUpdate(); // MODIFICAR CON PUNTOS
														// INICIALES USUARIO

			stmtInsertUserIntoUserRoles.setString(1, user.getUsername());
			stmtInsertUserIntoUserRoles.executeUpdate();

			System.out.println(user.toString());

			// Hasta aquí está ejecutado pero no sobre la BD
			// El commit escribe los dos registros definitivamente en la BD
			conn.commit();

		} catch (SQLException e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (SQLException e1) {
				}
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				// Para poder reutilizar la cnx, activamos el autocommit porque
				// sino no se llegará a cerrar y será inútil en el pool
				conn.setAutoCommit(true);
				conn.close();
			} catch (SQLException e) {
			}
		}
		user.setPassword(null);
		return user;
	}

	// Modificar datos de usuario
	@PUT
	@Consumes(MediaType.UTROLL_API_USER)
	@Produces(MediaType.UTROLL_API_USER)
	public User updateUser(User user) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			User user1 = getUserFromDatabase(security.getUserPrincipal().getName(), false);
			
			stmt = conn.prepareStatement(UPDATE_USER_QUERY);
			stmt.setString(1, user.getName());
			stmt.setString(2, user.getEmail());
			if (user.getAge() == 0)
				stmt.setInt(3, user1.getAge());
			else
				stmt.setInt(3, user.getAge());
			stmt.setString(4, security.getUserPrincipal().getName());

			int rows = stmt.executeUpdate();
			if (rows == 1)
				user = getUserFromDatabase(security.getUserPrincipal()
						.getName(), false);
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

	// Unirse a un grupo
	@PUT
	@Path("/joingroup/{groupid}")
	@Produces(MediaType.UTROLL_API_USER)
	public User joinGroup(@PathParam("groupid") int groupid) {
		User user = new User();
		// validateBelongingToNoGroup(security.getUserPrincipal().getName());

		int myPresentGroup = validateBelongingToNoGroup(security
				.getUserPrincipal().getName());

		if (myPresentGroup != 0) {
			// throw new ServerErrorException("You already belong to group " +
			// myPresentGroup, Response.Status.INTERNAL_SERVER_ERROR);
		}

		validateGroupIsOpen(groupid);

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		try {
			stmt = conn.prepareStatement(UPDATE_USER_GROUP_QUERY);
			stmt.setInt(1, groupid);
			stmt.setString(2, security.getUserPrincipal().getName());

			int rows = stmt.executeUpdate();
			if (rows == 1)
				user = getUserFromDatabase(security.getUserPrincipal()
						.getName(), false);

			int points_group = 0;
			stmt1 = conn.prepareStatement(GET_POINTS_GROUP_QUERY);
			stmt1.setInt(1, groupid);

			ResultSet rs = stmt1.executeQuery();
			if (rs.next())
				points_group = rs.getInt("price");

			int points_user = user.getPoints() - points_group;
			stmt2 = conn.prepareStatement(UPDATE_USER_POINTS_QUERY);
			stmt2.setInt(1, points_user);
			stmt2.setInt(2, points_user);
			stmt2.setString(3, security.getUserPrincipal().getName());

			int rows2 = stmt2.executeUpdate();
			if (rows2 == 1)
				user = getUserFromDatabase(security.getUserPrincipal()
						.getName(), false);

		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (stmt1 != null)
					stmt1.close();
				if (stmt2 != null)
					stmt2.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return user;
	}

	// Votar a un usuario
	@PUT
	@Path("/vote/{username}")
	@Produces(MediaType.UTROLL_API_USER)
	public User voteUser(@PathParam("username") String username) {
		User user = new User();

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
			stmt = conn.prepareStatement(UPDATE_VOTED_USER_QUERY);
			stmt.setString(1, username);
			stmt.executeUpdate();

			stmt1 = conn.prepareStatement(UPDATE_VOTER_USER_QUERY);
			stmt1.setString(1, username);
			stmt1.setString(2, security.getUserPrincipal().getName());
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

		return user;
	}

	// Comprobar usuario y contraseña usados
	@Path("/login")
	@POST
	@Produces(MediaType.UTROLL_API_USER)
	@Consumes(MediaType.UTROLL_API_USER)
	public User checkLogin(User user) {
		if (user.getUsername() == null || user.getPassword() == null)
			throw new BadRequestException(
					"username and password cannot be null.");

		String pwdDigest = DigestUtils.md5Hex(user.getPassword());
		String storedPwd = getUserFromDatabase(user.getUsername(), true)
				.getPassword();

		user.setLoginSuccessful(pwdDigest.equals(storedPwd));
		user.setPassword(null);
		return user;
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
					user.setPassword(rs.getString("password"));
				user.setEmail(rs.getString("email"));
				user.setName(rs.getString("name"));
				user.setAge(rs.getInt("age"));
				user.setGroupid(rs.getInt("groupid"));
				user.setPoints(rs.getInt("points"));
				user.setPoints_max(rs.getInt("points_max"));
				user.setTroll(rs.getBoolean("isTroll"));
				user.setVotedBy(rs.getInt("votedBy"));
				user.setVote(rs.getString("vote"));
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

	// Método para validar si el username está libre
	private int validateUser(String username) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(VALIDATE_USERNAME_QUERY);
			stmt.setString(1, username);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return 0; // Username ocupado
			} else
				return 1; // Username libre
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
	}

	// Método para validar que no estás metido en ningún grupo
	private int validateBelongingToNoGroup(String username) {
		Connection conn = null;
		int groupid = 0;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(VALIDATE_GROUP_BELONGING_QUERY);
			stmt.setString(1, username);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				groupid = rs.getInt("groupid");

				if (groupid != 0) {
					throw new BadRequestException(
							"You already belong to a group");
				}
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
		return groupid;
	}

	// Método para validar que el grupo está todavía abierto
	private void validateGroupIsOpen(int groupid) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(VALIDATE_GROUP_IS_OPEN_QUERY);
			stmt.setInt(1, groupid);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				String state = rs.getString("state");

				if (state.equals("open")) {

				} else {
					throw new BadRequestException("The group is not open");
				}
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
	}

	// Para trabajar con parámetros de seguridad
	@Context
	private SecurityContext security;
}
