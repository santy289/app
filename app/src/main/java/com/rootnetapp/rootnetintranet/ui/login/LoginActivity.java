package com.rootnetapp.rootnetintranet.ui.login;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.databinding.ActivityLoginBinding;
import com.rootnetapp.rootnetintranet.models.responses.domain.ClientResponse;
import com.rootnetapp.rootnetintranet.models.responses.login.LoginResponse;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.sync.SyncActivity;
import com.rootnetapp.rootnetintranet.ui.domain.DomainActivity;
import com.rootnetapp.rootnetintranet.ui.resetPass.resetpassdialog.ResetPasswordDialog;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import javax.inject.Inject;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;

public class LoginActivity extends AppCompatActivity {

    @Inject
    LoginViewModelFactory loginViewModelFactory;
    LoginViewModel loginViewModel;
    private ActivityLoginBinding loginBinding;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        loginViewModel = ViewModelProviders
                .of(this, loginViewModelFactory)
                .get(LoginViewModel.class);
        ClientResponse domain;
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<ClientResponse> jsonAdapter = moshi.adapter(ClientResponse.class);
        //todo Preguntar como implementar SharesPreferencesModule en los Viewmodels para cada tipo de clase guardada
        sharedPref = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String json = sharedPref.getString("domain", "");
        //todo cambiar por consulta al viewmodel
        if (json.isEmpty()) {
            Log.d("test", "onCreate: ALGO PASO");//todo mejorar esta validacion
        } else {
            try {
                domain = jsonAdapter.fromJson(json);
                Utils.domain = "https://" + domain.getClient().getApiUrl();
                Utils.imgDomain = "http://" + domain.getClient().getApiUrl();
                Picasso.get().load(Utils.URL + domain.getClient().getLogoUrl()).into(loginBinding.logo);
                RetrofitUrlManager.getInstance().putDomain("api", Utils.domain);
                //todo solo para PRUEBAS
                RetrofitUrlManager.getInstance().putDomain("localhost", "http://192.168.42.183/");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        subscribe();
    }

    private void subscribe() {
        final Observer<LoginResponse> loginObserver = ((LoginResponse data) -> {
            Utils.hideLoading();
            if (null != data) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("token", data.getToken()).apply();
                String user = loginBinding.inputUser.getText().toString().trim();
                String password = loginBinding.inputPassword.getText().toString().trim();
                editor.putString("username", user).apply();
                editor.putString("password", password).apply();
                //todo manejo de JWT
                JWT jwt = new JWT(data.getToken());
                String username = jwt.getClaim("username").asString();
                String userType = jwt.getClaim("user_type").asString();
                String locale = jwt.getClaim("locale").asString();
                String name = jwt.getClaim("full_name").asString();
                //String department = jwt.getClaim("department").asString();
                //claim.asString();
                Log.d("test", "username: " + username +
                        " Type: " + userType + " locale: " + locale + " Name: " + name);
                startActivity(new Intent(this, SyncActivity.class));
                finishAffinity();
            }
        });

        final Observer<Integer> errorObserver = ((Integer data) -> {
            Utils.hideLoading();
            if (null != data) {
                //TODO mejorar toast
                Toast.makeText(this, getString(data), Toast.LENGTH_LONG).show();
            }
        });
        loginViewModel.getObservableLogin().observe(this, loginObserver);
        loginViewModel.getObservableError().observe(this, errorObserver);
    }

    private void login() {
        String user = loginBinding.inputUser.getText().toString().trim();
        String password = loginBinding.inputPassword.getText().toString().trim();
        loginBinding.tilUser.setError(null);
        loginBinding.tilPassword.setError(null);
        boolean canLogin = true;
        if (TextUtils.isEmpty(user)) {
            loginBinding.tilUser.setError(getString(R.string.empty_user));
            canLogin = false;
        }
        if (TextUtils.isEmpty(password)) {
            loginBinding.tilPassword.setError(getString(R.string.empty_password));
            canLogin = false;
        }
        if (canLogin) {
            Utils.showLoading(this);
            loginViewModel.login(user, password);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_change:
                startActivity(new Intent(this, DomainActivity.class));
                finishAffinity();
                break;
            case R.id.btn_forgot_pass:
                ResetPasswordDialog.newInstance().show(getSupportFragmentManager(), "password_recover");
                break;
            default:
                break;
        }
    }

}
