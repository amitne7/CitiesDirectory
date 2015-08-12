package com.panaceasoft.citiesdirectory.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    //private ArrayList<CategoryData> categoryArrayList;
    //private ArrayList<SubCategoryData> subCategoryArrayList;
    private int selectedCityID;

    ///////
    private PCityData pCity;
    private ArrayList<PCategoryData> categoryArrayList;
    private ArrayList<PSubCategoryData> subCategoryArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_city);

        setupToolbar();
        setupCollapsingToolbarLayout();

        detailImage = (ImageView) findViewById(R.id.detail_image);
        //txtTitle = (TextView) findViewById(R.id.title);
        pCity = GlobalData.citydata;
        Utils.psLog("Selected City : " + pCity.name);

        /*
        CityDataWrapper dw = (CityDataWrapper) getIntent().getSerializableExtra("selected_city");
        ctd = dw.getCity();
        savePreference(ctd);
        */
        savePreference(pCity);

        //selectedCityID = ctd.getId();
        selectedCityID = pCity.id;


        if(collapsingToolbar != null){
            //collapsingToolbar.setTitle(ctd.getName());
            collapsingToolbar.setTitle(pCity.name);
        }
        //Picasso.with(getApplicationContext()).load(Config.APP_IMAGES_URL + ctd.getCover_image_file()).into(detailImage);
        Picasso.with(getApplicationContext()).load(Config.APP_IMAGES_URL + pCity.cover_image_file).into(detailImage);


        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        //mLayoutManager = new GridLayoutManager(this, 2);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CategoryAdapter(myDataset, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);

        //categoryArrayList = ctd.getCategoryData();
        categoryArrayList = pCity.categories;
        //for(CategoryData cd : categoryArrayList) {
        for(PCategoryData cd : categoryArrayList) {
            /*
            subCategoryArrayList = cd.getSubCategoryData();

            info = new CategoryRowData();
            info.setCatName(cd.getName());
            info.setCatImage(cd.getCover_image_file());
            myDataset.add(info);
            */
            subCategoryArrayList = cd.sub_categories;

            info = new CategoryRowData();
            info.setCatName(cd.name);
            info.setCatImage(cd.cover_image_file);
            myDataset.add(info);
        }
        mAdapter.notifyItemInserted(myDataset.size());




        /*
        mAdapter.setOnLoadMoreListener(new MyAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add progress item
                myDataset.add(null);
                mAdapter.notifyItemInserted(myDataset.size() - 1);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //remove progress item
                        myDataset.remove(myDataset.size() - 1);
                        mAdapter.notifyItemRemoved(myDataset.size());
                        //add items one by one
                        for (int i = 0; i < 16; i++) {

                            info = new CustomRowInformation();

                            info.setTitle("Item " + (myDataset.size() + 1)  );
                            myDataset.add(info);
                            mAdapter.notifyItemInserted(myDataset.size());
                        }
                        mAdapter.setLoaded();
                        //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
                    }
                }, 2000);
                System.out.println("load");
            }
        });
        */

        // for grid layout manager

//        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                switch(mAdapter.getItemViewType(position)){
//                    case CategoryAdapter.VIEW_ITEM:
//                        return 1;
//                    case CategoryAdapter.VIEW_PROG:
//                        return 2; //number of columns of the grid
//                    default:
//                        return -1;
//                }
//            }
//        });

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Toast.makeText(getActivity(), " Position "+ position, Toast.LENGTH_SHORT).show();
                //mDrawerLayout.closeDrawer(GravityCompat.START);
                onItemClicked(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }

    public void onItemClicked( int position){
        /*
        final Intent intent;
        intent = new Intent(this,SubCategoryActivity.class);
        intent.putExtra("selected_categories", new CategoryDataWrapper(ctd.getCategoryData()));
        intent.putExtra("selected_category_index", ctd.getCategoryData().get(position).getId()-1 );
        intent.putExtra("selected_city_id", selectedCityID + "");
        startActivity(intent);
        */
        final Intent intent;
        intent = new Intent(this,SubCategoryActivity.class);
        //intent.putExtra("selected_categories", new CategoryDataWrapper(ctd.getCategoryData()));
        //GlobalData.categoryData = pCity.categories;
        intent.putExtra("selected_category_index", position );

        Utils.psLog("Selected City ID :> " + selectedCityID);

        intent.putExtra("selected_city_id", selectedCityID);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_one, menu);
        return true;
    }

    private void setupCollapsingToolbarLayout(){

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
//        collapsingToolbar.setTitle(cheeseName);
//        txtTitle = (TextView) findViewById(R.id.title);
//        if(Utils.isAndroid_5_0()){
//            txtTitle.setPadding((int) this.getResources().getDimension(R.dimen.app_bar_title_padding_left), (int) (this.getResources().getDimension(R.dimen.app_bar_title_padding_bottom) - 25), 0, 0);
//            txtTitle.requestLayout();
//        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(Utils.isAndroid_5_0()){
            //substitute parameters for left, top, right, bottom
            Utils.setMargins(toolbar, 0, -102, 0, 0);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void savePreference(PCityData ct) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("_id", ct.id);
        editor.putString("_name", ct.name);
        editor.putString("_cover_image", ct.cover_image_file);
        editor.putString("_address", ct.address);
        editor.commit();
    }
}
