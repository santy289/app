package com.rootnetapp.rootnetintranet.models.responses.country;

import com.squareup.moshi.Json;

public class Country {

    @Json(name = "country_id")
    private int countryId;
    @Json(name = "description")
    private String description;
    @Json(name = "currency")
    private String currency;
    @Json(name = "currency_symbol")
    private String currencySymbol;
    @Json(name = "phone_code")
    private String phoneCode;
    @Json(name = "alpha-2")
    private String alpha2;
    @Json(name = "alpha-3")
    private String alpha3;
    @Json(name = "isoNumeric")
    private int isoNumeric;

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getAlpha2() {
        return alpha2;
    }

    public void setAlpha2(String alpha2) {
        this.alpha2 = alpha2;
    }

    public String getAlpha3() {
        return alpha3;
    }

    public void setAlpha3(String alpha3) {
        this.alpha3 = alpha3;
    }

    public int getIsoNumeric() {
        return isoNumeric;
    }

    public void setIsoNumeric(int isoNumeric) {
        this.isoNumeric = isoNumeric;
    }

}