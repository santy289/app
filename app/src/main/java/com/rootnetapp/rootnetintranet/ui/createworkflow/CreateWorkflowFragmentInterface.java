package com.rootnetapp.rootnetintranet.ui.createworkflow;

public interface CreateWorkflowFragmentInterface {

    void downloadFile(int fileId);

    /**
     * If you return true the back press will not be taken into account, otherwise the activity will act naturally
     * @return true if your processing has priority if not false
     */
    boolean onBackPressed();
}
