package com.buaa.utils;
/**
 * Tool class containing operations related to loading image files.
 *
 * @version 0.1.0
 * @author JQKonatsu
 * @create 2022/12/14 14:50
 * @since 0.1.0
 **/


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;

public class ImageHelper {
    /**
     * Load pictures through the given path.
     * @param imgpath is the absolute path of image.
     * @return The Bitmap of given image.
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     */

    public static Bitmap loadBitmap(String imgpath) {
        return BitmapFactory.decodeFile(imgpath);
    }

    /**
     * Another method to load pictures through the given path, which can get thumbnails by loading options.
     * @param imgpath is the absolute path of image.
     * @param opts is a BitmapFactory.Options containing the parameters of thumbnails.
     * @return The Bitmap of given image.
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     */
    public static Bitmap loadBitmap(String imgpath, BitmapFactory.Options opts) {
        return BitmapFactory.decodeFile(imgpath, opts);
    }

    /**
     * Load pictures from the given path and specify whether to automatically rotate the direction.
     * @param imgpath the path of image.
     * @param adjustOritation whether to automatically rotate the direction
     * @return the bitMap of given image.
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     */

    public static Bitmap loadBitmap(String imgpath, boolean adjustOritation) {
        if (!adjustOritation) {
            return loadBitmap(imgpath);
        } else {
            Bitmap bm = loadBitmap(imgpath);
            int digree = 0;
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(imgpath);
            } catch (IOException e) {
                e.printStackTrace();
                exif = null;
            }
            if (exif != null) {
                // 读取图片中相机方向信息
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                // 计算旋转角度
                switch (ori) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        digree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        digree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        digree = 270;
                        break;
                    default:
                        digree = 0;
                        break;
                }
            }
            if (digree != 0) {
                // 旋转图片
                Matrix m = new Matrix();
                m.postRotate(digree);
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
            }
            return bm;
        }
    }

    /**
     * Another method to load pictures from the given path and specify whether to automatically rotate the direction.
     * @param imgpath the path of image.
     * @param adjustOritation whether to automatically rotate the direction
     * @param opts is a BitmapFactory.Options containing the parameters of thumbnails.
     * @return the bitMap of given image.
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     */
    public static Bitmap loadBitmap(String imgpath, boolean adjustOritation, BitmapFactory.Options opts) {
        if (!adjustOritation) {
            return loadBitmap(imgpath, opts);
        } else {
            Bitmap bm = loadBitmap(imgpath, opts);
            int digree = 0;
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(imgpath);
            } catch (IOException e) {
                e.printStackTrace();
                exif = null;
            }
            if (exif != null) {
                // 读取图片中相机方向信息
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                // 计算旋转角度
                switch (ori) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        digree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        digree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        digree = 270;
                        break;
                    default:
                        digree = 0;
                        break;
                }
            }
            if (digree != 0) {
                // 旋转图片
                Matrix m = new Matrix();
                m.postRotate(digree);
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
            }
            return bm;
        }
    }
}
