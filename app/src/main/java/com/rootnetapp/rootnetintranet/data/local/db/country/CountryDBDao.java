package com.rootnetapp.rootnetintranet.data.local.db.country;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.rootnetapp.rootnetintranet.data.local.db.profile.Profile;
import com.rootnetapp.rootnetintranet.data.local.db.profile.forms.FormCreateProfile;
import com.rootnetapp.rootnetintranet.models.createworkflow.PhoneFieldData;

import java.util.List;

@Dao
public interface CountryDBDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertCountryList(List<CountryDB> countryList);

    @Query("DELETE FROM countrydb")
    public int deleteAllCountries();

    @Query("SELECT country_id, phone_code FROM countrydb WHERE country_id = :id")
    public List<PhoneFieldData> getCountryId(int id);

}
