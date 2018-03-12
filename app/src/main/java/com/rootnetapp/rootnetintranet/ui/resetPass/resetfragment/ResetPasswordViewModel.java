package com.rootnetapp.rootnetintranet.ui.resetPass.resetfragment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.responses.resetPass.ResetPasswordResponse;

/**
 * Created by Propietario on 12/03/2018.
 */

public class ResetPasswordViewModel extends ViewModel {

    private MutableLiveData<ResetPasswordResponse> mResetLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private ResetPasswordRepository resetPasswordRepository;

    public ResetPasswordViewModel(ResetPasswordRepository resetPasswordRepository) {
        this.resetPasswordRepository = resetPasswordRepository;
    }

    protected void resetPassword() {
        resetPasswordRepository.resetPassword().subscribe(this::onResetSuccess, this::onResetFailure);
    }

    private void onResetSuccess(ResetPasswordResponse resetPasswordResponse) {
        mResetLiveData.setValue(resetPasswordResponse);
    }

    private void onResetFailure(Throwable throwable) {
        mErrorLiveData.setValue(R.string.failure_connect);
    }

    protected LiveData<ResetPasswordResponse> getObservableLogin() {
        if (mResetLiveData == null) {
            mResetLiveData = new MutableLiveData<>();
        }
        return mResetLiveData;
    }

    protected LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }

}
