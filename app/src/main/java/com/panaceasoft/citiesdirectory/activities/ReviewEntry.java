package com.panaceasoft.citiesdirectory.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.GlobalData;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.models.PItemData;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.HashMap;

public class ReviewEntry extends AppCompatActivity {
    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Private Variables
    //-------------------------------------------------------------------------------------------------------------------------------------

    private Toolbar toolbar;
    private SharedPreferences pref;
    private TextView txtUserName;
    private TextView txtUserEmail;
    private EditText txtReviewMessage;
    private int selectedItemId;
    private int selectedCityId;
    private ProgressBar pb;
    private String jsonStatusSuccessString;
    private SpannableString reviewString;

    //-------------------------------------------------------------------------------------------------------------------------------------
    //end region // Private Variables
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_entry);

        initUI();

        initData();

        bindData();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //end region // Override Functions
    //-------------------------------------------------------------------------------------------------------------------------------------


    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // init UI Functions
    //-------------------------------------------------------------------------------------------------------------------------------------


    private void initUI() {
        initToolbar();
    }

    private void initToolbar() {
        try {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            toolbar.setTitle(reviewString);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initToolbar.", e);
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //end region // init UI Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // init Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    private void initData() {
        try {
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
            reviewString = Utils.getSpannableString(getString(R.string.review));

            pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            selectedItemId = getIntent().getIntExtra("selected_item_id", 0);
            selectedCityId = getIntent().getIntExtra("selected_city_id", 0);
        }catch(Exception e){
            Utils.psErrorLogE("Error in init data.", e);
        }

    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //end region // init Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------


    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Bind Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    private void bindData() {
        try {
            txtUserName = (TextView) findViewById(R.id.login_user_name);
            txtUserEmail = (TextView) findViewById(R.id.login_user_email);

            txtUserName.setText(pref.getString("_login_user_name", "").toString());
            txtUserEmail.setText(pref.getString("_login_user_email", "").toString());
        } catch (Exception e) {
            Utils.psErrorLogE("Error in bindData.", e);
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //end region // Bind Data Functions
    //-------------------------------------------------------------------------------------------------------------------------------------


    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Public Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    public void doReview(View view) {
        if (inputValidation()) {
            pb = (ProgressBar) findViewById(R.id.loading_spinner);
            pb.setVisibility(view.VISIBLE);

            final String URL = Config.APP_API_URL + Config.POST_REVIEW + getIntent().getExtras().getInt("selected_item_id");
            txtReviewMessage = (EditText) findViewById(R.id.input_review_message);

            HashMap<String, String> params = new HashMap<>();
            params.put("review", txtReviewMessage.getText().toString().trim());
            params.put("appuser_id", String.valueOf(pref.getInt("_login_user_id", 0)));
            params.put("city_id", String.valueOf(pref.getInt("_id", 0)));

            doSubmit(URL, params, view);
        }

    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //end region // Public Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------
    //region // Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

    private void doSubmit(String postURL, HashMap<String, String> params, final View view) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String success_status = response.getString("status");

                            if (success_status.equals(jsonStatusSuccessString)) {
                                Gson gson = new Gson();
                                Type listType = new TypeToken<PItemData>() {
                                }.getType();
                                GlobalData.itemData = (PItemData) gson.fromJson(response.getString("data"), listType);

                                pb = (ProgressBar) findViewById(R.id.loading_spinner);
                                pb.setVisibility(View.GONE);

                                Utils.psLog(success_status);
                                if (success_status != null) {
                                    Utils.psLog(" --- Need to refresh review list and count --- ");
                                    //showSuccessPopup();
                                    Intent in = new Intent();
                                    setResult(RESULT_OK, in);
                                    finish();
                                } else {
                                    showFailPopup();
                                }
                            } else {

                                Utils.psLog("Error in loading.");
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

    private void showFailPopup() {
        new MaterialDialog.Builder(this)
                .title(R.string.review)
                .content(R.string.review_fail)
                .positiveText(R.string.OK)
                .show();
    }

    private void showSuccessPopup() {
        new MaterialDialog.Builder(this)
                .title(R.string.review)
                .content(R.string.review_success)
                .positiveText(R.string.OK)
                .callback(new MaterialDialog.ButtonCallback() {
                    public void onPositive(MaterialDialog dialog) {
                        finish();
                    }
                })
                .show();
    }

    private boolean inputValidation() {
        txtReviewMessage = (EditText) findViewById(R.id.input_review_message);

        if (txtReviewMessage.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), R.string.review_validation_message,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;

    }

    //-------------------------------------------------------------------------------------------------------------------------------------
    //end region // Private Functions
    //-------------------------------------------------------------------------------------------------------------------------------------

}
