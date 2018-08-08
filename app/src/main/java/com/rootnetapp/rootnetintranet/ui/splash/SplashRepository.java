package com.rootnetapp.rootnetintranet.ui.splash;

import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

public class SplashRepository {

    ApiInterface services;

    public SplashRepository(ApiInterface apiInterface) {
        this.services = apiInterface;
    }
}
