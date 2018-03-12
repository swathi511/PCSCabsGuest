package com.hjsoft.guestbooktaxi.activity;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hjsoft.guestbooktaxi.R;
import com.hjsoft.guestbooktaxi.SessionManager;
import com.hjsoft.guestbooktaxi.adapter.DBAdapter;
import com.hjsoft.guestbooktaxi.model.BookCabPojo;
import com.hjsoft.guestbooktaxi.model.CancelData;
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
    FloatingActionButton fabNext;
    EditText etOtp1,etOtp2,etOtp3,etOtp4;
    DBAdapter dbAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        tvBack=(TextView)findViewById(R.id.alo_tv_back);
        etOTP=(EditText)findViewById(R.id.alo_et_otp);
        btOk=(Button)findViewById(R.id.alo_bt_ok);
        fabNext=(FloatingActionButton)findViewById(R.id.alo_fab_next);
        etOtp1=(EditText)findViewById(R.id.alo_et_otp1);
        etOtp2=(EditText)findViewById(R.id.alo_et_otp2);
        etOtp3=(EditText)findViewById(R.id.alo_et_otp3);
        etOtp4=(EditText)findViewById(R.id.alo_et_otp4);

        etOtp1.addTextChangedListener(new GenericTextWatcher(etOtp1));
        etOtp2.addTextChangedListener(new GenericTextWatcher(etOtp2));
        etOtp3.addTextChangedListener(new GenericTextWatcher(etOtp3));
        etOtp4.addTextChangedListener(new GenericTextWatcher(etOtp4));


        REST_CLIENT= RestClient.get();
        session=new SessionManager(getApplicationContext());
        dbAdapter=new DBAdapter(getApplicationContext());
        dbAdapter=dbAdapter.open();

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

        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(LoginOTPActivity.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Please wait...");
                progressDialog.show();
                //stOTP=etOTP.getText().toString().trim();
                stOTP=etOtp1.getText().toString().trim()+etOtp2.getText().toString().trim()+
                        etOtp3.getText().toString().trim()+etOtp4.getText().toString().trim();

                //System.out.println("##########"+stOTP);

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
                            String stProfileId = data.getMessage().split("-")[0];

                            System.out.println(stProfileId);
                            session.createLoginSession(stMobile, stProfileId);
                            editor.putString("name",data.getMessage().split("-")[1]);
                            editor.putString("mobile",stMobile);
                            editor.commit();

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
                            String stProfileId = data.getMessage().split("-")[0];

                            //System.out.println(stProfileId);
                            session.createLoginSession(stMobile, stProfileId);
                            editor.putString("name",data.getMessage().split("-")[1]);
                            editor.putString("mobile",stMobile);
                            editor.commit();

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

//                    ArrayList<String> a=dbAdapter.getOsCities();
//                    System.out.println("ooooooooooo"+a.size());
                    //dbAdapter.deleteOsCities();

                    //System.out.println("ooooooooooo"+a.size());
                    //alertDialog.dismiss();
                    String s[]=cityList.getLocalities().split(",");

                    for(int i=0;i<s.length;i++)
                    {
                        dbAdapter.insertOsCity(s[i]);
                        dbAdapter.insertOsCity(s[i].toUpperCase());
                        dbAdapter.insertOsCity(s[i].toLowerCase());
                    }
                    getCancelData();

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

    public class GenericTextWatcher implements TextWatcher
    {
        private View view;
        private GenericTextWatcher(View view)
        {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // TODO Auto-generated method stub
            String text = editable.toString();
            switch(view.getId())
            {

                case R.id.alo_et_otp1:
                    if(text.length()==1)
                        etOtp2.requestFocus();
                    break;
                case R.id.alo_et_otp2:
                    if(text.length()==1)
                        etOtp3.requestFocus();
                    break;
                case R.id.alo_et_otp3:
                    if(text.length()==1)
                        etOtp4.requestFocus();
                    break;
                case R.id.alo_et_otp4:
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }
    }

    public void getCancelData()
    {
        //System.out.println("getting Canel data");

        Call<ArrayList<CancelData>> call=REST_CLIENT.getCancelList(companyId,"guest");
        call.enqueue(new Callback<ArrayList<CancelData>>() {
            @Override
            public void onResponse(Call<ArrayList<CancelData>> call, Response<ArrayList<CancelData>> response) {

                ArrayList<CancelData> cdList=new ArrayList<CancelData>();
                CancelData cd;
                if(response.isSuccessful())
                {
                    cdList=response.body();
                    editor.putBoolean("cancelOptions",false);
                    editor.commit();

                    dbAdapter.deleteCancelData();

                    for(int i=0;i<cdList.size();i++)
                    {
                        cd=cdList.get(i);

                        dbAdapter.insertCancelOptions(cd.getReason(),cd.getId());

                        //System.out.println("****** "+cd.getReason());
                    }
                    Toast.makeText(LoginOTPActivity.this,"Please wait!\nGetting location..",Toast.LENGTH_SHORT).show();
                    //alertDialog.dismiss();
                    Intent i=new Intent(LoginOTPActivity.this,HomeActivity.class);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<CancelData>> call, Throwable t) {

            }
        });
    }
}
