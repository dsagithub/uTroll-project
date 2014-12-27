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

public class GroupCollection {
	@InjectLinks({
			@InjectLink(resource = GroupResource.class, style = Style.ABSOLUTE, rel = "create-group", title = "Create group", type = MediaType.UTROLL_API_GROUP) })
	private List<Link> links;
	private List<Group> groups;
	//private long newestTimestamp;
	//private long oldestTimestamp;

	public GroupCollection() {
		super();
		groups = new ArrayList<>();
	}
	
	public void addGroup(Group group) {
		groups.add(group);
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	
}