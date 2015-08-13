package com.panaceasoft.citiesdirectory.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.panaceasoft.citiesdirectory.R;


public class UserForgotPasswordFragment extends Fragment {
    private View view;
    private EditText input_email;
    private Button button_request;
    private ProgressBar pb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_forgot_password, container, false);
        return view;
    }

    private void initUI() {
        input_email = (EditText) this.view.findViewById(R.id.input_email);
        button_request = (Button) this.view.findViewById(R.id.button_request);
        button_request.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                doRequest();
            }
        });
    }

    private void doRequest() {
        if(inputValidation()) {
            //pb.setVisibility(view.VISIBLE);
            //final String URL = Config.APP_API_URL + Config.GET_FORGOT_PASSWORD + input_email.getText().toString().trim();
            //doSubmit(URL,view);
        }
    }

    private boolean inputValidation() {
        if(input_email.getText().toString().equals("")) {
            Toast.makeText(getActivity().getApplicationContext(), R.string.email_validation_message,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void doSubmit(String uri,View view) {
        StringRequest request = new StringRequest(uri,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pb.setVisibility(View.GONE);
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError ex) {
                        pb.setVisibility(View.GONE);

                        NetworkResponse response = ex.networkResponse;
                        if(response != null && response.data != null){

                        } else {

                        }

                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));



        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(request);
    }

}
