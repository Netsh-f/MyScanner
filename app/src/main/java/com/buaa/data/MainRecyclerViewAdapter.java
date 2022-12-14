package com.buaa.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buaa.myscanner.MainActivity;
import com.buaa.myscanner.R;
import com.buaa.utils.ImageHelper;

import java.util.List;

/**
 * Customize the subclass of the RecyclerView.Adapter class,
 * used to store preview effects and update views.
 *
 * @author JQKonatsu
 * @version 0.1.0
 * @since 0.1.0
 **/
public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ImageViewHolder> {
    private List<TaskImage> imageList;

    public MainRecyclerViewAdapter(List<TaskImage> imageList) {
        this.imageList = imageList;
    }

    /**
     * the getter of imageList for use of other classes.
     * 2022/12/14 15:47
     *
     * @return imageList
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     */

    public List<TaskImage> getImageList() {
        return imageList;
    }

    /**
     * onCreateViewHolder in RecyclerView.Adapter.
     * 2022/12/14 15:49
     *
     * @param parent   ViewGroup parent
     * @param viewType int viewType
     * @return ImageViewHolder
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     */

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recyclerview_item, parent, false);
        return new ImageViewHolder(view);
    }

    /**
     * onBindViewHolder in RecyclerView.Adapter.
     * 2022/12/14 15:48
     *
     * @param holder   ImageViewHolder holder
     * @param position int position
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     */

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        if (imageList != null) {
            TaskImage current = imageList.get(position);
            String path = current.getAbsolutePath();
//            Bitmap bitmap = BitmapFactory.decodeFile(current.getAbsolutePath());

            //取得缩略图设置
            BitmapFactory.Options options = ImageHelper.getThumbnailOption(path, 1000);
            Bitmap bitmap = ImageHelper.loadBitmap(path, true, options);

            Bitmap henceBitmap = MainActivity.getImagineFilter().perform(bitmap);

            holder.imageItemView.setImageBitmap(henceBitmap);
        }
    }

    /**
     * get the size of imageList.
     * 2022/12/14 15:46
     *
     * @return the size
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     */

    @Override
    public int getItemCount() {
        return imageList == null ? 0 : imageList.size();
    }

    /**
     * The method to add one image to the list, and update the preview.
     *
     * @param image is the image to be added.
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     */

    public void addImages(TaskImage image) {
        imageList.add(image);
        notifyDataSetChanged();
    }

    /**
     * The method to delete a image in the list, and update the preview.
     *
     * @param position is the serial number in the list.
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     */

    public void deleteImage(int position) {
        imageList.remove(position);
        notifyDataSetChanged();
    }

    /**
     * Set the content of each small element in the recyclerView,
     * and set Bitmap for each imageView.
     *
     * @author JQKonatsu
     * @version 0.1.0
     * @date 2022/12/14 15:16
     * @since 0.1.0
     */

    class ImageViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageItemView;

        private ImageViewHolder(View itemView) {
            super(itemView);
            imageItemView = itemView.findViewById(R.id.imageView_for_recyclerView);
        }
    }

    /**
     * get an image by the serial number in the list.
     *
     * @param position is the serial number.
     * @return A TaskImage
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     */

    public TaskImage getTaskImageAtPosition(int position) {
        return imageList.get(position);
    }
}
