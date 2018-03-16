package com.rootnetapp.rootnetintranet.ui.editprofile;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.edituser.EditUserResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Propietario on 15/03/2018.
 */

public class EditProfileRepository {

    private AppDatabase database;
    private ApiInterface services;

    public EditProfileRepository(AppDatabase database, ApiInterface services) {
        this.database = database;
        this.services = services;
    }

    public Observable<EditUserResponse> editUserService(String token, int id, String fullName, String email, String phoneNumber) {
        return services.editUser(token, id, fullName, email, phoneNumber).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Boolean> editUserLocal(User user) {
        return Observable.fromCallable(()->{
            List<User> list = new ArrayList<>();
            list.add(user);
            database.userDao().insertAll(list);
            return true;
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<User> getUser(int id) {
        return Observable.fromCallable(()-> database.userDao().getUserById(id))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}