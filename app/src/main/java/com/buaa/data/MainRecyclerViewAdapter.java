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
import com.buaa.utils.ImageHelper;

import java.util.List;

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ImageViewHolder> {
    private List<TaskImage> imageList;

    public MainRecyclerViewAdapter(List<TaskImage> imageList) {
        this.imageList = imageList;
    }

    public List<TaskImage> getImageList() {
        return imageList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recyclerview_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        if (imageList != null) {
            TaskImage current = imageList.get(position);
//            Bitmap bitmap = BitmapFactory.decodeFile(current.getAbsolutePath());

            Bitmap bitmap = ImageHelper.loadBitmap(current.getAbsolutePath(), true);

            holder.imageItemView.setImageBitmap(bitmap);
            Log.d("======setBitMap======", current.getAbsolutePath());
        }
    }

    @Override
    public int getItemCount() {
        return imageList == null ? 0 : imageList.size();
    }

    public void addImages(TaskImage image) {
        imageList.add(image);
        notifyDataSetChanged();
    }

    public void deleteImage(int position) {
        imageList.remove(position);
        notifyDataSetChanged();
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

    public TaskImage getTaskImageAtPosition(int position) {
        return imageList.get(position);
    }
}
