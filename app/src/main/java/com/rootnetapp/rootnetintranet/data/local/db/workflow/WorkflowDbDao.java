package com.rootnetapp.rootnetintranet.data.local.db.workflow;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.sqlite.db.SupportSQLiteQuery;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDb;

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

    @Query("SELECT workflowdb.id AS workflowId, workflowtypedb.id AS workflowTypeId, workflowdb.remaining_time AS remainingTime, " +
            "workflowtypedb.name AS workflowTypeName, workflowdb.title, workflowdb.workflow_type_key, " +
            "workflowdb.full_name, workflowdb.current_status_name, workflowdb.created_at, workflowdb.updated_at, " +
            "workflowdb.start, workflowdb.status, workflowdb.current_status, workflowdb.`end` " +
            "FROM workflowdb, workflowtypedb " +
            "WHERE workflowdb.workflow_type_id = workflowtypedb.id " +
            "ORDER BY workflowdb.created_at DESC")
    public DataSource.Factory<Integer, WorkflowListItem> getWorkflows();

    @Query("SELECT workflowdb.id AS workflowId, workflowtypedb.id AS workflowTypeId, workflowdb.remaining_time AS remainingTime, " +
            "workflowtypedb.name AS workflowTypeName, workflowdb.title, workflowdb.workflow_type_key, " +
            "workflowdb.full_name, workflowdb.current_status_name, workflowdb.created_at, workflowdb.updated_at, " +
            "workflowdb.start, workflowdb.current_status, workflowdb.status, workflowdb.`end` " +
            "FROM workflowtypedb INNER JOIN workflowdb " +
            "ON workflowdb.workflow_type_id = workflowtypedb.id " +
            "WHERE workflowdb.workflow_type_id = :workflowTypeId " +
            "ORDER BY workflowdb.created_at DESC")
    public DataSource.Factory<Integer, WorkflowListItem> getWorkflowsBy(int workflowTypeId);

    @RawQuery(observedEntities = {
            WorkflowDb.class,
            WorkflowTypeDb.class
    })
    public DataSource.Factory<Integer, WorkflowListItem> getWorkflowsWithFilter(SupportSQLiteQuery query);


    @Query("SELECT * FROM workflowdb WHERE workflow_type_id = :workflowTypeId")
    public LiveData<List<WorkflowDb>> getWorkflowsByType(int workflowTypeId);

    @Query("SELECT * FROM workflowdb WHERE user_id = :userId")
    public LiveData<List<WorkflowDb>> getObservableWorkflowsByUser(int userId);

//    @Transaction
//    @Query("SELECT id, name FROM WorkflowTypeDb WHERE id = :workflowTypeId")
//    public List<WorkflowTypeAndWorkflows> loadWorkflowTypeAndWorkflows(int workflowTypeId);

}
