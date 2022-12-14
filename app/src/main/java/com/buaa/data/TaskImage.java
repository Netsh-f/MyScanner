package com.buaa.data;

import android.net.Uri;

/**
 * A data class for storing images. The main member is the path of the image.
 *
 * @author JQKonatsu
 * @version 0.1.0
 * @since 0.1.0
 **/
public class TaskImage {
    private Uri uri;
    private String absolutePath;
    private String relativePath;

    /**
     * the getter of absolutePath.
     * 2022/12/14 15:45
     *
     * @return absolutePath
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     */

    public String getAbsolutePath() {
        return absolutePath;
    }

    /**
     * the constructor with parameters.
     * 2022/12/14 15:44
     *
     * @param uri uri
     * @param absolutePath absolutePath
     * @param relativePath relativePath
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     */

    public TaskImage(Uri uri, String absolutePath, String relativePath) {
        this.uri = uri;
        this.absolutePath = absolutePath;
        this.relativePath = relativePath;
    }

    /**
     * The constructor of TaskImage.
     * 2022/12/14 15:43
     *
     * @param absolutePath absolutePath
     * @author JQKonatsu
     * @version 0.1.0
     * @since 0.1.0
     */

    public TaskImage(String absolutePath) {
        this.absolutePath = absolutePath;
    }
}
