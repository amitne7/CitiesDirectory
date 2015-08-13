package com.panaceasoft.citiesdirectory.activities;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.GlobalData;
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
    static TextView txtImgDesc;
    private static PNewsData newsData;
    private static PItemData itemData;

    private Bundle bundle;
    private String from;

    private static ArrayList<PImageData> imageArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        ExtendedViewPager mViewPager = (ExtendedViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(new TouchImageAdapter());
        mViewPager.setCurrentItem((Integer.MAX_VALUE / 2) - (Integer.MAX_VALUE / 2) % 12);
        txtImgDesc = (TextView) findViewById(R.id.img_desc);


        bundle = getIntent().getBundleExtra("images_bundle");
        from =bundle.getString("from");
        if(from.toString().equals("item")) {
            itemData = bundle.getParcelable("images");
            imageArray = itemData.images;
        } else {
            newsData = bundle.getParcelable("images");
            imageArray = newsData.images;
        }



    }

    static class TouchImageAdapter extends PagerAdapter {

        Context context;

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }


        @Override
        public View instantiateItem(ViewGroup container, int position) {
            Log.d("TEMAPS", position + "");
            TouchImageView imgView = new TouchImageView(container.getContext());
            TextView imgDesc = new TextView(container.getContext());


            if(position >=  imageArray.size()) {
                position = position %  imageArray.size();
            }
            Picasso.with(context).load(Config.APP_IMAGES_URL + imageArray.get(position).path).into(imgView);

            txtImgDesc.setText(imageArray.get(position).description);
            Utils.psLog("Img Desc " + imageArray.get(position).description);

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
}
