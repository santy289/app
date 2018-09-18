package com.rootnetapp.rootnetintranet.data.local.db.profile;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.rootnetapp.rootnetintranet.data.local.db.profile.forms.FormCreateProfile;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;

import java.util.List;

@Dao
public interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertProfiles(List<Profile> profiles);

    @Query("DELETE FROM profile")
    public int deleteAllProfiles();

    @Query("SELECT id, full_name, picture FROM profile")
    public List<FormCreateProfile> getAllProfiles();
}
