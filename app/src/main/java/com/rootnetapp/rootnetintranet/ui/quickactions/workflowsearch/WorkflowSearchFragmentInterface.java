package com.rootnetapp.rootnetintranet.ui.quickactions.workflowsearch;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;

public interface WorkflowSearchFragmentInterface {

    //    void performAction(WorkflowListItem item); //todo use the minimalistic class
    void performAction(WorkflowDb item);
}
