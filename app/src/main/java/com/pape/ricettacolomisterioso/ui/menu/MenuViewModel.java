package com.pape.ricettacolomisterioso.ui.menu;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MenuViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public MenuViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragmentooooooollololo");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
