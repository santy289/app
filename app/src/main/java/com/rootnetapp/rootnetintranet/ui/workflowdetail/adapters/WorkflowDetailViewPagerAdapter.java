package com.rootnetapp.rootnetintranet.ui.workflowdetail.adapters;

import android.content.Context;

import com.google.android.material.tabs.TabLayout;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.WorkflowDetailFragment;
import com.rootnetapp.rootnetintranet.ui.workflowdetail.status.StatusFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * Adapter for {@link ViewPager} in {@link WorkflowDetailFragment}. Displays a fixed number of pages
 * linked with a {@link TabLayout}.
 */
public class WorkflowDetailViewPagerAdapter extends FragmentPagerAdapter {

    private final Context mContext;
    private final WorkflowListItem mWorkflowItem;

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

        return StatusFragment.newInstance(mWorkflowItem);

        //todo set fragment for each tab
        /*switch (position){
            case 0:
                return StatusFragment.newInstance(mWorkflowItem);
            default:
                return null;
        }
*/
    }

    /**
     * This determines the number of tabs/pages.
     *
     * @return number of tabs/pages.
     */
    @Override
    public int getCount() {
        return 7; //todo proper count
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
        return mContext.getString(R.string.workflow_detail_status_fragment_title);

        //todo set title for each tab
        /*// Generate title based on item position
        switch (position) {
            case 0:
                return mContext.getString(R.string.workflow_detail_status_fragment_title);
            default:
                return null;
        }*/
    }

}