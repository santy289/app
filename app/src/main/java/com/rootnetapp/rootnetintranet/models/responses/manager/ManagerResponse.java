package com.rootnetapp.rootnetintranet.models.responses.manager;

import com.squareup.moshi.Json;

/**
 * Created by root on 25/04/18.
 */

public class ManagerResponse {

    @Json(name = "my_workflows")
    private MyWorkflows myWorkflows;
    @Json(name = "company_workflows")
    private CompanyWorkflows companyWorkflows;

    public MyWorkflows getMyWorkflows() {
        return myWorkflows;
    }

    public void setMyWorkflows(MyWorkflows myWorkflows) {
        this.myWorkflows = myWorkflows;
    }

    public CompanyWorkflows getCompanyWorkflows() {
        return companyWorkflows;
    }

    public void setCompanyWorkflows(CompanyWorkflows companyWorkflows) {
        this.companyWorkflows = companyWorkflows;
    }

}
