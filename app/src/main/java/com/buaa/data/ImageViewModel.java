package com.buaa.data;

import android.app.Application;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.buaa.imagine.Imagine;
import com.buaa.imagine.filter.DocumentFilter;
import com.buaa.myscanner.MainActivity;
import com.buaa.pdfpacker.PDFPacker;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ImageViewModel extends AndroidViewModel {
    private List<TaskImage> imageList;
    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private static final String pdfPath = "PDF";

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

    public void sharePDF(List<TaskImage> list) {
        new SharePDFAsyncTask().execute(list);
    }

    private static class SharePDFAsyncTask extends AsyncTask<List<TaskImage>, Void, Void> {
        @Override
        protected Void doInBackground(List<TaskImage>... lists) {
            ArrayList<String> pathList = new ArrayList<>();

            lists[0].forEach(taskImage -> {
                pathList.add(taskImage.getAbsolutePath());
            });

            Imagine imagine = Imagine.getInstance();
            imagine.reset();
            try {
                imagine.importImagesFromFilenames(pathList);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imagine.applyFilter(new DocumentFilter());

            String filesDir = MainActivity.getContext().getExternalFilesDir(null).getAbsolutePath();
            String name = new SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                    .format(System.currentTimeMillis());
            String srcPath = Paths.get(filesDir, name).toString();
            String destPath = Paths.get(filesDir, pdfPath).toString();
            String pdfName = name;
            try {
                imagine.exportImagesToDirectory(srcPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.e("======sharePDF======", "complete export");

            try {
                PDFPacker.getInstance().packImagesToPDF(srcPath, destPath, pdfName);
            } catch (IOException e) {
                Log.d(MainActivity.myTag, "export PDF error");
                e.printStackTrace();
            }

            return null;
        }
    }
}
