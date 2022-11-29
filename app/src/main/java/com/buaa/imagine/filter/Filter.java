/**
 * Project Name: Imagine
 *    File Name: IFilter.java
 *   Programmer: Tony Skywalker
 *   Start Date: November 18, 2022
 *  Last Update: November 29, 2022
 *     Overview:
 */

package com.buaa.imagine.filter;

import android.graphics.Bitmap;

import com.buaa.imagine.utils.Convertor;

import org.opencv.core.Mat;

/**
 * Interface for all filters.
 */
public abstract class Filter {
	/**
	 * Set filter's params.
	 * @param params must in [0.0, 1.0]
	 */
	public void setParams(double... params) {
	}

	/**
	 * Perform specific filter.
	 * @param mat the source image Mat
	 * @return filtered image Mat
	 */
	public abstract Mat perform(Mat mat);

	/**
	 * Perform specific filter to Bitmap.
	 * @param bitmap the source image
	 * @return filtered image
	 */
	public Bitmap perform(Bitmap bitmap) {
		return Convertor.matToBitmap(perform(Convertor.bitmapToMat(bitmap)));
	}
}
