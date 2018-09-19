package com.rootnetapp.rootnetintranet.data.local.db.workflowtype;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.createform.FormFieldsByWorkflowType;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.workflowlist.WorkflowTypeItemMenu;

import java.util.List;

@Dao
public interface WorkflowTypeDbDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertWorkflowTypes(List<WorkflowTypeDb> workflowTypeDbs);

    @Insert
    public void insertWorkflowType(WorkflowTypeDb workflowTypeDb);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllFields(List<Field> Fields);

    @Update
    public void updateWorkflowTypes(WorkflowTypeDb... workflowTypeDbs);

    @Delete
    public void deleteWorkflowTypes(WorkflowTypeDb... workflowTypeDbs);

    @Query("DELETE FROM workflowtypedb")
    public void deleteAllWorkfloyTypes();

    @Query("DELETE FROM field")
    public void deleteAllFields();

    @Query("SELECT * FROM workflowtypedb")
    public List<WorkflowTypeDb> getAllWorkflowTypes();

    @Query("SELECT * FROM workflowtypedb")
    public LiveData<List<WorkflowTypeDb>> getObservableWorkflowTypes();

    @Query("SELECT id, name FROM workflowtypedb")
    public LiveData<List<WorkflowTypeItemMenu>> getObservableTypesForMenu();

    @Query("SELECT id, name FROM workflowtypedb")
    public List<WorkflowTypeItemMenu> getListOfWorkflowNames();

    @Query("SELECT field.id AS id, field.field_id AS fieldId, workflowtypedb.id AS workflowTypeId, " +
            "workflowtypedb.name AS workflowTypeName, field.field_name, field.field_config," +
            "field.show_form, field.required " +
            "FROM workflowtypedb INNER JOIN field " +
            "ON field.workflow_type_id = workflowtypedb.id " +
            "WHERE field.workflow_type_id = :byId")
    public List<FormFieldsByWorkflowType> getFields(int byId);

}
