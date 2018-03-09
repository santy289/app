package com.rootnetapp.rootnetintranet.ui.domain;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.responses.ClientResponse;

/**
 * Created by Propietario on 09/03/2018.
 */

public class DomainViewModel extends ViewModel {

    private MutableLiveData<ClientResponse> mDomainLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private DomainRepository domainRepository;

    public DomainViewModel(DomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    protected void checkDomain(String domain) {
        domainRepository.checkDomain(domain).subscribe(this::onCheckDomainSuccess, this::onCheckDomainFailure);
    }

    private void onCheckDomainSuccess(ClientResponse o) {
        mDomainLiveData.setValue(o);
    }

    private void onCheckDomainFailure(Throwable throwable) {
        Log.d("test", "onCheckDomainFailure: " + throwable.getMessage());
        mErrorLiveData.setValue(R.string.failure_connect);
    }

    protected LiveData<ClientResponse> getObservableDomain() {
        if (mDomainLiveData == null) {
            mDomainLiveData = new MutableLiveData<>();
        }
        return mDomainLiveData;
    }

    protected LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }

}
