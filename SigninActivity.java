package com.example.hp.loginapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String ip="http://172.26.166.165";  //ip of the server
    public static final String LOGIN_URL = ip+"/uploading/alogin.php";
    public static final String PREFS_NAME="MyPrefsFile";

    public static final String KEY_USERNAME="username";
    public static final String KEY_PASSWORD="password";

    private EditText editTextUsername;
    private EditText editTextPassword;
    private TextView signin;
    private TextView create;
    public static String username;
    private String password;

    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = getSharedPreferences(SigninActivity.PREFS_NAME, 0);
        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);
        if (hasLoggedIn) {
            openOptions();
        }


        setContentView(R.layout.activity_main);

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);


        create = (TextView) findViewById(R.id.Create);
        signin = (TextView) findViewById(R.id.signin);


        create.setOnClickListener(this);
        signin.setOnClickListener(this);


    }


    private void userLogin() {
        username = editTextUsername.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();

        progress = new ProgressDialog(this);
        progress.setMessage("Signing in...");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();
        final int totalProgressTime = 100;
        final Thread t = new Thread() {
            @Override
            public void run() {
                int jumpTime = 0;

                while(jumpTime < totalProgressTime) {
                    try {
                        sleep(10);
                        jumpTime += 5;
                        progress.setProgress(jumpTime);
                        // progress.show();

                    }
                    catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        if(response.trim().equals("success")) {
                            SharedPreferences settings =getSharedPreferences(SigninActivity.PREFS_NAME,0);
                            SharedPreferences.Editor editor=settings.edit();
                            editor.putBoolean("hasLoggedIn",true);
                            editor.putString("user",username);

                            editor.commit();
                            openOptions();
                            signin.setEnabled(true);
                        }

                        else{


                            Toast.makeText(SigninActivity.this,response,Toast.LENGTH_LONG).show();
                            signin.setEnabled(true);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                        Toast.makeText(SigninActivity.this,"No connection",Toast.LENGTH_LONG ).show();
                        signin.setEnabled(true);
                    }
                }){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String, String>();
                map.put(KEY_USERNAME,username);
                map.put(KEY_PASSWORD,password);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void openOptions(){
        Intent intent = new Intent(this, Navigator.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {

        if (v == signin){
            signin.setEnabled(false);
            userLogin();
        }
        else if (v == create){
            Intent intent = new Intent(this,register.class);
            startActivity(intent);
            finish();
        }
    }

}

