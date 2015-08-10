package com.panaceasoft.citiesdirectory.activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.utilities.PSVolley;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import com.pnikosis.materialishprogress.ProgressWheel;

import android.support.v7.app.AlertDialog;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class InquiryActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText input_name;
    private EditText input_email;
    private EditText input_message;


    private SharedPreferences pref;

    ProgressBar pb;

    //private Animation shake;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        setContentView(R.layout.activity_inquiry);
        setupToolbar();

    }


    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.inquiry));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void doInquiry(View view) {

        if (inputValidation()) {

            pb = (ProgressBar) findViewById(R.id.loading_spinner);
            pb.setVisibility(view.VISIBLE);

            final String URL = Config.APP_API_URL + Config.POST_SHOP_INQUIRY + pref.getInt("_id", 0);
            Utils.psLog(URL);
            input_name = (EditText) findViewById(R.id.input_name);
            input_email = (EditText) findViewById(R.id.input_email);
            input_message = (EditText) findViewById(R.id.input_message);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("name", input_name.getText().toString());
            params.put("email", input_email.getText().toString());
            params.put("message", input_message.getText().toString());
            doSubmit(URL, params, view);
        }
    }

    public void doSubmit(String URL, final HashMap<String,String> params, final View view) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //VolleyLog.v("Response:%n %s", response.toString(4));

                            pb = (ProgressBar) findViewById(R.id.loading_spinner);
                            pb.setVisibility(view.GONE);

                            String success_status = response.getString("success");
                            Utils.psLog(success_status);
                            if(success_status != null){
                                showSuccessPopup();
                            } else {
                                showFailPopup();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    public void showSuccessPopup() {
        new MaterialDialog.Builder(this)
                .title(R.string.inquiry)
                .content(R.string.inquiry_success)
                .positiveText(R.string.OK)
                .callback(new MaterialDialog.ButtonCallback() {
                    public void onPositive(MaterialDialog dialog) {
                        finish();
                    }
                })
                .show();
    }

    public void showFailPopup() {
        new MaterialDialog.Builder(this)
                .title(R.string.inquiry)
                .content(R.string.inquiry_fail)
                .positiveText(R.string.OK)
                .show();
    }

    public boolean inputValidation() {

        input_name = (EditText) findViewById(R.id.input_name);
        input_email = (EditText) findViewById(R.id.input_email);
        input_message = (EditText) findViewById(R.id.input_message);

        //shake = AnimationUtils.loadAnimation(this, R.anim.shake);

        if(input_name.getText().toString().equals("")){
            //input_name.setAnimation(shake);
            Utils.psLog("Name Fail");
            Toast.makeText(getApplicationContext(), R.string.name_validation_message,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if(input_email.getText().toString().equals("")) {
            Utils.psLog("Email Fail");
            Toast.makeText(getApplicationContext(), R.string.email_validation_message,
                    Toast.LENGTH_LONG).show();
            return false;
        } else {
            if(!Utils.isEmailFormatValid(input_email.getText().toString())) {
                Toast.makeText(getApplicationContext(), R.string.email_format_validation_message,
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }

        if(input_message.getText().toString().equals("")){
            Utils.psLog("Msg Fail");
            Toast.makeText(getApplicationContext(), R.string.inquiry_validation_message,
                    Toast.LENGTH_LONG).show();
            //input_message.setAnimation(shake);
            return false;
        }

        return true;
    }

}
