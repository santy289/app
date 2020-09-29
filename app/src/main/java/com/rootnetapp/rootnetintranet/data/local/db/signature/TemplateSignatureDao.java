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

    @Query("DELETE FROM template_signature WHERE workflow_type_id = :workflowTypeId AND workflow_id = :workflowId")
    void deleteAllById(int workflowTypeId, int workflowId);

    @Query("SELECT * FROM template_signature WHERE workflow_type_id = :workflowTypeId AND workflow_id = :workflowId")
    LiveData<List<TemplateSignature>> getAllTemplatesByWorkflowTypeId(int workflowTypeId, int workflowId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSigner(TemplateSigner signer);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllSigners(List<TemplateSigner> signers);

    @Query("DELETE FROM template_signer")
    void deleteAllSigners();

    @Query("DELETE FROM template_signer WHERE workflowTypeId = :workflowTypeId AND workflowId = :workflowId")
    void deleteAllSignersById(int workflowTypeId, int workflowId);

    @Query("SELECT * FROM template_signer WHERE workflowTypeId = :workflowTypeId AND workflowId = :workflowId AND templateId = :templateId")
    LiveData<List<TemplateSigner>> getAllSignersByIds(int workflowTypeId, int workflowId, int templateId);
}
