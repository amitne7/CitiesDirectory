package com.panaceasoft.citiesdirectory.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.GlobalData;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.models.PImageData;
import com.panaceasoft.citiesdirectory.models.PItemData;
import com.panaceasoft.citiesdirectory.models.PReviewData;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class DetailActivity extends AppCompatActivity {

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Private Variables
    //-------------------------------------------------------------------------------------------------------------------------------------
    private Toolbar toolbar;
    private ImageView detailImage;
    private TextView txtTitle;
    private ArrayList<PImageData> itemImageData;
    private ArrayList<PReviewData> itemReviewData;
    private MapView mMapView;
    private GoogleMap googleMap;
    private SharedPreferences pref;
    private TextView txtLikeCount;
    private TextView txtReviewCount;
    private TextView txtItemPrice;
    private TextView txtTotalReview;
    private TextView txtReviewMessage;
    private TextView txtNameTime;
    private TextView txtShopName;
    private TextView txtAddress;
    private TextView txtPhone;
    private TextView txtEmail;
    private TextView txtDescription;
    private TextView title;
    private ImageView userPhoto;
    private Button btnLike;
    private Button btnMoreReview;
    private Button btnInquiry;
    private FloatingActionButton fab;
    private int selectedItemId;
    private int selectedCityId;
    private Bundle bundle;
    private Intent intent;
    private Boolean isFavourite = false;
    private RatingBar getRatingBar;
    private RatingBar setRatingBar;
    private TextView ratingCount;
    private Animation animation;
    private String jsonStatusSuccessString;

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Private Variables
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        initData();

        initUI(savedInstanceState);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Utils.psLog("OnActivityResult");
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                bindData();
            }
        }
    }

    @Override
    public void onBackPressed() {
        try {
            Intent in = new Intent();
            in.putExtra("selected_item_id", GlobalData.itemData.id);
            in.putExtra("like_count", GlobalData.itemData.like_count);
            in.putExtra("review_count", GlobalData.itemData.review_count);
            setResult(RESULT_OK, in);

            GlobalData.itemData = null;

            finish();
            overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
        }catch (Exception e){
            Utils.psErrorLogE("Error in BackPress.", e);
            finish();
            return;
        }
        return;
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Init Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void initData() {
        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        selectedItemId = getIntent().getIntExtra("selected_item_id", 0);
        selectedCityId = getIntent().getIntExtra("selected_city_id", 0);
        requestData(Config.APP_API_URL + Config.ITEMS_BY_ID + selectedItemId + "/city_id/" + selectedCityId);
        jsonStatusSuccessString = getResources().getString(R.string.json_status_success);

        updateTouchCount(selectedItemId);
    }

    private void updateTouchCount(int selectedItemId) {
        try {
            final String URL = Config.APP_API_URL + Config.POST_TOUCH_COUNT + selectedItemId;
            Utils.psLog(URL);
            HashMap<String, String> params = new HashMap<>();
            params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
            params.put("city_id", String.valueOf(pref.getInt("_id", 0)));
            doSubmit(URL, params, "touch");
        }catch (Exception e){
            Utils.psErrorLogE("Error in Touch Count.", e);
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion init Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // init UI Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void initUI(Bundle savedInstanceState) {

        try {
            initToolbar();

            btnLike = (Button) findViewById(R.id.btn_like);
            btnLike.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            txtLikeCount = (TextView) findViewById(R.id.total_like_count);
            txtLikeCount.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            txtReviewCount = (TextView) findViewById(R.id.total_review_count);
            txtReviewCount.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            txtTotalReview = (TextView) findViewById(R.id.total_review);
            txtTotalReview.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            txtReviewMessage = (TextView) findViewById(R.id.review_message);
            txtReviewMessage.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            txtNameTime = (TextView) findViewById(R.id.name_time);
            txtNameTime.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            txtAddress = (TextView) findViewById(R.id.address);
            txtAddress.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            txtPhone = (TextView) findViewById(R.id.phone);
            txtPhone.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            txtEmail = (TextView) findViewById(R.id.mail);
            txtPhone.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            txtDescription = (TextView) findViewById(R.id.txtDescription);
            txtDescription.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            txtTitle = (TextView) findViewById(R.id.title);
            txtTitle.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            title = (TextView) findViewById(R.id.title);
            title.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            userPhoto = (ImageView) findViewById(R.id.user_photo);
            detailImage = (ImageView) findViewById(R.id.detail_image);

            btnMoreReview = (Button) findViewById(R.id.btn_more_review);
            btnMoreReview.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            btnInquiry = (Button) findViewById(R.id.btn_inquiry);
            btnInquiry.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

            getRatingBar = (RatingBar) findViewById(R.id.get_rating);
            setRatingBar = (RatingBar) findViewById(R.id.set_rating);
            ratingCount = (TextView) findViewById(R.id.rating_count);
            animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.pop_out);


            fab = (FloatingActionButton) findViewById(R.id.fab);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doFavourite(v);

                    Utils.psLog("Start Animation.");


                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            Utils.psLog("Started Animation.");
                            if (isFavourite) {
                                isFavourite = false;
                                fab.setImageResource(R.drawable.ic_favorite_border);
                            } else {
                                isFavourite = true;
                                fab.setImageResource(R.drawable.ic_favorite_white);
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }


                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    fab.clearAnimation();
                    fab.startAnimation(animation);
                }
            });

            btnLike.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    doLike(v);

                    Animation rotate = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fab_in);
                    btnLike.startAnimation(rotate);
                    rotate.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }
            });

            getRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    if (pref.getInt("_login_user_id", 0) != 0) {
                        ratingChanged(ratingBar, rating, fromUser);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.login_required,
                                Toast.LENGTH_LONG).show();
                        getRatingBar.setRating(0);
                    }
                }
            });

            initCollapsingToolbarLayout();

            initilizeMap(savedInstanceState);

            AdView mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

            AdView mAdView2 = (AdView) findViewById(R.id.adView2);
            AdRequest adRequest2 = new AdRequest.Builder().build();
            mAdView2.loadAd(adRequest2);

        }catch(Exception e){
            Utils.psErrorLogE("Error in Init UI.", e);
        }

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Utils.isAndroid_5_0()) {
            Utils.setMargins(toolbar, 0, -78, 0, 0);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initCollapsingToolbarLayout() {
        if (Utils.isAndroid_5_0()) {
            txtTitle.setPadding((int) this.getResources().getDimension(R.dimen.app_bar_title_padding_left), (int) (this.getResources().getDimension(R.dimen.app_bar_title_padding_bottom) - 25), 0, 0);
            txtTitle.requestLayout();
        }
    }

    private void initilizeMap(Bundle savedInstanceState) {
        if (Utils.isGooglePlayServicesOK(this)) {
            mMapView = (MapView) findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume();
            try {
                MapsInitializer.initialize(getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion init UI Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Bind Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void bindData() {
        bindTitle();
        bindToolbarImage();
        bindCountValues();
        bindReview();
        bindDescription();
        bindShopInfo();
        bindFavourite(fab);
        bindLike(txtLikeCount);
        bindRate();
    }

    private void bindTitle() {
        title.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));
        title.setText(GlobalData.itemData.name);
    }

    private void bindToolbarImage() {
        try {
            detailImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGallery();
                }
            });
            Picasso.with(getApplicationContext()).load(Config.APP_IMAGES_URL + GlobalData.itemData.images.get(0).path).into(detailImage);
        }catch(Exception e){
            Utils.psErrorLogE("Error in Bind Toolbar Image.", e);
        }
    }

    private void bindCountValues() {
        try {
            txtLikeCount.setText(" " + GlobalData.itemData.like_count + " ");
            txtReviewCount.setText(" " + GlobalData.itemData.review_count + " ");
        }catch(Exception e){
            Utils.psErrorLogE("Error in Bind Count.", e);
        }
    }

    private void bindReview() {
        try {
            itemReviewData = GlobalData.itemData.reviews;

            View view = null;
            txtNameTime.setVisibility(View.VISIBLE);
            txtReviewMessage.setVisibility(View.VISIBLE);
            btnMoreReview.setText(getString(R.string.view_more_review));

            int i = 0;
            if (itemReviewData.size() > 0) {
                if (itemReviewData.size() == 1) {
                    txtTotalReview.setText(itemReviewData.size() + " " + getString(R.string.review));
                } else {
                    txtTotalReview.setText(itemReviewData.size() + " " + getString(R.string.reviews));
                }


                for (PReviewData reviewData : itemReviewData) {
                    if (i == 0) {
                        txtNameTime.setText(reviewData.appuser_name + " " + "(" + reviewData.added + ")");
                        txtReviewMessage.setText(reviewData.review);
                        if (!reviewData.profile_photo.equals("")) {
                            Utils.psLog(" Loading User photo : " + Config.APP_IMAGES_URL + reviewData.profile_photo);
                            Picasso.with(this).load(Config.APP_IMAGES_URL + reviewData.profile_photo).resize(150, 150).into(userPhoto);
                        } else {
                            userPhoto.setColorFilter(Color.argb(114, 114, 114, 114));
                        }
                        i++;
                    }
                    break;
                }


            } else {
                txtTotalReview.setText(getString(R.string.no_review_count));
                txtNameTime.setVisibility(view.GONE);
                txtReviewMessage.setVisibility(view.GONE);
                btnMoreReview.setText(getString(R.string.add_first_review));

                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_rate_review_black);
                userPhoto.setImageDrawable(myDrawable);
                //userPhoto.setColorFilter(Color.argb(-1, 114, 114, 114));

            }
        }catch (Exception e){
            Utils.psErrorLogE("Error in Bind Reviews.", e);
        }
    }

    private void bindDescription() {
        txtDescription.setText(GlobalData.itemData.description);
    }

    private void bindShopInfo() {

        txtAddress.setText(GlobalData.itemData.address);
        txtPhone.setText(GlobalData.itemData.phone);
        txtEmail.setText(GlobalData.itemData.email);

        try {
            googleMap = mMapView.getMap();
            double latitude = Double.parseDouble(GlobalData.itemData.lat);
            double longitude = Double.parseDouble(GlobalData.itemData.lng);

            MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(GlobalData.itemData.name);
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(latitude, longitude)).zoom(15).build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            googleMap.addMarker(marker);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in map initialize.", e);
        }

    }

    private void bindFavourite(FloatingActionButton fab) {
        try {
            if (pref.getInt("_login_user_id", 0) != 0) {
                final String URL = Config.APP_API_URL + Config.GET_FAVOURITE + GlobalData.itemData.id;
                Utils.psLog(URL);
                HashMap<String, String> params = new HashMap<>();
                params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
                params.put("city_id", String.valueOf(pref.getInt("_id", 0)));
                getFavourite(URL, params, fab);
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in Bind Favourite.", e);
        }
    }

    public void bindLike(View view) {
        try {
            if (pref.getInt("_login_user_id", 0) != 0) {
                final String URL = Config.APP_API_URL + Config.GET_LIKE + GlobalData.itemData.id;
                Utils.psLog(URL);
                HashMap<String, String> params = new HashMap<>();
                params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
                params.put("city_id", String.valueOf(pref.getInt("_id", 0)));
                getLike(URL, params, view);
            }
        }catch (Exception e){
            Utils.psErrorLogE("Error in Bind Like. ", e);
        }
    }

    public void bindRate() {
        try {
            final String URL = Config.APP_API_URL + Config.POST_ITEM_IS_RATE + GlobalData.itemData.id;
            Utils.psLog(URL);
            HashMap<String, String> params = new HashMap<>();
            params.put("city_id", String.valueOf(pref.getInt("_id", 0)));
            getRate(URL, params);
        }catch (Exception e){
            Utils.psErrorLogE("Error in bind Rating", e);
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Bind Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void getFavourite(String postURL, HashMap<String, String> params, final FloatingActionButton fab) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String success_status = response.getString("status");
                            String data_status = response.getString("data");
                            if (success_status.equals(jsonStatusSuccessString)) {
                                if(data_status.toString().equals("yes")) {
                                    isFavourite = true;
                                    fab.setImageResource(R.drawable.ic_favorite_white);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        // add the request object to the queue to be executed
        mRequestQueue.add(req);
    }

    private void getLike(String postURL, HashMap<String, String> params, final View view) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String success_status = response.getString("status");

                            if (success_status.equals(jsonStatusSuccessString)) {
                                txtLikeCount.setText(response.getString("total"));
                                btnLike.setBackgroundResource(R.drawable.ic_done);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        // add the request object to the queue to be executed
        mRequestQueue.add(req);
    }

    private void requestData(String uri) {
        Utils.psLog("Item Detail " + uri);
        JsonObjectRequest request = new JsonObjectRequest(uri,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccessString)) {

                                Gson gson = new Gson();
                                Type listType = new TypeToken<PItemData>() {
                                }.getType();
                                GlobalData.itemData = (PItemData) gson.fromJson(response.getString("data"), listType);

                                if (GlobalData.itemData != null) {
                                    bindData();
                                }

                            } else {

                                Utils.psLog("Error in loading.");
                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError ex) {
                        Log.d("Volley Error ", ex.getMessage());
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(this.getApplicationContext());
        queue.add(request);
    }

    private void openGallery() {
        bundle = new Bundle();
        bundle.putParcelable("images", GlobalData.itemData);
        bundle.putString("from", "item");

        intent = new Intent(getApplicationContext(), GalleryActivity.class);
        intent.putExtra("images_bundle", bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }

    private void getRate(String postURL, HashMap<String, String> params) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String success_status = response.getString("status");

                            if (success_status.equals(jsonStatusSuccessString)) {
                                setRatingBar.setRating(Float.parseFloat(response.getString("total")));
                                if (Float.parseFloat(response.getString("total")) != 0.0) {
                                    ratingCount.setText("Total Rating : " + response.getString("total"));
                                }
                            } else {
                                ratingCount.setText(getString(R.string.first_rating));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        // add the request object to the queue to be executed
        mRequestQueue.add(req);
    }

    private void doSubmit(String postURL, HashMap<String, String> params, final String fromWhere) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccessString)) {

                                if (fromWhere.toString().equals("like")) {
                                    Utils.psLog("Count From Server : " + response.getString("data"));
                                    GlobalData.itemData.like_count = response.getString("data");
                                    txtLikeCount.setText(" " + GlobalData.itemData.like_count + " ");
                                }
                            } else {
                                showFailPopup();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        // add the request object to the queue to be executed
        mRequestQueue.add(req);
    }

    private void showFailPopup() {
        new MaterialDialog.Builder(this)
                .title(R.string.sorry_title)
                .content(R.string.like_fail)
                .positiveText(R.string.OK)
                .show();
    }

    private void showRatingFailPopup() {
        new MaterialDialog.Builder(this)
                .title(R.string.sorry_title)
                .content(R.string.rating_fail)
                .positiveText(R.string.OK)
                .show();
    }

    private void ratingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

        Utils.psLog(String.valueOf(rating));

        final String URL = Config.APP_API_URL + Config.POST_ITEM_RATING + selectedItemId;
        Utils.psLog(URL);
        HashMap<String, String> params = new HashMap<>();
        params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
        params.put("rating", String.valueOf(rating));
        params.put("city_id", String.valueOf(pref.getInt("_id", 0)));

        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String success_status = response.getString("status");

                            if (success_status.equals(jsonStatusSuccessString)) {
                                setRatingBar.setRating(Float.parseFloat(response.getString("rating")));
                                ratingCount.setText("Total Rating : " + response.getString("rating"));
                            } else {
                                showRatingFailPopup();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               Utils.psErrorLogE("Error in rating Change.", error);
            }
        });

        // add the request object to the queue to be executed
        mRequestQueue.add(req);

    }

    private void showNeedLogin() {
        new MaterialDialog.Builder(this)
                .title(R.string.sorry_title)
                .content(R.string.login_required)
                .positiveText(R.string.OK)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        Intent intent = new Intent(getApplicationContext(), UserLoginActivity.class);
                        startActivity(intent);
                    }
                })
                .show();
    }

    // Method to share either text or URL.
    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "CityDirectory");

        share.putExtra(Intent.EXTRA_TEXT, "http://codecanyon.net/user/panacea-soft/portfolio");

        startActivity(Intent.createChooser(share, "Share link!"));
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Public Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    public void doPhoneCall(View view) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + pref.getString("_phone", "")));
        startActivity(intent);
    }

    public void doEmail(View view) {
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{pref.getString("_email", "")});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hello");
        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    public void doInquiry(View view) {
        final Intent intent;
        intent = new Intent(this, InquiryActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }

    public void doReview(View view) {
        if (itemReviewData.size() > 0) {
            Intent intent = new Intent(this, ReviewListActivity.class);
            intent.putExtra("selected_item_id", selectedItemId);
            intent.putExtra("selected_city_id", selectedCityId);
            startActivityForResult(intent, 1);
            overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
        } else {
            if (pref.getInt("_login_user_id", 0) != 0) {
                Intent intent = new Intent(this, ReviewEntry.class);
                intent.putExtra("selected_item_id", selectedItemId);
                intent.putExtra("selected_city_id", selectedCityId);
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
            } else {
                Intent intent = new Intent(this, UserLoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
            }
        }
    }

    public void doFavourite(View view) {
        if (pref.getInt("_login_user_id", 0) != 0) {
            final String URL = Config.APP_API_URL + Config.POST_ITEM_FAVOURITE + GlobalData.itemData.id;
            Utils.psLog(URL);
            HashMap<String, String> params = new HashMap<>();
            params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
            params.put("city_id", String.valueOf(pref.getInt("_id", 0)));
            doSubmit(URL, params, "favourite");
        } else {
            if (isFavourite) {
                isFavourite = false;
                fab.setImageResource(R.drawable.ic_favorite_border);
            } else {
                isFavourite = true;
                fab.setImageResource(R.drawable.ic_favorite_white);
            }
            showNeedLogin();
        }
    }

    public void doLike(View view) {
        if (pref.getInt("_login_user_id", 0) != 0) {
            final String URL = Config.APP_API_URL + Config.POST_ITEM_LIKE + GlobalData.itemData.id;
            Utils.psLog(URL);
            HashMap<String, String> params = new HashMap<>();
            params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
            params.put("city_id", String.valueOf(pref.getInt("_id", 0)));
            doSubmit(URL, params, "like");
        } else {
            showNeedLogin();
        }
    }

    public void doShare(View view) {

        shareTextUrl();

    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Public Functions
    //-------------------------------------------------------------------------------------------------------------------------------------


}