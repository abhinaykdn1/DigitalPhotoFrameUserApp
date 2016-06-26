package com.example.hp.loginapp;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
public class SettingFragment extends Fragment implements View.OnClickListener {
    private static final String REGISTER_URL = SigninActivity.ip+"/uploading/sendSettings.php";
    public static final String KEY_TIME= "time";


    private EditText editTextTime;
    private Button buttonSend;



    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_setting, container, false);


        editTextTime = (EditText)v.findViewById(R.id.editTextTime);
        buttonSend = (Button)v.findViewById(R.id.buttonSend);


        buttonSend.setOnClickListener(this);


        return v;
    }
    private void sendMsg(){
        final String time = editTextTime.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getActivity(),response,Toast.LENGTH_LONG).show();

                        if(response.trim().equals("sent")) {
                                      editTextTime.setText("");
                         //   finish();
                        }
                        else{
                            buttonSend.setEnabled(true);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),"Connection Error",Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                SharedPreferences settings = getActivity().getSharedPreferences(SigninActivity.PREFS_NAME, 0);
                String user=settings.getString("user","no name");

                Map<String,String> params = new HashMap<String, String>();
                params.put(KEY_TIME,time);
                params.put(SigninActivity.KEY_USERNAME, user);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);

    }



    @Override
    public void onClick(View v) {
        buttonSend.setEnabled(false);
        sendMsg();
        buttonSend.setEnabled(true);

    }

}
