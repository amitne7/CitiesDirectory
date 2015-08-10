package com.panaceasoft.citiesdirectory.adapters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.GlobalData;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.activities.SelectedCityActivity;
import com.panaceasoft.citiesdirectory.models.CityData;
import com.panaceasoft.citiesdirectory.models.PCityData;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.panaceasoft.citiesdirectory.R.anim.fab_in;

/**
 * Created by Panacea-Soft on 15/7/15.
 * Contact Email : teamps.is.cool@gmail.com
 */


public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder>  {

    private List<CityData> cities;
    private Activity activity;
    private int lastPosition = -1;

    ////////
    private List<PCityData> pCityDataList;

    public static class CityViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout cv;
        TextView cityName;
        ImageView cityPhoto;
        TextView cityDesc;


        CityViewHolder(View itemView) {
            super(itemView);
            cv = (RelativeLayout)itemView.findViewById(R.id.shop_cv);
            cityName = (TextView)itemView.findViewById(R.id.city_name);
            cityDesc = (TextView)itemView.findViewById(R.id.city_desc);
            cityPhoto = (ImageView)itemView.findViewById(R.id.city_photo);
            cityName.setTypeface(Utils.getTypeFace(Utils.Fonts.NOTO_SANS));
            cityDesc.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));
        }


    }



    //public CityAdapter(Context context, List<CityData> cities){
    public CityAdapter(Context context, List<PCityData> cities){
        this.activity = (Activity) context;
        //this.cities = cities;
        this.pCityDataList = cities;
        //queue = Volley.newRequestQueue(context);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_row_container, parent, false);
        CityViewHolder svh = new CityViewHolder(v);
        return svh;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public void onBindViewHolder(final CityViewHolder holder, int position) {

        //final CityData city = cities.get(position);
        final PCityData city = pCityDataList.get(position);

        //holder.cityName.setText(city.getName());
        //holder.cityDesc.setText(city.getDescription());
        //Picasso.with(holder.cityPhoto.getContext()).load(Config.APP_IMAGES_URL + city.getCover_image_file()).into(holder.cityPhoto);

        holder.cityName.setText(city.name);
        holder.cityDesc.setText(city.description);
        Picasso.with(holder.cityPhoto.getContext()).load(Config.APP_IMAGES_URL + city.cover_image_file).into(holder.cityPhoto);


        setAnimation(holder.cv, position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final Intent intent;
                //GlobalData.citydata = city;
                Log.d(" Selected City Name : ", city.name);
                Utils.psLog("City ID > " + city.id);

                /*
                intent = new Intent(holder.itemView.getContext(),SelectedCityActivity.class);
                intent.putExtra("selected_city", new CityDataWrapper(city));
                intent.putExtra("selected_city_id", city.getId());
                holder.itemView.getContext().startActivity(intent);
                */
                intent = new Intent(holder.itemView.getContext(),SelectedCityActivity.class);
                //intent.putExtra("selected_city", new CityDataWrapper(city));
                GlobalData.citydata = city;
                intent.putExtra("selected_city_id", city.id);
                holder.itemView.getContext().startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        //return cities.size();
        return pCityDataList.size();
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(activity, R.anim.abc_slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }else{
            lastPosition = position;
        }
    }

}
