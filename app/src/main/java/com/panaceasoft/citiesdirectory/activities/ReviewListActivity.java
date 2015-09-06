package com.panaceasoft.citiesdirectory.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.View;
import android.widget.ListView;

import com.panaceasoft.citiesdirectory.GlobalData;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.adapters.ReviewAdapter;
import com.panaceasoft.citiesdirectory.models.DatabaseHelper;
import com.panaceasoft.citiesdirectory.utilities.Utils;

public class ReviewListActivity extends AppCompatActivity {

    private ListView listView;
    private ReviewAdapter adapter;
    private Toolbar toolbar;
    private int selectedItemId;
    private int selectedCityId;
    private SharedPreferences pref;
    private SpannableString reviewListString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        initData();
        setContentView(R.layout.activity_review_list);
        setupToolbar();
        setupFAB();
        prepareData();
        initList();
        refreshData();
    }

    private void initData() {
        try {

            reviewListString = Utils.getSpannableString(getString(R.string.review_list));
        }catch(Exception e){
            Utils.psErrorLogE("Error in init data.", e);
        }
    }

    private void initList() {
        listView = (ListView) findViewById(R.id.review_list);
    }

    private void prepareData() {
        selectedItemId = getIntent().getIntExtra("selected_item_id", 0);
        selectedCityId = getIntent().getIntExtra("selected_city_id", 0);

    }


    private void refreshData() {
        adapter = new ReviewAdapter(this,GlobalData.itemData.reviews);
        listView.setAdapter(adapter);
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

        toolbar.setTitle(reviewListString);
    }

    private void setupFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_new_review);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pref.getInt("_login_user_id", 0) != 0) {
                    Intent intent = new Intent(getApplicationContext(), ReviewEntry.class);
                    intent.putExtra("selected_item_id", selectedItemId);
                    intent.putExtra("selected_city_id", selectedCityId);
                    startActivityForResult(intent, 1);
                } else {
                    Intent intent = new Intent(getApplicationContext(), UserLoginActivity.class);
                    startActivity(intent);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1){

            if(resultCode == RESULT_OK){
                refreshData();
            }
        }

        Utils.psLog(" Result : " + requestCode + " : " + resultCode );
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent();
        setResult(RESULT_OK,in);
        finish();
        return;
    }


}
