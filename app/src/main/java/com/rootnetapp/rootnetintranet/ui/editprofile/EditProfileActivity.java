package com.rootnetapp.rootnetintranet.ui.editprofile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.databinding.ActivityEditProfileBinding;
import com.rootnetapp.rootnetintranet.models.responses.user.LoggedUser;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;

import javax.inject.Inject;

import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class EditProfileActivity extends AppCompatActivity {

    @Inject
    EditProfileViewModelFactory editProfileViewModelFactory;
    private EditProfileViewModel editProfileViewModel;
    private ActivityEditProfileBinding activityEditProfileBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        activityEditProfileBinding = DataBindingUtil
                .setContentView(this, R.layout.activity_edit_profile);
        editProfileViewModel = ViewModelProviders
                .of(this, editProfileViewModelFactory)
                .get(EditProfileViewModel.class);

        //TODO preferences inyectadas con Dagger
        SharedPreferences prefs = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString(PreferenceKeys.PREF_TOKEN, "");

        subscribe();
        setActionBar();

        editProfileViewModel.init(token);
    }

    private void subscribe() {
        final Observer<Boolean> statusObserver = ((Boolean data) -> {
            Utils.hideLoading();
            if (data) {
                finish();
            }
        });
        editProfileViewModel.getObservableStatus().observe(this, statusObserver);
        editProfileViewModel.getObservableUser().observe(this, this::updateUserUi);
        editProfileViewModel.getObservableError().observe(this, this::showToastMessage);
        editProfileViewModel.getObservableShowLoading().observe(this, this::showLoading);
    }

    private void setActionBar() {
        setSupportActionBar(activityEditProfileBinding.toolbar);
        activityEditProfileBinding.toolbar.setTitle(getString(R.string.edit_profile));
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void editUser() {

        /*activityEditProfileBinding.tilEmail.setError(null);
        activityEditProfileBinding.tilName.setError(null);
        //activityEditProfileBinding.tilPhone.setError(null);
        boolean canUpdate = true;
        if (TextUtils.isEmpty(activityEditProfileBinding.inputName.getText().toString())) {
            canUpdate = false;
            activityEditProfileBinding.tilName.setError(getString(R.string.empty_name));
        }
        if (TextUtils.isEmpty(activityEditProfileBinding.inputEmail.getText().toString())) {
            canUpdate = false;
            activityEditProfileBinding.tilEmail.setError(getString(R.string.empty_email));
        }
        if (canUpdate) {
            Utils.showLoading(this);
            editProfileViewModel.editUser("Bearer " + token, id,
                    activityEditProfileBinding.inputName.getText().toString(),
                    activityEditProfileBinding.inputEmail.getText().toString(),
                    activityEditProfileBinding.inputPhone.getText().toString());
        }*/
    }

    @UiThread
    private void updateUserUi(LoggedUser user) {
        if (user == null) return;

        activityEditProfileBinding.inputName.setText(user.getFullName());
        activityEditProfileBinding.inputEmail.setText(user.getEmail());
        activityEditProfileBinding.inputPhone.setText(user.getPhoneNumber());
        activityEditProfileBinding.btnAccept.setOnClickListener(view -> editUser());
    }

    @UiThread
    private void showToastMessage(@StringRes int messageRes) {
        Toast.makeText(
                this,
                getString(messageRes),
                Toast.LENGTH_SHORT)
                .show();
    }

    @UiThread
    private void showLoading(boolean show) {
        if (show) {
            Utils.showLoading(this);
        } else {
            Utils.hideLoading();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
