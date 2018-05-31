package com.hjsoft.guestbooktaxi.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hjsoft.guestbooktaxi.R;
import com.hjsoft.guestbooktaxi.SessionManager;
import com.hjsoft.guestbooktaxi.activity.OutStationActivity;
import com.hjsoft.guestbooktaxi.activity.PaymentActivity;
import com.hjsoft.guestbooktaxi.adapter.DBAdapter;
import com.hjsoft.guestbooktaxi.model.BookCabPojo;
import com.hjsoft.guestbooktaxi.model.Distance;
import com.hjsoft.guestbooktaxi.model.Duration;
import com.hjsoft.guestbooktaxi.model.DurationPojo;
import com.hjsoft.guestbooktaxi.model.Element;
import com.hjsoft.guestbooktaxi.model.OutStationPojo;
import com.hjsoft.guestbooktaxi.model.Row;
import com.hjsoft.guestbooktaxi.webservices.API;
import com.hjsoft.guestbooktaxi.webservices.RestClient;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 22/2/17.
 */
public class OutStationFragment extends Fragment {

    View v;
    TextView tvDateTimeText,tvPkgText,tvDateTime,tvPkg,tvCab;
    DatePicker dp;
    TimePicker tp;
    int hr,min,day,mnth,yr;
    Button ok,cancel;
    String stDate,stTime,stPkg="";
    Button btBook;
    TextView btFareEstimate;
    String city;
    String pickupLat,pickupLong,dropLat,dropLong,pickupLoc,dropLoc;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";
    API REST_CLIENT;
    String stKms;
    int stPkgNo;
    int amnt=0;
    LinearLayout lLayout,lBookingLayout;
    ImageView ivDateTime,ivPkg,ivCab;
    String stCab="";
    String guestProfileId;
    HashMap<String, String> user;
    SessionManager session;
    String companyId="CMP00001";
    TextView tvBookingid;
    TextView tvPickup,tvDrop;
    String stPaymentType="";
    TextView tvCash,tvWallet;
    DBAdapter dbAdapter;
    String stWalletAmount;
    TextView tvAddMoney,tvCoupon,tvSurgeMsg;
    String stDuration,stHrs;
    String stCoupon="-";
    double surgeValue;
    int gst;
    List<OutStationPojo> dataList;
    OutStationPojo data;
    String stTimeForFare="00:00:00";
    Spinner sp;
    TextView tvTap,tvFareVary;
    String item="";
    OutStationActivity a;
    TextView tvFareEstimate;
    LinearLayout llFareEstimate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v= inflater.inflate(R.layout.fragment_out_station, container,false);

        tvDateTimeText=(TextView)v.findViewById(R.id.fos_tv_datetime_text);
        tvPkgText=(TextView)v.findViewById(R.id.fos_tv_pkg_text);
        tvDateTime=(TextView)v.findViewById(R.id.fos_tv_datetime);
        tvPkg=(TextView)v.findViewById(R.id.fos_tv_pkg);
        tvCab=(TextView)v.findViewById(R.id.fos_tv_cab);
        tvBookingid=(TextView)v.findViewById(R.id.fos_tv_bid);
        btFareEstimate=(TextView)v.findViewById(R.id.fos_bt_fare_estimate);
        btBook=(Button)v.findViewById(R.id.fos_bt_book);
        lLayout=(LinearLayout)v.findViewById(R.id.fos_ll);
        lBookingLayout=(LinearLayout)v.findViewById(R.id.fos_ll_0);
        ivDateTime=(ImageView)v.findViewById(R.id.fos_iv_datetime);
        ivPkg=(ImageView)v.findViewById(R.id.fos_iv_pkg);
        ivCab=(ImageView)v.findViewById(R.id.fos_iv_cab);
        tvPickup=(TextView)v.findViewById(R.id.fos_tv_pickup_loc);
        tvDrop=(TextView)v.findViewById(R.id.fos_tv_drop_loc);
        tvCash=(TextView)v.findViewById(R.id.fos_tv_cash);
        tvWallet=(TextView)v.findViewById(R.id.fos_tv_wallet);
        pref = getActivity().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        tvAddMoney=(TextView)v.findViewById(R.id.fos_tv_add_money);
        tvSurgeMsg=(TextView)v.findViewById(R.id.fos_tv_surge_msg);
        tvSurgeMsg.setVisibility(View.GONE);
        tvCoupon=(TextView)v.findViewById(R.id.fos_tv_coupon);
        tvAddMoney.setVisibility(View.GONE);
        sp=(Spinner)v.findViewById(R.id.fos_spinner);
        tvTap=(TextView)v.findViewById(R.id.fos_tv_tap);
        tvFareVary=(TextView)v.findViewById(R.id.fos_tv_fare_vary);
        ivDateTime.setVisibility(View.GONE);
        ivPkg.setVisibility(View.GONE);
        ivCab.setVisibility(View.GONE);
        tvFareEstimate=(TextView)v.findViewById(R.id.fos_tv_fare_est);
        tvFareEstimate.setVisibility(View.GONE);
        llFareEstimate=(LinearLayout)v.findViewById(R.id.fos_ll6);
        llFareEstimate.setVisibility(View.GONE);

        editor = pref.edit();
        REST_CLIENT= RestClient.get();

        dbAdapter=new DBAdapter(getContext());
        dbAdapter=dbAdapter.open();

        //btBook.setClickable(false);
        //btBook.setEnabled(false);
        btBook.setAlpha(Float.parseFloat("0.5"));

        session=new SessionManager(getActivity());
        user=session.getUserDetails();
        guestProfileId=user.get(SessionManager.KEY_PROFILE_ID);

        Bundle d=getActivity().getIntent().getExtras();
        if(d!=null) {
            city= d.getString("city");
        }

        pickupLoc=pref.getString("pickup_location",null);
        dropLoc=pref.getString("drop_location",null);
        pickupLat=pref.getString("pickup_lat",null);
        pickupLong=pref.getString("pickup_long",null);
        dropLat=pref.getString("drop_lat",null);
        dropLong=pref.getString("drop_long",null);

        tvPickup.setText(pickupLoc);
        tvDrop.setText(dropLoc);

        btFareEstimate.setEnabled(false);
        btFareEstimate.setClickable(false);
        btFareEstimate.setAlpha(Float.parseFloat("0.5"));
        tvCash.setEnabled(false);
        tvCash.setClickable(false);
        tvWallet.setEnabled(false);
        tvWallet.setClickable(false);
        tvCash.setAlpha(Float.parseFloat("0.5"));
        tvWallet.setAlpha(Float.parseFloat("0.5"));

        showDateTimeSettings();

        tvCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvWallet.setText("WALLET");
                tvAddMoney.setVisibility(View.GONE);

                tvWallet.setTextColor(Color.parseColor("#9e9e9e"));
                tvCash.setTextColor(Color.parseColor("#0067de"));
                stPaymentType="cash";
                btBook.setAlpha(1);
                //btBook.setClickable(true);
                //btBook.setEnabled(true);
            }
        });

        tvWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //getFareEstimate();

                stWalletAmount=dbAdapter.getWalletAmount();

                tvWallet.setText(getString(R.string.Rs)+" "+stWalletAmount+"  WALLET");



                if(Integer.parseInt(stWalletAmount)<amnt)
                {
                    Toast.makeText(getActivity(),"Insufficient balance! Please Add Money.",Toast.LENGTH_SHORT).show();
                    btBook.setAlpha(Float.parseFloat("0.5"));
                    //btBook.setClickable(false);
                    //btBook.setEnabled(false);
                    tvAddMoney.setVisibility(View.VISIBLE);
                    tvCash.setTextColor(Color.parseColor("#9e9e9e"));
                    tvWallet.setTextColor(Color.parseColor("#9e9e9e"));
                    stPaymentType="";
                }
                else {
                    btBook.setAlpha(1);
                    tvCash.setTextColor(Color.parseColor("#9e9e9e"));
                    tvWallet.setTextColor(Color.parseColor("#0067de"));
                    stPaymentType="wallet";
                    //btBook.setClickable(true);
                    //btBook.setEnabled(true);

                }

