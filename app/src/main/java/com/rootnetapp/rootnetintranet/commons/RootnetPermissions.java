package com.rootnetapp.rootnetintranet.commons;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.StringDef;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
        RootnetPermissions.INTRANET_BOOKING_VIEW_MY_BOOKINGS,
        RootnetPermissions.INTRANET_BOOKING_VIEW_ALL_BOOKINGS,
        RootnetPermissions.INTRANET_BOOKING_CREATE_BOOKING,
        RootnetPermissions.INTRANET_BOOKING_EDIT_BOOKING,
        RootnetPermissions.INTRANET_BOOKING_DELETE_BOOKING,
        RootnetPermissions.INTRANET_RESOURCING_VIEW,
        RootnetPermissions.INTRANET_BOOKING_VIEW_OWN_WEEK,
        RootnetPermissions.INTRANET_BOOKING_VIEW_BOOKING_COMMENT,
        RootnetPermissions.INTRANET_BOOKING_CRUD_OWN_BOOKING_COMMENT,
        RootnetPermissions.INTRANET_BOOKING_CRUD_ALL_BOOKING_COMMENT,
        RootnetPermissions.INTRANET_BOOKING_CREATE_OWN_BOOKING,
        RootnetPermissions.INTRANET_BOOKING_EDIT_OWN_BOOKING,
        RootnetPermissions.INTRANET_BOOKING_DELETE_OWN_BOOKING,
        RootnetPermissions.INTRANET_FEEDBACK_VIEW,
        RootnetPermissions.INTRANET_FEEDBACK_CRUD,
        RootnetPermissions.INTRANET_FEEDBACK_ACTION_VIEW,
        RootnetPermissions.INTRANET_FEEDBACK_ACTION_CRUD,
        RootnetPermissions.INTRANET_FEEDBACK_TAG_VIEW,
        RootnetPermissions.INTRANET_FEEDBACK_TAG_CRUD,
        RootnetPermissions.INTRANET_FEEDBACK_ACTION_TAG_VIEW,
        RootnetPermissions.INTRANET_FEEDBACK_ACTION_TAG_CRUD,
        RootnetPermissions.INTRANET_HOME_VIEW,
        RootnetPermissions.INTRANET_PEOPLE_EDIT,
        RootnetPermissions.INTRANET_PEOPLE_CRUD,
        RootnetPermissions.INTRANET_SCHEDULE_VIEW,
        RootnetPermissions.INTRANET_SCHEDULE_CRUD,
        RootnetPermissions.PROFILE_OWN_CRUD,
        RootnetPermissions.TEMPLATE_VIEW,
        RootnetPermissions.TEMPLATE_CRUD,
        RootnetPermissions.TEMPLATE_FILE_CREATE,
        RootnetPermissions.TIMELINE_VIEW,
        RootnetPermissions.TIMELINE_CRUD,
        RootnetPermissions.TIMELINE_CRUD_OWN,
        RootnetPermissions.WORKFLOW_CREATE,
        RootnetPermissions.WORKFLOW_DELETE,
        RootnetPermissions.WORKFLOW_DELETE_ALL,
        RootnetPermissions.WORKFLOW_MANAGE,
        RootnetPermissions.WORKFLOW_EXPORT,
        RootnetPermissions.WORKFLOW_ACTIVATE_ALL,
        RootnetPermissions.WORKFLOW_OPEN_ALL,
        RootnetPermissions.WORKFLOW_EDIT_OWN_COMMENT,
        RootnetPermissions.WORKFLOW_DELETE_OWN_COMMENT,
        RootnetPermissions.WORKFLOW_ADD_FILE,
        RootnetPermissions.WORKFLOW_INACTIVE_VIEW,
        RootnetPermissions.WORKFLOW_CLOSED_VIEW,
        RootnetPermissions.WORKFLOW_VIEW_ALL,
        RootnetPermissions.WORKFLOW_VIEW,
        RootnetPermissions.WORKFLOW_EDIT_OWN,
        RootnetPermissions.WORKFLOW_EDIT_ALL,
        RootnetPermissions.WORKFLOW_IMPORT,
        RootnetPermissions.WORKFLOW_FILE_VIEW,
        RootnetPermissions.WORKFLOW_FILE_CREATE,
        RootnetPermissions.WORKFLOW_FILE_CREATE_ALL,
        RootnetPermissions.WORKFLOW_FILE_DELETE,
        RootnetPermissions.WORKFLOW_TEMPLATE_VIEW,
        RootnetPermissions.WORKFLOW_TEMPLATE_CRUD,
        RootnetPermissions.WORKFLOW_TYPE_VIEW,
        RootnetPermissions.WORKFLOW_TYPE_CRUD,
        RootnetPermissions.WORKFLOW_TYPE_FIELD_CRUD,
        RootnetPermissions.WORKFLOW_TYPE_STATUS_CRUD,
        RootnetPermissions.WORKFLOW_COMMENT_VIEW,
        RootnetPermissions.WORKFLOW_COMMENT_CRUD_OWN,
        RootnetPermissions.WORKFLOW_COMMENT_CRUD_ALL,
        RootnetPermissions.WORKFLOW_STATUS_UPDATE_ALL,
        RootnetPermissions.WORKFLOW_COMMENT_PRIVATE_VIEW,
        RootnetPermissions.WORKFLOW_PERMISSION_CRUD,
        RootnetPermissions.WORKFLOW_CRUD_TAG,
        RootnetPermissions.WORKFLOW_CRUD_CATEGORY,
        RootnetPermissions.WORKFLOW_VIEW_ARCHIVE_FILE,
        RootnetPermissions.WORKFLOW_DEFINE_SPECIFIC,
        RootnetPermissions.WORKFLOW_VIEW_BY_INVOLVED,
        RootnetPermissions.WORKFLOW_EDIT_MY_OWN,
        RootnetPermissions.WORKFLOW_EDIT_BY_INVOLVED,
        RootnetPermissions.WORKFLOW_DELETE_APPROVAL_HISTORY,
        RootnetPermissions.WORKFLOW_UPDATE_APPROVAL_HISTORY,
        RootnetPermissions.WORKFLOW_VIEW_BY_PEOPLE_INVOLVED,
        RootnetPermissions.WORKFLOW_FILE_VIEW_BY_PEOPLE_INVOLVED,
        RootnetPermissions.WORKFLOW_COMMENT_BY_PEOPLE_INVOLVED
})
public @interface RootnetPermissions {

