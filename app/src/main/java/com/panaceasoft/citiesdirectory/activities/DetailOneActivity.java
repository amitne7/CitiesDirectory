package com.panaceasoft.citiesdirectory.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.utilities.Utils;

import java.text.DecimalFormat;

/**
 * Created by Panacea-Soft on 7/15/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class DetailOneActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView detailImage;
    private TextView txtTitle;
    private RatingBar getRatingBar;
    private RatingBar setRatingBar;
    private TextView countText;
    private int count;
    private float curRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_one);

        // set up toolbar
        setupToolbar();
        setupCollapsingToolbarLayout();

        detailImage = (ImageView) findViewById(R.id.detail_image);
        detailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        getRatingBar = (RatingBar) findViewById(R.id.getRating);
        setRatingBar = (RatingBar) findViewById(R.id.setRating);
        countText = (TextView) findViewById(R.id.countText);

        getRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingChanged(ratingBar, rating, fromUser);
            }
        });

        // Testing Toolbar Height
        int toolBarHeight = Utils.getToolbarHeight(this);
        Log.d("TEAMPS", toolBarHeight + " : Toolbar Height");
        // 5.1 = D/TEAMPS﹕
        // 5.0 = D/TEAMPS﹕
        // 4.2 = D/TEAMPS﹕   : diff =

    }

    public void ratingChanged(RatingBar ratingBar, float rating, boolean fromUser){
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        curRate = Float.valueOf(decimalFormat.format((curRate * count + rating)/ ++count));

        setRatingBar.setRating(curRate);
        countText.setText(count + " Ratings");
    }
    public void openGallery(){
        startActivity(new Intent(DetailOneActivity.this, GalleryActivity.class));

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_one, menu);
        return true;
    }

    private void setupCollapsingToolbarLayout() {

        txtTitle = (TextView) findViewById(R.id.title);
        if(Utils.isAndroid_5_0()){
            txtTitle.setPadding((int) this.getResources().getDimension(R.dimen.app_bar_title_padding_left), (int) (this.getResources().getDimension(R.dimen.app_bar_title_padding_bottom) - 25), 0, 0);
            txtTitle.requestLayout();
        }

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

    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Hola");
        setSupportActionBar(toolbar);

        if(Utils.isAndroid_5_0()){
            //substitute parameters for left, top, right, bottom
            Utils.setMargins(toolbar, 0, -78, 0, 0);
        }

//        String version = Build.VERSION.RELEASE;
//        if(version != "" && version != null){
//            String[] versionDetail = version.split("\\.");
//            Log.d("TEAMPS", "0 : "+ versionDetail[0] + " 1 : " + versionDetail[1]);
//            if(versionDetail[0].equals("5")){
//                if(versionDetail[1].equals("0") || versionDetail[1].equals("00")){
//                    //substitute parameters for left, top, right, bottom
//                    Utils.setMargins(toolbar, 0, -78, 0, 0);
//                }
//            }
//        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
       // getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
