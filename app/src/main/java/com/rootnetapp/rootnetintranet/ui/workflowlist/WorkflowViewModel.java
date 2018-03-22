package com.rootnetapp.rootnetintranet.ui.workflowlist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.models.responses.workflows.WorkflowResponse;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by root on 19/03/18.
 */

public class WorkflowViewModel extends ViewModel {

    private MutableLiveData<List<Workflow>> mUserLiveData;
    private MutableLiveData<Integer> mErrorLiveData;
    private WorkflowRepository workflowRepository;
    private List<Workflow> workflows, unordered;
    private String auth;
    //todo REMOVE, solo testing
    private String auth2 = "Bearer "+Utils.testToken;
    public WorkflowViewModel(WorkflowRepository workflowRepository) {
        this.workflowRepository = workflowRepository;
    }

    protected void getWorkflows(String auth) {
        this.auth = auth;
        try {
            if (Utils.isConnected()) {
                getWorkflowsFromService(auth2, 0);
            } else {
                getWorkflowsFromLocal(null);
            }
        } catch (InterruptedException | IOException e) {
            getWorkflowsFromLocal(null);
            //e.printStackTrace();
        }
    }

    private void getWorkflowsFromService(String auth, int page) {
        workflowRepository.getWorkflowsFromService(auth, page).subscribe(this::onServiceSuccess,
                this::getWorkflowsFromLocal);
    }

    private void getWorkflowsFromLocal(Throwable throwable) {
        workflowRepository.getWorkflowsFromInternal().subscribe(this::onWorkflowSuccess,
                this::onWorkflowFailure);
    }

    private void onServiceSuccess(WorkflowResponse workflowResponse) {
        workflows.addAll(workflowResponse.getList());
        if (!workflowResponse.getPager().isIsLastPage()) {
            //todo CAMBIAR AUTH
            getWorkflowsFromService(auth2, workflowResponse.getPager().getNextPage());
        } else {
            workflowRepository.setWorkflowsOnInternal(workflows).subscribe(this::onWorkflowSuccess,
                    this::onWorkflowFailure);
        }
    }

    private void onWorkflowSuccess(List<Workflow> workflowList) {
        unordered = new ArrayList<>();
        unordered.addAll(workflowList);
        mUserLiveData.setValue(workflowList);
    }

    private void onWorkflowFailure(Throwable throwable) {
        mErrorLiveData.setValue(R.string.failure_connect);
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

    public void applyFilters(WorkflowFragment.Sort sorting) {
        List<Workflow> workflows = mUserLiveData.getValue();

        switch (sorting.getSortingType()) {
            case NONE: {
                workflows = new ArrayList<>();
                workflows.addAll(unordered);
                break;
            }
            case BYNUMBER: {
                Collections.sort(workflows, (s1, s2) -> {
                    if (sorting.getNumberSortOrder().equals(WorkflowFragment.sortOrder.ASC)) {
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
                        if (sorting.getCreatedSortOrder().equals(WorkflowFragment.sortOrder.ASC)) {
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

        mUserLiveData.setValue(workflows);
    }
}
