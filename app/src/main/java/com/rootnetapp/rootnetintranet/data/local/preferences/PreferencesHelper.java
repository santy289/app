package com.rootnetapp.rootnetintranet.data.local.preferences;

import io.reactivex.Observable;

public interface PreferencesHelper<T> {
    Observable<T> save(String key, T value);
    Observable<T> get(String key, Class<T> generic);
    Observable<Boolean> clear();
}
