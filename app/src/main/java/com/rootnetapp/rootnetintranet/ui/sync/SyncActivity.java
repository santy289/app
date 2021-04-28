package com.rootnetapp.rootnetintranet.ui.sync;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.fcm.NotificationDataKeys;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.domain.DomainActivity;
import com.rootnetapp.rootnetintranet.ui.main.MainActivity;

import javax.inject.Inject;

public class SyncActivity extends AppCompatActivity {

    @Inject
    SyncHelper syncHelper;
    private ProgressBar bar;

    private String notificationWorkflowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        checkForExternalIntent();

        SharedPreferences prefs = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString(PreferenceKeys.PREF_TOKEN, "");

        if (syncHelper != null) {
            syncHelper = new SyncHelper(syncHelper.getApiInterface(), syncHelper.getDatabase());
        }

        bar = findViewById(R.id.progress_bar);
        bar.setMax(SyncHelper.MAX_ENDPOINT_CALLS);
        subscribe();
        syncHelper.syncData(token);
    }

    private void checkForExternalIntent() {
        notificationWorkflowId = getIntent().getStringExtra(NotificationDataKeys.KEY_WORKFLOW_ID);
    }

    private void attemptToLogin() {
        SharedPreferences prefs = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String user = prefs.getString("username", "");
        String password = prefs.getString("password", "");
        syncHelper.attemptLogin(user, password);
    }

    private void goToDomain(Boolean open) {
        startActivity(new Intent(SyncActivity.this, DomainActivity.class));
        finishAffinity();
    }

    private void saveInPreferences(String key, String content) {
        SharedPreferences sharedPref = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        sharedPref.edit().putString(key, content).apply();
    }

    private void saveIntegerInPreference(String key, Integer id) {
        SharedPreferences sharedPref = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        sharedPref.edit().putInt(key, id).apply();
    }

    private void subscribe() {
        subscribeForLogin();
        final Observer<Boolean> syncObserver = ((Boolean data) -> {
            syncHelper.clearDisposables();
            syncHelper.getObservableSync().removeObservers(this);
            goToMain();
        });

        final Observer<Integer> progressObserver = ((Integer data) -> {
            if (data != null) {
                bar.setProgress(data);
            }
        });

        syncHelper.getObservableSync().observe(this, syncObserver);
        syncHelper.getObservableProgress().observe(this, progressObserver);
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);

        if (!TextUtils.isEmpty(notificationWorkflowId)) {
            intent.putExtra(NotificationDataKeys.KEY_WORKFLOW_ID, notificationWorkflowId);
        }

        startActivity(intent);
        finishAffinity();
    }

    private void subscribeForLogin() {
        final Observer<Boolean> attemptTokenRefreshObserver = (response -> attemptToLogin());
        final Observer<String> saveToPreferenceObserver = (content -> saveInPreferences(
                PreferenceKeys.PREF_TOKEN, content));
        final Observer<Boolean> goToDomainObserver = (this::goToDomain);
        syncHelper.getObservableAttemptTokenRefresh().observe(this, attemptTokenRefreshObserver);
        syncHelper.getObservableSavetoPreference().observe(this, saveToPreferenceObserver);
        syncHelper.getObservableGoToDomain().observe(this, goToDomainObserver);
        syncHelper.saveIdToPreference.observe(this,
                integer -> saveIntegerInPreference(PreferenceKeys.PREF_CATEGORY, integer));
        syncHelper.saveStringToPreference.observe(this, value ->
                saveInPreferences(value[SyncHelper.INDEX_KEY_STRING],
                        value[SyncHelper.INDEX_KEY_VALUE]
                ));
        syncHelper.message.observe(this, message -> {
            if (message == null) {
                return;
            }
            Toast.makeText(this, message.messageRes, Toast.LENGTH_SHORT).show();
        });
    }
}
