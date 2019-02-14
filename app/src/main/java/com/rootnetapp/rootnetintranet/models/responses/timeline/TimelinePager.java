package com.rootnetapp.rootnetintranet.models.responses.timeline;

import com.squareup.moshi.Json;

public class TimelinePager {

    @Json(name = "paginate")
    private Boolean paginate;
    @Json(name = "first_page")
    private Integer firstPage;
    @Json(name = "last_page")
    private Integer lastPage;
    @Json(name = "is_last_page")
    private Boolean isLastPage;
    @Json(name = "is_first_page")
    private Boolean isFirstPage;
    @Json(name = "first_index")
    private Integer firstIndex;
    @Json(name = "last_index")
    private Integer lastIndex;
    @Json(name = "next_page")
    private Integer nextPage;
    @Json(name = "prev_page")
    private Integer prevPage;
    @Json(name = "current_page")
    private Integer currentPage;
    @Json(name = "count")
    private Integer count;
    @Json(name = "limit")
    private Integer limit;
    @Json(name = "query")
    private String query;

    public Boolean getPaginate() {
        return paginate;
    }

    public void setPaginate(Boolean paginate) {
        this.paginate = paginate;
    }

    public Integer getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(Integer firstPage) {
        this.firstPage = firstPage;
    }

    public Integer getLastPage() {
        return lastPage;
    }

    public void setLastPage(Integer lastPage) {
        this.lastPage = lastPage;
    }

    public Boolean getIsLastPage() {
        return isLastPage;
    }

    public void setIsLastPage(Boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    public Boolean getIsFirstPage() {
        return isFirstPage;
    }

    public void setIsFirstPage(Boolean isFirstPage) {
        this.isFirstPage = isFirstPage;
    }

    public Integer getFirstIndex() {
        return firstIndex;
    }

    public void setFirstIndex(Integer firstIndex) {
        this.firstIndex = firstIndex;
    }

    public Integer getLastIndex() {
        return lastIndex;
    }

    public void setLastIndex(Integer lastIndex) {
        this.lastIndex = lastIndex;
    }

    public Integer getNextPage() {
        return nextPage;
    }

    public void setNextPage(Integer nextPage) {
        this.nextPage = nextPage;
    }

    public Integer getPrevPage() {
        return prevPage;
    }

    public void setPrevPage(Integer prevPage) {
        this.prevPage = prevPage;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

}