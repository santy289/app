package com.rootnetapp.rootnetintranet.ui.editprofile;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.models.responses.edituser.EditUserResponse;

/**
 * Created by Propietario on 15/03/2018.
 */

public class EditProfileViewModel extends ViewModel {
    private MutableLiveData<User> mUserLiveData;
    private MutableLiveData<Boolean> mStatusLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private EditProfileRepository editProfileRepository;

    public EditProfileViewModel(EditProfileRepository editProfileRepository) {
        this.editProfileRepository = editProfileRepository;
    }

    protected void getUser(int id) {
        editProfileRepository.getUser(id).subscribe(this::onGetUserSuccess, this::onUserFailure);
    }

    private void onGetUserSuccess(User user) {
        mUserLiveData.setValue(user);
    }

    protected void editUser(String token, int id,
                            String fullName, String email, String phoneNumber) {
        editProfileRepository.editUserService(token, id, fullName, email, phoneNumber).subscribe(this::onEditRemoteSuccess, this::onUserFailure);
    }

    private void onEditRemoteSuccess(EditUserResponse editUserResponse) {
        editProfileRepository.editUserLocal(editUserResponse.getProfile()).subscribe(this::onEditLocalSuccess, this::onUserFailure);
    }

    private void onEditLocalSuccess(Boolean aBoolean) {
        mStatusLiveData.setValue(true);
    }

    private void onUserFailure(Throwable throwable) {
        mErrorLiveData.setValue(R.string.failure_connect);
    }

    protected LiveData<Boolean> getObservableStatus() {
        if (mStatusLiveData == null) {
            mStatusLiveData = new MutableLiveData<>();
        }
        return mStatusLiveData;
    }

    protected LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }

    public LiveData<User> getObservableUser() {
        if (mUserLiveData == null) {
            mUserLiveData = new MutableLiveData<>();
        }
        return mUserLiveData;
    }
}
