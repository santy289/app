package com.rootnetapp.rootnetintranet.ui.workflowdetail.flowchart;

import android.content.SharedPreferences;
import android.util.Log;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.models.responses.domain.ClientResponse;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.disposables.CompositeDisposable;

public class FlowchartViewModel extends ViewModel {

    private static final String TAG = "FlowchartViewModel";

    private FlowchartRepository repository;
    private MutableLiveData<WebViewData> mWebViewDataLiveData;

    private String mToken;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public FlowchartViewModel(FlowchartRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }

    protected void init(SharedPreferences sharedPreferences, String token,
                        WorkflowListItem workflow) {
        mToken = token;

        String json = sharedPreferences.getString("domain", "");
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<ClientResponse> jsonAdapter = moshi.adapter(ClientResponse.class);
        ClientResponse domain;

        try {
            domain = jsonAdapter.fromJson(json);

            String url = "https://" + domain.getClient().getDomain()
                    + "/Intranet/workflow/tree/" + workflow.getWorkflowId();

            WebViewData data = new WebViewData(url, getLocalStorageItemScripts());
            mWebViewDataLiveData.setValue(data);
        } catch (IOException e) {
            Log.e(TAG, "initMainViewModel: error: " + e.getMessage());
        }
    }

    /**
     * Generates a list of JavaScript scripts to be executed once the WebView loads.
     *
     * @return list of scripts
     */
    private List<String> getLocalStorageItemScripts() {
        List<String> scripts = new ArrayList<>();

        String rawToken = mToken.replace("Bearer ", "");
        scripts.add(getScriptSetLocalStorageItem("jwt", "\\\"" + rawToken + "\\\""));
        scripts.add(getScriptSetLocalStorageItem("lang", Locale.getDefault().getLanguage()));
        scripts.add(getScriptSetLocalStorageItem("RootnetAcl",
                "{\"roles\":[\"rootnet_roles\"],\"abilities\":{\"rootnet_roles\":[\"ROLE_USER\",\"ROLE_ROOTNET_USER\",\"ROLE_ADMIN\",\"ROLE_ACCOUNT_CREATE\",\"ROLE_ACCOUNT_UPDATE\",\"ROLE_ACCOUNT_DELETE\",\"ROLE_ACCOUNT_DELETE_ALL\",\"ROLE_ACCOUNT_DEACTIVATE\",\"ROLE_ACCOUNT_DEACTIVATE_ALL\",\"ROLE_ACCOUNT_MANAGE_USERS\",\"ROLE_SUPPORT_TICKET\",\"ROLE_SUPPORT_TICKET_CREATE\",\"ROLE_SUPPORT_TICKET_UPDATE\",\"ROLE_SUPPORT_TICKET_DELETE\",\"ROLE_SUPPORT_TICKET_DELETE_ALL\",\"ROLE_SUPPORT_TICKET_STATUS\",\"ROLE_MANAGE_ACCOUNTS_GENERALS\",\"ROLE_MANAGE_ACCOUNTS_CONTACT_TYPES\",\"ROLE_MANAGE_ACCOUNTS_CONTACT_TYPE_FIELDS\",\"ROLE_MANAGE_ACCOUNTS_CONTACT_TYPE_LISTS\",\"ROLE_MANAGE_ACCOUNTS_CONTACT_TRACKING_FIELDS\",\"ROLE_MANAGE_ACCOUNTS_SUB_CONTACT_FIELDS\",\"ROLE_MANAGE_ACCOUNTS_CONTACT_TYPE_PRODUCTS\",\"ROLE_MANAGE_ACCOUNTS_LOG\",\"ROLE_SUB_CONTACT_CREATE\",\"ROLE_SUB_CONTACT_UPDATE\",\"ROLE_SUB_CONTACT_DELETE\",\"ROLE_MANAGE_SUPPORT_PRIORITIES\",\"ROLE_MANAGE_SUPPORT_RATINGS\",\"ROLE_MANAGE_SUPPORT_STATUS\",\"ROLE_MANAGE_SUPPORT_TICKET_TYPE\",\"ROLE_MANAGE_ROLES\",\"ROLE_MANAGE_PERMISSIONS\",\"ROLE_MANAGE_PROJECTS\",\"ROLE_MANAGE_NOTIFICATIONS\",\"ROLE_MANAGE_SALES_FORCE\",\"ROLE_PRODUCT_CREATE\",\"ROLE_PRODUCT_DELETE\",\"ROLE_PRODUCT_UPDATE\",\"ROLE_PRODUCTS\",\"ROLE_SERVICE_CREATE\",\"ROLE_SERVICE_DELETE\",\"ROLE_SERVICE_UPDATE\",\"ROLE_SERVICES\",\"ROLE_CONTACT_CREATE\",\"ROLE_CONTACT_UPDATE\",\"ROLE_CONTACT_DELETE\",\"ROLE_CONTACT_DELETE_ALL\",\"ROLE_CONTACT_DEACTIVATE\",\"ROLE_CONTACT_DEACTIVATE_ALL\",\"ROLE_CONTACT_UPDATE_TYPE\",\"ROLE_CONTACT_IMPORT\",\"ROLE_CONTACT_TRACKING_CREATE\",\"ROLE_CONTACT_HISTORY\",\"ROLE_PRODUCT_SERVICE_ACTIVATE\",\"ROLE_MANAGE_ORGANIZATION_USERS\",\"ROLE_MANAGE_ORGANIZATION_GENERALS\",\"ROLE_MANAGE_ORGANIZATION_DEPARTMENTS\",\"ROLE_MANAGE_OWN_SPRINT\",\"ROLE_MANAGE_ALL_SPRINT\",\"ROLE_MANAGE_ALL_GOAL\",\"ROLE_MANAGE_OWN_GOAL\",\"ROLE_MANAGE_SALE_FLOW\",\"ROLE_MANAGE_SALE_FIELD\",\"ROLE_DELETE_CLOSED_SPRINT\",\"ROLE_SEE_ALL_OPPORTUNITIES\",\"ROLE_SEE_OWN_OPPORTUNITIES\",\"ROLE_EDIT_ALL_OPPORTUNITIES\",\"ROLE_EDIT_OWN_OPPORTUNITIES\",\"ROLE_ADD_ALL_OPPORTUNITIES\",\"ROLE_ADD_OWN_OPPORTUNITIES\",\"ROLE_DELETE_ALL_OPPORTUNITIES\",\"ROLE_DELETE_OWN_OPPORTUNITIES\",\"ROLE_SEE_ALL_STATUS_CHANGED\",\"ROLE_SEE_OWN_STATUS_CHANGED\",\"ROLE_CREATE_REVITIONS\",\"ROLE_SEE_REVITIONS\",\"ROLE_MANAGE_SALES_PERFORMANCE\",\"ROLE_MANAGE_ALL_SALES_PERFORMANCE\",\"ROLE_BOARDS\",\"ROLE_MANAGE_INTEGRATIONS\",\"ROLE_RECURRENCES\",\"ROLE_MANAGE_MISCELLANEOUS\",\"ROLE_SPRINT_STATUS_DELETE_OWN\",\"ROLE_SPRINT_STATUS_DELETE_ALL\",\"ROLE_PROJECT_EXPORT\",\"ROLE_TRACK_DELETE_OWN\",\"ROLE_TRACK_DELETE_ALL\",\"ROLE_CONTACT_EXPORT\",\"ROLE_INTRANET_HOME_VIEW\",\"ROLE_WORKFLOW_CREATE\",\"ROLE_WORKFLOW_DELETE\",\"ROLE_WORKFLOW_SWITCH_STATE\",\"ROLE_WORKFLOW_DELETE_ALL\",\"ROLE_MANAGE_WORKFLOW\",\"ROLE_WORKFLOW_EXPORT\",\"ROLE_WORKFLOW_ACTIVATE_ALL\",\"ROLE_WORKFLOW_EDIT_ALL\",\"ROLE_WORKFLOW_IMPORT\",\"ROLE_PROJECT_SWITCH_STATE\",\"ROLE_PROJECT_DELETE_ALL\",\"ROLE_PROJECT_ACTIVATE_ALL\",\"ROLE_PROJECT_EDIT_ALL\",\"ROLE_PROJECT_CREATE\",\"ROLE_PROJECT_DELETE\",\"ROLE_MANAGE_PROJECT\",\"ROLE_ACCOUNT_PAGE\"]}}"));

        return scripts;
    }

