package com.panaceasoft.citiesdirectory.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.GlobalData;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.listeners.SelectListener;
import com.panaceasoft.citiesdirectory.models.PImageData;
import com.panaceasoft.citiesdirectory.models.PItemAttributeData;
import com.panaceasoft.citiesdirectory.models.PItemAttributeDetailData;
import com.panaceasoft.citiesdirectory.models.PItemData;
import com.panaceasoft.citiesdirectory.models.PReviewData;
import com.panaceasoft.citiesdirectory.uis.PSPopupSingleSelectView;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import com.squareup.picasso.Picasso;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.google.android.gms.maps.MapsInitializer;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class DetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView detailImage;
    private TextView txtTitle;
    private ArrayList<PImageData> itemImageData;
    private ArrayList<PReviewData> itemReviewData;
    private ArrayList<PItemAttributeData> itemAttributeData;
    private ArrayList<PItemAttributeDetailData> itemAttributeDetailData;
    private List<PSPopupSingleSelectView> psPopupSingleSelectViews = new ArrayList<PSPopupSingleSelectView>();
    private MapView mMapView;
    private GoogleMap googleMap;
    private SharedPreferences pref;
    private TextView txtLikeCount ;
    private TextView txtReviewCount ;
    private TextView txtItemPrice ;
    private TextView txtTotalReview ;
    private TextView txtReviewMessage ;
    private TextView txtNameTime ;
    private TextView txtShopName ;
    private TextView txtAddress ;
    private TextView txtPhone ;
    private TextView txtEmail ;
    private TextView txtDescription ;
    private TextView title ;
    private ImageView userPhoto ;
    private Button btnMoreReview ;
    private FloatingActionButton fab;
    private int selected_item_id;
    private int selected_shop_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        // Get Data for City Data
        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        initUI();

        loadData();

        setupToolbar();

        initilizeMap(savedInstanceState);

    }

    private void initUI() {

        //popupContainer = (LinearLayout) findViewById(R.id.choose_container);
        txtLikeCount = (TextView) findViewById(R.id.total_like_count);
        txtReviewCount = (TextView) findViewById(R.id.total_review_count);
        //txtItemPrice = (TextView) findViewById(R.id.item_price);
        txtTotalReview = (TextView) findViewById(R.id.total_review);
        txtReviewMessage = (TextView) findViewById(R.id.review_message);
        txtNameTime = (TextView) findViewById(R.id.name_time);
        //txtShopName = (TextView) findViewById(R.id.shop_name);
        txtAddress = (TextView) findViewById(R.id.address);
        txtPhone = (TextView) findViewById(R.id.phone);
        txtEmail = (TextView) findViewById(R.id.mail);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        title = (TextView) findViewById(R.id.title);
        userPhoto = (ImageView) findViewById(R.id.user_photo);
        detailImage = (ImageView) findViewById(R.id.detail_image);
        txtTitle = (TextView) findViewById(R.id.title);
        btnMoreReview = (Button) findViewById(R.id.btn_more_review);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GlobalData.itemData.like_count = GlobalData.itemData.like_count + 1;
                //refreshData();
                doFavourite(v);
            }
        });


    }

    private void loadData() {

        selected_item_id = getIntent().getIntExtra("selected_item_id", 0);
        selected_shop_id = getIntent().getIntExtra("selected_shop_id", 0);
        requestData(Config.APP_API_URL + Config.ITEMS_BY_ID + selected_item_id + "/shop_id/" + selected_shop_id);

    }

    private void refreshData(){

        setupCollapsingToolbarLayout();

        setupTitle();

        setupToolbarImage();

        setupCountValues();

        //setupPrice();

        setupDescription();

        //setupPopUP();

        setupShopInfo();

        setupReview();

    }

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
        intent = new Intent(this,InquiryActivity.class);
        startActivity(intent);
    }

    public void doReview(View view) {
        Intent intent = new Intent(this,ReviewListActivity.class);
        intent.putExtra("selected_item_id", selected_item_id);
        intent.putExtra("selected_shop_id", selected_shop_id);

        // Testing Code
        ArrayList<PItemData> PID = new ArrayList<PItemData>();
        PItemData p = GlobalData.itemData;
        PID.add(p);
        PID.add(p);
        PID.add(p);
        intent.putParcelableArrayListExtra("list", PID);

        intent.putExtra("obj", p);
        startActivityForResult(intent, 1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Utils.psLog("OnActivityResult");
        if(requestCode == 1){

            if(resultCode == RESULT_OK){
                refreshData();
            }

        }

    }

    @Override
    public void onBackPressed() {
        // Code to do refresh Data in Detail Activity
        Intent in = new Intent();
        in.putExtra("selected_item_id", GlobalData.itemData.id);
        in.putExtra("like_count", GlobalData.itemData.like_count);
        in.putExtra("review_count", GlobalData.itemData.review_count);
        setResult(RESULT_OK, in);

        // Clear Global Data
        GlobalData.itemData = null;

        finish();
        return;
    }

    // Private Functions

    private void setupCollapsingToolbarLayout() {

        if(Utils.isAndroid_5_0()){
            txtTitle.setPadding((int) this.getResources().getDimension(R.dimen.app_bar_title_padding_left), (int) (this.getResources().getDimension(R.dimen.app_bar_title_padding_bottom) - 25), 0, 0);
            txtTitle.requestLayout();
        }

    }

    private void requestData(String uri) {
        Utils.psLog("Item Detail " + uri);
        StringRequest request = new StringRequest(uri,

                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Gson gson = new Gson();
                        Type listType = new TypeToken<PItemData>() {}.getType();
                        GlobalData.itemData = (PItemData) gson.fromJson(response, listType);

                        if(GlobalData.itemData != null){
                            refreshData();
                        }
                        //String a = it.reviews.get(0).review;

                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError ex) {
                        Log.d("Volley Error " , ex.getMessage());
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(this.getApplicationContext());
        queue.add(request);
    }

    private void setupToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if(Utils.isAndroid_5_0()){
            //substitute parameters for left, top, right, bottom
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

    private void setupTitle(){
        title.setText(GlobalData.itemData.name);
    }

    private void setupToolbarImage() {

        detailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        // Load Detail Image
        Utils.psLog(" Detial Cover Image : " + GlobalData.itemData.images.get(0).path);
        Picasso.with(getApplicationContext()).load(Config.APP_IMAGES_URL + GlobalData.itemData.images.get(0).path).into(detailImage);

    }

    private void openGallery(){
        startActivity(new Intent(DetailActivity.this, GalleryActivity.class));

    }

    private void setupPrice() {
        //txtItemPrice.setText(Float.toString(GlobalData.itemData.unit_price) + " " + pref.getString("_currency_symbol", "") + "(" + pref.getString("_currency_short_form", "") + ")");
    }

    private void setupDescription() {
        txtDescription.setText(GlobalData.itemData.description);
    }

    private void setupPopUP() {
        //To get attribute & detail data
        //popupContainer.removeAllViews();

        itemAttributeData = GlobalData.itemData.attributes;
        if(itemAttributeData.size() > 0) {
            for(PItemAttributeData attData : itemAttributeData) {
                Utils.psLog(attData.name);

                PSPopupSingleSelectView ps1 = setupPopUpUI(attData.name, "Please Select " + attData.name, attData.details);
                ps1.setOnSelectListener(new SelectListener() {

                    @Override
                    public void Select(View view, int position, CharSequence text) {
                        Utils.psLog(" Selected PS 1" + text);
                    }

                    @Override
                    public void Select(View view, int position, CharSequence text, int id) {

                    }

                    @Override
                    public void Select(View view, int position, CharSequence text, int id, float additionalPrice) {
                        Utils.psLog(" Selected PS 1 . ID " + id + " Price " + additionalPrice);
                    }

                });
                psPopupSingleSelectViews.add(ps1);

                // PS2 is the demo data only
                // Need to remove in actual project.
                // There is no two popup. That why try to create to become two pop up.
                PSPopupSingleSelectView ps2 = setupPopUpUI(attData.name, "Please Select " + attData.name, attData.details);
                ps2.setOnSelectListener(new SelectListener() {

                    @Override
                    public void Select(View view, int position, CharSequence text) {
                        Utils.psLog(" Selected PS 2" + text);
                    }

                    @Override
                    public void Select(View view, int position, CharSequence text, int id) {

                    }

                    @Override
                    public void Select(View view, int position, CharSequence text, int id, float additionalPrice) {
                        Utils.psLog(" Selected PS 2 . ID " + id + " Price " + additionalPrice);
                    }

                });
                psPopupSingleSelectViews.add(ps2);
                // END PS2

            }
        }
    }

    private void setupShopInfo() {

        //Get Preference

        //txtShopName.setText(pref.getString("_name", ""));

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
        }catch (Exception e){
            Utils.psLog("Error in map initialize.");
        }

    }

    private void setupCountValues() {

        txtLikeCount.setText(" " + GlobalData.itemData.like_count + " ");

        txtReviewCount.setText(" " + GlobalData.itemData.review_count + " ");

    }

    private void setupReview() {
        itemReviewData = GlobalData.itemData.reviews;

        View view = null;
        txtNameTime.setVisibility(View.VISIBLE);
        txtReviewMessage.setVisibility(View.VISIBLE);
        btnMoreReview.setText(getString(R.string.view_more_review));

        int i = 0;
        if(itemReviewData.size() > 0) {
            if(itemReviewData.size() == 1) {
                txtTotalReview.setText(itemReviewData.size() + " " + getString(R.string.review));
            } else {
                txtTotalReview.setText(itemReviewData.size() + " " + getString(R.string.reviews));
            }


            for (PReviewData reviewData : itemReviewData) {
                if(i == 0) {
                    txtNameTime.setText(reviewData.appuser_name + " " + "(" + reviewData.added + ")");
                    txtReviewMessage.setText(reviewData.review);
                    if (!reviewData.profile_photo.equals("")) {
                        Picasso.with(this).load(Config.APP_IMAGES_URL + reviewData.profile_photo).into(userPhoto);
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
            userPhoto.setColorFilter(Color.argb(-1, 114, 114, 114));


        }
    }

    private void initilizeMap(Bundle savedInstanceState) {

        if(Utils.isGooglePlayServicesOK(this)) {

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

    private PSPopupSingleSelectView setupPopUpUI(String mainTitle, String defaultTitle, ArrayList<PItemAttributeDetailData> itemAttributeDetailData) {

        TextView tv1 = new TextView(this);
        tv1.setTypeface(null, Typeface.BOLD);
        tv1.setPadding(0, 8, 0, 0);
        tv1.setText(mainTitle);

        //popupContainer.addView(tv1);

        PSPopupSingleSelectView psPopupSingleSelectView = new PSPopupSingleSelectView(this, defaultTitle, itemAttributeDetailData);

        //popupContainer.addView(psPopupSingleSelectView);

        //((LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.ui_line, popupContainer);

//        PSPopupSingleSelectView psPopupSingleSelectView = (PSPopupSingleSelectView) findViewById(R.id.size_chooser);
//        //psPopupSingleSelectView.setItems(size);
//        psPopupSingleSelectView.setItemsWithItemObject(itemAttributeDetailData);
//        psPopupSingleSelectView.setOnSelectListener(new SelectListener() {
//
//            @Override
//            public void Select(View view, int position, CharSequence text) {
//                Utils.psLog(" Selected " + text);
//            }
//
//        });

        return psPopupSingleSelectView;

    }

    public void doFavourite(View view) {
        if(pref.getInt("_login_user_id",0) != 0) {
            final String URL = Config.APP_API_URL + Config.POST_ITEM_FAVOURITE + GlobalData.itemData.id;
            Utils.psLog(URL);
            HashMap<String, String> params = new HashMap<>();
            params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
            params.put("shop_id", String.valueOf(pref.getInt("_id", 0)));
            doSubmit(URL, params, view);
        } else {
            showNeedLogin();
        }
    }

    public void doLike(View view) {

        if(pref.getInt("_login_user_id",0) != 0) {
            final String URL = Config.APP_API_URL + Config.POST_ITEM_LIKE + GlobalData.itemData.id;
            Utils.psLog(URL);
            HashMap<String, String> params = new HashMap<>();
            params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
            params.put("shop_id", String.valueOf(pref.getInt("_id", 0)));
            doSubmit(URL, params, view);
        } else {
            showNeedLogin();
        }

    }

    private void doSubmit(String postURL, HashMap<String, String> params, final View view) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String success_status = response.getString("success");

                            if(success_status != null){
                                //showSuccessPopup();
                                // txtLikeCount.setText(" " + GlobalData.itemData.like_count + " ");
                                Utils.psLog("Count From Server : " + response.getString("total"));
                                GlobalData.itemData.like_count = response.getString("total");
                                //GlobalData.itemData.like_count = response.getInt("total");
                                txtLikeCount.setText(" " + GlobalData.itemData.like_count + " ");
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
                .title(R.string.like)
                .content(R.string.like_fail)
                .positiveText(R.string.OK)
                .show();
    }

    private void showNeedLogin() {
        new MaterialDialog.Builder(this)
                .title(R.string.like)
                .content(R.string.like_need_auth)
                .positiveText(R.string.OK)
                .show();
    }

    public void doShare(View view) {
        String message = String.valueOf(R.string.share_message);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(share, "Please choose"));
    }

//    private void prepareData() {
//        //Prepare Data
//
//        // Get Data for City Data
//        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//
//        // Prepare for selected item data
//        wrap = (ItemDataWrapper) getIntent().getSerializableExtra("selected_item");
//        itemReviewData = wrap.getItem().getReviewData();
//
//        GlobalData.itemData = wrap.getItem();
//
//        Utils.psLog(GlobalData.itemData.getName());
//
//        //To get images data
//        itemImageData = wrap.getItem().getImageData();
//        if(itemImageData.size() > 0) {
//            Utils.psLog("......... Item Image ...........");
//            for (ImageData imageData : itemImageData) {
//                Utils.psLog(imageData.getPath());
//            }
//        }
//
//
//
//        //To get attribute & detail data
//        itemAttributeData = wrap.getItem().getItemAttributeData();
//        if(itemAttributeData.size() > 0) {
//            for(ItemAttributeData attData : itemAttributeData) {
//                Utils.psLog(attData.getName());
//
//                itemAttributeDetailData = attData.getDetails();
//                if(itemAttributeDetailData.size() > 0) {
//                    for(ItemAttributeDetailData attDetail : itemAttributeDetailData) {
//                        Utils.psLog(attDetail.getName());
//                    }
//
//                }
//
//            }
//        }
//    }
}


//        getRatingBar = (RatingBar) findViewById(R.id.getRating);
//        setRatingBar = (RatingBar) findViewById(R.id.setRating);
//        countText = (TextView) findViewById(R.id.countText);
//
//        getRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                ratingChanged(ratingBar, rating, fromUser);
//            }
//        });

//    public void ratingChanged(RatingBar ratingBar, float rating, boolean fromUser){
//        DecimalFormat decimalFormat = new DecimalFormat("#.#");
//        curRate = Float.valueOf(decimalFormat.format((curRate * count + rating)/ ++count));
//
//        setRatingBar.setRating(curRate);
//        countText.setText(count + " Ratings");
//    }