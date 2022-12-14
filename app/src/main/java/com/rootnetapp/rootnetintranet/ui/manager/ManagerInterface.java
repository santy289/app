package com.rootnetapp.rootnetintranet.ui.manager;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;

/**
 * Created by root on 19/04/18.
 */

public interface ManagerInterface {

    void setDate(String start, String end);

    void showWorkflow(WorkflowListItem workflowListItem);

    void showToastMessage(int stringRes);

}
