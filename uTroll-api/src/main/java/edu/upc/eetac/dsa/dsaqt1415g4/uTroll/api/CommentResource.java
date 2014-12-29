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
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model.CommentCollection;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model.User;

@Path("/comments")
public class CommentResource {
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();

	private final static String GET_COMMENT_BY_COMMENTID_QUERY = "select * from comment where commentid=?";
	private final static String GET_COMMENTS_BY_GROUP_QUERY = "select * from comment where groupid=? order by creation_timestamp desc limit ?";
	private final static String GET_COMMENTS_QUERY = "select * from comment order by creation_timestamp desc limit ?";
	private final static String INSERT_COMMENT_QUERY = "insert into comment (username, creator, content, likes, dislikes, groupid) values (?, ?, ?, ?, ?, ?)";
	private final static String UPDATE_COMMENT_QUERY = "update comment set content=ifnull(?, content), likes=ifnull(?, likes), dislikes=ifnull(?, dislikes) where commentid=?";
	private final static String UPDATE_LIKE_COMMENT_QUERY = "update comment set likes=ifnull(?, likes), dislikes=ifnull(?, dislikes) where commentid=?";
	private final static String CHECK_LIKE_COMMENT_QUERY = "select * from likes where commentid = ? and username = ?";
	private final static String UPDATE_LIKE_DISLIKE_QUERY = "update likes set likeComment=ifnull(?, likeComment), dislikeComment=ifnull(?, dislikeComment) where commentid = ? and username = ?";
	private final static String CREATE_LIKE_DISLIKE_QUERY = "insert into likes (commentid, username, likeComment, dislikeComment) values (?, ?, ?, ?)";

	private final static String GET_MYGROUP_QUERY = "select groupid from users where username=?";

