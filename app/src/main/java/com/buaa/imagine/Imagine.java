/**
 * Project Name: Imagine
 *    File Name: Imagine.java
 *   Programmer: Tony Skywalker
 *   Start Date: November 18, 2022
 *  Last Update: November 21, 2022
 *     Overview: Fundamental module of the project.
 */

package com.buaa.imagine;

import android.graphics.Bitmap;

import com.buaa.imagine.filter.IFilter;
import com.buaa.imagine.utils.Convertor;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Imagine is the fundamental class, which manages all queued images.
 * It uses external Filter to modify them, or Convertor to convert to or
 * from Bitmap.
 */
public class Imagine {
	private static Imagine instance;	// Thread safe Singleton Pattern.
	public static Imagine getInstance() {
		if (instance == null) {
			synchronized (Imagine.class) {
				if (instance == null) {
					instance = new Imagine();
				}
			}
		}

		return instance;
	}

	private Imagine() {}

//	/**
//	 * TODO:
//	 * I'm not sure if Android need this...
//	 * Probably not.
//	 */
//	static {
//		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//	}

	// All data are stored as Mat.
	private ArrayList<Mat> originalImages = new ArrayList<Mat>();
	private ArrayList<Mat> modifiedImages = new ArrayList<Mat>();

	/**
	 * Get the current size of the Imagine.
	 * @return the size
	 */
	public int getSize() {
		return originalImages.size();
	}

	/**
	 * Reset the Imagine by making modifiedImages and originalImages
	 * the same.
	 */
	public void reset() {
		modifiedImages.clear();
		for (Mat mat : originalImages) {
			modifiedImages.add(mat.clone());
		}
	}


	/**
	 * Import a single image.
	 * @param bitmap the image to import
	 */
	public void importImage(Bitmap bitmap) {
		originalImages.add(Convertor.bitmapToMat(bitmap));
		modifiedImages.add(Convertor.bitmapToMat(bitmap));
	}

	/**
	 * Import a series of images.
	 * @param bitmaps the images to import
	 */
	public void importImages(ArrayList<Bitmap> bitmaps) {
		for (Bitmap bitmap : bitmaps) {
			importImage(bitmap);
		}
	}

	/**
	 * Import a series of images from given filenames.
	 * @param filenames filenames of images
	 * @throws IOException
	 */
	public void importImagesFromFilenames(ArrayList<String> filenames) throws IOException {
		for (String filename : filenames) {
			Mat mat = null;
			try {
				mat = Imgcodecs.imread(filename);
			} catch (Exception e) {
				throw new IOException();
			}
			originalImages.add(mat);
			modifiedImages.add(mat.clone());
		}
	}

	/**
	 * Import images from given directory.
	 * WARNING: Directory must exist and only have image files!
	 * @param directory
	 * @throws IOException
	 */
	public void importImagesFromDirectory(String directory) throws IOException {
		File dir = new File(directory);
		if (!(dir.isDirectory() && dir.exists())) {
			throw new IOException();
		}

		File[] files = dir.listFiles();
		ArrayList<String> filenames = new ArrayList<>();
		for (File file : files) {
			filenames.add(file.getPath().toString());
		}

		importImagesFromFilenames(filenames);
	}


	/**
	 * Export a single image.
	 * @param index index of the image to export
	 * @return the bitmap at specified index
	 */
	public Bitmap exportImage(int index) {
		Bitmap bitmap = null;

		try {
			bitmap = Convertor.matToBitmap(modifiedImages.get(index));
		} catch (IndexOutOfBoundsException e) {
			bitmap = null;
		}

		return bitmap;
	}

	/**
	 * Export a series of images in [begin, end)
	 * @param begin the beginning index
	 * @param end the ending index, which is not included
	 * @return
	 */
	public ArrayList<Bitmap> exportImages(int begin, int end) {
		ArrayList<Bitmap> exports = new ArrayList<Bitmap>();

		// Perform range check.
		if (end <= begin) {
			return exports;
		}
		if (end >= modifiedImages.size()) {
			end = modifiedImages.size();
		}

		for (int i = begin; i < end; i++) {
			exports.add(Convertor.matToBitmap(modifiedImages.get(i)));
		}

		return exports;
	}

	/**
	 * Export all images.
	 * @return all modified images
	 */
	public ArrayList<Bitmap> exportImages() {
		ArrayList<Bitmap> exports = new ArrayList<Bitmap>();

		for (Mat mat : modifiedImages) {
			exports.add(Convertor.matToBitmap(mat));
		}

		return exports;
	}

	/**
	 * Export images to given directory. The images will be stored as jpeg
	 * and will be given names from 0 to (size - 1).
	 * Warning: directory must exist!
	 * @param directory the root directory of images.
	 * @throws IOException
	 */
	public void exportImagesToDirectory(String directory) throws IOException {
		File dir = new File(directory);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		for (int i = 0; i < modifiedImages.size(); i++) {
			String filename = String.format("%d.jpeg", i);
			try {
				Imgcodecs.imwrite(Paths.get(directory, filename).toString(), modifiedImages.get(i));
			} catch (Exception e) {
				throw new IOException();
			}
		}
	}


	/**
	 * Apply filter to specified image.
	 * @param filter the filter to apply
	 * @param index the specified index of the image
	 */
	public void applyFilter(IFilter filter, int index) {
		Mat mat = null;

		try {
			mat = originalImages.get(index);
			modifiedImages.set(index, filter.perform(mat));
		} catch (IndexOutOfBoundsException e) {
			return;
		}
	}

	/**
	 * Apply filter to a range of images in [begin, end)
	 * @param filter the filter to apply
	 * @param begin the beginning index
	 * @param end the ending index, which is not included
	 */
	public void applyFilter(IFilter filter, int begin, int end) {
		for (int i = begin; i < end; i++) {
			applyFilter(filter, i);
		}
	}

	/**
	 * Apply filter to specified indexes.
	 * @param filter the filter to apply
	 * @param indexList specified indexes
	 */
	public void applyFilter(IFilter filter, ArrayList<Integer> indexList) {
		for (Integer i : indexList) {
			applyFilter(filter, i);
		}
	}

	/**
	 * Apply filter to all images.
	 * @param filter the filter to apply
	 */
	public void applyFilter(IFilter filter) {
		modifiedImages.clear();
		for (Mat mat : originalImages) {
			modifiedImages.add(filter.perform(mat));
		}
	}
}