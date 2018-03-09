package com.rootnetapp.rootnetintranet.ui.login;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.rootnetapp.rootnetintranet.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //domainBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        setContentView(R.layout.activity_login);
    }
}
