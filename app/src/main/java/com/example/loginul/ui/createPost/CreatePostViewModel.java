package com.example.loginul.ui.createPost;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CreatePostViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CreatePostViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is CreatePost fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}