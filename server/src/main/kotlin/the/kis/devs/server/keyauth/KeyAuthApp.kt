package the.kis.devs.server.keyauth;

import the.kis.devs.server.keyauth.api.KeyAuth;

/**
 * @author SprayD
 */
public class KeyAuthApp {
	private static final String url = "https://keyauth.win/api/1.1/";
	
	private static final String ownerid = "hW04ojuQKY"; // You can find out the owner id in the profile settings keyauth.win
	private static final String appname = "kismanccplus"; // Application name
	private static final String version = "1.0"; // Application version

	public static KeyAuth keyAuth = new KeyAuth(appname, ownerid, version, url);
}
