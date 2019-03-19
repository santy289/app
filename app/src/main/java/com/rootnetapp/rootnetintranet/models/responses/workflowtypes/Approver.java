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

    public Boolean approved;
    public boolean isGlobal;
    public boolean isStatusSpecific;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Approver approver = (Approver) o;

        if (entityId != approver.entityId) return false;
        if (entityType != null ? !entityType
                .equals(approver.entityType) : approver.entityType != null) {
            return false;
        }
        return entityName != null ? entityName
                .equals(approver.entityName) : approver.entityName == null;
    }

    @Override
    public int hashCode() {
        int result = entityType != null ? entityType.hashCode() : 0;
        result = 31 * result + entityId;
        result = 31 * result + (entityName != null ? entityName.hashCode() : 0);
        return result;
    }
}
