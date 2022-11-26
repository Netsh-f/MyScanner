/**
 * Project Name: Imagine
 *    File Name: IFilter.java
 *   Programmer: Tony Skywalker
 *   Start Date: November 18, 2022
 *  Last Update:
 *     Overview:
 */

package com.buaa.imagine.filter;

import org.opencv.core.Mat;

/**
 * Interface for all filters.
 */
public interface IFilter {
	/**
	 * Perform specific filter.
	 * @param mat the source image Mat
	 * @return filtered image Mat
	 */
	Mat perform(Mat mat);

	/**
	 * Set filter's params.
	 * @param params must in [0.0, 1.0]
	 */
	void setParam(double... params);
}
