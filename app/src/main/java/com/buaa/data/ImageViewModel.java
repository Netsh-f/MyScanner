package com.buaa.data;
/**
 * A class that provides asynchronous execution to execute background code.
 * @version 0.1.0
 * @author JQKonatsu
 * @since 0.1.0
 * @create 2022/12/14 14:59
 **/

import android.app.Application;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.buaa.myscanner.MainActivity;
import com.buaa.utils.PDFHelper;

import java.util.ArrayList;
import java.util.List;

public class ImageViewModel extends AndroidViewModel {
    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private static final String mPdfPath = "PDF";

    /**
     * Constructor of ImageViewModel, which can initialize TaskImageList.
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     * @date 2022/12/14 15:02
     */
    
    public ImageViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Asynchronous method for sharing PDF files.
     * @param list the images list to be the content of PDF file.
     * @param pdfName the title of PDF file.
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     * @date 2022/12/14 15:06
     */
    
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

    /**
     * Asynchronous method for upload PDF files to BHPan.
     * @param list the images list to be the content of PDF file.
     * @param pdfName the title of PDF file.
     * @param bhPanUrl the url of BHPan.
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     * @date 2022/12/14 15:07
     */

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
