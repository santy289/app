package com.rootnetapp.rootnetintranet.ui.qrtoken;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.commons.Utils;

import io.reactivex.disposables.CompositeDisposable;

public class QRTokenViewModel extends ViewModel {

    private static final String TAG = "QRTokenViewModel";

    private MutableLiveData<Integer> mShowToastMessage;
    private MutableLiveData<Boolean> mShowLoadingLiveData;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    public QRTokenViewModel() {
    }

    @Override
    protected void onCleared() {
        mDisposables.clear();
    }
    protected LiveData<Integer> getObservableShowToastMessage() {
        if (mShowToastMessage == null) {
            mShowToastMessage = new MutableLiveData<>();
        }
        return mShowToastMessage;
    }

    protected LiveData<Boolean> getObservableShowLoading() {
        if (mShowLoadingLiveData == null) {
            mShowLoadingLiveData = new MutableLiveData<>();
        }
        return mShowLoadingLiveData;
    }
}
