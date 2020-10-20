package com.rootnetapp.rootnetintranet.ui.workflowlist.repo;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.profile.forms.FormCreateProfile;
import com.rootnetapp.rootnetintranet.data.local.db.user.UserDao;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDbDao;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDbDao;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.createform.FormFieldsByWorkflowType;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.workflowlist.WorkflowTypeItemMenu;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.activation.WorkflowActivationResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflowdetail.DeleteWorkflowResponse;
import com.rootnetapp.rootnetintranet.models.responses.workflows.PostDeleteWorkflows;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponseDb;
import com.rootnetapp.rootnetintranet.models.responses.workflowtypes.ListsResponse;

import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.rootnetapp.rootnetintranet.ui.workflowlist.repo.WorkflowListBoundaryCallback.NO_WORKFLOW_TYPE;

public class WorkflowRepository implements IncomingWorkflowsCallback {

    public static int ENDPOINT_PAGE_SIZE = 30;
    private static int LIST_PAGE_SIZE = 30;
    public final static String WORKFLOWID = "workflowdb.id";
    public final static String WORKFLOW_CREATED = "workflowdb.created_at";
    public final static String WORKFLOW_UPDATED = "workflowdb.updated_at";

    private int currentPage = 1;
    private int lastPage = 1;
    private ApiInterface service;
    private WorkflowDbDao workflowDbDao;
    private WorkflowTypeDbDao workflowTypeDbDao;
    private LiveData<PagedList<WorkflowListItem>> allWorkflows;
    private MutableLiveData<Boolean> handleRepoError;
    private MutableLiveData<Boolean> handleRepoSuccess;
    private MutableLiveData<Boolean> handleRepoSuccessNoFilters;
    private MutableLiveData<Boolean> handleRestSuccessWithNoApplyFilter;
    private MutableLiveData<Boolean> handleDeleteWorkflows;
    private MutableLiveData<Boolean> handleGetAllWorkflows;
    public MutableLiveData<Boolean> showLoadMore;
    private WorkflowListBoundaryCallback callback;
    private PagedList.Config pagedListConfig;
    private UserDao profileDao;

    private final static String TAG = "WorkflowRepository";
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final String baseWorkflowListQuery;

