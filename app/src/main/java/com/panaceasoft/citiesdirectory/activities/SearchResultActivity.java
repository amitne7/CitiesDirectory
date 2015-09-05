package com.panaceasoft.citiesdirectory.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.adapters.ItemAdapter;
import com.panaceasoft.citiesdirectory.listeners.ClickListener;
import com.panaceasoft.citiesdirectory.listeners.RecyclerTouchListener;
import com.panaceasoft.citiesdirectory.models.PItemData;
import com.panaceasoft.citiesdirectory.utilities.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchResultActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private ItemAdapter mAdapter;
    private List<PItemData> myDataset;
    private Intent intent;
    private List<PItemData> it;
    private PItemData pit;
    private TextView txtSelectedCity;
    private TextView txtSearchKeyword;
    private TextView txtRecordFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        setupToolbar();
        setUpRecyclerView();
        prepareForSearch();
    }

    private void prepareForSearch() {
        txtSelectedCity = (TextView) findViewById(R.id.search_city);
        txtSelectedCity.setText(getIntent().getStringExtra("selected_city_name"));
        txtSearchKeyword = (TextView) findViewById(R.id.search_keyword);
        txtSearchKeyword.setText(getIntent().getStringExtra("search_keyword"));
        txtRecordFound = (TextView) findViewById(R.id.record_found);


        final String URL = Config.APP_API_URL + Config.POST_ITEM_SEARCH + getIntent().getStringExtra("selected_city_id");
        Utils.psLog(URL);
        doSearch(this, URL, getIntent().getStringExtra("search_keyword"));
    }

    public void doSearch(Context context, final String URL, final String keyword) {
        Utils.psLog("<<< Inside doSearch >>>" + keyword);
        RequestQueue queue = Volley.newRequestQueue(context);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("keyword", keyword);

        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            String status = response.getString("status");
                            if (status.equals(getString(R.string.json_status_success))) {

                                if (myDataset.size() > 0) {
                                    myDataset.remove(myDataset.size() - 1);
                                    mAdapter.notifyItemRemoved(myDataset.size());
                                }

                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<PItemData>>() {}.getType();
                                it = (List<PItemData>) gson.fromJson(response.getString("data"), listType);

                                txtRecordFound.setText("Search Result Count : " + it.size());

                                for (PItemData pItem : it) {
                                    myDataset.add(pItem);
                                }

                                mAdapter.notifyItemInserted(myDataset.size());
                                mAdapter.setLoaded();


                            } else {

                                Utils.psLog("Error in loading.");
                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(sr);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(Utils.getSpannableString(getString(R.string.search_result)));
    }

    public void setUpRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        myDataset = new ArrayList<>();
        mAdapter = new ItemAdapter(myDataset, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);


        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView, new ClickListener() {
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
        final Intent intent;
        intent = new Intent(getApplicationContext(), DetailActivity.class);
        intent.putExtra("selected_item_id", myDataset.get(position).id);
        intent.putExtra("selected_city_id", myDataset.get(position).city_id + "");
        startActivity(intent);
    }

}
