package com.rootnetapp.rootnetintranet.ui.workflowlist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.SharedPreferences;
import android.util.Log;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowsResponse;
import com.rootnetapp.rootnetintranet.ui.domain.Sort;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class WorkflowViewModel extends ViewModel {
    private MutableLiveData<List<Workflow>> mUserLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private MutableLiveData<Boolean> showLoading;
    private WorkflowRepository workflowRepository;
    private LiveData<List<Workflow>> liveWorkflows, liveUnordered;
    private MutableLiveData<List<Workflow>> updateWithSortedList;

    private List<Workflow> workflows, unordered;
    private String token;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private static final String TAG = "WorkflowViewModel";
    public WorkflowViewModel(WorkflowRepository workflowRepository) {
        this.workflowRepository = workflowRepository;
        liveWorkflows = this.workflowRepository.getAllWorkflows();
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        workflowRepository.clearDisposables();
    }

    protected LiveData<List<Workflow>> getAllWorkflows() {
        return liveWorkflows;
    }

    protected void insert(Workflow workflow) {
        workflowRepository.insertWorkflow(workflow);
    }

    protected void initWorkflowList(SharedPreferences sharedPreferences) {
        token = "Bearer "+ sharedPreferences.getString("token","");
        getWorkflows(token);
    }

    protected void getWorkflows(String auth) {
        this.token = auth;
        try {
            if (Utils.isConnected()) {
                getWorkflowsFromService(this.token, 0);
            }
        } catch (InterruptedException | IOException e) {
            Log.d(TAG, "getWorkflows: Problems updating workflows - " + e.getMessage());
        }
    }

    private void getWorkflowsFromService(String auth, int page) {
        Disposable disposable = workflowRepository
                .getWorkflowsFromService(auth, page)
                .subscribe(
                        this::onServiceSuccess,
                        throwable -> Log.d(TAG, "getWorkflowsFromService: Cant get workflows from network - " + throwable.getMessage())
                );
        disposables.add(disposable);
    }

    private void onServiceSuccess(WorkflowsResponse workflowsResponse) {
        workflows = new ArrayList<>();
        workflows.addAll(workflowsResponse.getList());
        if (!workflowsResponse.getPager().isIsLastPage()) {
            // calling multiple times until we get to the last page.
            getWorkflowsFromService(token, workflowsResponse.getPager().getNextPage());
        }
        else {
            // Update database with new workflows from network.
            Disposable disposable = workflowRepository
                    .setWorkflowsLocalUpdate(workflows)
                    .subscribe(
                            this::onWorkflowSuccessUpdate,
                            throwable -> {
                                Log.d(TAG, "onServiceSuccess: problem saving to db - " + throwable.getMessage());
                                mErrorLiveData.setValue(R.string.failure_connect);
                            }
                    );
            disposables.add(disposable);
        }
    }

    private void onWorkflowSuccessUpdate(List<Workflow> workflowList) {
        Log.d(TAG, "onWorkflowSuccessUpdate: local database workflows updated.");
    }

    public void applyFilters(Sort sorting) {
//        List<Workflow> workflows = mUserLiveData.getValue();
        List<Workflow> workflows = liveWorkflows.getValue();
        if (workflows == null) {
            return;
        }

        switch (sorting.getSortingType()) {
            case NONE: {
                workflows = new ArrayList<>(unordered); //TODO check if it works passing it on the constructor
//                workflows.addAll(unordered);
                break;
            }
            case BYNUMBER: {
                Collections.sort(workflows, (s1, s2) -> {
                    if (sorting.getNumberSortOrder().equals(Sort.sortOrder.ASC)) {
                        /*For ascending order*/
                        return s1.getId() - s2.getId();
                    } else {
                        /*For descending order*/
                        return s2.getId() - s1.getId();
                    }
                });
                break;
            }
            case BYCREATE: {
                Collections.sort(workflows, (s1, s2) -> {
                    String str1 = s1.getStart().split("T")[0];
                    String str2 = s2.getStart().split("T")[0];
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    try {
                        Date date1 = format.parse(str1);
                        Date date2 = format.parse(str2);
                        if (sorting.getCreatedSortOrder().equals(Sort.sortOrder.ASC)) {
                            return date1.compareTo(date2);
                        } else {
                            return date2.compareTo(date1);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 1;
                    }
                });
                break;
            }
            case BYUPDATE: {
                //todo falta este dato del servicio.
                break;
            }
        }

//        mUserLiveData.setValue(workflows);
        updateWithSortedList.setValue(workflows);
    }

    protected LiveData<List<Workflow>> getObservableWorkflows() {
        if (mUserLiveData == null) {
            mUserLiveData = new MutableLiveData<>();
        }
        return mUserLiveData;
    }

    protected LiveData<Integer> getObservableError() {
        if (mErrorLiveData == null) {
            mErrorLiveData = new MutableLiveData<>();
        }
        return mErrorLiveData;
    }

    protected LiveData<Boolean> getObservableShowLoading() {
        if (showLoading == null) {
            showLoading = new MutableLiveData<>();
        }
        return showLoading;
    }

    protected LiveData<List<Workflow>> getObservableUpdateWithSortedList() {
        if (updateWithSortedList == null) {
            updateWithSortedList = new MutableLiveData<>();
        }
        return updateWithSortedList;
    }
}
