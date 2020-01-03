package com.rootnetapp.rootnetintranet.ui.massapproval;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.status.StatusRepository;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

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

    public Observable<WorkflowResponse> postMassApproval(String token,
                                                         Map<String, Object> body) {
        return service.postMassApproval(token, body).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
