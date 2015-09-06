package com.panaceasoft.citiesdirectory.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
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
import com.panaceasoft.citiesdirectory.activities.SubCategoryActivity;
import com.panaceasoft.citiesdirectory.adapters.ItemAdapter;
import com.panaceasoft.citiesdirectory.listeners.ClickListener;
import com.panaceasoft.citiesdirectory.listeners.RecyclerTouchListener;
import com.panaceasoft.citiesdirectory.listeners.SelectListener;
import com.panaceasoft.citiesdirectory.models.PItemData;
import com.panaceasoft.citiesdirectory.uis.PSPopupSingleSelectView;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Panacea-Soft on 8/6/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class SearchFragment extends Fragment {

    private LinearLayout popupContainer;
    private View view;
    private Button btn_search;
    private TextView txt_search;

    private List<PItemData> it;
    private List<PItemData> myDataset;
    private ItemAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ProgressWheel progressWheel;
    private StaggeredGridLayoutManager mLayoutManager;
    private int selectedCityId;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        initUI(view);
        setupProgressWheel(view);
        setupRecyclerView(view);

        return view;
    }

    private void initUI(View view) {
        popupContainer = (LinearLayout) view.findViewById(R.id.choose_container);
        popupContainer.removeAllViews();

        PSPopupSingleSelectView psPopupSingleSelectView = new PSPopupSingleSelectView(getActivity(), "Please select city", GlobalData.cityDatas, "");
        psPopupSingleSelectView.setOnSelectListener(new SelectListener() {
            @Override
            public void Select(View view, int position, CharSequence text) {

            }

            @Override
            public void Select(View view, int position, CharSequence text, int id) {
                selectedCityId = id;
            }

            @Override
            public void Select(View view, int position, CharSequence text, int id, float additionalPrice) {

            }
        });
        popupContainer.addView(psPopupSingleSelectView);

        btn_search = (Button) view.findViewById(R.id.button_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                prepareForSearch(v);
            }
        });

        txt_search = (TextView) view.findViewById(R.id.input_search);



    }

    private void prepareForSearch(View v) {
        progressWheel.setVisibility(v.VISIBLE);
        final String URL = Config.APP_API_URL + Config.POST_ITEM_SEARCH + selectedCityId; //Need to change selected city id from Popup
        Utils.psLog(URL);
        doSearch(getActivity().getApplicationContext(), URL, txt_search.getText().toString().trim(), v);
    }

    public void doSearch(Context context, final String URL, final String keyword, final View v) {
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
                        progressWheel.setVisibility(v.GONE);

                        if (myDataset.size() > 0) {
                            myDataset.remove(myDataset.size() - 1);
                            mAdapter.notifyItemRemoved(myDataset.size());
                        }

                        Gson gson = new Gson();

                        Type listType = new TypeToken<List<PItemData>>() {}.getType();
                        Utils.psLog(response.getString("data"));
                        chkReturn(response.getString("data"));

                        it = (List<PItemData>) gson.fromJson(response.getString("data"), listType);
                        Utils.psLog("Search Count : " + it.size());
                        for (PItemData pItem : it) {

                            myDataset.add(pItem);

                        }
                        mAdapter.notifyItemInserted(myDataset.size());
                        mAdapter.setLoaded();

                    } else {

                        Utils.psLog("Error in loading.");
                    }
                } catch (JSONException e) {
                    Utils.psLog("******** " + e.getMessage());
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

    public void setA(List<PItemData> pit){

        List<PItemData> a = pit;
        int b = 0;
    }

    public interface PostCommentResponseListener {
        public void requestStarted();

        public void requestCompleted();

        public void requestEndedWithError(VolleyError error);
    }

    private void setupProgressWheel(View view) {
        progressWheel = (ProgressWheel) view.findViewById(R.id.progress_wheel);
        progressWheel.setVisibility(View.GONE);
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
        ((SubCategoryActivity) getActivity()).openActivity(myDataset.get(position).id, myDataset.get(position).city_id);
    }

    public void chkReturn(String rep) {
        Utils.psLog(rep);
    }

}
