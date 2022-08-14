package the.kis.devs.server.keyauth.user

import org.json.JSONObject

class UserData(
    json: JSONObject
) {
    val username: String
    val subscription: String
    val expiry: String

    init {
        val info = json.getJSONObject("info")
        val subArray = info.getJSONArray("subscriptions")
        val subObject = subArray.getJSONObject(0)
        username = info.getString("username")
        subscription = subObject.getString("subscription")
        expiry = subObject.getString("expiry")
    }
}