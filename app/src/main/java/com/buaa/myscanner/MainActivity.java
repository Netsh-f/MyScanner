package com.buaa.myscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.buaa.data.ImageViewModel;
import com.buaa.data.MainRecyclerViewAdapter;
import com.buaa.data.TaskImage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton fabStartCamera;
    private ImageViewModel imageViewModel;
    private MainRecyclerViewAdapter recyclerViewAdapter;
    public static final int START_CAMERA_REQUEST_CODE = 1;

    private static MainRecyclerViewAdapter globalRecyclerViewAdapter;

    private static void setAdapter(MainRecyclerViewAdapter adapter) {
        globalRecyclerViewAdapter = adapter;
    }

    public static MainRecyclerViewAdapter getAdapter() {
        return globalRecyclerViewAdapter;
    }


    public ImageViewModel getImageViewModel() {
        return imageViewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
//        recyclerViewAdapter = new MainRecyclerViewAdapter(getTaskImageList());
        recyclerViewAdapter = new MainRecyclerViewAdapter(new ArrayList<TaskImage>());
        recyclerView.setAdapter(recyclerViewAdapter);
        MainActivity.setAdapter(recyclerViewAdapter);

        imageViewModel = new ViewModelProvider(this).get(ImageViewModel.class);

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
                Toast.makeText(MainActivity.this, "delete image: "+image.getAbsolutePath(), Toast.LENGTH_LONG).show();
                recyclerViewAdapter.deleteImage(position);
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);

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

                Log.d("======imageUri======", imageUri.getPath());
                Log.d("======path======", path);
                Log.d("======relativePath======", relativePath);

                TaskImage taskImage = new TaskImage(imageUri, path, relativePath);
                imageList.add(taskImage);
            }
            cursor.close();
        }
        return imageList;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == START_CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Log.d("======onActivityResult======", "addImages");
            TaskImage taskImage = new TaskImage(data.getStringExtra(CameraActivity.NEW_PHOTO_PATH));
            recyclerViewAdapter.addImages(taskImage);
        }
    }
}