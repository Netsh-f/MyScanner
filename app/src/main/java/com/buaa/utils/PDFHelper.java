package com.buaa.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

    public static String makePdf(List<TaskImage> list, String pdfName) {
        ArrayList<String> pathList = new ArrayList<>();

        Log.d(MainActivity.myTag, "listSize = " + list.size());

        list.forEach(taskImage -> {
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

        Log.d(MainActivity.myTag, "after imagine reset and import, imagine.size = " + imagine.getSize());

        String filesDir = MainActivity.getContext().getExternalFilesDir(null).getAbsolutePath();
        String name = new SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis());
        String srcPath = Paths.get(filesDir, name).toString();
        String destPath = Paths.get(filesDir, mPdfPath).toString();

        if (pdfName == "") {//如果输入为空，默认PDF名称为创建时间
            pdfName = name;
        }

        try {
            imagine.exportImagesToDirectory(srcPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            PDFPacker.getInstance().packImagesToPDF(srcPath, destPath, pdfName);
        } catch (IOException e) {
            Log.d(MainActivity.myTag, "export PDF error");
            e.printStackTrace();
        }

        return Paths.get(destPath, pdfName + ".pdf").toString();
    }

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
