package com.rootnetapp.rootnetintranet.ui.workflowlist;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;

public interface WorkflowFragmentInterface {
    void dataAdded();

    void showDetail(WorkflowListItem item);
}
