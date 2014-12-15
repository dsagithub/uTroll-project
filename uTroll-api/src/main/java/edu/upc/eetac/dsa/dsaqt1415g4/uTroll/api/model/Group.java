package edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api.model;

public class Group {
	private int groupid;
	private String groupname;
	private int price;
	private long endingTimestamp;
	private long creationTimestamp;
	private String state;

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

}
