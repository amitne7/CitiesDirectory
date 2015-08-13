package com.panaceasoft.citiesdirectory.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.utilities.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by Panacea-Soft on 8/6/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class NotificationFragment extends Fragment {
    private View view;
    private ToggleButton tgNoti;
    private Button btnSubmit;
    String regId = "";
    GoogleCloudMessaging gcmObj;
    Context applicationContext;
    private SharedPreferences pref;
    private TextView txtMessage;

    ///
    RequestParams params = new RequestParams();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    AsyncTask<Void, Void, String> createRegIdTask;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        view = inflater.inflate(R.layout.fragment_notification, container, false);
        initUI();
        return view;
    }

    private void initUI() {
        tgNoti = (ToggleButton) view.findViewById(R.id.toggle_noti);
        btnSubmit = (Button) view.findViewById(R.id.button_submit);
        txtMessage = (TextView) view.findViewById(R.id.latest_push_message);

        if(pref.getBoolean("_push_noti_setting",false)) {
            tgNoti.setChecked(true);
        } else {
            tgNoti.setChecked(false);
        }

        if(!pref.getString("_push_noti_message","").toString().equals("")) {
            txtMessage.setText(pref.getString("_push_noti_message","").toString());
        } else {
            txtMessage.setText("No Message");
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //doSubmit(v);
                registerInBackground();
            }
        });

    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcmObj == null) {
                        gcmObj = GoogleCloudMessaging
                                .getInstance(getActivity().getApplicationContext());
                    }
                    regId = gcmObj
                            .register(Config.GOOGLE_PROJECT_NO);
                    msg = "Registration ID :" + regId;

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Utils.psLog(" Msg Val " + msg);
                storeRegIdinServer();
            }
        }.execute(null, null, null);
    }


    private void storeRegIdinServer() {
        params.put("regId", regId);
        final String URL = "http://www.panacea-soft.com/gcm/gcm.php?shareRegId=true";
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(URL, params,
                new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {
                        // Hide Progress Dialog
                        Utils.psLog("Server Resp : " + response);


                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        // Hide Progress Dialog

                        // When Http response code is '404'
                        if (statusCode == 404) {
                            Toast.makeText(applicationContext,
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(applicationContext,
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    applicationContext,
                                    "Unexpected Error occcured! [Most common Error: Device might "
                                            + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
