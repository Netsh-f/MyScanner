/**
 * Project Name: Imagine
 *    File Name: DocumentFilter.java
 *   Programmer: Tony Skywalker
 *   Start Date: November 18, 2022
 *  Last Update:
 *     Overview: Just like a scanner. :)
 */

package com.buaa.imagine.filter;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class DocumentFilter extends Filter {
	final int SHARPEN_AMOUNT = 101;
	final int GAUSSIAN_BLUR_SIZE = 101;
	private double gamma = 5.0;	// Parameter for gamma adjust

	/**
	 * Set Parameter.
	 * @param params gamma[0.0, 1.0]
	 */
	@Override
	public void setParams(double... params) {
		if (params.length >= 1) {
			gamma = params[0];
			if (gamma < 0.0) {
				gamma = 0.0;
			} else if (gamma > 1.0) {
				gamma = 1.0;
			}
			gamma = 0.5 + gamma * gamma * 20;
		}
	}

	/**
	 * Perform document filter, including B&W, color enhance.
	 * @param mat the source image Mat
	 * @return filtered image Mat
	 */
	@Override
	public Mat perform(Mat mat) {
		Mat src = mat.clone();	// Do not damage the original one.

		Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
		src.convertTo(src, CvType.CV_32FC1, 1.0 / 255.0);

		Mat ret = reduceBackground(src);

		// This seems to be useless...
		// Imgproc.GaussianBlur(ret, ret, new Size(1, 1), 0, 0, 4);

		ret = gammaAdjust(ret); // Enhance image.

		// This may add some unwanted noise. :(
		// Imgproc.adaptiveThreshold(ret, ret, 255, 0, 0, 31, 10);

		return ret;
	}

	/**
	 * Reduce image background color and sharpen text.
	 * @param src source Mat
	 * @return modified Mat
	 */
	private Mat reduceBackground(Mat src) {
		Mat gauss = new Mat();
		Mat temp = new Mat();
		Mat ret = new Mat();

		Imgproc.blur(src, gauss, new Size(GAUSSIAN_BLUR_SIZE, GAUSSIAN_BLUR_SIZE));
		Core.divide(src, gauss, temp);

		temp = sharpen(temp, SHARPEN_AMOUNT);
		temp.convertTo(ret, CvType.CV_8UC1, 255);

		return ret;
	}

	/**
	 * Sharpen image.
	 * @param src source image Mat
	 * @param amount Err... the amount of sharpen
	 * @return modified image Mat
	 */
	private Mat sharpen(Mat src, int amount) {
		Mat dest = new Mat();
		Mat blurMat = new Mat();
		double sigma = 3.0;
		double beta = amount / 100.0;

		Imgproc.GaussianBlur(src, blurMat, new Size(7, 7), sigma, sigma, 4);
		Mat temp = new Mat();

		Core.subtract(src, blurMat, temp);
		Core.addWeighted(src, 1, temp, beta, 0, dest);

		return dest;
	}

	/**
	 * Adjust brightness and contrast of image.
	 * @param src the source image Mat
	 * @return modified image Mat
	 */
	private Mat gammaAdjust(Mat src) {
		Mat temp = src.clone();
		Mat ret = src.clone();

		src.convertTo(temp, CvType.CV_64F, 1.0 / 255, 0);
		Core.pow(temp, gamma, ret);
		ret.convertTo(ret, CvType.CV_8U, 255, 0);

		return ret;
	}
}
