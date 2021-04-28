package com.rootnetapp.rootnetintranet.models.ui.general;

import androidx.annotation.IdRes;

public class DialogBoxState {
    @IdRes private int title;
    @IdRes private int message;
    @IdRes private int negative;
    @IdRes private int positive;
    private boolean showNegative;

    public DialogBoxState(int title, int message, int negative, int positive, boolean showNegative) {
        this.title = title;
        this.message = message;
        this.negative = negative;
        this.positive = positive;
        this.showNegative = showNegative;
    }

    public boolean isShowNegative() {
        return showNegative;
    }

    public int getTitle() {
        return title;
    }

    public int getMessage() {
        return message;
    }

    public int getNegative() {
        return negative;
    }

    public int getPositive() {
        return positive;
    }
}
