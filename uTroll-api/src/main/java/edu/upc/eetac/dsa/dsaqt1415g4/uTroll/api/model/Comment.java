package edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model;

import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.InjectLink.Style;

import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.CommentResource;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.GroupResource;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.MediaType;

public class Comment {
	private int commentid;
	private String username;
	private String creator;
	private String content;
	private int likes;
	private int dislikes;
	private int groupid;
	private long last_modified;
	private long creation_timestamp;

	@InjectLinks({
		@InjectLink(resource = CommentResource.class, style = Style.ABSOLUTE, rel = "self edit", title = "Self edit", type = MediaType.UTROLL_API_COMMENT, method = "getComment", bindings = @Binding(name = "commentid", value = "${instance.commentid}")),
		@InjectLink(resource = CommentResource.class, style = Style.ABSOLUTE, rel = "like", title = "Like comment", type = MediaType.UTROLL_API_COMMENT, method = "likeComment", bindings = @Binding(name = "commentid", value = "${instance.commentid}")),
		@InjectLink(resource = CommentResource.class, style = Style.ABSOLUTE, rel = "dislike", title = "Dislike comment", type = MediaType.UTROLL_API_COMMENT, method = "dislikeComment", bindings = @Binding(name = "commentid", value = "${instance.commentid}"))
		})
	private List<Link> links;

	public int getCommentid() {
		return commentid;
	}

	public void setCommentid(int commentid) {
		this.commentid = commentid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public int getDislikes() {
		return dislikes;
	}

	public void setDislikes(int dislikes) {
		this.dislikes = dislikes;
	}

	public int getGroupid() {
		return groupid;
	}

	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}

	public long getLast_modified() {
		return last_modified;
	}

	public void setLast_modified(long last_modified) {
		this.last_modified = last_modified;
	}

	public long getCreation_timestamp() {
		return creation_timestamp;
	}

	public void setCreation_timestamp(long creation_timestamp) {
		this.creation_timestamp = creation_timestamp;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
}
