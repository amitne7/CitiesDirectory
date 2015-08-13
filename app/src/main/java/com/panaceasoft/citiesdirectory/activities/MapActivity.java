package com.panaceasoft.citiesdirectory.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.panaceasoft.citiesdirectory.R;

public class MapActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private String selected_city_id;
    private String selected_sub_cat_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setupToolbar();
        prepareData();
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.explore_on_map));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void prepareData() {
        selected_city_id = getIntent().getStringExtra("selected_city_id");
        selected_sub_cat_id = getIntent().getStringExtra("selected_sub_cat_id");
    }



}