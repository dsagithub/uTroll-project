package edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

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
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model.CommentCollection;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model.Group;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model.GroupCollection;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model.User;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model.UserCollection;

@Path("/groups")
public class GroupResource {
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();

	private final static String GET_GROUP_BY_GROUPID_QUERY = "select * from groups where groupid=?";
	private final static String GET_GROUPS_QUERY = "select * from groups";
	private final static String CREATE_GROUP_QUERY = "insert into groups (groupname, price, ending_timestamp, closing_timestamp, creator, troll, state) values(?, ?, ?, ?, ?, ?, ?)";
	private final static String UPDATE_GROUP_QUERY = "update groups set state = ? where groupid = ?";
	private final static String VALIDATE_CREATOR = "select groupid from users where username = ?";
	private final static String UPDATE_USER_GROUP_QUERY = "update users set groupid = ? where username = ?";
	private final static String VALIDATE_USER = "select groupname from groups where groupid = ? and creator = ?";
	private final static String GET_USERS_IN_A_GROUP_QUERY = "select username from users where groupid = ?";
	private final static String UPDATE_USER_TROLL_QUERY = "update users set isTroll = ? where username = ?";
	private final static String UPDATE_GROUP_TROLL_QUERY = "update groups set troll = ? where groupid = ?";
	private final static String UPDATE_USER_ON_GROUP_CLOSURE_QUERY = "update users set isTroll = false, groupid = 0  where username = ?";
	private final static String CREATE_EVENT = "create event evento? on schedule at ? do update groups set state = ? where groupid = ?";
	private final static String CREATE_EVENT_CLOSURE = "create event eventocierre? on schedule at ? do update groups set state = ? where groupid = ?";


	// Obtener lista de grupos
	@GET
	@Produces(MediaType.UTROLL_API_GROUP_COLLECTION)
	public GroupCollection getGroups() {
		GroupCollection groups = new GroupCollection();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_GROUPS_QUERY);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Group group = new Group();
				group.setCreationTimestamp(rs.getLong("creation_timestamp"));
				// group.setEndingTimestamp(rs.getLong("ending_timestamp"));
				group.setGroupid(rs.getInt("groupid"));
				group.setGroupname(rs.getString("groupname"));
				group.setPrice(rs.getInt("price"));
				group.setState(rs.getString("state"));
				group.setCreator(rs.getString("creator"));
				group.setTroll(rs.getString("troll"));

				groups.addGroup(group);
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

