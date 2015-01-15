package edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.InjectLink.Style;

import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.FriendListResource;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.MediaType;

public class FriendListCollection {
	@InjectLinks({ @InjectLink(resource = FriendListResource.class, style = Style.ABSOLUTE, rel = "create-friendlist", title = "Create friendlist", type = MediaType.UTROLL_API_FRIENDLIST_COLLECTION) })
	private List<Link> links;
	private List<FriendList> friendlist;
	public FriendListCollection() {
		super();
		friendlist = new ArrayList<>();
	}
	
	public void addFriend(FriendList friend) {
		friendlist.add(friend);
	}
	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public List<FriendList> getFriendlist() {
		return friendlist;
	}

	public void setFriendlist(List<FriendList> friendlist) {
		this.friendlist = friendlist;
	}
}
