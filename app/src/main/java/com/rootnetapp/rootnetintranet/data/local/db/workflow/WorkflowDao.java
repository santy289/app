package com.rootnetapp.rootnetintranet.data.local.db.workflow;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import android.database.Cursor;

import java.util.List;

@Dao
public abstract class WorkflowDao {

    public void insertAll(List<Workflow> workflows) {
        insertAllWorkflows(workflows);
    }

    @Query("SELECT * FROM workflow")
    public abstract List<Workflow> getAllWorkflows();

    @Query("SELECT * FROM workflow")
    public abstract LiveData<List<Workflow>> getWorkflows();

    @Query("SELECT * FROM workflow WHERE id = :id")
    public abstract Workflow getWorkflow(int id);

    @Query("SELECT id as _id, * FROM workflow WHERE title LIKE '%' || :name || '%'")
    public abstract Cursor getWorkflowsLike(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertAllWorkflows(List<Workflow> workflows);

    @Insert
    public abstract void insertWorkflow(Workflow workflow);

    @Query("DELETE FROM workflow")
    public abstract void clearWorkflows();

}
