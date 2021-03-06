package com.panaceasoft.citiesdirectory.fragments;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.EditText;
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
import com.panaceasoft.citiesdirectory.activities.UserLoginActivity;
import com.panaceasoft.citiesdirectory.activities.UserRegisterActivity;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

/**
 * Created by Panacea-Soft on 8/1/15.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class UserRegisterFragment extends Fragment {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/
    private View view;
    private EditText txtName;
    private EditText txtEmail;
    private EditText txtPassword;
    private String userName;
    private String email;
    private Button btnRegister;
    private Button btnCancel;
    private ProgressDialog prgDialog;
    private String jsonStatusSuccessString;

    /**------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_register, container, false);

        initData();

        initUI();

        return view;
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/


    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **-----------------------------------------------------------------------------------------------*/

    private void initData() {
        try {
            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);

        } catch (Exception e) {
            Utils.psErrorLogE("Error in init data.", e);
        }
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/
    private void initUI() {
        txtName = (EditText) this.view.findViewById(R.id.input_name);
        txtEmail = (EditText) this.view.findViewById(R.id.input_email);
        txtPassword = (EditText) this.view.findViewById(R.id.input_password);
        btnRegister = (Button) this.view.findViewById(R.id.button_register);
        btnCancel = (Button) this.view.findViewById(R.id.button_cancel);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegister();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCancel();
            }
        });

        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    private void doCancel() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).openFragment(R.id.nav_profile);
        } else if (getActivity() instanceof UserRegisterActivity) {
            getActivity().finish();
        }
    }

    private boolean inputValidation() {

        if (txtName.getText().toString().equals("")) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.name_validation_message,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (txtEmail.getText().toString().equals("")) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.email_validation_message,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (txtPassword.getText().toString().equals("")) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.password_validation_message,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void doRegister() {

        if (inputValidation()) {

            final String URL = Config.APP_API_URL + Config.POST_USER_REGISTER;
            Utils.psLog(URL);

            userName = txtName.getText().toString().trim();
            email = txtEmail.getText().toString().trim();

            HashMap<String, String> params = new HashMap<>();
            params.put("username", txtName.getText().toString().trim());
            params.put("email", txtEmail.getText().toString().trim());
            params.put("password", txtPassword.getText().toString().trim());
            params.put("about_me", "");
            doSubmit(URL, params, view);

        }

    }

    private void doSubmit(String postURL, HashMap<String, String> params, final View view) {
        prgDialog.show();
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest req = new JsonObjectRequest(postURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String status = response.getString("status");
                            if (status.equals(jsonStatusSuccessString)) {

                                String user_id = response.getString("data");

                                //Save Login User Info
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putInt("_login_user_id", Integer.parseInt(user_id));
                                editor.putString("_login_user_name", userName);
                                editor.putString("_login_user_email", email);
                                editor.putString("_login_user_about_me", "");
                                editor.commit();

                                // Update Menu
                                Utils.activity.bindMenu();

                                // Show profile Menu
                                if (getActivity() instanceof MainActivity) {
                                    ((MainActivity) getActivity()).openFragment(R.id.nav_profile_login);
                                }

                                prgDialog.cancel();
                                showSuccessPopup();

                            } else {
                                prgDialog.cancel();
                                Utils.psLog("Register Fail");
                                showFailPopup();

                            }


                        } catch (JSONException e) {
                            prgDialog.cancel();
                            Utils.psLog("Register Fail");
                            showFailPopup();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                prgDialog.cancel();
                Utils.psLog("Error: " + error.getMessage());
            }
        });

        // add the request object to the queue to be executed
        mRequestQueue.add(req);

    }

    private void showFailPopup() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.register)
                .content(R.string.login_fail)
                .positiveText(R.string.OK)
                .show();
    }

    private void showSuccessPopup() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.register)
                .content(R.string.register_success)
                .positiveText(R.string.OK)
                .callback(new MaterialDialog.ButtonCallback() {
                    public void onPositive(MaterialDialog dialog) {
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).openFragment(R.id.nav_profile);
                        } else if (getActivity() instanceof UserLoginActivity) {
                            getActivity().finish();
                        }
                    }
                })
                .show();

    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/
}

