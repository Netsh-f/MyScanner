package com.buaa.data;
/**
 * A data class for storing images. The main member is the path of the image.
 * @version 0.1.0
 * @author JQKonatsu
 * @since 0.1.0
 * @create 2022/12/14 15:19
 **/

import android.net.Uri;

public class TaskImage {
    private Uri uri;
    private String absolutePath;
    private String relativePath;

    public String getAbsolutePath() {
        return absolutePath;
    }

    public TaskImage(Uri uri, String absolutePath, String relativePath) {
        this.uri = uri;
        this.absolutePath = absolutePath;
        this.relativePath = relativePath;
    }

    public TaskImage(String absolutePath) {
        this.absolutePath = absolutePath;
    }
}
