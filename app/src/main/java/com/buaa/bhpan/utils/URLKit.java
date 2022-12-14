/**
 * Project Name: bhpan
 *    File Name: URLKit.java
 *   Programmer: Tony Skywalker
 *   Start Date: November 28, 2022
 *  Last Update:
 *     Overview: Kit for request url.
 */

package com.buaa.bhpan.utils;

/**
 * Provide all request urls.
 */
public class URLKit {
	// server url
	private static final String BASE_URL = "https://bhpan.buaa.edu.cn/api/v1/";

	// request modes
	private static final String MODE_AUTH = "auth1";
	private static final String MODE_LINK = "link";

	// specific methods
	private static final String METHOD_HEAD = "?method=";
	private static final String GET_CONFIG = "getconfig";
	private static final String LIST_DIR = "listdir";
	private static final String OS_BEGIN_UPLOAD = "osbeginupload";
	private static final String OS_END_UPLOAD = "osendupload";

	/**
	 * Get getconfig request url.
	 * @return corresponding url
	 */
	public static String getconfig() {
		return BASE_URL + MODE_AUTH + METHOD_HEAD + GET_CONFIG;
	}

	/**
	 * Get listdir request url.
	 * @return corresponding url
	 */
	public static String listdir() {
		return BASE_URL + MODE_LINK + METHOD_HEAD + LIST_DIR;
	}

	/**
	 * Get osbeginupload request url.
	 * @return corresponding url
	 */
	public static String osbeginupload() {
		return BASE_URL + MODE_LINK + METHOD_HEAD + OS_BEGIN_UPLOAD;
	}

	/**
	 * Get osendupload request url.
	 * @return corresponding url
	 */
	public static String osendupload() {
		return BASE_URL + MODE_LINK + METHOD_HEAD + OS_END_UPLOAD;
	}
}
