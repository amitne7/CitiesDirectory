package com.panaceasoft.citiesdirectory.uis;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.listeners.SelectListener;
import com.panaceasoft.citiesdirectory.models.PCityData;
import com.panaceasoft.citiesdirectory.utilities.Utils;

import java.util.ArrayList;

/**
 * Created by Panacea-Soft on 7/25/15.
 * Contact Email : teamps.is.cool@gmail.com
 */


public class PSPopupSingleSelectView extends LinearLayout {

    public SelectListener onSelectListener;
    private RelativeLayout mLayout;
    private TextView mTextView;
    private int selectedIndex = 0;
    private CharSequence[] items;
    private String title = "";
    private ArrayList<PCityData> pCityDatas;

    public PSPopupSingleSelectView(Context context) {
        super(context);
        Utils.psLog("1***");
        initUI(context);
    }

    public PSPopupSingleSelectView(Context context, String title, ArrayList<PCityData> pCityDatas, String s) {
        super(context, null);
        Utils.psLog("3***");
        this.title = title;
        setItemsWithPCityData(pCityDatas);
        initUI(context);

    }

    public PSPopupSingleSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Utils.psLog("4***");
        initUI(context);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.UIPopup,
                0, 0);

        try {
            items = a.getTextArray(R.styleable.UIPopup_items);
            title = a.getString(R.styleable.UIPopup_pTitle);

        } finally {
            a.recycle();
        }
    }

    /**
     * Inflate the UI for the layout
     *
     * @param context for the view
     */
    private void initUI(Context context) {
        Utils.psLog("initUI" + context.toString());
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.ui_based_pop_up_chooser_view, this);
        onFinishInflateCustom();
    }


    protected void onFinishInflateCustom() {
        super.onFinishInflate();
        Utils.psLog("Inflate ***");
        mLayout = (RelativeLayout) findViewById(R.id.mLayout);
        mTextView = (TextView) findViewById(R.id.mText);

        if (!title.equals("")) {
            mTextView.setText(title);
        }

        mLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(pCityDatas != null && pCityDatas.size() > 0) {
                        MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                                .title(title)
                                .items(items)
                                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                    @Override
                                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                        /**
                                         * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                         * returning false here won't allow the newly selected radio button to actually be selected.
                                         **/

                                        mTextView.setText(text);
                                        selectedIndex = which;

                                        onSelectListener.Select(view, which, text);

                                        if (pCityDatas != null && pCityDatas.size() > 0) {
                                            onSelectListener.Select(view, which, text, pCityDatas.get(which).id);
                                        }
                                        return true;
                                    }
                                })
                                .positiveText("Choose")
                                .show();

                        dialog.setSelectedIndex(selectedIndex);
                    }else{
                        Toast.makeText(getContext(), "There is no city to select.", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Utils.psErrorLogE("Error in Popup Dialog." , e);
                }
            }
        });
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CharSequence[] getItems() {
        return items;
    }

    public void setItems(CharSequence[] items) {
        this.items = items;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public SelectListener getOnSelectListener() {
        return onSelectListener;
    }

    public void setOnSelectListener(SelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }



    public void setItemsWithPCityData(ArrayList<PCityData> pCityDatas) {
        int i = 0;
        this.pCityDatas = pCityDatas;
        try {
            this.items = new CharSequence[pCityDatas.size()];
            Utils.psLog("setup ***" + items.length);

            if(pCityDatas != null && pCityDatas.size() > 0){
                for (PCityData attDetail : pCityDatas) {
                    this.items[i++] = attDetail.name.toString();
                }
            }
        } catch (Exception e) {
            Utils.psLog("Error");
        }

    }
}
