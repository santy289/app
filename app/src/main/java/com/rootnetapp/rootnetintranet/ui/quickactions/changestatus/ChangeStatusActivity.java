package com.rootnetapp.rootnetintranet.ui.quickactions.changestatus;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.data.local.db.workflow.workflowlist.WorkflowListItem;
import com.rootnetapp.rootnetintranet.databinding.ActivityChangeStatusBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class ChangeStatusActivity extends AppCompatActivity {

    public static final String EXTRA_WORKFLOW_LIST_ITEM = "Extra.WorkflowListItem";
    public static final String EXTRA_TITLE = "Extra.Title";
    public static final String EXTRA_SUBTITLE = "Extra.Subtitle";

    private ActivityChangeStatusBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_change_status);

        ((RootnetApp) getApplication()).getAppComponent().inject(this);

        SharedPreferences prefs = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("token", "");
        WorkflowListItem item = getIntent().getParcelableExtra(EXTRA_WORKFLOW_LIST_ITEM);

        setActionBar();

        showFragment(ChangeStatusFragment.newInstance(item));
    }

    private void setActionBar() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (title == null) title = (String) getTitle();
        getSupportActionBar().setTitle(title);

        String subtitle = getIntent().getStringExtra(EXTRA_SUBTITLE);
        if (subtitle != null) getSupportActionBar().setSubtitle(subtitle);
    }

    private void showFragment(Fragment fragment) {
        String tag = fragment.getClass().getSimpleName();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}