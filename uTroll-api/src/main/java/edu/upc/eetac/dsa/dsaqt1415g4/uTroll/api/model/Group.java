package edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model;

import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.InjectLink.Style;

import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.CommentResource;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.GroupResource;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.UserResource;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.MediaType;

public class Group {
	private int groupid;
	private String groupname;
	private int price;
	private long endingTimestamp;
	private long creationTimestamp;
	private String creator;
	private String state;
	private String troll;
	
	@InjectLinks({
		@InjectLink(resource = GroupResource.class, style = Style.ABSOLUTE, rel = "create-group", title = "Create group", type = MediaType.UTROLL_API_GROUP),
		@InjectLink(resource = GroupResource.class, style = Style.ABSOLUTE, rel = "self edit", title = "Self edit", type = MediaType.UTROLL_API_GROUP, method = "getGroup", bindings = @Binding(name = "groupid", value = "${instance.groupid}")),
		@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "join group", title = "Join group", type = MediaType.UTROLL_API_USER, method = "joinGroup", bindings = @Binding(name = "groupid", value = "${instance.groupid}")),
		@InjectLink(resource = GroupResource.class, style = Style.ABSOLUTE, rel = "update group state", title = "Update group state", type = MediaType.UTROLL_API_GROUP, method = "updateGroup", bindings = @Binding(name = "groupid", value = "${instance.groupid}")),
		@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "get users", title = "Get the users in this group", type = MediaType.UTROLL_API_USER_COLLECTION, method = "getUsersInGroup", bindings = @Binding(name = "groupid", value = "${instance.groupid}"))
		})
	private List<Link> links;

	public int getGroupid() {
		return groupid;
	}

	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public long getEndingTimestamp() {
		return endingTimestamp;
	}

	public void setEndingTimestamp(long endingTimestamp) {
		this.endingTimestamp = endingTimestamp;
	}

	public long getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(long creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public String getTroll() {
		return troll;
	}

	public void setTroll(String troll) {
		this.troll = troll;
	}

}
