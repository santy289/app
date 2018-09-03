package com.rootnetapp.rootnetintranet.data.local.db.test;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface WorkflowDbDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertWorkflows(List<WorkflowDb> workflows);

    @Insert
    public void insertWorkflow(WorkflowDb workflowDb);

    @Update
    public void updateWorkflows(WorkflowDb... workflowDbs);

    @Delete
    public void deleteWorkflows(WorkflowDb... workflowDbs);

    @Query("DELETE FROM workflowdb")
    public int deleteAllWorkflows();

    @Query("SELECT * FROM workflowdb")
    public List<WorkflowDb> getAllWorkflows();

    @Query("SELECT * FROM workflowdb")
    public LiveData<List<WorkflowDb>> getWorkflows();

    @Query("SELECT * FROM workflowdb WHERE workflow_type_id = :workflowTypeId")
    public LiveData<List<WorkflowDb>> getWorkflowsByType(int workflowTypeId);

    @Query("SELECT * FROM workflowdb WHERE user_id = :userId")
    public LiveData<List<WorkflowDb>> getObservableWorkflowsByUser(int userId);


}
