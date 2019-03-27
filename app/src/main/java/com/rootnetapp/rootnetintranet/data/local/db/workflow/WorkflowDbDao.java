package com.rootnetapp.rootnetintranet.data.local.db.workflow;

import com.rootnetapp.rootnetintranet.data.local.db.workflow.detail.WorkflowTypeId;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDb;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;
import io.reactivex.Single;

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

    @Delete
    public void deleteWorkflows(List<WorkflowDb> workflowDbs);

    @Query("DELETE FROM workflowdb WHERE workflowdb.id IN (:workflowIds)")
    public void deleteWorkflowsByIds(List<Integer> workflowIds);

    @Query("DELETE FROM workflowdb")
    public int deleteAllWorkflows();

    @Query("SELECT * FROM workflowdb")
    public List<WorkflowDb> getAllWorkflows();

    @Query("SELECT workflowdb.id AS workflowId, workflowtypedb.id AS workflowTypeId, workflowdb.remaining_time AS remainingTime, " +
            "workflowtypedb.name AS workflowTypeName, workflowdb.title, workflowdb.workflow_type_key, " +
            "workflowdb.full_name, workflowdb.user_id, workflowdb.current_status_name, workflowdb.created_at, workflowdb.updated_at, " +
            "workflowdb.start, workflowdb.status, workflowdb.current_status, workflowdb.`end` " +
            "FROM workflowdb, workflowtypedb " +
            "WHERE workflowdb.workflow_type_id = workflowtypedb.id " +
            "ORDER BY workflowdb.created_at DESC")
    public DataSource.Factory<Integer, WorkflowListItem> getWorkflows();

    @Query("SELECT workflowdb.id AS workflowId, workflowtypedb.id AS workflowTypeId, workflowdb.remaining_time AS remainingTime, " +
            "workflowtypedb.name AS workflowTypeName, workflowdb.title, workflowdb.workflow_type_key, " +
            "workflowdb.full_name, workflowdb.user_id, workflowdb.current_status_name, workflowdb.created_at, workflowdb.updated_at, " +
            "workflowdb.start, workflowdb.status, workflowdb.current_status, workflowdb.`end` " +
            "FROM workflowdb, workflowtypedb " +
            "WHERE workflowdb.workflow_type_id = workflowtypedb.id " +
            "ORDER BY workflowdb.updated_at DESC")
    public DataSource.Factory<Integer, WorkflowListItem> getWorkflowsByUpdatedAt();

    @Query("SELECT workflowdb.id AS workflowId, workflowtypedb.id AS workflowTypeId, workflowdb.remaining_time AS remainingTime, " +
            "workflowtypedb.name AS workflowTypeName, workflowdb.title, workflowdb.workflow_type_key, " +
            "workflowdb.full_name, workflowdb.user_id, workflowdb.current_status_name, workflowdb.created_at, workflowdb.updated_at, " +
            "workflowdb.start, workflowdb.status, workflowdb.current_status, workflowdb.`end` " +
            "FROM workflowdb, workflowtypedb " +
            "WHERE workflowdb.workflow_type_id = workflowtypedb.id " +
            "AND (workflowdb.title LIKE '%' || :query || '%' OR WorkflowTypeDb.name LIKE '%' || :query || '%' OR workflowdb.description LIKE '%' || :query || '%' OR workflowdb.workflow_type_key LIKE '%' || :query || '%' OR workflowdb.full_name LIKE '%' || :query || '%') " +
            "ORDER BY workflowdb.updated_at DESC")
    public DataSource.Factory<Integer, WorkflowListItem> searchWorkflow(String query);


    @Query("SELECT workflowdb.id AS workflowId, workflowtypedb.id AS workflowTypeId, workflowdb.remaining_time AS remainingTime, " +
            "workflowtypedb.name AS workflowTypeName, workflowdb.title, workflowdb.workflow_type_key, " +
            "workflowdb.full_name, workflowdb.user_id, workflowdb.current_status_name, workflowdb.created_at, workflowdb.updated_at, " +
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

    @Query("SELECT workflowdb.id AS workflowId, workflowtypedb.id AS workflowTypeId, workflowdb.remaining_time AS remainingTime, " +
            "workflowtypedb.name AS workflowTypeName, workflowdb.title, workflowdb.workflow_type_key, " +
            "workflowdb.full_name, workflowdb.user_id, workflowdb.current_status_name, workflowdb.created_at, workflowdb.updated_at, " +
            "workflowdb.start, workflowdb.current_status, workflowdb.status, workflowdb.`end` " +
            "FROM workflowtypedb INNER JOIN workflowdb " +
            "ON workflowdb.workflow_type_id = workflowtypedb.id " +
            "WHERE workflowdb.workflow_type_id = :workflowTypeId " +
            "AND workflowdb.id = :workflowId " +
            "ORDER BY workflowdb.created_at DESC")
    public Single<WorkflowListItem> getWorkflowDbBy(int workflowId, int workflowTypeId);


    @Query("SELECT workflow_type_id FROM workflowdb WHERE id = :workflowId")
    public Single<WorkflowTypeId> loadWorkflowTypeId(int workflowId);
}
