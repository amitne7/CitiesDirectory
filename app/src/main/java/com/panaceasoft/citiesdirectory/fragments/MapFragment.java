package com.panaceasoft.citiesdirectory.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.activities.DetailActivity;
import com.panaceasoft.citiesdirectory.adapters.MapPopupAdapter;
import com.panaceasoft.citiesdirectory.listeners.GPSTracker;
import com.panaceasoft.citiesdirectory.models.PItemData;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.rey.material.widget.Slider;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class MapFragment extends Fragment {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/
    private GoogleMap googleMap;
    private Marker customMarker;
    private LatLng markerLatLng;
    private ProgressWheel progressWheel;
    private ArrayList<PItemData> items;
    private TextView display_message;
    private RequestQueue queue;
    private boolean checkingLatLng = false;
    private HashMap<String, Uri> images = new HashMap<String, Uri>();
    private HashMap<String, Uri> markerImages = new HashMap<String, Uri>();
    private HashMap<Marker, PItemData> markerInfo = new HashMap<Marker, PItemData>();
    private HashMap<String, String> markerAddress = new HashMap<String, String>();
    private SharedPreferences pref;
    private int selectedCityId;
    private int selectedSubCatId;
    private double selectedRegionLat;
    private double selectedRegionLng;
    private double currentLongitude;
    private double currentLatitude;
    View marker;
    MaterialDialog dialog;
    MapView mMapView;
    private String jsonStatusSuccessString;
    private SpannableString connectionErrorString;

    /**------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map, container, false);

        initData();

        initUI(v, inflater, container, savedInstanceState);

        return v;

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - INit data Functions
     **------------------------------------------------------------------------------------------------*/
    private void initData() {

        try {
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
            connectionErrorString = Utils.getSpannableString(getString(R.string.connection_error));
        }catch(Exception e){
            Utils.psErrorLogE("Error in init data.", e);
        }

    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/
    private void initUI(View v, LayoutInflater inflater, ViewGroup container,
                        Bundle savedInstanceState){
        if (Utils.isGooglePlayServicesOK(getActivity())) {
            Utils.psLog("Google Play Service is ready for Google Map");
            initFAB(v);
            initMessage(v);
            loadPreferenceData();
            loadMap(v, savedInstanceState, inflater, container);
            queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        } else {
            showNoServicePopup();
        }
    }

    private void initFAB(View v) {
        FloatingActionButton locationSearchFAB = (FloatingActionButton) v.findViewById(R.id.location_search_fab);

        locationSearchFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkingLatLng) {
                    if (readyLatLng()) {
                        showSearchPopup(view);
                    } else {
                        showWaitPopup();
                    }
                } else {

                    showSearchPopup(view);

                }
            }
        });

    }

    private void initMessage(View v) {
        display_message = (TextView) v.findViewById(R.id.display_message);
        display_message.setVisibility(v.GONE);

        progressWheel = (ProgressWheel) v.findViewById(R.id.progress_wheel);
        progressWheel.setVisibility(v.VISIBLE);
    }

    private void loadMap(View v, Bundle savedInstanceState, LayoutInflater inflater, ViewGroup container) {
        mMapView = (MapView) v.findViewById(R.id.mapView);

        ViewGroup.LayoutParams params = mMapView.getLayoutParams();
        params.height = Utils.getScreenHeight() - 100;
        mMapView.setLayoutParams(params);

        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        marker = inflater.inflate(R.layout.custom_marker, container, false);

        requestData(Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + selectedCityId + "/sub_cat_id/" + selectedSubCatId + "/item/all/", marker);

    }

    private void loadPreferenceData() {
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        selectedCityId = pref.getInt("_selected_city_id", 0);
        selectedSubCatId = pref.getInt("_selected_sub_cat_id", 0);
        selectedRegionLat = Double.parseDouble(pref.getString("_city_region_lat", ""));
        selectedRegionLng = Double.parseDouble(pref.getString("_city_region_lng", ""));
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - INit UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     **------------------------------------------------------------------------------------------------*/
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";

        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("Current Address ( ");
                Utils.psLog("Getting Address.");
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    if (i != returnedAddress.getMaxAddressLineIndex() - 1) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    } else {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("");
                    }
                }
                strReturnedAddress.append(" )");
                strAdd = strReturnedAddress.toString();
                Utils.psLog("My loction address --- " + "" + strReturnedAddress.toString());
            } else {
                Utils.psLog("My Current loction address" + "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.psLog("My Current loction address >>" + e.getMessage());
        }
        return strAdd;
    }

    private void requestData(String uri, final View marker) {
        JsonObjectRequest request = new JsonObjectRequest(uri,

                new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccessString)) {

                                progressWheel.setVisibility(View.GONE);
                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<PItemData>>() {
                                }.getType();
                                items = gson.fromJson(response.getString("data"), listType);

                                Utils.psLog("Total : " + items.size());

                                googleMap = mMapView.getMap();

                                for (final PItemData itd : items) {
                                    double latitude = Double.parseDouble(itd.lat);
                                    double longitude = Double.parseDouble(itd.lng);

                                    markerLatLng = new LatLng(latitude, longitude);

                                    customMarker = googleMap.addMarker(new MarkerOptions()
                                            .position(markerLatLng)
                                            .title(itd.name)
                                            .snippet(itd.description.substring(0, Math.min(itd.description.length(), 80)) + "...")
                                            .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActivity(), marker)))
                                            .anchor(0.5f, 1));
                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                            .target(new LatLng(selectedRegionLat, selectedRegionLng)).zoom(10).build();
                                    googleMap.animateCamera(CameraUpdateFactory
                                            .newCameraPosition(cameraPosition));


                                    if (markerImages != null) {
                                        markerImages.put(customMarker.getId(), Uri.parse(Config.APP_IMAGES_URL + itd.images.get(0).path));
                                    }

                                    if (markerInfo != null) {
                                        markerInfo.put(customMarker, itd);
                                    }

                                    if (markerAddress != null) {
                                        markerAddress.put(customMarker.getId(), itd.address);
                                    }

                                    googleMap.setInfoWindowAdapter(new MapPopupAdapter(getActivity(), getActivity().getLayoutInflater(), markerImages, markerAddress));
                                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(Marker marker) {
                                            marker.showInfoWindow();
                                            googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                                            return true;
                                        }
                                    });

                                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                        @Override
                                        public void onInfoWindowClick(Marker marker) {

                                            PItemData ct = markerInfo.get(marker);
                                            Utils.psLog("Selected Item Name : " + ct.name);
                                            final Intent intent;
                                            intent = new Intent(getActivity(), DetailActivity.class);
                                            intent.putExtra("selected_item_id", ct.id);
                                            intent.putExtra("selected_city_id", selectedCityId);
                                            startActivity(intent);

                                        }
                                    });
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
                        Log.d(">> Volley Error ", ex.getMessage() + "");
                        progressWheel.setVisibility(View.GONE);


                        NetworkResponse response = ex.networkResponse;
                        if (response != null && response.data != null) {


                        } else {
                            display_message.setVisibility(View.VISIBLE);
                            display_message.setText(connectionErrorString);

                        }

                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(request);
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Public Functions
     **------------------------------------------------------------------------------------------------*/
    public void showSearchPopup(View view) {
        checkingLatLng = false;
        boolean wrapInScrollView = true;
        dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.location_search_title)
                .customView(R.layout.slider, wrapInScrollView)
                .show();

        view = dialog.getCustomView();
        Button BtnSearch = (Button) view.findViewById(R.id.button_search);
        final TextView addressTextView = (TextView) view.findViewById(R.id.complete_address);
        getCurrentLocation(addressTextView);
        final Slider slider = (Slider) view.findViewById(R.id.location_slider);
        BtnSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.hide();
                Utils.psLog(String.valueOf(slider.getValue()));
                googleMap.clear();
                Utils.psLog(Config.APP_API_URL + Config.SEARCH_BY_GEO + slider.getValue() + "/userLat/" + currentLatitude + "/userLong/" + currentLongitude + "/city_id/" + selectedCityId + "/sub_cat_id/" + selectedSubCatId);
                requestData(Config.APP_API_URL + Config.SEARCH_BY_GEO + slider.getValue() + "/userLat/" + currentLatitude + "/userLong/" + currentLongitude + "/city_id/" + selectedCityId + "/sub_cat_id/" + selectedSubCatId, marker);

            }
        });
    }

    public void showWaitPopup() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.pls_wait)
                .content(R.string.gps_not_ready)
                .positiveText(R.string.OK)
                .show();
    }

    public void showNoServicePopup() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.sorry_title)
                .content(R.string.no_google_play)
                .positiveText(R.string.OK)
                .show();
    }

    public boolean readyLatLng() {
        GPSTracker gps = new GPSTracker(getActivity());

        if (gps.canGetLocation()) {
            currentLatitude = gps.getLatitude();
            currentLongitude = gps.getLongitude();
            if (currentLatitude != 0.0 && currentLongitude != 0.0) {
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }

    }

    public void getCurrentLocation(TextView tv) {

        GPSTracker gps = new GPSTracker(getActivity());

        if (gps.canGetLocation()) {
            currentLatitude = gps.getLatitude();
            currentLongitude = gps.getLongitude();

        } else {
            gps.showSettingsAlert();
            checkingLatLng = true;
            dialog.hide();
        }

        tv.setText(getCompleteAddressString(currentLatitude, currentLongitude));

    }

    public Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Public Functions
     **------------------------------------------------------------------------------------------------*/


}
