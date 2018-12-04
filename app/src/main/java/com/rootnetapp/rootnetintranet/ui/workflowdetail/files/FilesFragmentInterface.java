package com.rootnetapp.rootnetintranet.ui.workflowdetail.files;

import com.rootnetapp.rootnetintranet.models.responses.file.DocumentsFile;
import com.rootnetapp.rootnetintranet.models.responses.workflows.presets.Preset;

public interface FilesFragmentInterface {

    void downloadPreset(Preset preset);

    void downloadDocumentFile(DocumentsFile documentsFile);
}
