package com.hjsoft.guestbooktaxi.activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.hjsoft.guestbooktaxi.R;
import com.hjsoft.guestbooktaxi.SessionManager;
import com.hjsoft.guestbooktaxi.adapter.DBAdapter;
import com.hjsoft.guestbooktaxi.fragments.MyBottomSheetDialogFragment;
import com.hjsoft.guestbooktaxi.model.BookCabPojo;
import com.hjsoft.guestbooktaxi.model.CatPojo;
import com.hjsoft.guestbooktaxi.model.CityCenterPojo;
import com.hjsoft.guestbooktaxi.model.Distance;
import com.hjsoft.guestbooktaxi.model.Duration;
import com.hjsoft.guestbooktaxi.model.DurationPojo;
import com.hjsoft.guestbooktaxi.model.Element;
import com.hjsoft.guestbooktaxi.model.Row;
import com.hjsoft.guestbooktaxi.model.ServiceLocationPojo;
import com.hjsoft.guestbooktaxi.webservices.API;
import com.hjsoft.guestbooktaxi.webservices.RestClient;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 23/11/16.
 */
public class MainActivity extends AppCompatActivity {

    Button btLogin,btValidateOTP;
    TextView tvSignUp;
    API REST_CLIENT;
    EditText etEmail;
    String stEmail;
    //BottomSheetDialogFragment myBottomSheet;
    SessionManager session;
    String companyId="CMP00001";
    int count=0;
    DBAdapter dbAdapter;
    HashMap<String, String> user;
    String stMobileNumber;
    int j=0;
    TextView tvLoc1,tvLoc2,tvLoc3,tvLoc4,tvLoc5;
    //String city="";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";
    //String version="4.5";
    String city="Visakhapatnam";
    String version="2";//20


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        REST_CLIENT=RestClient.get();
        btLogin=(Button)findViewById(R.id.al_bt_login);
        btValidateOTP=(Button)findViewById(R.id.al_bt_validate_otp);
        tvSignUp=(TextView)findViewById(R.id.al_tv_signup);
        etEmail=(EditText)findViewById(R.id.al_et_email);
        // etPwd=(EditText)findViewById(R.id.al_et_pwd);
        session=new SessionManager(getApplicationContext());
        user=session.getUserDetails();

        stMobileNumber=user.get(SessionManager.KEY_MOBILE);

