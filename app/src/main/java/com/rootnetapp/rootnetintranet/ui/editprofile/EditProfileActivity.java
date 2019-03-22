package com.rootnetapp.rootnetintranet.ui.editprofile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import androidx.lifecycle.ViewModelProviders;

public class EditProfileActivity extends AppCompatActivity {

    @Inject
    EditProfileViewModelFactory editProfileViewModelFactory;
    private EditProfileViewModel editProfileViewModel;
    private ActivityEditProfileBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        mBinding = DataBindingUtil
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
        editProfileViewModel.getObservableStatus().observe(this, this::finishActivity);
        editProfileViewModel.getObservableUser().observe(this, this::updateUserUi);
        editProfileViewModel.getObservableError().observe(this, this::showToastMessage);
        editProfileViewModel.getObservableShowLoading().observe(this, this::showLoading);
    }

    private void setActionBar() {
        setSupportActionBar(mBinding.toolbar);
        mBinding.toolbar.setTitle(getString(R.string.edit_profile));
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void editUser() {
        mBinding.tilEmail.setError(null);
        mBinding.tilName.setError(null);
        //mBinding.tilPhone.setError(null);
        boolean canUpdate = true;
        if (TextUtils.isEmpty(mBinding.inputName.getText().toString())) {
            canUpdate = false;
            mBinding.tilName.setError(getString(R.string.empty_name));
        }
        if (TextUtils.isEmpty(mBinding.inputEmail.getText().toString())) {
            canUpdate = false;
            mBinding.tilEmail.setError(getString(R.string.empty_email));
        }
        if (canUpdate) {
            hideSoftInputKeyboard();

            editProfileViewModel.editUser(
                    mBinding.inputName.getText().toString(),
                    mBinding.inputEmail.getText().toString(),
                    mBinding.inputPhone.getText().toString()
            );
        }
    }

    @UiThread
    private void finishActivity(boolean isFinish){
        if (!isFinish) return;

        setResult(RESULT_OK);
        finish();
    }

    @UiThread
    private void updateUserUi(LoggedUser user) {
        if (user == null) return;

        mBinding.inputName.setText(user.getFullName());
        mBinding.inputEmail.setText(user.getEmail());
        mBinding.inputPhone.setText(user.getPhoneNumber());

        mBinding.btnAccept.setOnClickListener(view -> editUser());
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

    private void hideSoftInputKeyboard() {
        // Check if no view has focus:
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
