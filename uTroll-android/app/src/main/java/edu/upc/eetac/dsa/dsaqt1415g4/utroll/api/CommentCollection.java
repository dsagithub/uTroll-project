package edu.upc.eetac.dsa.dsaqt1415g4.utroll.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentCollection {
    private List<Comment> comments;
    private Map<String, Link> links = new HashMap<String, Link>();

    public CommentCollection() {
        super();
        comments = new ArrayList<Comment>();
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Map<String, Link> getLinks() {
        return links;
    }

    public void setLinks(Map<String, Link> links) {
        this.links = links;
    }
}