package edu.upc.eetac.dsa.dsaqt1415g4.utroll.api;

import java.util.HashMap;
import java.util.Map;

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
    private int votedBy;
    private String vote;
    private Map<String, Link> links = new HashMap<String, Link>();
    private String eTag;

    @Override //ESTO SE INCLUYE PARA EL SPINNER
    public String toString() {
        return this.name;
    }

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

    public int getVotedBy() {
        return votedBy;
    }

    public void setVotedBy(int votedBy) {
        this.votedBy = votedBy;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
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