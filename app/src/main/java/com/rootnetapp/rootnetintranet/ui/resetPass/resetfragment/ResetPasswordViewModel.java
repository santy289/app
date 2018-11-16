package com.rootnetapp.rootnetintranet.ui.resetPass.resetfragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.responses.resetPass.ResetPasswordResponse;

/**
 * Created by Propietario on 12/03/2018.
 */

public class ResetPasswordViewModel extends ViewModel {

    private MutableLiveData<ResetPasswordResponse> mResetLiveData;
    private MutableLiveData<ResetPasswordResponse> mTokenLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private ResetPasswordRepository resetPasswordRepository;

    public ResetPasswordViewModel(ResetPasswordRepository resetPasswordRepository) {
        this.resetPasswordRepository = resetPasswordRepository;
    }

    protected void validateToken(String token) {
        resetPasswordRepository.validateToken(token).subscribe(this::onValidateSuccess, this::onResetFailure);
    }

    protected void resetPassword(String token, String username, String password, String repeatNewPassword) {
        resetPasswordRepository.resetPassword(token, username, password, repeatNewPassword).subscribe(this::onResetSuccess, this::onResetFailure);
    }

    private void onValidateSuccess(ResetPasswordResponse resetPasswordResponse) {
        mTokenLiveData.setValue(resetPasswordResponse);
    }

    private void onResetSuccess(ResetPasswordResponse resetPasswordResponse) {
        mResetLiveData.setValue(resetPasswordResponse);
    }

    private void onResetFailure(Throwable throwable) {
        mErrorLiveData.setValue(R.string.failure_connect);
    }

    protected LiveData<ResetPasswordResponse> getObservableValidate() {
        if (mTokenLiveData == null) {
            mTokenLiveData = new MutableLiveData<>();
        }
        return mTokenLiveData;
    }

    protected LiveData<ResetPasswordResponse> getObservableReset() {
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
