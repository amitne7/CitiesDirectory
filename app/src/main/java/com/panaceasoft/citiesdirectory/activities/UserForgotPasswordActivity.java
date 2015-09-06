package com.panaceasoft.citiesdirectory.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.View;

import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.utilities.Utils;

public class UserForgotPasswordActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SpannableString registerString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_forgot_password);

        initData();

        setupToolbar();
    }

    private void initData() {
        try {
            registerString = Utils.getSpannableString(getString(R.string.register));
        }catch(Exception e){
            Utils.psErrorLogE("Error in init data.", e);
        }
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitle(registerString);

    }

}
