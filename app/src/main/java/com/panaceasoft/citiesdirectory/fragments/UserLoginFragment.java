package com.panaceasoft.citiesdirectory.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.activities.MainActivity;
import com.panaceasoft.citiesdirectory.activities.UserForgotPasswordActivity;
import com.panaceasoft.citiesdirectory.activities.UserLoginActivity;
import com.panaceasoft.citiesdirectory.activities.UserRegisterActivity;
import com.panaceasoft.citiesdirectory.models.Users;
import com.panaceasoft.citiesdirectory.models.DatabaseHelper;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

/**
 * Created by Panacea-Soft on 8/1/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class UserLoginFragment extends Fragment {
    private View view;
    private EditText txtEmail;
    private EditText txtPassword;
    private Button btnLogin;
    private Button btnForgot;
    private Button btnRegister;
    private ProgressBar pb;
    private MaterialDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_login, container, false);
        initUI();
        return view;
    }

    private void initUI() {
        pb = (ProgressBar) this.view.findViewById(R.id.loading_spinner);

        txtEmail = (EditText) this.view.findViewById(R.id.input_email);
        txtEmail.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

        txtPassword = (EditText) this.view.findViewById(R.id.input_password);
        txtPassword.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

        btnLogin = (Button) this.view.findViewById(R.id.button_login);
        btnLogin.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

        btnForgot = (Button) this.view.findViewById(R.id.button_forgot);
        btnForgot.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

        btnRegister = (Button) this.view.findViewById(R.id.button_register);
        btnRegister.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });

        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.psLog("Forgot Click Here");
                doForgot();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegister();
            }
        });
    }

    private void doLogin() {

        if(inputValidation()) {

            pb.setVisibility(view.VISIBLE);

            final String URL = Config.APP_API_URL + Config.POST_USER_LOGIN;
            Utils.psLog(URL);

            HashMap<String, String> params = new HashMap<>();
            params.put("email", txtEmail.getText().toString().trim());
            params.put("password",txtPassword.getText().toString().trim());

            doSubmit(URL, params, view);

        }

    }

    private void doForgot() {
        if(getActivity() instanceof MainActivity) {

            ((MainActivity) getActivity()).openFragment(R.id.nav_register);
        }else if(getActivity() instanceof UserLoginActivity) {

            startActivity(new Intent(getActivity(),UserForgotPasswordActivity.class));
        }
    }

    private void doRegister() {
        if(getActivity() instanceof MainActivity) {

            ((MainActivity) getActivity()).openFragment(R.id.nav_register);
        }else if(getActivity() instanceof UserLoginActivity) {

            startActivity(new Intent(getActivity(),UserRegisterActivity.class));
        }
    }

    private void doSubmit(String postURL, HashMap<String, String> params, final View view) {

        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            pb.setVisibility(view.GONE);
                            Utils.psLog(" .... Starting User Login Callback .... ");

                            String user_id = response.getString("id");
                            String user_name = response.getString("username");
                            String email = response.getString("email");
                            String about_me = response.getString("about_me");
                            String is_banned = response.getString("is_banned");
                            String user_profile_photo = response.getString("profile_photo");

                            if(user_id != null){
                                //Utils.psLog("Successful Login, Need to Store in SQLite DB.");

                                //DatabaseHelper db = new DatabaseHelper(getActivity().getApplication());
                                //Utils.psLog("..... Inserting into DB....");
                                //db.addUser(new Users(Integer.parseInt(user_id), user_name, email, about_me, Integer.parseInt(is_banned), user_profile_photo));

                                //Utils.psLog(" User Count : " + db.getUserCount());

                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putInt("_login_user_id", Integer.parseInt(user_id));
                                editor.putString("_login_user_name", user_name);
                                editor.putString("_login_user_email", email);
                                editor.putString("_login_user_about_me", about_me);
                                editor.putString("_login_user_photo", user_profile_photo);
                                editor.commit();

                                Utils.activity.loadProfileImage(user_name, user_profile_photo);

                                // Update Menu
                                Utils.activity.changeMenu();

                                // Show profile Menu
                                if(getActivity() instanceof MainActivity) {
                                    ((MainActivity) getActivity()).openFragment(R.id.nav_profile_login);
                                }

                            } else {
                                Utils.psLog("Login Fail");
                                showFailPopup();

                            }

                        } catch (JSONException e) {
                            Utils.psLog("Login Fail : " + e.getMessage());
                            e.printStackTrace();
                            showFailPopup();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.psLog("Error: "+ error.getMessage());
            }
        });

        // add the request object to the queue to be executed
        mRequestQueue.add(req);

    }

    private boolean inputValidation() {

        if(txtEmail.getText().toString().equals("")) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.email_validation_message,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if(txtPassword.getText().toString().equals("")) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.password_validation_message,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;

    }

    private void showFailPopup() {
        if(dialog == null) {
            dialog = new MaterialDialog.Builder(getActivity())
                    .title(R.string.login)
                    .content(R.string.login_fail)
                    .positiveText(R.string.OK)
                    .show();
        }else{
            dialog.show();
        }
    }

}
