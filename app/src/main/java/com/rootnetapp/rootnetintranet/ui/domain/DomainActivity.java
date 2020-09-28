package com.rootnetapp.rootnetintranet.ui.domain;

import androidx.lifecycle.ViewModelProviders;
import androidx.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.databinding.ActivityDomainBinding;
import com.rootnetapp.rootnetintranet.models.responses.domain.ClientResponse;
import com.rootnetapp.rootnetintranet.models.responses.domain.Module_;
import com.rootnetapp.rootnetintranet.models.responses.domain.Product;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.login.LoginActivity;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

public class DomainActivity extends AppCompatActivity {

    private static final String TAG = "DomainActivityLog";
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
        SharedPreferences sharedPref = getSharedPreferences(PreferenceKeys.PREF_SESSION, Context.MODE_PRIVATE);
        boolean isSignatureEnabled = isSignatureProductEnabled(jsonString);
        sharedPref.edit()
                .putString(PreferenceKeys.PREF_DOMAIN, jsonString)
                .putBoolean(PreferenceKeys.PREF_SIGNATURE, isSignatureEnabled)
                .apply();
    }

    private boolean isSignatureProductEnabled(String json) {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<ClientResponse> jsonAdapter = moshi.adapter(ClientResponse.class);
        ClientResponse domain;
        try {
            domain = jsonAdapter.fromJson(json);
            if (domain == null || domain.getClient() == null || domain.getClient().getProducts() == null) {
                return false;
            }
            List<Product> products = domain.getClient().getProducts();
            for (Product product : products) {
                if (product.getMachineName() == null || !product.getMachineName().equals("intranet")) {
                    continue;
                }
                List<Module_> modules = product.getModules();
                for (Module_ module : modules) {
                    if (module.getMachineName() != null && module.getMachineName().equals("workflows_signature_validateid")) {
                        return true;
                    }
                }
            }
            return false;
        } catch (IOException e) {
            Log.d(TAG, "setSignatureProductEnabled: " + e.getMessage());
            return false;
        }
    }

    private void openLogin(boolean open) {
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }

    private void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
    }

}