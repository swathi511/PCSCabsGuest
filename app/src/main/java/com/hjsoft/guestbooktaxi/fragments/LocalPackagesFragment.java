package com.hjsoft.guestbooktaxi.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hjsoft.guestbooktaxi.R;
import com.hjsoft.guestbooktaxi.SessionManager;
import com.hjsoft.guestbooktaxi.activity.PaymentActivity;
import com.hjsoft.guestbooktaxi.adapter.DBAdapter;
import com.hjsoft.guestbooktaxi.model.AllRidesPojo;
import com.hjsoft.guestbooktaxi.model.BookCabPojo;
import com.hjsoft.guestbooktaxi.webservices.API;
import com.hjsoft.guestbooktaxi.webservices.RestClient;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 21/4/17.
 */
public class LocalPackagesFragment extends Fragment {


    View rootView;
    TextView tvDateTime,tvPackage,tvCategory,tvFare;
    TextView tvAddKm,tvAddHr;
    Button btBook;
    DatePicker dp;
    TimePicker tp;
    int hr,min,day,mnth,yr;
    Button ok,cancel;
    String stDate,stTime,stPkg="";
    TextView tvDateTimeText;
    ImageView ivDateTime;
    Bundle b;
    String stCabCategory,stTravelPackage,stTravelType="local",stPaymentType="cash",stFare,stLocalFare;
    TextView tvCash,tvWallet,tvAddMoney;
    String slabHr,slabKm,stWalletAmount;
    DBAdapter dbAdapter;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";
    String guestProfileId;
    HashMap<String, String> user;
    SessionManager session;
    String companyId="CMP00001";
    String pickupLat,pickupLong,dropLat,dropLong,pickupLoc,dropLoc;
    String stCity;
    API REST_CLIENT;
    LinearLayout llVisible;
    TextView tvBid,tvCoupon;
    int hour_cal,min_cal;
    String stCoupon="-";



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView= inflater.inflate(R.layout.fragment_local_packages, container, false);

        tvDateTime=(TextView) rootView.findViewById(R.id.flp_tv_datetime);
        tvPackage=(TextView)rootView.findViewById(R.id.flp_tv_pkg);
        tvCategory=(TextView)rootView.findViewById(R.id.flp_tv_cab);
        tvAddHr=(TextView)rootView.findViewById(R.id.flp_tv_add_hr);
        tvAddKm=(TextView)rootView.findViewById(R.id.flp_tv_add_km);
        tvCash=(TextView)rootView.findViewById(R.id.flp_tv_cash);
        tvWallet=(TextView)rootView.findViewById(R.id.flp_tv_wallet);
        tvAddMoney=(TextView)rootView.findViewById(R.id.flp_tv_add_money);
        tvFare=(TextView)rootView.findViewById(R.id.flp_tv_fare);
        tvCoupon=(TextView)rootView.findViewById(R.id.flp_tv_coupon);

        tvDateTimeText=(TextView)rootView.findViewById(R.id.flp_tv_datetime_text);
        ivDateTime=(ImageView)rootView.findViewById(R.id.flp_iv_datetime);

        btBook=(Button) rootView.findViewById(R.id.flp_bt_book);
        btBook.setEnabled(false);
        btBook.setClickable(false);

        llVisible=(LinearLayout)rootView.findViewById(R.id.flp_ll_0);
        tvBid=(TextView)rootView.findViewById(R.id.flp_tv_bid);

        dbAdapter=new DBAdapter(getContext());
        dbAdapter=dbAdapter.open();

        pref = getActivity().getSharedPreferences(PREF_NAME, PRIVATE_MODE);

        editor = pref.edit();
        session=new SessionManager(getActivity());
        user=session.getUserDetails();
        guestProfileId=user.get(SessionManager.KEY_PROFILE_ID);

        pickupLoc=pref.getString("pickup_location",null);
        dropLoc=pref.getString("drop_location",null);
        pickupLat=pref.getString("pickup_lat",null);
        pickupLong=pref.getString("pickup_long",null);
        dropLat=pref.getString("drop_lat",null);
        dropLong=pref.getString("drop_long",null);

        REST_CLIENT= RestClient.get();

        b=getActivity().getIntent().getExtras();

