package com.rootnetapp.rootnetintranet.ui;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.rootnetapp.rootnetintranet.di.AppComponent;
import com.rootnetapp.rootnetintranet.di.AppModule;
import com.rootnetapp.rootnetintranet.di.DaggerAppComponent;

public class RootnetApp extends Application {

    public static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initDagger();
        AndroidThreeTen.init(this);
    }

    private void initDagger() {
        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
