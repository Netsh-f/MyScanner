package com.buaa.data;

import android.app.Application;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ImageViewModel extends AndroidViewModel {
    private LiveData<List<TaskImage>> mAllImage;

    public LiveData<List<TaskImage>> getAllImage() {
        return mAllImage;
    }

    public ImageViewModel(@NonNull Application application) {
        super(application);

    }
}
