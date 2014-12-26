package edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.CommentResource;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.MediaType;

public class CommentCollection {
	@InjectLinks({
			@InjectLink(resource = CommentResource.class, style = Style.ABSOLUTE, rel = "create-comment", title = "Create comment", type = MediaType.UTROLL_API_COMMENT) })
	private List<Link> links;
	private List<Comment> comments;
	//private long newestTimestamp;
	//private long oldestTimestamp;

	public CommentCollection() {
		super();
		comments = new ArrayList<>();
	}
	
	public void addComment(Comment comment) {
		comments.add(comment);
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
	
}