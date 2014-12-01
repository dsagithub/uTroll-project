package edu.upc.eetac.dsa.dsaqt1415g4.uTroll.api;

import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class uTrollApplication extends ResourceConfig {
	public uTrollApplication() {
		super();
		register(DeclarativeLinkingFeature.class);
	}
}
