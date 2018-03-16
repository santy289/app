package com.rootnetapp.rootnetintranet.ui;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.SyncHelper;
import com.rootnetapp.rootnetintranet.ui.main.MainActivity;

import javax.inject.Inject;

public class SyncActivity extends AppCompatActivity {

    @Inject
    SyncHelper syncHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //TODO preferences inyectadas con Dagger
        SharedPreferences prefs = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer "+ prefs.getString("token","");
        subscribe();
        syncHelper.clearData(token);
    }

    private void subscribe() {
        final Observer<Boolean> syncObserver = ((Boolean data) -> {
            if(data){
                Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Failure", Toast.LENGTH_LONG).show();
            }
            startActivity(new Intent(this, MainActivity.class));
            finishAffinity();
        });
        syncHelper.getObservableSync().observe(this, syncObserver);
    }

}
