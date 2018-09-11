package com.rootnetapp.rootnetintranet.data.local.db.workflowtype;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface WorkflowTypeDbDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertWorkflowTypes(List<WorkflowTypeDb> workflowTypeDbs);

    @Insert
    public void insertWorkflowType(WorkflowTypeDb workflowTypeDb);

    @Update
    public void updateWorkflowTypes(WorkflowTypeDb... workflowTypeDbs);

    @Delete
    public void deleteWorkflowTypes(WorkflowTypeDb... workflowTypeDbs);

    @Query("DELETE FROM workflowtypedb")
    public void deleteAllWorkfloyTypes();

    @Query("SELECT * FROM workflowtypedb")
    public List<WorkflowTypeDb> getAllWorkflowTypes();

    @Query("SELECT * FROM workflowtypedb")
    public LiveData<List<WorkflowTypeDb>> getObservableWorkflowTypes();

}
