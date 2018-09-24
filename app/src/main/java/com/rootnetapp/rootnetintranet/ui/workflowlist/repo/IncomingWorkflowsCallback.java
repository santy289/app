package com.rootnetapp.rootnetintranet.ui.workflowlist.repo;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;

import java.util.List;

public interface IncomingWorkflowsCallback {
    void handleResponse(List<WorkflowDb> workflowsResponse, int lastPage);
    void showLoadingMore(boolean loadMore);
}
