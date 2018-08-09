package com.rootnetapp.rootnetintranet.ui.splash;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.sync.SyncActivity;
import com.rootnetapp.rootnetintranet.ui.domain.DomainActivity;

import javax.inject.Inject;

public class SplashActivity extends AppCompatActivity {

    @Inject
    SplashViewModelFactory splashViewModelFactory;
    SplashViewModel splashViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        splashViewModel = ViewModelProviders
                .of(this, splashViewModelFactory)
                .get(SplashViewModel.class);

        subscribe();
        SharedPreferences sharedPreferences = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        splashViewModel.initSplashViewModel(sharedPreferences);
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
        startActivity(new Intent(SplashActivity.this, DomainActivity.class));
        finish();
    }

    private void goToSync(Boolean open) {
        startActivity(new Intent(SplashActivity.this, SyncActivity.class));
        finishAffinity();
    }

    private void saveInPreferences(String[] content) {
        SharedPreferences.Editor editor = getSharedPreferences("Sessions", Context.MODE_PRIVATE).edit();
        editor.putString(content[0], content[1]).apply();
    }
}
