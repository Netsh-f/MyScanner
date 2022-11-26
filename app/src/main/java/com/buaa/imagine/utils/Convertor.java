/**
 * Project Name: Imagine
 *    File Name: Convertor.java
 *   Programmer: Tony Skywalker
 *   Start Date: November 18, 2022
 *  Last Update:
 *     Overview: Some conversion works.
 */

package com.buaa.imagine.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

/**
 * Utility class to convert Bitmap and OpenCV Mat.
 * Comments: Windows doesn't seem to have Bitmap and Utils package.
 */
public class Convertor {
	/**
	 * Convert OpenCV Mat to Bitmap.
 	 * @param mat the Mat to convert
	 * @return converted Bitmap
	 */
	public static Bitmap matToBitmap(Mat mat) {

		Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.RGB_565);
		Utils.matToBitmap(mat, bitmap);

		return bitmap;
	}


	/**
	 * Convert Bitmap to OpenCV Mat.
	 * @param bitmap the Bitmap to convert
	 * @return converted OpenCV Mat
	 */
	public static Mat bitmapToMat(Bitmap bitmap) {

		Mat mat = new Mat();
		Utils.bitmapToMat(bitmap, mat);

		return mat;
	}
}
