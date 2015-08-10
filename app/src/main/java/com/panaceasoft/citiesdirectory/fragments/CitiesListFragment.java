package com.panaceasoft.citiesdirectory.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.GlobalData;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.adapters.CityAdapter;
import com.panaceasoft.citiesdirectory.listeners.ClickListener;
import com.panaceasoft.citiesdirectory.listeners.RecyclerTouchListener;
import com.panaceasoft.citiesdirectory.models.CategoryData;
import com.panaceasoft.citiesdirectory.models.CityData;
import com.panaceasoft.citiesdirectory.models.PCityData;
import com.panaceasoft.citiesdirectory.models.SubCategoryData;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.apache.http.HttpStatus;

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
    private List<CityData> sh;
    private List<CityData> dataSet;
    private ArrayList<CategoryData> categoryArrayList;
    private ArrayList<SubCategoryData> subCategoryArrayList;
    private CityAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView display_message;
    ///
    private ArrayList<PCityData> pCityDataList;
    private ArrayList<PCityData> pCityDataSet;

    public CitiesListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cities_list, container, false);

        requestData(Config.APP_API_URL + Config.GET_ALL);

        setupProgressWheel(view);
        setupSwipeRefreshLayout(view);
        setupRecyclerView(view);

        return view;
    }

    private void setupProgressWheel(View view) {
        progressWheel = (ProgressWheel) view.findViewById(R.id.progress_wheel);
    }

    private void setupSwipeRefreshLayout(View view){
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

        dataSet = new ArrayList<>();
        pCityDataSet = new ArrayList<>();
        //adapter = new CityAdapter(getActivity(),dataSet);
        adapter = new CityAdapter(getActivity(),pCityDataSet);
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
        StringRequest request = new StringRequest(uri,

                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {

                        progressWheel.setVisibility(View.GONE);
                        Gson gson = new Gson();
//                        Type listType = new TypeToken<List<CityData>>() {}.getType();
//                        sh = (List<CityData>) gson.fromJson(response,listType);
//                        updateDisplay();

                        Type listType = new TypeToken<List<PCityData>>() {}.getType();
                        pCityDataList = gson.fromJson(response,listType);

                        updateGlobalCityList();
                        updateDisplay();

                    }
                },



                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError ex) {
                       // Log.d(">> Volley Error ", ex.getMessage());
                        progressWheel.setVisibility(View.GONE);

                        //NetworkResponse networkResponse = ex.networkResponse;
                        //Log.d(">> Status Code " , String.valueOf(networkResponse.statusCode));


                        //Log.d("Server Response Code : ", String.valueOf(networkResponse.statusCode));
                        //if (null == networkResponse.statusCode) {

                        //}else if (networkResponse != null && networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
                            // HTTP Status Code: 401 Unauthorized
                       // }
                        NetworkResponse response = ex.networkResponse;
                        if(response != null && response.data != null){

                           // Log.d(">> Status Code " , String.valueOf(response.statusCode));

                        } else {
                            display_message.setVisibility(View.VISIBLE);
                            String message = getResources().getString(R.string.wrong_url);
                            display_message.setText(message);
                            //Context context;
                            //String mess = context.getString(R.string.)
                            //String URL = Resources.getSystem().getString(R.id.wrong);
                            //display_message.setText(getResources().getString(R.id.wr));
                            //String mess = getString(R.id.wron)
                            //String user=getApplicationContext().getResources().getString()
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

    private void updateGlobalCityList() {
        for (PCityData cd : pCityDataList) {
            //cd.categories.clear();
            //cd.categories = null;
            GlobalData.cityDatas.add(cd);
        }
    }

    private void updateDisplay() {

        if(swipeRefreshLayout.isRefreshing()){
            //dataSet.clear();
            pCityDataSet.clear();
            adapter.notifyDataSetChanged();
            int i = 0;
            //for (CityData cd : sh) {
            for (PCityData cd : pCityDataList) {
                if(i != 0) {
                    //dataSet.add(cd);
                    pCityDataSet.add(cd);
                }
                i++;
            }
        }else {

            //for (CityData cd : sh) {
            for (PCityData cd : pCityDataList) {
                //dataSet.add(cd);
                pCityDataSet.add(cd);
            }
        }
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
        //mRecyclerView.setAdapter(adapter);
        //adapter.notifyItemInserted(dataSet.size());
        adapter.notifyItemInserted(pCityDataSet.size());

    }

    public void onItemClicked( int position){

        Toast.makeText(getActivity(), " Position is " + position, Toast.LENGTH_SHORT).show();
    }

}
