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
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.sync.SyncActivity;
import com.rootnetapp.rootnetintranet.ui.domain.DomainActivity;
import com.rootnetapp.rootnetintranet.ui.resetPass.resetpassdialog.ResetPasswordDialog;
import com.squareup.picasso.Picasso;
import javax.inject.Inject;

public class LoginActivity extends AppCompatActivity {

    @Inject
    LoginViewModelFactory loginViewModelFactory;
    LoginViewModel loginViewModel;
    private ActivityLoginBinding loginBinding;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        loginViewModel = ViewModelProviders
                .of(this, loginViewModelFactory)
                .get(LoginViewModel.class);
        subscribe();
        SharedPreferences sharedPreferences = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        loginViewModel.initLoginViewModel(sharedPreferences);
    }

    private void subscribe() {
        final Observer<Integer> errorObserver = ((Integer data) -> {
            Utils.hideLoading();
            if (null != data) {
                //TODO mejorar toast
                Toast.makeText(this, getString(data), Toast.LENGTH_LONG).show();
            }
        });

        final Observer<String> showLogoObserver = (this::showLogo);
        final Observer<String[]> saveToPreferenceObserver = (this::saveInPreferences);
        final Observer<Boolean> goToSyncActivityObserver = (this::goToSyncActivity);
        loginViewModel.getObservableError().observe(this, errorObserver);
        loginViewModel.getObservableShowLogo().observe(this, showLogoObserver);
        loginViewModel.getObservableSaveToPreference().observe(this, saveToPreferenceObserver);
        loginViewModel.getObservableGoToSyncActivity().observe(this, goToSyncActivityObserver);
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

    private void showLogo(String logoUrl) {
        Log.d(TAG, "showLogo: " + Utils.URL + logoUrl);
        Picasso.get().load(Utils.URL + logoUrl).into(loginBinding.logo);
    }

    private void saveInPreferences(String[] content) {
        SharedPreferences.Editor editor = getSharedPreferences("Sessions", Context.MODE_PRIVATE).edit();
        editor.putString(content[0], content[1]).apply();
    }

    private void goToSyncActivity(boolean open) {
        startActivity(new Intent(this, SyncActivity.class));
        finishAffinity();
    }

}
