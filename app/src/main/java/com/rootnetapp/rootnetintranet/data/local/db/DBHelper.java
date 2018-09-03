package com.rootnetapp.rootnetintranet.data.local.db;

import com.rootnetapp.rootnetintranet.data.local.db.test.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.test2.WorkflowTypeDb;

import java.util.ArrayList;
import java.util.List;

public class DBHelper {
    static public List<WorkflowDb> prepareWorkflowDbForStorage(List<WorkflowDb> workflows) {
        List<WorkflowDb> newList = new ArrayList<>();
        WorkflowDb workflow;
        WorkflowTypeDb workflowType;
        for (int i = 0; i < workflows.size(); i++) {
            workflow = workflows.get(i);
            workflowType = workflow.getWorkflowType();
            int id = workflowType.getId();
            workflow.setWorkflowTypeId(id);
            newList.add(workflow);
        }
        return newList;
    }
}
