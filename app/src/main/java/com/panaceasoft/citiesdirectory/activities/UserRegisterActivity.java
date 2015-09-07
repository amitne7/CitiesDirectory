package com.panaceasoft.citiesdirectory.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.View;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.utilities.Utils;

public class UserRegisterActivity extends AppCompatActivity {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    private Toolbar toolbar;
    private SpannableString registerString;

    /**------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        initData();

        initToolbar();
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void initData() {
        try {
            registerString = Utils.getSpannableString(getString(R.string.register));
        }catch(Exception e){
            Utils.psErrorLogE("Error in init data.", e);
        }
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    private void initToolbar() {
        try {
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
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initToolbar.", e);
        }
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/


}
