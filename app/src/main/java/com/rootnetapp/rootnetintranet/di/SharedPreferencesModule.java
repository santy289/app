package com.rootnetapp.rootnetintranet.di;

import android.content.Context;
import android.content.SharedPreferences;
import com.rootnetapp.rootnetintranet.data.local.preferences.Preferences;
import com.rootnetapp.rootnetintranet.models.Session;
import com.squareup.moshi.JsonAdapter;
import dagger.Module;
import dagger.Provides;

@Module
public class SharedPreferencesModule {
    @Provides
    Preferences<Session> providePreferencesSession(SharedPreferences sharedPreferences, JsonAdapter<Session> jsonAdapter) {
        return new Preferences<>(sharedPreferences, jsonAdapter);
    }

    @Provides
    SharedPreferences provideSharedPreferences(Context context){
        return context.getSharedPreferences("Sessions", Context.MODE_PRIVATE);
    }

}
