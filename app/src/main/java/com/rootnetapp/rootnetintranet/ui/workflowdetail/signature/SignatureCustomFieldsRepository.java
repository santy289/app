package com.rootnetapp.rootnetintranet.ui.workflowdetail.signature;

import com.rootnetapp.rootnetintranet.data.local.db.AppDatabase;
import com.rootnetapp.rootnetintranet.data.remote.ApiInterface;

public class SignatureCustomFieldsRepository extends SignatureRepository {


    protected SignatureCustomFieldsRepository(ApiInterface service, AppDatabase database) {
        super(service, database);
    }


}
