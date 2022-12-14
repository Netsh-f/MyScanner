/**
 * Project Name: bhpan
 *    File Name: HttpFilePost.java
 *   Programmer: Tony Skywalker
 *   Start Date: November 28, 2022
 *  Last Update:
 *     Overview: Complex post task for file.
 */


package com.buaa.bhpan.utils;

import com.buaa.bhpan.exception.UploadFailException;

import okhttp3.*;

import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * This helps to post PDF file with data-form method.
 */
public class HttpFilePost {
	private static OkHttpClient client = null;
	private static final int CONNECT_TIMEOUT = 10000;
	private static final int READ_TIMEOUT = 10000;
	private static final int MAX_CONNECTION = 20;
	private static final int KEEP_ALIVE_DURATION = 20;

	static {
		// Avoid verification of local path.
		X509TrustManager manager = SSLSocketClientUtil.getX509TrustManager();
		client = new OkHttpClient.Builder()
				.connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
				.readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
				.sslSocketFactory(SSLSocketClientUtil.getSocketFactory(manager), manager)
				.hostnameVerifier(SSLSocketClientUtil.getHostnameVerifier())
				.connectionPool(new ConnectionPool(MAX_CONNECTION, KEEP_ALIVE_DURATION, TimeUnit.MINUTES))
				.build();
	}

	/**
	 * Upload file to bhpan with data-form
	 * @param url target url
	 * @param headers request headers
	 * @param dataFormBuilder a callback to fill data form
	 * @return upload request response
	 * @throws UploadFailException
	 */
	public static String post(final String url, final Map<String, String> headers, IDataFormBuilder dataFormBuilder) throws UploadFailException {
		final String BOUNDARY = "-------------------------" + UUID.randomUUID().toString();

		// Construct request header.
		Request.Builder requestBuilder = new Request.Builder().url(url);
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			requestBuilder.addHeader(entry.getKey(), entry.getValue());
		}
		requestBuilder.addHeader("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

		// Construct request data-form.
		MultipartBody.Builder formBuilder = new MultipartBody.Builder(BOUNDARY).setType(MultipartBody.FORM);
		dataFormBuilder.addContent(formBuilder);

		// Build request.
		Request request = requestBuilder.url(url).post(formBuilder.build()).build();
		Response response = null;
		try {
			response = client.newCall(request).execute();
		} catch (IOException e) {
			throw new UploadFailException("Failed to upload file");
		}

		return response.body().toString();
	}
}
