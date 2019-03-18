package com.rootnetapp.rootnetintranet.commons;

import android.text.TextUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import androidx.annotation.StringDef;

public class RootnetPermissionsUtils {

    //region Permissions
    //region Intranet
    //region Booking
    public static final String INTRANET_BOOKING_VIEW_MY_BOOKINGS = "intranet_booking_view_my_bookings";
    public static final String INTRANET_BOOKING_VIEW_ALL_BOOKINGS = "intranet_booking_view_all_bookings";
    public static final String INTRANET_BOOKING_CREATE_BOOKING = "intranet_booking_create_booking";
    public static final String INTRANET_BOOKING_EDIT_BOOKING = "intranet_booking_edit_booking";
    public static final String INTRANET_BOOKING_DELETE_BOOKING = "intranet_booking_delete_booking";
    public static final String INTRANET_RESOURCING_VIEW = "intranet_resourcing_view";
    public static final String INTRANET_BOOKING_VIEW_OWN_WEEK = "intranet_booking_view_own_week";
    public static final String INTRANET_BOOKING_VIEW_BOOKING_COMMENT = "intranet_booking_view_booking_comment";
    public static final String INTRANET_BOOKING_CRUD_OWN_BOOKING_COMMENT = "intranet_booking_crud_own_booking_comment";
    public static final String INTRANET_BOOKING_CRUD_ALL_BOOKING_COMMENT = "intranet_booking_crud_all_booking_comment";
    public static final String INTRANET_BOOKING_CREATE_OWN_BOOKING = "intranet_booking_create_own_booking";
    public static final String INTRANET_BOOKING_EDIT_OWN_BOOKING = "intranet_booking_edit_own_booking";
    public static final String INTRANET_BOOKING_DELETE_OWN_BOOKING = "intranet_booking_delete_own_booking";
    //endregion
    //region Feedback
    public static final String INTRANET_FEEDBACK_VIEW = "intranet_feedback_view";
    public static final String INTRANET_FEEDBACK_CRUD = "intranet_feedback_crud";
    public static final String INTRANET_FEEDBACK_ACTION_VIEW = "intranet_feedback_action_view";
    public static final String INTRANET_FEEDBACK_ACTION_CRUD = "intranet_feedback_action_crud";
    public static final String INTRANET_FEEDBACK_TAG_VIEW = "intranet_feedback_tag_view";
    public static final String INTRANET_FEEDBACK_TAG_CRUD = "intranet_feedback_tag_crud";
    public static final String INTRANET_FEEDBACK_ACTION_TAG_VIEW = "intranet_feedback_action_tag_view";
    public static final String INTRANET_FEEDBACK_ACTION_TAG_CRUD = "intranet_feedback_action_tag_crud";
    //endregion
    //region Home
    public static final String INTRANET_HOME_VIEW = "intranet_home_view";
    //endregion
    //region People
    public static final String INTRANET_PEOPLE_EDIT = "intranet_people_edit";
    public static final String INTRANET_PEOPLE_CRUD = "intranet_people_crud";
    //endregion
    //region Schedule
    public static final String INTRANET_SCHEDULE_VIEW = "intranet_schedule_view";
    public static final String INTRANET_SCHEDULE_CRUD = "intranet_schedule_crud";
    //endregion
    //endregion

    //region Profile
    public static final String PROFILE_OWN_CRUD = "profile_own_crud";
    //endregion

    //region Template
    public static final String TEMPLATE_VIEW = "template_view";
    public static final String TEMPLATE_CRUD = "template_crud";
    public static final String TEMPLATE_FILE_CREATE = "template_file_create";
    //endregion

    //region Timeline
    public static final String TIMELINE_VIEW = "timeline_view";
    public static final String TIMELINE_CRUD = "timeline_crud";
    public static final String TIMELINE_CRUD_OWN = "timeline_crud_own";
    //endregion

