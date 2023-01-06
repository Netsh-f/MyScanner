package com.buaa.myscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.buaa.data.ImageViewModel;
import com.buaa.data.MainRecyclerViewAdapter;
import com.buaa.data.TaskImage;
import com.buaa.imagine.filter.DocumentFilter;
import com.buaa.imagine.filter.Filter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity is the main interface of the program,
 * which includes the function of previewing pictures,
 * as well as the buttons to start the camera and share.
 *
 * @author JQKonatsu
 * @version 0.1.0
 * @since 0.1.0
 **/
public class MainActivity extends AppCompatActivity {
    private FloatingActionButton fabStartCamera;
    private MainRecyclerViewAdapter recyclerViewAdapter;
    private Filter imagineFilter;
    private ImageViewModel imageViewModel;
    private static Context context;
    public static final int START_CAMERA_REQUEST_CODE = 1;

    public static String myTag = "myTag";

    private static MainRecyclerViewAdapter globalRecyclerViewAdapter;
    private static Filter globalImagineFilter;

    /**
     * The onCreate method in MainActivity.
     *
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadOpenCv();
        context = getApplicationContext();

        imageViewModel = new ViewModelProvider(this).get(ImageViewModel.class);
        imagineFilter = new DocumentFilter();
        MainActivity.setFilter(imagineFilter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.my_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.share_btn_in_toolbar:
                        if (recyclerViewAdapter.getImageList().size() == 0) {
                            Toast.makeText(MainActivity.this, "至少要有一张图片", Toast.LENGTH_SHORT).show();
                        } else {
                            sharePdfInputDialog();
                        }
                        break;
                    case R.id.settings_btn_in_toolbar:
                        if (recyclerViewAdapter.getImageList().size() == 0) {
                            Toast.makeText(MainActivity.this, "至少要有一张图片", Toast.LENGTH_SHORT).show();
                        } else {
                            uploadPdfToBHPan();
                        }
                        break;
                    case R.id.help_btn_in_toolbar:
                        Toast.makeText(MainActivity.this,
                                "右滑删除图片\n点击分享按钮即可生成PDF\n",
                                Toast.LENGTH_LONG).show();
                        break;
                    case R.id.about_btn_in_toolbar:
                        showInfo();
                        break;
                }
                return false;
            }
        });

        fabStartCamera = findViewById(R.id.fab_startCamera);
        fabStartCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivityForResult(intent, START_CAMERA_REQUEST_CODE);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerViewAdapter = new MainRecyclerViewAdapter(new ArrayList<TaskImage>());
        recyclerView.setAdapter(recyclerViewAdapter);
        MainActivity.setAdapter(recyclerViewAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder,
                                 int direction) {
                int position = viewHolder.getAdapterPosition();
                TaskImage image = recyclerViewAdapter.getTaskImageAtPosition(position);
//                Toast.makeText(MainActivity.this, "delete image: " + image.getAbsolutePath(), Toast.LENGTH_LONG).show();
                recyclerViewAdapter.deleteImage(position);
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    /**
     * Set global adapter of recyclerView for use by other classes.
     *
     * @param adapter is an instance created in onCreate.
     * @author JQKonatsu
     * @version 0.1.0
     * @create 2022/11/09 14:09
     * @since 0.1.0
     */
    private static void setAdapter(MainRecyclerViewAdapter adapter) {
        globalRecyclerViewAdapter = adapter;
    }

    /**
     * The getter of global adapter for use by other classes
     *
     * @return globalRecyclerViewAdapter
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     */
    public static MainRecyclerViewAdapter getAdapter() {
        return globalRecyclerViewAdapter;
    }

    /**
     * The setter of globalImagineFilter for use by other classes.
     *
     * @param filter is an DocumentFilter() instance created in onCreate
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     */
    public static void setFilter(Filter filter) {
        globalImagineFilter = filter;
    }

    /**
     * The getter of globalImagineFilter for use by other classes.
     *
     * @return globalImagineFilter
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     */

    public static Filter getImagineFilter() {
        return globalImagineFilter;
    }

    /**
     * A deprecated method.
     * The function of this method is to read the local picture list at startup,
     * and it is used in sustainable applications.
     * It is not needed in the current version.
     *
     * @return The imageList of images in "Pictures/MyScanner/"
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     */

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
        Cursor cursor = getContentResolver().query(tableUri, projection, selection, args, order);

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

                TaskImage taskImage = new TaskImage(imageUri, path, relativePath);
                imageList.add(taskImage);
            }
            cursor.close();
        }
        return imageList;
    }

    /**
     * The method to load openCv dependency.
     *
     * @author JQKonatsu
     * @version 0.1.0
     * @create 2022/11/09 14:24
     * @since 0.1.0
     */

    private void loadOpenCv() {
        boolean success = OpenCVLoader.initDebug();   //对OpenCV库进行初始化加载，bool返回值可以判断是否加载成功。
//        if (success) {
//            Toast.makeText(this.getApplicationContext(), "OpenCV库加载成功", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this.getApplicationContext(), "OpenCV库加载失败", Toast.LENGTH_SHORT).show();
//        }
    }

    /**
     * The getter of context in MainActivity.
     *
     * @return context
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     */

    public static Context getContext() {
        return context;
    }

    /**
     * The method to be executed when clicking the share button will pop up a dialog box
     * to let the user name the generated PDF file.
     *
     * @author JQKonatsu
     * @version 0.1.0
     * @date 2022/11/09 14:26
     * @since 0.1.0
     */

    private void sharePdfInputDialog() {
        EditText editText = new EditText(MainActivity.this);
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(MainActivity.this);
        inputDialog.setTitle("PDF命名为").setView(editText);
        inputDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                imageViewModel.sharePDFRename(recyclerViewAdapter.getImageList(), editText.getText().toString());
            }
        }).show();
    }

    /**
     * Method not completed yet.
     * Code executed when clicking the button of uploading to the cloud disk of Beijing Airlines.
     *
     * @author JQKonatsu
     * @version 0.1.0
     * @date 2022/11/09 14:27
     * @since 0.1.0
     */

    private void uploadPdfToBHPan() {

        EditText editTextPdfName = new EditText(MainActivity.this);
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(MainActivity.this);
        inputDialog.setTitle("PDF命名为").setView(editTextPdfName);

        inputDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pdfName = editTextPdfName.getText().toString();

                EditText editTextBHPan = new EditText(MainActivity.this);
                AlertDialog.Builder inputDialogBHPan = new AlertDialog.Builder(MainActivity.this);
                inputDialogBHPan.setTitle("北航云盘链接为").setView(editTextBHPan);

                inputDialogBHPan.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imageViewModel.uploadPdfToBHPan(recyclerViewAdapter.getImageList(),
                                pdfName, editTextBHPan.getText().toString());
                    }
                }).show();
            }
        }).show();
    }

    /**
     *
     * 2023/1/5 17:07
     *
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     */
    private void showInfo() {
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setIcon(R.drawable.atri)
                .setTitle("MyScanner")
                .setMessage("version 1.0.1-normal\n\nStaff\n    JQKonatsu\n    Turmoil\n    Kangyx\n    ThunderUp")
                .show();
    }
}