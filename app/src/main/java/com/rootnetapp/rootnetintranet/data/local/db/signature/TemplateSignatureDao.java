package com.rootnetapp.rootnetintranet.data.local.db.signature;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TemplateSignatureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TemplateSignature templateSignature);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TemplateSignature> templates);

    @Query("DELETE FROM template_signature")
    void deleteAll();

    @Query("DELETE FROM template_signature WHERE workflow_type_id = :workflowTypeId")
    void deleteAllById(int workflowTypeId);

    @Query("SELECT * FROM template_signature WHERE workflow_type_id = :workflowTypeId")
    LiveData<List<TemplateSignature>> getAllTemplatesByWorkflowTypeId(int workflowTypeId);
}
