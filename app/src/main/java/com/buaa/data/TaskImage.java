package com.buaa.data;

import android.net.Uri;

public class TaskImage {
    private Uri uri;
    private String absolutePath;
    private String relativePath;

    public String getAbsolutePath() {
        return absolutePath;
    }

    public Uri getUri() {
        return uri;
    }

    public TaskImage() {
    }

    public TaskImage(Uri uri, String absolutePath, String relativePath) {
        this.uri = uri;
        this.absolutePath = absolutePath;
        this.relativePath = relativePath;
    }
}
