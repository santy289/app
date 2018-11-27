package com.rootnetapp.rootnetintranet.ui.quickactions;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.databinding.ActivityQuickActionsBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;
import com.rootnetapp.rootnetintranet.ui.quickactions.workflowsearch.WorkflowSearchFragment;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

public class QuickActionsActivity extends AppCompatActivity {

    public static final String EXTRA_ACTION = "ExtraAction";

    private static final String TAG = "QuickActionsActivity";

    @Inject
    QuickActionsViewModelFactory quickActionsViewModelFactory;
    QuickActionsViewModel quickActionsViewModel;
    private ActivityQuickActionsBinding mBinding;
    private FragmentManager fragmentManager;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_quick_actions);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        quickActionsViewModel = ViewModelProviders
                .of(this, quickActionsViewModelFactory)
                .get(QuickActionsViewModel.class);
        fragmentManager = getSupportFragmentManager();
        sharedPref = getSharedPreferences("Sessions", Context.MODE_PRIVATE);

        fragmentManager = getSupportFragmentManager();

        @QuickAction int action = getIntent()
                .getIntExtra(EXTRA_ACTION, QuickAction.APPROVE_WORKFLOW);

        setActionBar();
        showFragment(WorkflowSearchFragment.newInstance(action), false);
    }

    private void setActionBar() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getTitle());
    }

    public void showFragment(Fragment fragment, boolean addtobackstack) {
        String tag = fragment.getClass().getSimpleName();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.container, fragment);
        if (addtobackstack) {
            transaction.addToBackStack(tag);
        }
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