        pref = getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        //myBottomSheet = MyBottomSheetDialogFragment.newInstance("Modal Bottom Sheet");
        btValidateOTP.setVisibility(View.GONE);
        dbAdapter=new DBAdapter(getApplicationContext());
        dbAdapter=dbAdapter.open();

        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction())) {
                Log.w("data", "Main Activity is not the root.  Finishing Main Activity instead of launching.");
                finish();
                return;
            }
        }


        if(session.checkLogin())
        {
            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            JsonObject v = new JsonObject();
            v.addProperty("mobile",stMobileNumber);
            v.addProperty("companyid",companyId);
            v.addProperty("login","no");
            v.addProperty("version",version);

            Call<BookCabPojo> call1=REST_CLIENT.loginForOTP(v);
            call1.enqueue(new Callback<BookCabPojo>() {
                @Override
                public void onResponse(Call<BookCabPojo> call, Response<BookCabPojo> response) {

                    BookCabPojo data;
                    if (response.isSuccessful()) {

                        data=response.body();

                        String stData[]=data.getMessage().split("-");

                        if(stData.length==0)
                        {

                        }
                        else {

                            String walletAmount=stData[1];
                            String timeUpdated=java.text.DateFormat.getTimeInstance().format(new Date());

                            if(dbAdapter.isWalletPresent()>0)
                            {
                                dbAdapter.updateWalletAmount(walletAmount,timeUpdated);

                            }
                            else {

                                dbAdapter.insertWalletAmount(walletAmount,timeUpdated);
                            }
                        }

                        progressDialog.dismiss();
                        getCityCenterCoordinates();

                        /*Intent i = new Intent(MainActivity.this, HomeActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();*/
                    }

                    else {

                        progressDialog.dismiss();

                        if(response.message().equals("Version mismatched"))
                        {
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);

                            LayoutInflater inflater = getLayoutInflater();
                            final View dialogView = inflater.inflate(R.layout.alert_update, null);

                            dialogBuilder.setView(dialogView);

                            final AlertDialog alertDialog = dialogBuilder.create();
                            alertDialog.show();


                            TextView tvOk=(TextView)dialogView.findViewById(R.id.au_bt_ok);
                            tvOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    alertDialog.dismiss();
                                    finish();

                                    openAppRating(getApplicationContext());
                                }
                            });

                        }
                    }
                }

                @Override
                public void onFailure(Call<BookCabPojo> call, Throwable t) {

                    progressDialog.dismiss();

                    Toast.makeText(MainActivity.this,"No Internet Connection!",Toast.LENGTH_SHORT).show();
                    finish();

                }
            });
        }

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btValidateOTP.setVisibility(View.GONE);

                stEmail = etEmail.getText().toString().trim();
                //stPwd = etPwd.getText().toString().trim();

                if (stEmail.equals("")||stEmail.length()!=10) {

                    Toast.makeText(MainActivity.this, "Enter Valid Mobile Number !", Toast.LENGTH_SHORT).show();
                    etEmail.setText("");
                }
                else {

                    final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Please Wait...");
                    progressDialog.show();

                    JsonObject v = new JsonObject();
                    v.addProperty("mobile",stEmail);
                    v.addProperty("companyid",companyId);
                    v.addProperty("login","yes");
                    v.addProperty("version",version);

                    Call<BookCabPojo> call=REST_CLIENT.loginForOTP(v);
                    call.enqueue(new Callback<BookCabPojo>() {
                        @Override
                        public void onResponse(Call<BookCabPojo> call, Response<BookCabPojo> response) {


                            progressDialog.dismiss();

                            BookCabPojo data;

                            if (response.isSuccessful()) {

                                data=response.body();

                                String stData[]=data.getMessage().split("-");

                                if(stData.length==0)
                                {

                                }
                                else {

                                    String walletAmount=stData[1];
                                    String timeUpdated=java.text.DateFormat.getTimeInstance().format(new Date());

                                    if(dbAdapter.isWalletPresent()>0)
                                    {
                                        dbAdapter.updateWalletAmount(walletAmount,timeUpdated);
                                    }
                                    else {

                                        dbAdapter.insertWalletAmount(walletAmount,timeUpdated);
                                    }
                                }

                                Intent i=new Intent(MainActivity.this,LoginOTPActivity.class);
                                i.putExtra("stMobile",stEmail);
                                startActivity(i);
                                finish();
                            }
                            else
                            {
                                if(response.message().equals("Version mismatched"))
                                {

                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);

                                    LayoutInflater inflater = getLayoutInflater();
                                    final View dialogView = inflater.inflate(R.layout.alert_update, null);

                                    dialogBuilder.setView(dialogView);

                                    final AlertDialog alertDialog = dialogBuilder.create();
                                    alertDialog.show();

                                    TextView tvOk=(TextView)dialogView.findViewById(R.id.au_bt_ok);
                                    tvOk.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            alertDialog.dismiss();
                                            finish();

                                            openAppRating(getApplicationContext());
                                        }
                                    });
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Mobile Number doesn't exist!\n Please Signup!", Toast.LENGTH_SHORT).show();

                                    Intent i=new Intent(MainActivity.this,RegisterActivity.class);
                                    startActivity(i);
                                    finish();

                                }


                            }
                        }

                        @Override
                        public void onFailure(Call<BookCabPojo> call, Throwable t) {

                            progressDialog.dismiss();

                            Toast.makeText(MainActivity.this,"Please check Internet connection!",Toast.LENGTH_SHORT).show();

                            /*if(myBottomSheet!=null) {

                                if (myBottomSheet.isAdded()) {
                                    //return;

                                } else {

                                    myBottomSheet.show(getSupportFragmentManager(), myBottomSheet.getTag());
                                }
                            }*/
                        }
                    });
                }
            }
        });

        btValidateOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showOTPAlertDialog();
            }
        });
    }


    public void showOTPAlertDialog()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_otp_login, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        final EditText etOTP = (EditText) dialogView.findViewById(R.id.aol_et_otp);
        Button ok = (Button) dialogView.findViewById(R.id.aol_bt_ok);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String stOTP = etOTP.getText().toString().trim();

                JsonObject v1 = new JsonObject();
                v1.addProperty("mobile", stEmail);
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
                            session.createLoginSession(stEmail, stProfileId);

                            alertDialog.dismiss();

                            getCityCenterCoordinates();

                        } else {

                            alertDialog.dismiss();
                            count++;

                            if(count==3)
                            {
                                btValidateOTP.setVisibility(View.GONE);
                            }
                            else {
                                Toast.makeText(MainActivity.this, "OTP Authentication Failed!", Toast.LENGTH_SHORT).show();
                                btValidateOTP.setVisibility(View.VISIBLE);
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<BookCabPojo> call, Throwable t) {

                        Toast.makeText(MainActivity.this,"No Internet Connection!",Toast.LENGTH_SHORT).show();

                    }
                });


            }
        });
    }

    public void selectServiceLocations()
    {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_locations, null);

        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        tvLoc1=(TextView)dialogView.findViewById(R.id.ale_tv_loc1);
        tvLoc2=(TextView)dialogView.findViewById(R.id.ale_tv_loc2);
        tvLoc3=(TextView)dialogView.findViewById(R.id.ale_tv_loc3);
        tvLoc4=(TextView)dialogView.findViewById(R.id.ale_tv_loc4);
        tvLoc5=(TextView)dialogView.findViewById(R.id.ale_tv_loc5);

        tvLoc1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvLoc1.setTextColor(Color.parseColor("#000000"));


                city=tvLoc1.getText().toString().trim();
                editor.putString("city",city);
                editor.commit();
                alertDialog.dismiss();
                Intent i = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });

        tvLoc2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvLoc2.setTextColor(Color.parseColor("#000000"));

                city=tvLoc2.getText().toString().trim();
                editor.putString("city",city);
                editor.commit();

                alertDialog.dismiss();

                Intent i = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });

        tvLoc3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvLoc3.setTextColor(Color.parseColor("#000000"));


                city=tvLoc3.getText().toString().trim();
                editor.putString("city",city);
                editor.commit();

                alertDialog.dismiss();

                Intent i = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });

        tvLoc4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvLoc4.setTextColor(Color.parseColor("#000000"));


                city=tvLoc4.getText().toString().trim();
                editor.putString("city",city);
                editor.commit();

                alertDialog.dismiss();


                Intent i = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });

        tvLoc5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvLoc5.setTextColor(Color.parseColor("#000000"));


                city=tvLoc5.getText().toString().trim();
                editor.putString("city",city);
                editor.commit();

                alertDialog.dismiss();

                Intent i = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });



        Call<List<ServiceLocationPojo>> call=REST_CLIENT.getServiceLocations("CMP00001");
        call.enqueue(new Callback<List<ServiceLocationPojo>>() {
            @Override
            public void onResponse(Call<List<ServiceLocationPojo>> call, Response<List<ServiceLocationPojo>> response) {

                ServiceLocationPojo data;
                List<ServiceLocationPojo> dataList;

                if(response.isSuccessful())
                {
                    dataList=response.body();

                    for(int i=0;i<dataList.size();i++)
                    {
                        data=dataList.get(i);
                        switch (j)
                        {
                            case 0:tvLoc1.setText(data.getLocation());
                                tvLoc1.setVisibility(View.VISIBLE);
                                j++;
                                break;
                            case 1:tvLoc2.setText(data.getLocation());
                                tvLoc2.setVisibility(View.VISIBLE);
                                j++;
                                break;
                            case 2:tvLoc3.setText(data.getLocation());
                                tvLoc3.setVisibility(View.VISIBLE);
                                j++;
                                break;
                            case 3:tvLoc4.setText(data.getLocation());
                                tvLoc4.setVisibility(View.VISIBLE);
                                j++;
                                break;
                            case 4:tvLoc5.setText(data.getLocation());
                                tvLoc5.setVisibility(View.VISIBLE);
                                j++;
                                break;
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<List<ServiceLocationPojo>> call, Throwable t) {

                Toast.makeText(MainActivity.this,"No Internet Connection!",Toast.LENGTH_LONG).show();

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
                    Intent i=new Intent(MainActivity.this,HomeActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }

            }

            @Override
            public void onFailure(Call<List<CityCenterPojo>> call, Throwable t) {

                Toast.makeText(MainActivity.this,"No Internet Connection!",Toast.LENGTH_SHORT).show();
                finish();

            }
        });
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        finish();
    }

    public static void openAppRating(Context context) {
        // you can also use BuildConfig.APPLICATION_ID
        String appId = context.getPackageName();
        Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + appId));
        boolean marketFound = false;

        // find all applications able to handle our rateIntent
        final List<ResolveInfo> otherApps = context.getPackageManager()
                .queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp: otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName
                    .equals("com.android.vending")) {

                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                );
                // make sure it does NOT open in the stack of your activity
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // task reparenting if needed
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                // if the Google Play was already open in a search result
                //  this make sure it still go to the app page you requested
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // this make sure only the Google Play app is allowed to
                // intercept the intent
                rateIntent.setComponent(componentName);
                context.startActivity(rateIntent);
                marketFound = true;
                break;

            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id="+appId));
            context.startActivity(webIntent);
        }
    }
}