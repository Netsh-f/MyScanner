package com.buaa.myscanner;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.FallbackStrategy;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.VideoCapture;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.buaa.data.MainRecyclerViewAdapter;
import com.buaa.data.TaskImage;
import com.buaa.myscanner.databinding.ActivityCameraBinding;
import com.buaa.utils.FileHelper;
import com.buaa.utils.MediaStoreHelper;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {
    private ActivityCameraBinding viewBinding;

    private ImageCapture imageCapture = null;

    private VideoCapture<Recorder> videoCapture = null;

    private ExecutorService cameraExecutor;
    private static final String[] REQUIRED_PERMISSIONS;
    private final int REQUEST_CODE_PERMISSIONS = 10;

    private final String TAG = "CameraXApp";
    private final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";

    public static final String NEW_PHOTO_PATH = "com.buaa.myscanner.CameraActivity new photo path";

    static {
        if (Build.VERSION.SDK_INT <= 28) {
            REQUIRED_PERMISSIONS = new String[]{
                    "android.permission.CAMERA",
                    "android.permission.RECORD_AUDIO",
                    "android.permission.WRITE_EXTERNAL_STORAGE"};
        } else {
            REQUIRED_PERMISSIONS = new String[]{
                    "android.permission.CAMERA",
                    "android.permission.RECORD_AUDIO"};
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
        // Set up the listeners for take photo and video capture buttons
//        viewBinding.imageCaptureButton.setOnClickListener(takePhoto);
        viewBinding.fabTakePhoto.setOnClickListener(takePhoto);
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private View.OnClickListener takePhoto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // Get a stable reference of the modifiable image capture use case
            ImageCapture imageCapture = CameraActivity.this.imageCapture;
            if (imageCapture == null)
                return;

            // Create time stamped name and MediaStore entry.
            String name = new SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                    .format(System.currentTimeMillis());
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyScanner");
            }

            // Create output options object which contains file + metadata
            ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions
                    .Builder(getContentResolver(),
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues)
                    .build();

            // Set up image capture listener, which is triggered after photo has
            // been taken
            final Application application = getApplication();
            imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(CameraActivity.this),
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                            String path = output.getSavedUri().getPath();
                            Log.d("======Photo capture succeeded: ======", path);

                            String[] projection = new String[]{MediaStore.Images.Media.DATA};
                            Cursor cursor = getContentResolver().query(
                                    output.getSavedUri(), projection, null, null, null);
                            cursor.moveToFirst();
                            int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                            String absolutePath = cursor.getString(dataIndex);
                            cursor.close();

                            MainRecyclerViewAdapter adapter = MainActivity.getAdapter();
                            adapter.addImages(new TaskImage(absolutePath));
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exc) {
                            Log.e(TAG, "Photo capture failed: ${exc.message}", exc);
                        }
                    }
            );
            finish();
        }
    };

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(
                new Runnable() {
                    @Override
                    public void run() {
                        // Used to bind the lifecycle of cameras to the lifecycle owner
                        ProcessCameraProvider cameraProvider = null;
                        try {
                            cameraProvider = cameraProviderFuture.get();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // Preview
                        Preview preview = new Preview.Builder()
                                .build();
                        preview.setSurfaceProvider(viewBinding.viewFinder.getSurfaceProvider());

                        Recorder recorder = new Recorder.Builder()
                                .setQualitySelector(QualitySelector.from(Quality.HIGHEST,
                                        FallbackStrategy.higherQualityOrLowerThan(Quality.SD)))
                                .build();

                        videoCapture = VideoCapture.withOutput(recorder);

                        imageCapture = new ImageCapture.Builder().build();

                        ImageAnalysis imageAnalyzer = new ImageAnalysis.Builder()
                                .build();

                        imageAnalyzer.setAnalyzer(cameraExecutor, luma ->
                                Log.d(TAG, "Average luminosity: $luma")
                        );


                        // Select back camera as a default
                        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                        try {
                            // Unbind use cases before rebinding
                            cameraProvider.unbindAll();

                            // Bind use cases to camera
                            cameraProvider.bindToLifecycle(
                                    CameraActivity.this, cameraSelector, preview, imageCapture, videoCapture);


                        } catch (Exception exc) {
                            Log.e(TAG, "Use case binding failed", exc);
                        }

                    }
                }
                , ContextCompat.getMainExecutor(this));
    }


    private boolean allPermissionsGranted() {
        boolean res = true;
        for (String it : REQUIRED_PERMISSIONS) {
            res = res && ContextCompat.checkSelfPermission(
                    getBaseContext(), it) == PackageManager.PERMISSION_GRANTED;
        }
        return res;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}