package com.rootnetapp.rootnetintranet.ui.workflowlist;

public class Sort {
    public enum sortType {
        NONE,
        BYNUMBER,
        BYCREATE,
        BYUPDATE
    }

    public enum sortOrder {
        ASC,
        DESC
    }

    private sortType sortingType;
    private sortOrder numberSortOrder;
    private sortOrder createdSortOrder;
    private sortOrder updatedSortOrder;

    public Sort() {
        this.sortingType = sortType.NONE;
        this.numberSortOrder = sortOrder.DESC;
        this.createdSortOrder = sortOrder.DESC;
        this.updatedSortOrder = sortOrder.DESC;
    }

    public sortType getSortingType() {
        return sortingType;
    }

    public void setSortingType(sortType sortingType) {
        this.sortingType = sortingType;
    }

    public sortOrder getNumberSortOrder() {
        return numberSortOrder;
    }

    public void setNumberSortOrder(sortOrder numberSortOrder) {
        this.numberSortOrder = numberSortOrder;
    }

    public sortOrder getCreatedSortOrder() {
        return createdSortOrder;
    }

    public void setCreatedSortOrder(sortOrder createdSortOrder) {
        this.createdSortOrder = createdSortOrder;
    }

    public sortOrder getUpdatedSortOrder() {
        return updatedSortOrder;
    }

    public void setUpdatedSortOrder(sortOrder updatedSortOrder) {
        this.updatedSortOrder = updatedSortOrder;
    }
}
