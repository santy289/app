package com.rootnetapp.rootnetintranet.ui.editprofile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.responses.edituser.EditUserResponse;

/**
 * Created by Propietario on 15/03/2018.
 */

public class EditProfileViewModel extends ViewModel {
    private MutableLiveData<Boolean> mUserLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private EditProfileRepository editProfileRepository;

    public EditProfileViewModel(EditProfileRepository editProfileRepository) {
        this.editProfileRepository = editProfileRepository;
    }

    protected void editUser(String fullName, String email, String phoneNumber) {
        editProfileRepository.editUserService(fullName, email, phoneNumber).subscribe(this::onEditRemoteSuccess, this::onUserFailure);
    }

    private void onEditRemoteSuccess(EditUserResponse editUserResponse) {
        editProfileRepository.editUserLocal(editUserResponse.getProfile()).subscribe(this::onEditLocalSuccess, this::onUserFailure);
    }

    private void onEditLocalSuccess(Boolean aBoolean) {
        mUserLiveData.setValue(true);
    }

    private void onUserFailure(Throwable throwable) {
        mErrorLiveData.setValue(R.string.failure_connect);
    }

    protected LiveData<Boolean> getObservableStatus() {
        if (mUserLiveData == null) {
            mUserLiveData = new MutableLiveData<>();
        }
        return mUserLiveData;
    }

    protected LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }
}