    //region Workflow
    /**
     * Determines the ability to create a new workflow.
     */
    public static final String WORKFLOW_CREATE = "workflow_create";
    /**
     * Determines the ability to delete a workflow.
     */
    public static final String WORKFLOW_DELETE = "workflow_delete";
    /**
     * Determines the ability to bulk delete workflows.
     */
    public static final String WORKFLOW_DELETE_ALL = "workflow_delete_all";
    /**
     * Determines the ability to create, update and delete all workflows.
     */
    public static final String WORKFLOW_MANAGE = "workflow_manage";
    /**
     * Determines the ability to export a workflow from the platform.
     */
    public static final String WORKFLOW_EXPORT = "workflow_export";
    /**
     * Determines the ability to activate a workflow.
     */
    public static final String WORKFLOW_ACTIVATE_ALL = "workflow_activate_all";
    /**
     * Determines the ability to open and close a workflow.
     */
    public static final String WORKFLOW_OPEN_ALL = "workflow_open_all";
    /**
     * Determines the ability to view an inactive workflow.
     */
    public static final String WORKFLOW_INACTIVE_VIEW = "workflow_inactive_view";
    /**
     * Determines the ability to view a closed workflow.
     */
    public static final String WORKFLOW_CLOSED_VIEW = "workflow_closed_view";
    /**
     * Determines the ability to view a list of workflows.
     */
    public static final String WORKFLOW_VIEW_ALL = "workflow_view_all";
    /**
     * Determines the ability to view the workflow details.
     */
    public static final String WORKFLOW_VIEW = "workflow_view";
    /**
     * Determines the ability to import workflows to the platform.
     */
    public static final String WORKFLOW_IMPORT = "workflow_import";
    //region Files
    /**
     * Determines the ability to view a workflow file.
     */
    public static final String WORKFLOW_FILE_VIEW = "workflow_file_view";
    /**
     * Determines the ability to upload files to a workflow.
     */
    public static final String WORKFLOW_FILE_CREATE = "workflow_file_create";
    /**
     * Determines the ability to upload files to all workflows.
     */
    public static final String WORKFLOW_FILE_CREATE_ALL = "workflow_file_create_all";
    /**
     * Determines the ability to delete a workflow file.
     */
    public static final String WORKFLOW_FILE_DELETE = "workflow_file_delete";
    /**
     * Determines the ability to view the files of the workflows where the user is part of the people involved group.
     */
    public static final String WORKFLOW_FILE_VIEW_BY_PEOPLE_INVOLVED = "workflow_file_view_by_people_involved";
    /**
     * Determines the ability to view all archived files of the workflows the user has access to.
     */
    public static final String WORKFLOW_VIEW_ARCHIVE_FILE = "workflow_view_archive_file";
    /**
     * Determines the ability to view the workflow import templates.
     */
    public static final String WORKFLOW_TEMPLATE_VIEW = "workflow_template_view";
    /**
     * Determines the ability to create, edit and delete the workflow import templates.
     */
    public static final String WORKFLOW_TEMPLATE_CRUD = "workflow_template_crud";
    //endregion
    //region Workflow Type
    /**
     * Determines the ability to view what type of workflow each one is.
     */
    public static final String WORKFLOW_TYPE_VIEW = "workflow_type_view";
    /**
     * Determines the ability to create, edit and view the workflow types.
     */
    public static final String WORKFLOW_TYPE_CRUD = "workflow_type_crud";
    /**
     * Determines the ability to create, edit and view the workflow type fields.
     */
    public static final String WORKFLOW_TYPE_FIELD_CRUD = "workflow_type_field_crud";
    /**
     * Determines the ability to create, edit, view and delete all workflow type status.
     */
    public static final String WORKFLOW_TYPE_STATUS_CRUD = "workflow_type_status_crud";
    //endregion
    //region Comments
    /**
     * Determines the ability to edit the user's workflow comments.
     */
    public static final String WORKFLOW_EDIT_OWN_COMMENT = "workflow_edit_own_comment";
    /**
     * Determines the ability to delete the user's workflow comments.
     */
    public static final String WORKFLOW_DELETE_OWN_COMMENT = "workflow_delete_own_comment";
    /**
     * Determines the ability to view the comments of a workflow.
     */
    public static final String WORKFLOW_COMMENT_VIEW = "workflow_comment_view";
    /**
     * Determines the ability to create, edit and delete the comments of the user's workflows.
     */
    public static final String WORKFLOW_COMMENT_CRUD_OWN = "workflow_comment_crud_own";
    /**
     * Determines the ability to create, edit and delete the comments of all workflows.
     */
    public static final String WORKFLOW_COMMENT_CRUD_ALL = "workflow_comment_crud_all";
    /**
     * Determines the ability to view the private comments of a workflow.
     */
    public static final String WORKFLOW_COMMENT_PRIVATE_VIEW = "workflow_comment_private_view";
    /**
     * Determines the ability to comment on the workflows that the user is part of the people
     * involved group.
     */
    public static final String WORKFLOW_COMMENT_BY_PEOPLE_INVOLVED = "workflow_comment_by_people_involved";
    //endregion
    /**
     * Determines the ability to update the current status of all workflows.
     */
    public static final String WORKFLOW_STATUS_UPDATE_ALL = "workflow_status_update_all";
    /**
     * Determines the ability to manage each user;s access to all workflow actions.
     */
    public static final String WORKFLOW_PERMISSION_CRUD = "workflow_permission_crud";
    /**
     * Determines the ability to update workflow tag related settings.
     */
    public static final String WORKFLOW_CRUD_TAG = "workflow_crud_tag";
    /**
     * Determines the ability to update workflow category related settings.
     */
    public static final String WORKFLOW_CRUD_CATEGORY = "workflow_crud_category";
    /**
     * Determines the ability to define the specific approvers when creating a workflow.
     */
    public static final String WORKFLOW_DEFINE_SPECIFIC = "workflow_define_specific";
    /**
     * Determines the ability to view the workflow when the user is part of the workflow's people
     * involved group.
     */
    public static final String WORKFLOW_VIEW_BY_INVOLVED = "workflow_view_by_involved";
    /**
     * Related to the general ability of workflow editing. This is tied to the workflow type.
     * Normally, if a user has the permission to edit workflows, they are limited to certain
     * workflow types.
     */
    public static final String WORKFLOW_EDIT_OWN = "workflow_edit_own";
    /**
     * Determines the ability to edit the user's own created workflows.
     */
    public static final String WORKFLOW_EDIT_MY_OWN = "workflow_edit_my_own";
    /**
     * Determines the ability to edit any workflow there is.
     */
    public static final String WORKFLOW_EDIT_ALL = "workflow_edit_all";
    /**
     * Determines the ability to edit the workflow when the user is part of the workflow's people
     * involved group.
     */
    public static final String WORKFLOW_EDIT_BY_INVOLVED = "workflow_edit_by_involved";
    /**
     * Determines the ability to delete all workflows' approval history.
     */
    public static final String WORKFLOW_DELETE_APPROVAL_HISTORY = "workflow_delete_approval_history";
    /**
     * Determines the ability to update all workflows' approval history.
     */
    public static final String WORKFLOW_UPDATE_APPROVAL_HISTORY = "workflow_update_approval_history";
    /**
     * Determines the ability to view the workflows where the user is part of the people involved group.
     */
    public static final String WORKFLOW_VIEW_BY_PEOPLE_INVOLVED = "workflow_view_by_people_involved";
    //endregion
    //endregion

