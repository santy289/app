package com.rootnetapp.rootnetintranet.interfaces;

public interface BoundaryCallbackInterface {
    void clearDisposables();
    void updateCurrentPage(int pageNumber);
    void updateIsLoading(boolean isLoading);
}
