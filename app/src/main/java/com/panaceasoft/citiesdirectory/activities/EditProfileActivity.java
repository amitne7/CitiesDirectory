package com.panaceasoft.citiesdirectory.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.panaceasoft.citiesdirectory.Config;
import com.panaceasoft.citiesdirectory.R;
import com.panaceasoft.citiesdirectory.utilities.Utils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText etUserName;
    private EditText etEmail;
    private EditText etAboutMe;
    private EditText etDeliveryAddress;
    private EditText etBillingAddress;
    private ImageView ivProfilePhoto;
    private SharedPreferences pref;
    private ProgressBar pb;
    private int userId;
    private static int RESULT_LOAD_IMAGE = 1;
    private String encodedString;
    private String fileName;
    private String selectedImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setupToolbar();
        setupUI();
        setupData();
    }

    private void setupData() {
        pref = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        userId = pref.getInt("_login_user_id",0);
        etUserName.setText(pref.getString("_login_user_name", "").toString());
        etEmail.setText(pref.getString("_login_user_email", "").toString());
        etAboutMe.setText(pref.getString("_login_user_about_me", "").toString());
        etDeliveryAddress.setText(pref.getString("_login_user_del_address", "").toString());
        etBillingAddress.setText(pref.getString("_login_user_bill_address", "").toString());

        File file = null;

        file = new File(Environment.getExternalStorageDirectory()+"/"+ pref.getString("_login_user_name", "")+".jpg");

        if(file.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            ivProfilePhoto.setImageBitmap(myBitmap);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);

        return true;
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.edit_profile));
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
        etUserName = (EditText) findViewById(R.id.input_name);
        etEmail = (EditText) findViewById(R.id.input_email);
        etAboutMe = (EditText) findViewById(R.id.input_about_me);
        etDeliveryAddress = (EditText) findViewById(R.id.input_delivery_address);
        etBillingAddress = (EditText) findViewById(R.id.input_billing_address);
        ivProfilePhoto = (ImageView) findViewById(R.id.iv_profile_photo);
        ivProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            final Intent intent;
            intent = new Intent(this, PasswordUpdateActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Code to do refresh Data in Detail Activity
        Intent in = new Intent();
        setResult(RESULT_OK, in);
        finish();
        return;
    }

    public void doUpdate(View view){
        // Update code here
       // Utils.psLog(" " + etUserName.getText());

        // Server code here
        if(inputValidation()) {
            pb = (ProgressBar) findViewById(R.id.loading_spinner);
            pb.setVisibility(view.VISIBLE);

            final String URL = Config.APP_API_URL + Config.POST_USER_UPDATE + userId;
            Utils.psLog(URL);

            HashMap<String, String> params = new HashMap<>();
            params.put("username", etUserName.getText().toString().trim());
            params.put("email", etEmail.getText().toString().trim());
            params.put("about_me", etAboutMe.getText().toString().trim());

            doSubmit(URL, params);

        }




    }

    private boolean inputValidation() {

        if(etUserName.getText().toString().trim() == ""){
            Toast.makeText(getApplicationContext(), R.string.name_validation_message,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if(etEmail.getText().toString().trim() == ""){
            Toast.makeText(getApplicationContext(), R.string.email_validation_message,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void doSubmit(String postURL, HashMap<String, String> params) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT, postURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String success_status = response.getString("success");

                            // after server success
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("_login_user_name", etUserName.getText().toString());
                            editor.putString("_login_user_email", etEmail.getText().toString());
                            editor.putString("_login_user_about_me", etAboutMe.getText().toString());
                            editor.putString("_login_user_del_address", etAboutMe.getText().toString());
                            editor.putString("_login_user_bill_address", etAboutMe.getText().toString());

                            editor.commit();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ivProfilePhoto.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            String fileNameSegments[] = picturePath.split("/");
            fileName = fileNameSegments[fileNameSegments.length - 1];

            Bitmap myImg = BitmapFactory.decodeFile(picturePath);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Must compress the Image to reduce image size to make upload easy
            myImg.compress(Bitmap.CompressFormat.PNG, 50, stream);
            byte[] byte_arr = stream.toByteArray();
            // Encode Image to String
            encodedString = Base64.encodeToString(byte_arr, 0);

            uploadImage();

        }

    }

    /**
     * API call for upload selected image from gallery to the server
     */
    public void uploadImage() {

        RequestQueue rq = Volley.newRequestQueue(this);
        //String url = Config.APP_API_URL + Config.POST_IMAGE_PROFILE + userId + "/fileType/profile";
        String url = "http://192.168.1.2/test/php_upload.php";
        Utils.psLog("URL" + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    Utils.psLog("RESPONSE"+ response);
                    JSONObject json = new JSONObject(response);

                    Toast.makeText(getBaseContext(),
                            "The image is upload", Toast.LENGTH_SHORT)
                            .show();

                } catch (JSONException e) {
                    Utils.psLog("JSON Exception"+ e.toString());
                    Toast.makeText(getBaseContext(),
                            "Error while loadin data!",
                            Toast.LENGTH_LONG).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.psLog("ERROR"+ "Error [" + error + "]");
                Toast.makeText(getBaseContext(),
                        "Cannot connect to server", Toast.LENGTH_LONG)
                        .show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("image", encodedString);
                params.put("filename", fileName);

                return params;

            }

        };
        rq.add(stringRequest);
    }
}
