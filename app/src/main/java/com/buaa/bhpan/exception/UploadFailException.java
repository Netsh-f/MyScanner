/**
 * Project Name: bhpan
 *    File Name: UploadFailException.java
 *   Programmer: Tony Skywalker
 *   Start Date: November 28, 2022
 *  Last Update:
 *     Overview:
 */

package com.buaa.bhpan.exception;

/**
 * The exception to throw when upload fails.
 */
public class UploadFailException extends Exception {
	/**
	 * Constructor of the object.
	 * @param s the reason why
	 */
	public UploadFailException(String s) {
		super(s);
	}
}