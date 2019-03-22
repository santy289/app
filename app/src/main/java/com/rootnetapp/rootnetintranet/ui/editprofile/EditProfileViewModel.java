package com.rootnetapp.rootnetintranet.ui.editprofile;

import com.rootnetapp.rootnetintranet.R;
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
    private MutableLiveData<Integer> mToastMessageLiveData;
    private MutableLiveData<Boolean> mShowLoadingLiveData;

    private EditProfileRepository mRepository;
    private String mToken;
    private LoggedUser mLoggedUser;
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
        if (loggedProfileResponse == null) {
            mToastMessageLiveData.setValue(R.string.failure_connect);
            return;
        }

        mLoggedUser = loggedProfileResponse.getLoggedUser();

        mShowLoadingLiveData.setValue(false);
        mUserLiveData.setValue(mLoggedUser);
    }

    private void onUserFailure(Throwable throwable) {
        mShowLoadingLiveData.setValue(false);
        mToastMessageLiveData.setValue(Utils.getOnFailureStringRes(throwable));
    }

    protected void editUser(String fullName, String email, String phoneNumber) {
        if (mLoggedUser == null) {
            return;
        }

        mShowLoadingLiveData.setValue(true);

        Disposable disposable = mRepository
                .editUserService(mToken, mLoggedUser.getId(), fullName, email, phoneNumber)
                .subscribe(this::onEditRemoteSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    private void onEditRemoteSuccess(EditUserResponse editUserResponse) {
        Disposable disposable = mRepository.editUserLocal(editUserResponse.getProfile())
                .subscribe(this::onEditLocalSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    private void onEditLocalSuccess(Boolean ignored) {
        mShowLoadingLiveData.setValue(false);
        mStatusLiveData.setValue(true);
    }

    protected void changePassword(String newPassword, String repeatedPassword) {
        if (mLoggedUser == null) {
            return;
        }

        mShowLoadingLiveData.setValue(true);

        Disposable disposable = mRepository
                .changePassword(mToken, mLoggedUser.getId(), newPassword, repeatedPassword)
                .subscribe(this::onChangePasswordSuccess, this::onFailure);

        mDisposables.add(disposable);
    }

    private void onChangePasswordSuccess(EditUserResponse editUserResponse) {
        mShowLoadingLiveData.setValue(false);
        mToastMessageLiveData.setValue(R.string.password_changed_successfully);
    }

    private void onFailure(Throwable throwable) {
        mShowLoadingLiveData.setValue(false);
        mToastMessageLiveData.setValue(Utils.getOnFailureStringRes(throwable));
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

    protected LiveData<Integer> getObservableToastMessage() {
        if (mToastMessageLiveData == null) {
            mToastMessageLiveData = new MutableLiveData<>();
        }
        return mToastMessageLiveData;
    }

    protected LiveData<LoggedUser> getObservableUser() {
        if (mUserLiveData == null) {
            mUserLiveData = new MutableLiveData<>();
        }
        return mUserLiveData;
    }
}
