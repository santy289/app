package com.rootnetapp.rootnetintranet.data.local.db.user;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Propietario on 14/03/2018.
 */

@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE id = :userId")
    User getUserById(int userId);

//todo maybe remove
    @Query("UPDATE user SET fullName = :userFullname, email = :userEmail," +
            " phoneNumber = :userPhone  WHERE id = :userId")
    int editUser(int userId, String userFullname, String userEmail, String userPhone);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<User> users);

    @Delete
    void delete(User user);

    @Query("DELETE FROM user")
    int deleteAll();

}
