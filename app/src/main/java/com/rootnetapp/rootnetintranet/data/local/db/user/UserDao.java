package com.rootnetapp.rootnetintranet.data.local.db.user;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.rootnetapp.rootnetintranet.data.local.db.profile.forms.FormCreateProfile;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE id = :userId")
    User getUserById(int userId);

    @Query("SELECT id, fullName, picture, username, email FROM user")
    public List<FormCreateProfile> getAllProfiles();

//todo maybe remove
    @Query("UPDATE user SET fullName = :userFullname, email = :userEmail," +
            " phoneNumber = :userPhone  WHERE id = :userId")
    int editUser(int userId, String userFullname, String userEmail, String userPhone);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<User> users);

    @Delete
    void delete(User user);


    @Query("DELETE FROM user")
    int clearUser();

}
