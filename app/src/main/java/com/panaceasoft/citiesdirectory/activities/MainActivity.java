package com.panaceasoft.citiesdirectory.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.MenuItem;
import android.view.View;

import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.fragments.CitiesListFragment;
import com.panaceasoft.citiesdirectory.fragments.FavouritesListFragment;
import com.panaceasoft.citiesdirectory.fragments.MapFragment;


import com.panaceasoft.citiesdirectory.fragments.NotificationFragment;
import com.panaceasoft.citiesdirectory.fragments.ProfileFragment;

import com.panaceasoft.citiesdirectory.fragments.SearchFragment;
import com.panaceasoft.citiesdirectory.fragments.UserForgotPasswordFragment;
import com.panaceasoft.citiesdirectory.fragments.UserLoginFragment;
import com.panaceasoft.citiesdirectory.fragments.UserRegisterFragment;

import com.panaceasoft.citiesdirectory.models.Users;
import com.panaceasoft.citiesdirectory.models.DatabaseHelper;
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
    private MenuItem searchItem;
    private Fragment fragment = null;
    private boolean notiFlag;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        notiFlag = getIntent().getBooleanExtra("show_noti", false);

        setupToolbar();
        setUpDrawerLayout();
        setUpNavigationView();
        setUpFAB();
        setUpUtils();
        loadLoginUserInfo();
        changeMenu();


        SpannableString s = new SpannableString("My Title");

       // s.setSpan(Utils.getTypeFace(Utils.Fonts.NOTO_SANS), 0, s.length(),
         //        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        toolbar.setTitle("s");

    }

    private void setUpUtils() {
        new Utils(this);
    }

    public void changeMenu() {
        /*
        DatabaseHelper db = new DatabaseHelper(getApplication());
        if (db != null && db.getUserCount() > 0) {
            navigationView.getMenu().setGroupVisible(R.id.group_after_login, true);
            navigationView.getMenu().setGroupVisible(R.id.group_before_login, false);
        } else {
            navigationView.getMenu().setGroupVisible(R.id.group_before_login, true);
            navigationView.getMenu().setGroupVisible(R.id.group_after_login, false);
        }
        */
        if (pref.getInt("_login_user_id", 0) != 0) {
            navigationView.getMenu().setGroupVisible(R.id.group_after_login, true);
            navigationView.getMenu().setGroupVisible(R.id.group_before_login, false);
        } else {
            navigationView.getMenu().setGroupVisible(R.id.group_before_login, true);
            navigationView.getMenu().setGroupVisible(R.id.group_after_login, false);
        }
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("ss");
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
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
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
    }

    public void openFragment(int menuId) {


        switch (menuId) {
            case R.id.nav_home:
            case R.id.nav_home_login:
                disableFAB();
                fragment = new CitiesListFragment();
                break;

            case R.id.nav_switch_city:
            case R.id.nav_switch_city_login:
                disableFAB();
                fragment = new CitiesListFragment();
                break;

            case R.id.nav_profile:
            case R.id.nav_profile_login:
                /*
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
                */
                if (pref.getInt("_login_user_id", 0) != 0) {
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
                disableFAB();
                fragment = new MapFragment();
                break;

            case R.id.nav_register:
                fragment = new UserRegisterFragment();
                break;

            case R.id.nav_forgot:
                fragment = new UserForgotPasswordFragment();
                Utils.psLog("Forgot PW");
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
                break;
        }

        if (currentMenuId != menuId && menuId != R.id.nav_logout) {
            currentMenuId = menuId;

            updateFragment(fragment);

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
        /*
        DatabaseHelper db = new DatabaseHelper(getApplication());
        db.deleteAllUser();
        changeMenu();
        openFragment(R.id.nav_home);
        */
        pref.edit().remove("_login_user_id").commit();
        pref.edit().remove("_login_user_name").commit();
        pref.edit().remove("_login_user_email").commit();
        pref.edit().remove("_login_user_about_me").commit();
    }


    private void loadLoginUserInfo() {
        Utils.psLog("Login User Id " + pref.getInt("_login_user_id", 0));
        /*
        DatabaseHelper db = new DatabaseHelper(getApplication());
        ArrayList<Users> usersArrayList = db.getAllUsers();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();

        if (usersArrayList.size() > 0) {

            user = usersArrayList.get(0);
            editor.putInt("_login_user_id", usersArrayList.get(0).getId());
            editor.putString("_login_user_name", usersArrayList.get(0).getUser_name());
            editor.putString("_login_user_email", usersArrayList.get(0).getEmail());
            editor.putString("_login_user_about_me", usersArrayList.get(0).getAbout_me());
        } else {
            editor.putInt("_login_user_id", 0);
        }
        editor.commit();
        */
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

    public void loadProfileImage(String name, String path) {

        if(!path.toString().equals("")){
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

                        file = new File(Environment.getExternalStorageDirectory() + "/" + fileName);

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
                    //.load("http://www.cindyomidi.net/wp-content/uploads/2015/03/Fine-art1.jpg")
                    .load(Config.APP_IMAGES_URL + path)
                    .into(target);
        }

    }
}
