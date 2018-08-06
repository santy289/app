package com.rootnetapp.rootnetintranet.data.local.db.workflow;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

/**
 * Created by root on 16/03/18.
 */

@Dao
public abstract class WorkflowDao {

    public void insertAll(List<Workflow> workflows) {
        for(Workflow workflow: workflows) {


            /*if(workflow.getWorkflowStateInfo() != null){
                workflow.setWorkflowStateId(workflow.getWorkflowStateInfo().getId());
            }*/
        }
        insertAllWorkflows(workflows);
    }

    @Query("SELECT * FROM workflow")
    public abstract List<Workflow> getAllWorkflows();

    @Query("SELECT * FROM workflow WHERE id = :id")
    public abstract Workflow getWorkflow(int id);

    @Query("SELECT id as _id, * FROM workflow WHERE title LIKE :name")
    public abstract Cursor getWorkflowsLike(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertAllWorkflows(List<Workflow> workflows);

    @Query("DELETE FROM workflow")
    public abstract void clearWorkflows();

}