    //region Intranet
    //region Booking
    String INTRANET_BOOKING_VIEW_MY_BOOKINGS = "intranet_booking_view_my_bookings";
    String INTRANET_BOOKING_VIEW_ALL_BOOKINGS = "intranet_booking_view_all_bookings";
    String INTRANET_BOOKING_CREATE_BOOKING = "intranet_booking_create_booking";
    String INTRANET_BOOKING_EDIT_BOOKING = "intranet_booking_edit_booking";
    String INTRANET_BOOKING_DELETE_BOOKING = "intranet_booking_delete_booking";
    String INTRANET_RESOURCING_VIEW = "intranet_resourcing_view";
    String INTRANET_BOOKING_VIEW_OWN_WEEK = "intranet_booking_view_own_week";
    String INTRANET_BOOKING_VIEW_BOOKING_COMMENT = "intranet_booking_view_booking_comment";
    String INTRANET_BOOKING_CRUD_OWN_BOOKING_COMMENT = "intranet_booking_crud_own_booking_comment";
    String INTRANET_BOOKING_CRUD_ALL_BOOKING_COMMENT = "intranet_booking_crud_all_booking_comment";
    String INTRANET_BOOKING_CREATE_OWN_BOOKING = "intranet_booking_create_own_booking";
    String INTRANET_BOOKING_EDIT_OWN_BOOKING = "intranet_booking_edit_own_booking";
    String INTRANET_BOOKING_DELETE_OWN_BOOKING = "intranet_booking_delete_own_booking";
    //endregion
    //region Feedback
    String INTRANET_FEEDBACK_VIEW = "intranet_feedback_view";
    String INTRANET_FEEDBACK_CRUD = "intranet_feedback_crud";
    String INTRANET_FEEDBACK_ACTION_VIEW = "intranet_feedback_action_view";
    String INTRANET_FEEDBACK_ACTION_CRUD = "intranet_feedback_action_crud";
    String INTRANET_FEEDBACK_TAG_VIEW = "intranet_feedback_tag_view";
    String INTRANET_FEEDBACK_TAG_CRUD = "intranet_feedback_tag_crud";
    String INTRANET_FEEDBACK_ACTION_TAG_VIEW = "intranet_feedback_action_tag_view";
    String INTRANET_FEEDBACK_ACTION_TAG_CRUD = "intranet_feedback_action_tag_crud";
    //endregion
    //region Home
    String INTRANET_HOME_VIEW = "intranet_home_view";
    //endregion
    //region People
    String INTRANET_PEOPLE_EDIT = "intranet_people_edit";
    String INTRANET_PEOPLE_CRUD = "intranet_people_crud";
    //endregion
    //region Schedule
    String INTRANET_SCHEDULE_VIEW = "intranet_schedule_view";
    String INTRANET_SCHEDULE_CRUD = "intranet_schedule_crud";
    //endregion
    //endregion

