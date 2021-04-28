package com.rootnetapp.rootnetintranet.models.responses.signature;

public class Signer {
    private int id;
    private boolean enabled;
    private boolean isFieldUser;
    private SignerDetails details;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isFieldUser() {
        return isFieldUser;
    }

    public void setFieldUser(boolean fieldUser) {
        isFieldUser = fieldUser;
    }

    public SignerDetails getDetails() {
        return details;
    }

    public void setDetails(SignerDetails details) {
        this.details = details;
    }
}
