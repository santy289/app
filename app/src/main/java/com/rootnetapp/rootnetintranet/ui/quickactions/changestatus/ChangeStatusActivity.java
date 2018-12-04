package com.rootnetapp.rootnetintranet.ui.quickactions.changestatus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebSettings;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.ActivityChangeStatusBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;

import javax.inject.Inject;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

public class ChangeStatusActivity extends AppCompatActivity {

    public static final String EXTRA_WORKFLOW_LIST_ITEM = "Extra.WorkflowListItem";

    private static final String TAG = "ChangeStatusActivity";

    @Inject
    ChangeStatusViewModelFactory changeStatusViewModelFactory;
    private ChangeStatusViewModel changeStatusViewModel;
    private ActivityChangeStatusBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_change_status);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        changeStatusViewModel = ViewModelProviders
                .of(this, changeStatusViewModelFactory)
                .get(ChangeStatusViewModel.class);
        SharedPreferences prefs = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("token", "");
        WorkflowListItem item = getIntent().getParcelableExtra(EXTRA_WORKFLOW_LIST_ITEM);

        setActionBar();
        subscribe();

        changeStatusViewModel.init(prefs, token, item);
    }

    private void subscribe() {
        changeStatusViewModel.getObservableWebViewData().observe(this, this::setupWebView);
    }

    private void setActionBar() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(getTitle());
    }

    /**
     * Creates and setups the WebView that will be used to display the ChangeStatus action. The web
     * page uses Angular.js with HTML5, so we need to enable all of the HTML5 features of the
     * WebView. Also, we need to send our mobile device user session to the web page, using the
     * WebView localStorage.
     *
     * @param data the object holding every value we need to setup the WebView.
     */
    @UiThread
    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView(WebViewData data) {
        // TODO check session, the bearer token is not enough to open the active session.
        // For the first load, the website will prompt the login page. After that, consecutively, it will display the proper page.
        WebSettings ws = mBinding.webView.getSettings();

        ws.setJavaScriptEnabled(true);
        ws.setAllowFileAccess(true);

        Log.d(TAG, "Enabling HTML5-Features");
        ws.setDomStorageEnabled(true);
        ws.setDatabaseEnabled(true);
        ws.setAppCachePath(
                getFilesDir().getPath() + getFilesDir().getPath() + getPackageName() + "/cache/");
        ws.setAppCacheEnabled(true);
        Log.d(TAG, "Enabled HTML5-Features");

        //fixme this should save on the browser localStorage and load the user session
        /*mBinding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView webView, String url) {
                String authScript = "localStorage.setItem('jwt','" + mToken + "');";
                webView.evaluateJavascript(authScript, null);

                Moshi moshi = new Moshi.Builder().build();

                JsonAdapter<ClientResponse> clientResponseJsonAdapter = moshi.adapter(ClientResponse.class);
                String globalJson = clientResponseJsonAdapter.toJson(data.getClientResponse());
                String globalScript = "localStorage.setItem('global','" + globalJson + "');";
                webView.evaluateJavascript(globalScript, null);

                JsonAdapter<Client> clientJsonAdapter = moshi.adapter(Client.class);
                String clientJson = clientJsonAdapter.toJson(data.getClientResponse().getClient());
                String clientScript = "localStorage.setItem('client','" + clientJson + "');";
                webView.evaluateJavascript(clientScript, null);
            }
        });*/
        mBinding.webView.loadUrl(data.getUrl(), data.getHeaders());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}