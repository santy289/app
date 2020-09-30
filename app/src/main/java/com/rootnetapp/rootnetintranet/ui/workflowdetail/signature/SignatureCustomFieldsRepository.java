package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;
import com.rootnetapp.rootnetintranet.models.requests.signature.SignatureInitiateRequest;
import com.rootnetapp.rootnetintranet.models.responses.signature.Fields;
import com.rootnetapp.rootnetintranet.models.responses.signature.InitiateSigningResponse;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SignatureCustomFieldsRepository extends SignatureRepository {


    protected SignatureCustomFieldsRepository(ApiInterface service, AppDatabase database) {
        super(service, database);
    }

    protected Observable<Object> initializeWithCustomFields(String token,
                                                            String signatureType,
                                                            int templateId,
                                                            int workflowId,
                                                            List<Fields> customFields) {
        SignatureInitiateRequest request = new SignatureInitiateRequest(
                signatureType,
                templateId,
                workflowId,
                customFields
        );
        return service.initiateSigningWithFields(token, request)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

}
