package com.rootnetapp.rootnetintranet.data.local.db.profile;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.rootnetapp.rootnetintranet.data.local.db.profile.workflowdetail.ProfileInvolved;

import java.util.List;

@Dao
public interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertProfiles(List<Profile> profiles);

    @Query("DELETE FROM profile")
    public int deleteAllProfiles();

    @Query("SELECT full_name, picture FROM profile WHERE id = :id")
    public ProfileInvolved getProfilesInvolved(int id);

    // TODO check which full name column naming to keep full_name or fullName because it is causing some issues with some other models and queries.
//    @Query("SELECT id, full_name, picture FROM profile")
//    public List<FormCreateProfile> getAllProfiles();
}
