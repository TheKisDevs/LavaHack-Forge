package com.kisman.cc.util.protect.keyauth.api;

import com.kisman.cc.Kisman;
import com.kisman.cc.util.protect.keyauth.util.HWID;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class KeyAuth {
	public final String appname;
	public final String ownerid;
	public final String version;
	public final String url;

	protected String sessionid;
	protected boolean initialized;

	public KeyAuth(String appname, String ownerid, String version, String url) {
		this.appname = appname;
		this.ownerid = ownerid;
		this.version = version;
		this.url = url;
	}

	static {
		try {
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				public void checkClientTrusted(X509Certificate[] certs, String authType) {}
				public void checkServerTrusted(X509Certificate[] certs, String authType) {}
			}};

			SSLContext sslcontext = SSLContext.getInstance("SSL");
			sslcontext.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
			CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
			Unirest.setHttpClient(httpclient);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void init() {
		try {
			HttpResponse<String> response = Unirest.post(url).field("type", "init").field("ver", version).field("name", appname).field("ownerid", ownerid).asString();

			try {
				JSONObject responseJSON = new JSONObject(response.getBody());

				if (responseJSON.getBoolean("success")) {
					sessionid = responseJSON.getString("sessionid");
					initialized = true;
					return;
				}
			} catch (Exception e) {e.printStackTrace();}
		} catch (UnirestException e) {e.printStackTrace();}
		Kisman.unsafeCrash();
	}

	public boolean license(String key) {
		if (!initialized) {
			init();
		}

		try {
			HttpResponse<String> response = Unirest.post(url).field("type", "license").field("key", key).field("hwid", HWID.getHWID()).field("sessionid", sessionid).field("name", appname).field("ownerid", ownerid).asString();

			try {
				if (new JSONObject(response.getBody()).getBoolean("success")) return true;
			} catch (Exception e) {e.printStackTrace();}
		} catch (UnirestException e) {e.printStackTrace();}
		Kisman.unsafeCrash();
		return false;
	}
}
