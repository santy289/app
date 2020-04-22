package com.rootnetapp.rootnetintranet.ui.resourcing.planner.models;

import com.rootnetapp.rootnetintranet.models.responses.resourcing.Booking;

import java.util.List;

/**
 * Created by ldemorais on 4/21/20. ldemorais@hypernovalabs.com
 */
public class PersonBooking {
    private Integer personId;
    private String personName;
    private String personAvatar;

    public PersonBooking(int personId, String personName) {
        this.personId = personId;
        this.personName = personName;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonAvatar() {
        return personAvatar;
    }

    public void setPersonAvatar(String personAvatar) {
        this.personAvatar = personAvatar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonBooking that = (PersonBooking) o;

        if (personId != null ? !personId.equals(that.personId) : that.personId != null)
            return false;
        return personName != null ? personName.equals(that.personName) : that.personName == null;
    }

    @Override
    public int hashCode() {
        int result = personId != null ? personId.hashCode() : 0;
        result = 31 * result + (personName != null ? personName.hashCode() : 0);
        return result;
    }
}
