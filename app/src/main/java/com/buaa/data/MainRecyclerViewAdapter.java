package com.buaa.data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buaa.myscanner.R;

import java.util.List;

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ImageViewHolder> {
    private List<TaskImage> mImageList;

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
        if (mImageList != null) {
            TaskImage current = mImageList.get(position);
            holder.imageItemView.setImageURI(current.getUri());
        }
    }

    @Override
    public int getItemCount() {
        return imageList == null ? 0 : imageList.size();
    }

    public void setImages(List<TaskImage> images) {
        mImageList = images;
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
