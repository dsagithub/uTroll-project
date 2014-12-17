package edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

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
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model.User;

@Path("/comments")
public class CommentResource {
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();

	private final static String GET_COMMENT_BY_COMMENTID_QUERY = "select * from comment where commentid=?";
	private final static String INSERT_COMMENT_QUERY = "insert into comment (username, creator, content, likes, dislikes, groupid) values (?, ?, ?, ?, ?, ?)";
	private final static String UPDATE_COMMENT_QUERY = "update comment set content=ifnull(?, content), likes=ifnull(?, likes), dislikes=ifnull(?, dislikes) where commentid=?";
	private final static String UPDATE_LIKE_COMMENT_QUERY = "update comment set likes=? where commentid=?";
	private final static String UPDATE_DISLIKE_COMMENT_QUERY = "update comment set dislikes=? where commentid=?";

//	// Obtener colección de comentarios
//	@GET
//	@Produces(MediaType.UTROLL_API_COMMENT_COLLECTION)
//	public CommentCollection getStings(@QueryParam("length") int length,
//			@QueryParam("before") long before, @QueryParam("after") long after) {
//		CommentCollection stings = new CommentCollection();
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
//			boolean updateFromLast = after > 0;
//			stmt = updateFromLast ? conn
//					.prepareStatement(GET_STINGS_QUERY_FROM_LAST) : conn
//					.prepareStatement(GET_STINGS_QUERY);
//			if (updateFromLast) {
//				stmt.setTimestamp(1, new Timestamp(after));
//			} else {
//				if (before > 0)
//					stmt.setTimestamp(1, new Timestamp(before));
//				else
//					stmt.setTimestamp(1, null);
//				length = (length <= 0) ? 5 : length;
//				stmt.setInt(2, length);
//			}
//			ResultSet rs = stmt.executeQuery();
//			boolean first = true;
//			long oldestTimestamp = 0;
//			while (rs.next()) {
//				Sting sting = new Sting();
//				sting.setStingid(rs.getInt("stingid"));
//				sting.setUsername(rs.getString("username"));
//				// sting.setAuthor(rs.getString("name"));
//				sting.setSubject(rs.getString("subject"));
//				sting.setContent(rs.getString("content"));
//				oldestTimestamp = rs.getTimestamp("last_modified").getTime();
//				sting.setLastModified(oldestTimestamp);
//				if (first) {
//					first = false;
//					stings.setNewestTimestamp(sting.getLastModified());
//				}
//				stings.addSting(sting);
//			}
//			stings.setOldestTimestamp(oldestTimestamp);
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
//		return stings;
//	}

	// Método obtención usuario - cacheable
	@GET
	@Path("/{commentid}")
	@Produces(MediaType.UTROLL_API_COMMENT)
	public Response getUser(@PathParam("commentid") int commentid,
			@Context Request request) {
		CacheControl cc = new CacheControl();

		Comment comment = getCommentFromDatabase(commentid);

		// Como en este caso no tenemos un last modified que pueda variar,
		// podemos hacer un md5 entre el usuario (invariable) y el email
		// (variable) para ver si el recurso sigue siendo válido
		String eTagDigest = DigestUtils.md5Hex(comment.getLikes()
				+ comment.getDislikes() + comment.getLast_modified()
				+ comment.getContent());

		EntityTag eTag = new EntityTag(eTagDigest);

		// Verificar si coincide con el etag de la peticion http
		Response.ResponseBuilder rb = request.evaluatePreconditions(eTag);

		if (rb != null) {
			return rb.cacheControl(cc).tag(eTag).build();
		}
		rb = Response.ok(comment).cacheControl(cc).tag(eTag); // ok = status
																// 200OK

		return rb.build();
	}

