package com.panaceasoft.citiesdirectory.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.adapters.NewsAdapter;
import com.panaceasoft.citiesdirectory.models.PNewsData;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NewsListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    private Toolbar toolbar;
    private int selectedCityId;
    private ListView listView;
    private ArrayList<PNewsData> newsDataSet;
    private ArrayList<PNewsData> news;
    private NewsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PNewsData newsData;
    private Bundle bundle;
    private Intent intent;
    private String jsonStatusSuccessString;
    private SpannableString newsListString;
    /**------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        initData();

        initUI();

        bindData();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }

    @Override
    public void onRefresh() {
        requestData();
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    private void initUI() {
        initToolbar();
        initList();

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void initToolbar() {
        try {
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
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initToolbar.", e);
        }
    }

    private void initList() {
        try {
            listView = (ListView) findViewById(R.id.news_list);
            listView.setOnItemClickListener(new ListClickHandler());
            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initList.", e);
        }
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void initData(){
        try {
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
            newsListString = Utils.getSpannableString(getString(R.string.news_list));
        } catch (Resources.NotFoundException e) {
            Utils.psErrorLogE("Error in initToolbar.", e);
        }
    }
    /**------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void bindData() {

        toolbar.setTitle(newsListString);
        selectedCityId = Integer.parseInt(getIntent().getStringExtra("selected_city_id"));
        newsDataSet = new ArrayList<>();
        adapter = new NewsAdapter(this, newsDataSet);
        listView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        requestData();
                                    }
                                }
        );
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     **------------------------------------------------------------------------------------------------*/


    private void requestData() {

        final String uri = Config.APP_API_URL + Config.GET_CITY_NEWS + selectedCityId;
        Utils.psLog(uri);
        swipeRefreshLayout.setRefreshing(true);
        JsonObjectRequest request = new JsonObjectRequest(uri,

                new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccessString)) {

                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<PNewsData>>() {
                                }.getType();
                                news = gson.fromJson(response.getString("data"), listType);

                                Utils.psLog("Total News : " + news.size());
                                newsDataSet.clear();
                                adapter.notifyDataSetChanged();
                                for (PNewsData nd : news) {
                                    newsDataSet.add(nd);
                                }

                                swipeRefreshLayout.setRefreshing(false);

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

                        swipeRefreshLayout.setRefreshing(false);

                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        RequestQueue queue = Volley.newRequestQueue(this.getApplicationContext());
        queue.add(request);
    }
    /**------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Listener Class
     **------------------------------------------------------------------------------------------------*/
    public class ListClickHandler implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
            // TODO Auto-generated method stub
            newsData = (PNewsData) adapter.getItemAtPosition(position);
            Utils.psLog(" Title " + newsData.title);
            bundle = new Bundle();
            bundle.putParcelable("news", newsData);

            intent = new Intent(getApplicationContext(), NewsDetailActivity.class);
            intent.putExtra("news_bundle", bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
        }

    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Listener Class
     **------------------------------------------------------------------------------------------------*/





}
