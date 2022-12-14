/**
 * Project Name: bhpan
 * File Name: BHPan.java
 * Programmer: Tony Skywalker
 * Start Date: November 28, 2022
 * Last Update:
 * Overview: All-in-one solution for file upload.
 */

package com.buaa.bhpan;

import com.buaa.bhpan.exception.UploadFailException;
import com.buaa.bhpan.utils.HttpFilePost;
import com.buaa.bhpan.utils.HttpJSONPost;
import com.buaa.bhpan.utils.IDataFormBuilder;
import com.buaa.bhpan.utils.JSONUtils;
import com.buaa.bhpan.utils.URLKit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * All-in-one class for BHPan PDF file upload.
 */
public class BHPan {
	/**
	 * Upload file to BHPan.
	 *
	 * <p>
	 * Upload file to bhpan needs three major steps:<br/>
	 * 1. osbeginupload: 	tell the server you are ready to upload file, and
	 * then you will get corresponding info for the following steps.<br/>
	 * 2.   upload file:	establish a connection with the bhpan server and
	 * transfer the file in form-data method.<br/>
	 * 3.   osendupload:	this step is for confirmation, file will eventually
	 * show up in the folder after this request.<br/>
	 * </p>
	 *
	 * @param link     the bhpan share link, easy to get, huh?
	 * @param filename the filepath to upload. Must exist!
	 */
	public static void upload(String link, String filename) throws UploadFailException {
		File file = new File(filename);
		if (!(file.exists() && file.isFile())) {
			throw new UploadFailException("File not found");
		}
		link = parseLink(link);
		if ("".equals(link)) {
			throw new UploadFailException("Invalid link");
		}

		// osbeginupload
		JSONObject beginResponse = osBeginUpload(link, file);
		Map<String, String> info = null;
		try {
			info = parseEssentialInfo(beginResponse);
		} catch (JSONException e) {
			info = new HashMap<>();
		}

		// upload file
		postFile(info, file);

		// osendupload
		osEndUpload(link, info);
	}


	/**
	 * Parse the original bhpan link to get the actual link.
	 *
	 * <p>
	 * Examples:<br/>
	 * https://bhpan.buaa.edu.cn:443/link/1C33857330D673B122C474135DD37207<br/>
	 * https://bhpan.buaa.edu.cn/#/link/1C33857330D673B122C474135DD37207<br/>
	 * </p>
	 *
	 * @param link the original bhpan link.
	 * @return the actual link, empty String if pattern invalid
	 */
	private static String parseLink(String link) {
		if (!link.matches("^https://bhpan.buaa.edu.cn((:443)|(/#))/link/[0-9A-Z]{32}$")) {
			return "";
		}

		return link.substring(link.lastIndexOf('/') + 1);
	}

	/**
	 * Perform osbeginupload request.
	 *
	 * <p>
	 * The format of request payload is as follows. '...' is for ellipsis.<br/>
	 * {<br/>
	 * &nbsp;&nbsp;"link":"1C3...207",<br/>
	 * &nbsp;&nbsp;"docid":"gns://930...DD9/1D5...30B/764...E92",<br/>
	 * &nbsp;&nbsp;"password":"",<br/>
	 * &nbsp;&nbsp;"length":9065544,<br/>
	 * &nbsp;&nbsp;"name":"123456.pdf",<br/>
	 * &nbsp;&nbsp;"ondup":1,<br/>
	 * &nbsp;&nbsp;"client_mtime":1662034274828000,<br/>
	 * &nbsp;&nbsp;"reqhost":"bhpan.buaa.edu.cn",<br/>
	 * &nbsp;&nbsp;"reqmethod":"POST",<br/>
	 * &nbsp;&nbsp;"usehttps":true<br/>
	 * }
	 * </p>
	 *
	 * @param link the actual link
	 * @param file the file to upload
	 * @return response json from server
	 * @throws UploadFailException
	 */
	private static JSONObject osBeginUpload(String link, File file) throws UploadFailException {
		HashMap<String, Object> map = new HashMap<>();

		map.put("link", link);
		// map.put("docid", "");	// doesn't need?
		map.put("password", "");
		map.put("length", file.length());
		map.put("name", file.getName());
		map.put("ondup", 3);    // replace on duplication without confirmation
		map.put("client_mtime", String.format("%d000", System.currentTimeMillis()));
		map.put("reqhost", "bhpan.buaa.edu.cn");
		map.put("reqmethod", "POST");
		map.put("usehttps", true);

		return HttpJSONPost.post(URLKit.osbeginupload(), JSONUtils.mapToJSONString(map));
	}

