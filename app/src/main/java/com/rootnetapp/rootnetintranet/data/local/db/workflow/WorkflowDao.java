package com.rootnetapp.rootnetintranet.data.local.db.workflow;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by root on 16/03/18.
 */

@Dao
public abstract class WorkflowDao {

    /*public List<Workflow> getAll() {
        List<Workflow> workflows = getAllWorkflows();
        for(Workflow workflow: workflows) {
            workflow.setAuthor(getPerson(workflow.getAuthorId()));
            workflow.setWorkflowType(getWorkflowType(workflow.getWorkflowTypeId()));
        }
        return workflows;
    }*/

    public void insertAll(List<Workflow> workflows) {
        for(Workflow workflow: workflows) {
            /*workflow.setAuthorId(workflow.getAuthor().getId());
            insertPerson(workflow.getAuthor());
            workflow.setWorkflowTypeId(workflow.getWorkflowType().getId());
            insertWorkflowType(workflow.getWorkflowType());*/
            if(workflow.getWorkflowStateInfo() != null){
                workflow.setWorkflowStateId(workflow.getWorkflowStateInfo().getId());
            }
        }

        insertAllWorkflows(workflows);
    }

    /*public Workflow getById(int id) {
        Workflow workflow = getWorkflow(id);
        workflow.setAuthor(getPerson(workflow.getAuthorId()));
        workflow.setWorkflowType(getWorkflowType(workflow.getWorkflowTypeId()));
        return workflow;
    }*/

    /*public void deleteWorkflows() {
        clearWorkflows();
        /*clearPersons();
        clearWorkflowTypes();
    }*/

    @Query("SELECT * FROM workflow")
    public abstract List<Workflow> getAllWorkflows();

    @Query("SELECT * FROM workflow WHERE id = :id")
    public abstract Workflow getWorkflow(int id);

    /*@Query("SELECT * FROM person WHERE id = :id")
    abstract Person getPerson(int id);

    @Query("SELECT * FROM workflowtype WHERE id = :id")
    abstract WorkflowType getWorkflowType(int id);*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertAllWorkflows(List<Workflow> workflows);

    /*@Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertPerson(Person person);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertWorkflowType(WorkflowType workflowType);*/

    @Query("DELETE FROM workflow")
    public abstract void clearWorkflows();

    /*@Query("DELETE FROM person")
    abstract void clearPersons();

    @Query("DELETE FROM workflowtype")
    abstract void clearWorkflowTypes();*/

}
