package com.panaceasoft.citiesdirectory.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.fragments.CitiesListFragment;
import com.panaceasoft.citiesdirectory.fragments.FavouritesListFragment;
import com.panaceasoft.citiesdirectory.fragments.NotificationFragment;
import com.panaceasoft.citiesdirectory.fragments.ProfileFragment;
import com.panaceasoft.citiesdirectory.fragments.SearchFragment;
import com.panaceasoft.citiesdirectory.fragments.UserForgotPasswordFragment;
import com.panaceasoft.citiesdirectory.fragments.UserLoginFragment;
import com.panaceasoft.citiesdirectory.fragments.UserRegisterFragment;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.io.File;
import java.io.FileOutputStream;


/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class MainActivity extends AppCompatActivity {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private & Public Variables
     **------------------------------------------------------------------------------------------------*/
    private Toolbar toolbar = null;
    private ActionBarDrawerToggle drawerToggle = null;
    private DrawerLayout drawerLayout = null;
    private NavigationView navigationView = null;
    private int currentMenuId = 0;
    private FABActions fabActions;
    private boolean notiFlag;
    private SharedPreferences pref;
    private FloatingActionButton fab;
    private SpannableString appNameString;
    private SpannableString profileString;
    private SpannableString registerString;
    private SpannableString forgotPasswordString;
    private SpannableString searchKeywordString;
    private SpannableString favouriteItemString;

    public Fragment fragment = null;

    /**------------------------------------------------------------------------------------------------
     * End Block - Private & PublicVariables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUtils();

        initUI();

        initData();

        bindData();

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

    /**------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Utils Class
     **------------------------------------------------------------------------------------------------*/

    private void initUtils() {
        new Utils(this);
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Init Utils Class
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    private void initUI(){
        initToolbar();
        initDrawerLayout();
        initNavigationView();
        initFAB();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initDrawerLayout() {
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

    private void initNavigationView() {
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
    }

    private void initFAB() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.bringToFront();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabClicked(view);
            }
        });
    }
    /**------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void initData(){
        try {
            pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            notiFlag = getIntent().getBooleanExtra("show_noti", false);
            Utils.psLog("Notification Flag : " + notiFlag);
            if (notiFlag) {
                savePushMessage(getIntent().getStringExtra("msg"));
                openFragment(R.id.nav_push_noti);
            } else {
                openFragment(R.id.nav_home);
            }
        }catch(Exception e){
            Utils.psErrorLogE("Error in getting notification flag data.", e);
        }

        try {
            appNameString = Utils.getSpannableString(getString(R.string.app_name));
            profileString = Utils.getSpannableString(getString(R.string.profile));
            registerString = Utils.getSpannableString(getString(R.string.register));
            forgotPasswordString = Utils.getSpannableString(getString(R.string.forgot_password));
            searchKeywordString = Utils.getSpannableString(getString(R.string.search_keyword));
            favouriteItemString = Utils.getSpannableString(getString(R.string.favourite_item));

        }catch(Exception e){
            Utils.psErrorLogE("Error in init Data.", e);
        }

    }
    /**------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void bindData() {

        toolbar.setTitle(appNameString);

        bindMenu();

    }

    // This function will change the menu based on the user is logged in or not.
    public void bindMenu() {
        if (pref.getInt("_login_user_id", 0) != 0) {
            navigationView.getMenu().setGroupVisible(R.id.group_after_login, true);
            navigationView.getMenu().setGroupVisible(R.id.group_before_login, false);
        } else {
            navigationView.getMenu().setGroupVisible(R.id.group_before_login, true);
            navigationView.getMenu().setGroupVisible(R.id.group_after_login, false);
        }
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    private void disableFAB() {
        fab.setVisibility(View.GONE);
    }

    private void enableFAB() {
        fab.setVisibility(View.VISIBLE);
    }

    private void updateFABIcon(int icon) {
        fab.setImageResource(icon);
    }

    private void updateFABAction(FABActions action) {
        fabActions = action;
    }

    private void navigationMenuChanged(MenuItem menuItem) {
        openFragment(menuItem.getItemId());
        menuItem.setChecked(true);
        drawerLayout.closeDrawers();
    }

    private void updateFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }

    private void doLogout() {
        pref.edit().remove("_login_user_id").commit();
        pref.edit().remove("_login_user_name").commit();
        pref.edit().remove("_login_user_email").commit();
        pref.edit().remove("_login_user_about_me").commit();
        pref.edit().remove("_login_user_photo").commit();

        bindMenu();

        openFragment(R.id.nav_home);
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

    public void fabClicked(View view) {
        if (fabActions == FABActions.PROFILE) {
            final Intent intent;
            intent = new Intent(this, EditProfileActivity.class);
            startActivityForResult(intent, 1);
        }
    }

    public void openFragment(int menuId) {

        switch (menuId) {
            case R.id.nav_home:
            case R.id.nav_home_login:
                disableFAB();
                fragment = new CitiesListFragment();
                toolbar.setTitle(appNameString);
                break;

            case R.id.nav_profile:
            case R.id.nav_profile_login:
                if (pref.getInt("_login_user_id", 0) != 0) {
                    enableFAB();
                    updateFABIcon(R.drawable.ic_edit_white);
                    updateFABAction(FABActions.PROFILE);
                    fragment = new ProfileFragment();
                } else {
                    fragment = new UserLoginFragment();
                }
                toolbar.setTitle(profileString);
                break;

            case R.id.nav_register:
                fragment = new UserRegisterFragment();
                toolbar.setTitle(registerString);
                break;

            case R.id.nav_forgot:
                fragment = new UserForgotPasswordFragment();
                toolbar.setTitle(forgotPasswordString);
                break;

            case R.id.nav_logout:
                doLogout();
                break;

            case R.id.nav_search_keyword:
            case R.id.nav_search_keyword_login:
                disableFAB();
                fragment = new SearchFragment();
                toolbar.setTitle(searchKeywordString);
                break;

            case R.id.nav_push_noti:
            case R.id.nav_push_noti_login:
                disableFAB();
                fragment = new NotificationFragment();
                //toolbar.setTitle(Utils.getSpannableString(getString(R.string.push_noti_setting)));
                break;

            case R.id.nav_favourite_item_login:
                disableFAB();
                fragment = new FavouritesListFragment();
                toolbar.setTitle(favouriteItemString);
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

    // Neet to check
    public void refreshProfileData() {

        if (fragment instanceof ProfileFragment) {
            ((ProfileFragment) fragment).bindData();
        }
    }

    public void refreshProfile(){
        openFragment(R.id.nav_profile_login);
    }

    public void refreshNotification(){
        try {
            fragment = new NotificationFragment();

            updateFragment(fragment);
            if (pref.getInt("_login_user_id", 0) != 0) {
                currentMenuId = R.id.nav_push_noti_login;
            }else{
                currentMenuId = R.id.nav_push_noti;
            }

            navigationView.getMenu().findItem(currentMenuId).setChecked(true);
        } catch (Exception e) {
            Utils.psErrorLogE("Refresh Notification View Error. " , e);
        }

    }

    public void savePushMessage(String msg) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("_push_noti_message", msg);
        editor.commit();
    }

    public void loadProfileImage(String path) {

        if(!path.toString().equals("")){

            final String fileName = path;
            Utils.psLog("file name : " + fileName);

            Target target = new Target() {

                @Override
                public void onPrepareLoad(Drawable arg0) {
                    Utils.psLog("Prepare Image to load.");
                    return;
                }

                @Override
                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                    Utils.psLog("inside onBitmapLoaded ");

                    try {
                        File file;

                        file = new File(Environment.getExternalStorageDirectory() + "/" + fileName);

                        file.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                        ostream.close();
                        Utils.psLog("Success Image Loaded.");

                        refreshProfile();

                    } catch (Exception e) {
                        Utils.psErrorLogE(e.getMessage(), e);
                    }

                }

                @Override
                public void onBitmapFailed(Drawable arg0) {
                    Utils.psLog("Fail Fail Fail");
                    return;
                }
            };

            Utils.psLog("profile photo : " + Config.APP_IMAGES_URL + path);
            Picasso.with(this)
                    //.load("http://www.cindyomidi.net/wp-content/uploads/2015/03/Fine-art1.jpg")
                    .load(Config.APP_IMAGES_URL + path)
                    .resize(150,150)
                    .into(target);
        }

    }
    /**------------------------------------------------------------------------------------------------
     * End Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Enum
     **------------------------------------------------------------------------------------------------*/
    private enum FABActions {
        PROFILE
    }
    /**------------------------------------------------------------------------------------------------
     * End Block - Enum
     **------------------------------------------------------------------------------------------------*/

}
