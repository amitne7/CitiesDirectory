package com.panaceasoft.citiesdirectory.fragments;

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
import com.android.volley.toolbox.StringRequest;
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

    private List<PItemData> it;
    private List<PItemData> myDataset;
    private ItemAdapter mAdapter;
    private ProgressWheel progressWheel;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private TextView display_message;
    private SharedPreferences pref;

    public FavouritesListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        View view = inflater.inflate(R.layout.fragment_favourites_list, container, false);

        Utils.psLog(Config.APP_API_URL + Config.GET_FAVOURITE_ITEMS + pref.getInt("_login_user_id", 0) + "/count/" + Config.PAGINATION + "/form/0");
        requestData(Config.APP_API_URL + Config.GET_FAVOURITE_ITEMS + pref.getInt("_login_user_id", 0) + "/count/" + Config.PAGINATION + "/form/0");

        setupProgressWheel(view);
        setupRecyclerView(view);

        display_message = (TextView) view.findViewById(R.id.display_message);
        display_message.setVisibility(view.GONE);

        return view;
    }

    private void requestData(String uri) {
        StringRequest request = new StringRequest(uri,

                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject json = new JSONObject(response);
                            Utils.psLog(json.getString("error"));

                            if(json.getString("error") != null) {
                                display_message.setVisibility(View.VISIBLE);
                                display_message.setText(getString(R.string.no_data_available));
                            }

                        } catch (JSONException e) {
                            if(myDataset.size() > 0) {
                                myDataset.remove(myDataset.size() - 1);
                                mAdapter.notifyItemRemoved(myDataset.size());
                            }

                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<PItemData>>() {
                            }.getType();
                            it = (List< PItemData>) gson.fromJson(response, listType);

                            progressWheel.setVisibility(View.GONE);
                            for (PItemData pItem : it) {

                                myDataset.add(pItem);

                            }
                            mAdapter.notifyItemInserted(myDataset.size());
                            mAdapter.setLoaded();

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

    private void setupProgressWheel(View view) {
        progressWheel = (ProgressWheel) view.findViewById(R.id.progress_wheel);
    }

    private void setupRecyclerView(View view) {
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

    public void onItemClicked(int position) {
        ((SubCategoryActivity)getActivity()).openActivity(myDataset.get(position).id, myDataset.get(position).shop_id);
    }


}