	// Crear un comentario
	@POST
	@Consumes(MediaType.UTROLL_API_COMMENT)
	@Produces(MediaType.UTROLL_API_COMMENT)
	public Comment createComment(Comment comment) {
		validateComment(comment);

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(INSERT_COMMENT_QUERY,
					Statement.RETURN_GENERATED_KEYS);

			// stmt.setString(1, security.getUserPrincipal().getName());
			stmt.setString(1, comment.getUsername());
			stmt.setString(2, comment.getCreator());
			stmt.setString(3, comment.getContent());
			stmt.setInt(4, 0);
			stmt.setInt(5, 0);
			stmt.setInt(6, comment.getGroupid());

			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int commentid = rs.getInt(1);

				comment = getCommentFromDatabase(commentid);
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

		return comment;
	}

	// Modificar los datos de un comentario
	@PUT
	@Path("/{commentid}")
	@Consumes(MediaType.UTROLL_API_COMMENT)
	@Produces(MediaType.UTROLL_API_COMMENT)
	public Comment updateComment(@PathParam("commentid") int commentid,
			Comment comment) {
		// validateUser(commentid);

		// validateComment(comment);

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(UPDATE_COMMENT_QUERY);
			stmt.setString(1, comment.getContent());
			stmt.setInt(2, comment.getLikes());
			stmt.setInt(3, comment.getDislikes());
			stmt.setInt(4, commentid);

			int rows = stmt.executeUpdate();
			if (rows == 1)
				comment = getCommentFromDatabase(commentid);
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

		return comment;
	}

	// Darle like a un comentario
	@PUT
	@Path("/like/{commentid}")
	@Consumes(MediaType.UTROLL_API_COMMENT)
	@Produces(MediaType.UTROLL_API_COMMENT)
	public Comment likeComment(@PathParam("commentid") int commentid) {
		// validateUser(commentid);

		// validateComment(comment);

		Comment comment = null;

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			comment = getCommentFromDatabase(commentid);

			stmt = conn.prepareStatement(UPDATE_LIKE_COMMENT_QUERY);
			// stmt.setString(1, null);
			stmt.setInt(1, (comment.getLikes() + 1));
			stmt.setInt(2, commentid);

			int rows = stmt.executeUpdate();
			if (rows == 1)
				comment = getCommentFromDatabase(commentid);
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

		return comment;
	}

	// Darle dislike a un comentario
	@PUT
	@Path("/dislike/{commentid}")
	@Consumes(MediaType.UTROLL_API_COMMENT)
	@Produces(MediaType.UTROLL_API_COMMENT)
	public Comment dislikeComment(@PathParam("commentid") int commentid) {
		// validateUser(commentid);

		// validateComment(comment);

		Comment comment = null;

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			comment = getCommentFromDatabase(commentid);

			stmt = conn.prepareStatement(UPDATE_DISLIKE_COMMENT_QUERY);
			// stmt.setString(1, null);
			stmt.setInt(1, (comment.getDislikes() + 1));
			stmt.setInt(2, commentid);

			int rows = stmt.executeUpdate();
			if (rows == 1)
				comment = getCommentFromDatabase(commentid);
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

		return comment;
	}

	// El bool password define si se quiere recuperar o no el pwd de la BD
	private Comment getCommentFromDatabase(int commentid) {
		Comment comment = new Comment();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_COMMENT_BY_COMMENTID_QUERY);
			stmt.setInt(1, commentid);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				comment.setCommentid(rs.getInt("commentid"));
				comment.setContent(rs.getString("content"));
				comment.setCreation_timestamp(rs.getTimestamp(
						"creation_timestamp").getTime());
				comment.setCreator(rs.getString("creator"));
				comment.setDislikes(rs.getInt("dislikes"));
				comment.setGroupid(rs.getInt("groupid"));
				comment.setLast_modified(rs.getTimestamp("last_modified")
						.getTime());
				comment.setLikes(rs.getInt("likes"));
				comment.setUsername(rs.getString("username"));
			} else
				throw new NotFoundException("Comment not found.");
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

		return comment;
	}

	// Método para validar un comentario
	private void validateComment(Comment comment) {
		if (comment.getContent() == null)
			throw new BadRequestException("Content can't be null.");
		if (comment.getContent().length() > 500)
			throw new BadRequestException(
					"Content can't be greater than 500 characters.");
	}

	// Para trabajar con parámetros de seguridad
	@Context
	private SecurityContext security;
}
