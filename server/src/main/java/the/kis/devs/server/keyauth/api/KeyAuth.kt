package the.kis.devs.server.keyauth.api

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import com.mashape.unirest.http.exceptions.UnirestException
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.HttpClients
import org.json.JSONObject
import the.kis.devs.server.keyauth.user.UserData
import java.lang.Exception
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class KeyAuth(
    private val appname : String,
    private val ownerid : String,
    private val version : String,
    private val url : String
) {
    var sessionid : String? = null
    var initialized = false
    var userData : UserData? = null

    companion object {
        init {
            try {
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    override fun getAcceptedIssuers() : Array<X509Certificate>? { return null }
                    override fun checkClientTrusted(certs : Array<X509Certificate>, authType : String) {}
                    override fun checkServerTrusted(certs : Array<X509Certificate>, authType : String) {}
                })
                val sslcontext = SSLContext.getInstance("SSL")
                sslcontext.init(
                    null,
                    trustAllCerts,
                    SecureRandom()
                )
                HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.socketFactory)
                val sslsf = SSLConnectionSocketFactory(sslcontext)
                val httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build()
                Unirest.setHttpClient(httpclient)
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }

    fun init() : Boolean {
        val response : HttpResponse<String>
        try {
            response = Unirest.post(url).field("type", "init").field("ver", version).field("name", appname)
                .field("ownerid", ownerid).asString()
            try {
                val responseJSON = JSONObject(response.body)
                if (!response.body.equals("KeyAuth_Invalid", ignoreCase = true) && responseJSON.getBoolean("success")) {
                    sessionid = responseJSON.getString("sessionid")
                    initialized = true
                    return true
                }
            } catch (ignored : Exception) { }
        } catch (e : UnirestException) { e.printStackTrace() }
        return false
    }

    fun license(
        key : String, 
        hwid : String
    ) : Boolean {
        init()

        val response : HttpResponse<String>

        try {
            response = Unirest.post(url).field("type", "license").field("key", key).field("hwid", hwid)
                .field("sessionid", sessionid).field("name", appname).field("ownerid", ownerid).asString()
            try {
                val responseJSON = JSONObject(response.body)
                if (responseJSON.getBoolean("success")) {
                    userData = UserData(responseJSON)
                    return true
                }
            } catch (ignored : Exception) { }
        } catch (e : UnirestException) {
            e.printStackTrace()
        }
        return false
    }

    init {
        init()
    }
}