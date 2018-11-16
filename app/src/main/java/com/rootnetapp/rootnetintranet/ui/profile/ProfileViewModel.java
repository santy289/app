package com.rootnetapp.rootnetintranet.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;

/**
 * Created by Propietario on 15/03/2018.
 */

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<User> mUserLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private ProfileRepository profileRepository;

    public ProfileViewModel(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public void getUser(int id) {
        profileRepository.getUser(id).subscribe(this::onUserSuccess, this::onUserFailure);
    }

    private void onUserSuccess(User user) {
        mUserLiveData.setValue(user);
    }

    private void onUserFailure(Throwable throwable) {
        mErrorLiveData.setValue(R.string.failure_connect);
    }

    public LiveData<User> getObservableUser() {
        if (mUserLiveData == null) {
            mUserLiveData = new MutableLiveData<>();
        }
        return mUserLiveData;
    }

    public LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }
}
