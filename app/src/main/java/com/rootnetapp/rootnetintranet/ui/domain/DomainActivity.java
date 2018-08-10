package com.rootnetapp.rootnetintranet.ui.domain;

import android.arch.lifecycle.ViewModelProviders;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.databinding.ActivityDomainBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.login.LoginActivity;

import javax.inject.Inject;

public class DomainActivity extends AppCompatActivity {

    @Inject
    DomainViewModelFactory domainViewModelFactory;
    DomainViewModel domainViewModel;
    private ActivityDomainBinding domainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        domainBinding = DataBindingUtil.setContentView(this, R.layout.activity_domain);
        domainViewModel = ViewModelProviders
                .of(this, domainViewModelFactory)
                .get(DomainViewModel.class);
        subscribe();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_check_domain:
                checkDomain();
                break;
            case R.id.btn_get_plan:
                // Todo funcionalidad Get a plan!!
                break;
            default:
                break;
        }
    }

    private void checkDomain() {
        String domain = domainBinding.inputDomain.getText().toString().trim();
        domainBinding.tilDomain.setError(null);
        if (TextUtils.isEmpty(domain)) {
            domainBinding.tilDomain.setError(getString(R.string.empty_domain));
        } else {
            Utils.showLoading(this);
            domainViewModel.checkDomain(domain + Utils.remainderOfDomain);
        }
    }

    private void subscribe() {
        final Observer<Integer> errorObserver = ((Integer data) -> {
            Utils.hideLoading();
            if (null != data) {
                //TODO mejorar toast
                Toast.makeText(this, getString(data), Toast.LENGTH_LONG).show();
            }
        });
        final Observer<Boolean> hideLoadingWidgetObserver = ((show) -> Utils.hideLoading());
        final Observer<String> saveToPreferenceObserver = (this::saveInPreferences);
        final Observer<Integer> showToastObserver = (this::showToast);
        final Observer<Boolean> openLoginObserver = (this::openLogin);
        domainViewModel.getObservableError().observe(this, errorObserver);
        domainViewModel.getObservableSaveToPreferences().observe(this, saveToPreferenceObserver);
        domainViewModel.getObservableShowToast().observe(this, showToastObserver);
        domainViewModel.getObservableOpenLogin().observe(this, openLoginObserver);
        domainViewModel.getObservableHideLoadingWidget().observe(this, hideLoadingWidgetObserver);
    }

    private void saveInPreferences(String jsonString) {
        SharedPreferences sharedPref = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        sharedPref.edit().putString("domain", jsonString).apply();
    }

    private void openLogin(boolean open) {
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }

    private void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
    }

}