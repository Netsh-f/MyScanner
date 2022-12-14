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
     * Get an option as a parameter in the method of loading Bitmap thumbnails.
     * 2022/12/14 16:47
     * 
     * @param path the path of image
     * @param index This is the width of the picture. It is appropriate to set it to 1000
     * @return A BitmapFactory.Options with thumbnails parameters
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     */
    
    public static BitmapFactory.Options getThumbnailOption(String path, int index){
        //取得缩略图
        BitmapFactory.Options options = new BitmapFactory.Options();//需要一个options对象来设置图像的参数。
        options.inJustDecodeBounds = true;//这个参数为true的时候标示我们在下一步获取的old_bmp并不是一个图像，返回的只是图像的宽，高之类的数据，目的是得到图像的宽高，好自定义处理。
        Bitmap old_bmp = BitmapFactory.decodeFile(path, options);//在这里我们得到图像的一些数据，path是本地图片的路径。
        options.inSampleSize = options.outWidth / index;//计算出缩小倍率，分母是宽度设置，单位px，现在是800px，你也可以获取你的ImageView的宽度，从而计算出缩小倍率。如果options.inSampleSize =  10 的话，意思是长和宽同事缩小10倍。
        options.inJustDecodeBounds = false;//这次我们需要真正的图像，所以在之前我们改为true现在要改回来。
        options.inPreferredConfig = Bitmap.Config.RGB_565;//ALPHA_8 代表8位Alpha位图ARGB_4444 代表16位ARGB位图ARGB_8888 代表32位ARGB位图RGB_565 代表8位RGB位图，感兴趣的同学可以详细的搜一下。
        options.inDither = false;    //不进行图片抖动处理
        options.inPreferredConfig = null;  //设置让解码器以最佳方式解码
        return options;
    }
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
