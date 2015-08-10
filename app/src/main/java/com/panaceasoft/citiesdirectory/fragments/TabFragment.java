package com.panaceasoft.citiesdirectory.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.activities.DetailActivity;
import com.panaceasoft.citiesdirectory.activities.DetailOneActivity;
import com.panaceasoft.citiesdirectory.activities.SubCategoryActivity;
import com.panaceasoft.citiesdirectory.adapters.ItemAdapter;
import com.panaceasoft.citiesdirectory.listeners.ClickListener;
import com.panaceasoft.citiesdirectory.listeners.RecyclerTouchListener;
import com.panaceasoft.citiesdirectory.models.CustomRowInformation;
import com.panaceasoft.citiesdirectory.models.ImageData;
import com.panaceasoft.citiesdirectory.models.ItemAttributeData;
import com.panaceasoft.citiesdirectory.models.ItemData;
import com.panaceasoft.citiesdirectory.models.ItemDataWrapper;
import com.panaceasoft.citiesdirectory.models.PItemData;
import com.panaceasoft.citiesdirectory.models.PSubCategoryData;
import com.panaceasoft.citiesdirectory.models.SubCategoryData;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class TabFragment extends Fragment {

    public String selectedCityID;
    //private SubCategoryData subCategoryData = null;

    private ArrayList<ImageData> imageDataArrayList;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private ItemAdapter mAdapter;

    private ProgressWheel progressWheel;
    private Handler handler;

    //
    private PSubCategoryData subCategoryData;
    private List<PItemData> it;
    private List<PItemData> myDataset;

    // TODO: Rename and change types and number of parameters
    //public static TabFragment newInstance(SubCategoryData subCategoryData, String CityID) {
    public static TabFragment newInstance(PSubCategoryData subCategoryData, String CityID) {
        TabFragment fragment = new TabFragment();
        fragment.setData(subCategoryData, CityID);
        //SelectedCityID = CityID;

        return fragment;
    }

    public TabFragment() {

    }

    //public void setData(SubCategoryData subCategoryData, String selectedCityID) {
    public void setData(PSubCategoryData subCategoryData, String selectedCityID) {
        //this.subCategoryData = subCategoryData;
        this.subCategoryData = subCategoryData;
        this.selectedCityID = selectedCityID;
    }

    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("TEAMPS", " Sub Cat Data Tab Frag : " + subCategoryData.name);

        View view = inflater.inflate(R.layout.fragment_tab, container, false);

        //Button txt = (Button) view.findViewById(R.id.btnLogin);
        //txt.setText(subCategoryData.name);

        Log.d("TEAMPS", "URL : " + Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + selectedCityID + "/sub_cat_id/" + subCategoryData.id + "/item/all/");

        // Inflate the layout for this fragment
        requestData(Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + selectedCityID + "/sub_cat_id/" + subCategoryData.id + "/item/all/count/" + Config.PAGINATION + "/form/0");

        setupProgressWheel(view);
        setupRecyclerView(view);


        mAdapter.setOnLoadMoreListener(new ItemAdapter.OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                //add progress item
                int from = myDataset.size();
                myDataset.add(null);
                mAdapter.notifyItemInserted(myDataset.size() - 1);



                Utils.psLog("Inside Load More Function");

                Log.d("API URL : ", Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + 1 + "/sub_cat_id/" + subCategoryData.id + "/item/all/count/" + Config.PAGINATION + "/form/" + from);

                requestData(Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + 1 + "/sub_cat_id/" + subCategoryData.id + "/item/all/count/" + Config.PAGINATION + "/from/" + from);

            }

        });




        return view;
    }

    private void setupProgressWheel(View view) {
        progressWheel = (ProgressWheel) view.findViewById(R.id.progress_wheel);
    }

    private void setupRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        //mLayoutManager = new GridLayoutManager(this, 2);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        myDataset = new ArrayList<>();

        mAdapter = new ItemAdapter(myDataset, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);


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

                        if(myDataset.size() > 0) {
                            myDataset.remove(myDataset.size() - 1);
                            mAdapter.notifyItemRemoved(myDataset.size());
                        }

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<PItemData>>() {
                        }.getType();
                        it = (List< PItemData>) gson.fromJson(response, listType);

                        progressWheel.setVisibility(View.GONE);
                        //categoryArrayList = ctd.getCategoryData();


                        for(PItemData pItem : it) {

                            myDataset.add(pItem);

                        }


                        mAdapter.notifyItemInserted(myDataset.size());
                        mAdapter.setLoaded();
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError ex) {
                        //Log.d("Volley Error " , ex.getMessage());
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(request);
    }

    public void onItemClicked(int position) {
        ((SubCategoryActivity)getActivity()).openActivity(myDataset.get(position).id, myDataset.get(position).shop_id);
    }

    public void refershLikeAndReview(int itemID, String likeCount, String reviewCount){

        mAdapter.updateItemLikeAndReviewCount(itemID, likeCount, reviewCount);
        mAdapter.notifyDataSetChanged();
    }



}
