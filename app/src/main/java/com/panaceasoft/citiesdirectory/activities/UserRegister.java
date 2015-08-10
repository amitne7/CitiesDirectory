package com.panaceasoft.citiesdirectory.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.panaceasoft.citiesdirectory.R;

public class UserRegister extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        setupToolbar();
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.register));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

}

// OLD CODE

//private EditText input_name;
//private EditText input_email;
//private EditText input_password;
//private String userName;
//private String email;
//
//ProgressBar pb;

//    private boolean inputValidation() {
//        input_name = (EditText) findViewById(R.id.input_name);
//        input_email = (EditText) findViewById(R.id.input_email);
//        input_password = (EditText) findViewById(R.id.input_password);
//
//        if(input_name.getText().toString().equals("")) {
//            Toast.makeText(getApplicationContext(), R.string.name_validation_message,
//                    Toast.LENGTH_LONG).show();
//            return false;
//        }
//
//        if(input_email.getText().toString().equals("")) {
//            Toast.makeText(getApplicationContext(), R.string.email_validation_message,
//                    Toast.LENGTH_LONG).show();
//            return false;
//        }
//
//        if(input_password.getText().toString().equals("")) {
//            Toast.makeText(getApplicationContext(), R.string.password_validation_message,
//                    Toast.LENGTH_LONG).show();
//            return false;
//        }
//        return true;
//    }
//
//    public void doRegister(View view) {
//
//        if(inputValidation()) {
//            pb = (ProgressBar) findViewById(R.id.loading_spinner);
//            pb.setVisibility(view.VISIBLE);
//
//            final String URL = Config.APP_API_URL + Config.POST_USER_REGISTER;
//            Utils.psLog(URL);
//
//            input_name = (EditText) findViewById(R.id.input_name);
//            input_email = (EditText) findViewById(R.id.input_email);
//            input_password = (EditText) findViewById(R.id.input_password);
//
//            userName = input_name.getText().toString().trim();
//            email = input_email.getText().toString().trim();
//
//            HashMap<String, String> params = new HashMap<>();
//            params.put("username", input_name.getText().toString().trim());
//            params.put("email", input_email.getText().toString().trim());
//            params.put("password", input_password.getText().toString().trim());
//
//            doSubmit(URL, params, view);
//
//
//        }
//
//    }
//
//    private void doSubmit(String postURL, HashMap<String, String> params, final View view) {
//
//        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
//        JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            //VolleyLog.v("Response:%n %s", response.toString(4));
//
//                            pb = (ProgressBar) findViewById(R.id.loading_spinner);
//                            pb.setVisibility(view.GONE);
//
//                            String user_id = response.getString("user_id");
//                            /*
//                            String user_name = response.getString("username");
//                            String email = response.getString("email");
//                            String about_me = response.getString("about_me");
//                            String is_banned = response.getString("is_banned");
//                            String user_profile_photo = response.getString("profile_photo");
//                            String user_bg_photo = response.getString("background_photo");
//                            String delivery_address = response.getString("delivery_address");
//                            String billing_address = response.getString("billing_address");
//                            */
//
//
//                            Utils.psLog(user_id);
//                            if(user_id != null){
//                                //showSuccessPopup();
//                                Utils.psLog("Successful Register, Need to Store in SQLite DB.");
//
//
//                                DatabaseHelper db = new DatabaseHelper(getApplication());
//                                Utils.psLog("..... Inserting into DB....");
//                                db.addUser(new Users(Integer.parseInt(user_id), userName, email, "", 0, "", "", "", ""));
//
//                                Utils.psLog(" User Count : " + db.getUserCount());
//
//                                //Save Login User Info
//                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                                SharedPreferences.Editor editor = prefs.edit();
//                                editor.putInt("_login_user_id", Integer.parseInt(user_id));
//                                editor.putString("_login_user_name", userName);
//                                editor.putString("_login_user_email", email);
//                                editor.putString("_login_user_about_me", "");
//                                editor.putString("_login_user_del_address", "");
//                                editor.putString("_login_user_bill_address", "");
//                                editor.commit();
//
//                                showSuccessPopup();
//
//                            } else {
//                                //showFailPopup();
//                                Utils.psLog("Register Fail");
//                                showFailPopup();
//
//                            }
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.e("Error: ", error.getMessage());
//            }
//        });
//
//        // add the request object to the queue to be executed
//        mRequestQueue.add(req);
//
//    }
//
//    public void showFailPopup() {
//        new MaterialDialog.Builder(this)
//                .title(R.string.register)
//                .content(R.string.login_fail)
//                .positiveText(R.string.OK)
//                .show();
//    }
//
//    private void showSuccessPopup() {
//        new MaterialDialog.Builder(this)
//                .title(R.string.register)
//                .content(R.string.register_success)
//                .positiveText(R.string.OK)
//                .callback(new MaterialDialog.ButtonCallback() {
//                    public void onPositive(MaterialDialog dialog) {
//                        finish();
//                    }
//                })
//                .show();
//    }

// END OLD CODE
