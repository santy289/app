package com.rootnetapp.rootnetintranet.ui.domain;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
;
import android.util.Log;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.models.responses.domain.ClientResponse;
import com.rootnetapp.rootnetintranet.models.responses.domain.Product;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class DomainViewModel extends ViewModel {

    private MutableLiveData<Integer> mErrorLiveData;
    private MutableLiveData<String> saveToPreferences;
    private MutableLiveData<Integer> showToast;
    private MutableLiveData<Boolean> openLogin;
    private MutableLiveData<Boolean> hideLoadingWidget;

    private DomainRepository domainRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private static final String TAG = "DomainViewModel";

    public DomainViewModel(DomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }

    protected void checkDomain(String domain) {
        Disposable disposable = domainRepository.checkDomain(domain).subscribe(clientResponse -> {
            hideLoadingWidget.setValue(true);
            if (null != clientResponse) {
                boolean active = false;
                for (Product product: clientResponse.getClient().getProducts()) {
                    if(product.getMachineName().equals("intranet")){
                        active = true;
                        Moshi moshi = new Moshi.Builder().build();
                        JsonAdapter<ClientResponse> jsonAdapter = moshi.adapter(ClientResponse.class);
                        String jsonString = jsonAdapter.toJson(clientResponse);
                        saveToPreferences.setValue(jsonString);
                        openLogin.setValue(true);
                    }
                }
                if(!active){
                    showToast.setValue(R.string.product_not_enabled);
                }
            }
        }, throwable -> {
            hideLoadingWidget.setValue(true);
            Log.d(TAG, "checkDomain: Error - " + throwable.getMessage());
            mErrorLiveData.setValue(R.string.failure_connect);
        });

        disposables.add(disposable);

    }

    protected LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }

    protected LiveData<String> getObservableSaveToPreferences() {
        if (saveToPreferences == null) {
            saveToPreferences = new MutableLiveData<>();
        }
        return saveToPreferences;
    }

    protected LiveData<Integer> getObservableShowToast() {
        if (showToast == null) {
            showToast = new MutableLiveData<>();
        }
        return showToast;
    }

    protected LiveData<Boolean> getObservableOpenLogin() {
        if (openLogin == null) {
            openLogin = new MutableLiveData<>();
        }
        return openLogin;
    }

    protected LiveData<Boolean> getObservableHideLoadingWidget() {
        if (hideLoadingWidget == null) {
            hideLoadingWidget = new MutableLiveData<>();
        }
        return hideLoadingWidget;
    }

}
