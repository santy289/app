package com.rootnetapp.rootnetintranet.ui.sync;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.main.MainActivity;

import javax.inject.Inject;

public class SyncActivity extends AppCompatActivity {

    @Inject
    SyncHelper syncHelper;
    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //TODO preferences inyectadas con Dagger
        SharedPreferences prefs = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("token", "");
        bar = findViewById(R.id.progress_bar);
        bar.setMax(2);
        subscribe();
        syncHelper.clearData(token);
    }

    private void subscribe() {
        final Observer<Boolean> syncObserver = ((Boolean data) -> {
            if (data) {
                Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failure", Toast.LENGTH_LONG).show();
            }
            startActivity(new Intent(this, MainActivity.class));
            finishAffinity();
        });

        final Observer<Integer> progressObserver = ((Integer data) -> {
            if (data != null) {
                bar.setProgress(data);
            }
        });
        syncHelper.getObservableSync().observe(this, syncObserver);
        syncHelper.getObservableProgress().observe(this, progressObserver);
    }

}
