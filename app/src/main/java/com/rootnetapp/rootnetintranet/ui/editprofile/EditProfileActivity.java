package com.rootnetapp.rootnetintranet.ui.editprofile;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.databinding.ActivityEditProfileBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;

import javax.inject.Inject;

public class EditProfileActivity extends AppCompatActivity {

    @Inject
    EditProfileViewModelFactory editProfileViewModelFactory;
    EditProfileViewModel editProfileViewModel;
    private ActivityEditProfileBinding activityEditProfileBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        activityEditProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        editProfileViewModel = ViewModelProviders
                .of(this, editProfileViewModelFactory)
                .get(EditProfileViewModel.class);
        setSupportActionBar(activityEditProfileBinding.toolbar);
        activityEditProfileBinding.toolbar.setTitle(getString(R.string.edit_profile));
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        subscribe();
        //todo get user from internal db
    }

    private void subscribe() {
        final Observer<User> userObserver = ((User data) -> {
            Utils.hideLoading();
            if (data != null) {
                activityEditProfileBinding.inputName.setText(data.getFullName());
                activityEditProfileBinding.inputEmail.setText(data.getEmail());
                activityEditProfileBinding.inputPhone.setText(data.getPhoneNumber());
                activityEditProfileBinding.btnAccept.setOnClickListener(view -> editUser());
            }
        });
        final Observer<Boolean> statusObserver = ((Boolean data) -> {
            Utils.hideLoading();
            if (data) {
                finishAffinity();
            }
        });
        final Observer<Integer> errorObserver = ((Integer data) -> {
            Utils.hideLoading();
            if (null != data) {
                //TODO mejorar toast
                Toast.makeText(this, getString(data), Toast.LENGTH_LONG).show();
            }
        });
        editProfileViewModel.getObservableStatus().observe(this, statusObserver);
        editProfileViewModel.getObservableError().observe(this, errorObserver);
    }

    private void editUser() {

        //todo check empty fields

        //editProfileViewModel.editUser();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finishAffinity(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

}
