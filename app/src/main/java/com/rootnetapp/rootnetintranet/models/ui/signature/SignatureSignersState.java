package com.rootnetapp.rootnetintranet.models.ui.signature;

import androidx.annotation.StringRes;

import java.util.List;

public class SignatureSignersState {
    private boolean showMessage;
    private List<SignerItem> signerItems;
    @StringRes
    private int message;

    public SignatureSignersState(boolean showMessage, List<SignerItem> signerItems, @StringRes int message) {
        this.showMessage = showMessage;
        this.signerItems = signerItems;
        this.message = message;
    }

    public boolean isShowMessage() {
        return showMessage;
    }

    public List<SignerItem> getSignerItems() {
        return signerItems;
    }

    public int getMessage() {
        return message;
    }
}
