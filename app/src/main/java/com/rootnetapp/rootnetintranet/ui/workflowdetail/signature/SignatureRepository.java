package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import androidx.lifecycle.LiveData;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.signature.TemplateSignature;
import com.rootnetapp.rootnetintranet.data.local.db.signature.TemplateSignatureDao;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
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

    protected LiveData<List<TemplateSignature>> getAllTemplatesBy(int workflowTypeId) {
        return templateSignatureDao.getAllTemplatesByWorkflowTypeId(workflowTypeId);
    }

    protected Observable<TemplatesResponse> getTemplatesBy(String token, int workflowTypeId, int workflowId) {
        return service.getSignatureTemplatesBy(token, workflowTypeId, workflowId)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    protected Observable<Boolean> saveTemplates(List<TemplateSignature> templates) {
        return Observable.fromCallable(() -> {
            if (templates == null || templates.size() == 0) {
                return false;
            }
            TemplateSignature templateSignature = templates.get(0);
            templateSignatureDao.deleteAllById(templateSignature.getWorkflowTypeId());
            templateSignatureDao.insertAll(templates);
            return true;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

}
