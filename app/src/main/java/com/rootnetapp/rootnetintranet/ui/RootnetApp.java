package com.rootnetapp.rootnetintranet.ui;

import android.app.Application;

import com.rootnetapp.rootnetintranet.di.AppComponent;
import com.rootnetapp.rootnetintranet.di.AppModule;
import com.rootnetapp.rootnetintranet.di.DaggerAppComponent;


/**
 * Created by Propietario on 09/03/2018.
 */

public class RootnetApp extends Application {

    public static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initDagger();
    }

    private void initDagger() {
        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
