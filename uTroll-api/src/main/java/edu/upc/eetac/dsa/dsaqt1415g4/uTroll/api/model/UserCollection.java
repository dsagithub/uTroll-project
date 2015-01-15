package edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.CommentResource;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.GroupResource;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.MediaType;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.UserResource;

public class UserCollection {
	@InjectLinks({ @InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "create-user", title = "Create user", type = MediaType.UTROLL_API_USER) })
	private List<Link> links;
	private List<User> users;

	public UserCollection() {
		super();
		users = new ArrayList<>();
	}

	public void addUser(User user) {
		users.add(user);
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}