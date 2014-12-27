package edu.upc.eetac.dsa.dsaqt1415g4.utroll.api;

import java.util.HashMap;
import java.util.Map;

public class uTrollRootAPI {

    private Map<String, Link> links;

    public uTrollRootAPI() {
        links = new HashMap<String, Link>();
    }

    public Map<String, Link> getLinks() {
        return links;
    }

}