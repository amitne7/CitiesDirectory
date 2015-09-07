package com.panaceasoft.citiesdirectory.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.models.PImageData;
import com.panaceasoft.citiesdirectory.models.PItemData;
import com.panaceasoft.citiesdirectory.models.PNewsData;
import com.panaceasoft.citiesdirectory.uis.ExtendedViewPager;
import com.panaceasoft.citiesdirectory.uis.TouchImageView;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class GalleryActivity extends AppCompatActivity {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    private TextView txtImgDesc;
    private static PNewsData newsData;
    private static PItemData itemData;
    private Bundle bundle;
    private String from;
    private static ArrayList<PImageData> imageArray;
    private TouchImageView imgView;

    /**------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        initUI();

        initData();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }

    class TouchImageAdapter extends PagerAdapter {

        Context context;

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }


        @Override
        public View instantiateItem(ViewGroup container, int position) {
            imgView = new TouchImageView(container.getContext());
            if(position >=  imageArray.size()) {
                position = position %  imageArray.size();
            }
            Picasso.with(context).load(Config.APP_IMAGES_URL + imageArray.get(position).path).into(imgView);
            txtImgDesc.setText(imageArray.get(position).description);
            container.addView(imgView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            return imgView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    private void initUI() {
        try {
            ExtendedViewPager mViewPager = (ExtendedViewPager) findViewById(R.id.view_pager);
            mViewPager.setAdapter(new TouchImageAdapter());
            mViewPager.setCurrentItem((Integer.MAX_VALUE / 2) - (Integer.MAX_VALUE / 2) % 12);
            txtImgDesc = (TextView) findViewById(R.id.img_desc);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initUI.", e);
        }
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void initData() {
        try {
            bundle = getIntent().getBundleExtra("images_bundle");
            from =bundle.getString("from");
            if(from.toString().equals("item")) {
                itemData = bundle.getParcelable("images");
                imageArray = itemData.images;
            } else {
                newsData = bundle.getParcelable("images");
                imageArray = newsData.images;
            }
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initData.", e);
        }
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

}
