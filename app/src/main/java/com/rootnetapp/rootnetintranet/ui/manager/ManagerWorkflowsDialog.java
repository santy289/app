package com.rootnetapp.rootnetintranet.ui.manager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.WorkflowDb;
import com.rootnetapp.rootnetintranet.databinding.DialogWorkflowManagerBinding;
import com.rootnetapp.rootnetintranet.ui.manager.adapters.ManagerDialogAdapter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import static com.rootnetapp.rootnetintranet.ui.manager.ManagerWorkflowsDialog.DialogType.COMPANY_CLOSED;
import static com.rootnetapp.rootnetintranet.ui.manager.ManagerWorkflowsDialog.DialogType.COMPANY_OPEN;
import static com.rootnetapp.rootnetintranet.ui.manager.ManagerWorkflowsDialog.DialogType.COMPANY_OUT_OF_TIME;
import static com.rootnetapp.rootnetintranet.ui.manager.ManagerWorkflowsDialog.DialogType.COMPANY_PENDING;
import static com.rootnetapp.rootnetintranet.ui.manager.ManagerWorkflowsDialog.DialogType.COMPANY_UPDATED;
import static com.rootnetapp.rootnetintranet.ui.manager.ManagerWorkflowsDialog.DialogType.USER_CLOSED;
import static com.rootnetapp.rootnetintranet.ui.manager.ManagerWorkflowsDialog.DialogType.USER_OPEN;
import static com.rootnetapp.rootnetintranet.ui.manager.ManagerWorkflowsDialog.DialogType.USER_OUT_OF_TIME;
import static com.rootnetapp.rootnetintranet.ui.manager.ManagerWorkflowsDialog.DialogType.USER_PENDING;
import static com.rootnetapp.rootnetintranet.ui.manager.ManagerWorkflowsDialog.DialogType.USER_UPDATED;

/**
 * Created by root on 19/04/18.
 */

public class ManagerWorkflowsDialog extends DialogFragment {

    private DialogWorkflowManagerBinding mBinding;
    private ManagerInterface mManagerInterface;
    private @DialogType int mDialogType;
    private List<WorkflowDb> mWorkflowList;

    public static ManagerWorkflowsDialog newInstance(ManagerInterface anInterface, @DialogType int type,
                                                     @NonNull List<WorkflowDb> workflows) {
        ManagerWorkflowsDialog fragment = new ManagerWorkflowsDialog();
        fragment.mManagerInterface = anInterface;
        fragment.mDialogType = type;
        fragment.mWorkflowList = workflows;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_workflow_manager, container, false);
        //((RootnetApp) getActivity().getApplication()).getAppComponent().inject(this);
        setCancelable(false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        switch (mDialogType) {
            case USER_PENDING:
                mBinding.square.setText(R.string.workflow_manager_user_pending_dialog_title);
                break;
            case USER_OPEN:
                mBinding.square.setText(R.string.workflow_manager_user_open_dialog_title);
                break;
            case USER_CLOSED:
                mBinding.square.setText(R.string.workflow_manager_user_closed_dialog_title);
                break;
            case USER_OUT_OF_TIME:
                mBinding.square.setText(R.string.workflow_manager_user_out_of_time_dialog_title);
                break;
            case USER_UPDATED:
                mBinding.square.setText(R.string.workflow_manager_user_updated_dialog_title);
                break;

            case COMPANY_PENDING:
                mBinding.square.setText(R.string.workflow_manager_company_pending_dialog_title);
                break;
            case COMPANY_OPEN:
                mBinding.square.setText(R.string.workflow_manager_company_open_dialog_title);
                break;
            case COMPANY_CLOSED:
                mBinding.square.setText(R.string.workflow_manager_company_closed_dialog_title);
                break;
            case COMPANY_OUT_OF_TIME:
                mBinding.square.setText(R.string.workflow_manager_company_out_of_time_dialog_title);
                break;
            case COMPANY_UPDATED:
                mBinding.square.setText(R.string.workflow_manager_company_updated_dialog_title);
                break;
        }

        mBinding.btnClose.setOnClickListener(view -> dismiss());
        mBinding.recWorkflows.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.recWorkflows.setAdapter(new ManagerDialogAdapter(mWorkflowList, mManagerInterface));

        return mBinding.getRoot();
    }
    
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            USER_PENDING, USER_OPEN, USER_CLOSED, USER_OUT_OF_TIME, USER_UPDATED,
            COMPANY_PENDING, COMPANY_OPEN, COMPANY_CLOSED, COMPANY_OUT_OF_TIME, COMPANY_UPDATED
    })
    public @interface DialogType {

        int USER_PENDING = 0;
        int USER_OPEN = 1;
        int USER_CLOSED = 2;
        int USER_OUT_OF_TIME = 3;
        int USER_UPDATED = 4;

        int COMPANY_PENDING = 5;
        int COMPANY_OPEN = 6;
        int COMPANY_CLOSED = 7;
        int COMPANY_OUT_OF_TIME = 8;
        int COMPANY_UPDATED = 9;
    }

}
