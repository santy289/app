package com.rootnetapp.rootnetintranet.models.responses.signature;

import android.text.TextUtils;

import com.squareup.moshi.Json;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DocumentSigner {
    private String email;

    @Json(name = "internal_id")
    private String internalId;

    @Json(name = "internal_id_type")
    private String internalIdType;

    private String name;

    private boolean ready;

    private int profileId;

    @Json(name = "signer_status")
    private String signerStatus;

    @Json(name = "operation_time")
    private String operationTime;

    public String getOperationTime() {
        return operationTime;
    }

    public String getDisplayTime() {
        if (TextUtils.isEmpty(operationTime)) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SimpleDateFormat formatterOut = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        try {
            Date date = format.parse(operationTime);
            if (date == null) {
                return "";
            }
            return formatterOut.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setOperationTime(String operationTime) {
        this.operationTime = operationTime;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public String getInternalIdType() {
        return internalIdType;
    }

    public void setInternalIdType(String internalIdType) {
        this.internalIdType = internalIdType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public String getSignerStatus() {
        return signerStatus;
    }

    public void setSignerStatus(String signerStatus) {
        this.signerStatus = signerStatus;
    }
}
