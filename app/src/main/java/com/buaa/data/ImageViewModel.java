package com.buaa.data;

import android.app.Application;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.buaa.bhpan.BHPan;
import com.buaa.bhpan.exception.UploadFailException;
import com.buaa.myscanner.MainActivity;
import com.buaa.utils.PDFHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that provides asynchronous execution to execute background code.
 *
 * @author JQKonatsu
 * @version 0.1.0
 * @since 0.1.0
 **/
public class ImageViewModel extends AndroidViewModel {
    private static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private static final String mPdfPath = "PDF";
    public static boolean uploadFlag = false;
    public static String uploadFailMessage;

    /**
     * Constructor of ImageViewModel, which can initialize TaskImageList.
     *
     * @param application application
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     */

    public ImageViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Asynchronous method for sharing PDF files.
     *
     * @param list    the images list to be the content of PDF file.
     * @param pdfName the title of PDF file.
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
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
     *
     * @param list     the images list to be the content of PDF file.
     * @param pdfName  the title of PDF file.
     * @param bhPanUrl the url of BHPan.
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
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

            try {
                ImageViewModel.uploadFlag = false;
                BHPan.upload(bhPanUrl, pdfAbsolutePath);
                ImageViewModel.uploadFlag = true;
            } catch (UploadFailException e) {
                e.printStackTrace();
                ImageViewModel.uploadFailMessage = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            if (ImageViewModel.uploadFlag) {
                Toast.makeText(MainActivity.getContext(), "上传成功", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.getContext(), "上传失败:" + ImageViewModel.uploadFailMessage, Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(unused);
        }
    }
}
