package com.rootnetapp.rootnetintranet.ui.editprofile;

import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.models.responses.edituser.EditUserResponse;
import com.rootnetapp.rootnetintranet.models.responses.user.LoggedProfileResponse;
import com.rootnetapp.rootnetintranet.models.responses.user.LoggedUser;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Propietario on 15/03/2018.
 */

public class EditProfileViewModel extends ViewModel {
    private MutableLiveData<LoggedUser> mUserLiveData;
    private MutableLiveData<Boolean> mStatusLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private MutableLiveData<Boolean> mShowLoadingLiveData;
    private EditProfileRepository mRepository;
    private String mToken;
    private CompositeDisposable mDisposables;

    public EditProfileViewModel(EditProfileRepository repository) {
        mRepository = repository;
        mDisposables = new CompositeDisposable();
    }

    protected void init(String token) {
        mToken = token;

        getUser();
    }

    private void getUser() {
        mShowLoadingLiveData.setValue(true);

        Disposable disposable = mRepository
                .getLoggedProfile(mToken)
                .subscribe(this::onUserSuccess, this::onUserFailure);

        mDisposables.add(disposable);
    }

    private void onUserSuccess(LoggedProfileResponse loggedProfileResponse) {
        mShowLoadingLiveData.setValue(false);
        mUserLiveData.setValue(loggedProfileResponse.getLoggedUser());
    }

    private void onUserFailure(Throwable throwable) {
        mShowLoadingLiveData.setValue(false);
        mErrorLiveData.setValue(Utils.getOnFailureStringRes(throwable));
    }

    protected void editUser(String token, int id,
                            String fullName, String email, String phoneNumber) {
        mRepository.editUserService(token, id, fullName, email, phoneNumber).subscribe(this::onEditRemoteSuccess, this::onUserFailure);
    }

    private void onEditRemoteSuccess(EditUserResponse editUserResponse) {
        mRepository.editUserLocal(editUserResponse.getProfile()).subscribe(this::onEditLocalSuccess, this::onUserFailure);
    }

    private void onEditLocalSuccess(Boolean aBoolean) {
        mStatusLiveData.setValue(true);
    }

    @Override
    protected void onCleared() {
        mDisposables.clear();
    }

    protected LiveData<Boolean> getObservableShowLoading() {
        if (mShowLoadingLiveData == null) {
            mShowLoadingLiveData = new MutableLiveData<>();
        }
        return mShowLoadingLiveData;
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

    protected LiveData<LoggedUser> getObservableUser() {
        if (mUserLiveData == null) {
            mUserLiveData = new MutableLiveData<>();
        }
        return mUserLiveData;
    }
}
