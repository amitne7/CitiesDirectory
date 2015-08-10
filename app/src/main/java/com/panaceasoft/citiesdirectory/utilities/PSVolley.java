package com.panaceasoft.citiesdirectory.utilities;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.activities.InquiryActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Panacea-Soft on 21/7/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class PSVolley {

    public static void doPost(String URL, final HashMap<String,String> params,Context context, final VolleyCallback volleyCallback) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //VolleyLog.v("Response:%n %s", response.toString(4));
                        //return response;
                        //Config.apiResponse = response;
                        //Config.setApiResponse(response);
                        volleyCallback.onSuccess(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        // add the request object to the queue to be executed
        mRequestQueue.add(req);
    }

    public interface VolleyCallback{
        void onSuccess(JSONObject result);
    }

}
