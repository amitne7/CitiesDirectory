package com.panaceasoft.citiesdirectory.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.models.PImageData;
import com.panaceasoft.citiesdirectory.models.PNewsData;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class NewsDetailActivity extends AppCompatActivity {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    private Toolbar toolbar;
    private PNewsData newsData;
    private ImageView newsImage;
    private TextView newsTitle;
    private TextView newsDescription;
    private CollapsingToolbarLayout collapsingToolbar;
    private ArrayList<PImageData> imageData;
    private Bundle bundle;
    private Intent intent;

    /**------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        initUI();

        initData();

        bindData();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    private void initUI() {
        initToolbar();
        initCollapsingToolbarLayout();
    }

    private void initToolbar() {
        try {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if(Utils.isAndroid_5_0()){
                Utils.setMargins(toolbar, 0, -102, 0, 0);
            }
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

    private void initCollapsingToolbarLayout(){
        try {
            collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initCollapsingToolbarLayout.", e);
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
            bundle = getIntent().getBundleExtra("news_bundle");
            newsData = bundle.getParcelable("news");
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initData.", e);
        }
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void bindData() {

        try {
            newsImage = (ImageView) findViewById(R.id.news_image);
            Picasso.with(getApplicationContext()).load(Config.APP_IMAGES_URL + newsData.images.get(0).path).into(newsImage);
            newsImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.psLog("Open Gallery");
                    bundle = new Bundle();
                    bundle.putParcelable("images", newsData);
                    bundle.putString("from","news");

                    intent = new Intent(getApplicationContext(), GalleryActivity.class);
                    intent.putExtra("images_bundle", bundle);
                    startActivity(intent);

                }
            });


            if(collapsingToolbar != null){
                collapsingToolbar.setTitle(newsData.title);
            }

            newsDescription = (TextView) findViewById(R.id.news_description);
            newsDescription.setText(newsData.description);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in bindData.", e);
        }

    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

}
