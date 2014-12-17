package edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api;

public interface MediaType {
	public final static String UTROLL_API_USER = "application/vnd.uTroll.api.user+json";
	public final static String UTROLL_API_FRIENDLIST = "application/vnd.uTroll.api.friendlist+json";
	public final static String UTROLL_API_GROUP = "application/vnd.uTroll.api.group+json";
	public final static String UTROLL_API_GROUP_COLLECTION = "application/vnd.uTroll.api.group.collection+json";
	public final static String UTROLL_API_COMMENT = "application/vnd.uTroll.api.comment+json";
	public final static String UTROLL_API_COMMENT_COLLECTION = "application/vnd.uTroll.api.comment.collection+json";
	
	public final static String UTROLL_API_ERROR = "application/vnd.dsa.uTroll.error+json";
}