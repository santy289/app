package com.rootnetapp.rootnetintranet.ui.workflowdetail.flowchart;

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
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class FlowchartFragment extends Fragment {

    private static final String TAG = "FlowchartFragment";

    @Inject
    FlowchartViewModelFactory flowchartViewModelFactory;
    private FlowchartViewModel flowchartViewModel;
    private FragmentWorkflowDetailDiagramBinding mBinding;
    private WorkflowListItem mWorkflowListItem;
    private int loadCounter, localStorageCount, localStorageCompleted;
    private static final String SAVE_WORKFLOW_TYPE = "SAVE_WORKFLOW_TYPE";

    public FlowchartFragment() {
        // Required empty public constructor
    }

    public static FlowchartFragment newInstance(WorkflowListItem item) {
        FlowchartFragment fragment = new FlowchartFragment();
        fragment.mWorkflowListItem = item;
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVE_WORKFLOW_TYPE, mWorkflowListItem);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.mWorkflowListItem = savedInstanceState.getParcelable(SAVE_WORKFLOW_TYPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_workflow_detail_diagram, container, false);
        View view = mBinding.getRoot();
        ((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        flowchartViewModel = ViewModelProviders
                .of(this, flowchartViewModelFactory)
                .get(FlowchartViewModel.class);

        SharedPreferences prefs = getContext()
                .getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("token", "");

        subscribe();
        flowchartViewModel.init(prefs, token, mWorkflowListItem);

        return view;
    }

    private void subscribe() {
        flowchartViewModel.getObservableWebViewData().observe(getViewLifecycleOwner(), this::setupWebView);
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
                    String scriptGetJwt = flowchartViewModel.getScriptGetLocalStorageItem("jwt");
                    webView.evaluateJavascript(scriptGetJwt, token -> {
                        Log.d("", "");

                        //check if the Android WebView needs a new jwt token
                        if (flowchartViewModel.isTokenInvalid(token)) {

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
                            showWebView(true);
                            showLoading(false);
                        }
                    });
                } else {
                    showWebView(true);
                    showLoading(false);
                }
            }
        });

        //load the page
        mBinding.webView.loadUrl(data.getUrl());
    }

    @UiThread
    private void showWebView(boolean show){
        mBinding.webView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @UiThread
    private void showLoading(boolean show) {
        mBinding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}