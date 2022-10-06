package com.rootnetapp.rootnetintranet.ui.main;

public class QuickActionsVisibility {
    private boolean showComment;
    private boolean showChangeStatus;
    private boolean showApprove = false;
    private boolean showEdit;

    public boolean isShowComment() {
        return showComment;
    }

    public void setShowComment(boolean showComment) {
        this.showComment = showComment;
    }

    public boolean isShowChangeStatus() {
        return showChangeStatus;
    }

    public void setShowChangeStatus(boolean showChangeStatus) {
        this.showChangeStatus = showChangeStatus;
    }

    public boolean isShowApprove() {
        return showApprove;
    }

    public void setShowApprove(boolean showApprove) {
        this.showApprove = showApprove;
    }

    public boolean isShowEdit() {
        return showEdit;
    }

    public void setShowEdit(boolean showEdit) {
        this.showEdit = showEdit;
    }
}
