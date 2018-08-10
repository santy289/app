package com.rootnetapp.rootnetintranet.ui.profile;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ProfileRepository {

    private AppDatabase database;

    public ProfileRepository(AppDatabase database) {
        this.database = database;
    }

    public Observable<User> getUser(int id) {
        return Observable.fromCallable(()-> database.userDao().getUserById(id))
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

}
