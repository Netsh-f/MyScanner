package com.buaa.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buaa.myscanner.R;

import java.util.List;

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ImageViewHolder> {
    private List<TaskImage> imageList;

    public MainRecyclerViewAdapter(List<TaskImage> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recyclerview_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Log.d("======setBitMap======", "onBindViewHolder");
        if (imageList != null) {
            TaskImage current = imageList.get(position);
            Bitmap bitmap = BitmapFactory.decodeFile(current.getAbsolutePath());
            holder.imageItemView.setImageBitmap(bitmap);
            Log.d("======setBitMap======", current.getAbsolutePath());
        }
    }

    @Override
    public int getItemCount() {
        return imageList == null ? 0 : imageList.size();
    }

    public void setImages(List<TaskImage> images) {
        imageList = images;
        notifyDataSetChanged();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageItemView;

        private ImageViewHolder(View itemView) {
            super(itemView);
            imageItemView = itemView.findViewById(R.id.imageView_for_recyclerView);
        }
    }
}
