package com.panaceasoft.citiesdirectory.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.panaceasoft.citiesdirectory.GlobalData;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.activities.SearchResultActivity;
import com.panaceasoft.citiesdirectory.adapters.ItemAdapter;
import com.panaceasoft.citiesdirectory.listeners.SelectListener;
import com.panaceasoft.citiesdirectory.models.PItemData;
import com.panaceasoft.citiesdirectory.uis.PSPopupSingleSelectView;
import com.pnikosis.materialishprogress.ProgressWheel;
import java.util.List;

/**
 * Created by Panacea-Soft on 8/6/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class SearchFragment extends Fragment {

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Private Variables
    //-------------------------------------------------------------------------------------------------------------------------------------
    private LinearLayout popupContainer;
    private View view;
    private Button btn_search;
    private TextView txt_search;
    private int selectedCityId;
    private String selectedCityName;
    private Intent intent;
    private String selectCityString;
    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Private Variables
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Constructor
    //-------------------------------------------------------------------------------------------------------------------------------------
    public SearchFragment() {

    }
    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Constructor
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);

        initData();

        initUI(view);

        return view;
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Init UI Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void initUI(View view) {

        popupContainer = (LinearLayout) view.findViewById(R.id.choose_container);
        popupContainer.removeAllViews();

        PSPopupSingleSelectView psPopupSingleSelectView = new PSPopupSingleSelectView(getActivity(), selectCityString, GlobalData.cityDatas, "");
        psPopupSingleSelectView.setOnSelectListener(new SelectListener() {
            @Override
            public void Select(View view, int position, CharSequence text) {

            }

            @Override
            public void Select(View view, int position, CharSequence text, int id) {
                selectedCityId = id;
                selectedCityName = text.toString();
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

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Init UI Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Init Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void initData(){
        selectCityString = getResources().getString(R.string.select_city);
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Init Data
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------
    private void prepareForSearch(View v) {
        intent = new Intent(getActivity(), SearchResultActivity.class);
        intent.putExtra("selected_city_id", selectedCityId + "");
        intent.putExtra("search_keyword", txt_search.getText().toString().trim());
        intent.putExtra("selected_city_name", selectedCityName);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
    }
    //-------------------------------------------------------------------------------------------------------------------------------------
    //endregion Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

}