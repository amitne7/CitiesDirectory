package com.panaceasoft.citiesdirectory.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.panaceasoft.citiesdirectory.R;

public class UserLogin extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        setupToolbar();
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.login));
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


// Old Code

//    private EditText input_email;
//    private EditText input_password;
//    ProgressBar pb;

//
//    public void doLogin(View view) {
//
//        if(inputValidation()) {
//            pb = (ProgressBar) findViewById(R.id.loading_spinner);
//            pb.setVisibility(view.VISIBLE);
//
//            final String URL = Config.APP_API_URL + Config.POST_USER_LOGIN;
//            Utils.psLog(URL);
//
//            input_email = (EditText) findViewById(R.id.input_email);
//            input_password = (EditText) findViewById(R.id.input_password);
//
//            HashMap<String, String> params = new HashMap<>();
//            params.put("email", input_email.getText().toString().trim());
//            params.put("password",input_password.getText().toString().trim());
//
//            doSubmit(URL, params, view);
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
//                            String user_id = response.getString("id");
//                            String user_name = response.getString("username");
//                            String email = response.getString("email");
//                            String about_me = response.getString("about_me");
//                            String is_banned = response.getString("is_banned");
//                            String user_profile_photo = response.getString("profile_photo");
//                            String user_bg_photo = response.getString("background_photo");
//                            String delivery_address = response.getString("delivery_address");
//                            String billing_address = response.getString("billing_address");
//
//
//                            Utils.psLog(user_id);
//                            if(user_id != null){
//                                //showSuccessPopup();
//                                Utils.psLog("Successful Login, Need to Store in SQLite DB.");
//
//
//                                /*
//                                if(!response.getString("profile_photo").toString().equals("")) {
//                                    InputStream in = new URL(Config.APP_IMAGES_URL + user_profile_photo).openConnection().getInputStream();
//                                    Bitmap profileBitmap = BitmapFactory.decodeStream(in);
//                                    Utils.saveBitmapImage(getApplicationContext(),profileBitmap,"profile_photo");
//                                }
//                                */
//
//                                /*
//                                if(!response.getString("background_photo").toString().equals("")) {
//                                    InputStream in = new URL(Config.APP_IMAGES_URL + user_bg_photo).openConnection().getInputStream();
//                                    Bitmap bgBitmap = BitmapFactory.decodeStream(in);
//                                    Utils.saveBitmapImage(getApplicationContext(),bgBitmap,"background_photo");
//                                }
//                                */
//
//
//                                DatabaseHelper db = new DatabaseHelper(getApplication());
//                                Utils.psLog("..... Inserting into DB....");
//                                db.addUser(new Users(Integer.parseInt(user_id), user_name, email, about_me, Integer.parseInt(is_banned), user_profile_photo, user_bg_photo, delivery_address, billing_address));
//
//                                Utils.psLog(" User Count : " + db.getUserCount());
//
//                                //Save Login User Info
//                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                                SharedPreferences.Editor editor = prefs.edit();
//                                editor.putInt("_login_user_id", Integer.parseInt(user_id));
//                                editor.putString("_login_user_name", user_name);
//                                editor.putString("_login_user_email", email);
//                                editor.putString("_login_user_about_me", about_me);
//                                editor.putString("_login_user_del_address", delivery_address);
//                                editor.putString("_login_user_bill_address", billing_address);
//                                editor.commit();
//
//
//                            } else {
//                                //showFailPopup();
//                                Utils.psLog("Login Fail");
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
//    private boolean inputValidation() {
//        input_email = (EditText) findViewById(R.id.input_email);
//        input_password = (EditText) findViewById(R.id.input_password);
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
//
//        return true;
//
//    }
//
//    public void showFailPopup() {
//        new MaterialDialog.Builder(this)
//                .title(R.string.login)
//                .content(R.string.login_fail)
//                .positiveText(R.string.OK)
//                .show();
//    }
//
//
//    public void doRegister(View view) {
//        Intent intent = new Intent(this,UserRegister.class);
//        startActivity(intent);
//    }


// End Old Code