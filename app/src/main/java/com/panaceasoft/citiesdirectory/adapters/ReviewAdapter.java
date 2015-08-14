package com.panaceasoft.citiesdirectory.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.models.PReviewData;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Panacea-Soft on 30/7/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class ReviewAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<PReviewData> reviewData;

    public ReviewAdapter(Activity activity, ArrayList<PReviewData> reviewData) {
        this.activity = activity;
        this.reviewData = reviewData;
    }

    @Override
    public int getCount() {
        return reviewData.size();
    }

    @Override
    public Object getItem(int position) {
        return reviewData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.review_row, null);

        TextView txtUserName = (TextView) convertView.findViewById(R.id.user_name);
        txtUserName.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));
        TextView txtMessage = (TextView) convertView.findViewById(R.id.message);
        txtMessage.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));
        TextView txtAgo = (TextView) convertView.findViewById(R.id.ago);
        txtAgo.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));
        final ImageView imgUserPhoto = (ImageView) convertView.findViewById(R.id.thumbnail);

        PReviewData review = reviewData.get(position);

        txtUserName.setText(review.appuser_name);
        txtMessage.setText(review.review);
        txtAgo.setText(review.added);

        Picasso.with(activity.getApplicationContext()).load(Config.APP_IMAGES_URL + review.profile_photo).into(imgUserPhoto);

        return convertView;
    }
}