//                if(Integer.parseInt(stWalletAmount)<amnt)
//                {
//                    Toast.makeText(getActivity(),"Insufficient balance! Please Add Money.",Toast.LENGTH_LONG).show();
//                    btBook.setAlpha(Float.parseFloat("0.5"));
//                    btBook.setClickable(false);
//                    btBook.setEnabled(false);
//                }
//                else {
//                    btBook.setAlpha(1);
//                    btBook.setClickable(true);
//                    btBook.setEnabled(true);
//
//                }
            }
        });

        tvAddMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(getActivity(), PaymentActivity.class);
                i.putExtra("value","yes");
                startActivity(i);
            }
        });

        tvCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.alert_coupon, null);
                dialogBuilder.setView(dialogView);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                final EditText etCoupon=(EditText)dialogView.findViewById(R.id.ac_et_otp);
                Button btOk=(Button)dialogView.findViewById(R.id.ac_bt_ok);

                btOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        stCoupon=etCoupon.getText().toString().trim();

                        if(stCoupon.equals(""))
                        {
                            Toast.makeText(getActivity(),"Please enter coupon code",Toast.LENGTH_SHORT).show();
                            stCoupon="-";
                        }
                        else {

                            tvCoupon.setText(stCoupon);
                            tvCoupon.setTypeface(null, Typeface.BOLD);
                            tvCoupon.setTextColor(Color.parseColor("#FF6F00"));
                            //tvCoupon.setTextColor(Color.parseColor("#FBC02D"));
                            //tvCoupon.setBackgroundColor(Color.parseColor("#FFF176"));

                            tvCoupon.setTextSize(13);
                            alertDialog.dismiss();
                        }

                        //Promcode Not Applicable
                    }
                });

            }
        });

        btBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(stPaymentType.equals(""))
                {
                    Toast.makeText(getActivity(),"Please select the payment mode!",Toast.LENGTH_SHORT).show();
                }
                else {

                    if (dbAdapter.checkIfPlaceNameExists(dropLoc)) {

                    } else {

                        if (!(dropLat.equals("-")) || !(dropLong.equals("-"))) {
                            dbAdapter.insertUserLocation("1", dropLoc, Double.parseDouble(dropLat), Double.parseDouble(dropLong));
                        }
                    }

                    if (stPkg.equals("") || stCab.equals("")) {
                        Toast.makeText(getActivity(), "Please enter all the details", Toast.LENGTH_LONG).show();
                    } else {

                        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Please Wait ...");
                        progressDialog.show();

                        // System.out.println("fare estimate issssss "+amnt);

                        JsonObject v = new JsonObject();
                        v.addProperty("GuestProfileid", guestProfileId);
                        v.addProperty("PickupLat", pickupLat);
                        v.addProperty("PickupLong", pickupLong);
                        v.addProperty("DropLat", dropLat);
                        v.addProperty("DropLong", dropLong);
                        v.addProperty("PickupLoc", pickupLoc);
                        v.addProperty("DropLoc", dropLoc);
                        v.addProperty("companyid", companyId);
                        v.addProperty("scheduleddate", stDate + " " + stTime);
                        v.addProperty("scheduledtime", stTime);
                        v.addProperty("traveltype", "outstation");
                        v.addProperty("travelpackage", stPkg);
                        v.addProperty("vehiclecategory", stCab);
                        v.addProperty("bookingType", "AppBooking");
                        v.addProperty("location", city);
                        v.addProperty("payment_type", stPaymentType);
                        v.addProperty("fare_estimate", amnt);
                        v.addProperty("slabhours", "-");
                        v.addProperty("slabkms", "-");
                        v.addProperty("Promocode", stCoupon);
                        //  v.addProperty("Profileid"," ");

                        System.out.println("promocode is " + stCoupon);

                        System.out.println("data is " + stPkg + ":" + stCab + ":" + amnt);

                        Call<BookCabPojo> call = REST_CLIENT.sendOutstationDetails(v);
                        call.enqueue(new Callback<BookCabPojo>() {
                            @Override
                            public void onResponse(Call<BookCabPojo> call, Response<BookCabPojo> response) {

                                BookCabPojo data;

                                if (response.isSuccessful()) {

                                    data = response.body();
                                    System.out.println("********* " + data.getMessage());

                                    String d[] = data.getMessage().split(",");

                                    progressDialog.dismiss();

                                    if (d.length == 1) {

                                        if(data.getMessage().equals("Access denied"))
                                        {
                                            Toast.makeText(getActivity(),"Access denied!",Toast.LENGTH_SHORT).show();
                                        }
                                        else {

                                            lBookingLayout.setVisibility(View.VISIBLE);

                                            btBook.setVisibility(View.INVISIBLE);
                                            // tvBookingid.setText("' "+data.getMessage()+" ' is your Booking Id !");
                                            // tvBookingid.setVisibility(View.VISIBLE);
                                            tvBookingid.setText("Booking Id ' " + data.getMessage() + " ' generated");
                                            ivCab.setClickable(false);
                                            ivPkg.setClickable(false);
                                            ivDateTime.setClickable(false);
                                            btFareEstimate.setClickable(false);
                                            tvCash.setClickable(false);
                                            tvWallet.setClickable(false);
                                            tvCoupon.setClickable(false);
                                        }
                                    }
                                    else {

                                        if(d[0].equals("Access denied"))
                                        {
                                            Toast.makeText(getActivity(),data.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            tvCoupon.setText("Apply Coupon");
                                            tvCoupon.setTypeface(null, Typeface.BOLD);
                                            tvCoupon.setTextSize(12);
                                            tvCoupon.setTextColor(Color.parseColor("#FF8F00"));
                                            stCoupon = "-";
                                            Toast.makeText(getActivity(), "Coupon: " + d[1], Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<BookCabPojo> call, Throwable t) {

                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Connectivity Error..Please Retry!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });

        ivDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.alert_date_time, null);
                dialogBuilder.setView(dialogView);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                dp=(DatePicker)dialogView.findViewById(R.id.datePicker);
                tp=(TimePicker)dialogView.findViewById(R.id.simpleTimePicker);
                ok=(Button)dialogView.findViewById(R.id.ok);
                cancel=(Button)dialogView.findViewById(R.id.cancel);
                cancel.setVisibility(View.GONE);

                hr=tp.getCurrentHour();

                min=tp.getCurrentMinute();
                long now = System.currentTimeMillis() - 1000;

                dp.setMinDate(now);
                dp.setMaxDate(now+(1000*60*60*24*7));

                day = dp.getDayOfMonth();
                mnth = dp.getMonth() + 1;
                yr = dp.getYear();

                //System.out.println("date is "+day+":"+mnth+":"+yr);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                dp.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {

                        day=dayOfMonth;
                        mnth=month+1;
                        yr=year;

                    }
                });

                tp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker timePicker, int i, int i1) {

                        hr=i;
                        min=i1+1;

                    }
                });

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        System.out.println("************* "+mnth);

                        Date d=makeDateGMT(yr,mnth-1,day);
                        Date d1=new Date();

                        if(d.equals(d1))
                        {

                            Calendar datetime = Calendar.getInstance();
                            Calendar c = Calendar.getInstance();
                            datetime.set(Calendar.HOUR_OF_DAY, hr);
                            datetime.set(Calendar.MINUTE, min);

                            System.out.println("@@@@@@@ "+datetime.toString()+"::"+c.toString());

                            if(datetime.getTimeInMillis() > c.getTimeInMillis()){

                                // stDate=day+"/"+mnth+"/"+yr;
                                stDate=yr+"-"+mnth+"-"+day;
                                stTime=hr+":"+min;
                                DecimalFormat f=new DecimalFormat("00");
                                String hr1,min1;
                                hr1=f.format(hr);
                                min1=f.format(min);
                                stTimeForFare=hr1+":"+min1+":"+"00";

                                stTime=convert24To12System(hr,min);

                                tvDateTime.setVisibility(View.VISIBLE);
                                tvDateTime.setText(stDate+" "+stTime);
                                tvDateTime.setTextColor(Color.parseColor("#000000"));
                                alertDialog.dismiss();

                                getFareEstimate();

                            }else{
                                Toast.makeText(getActivity(),"Please Choose Time ahead of current time!",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            // stDate=day+"/"+mnth+"/"+yr;
                            stDate=yr+"-"+mnth+"-"+day;
                            stTime=hr+":"+min;
                            DecimalFormat f=new DecimalFormat("00");
                            String hr1,min1;
                            hr1=f.format(hr);
                            min1=f.format(min);
                            stTimeForFare=hr1+":"+min1+":"+"00";

                            stTime=convert24To12System(hr,min);

                            tvDateTime.setVisibility(View.VISIBLE);

                            tvDateTime.setText(stDate+" "+stTime);
                            tvDateTime.setTextColor(Color.parseColor("#000000"));
                            alertDialog.dismiss();

                            getFareEstimate();
                        }
                    }
                });
            }
        });

        ivPkg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.alert_os_packages, null);
                dialogBuilder.setView(dialogView);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                TextView tv1waytrip=(TextView)dialogView.findViewById(R.id.apd_tv_1way);
                TextView tv1rnd=(TextView)dialogView.findViewById(R.id.apd_tv_1rnd);
                TextView tv2rnd=(TextView)dialogView.findViewById(R.id.apd_tv_2rnd);
                TextView tv3rnd=(TextView)dialogView.findViewById(R.id.apd_tv_3rnd);
                TextView tv4rnd=(TextView)dialogView.findViewById(R.id.apd_tv_4rnd);
                TextView tv5rnd=(TextView)dialogView.findViewById(R.id.apd_tv_5rnd);
                TextView tv6rnd=(TextView)dialogView.findViewById(R.id.apd_tv_6rnd);

                tv1waytrip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        stPkg="1 way trip";
                        stPkgNo=1;

                        alertDialog.dismiss();
                        lLayout.setVisibility(View.VISIBLE);

                        tvPkg.setText(stPkg);
                        tvPkg.setTextColor(Color.parseColor("#000000"));
                        //  btBook.setAlpha(1);
                        getFareEstimate();
                    }
                });

                tv1rnd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        stPkg="1 day  round trip";
                        stPkgNo=1;

                        alertDialog.dismiss();
                        lLayout.setVisibility(View.VISIBLE);

                        tvPkg.setText(stPkg);
                        tvPkg.setTextColor(Color.parseColor("#000000"));
                        //   btBook.setAlpha(1);
                        getFareEstimate();
                    }
                });
                tv2rnd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        stPkg="2 day  round trip";
                        stPkgNo=2;

                        alertDialog.dismiss();
                        lLayout.setVisibility(View.VISIBLE);

                        tvPkg.setText(stPkg);
                        tvPkg.setTextColor(Color.parseColor("#000000"));
                        //  btBook.setAlpha(1);
                        getFareEstimate();
                    }
                });
                tv3rnd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        stPkg="3 day  round trip";
                        stPkgNo=3;

                        alertDialog.dismiss();
                        lLayout.setVisibility(View.VISIBLE);

                        tvPkg.setText(stPkg);
                        tvPkg.setTextColor(Color.parseColor("#000000"));

                        //    btBook.setAlpha(1);
                        getFareEstimate();
                    }
                });
                tv4rnd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        stPkg="4 day  round trip";
                        stPkgNo=4;

                        alertDialog.dismiss();
                        lLayout.setVisibility(View.VISIBLE);

                        tvPkg.setText(stPkg);
                        tvPkg.setTextColor(Color.parseColor("#000000"));

                        //  btBook.setAlpha(1);
                        getFareEstimate();
                    }
                });
                tv5rnd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        stPkg="5 day  round trip";
                        stPkgNo=5;

                        alertDialog.dismiss();
                        lLayout.setVisibility(View.VISIBLE);

                        tvPkg.setText(stPkg);
                        tvPkg.setTextColor(Color.parseColor("#000000"));

                        //  btBook.setAlpha(1);
                        getFareEstimate();
                    }
                });
                tv6rnd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        stPkg="6 day  round trip";
                        stPkgNo=6;

                        alertDialog.dismiss();
                        lLayout.setVisibility(View.VISIBLE);

                        tvPkg.setText(stPkg);
                        tvPkg.setTextColor(Color.parseColor("#000000"));

                        //   btBook.setAlpha(1);
                        getFareEstimate();
                    }
                });

            }
        });

        ivCab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAfterCabCategory();
            }
        });

        btFareEstimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Getting Fare Estimate ...");
                progressDialog.show();

                // AIzaSyC4Ccgq_w6OhyF6IVblH3KByt5tKuJNtdM
                String urlString="https://maps.googleapis.com/maps/api/distancematrix/json?" +
                        "origins="+pickupLat+","+pickupLong+"&destinations="+dropLat+","+dropLong+"&key=AIzaSyC4Ccgq_w6OhyF6IVblH3KByt5tKuJNtdM";

                Call<DurationPojo> call=REST_CLIENT.getDistanceDetails(urlString);
                call.enqueue(new Callback<DurationPojo>() {
                    @Override
                    public void onResponse(Call<DurationPojo> call, Response<DurationPojo> response) {

                        DurationPojo d;

                        if(response.isSuccessful()) {
                            d = response.body();
                            List<Row> r = d.getRows();
                            Row r1;
                            for (int a = 0; a < r.size(); a++) {
                                r1 = r.get(a);
                                List<Element> e = r1.getElements();

                                Element e1;

                                for (int b = 0; b < e.size(); b++) {
                                    e1 = e.get(b);
                                    Distance t1 = e1.getDistance();

                                    Duration t2 = e1.getDuration();

                                    String stDist = t1.getText();
                                    String stDistDetails[] = stDist.split(" ");
                                    stKms = stDistDetails[0];


                                    //stDuration=String.valueOf((t2.getValue())/60);
                                    //not required ... stHrs=String.valueOf(t2.getValue());
                                    //stHrs=String.valueOf(Double.parseDouble(stDuration)/60);

                                    //System.out.println("d & t is "+stKms+":"+stDuration);

                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

                                    LayoutInflater inflater = getActivity().getLayoutInflater();
                                    final View dialogView = inflater.inflate(R.layout.alert_outstation_cab_details, null);
                                    dialogBuilder.setView(dialogView);

                                    final AlertDialog alertDialog = dialogBuilder.create();
                                    alertDialog.show();

                                    final TextView tvMini=(TextView)dialogView.findViewById(R.id.aocd_tv_mini);
                                    final TextView tvMiniKms=(TextView)dialogView.findViewById(R.id.aocd_tv_mini_kms);
                                    final TextView tvMiniKmsRate=(TextView)dialogView.findViewById(R.id.aocd_tv_mini_kms_rate);
                                    final  TextView tvMinmKms=(TextView)dialogView.findViewById(R.id.aocd_tv_min_kms);
                                    final TextView tvAllowedhrs=(TextView)dialogView.findViewById(R.id.aocd_tv_allowed_hrs);
                                    final TextView tvExtraRate=(TextView)dialogView.findViewById(R.id.aocd_tv_extra_rate);
                                    final TextView tvDriverAllowance=(TextView)dialogView.findViewById(R.id.aocd_tv_driver_allowance);
                                    final TextView tvSurgecharge=(TextView)dialogView.findViewById(R.id.aocd_tv_surge);
                                    final TextView tvGST=(TextView)dialogView.findViewById(R.id.aocd_tv_gst);


                                    JsonObject v=new JsonObject();
                                    v.addProperty("companyid",companyId);
                                    v.addProperty("location",city);
                                    v.addProperty("traveltype","outstation");
                                    v.addProperty("vehicle_type","All");
                                    v.addProperty("approxkms",stKms);
                                    v.addProperty("duration",stDuration);

                                    Call<List<OutStationPojo>> call1=REST_CLIENT.getFareEstimate(v);
                                    call1.enqueue(new Callback<List<OutStationPojo>>() {
                                        @Override
                                        public void onResponse(Call<List<OutStationPojo>> call, Response<List<OutStationPojo>> response) {

                                            if(response.isSuccessful())
                                            {
                                                dataList=response.body();

                                                for(int i=0;i<dataList.size();i++)
                                                {
                                                    data=dataList.get(i);

                                                    //tvSurgeMsg.setVisibility(View.GONE);

                                                    switch (i)
                                                    {
                                                        case 0:if(stCab.equals("Mini")) {

                                                            if (data.getPeakhoursdata().equals("")) {

                                                                if(stPkg.equals("1 way trip")) {

                                                                    amnt = getAmntOneWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin());
                                                                }
                                                                else {
                                                                    amnt=getAmntTwoWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin(),data.getMinKms(),data.getAllowedHrs());
                                                                }

                                                                gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;

                                                                tvSurgeMsg.setVisibility(View.GONE);


                                                            } else {

                                                                //start--new logic

                                                                if(stPkg.equals("1 way trip")) {

                                                                    amnt = getAmntOneWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin());
                                                                }
                                                                else {
                                                                    amnt=getAmntTwoWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin(),data.getMinKms(),data.getAllowedHrs());
                                                                }

                                                                gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;

                                                                tvSurgeMsg.setVisibility(View.GONE);

                                                                //end--new logic

                                                                System.out.println("^^^^^ "+data.getPeakhoursdata());

                                                                String v[] = data.getPeakhoursdata().split(",");

                                                                outerloop:

                                                                for (int l = 0; l < v.length; l++) {
                                                                    String w = v[l];
                                                                    String y[] = w.split("-");

                                                                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                                                                    try {
                                                                        Date d1 = format.parse(stTimeForFare);
                                                                        Date d2 = format.parse(y[0]);
                                                                        Date d3 = format.parse(y[1]);

                                                                        String z[] = y[4].split("\\|");

                                                                        System.out.println("z.lenght" + z.length);

                                                                        if (z.length != 0) {
                                                                            for (int k = 0; k < z.length; k++) {

                                                                                if (z[k].equals("outstation")) {

                                                                                    if (isWithinRange(d1, d2, d3)) {

                                                                                        if(stPkg.equals("1 way trip")) {

                                                                                            amnt = getAmntOneWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin());
                                                                                        }
                                                                                        else {
                                                                                            amnt=getAmntTwoWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin(),data.getMinKms(),data.getAllowedHrs());
                                                                                        }

                                                                                        int a = Integer.parseInt(y[2]) * amnt;
                                                                                        int b = a / Integer.parseInt(y[3]);

                                                                                        System.out.println("Surge value issss "+b);

                                                                                        surgeValue = b;
                                                                                        amnt=amnt+(int)surgeValue;
                                                                                        tvSurgeMsg.setVisibility(View.VISIBLE);

                                                                                        gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;

                                                                                        tvSurgecharge.setText(y[2]+" - "+y[3]);
                                                                                        break outerloop;

                                                                                    }
                                                                                    else {

                                                                                        if(stPkg.equals("1 way trip")) {

                                                                                            amnt = getAmntOneWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin());
                                                                                        }
                                                                                        else {
                                                                                            amnt=getAmntTwoWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin(),data.getMinKms(),data.getAllowedHrs());
                                                                                        }

                                                                                        gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;


                                                                                        tvSurgecharge.setText("-");
                                                                                        tvSurgeMsg.setVisibility(View.GONE);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        //System.out.println(date);
                                                                    } catch (ParseException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }
                                                            //int f = (int) Double.parseDouble(data.getTotalfare());

                                                            if(stPkg.equals("1 way trip"))
                                                            {
                                                                tvMiniKms.setText(stKms);
                                                                tvMiniKmsRate.setText(getString(R.string.Rs)+" "+2*Double.parseDouble(data.getOutsidekmsrate()));
                                                            }
                                                            else {
                                                                tvMiniKms.setText(String.valueOf(2*Double.parseDouble(stKms)));
                                                                tvMiniKmsRate.setText(getString(R.string.Rs)+" "+(data.getOutsidekmsrate()));

                                                            }
                                                            //amnt=amnt+(15*amnt)/100;
                                                            tvMini.setText(getString(R.string.Rs)+" " + String.valueOf(amnt));
                                                            tvMinmKms.setText(data.getMinKms());
                                                            tvAllowedhrs.setText(data.getAllowedHrs());
                                                            tvExtraRate.setText(getString(R.string.Rs)+" "+data.getExtraRatePerMin());
                                                            tvDriverAllowance.setText(getString(R.string.Rs)+" "+data.getDriverAllowance());
                                                            tvGST.setText(getString(R.string.Rs)+" "+gst);


                                                            btFareEstimate.setText("Approx. Fare "+getString(R.string.Rs)+" " + String.valueOf(amnt)+" + "+getString(R.string.Rs)+" "+gst+" GST");

                                                            progressDialog.dismiss();
                                                        }
                                                            break;
                                                        case 2:if(stCab.equals("SUV")) {

                                                            if (data.getPeakhoursdata().equals("")) {

                                                                if(stPkg.equals("1 way trip")) {

                                                                    amnt = getAmntOneWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin());
                                                                }
                                                                else {
                                                                    amnt=getAmntTwoWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin(),data.getMinKms(),data.getAllowedHrs());
                                                                }

                                                                gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;

                                                                tvSurgeMsg.setVisibility(View.GONE);
                                                            } else {

                                                                //start--new logic

                                                                if(stPkg.equals("1 way trip")) {

                                                                    amnt = getAmntOneWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin());
                                                                }
                                                                else {
                                                                    amnt=getAmntTwoWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin(),data.getMinKms(),data.getAllowedHrs());
                                                                }

                                                                gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;

                                                                tvSurgeMsg.setVisibility(View.GONE);

                                                                //end--new logic

                                                                System.out.println("^^^^^ "+data.getPeakhoursdata());

                                                                String v[] = data.getPeakhoursdata().split(",");

                                                                outerloop:

                                                                for (int l = 0; l < v.length; l++) {
                                                                    String w = v[l];
                                                                    String y[] = w.split("-");

                                                                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                                                                    try {
                                                                        Date d1 = format.parse(stTimeForFare);
                                                                        Date d2 = format.parse(y[0]);
                                                                        Date d3 = format.parse(y[1]);

                                                                        String z[] = y[4].split("\\|");

                                                                        System.out.println("z.lenght" + z.length);

                                                                        if (z.length != 0) {
                                                                            for (int k = 0; k < z.length; k++) {

                                                                                if (z[k].equals("outstation")) {

                                                                                    if (isWithinRange(d1, d2, d3)) {

                                                                                        if(stPkg.equals("1 way trip")) {

                                                                                            amnt = getAmntOneWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin());
                                                                                        }
                                                                                        else {
                                                                                            amnt=getAmntTwoWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin(),data.getMinKms(),data.getAllowedHrs());
                                                                                        }

                                                                                        int a = Integer.parseInt(y[2]) * amnt;
                                                                                        int b = a / Integer.parseInt(y[3]);

                                                                                        System.out.println("Surge value issss "+b);

                                                                                        surgeValue = b;
                                                                                        amnt=amnt+(int)surgeValue;
                                                                                        tvSurgeMsg.setVisibility(View.VISIBLE);

                                                                                        gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;

                                                                                        tvSurgecharge.setText(y[2]+" - "+y[3]);
                                                                                        break outerloop;

                                                                                    }
                                                                                    else {

                                                                                        if(stPkg.equals("1 way trip")) {

                                                                                            amnt = getAmntOneWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin());
                                                                                        }
                                                                                        else {
                                                                                            amnt=getAmntTwoWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin(),data.getMinKms(),data.getAllowedHrs());
                                                                                        }

                                                                                        gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;


                                                                                        tvSurgecharge.setText("-");
                                                                                        tvSurgeMsg.setVisibility(View.GONE);
                                                                                    }

                                                                                }
                                                                            }
                                                                        }
                                                                        //System.out.println(date);
                                                                    } catch (ParseException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }
                                                            //int f = (int) Double.parseDouble(data.getTotalfare());

                                                            if(stPkg.equals("1 way trip"))
                                                            {
                                                                tvMiniKms.setText(stKms);
                                                                tvMiniKmsRate.setText(getString(R.string.Rs)+" "+2*Double.parseDouble(data.getOutsidekmsrate()));
                                                            }
                                                            else {
                                                                tvMiniKms.setText(String.valueOf(2*Double.parseDouble(stKms)));
                                                                tvMiniKmsRate.setText(getString(R.string.Rs)+" "+(data.getOutsidekmsrate()));

                                                            }
                                                            //amnt=amnt+(15*amnt)/100;
                                                            tvMini.setText(getString(R.string.Rs)+" " + String.valueOf(amnt));
                                                            tvMinmKms.setText(data.getMinKms());
                                                            tvAllowedhrs.setText(data.getAllowedHrs());
                                                            tvExtraRate.setText(getString(R.string.Rs)+" "+data.getExtraRatePerMin());
                                                            tvDriverAllowance.setText(getString(R.string.Rs)+" "+data.getDriverAllowance());
                                                            tvGST.setText(getString(R.string.Rs)+" "+gst);


                                                            btFareEstimate.setText("Approx. Fare "+getString(R.string.Rs)+" " + String.valueOf(amnt)+" + "+getString(R.string.Rs)+" "+gst+" GST");



                                                            /*if(stPkg.equals("1 way trip"))
                                                            {
                                                                tvMiniKms.setText(stKms);
                                                                tvMiniKmsRate.setText("Rs. "+2*Double.parseDouble(data.getOutsidekmsrate()));
                                                            }
                                                            else {
                                                                tvMiniKms.setText(String.valueOf(2*Double.parseDouble(stKms)));
                                                                tvMiniKmsRate.setText("Rs. "+(data.getOutsidekmsrate()));

                                                            }
                                                            //amnt=amnt+(15*amnt)/100;
                                                            tvMini.setText("Rs. " + String.valueOf(amnt));
                                                            tvMinmKms.setText(data.getMinKms());
                                                            tvAllowedhrs.setText(data.getAllowedHrs());
                                                            tvExtraRate.setText("Rs. "+data.getExtraRatePerMin());
                                                            tvDriverAllowance.setText("Rs. "+data.getDriverAllowance());
                                                            tvGST.setText("Rs. "+gst);
                                                            //tvSurgecharge.setText("Applied");

                                                            btFareEstimate.setText("Approx. Fare Rs. " + String.valueOf(amnt)+" + Rs."+gst+" GST");*/
                                                            progressDialog.dismiss();
                                                        }
                                                            break;

                                                        case 1: if(stCab.equals("Sedan")) {

                                                            if (data.getPeakhoursdata().equals("")) {

                                                                if(stPkg.equals("1 way trip")) {

                                                                    amnt = getAmntOneWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin());
                                                                }
                                                                else {
                                                                    amnt=getAmntTwoWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin(),data.getMinKms(),data.getAllowedHrs());
                                                                }

                                                                gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;

                                                                tvSurgeMsg.setVisibility(View.GONE);

                                                            } else {

                                                                //start--new logic

                                                                if(stPkg.equals("1 way trip")) {

                                                                    amnt = getAmntOneWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin());
                                                                }
                                                                else {
                                                                    amnt=getAmntTwoWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin(),data.getMinKms(),data.getAllowedHrs());
                                                                }

                                                                gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;

                                                                tvSurgeMsg.setVisibility(View.GONE);

                                                                //end--new logic

                                                                System.out.println("^^^^^ "+data.getPeakhoursdata());

                                                                String v[] = data.getPeakhoursdata().split(",");

                                                                outerloop:

                                                                for (int l = 0; l < v.length; l++) {
                                                                    String w = v[l];
                                                                    String y[] = w.split("-");

                                                                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                                                                    try {
                                                                        Date d1 = format.parse(stTimeForFare);
                                                                        Date d2 = format.parse(y[0]);
                                                                        Date d3 = format.parse(y[1]);

                                                                        String z[] = y[4].split("\\|");

                                                                        System.out.println("z.lenght" + z.length);

                                                                        if (z.length != 0) {
                                                                            for (int k = 0; k < z.length; k++) {

                                                                                if (z[k].equals("outstation")) {

                                                                                    if (isWithinRange(d1, d2, d3)) {

                                                                                        if(stPkg.equals("1 way trip")) {

                                                                                            amnt = getAmntOneWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin());
                                                                                        }
                                                                                        else {
                                                                                            amnt=getAmntTwoWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin(),data.getMinKms(),data.getAllowedHrs());
                                                                                        }

                                                                                        int a = Integer.parseInt(y[2]) * amnt;
                                                                                        int b = a / Integer.parseInt(y[3]);

                                                                                        System.out.println("Surge value issss "+b);

                                                                                        surgeValue = b;
                                                                                        amnt=amnt+(int)surgeValue;
                                                                                        tvSurgeMsg.setVisibility(View.VISIBLE);

                                                                                        gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;

                                                                                        tvSurgecharge.setText(y[2]+" - "+y[3]);
                                                                                        break outerloop;

                                                                                    }
                                                                                    else {

                                                                                        if(stPkg.equals("1 way trip")) {

                                                                                            amnt = getAmntOneWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin());
                                                                                        }
                                                                                        else {
                                                                                            amnt=getAmntTwoWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin(),data.getMinKms(),data.getAllowedHrs());
                                                                                        }

                                                                                        gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;


                                                                                        tvSurgecharge.setText("-");
                                                                                        tvSurgeMsg.setVisibility(View.GONE);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        //System.out.println(date);
                                                                    } catch (ParseException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }
                                                            //int f = (int) Double.parseDouble(data.getTotalfare());

                                                            if(stPkg.equals("1 way trip"))
                                                            {
                                                                tvMiniKms.setText(stKms);
                                                                tvMiniKmsRate.setText(getString(R.string.Rs)+" "+2*Double.parseDouble(data.getOutsidekmsrate()));
                                                            }
                                                            else {
                                                                tvMiniKms.setText(String.valueOf(2*Double.parseDouble(stKms)));
                                                                tvMiniKmsRate.setText(getString(R.string.Rs)+" "+(data.getOutsidekmsrate()));

                                                            }
                                                            //amnt=amnt+(15*amnt)/100;
                                                            tvMini.setText(getString(R.string.Rs)+" " + String.valueOf(amnt));
                                                            tvMinmKms.setText(data.getMinKms());
                                                            tvAllowedhrs.setText(data.getAllowedHrs());
                                                            tvExtraRate.setText(getString(R.string.Rs)+" "+data.getExtraRatePerMin());
                                                            tvDriverAllowance.setText(getString(R.string.Rs)+" "+data.getDriverAllowance());
                                                            tvGST.setText(getString(R.string.Rs)+" "+gst);


                                                            btFareEstimate.setText("Approx. Fare "+getString(R.string.Rs)+" " + String.valueOf(amnt)+" + "+getString(R.string.Rs)+" "+gst+" GST");

                                                            progressDialog.dismiss();

                                                        }
                                                            break;
                                                    }


                                                }
                                            }
                                            else {

                                                progressDialog.dismiss();
                                                Toast.makeText(getActivity(),response.message(),Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<List<OutStationPojo>> call, Throwable t) {

                                            progressDialog.dismiss();
                                            Toast.makeText(getActivity(),"Please check Internet connection!",Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<DurationPojo> call, Throwable t) {

                        Toast.makeText(getActivity(),"Please Check Internet  Connection !",Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        return v;
    }

    public void getFareEstimate()
    {
        /*btFareEstimate.setVisibility(View.VISIBLE);
        tvTap.setVisibility(View.VISIBLE);
        tvFareVary.setVisibility(View.VISIBLE);*/

        tvFareEstimate.setVisibility(View.VISIBLE);
        llFareEstimate.setVisibility(View.VISIBLE);

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Getting Fare Estimate ...");
        progressDialog.show();

        // AIzaSyC4Ccgq_w6OhyF6IVblH3KByt5tKuJNtdM
        String urlString="https://maps.googleapis.com/maps/api/distancematrix/json?" +
                "origins="+pickupLat+","+pickupLong+"&destinations="+dropLat+","+dropLong+"&key=AIzaSyC4Ccgq_w6OhyF6IVblH3KByt5tKuJNtdM";

        Log.i("os url",urlString);

        Call<DurationPojo> call=REST_CLIENT.getDistanceDetails(urlString);
        call.enqueue(new Callback<DurationPojo>() {
            @Override
            public void onResponse(Call<DurationPojo> call, Response<DurationPojo> response) {

                DurationPojo d;

                if(response.isSuccessful()) {
                    d = response.body();
                    List<Row> r = d.getRows();
                    Row r1;
                    for (int a = 0; a < r.size(); a++) {
                        r1 = r.get(a);
                        List<Element> e = r1.getElements();

                        Element e1;

                        for (int b = 0; b < e.size(); b++) {
                            e1 = e.get(b);
                            Distance t1 = e1.getDistance();

                            Duration t2 = e1.getDuration();

                            String stDist = t1.getText();
                            String stDistDetails[] = stDist.split(" ");
                            stKms = stDistDetails[0];


                            //stDuration=String.valueOf((t2.getValue())/60);
                            //stHrs=String.valueOf(Double.parseDouble(stDuration)/60);

                            System.out.println("d & t is "+stKms+":"+stDuration);

                           /* AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

                            LayoutInflater inflater = getActivity().getLayoutInflater();
                            final View dialogView = inflater.inflate(R.layout.alert_outstation_cab_details, null);
                            dialogBuilder.setView(dialogView);

                            final AlertDialog alertDialog = dialogBuilder.create();
                            alertDialog.show();

                            final TextView tvMini=(TextView)dialogView.findViewById(R.id.aocd_tv_mini);
                            final TextView tvMiniKms=(TextView)dialogView.findViewById(R.id.aocd_tv_mini_kms);
                            final TextView tvMiniKmsRate=(TextView)dialogView.findViewById(R.id.aocd_tv_mini_kms_rate);*/


                            JsonObject v=new JsonObject();
                            v.addProperty("companyid",companyId);
                            v.addProperty("location",city);
                            v.addProperty("traveltype","outstation");
                            v.addProperty("vehicle_type","All");
                            v.addProperty("approxkms",stKms);
                            v.addProperty("duration",stDuration);

                            Call<List<OutStationPojo>> call1=REST_CLIENT.getFareEstimate(v);
                            call1.enqueue(new Callback<List<OutStationPojo>>() {
                                @Override
                                public void onResponse(Call<List<OutStationPojo>> call, Response<List<OutStationPojo>> response) {

                                    List<OutStationPojo> dataList;
                                    OutStationPojo data;

                                    if(response.isSuccessful())
                                    {
                                        dataList=response.body();

                                        for(int i=0;i<dataList.size();i++)
                                        {
                                            data=dataList.get(i);

                                            //tvSurgeMsg.setVisibility(View.GONE);

                                            switch (i)
                                            {
                                                case 0:if(stCab.equals("Mini")) {

                                                    if (data.getPeakhoursdata().equals("")) {

                                                        if(stPkg.equals("1 way trip")) {

                                                            amnt = getAmntOneWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin());
                                                        }
                                                        else {
                                                            amnt=getAmntTwoWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin(),data.getMinKms(),data.getAllowedHrs());
                                                        }

                                                        gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;

                                                        tvSurgeMsg.setVisibility(View.GONE);

                                                    } else {

                                                        //start--new logic

                                                        if(stPkg.equals("1 way trip")) {

                                                            amnt = getAmntOneWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin());
                                                        }
                                                        else {
                                                            amnt=getAmntTwoWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin(),data.getMinKms(),data.getAllowedHrs());
                                                        }

                                                        gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;

                                                        tvSurgeMsg.setVisibility(View.GONE);

                                                        //end--new logic

                                                        System.out.println("^^^^^ "+data.getPeakhoursdata());

                                                        String v[] = data.getPeakhoursdata().split(",");

                                                        outerloop:

                                                        for (int l = 0; l < v.length; l++) {
                                                            String w = v[l];
                                                            String y[] = w.split("-");

                                                            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                                                            try {
                                                                System.out.println("stTimefor fare "+stTimeForFare);
                                                                Date d1 = format.parse(stTimeForFare);
                                                                Date d2 = format.parse(y[0]);
                                                                Date d3 = format.parse(y[1]);

                                                                String z[] = y[4].split("\\|");

                                                                System.out.println("z.lenght" + z.length);

                                                                if (z.length != 0) {
                                                                    for (int k = 0; k < z.length; k++) {

                                                                        if (z[k].equals("outstation")) {

                                                                            if (isWithinRange(d1, d2, d3)) {

                                                                                System.out.println("within range");

                                                                                if(stPkg.equals("1 way trip")) {

                                                                                    amnt = getAmntOneWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin());
                                                                                }
                                                                                else {
                                                                                    amnt=getAmntTwoWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin(),data.getMinKms(),data.getAllowedHrs());
                                                                                }

                                                                                int a = Integer.parseInt(y[2]) * amnt;
                                                                                int b = a / Integer.parseInt(y[3]);

                                                                                System.out.println("Surge value issss "+b);

                                                                                surgeValue = b;
                                                                                amnt=amnt+(int)surgeValue;
                                                                                tvSurgeMsg.setVisibility(View.VISIBLE);

                                                                                gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;

                                                                                break outerloop;

                                                                            }

                                                                            else {

                                                                                if(stPkg.equals("1 way trip")) {

                                                                                    amnt = getAmntOneWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin());
                                                                                }
                                                                                else {
                                                                                    amnt=getAmntTwoWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin(),data.getMinKms(),data.getAllowedHrs());
                                                                                }

                                                                                gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;

                                                                                tvSurgeMsg.setVisibility(View.GONE);

                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                //System.out.println(date);
                                                            } catch (ParseException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                    //int f = (int) Double.parseDouble(data.getTotalfare());

                                                    btFareEstimate.setText("Approx. Fare " +getString(R.string.Rs) +" "+ String.valueOf(amnt)+" + "+getString(R.string.Rs) +" "+gst+" GST");
                                                    progressDialog.dismiss();

                                                }
                                                    break;
                                                case 2:if(stCab.equals("SUV")) {

                                                    if (data.getPeakhoursdata().equals("")) {

                                                        if(stPkg.equals("1 way trip")) {

                                                            amnt = getAmntOneWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin());
                                                        }
                                                        else {
                                                            amnt=getAmntTwoWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin(),data.getMinKms(),data.getAllowedHrs());
                                                        }

                                                        gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;
                                                        tvSurgeMsg.setVisibility(View.GONE);

                                                    } else {

                                                        //start--new logic

                                                        if(stPkg.equals("1 way trip")) {

                                                            amnt = getAmntOneWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin());
                                                        }
                                                        else {
                                                            amnt=getAmntTwoWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin(),data.getMinKms(),data.getAllowedHrs());
                                                        }

                                                        gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;

                                                        tvSurgeMsg.setVisibility(View.GONE);

                                                        //end--new logic

                                                        System.out.println("^^^^^ "+data.getPeakhoursdata());

                                                        String v[] = data.getPeakhoursdata().split(",");

                                                        outerloop:

                                                        for (int l = 0; l < v.length; l++) {
                                                            String w = v[l];
                                                            String y[] = w.split("-");

                                                            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                                                            try {
                                                                Date d1 = format.parse(stTimeForFare);
                                                                Date d2 = format.parse(y[0]);
                                                                Date d3 = format.parse(y[1]);

                                                                String z[] = y[4].split("\\|");

                                                                System.out.println("z.lenght" + z.length);

                                                                if (z.length != 0) {
                                                                    for (int k = 0; k < z.length; k++) {

                                                                        if (z[k].equals("outstation")) {

                                                                            if (isWithinRange(d1, d2, d3)) {

                                                                                if(stPkg.equals("1 way trip")) {

                                                                                    amnt = getAmntOneWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin());
                                                                                }
                                                                                else {
                                                                                    amnt=getAmntTwoWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin(),data.getMinKms(),data.getAllowedHrs());
                                                                                }

                                                                                int a = Integer.parseInt(y[2]) * amnt;
                                                                                int b = a / Integer.parseInt(y[3]);

                                                                                System.out.println("Surge value issss "+b);

                                                                                surgeValue = b;
                                                                                amnt=amnt+(int)surgeValue;
                                                                                tvSurgeMsg.setVisibility(View.VISIBLE);

                                                                                gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;

                                                                                break outerloop;

                                                                            }
                                                                            else {

                                                                                if(stPkg.equals("1 way trip")) {

                                                                                    amnt = getAmntOneWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin());
                                                                                }
                                                                                else {
                                                                                    amnt=getAmntTwoWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin(),data.getMinKms(),data.getAllowedHrs());
                                                                                }

                                                                                gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;
                                                                                tvSurgeMsg.setVisibility(View.GONE);

                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                //System.out.println(date);
                                                            } catch (ParseException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                    //int f = (int) Double.parseDouble(data.getTotalfare());

                                                    //btFareEstimate.setText("Approx. Fare Rs. " + String.valueOf(amnt)+" + Rs."+gst+" GST");
                                                    btFareEstimate.setText("Approx. Fare " +getString(R.string.Rs) +" "+ String.valueOf(amnt)+" + "+getString(R.string.Rs) +" "+gst+" GST");
                                                    progressDialog.dismiss();
                                                }
                                                    break;

                                                case 1: if(stCab.equals("Sedan")) {

                                                    if (data.getPeakhoursdata().equals("")) {

                                                        if(stPkg.equals("1 way trip")) {

                                                            amnt = getAmntOneWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin());
                                                        }
                                                        else {
                                                            amnt=getAmntTwoWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin(),data.getMinKms(),data.getAllowedHrs());
                                                        }

                                                        gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;

                                                        tvSurgeMsg.setVisibility(View.GONE);
                                                    } else {

                                                        //start--new logic

                                                        if(stPkg.equals("1 way trip")) {

                                                            amnt = getAmntOneWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin());
                                                        }
                                                        else {
                                                            amnt=getAmntTwoWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin(),data.getMinKms(),data.getAllowedHrs());
                                                        }

                                                        gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;

                                                        tvSurgeMsg.setVisibility(View.GONE);

                                                        //end--new logic

                                                        System.out.println("^^^^^ "+data.getPeakhoursdata());

                                                        String v[] = data.getPeakhoursdata().split(",");

                                                        outerloop:

                                                        for (int l = 0; l < v.length; l++) {
                                                            String w = v[l];
                                                            String y[] = w.split("-");

                                                            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                                                            try {
                                                                Date d1 = format.parse(stTimeForFare);
                                                                Date d2 = format.parse(y[0]);
                                                                Date d3 = format.parse(y[1]);

                                                                String z[] = y[4].split("\\|");

                                                                System.out.println("z.lenght" + z.length);

                                                                if (z.length != 0) {
                                                                    for (int k = 0; k < z.length; k++) {

                                                                        if (z[k].equals("outstation")) {

                                                                            if (isWithinRange(d1, d2, d3)) {

                                                                                if(stPkg.equals("1 way trip")) {

                                                                                    amnt = getAmntOneWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin());
                                                                                }
                                                                                else {
                                                                                    amnt=getAmntTwoWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin(),data.getMinKms(),data.getAllowedHrs());
                                                                                }

                                                                                int a = Integer.parseInt(y[2]) * amnt;
                                                                                int b = a / Integer.parseInt(y[3]);

                                                                                System.out.println("Surge value issss "+b);

                                                                                surgeValue = b;
                                                                                amnt=amnt+(int)surgeValue;
                                                                                tvSurgeMsg.setVisibility(View.VISIBLE);

                                                                                gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;

                                                                                break outerloop;

                                                                            }
                                                                            else {

                                                                                if(stPkg.equals("1 way trip")) {

                                                                                    amnt = getAmntOneWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin());
                                                                                }
                                                                                else {
                                                                                    amnt=getAmntTwoWay(data.getOutsidekmsrate(),data.getBasefare(),data.getDriverAllowance(),data.getExtraRatePerMin(),data.getMinKms(),data.getAllowedHrs());
                                                                                }

                                                                                gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;
                                                                                tvSurgeMsg.setVisibility(View.GONE);

                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                //System.out.println(date);
                                                            } catch (ParseException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                    //int f = (int) Double.parseDouble(data.getTotalfare());

                                                    //btFareEstimate.setText("Approx. Fare Rs. " + String.valueOf(amnt)+" + Rs."+gst+" GST");
                                                    btFareEstimate.setText("Approx. Fare " +getString(R.string.Rs) +" "+ String.valueOf(amnt)+" + "+getString(R.string.Rs) +" "+gst+" GST");
                                                    progressDialog.dismiss();
                                                }
                                                    break;
                                            }


                                        }
                                    }
                                    else {

                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(),response.message(),Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<OutStationPojo>> call, Throwable t) {

                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(),"Internet issue..Please try again!",Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DurationPojo> call, Throwable t) {

                progressDialog.dismiss();
                Toast.makeText(getActivity(),"Please Check Internet  Connection !",Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void showDateTimeSettings()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_date_time, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();

        dp=(DatePicker)dialogView.findViewById(R.id.datePicker);
        tp=(TimePicker)dialogView.findViewById(R.id.simpleTimePicker);
        ok=(Button)dialogView.findViewById(R.id.ok);
        cancel=(Button)dialogView.findViewById(R.id.cancel);

        hr=tp.getCurrentHour();
        min=tp.getCurrentMinute();
        long now = System.currentTimeMillis() - 1000;

        dp.setMinDate(now);
        dp.setMaxDate(now+(1000*60*60*24*7));

        day = dp.getDayOfMonth();
        mnth = dp.getMonth()+1;
        yr = dp.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        dp.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {

                day=dayOfMonth;
                mnth=month+1;
                yr=year;
            }
        });


        tp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {

                hr=i;
                min=i1+1;

            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //System.out.println("************* "+mnth);

                Date d=makeDateGMT(yr,mnth-1,day);
                Date d1=new Date();

                if(d.equals(d1))
                {

                    Calendar datetime = Calendar.getInstance();
                    Calendar c = Calendar.getInstance();
                    datetime.set(Calendar.HOUR_OF_DAY, hr);
                    datetime.set(Calendar.MINUTE, min);

                    if(datetime.getTimeInMillis() > c.getTimeInMillis()){

                        stDate=yr+"-"+mnth+"-"+day;
                        stTime=hr+":"+min;
                        DecimalFormat f=new DecimalFormat("00");
                        String hr1,min1;
                        hr1=f.format(hr);
                        min1=f.format(min);
                        stTimeForFare=hr1+":"+min1+":"+"00";

                        stTime=convert24To12System(hr,min);

                        tvDateTime.setVisibility(View.VISIBLE);
                        tvDateTime.setText(stDate+" "+stTime);
                        tvDateTime.setTextColor(Color.parseColor("#000000"));
                        alertDialog.dismiss();

                        showPackageDetails();

                    }else{
                        Toast.makeText(getActivity(),"Please Choose Time ahead of current time",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    stDate=yr+"-"+mnth+"-"+day;
                    stTime=hr+":"+min;
                    DecimalFormat f=new DecimalFormat("00");
                    String hr1,min1;
                    hr1=f.format(hr);
                    min1=f.format(min);
                    stTimeForFare=hr1+":"+min1+":"+"00";

                    stTime=convert24To12System(hr,min);

                    tvDateTime.setVisibility(View.VISIBLE);

                    tvDateTime.setText(stDate+" "+stTime);
                    tvDateTime.setTextColor(Color.parseColor("#000000"));
                    alertDialog.dismiss();

                    showPackageDetails();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
                getActivity().finish();

            }
        });

    }

    private Date makeDateGMT(int year, int month, int day) {
        GregorianCalendar calendar = new GregorianCalendar();
        // calendar.setTimeZone(TimeZone.getTimeZone());
        calendar.set(year,month, day);
        return calendar.getTime();
    }

    public void showPackageDetails()
    {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_os_packages, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();

        TextView tv1waytrip=(TextView)dialogView.findViewById(R.id.apd_tv_1way);
        TextView tv1rnd=(TextView)dialogView.findViewById(R.id.apd_tv_1rnd);
        TextView tv2rnd=(TextView)dialogView.findViewById(R.id.apd_tv_2rnd);
        TextView tv3rnd=(TextView)dialogView.findViewById(R.id.apd_tv_3rnd);
        TextView tv4rnd=(TextView)dialogView.findViewById(R.id.apd_tv_4rnd);
        TextView tv5rnd=(TextView)dialogView.findViewById(R.id.apd_tv_5rnd);
        TextView tv6rnd=(TextView)dialogView.findViewById(R.id.apd_tv_6rnd);

        tv1waytrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stPkg="1 way trip";
                stPkgNo=1;

                alertDialog.dismiss();
                lLayout.setVisibility(View.VISIBLE);

                tvPkg.setText(stPkg);
                tvPkg.setTextColor(Color.parseColor("#000000"));


                showCabCategory();
            }
        });

        tv1rnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stPkg="1 day  round trip";
                stPkgNo=1;

                alertDialog.dismiss();
                lLayout.setVisibility(View.VISIBLE);

                tvPkg.setText(stPkg);
                tvPkg.setTextColor(Color.parseColor("#000000"));


                showCabCategory();
            }
        });
        tv2rnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stPkg="2 day  round trip";
                stPkgNo=2;

                alertDialog.dismiss();
                lLayout.setVisibility(View.VISIBLE);

                tvPkg.setText(stPkg);
                tvPkg.setTextColor(Color.parseColor("#000000"));


                showCabCategory();
            }
        });
        tv3rnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stPkg="3 day  round trip";
                stPkgNo=3;

                alertDialog.dismiss();
                lLayout.setVisibility(View.VISIBLE);

                tvPkg.setText(stPkg);
                tvPkg.setTextColor(Color.parseColor("#000000"));

                showCabCategory();
            }
        });
        tv4rnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stPkg="4 day  round trip";
                stPkgNo=4;

                alertDialog.dismiss();
                lLayout.setVisibility(View.VISIBLE);

                tvPkg.setText(stPkg);
                tvPkg.setTextColor(Color.parseColor("#000000"));

                showCabCategory();

            }
        });
        tv5rnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stPkg="5 day  round trip";
                stPkgNo=5;

                alertDialog.dismiss();
                lLayout.setVisibility(View.VISIBLE);

                tvPkg.setText(stPkg);
                tvPkg.setTextColor(Color.parseColor("#000000"));

                showCabCategory();
            }
        });
        tv6rnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stPkg="6 day  round trip";
                stPkgNo=6;

                alertDialog.dismiss();
                lLayout.setVisibility(View.VISIBLE);

                tvPkg.setText(stPkg);
                tvPkg.setTextColor(Color.parseColor("#000000"));


                showCabCategory();
            }
        });


    }

    public void showAfterCabCategory()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_os_cab_category, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        final TextView tvMini=(TextView)dialogView.findViewById(R.id.aocc_tv_mini);
        final TextView tvMicra=(TextView)dialogView.findViewById(R.id.aocc_tv_micra);
        final TextView tvSedan=(TextView)dialogView.findViewById(R.id.aocc_tv_sedan);

        tvMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tvMini.setTextColor(getResources().getColor(R.color.colorTextBlue));
                tvCab.setText("Mini");
                tvCab.setTextColor(Color.parseColor("#000000"));
                stCab="Mini";
                alertDialog.dismiss();
                btFareEstimate.setText("Get Fare Estimate");


                btFareEstimate.setEnabled(true);
                btFareEstimate.setClickable(true);
                btFareEstimate.setAlpha(1);
                tvCash.setEnabled(true);
                tvCash.setClickable(true);
                tvCash.setAlpha(1);
                tvWallet.setEnabled(true);
                tvWallet.setClickable(true);
                tvWallet.setAlpha(1);

                getFareEstimate();
                //populateSpinnerValues();
            }
        });

        tvMicra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tvMicra.setTextColor(getResources().getColor(R.color.colorTextBlue));
                tvCab.setText("SUV");
                tvCab.setTextColor(Color.parseColor("#000000"));
                stCab="SUV";
                alertDialog.dismiss();
                btFareEstimate.setText("Get Fare Estimate");



                btFareEstimate.setEnabled(true);
                btFareEstimate.setClickable(true);
                btFareEstimate.setAlpha(1);
                tvCash.setEnabled(true);
                tvCash.setClickable(true);
                tvCash.setAlpha(1);
                tvWallet.setEnabled(true);
                tvWallet.setClickable(true);
                tvWallet.setAlpha(1);

                getFareEstimate();
                //populateSpinnerValues();
            }
        });

        tvSedan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tvSedan.setTextColor(getResources().getColor(R.color.colorTextBlue));
                tvCab.setText("Sedan");
                tvCab.setTextColor(Color.parseColor("#000000"));
                stCab="Sedan";
                alertDialog.dismiss();
                btFareEstimate.setText("Get Fare Estimate");



                btFareEstimate.setEnabled(true);
                btFareEstimate.setClickable(true);
                btFareEstimate.setAlpha(1);
                tvCash.setEnabled(true);
                tvCash.setClickable(true);
                tvCash.setAlpha(1);
                tvWallet.setEnabled(true);
                tvWallet.setClickable(true);
                tvWallet.setAlpha(1);

                getFareEstimate();
                //populateSpinnerValues();
            }
        });


    }

    public void showCabCategory()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_os_cab_category, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        final TextView tvMini=(TextView)dialogView.findViewById(R.id.aocc_tv_mini);
        final TextView tvMicra=(TextView)dialogView.findViewById(R.id.aocc_tv_micra);
        final TextView tvSedan=(TextView)dialogView.findViewById(R.id.aocc_tv_sedan);

        tvMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tvMini.setTextColor(getResources().getColor(R.color.colorTextBlue));
                tvCab.setText("Mini");
                tvCab.setTextColor(Color.parseColor("#000000"));
                stCab="Mini";
                alertDialog.dismiss();
                btFareEstimate.setText("Get Fare Estimate");


                btFareEstimate.setEnabled(true);
                btFareEstimate.setClickable(true);
                btFareEstimate.setAlpha(1);
                tvCash.setEnabled(true);
                tvCash.setClickable(true);
                tvCash.setAlpha(1);
                tvWallet.setEnabled(true);
                tvWallet.setClickable(true);
                tvWallet.setAlpha(1);

                //getFareEstimate();
                populateSpinnerValues();
            }
        });

        tvMicra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tvMicra.setTextColor(getResources().getColor(R.color.colorTextBlue));
                tvCab.setText("SUV");
                tvCab.setTextColor(Color.parseColor("#000000"));
                stCab="SUV";
                alertDialog.dismiss();
                btFareEstimate.setText("Get Fare Estimate");



                btFareEstimate.setEnabled(true);
                btFareEstimate.setClickable(true);
                btFareEstimate.setAlpha(1);
                tvCash.setEnabled(true);
                tvCash.setClickable(true);
                tvCash.setAlpha(1);
                tvWallet.setEnabled(true);
                tvWallet.setClickable(true);
                tvWallet.setAlpha(1);

                //getFareEstimate();
                populateSpinnerValues();
            }
        });

        tvSedan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tvSedan.setTextColor(getResources().getColor(R.color.colorTextBlue));
                tvCab.setText("Sedan");
                tvCab.setTextColor(Color.parseColor("#000000"));
                stCab="Sedan";
                alertDialog.dismiss();
                btFareEstimate.setText("Get Fare Estimate");



                btFareEstimate.setEnabled(true);
                btFareEstimate.setClickable(true);
                btFareEstimate.setAlpha(1);
                tvCash.setEnabled(true);
                tvCash.setClickable(true);
                tvCash.setAlpha(1);
                tvWallet.setEnabled(true);
                tvWallet.setClickable(true);
                tvWallet.setAlpha(1);

                //getFareEstimate();
                populateSpinnerValues();
            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
        // Toast.makeText(getActivity(),"pause",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(getActivity(),"resume",Toast.LENGTH_LONG).show();

        tvWallet.setText("WALLET");

        tvWallet.setTextColor(Color.parseColor("#9e9e9e"));
        tvCash.setTextColor(Color.parseColor("#9e9e9e"));
        stPaymentType="";

        tvAddMoney.setVisibility(View.GONE);
    }

    public static String convert24To12System (int hour, int minute) {
        String time = "";
        String am_pm = "";
        if (hour < 12 ) {
            if (hour == 0) hour = 12;
            am_pm = "AM";
        }
        else {
            if (hour != 12)
                hour-=12;
            am_pm = "PM";
        }
        String h = hour+"", m = minute+"";
        if(h.length() == 1) h = "0"+h;
        if(m.length() == 1) m = "0"+m;
        time = h+":"+m+" "+am_pm;
        return time;
    }

    public static String getCurrentTime() {
        //date output format
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    boolean isWithinRange(Date testDate, Date startDate, Date endDate) {
        return !(testDate.before(startDate) || testDate.after(endDate));
    }

    public int getAmntOneWay(String oskr,String bf,String da,String rtpm)
    {

        System.out.println("stKKKKKKKMMMMMMMMMSSSSSSSSSS"+stKms+":"+stDuration+":"+stHrs);

        if(Double.parseDouble(stKms)<100) {

            amnt = (int) Math.round(stPkgNo * Double.parseDouble(stKms) * 2 * Double.parseDouble(oskr));
            System.out.println("amnt1 "+amnt);
            amnt = (int) Math.round(amnt+Double.parseDouble(bf)+((Double.parseDouble(stDuration))*(Double.parseDouble(rtpm))));
            System.out.println("amnt2 "+amnt);
        }
        else //if(Double.parseDouble(stKms)>100)
        {
            amnt = (int) Math.round(stPkgNo * Double.parseDouble(stKms) * 2 * Double.parseDouble(oskr));
            amnt = (int) Math.round(amnt+Double.parseDouble(bf)+((Double.parseDouble(stDuration))*(Double.parseDouble(rtpm))));
            amnt = (int) Math.round(amnt+Integer.parseInt(da));
        }
        /*else if(Double.parseDouble(stKms)>100&&Integer.parseInt(stKms)<=250)
        {
            stKms="250";
            amnt = (int) Math.round(Double.parseDouble(stKms) * Double.parseDouble(data.getOutsidekmsrate()));

        }
        else {
            amnt = (int) Math.round(Double.parseDouble(stKms) * Double.parseDouble(data.getOutsidekmsrate()));

        }*/
        return amnt;
    }

    public int getAmntTwoWay(String oskr,String bf,String da,String rtpm,String mk,String ah)
    {

        System.out.println("stKKKKKKKMMMMMMMMMSSSSSSSSSS  "+stKms+":"+stDuration+":"+stHrs);

        System.out.println("Minkms "+mk+":"+"Allowe hrs "+ah);

        if((2*Double.parseDouble(stKms))<=200) {

            amnt = (int) Math.round(stPkgNo * 2 * Double.parseDouble(stKms) * Double.parseDouble(oskr));
            amnt = (int) Math.round(amnt+Double.parseDouble(bf)+((Double.parseDouble(stDuration))*(Double.parseDouble(rtpm))));
        }
        else if(((2*Double.parseDouble(stKms))>200)&&((2*Double.parseDouble(stKms))<=Double.parseDouble(mk)))
        {
            amnt = (int) Math.round(stPkgNo * Double.parseDouble(mk) * Double.parseDouble(oskr));

            System.out.println("Allowed hrs "+ah+"Hrs are "+stHrs);

            if(Double.parseDouble(ah)>Double.parseDouble(stHrs)) {

                amnt = (int) Math.round(amnt + Double.parseDouble(bf));
                amnt = Math.round(amnt + Integer.parseInt(da));
            }
            else {
                double v= (Double.parseDouble(stHrs)-(Double.parseDouble(ah)))*60;
                v=v*(Double.parseDouble(rtpm));
                amnt =(int) Math.round(amnt+v+Integer.parseInt(da));
            }


        }
        else {

            System.out.println("stKms in else "+stKms);

            amnt = (int) Math.round(stPkgNo * 2*Double.parseDouble(stKms) * Double.parseDouble(oskr));

            System.out.println("Allowed hrs "+ah+"Hrs are "+stHrs);

            if(Double.parseDouble(ah)>Double.parseDouble(stHrs)) {

                amnt = (int) Math.round(amnt + Double.parseDouble(bf));
                amnt = Math.round(amnt + Integer.parseInt(da));
            }
            else {
                double v= (Double.parseDouble(stHrs)-(Double.parseDouble(ah)))*60;
                v=v*(Double.parseDouble(rtpm));
                amnt =(int) Math.round(amnt+v+Integer.parseInt(da));
            }


        }

        System.out.println("amnt after all "+amnt);


        /*else if(Double.parseDouble(stKms)>100&&Integer.parseInt(stKms)<=250)
        {
            stKms="250";
            amnt = (int) Math.round(Double.parseDouble(stKms) * Double.parseDouble(data.getOutsidekmsrate()));

        }
        else {
            amnt = (int) Math.round(Double.parseDouble(stKms) * Double.parseDouble(data.getOutsidekmsrate()));

        }*/
        return amnt;
    }


    public void oscode()
    {
        /*

                                                                            if(Double.parseDouble(stKms)<100) {

                                                                                amnt = (int) Math.round(2 * stPkgNo * Double.parseDouble(stKms) * Double.parseDouble(data.getOutsidekmsrate()));

                                                                                int a = Integer.parseInt(y[2]) * amnt;
                                                                                int b = a / Integer.parseInt(y[3]);

                                                                                System.out.println("Surge value issss "+b);

                                                                                surgeValue = b;
                                                                                amnt=amnt+(int)surgeValue;

                                                                                gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;

                                                                            }
                                                                            else if(Double.parseDouble(stKms)>100&&Integer.parseInt(stKms)<=250)
                                                                            {
                                                                                stKms="250";
                                                                                amnt = (int) Math.round(Double.parseDouble(stKms) * Double.parseDouble(data.getOutsidekmsrate()));
                                                                                int a = Integer.parseInt(y[2]) * amnt;
                                                                                int b = a / Integer.parseInt(y[3]);

                                                                                System.out.println("Surge value issss "+b);

                                                                                surgeValue = b;

                                                                                amnt=amnt+(int)surgeValue;

                                                                                gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;

                                                                            }
                                                                            else {
                                                                                amnt = (int) Math.round(Double.parseDouble(stKms) * Double.parseDouble(data.getOutsidekmsrate()));

                                                                                int a = Integer.parseInt(y[2]) * amnt;
                                                                                int b = a / Integer.parseInt(y[3]);

                                                                                System.out.println("Surge value issss "+b);

                                                                                surgeValue = b;
                                                                                amnt=amnt+(int)surgeValue;

                                                                                gst = (int) (amnt * Double.parseDouble(data.getServicetax())) / 100;
                                                                            }

                                                                            //tvSurgeCharges.setVisibility(View.VISIBLE);

                                                                            break;*/
    }

    public void populateSpinnerValues()
    {

        Toast.makeText(getActivity(),"Select travel time!",Toast.LENGTH_SHORT).show();

        final List<String> categories = new ArrayList<String>();
        categories.add("--- select travel hr(s) ---");
        categories.add("1 hr");
        categories.add("2 hrs");
        categories.add("3 hrs");
        categories.add("4 hrs");
        categories.add("5 hrs");
        categories.add("6 hrs");
        categories.add("7 hrs");
        categories.add("8 hrs");
        categories.add("9 hrs");
        categories.add("10 hrs");
        categories.add("11 hrs");
        categories.add("12 hrs");
        categories.add("13 hrs");
        categories.add("14 hrs");
        categories.add("15 hrs");
        categories.add("16 hrs");
        categories.add("17 hrs");
        categories.add("18 hrs");
        categories.add("19 hrs");
        categories.add("20 hrs");
        categories.add("21 hrs");
        categories.add("22 hrs");
        categories.add("23 hrs");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(a, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        sp.setAdapter(dataAdapter);


        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                if (position != 0) {

                    item = parent.getItemAtPosition(position).toString();
                    item=item.split(" ")[0];
                    System.out.println("item is 0"+item);
                    stHrs=item;
                    stDuration=String.valueOf(Integer.parseInt(stHrs)*60);

                    System.out.println("Hrs & minute "+stHrs+":"+stDuration);
                    getFareEstimate();
                    ivDateTime.setVisibility(View.VISIBLE);
                    ivCab.setVisibility(View.VISIBLE);
                    ivPkg.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OutStationActivity){
            a=(OutStationActivity) context;
        }

    }
}









