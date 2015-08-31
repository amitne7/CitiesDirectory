package com.panaceasoft.citiesdirectory.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.GlobalData;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.adapters.CategoryAdapter;
import com.panaceasoft.citiesdirectory.listeners.ClickListener;
import com.panaceasoft.citiesdirectory.listeners.RecyclerTouchListener;

import com.panaceasoft.citiesdirectory.models.CategoryRowData;
import com.panaceasoft.citiesdirectory.models.PCategoryData;
import com.panaceasoft.citiesdirectory.models.PCityData;
import com.panaceasoft.citiesdirectory.models.PSubCategoryData;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class SelectedCityActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private ImageView detailImage;
    private TextView txtTitle;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private CategoryAdapter mAdapter;
    private List<CategoryRowData> myDataset = new ArrayList<>();
    private CategoryRowData info;
    private int selectedCityID;
    private PCityData pCity;
    private ArrayList<PCategoryData> categoryArrayList;
    private ArrayList<PSubCategoryData> subCategoryArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_city);

        setupToolbar();
        setupCollapsingToolbarLayout();
        prepareData();
        savePreference(pCity);
        setUpShopInfo();
        loadGrid();

    }

    public void loadGrid() {
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CategoryAdapter(myDataset, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);

        categoryArrayList = pCity.categories;
        for(PCategoryData cd : categoryArrayList) {
            subCategoryArrayList = cd.sub_categories;

            info = new CategoryRowData();
            info.setCatName(cd.name);
            info.setCatImage(cd.cover_image_file);
            myDataset.add(info);
        }
        mAdapter.notifyItemInserted(myDataset.size());

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                onItemClicked(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        mRecyclerView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
    }

    public void onItemClicked( int position){
        final Intent intent;
        intent = new Intent(this,SubCategoryActivity.class);
        intent.putExtra("selected_category_index", position );
        intent.putExtra("selected_city_id", selectedCityID);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news, menu);
        return true;
    }

    private void prepareData() {
        detailImage = (ImageView) findViewById(R.id.detail_image);
        pCity = GlobalData.citydata;
        selectedCityID = pCity.id;
    }

    private void setUpShopInfo() {
        if(collapsingToolbar != null){

            collapsingToolbar.setTitle(Utils.getSpannableString(pCity.name));
            makeCollapsingToolbarLayoutLooksGood(collapsingToolbar);

        }
        Picasso.with(getApplicationContext()).load(Config.APP_IMAGES_URL + pCity.cover_image_file).into(detailImage);
    }

    private void makeCollapsingToolbarLayoutLooksGood(CollapsingToolbarLayout collapsingToolbarLayout) {
        try {
            final Field field = collapsingToolbarLayout.getClass().getDeclaredField("mCollapsingTextHelper");
            field.setAccessible(true);

            final Object object = field.get(collapsingToolbarLayout);
            final Field tpf = object.getClass().getDeclaredField("mTextPaint");
            tpf.setAccessible(true);

            ((TextPaint) tpf.get(object)).setTypeface(Utils.getTypeFace(Utils.Fonts.NOTO_SANS));
            ((TextPaint) tpf.get(object)).setColor(getResources().getColor(R.color.colorAccent));
        } catch (Exception ignored) {
        }
    }

    private void setupCollapsingToolbarLayout(){
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_news) {
            Utils.psLog("Open News Activity");
            final  Intent intent;
            intent = new Intent(this, NewsListActivity.class);
            intent.putExtra("selected_city_id", selectedCityID + "");
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(Utils.isAndroid_5_0()){
            Utils.setMargins(toolbar, 0, -102, 0, 0);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitle("");


        toolbar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

    }

    private void savePreference(PCityData ct) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("_id", ct.id);
        editor.putString("_name", ct.name);
        editor.putString("_cover_image", ct.cover_image_file);
        editor.putString("_address", ct.address);
        editor.putString("_city_region_lat", ct.lat);
        editor.putString("_city_region_lng", ct.lng);
        editor.commit();
    }
}
