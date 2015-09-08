package com.panaceasoft.citiesdirectory.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.DefaultRetryPolicy;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    private Toolbar toolbar;
    private EditText etUserName;
    private EditText etEmail;
    private EditText etAboutMe;
    private ImageView ivProfilePhoto;
    private SharedPreferences pref;
    private ProgressBar pb;
    private int userId;
    private static int RESULT_LOAD_IMAGE = 1;
    private String encodedString;
    private String fileName;
    private Bitmap myImg;
    private ProgressDialog prgDialog;
    private SpannableString editProfileString;
    private String jsonStatusSuccessString ;
    private String photoUploadSuccess;
    private String photoUploadNotSuccess;

    /**------------------------------------------------------------------------------------------------
     * End Block - Private Variables
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Override Functions
     **------------------------------------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initData();

        initUI();

        bindData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            final Intent intent;
            intent = new Intent(this, PasswordUpdateActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_to_left, R.anim.blank_anim);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.blank_anim,R.anim.left_to_right);
        return;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                String fileNameSegments[] = picturePath.split("/");
                fileName = fileNameSegments[fileNameSegments.length - 1];

                myImg = Bitmap.createBitmap(getResizedBitmap(Utils.getUnRotatedImage(picturePath, BitmapFactory.decodeFile(picturePath)), 400));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                myImg.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                byte[] byte_arr = stream.toByteArray();
                encodedString = Base64.encodeToString(byte_arr, 0);
                uploadImage();

            }
        }catch(Exception e){
            Utils.psErrorLogE("Error in load image.", e);
        }

    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Override Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/
    private void initData(){
        try {
            pref = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
            userId = pref.getInt("_login_user_id", 0);

            editProfileString = Utils.getSpannableString(getString(R.string.edit_profile));

            jsonStatusSuccessString = getResources().getString(R.string.json_status_success);
            photoUploadSuccess = getResources().getString(R.string.photo_upload_success);
            photoUploadNotSuccess = getResources().getString(R.string.photo_upload_not_success);
        } catch (Resources.NotFoundException e) {
            Utils.psErrorLogE("Error in initData.", e);
        }

    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Init Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/
    private void initUI(){
        try {
            initToolbar();

            etUserName = (EditText) findViewById(R.id.input_name);
            etEmail = (EditText) findViewById(R.id.input_email);
            etAboutMe = (EditText) findViewById(R.id.input_about_me);
            ivProfilePhoto = (ImageView) findViewById(R.id.fab);
            ivProfilePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent i = new Intent(
                                Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        startActivityForResult(i, RESULT_LOAD_IMAGE);
                    }catch (Exception e){
                        Utils.psErrorLogE("Error in Image Gallery.", e);
                    }
                }
            });

            prgDialog = new ProgressDialog(this);
            prgDialog.setMessage("Please wait...");
            prgDialog.setCancelable(false);
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initUI.", e);
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
        } catch (Exception e) {
            Utils.psErrorLogE("Error in initToolbar.", e);
        }

    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Init UI Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/
    private void bindData() {
        try {
            etUserName.setText(pref.getString("_login_user_name", "").toString());
            etEmail.setText(pref.getString("_login_user_email", "").toString());
            etAboutMe.setText(pref.getString("_login_user_about_me", "").toString());

            File file;

            file = new File(Environment.getExternalStorageDirectory() + "/" + pref.getString("_login_user_photo", ""));
            if (file.exists()) {

                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                ivProfilePhoto.setImageBitmap(myBitmap);

            } else {
                Drawable myDrawable = getResources().getDrawable(R.drawable.ic_account);
                ivProfilePhoto.setImageDrawable(myDrawable);
            }

            toolbar.setTitle(editProfileString);

        }catch(Exception e){
            Utils.psErrorLogE("Error in Bind Data.", e);
        }
    }
    /**------------------------------------------------------------------------------------------------
     * End Block - Bind Data Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Private Functions
     **------------------------------------------------------------------------------------------------*/
    private boolean inputValidation() {

        if (etUserName.getText().toString().trim() == "") {
            Toast.makeText(getApplicationContext(), R.string.name_validation_message,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (etEmail.getText().toString().trim() == "") {
            Toast.makeText(getApplicationContext(), R.string.email_validation_message,
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

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

                                // after server success
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("_login_user_name", etUserName.getText().toString());
                                editor.putString("_login_user_email", etEmail.getText().toString());
                                editor.putString("_login_user_about_me", etAboutMe.getText().toString());

                                editor.commit();

                                prgDialog.cancel();
                                showSuccessMessage(response.getString("data"));

                                Utils.activity.refreshProfileData();

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

    private void showSuccessMessage(String success_status) {
        Toast.makeText(this, success_status, Toast.LENGTH_SHORT).show();
    }

    private void showFailPopup() {
        new MaterialDialog.Builder(this)
                .title(R.string.register)
                .content(R.string.profile_edit_fail)
                .positiveText(R.string.OK)
                .show();
    }

    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void uploadImage() {

        prgDialog.show();
        RequestQueue rq = Volley.newRequestQueue(this);
        String url = Config.APP_API_URL + Config.POST_PROFILE_IMAGE;
        Utils.psLog("URL" + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    Utils.psLog("Server RESPONSE >> " + response);
                    JSONObject obj = new JSONObject(response);
                    String status = obj.getString("status");
                    if (status.equals(jsonStatusSuccessString)) {
                        String file_name = obj.getString("data");

                        Utils.psLog("success img upload to server");

                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("_login_user_photo", file_name);
                        editor.commit();

                        File file = null;

                        file = new File(Environment.getExternalStorageDirectory() + "/" + file_name);

                        file.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(file);
                        myImg.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                        ostream.close();

                        ivProfilePhoto.setImageBitmap(myImg);
                        prgDialog.cancel();
                        Toast.makeText(getBaseContext(),photoUploadSuccess
                                , Toast.LENGTH_SHORT)
                                .show();

                        Utils.activity.refreshProfileData();


                    } else {
                        Toast.makeText(getBaseContext(),
                                photoUploadNotSuccess, Toast.LENGTH_SHORT)
                                .show();
                        prgDialog.cancel();
                    }

                } catch (JSONException e) {
                    Utils.psLog("JSON Exception" + e.toString());
                    Toast.makeText(getBaseContext(),
                            "Error while loading data!",
                            Toast.LENGTH_LONG).show();
                    prgDialog.cancel();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    prgDialog.cancel();
                } catch (IOException e) {
                    e.printStackTrace();
                    prgDialog.cancel();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.psLog("ERROR" + "Error [" + error + "]");
                Toast.makeText(getBaseContext(),
                        "Cannot connect to server", Toast.LENGTH_LONG)
                        .show();
                prgDialog.hide();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("image", encodedString);
                params.put("filename", fileName);
                params.put("userId", userId + "");

                return params;

            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(stringRequest);
    }
    /**------------------------------------------------------------------------------------------------
     * End Block - Private Functions
     **------------------------------------------------------------------------------------------------*/

    /**------------------------------------------------------------------------------------------------
     * Start Block - Public Functions
     **------------------------------------------------------------------------------------------------*/
    public void doUpdate(View view) {

        if (inputValidation()) {
            pb = (ProgressBar) findViewById(R.id.loading_spinner);
            pb.setVisibility(view.VISIBLE);

            final String URL = Config.APP_API_URL + Config.PUT_USER_UPDATE + userId;
            Utils.psLog(URL);

            HashMap<String, String> params = new HashMap<>();
            params.put("username", etUserName.getText().toString().trim());
            params.put("email", etEmail.getText().toString().trim());
            params.put("about_me", etAboutMe.getText().toString().trim());

            doSubmit(URL, params);

        }

    }

    /**------------------------------------------------------------------------------------------------
     * End Block - Public Functions
     **------------------------------------------------------------------------------------------------*/


}
