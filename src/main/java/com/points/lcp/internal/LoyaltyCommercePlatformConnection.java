package com.points.lcp.internal;

import java.net.URL;
import java.util.Date;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * This class represents an extension connection just as example (there is no real connection with anything here c:).
 */
public final class LoyaltyCommercePlatformConnection {
	
	private static String contentType="application/json";
	private static Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

  private final String server;
  private final String lpId;
  private final String macId;
  private final String macKey;
  private final HttpClient httpClient;

  public LoyaltyCommercePlatformConnection(HttpClient httpClient, String server, String lpId, String macId, String macKey) {
	this.httpClient = httpClient;
    this.server = server;
    this.lpId = lpId;
    this.macId = macId;
    this.macKey = macKey;
  }

  public String getServer() {
    return server;
  }
  
  public String getLpId() {
    return lpId;
  }
  
  public String getMacId() {
    return macId;
  }
  
  public String getMacKey() {
    return macKey;
  }

  public void invalidate() {
    // do something to invalidate this connection!
  }
  
  public JsonObject callLCP(JsonObject request, String method) throws Exception{
	  String fullUrl = "https://"+server+"/v1/lps/"+lpId+"/"+method+"/";
	  HttpPost httpPost = new HttpPost(fullUrl);
	  String requestBody = gson.toJson(request);
	  String authorizationHeader = generateAuthorizationHeader(macId, macKey, contentType, requestBody, new URL(fullUrl));
	  httpPost.addHeader("Authorization", authorizationHeader);
	  httpPost.addHeader("Content-type",contentType);
	  httpPost.setEntity(new StringEntity(requestBody,ContentType.APPLICATION_JSON));
	  HttpResponse response = httpClient.execute(httpPost);
	  return null;
  }
  
  private static String generateAuthorizationHeader(String macId, String macKey,
			String contentType, String requestBody, URL lcpURL)
			throws Exception {
		// Step 1: Generate epoch time in seconds
		String ts = "" + new Date().getTime();
		ts = ts.substring(0, ts.length() - 3);

		// Step 2: Generate nonce
		byte[] noncebytes = new byte[8];
		new Random(new Date().getTime()).nextBytes(noncebytes);
		String nonce = new String(Base64.encodeBase64(noncebytes));

		// Step 3: Generate ext
		String ext = "";
		if (requestBody != null && contentType != null) {
			ext = DigestUtils.shaHex(contentType + requestBody);
		}

		// Step 4: Build normalized request string
		String normalizedRequestString = ts + "\n" + nonce + "\nPOST\n"
				+ lcpURL.getPath() + "\n" + lcpURL.getHost() + "\n"
				+ "443" + "\n" + ext + "\n";

		// Step 5: Base64 decode the MAC key from URL-safe alphabet
		macKey = macKey.replaceAll("-", "+").replaceAll("_", "/");
		if (macKey.length() % 4 > 0) {
			macKey+="===".substring(0,4-(macKey.length() % 4));
		}
		Mac sha1_HMAC = Mac.getInstance("HmacSHA1");
		SecretKeySpec secret_key = new SecretKeySpec(Base64.decodeBase64(macKey
				.getBytes()), "HmacSHA1");
		sha1_HMAC.init(secret_key);
		String mac = new String(Base64.encodeBase64(sha1_HMAC
				.doFinal(normalizedRequestString.getBytes())));

		// Step 8: Build Authorization header
		StringBuffer authorizationHeader = new StringBuffer("MAC ");
		authorizationHeader.append("id=\"").append(macId).append("\", ");
		authorizationHeader.append("ts=\"").append(ts).append("\", ");
		authorizationHeader.append("nonce=\"").append(nonce).append("\", ");
		authorizationHeader.append("ext=\"").append(ext).append("\", ");
		authorizationHeader.append("mac=\"").append(mac).append("\"");

		return authorizationHeader.toString();
	}
}
