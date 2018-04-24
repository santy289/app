package com.rootnetapp.rootnetintranet.models.responses.user;

import com.squareup.moshi.Json;

/**
 * Created by Propietario on 14/03/2018.
 */

public class Pager {

    @Json(name = "paginate")
    private boolean paginate;
    @Json(name = "first_page")
    private int firstPage;
    @Json(name = "last_page")
    private int lastPage;
    @Json(name = "is_last_page")
    private boolean isLastPage;
    @Json(name = "is_first_page")
    private boolean isFirstPage;
    @Json(name = "first_index")
    private int firstIndex;
    @Json(name = "last_index")
    private int lastIndex;
    @Json(name = "next_page")
    private int nextPage;
    @Json(name = "prev_page")
    private int prevPage;
    @Json(name = "current_page")
    private int currentPage;
    @Json(name = "count")
    private int count;
    @Json(name = "limit")
    private int limit;
    @Json(name = "query")
    private String query;

    public boolean isPaginate() {
        return paginate;
    }

    public void setPaginate(boolean paginate) {
        this.paginate = paginate;
    }

    public int getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(int firstPage) {
        this.firstPage = firstPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public boolean isIsLastPage() {
        return isLastPage;
    }

    public void setIsLastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    public boolean isIsFirstPage() {
        return isFirstPage;
    }

    public void setIsFirstPage(boolean isFirstPage) {
        this.isFirstPage = isFirstPage;
    }

    public int getFirstIndex() {
        return firstIndex;
    }

    public void setFirstIndex(int firstIndex) {
        this.firstIndex = firstIndex;
    }

    public int getLastIndex() {
        return lastIndex;
    }

    public void setLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int getPrevPage() {
        return prevPage;
    }

    public void setPrevPage(int prevPage) {
        this.prevPage = prevPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
