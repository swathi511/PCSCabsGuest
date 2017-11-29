package com.hjsoft.guestbooktaxi.activity;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hjsoft.guestbooktaxi.R;
import com.hjsoft.guestbooktaxi.SessionManager;
import com.hjsoft.guestbooktaxi.model.BookCabPojo;
import com.hjsoft.guestbooktaxi.model.CityCenterPojo;
import com.hjsoft.guestbooktaxi.webservices.API;
import com.hjsoft.guestbooktaxi.webservices.RestClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 6/7/17.
 */
public class LoginOTPActivity extends AppCompatActivity {

    TextView tvBack;
    EditText etOTP;
    Button btOk;
    String stMobile,companyId="CMP00001",stOTP;
    Bundle b;
    API REST_CLIENT;
    SessionManager session;
    String city="Visakhapatnam";
    AlertDialog alertDialog;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";
    int count=0;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        tvBack=(TextView)findViewById(R.id.alo_tv_back);
        etOTP=(EditText)findViewById(R.id.alo_et_otp);
        btOk=(Button)findViewById(R.id.alo_bt_ok);

        REST_CLIENT= RestClient.get();
        session=new SessionManager(getApplicationContext());

        pref = getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        b=getIntent().getExtras();
        stMobile=b.getString("stMobile");

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(LoginOTPActivity.this,MainActivity.class);
                startActivity(i);
                finish();

            }
        });

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                progressDialog = new ProgressDialog(LoginOTPActivity.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Please wait...");
                progressDialog.show();
                stOTP=etOTP.getText().toString().trim();

                JsonObject v1 = new JsonObject();
                v1.addProperty("mobile", stMobile);
                v1.addProperty("otp", stOTP);
                v1.addProperty("companyid",companyId);

                Call<BookCabPojo> call = REST_CLIENT.loginWithOTP(v1);
                call.enqueue(new Callback<BookCabPojo>() {
                    @Override
                    public void onResponse(Call<BookCabPojo> call, Response<BookCabPojo> response) {

                        BookCabPojo data;

                        if (response.isSuccessful()) {
                            data = response.body();
                            String stProfileId = data.getMessage();

                            //System.out.println(stProfileId);
                            session.createLoginSession(stMobile, stProfileId);

                            getCityCenterCoordinates();

                        }
                        else {

                            progressDialog.dismiss();
                            count++;

                            if(count==3)
                            {
                                Toast.makeText(LoginOTPActivity.this, "OTP Authentication Failed!", Toast.LENGTH_SHORT).show();

                                finish();
                            }
                            else {
                                Toast.makeText(LoginOTPActivity.this, "OTP Authentication Failed!", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<BookCabPojo> call, Throwable t) {

                        progressDialog.dismiss();

                        Toast.makeText(LoginOTPActivity.this,"No Internet Connection!",Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });



    }

    public void getCityCenterCoordinates()
    {

        Call<List<CityCenterPojo>> call=REST_CLIENT.getCoordinates(city,companyId);
        call.enqueue(new Callback<List<CityCenterPojo>>() {
            @Override
            public void onResponse(Call<List<CityCenterPojo>> call, Response<List<CityCenterPojo>> response) {

                ArrayList<CityCenterPojo> cityDataList;
                CityCenterPojo cityList;
                progressDialog.dismiss();

                if(response.isSuccessful())
                {
                    cityDataList=(ArrayList<CityCenterPojo>) response.body();

                    cityList=cityDataList.get(0);

                    System.out.println(cityList.getLatitude()+":::"+cityList.getLongitude());

                    editor.putString("cityCenterLat",cityList.getLatitude());
                    editor.putString("cityCenterLong",cityList.getLongitude());
                    editor.putString("city",city);
                    editor.putString("radius",cityList.getCutOfRadius());
                    editor.commit();
                    Toast.makeText(LoginOTPActivity.this,"Valid OTP!\nGetting location..",Toast.LENGTH_SHORT).show();
                    //alertDialog.dismiss();
                    Intent i=new Intent(LoginOTPActivity.this,HomeActivity.class);
                    startActivity(i);
                    finish();
                }
                else {

                    Toast.makeText(LoginOTPActivity.this,response.message(),Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<List<CityCenterPojo>> call, Throwable t) {

                progressDialog.dismiss();

                Toast.makeText(LoginOTPActivity.this,"No Internet Connection!",Toast.LENGTH_SHORT).show();

            }
        });
    }
}