    private static final String SEPARATOR = ",";

    private List<String> mUserPermissionsList;

    public RootnetPermissionsUtils(String userPermissions) {
        if (TextUtils.isEmpty(userPermissions)) {
            mUserPermissionsList = new ArrayList<>();
            return;
        }

        String[] split = userPermissions.split(SEPARATOR);
        mUserPermissionsList = Arrays.asList(split);
    }

    //region Permission Validation Methods
    public boolean hasPermission(@RootnetPermission String permissionToCheck) {
        return mUserPermissionsList.contains(permissionToCheck);
    }

    public boolean hasPermissions(@RootnetPermission String[] permissionsToCheck) {
        return mUserPermissionsList.containsAll(Arrays.asList(permissionsToCheck));
    }

    public boolean hasPermissions(@RootnetPermission List<String> permissionsToCheck) {
        return mUserPermissionsList.containsAll(permissionsToCheck);
    }
    //endregion

    //region Static Methods
    public static String getPermissionsStringFromMap(Map<String, Object> permissionsMap) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String key : permissionsMap.keySet()) {
            stringBuilder.append(key); //permission value
            stringBuilder.append(SEPARATOR); //separator
        }

        stringBuilder.deleteCharAt(stringBuilder.length() - 1); //delete last separator

        return stringBuilder.toString();
    }
    //endregion

    //region Interface
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            INTRANET_BOOKING_VIEW_MY_BOOKINGS,
            INTRANET_BOOKING_VIEW_ALL_BOOKINGS,
            INTRANET_BOOKING_CREATE_BOOKING,
            INTRANET_BOOKING_EDIT_BOOKING,
            INTRANET_BOOKING_DELETE_BOOKING,
            INTRANET_RESOURCING_VIEW,
            INTRANET_BOOKING_VIEW_OWN_WEEK,
            INTRANET_BOOKING_VIEW_BOOKING_COMMENT,
            INTRANET_BOOKING_CRUD_OWN_BOOKING_COMMENT,
            INTRANET_BOOKING_CRUD_ALL_BOOKING_COMMENT,
            INTRANET_BOOKING_CREATE_OWN_BOOKING,
            INTRANET_BOOKING_EDIT_OWN_BOOKING,
            INTRANET_BOOKING_DELETE_OWN_BOOKING,
            INTRANET_FEEDBACK_VIEW,
            INTRANET_FEEDBACK_CRUD,
            INTRANET_FEEDBACK_ACTION_VIEW,
            INTRANET_FEEDBACK_ACTION_CRUD,
            INTRANET_FEEDBACK_TAG_VIEW,
            INTRANET_FEEDBACK_TAG_CRUD,
            INTRANET_FEEDBACK_ACTION_TAG_VIEW,
            INTRANET_FEEDBACK_ACTION_TAG_CRUD,
            INTRANET_HOME_VIEW,
            INTRANET_PEOPLE_EDIT,
            INTRANET_PEOPLE_CRUD,
            INTRANET_SCHEDULE_VIEW,
            INTRANET_SCHEDULE_CRUD,
            PROFILE_OWN_CRUD,
            TEMPLATE_VIEW,
            TEMPLATE_CRUD,
            TEMPLATE_FILE_CREATE,
            TIMELINE_VIEW,
            TIMELINE_CRUD,
            TIMELINE_CRUD_OWN,
            WORKFLOW_CREATE,
            WORKFLOW_DELETE,
            WORKFLOW_DELETE_ALL,
            WORKFLOW_MANAGE,
            WORKFLOW_EXPORT,
            WORKFLOW_ACTIVATE_ALL,
            WORKFLOW_OPEN_ALL,
            WORKFLOW_EDIT_OWN_COMMENT,
            WORKFLOW_DELETE_OWN_COMMENT,
            WORKFLOW_INACTIVE_VIEW,
            WORKFLOW_CLOSED_VIEW,
            WORKFLOW_VIEW_ALL,
            WORKFLOW_VIEW,
            WORKFLOW_EDIT_OWN,
            WORKFLOW_EDIT_ALL,
            WORKFLOW_IMPORT,
            WORKFLOW_FILE_VIEW,
            WORKFLOW_FILE_CREATE,
            WORKFLOW_FILE_CREATE_ALL,
            WORKFLOW_FILE_DELETE,
            WORKFLOW_TEMPLATE_VIEW,
            WORKFLOW_TEMPLATE_CRUD,
            WORKFLOW_TYPE_VIEW,
            WORKFLOW_TYPE_CRUD,
            WORKFLOW_TYPE_FIELD_CRUD,
            WORKFLOW_TYPE_STATUS_CRUD,
            WORKFLOW_COMMENT_VIEW,
            WORKFLOW_COMMENT_CRUD_OWN,
            WORKFLOW_COMMENT_CRUD_ALL,
            WORKFLOW_STATUS_UPDATE_ALL,
            WORKFLOW_COMMENT_PRIVATE_VIEW,
            WORKFLOW_PERMISSION_CRUD,
            WORKFLOW_CRUD_TAG,
            WORKFLOW_CRUD_CATEGORY,
            WORKFLOW_VIEW_ARCHIVE_FILE,
            WORKFLOW_DEFINE_SPECIFIC,
            WORKFLOW_VIEW_BY_INVOLVED,
            WORKFLOW_EDIT_MY_OWN,
            WORKFLOW_EDIT_BY_INVOLVED,
            WORKFLOW_DELETE_APPROVAL_HISTORY,
            WORKFLOW_UPDATE_APPROVAL_HISTORY,
            WORKFLOW_VIEW_BY_PEOPLE_INVOLVED,
            WORKFLOW_FILE_VIEW_BY_PEOPLE_INVOLVED,
            WORKFLOW_COMMENT_BY_PEOPLE_INVOLVED
    })
    public @interface RootnetPermission {
    }
    //endregion
}
