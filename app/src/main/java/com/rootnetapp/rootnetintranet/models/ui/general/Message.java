package com.rootnetapp.rootnetintranet.models.ui.general;

import androidx.annotation.StringRes;

public class Message {
    @StringRes
    public int titleRes;
    @StringRes
    public int messageRes;

    public Message(@StringRes int titleRes, @StringRes int messageRes) {
        this.titleRes = titleRes;
        this.messageRes = messageRes;
    }

    public Message(@StringRes int messageRes) {
        this.messageRes = messageRes;
    }
}
