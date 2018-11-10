package com.rootnetapp.rootnetintranet.ui.editprofile;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
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
    int id;
    String token;

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
        Utils.showLoading(this);
        subscribe();
        //TODO preferences inyectadas con Dagger
        SharedPreferences prefs = getSharedPreferences("Sessions", Context.MODE_PRIVATE);
        token = prefs.getString("token","");
        JWT jwt = new JWT(token);
        id = Integer.parseInt(jwt.getClaim("profile_id").asString());
        editProfileViewModel.getUser(id);
    }

    private void subscribe() {
        final Observer<User> userObserver = ((User data) -> {
            if (data != null) {
                activityEditProfileBinding.inputName.setText(data.getFullName());
                activityEditProfileBinding.inputEmail.setText(data.getEmail());
                activityEditProfileBinding.inputPhone.setText(data.getPhoneNumber());
                activityEditProfileBinding.btnAccept.setOnClickListener(view -> editUser());
            }
            Utils.hideLoading();
        });
        final Observer<Boolean> statusObserver = ((Boolean data) -> {
            Utils.hideLoading();
            if (data) {
                finish();
            }
        });
        final Observer<Integer> errorObserver = ((Integer data) -> {
            Utils.hideLoading();
            if (null != data) {
                //TODO mejorar toast
                Toast.makeText(this, getString(data), Toast.LENGTH_LONG).show();
            }
        });
        editProfileViewModel.getObservableUser().observe(this, userObserver);
        editProfileViewModel.getObservableStatus().observe(this, statusObserver);
        editProfileViewModel.getObservableError().observe(this, errorObserver);
    }

    private void editUser() {

        activityEditProfileBinding.tilEmail.setError(null);
        activityEditProfileBinding.tilName.setError(null);
        //activityEditProfileBinding.tilPhone.setError(null);
        boolean canUpdate = true;
        if(TextUtils.isEmpty(activityEditProfileBinding.inputName.getText().toString())){
            canUpdate = false;
            activityEditProfileBinding.tilName.setError(getString(R.string.empty_name));
        }
        if(TextUtils.isEmpty(activityEditProfileBinding.inputEmail.getText().toString())){
            canUpdate = false;
            activityEditProfileBinding.tilEmail.setError(getString(R.string.empty_email));
        }
        if(canUpdate){
            Utils.showLoading(this);
            editProfileViewModel.editUser("Bearer "+token, id,
                    activityEditProfileBinding.inputName.getText().toString(),
                    activityEditProfileBinding.inputEmail.getText().toString(),
                    activityEditProfileBinding.inputPhone.getText().toString());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

}
