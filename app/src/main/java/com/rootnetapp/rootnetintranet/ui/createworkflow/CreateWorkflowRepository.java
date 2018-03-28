package com.rootnetapp.rootnetintranet.ui.createworkflow;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.country.CountriesResponse;
import com.rootnetapp.rootnetintranet.models.responses.createworkflow.CreateWorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.products.ProductsResponse;
import com.rootnetapp.rootnetintranet.models.responses.services.ServicesResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListsResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypesResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowuser.WorkflowUserResponse;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 22/03/18.
 */

public class CreateWorkflowRepository {

    ApiInterface service;
    AppDatabase database;

    public CreateWorkflowRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
        this.database = database;
    }

    public Observable<WorkflowTypesResponse> getWorkflowTypes(String auth) {
        return service.getWorkflowTypes(auth).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ListsResponse> getList(String auth, int id) {
        return service.getListItems(auth, id).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ProductsResponse> getProducts(String auth) {
        return service.getProducts(auth).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ServicesResponse> getServices(String auth) {
        return service.getServices(auth).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<WorkflowUserResponse> getUsers(String auth) {
        return service.getWorkflowUsers(auth).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CountriesResponse> getCountries(String auth) {
        return service.getCountries(auth).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CreateWorkflowResponse> createWorkflow(String auth, int workflowTypeId,
                                                             String title, String workflowMetas,
                                                             String start, String description) {
        return service.createWorkflow(auth, workflowTypeId, title, workflowMetas, start ,description).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

}