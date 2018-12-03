package com.rootnetapp.rootnetintranet.ui.quickactions.changestatus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
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
    ChangeStatusViewModel changeStatusViewModel;
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

        mBinding.webView.setWebChromeClient(new WebChromeClient());
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