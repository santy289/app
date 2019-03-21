package com.rootnetapp.rootnetintranet.ui.profile;

import com.rootnetapp.rootnetintranet.commons.Utils;
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

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<LoggedUser> mUserLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private MutableLiveData<Boolean> mShowLoadingLiveData;
    private ProfileRepository mRepository;
    private CompositeDisposable mDisposables;
    private String mToken;

    public ProfileViewModel(ProfileRepository mRepository) {
        this.mRepository = mRepository;
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

    @Override
    protected void onCleared() {
        mDisposables.clear();
    }

    protected LiveData<LoggedUser> getObservableUser() {
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

    protected LiveData<Boolean> getObservableShowLoading() {
        if (mShowLoadingLiveData == null) {
            mShowLoadingLiveData = new MutableLiveData<>();
        }
        return mShowLoadingLiveData;
    }
}
