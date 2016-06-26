package com.example.hp.loginapp;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Interpolator;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSendPic extends Fragment implements View.OnClickListener {
    private Button buttonChoose;
    private Button buttonUpload;
    private ImageView imageView;
    private EditText editTextName;
    private ProgressDialog progress;

    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    private String UPLOAD_URL =SigninActivity.ip+"/uploading/upload.php";
    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";



    public FragmentSendPic() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v=inflater.inflate(R.layout.fragment_fragment_send_pic, container, false);
        buttonChoose = (Button)v.findViewById(R.id.buttonChoose);         //initializing the views
        buttonUpload = (Button) v.findViewById(R.id.buttonUpload);
        editTextName = (EditText) v.findViewById(R.id.editText);
        imageView  = (ImageView) v.findViewById(R.id.imageView);

        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);


        return v;
    }



    private void showFileChooser()  {                                   //this method is used to choose image from gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);    //starts an activity which returns an intent object
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {                     //receives the result as intent object
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                //Setting the Bitmap to ImageView

                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
    public void onClick(View v) {
        if(v == buttonChoose){
            buttonChoose.setEnabled(false);
            showFileChooser();
            buttonChoose.setEnabled(true);

        }
        else if(v==buttonUpload){
            buttonUpload.setEnabled(false);
            uploadImage();
          //  buttonUpload.setEnabled(true);
        }
    }



    public String getStringImage(Bitmap bmp)          //converts the bitmap image to base64 string
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }



    private void uploadImage()                        //to upload image to the server
    {
        //Showing the progress dialog
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Uploading :) ");
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
                        if (s.trim().equals("Successfully Uploaded")) {
                            imageView.setImageDrawable(null);
                            editTextName.setText("");
                            buttonUpload.setEnabled(true);
                           // finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        progress.dismiss();

                        //Showing toast
                        Toast.makeText(getActivity(), volleyError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        buttonUpload.setEnabled(true);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Getting Image Name
                String name = editTextName.getText().toString().trim();


                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();
                SharedPreferences settings = getActivity().getSharedPreferences(SigninActivity.PREFS_NAME, 0);
                String user=settings.getString("user","no name");

                //Adding parameters
                params.put(KEY_IMAGE, image);
                params.put(KEY_NAME, name);
                params.put(SigninActivity.KEY_USERNAME, user);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }




}
