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

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.databinding.ActivityLoginBinding;
import com.rootnetapp.rootnetintranet.models.responses.domain.ClientResponse;
import com.rootnetapp.rootnetintranet.models.responses.login.JWToken;
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

    private static final String TAG = "LoginActivity.Rootnet";

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
            Log.d(TAG, "onCreate: ALGO PASO");//todo mejorar esta validacion
        } else {
            try {
                domain = jsonAdapter.fromJson(json);
                Utils.imgDomain = "http://" + domain.getClient().getApiUrl();
                Picasso.get().load(Utils.URL + domain.getClient().getLogoUrl()).into(loginBinding.logo);
                String newApiUrl = domain.getClient().getApiUrl();
                newApiUrl = Utils.replaceLast(newApiUrl, "/v1/", "");
                Utils.domain = "https://" + newApiUrl;
                RetrofitUrlManager.getInstance().putDomain("api", Utils.domain);
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
                String token = data.getToken();
                editor.putString("token", token).apply();
                String user = loginBinding.inputUser.getText().toString().trim();
                String password = loginBinding.inputPassword.getText().toString().trim();
                editor.putString("username", user).apply();
                editor.putString("password", password).apply();

                JWToken result = Utils.decode(token);
                String username = result.getUserName();
                String userType = result.getUserType();
                String locale = result.getLocale();
                String name = result.getFullName();

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
