package com.rootnetapp.rootnetintranet.ui.qrtoken;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.models.responses.login.TemporaryTokenResponse;

import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.exception.QRGenerationException;

import java.io.File;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class QRTokenViewModel extends ViewModel {

    private static final String TAG = "QRTokenViewModel";

    private QRTokenRepository mRepository;

    private MutableLiveData<Integer> mShowToastMessage;
    private MutableLiveData<Boolean> mShowLoadingLiveData;
    private MutableLiveData<File> mShowQRCodeLiveData;

    private final CompositeDisposable mDisposables = new CompositeDisposable();
    private String mToken;

    public QRTokenViewModel(QRTokenRepository repository) {
        mRepository = repository;
    }

    @Override
    protected void onCleared() {
        mDisposables.clear();
    }

    protected void initQRToken(String token) {
        mToken = token;

        requestTemporaryToken();
    }

    private void requestTemporaryToken() {
        mShowLoadingLiveData.setValue(true);
        Disposable disposable = mRepository
                .requestTemporaryToken(mToken)
                .subscribe(this::onTemporaryTokenSuccess, this::onFailure);
        mDisposables.add(disposable);
    }

    private void onTemporaryTokenSuccess(TemporaryTokenResponse response) {
        mShowLoadingLiveData.setValue(false);

        String token = response.getToken();

        if (TextUtils.isEmpty(token)) {
            mShowToastMessage.setValue(R.string.failure_connect);
        }

        try {
            File file = QRCode.from(token).withSize(300, 300).file();
            mShowQRCodeLiveData.setValue(file);
        } catch(QRGenerationException e) {
            mShowToastMessage.setValue(R.string.failure_connect);
        }
    }

    private void onFailure(Throwable throwable) {
        mShowLoadingLiveData.setValue(false);

        Log.d(TAG, "onFailure: " + throwable.getMessage());
        mShowToastMessage.setValue(Utils.getOnFailureStringRes(throwable));
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

    protected LiveData<File> getObservableShowQRCode() {
        if (mShowQRCodeLiveData == null) {
            mShowQRCodeLiveData = new MutableLiveData<>();
        }
        return mShowQRCodeLiveData;
    }
}