		return groups;
	}

	// Método obtención grupo por id
	@GET
	@Path("/{groupid}")
	@Produces(MediaType.UTROLL_API_GROUP)
	public Group getGroup(@PathParam("groupid") int groupid) {
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
				// group.setEndingTimestamp(rs.getLong("ending_timestamp"));
				group.setGroupid(rs.getInt("groupid"));
				group.setGroupname(rs.getString("groupname"));
				group.setPrice(rs.getInt("price"));
				group.setState(rs.getString("state"));
				group.setCreator(rs.getString("creator"));
				group.setTroll(rs.getString("troll"));
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

	// Crear un nuevo grupo
	@POST
	@Consumes(MediaType.UTROLL_API_GROUP)
	@Produces(MediaType.UTROLL_API_GROUP)
	public Group createGroup(Group group) {
		validateCreator(); // Comprobar que no pertenezco ya a un grupo

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		String fecha = group.getEndingTimestamp();
		String fechacierre = group.getClosingTimestamp();
		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		try {
			stmt = conn.prepareStatement(CREATE_GROUP_QUERY,
					Statement.RETURN_GENERATED_KEYS);

			stmt.setString(1, group.getGroupname());
			stmt.setInt(2, group.getPrice());
			// stmt.setLong(3, group.getCreationTimestamp());
			// stmt.setLong(3, group.getEndingTimestamp());
			stmt.setString(3, group.getEndingTimestamp());
			stmt.setString(4, group.getClosingTimestamp());
			stmt.setString(5, security.getUserPrincipal().getName());
			stmt.setString(6, "No Troll");
			stmt.setString(7, "open");

			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int groupid = rs.getInt(1);

				group = getGroupFromDatabase(groupid);
			} else {
				// Something has failed...
			}

			// Crear eventos para cambiar el estado del grupo
			stmt1 = conn.prepareStatement(CREATE_EVENT);
			stmt1.setInt(1, group.getGroupid());
			stmt1.setString(2, fecha);
			stmt1.setString(3, "active");
			stmt1.setInt(4, group.getGroupid());
			stmt1.executeUpdate();
			
			stmt2 = conn.prepareStatement(CREATE_EVENT_CLOSURE);
			stmt2.setInt(1, group.getGroupid());
			stmt2.setString(2, fechacierre);
			stmt2.setString(3, "closed");
			stmt2.setInt(4, group.getGroupid());
			stmt2.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
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

		updateUserGroup(group.getGroupid());

		return group;
	}

	// Modificar el estado de un grupo
	@PUT
	@Path("/{groupid}")
	@Consumes(MediaType.UTROLL_API_GROUP)
	@Produces(MediaType.UTROLL_API_GROUP)
	public Group updateGroup(@PathParam("groupid") int groupid, Group group) {
		validateUser(groupid);

		validateUpdateGroup(group, groupid);

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(UPDATE_GROUP_QUERY);
			stmt.setString(1, group.getState());
			stmt.setInt(2, groupid);

			int rows = stmt.executeUpdate();
			if (rows == 1)
				group = getGroupFromDatabase(groupid);
			else {
				throw new NotFoundException("Group not found");
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

		return group;
	}

	// Recuperar grupo de la base de datos por su ID
	private Group getGroupFromDatabase(int groupid) {
		Group group = new Group();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_GROUP_BY_GROUPID_QUERY);
			stmt.setInt(1, groupid);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				group.setCreationTimestamp(rs.getLong("creation_timestamp"));
				// group.setEndingTimestamp(rs.getLong("ending_timestamp"));
				group.setGroupid(rs.getInt("groupid"));
				group.setGroupname(rs.getString("groupname"));
				group.setPrice(rs.getInt("price"));
				group.setState(rs.getString("state"));
				group.setCreator(rs.getString("creator"));
				group.setTroll(rs.getString("troll"));
			} else
				throw new NotFoundException("Group not found.");
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

		return group;
	}

	// Método para asignarle a un usuario el grupo que ha creado
	private void updateUserGroup(int groupid) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(UPDATE_USER_GROUP_QUERY);
			stmt.setInt(1, groupid);
			stmt.setString(2, security.getUserPrincipal().getName());

			int rows = stmt.executeUpdate();
			if (rows != 1)
				throw new NotFoundException("user not found");
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

	// Método para validar que no pertenezco ya a un grupo
	private void validateCreator() {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(VALIDATE_CREATOR);
			stmt.setString(1, security.getUserPrincipal().getName());
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				int groupid = rs.getInt("groupid");
				if (groupid != 0)
					throw new BadRequestException(
							"You already belong to a group");
			} else {

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

	// Método para validar que soy el creador del grupo que quiero modificar
	private void validateUser(int groupid) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(VALIDATE_USER);
			stmt.setInt(1, groupid);
			stmt.setString(2, security.getUserPrincipal().getName());
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {

			} else {
				throw new BadRequestException(
						"You are not the creator of this group");
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

	// Método para validar el uso de uno de los estados por defecto
	private void validateUpdateGroup(Group group, int groupid) {
		if (group.getState().equals("open")) {

		} else if (group.getState().equals("closed")) {
			// Se saca a los usuarios del grupo
			usersOutOfGroup(groupid);
		} else if (group.getState().equals("active")) {
			// Sorteo de Troll
			getTheTrollInAGroup(groupid);
		} else {
			throw new BadRequestException("The state is not valid");
		}
	}

	private void usersOutOfGroup(int groupid) {
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

				users.addUser(user);
			}

			// Ponemos el groupid de cada usuario del grupo a 0
			for (User user : users.getUsers()) {
				PreparedStatement stmt1 = null;

				stmt1 = conn
						.prepareStatement(UPDATE_USER_ON_GROUP_CLOSURE_QUERY);
				stmt1.setString(1, user.getUsername());
				stmt1.executeUpdate();

				if (stmt1 != null) {
					stmt1.close();
				}
			}

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
				if (stmt != null) {
					stmt.close();
				}
				conn.close();
			} catch (SQLException e) {
			}
		}
	}

	private void getTheTrollInAGroup(int groupid) {
		UserCollection users = new UserCollection();

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
			stmt = conn.prepareStatement(GET_USERS_IN_A_GROUP_QUERY);

			stmt.setInt(1, groupid);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				User user = new User();
				user.setUsername(rs.getString("username"));

				users.addUser(user);
			}

			// Obtenemos el Troll de forma aleatoria
			int N = users.getUsers().size();
			Random r = new Random();
			int TrollIndex = r.nextInt(N);

			conn.setAutoCommit(false);

			User troll = users.getUsers().get(TrollIndex);
			stmt1 = conn.prepareStatement(UPDATE_USER_TROLL_QUERY);
			stmt1.setBoolean(1, true);
			stmt1.setString(2, troll.getUsername());
			stmt1.executeUpdate();

			stmt2 = conn.prepareStatement(UPDATE_GROUP_TROLL_QUERY);
			stmt2.setString(1, troll.getUsername());
			stmt2.setInt(2, groupid);
			stmt2.executeUpdate();

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
				if (stmt != null) {
					stmt.close();
				}
				if (stmt1 != null) {
					stmt1.close();
				}
				if (stmt2 != null) {
					stmt2.close();
				}
				conn.setAutoCommit(true);
				conn.close();
			} catch (SQLException e) {
			}
		}
	}

	// Para trabajar con parámetros de seguridad
	@Context
	private SecurityContext security;
}
