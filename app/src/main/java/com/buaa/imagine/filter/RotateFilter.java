package com.buaa.imagine.filter;

import org.opencv.core.Core;
import org.opencv.core.Mat;

public class RotateFilter extends Filter {
	/**
	 * Rotate the image by 90 degree clockwise.
	 * @param mat the source image Mat
	 * @return filtered image Mat
	 */
	@Override
	public Mat perform(Mat mat) {
		Mat transpose = new Mat();
		Core.transpose(mat, transpose);
		Mat flip = new Mat();
		Core.flip(transpose, flip, 1);

		return flip;
	}
}
