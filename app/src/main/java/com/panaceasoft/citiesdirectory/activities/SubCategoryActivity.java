package com.panaceasoft.citiesdirectory.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/
    private Toolbar toolbar;
    private ArrayList<? extends Parcelable> categoryArrayList = null;
    private int selectedCategoryIndex = 0;
    private int selectedCityId;
    private int C_FRAGMENTS_TO_KEEP_IN_MEMORY=0;
    private ViewPager viewPager;
    private ArrayList<PCategoryData> categoriesList;
    private ArrayList<PSubCategoryData> subCategoriesList;
    private SharedPreferences prefs;
    /**------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        initData();

        initUI();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
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
        int id = item.getItemId();
        loadCategoryUI(id);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1){

            if(resultCode == RESULT_OK){
                TabFragment fragment = (TabFragment) ((Adapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem());
                fragment.refershLikeAndReview(data.getIntExtra("selected_item_id",0), data.getStringExtra("like_count"), data.getStringExtra("review_count"));
            }

        }

    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/
    private void initUI() {
        try {
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            if (viewPager != null) {
                initViewPager(viewPager, subCategoriesList);
            }
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            tabLayout.setupWithViewPager(viewPager);
            updateTabFonts(tabLayout);

            initFAB();
            initToolbar();
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initUI.", e);
        }

    }

    private void initFAB() {
        try {
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFabClicked(v);
                }
            });
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initFAB.", e);
        }
    }

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
            toolbar.setTitle(Utils.getSpannableString(categoriesList.get(selectedCategoryIndex).name));
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initToolbar.", e);
        }
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/
    private void initData() {
        try {
            categoriesList = GlobalData.citydata.categories;
            selectedCategoryIndex = getIntent().getIntExtra("selected_category_index", 0);
            selectedCityId = getIntent().getIntExtra("selected_city_id", 0);
            subCategoriesList = categoriesList.get(selectedCategoryIndex).sub_categories;
            selectedCategoryIndex = getIntent().getIntExtra("selected_category_index", 0);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initData.", e);
        }
    }
    /**------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     **------------------------------------------------------------------------------------------------*/
    private void updateTabFonts(TabLayout tabLayout){
        for(int i =0 ; i< tabLayout.getTabCount(); i++) {
            TextView tt = new TextView(this);
            tt.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));
            tt.setTextColor(Color.WHITE);
            tt.setText(tabLayout.getTabAt(i).getText());
            tabLayout.getTabAt(i).setCustomView(tt);
        }
    }

    private void onFabClicked(View v) {
        TabFragment fragment = (TabFragment) ((Adapter) viewPager.getAdapter()).getItem(viewPager.getCurrentItem());
        prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("_selected_city_id", fragment.selectedCityID);
        editor.putInt("_selected_sub_cat_id", fragment.selectedSubCategoryID);
        editor.commit();

        final Intent intent;
        intent = new Intent(this, MapActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }

    private void initViewPager(ViewPager viewPager, ArrayList<PSubCategoryData> subCategoryArrayList) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        C_FRAGMENTS_TO_KEEP_IN_MEMORY = subCategoryArrayList.size();
        for(PSubCategoryData scd : subCategoryArrayList) {
            adapter.addFragment(new TabFragment().newInstance(scd, selectedCityId),Utils.getSpannableString( scd.name)+"");

        }

        viewPager.setOffscreenPageLimit(C_FRAGMENTS_TO_KEEP_IN_MEMORY);

        viewPager.setAdapter(adapter);


    }

    private void loadCategoryUI(int id){
        Intent intent = new Intent(this,SubCategoryActivity.class);
        intent.putExtra("selected_category_index", id);
        intent.putExtra("selected_city_id", selectedCityId);
        startActivity(intent);
        this.finish();
    }
    /**------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

    public void openActivity(int selected_item_id, int selected_city_id){
        final Intent intent;
        intent = new Intent(this, DetailActivity.class);
        Utils.psLog("Selected City ID : " + selectedCityId);
        intent.putExtra("selected_item_id", selected_item_id);
        intent.putExtra("selected_city_id", selectedCityId);
        startActivityForResult(intent, 1);
        overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }
    /**------------------------------------------------------------------------------------------------
     * End Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Static Class
     **------------------------------------------------------------------------------------------------*/
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
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
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
        public SpannableString getPageTitle(int position) {
            return Utils.getSpannableString(mFragmentTitles.get(position));
        }

    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Static Class
     **------------------------------------------------------------------------------------------------*/


}
