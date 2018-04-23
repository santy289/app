package com.rootnetapp.rootnetintranet.services.manager;

import android.app.IntentService;
import android.app.Service;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.rootnetapp.rootnetintranet.ui.RootnetApp;

import javax.inject.Inject;

public class WorkflowManagerService extends IntentService {

    @Inject
    WorkflowManagerViewModelFactory factory;
    WorkflowManagerViewModel viewModel;

    public WorkflowManagerService(String name) {
        super(name);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        /*viewModel = ViewModelProviders
                .of(this, factory)
                .get(WorkflowManagerViewModelFactory.class);*/
        //todo do viewmodel request
    }
}
