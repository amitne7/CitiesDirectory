package com.panaceasoft.citiesdirectory.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.GlobalData;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.activities.SelectedCityActivity;
import com.panaceasoft.citiesdirectory.adapters.CityAdapter;
import com.panaceasoft.citiesdirectory.listeners.ClickListener;
import com.panaceasoft.citiesdirectory.listeners.RecyclerTouchListener;
import com.panaceasoft.citiesdirectory.models.PCityData;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class CitiesListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ProgressWheel progressWheel;
    private CityAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView display_message;
    private ArrayList<PCityData> pCityDataList;
    private ArrayList<PCityData> pCityDataSet;

    private NestedScrollView singleLayout;
    private TextView scCityName;
    private TextView scCityLocation;
    private TextView scCityAbout;
    private TextView scCityCatCount;
    private TextView scCitySubCatCount;
    private TextView scCityItemCount;
    private ImageView scCityPhoto;
    private Button scCityExplore;

    public CitiesListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cities_list, container, false);

        initSingleUI(view);

        requestData(Config.APP_API_URL + Config.GET_ALL);

        setupSwipeRefreshLayout(view);

        setupProgressWheel(view);
        setupRecyclerView(view);

        startLoading();



        return view;
    }

    private void initSingleUI(View view) {
        singleLayout =(NestedScrollView) view.findViewById(R.id.single_city_layout);

        scCityName = (TextView) view.findViewById(R.id.sc_city_name);
        scCityLocation = (TextView) view.findViewById(R.id.sc_city_loc);
        scCityAbout = (TextView) view.findViewById(R.id.sc_city_desc);
        scCityCatCount = (TextView) view.findViewById(R.id.txt_cat_count);
        scCitySubCatCount = (TextView) view.findViewById(R.id.txt_sub_cat_count);
        scCityItemCount = (TextView) view.findViewById(R.id.txt_item_count);
        scCityPhoto = (ImageView) view.findViewById(R.id.sc_city_photo);
        scCityExplore = (Button) view.findViewById(R.id.button_explore);

        int screenWidth = Utils.getScreenWidth();

        int rlWidth = (screenWidth/3) - 20;

        RelativeLayout r1 = (RelativeLayout) view.findViewById(R.id.rl_count1);
        RelativeLayout r2 = (RelativeLayout) view.findViewById(R.id.rl_count2);
        RelativeLayout r3 = (RelativeLayout) view.findViewById(R.id.rl_count3);

        r1.setMinimumWidth(rlWidth);
        r2.setMinimumWidth(rlWidth);
        r3.setMinimumWidth(rlWidth);

        scCityPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(pCityDataList !=null && pCityDataList.size() > 0) {
                    final Intent intent;
                    intent = new Intent(getActivity(), SelectedCityActivity.class);
                    GlobalData.citydata = pCityDataList.get(0);
                    intent.putExtra("selected_city_id", pCityDataList.get(0).id);
                    getActivity().startActivity(intent);
                }

            }
        });

        scCityExplore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(pCityDataList !=null && pCityDataList.size() > 0) {
                    final Intent intent;
                    intent = new Intent(getActivity(), SelectedCityActivity.class);
                    GlobalData.citydata = pCityDataList.get(0);
                    intent.putExtra("selected_city_id", pCityDataList.get(0).id);
                    getActivity().startActivity(intent);
                }

            }
        });

    }

    private void setupProgressWheel(View view) {
        progressWheel = (ProgressWheel) view.findViewById(R.id.progress_wheel);
    }

    private void setupSwipeRefreshLayout(View view) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData(Config.APP_API_URL + Config.GET_ALL);
            }
        });
    }

    private void setupRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);
        display_message = (TextView) view.findViewById(R.id.display_message);
        display_message.setVisibility(view.GONE);

        pCityDataSet = new ArrayList<>();
        adapter = new CityAdapter(getActivity(), pCityDataSet);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                onItemClicked(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void requestData(String uri) {
        JsonObjectRequest request = new JsonObjectRequest(uri,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals(getString(R.string.json_status_success))) {
                                progressWheel.setVisibility(View.GONE);
                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<PCityData>>() {
                                }.getType();

                                pCityDataList = gson.fromJson(response.getString("data"), listType); //fromJson(response.getString("data")+"",listType);


                                if(pCityDataList.size() < 1) {
                                    singleLayout.setVisibility(View.GONE);
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                    updateDisplay();
                                }else{
                                    mRecyclerView.setVisibility(View.GONE);
                                    singleLayout.setVisibility(View.VISIBLE);
                                    stopLoading();
                                    updateSingleDisplay();
                                }

                                updateGlobalCityList();
                            } else {
                                stopLoading();
                                Utils.psLog("Error in loading CityList.");
                            }
                        } catch (JSONException e) {
                            stopLoading();
                            e.printStackTrace();
                        }
                    }
                },


                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError ex) {
                        progressWheel.setVisibility(View.GONE);
                        stopLoading();
                        NetworkResponse response = ex.networkResponse;
                        if (response != null && response.data != null) {

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

    private void updateSingleDisplay() {
        if(pCityDataList.size() > 0){

            singleLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));


            scCityName.setText(pCityDataList.get(0).name);
            scCityLocation.setText(pCityDataList.get(0).address);
            scCityAbout.setText(pCityDataList.get(0).description);

            scCityCatCount.setText(pCityDataList.get(0).category_count+ " Categories");
            scCitySubCatCount.setText(pCityDataList.get(0).sub_category_count + " Sub Categories");
            scCityItemCount.setText(pCityDataList.get(0).item_count+ " Items");

            Picasso.with(getActivity()).load(Config.APP_IMAGES_URL + pCityDataList.get(0).cover_image_file).into(scCityPhoto);
        }
    }

    private void updateGlobalCityList() {
        GlobalData.cityDatas.clear();
        for (PCityData cd : pCityDataList) {
            GlobalData.cityDatas.add(cd);
        }
    }

    private void updateDisplay() {

        if (swipeRefreshLayout.isRefreshing()) {
            pCityDataSet.clear();
            adapter.notifyDataSetChanged();
            int i = 0;
            for (PCityData cd : pCityDataList) {
                pCityDataSet.add(cd);
                i++;
            }
        } else {
            for (PCityData cd : pCityDataList) {
                pCityDataSet.add(cd);
            }
        }
        stopLoading();
        adapter.notifyItemInserted(pCityDataSet.size());
    }

    public void onItemClicked(int position) {

    }

    private void startLoading(){
        try{
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        }catch (Exception e){}
    }

    private void stopLoading(){
        try {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }catch (Exception e){}

    }

}
