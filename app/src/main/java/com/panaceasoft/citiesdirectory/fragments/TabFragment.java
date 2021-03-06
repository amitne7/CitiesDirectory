package com.panaceasoft.citiesdirectory.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.activities.SubCategoryActivity;
import com.panaceasoft.citiesdirectory.adapters.ItemAdapter;
import com.panaceasoft.citiesdirectory.listeners.ClickListener;
import com.panaceasoft.citiesdirectory.listeners.RecyclerTouchListener;
import com.panaceasoft.citiesdirectory.models.PItemData;
import com.panaceasoft.citiesdirectory.models.PSubCategoryData;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import com.pnikosis.materialishprogress.ProgressWheel;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class TabFragment extends Fragment {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/
    public int selectedCityID;
    public int selectedSubCategoryID;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private ItemAdapter mAdapter;
    private ProgressWheel progressWheel;
    private PSubCategoryData subCategoryData;
    private List<PItemData> it;
    private List<PItemData> myDataset;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String jsonStatusSuccess ;

    /**------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - New Instance Function
     **------------------------------------------------------------------------------------------------*/
    public static TabFragment newInstance(PSubCategoryData subCategoryData, int CityID) {
        TabFragment fragment = new TabFragment();
        fragment.setData(subCategoryData, CityID);
    return fragment;
    }

    public TabFragment() {

    }

    public void setData(PSubCategoryData subCategoryData, int selectedCityID) {
        this.subCategoryData = subCategoryData;
        this.selectedCityID = selectedCityID;
        this.selectedSubCategoryID = subCategoryData.id;

    }

    /**------------------------------------------------------------------------------------------------
     * End Block - New Instance Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab, container, false);

        initData();

        initUI(view);

        return view;
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void initData() {
        try {
            this.jsonStatusSuccess = getResources().getString(R.string.json_status_success);
            // Inflate the layout for this fragment
            Utils.psLog(Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + selectedCityID + "/sub_cat_id/" + subCategoryData.id + "/item/all/count/" + Config.PAGINATION + "/form/0");
            requestData(Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + selectedCityID + "/sub_cat_id/" + subCategoryData.id + "/item/all/count/" + Config.PAGINATION + "/form/0");
        }catch(Exception e){
            Utils.psErrorLogE("Error in initData.", e);
        }
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    private void initUI(View view) {
        initProgressWheel(view);
        initRecyclerView(view);
        initSwipeRefreshLayout(view);

        mAdapter.setOnLoadMoreListener(new ItemAdapter.OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                //add progress item
                int from = myDataset.size();
                myDataset.add(null);
                mAdapter.notifyItemInserted(myDataset.size() - 1);
                Log.d("API URL : ", Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + 1 + "/sub_cat_id/" + subCategoryData.id + "/item/all/count/" + Config.PAGINATION + "/form/" + from);
                requestData(Config.APP_API_URL + Config.ITEMS_BY_SUB_CATEGORY + 1 + "/sub_cat_id/" + subCategoryData.id + "/item/all/count/" + Config.PAGINATION + "/from/" + from);
            }

        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        startLoading();

    }

    private void initProgressWheel(View view) {
        progressWheel = (ProgressWheel) view.findViewById(R.id.progress_wheel);
    }

    private void initSwipeRefreshLayout(View view) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                stopLoading();
            }
        });
    }

    private void initRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

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

    /**------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    private void requestData(String uri) {

        JsonObjectRequest request = new JsonObjectRequest(uri,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccess)) {

                                if (myDataset.size() > 0) {
                                    myDataset.remove(myDataset.size() - 1);
                                    mAdapter.notifyItemRemoved(myDataset.size());
                                }

                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<PItemData>>() {
                                }.getType();
                                it = (List<PItemData>) gson.fromJson(response.getString("data"), listType);

                                progressWheel.setVisibility(View.GONE);

                                for (PItemData pItem : it) {

                                    myDataset.add(pItem);

                                }

                                stopLoading();

                                mAdapter.notifyItemInserted(myDataset.size());
                                mAdapter.setLoaded();
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
                        //Log.d("Volley Error " , ex.getMessage());
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(request);
    }

    private void startLoading() {
        try {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        } catch (Exception e) {
        }
    }

    private void stopLoading() {
        try {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        } catch (Exception e) {
        }

    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

    public void onItemClicked(int position) {
        ((SubCategoryActivity) getActivity()).openActivity(myDataset.get(position).id, myDataset.get(position).city_id);
    }

    public void refershLikeAndReview(int itemID, String likeCount, String reviewCount) {

        mAdapter.updateItemLikeAndReviewCount(itemID, likeCount, reviewCount);
        mAdapter.notifyDataSetChanged();
    }
    /**------------------------------------------------------------------------------------------------
     * End Block - Public Functions
     **------------------------------------------------------------------------------------------------*/




}
