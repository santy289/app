package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

import android.content.Context;

import com.google.android.material.tabs.TabLayout;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailActivity;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.approvalhistory.ApprovalHistoryFragment;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.comments.CommentsFragment;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.files.FilesFragment;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.information.BaseInformationFragment;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.peopleinvolved.PeopleInvolvedFragment;
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
            case 0:
                return StatusFragment.newInstance(mWorkflowItem);
            case 1:
                return BaseInformationFragment.newInstance(mWorkflowItem);
            case 2:
                return PeopleInvolvedFragment.newInstance(mWorkflowItem);
            case 3:
                return ApprovalHistoryFragment.newInstance(mWorkflowItem);
            case 4:
                return CommentsFragment.newInstance(mWorkflowItem, true);
            case 5:
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
        return 6;
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
            case 0:
                return mContext.getString(R.string.workflow_detail_status_fragment_title);
            case 1:
                return mContext.getString(R.string.workflow_detail_information_fragment_title);
            case 2:
                return mContext.getString(R.string.workflow_detail_people_involved_fragment_title);
            case 3:
                return mContext.getString(R.string.workflow_detail_approval_history_fragment_title);
            case 4:
                title = mContext.getString(R.string.workflow_detail_comments_fragment_title);
                if (getCommentsCounter() > 0) {
                    title += " (" + getCommentsCounter() + ")";
                }
                return title;
            case 5:
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