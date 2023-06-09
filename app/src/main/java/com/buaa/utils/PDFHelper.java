package com.buaa.utils;
/**
 * Tool class containing operations related to PDF files.
 * @version 0.1.0
 * @author JQKonatsu
 * @since 0.1.0
 * @create 2022/12/14 14:50
 **/

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.buaa.data.TaskImage;
import com.buaa.imagine.Imagine;
import com.buaa.imagine.filter.DocumentFilter;
import com.buaa.myscanner.MainActivity;
import com.buaa.pdfpacker.PDFPacker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PDFHelper {
    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private static final String mPdfPath = "PDF";

    /**
     * Method of generating PDF file
     * @param list is a List for TaskImage which is the content of PDF, pdfName is the title of PDF file.
     * @param pdfName the title of PDF.
     * @return The absolute path of PDF file.
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     */
    
    public static String makePdf(List<TaskImage> list, String pdfName) {
//        ArrayList<String> pathList = new ArrayList<>();
        ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();

        list.forEach(taskImage -> {
//            pathList.add(taskImage.getAbsolutePath());

            String path = taskImage.getAbsolutePath();
            BitmapFactory.Options options = ImageHelper.getThumbnailOption(path, 1500);
            Bitmap bitmap = ImageHelper.loadBitmap(path, true, options);
            bitmapArrayList.add(bitmap);
        });

        Imagine imagine = Imagine.getInstance();
        imagine.clear();
//        try {
//            imagine.importImagesFromFilenames(pathList);
            imagine.importImages(bitmapArrayList);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        imagine.applyFilter(new DocumentFilter());

        String filesDir = MainActivity.getContext().getExternalFilesDir(null).getAbsolutePath();
        String name = new SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis());
        String srcPath = Paths.get(filesDir, "images", name).toString();
        String destPath = Paths.get(filesDir, mPdfPath).toString();

        try {
            imagine.exportImagesToDirectory(srcPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //下面是生成pdf
        if (pdfName == "") {//如果输入为空，默认PDF名称为创建时间
            pdfName = name;
        }

        try {
            PDFPacker.getInstance().packImagesToPDF(srcPath, destPath, pdfName);
        } catch (IOException e) {
            Log.d(MainActivity.myTag, "export PDF error");
            e.printStackTrace();
        }

        return Paths.get(destPath, pdfName + ".pdf").toString();
    }

    /**
     * Method for sharing PDf file.
     * @param context context
     * @param pdfFilePath is the absolute path of PDf file.
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     */
    
    @SuppressLint("QueryPermissionsNeeded")
    public static void sharePdf(Context context, String pdfFilePath) {
        File shareFile = new File(pdfFilePath);
        if (!shareFile.exists()) return;
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", shareFile);
            intent.putExtra(Intent.EXTRA_STREAM, contentUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(shareFile));
        }
        intent.setType("application/pdf");
        Intent chooser = Intent.createChooser(intent, "分享到应用");
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(chooser);
        }
    }
}
