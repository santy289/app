package com.rootnetapp.rootnetintranet.di;

import android.arch.persistence.room.Room;
import android.content.Context;

import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.ui.sync.SyncHelper;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import static com.rootnetapp.rootnetintranet.commons.MyOkHttpClient.getUnsafeOkHttpClient;

@Module
public class NetModule {

    @Provides
    @Singleton
    Retrofit provideRetrofit() {
        /*OkHttpClient client = RetrofitUrlManager.getInstance().with(new OkHttpClient.Builder())
                .build();*/

        OkHttpClient client = getUnsafeOkHttpClient();
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

    @Provides
    @Singleton
    public AppDatabase provideDatabase(Context context){
        return Room.databaseBuilder(context,
                AppDatabase.class, "IntranetDB")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    @Singleton
    public SyncHelper provideSync(ApiInterface apiInterface, AppDatabase database){
        return new SyncHelper(apiInterface, database);
    }

}