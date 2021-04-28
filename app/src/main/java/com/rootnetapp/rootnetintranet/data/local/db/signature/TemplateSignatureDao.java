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

    @Query("SELECT * FROM template_signature WHERE id = :templateId AND workflow_type_id = :workflowTypeId AND workflow_id = :workflowId")
    List<TemplateSignature> findTemplateDocumentById(int templateId, int workflowTypeId, int workflowId);

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

    @Query("SELECT * FROM template_signer WHERE workflowTypeId = :workflowTypeId AND workflowId = :workflowId AND templateId = :templateId")
    List<TemplateSigner> getListAllSignersByIds(int workflowTypeId, int workflowId, int templateId);

    @Query("UPDATE template_signer SET isReady = :isReady, operationTime = :operationTime WHERE userId = :userId AND workflowId = :workflowId AND workflowTypeId = :workflowTypeId AND templateId = :templateId")
    void updateTemplateSigner(int userId, int workflowId, int workflowTypeId, int templateId, boolean isReady, String operationTime);

    @Query("UPDATE template_signature SET file_name = :fileName, expiration_time = :expirationTime, provider_document_id = :providerDocumentId WHERE workflow_id = :workflowId AND workflow_type_id = :workflowTypeId AND id = :templateId")
    void updateDocumentDataInTemplates(int workflowId, int workflowTypeId, int templateId, String fileName, String expirationTime, String providerDocumentId);
}
