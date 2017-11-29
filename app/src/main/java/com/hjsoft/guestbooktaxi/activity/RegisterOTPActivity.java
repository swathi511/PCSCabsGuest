package com.hjsoft.guestbooktaxi.activity;

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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 6/7/17.
 */
public class RegisterOTPActivity extends AppCompatActivity {

    TextView tvBack;
    EditText etOTP;
    Button btOk;
    String stName,stMobile,stCity,stEmail,companyId="CMP00001",stOTP;
    Bundle b;
    API REST_CLIENT;
    SessionManager session;
    String city="Visakhapatnam";
    AlertDialog alertDialog;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_otp);

        tvBack=(TextView)findViewById(R.id.aro_tv_back);
        etOTP=(EditText)findViewById(R.id.aro_et_otp);
        btOk=(Button)findViewById(R.id.aro_bt_ok);

        REST_CLIENT= RestClient.get();
        session=new SessionManager(getApplicationContext());

        pref = getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        b=getIntent().getExtras();
        stName=b.getString("stName");
        stMobile=b.getString("stMobile");
        stCity=b.getString("stCity");
        stEmail=b.getString("stEmail");


        System.out.println(stName+stMobile+stCity+stEmail);

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //finish();
                Intent i=new Intent(RegisterOTPActivity.this,RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stOTP=etOTP.getText().toString().trim();

                JsonObject v1=new JsonObject();
                v1.addProperty("name",stName);
                v1.addProperty("mobile",stMobile);
                v1.addProperty("city",stCity);
                v1.addProperty("email",stEmail);
                v1.addProperty("otp",stOTP);
                v1.addProperty("companyid",companyId);


                Call<BookCabPojo> call=REST_CLIENT.registerWithOTP(v1);
                call.enqueue(new Callback<BookCabPojo>() {
                    @Override
                    public void onResponse(Call<BookCabPojo> call, Response<BookCabPojo> response) {

                        BookCabPojo data;

                        if(response.isSuccessful())
                        {
                            data = response.body();
                            String stProfileId = data.getMessage();

                           session.createLoginSession(stMobile, stProfileId);

                            getCityCenterCoordinates();

                        }
                        else
                        {
                            Toast.makeText(RegisterOTPActivity.this,"OTP Authentication Failed !",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BookCabPojo> call, Throwable t) {

                        Toast.makeText(RegisterOTPActivity.this,"No Internet! Try again",Toast.LENGTH_LONG).show();

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

                List<CityCenterPojo> cityDataList;
                CityCenterPojo cityList;

                if(response.isSuccessful())
                {
                    cityDataList=response.body();

                    cityList=cityDataList.get(0);

                    System.out.println(cityList.getLatitude()+":::"+cityList.getLongitude());

                    editor.putString("cityCenterLat",cityList.getLatitude());
                    editor.putString("cityCenterLong",cityList.getLongitude());
                    editor.putString("city",city);
                    editor.putString("radius",cityList.getCutOfRadius());
                    editor.commit();

                    Toast.makeText(RegisterOTPActivity.this,"Registered successfully!\nGetting location...",Toast.LENGTH_SHORT).show();
                    //alertDialog.dismiss();
                    Intent i=new Intent(RegisterOTPActivity.this,HomeActivity.class);
                    //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }

            }

            @Override
            public void onFailure(Call<List<CityCenterPojo>> call, Throwable t) {

                Toast.makeText(RegisterOTPActivity.this,"No Internet Connection ! Try again",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
