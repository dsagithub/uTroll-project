package edu.upc.eetac.dsa.dsaqt1415g4.utroll.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserCollection {
    private List<User> users;
    private Map<String, Link> links = new HashMap<String, Link>();

    public UserCollection() {
        super();
        users = new ArrayList<User>();
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Map<String, Link> getLinks() {
        return links;
    }

    public void setLinks(Map<String, Link> links) {
        this.links = links;
    }
}