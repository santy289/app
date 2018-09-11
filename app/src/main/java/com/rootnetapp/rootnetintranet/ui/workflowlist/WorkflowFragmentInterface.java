package com.rootnetapp.rootnetintranet.ui.workflowlist;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;

public interface WorkflowFragmentInterface {
    void dataAdded();

    void showDetail(WorkflowDb item);
}
