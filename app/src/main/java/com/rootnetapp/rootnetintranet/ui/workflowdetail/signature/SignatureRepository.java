package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import androidx.lifecycle.LiveData;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.local.db.signature.TemplateSignature;
import com.rootnetapp.rootnetintranet.data.local.db.signature.TemplateSignatureDao;
import com.rootnetapp.rootnetintranet.data.local.db.signature.TemplateSigner;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.requests.signature.SignatureInitiateRequest;
import com.rootnetapp.rootnetintranet.models.responses.signature.DocumentListResponse;
import com.rootnetapp.rootnetintranet.models.responses.signature.DocumentResponse;
import com.rootnetapp.rootnetintranet.models.responses.signature.DocumentSigner;
import com.rootnetapp.rootnetintranet.models.responses.signature.InitiateSigningResponse;
import com.rootnetapp.rootnetintranet.models.responses.signature.SignatureTemplate;
import com.rootnetapp.rootnetintranet.models.responses.signature.Signer;
import com.rootnetapp.rootnetintranet.models.responses.signature.TemplatesResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SignatureRepository {

    protected ApiInterface service;
    protected TemplateSignatureDao templateSignatureDao;


    protected SignatureRepository(ApiInterface service, AppDatabase database) {
        this.service = service;
        this.templateSignatureDao = database.templateSignatureDao();
    }

    protected Observable<Object> overwriteDocument(String token, int workflowId, int templateId) {
        return service.deleteDocument(token, workflowId, templateId)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    protected Observable<InitiateSigningResponse> initiateSigning(String token, SignatureInitiateRequest body) {
        return service.initiateSigning(token, body)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
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

    /**
     * This function checks for the documentSigners (signers coming from the network, which already
     * signed or rejected the document) and updates our existing templateSigners already in the database.
     *
     * @param documentListResponse
     * @param workflowTypeId
     * @param workflowId
     * @return
     */
    protected Observable<Boolean> saveSignatureDocuments(
            DocumentListResponse documentListResponse,
            int workflowTypeId,
            int workflowId) {
        return Observable.fromCallable(() -> {
            List<DocumentResponse> documentResponses = documentListResponse.getResponse();
            for (DocumentResponse documentResponse : documentResponses) {
                List<DocumentSigner> documentSigners = documentResponse.getSigners();
                if (documentSigners == null || documentSigners.size() < 1) {
                    continue;
                }

                List<TemplateSigner> templateSigners = templateSignatureDao.getListAllSignersByIds(
                        workflowTypeId,
                        workflowId,
                        documentResponse.getTemplateId()
                );

                if (templateSigners == null || templateSigners.size() < 1) {
                    continue;
                }

                for (DocumentSigner documentSigner : documentSigners) {
                    for (TemplateSigner templateSigner : templateSigners) {
                        if (templateSigner.getUserId() != documentSigner.getProfileId()) {
                            continue;
                        }
                        if (!documentSigner.isReady()) {
                            continue;
                        }
                        templateSignatureDao.updateTemplateSigner(
                                templateSigner.getUserId(),
                                workflowId,
                                workflowTypeId,
                                documentResponse.getTemplateId(),
                                documentSigner.isReady(),
                                documentSigner.getDisplayTime()
                        );
                    }
                }
            }
            return true;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * This functions handle taking a TemplateResponse from network and parsing it to the database
     * models for the app.
     *
     * @param templatesResponse
     * @param workflowId
     * @param workflowTypeId
     * @return
     */
    protected Observable<Boolean> processAndTemplateResponse(TemplatesResponse templatesResponse, int workflowId, int workflowTypeId) {
        return Observable.fromCallable(() -> {
            List<TemplateSignature> templateSignatures = new ArrayList<>();
            TemplateSignature templateSignature;
            for (SignatureTemplate signatureTemplate : templatesResponse.getResponse()) {
                templateSignature = new TemplateSignature(
                        signatureTemplate.getTemplateId(),
                        workflowTypeId,
                        workflowId,
                        signatureTemplate.getName(),
                        signatureTemplate.getDocumentStatus(),
                        signatureTemplate.getTemplateStatus()
                );
                templateSignatures.add(templateSignature);

                List<Signer> signers = signatureTemplate.getUsers();
                if (signers == null || signers.size() < 1) {
                    continue;
                }

                List<TemplateSigner> templateSignerList = new ArrayList<>();
                TemplateSigner templateSigner;
                for (Signer signer : signers) {
                    templateSigner = new TemplateSigner(
                            signer.getId(),
                            workflowId,
                            workflowTypeId,
                            signatureTemplate.getTemplateId(),
                            signer.isEnabled(),
                            signer.isFieldUser(),
                            signer.getDetails().getFirstName(),
                            signer.getDetails().getLastName(),
                            signer.getDetails().isExternalUser(),
                            signer.getDetails().getEmail(),
                            signer.getDetails().getRole(),
                            signer.getDetails().getFullName(),
                            false,
                            ""
                    );
                    templateSignerList.add(templateSigner);
                }


                if (templateSignerList.size() == 0) {
                    return false;
                }
                templateSignatureDao.deleteAllSignersById(workflowTypeId, workflowId);
                templateSignatureDao.insertAllSigners(templateSignerList);
            }

            if (templateSignatures.size() == 0) {
                return false;
            }

            templateSignatureDao.deleteAllById(workflowTypeId, workflowId);
            templateSignatureDao.insertAll(templateSignatures);
            return true;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
