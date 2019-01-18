package com.rootnetapp.rootnetintranet.ui.createworkflow;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.country.CountryDBDao;
import com.rootnetapp.rootnetintranet.data.local.db.profile.forms.FormCreateProfile;
import com.rootnetapp.rootnetintranet.data.local.db.user.UserDao;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDbDao;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.createform.FormFieldsByWorkflowType;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.workflowlist.WorkflowTypeItemMenu;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.createworkflow.CreateRequest;
import com.rootnetapp.rootnetintranet.models.createworkflow.CurrencyFieldData;
import com.rootnetapp.rootnetintranet.models.createworkflow.FilePost;
import com.rootnetapp.rootnetintranet.models.createworkflow.PhoneFieldData;
import com.rootnetapp.rootnetintranet.models.requests.createworkflow.EditRequest;
import com.rootnetapp.rootnetintranet.models.responses.country.CountriesResponse;
import com.rootnetapp.rootnetintranet.models.responses.createworkflow.CreateWorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.createworkflow.FileUploadResponse;
import com.rootnetapp.rootnetintranet.models.responses.downloadfile.DownloadFileResponse;
import com.rootnetapp.rootnetintranet.models.responses.products.ProductsResponse;
import com.rootnetapp.rootnetintranet.models.responses.project.ProjectResponse;
import com.rootnetapp.rootnetintranet.models.responses.role.RoleResponse;
import com.rootnetapp.rootnetintranet.models.responses.services.ServicesResponse;
import com.rootnetapp.rootnetintranet.models.responses.user.ProfileResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListsResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypeResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.WorkflowTypesResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowuser.WorkflowUserResponse;

import java.util.List;

import androidx.lifecycle.LiveData;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CreateWorkflowRepository {

    private final ApiInterface service;
    private final AppDatabase database;
    private final WorkflowTypeDbDao workflowTypeDbDao;
    private final UserDao profileDao;
    private final CountryDBDao countryDBDao;

    private final LiveData<List<WorkflowTypeItemMenu>> workflowTypeMenuItems;

    public CreateWorkflowRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
        this.database = database;
        this.workflowTypeDbDao = this.database.workflowTypeDbDao();
        this.profileDao = this.database.userDao();
        this.countryDBDao = this.database.countryDBDao();
        this.workflowTypeMenuItems = workflowTypeDbDao.getObservableTypesForMenu();
    }

    public LiveData<List<WorkflowTypeItemMenu>> getWorkflowTypeMenuItems() {
        return workflowTypeMenuItems;
    }

    public List<WorkflowTypeItemMenu> getWorklowTypeNames() {
        return workflowTypeDbDao.getListOfWorkflowNames();
    }

    public WorkflowTypeDb getWorklowType(int workflowTypeId) {
        return workflowTypeDbDao.getWorkflowTypeBy(workflowTypeId);
    }

    public List<FormCreateProfile> getProfiles() {
        return profileDao.getAllProfiles();
    }

    public List<FormFieldsByWorkflowType> getFiedsByWorkflowType(int byId) {
        return workflowTypeDbDao.getFields(byId);
    }

    protected Observable<WorkflowResponse> getWorkflow(String auth, int workflowId) {
        return service.getWorkflow(auth, workflowId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    protected Observable<WorkflowTypeResponse> getWorkflowType(String auth, int typeId) {
        return service.getWorkflowType(auth, typeId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<WorkflowTypesResponse> getWorkflowTypes(String auth) {
        return service.getWorkflowTypes(auth).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<PhoneFieldData>> getCountryCodes() {
        return countryDBDao.getAllCountriesCodes().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<CurrencyFieldData>> getCurrencyCodes() {
        return countryDBDao.getAllCurrencyCodes().subscribeOn(Schedulers.newThread())
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

    public Observable<RoleResponse> getRoles(String auth) {
        return service.getRoles(auth).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ProjectResponse> getProjects(String auth) {
        return service.getProjects(auth).subscribeOn(Schedulers.newThread())
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

    public Observable<CreateWorkflowResponse> createWorkflow(String token, CreateRequest body) {
        return service.createWorkflow(token, body).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CreateWorkflowResponse> editWorkflow(String token, EditRequest request) {
        return service.editWorkflow(token, request.getWorkflowId(), request).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<FileUploadResponse> uploadFile(String token, FilePost body) {
        return service.uploadFile(token, body).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<CreateWorkflowResponse> createWorkflow(String token, String body) {
        return service.createWorkflow(token, body).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    protected Observable<DownloadFileResponse> downloadFile(String auth, String entity, int fileId) {
        return service.downloadFile(auth, entity, fileId).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    protected Observable<ProfileResponse> getProfiles(String auth, boolean enabled) {
        return service.getProfiles(auth, enabled).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}