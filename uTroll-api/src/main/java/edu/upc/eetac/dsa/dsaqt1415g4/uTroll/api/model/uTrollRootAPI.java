package edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model;

import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.CommentResource;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.FriendListResource;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.GroupResource;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.MediaType;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.UserResource;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.uTrollRootAPIResource;

//Es un POJO (no hereda de nada, atributos privados, getters y setters)
public class uTrollRootAPI {
	@InjectLinks({
			@InjectLink(resource = uTrollRootAPIResource.class, style = Style.ABSOLUTE, rel = "self bookmark home", title = "uTroll Root API", method = "getRootAPI"),
			@InjectLink(resource = CommentResource.class, style = Style.ABSOLUTE, rel = "comments", title = "Latest comments", type = MediaType.UTROLL_API_COMMENT_COLLECTION),
			@InjectLink(resource = CommentResource.class, style = Style.ABSOLUTE, rel = "post-comment", title = "Post comment", type = MediaType.UTROLL_API_COMMENT, method = "createComment"),
			@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "login", title = "Check login", type = MediaType.UTROLL_API_USER, method = "checkLogin"),
			@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "vote", title = "Vote Troll", type = MediaType.UTROLL_API_USER),
			@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "users", title = "Get users by username", type = MediaType.UTROLL_API_USER_COLLECTION),
			@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "create-user", title = "Create user", type = MediaType.UTROLL_API_USER, method = "createUser"),
			@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "update-user", title = "Update user", type = MediaType.UTROLL_API_USER, method = "updateUser"),
			@InjectLink(resource = FriendListResource.class, style = Style.ABSOLUTE, rel = "friend", title = "FriendList resource", type = MediaType.UTROLL_API_FRIENDLIST),
			@InjectLink(resource = FriendListResource.class, style = Style.ABSOLUTE, rel = "pending-friends", title = "Pending friends", type = MediaType.UTROLL_API_USER_COLLECTION, method = "getPendingFriends"),
			@InjectLink(resource = GroupResource.class, style = Style.ABSOLUTE, rel = "create-group", title = "Create group", type = MediaType.UTROLL_API_GROUP, method = "createGroup"),
			@InjectLink(resource = GroupResource.class, style = Style.ABSOLUTE, rel = "groups", title = "Latest groups", type = MediaType.UTROLL_API_GROUP_COLLECTION) })
	private List<Link> links;

	// Style.ABSOLUTE -> vamos a ver la URI absoluta
	// (http://localhost:[p]/beeter-api/) la última barra viene de que el método
	// BeeterRootAPIResource tiene @Path("/")

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
}