    public WorkflowRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
        workflowDbDao = database.workflowDbDao();
        workflowTypeDbDao = database.workflowTypeDbDao();
        this.profileDao = database.userDao();
        pagedListConfig = (new PagedList.Config.Builder())
                .setEnablePlaceholders(false)
                .setPageSize(LIST_PAGE_SIZE)
                .build();
        showLoadMore = new MutableLiveData<>();
        baseWorkflowListQuery = "SELECT workflowdb.id AS workflowId, workflowtypedb.id AS workflowTypeId, workflowdb.remaining_time AS remainingTime, " +
                "workflowtypedb.name AS workflowTypeName, workflowdb.title, workflowdb.workflow_type_key, " +
                "workflowdb.full_name, workflowdb.current_status_name, workflowdb.created_at, workflowdb.updated_at, " +
                "workflowdb.start, workflowdb.status, workflowdb.current_status, workflowdb.`end` " +
                "FROM workflowdb LEFT JOIN workflowtypedb " +
                "ON workflowdb.workflow_type_id = workflowtypedb.id ";
    }

    @Override
    public void handleResponse(List<WorkflowDb> workflowsResponse, int lastPage) {
        this.lastPage = lastPage;
        insertWorkflows(workflowsResponse);
    }

    public Observable<ListsResponse> getList(String auth, int id) {
        return service.getListItems(auth, id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void showLoadingMore(boolean show) {
        showLoadMore.setValue(show);
    }

    public List<WorkflowTypeItemMenu> getWorkflowTypesForMenu() {
        return workflowTypeDbDao.getTypesForMenu();
    }

    public LiveData<PagedList<WorkflowListItem>> getAllWorkflows() {
        return allWorkflows;
    }

    /**
     * It prepare a DataSource to some specific local database query. Add a BoundaryCallback to a
     * new instance of a PagedList LiveData.
     *
     * @param token
     */
    public void setWorkflowList(String token) {
        DataSource.Factory<Integer, WorkflowListItem> factory = workflowDbDao.getWorkflows();

        callback = new WorkflowListBoundaryCallback(
                service,
                token,
                currentPage,
                this,
                ""
        );

        allWorkflows = new LivePagedListBuilder<>(factory, pagedListConfig)
                .setBoundaryCallback(callback)
                .build();
    }

    public List<FormFieldsByWorkflowType> getFiedsByWorkflowType(int byId) {
        return workflowTypeDbDao.getFields(byId);
    }

    /**
     * Method uses a raw query for querying workflows by status or by a search term on the title.
     *
     * @param status
     * @param token
     * @param id
     * @param searchText
     */
    public void rawQueryWorkflowListByFilters(boolean status, String token, String id,
                                              String searchText) {
        Object[] objects;
        String queryString;
        if (TextUtils.isEmpty(searchText)) {
            queryString = baseWorkflowListQuery +
                    "WHERE workflowdb.status = ? " +
                    "ORDER BY workflowdb.created_at DESC";
            objects = new Object[]{status};
        } else {
            queryString = baseWorkflowListQuery +
                    "WHERE workflowdb.status = ? " +
                    "AND (workflowdb.title LIKE '%' || ? || '%' OR WorkflowTypeDb.name LIKE '%' || ? || '%' OR workflowdb.description LIKE '%' || ? || '%' OR workflowdb.workflow_type_key LIKE '%' || ? || '%' OR workflowdb.full_name LIKE '%' || ? || '%') " +
                    "ORDER BY workflowdb.created_at DESC";
            objects = new Object[]{status, searchText, searchText, searchText, searchText,
                                   searchText};
        }
        startRawQuery(queryString, token, objects, id, searchText);
    }

    /**
     * Method uses a raw query for querying workflows by original workflow type id, by status or by
     * a search term on the title.
     *
     * @param status
     * @param originalTypeId
     * @param token
     * @param id
     * @param searchText
     */
    public void rawQueryWorkflowListByFilters(boolean status, int originalTypeId, String token,
                                              String id, String searchText) {
        Object[] objects;
        String queryString;
        if (TextUtils.isEmpty(searchText)) {
            queryString = baseWorkflowListQuery +
                    "WHERE (workflowtypedb.original_id = ? OR workflowtypedb.id = ?)" +
                    "AND workflowdb.status = ? " +
                    "ORDER BY workflowdb.created_at DESC";
            objects = new Object[]{originalTypeId, originalTypeId, status};
        } else {
            queryString = baseWorkflowListQuery +
                    "WHERE (workflowtypedb.original_id = ? OR workflowtypedb.id = ?)" +
                    "AND workflowdb.status = ? " +
                    "AND (workflowdb.title LIKE '%' || ? || '%' OR WorkflowTypeDb.name LIKE '%' || ? || '%' OR workflowdb.description LIKE '%' || ? || '%' OR workflowdb.workflow_type_key LIKE '%' || ? || '%' OR workflowdb.full_name LIKE '%' || ? || '%') " +
                    "ORDER BY workflowdb.created_at DESC";
            objects = new Object[]{originalTypeId, originalTypeId, status, searchText, searchText,
                                   searchText, searchText, searchText};
        }
        // TODO pass the workflowTypeId as well we need it later. and modify the other functions too.
        startRawQuery(queryString, token, objects, id, originalTypeId, searchText);
    }

    /**
     * Method uses a raw query for querying workflows by original workflow type id, by status or by
     * a search term on the title. And by specifying a column as descending or ascending orders.
     *
     * @param status
     * @param originalTypeId
     * @param column
     * @param isDescending
     * @param token
     * @param id
     * @param searchText
     */
    public void rawQueryWorkflowListByFilters(boolean status, int originalTypeId, String column,
                                              boolean isDescending, String token, String id,
                                              String searchText) {
        String queryString;
        Object[] objects;

        if (TextUtils.isEmpty(searchText)) {
            queryString = baseWorkflowListQuery +
                    "WHERE (workflowtypedb.original_id = ? OR workflowtypedb.id = ?)" +
                    "AND workflowdb.status = ? ";
            objects = new Object[]{originalTypeId, originalTypeId, status};
        } else {
            queryString = baseWorkflowListQuery +
                    "WHERE (workflowtypedb.original_id = ? OR workflowtypedb.id = ?)" +
                    "AND workflowdb.status = ? " +
                    "AND (workflowdb.title LIKE '%' || ? || '%' OR WorkflowTypeDb.name LIKE '%' || ? || '%' OR workflowdb.description LIKE '%' || ? || '%' OR workflowdb.workflow_type_key LIKE '%' || ? || '%' OR workflowdb.full_name LIKE '%' || ? || '%')";
            objects = new Object[]{originalTypeId, originalTypeId, status, searchText, searchText,
                                   searchText, searchText, searchText};
        }
        if (isDescending) {
            queryString += "ORDER BY " + column + " DESC";
        } else {
            queryString += "ORDER BY " + column + " ASC";
        }

        startRawQuery(queryString, token, objects, id, searchText);
    }

    /**
     * Method uses a raw query for querying workflows by status or by a search term on the title.
     * And by specifying a column as descending or ascending orders.
     *
     * @param status
     * @param column
     * @param isDescending
     * @param token
     * @param id
     * @param searchText
     */
    public void rawQueryWorkflowListByFilters(boolean status, String column, boolean isDescending,
                                              String token, String id, String searchText) {
        String queryString;
        Object[] objects;

        if (TextUtils.isEmpty(searchText)) {
            queryString = baseWorkflowListQuery +
                    "WHERE workflowdb.status = ? ";
            objects = new Object[]{status};
        } else {
            queryString = baseWorkflowListQuery +
                    "WHERE workflowdb.status = ? " +
                    "AND (workflowdb.title LIKE '%' || ? || '%' OR WorkflowTypeDb.name LIKE '%' || ? || '%' OR workflowdb.description LIKE '%' || ? || '%' OR workflowdb.workflow_type_key LIKE '%' || ? || '%' OR workflowdb.full_name LIKE '%' || ? || '%')";
            objects = new Object[]{status, searchText, searchText, searchText, searchText,
                                   searchText};
        }
        if (isDescending) {
            queryString += "ORDER BY " + column + " DESC ";
        } else {
            queryString += "ORDER BY " + column + " ASC ";
        }
        startRawQuery(queryString, token, objects, id, searchText);
    }

    private void startRawQuery(String queryString, String token, Object[] objects, String id,
                               String searchText) {
        SimpleSQLiteQuery sqlQuery = new SimpleSQLiteQuery(queryString, objects);
        getWorkflowsByFilters(token, sqlQuery, id, NO_WORKFLOW_TYPE, searchText);
    }

    private void startRawQuery(String queryString, String token, Object[] objects, String id,
                               int workflowTypeId, String searchText) {
        SimpleSQLiteQuery sqlQuery = new SimpleSQLiteQuery(queryString, objects);
        getWorkflowsByFilters(token, sqlQuery, id, workflowTypeId, searchText);
    }

    /**
     * Method generates a DataSource factory for a ListAdapter. Also it verifies that a boundary
     * callback is not already initiated with some background thread work, if we have something
     * already running on the boundary callback this method will dispose any background work. It
     * will also set a Boundary Callback for the appropriate scenario. This could be a workflow list
     * request without filters or with some kind of filters, such as original workflow  type
     *
     * @param token
     * @param query
     * @param id
     * @param workflowTypeId
     */
    private void getWorkflowsByFilters(String token, SupportSQLiteQuery query, String id,
                                       int workflowTypeId, String searchText) {
        DataSource.Factory<Integer, WorkflowListItem> factory = workflowDbDao
                .getWorkflowsWithFilter(query);

        if (callback != null) {
            callback.clearDisposables();
        }

        if (workflowTypeId != NO_WORKFLOW_TYPE) {
            callback = new WorkflowListBoundaryCallback(
                    service,
                    token,
                    currentPage,
                    this,
                    "",
                    workflowTypeId,
                    searchText
            );

            allWorkflows = new LivePagedListBuilder<>(factory, pagedListConfig)
                    .setBoundaryCallback(callback)
                    .build();
            return;
        }

        // TODO goes here
        if (TextUtils.isEmpty(id)) {
            callback = new WorkflowListBoundaryCallback(
                    service,
                    token,
                    currentPage,
                    this,
                    TextUtils.isEmpty(searchText) ? "" : searchText
            );

            allWorkflows = new LivePagedListBuilder<>(factory, pagedListConfig)
                    .setBoundaryCallback(callback)
                    .build();
            return;
        }

        callback = new WorkflowListBoundaryCallback(
                service,
                token,
                currentPage,
                this,
                id,
                NO_WORKFLOW_TYPE,
                searchText
        );

        allWorkflows = new LivePagedListBuilder<>(factory, pagedListConfig)
                .setBoundaryCallback(callback)
                .build();
    }

    public void clearDisposables() {
        disposables.clear();
        callback.clearDisposables();
    }

    /**
     * Inserts workflows without deleting any workflows previously created.
     *
     * @param worflows
     */
    public void insertWorkflows(List<WorkflowDb> worflows) {
        Disposable disposable = Observable.fromCallable(() -> {
            workflowDbDao.insertWorkflows(worflows);
            callback.updateIsLoading(false);
            return true;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    showLoadingMore(false);
                    if (currentPage < lastPage) {
                        currentPage = currentPage + 1;
                    }
                    callback.updateCurrentPage(currentPage);
                }, throwable -> {
                    callback.updateIsLoading(false);
                    showLoadingMore(false);
                    Log.d(TAG, "failure: Can't save to DB: " + throwable.getMessage());
                });
        disposables.add(disposable);
    }

    public void insertWorkflow(WorkflowDb workflow) {
        Disposable disposable = Completable.fromCallable(() -> {
            workflowDbDao.insertWorkflow(workflow);
            return true;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        disposables.add(disposable);
    }

    private void workflowDbSuccessFilter(WorkflowResponseDb workflowsResponse) {
        Disposable disposable = Observable.fromCallable(() -> {
            workflowDbDao.deleteAllWorkflows();
            workflowDbDao.insertWorkflows(workflowsResponse.getList());
            return true;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    Log.d(TAG, "workflowDbSuccessFilter: ");
                    handleRepoSuccessNoFilters.postValue(true);
                }, throwable -> {
                    Log.d(TAG, "workflowDbSuccessFilter: error " + throwable.getMessage());
                    handleRepoError.postValue(true);
                });

        disposables.add(disposable);
    }

    public void getWorkflowsByBaseFilters(String token, Map<String, Object> options) {
        Disposable disposable = service
                .getWorkflowsByBaseFilters(
                        token,
                        100,
                        1,
                        false,
                        options)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::workflowDbSuccessFilter, throwable -> {
                    Log.d(TAG, "getAllWorkflows: error: " + throwable.getMessage());
                    handleRepoError.postValue(true);
                });
        disposables.add(disposable);
    }

    public List<FormCreateProfile> getProfiles() {
        return profileDao.getAllProfiles();
    }

    public Observable<WorkflowActivationResponse> postOpenCloseActivation(String token,
                                                                          List<Integer> workflowIds,
                                                                          boolean open) {
        return service.postWorkflowActivationOpenClose(token, workflowIds, open)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<WorkflowActivationResponse> postEnableDisableActivation(String token,
                                                                          List<Integer> workflowIds,
                                                                          boolean enable) {
        return service.postWorkflowActivationEnableDisable(token, workflowIds, enable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<DeleteWorkflowResponse> postDeleteWorkflows(String token,
                                                                  int workflowId,
                                                                  PostDeleteWorkflows postDeleteWorkflows){
        return service.deleteWorkflows(token, workflowId, postDeleteWorkflows)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ListsResponse> getCategoryList(String auth, int id) {
        return service.getListItems(auth, id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void deleteWorkflowsLocal(List<WorkflowDb> workflowDbs) {
        Disposable disposable = Observable.fromCallable(() -> {
            workflowDbDao.deleteWorkflows(workflowDbs);
            return true;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    Log.d(TAG, "deleteWorkflowsLocal: ");
                    // No apply filters
                    handleDeleteWorkflows.postValue(true);
                }, throwable -> {
                    Log.d(TAG, "deleteWorkflowsLocal: error " + throwable.getMessage());
                    handleRepoError.postValue(true);
                });
        disposables.add(disposable);
    }

    public void deleteWorkflowsLocalByIds(List<Integer> workflowIds) {
        Disposable disposable = Observable.fromCallable(() -> {
            workflowDbDao.deleteWorkflowsByIds(workflowIds);
            return true;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    Log.d(TAG, "deleteWorkflowsLocalByIds: ");
                    // No apply filters
                    handleDeleteWorkflows.postValue(true);
                }, throwable -> {
                    Log.d(TAG, "deleteWorkflowsLocalByIds: error " + throwable.getMessage());
                    handleRepoError.postValue(true);
                });
        disposables.add(disposable);
    }

    public LiveData<Boolean> getObservableHandleRepoError() {
        if (handleRepoError == null) {
            handleRepoError = new MutableLiveData<>();
        }
        return handleRepoError;
    }

    public LiveData<Boolean> getObservableHandleRepoSuccess() {
        if (handleRepoSuccess == null) {
            handleRepoSuccess = new MutableLiveData<>();
        }
        return handleRepoSuccess;
    }

    public LiveData<Boolean> getObservableHandleRepoSuccessNoFilter() {
        if (handleRepoSuccessNoFilters == null) {
            handleRepoSuccessNoFilters = new MutableLiveData<>();
        }
        return handleRepoSuccessNoFilters;
    }

    public LiveData<Boolean> getObservableHandleRestSuccessWithNoApplyFilter() {
        if (handleRestSuccessWithNoApplyFilter == null) {
            handleRestSuccessWithNoApplyFilter = new MutableLiveData<>();
        }
        return handleRestSuccessWithNoApplyFilter;
    }

    public LiveData<Boolean> getObservableHandleDeleteWorkflows() {
        if (handleDeleteWorkflows == null) {
            handleDeleteWorkflows = new MutableLiveData<>();
        }
        return handleDeleteWorkflows;
    }

    public LiveData<Boolean> getObservableHandleGetAllWorkflows() {
        if (handleGetAllWorkflows == null) {
            handleGetAllWorkflows = new MutableLiveData<>();
        }
        return handleGetAllWorkflows;
    }
}