	// Obtener comentarios del grupo al que pertenezco
	@GET
	@Produces(MediaType.UTROLL_API_COMMENT_COLLECTION)
	public CommentCollection getComments(@QueryParam("length") int length) {
		CommentCollection comments = new CommentCollection();

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null;
		try {
			int mygroup = getMyGroup(security.getUserPrincipal().getName());

			stmt = conn.prepareStatement(GET_COMMENTS_QUERY);
			// stmt.setInt(1, mygroup);
			// if (length <= 0)
			// length = 5;
			// stmt.setInt(2, length);

			if (length <= 0)
				length = 5;
			stmt.setInt(1, length);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Comment comment = new Comment();
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

				comments.addComment(comment);
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

		return comments;
	}

	// Método obtención comentario - cacheable
	@GET
	@Path("/{commentid}")
	@Produces(MediaType.UTROLL_API_COMMENT)
	public Response getComment(@PathParam("commentid") int commentid,
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

	// Crear un comentario en el grupo al que pertenezco
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

		int mygroup = getMyGroup(security.getUserPrincipal().getName());

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(INSERT_COMMENT_QUERY,
					Statement.RETURN_GENERATED_KEYS);

			stmt.setString(1, security.getUserPrincipal().getName());
			stmt.setString(2, security.getUserPrincipal().getName()); //MODIFICAR ESTO PARA EL TROLL
			stmt.setString(3, comment.getContent());
			stmt.setInt(4, 0);
			stmt.setInt(5, 0);
			stmt.setInt(6, mygroup);

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
		int previousLike = checkPreviousLike(commentid, security.getUserPrincipal().getName());

		Comment comment = null;

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null; //Tabla comentarios
		PreparedStatement stmt1 = null; //Tabla likes
		PreparedStatement stmt2 = null; //Crear entrada en tabla likes
		try {
			comment = getCommentFromDatabase(commentid);

			stmt = conn.prepareStatement(UPDATE_LIKE_COMMENT_QUERY);

			if (previousLike == 1) {
				stmt.setInt(1, (comment.getLikes() + 1));
				stmt.setString(2, null);
			} else if (previousLike == 2) {
				stmt.setInt(1, (comment.getLikes() - 1));
				stmt.setString(2, null);
			} else if (previousLike == 3) {
				stmt.setInt(1, (comment.getLikes() + 1));
				stmt.setInt(2, (comment.getDislikes() - 1));
			}
			else if (previousLike == 4) {
				stmt.setInt(1, (comment.getLikes() + 1));
				stmt.setString(2, null);
			}
			
			stmt.setInt(3, commentid);

			int rows = stmt.executeUpdate();
			if (rows == 1)
				comment = getCommentFromDatabase(commentid);
			else {
				throw new NotFoundException("Comment not found");
			}
			
			// Actualizar la tabla que mapea likes, usuarios y comentarios
			stmt1 = conn.prepareStatement(UPDATE_LIKE_DISLIKE_QUERY);
			stmt2 = conn.prepareStatement(CREATE_LIKE_DISLIKE_QUERY);
			if (previousLike == 1) {
				stmt1.setBoolean(1, true);
				stmt1.setString(2, null);
				stmt1.setInt(3, commentid);
				stmt1.setString(4, security.getUserPrincipal().getName());
				stmt1.executeUpdate();
			} else if (previousLike == 2) {
				stmt1.setBoolean(1, false);
				stmt1.setString(2, null);
				stmt1.setInt(3, commentid);
				stmt1.setString(4, security.getUserPrincipal().getName());
				stmt1.executeUpdate();
			} else if (previousLike == 3) {
				stmt1.setBoolean(1, true);
				stmt1.setBoolean(2, false);
				stmt1.setInt(3, commentid);
				stmt1.setString(4, security.getUserPrincipal().getName());
				stmt1.executeUpdate();
			}
			else if (previousLike == 4){
				stmt2.setBoolean(3, true);
				stmt2.setBoolean(4, false);
				stmt2.setInt(1, commentid);
				stmt2.setString(2, security.getUserPrincipal().getName());
				stmt2.executeUpdate();
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
				if (stmt2 != null)
					stmt2.close();
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
		int previousLike = checkPreviousLike(commentid, security.getUserPrincipal().getName());

		Comment comment = null;
		Comment comment1 = null;

		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		PreparedStatement stmt = null; //Tabla comentarios
		PreparedStatement stmt1 = null; //Tabla likes
		PreparedStatement stmt2 = null; //Crear entrada en tabla likes
		try {
			comment = getCommentFromDatabase(commentid);

			stmt = conn.prepareStatement(UPDATE_LIKE_COMMENT_QUERY);

			if (previousLike == 1) {
				stmt.setInt(2, (comment.getDislikes() + 1));
				stmt.setString(1, null);
			} else if (previousLike == 2) {
				stmt.setInt(2, (comment.getDislikes() + 1));
				stmt.setInt(1, (comment.getLikes() - 1));
			} else if (previousLike == 3) {
				stmt.setInt(2, (comment.getDislikes() - 1));
				stmt.setString(1, null);
			} else if (previousLike == 4) {
				stmt.setInt(2, (comment.getDislikes() + 1));
				stmt.setString(1, null);
			}
			
			stmt.setInt(3, commentid);

			int rows = stmt.executeUpdate();
			if (rows == 1)
				comment1 = getCommentFromDatabase(commentid);
			else {
				throw new NotFoundException("Comment not found");
			}
			
			// Actualizar la tabla que mapea likes, usuarios y comentarios
			stmt1 = conn.prepareStatement(UPDATE_LIKE_DISLIKE_QUERY);
			stmt2 = conn.prepareStatement(CREATE_LIKE_DISLIKE_QUERY);
			if (previousLike == 1) {
				stmt1.setBoolean(2, true);
				stmt1.setString(1, null);
				stmt1.setInt(3, commentid);
				stmt1.setString(4, security.getUserPrincipal().getName());
				stmt1.executeUpdate();
			} else if (previousLike == 2) {
				stmt1.setBoolean(2, true);
				stmt1.setBoolean(1, false);
				stmt1.setInt(3, commentid);
				stmt1.setString(4, security.getUserPrincipal().getName());
				stmt1.executeUpdate();
			} else if (previousLike == 3) {
				stmt1.setBoolean(2, false);
				stmt1.setString(1, null);
				stmt1.setInt(3, commentid);
				stmt1.setString(4, security.getUserPrincipal().getName());
				stmt1.executeUpdate();
			}
			else if (previousLike == 4){
				stmt2.setBoolean(4, true);
				stmt2.setBoolean(3, false);
				stmt2.setInt(1, commentid);
				stmt2.setString(2, security.getUserPrincipal().getName());
				stmt2.executeUpdate();
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
				if (stmt2 != null)
					stmt2.close();
				conn.close();
			} catch (SQLException e) {
			}
		}

		return comment1;
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

	// Comprobar si ya se le ha dado a like al comentario
	private int checkPreviousLike(int commentid, String username) {
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
			stmt = conn.prepareStatement(CHECK_LIKE_COMMENT_QUERY);
			stmt.setInt(1, commentid);
			stmt.setString(2, username);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				boolean liked = rs.getBoolean("likeComment");
				boolean disliked = rs.getBoolean("dislikeComment");
				
				if (!liked && !disliked)
					return 1;
				else if (liked)
					return 2;
				else if (disliked)
					return 3;
			} else
				return 4;
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
		
		return 0;
	}

	// Método para validar un comentario
	private void validateComment(Comment comment) {
		if (comment.getContent() == null)
			throw new BadRequestException("Content can't be null.");
		if (comment.getContent().length() > 500)
			throw new BadRequestException(
					"Content can't be greater than 500 characters.");
	}

	// Método para obtener el grupo al que pertenezco
	private int getMyGroup(String username) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}

		int mygroup = 0;
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_MYGROUP_QUERY);
			stmt.setString(1, security.getUserPrincipal().getName());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				mygroup = rs.getInt("groupid");
			}
			// if (mygroup == 0)
			// throw new BadRequestException("No perteneces a ningún grupo");
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
		return mygroup;
	}

	// Para trabajar con parámetros de seguridad
	@Context
	private SecurityContext security;
}
