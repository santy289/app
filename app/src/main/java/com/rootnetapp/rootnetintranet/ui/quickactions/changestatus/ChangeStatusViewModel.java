package com.rootnetapp.rootnetintranet.ui.quickactions.changestatus;

import android.content.SharedPreferences;
import android.util.Log;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.models.responses.domain.ClientResponse;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;

public class ChangeStatusViewModel extends ViewModel {

    private static final String TAG = "ChangeStatusViewModel";

    private ChangeStatusRepository repository;
    private MutableLiveData<WebViewData> mWebViewDataLiveData;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public ChangeStatusViewModel(ChangeStatusRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }

    protected void init(SharedPreferences sharedPreferences, String token,
                        WorkflowListItem workflow) {
        String json = sharedPreferences.getString("domain", "");
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<ClientResponse> jsonAdapter = moshi.adapter(ClientResponse.class);
        ClientResponse domain;

        try {
            domain = jsonAdapter.fromJson(json);

            String url = "https://" + domain.getClient()
                    .getDomain() + "/Intranet/workflow/" + workflow.getWorkflowId();

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", token);

            WebViewData data = new WebViewData(headers, url, domain);
            mWebViewDataLiveData.setValue(data);
        } catch (IOException e) {
            Log.e(TAG, "initMainViewModel: error: " + e.getMessage());
        }
    }

    protected LiveData<WebViewData> getObservableWebViewData() {
        if (mWebViewDataLiveData == null) {
            mWebViewDataLiveData = new MutableLiveData<>();
        }
        return mWebViewDataLiveData;
    }
}
