package com.rootnetapp.rootnetintranet.ui.createworkflow;

import android.arch.lifecycle.LiveData;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.profile.ProfileDao;
import com.rootnetapp.rootnetintranet.data.local.db.profile.forms.FormCreateProfile;
import com.rootnetapp.rootnetintranet.data.local.db.user.UserDao;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDbDao;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.workflowlist.WorkflowTypeItemMenu;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.country.CountriesResponse;
import com.rootnetapp.rootnetintranet.models.responses.createworkflow.CreateWorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.products.ProductsResponse;
import com.rootnetapp.rootnetintranet.models.responses.services.ServicesResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListsResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypesResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowuser.WorkflowUserResponse;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateWorkflowRepository {

    private ApiInterface service;
    private AppDatabase database;
    private WorkflowTypeDbDao workflowTypeDbDao;
    private UserDao profileDao;

    private LiveData<List<WorkflowTypeItemMenu>> workflowTypeMenuItems;

    public CreateWorkflowRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
        this.database = database;
        workflowTypeDbDao = this.database.workflowTypeDbDao();
        profileDao = this.database.userDao();
        this.workflowTypeMenuItems = workflowTypeDbDao.getObservableTypesForMenu();

    }

    public LiveData<List<WorkflowTypeItemMenu>> getWorkflowTypeMenuItems() {
        return workflowTypeMenuItems;
    }

    public List<WorkflowTypeItemMenu> getWorklowTypeNames() {
        return workflowTypeDbDao.getListOfWorkflowNames();
    }

    public List<FormCreateProfile> getProfiles() {
        return profileDao.getAllProfiles();
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