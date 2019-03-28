package com.rootnetapp.rootnetintranet.models.responses.contact;

import com.squareup.moshi.Json;

public class Contact {

    @Json(name = "Id")
    private Integer id;
    @Json(name = "Active")
    private Boolean active;
    @Json(name = "subContact")
    private Integer subContact;
    @Json(name = "ContactTypeId")
    private Integer contactTypeId;
    @Json(name = "Favorite")
    private Boolean favorite;
    @Json(name = "Author")
    private String author;
    @Json(name = "name")
    private String name;
    @Json(name = "last_name")
    private String lastName;
    @Json(name = "full_name")
    private String fullName;
    @Json(name = "company")
    private String company;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getSubContact() {
        return subContact;
    }

    public void setSubContact(Integer subContact) {
        this.subContact = subContact;
    }

    public Integer getContactTypeId() {
        return contactTypeId;
    }

    public void setContactTypeId(Integer contactTypeId) {
        this.contactTypeId = contactTypeId;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

}