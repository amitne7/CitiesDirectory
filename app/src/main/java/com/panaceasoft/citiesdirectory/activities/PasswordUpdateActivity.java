package com.panaceasoft.citiesdirectory.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

public class PasswordUpdateActivity extends AppCompatActivity {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    private Toolbar toolbar;
    private EditText etNewPassword;
    private EditText etConfirmNewPassword;
    private int userId;
    private SharedPreferences pref;
    private ProgressDialog prgDialog;
    private String jsonStatusSuccessString;
    private SpannableString passwordChangeString;

    /**------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_update);

        initData();

        initUI();

        bindData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_password_update, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.blank_anim, R.anim.left_to_right);
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/


    private void initUI() {
        initToolbar();
        initProgressDialog();

        etNewPassword = (EditText) findViewById(R.id.input_password);
        etConfirmNewPassword = (EditText) findViewById(R.id.input_password_confirm);
    }

    private void initProgressDialog() {
        try {
            prgDialog = new ProgressDialog(this);
            prgDialog.setMessage("Please wait...");
            prgDialog.setCancelable(false);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initProgressDialog.", e);
        }
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

            toolbar.setTitle(passwordChangeString);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initToolbar.", e);
        }
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void initData(){
        try {
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
            passwordChangeString = Utils.getSpannableString(getString(R.string.password_change));
        }catch(Exception e){
            Utils.psErrorLogE("Error in init data.", e);
        }
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    private void bindData() {
        try {
            pref = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
            userId = pref.getInt("_login_user_id", 0);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in bindData.", e);
        }
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*///-------------------------------------------------------------------------------------------------------------------------------------

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     **------------------------------------------------------------------------------------------------*/
    private void doSubmit(String postURL, HashMap<String, String> params) {
        prgDialog.show();
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT, postURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccessString)) {

                                showSuccessMessage(response.getString("data"));

                                prgDialog.cancel();

                                onBackPressed();
                            } else {
                                prgDialog.cancel();
                                Utils.psLog("Error in loading.");
                            }

                        } catch (JSONException e) {
                            prgDialog.cancel();
                            showFailPopup();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                prgDialog.cancel();
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        // add the request object to the queue to be executed
        mRequestQueue.add(req);
    }

    private boolean inputValidation() {

        if (etNewPassword.getText().toString().trim() == "") {
            Toast.makeText(getApplicationContext(), R.string.password_validation_message,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (etConfirmNewPassword.getText().toString().trim() == "") {
            Toast.makeText(getApplicationContext(), R.string.password_validation_message,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (!etNewPassword.getText().toString().trim().equals(etConfirmNewPassword.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), R.string.password_not_equal,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void showSuccessMessage(String success_status) {
        Toast.makeText(this, success_status, Toast.LENGTH_SHORT).show();
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

    public void doUpdate(View view) {
        if (inputValidation()) {
            final String URL = Config.APP_API_URL + Config.POST_USER_UPDATE + userId;
            Utils.psLog(URL);

            HashMap<String, String> params = new HashMap<>();
            params.put("password", etNewPassword.getText().toString().trim());
            doSubmit(URL, params);
        }
    }

    public void showFailPopup() {
        new MaterialDialog.Builder(this)
                .title(R.string.register)
                .content(R.string.profile_edit_fail)
                .positiveText(R.string.OK)
                .show();
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Public Functions
     **------------------------------------------------------------------------------------------------*/

}
