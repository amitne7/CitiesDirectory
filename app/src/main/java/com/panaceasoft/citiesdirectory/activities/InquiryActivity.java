package com.panaceasoft.citiesdirectory.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import android.widget.Button;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class InquiryActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText txtName;
    private EditText txtEmail;
    private EditText txtMessage;
    private SharedPreferences pref;
    private ProgressBar pb;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        setContentView(R.layout.activity_inquiry);
        setupToolbar();
        initUI();
    }

    private void initUI() {
        txtName = (EditText) findViewById(R.id.input_name);
        txtName.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));
        txtEmail = (EditText) findViewById(R.id.input_email);
        txtEmail.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));
        txtMessage = (EditText) findViewById(R.id.input_message);
        txtMessage.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));
        btnSubmit = (Button) findViewById(R.id.button_submit);
        btnSubmit.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));
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

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("name", txtName.getText().toString());
            params.put("email", txtEmail.getText().toString());
            params.put("message", txtMessage.getText().toString());
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

        if(txtName.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), R.string.name_validation_message,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if(txtEmail.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), R.string.email_validation_message,
                    Toast.LENGTH_LONG).show();
            return false;
        } else {
            if(!Utils.isEmailFormatValid(txtEmail.getText().toString())) {
                Toast.makeText(getApplicationContext(), R.string.email_format_validation_message,
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }

        if(txtMessage.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), R.string.inquiry_validation_message,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

}
