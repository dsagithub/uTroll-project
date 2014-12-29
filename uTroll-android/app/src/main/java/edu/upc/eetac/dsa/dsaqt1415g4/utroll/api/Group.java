package edu.upc.eetac.dsa.dsaqt1415g4.utroll.api;

import java.util.HashMap;
import java.util.Map;

public class Group {
    private int groupid;
    private String groupname;
    private int price;
    private long endingTimestamp;
    private long creationTimestamp;
    private String creator;
    private String troll;
    private String state;
    private Map<String, Link> links = new HashMap<String, Link>();
    private String eTag;

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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getTroll() {
        return troll;
    }

    public void setTroll(String troll) {
        this.troll = troll;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Map<String, Link> getLinks() {
        return links;
    }

    public void setLinks(Map<String, Link> links) {
        this.links = links;
    }

    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }
}