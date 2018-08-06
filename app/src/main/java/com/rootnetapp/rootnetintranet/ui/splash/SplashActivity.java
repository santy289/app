package com.rootnetapp.rootnetintranet.ui.splash;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.models.responses.domain.ClientResponse;
import com.rootnetapp.rootnetintranet.models.responses.login.LoginResponse;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.sync.SyncActivity;
import com.rootnetapp.rootnetintranet.ui.domain.DomainActivity;
import com.rootnetapp.rootnetintranet.ui.login.LoginViewModel;
import com.rootnetapp.rootnetintranet.ui.login.LoginViewModelFactory;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

import javax.inject.Inject;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;

public class SplashActivity extends AppCompatActivity {

    @Inject
    LoginViewModelFactory loginViewModelFactory;
    LoginViewModel loginViewModel;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        loginViewModel = ViewModelProviders
                .of(this, loginViewModelFactory)
                .get(LoginViewModel.class);
        sharedPref = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String user = sharedPref.getString("username", "");
        String password = sharedPref.getString("password", "");
        if(TextUtils.isEmpty(user)||TextUtils.isEmpty(password)){
            startActivity(new Intent(SplashActivity.this, DomainActivity.class));
            // close splash activity
            finish();
        }else{
            String json = sharedPref.getString("domain", "");
            //todo cambiar por consulta al viewmodel
            if (json.isEmpty()) {
                Log.d("test", "onCreate: ALGO PASO");//todo mejorar esta validacion
            } else {
                try {
                    Moshi moshi = new Moshi.Builder().build();
                    JsonAdapter<ClientResponse> jsonAdapter = moshi.adapter(ClientResponse.class);
                    ClientResponse domain = jsonAdapter.fromJson(json);
                    Utils.domain = "https://" + domain.getClient().getApiUrl();
                    Utils.imgDomain = "http://" + domain.getClient().getApiUrl();
                    RetrofitUrlManager.getInstance().putDomain("api", Utils.domain);
                    //todo solo para PRUEBAS
                    //RetrofitUrlManager.getInstance().putDomain("localhost", "http://192.168.42.183/");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            subscribe();
            loginViewModel.login(user, password);
        }
    }

    private void subscribe() {

        final Observer<LoginResponse> loginObserver = ((LoginResponse data) -> {
            if (null != data) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("token",data.getToken()).apply();
                startActivity(new Intent(this, SyncActivity.class));
                finishAffinity();
            }
        });

        final Observer<Integer> errorObserver = ((Integer data) -> {
            startActivity(new Intent(SplashActivity.this, DomainActivity.class));
            // close splash activity
            finish();
        });
        loginViewModel.getObservableLogin().observe(this, loginObserver);
        loginViewModel.getObservableError().observe(this, errorObserver);
    }
}
