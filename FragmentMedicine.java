package com.example.hp.loginapp;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Hashtable;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMedicine extends Fragment implements View.OnClickListener {
    private static final String UPLOAD_URL = SigninActivity.ip+"/uploading/sendMedicine.php";
    public static final String KEY_MEDICINE = "days";
    public static final String KEY_SLOT1 = "slot1";
    public static final String KEY_SLOT2 = "slot2";
    public static final String KEY_SLOT3 = "slot3";
    private CheckBox sun;
    private CheckBox   mon;
    private CheckBox  tues;
    private CheckBox wed;
    private CheckBox  thurs;
    private CheckBox fri;
    private CheckBox sat;
    private EditText slot1;
    private EditText slot2;
    private EditText slot3;
    private Button send;
    private int days=0;
    private ProgressDialog progress;

    public FragmentMedicine() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View v = inflater.inflate(R.layout.fragment_fragment_medicine, container, false);
        mon = (CheckBox) v.findViewById(R.id.checkBox1);
        tues = (CheckBox) v.findViewById(R.id.checkBox2);
        wed = (CheckBox) v.findViewById(R.id.checkBox3);
        thurs = (CheckBox) v.findViewById(R.id.checkBox4);
        fri = (CheckBox) v.findViewById(R.id.checkBox5);
        sat = (CheckBox) v.findViewById(R.id.checkBox6);
        sun = (CheckBox) v.findViewById(R.id.checkBox7);

        send = (Button) v.findViewById(R.id.send);

        mon.setOnClickListener(this);
        tues.setOnClickListener(this);
        wed.setOnClickListener(this);
        thurs.setOnClickListener(this);
        fri.setOnClickListener(this);
        sat.setOnClickListener(this);
        sun.setOnClickListener(this);

        slot1 = (EditText) v.findViewById(R.id.slot1);
        slot2 = (EditText) v.findViewById(R.id.slot2);
        slot3 = (EditText) v.findViewById(R.id.slot3);
        send = (Button) v.findViewById(R.id.send);
        send.setOnClickListener(this);
         return v;
    }



        @Override
        public void onClick(View v)
        {
            if (v == mon)
                days += 1;
            if (v == tues)
                days += 10;
            if (v == wed)
                days += 100;
            if (v == thurs)
                days += 1000;
            if (v == fri)
                days += 10000;
            if (v == sat)
                days += 100000;
            if (v == sun)
                days += 1000000;
            if (v == send) {
                upload();
                send.setEnabled(false);
            }
        }

        private void upload(){

            sun.setChecked(false);
            mon.setChecked(false);
            tues.setChecked(false);
            wed.setChecked(false);
            thurs.setChecked(false);
            fri.setChecked(false);
            sat.setChecked(false);



            final String days2;
            final String slota,slotb,slotc;
            days2 = Integer.toString(days);
            days = 0;
            slota = slot1.getText().toString().trim();
            slotb = slot2.getText().toString().trim();
            slotc = slot3.getText().toString().trim();
            //Showing the progress dialog
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

            StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            //Disimissing the progress dialog
                            progress.dismiss();
                            //Showing toast message of the response
                            Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
                            slot1.setText("");
                            slot2.setText("");
                            slot3.setText("");
                            send.setEnabled(true);


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            //Dismissing the progress dialog
                            progress.dismiss();

                            //Showing toast
                            Toast.makeText(getActivity(), "Connection Error", Toast.LENGTH_LONG).show();
                            send.setEnabled(true);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {





                    //Creating parameters
                    Map<String, String> params = new Hashtable<String, String>();
                    SharedPreferences settings = getActivity().getSharedPreferences(SigninActivity.PREFS_NAME, 0);
                    String user=settings.getString("user","no name");

                    //Adding parameters
                    params.put(KEY_MEDICINE,days2);
                    params.put(SigninActivity.KEY_USERNAME, user);
                    params.put(KEY_SLOT1, slota);
                    params.put(KEY_SLOT2, slotb);
                    params.put(KEY_SLOT3, slotc);

                    //returning parameters
                    return params;
                }
            };

            //Creating a Request Queue
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

            //Adding request to the queue
            requestQueue.add(stringRequest);
        }




    }


