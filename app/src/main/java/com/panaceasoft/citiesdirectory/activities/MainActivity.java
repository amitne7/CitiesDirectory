package com.panaceasoft.citiesdirectory.activities;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.fragments.ContactUsFragment;
import com.panaceasoft.citiesdirectory.fragments.CitiesListFragment;
import com.panaceasoft.citiesdirectory.fragments.FavouritesListFragment;
import com.panaceasoft.citiesdirectory.fragments.MapFragment;


import com.panaceasoft.citiesdirectory.fragments.NotificationFragment;
import com.panaceasoft.citiesdirectory.fragments.ProfileFragment;

import com.panaceasoft.citiesdirectory.fragments.TabFragment;
import com.panaceasoft.citiesdirectory.fragments.SearchFragment;
import com.panaceasoft.citiesdirectory.fragments.UserLoginFragment;
import com.panaceasoft.citiesdirectory.fragments.UserRegisterFragment;
import com.panaceasoft.citiesdirectory.listeners.GPSTracker;

import com.panaceasoft.citiesdirectory.models.Users;
import com.panaceasoft.citiesdirectory.utilities.DatabaseHelper;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;


/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar = null;
    private ActionBarDrawerToggle drawerToggle = null;
    private DrawerLayout drawerLayout = null;
    private NavigationView navigationView = null;
    private int currentMenuId = 0;
    public Users user;
    private SearchView searchView;
    private FABActions fabActions;
    // For Search Menu
    private MenuItem searchItem;
    private Fragment fragment = null;

    int mile = 0;

    private boolean notiFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notiFlag = getIntent().getBooleanExtra("show_noti", false);

        Utils.psLog(" > " + notiFlag);

        setupToolbar();
        setUpDrawerLayout();
        setUpNavigationView();
        setUpFAB();

        setUpUtils();

        loadLoginUserInfo();

        changeMenu();
        /*// Testing Toolbar Height
        int toolBarHeight = Utils.getToolbarHeight(this);
        Log.d("TEAMPS", toolBarHeight + " : Toolbar Height");
        // 5.1 = D/TEAMPS﹕ 112
        // 5.0 = D/TEAMPS﹕ 112
        // 4.2 = D/TEAMPS﹕ 84  : diff = 28

        int statusBarHeight = getStatusBarHeight();
        Log.d("TEAMPS", toolBarHeight + " : Status Bar Height");
        // 5.1 = D/TEAMPS﹕ 112
        // 5.0 = D/TEAMPS﹕ 112
        // 4.2 = D/TEAMPS﹕ 84  : diff = 28

        // Testing Version
        Log.d("TEAMPS", Build.VERSION.RELEASE + " - " + BuildConfig.VERSION_NAME+ " Android Version");

        // Testing Screen Width and height
        Point size = Utils.getScreenSize();
        Log.d("TEAMPS", "width : " + size.x +  " height :  " + size.y);
        // 5.1 = 768 - 1184
        // 5.0 =  width : 768 height :  1184
        // huawei = width : 540 height :  960
        // lenovo =


*/
    }

    private void setUpUtils() {
        new Utils(this);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchItem = (MenuItem) findViewById(R.id.action_search);

//        // Get the SearchView and set the searchable configuration
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        // Assumes current activity is the searchable activity
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            changeMenu();
            return true;
        } else if (id == R.id.action_search) {
            //searchStart();
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void searchStart() {
        searchItem = (MenuItem) findViewById(R.id.action_search);

        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);
        Log.d("TEAMPS", "Search Start");
        SearchView searchView = null;
        if (searchItem != null) {
            Log.d("TEAMPS", "Search Item is not null");
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        }

    }

    public void changeMenu() {
        DatabaseHelper db = new DatabaseHelper(getApplication());
        if (db != null && db.getUserCount() > 0) {
            navigationView.getMenu().setGroupVisible(R.id.group_after_login, true);
            navigationView.getMenu().setGroupVisible(R.id.group_before_login, false);
        } else {
            navigationView.getMenu().setGroupVisible(R.id.group_before_login, true);
            navigationView.getMenu().setGroupVisible(R.id.group_after_login, false);
        }

    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    public void setUpDrawerLayout() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawerLayout != null && toolbar != null) {
            drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    // invalidateOptionsMenu(this);
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    //invalidateOptionsMenu(this);
                }
            };

            drawerLayout.setDrawerListener(drawerToggle);

            drawerLayout.post(new Runnable() {
                @Override
                public void run() {
                    drawerToggle.syncState();
                }
            });
        }
    }


    private void setUpFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.bringToFront();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fabClicked(view);
                //Snackbar.make(view, "Why you click me! Why Why", Snackbar.LENGTH_SHORT)
                //       .setAction("Action", null).show();


                //startActivity(new Intent(MainActivity.this, SubCategoryActivity.class));
                //searchme();
            }
        });
    }

    private void fabClicked(View view) {
        if (fabActions == FABActions.PROFILE) {
            final Intent intent;
            intent = new Intent(this, EditProfileActivity.class);
            startActivityForResult(intent, 1);
        } else {
            Snackbar.make(view, "Why you click me! " + fabActions, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }
    }

    private void disableFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

    }

    private void enableFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

    }

    private void updateFABIcon(int icon) {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(icon);
    }

    private void updateFABAction(FABActions action) {
        fabActions = action;
    }


    private void searchme() {
        //searchView.setIconifiedByDefault(true);
        //searchView.setIconified(false);
        searchView.onActionViewExpanded();

        /*this.getSupportActionBar().startActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                searchStart();
                return true;

            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });*/
    }

    private void setUpNavigationView() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (navigationView != null) {

            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {

                            navigationMenuChanged(menuItem);

                            return true;
                        }
                    });
        }

        if (notiFlag) {
            savePushMessage(getIntent().getStringExtra("msg"));
            openFragment(R.id.nav_push_noti);
        } else {
            openFragment(R.id.nav_home);
        }


    }

    private void navigationMenuChanged(MenuItem menuItem) {

        openFragment(menuItem.getItemId());
        menuItem.setChecked(true);
        drawerLayout.closeDrawers();

        /* OLD CODE
         switch (menuItem.getItemId()){

            case R.id.nav_home:
            case R.id.nav_home_login:
                openFragment(R.id.nav_home);
                break;
            case R.id.nav_switch_city:
            case R.id.nav_switch_city_login:
                openFragment(R.id.nav_switch_city);
                break;
            case R.id.nav_map:
            case R.id.nav_map_login:
                openFragment(R.id.nav_map);
                break;
            case R.id.nav_profile:
            case R.id.nav_profile_login:
                openFragment(R.id.nav_profile);
                break;
            case R.id.nav_logout:

               openFragment(R.id.nav_logout);
                break;
            default:
                Toast.makeText(this, "Others", Toast.LENGTH_SHORT).show();
                break;
        }
        */

    }

    public void openFragment(int menuId) {


        switch (menuId) {
            case R.id.nav_home:
            case R.id.nav_home_login:
                disableFAB();
                fragment = new CitiesListFragment();
                //fragment = new MapFragment();
                break;

            case R.id.nav_switch_city:
            case R.id.nav_switch_city_login:
                disableFAB();
                fragment = new CitiesListFragment();
                break;

            case R.id.nav_profile:
            case R.id.nav_profile_login:
                // Need to add checking here
                // If user is already login
                //      Need to open profile fragment
                // If not
                //      Need to show Login fragment

                DatabaseHelper db = new DatabaseHelper(getApplication());
                if (db != null && db.getUserCount() > 0) {
                    enableFAB();
                    updateFABIcon(R.drawable.ic_edit_white);
                    updateFABAction(FABActions.PROFILE);
                    fragment = new ProfileFragment();
                } else {
                    fragment = new UserLoginFragment();
                }
                break;

            case R.id.nav_map:
            case R.id.nav_map_login:
                //locationSearchFAB();
                disableFAB();
                fragment = new MapFragment();
                /*
                Bundle bundle = new Bundle();
                bundle.putInt("mile", mile);
                fragment.setArguments(bundle);
                */
                break;

            case R.id.nav_register:
                fragment = new UserRegisterFragment();
                break;

            case R.id.nav_logout:
                doLogout();

                break;

            case R.id.nav_search_keyword:
            case R.id.nav_search_keyword_login:
                fragment = new SearchFragment();
                break;

            case R.id.nav_push_noti:
            case R.id.nav_push_noti_login:
                fragment = new NotificationFragment();
                break;

            case R.id.nav_favourite_item_login:
                fragment = new FavouritesListFragment();
                break;

            default:
                fragment = new ContactUsFragment();
                Toast.makeText(this, " Menu is " + menuId, Toast.LENGTH_SHORT).show();
                break;
        }

        //if(mContent != fragment) {
        if (currentMenuId != menuId && menuId != R.id.nav_logout) {
            currentMenuId = menuId;

            updateFragment(fragment);

            // Update Menu ID Selection
            try {
                navigationView.getMenu().findItem(menuId).setChecked(true);
            } catch (Exception e) {
            }
        }


    }

    private void updateFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        transaction.replace(R.id.content_frame, fragment);

        transaction.commit();
    }

    private void doLogout() {

        DatabaseHelper db = new DatabaseHelper(getApplication());
        // db.deleteUser(user);
        db.deleteAllUser();

        // Update the Menu
        changeMenu();

        // Open Main Fragment
        openFragment(R.id.nav_home);
    }


    private void loadLoginUserInfo() {
        DatabaseHelper db = new DatabaseHelper(getApplication());
        ArrayList<Users> usersArrayList = db.getAllUsers();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();

        if (usersArrayList.size() > 0) {

            user = usersArrayList.get(0);

            //There is login user
            editor.putInt("_login_user_id", usersArrayList.get(0).getId());
            editor.putString("_login_user_name", usersArrayList.get(0).getUser_name());
            editor.putString("_login_user_email", usersArrayList.get(0).getEmail());
            editor.putString("_login_user_about_me", usersArrayList.get(0).getAbout_me());
            //editor.putString("_login_user_del_address", usersArrayList.get(0).getDelivery_address());
            //editor.putString("_login_user_bill_address", usersArrayList.get(0).getBilling_address());
        } else {
            //User not yet login
            editor.putInt("_login_user_id", 0);
        }
        editor.commit();
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        getSupportFragmentManag®er().findFragmentById(R.id.content_frame).onActivityResult(requestCode, resultCode, data);
//
//    }


    @Override
    protected void onResume() {
        super.onResume();

        String s = Config.base_url;
        String a = s;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Utils.psLog("OnActivityResult");
        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {
                refreshProfileData();
            }

        }

    }

    private void refreshProfileData() {
        //Fragment fragment =new CitiesListFragment();
        //this.updateFragment(fragment);

        if (fragment instanceof ProfileFragment) {
            ((ProfileFragment) fragment).setupData();
        }
    }

    private enum FABActions {
        SEARCH,
        PROFILE
    }

    private void savePushMessage(String msg) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("_push_noti_message", msg);
        editor.commit();

    }

    public void loadProfileImage(String name, String path){
        final String fileName = name + ".jpg";
        Target target = new Target() {

            @Override
            public void onPrepareLoad(Drawable arg0) {
                Utils.psLog("Prepare Image to load.");
                return;
            }

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {

                try {
                    File file = null;

                    file = new File(Environment.getExternalStorageDirectory()+ "/"+fileName);

                    file.createNewFile();
                    FileOutputStream ostream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                    ostream.close();
                    Utils.psLog("Success Image Loaded.");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBitmapFailed(Drawable arg0) {
                return;
            }
        };

        Picasso.with(this)
                .load("http://www.cindyomidi.net/wp-content/uploads/2015/03/Fine-art1.jpg")
                .into(target);
    }
}
