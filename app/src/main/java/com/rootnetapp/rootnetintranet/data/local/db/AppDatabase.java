package com.rootnetapp.rootnetintranet.data.local.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

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
        Field.class
}, version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract WorkflowDao workflowDao();
    public abstract WorkflowDbDao workflowDbDao();
    public abstract WorkflowTypeDbDao workflowTypeDbDao();
}
