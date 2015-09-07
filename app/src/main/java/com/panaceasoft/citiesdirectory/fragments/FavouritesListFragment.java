package com.panaceasoft.citiesdirectory.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.activities.DetailActivity;
import com.panaceasoft.citiesdirectory.adapters.ItemAdapter;
import com.panaceasoft.citiesdirectory.listeners.ClickListener;
import com.panaceasoft.citiesdirectory.listeners.RecyclerTouchListener;
import com.panaceasoft.citiesdirectory.models.PItemData;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import com.pnikosis.materialishprogress.ProgressWheel;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Panacea-Soft on 8/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class FavouritesListFragment extends Fragment {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    private List<PItemData> items;
    private List<PItemData> myDataset;
    private ItemAdapter mAdapter;
    private ProgressWheel progressWheel;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private TextView displayMessage;
    private SharedPreferences pref;
    private String noDataAvaiString;
    private String jsonStatusSuccessString;

    /**------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favourites_list, container, false);

        initData();

        initUI(view);

        return view;
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - init Data Functions
     **------------------------------------------------------------------------------------------------*/
    private void initData() {
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        Utils.psLog(Config.APP_API_URL + Config.GET_FAVOURITE_ITEMS + pref.getInt("_login_user_id", 0) + "/count/" + Config.PAGINATION + "/form/0");
        requestData(Config.APP_API_URL + Config.GET_FAVOURITE_ITEMS + pref.getInt("_login_user_id", 0) + "/count/" + Config.PAGINATION + "/form/0");

        try {
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
            noDataAvaiString = getResources().getString(R.string.no_data_available);

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

    private void initUI(View view){
        initProgressWheel(view);
        initRecyclerView(view);

        displayMessage = (TextView) view.findViewById(R.id.display_message);
        displayMessage.setVisibility(view.GONE);
    }

    private void initProgressWheel(View view) {
        progressWheel = (ProgressWheel) view.findViewById(R.id.progress_wheel);
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
                            if (status.equals(jsonStatusSuccessString)) {

                                if (myDataset.size() > 0) {
                                    myDataset.remove(myDataset.size() - 1);
                                    mAdapter.notifyItemRemoved(myDataset.size());
                                }

                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<PItemData>>() {
                                }.getType();
                                items = (List<PItemData>) gson.fromJson(response.getString("data"), listType);

                                Utils.psLog("Count : " + items.size());

                                progressWheel.setVisibility(View.GONE);
                                for (PItemData pItem : items) {
                                    myDataset.add(pItem);
                                }
                                mAdapter.notifyItemInserted(myDataset.size());
                                mAdapter.setLoaded();
                            }else{
                                displayMessage.setVisibility(View.VISIBLE);
                                displayMessage.setText(noDataAvaiString);
                            }

                        } catch (JSONException e) {
                            displayMessage.setVisibility(View.VISIBLE);
                            displayMessage.setText(noDataAvaiString);

                        }


                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError ex) {
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(request);
    }

    private void onItemClicked(int position) {
        //((SubCategoryActivity)getActivity()).openActivity(myDataset.get(position).id, myDataset.get(position).city_id);
        final Intent intent;
        intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("selected_item_id", myDataset.get(position).id);
        intent.putExtra("selected_city_id", myDataset.get(position).city_id);
        startActivity(intent);
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/


}
