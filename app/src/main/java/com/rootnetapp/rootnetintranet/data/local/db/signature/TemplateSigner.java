package com.rootnetapp.rootnetintranet.data.local.db.signature;

import androidx.room.Entity;

@Entity(tableName = "template_signer", primaryKeys = {"userId", "templateId", "workflowId", "workflowTypeId"})
public class TemplateSigner {
    private int userId;
    private int workflowId;
    private int workflowTypeId;
    private int templateId;
    private boolean enabled;
    private boolean isFieldUser;
    private String firstName;
    private String lastName;
    private boolean isExternalUser;
    private String email;
    private String role;
    private String fullName;

    public TemplateSigner(int userId, int workflowId, int workflowTypeId, int templateId, boolean enabled, boolean isFieldUser, String firstName, String lastName, boolean isExternalUser, String email, String role, String fullName) {
        this.userId = userId;
        this.workflowId = workflowId;
        this.workflowTypeId = workflowTypeId;
        this.templateId = templateId;
        this.enabled = enabled;
        this.isFieldUser = isFieldUser;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isExternalUser = isExternalUser;
        this.email = email;
        this.role = role;
        this.fullName = fullName;
    }

    public int getUserId() {
        return userId;
    }

    public int getWorkflowId() {
        return workflowId;
    }

    public int getWorkflowTypeId() {
        return workflowTypeId;
    }

    public int getTemplateId() {
        return templateId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isFieldUser() {
        return isFieldUser;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isExternalUser() {
        return isExternalUser;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getFullName() {
        return fullName;
    }
}
