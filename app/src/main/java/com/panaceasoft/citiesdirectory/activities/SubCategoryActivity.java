package com.panaceasoft.citiesdirectory.activities;

import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.panaceasoft.citiesdirectory.GlobalData;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.fragments.TabFragment;
import com.panaceasoft.citiesdirectory.models.PCategoryData;
import com.panaceasoft.citiesdirectory.models.PSubCategoryData;
import com.panaceasoft.citiesdirectory.utilities.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class SubCategoryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ArrayList<? extends Parcelable> categoryArrayList = null;

    private Menu menu;
    private int selectedCategoryIndex = 0;
    //private ArrayList<CategoryData> categoriesList;
    //private ArrayList<SubCategoryData> subCategoriesList =  null;
    private int selectedCityID;
    private int C_FRAGMENTS_TO_KEEP_IN_MEMORY=0;
    private ViewPager viewPager;

    ///////
    private ArrayList<PCategoryData> categoriesList;
    private ArrayList<PSubCategoryData> subCategoriesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);


        /*
        // Prepare Data
        CategoryDataWrapper wrap =  (CategoryDataWrapper) getIntent().getSerializableExtra("selected_categories");
        categoriesList = wrap.getCategories();
        */
        categoriesList = GlobalData.citydata.categories;


        //Print Cat Name
        for (PCategoryData cd : categoriesList) {
            Log.d(" Cat Name : ", cd.name);
        }


        selectedCategoryIndex = getIntent().getIntExtra("selected_category_index", 0);
        selectedCityID = getIntent().getIntExtra("selected_city_id", 0);

        //subCategoriesList = categoriesList.get(selectedCategoryIndex).getSubCategoryData();
        subCategoriesList = categoriesList.get(selectedCategoryIndex).sub_categories;

        //Print Sub Cat Name
        for (PSubCategoryData scd : subCategoriesList) {
            Log.d(" Sub Cat Name : ", scd.name);
        }

        // Title
        selectedCategoryIndex = getIntent().getIntExtra("selected_category_index", 0);
        Log.d("TEAMPS", "Selected Category : " + selectedCategoryIndex + " " + categoriesList.get(0).name);
        // set up toolbar
        setupToolbar();

        // Prepare Tabs
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager, subCategoriesList);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFabClicked(v);
            }
        });

    }

    private void onFabClicked(View v) {
        TabFragment fragment = (TabFragment) ((Adapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem());
        Toast.makeText(this," Selected Ciy :"+fragment.selectedCityID+" Sub Cat : "+fragment.selectedSubCategoryID, Toast.LENGTH_SHORT).show();
    }

    //private void setupViewPager(ViewPager viewPager, ArrayList<SubCategoryData> subCategoryArrayList) {
    private void setupViewPager(ViewPager viewPager, ArrayList<PSubCategoryData> subCategoryArrayList) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        C_FRAGMENTS_TO_KEEP_IN_MEMORY = subCategoryArrayList.size();
        for(PSubCategoryData scd : subCategoryArrayList) {
            adapter.addFragment(new TabFragment().newInstance(scd,selectedCityID), "" + scd.name);
        }
        viewPager.setOffscreenPageLimit(C_FRAGMENTS_TO_KEEP_IN_MEMORY);

        viewPager.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tab, menu);

        int i = 0;

        for(PCategoryData cd : categoriesList) {
            menu.add(0, i, 0, cd.name);
            i++;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Log.d("TEAMPS", "Selected Menu id : " + id);

        //noinspection SimplifiableIfStatement
        loadCategoryUI(id);

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Utils.psLog("OnActivityResult");
        if(requestCode == 1){

            if(resultCode == RESULT_OK){
                TabFragment fragment = (TabFragment) ((Adapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem());
                fragment.refershLikeAndReview(data.getIntExtra("selected_item_id",0), data.getStringExtra("like_count"), data.getStringExtra("review_count"));
            }

        }

    }

    public void openActivity(int selected_item_id, int selected_city_id){
        final Intent intent;

        intent = new Intent(this, DetailActivity.class);

        //intent.putExtra("selected_categories", new CategoryDataWrapper(ctd.getCategoryData()));

        //intent.putExtra("selected_category_index", 0);

        //intent.putExtra("selected_item", new ItemDataWrapper(myDataset.get(position)));
        Utils.psLog("Selected City ID : " + selectedCityID);
        intent.putExtra("selected_item_id", selected_item_id);
        intent.putExtra("selected_city_id", selectedCityID + "");

        startActivityForResult(intent, 1);
    }

    private void loadCategoryUI(int id){

        Utils.psLog(" Load Category id : " + id);

        Intent intent = new Intent(this,SubCategoryActivity.class);
        //intent.putExtra("selected_categories", new CategoryDataWrapper(categoriesList));
        intent.putExtra("selected_categories", categoriesList);
        intent.putExtra("selected_category_index", id);
        startActivity(intent);

        this.finish();

    }
    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle(categoriesList.get(selectedCategoryIndex).getName());
        toolbar.setTitle(categoriesList.get(selectedCategoryIndex).name);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }



        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }



        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
