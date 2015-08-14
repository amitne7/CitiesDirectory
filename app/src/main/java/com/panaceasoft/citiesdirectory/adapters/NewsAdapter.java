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
import com.panaceasoft.citiesdirectory.models.PNewsData;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Panacea-Soft on 13/8/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class NewsAdapter extends BaseAdapter{

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<PNewsData> newsData;

    public NewsAdapter(Activity activity, ArrayList<PNewsData> newsData) {
        this.activity = activity;
        this.newsData = newsData;
    }

    @Override
    public int getCount() {
        return newsData.size();
    }

    @Override
    public Object getItem(int position) {
        return newsData.get(position);
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
            convertView = inflater.inflate(R.layout.news_row, null);

        TextView txtNewsTitle = (TextView) convertView.findViewById(R.id.news_title);
        txtNewsTitle.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));
        TextView txtMessage = (TextView) convertView.findViewById(R.id.message);
        txtMessage.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));
        TextView txtAgo = (TextView) convertView.findViewById(R.id.ago);
        txtAgo.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

        final ImageView imgNewsPhoto = (ImageView) convertView.findViewById(R.id.thumbnail);

        PNewsData news = newsData.get(position);

        txtNewsTitle.setText(news.title);
        txtMessage.setText(news.description.substring(0, Math.min(news.description.length(), 120)) + "...");
        txtAgo.setText(news.added);

        if(news.images.get(0).path != null) {
            Picasso.with(activity.getApplicationContext()).load(Config.APP_IMAGES_URL + news.images.get(0).path).into(imgNewsPhoto);
        }
        return convertView;
    }
}
