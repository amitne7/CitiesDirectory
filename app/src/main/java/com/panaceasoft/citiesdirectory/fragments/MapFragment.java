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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
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

import android.widget.RelativeLayout.LayoutParams;

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

    MapView mMapView;
    private GoogleMap googleMap;
    private Marker customMarker;
    private LatLng markerLatLng;

    private ProgressWheel progressWheel;
    private ArrayList<PItemData> sh;
    private TextView display_message;
    private RequestQueue queue;

    double currentLongitude;
    double currentLatitude;
    private Bitmap bm;
    View marker;
    MaterialDialog dialog;
    private boolean checkingLatLng = false;
    private HashMap<String, Uri> images=new HashMap<String, Uri>();
    private HashMap<String, Bitmap> markerImages =new HashMap<String, Bitmap>();
    private HashMap<Marker, PItemData> markerInfo = new HashMap<Marker, PItemData>();
    private HashMap<String, String> markerAddress = new HashMap<String, String>();
    private SharedPreferences pref;

    private int selectedCityId;
    private int selectedSubCatId;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map, container, false);
        if(Utils.isGooglePlayServicesOK(getActivity())) {
            Utils.psLog("Google Play Service is ready for Google Map");

            queue = Volley.newRequestQueue(getActivity().getApplicationContext());
            setUpFAB(v);

            display_message = (TextView) v.findViewById(R.id.display_message);
            display_message.setVisibility(v.GONE);

            mMapView = (MapView) v.findViewById(R.id.mapView);

            ViewGroup.LayoutParams params = mMapView.getLayoutParams();
            params.height = Utils.getScreenHeight(getActivity()) - 100;
            mMapView.setLayoutParams(params);

            mMapView.onCreate(savedInstanceState);

            mMapView.onResume();

            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            marker = inflater.inflate(R.layout.custom_marker, container, false);

            pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
            selectedCityId = pref.getInt("_selected_city_id", 0);
            selectedSubCatId = pref.getInt("_selected_sub_cat_id",0);

            //Log.d("TEAMPS", "URL : " + Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + selectedCityID + "/sub_cat_id/" + subCategoryData.id + "/item/all/");

            requestData(Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + selectedCityId + "/sub_cat_id/" + selectedSubCatId + "/item/all/", marker);

        } else {
            showNoServicePopup();
        }

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

    private void setUpFAB(View v) {
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
                Utils.psLog(String.valueOf(slider.getValue()));
                //mile = slider.getValue();
                Utils.psLog(Config.APP_API_URL + Config.SEARCH_BY_GEO + slider.getValue() + "/userLat/" + currentLatitude + "/userLong/" + currentLongitude + "/city_id/" + selectedCityId + "/sub_cat_id/" + selectedSubCatId);
                //Log.d("TEAMPS", "URL : " + Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + selectedCityID + "/sub_cat_id/" + subCategoryData.id + "/item/all/");
                googleMap.clear();
                requestData(Config.APP_API_URL + Config.SEARCH_BY_GEO + slider.getValue() + "/userLat/" + currentLatitude + "/userLong/" + currentLongitude + "/city_id/" + selectedCityId + "/sub_cat_id/" + selectedSubCatId, marker);
                dialog.hide();
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

        if(gps.canGetLocation()) {
            currentLatitude = gps.getLatitude();
            currentLongitude = gps.getLongitude();
            if(currentLatitude != 0.0 && currentLongitude != 0.0 ){
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

        if(gps.canGetLocation()) {
            currentLatitude = gps.getLatitude();
            currentLongitude = gps.getLongitude();

        } else {
            gps.showSettingsAlert();
            checkingLatLng = true;
            dialog.hide();
        }

        tv.setText(getCompleteAddressString(currentLatitude, currentLongitude));

    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";

        try {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            Utils.psLog(" Is present : " + geocoder.isPresent() + " = " + LATITUDE + " = " + LONGITUDE);
            if (addresses != null) {
                Utils.psLog("Address is not null. " + addresses.size());
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("Current Address ( ");

                Utils.psLog("Getting Address.");
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    if(i != returnedAddress.getMaxAddressLineIndex()-1){
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    } else {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("");
                    }
                }
                strReturnedAddress.append(" )");
                strAdd = strReturnedAddress.toString();
                Utils.psLog("My loction address --- "+ "" + strReturnedAddress.toString());
            } else {
                Utils.psLog("My Current loction address"+ "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.psLog("My Current loction address >>" + e.getMessage());
        }
        return strAdd;
    }



    // Convert a view to bitmap
    public static Bitmap createDrawableFromView(Context context, View view) {
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


    private void requestData(String uri, final View marker) {
        StringRequest request = new StringRequest(uri,

                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {

                        //progressWheel.setVisibility(View.GONE);
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<PItemData>>() {}.getType();
                        sh = gson.fromJson(response,listType);

                        Utils.psLog("Total : " + sh.size());

                        googleMap = mMapView.getMap();
                        Utils.psLog("City " + sh.get(0).name);

                        for (final PItemData s: sh){
                            Utils.psLog("Item Name : " + s.name);
                            //Utils.psLog("Img Path >" + Config.APP_IMAGES_URL + s.images.get(0).path);

                            final ImageView pin = (ImageView) marker.findViewById(R.id.img_pin);
                            try {
                                Utils.psLog("Img Path >" + Config.APP_IMAGES_URL + s.images.get(0).path);
                                ImageRequest request = new ImageRequest(Config.APP_IMAGES_URL + s.images.get(0).path,
                                        new Response.Listener<Bitmap>() {

                                            @Override
                                            public void onResponse(Bitmap arg0) {
                                                pin.setImageBitmap(arg0);
                                                //imageCache.put(shop.getId(), arg0);



                                                double latitude = Double.parseDouble(s.lat);
                                                double longitude = Double.parseDouble(s.lng);

                                                markerLatLng = new LatLng(latitude,longitude);

                                                customMarker = googleMap.addMarker(new MarkerOptions()
                                                        .position(markerLatLng)
                                                        .title(s.name)
                                                        .snippet(s.description.substring(0, Math.min(s.description.length(), 80)) + "...")
                                                        .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActivity(), marker)))
                                                        .anchor(0.5f, 1));
                                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                                        .target(new LatLng(Config.REGION_LAT, Config.REGION_LNG)).zoom(10).build();
                                                googleMap.animateCamera(CameraUpdateFactory
                                                        .newCameraPosition(cameraPosition));


                                                if(markerImages != null){
                                                    markerImages.put(customMarker.getId(),arg0);
                                                }

                                                if(markerInfo != null) {
                                                    markerInfo.put(customMarker, s);
                                                }

                                                if(markerAddress != null) {
                                                    markerAddress.put(customMarker.getId(),s.address);
                                                }

                                                googleMap.setInfoWindowAdapter(new MapPopupAdapter(getActivity(),getActivity().getLayoutInflater(), markerImages, markerAddress));
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
                                                        Utils.psLog("(: Ready to open Detail Page :)");
                                                        final Intent intent;
                                                        intent = new Intent(getActivity(), DetailActivity.class);
                                                        intent.putExtra("selected_item_id", ct.id);
                                                        intent.putExtra("selected_city_id", selectedCityId + "");
                                                        startActivity(intent);

                                                    }
                                                });

                                            }
                                        },
                                        80, 60,
                                        Bitmap.Config.ARGB_8888,

                                        new Response.ErrorListener() {

                                            @Override
                                            public void onErrorResponse(VolleyError arg0) {
                                                Log.d("Shop Adapter : ", arg0.getMessage());
                                            }
                                        }

                                );


                                queue.add(request);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.psLog(e.getMessage());
                            }


                        }


                    }
                },



                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError ex) {
                        Log.d(">> Volley Error ", ex.getMessage() + "");
                        //progressWheel.setVisibility(View.GONE);


                        NetworkResponse response = ex.networkResponse;
                        if(response != null && response.data != null){

                            // Log.d(">> Status Code " , String.valueOf(response.statusCode));

                        } else {
                            display_message.setVisibility(View.VISIBLE);
                            String message = getResources().getString(R.string.wrong_url);
                            display_message.setText(message);

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






}
