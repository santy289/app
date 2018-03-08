package com.rootnetapp.rootnetintranet.data.local.preferences;

import android.content.SharedPreferences;

import com.squareup.moshi.JsonAdapter;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

public class Preferences<T> implements PreferencesHelper<T> {
    SharedPreferences sharedPreferences;
    JsonAdapter<T> jsonAdapter;

    @Inject public Preferences(SharedPreferences sharedPreferences, JsonAdapter<T> jsonAdapter) {
        this.sharedPreferences = sharedPreferences;
        this.jsonAdapter = jsonAdapter;
    }


    @Override
    public Observable<T> save(String key, T value) {
        return Observable.create(subscriber -> {
            sharedPreferences.edit().putString(key, jsonAdapter.toJson(value)).apply();
            subscriber.onNext(value);
            subscriber.onComplete();
        });
    }

    @Override
    public Observable<T> get(String key, Class<T> generic) {
        return Observable.create((ObservableEmitter<T> subscriber) -> {
            String json = sharedPreferences.getString(key, "");
            if (json.isEmpty()) {
                T myClass = generic.newInstance();
                subscriber.onNext(myClass);
            } else {
                T myClass = jsonAdapter.fromJson(json);
                subscriber.onNext(myClass);
                subscriber.onComplete();
            }
        });
    }

    @Override
    public Observable<Boolean> clear() {
        return Observable.create(subscriber -> {
            sharedPreferences.edit().clear().apply();
            subscriber.onNext(true);
            subscriber.onComplete();
        });
    }
}
