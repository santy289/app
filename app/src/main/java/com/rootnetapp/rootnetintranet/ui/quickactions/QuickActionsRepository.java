package com.rootnetapp.rootnetintranet.ui.quickactions;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

public class QuickActionsRepository {

    private AppDatabase database;
    private ApiInterface apiInterface;

    public QuickActionsRepository(AppDatabase database, ApiInterface apiInterface) {
        this.database = database;
        this.apiInterface = apiInterface;
    }
}
