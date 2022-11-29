/**
 * Project Name: Imagine
 *    File Name: OriginalFilter.java
 *   Programmer: Tony Skywalker
 *   Start Date: November 18, 2022
 *  Last Update:
 *     Overview:
 */

package com.buaa.imagine.filter;

import org.opencv.core.Mat;

public class OriginalFilter extends Filter {
	/**
	 * Perform original filter. This actually does nothing.
	 * @param mat the source image Mat
	 * @return filtered image Mat
	 */
	@Override
	public Mat perform(Mat mat) {
		return mat.clone();
	}
}
