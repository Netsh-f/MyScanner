/**
 * Project Name: bhpan
 *    File Name: HttpPost.java
 *   Programmer: Tony Skywalker
 *   Start Date: November 28, 2022
 *  Last Update:
 *     Overview: Simple post task with json.
 */

package com.buaa.bhpan.utils;

import com.buaa.bhpan.exception.UploadFailException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * This helps to post simple JSON data.
 */
public class HttpJSONPost {
	/**
	 * Post json data to the server at given url link.
	 *
	 * @param link the target url link
	 * @param json string form json data
	 * @return return target server's response
	 * @throws UploadFailException
	 */
	public static JSONObject post(String link, String json) throws UploadFailException {
		String response = "";
		BufferedReader reader = null;
		HttpURLConnection connection = null;

		try {
			trustAllHosts();
			URL url = new URL(link);
			if ("https".equalsIgnoreCase(url.getProtocol())) {
				HttpsURLConnection httpsConnection = (HttpsURLConnection) url.openConnection();
				httpsConnection.setHostnameVerifier(DO_NOT_VERIFY);
				connection = httpsConnection;
			} else {
				connection = (HttpURLConnection) url.openConnection();
			}

			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			connection.setRequestProperty("accept", "application/json");

			if (json != null) {
				byte[] bytes = json.getBytes();
				connection.setRequestProperty("Content-Length", String.valueOf(bytes.length));
				OutputStream outputStream = connection.getOutputStream();
				outputStream.write(bytes);
				outputStream.flush();
				outputStream.close();
			}

			if (connection.getResponseCode() == 200) {
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
				response = reader.readLine();
			}
		} catch (IOException e) {
			throw new UploadFailException("Post failed");
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					throw new UploadFailException("Post failed");
				}
			}
		}

		JSONObject obj;
		try {
			obj = new JSONObject(response);
			return obj;
		} catch (JSONException e) {
			return null;
		}
	}

	/**
	 * Hmm... An internal class for hostname verification.
	 */
	private static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		@Override
		public boolean verify(String s, SSLSession sslSession) {
			return true;
		}
	};

	/**
	 * A void https certification problems.
	 */
	private static void trustAllHosts() {
		TrustManager[] trustAllCertifications = new TrustManager[]{
				new X509TrustManager() {
					@Override
					public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

					}

					@Override
					public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

					}

					@Override
					public X509Certificate[] getAcceptedIssuers() {
						return new X509Certificate[]{};
					}
				}
		};

		try {
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustAllCertifications, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			throw new RuntimeException();
		}
	}
}