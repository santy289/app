package com.rootnetapp.rootnetintranet.services.manager;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.rootnetapp.rootnetintranet.ui.RootnetApp;

import javax.inject.Inject;

public class WorkflowManagerService extends IntentService {

    @Inject
    WorkflowManagerServiceRepository repository;
    private String token;

    public WorkflowManagerService() {
        super("ManagerService");
    }

   /* @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        //TODO preferences inyectadas con Dagger
        SharedPreferences prefs = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        token = "Bearer "+ prefs.getString("token","");
        return Service.START_NOT_STICKY;
    }*/

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        //TODO preferences inyectadas con Dagger
        SharedPreferences prefs = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        token = "Bearer "+ prefs.getString("token","");
        /*while(true){
            repository.getPendingWorkflows(token);
            SystemClock.sleep(30000); //1 minutes
        }*/

    }

}