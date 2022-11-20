package com.buaa.myscanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.buaa.data.ImageViewModel;
import com.buaa.data.MainRecyclerViewAdapter;
import com.buaa.data.TaskImage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton fabStartCamera;
    private ImageViewModel mImageViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabStartCamera = findViewById(R.id.fab_startCamera);
        fabStartCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(new MainRecyclerViewAdapter(getTaskImageList()));


//        mImageViewModel = new ViewModelProvider(this).get(ImageViewModel.class);

    }

    private List<TaskImage> getTaskImageList() {
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

                Log.d("======path======", path);

                TaskImage taskImage = new TaskImage(imageUri, path, relativePath);
                imageList.add(taskImage);
            }
            cursor.close();
        }
        return imageList;
    }
}