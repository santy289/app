package com.rootnetapp.rootnetintranet.models.responses.domain;

import com.squareup.moshi.Json;

import java.util.List;

public class Client {

    @Json(name = "id")
    private int id;
    @Json(name = "name")
    private String name;
    @Json(name = "client_hash")
    private String clientHash;
    @Json(name = "logo_url")
    private String logoUrl;
    @Json(name = "domain")
    private String domain;
    @Json(name = "sub_domain")
    private String subDomain;
    @Json(name = "api_url")
    private String apiUrl;
    @Json(name = "contact_email")
    private String contactEmail;
    @Json(name = "contact_name")
    private String contactName;
    @Json(name = "enabled")
    private boolean enabled;
    @Json(name = "country")
    private Country country;
    @Json(name = "package")
    private Package _package;
    @Json(name = "products")
    private List<Product> products;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClientHash() {
        return clientHash;
    }

    public void setClientHash(String clientHash) {
        this.clientHash = clientHash;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSubDomain() {
        return subDomain;
    }

    public void setSubDomain(String subDomain) {
        this.subDomain = subDomain;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Package getPackage() {
        return _package;
    }

    public void setPackage(Package _package) {
        this._package = _package;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

}