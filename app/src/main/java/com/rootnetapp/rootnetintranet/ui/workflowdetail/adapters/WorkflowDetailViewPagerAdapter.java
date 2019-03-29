package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

import android.content.Context;

import com.google.android.material.tabs.TabLayout;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.flowchart.FlowchartFragment;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailActivity;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.approvalhistory.ApprovalHistoryFragment;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.comments.CommentsFragment;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.files.FilesFragment;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.information.BaseInformationFragment;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.peopleinvolved.BasePeopleInvolvedFragment;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.status.StatusFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * Adapter for {@link ViewPager} in {@link WorkflowDetailActivity}. Displays a fixed number of pages
 * linked with a {@link TabLayout}.
 */
public class WorkflowDetailViewPagerAdapter extends FragmentPagerAdapter {

    public static final int STATUS = 0;
    public static final int FLOWCHART = 1;
    public static final int INFORMATION = 2;
    public static final int PEOPLE_INVOLVED = 3;
    public static final int APPROVAL_HISTORY = 4;
    public static final int COMMENTS = 5;
    public static final int FILES = 6;

    private final Context mContext;
    private final WorkflowListItem mWorkflowItem;
    private int mCommentsCounter;
    private int mFilesCounter;

    public WorkflowDetailViewPagerAdapter(Context context, WorkflowListItem item,
                                          FragmentManager fm) {
        super(fm);
        this.mContext = context;
        this.mWorkflowItem = item;
    }

    /**
     * Creates an instance for the fragment on each tab, depending on the position.
     *
     * @param position position of tab.
     *
     * @return fragment to be displayed on the tab.
     */
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case STATUS:
                return StatusFragment.newInstance(mWorkflowItem);
            case FLOWCHART:
                return FlowchartFragment.newInstance(mWorkflowItem);
            case INFORMATION:
                return BaseInformationFragment.newInstance(mWorkflowItem);
            case PEOPLE_INVOLVED:
                return BasePeopleInvolvedFragment.newInstance(mWorkflowItem);
            case APPROVAL_HISTORY:
                return ApprovalHistoryFragment.newInstance(mWorkflowItem);
            case COMMENTS:
                return CommentsFragment.newInstance(mWorkflowItem, true);
            case FILES:
                return FilesFragment.newInstance(mWorkflowItem);
            default:
                return null;
        }
    }

    /**
     * This determines the number of tabs/pages.
     *
     * @return number of tabs/pages.
     */
    @Override
    public int getCount() {
        return 7;
    }

    /**
     * This determines the title for each tab of the {@link TabLayout}.
     *
     * @param position position of tab.
     *
     * @return title of tab.
     */
    @Override
    public CharSequence getPageTitle(int position) {

        String title;

        // Generate title based on item position
        switch (position) {
            case STATUS:
                return mContext.getString(R.string.workflow_detail_status_fragment_title);
            case FLOWCHART:
                return mContext.getString(R.string.workflow_detail_flowchart_fragment_title);
            case INFORMATION:
                return mContext.getString(R.string.workflow_detail_information_fragment_title);
            case PEOPLE_INVOLVED:
                return mContext.getString(R.string.workflow_detail_people_involved_fragment_title);
            case APPROVAL_HISTORY:
                return mContext.getString(R.string.workflow_detail_approval_history_fragment_title);
            case COMMENTS:
                title = mContext.getString(R.string.workflow_detail_comments_fragment_title);
                if (getCommentsCounter() > 0) {
                    title += " (" + getCommentsCounter() + ")";
                }
                return title;
            case FILES:
                title = mContext.getString(R.string.workflow_detail_files_fragment_title);
                if (getFilesCounter() > 0) {
                    title += " (" + getFilesCounter() + ")";
                }
                return title;
            default:
                return null;
        }
    }

    public int getCommentsCounter() {
        return mCommentsCounter;
    }

    public void setCommentsCounter(int commentsCounter) {
        this.mCommentsCounter = commentsCounter;
    }

    public int getFilesCounter() {
        return mFilesCounter;
    }

    public void setFilesCounter(int filesCounter) {
        this.mFilesCounter = filesCounter;
    }
}