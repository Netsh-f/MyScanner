package com.buaa.utils;

import android.util.Log;

import com.buaa.myscanner.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHelper {
    public static String getFilesPath() {
        //getExternalFilesDir(null).getAbsolutePath()     //->Android/data/com.buaa.myscanner/files
        return null;
    }

    public static void createNewFile(String path) throws IOException {
        File file = new File(path);
        String destDirectoryPath = file.getParent();
        if (destDirectoryPath != null) {
            File destDirectory = new File(destDirectoryPath);
            if (!destDirectory.exists()) {
                destDirectory.mkdirs();
            }
        }
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    public static void createNewDir(String path) throws IOException {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static void copyFile(String srcPath, String destPath) throws IOException {
        File srcFile = new File(srcPath);
        if (!srcFile.exists()) {
            Log.e("FileHelper ERROR======", "copyFile: srcFile is not exist");
        } else if (!srcFile.isFile()) {
            Log.e("FileHelper ERROR======", "copyFile: srcFile is not a file");
        } else if (!srcFile.canRead()) {
            Log.e("FileHelper ERROR======", "copyFile: srcFile is not readable");
        } else {
            FileInputStream fileInputStream = new FileInputStream(srcFile);
            FileHelper.createNewFile(destPath);
            File dsetFile = new File(destPath);
            FileOutputStream fileOutputStream = new FileOutputStream(dsetFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fileInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }

            fileInputStream.close();
            fileOutputStream.close();
        }
    }
}
