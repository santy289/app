package com.rootnetapp.rootnetintranet.ui.qrtoken;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.rootnetapp.rootnetintranet.R;
import com.rootnetapp.rootnetintranet.commons.Utils;
import com.rootnetapp.rootnetintranet.databinding.ActivityQrTokenBinding;
import com.rootnetapp.rootnetintranet.ui.RootnetApp;

import java.io.File;

import javax.inject.Inject;

public class QRTokenActivity extends AppCompatActivity {

    public static final String EXTRA_QR_CODE = "ExtraQRCode";

    private static final String TAG = "QRTokenActivity";

    @Inject
    QRTokenViewModelFactory viewModelFactory;
    private QRTokenViewModel viewModel;
    private ActivityQrTokenBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_qr_token);
        ((RootnetApp) getApplication()).getAppComponent().inject(this);
        viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(QRTokenViewModel.class);

        setActionBar();
        showQRCode((File) getIntent().getSerializableExtra(EXTRA_QR_CODE));
        subscribe();
    }

    private void setActionBar() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getTitle());
    }

    private void subscribe() {
        viewModel.getObservableShowLoading().observe(this, this::showLoading);
        viewModel.getObservableShowToastMessage().observe(this, this::showToastMessage);
    }

    private void showQRCode(File qrCode) {
        Glide.with(this)
                .load(qrCode)
                .into(mBinding.ivQrCode);
    }

    @UiThread
    private void showLoading(boolean show) {
        if (show) {
            Utils.showLoading(this);
        } else {
            Utils.hideLoading();
        }
    }

    @UiThread
    private void showToastMessage(@StringRes int messageRes) {
        Toast.makeText(
                this,
                getString(messageRes),
                Toast.LENGTH_SHORT)
                .show();
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
