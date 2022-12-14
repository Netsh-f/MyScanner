/**
 * Project Name: bhpan
 *    File Name: JSONUtils.java
 *   Programmer: Tony Skywalker
 *   Start Date: November 28, 2022
 *  Last Update:
 *     Overview: Some tools for json.
 */

package com.buaa.bhpan.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Utility tool for JSON object.
 */
public class JSONUtils {
	/**
	 * Convert map to json string.
	 * @param map keys and values
	 * @return json string
	 */
	public static String mapToJSONString(Map<String, Object> map) {
//		try {
			JSONObject jsonObject = new JSONObject(map);
			return jsonObject.toString();
//		} catch (JSONException e) {
//			return "";
//		}
	}

	/**
	 * Convert map to json object.
	 * @param map keys and values
	 * @return json object, null if any error occurs
	 */
	public static JSONObject mapToJSON(Map<String, Object> map) {
//		try {
			JSONObject obj = new JSONObject(map);
			return obj;
//		} catch (JSONException e) {
//			return null;
//		}
	}

	/**
	 * A utility function to get the key of a json entry.
	 * @param entry json entry, e.g. "name: Tony"
	 * @return the key, e.g. "name"
	 */
	public static String entryKey(String entry) {
		int index = entry.indexOf(':');
		return entry.substring(0, index);
	}

	/**
	 * A utility function to get the value of a json entry
	 * @param entry json entry
	 * @return the value
	 */
	public static String entryValue(String entry) {
		int index = entry.indexOf(':');		// first occurrence
		index++;
		while (entry.charAt(index) == ' ') {
			index++;
		}
		return entry.substring(index);
	}
}
