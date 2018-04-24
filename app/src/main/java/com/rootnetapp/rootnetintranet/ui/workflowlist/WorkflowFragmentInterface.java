package com.rootnetapp.rootnetintranet.ui.workflowlist;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;

/**
 * Created by root on 28/03/18.
 */

public interface WorkflowFragmentInterface {
    void dataAdded();

    void showDetail(Workflow item);
}
