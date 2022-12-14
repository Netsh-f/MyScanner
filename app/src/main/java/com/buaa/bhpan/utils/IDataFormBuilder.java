/**
 * Project Name: bhpan
 *    File Name: IDataFormBuilder.java
 *   Programmer: Tony Skywalker
 *   Start Date: November 28, 2022
 *  Last Update:
 *     Overview: An interface to build data-form for http client.
 */

package com.buaa.bhpan.utils;

import okhttp3.MultipartBody;

/**
 * For custom data-form creation.
 */
public interface IDataFormBuilder {
	/**
	 * Add content to multipart entity builder.
	 * @param builder the builder of the form
	 */
	void addContent(MultipartBody.Builder builder);
}
