package com.rootnetapp.rootnetintranet.ui.login;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.auth0.android.jwt.JWT;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.models.responses.domain.ClientResponse;
import com.rootnetapp.rootnetintranet.models.responses.login.JWToken;
import com.rootnetapp.rootnetintranet.models.responses.login.LoginResponse;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import retrofit2.HttpException;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginResponse> mLoginLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private MutableLiveData<String> showLogo;
    private MutableLiveData<String[]> saveToPreference;
    private MutableLiveData<Boolean> goToSyncActivity;
    private LoginRepository loginRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private String userName = "";
    private String password = "";

    private static final String TAG = "LoginViewModel";

    public LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }

    protected void initLoginViewModel(SharedPreferences sharedPreferences) {
        ClientResponse domain;
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<ClientResponse> jsonAdapter = moshi.adapter(ClientResponse.class);
        String json = sharedPreferences.getString("domain", "");
        if (json.isEmpty()) {
            Log.d(TAG, "onCreate: ALGO PASO");//todo mejorar esta validacion
        } else {
            try {
                domain = jsonAdapter.fromJson(json);
                Utils.setImgDomain(domain.getClient().getApiUrl());
                showLogo.setValue(domain.getClient().getLogoUrl());
                String newApiUrl = domain.getClient().getApiUrl();
                Utils.domain = Utils.getWebProtocol(newApiUrl) + newApiUrl;
                RetrofitUrlManager.getInstance().putDomain("api", Utils.domain);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void login(String user, String password) {
        this.userName = user;
        this.password = password;

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    String token = "";
                    if (task.isSuccessful()) {
                        // Get new Instance ID token
                        token = task.getResult().getToken();
                    }

                    Disposable disposable = loginRepository
                            .login(userName, LoginViewModel.this.password, token)
                            .subscribe(
                                    LoginViewModel.this::onLoginSuccess,
                                    LoginViewModel.this::onLoginFailure
                            );
                    disposables.add(disposable);
                });
    }

    private void onLoginSuccess(LoginResponse loginResponse) {
        Utils.hideLoading();

        if (loginResponse == null) {
            return;
        }

        String token = loginResponse.getToken();
        String[] content = new String[2];
        content[0] = PreferenceKeys.PREF_TOKEN;
        content[1] = token;
        saveToPreference.setValue(content);

        content[0] = PreferenceKeys.PREF_USER_NAME;
        content[1] = userName.trim();
        saveToPreference.setValue(content);

        content[0] = PreferenceKeys.PREF_PASSWORD;
        content[1] = password.trim();
        saveToPreference.setValue(content);

        JWToken result = Utils.decode(token);
        String username = result.getUserName();
        String userType = result.getUserType();
        String locale = result.getLocale();
        String name = result.getFullName();

        JWT jwt = new JWT(token);
        String profileId = jwt.getClaim("profile_id").asString();
        content[0] = PreferenceKeys.PREF_PROFILE_ID;
        content[1] = profileId;
        saveToPreference.setValue(content);

        goToSyncActivity.setValue(true);
    }

    private void onLoginFailure(Throwable throwable) {
        Log.d(TAG, "onLoginFailure: error - " + throwable.getMessage());

        int stringRes = R.string.failure_connect;

        if (throwable instanceof HttpException) {
            int httpCode = ((HttpException) throwable).code();
            if (httpCode == 401) stringRes = R.string.bad_credentials;
        }

        mErrorLiveData.setValue(stringRes);
    }

    public LiveData<LoginResponse> getObservableLogin() {
        if (mLoginLiveData == null) {
            mLoginLiveData = new MutableLiveData<>();
        }
        return mLoginLiveData;
    }

    public LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }

    public LiveData<String> getObservableShowLogo() {
        if (showLogo == null) {
            showLogo = new MutableLiveData<>();
        }
        return showLogo;
    }

    public LiveData<String[]> getObservableSaveToPreference() {
        if (saveToPreference == null) {
            saveToPreference = new MutableLiveData<>();
        }
        return saveToPreference;
    }

    public LiveData<Boolean> getObservableGoToSyncActivity() {
        if (goToSyncActivity == null) {
            goToSyncActivity = new MutableLiveData<>();
        }
        return goToSyncActivity;
    }

}