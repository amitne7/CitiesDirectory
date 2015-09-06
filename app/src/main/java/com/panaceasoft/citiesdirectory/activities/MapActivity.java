package com.panaceasoft.citiesdirectory.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.View;

import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.utilities.Utils;

public class MapActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SpannableString exploreOnMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initData();

        initUI();

        bindData();

    }

    private void initData() {
        try {
            exploreOnMap = Utils.getSpannableString(getString(R.string.explore_on_map));
        }catch(Exception e){
            Utils.psErrorLogE("Error in init data.", e);
        }
    }

    private void initUI() {
        initToolbar();
    }

    private void initToolbar() {
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
    }

    private void bindData(){
        toolbar.setTitle(exploreOnMap);
    }

}
