package com.rootnetapp.rootnetintranet.ui.quickactions.changestatus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
    public static final String EXTRA_TITLE = "Extra.Title";
    public static final String EXTRA_SUBTITLE = "Extra.Subtitle";

    private static final String TAG = "ChangeStatusActivity";

    @Inject
    ChangeStatusViewModelFactory changeStatusViewModelFactory;
    private ChangeStatusViewModel changeStatusViewModel;
    private ActivityChangeStatusBinding mBinding;
    private int loadCounter, localStorageCount, localStorageCompleted;

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

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (title == null) title = (String) getTitle();
        getSupportActionBar().setTitle(title);

        String subtitle = getIntent().getStringExtra(EXTRA_SUBTITLE);
        if (subtitle != null) getSupportActionBar().setSubtitle(subtitle);
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
        WebSettings ws = mBinding.webView.getSettings();

        ws.setJavaScriptEnabled(true);
        ws.setAllowFileAccess(true);

        Log.d(TAG, "Enabling HTML5-Features");
        ws.setDomStorageEnabled(true);
        ws.setDatabaseEnabled(true);
        ws.setAppCachePath(getFilesDir().getPath() + getPackageName() + "/cache/");
        ws.setAppCacheEnabled(true);
        Log.d(TAG, "Enabled HTML5-Features");

        loadCounter = localStorageCount = localStorageCompleted = 0;

        //create a listener that will execute several JavaScript scripts once the page loads
        mBinding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView webView, String url) {
                //this callback is fired after the scripts run

                if (loadCounter == 0) {
                    //execute this block only once
                    loadCounter++;

                    //check for the jwt token in the localStorage
                    String scriptGetJwt = changeStatusViewModel.getScriptGetLocalStorageItem("jwt");
                    webView.evaluateJavascript(scriptGetJwt, token -> {
                        Log.d("", "");

                        //check if the Android WebView needs a new jwt token
                        if (changeStatusViewModel.isTokenInvalid(token)) {

                            ValueCallback<String> callback = value -> {
                                localStorageCompleted++;

                                //check if all of the scripts were completed
                                if (localStorageCompleted >= localStorageCount) {
                                    String reloadScript = data.getReloadScript();
                                    //reload the page so the new localStorage items are used
                                    webView.evaluateJavascript(reloadScript, null);
                                }
                            };

                            for (String script : data.getLocalStorageScripts()) {
                                localStorageCount++;
                                webView.evaluateJavascript(script, callback);
                            }
                        }
                    });
                }
            }
        });

        //load the page
        mBinding.webView.loadUrl(data.getUrl());
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