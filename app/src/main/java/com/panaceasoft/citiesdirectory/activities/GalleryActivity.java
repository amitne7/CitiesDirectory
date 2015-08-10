package com.panaceasoft.citiesdirectory.activities;

import android.content.Context;
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
import com.panaceasoft.citiesdirectory.uis.ExtendedViewPager;
import com.panaceasoft.citiesdirectory.uis.TouchImageView;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import com.squareup.picasso.Picasso;

/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class GalleryActivity extends AppCompatActivity {
    static TextView txtImgDesc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        ExtendedViewPager mViewPager = (ExtendedViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(new TouchImageAdapter());
        mViewPager.setCurrentItem((Integer.MAX_VALUE / 2) - (Integer.MAX_VALUE / 2) % 12);

        txtImgDesc = (TextView) findViewById(R.id.img_desc);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static class TouchImageAdapter extends PagerAdapter {

        Context context;

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
            //return images.length;
        }


        @Override
        public View instantiateItem(ViewGroup container, int position) {
            Log.d("TEMAPS", position + "");
            TouchImageView imgView = new TouchImageView(container.getContext());
            TextView imgDesc = new TextView(container.getContext());


            if(position >=  GlobalData.itemData.images.size()) {
                position = position %  GlobalData.itemData.images.size();
            }
            Picasso.with(context).load(Config.APP_IMAGES_URL + GlobalData.itemData.images.get(position).path).into(imgView);

            txtImgDesc.setText(GlobalData.itemData.images.get(position).description);
            Utils.psLog("Img Desc " + GlobalData.itemData.images.get(position).description);

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
