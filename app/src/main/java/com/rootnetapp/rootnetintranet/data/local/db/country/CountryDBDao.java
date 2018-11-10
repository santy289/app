package com.rootnetapp.rootnetintranet.data.local.db.country;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.rootnetapp.rootnetintranet.models.createworkflow.CurrencyFieldData;
import com.rootnetapp.rootnetintranet.models.createworkflow.PhoneFieldData;

import java.util.List;

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
