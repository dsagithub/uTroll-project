package edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model;

public class FriendList {
	private int friendshipid;
	private String friend1;
	private String friend2;
	private String state;

	public int getFriendshipid() {
		return friendshipid;
	}

	public void setFriendshipid(int friendshipid) {
		this.friendshipid = friendshipid;
	}

	public String getFriend1() {
		return friend1;
	}

	public void setFriend1(String friend1) {
		this.friend1 = friend1;
	}

	public String getFriend2() {
		return friend2;
	}

	public void setFriend2(String friend2) {
		this.friend2 = friend2;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
