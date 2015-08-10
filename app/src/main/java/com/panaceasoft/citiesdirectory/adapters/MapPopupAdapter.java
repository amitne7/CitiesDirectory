package com.panaceasoft.citiesdirectory.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.HashMap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.models.CityData;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by Panacea-Soft on 26/7/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class MapPopupAdapter implements GoogleMap.InfoWindowAdapter {
    private View popup=null;
    private LayoutInflater inflater=null;
    private HashMap<String, Bitmap> images = null;
    private HashMap<String, String> addressInfo = null;
    private Context ctxt=null;
    private int iconWidth=-1;
    private int iconHeight=-1;
    private Marker lastMarker=null;

    public MapPopupAdapter(Context ctxt, LayoutInflater inflater,
                           HashMap<String, Bitmap> images, HashMap<String, String> addressInfo) {
        this.ctxt = ctxt;
        this.inflater = inflater;
        this.images = images;
        this.addressInfo = addressInfo;

        iconWidth=
                ctxt.getResources().getDimensionPixelSize(R.dimen.map_icon_width);
        iconHeight=
                ctxt.getResources().getDimensionPixelSize(R.dimen.map_icon_height);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return(null);
    }

    @SuppressLint("InflateParams")
    @Override
    public View getInfoContents(Marker marker) {
        if (popup == null) {
            popup=inflater.inflate(R.layout.popup_marker, null);
        }

        if (lastMarker == null
                || !lastMarker.getId().equals(marker.getId())) {
            lastMarker=marker;

            TextView tv=(TextView)popup.findViewById(R.id.title);

            tv.setText(marker.getTitle());
            tv = (TextView)popup.findViewById(R.id.snippet);
            tv.setText(marker.getSnippet());

            tv = (TextView) popup.findViewById(R.id.address);
            tv.setText(addressInfo.get(marker.getId()));

            Bitmap image = images.get(marker.getId());
            ImageView icon = (ImageView)popup.findViewById(R.id.icon);

            icon.setImageBitmap(image);

        }

        return(popup);
    }


}
