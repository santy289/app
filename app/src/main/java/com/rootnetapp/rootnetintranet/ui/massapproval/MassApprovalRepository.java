package com.rootnetapp.rootnetintranet.ui.massapproval;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.status.StatusRepository;

import io.reactivex.disposables.CompositeDisposable;

public class MassApprovalRepository extends StatusRepository {

    private final CompositeDisposable disposables = new CompositeDisposable();

    private static final String TAG = "MassApprovalRepository";

    protected MassApprovalRepository(ApiInterface service,
                                     AppDatabase database) {
        super(service, database);
    }

    protected void clearDisposables() {
        disposables.clear();
    }
}
