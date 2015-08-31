package com.panaceasoft.citiesdirectory.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.utilities.Utils;

public class MapActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private int selected_city_id;
    private int selected_sub_cat_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setupToolbar();
        prepareData();
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

        toolbar.setTitle(Utils.getSpannableString(getString(R.string.explore_on_map)));

    }

    private void prepareData() {
        selected_city_id = getIntent().getIntExtra("selected_city_id", 0);
        selected_sub_cat_id = getIntent().getIntExtra("selected_sub_cat_id",0);
    }



}
