package com.buaa.data;

import android.app.Application;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.buaa.imagine.Imagine;
import com.buaa.imagine.filter.DocumentFilter;
import com.buaa.myscanner.MainActivity;
import com.buaa.pdfpacker.PDFPacker;
import com.buaa.utils.PDFHelper;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ImageViewModel extends AndroidViewModel {
    private List<TaskImage> imageList;
    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private static final String mPdfPath = "PDF";

    public ImageViewModel(@NonNull Application application) {
        super(application);
        imageList = getTaskImageList();
    }

    public List<TaskImage> getTaskImageList() {
        Uri tableUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        List<TaskImage> imageList = new ArrayList<>();

        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.RELATIVE_PATH};

//        String selection = MediaStore.Images.Media.DISPLAY_NAME + "= ?";//查询条件
        String selection = MediaStore.Images.Media.RELATIVE_PATH + "=?";
        String[] args = new String[]{"Pictures/MyScanner/"};//条件参数 会替换掉上面的问号 注意相对路径最后有一个'/'
        String order = MediaStore.Files.FileColumns._ID;//按id排序
        Cursor cursor = getApplication().getContentResolver().query(tableUri, projection, selection, args, order);

        if (cursor != null) {
            //获取id字段是第几列
            int idIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
            //获取data字段是第几列
            int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            int relativePathIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.RELATIVE_PATH);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idIndex);
                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                String path = cursor.getString(dataIndex);
                String relativePath = cursor.getString(relativePathIndex);

                Log.d("======path======", path);

                TaskImage taskImage = new TaskImage(imageUri, path, relativePath);
                imageList.add(taskImage);
            }
            cursor.close();
        }
        return imageList;
    }

    public void sharePDFRename(List<TaskImage> list, String pdfName) {
        new SharePDFRenameAsyncTask().execute(list, pdfName);
    }

    private static class SharePDFRenameAsyncTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... params) {
            List<TaskImage> list = (List<TaskImage>) params[0];
            String pdfName = (String) params[1];

            String pdfAbsolutePath = PDFHelper.makePdf(list, pdfName);
            PDFHelper.sharePdf(MainActivity.getContext(), pdfAbsolutePath);

            return null;
        }
    }

    public void uploadPdfToBHPan(List<TaskImage> list, String pdfName, String bhPanUrl) {
        new UploadPdfToBHPanAsyncTask().execute(list, pdfName, bhPanUrl);
    }

    private static class UploadPdfToBHPanAsyncTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... para) {
            List<TaskImage> list = (ArrayList<TaskImage>) para[0];
            String pdfName = (String) para[1];
            String bhPanUrl = (String) para[2];

            String pdfAbsolutePath = PDFHelper.makePdf(list, pdfName);

//            try {
//                BHPan.upload(bhPanUrl, pdfAbsolutePath);
//            } catch (UploadFailException e) {
//                e.printStackTrace();
//            }
            return null;
        }
    }
}
