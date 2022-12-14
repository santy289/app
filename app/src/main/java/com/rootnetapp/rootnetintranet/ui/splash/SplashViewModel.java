package com.rootnetapp.rootnetintranet.ui.splash;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.iid.FirebaseInstanceId;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.models.responses.domain.ClientResponse;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;

public class SplashViewModel extends ViewModel {
    private MutableLiveData<Boolean> goToDomain;
    private MutableLiveData<Boolean> goToSync;
    private MutableLiveData<String[]> saveToPreference;
    private SplashRepository splashRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final static String TAG = "SplashViewModel";

    public SplashViewModel(SplashRepository splashRepository) {
        this.splashRepository = splashRepository;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }

    public void initSplashViewModel(SharedPreferences sharedPreferences) {
        String token = sharedPreferences.getString("token", "");
        if (TextUtils.isEmpty(token)) {
            String user = sharedPreferences.getString("username", "");
            String password = sharedPreferences.getString("password", "");
            attemptToLogin(user, password);
            return;
        }

        String json = sharedPreferences.getString("domain", "");
        if (json.isEmpty()) {
            Log.d(TAG, "initSplashViewModel: No domain value, opening Domain activity");
            goToDomain.setValue(true);
            return;
        }
        try {
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<ClientResponse> jsonAdapter = moshi.adapter(ClientResponse.class);
            ClientResponse domain = jsonAdapter.fromJson(json);
            if (domain == null && domain.getClient() == null) {
                Log.d(TAG, "initSplashViewModel: domain is null or client value is null");
                goToDomain.setValue(true);
                return;
            }
            String apiUrl = domain.getClient().getApiUrl();
            Utils.domain = Utils.getWebProtocol(apiUrl) + apiUrl;
            Utils.setImgDomain(domain.getClient().getApiUrl());
            RetrofitUrlManager.getInstance().putDomain("api", Utils.domain);
        } catch (IOException e) {
            e.printStackTrace();
            goToDomain.setValue(true);
            return;
        }
        goToSync.setValue(true);
    }

    public void attemptToLogin(String user, String password) {
        if(TextUtils.isEmpty(user)||TextUtils.isEmpty(password)){
            goToDomain.setValue(true);
            return;
        }

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    String firebaseToken = "";
                    if (task.isSuccessful()) {
                        // Get new Instance ID token
                        firebaseToken = task.getResult().getToken();
                    }

                    Disposable disposable = splashRepository.login(user, password, firebaseToken).subscribe(loginResponse -> {
                        if (loginResponse == null) {
                            goToDomain.setValue(true);
                            return;
                        }
                        String token = loginResponse.getToken();
                        String[] content = new String[2];
                        content[0] = "token";
                        content[1] = token;
                        saveToPreference.setValue(content);
                        goToSync.setValue(true);
                    }, throwable -> {
                        Log.d(TAG, "attemptToLogin: Something failed with network request: " + throwable.getMessage());
                        goToDomain.setValue(true);
                    });
                    disposables.add(disposable);
                });
    }

    public LiveData<Boolean> getObservableGoToDomain() {
        if (goToDomain == null) {
            goToDomain = new MutableLiveData<>();
        }
        return goToDomain;
    }
    public LiveData<Boolean> getObservableGoToSync() {
        if (goToSync == null) {
            goToSync = new MutableLiveData<>();
        }
        return goToSync;
    }

    public LiveData<String[]> getObservableSaveToPreference() {
        if (saveToPreference == null) {
            saveToPreference = new MutableLiveData<>();
        }
        return saveToPreference;
    }

}
