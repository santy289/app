package com.rootnetapp.rootnetintranet.models.responses.signature;

public class UserRequired {
    public int Id;
    public boolean IsFieldUser;
    public String ProviderId;
    public int UserId;
    public int WorkflowTypeHasSignatureFieldId;
    public int WorkflowTypeId;
    public String fullName;

    public UserRequired(int id, boolean isFieldUser, String providerId, int userId, int workflowTypeHasSignatureFieldId, int workflowTypeId, String fullName) {
        Id = id;
        IsFieldUser = isFieldUser;
        ProviderId = providerId;
        UserId = userId;
        WorkflowTypeHasSignatureFieldId = workflowTypeHasSignatureFieldId;
        WorkflowTypeId = workflowTypeId;
        fullName = fullName;
    }

    public int getId() {
        return Id;
    }

    public boolean isFieldUser() {
        return IsFieldUser;
    }

    public String getProviderId() {
        return ProviderId;
    }

    public int getUserId() {
        return UserId;
    }

    public int getWorkflowTypeHasSignatureFieldId() {
        return WorkflowTypeHasSignatureFieldId;
    }

    public int getWorkflowTypeId() {
        return WorkflowTypeId;
    }

    public String getFullName() {
        return fullName;
    }
}
