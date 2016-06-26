package com.example.hp.loginapp;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMsg extends Fragment implements View.OnClickListener{

    private static final String REGISTER_URL = SigninActivity.ip+"/uploading/message.php";
    public static final String KEY_MESSAGE = "message";


    private EditText etmessage;
    private Button btsend;
    private ProgressDialog progress;

    public FragmentMsg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_fragment_send_msg, container, false);
        etmessage = (EditText)view.findViewById(R.id.etmessage);
        btsend = (Button)view.findViewById(R.id.btsend);


        btsend.setOnClickListener(this);
        return view;

        // Inflate the layout for this fragment

    }
    private void sendMsg(){
        final String message = etmessage.getText().toString().trim();

        progress = new ProgressDialog(getActivity());
        progress.setMessage("Sending...");
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

                    }
                    catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        Toast.makeText(getActivity(),response,Toast.LENGTH_LONG).show();

                        if(response.trim().equals("message sent")) {
                            etmessage.setText("");
                            btsend.setEnabled(true);
                        }
                        else{

                            btsend.setEnabled(true);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                        Toast.makeText(getActivity(),"No connection",Toast.LENGTH_LONG).show();
                        btsend.setEnabled(true);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                SharedPreferences settings =getActivity().getSharedPreferences(SigninActivity.PREFS_NAME, 0);
                String user=settings.getString("user","no name");

                params.put(KEY_MESSAGE,message);
                params.put(SigninActivity.KEY_USERNAME, user);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);

    }



    @Override
    public void onClick(View v) {
        btsend.setEnabled(false);
        sendMsg();
    }
}
