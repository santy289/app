package com.rootnetapp.rootnetintranet.models.ui.signature;

public class SignatureTemplateMenuItem {
    private int templateId;
    private String name;
    private String templateStatus;
    private String documentStatus;

    public SignatureTemplateMenuItem(int templateId, String name, String templateStatus, String documentStatus) {
        this.templateId = templateId;
        this.name = name;
        this.templateStatus = templateStatus;
        this.documentStatus = documentStatus;
    }

    public int getTemplateId() {
        return templateId;
    }

    public String getName() {
        return name;
    }

    public String getTemplateStatus() {
        return templateStatus;
    }

    public String getDocumentStatus() {
        return documentStatus;
    }
}
