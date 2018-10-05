package com.rootnetapp.rootnetintranet.data.local.db.country;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.rootnetapp.rootnetintranet.data.local.db.profile.Profile;
import com.rootnetapp.rootnetintranet.data.local.db.profile.forms.FormCreateProfile;
import com.rootnetapp.rootnetintranet.models.createworkflow.CurrencyFieldData;
import com.rootnetapp.rootnetintranet.models.createworkflow.PhoneFieldData;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Dao
public interface CountryDBDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertCountryList(List<CountryDB> countryList);

    @Query("DELETE FROM countrydb")
    public int deleteAllCountries();

    @Query("SELECT country_id, description, phone_code FROM countrydb")
    public Single<List<PhoneFieldData>> getAllCountriesCodes();

    @Query("SELECT country_id, description, currency FROM countrydb")
    public Single<List<CurrencyFieldData>> getAllCurrencyCodes();

    @Query("SELECT country_id, description, phone_code FROM countrydb WHERE country_id = :id")
    public Single<PhoneFieldData> getCountryCodeBy(int id);

}
