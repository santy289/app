package com.rootnetapp.rootnetintranet.ui.editprofile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.PreferenceKeys;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.data.local.db.user.User;
import com.rootnetapp.rootnetintranet.databinding.ActivityEditProfileBinding;
import com.rootnetapp.rootnetintranet.databinding.DialogChangePasswordBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;

import javax.inject.Inject;

import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
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

        SharedPreferences prefs = getSharedPreferences(PreferenceKeys.PREF_SESSION, Context.MODE_PRIVATE);
        String token = "Bearer " + prefs.getString(PreferenceKeys.PREF_TOKEN, "");

        subscribe();
        setActionBar();
        setOnClickListeners();

        editProfileViewModel.init(token);
    }

    private void subscribe() {
        editProfileViewModel.getObservableStatus().observe(this, this::finishActivity);
        editProfileViewModel.getObservableUser().observe(this, this::updateUserUi);
        editProfileViewModel.getObservableToastMessage().observe(this, this::showToastMessage);
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

    private void setOnClickListeners() {
        mBinding.btnAccept.setOnClickListener(view -> editUser());
        mBinding.btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
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

    /**
     * Displays an AlertDialog with two password inputs, prompting the user's new desired password.
     */
    private void showChangePasswordDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme);

        DialogChangePasswordBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(this), R.layout.dialog_change_password, null, false);
        builder.setView(binding.getRoot());

        builder.setTitle(R.string.change_password);
        builder.setCancelable(false);

        builder.setPositiveButton(R.string.accept, null);
        builder.setNegativeButton(R.string.cancel, null);

        AlertDialog dialog = builder.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(
                v -> changePassword(dialog, binding));
    }

    /**
     * Verifies the input and proceed to send the request to change the user's password.
     *
     * @param dialog  AlertDialog of this view.
     * @param binding AlertDialog's view binding.
     */
    private void changePassword(AlertDialog dialog, DialogChangePasswordBinding binding) {
        binding.tilPassword.setError(null);
        binding.tilConfirmPassword.setError(null);
        boolean canUpdate = true;

        String password = binding.inputPassword.getText().toString();
        String confirmPassword = binding.inputConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(password)) {
            canUpdate = false;
            binding.tilPassword.setError(getString(R.string.empty_password));
        } else if (TextUtils.isEmpty(confirmPassword)) {
            canUpdate = false;
            binding.tilConfirmPassword.setError(getString(R.string.empty_confirm_password));
        } else if (!password.equals(confirmPassword)) {
            canUpdate = false;
            binding.tilConfirmPassword.setError(getString(R.string.passwords_dont_match));
        }

        if (canUpdate) {
            dialog.dismiss();
            hideSoftInputKeyboard();

            editProfileViewModel.changePassword(password, confirmPassword);
        }
    }

    @UiThread
    private void finishActivity(boolean isFinish) {
        if (!isFinish) return;

        setResult(RESULT_OK);
        finish();
    }

    @UiThread
    private void updateUserUi(User user) {
        if (user == null) return;

        mBinding.inputName.setText(user.getFullName());
        mBinding.inputEmail.setText(user.getEmail());
        mBinding.inputPhone.setText(user.getPhoneNumber());
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
