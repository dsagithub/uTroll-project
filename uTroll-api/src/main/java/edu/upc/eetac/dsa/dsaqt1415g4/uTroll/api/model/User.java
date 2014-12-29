package edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model;

import java.util.List;

import javax.ws.rs.core.Link;

import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.InjectLink.Style;

import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.MediaType;
import edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.UserResource;

public class User {
	private String username;
	private String password;
	private String name;
	private String email;
	private int age;
	private int points;
	private int points_max;
	private int groupid;
	private boolean isTroll;
	private boolean loginSuccessful;
	
	@InjectLinks({ @InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "create-user", title = "Create user", type = MediaType.UTROLL_API_USER) })
	private List<Link> links;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getPoints_max() {
		return points_max;
	}

	public void setPoints_max(int points_max) {
		this.points_max = points_max;
	}

	public int getGroupid() {
		return groupid;
	}

	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}

	public boolean isTroll() {
		return isTroll;
	}

	public void setTroll(boolean isTroll) {
		this.isTroll = isTroll;
	}

	public boolean isLoginSuccessful() {
		return loginSuccessful;
	}

	public void setLoginSuccessful(boolean loginSuccessful) {
		this.loginSuccessful = loginSuccessful;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
	
}