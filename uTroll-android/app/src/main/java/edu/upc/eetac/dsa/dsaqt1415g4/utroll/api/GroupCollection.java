package edu.upc.eetac.dsa.dsaqt1415g4.utroll.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupCollection {
    private List<Group> groups;
    private Map<String, Link> links = new HashMap<String, Link>();

    public GroupCollection() {
        super();
        groups = new ArrayList<Group>();
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public Map<String, Link> getLinks() {
        return links;
    }

    public void setLinks(Map<String, Link> links) {
        this.links = links;
    }
}