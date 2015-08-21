package com.panaceasoft.citiesdirectory.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import android.app.ProgressDialog;
import org.json.JSONObject;
import java.io.IOException;

/**
 * Created by Panacea-Soft on 8/6/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class NotificationFragment extends Fragment {
    private View view;
    private ToggleButton tgNoti;
    private Button btnSubmit;
    private String regId = "";
    GoogleCloudMessaging gcmObj;
    private SharedPreferences pref;
    private TextView txtMessage;
    RequestParams params = new RequestParams();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    AsyncTask<Void, Void, String> createRegIdTask;
    private String URL = "";
    ProgressDialog prgDialog;


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
                if (tgNoti.isChecked()) {
                    getTokenInBackground("reg");
                } else {
                    getTokenInBackground("unreg");
                }

            }
        });

        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);

    }

    private void getTokenInBackground(final String status) {
        prgDialog.show();
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
                if(!regId.equals("")) {
                    submitToServer(status, regId);
                } else {
                    Toast.makeText(
                            getActivity().getApplicationContext(),
                            getString(R.string.service_not_available),
                            Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);
    }


    private void submitToServer(final String toggleStatus, String token) {


        if(toggleStatus.toString().equals("reg")) {
            URL = Config.APP_API_URL + Config.POST_GCM_REGISTER;
        } else {
            URL = Config.APP_API_URL + Config.POST_GCM_UNREGISTER;
        }
        params.put("reg_id", token);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(URL, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {

                        hideProgress();

                        Utils.psLog("Server Resp : " + response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("status").toString().equals("yes")) {
                                if (toggleStatus.toString().equals("reg")) {
                                    Toast.makeText(
                                            getActivity().getApplicationContext(),
                                            getString(R.string.gcm_register_success),
                                            Toast.LENGTH_LONG).show();

                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putBoolean("_push_noti_setting", true);
                                    editor.commit();

                                } else {
                                    Toast.makeText(
                                            getActivity().getApplicationContext(),
                                            getString(R.string.gcm_unregister_success),
                                            Toast.LENGTH_LONG).show();

                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putBoolean("_push_noti_setting", true);
                                    editor.commit();
                                }
                            } else {
                                Toast.makeText(
                                        getActivity().getApplicationContext(),
                                        getString(R.string.gcm_register_not_success),
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (Throwable t) {
                            Utils.psLog("Catch : " + t.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {

                        hideProgress();

                        if (statusCode == 404) {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    getString(R.string.request_not_found),
                                    Toast.LENGTH_LONG).show();
                        } else if (statusCode == 500) {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    getString(R.string.something_wrong),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(
                                    getActivity().getApplicationContext(),
                                    getString(R.string.cannot_connect),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void hideProgress() {
        prgDialog.hide();
        if (prgDialog != null) {
            prgDialog.dismiss();
        }
    }

}
