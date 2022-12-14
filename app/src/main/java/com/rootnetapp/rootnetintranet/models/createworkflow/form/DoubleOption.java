package com.rootnetapp.rootnetintranet.models.createworkflow.form;

import java.util.Locale;

import androidx.annotation.NonNull;

public class DoubleOption implements BaseOption {

    private Option firstOption;
    private Option secondOption;

    public DoubleOption(Option firstOption, Option secondOption) {
        this.firstOption = firstOption;
        this.secondOption = secondOption;
    }

    public Option getFirstOption() {
        return firstOption;
    }

    public void setFirstOption(Option firstOption) {
        this.firstOption = firstOption;
    }

    public Option getSecondOption() {
        return secondOption;
    }

    public void setSecondOption(
            Option secondOption) {
        this.secondOption = secondOption;
    }

    @NonNull
    @Override
    public String toString() {
        String firstOptionName = getFirstOption() == null || getFirstOption()
                .getName() == null ? "" : getFirstOption().getName();
        String secondOptionName = getSecondOption() == null || getSecondOption()
                .getName() == null ? "" : getSecondOption().getName();

        if (!secondOptionName.isEmpty()) {
            secondOptionName = String.format(Locale.US, "(%s)", secondOptionName);
        }

        return String.format(Locale.US, "%s %s", firstOptionName, secondOptionName).trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DoubleOption that = (DoubleOption) o;

        if (!firstOption.equals(that.firstOption)) return false;
        return secondOption.equals(that.secondOption);
    }

    @Override
    public int hashCode() {
        int result = firstOption.hashCode();
        result = 31 * result + secondOption.hashCode();
        return result;
    }
}
