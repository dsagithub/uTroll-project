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
	// private final static String GET_GROUPS_QUERY = "select * from groups";
	private final static String GET_GROUPS_QUERY = "select * from groups where creator in (select friend2 from friend_list where (friend1=? or friend2=?) and state = 'accepted')";
	private final static String CREATE_GROUP_QUERY = "insert into groups (groupname, price, ending_timestamp, closing_timestamp, creator, troll, state) values(?, ?, ?, ?, ?, ?, ?)";
	private final static String UPDATE_GROUP_QUERY = "update groups set state = ? where groupid = ?";
	private final static String VALIDATE_CREATOR = "select groupid from users where username = ?";
	private final static String UPDATE_USER_GROUP_QUERY = "update users set groupid = ? where username = ?";
	private final static String UPDATE_USER_POINTS_QUERY = "update users set points = ?, points_max = greatest(points_max, ?) where username = ?";
	private final static String VALIDATE_USER = "select groupname from groups where groupid = ? and creator = ?";
	private final static String GET_USERS_IN_A_GROUP_QUERY = "select username from users where groupid = ?";
	private final static String UPDATE_USER_TROLL_QUERY = "update users set isTroll = ? where username = ?";
	private final static String UPDATE_GROUP_TROLL_QUERY = "update groups set troll = ? where groupid = ?";
	private final static String UPDATE_USER_ON_GROUP_CLOSURE_QUERY = "update users set isTroll = false, groupid = 0, vote = 'none', votedBy = 0  where username = ?";
	private final static String CREATE_EVENT = "create event evento? on schedule at ? do update groups set state = ? where groupid = ?";
	private final static String CREATE_EVENT_CLOSURE = "create event eventocierre? on schedule at ? do update groups set state = ? where groupid = ?";
	private final static String CREATE_EVENT_CLOSURE_A = "create event eventocierreA? on schedule at ? do create table temporal? ( username varchar (20) not null)";
	private final static String CREATE_EVENT_CLOSURE_B = "create event eventocierreB? on schedule at ? do insert into temporal? select username from users where groupid = ? order by rand() limit 1";
	private final static String CREATE_EVENT_CLOSURE_C = "create event eventocierreC? on schedule at ? do update users set isTroll = true where username = (select username from temporal?)";
	private final static String CREATE_EVENT_CLOSURE_D = "create event eventocierreD? on schedule at ? do update groups set troll = (select username from temporal?) where groupid = ?";
	private final static String CREATE_EVENT_CLOSURE_E = "create event eventocierreE? on schedule at ? do drop table temporal?";

	private final static String CREATE_EVENT_POINTS_A = "create event eventopuntosA? on schedule at ? do create table grouptemp? ( groupid int not null, price int not null, troll varchar (20) not null)";
	private final static String CREATE_EVENT_POINTS_B = "create event eventopuntosB? on schedule at ? do insert into grouptemp? select groupid, price, troll from groups where groupid = ?";
	private final static String CREATE_EVENT_POINTS_C = "create event eventopuntosC? on schedule at ? do create table usersOK? ( username varchar (20) not null)";
	private final static String CREATE_EVENT_POINTS_D = "create event eventopuntosD? on schedule at ? do insert into usersOK? select username from users where groupid = ? and vote = (select troll from grouptemp?)";
	private final static String CREATE_EVENT_POINTS_E = "create event eventopuntosE? on schedule at ? do create table usersFail? ( username varchar (20) not null)";
	private final static String CREATE_EVENT_POINTS_F = "create event eventopuntosF? on schedule at ? do insert into usersFail? select username from users where groupid = ? and vote != (select troll from grouptemp?)";
	private final static String CREATE_EVENT_POINTS_G = "create event eventopuntosG? on schedule at ? do update users set points = (points + 2*(select price from grouptemp?)), points_max = greatest(points_max, (points + 2*(select price from grouptemp?))) where username in (select username from usersOK?)";
	private final static String CREATE_EVENT_POINTS_H = "create event eventopuntosH? on schedule at ? do update users set points = (points + (select COUNT(username) from usersFail?)*(select price from grouptemp?)), points_max = greatest(points_max, (points + (select COUNT(username) from usersFail?)*(select price from grouptemp?))) where username = (select troll from grouptemp?)";
	private final static String CREATE_EVENT_POINTS_I = "create event eventopuntosI? on schedule at ? do update users set isTroll = false, groupid = 0, votedBy = 0, vote = 'none' where username in (select username from usersOK?)";
	private final static String CREATE_EVENT_POINTS_J = "create event eventopuntosJ? on schedule at ? do update users set isTroll = false, groupid = 0, votedBy = 0, vote = 'none' where username in (select username from usersFail?)";
	private final static String CREATE_EVENT_POINTS_K = "create event eventopuntosK? on schedule at ? do drop table grouptemp?";
	private final static String CREATE_EVENT_POINTS_L = "create event eventopuntosL? on schedule at ? do drop tables grouptemp?, usersOK?, usersFail?";

	private final static String GET_USER_BY_USERNAME_QUERY = "select * from users where username=?";
	private final static String GET_USERS_GOOD_VOTE_QUERY = "select * from users where groupid = ? and vote = ?";
	private final static String GET_USERS_FAILED_VOTE_QUERY = "select * from users where groupid = ? and vote != ?";
	private final static String GET_TROLL_GROUP_QUERY = "select * from users where groupid = ? and isTroll = true";

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

			stmt.setString(1, security.getUserPrincipal().getName());
			stmt.setString(2, security.getUserPrincipal().getName());

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
		String fechaSub = fecha.substring(0, fecha.length() - 3);
		String fechacierreSub = fechacierre.substring(0,
				fechacierre.length() - 3);
		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmtA = null;
		PreparedStatement stmtB = null;
		PreparedStatement stmtC = null;
		PreparedStatement stmtD = null;
		PreparedStatement stmtE = null;
		PreparedStatement stmtA1 = null;
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
			stmt1.setString(2, fechaSub + ":01");
			stmt1.setString(3, "active");
			stmt1.setInt(4, group.getGroupid());
			stmt1.executeUpdate();

			stmtA = conn.prepareStatement(CREATE_EVENT_CLOSURE_A);
			stmtA.setInt(1, group.getGroupid());
			stmtA.setString(2, fechaSub + ":04");
			stmtA.setInt(3, group.getGroupid());
			stmtA.executeUpdate();
			stmtB = conn.prepareStatement(CREATE_EVENT_CLOSURE_B);
			stmtB.setInt(1, group.getGroupid());
			stmtB.setString(2, fechaSub + ":07");
			stmtB.setInt(3, group.getGroupid());
			stmtB.setInt(4, group.getGroupid());
			stmtB.executeUpdate();
			stmtC = conn.prepareStatement(CREATE_EVENT_CLOSURE_C);
			stmtC.setInt(1, group.getGroupid());
			stmtC.setString(2, fechaSub + ":10");
			stmtC.setInt(3, group.getGroupid());
			stmtC.executeUpdate();
			stmtD = conn.prepareStatement(CREATE_EVENT_CLOSURE_D);
			stmtD.setInt(1, group.getGroupid());
			stmtD.setString(2, fechaSub + ":13");
			stmtD.setInt(3, group.getGroupid());
			stmtD.setInt(4, group.getGroupid());
			stmtD.executeUpdate();
			stmtE = conn.prepareStatement(CREATE_EVENT_CLOSURE_E);
			stmtE.setInt(1, group.getGroupid());
			stmtE.setString(2, fechaSub + ":16");
			stmtE.setInt(3, group.getGroupid());
			stmtE.executeUpdate();

			stmt2 = conn.prepareStatement(CREATE_EVENT_CLOSURE);
			stmt2.setInt(1, group.getGroupid());
			stmt2.setString(2, fechacierre);
			stmt2.setString(3, "closed");
			stmt2.setInt(4, group.getGroupid());
			stmt2.executeUpdate();

			stmtA1 = conn.prepareStatement(CREATE_EVENT_POINTS_A);
			stmtA1.setInt(1, group.getGroupid());
			stmtA1.setString(2, fechacierreSub + ":01");
			stmtA1.setInt(3, group.getGroupid());
			stmtA1.executeUpdate();
			stmtA1.close();
			stmtA1 = conn.prepareStatement(CREATE_EVENT_POINTS_B);
			stmtA1.setInt(1, group.getGroupid());
			stmtA1.setString(2, fechacierreSub + ":03");
			stmtA1.setInt(3, group.getGroupid());
			stmtA1.setInt(4, group.getGroupid());
			stmtA1.executeUpdate();
			stmtA1.close();
			stmtA1 = conn.prepareStatement(CREATE_EVENT_POINTS_C);
			stmtA1.setInt(1, group.getGroupid());
			stmtA1.setString(2, fechacierreSub + ":05");
			stmtA1.setInt(3, group.getGroupid());
			stmtA1.executeUpdate();
			stmtA1.close();
			stmtA1 = conn.prepareStatement(CREATE_EVENT_POINTS_D);
			stmtA1.setInt(1, group.getGroupid());
			stmtA1.setString(2, fechacierreSub + ":07");
			stmtA1.setInt(3, group.getGroupid());
			stmtA1.setInt(4, group.getGroupid());
			stmtA1.setInt(5, group.getGroupid());
			stmtA1.executeUpdate();
			stmtA1.close();
			stmtA1 = conn.prepareStatement(CREATE_EVENT_POINTS_E);
			stmtA1.setInt(1, group.getGroupid());
			stmtA1.setString(2, fechacierreSub + ":09");
			stmtA1.setInt(3, group.getGroupid());
			stmtA1.executeUpdate();
			stmtA1.close();
			stmtA1 = conn.prepareStatement(CREATE_EVENT_POINTS_F);
			stmtA1.setInt(1, group.getGroupid());
			stmtA1.setString(2, fechacierreSub + ":11");
			stmtA1.setInt(3, group.getGroupid());
			stmtA1.setInt(4, group.getGroupid());
			stmtA1.setInt(5, group.getGroupid());
			stmtA1.executeUpdate();
			stmtA1.close();
			stmtA1 = conn.prepareStatement(CREATE_EVENT_POINTS_G);
			stmtA1.setInt(1, group.getGroupid());
			stmtA1.setString(2, fechacierreSub + ":13");
			stmtA1.setInt(3, group.getGroupid());
			stmtA1.setInt(4, group.getGroupid());
			stmtA1.setInt(5, group.getGroupid());
			stmtA1.executeUpdate();
			stmtA1.close();
			stmtA1 = conn.prepareStatement(CREATE_EVENT_POINTS_H);
			stmtA1.setInt(1, group.getGroupid());
			stmtA1.setString(2, fechacierreSub + ":15");
			stmtA1.setInt(3, group.getGroupid());
			stmtA1.setInt(4, group.getGroupid());
			stmtA1.setInt(5, group.getGroupid());
			stmtA1.setInt(6, group.getGroupid());
			stmtA1.setInt(7, group.getGroupid());
			stmtA1.executeUpdate();
			stmtA1.close();
			stmtA1 = conn.prepareStatement(CREATE_EVENT_POINTS_I);
			stmtA1.setInt(1, group.getGroupid());
			stmtA1.setString(2, fechacierreSub + ":17");
			stmtA1.setInt(3, group.getGroupid());
			stmtA1.executeUpdate();
			stmtA1.close();
			stmtA1 = conn.prepareStatement(CREATE_EVENT_POINTS_J);
			stmtA1.setInt(1, group.getGroupid());
			stmtA1.setString(2, fechacierreSub + ":19");
			stmtA1.setInt(3, group.getGroupid());
			stmtA1.executeUpdate();
			stmtA1.close();
			stmtA1 = conn.prepareStatement(CREATE_EVENT_POINTS_K);
			stmtA1.setInt(1, group.getGroupid());
			stmtA1.setString(2, fechacierreSub + ":21");
			stmtA1.setInt(3, group.getGroupid());
			stmtA1.executeUpdate();
			stmtA1.close();
			stmtA1 = conn.prepareStatement(CREATE_EVENT_POINTS_L);
			stmtA1.setInt(1, group.getGroupid());
			stmtA1.setString(2, fechacierreSub + ":23");
			stmtA1.setInt(3, group.getGroupid());
			stmtA1.setInt(4, group.getGroupid());
			stmtA1.setInt(5, group.getGroupid());
			stmtA1.executeUpdate();
			stmtA1.close();
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
				if (stmtA != null)
					stmtA.close();
				if (stmtB != null)
					stmtB.close();
				if (stmtC != null)
					stmtC.close();
				if (stmtD != null)
					stmtD.close();
				if (stmtE != null)
					stmtE.close();
				if (stmtA1 != null)
					stmtA1.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		updateUserGroup(group.getGroupid(), group.getPrice());

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
	private void updateUserGroup(int groupid, int price) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		try {
			stmt = conn.prepareStatement(UPDATE_USER_GROUP_QUERY);
			stmt.setInt(1, groupid);
			stmt.setString(2, security.getUserPrincipal().getName());

			int rows = stmt.executeUpdate();
			if (rows != 1)
				throw new NotFoundException("user not found");

			User user = getUserFromDatabase(security.getUserPrincipal()
					.getName(), false);
			int points_user = user.getPoints() - price;
			if (points_user == 0)
				points_user = 5;
			stmt2 = conn.prepareStatement(UPDATE_USER_POINTS_QUERY);
			stmt2.setInt(1, points_user);
			stmt2.setInt(2, points_user);
			stmt2.setString(3, security.getUserPrincipal().getName());
			stmt2.executeUpdate();
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (stmt2 != null)
					stmt2.close();
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
			givePoints(groupid);
			usersOutOfGroup(groupid); // Sacar a los usuarios del grupo
		} else if (group.getState().equals("active")) {
			getTheTrollInAGroup(groupid); // Sorteo de Troll
		} else {
			throw new BadRequestException("The state is not valid");
		}
	}

	private void givePoints(int groupid) {
		UserCollection usersOK = new UserCollection();
		UserCollection usersFail = new UserCollection();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		PreparedStatement stmtVoteFail = null;
		PreparedStatement stmtGroup = null;
		try {
			// Queries del grupo
			stmtGroup = conn.prepareStatement(GET_GROUP_BY_GROUPID_QUERY);

			stmtGroup.setInt(1, groupid);

			ResultSet rsGroup = stmtGroup.executeQuery();
			Group group = new Group();
			while (rsGroup.next()) {
				group.setPrice(rsGroup.getInt("price"));
				group.setTroll(rsGroup.getString("troll"));
			}

			// Queries usuarios que han acertado
			stmt = conn.prepareStatement(GET_USERS_GOOD_VOTE_QUERY);

			stmt.setInt(1, groupid);
			stmt.setString(1, group.getTroll());

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				User user = new User();
				user.setUsername(rs.getString("username"));
				user.setPoints(rs.getInt("points"));

				usersOK.addUser(user);
			}

			// Queries usuarios que han fallado
			stmtVoteFail = conn.prepareStatement(GET_USERS_FAILED_VOTE_QUERY);

			stmtVoteFail.setInt(1, groupid);
			stmtVoteFail.setString(1, group.getTroll());

			ResultSet rsVoteFail = stmtVoteFail.executeQuery();
			while (rsVoteFail.next()) {
				User user = new User();
				user.setUsername(rsVoteFail.getString("username"));
				user.setPoints(rs.getInt("points"));

				usersFail.addUser(user);
			}

			// Repartir premios
			for (User user : usersOK.getUsers()) {
				PreparedStatement stmt1 = null;

				stmt1 = conn.prepareStatement(UPDATE_USER_POINTS_QUERY);
				stmt1.setInt(1, user.getPoints() + 2 * group.getPrice());
				stmt1.setInt(2, user.getPoints() + 2 * group.getPrice());
				stmt1.setString(3, user.getUsername());
				stmt1.executeUpdate();

				if (stmt1 != null) {
					stmt1.close();
				}
			}

			// Repartir premios Troll
			if (usersOK.getUsers().size() > usersFail.getUsers().size()) {
				PreparedStatement stmt1 = null;
				PreparedStatement stmt2 = null;
				stmt2 = conn.prepareStatement(GET_TROLL_GROUP_QUERY);
				stmt2.setInt(1, groupid);
				ResultSet rs2 = stmt2.executeQuery();
				User troll = new User();
				while (rs2.next()) {
					troll.setUsername(rs2.getString("username"));
					troll.setPoints(rs2.getInt("points"));
				}

				stmt1 = conn.prepareStatement(UPDATE_USER_POINTS_QUERY);
				stmt1.setInt(1, troll.getPoints() + usersFail.getUsers().size()
						* group.getPrice());
				stmt1.setInt(2, troll.getPoints() + usersFail.getUsers().size()
						* group.getPrice());
				stmt1.setString(3, troll.getUsername());
				stmt1.executeUpdate();

				if (stmt1 != null) {
					stmt1.close();
				}
				if (stmt2 != null) {
					stmt2.close();
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
				if (stmtGroup != null) {
					stmtGroup.close();
				}
				if (stmtVoteFail != null) {
					stmtVoteFail.close();
				}
				conn.close();
			} catch (SQLException e) {
			}
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

	// Para trabajar con parámetros de seguridad
	@Context
	private SecurityContext security;
}
