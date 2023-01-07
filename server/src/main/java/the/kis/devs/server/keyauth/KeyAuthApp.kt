package the.kis.devs.server.keyauth

import the.kis.devs.server.keyauth.api.KeyAuth

/**
 * @author SprayD
 */
object KeyAuthApp {
    private const val url = "https://keyauth.win/api/1.1/"
    private const val ownerid = "hW04ojuQKY" // You can find out the owner id in the profile settings keyauth.win
    private const val appname = "kismanccplus" // Application name
    private const val version = "1.0" // Application version
    var keyAuth = KeyAuth(appname, ownerid, version, url)
}