    //region Profile
    String PROFILE_OWN_CRUD = "profile_own_crud";
    //endregion

    //region Template
    String TEMPLATE_VIEW = "template_view";
    String TEMPLATE_CRUD = "template_crud";
    String TEMPLATE_FILE_CREATE = "template_file_create";
    //endregion

    //region Timeline
    String TIMELINE_VIEW = "timeline_view";
    String TIMELINE_CRUD = "timeline_crud";
    String TIMELINE_CRUD_OWN = "timeline_crud_own";
    //endregion

    //region Workflow
    String WORKFLOW_CREATE = "workflow_create";
    String WORKFLOW_DELETE = "workflow_delete";
    String WORKFLOW_DELETE_ALL = "workflow_delete_all";
    String WORKFLOW_MANAGE = "workflow_manage";
    String WORKFLOW_EXPORT = "workflow_export";
    String WORKFLOW_ACTIVATE_ALL = "workflow_activate_all";
    String WORKFLOW_OPEN_ALL = "workflow_open_all";
    String WORKFLOW_EDIT_OWN_COMMENT = "workflow_edit_own_comment";
    String WORKFLOW_DELETE_OWN_COMMENT = "workflow_delete_own_comment";
    String WORKFLOW_ADD_FILE = "workflow_add_file";
    String WORKFLOW_INACTIVE_VIEW = "workflow_inactive_view";
    String WORKFLOW_CLOSED_VIEW = "workflow_closed_view";
    String WORKFLOW_VIEW_ALL = "workflow_view_all";
    String WORKFLOW_VIEW = "workflow_view";
    String WORKFLOW_EDIT_OWN = "workflow_edit_own";
    String WORKFLOW_EDIT_ALL = "workflow_edit_all";
    String WORKFLOW_IMPORT = "workflow_import";
    String WORKFLOW_FILE_VIEW = "workflow_file_view";
    String WORKFLOW_FILE_CREATE = "workflow_file_create";
    String WORKFLOW_FILE_CREATE_ALL = "workflow_file_create_all";
    String WORKFLOW_FILE_DELETE = "workflow_file_delete";
    String WORKFLOW_TEMPLATE_VIEW = "workflow_template_view";
    String WORKFLOW_TEMPLATE_CRUD = "workflow_template_crud";
    String WORKFLOW_TYPE_VIEW = "workflow_type_view";
    String WORKFLOW_TYPE_CRUD = "workflow_type_crud";
    String WORKFLOW_TYPE_FIELD_CRUD = "workflow_type_field_crud";
    String WORKFLOW_TYPE_STATUS_CRUD = "workflow_type_status_crud";
    String WORKFLOW_COMMENT_VIEW = "workflow_comment_view";
    String WORKFLOW_COMMENT_CRUD_OWN = "workflow_comment_crud_own";
    String WORKFLOW_COMMENT_CRUD_ALL = "workflow_comment_crud_all";
    String WORKFLOW_STATUS_UPDATE_ALL = "workflow_status_update_all";
    String WORKFLOW_COMMENT_PRIVATE_VIEW = "workflow_comment_private_view";
    String WORKFLOW_PERMISSION_CRUD = "workflow_permission_crud";
    String WORKFLOW_CRUD_TAG = "workflow_crud_tag";
    String WORKFLOW_CRUD_CATEGORY = "workflow_crud_category";
    String WORKFLOW_VIEW_ARCHIVE_FILE = "workflow_view_archive_file";
    String WORKFLOW_DEFINE_SPECIFIC = "workflow_define_specific";
    String WORKFLOW_VIEW_BY_INVOLVED = "workflow_view_by_involved";
    String WORKFLOW_EDIT_MY_OWN = "workflow_edit_my_own";
    String WORKFLOW_EDIT_BY_INVOLVED = "workflow_edit_by_involved";
    String WORKFLOW_DELETE_APPROVAL_HISTORY = "workflow_delete_approval_history";
    String WORKFLOW_UPDATE_APPROVAL_HISTORY = "workflow_update_approval_history";
    String WORKFLOW_VIEW_BY_PEOPLE_INVOLVED = "workflow_view_by_people_involved";
    String WORKFLOW_FILE_VIEW_BY_PEOPLE_INVOLVED = "workflow_file_view_by_people_involved";
    String WORKFLOW_COMMENT_BY_PEOPLE_INVOLVED = "workflow_comment_by_people_involved";
    //endregion
}