    /**
     * Create a JavaScript script to set a localStorage value.
     *
     * @param key   localStorage key.
     * @param value localStorage value.
     *
     * @return script to set the localStorage value.
     */
    private String getScriptSetLocalStorageItem(String key, String value) {
        return String.format(Locale.US, "localStorage.setItem('%s','%s');", key, value);
    }

    /**
     * Create a script of a function that returns a specific localStorage value.
     *
     * @param key localStorage key.
     *
     * @return corresponding value to the localStorage key.
     */
    protected String getScriptGetLocalStorageItem(String key) {
        return String.format(
                Locale.US,
                "(function() { return localStorage.getItem('%s'); })();",
                key
        );
    }

    /**
     * Validates whether the jwt token in the WebView matches the SharedPreferences token,
     * determining its validation.
     *
     * @param jwtToken WebView localStorage jwt token.
     *
     * @return true - token needs to be updated; false - the token is good.
     */
    protected boolean isTokenInvalid(String jwtToken) {
        return jwtToken == null || !jwtToken
                .replace("\"\\\"", "") //remove extra quotes and escapes
                .replace("\\\"\"", "") //remove extra quotes and escapes
                .equals(mToken.replace("Bearer ", "")); //remove Bearer prefix
    }

    protected LiveData<WebViewData> getObservableWebViewData() {
        if (mWebViewDataLiveData == null) {
            mWebViewDataLiveData = new MutableLiveData<>();
        }
        return mWebViewDataLiveData;
    }
}
