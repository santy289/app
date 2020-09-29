package com.rootnetapp.rootnetintranet.models.responses.signature;

import java.util.List;

public class SignatureTemplate {
    private String documentStatus;
    private String name;
    private int templateId;
    private String templateStatus;
    private List<Signer> users;

    public String getDocumentStatus() {
        return documentStatus;
    }

    public String getName() {
        return name;
    }

    public int getTemplateId() {
        return templateId;
    }

    public String getTemplateStatus() {
        return templateStatus;
    }

    public void setDocumentStatus(String documentStatus) {
        this.documentStatus = documentStatus;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public void setTemplateStatus(String templateStatus) {
        this.templateStatus = templateStatus;
    }

    public List<Signer> getUsers() {
        return users;
    }

    public void setUsers(List<Signer> users) {
        this.users = users;
    }
}
