package com.rootnetapp.rootnetintranet.ui.quickactions.changestatus;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

public class ChangeStatusRepository {

    private AppDatabase database;
    private ApiInterface apiInterface;

    public ChangeStatusRepository(AppDatabase database, ApiInterface apiInterface) {
        this.database = database;
        this.apiInterface = apiInterface;
    }
}
