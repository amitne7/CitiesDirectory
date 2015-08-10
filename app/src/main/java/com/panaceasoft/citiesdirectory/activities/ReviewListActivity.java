package com.panaceasoft.citiesdirectory.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.ResultReceiver;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.panaceasoft.citiesdirectory.GlobalData;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.adapters.ReviewAdapter;
import com.panaceasoft.citiesdirectory.models.PItemData;

import com.panaceasoft.citiesdirectory.utilities.DatabaseHelper;
import com.panaceasoft.citiesdirectory.utilities.Utils;

import java.util.ArrayList;

public class ReviewListActivity extends AppCompatActivity {

    private ListView listView;
    private ReviewAdapter adapter;
    private Toolbar toolbar;
    private int selected_item_id;
    private int selected_shop_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_review_list);

        setupToolbar();

        setupFAB();

        prepareData();

        initList();

        refreshData();

    }

    private void initList() {
        listView = (ListView) findViewById(R.id.review_list);
    }

    private void prepareData() {
        selected_item_id = getIntent().getIntExtra("selected_item_id", 0);
        selected_shop_id = getIntent().getIntExtra("selected_shop_id", 0);

        ArrayList<PItemData> lists = getIntent().getParcelableArrayListExtra("list");
        Utils.psLog(" Count " + lists.size() + " . First name : "+ lists.get(0).name);

        PItemData pid = getIntent().getParcelableExtra("obj");
        Utils.psLog(" item data name : " + pid.name);
    }


    private void refreshData() {
        adapter = new ReviewAdapter(this,GlobalData.itemData.reviews);
        listView.setAdapter(adapter);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.review_list));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_new_review);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper db = new DatabaseHelper(getApplication());
                if(db.getUserCount() > 0) {
                    Intent intent = new Intent(getApplicationContext(), ReviewEntry.class);
                    intent.putExtra("selected_item_id", selected_item_id);
                    intent.putExtra("selected_shop_id", selected_shop_id);

                    startActivityForResult(intent, 1);
                } else {
                    Intent intent = new Intent(getApplicationContext(), UserLogin.class);
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
        // Code to do refresh Data in Detail Activity
        Intent in = new Intent();
        setResult(RESULT_OK,in);
        finish();
        return;
    }


}
