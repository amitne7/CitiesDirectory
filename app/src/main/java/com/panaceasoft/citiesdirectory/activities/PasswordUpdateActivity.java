package com.panaceasoft.citiesdirectory.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    private Toolbar toolbar;
    private EditText etNewPassword;
    private EditText etConfirmNewPassword;
    private ProgressBar pb;
    private int userId;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_update);
        setupToolbar();
        setupUI();
        setupData();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_password_update, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.password_change));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setupUI() {
        etNewPassword = (EditText) findViewById(R.id.input_password);
        etConfirmNewPassword = (EditText) findViewById(R.id.input_password_confirm);
    }

    private void setupData() {
        pref = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        userId = pref.getInt("_login_user_id",0);


    }

    public void doUpdate(View view){
        if(inputValidation()) {
            pb = (ProgressBar) findViewById(R.id.loading_spinner);
            pb.setVisibility(view.VISIBLE);

            final String URL = Config.APP_API_URL + Config.POST_USER_UPDATE + userId;
            Utils.psLog(URL);

            HashMap<String, String> params = new HashMap<>();
            params.put("password", etNewPassword.getText().toString().trim());

            doSubmit(URL, params);

        }
    }

    private void doSubmit(String postURL, HashMap<String, String> params) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT, postURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String success_status = response.getString("success");

                            showSuccessMessage(success_status);

                            onBackPressed();

                        } catch (JSONException e) {
                            showFailPopup();
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

    private boolean inputValidation() {

        if(etNewPassword.getText().toString().trim() == ""){
            Toast.makeText(getApplicationContext(), R.string.password_validation_message,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if(etConfirmNewPassword.getText().toString().trim() == ""){
            Toast.makeText(getApplicationContext(), R.string.password_validation_message,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if(! etNewPassword.getText().toString().trim().equals(etConfirmNewPassword.getText().toString().trim())){
            Toast.makeText(getApplicationContext(), R.string.password_not_equal,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void showSuccessMessage(String success_status) {
        Toast.makeText(this, success_status, Toast.LENGTH_SHORT).show();
    }

    public void showFailPopup() {
        new MaterialDialog.Builder(this)
                .title(R.string.register)
                .content(R.string.profile_edit_fail)
                .positiveText(R.string.OK)
                .show();
    }
}
