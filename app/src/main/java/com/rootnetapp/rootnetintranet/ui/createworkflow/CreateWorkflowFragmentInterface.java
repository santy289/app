package com.rootnetapp.rootnetintranet.ui.createworkflow;

import com.rootnetapp.rootnetintranet.models.createworkflow.form.GeolocationFormItem;

public interface CreateWorkflowFragmentInterface {

    void downloadFile(int fileId);

    void showLocation(GeolocationFormItem geolocationFormItem);

    /**
     * If you return true the back press will not be taken into account, otherwise the activity will act naturally
     * @return true if your processing has priority if not false
     */
    boolean onBackPressed();

    void showToastMessage(int stringRes);
}
