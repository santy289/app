
package com.rootnetapp.rootnetintranet.models.responses.workflowoverview;

import com.squareup.moshi.Json;

public class Overview {

    @Json(name = "my_workflows")
    private UserWorkflows userWorkflows;
    @Json(name = "company_workflows")
    private CompanyWorkflows companyWorkflows;

    public UserWorkflows getUserWorkflows() {
        return userWorkflows;
    }

    public void setUserWorkflows(UserWorkflows userWorkflows) {
        this.userWorkflows = userWorkflows;
    }

    public CompanyWorkflows getCompanyWorkflows() {
        return companyWorkflows;
    }

    public void setCompanyWorkflows(CompanyWorkflows companyWorkflows) {
        this.companyWorkflows = companyWorkflows;
    }

}
