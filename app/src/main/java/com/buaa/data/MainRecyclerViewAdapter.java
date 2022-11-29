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

import com.buaa.imagine.filter.Filter;
import com.buaa.myscanner.MainActivity;
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
            String path = current.getAbsolutePath();
//            Bitmap bitmap = BitmapFactory.decodeFile(current.getAbsolutePath());

            //取得缩略图
            BitmapFactory.Options options = new BitmapFactory.Options();//需要一个options对象来设置图像的参数。
            options.inJustDecodeBounds = true;//这个参数为true的时候标示我们在下一步获取的old_bmp并不是一个图像，返回的只是图像的宽，高之类的数据，目的是得到图像的宽高，好自定义处理。
            Bitmap old_bmp = BitmapFactory.decodeFile(path, options);//在这里我们得到图像的一些数据，path是本地图片的路径。
            options.inSampleSize = options.outWidth/1000;//计算出缩小倍率，分母是宽度设置，单位px，现在是800px，你也可以获取你的ImageView的宽度，从而计算出缩小倍率。如果options.inSampleSize =  10 的话，意思是长和宽同事缩小10倍。
            options.inJustDecodeBounds = false;//这次我们需要真正的图像，所以在之前我们改为true现在要改回来。
            options.inPreferredConfig = Bitmap.Config.RGB_565;//ALPHA_8 代表8位Alpha位图ARGB_4444 代表16位ARGB位图ARGB_8888 代表32位ARGB位图RGB_565 代表8位RGB位图，感兴趣的同学可以详细的搜一下。
            options.inDither = false;    //不进行图片抖动处理
            options.inPreferredConfig = null;  //设置让解码器以最佳方式解码

//            Bitmap bitmap = BitmapFactory.decodeFile(path, options);//得到我们想要的图片，也就是缩略过的。
            Bitmap bitmap = ImageHelper.loadBitmap(path, true, options);

            Bitmap henceBitmap = MainActivity.getImagineFilter().perform(bitmap);

            holder.imageItemView.setImageBitmap(henceBitmap);
            Log.d("======setBitMap======", path);
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
