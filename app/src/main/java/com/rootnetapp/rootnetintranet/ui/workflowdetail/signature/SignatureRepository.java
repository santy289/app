package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import androidx.lifecycle.LiveData;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.signature.TemplateSignature;
import com.rootnetapp.rootnetintranet.data.local.db.signature.TemplateSignatureDao;
import com.rootnetapp.rootnetintranet.data.local.db.signature.TemplateSigner;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.responses.signature.DocumentListResponse;
import com.rootnetapp.rootnetintranet.models.responses.signature.TemplatesResponse;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SignatureRepository {

    private ApiInterface service;
    private TemplateSignatureDao templateSignatureDao;


    protected SignatureRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
        this.templateSignatureDao = database.templateSignatureDao();
    }

    protected LiveData<List<TemplateSignature>> getAllTemplatesBy(int workflowTypeId, int workflowId) {
        return templateSignatureDao.getAllTemplatesByWorkflowTypeId(workflowTypeId, workflowId);
    }

    protected Observable<TemplatesResponse> getTemplatesBy(String token, int workflowTypeId, int workflowId) {
        return service.getSignatureTemplatesBy(token, workflowTypeId, workflowId)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    protected Observable<DocumentListResponse> getSignatureDocuments(String token, int workflowId) {
        return service.getSignatureDocumentList(token, workflowId)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


    protected Observable<Boolean> saveTemplates(List<TemplateSignature> templates) {
        return Observable.fromCallable(() -> {
            if (templates == null || templates.size() == 0) {
                return false;
            }
            TemplateSignature templateSignature = templates.get(0);
            templateSignatureDao.deleteAllById(templateSignature.getWorkflowTypeId(), templateSignature.getWorkflowId());
            templateSignatureDao.insertAll(templates);
            return true;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    protected LiveData<List<TemplateSigner>> getAllSignersBy(int workflowTypeId, int workflowId, int templateId) {
        return templateSignatureDao.getAllSignersByIds(workflowTypeId, workflowId, templateId);
    }

    protected Observable<List<TemplateSigner>> getAllSigners(int workflowTypeId, int workflowId, int templateId) {
        return Observable.fromCallable(() -> templateSignatureDao.getListAllSignersByIds(workflowTypeId, workflowId, templateId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    protected Observable<Boolean> saveSigners(List<TemplateSigner> signers) {
        return Observable.fromCallable(() -> {
            if (signers == null || signers.size() == 0) {
                return false;
            }
            TemplateSigner signer = signers.get(0);
            templateSignatureDao.deleteAllSignersById(signer.getWorkflowTypeId(), signer.getWorkflowId());
            templateSignatureDao.insertAllSigners(signers);
            return true;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
