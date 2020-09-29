package com.rootnetapp.rootnetintranet.data.local.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.rootnetapp.rootnetintranet.data.local.db.country.CountryDB;
import com.rootnetapp.rootnetintranet.data.local.db.country.CountryDBDao;
import com.rootnetapp.rootnetintranet.data.local.db.profile.Profile;
import com.rootnetapp.rootnetintranet.data.local.db.profile.ProfileDao;
import com.rootnetapp.rootnetintranet.data.local.db.signature.TemplateSignature;
import com.rootnetapp.rootnetintranet.data.local.db.signature.TemplateSignatureDao;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDbDao;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.Field;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDb;
import com.rootnetapp.rootnetintranet.data.local.db.workflowtype.WorkflowTypeDbDao;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.data.local.db.user.UserDao;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.Workflow;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDao;

@Database(entities = {
        User.class,
        Workflow.class,
        WorkflowDb.class,
        WorkflowTypeDb.class,
        Field.class,
        Profile.class,
        CountryDB.class,
        TemplateSignature.class
}, version = 7, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract WorkflowDao workflowDao();
    public abstract WorkflowDbDao workflowDbDao();
    public abstract WorkflowTypeDbDao workflowTypeDbDao();
    public abstract ProfileDao profileDao();
    public abstract CountryDBDao countryDBDao();
    public abstract TemplateSignatureDao templateSignatureDao();
}
