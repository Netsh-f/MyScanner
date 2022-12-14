/**
 * Project Name: bhpan
 *    File Name: SSLSocketClientUtil.java
 *   Programmer: Tony Skywalker
 *   Start Date: December 14, 2022
 *  Last Update:
 *     Overview: To solve https verification problem.
 */

package com.buaa.bhpan.utils;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * Avoid https verification. This is not intend to be seen other than HttpPost.
 **/
class SSLSocketClientUtil {
	protected static SSLSocketFactory getSocketFactory(TrustManager manager) {
		SSLSocketFactory socketFactory = null;
		try {
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, new TrustManager[]{manager}, new SecureRandom());
			socketFactory = sslContext.getSocketFactory();
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			e.printStackTrace();
		}

		return socketFactory;
	}

	/**
	 * This will do nothing with verification.
	 * @return custom trust manager.
	 */
	protected static X509TrustManager getX509TrustManager() {
		return new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		};
	}

	/**
	 * Just do not verify anything.
	 * @return custom hostname verifier.
	 */
	protected static HostnameVerifier getHostnameVerifier() {
		HostnameVerifier hostnameVerifier = new HostnameVerifier() {
			@Override
			public boolean verify(String s, SSLSession sslSession) {
				return true;
			}
		};

		return hostnameVerifier;
	}
}
