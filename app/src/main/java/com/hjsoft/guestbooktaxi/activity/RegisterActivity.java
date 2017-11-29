package com.hjsoft.guestbooktaxi.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
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
 * Created by hjsoft on 12/1/17.
 */
public class RegisterActivity extends AppCompatActivity {

    EditText etEmail,etName,etCity,etMobile;
    String stEmail,stName,stCity,stMobile;
    TextView btRegister;
    API REST_CLIENT;
    String companyId="CMP00001";
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
        setContentView(R.layout.activity_register);

        REST_CLIENT= RestClient.get();

        pref = getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        session=new SessionManager(getApplicationContext());

        etEmail=(EditText)findViewById(R.id.ar_et_email);
        etName=(EditText)findViewById(R.id.ar_et_name);
        //etPwd=(EditText)findViewById(R.id.ar_et_pwd);
        // etCpwd=(EditText)findViewById(R.id.ar_et_cpwd);
        etCity=(EditText)findViewById(R.id.ar_et_city);
        etMobile=(EditText)findViewById(R.id.ar_et_mobile);
        btRegister=(TextView) findViewById(R.id.ar_bt_register);

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stEmail=etEmail.getText().toString().trim();
                stName=etName.getText().toString().trim();
                //stPwd=etPwd.getText().toString().trim();
                // stCpwd=etCpwd.getText().toString().trim();
                stCity=etCity.getText().toString().trim();
                stMobile=etMobile.getText().toString().trim();

                if(stEmail.equals("")||stName.equals("")||stCity.equals("")||stMobile.equals(""))
                {
                    Toast.makeText(RegisterActivity.this,"Fill all the Fields!",Toast.LENGTH_SHORT).show();
                }
                else if(stMobile.length()!=10)
                {
                    Toast.makeText(RegisterActivity.this,"Enter valid Mobile Number",Toast.LENGTH_SHORT).show();
                    etMobile.setText("");
                }
                else if(!(android.util.Patterns.EMAIL_ADDRESS.matcher(stEmail).matches()))
                {
                    Toast.makeText(RegisterActivity.this,"Enter valid Email Address",Toast.LENGTH_SHORT).show();
                    etEmail.setText("");
                }
                else
                {
                    final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Please Wait...");
                    progressDialog.show();
                    JsonObject v=new JsonObject();
                    v.addProperty("name",stName);
                    v.addProperty("mobile",stMobile);
                    v.addProperty("city",stCity);
                    v.addProperty("email",stEmail);
                    v.addProperty("companyId",companyId);
                    v.addProperty("user","");

                    //v.addProperty("password",stPwd);
                    //v.addProperty("confirmpassword",stCpwd);
                    //Call<BookCabPojo> call=REST_CLIENT.registerInto(v);
                    Call<BookCabPojo> call=REST_CLIENT.registerForOTP(v);
                    call.enqueue(new Callback<BookCabPojo>() {
                        @Override
                        public void onResponse(Call<BookCabPojo> call, Response<BookCabPojo> response) {

                            progressDialog.dismiss();

                            if(response.isSuccessful())
                            {
                               /* AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RegisterActivity.this);

                                LayoutInflater inflater =getLayoutInflater();
                                final View dialogView = inflater.inflate(R.layout.alert_otp_register, null);
                                dialogBuilder.setView(dialogView);

                                final AlertDialog alertDialog = dialogBuilder.create();
                                alertDialog.show();

                                final EditText etOTP=(EditText)dialogView.findViewById(R.id.aao_et_otp);
                                Button ok=(Button) dialogView.findViewById(R.id.aao_bt_ok);

                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        String stOTP=etOTP.getText().toString().trim();

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

                                                alertDialog.dismiss();

                                                if(response.isSuccessful())
                                                {
//                                                    Toast.makeText(RegisterActivity.this,"Registered successfully!",Toast.LENGTH_SHORT).show();
//                                                    Intent i=new Intent(RegisterActivity.this,MainActivity.class);
//                                                    startActivity(i);
//                                                    finish();

                                                    data = response.body();
                                                    String stProfileId = data.getMessage();

                                                    System.out.println(stProfileId);

                                                    // Toast.makeText(MainActivity.this,stProfileId,Toast.LENGTH_SHORT).show();

                                                    session.createLoginSession(stMobile, stProfileId);

                                                    getCityCenterCoordinates();

                                                }
                                                else
                                                {
                                                    alertDialog.dismiss();
                                                    Toast.makeText(RegisterActivity.this,"OTP Authentication Failed !",Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<BookCabPojo> call, Throwable t) {

                                                alertDialog.dismiss();

                                                Toast.makeText(RegisterActivity.this,"No Internet Connection!",Toast.LENGTH_LONG).show();

                                            }
                                        });
                                    }
                                });*/

                                Intent i=new Intent(RegisterActivity.this,RegisterOTPActivity.class);
                                i.putExtra("stName",stName);
                                i.putExtra("stMobile",stMobile);
                                i.putExtra("stCity",stCity);
                                i.putExtra("stEmail",stEmail);
                                startActivity(i);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(RegisterActivity.this,response.message(),Toast.LENGTH_LONG).show();
                                Intent i=new Intent(RegisterActivity.this,MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<BookCabPojo> call, Throwable t) {

                            progressDialog.dismiss();

                            Toast.makeText(RegisterActivity.this,"No Internet Connection!",Toast.LENGTH_LONG).show();
                        }
                    });
                }
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

                    editor.putString("cityCenterLat",cityList.getLatitude());
                    editor.putString("cityCenterLong",cityList.getLongitude());
                    editor.putString("city",city);
                    editor.putString("radius",cityList.getCutOfRadius());
                    editor.commit();
                    //alertDialog.dismiss();
                    Intent i=new Intent(RegisterActivity.this,HomeActivity.class);
                    startActivity(i);
                    finish();
                }

            }

            @Override
            public void onFailure(Call<List<CityCenterPojo>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

