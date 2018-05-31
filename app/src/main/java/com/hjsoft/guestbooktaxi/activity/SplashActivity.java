package com.hjsoft.guestbooktaxi.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
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
import com.inrista.loggliest.Loggly;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 3/2/18.
 */
public class SplashActivity extends AppCompatActivity {

    SessionManager session;
    String companyId="CMP00001";
    HashMap<String, String> user;
    String stMobileNumber;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";
    String version="4.5";//4.5//5.9 //change loggly token while testing
    String city="Visakhapatnam";
    API REST_CLIENT;
    DBAdapter dbAdapter;
    ProgressBar pb;
    boolean debugLogs=true;
    final String TOKEN = "b505c85d-71ae-4ad6-803b-78b2f8893cb4"; //(swathipriya)


    private Thread.UncaughtExceptionHandler androidDefaultUEH;

    private Thread.UncaughtExceptionHandler handler1 = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {

            Log.e("TestApplication", "Uncaught exception is: ", ex);
            // log it & phone home.

            String trace = ex.toString() + "\n";

            for (StackTraceElement e1 : ex.getStackTrace()) {
                trace += "\t at " + e1.toString() + "\n";
            }

            Loggly.i("Uncaught Exception:","is"+trace);
            Loggly.forceUpload();

            androidDefaultUEH.uncaughtException(thread, ex);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        pb=(ProgressBar)findViewById(R.id.as_pb);
        //pb.setVisibility(View.GONE);

        androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler1);

        session=new SessionManager(getApplicationContext());
        user=session.getUserDetails();
        dbAdapter=new DBAdapter(getApplicationContext());
        dbAdapter=dbAdapter.open();
        REST_CLIENT= RestClient.get();

        pref = getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        stMobileNumber=user.get(SessionManager.KEY_MOBILE);

        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction())) {
                Log.w("data", "Main Activity is not the root.  Finishing Main Activity instead of launching.");
                finish();
                return;
            }
        }

        Loggly.with(SplashActivity.this,TOKEN)
                .appendDefaultInfo(true)
                .uploadIntervalLogCount(500)
                .uploadIntervalSecs(300) //5 min
                .maxSizeOnDisk(500000)
                //.appendStickyInfo("language", currentLanguage)
                .init();

        //System.out.println("checking session"+session.checkLogin());

        if(session.checkLogin())
        {
            //pb.setVisibility(View.VISIBLE);

            //System.out.println("session checking"+stMobileNumber+":"+companyId+":"+version);


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

                        //System.out.println("check Login success");

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

                        //System.out.println("@@@@@@@@@@@@@@@@@@@@@@"+stData[2]);

                        editor.putString("name",stData[2]);
                        editor.putString("mobile",stMobileNumber);
                        editor.putBoolean("debugLogs",true);
                        editor.commit();

                        getCityCenterCoordinates();

                        /*Intent i = new Intent(MainActivity.this, HomeActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();*/
                    }

                    else {

                        System.out.println("in esle session "+response.message());

                        if(response.message().equals("Version mismatched"))
                        {
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SplashActivity.this);

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

                    Toast.makeText(SplashActivity.this,"No Internet Connection!",Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                    //finish();

                }
            });
        }
        else {

            Intent i=new Intent(SplashActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }
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
                    //System.out.println("city center success");
                    cityDataList=response.body();

                    cityList=cityDataList.get(0);

                    editor.putString("cityCenterLat",cityList.getLatitude());
                    editor.putString("cityCenterLong",cityList.getLongitude());
                    editor.putString("city",city);
                    editor.putString("radius",cityList.getCutOfRadius());
                    editor.commit();
                    pb.setVisibility(View.GONE);

//                    ArrayList<String> a=dbAdapter.getOsCities();
//                    System.out.println("ooooooooooo"+a.size());
                    dbAdapter.deleteOsCities();

                    //a=dbAdapter.getOsCities();

                    //System.out.println("ooooooooooo"+a.size());
                    //alertDialog.dismiss();
                    String s[]=cityList.getLocalities().split(",");

                    for(int i=0;i<s.length;i++)
                    {
                        dbAdapter.insertOsCity(s[i]);
                        dbAdapter.insertOsCity(s[i].toUpperCase());
                        dbAdapter.insertOsCity(s[i].toLowerCase());

                        //System.out.println(s[i]+"***"+s[i].toUpperCase()+"***"+s[i].toLowerCase());
                    }
                    getCancelData();
                }
            }

            @Override
            public void onFailure(Call<List<CityCenterPojo>> call, Throwable t) {

                pb.setVisibility(View.GONE);

                Toast.makeText(SplashActivity.this,"Check Internet Connection!",Toast.LENGTH_LONG).show();
                //finish();

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

                        //System.out.println("****** "+cd.getReason()+cd.getId());
                    }

                    Intent i=new Intent(SplashActivity.this,HomeActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
