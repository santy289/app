package com.rootnetapp.rootnetintranet.ui.quickactions.changestatus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.FragmentWorkflowDetailDiagramBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class ChangeStatusFragment extends Fragment {

    private static final String TAG = "ChangeStatusFragment";

    @Inject
    ChangeStatusViewModelFactory changeStatusViewModelFactory;
    private ChangeStatusViewModel changeStatusViewModel;
    private FragmentWorkflowDetailDiagramBinding mBinding;
    private WorkflowListItem mWorkflowListItem;
    private int loadCounter, localStorageCount, localStorageCompleted;

    public ChangeStatusFragment() {
        // Required empty public constructor
    }

    public static ChangeStatusFragment newInstance(WorkflowListItem item) {
        ChangeStatusFragment fragment = new ChangeStatusFragment();
        fragment.mWorkflowListItem = item;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_workflow_detail_diagram, container, false);
        View view = mBinding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        changeStatusViewModel = ViewModelProviders
                .of(this, changeStatusViewModelFactory)
                .get(ChangeStatusViewModel.class);

        SharedPreferences prefs = getContext()
                .getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("token", "");

        subscribe();
        changeStatusViewModel.init(prefs, token, mWorkflowListItem);

        return view;
    }

    private void subscribe() {
        changeStatusViewModel.getObservableWebViewData().observe(this, this::setupWebView);
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
        ws.setAppCachePath(
                getActivity().getFilesDir().getPath() + getActivity().getPackageName() + "/cache/");
        ws.setAppCacheEnabled(true);
        Log.d(TAG, "Enabled HTML5-Features");

        loadCounter = localStorageCount = localStorageCompleted = 0;

        //create a listener that will execute several JavaScript scripts once the page loads
        mBinding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showLoading(true);
            }

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
                        } else {
                            showLoading(false);
                        }
                    });
                } else {
                    showLoading(false);
                }
            }
        });

        //load the page
        mBinding.webView.loadUrl(data.getUrl());
    }

    @UiThread
    private void showLoading(boolean show) {
        mBinding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}