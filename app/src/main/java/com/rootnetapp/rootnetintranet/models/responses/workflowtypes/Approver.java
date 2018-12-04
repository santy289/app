package com.rootnetapp.rootnetintranet.models.responses.workflowtypes;

import com.squareup.moshi.Json;

public class Approver {
    @Json(name = "entity_type")
    public String entityType;

    @Json(name = "entity_id")
    public int entityId;

    @Json(name = "id")
    public int id;

    @Json(name = "can_change_mind")
    public boolean canChangeMind;

    @Json(name = "is_active")
    public boolean isActive;

    @Json(name = "is_require")
    public boolean isRequire;

    @Json(name = "status_id")
    public int statusId;

    @Json(name = "entity_name")
    public String entityName;

    @Json(name = "entity_avatar")
    public String entityAvatar;

    public boolean approved;

}