        if(b!=null)
        {
            stTime=b.getString("time");

            showDateTimeSettings();

            stCity=b.getString("city");
            stCabCategory=b.getString("cabCat");
            stTravelPackage=b.getString("pkg");
            stFare=b.getString("fare");

            String localpkg[]=stTravelPackage.split("/");
            String data1=localpkg[0];
            String data2=localpkg[1];

            String stHr[]=data1.split(" ");
            String stHour=stHr[0];

            String stKm[]=data2.trim().split(" ");
            String stKmeter=stKm[0];

            slabHr=stHour;
            slabKm=stKmeter;

            tvPackage.setText(stTravelPackage);
            tvCategory.setText(stCabCategory);
            tvFare.setText(stFare);

            String stFareLocal[]=stFare.split(" ");
            stLocalFare=stFareLocal[1];
        }


        ivDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDateTimeSettings();

            }
        });

        tvCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvCash.setTextColor(Color.parseColor("#0067de"));
                tvWallet.setTextColor(Color.parseColor("#9e9e9e"));
                //tvAddMoney.setVisibility(View.GONE);

                stPaymentType="cash";
                btBook.setEnabled(true);
                btBook.setClickable(true);
                btBook.setAlpha(1);
            }
        });

        tvWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stPaymentType="wallet";
                stWalletAmount=dbAdapter.getWalletAmount();
                tvWallet.setText(getString(R.string.Rs)+" "+stWalletAmount+" WALLET");

                if(Integer.parseInt(stWalletAmount)<Integer.parseInt(stLocalFare))
                {
                    Toast.makeText(getActivity(),"Insufficient balance! Please Add Money.",Toast.LENGTH_SHORT).show();
                    tvAddMoney.setVisibility(View.VISIBLE);
                }
                else {

                    tvWallet.setTextColor(Color.parseColor("#0067de"));
                    tvCash.setTextColor(Color.parseColor("#9e9e9e"));
                    btBook.setEnabled(true);
                    btBook.setClickable(true);
                    btBook.setAlpha(1);
                }
            }
        });

        tvAddMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(getActivity(),PaymentActivity.class);
                i.putExtra("value","yes");
                startActivity(i);
            }
        });

        tvCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

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
                            tvCoupon.setTextColor(Color.parseColor("#4CAF50"));
                            //tvCoupon.setTextColor(Color.parseColor("#FBC02D"));
                            //tvCoupon.setBackgroundColor(Color.parseColor("#FFF176"));

                            tvCoupon.setTextSize(13);
                            alertDialog.dismiss();
                        }

                        //Promcode Not Applicable
                    }
                });*/

            }
        });


        btBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {


                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Please Wait...");
                progressDialog.show();

                //System.out.println(guestProfileId+":"+pickupLat+":"+pickupLong+":"+dropLat+":"+dropLong+":"+pickupLoc+":"+dropLat+":"+
                //dropLoc+":"+companyId+":"+stDate+":"+stTime+":"+stTravelPackage+":"+stCabCategory+":"+stCity+":"+stLocalFare+":"+slabHr+":"+slabKm);

                JsonObject v=new JsonObject();
                v.addProperty("GuestProfileid",guestProfileId);
                v.addProperty("PickupLat",pickupLat);
                v.addProperty("PickupLong",pickupLong);
                v.addProperty("DropLat",dropLat);
                v.addProperty("DropLong",dropLong);
                v.addProperty("PickupLoc",pickupLoc);
                v.addProperty("DropLoc",dropLoc);
                v.addProperty("companyid",companyId);
                v.addProperty("scheduleddate",stDate+" "+stTime);
                v.addProperty("scheduledtime",stTime);
                v.addProperty("traveltype","Packages");
                v.addProperty("travelpackage",stTravelPackage);
                v.addProperty("vehiclecategory",stCabCategory);
                v.addProperty("bookingType","AppBooking");
                v.addProperty("location",stCity);
                v.addProperty("payment_type",stPaymentType);
                v.addProperty("fare_estimate",stLocalFare);
                v.addProperty("slabhours",slabHr);
                v.addProperty("slabkms",slabKm);
                v.addProperty("Promocode",stCoupon);
                //  v.addProperty("Profileid"," ");

                System.out.println("promocode is "+stCoupon);


                // System.out.println("data"+pickupLat+pickupLong+pickupLoc+dropLat+dropLong+dropLoc+stDate+stTime+stTravelPackage+stCabCategory+stCity+stLocalFare+slabHr+slabKm);

                Call<BookCabPojo> call=REST_CLIENT.sendOutstationDetails(v);
                call.enqueue(new Callback<BookCabPojo>() {
                    @Override
                    public void onResponse(Call<BookCabPojo> call, Response<BookCabPojo> response) {

                        BookCabPojo data;

                        if(response.isSuccessful())
                        {
                            data=response.body();
                            System.out.println("********* "+data.getMessage());
                            String d[]=data.getMessage().split(",");
                            progressDialog.dismiss();

                            if(d.length==1) {

                                llVisible.setVisibility(View.VISIBLE);

                                btBook.setVisibility(View.INVISIBLE);
                                tvCash.setEnabled(false);
                                tvCash.setClickable(false);
                                tvWallet.setClickable(false);
                                tvWallet.setEnabled(false);
                                tvCoupon.setClickable(false);
                                tvBid.setText("Booking Id ' " + data.getMessage() + " ' generated");
                                ivDateTime.setClickable(false);
                            }
                            else {

                                tvCoupon.setText("Apply Coupon");
                                tvCoupon.setTypeface(null, Typeface.BOLD);
                                tvCoupon.setTextSize(12);
                                tvCoupon.setTextColor(Color.parseColor("#FF8F00"));
                                stCoupon = "-";
                                Toast.makeText(getActivity(), "Coupon: "+d[1], Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<BookCabPojo> call, Throwable t) {

                        progressDialog.dismiss();

                        Toast.makeText(getActivity(),"Connectivity Error..Please Retry!",Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        return  rootView;
    }

    public void showDateTimeSettings() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_date_time, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        dp = (DatePicker) dialogView.findViewById(R.id.datePicker);
        tp = (TimePicker) dialogView.findViewById(R.id.simpleTimePicker);
        ok = (Button) dialogView.findViewById(R.id.ok);
        cancel=(Button)dialogView.findViewById(R.id.cancel);

        hr = tp.getCurrentHour();
        min = tp.getCurrentMinute();
        long now = System.currentTimeMillis() - 1000;

        dp.setMinDate(now);
        dp.setMaxDate(now + (1000 * 60 * 60 * 24 * 7));

        day = dp.getDayOfMonth();
        mnth = dp.getMonth() + 1;
        yr = dp.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        dp.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {

                day = dayOfMonth;
                mnth = month + 1;
                yr = year;
            }
        });

        tp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {

                hr=i;
                min=i1;

            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               Date d = makeDateGMT(yr, mnth-1, day);
                Date d1 = new Date();

                if (d.equals(d1)) {

                    Calendar datetime = Calendar.getInstance();
                    Calendar c = Calendar.getInstance();
                    datetime.set(Calendar.HOUR_OF_DAY, hr);
                    datetime.set(Calendar.MINUTE, min);

                    if (datetime.getTimeInMillis() > c.getTimeInMillis()) {

                        stDate=yr+"-"+mnth+"-"+day;
                        stTime = hr + ":" + min;

                        stTime=convert24To12System(hr,min);

                        tvDateTime.setVisibility(View.VISIBLE);
                        tvDateTime.setText(stDate + " " + stTime);
                        alertDialog.dismiss();
                        //showPackageDetails();

                    } else {
                        Toast.makeText(getActivity(), "Please Choose Time ahead of current time", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // stDate=day+"/"+mnth+"/"+yr;
                    //stDate = day+"-"+mnth+"-"+yr;
                    stDate=yr+"-"+mnth+"-"+day;
                    stTime = hr + ":" + min;

                    //System.out.println("time before issssssssssssssssssssssssssssss "+stTime);

                    stTime=convert24To12System(hr,min);
                    tvDateTime.setVisibility(View.VISIBLE);

                    tvDateTime.setText(stDate + " " + stTime);
                    tvDateTime.setTextColor(Color.parseColor("#000000"));
                    alertDialog.dismiss();

                    //  showPackageDetails();
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
        final View dialogView = inflater.inflate(R.layout.alert_local_packages, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        Button ok=(Button)dialogView.findViewById(R.id.alp_bt_cancel);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
                showCabCategory();
            }
        });

    }

    public  void showCabCategory()
    {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_local_cab_category, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

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


}
