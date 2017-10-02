package com.rootnetapp.rootnetintranet.main;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.lifecycle.LifecycleAppCompatActivity;

public class MainActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private DrawerLayout drawerLayout;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        setupNavigationDrawer();
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab == null) {
            return;
        }
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_black);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.main_drawer_layout);
        drawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView == null) {
            return;
        }
        setupDrawerContent(navigationView);
    }

    private void setupDrawerContent(NavigationView navigationView) {

    }
}
