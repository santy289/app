package com.rootnetapp.rootnetintranet.ui.splash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.rootnetapp.rootnetintranet.fcm.NotificationDataKeys;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.domain.DomainActivity;
import com.rootnetapp.rootnetintranet.ui.sync.SyncActivity;

import java.util.List;

import javax.inject.Inject;

public class SplashActivity extends AppCompatActivity {

    @Inject
    SplashViewModelFactory splashViewModelFactory;
    SplashViewModel splashViewModel;

    private String notificationWorkflowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        splashViewModel = ViewModelProviders
                .of(this, splashViewModelFactory)
                .get(SplashViewModel.class);

        checkForPushNotificationIntent();
        checkForDeepLinkIntent();

        subscribe();
        SharedPreferences sharedPreferences = getSharedPreferences("Sessions",
                Context.MODE_PRIVATE);
        splashViewModel.initSplashViewModel(sharedPreferences);
    }

    private void checkForPushNotificationIntent() {
        notificationWorkflowId = getIntent().getStringExtra(NotificationDataKeys.KEY_WORKFLOW_ID);
    }

    private void checkForDeepLinkIntent() {
        Uri uri = getIntent().getData();

        if (uri != null) {
            List<String> pathSegments = uri.getPathSegments();

            //check if any of the path segments is a number, that would represent the workflow ID
            for (String path : pathSegments) {
                if (TextUtils.isDigitsOnly(path)) {
                    notificationWorkflowId = path;
                    break;
                }
            }
        }
    }

    private void subscribe() {
        final Observer<Boolean> goToDomainObserver = (this::goToDomain);
        final Observer<Boolean> goToSyncObserver = (this::goToSync);
        final Observer<String[]> saveToPreferenceObserver = (this::saveInPreferences);
        splashViewModel.getObservableGoToDomain().observe(this, goToDomainObserver);
        splashViewModel.getObservableGoToSync().observe(this, goToSyncObserver);
        splashViewModel.getObservableSaveToPreference().observe(this, saveToPreferenceObserver);
    }

    private void goToDomain(Boolean open) {
        startActivity(new Intent(this, DomainActivity.class));
        finish();
    }

    private void goToSync(Boolean open) {
        Intent intent = new Intent(this, SyncActivity.class);

        if (!TextUtils.isEmpty(notificationWorkflowId)) {
            intent.putExtra(NotificationDataKeys.KEY_WORKFLOW_ID, notificationWorkflowId);
        }

        startActivity(intent);
        finishAffinity();
    }

    private void saveInPreferences(String[] content) {
        SharedPreferences.Editor editor = getSharedPreferences("Sessions", Context.MODE_PRIVATE)
                .edit();
        editor.putString(content[0], content[1]).apply();
    }
}
