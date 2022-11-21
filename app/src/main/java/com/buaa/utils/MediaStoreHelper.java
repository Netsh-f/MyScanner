package com.buaa.utils;

import android.app.Application;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.buaa.data.TaskImage;

import java.util.ArrayList;
import java.util.List;

public class MediaStoreHelper {
    public static Application mApplication;

    public static void setApplication(Application application) {
        mApplication = application;
    }

    public static Application getApplication() {
        return mApplication;
    }

    public static String getAbsolutePath(Uri uri) {
        String absolutePath;
        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = mApplication.getContentResolver().query(
                uri, projection, null, null, null);
        cursor.moveToFirst();
        int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        absolutePath = cursor.getString(dataIndex);
        cursor.close();
        return absolutePath;
    }
}
