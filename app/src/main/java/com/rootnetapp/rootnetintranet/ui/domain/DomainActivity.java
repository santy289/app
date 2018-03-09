package com.rootnetapp.rootnetintranet.ui.domain;

import android.arch.lifecycle.ViewModelProviders;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.databinding.ActivityDomainBinding;
import com.rootnetapp.rootnetintranet.models.responses.ClientResponse;
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
            case R.id.check_domain:
                checkDomain();
                break;
            default:
                break;
        }
    }

    private void checkDomain() {
        Utils.showLoading(this);
        domainViewModel.checkDomain(domainBinding.inputDomain.getText().toString().trim() + Utils.remainderOfDomain);
    }

    private void subscribe() {
        final Observer<ClientResponse> domainObserver = ((ClientResponse data) -> {
            Utils.hideLoading();
            if (null != data) {
                //data.getClient().getDomain()
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
        final Observer<Integer> errorObserver = ((Integer data) -> {
            Utils.hideLoading();
            if (null != data) {
                //TODO mejorar toast
                Toast.makeText(this, getString(data), Toast.LENGTH_LONG).show();
            }
        });
        domainViewModel.getObservableDomain().observe(this, domainObserver);
        domainViewModel.getObservableError().observe(this, errorObserver);
    }

}