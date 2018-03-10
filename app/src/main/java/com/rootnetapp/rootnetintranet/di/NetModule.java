package com.rootnetapp.rootnetintranet.di;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

@Module
public class NetModule {

    @Provides
    @Singleton
    Retrofit provideRetrofit() {
        OkHttpClient client = RetrofitUrlManager.getInstance().with(new OkHttpClient.Builder())
                .build();
        return new Retrofit.Builder()
                .baseUrl(Utils.URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public ApiInterface apiInterface(Retrofit retrofit){
        return retrofit.create(ApiInterface.class);
    }

}