	/**
	 * Parse essential info for transfer in response of osbeginupload request.
	 * <p>
	 * The format of the response of osbeginupload is as follows.<br/>
	 * {<br/>
	 * &nbsp;&nbsp;"authrequest":	[<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;"POST",<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;"https://p300s.buaa.edu.cn:10002/bhpan_bucket",<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;"key: 7...d-3..0-4...a-b...6-2...c/930...DD9/1C2...842",<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;"AWSAccessKeyId: bhpan_zuhu",<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;"Policy: eyJ...aIn0=",<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;"Signature: 9j0...8A=",<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;"Content-Type: application/octet-stream"<br/>
	 * &nbsp;&nbsp;],<br/>
	 * &nbsp;&nbsp;"docid":"gns://930...DD9/1D5...30B/764...E92/CEB...FC5",<br/>
	 * &nbsp;&nbsp;"name":"123456.pdf",<br/>
	 * &nbsp;&nbsp;"rev":"1C2...842"<br/>
	 * }
	 * </p>
	 *
	 * @param json response of osbeginupload
	 * @return essential info
	 * @throws JSONException
	 */
	private static Map<String, String> parseEssentialInfo(JSONObject json) throws JSONException {
		JSONArray array = (JSONArray) json.get("authrequest");
		HashMap<String, String> info = new HashMap<>();

		for (int i = 0; i < array.length(); i++) {
			String str = (String) array.get(i);
			if ("POST".equals(str)) {
				info.put("reqmethod", "POST");
			} else if (str.startsWith("http")) {
				info.put("target", str);
			} else {
				info.put(JSONUtils.entryKey(str), JSONUtils.entryValue(str));
			}
		}
		info.put("docid", ((String) json.get("docid")).replace("\\", ""));
		info.put("name", (String) json.get("name"));
		info.put("rev", (String) json.get("rev"));

		return info;
	}

	/**
	 * Post the file to bhpan server with data-form.
	 *
	 * <p>
	 * header info for post file is as follows, not all of them are needed.<br/>
	 * &nbsp;&nbsp;Connection: keep-alive<br/>
	 * &nbsp;&nbsp;Content-Length: 5364940<br/>
	 * &nbsp;&nbsp;Host: p300s.buaa.edu.cn:10002<br/>
	 *</p>
	 *
	 * @param info essential info for upload
	 * @param file the file to upload
	 * @return response... actually no response :P
	 */
	private static String postFile(Map<String, String> info, File file) throws UploadFailException {
		HashMap<String, String> header = new HashMap<>();
		header.put("Host", "p300s.buaa.edu.cn:10002");
		header.put("Connection", "keep-alive");
		header.put("Content-Length", String.format("%s", file.length()));

		String response = HttpFilePost.post(info.get("target"), header, new IDataFormBuilder() {
			@Override
			public void addContent(MultipartBody.Builder builder) {
				builder.addFormDataPart("key", info.get("key"));
				builder.addFormDataPart("AWSAccessKeyId", info.get("AWSAccessKeyId"));
				builder.addFormDataPart("Policy", info.get("Policy"));
				builder.addFormDataPart("Signature", info.get("Signature"));
				builder.addFormDataPart("Content-Type", info.get("Content-Type"));

				RequestBody body = RequestBody.create(MediaType.parse("application/pdf"), file);
				builder.addFormDataPart("file", info.get("name"), body);
			}
		});

		return response;
	}

	/**
	 * Perform osendupload request.
	 *
	 * <p>
	 * The format of request payload is as follows.<br/>
	 * {<br/>
	 * &nbsp;&nbsp;"link":"1C33857330D673B122C474135DD37207",<br/>
	 * &nbsp;&nbsp;"docid":"gns://930...DD9/1D5...30B/764...E92",<br/>
	 * &nbsp;&nbsp;"rev":"E3052E4193A2413CB893EFC2D2948DE7"<br/>
	 * }
	 * </p>
	 *
	 * @param link the actual link
	 * @param info the essential info for upload
	 * @return return the response of the server
	 * @throws UploadFailException
	 */
	private static JSONObject osEndUpload(String link, Map<String, String> info) throws UploadFailException {
		HashMap<String, Object> map = new HashMap<>();

		map.put("link", link);
		map.put("docid", info.get("docid"));
		map.put("rev", info.get("rev"));

		return HttpJSONPost.post(URLKit.osendupload(), JSONUtils.mapToJSONString(map));
	}

	public static void main(String[] args) {
		String link = "https://bhpan.buaa.edu.cn:443/link/1C33857330D673B122C474135DD37207";
		String file = "Your file name";

		try {
			upload(link, file);
			System.out.println("Upload success");
		} catch (UploadFailException e) {
			System.out.println(e.getMessage());
			System.out.println("Upload failed.");
		}
	}
}
