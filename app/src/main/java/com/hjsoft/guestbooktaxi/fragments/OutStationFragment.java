package com.hjsoft.guestbooktaxi.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.hjsoft.guestbooktaxi.model.BookCabPojo;
import com.hjsoft.guestbooktaxi.model.Distance;
import com.hjsoft.guestbooktaxi.model.Duration;
import com.hjsoft.guestbooktaxi.model.DurationPojo;
import com.hjsoft.guestbooktaxi.model.Element;
import com.hjsoft.guestbooktaxi.model.OutStationPojo;
import com.hjsoft.guestbooktaxi.model.Row;
import com.hjsoft.guestbooktaxi.webservices.API;
import com.hjsoft.guestbooktaxi.webservices.RestClient;

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
    TextView tvAddMoney;

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
        tvAddMoney.setVisibility(View.GONE);

        editor = pref.edit();
        REST_CLIENT= RestClient.get();

        dbAdapter=new DBAdapter(getContext());
        dbAdapter=dbAdapter.open();

        btBook.setClickable(false);
        btBook.setEnabled(false);

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
                btBook.setClickable(true);
                btBook.setEnabled(true);
            }
        });

        tvWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getFareEstimate();

                stWalletAmount=dbAdapter.getWalletAmount();

                tvWallet.setText(getString(R.string.Rs)+" "+stWalletAmount+"  WALLET");

                tvCash.setTextColor(Color.parseColor("#9e9e9e"));
                tvWallet.setTextColor(Color.parseColor("#0067de"));
                stPaymentType="wallet";

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

        btBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (dbAdapter.checkIfPlaceNameExists(dropLoc)) {

                } else {

                    if (!(dropLat.equals("-")) || !(dropLong.equals("-"))) {
                        dbAdapter.insertUserLocation("1", dropLoc, Double.parseDouble(dropLat), Double.parseDouble(dropLong));
                    }
                }

                if(stPkg.equals("")||stCab.equals(""))
                {
                    Toast.makeText(getActivity(),"Please enter all the details",Toast.LENGTH_LONG).show();
                }
                else {

                    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Please Wait ...");
                    progressDialog.show();

                   // System.out.println("fare estimate issssss "+amnt);

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
                    v.addProperty("traveltype","outstation");
                    v.addProperty("travelpackage",stPkg);
                    v.addProperty("vehiclecategory",stCab);
                    v.addProperty("bookingType","AppBooking");
                    v.addProperty("location",city);
                    v.addProperty("payment_type",stPaymentType);
                    v.addProperty("fare_estimate",amnt);
                    v.addProperty("slabhours","-");
                    v.addProperty("slabkms","-");
                    //  v.addProperty("Profileid"," ");

                    System.out.println("data is "+stPkg+":"+stCab+":"+amnt);

                    Call<BookCabPojo> call=REST_CLIENT.sendOutstationDetails(v);
                    call.enqueue(new Callback<BookCabPojo>() {
                        @Override
                        public void onResponse(Call<BookCabPojo> call, Response<BookCabPojo> response) {

                            BookCabPojo data;

                            if(response.isSuccessful())
                            {
                                progressDialog.dismiss();
                                lBookingLayout.setVisibility(View.VISIBLE);
                                data=response.body();
                                btBook.setVisibility(View.INVISIBLE);
                                // tvBookingid.setText("' "+data.getMessage()+" ' is your Booking Id !");
                                // tvBookingid.setVisibility(View.VISIBLE);
                                tvBookingid.setText("Booking Id ' "+data.getMessage()+" ' generated");
                                ivCab.setClickable(false);
                                ivPkg.setClickable(false);
                                ivDateTime.setClickable(false);
                                btFareEstimate.setClickable(false);
                                tvCash.setClickable(false);
                                tvWallet.setClickable(false);
                            }
                        }

                        @Override
                        public void onFailure(Call<BookCabPojo> call, Throwable t) {

                            progressDialog.dismiss();
                            Toast.makeText(getActivity(),"Connectivity Error..Please Retry!",Toast.LENGTH_SHORT).show();
                        }
                    });
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

                        Date d=makeDateGMT(yr,mnth-1,day);
                        Date d1=new Date();

                        if(d.equals(d1))
                        {

                            Calendar datetime = Calendar.getInstance();
                            Calendar c = Calendar.getInstance();
                            datetime.set(Calendar.HOUR_OF_DAY, hr);
                            datetime.set(Calendar.MINUTE, min);

                            if(datetime.getTimeInMillis() > c.getTimeInMillis()){

                                // stDate=day+"/"+mnth+"/"+yr;
                                stDate=yr+"-"+mnth+"-"+day;
                                stTime=hr+":"+min;

                                stTime=convert24To12System(hr,min);

                                tvDateTime.setVisibility(View.VISIBLE);
                                tvDateTime.setText(stDate+" "+stTime);
                                tvDateTime.setTextColor(Color.parseColor("#000000"));
                                alertDialog.dismiss();

                            }else{
                                Toast.makeText(getActivity(),"Please Choose Time ahead of current time!",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            // stDate=day+"/"+mnth+"/"+yr;
                            stDate=yr+"-"+mnth+"-"+day;
                            stTime=hr+":"+min;

                            stTime=convert24To12System(hr,min);

                            tvDateTime.setVisibility(View.VISIBLE);

                            tvDateTime.setText(stDate+" "+stTime);
                            tvDateTime.setTextColor(Color.parseColor("#000000"));
                            alertDialog.dismiss();
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
                    }
                });

            }
        });

        ivCab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showCabCategory();
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
                                    progressDialog.dismiss();

                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

                                    LayoutInflater inflater = getActivity().getLayoutInflater();
                                    final View dialogView = inflater.inflate(R.layout.alert_outstation_cab_details, null);
                                    dialogBuilder.setView(dialogView);

                                    final AlertDialog alertDialog = dialogBuilder.create();
                                    alertDialog.show();

                                    final TextView tvMini=(TextView)dialogView.findViewById(R.id.aocd_tv_mini);
                                    final TextView tvMiniKms=(TextView)dialogView.findViewById(R.id.aocd_tv_mini_kms);
                                    final TextView tvMiniKmsRate=(TextView)dialogView.findViewById(R.id.aocd_tv_mini_kms_rate);


                                    JsonObject v=new JsonObject();
                                    v.addProperty("companyid",companyId);
                                    v.addProperty("location",city);
                                    v.addProperty("traveltype","outstation");
                                    v.addProperty("vehicle_type","All");
                                    v.addProperty("approxkms",stKms);

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

                                                    switch (i)
                                                    {
                                                        case 0:if(stCab.equals("Mini")) {
                                                            amnt = (int) Math.round(2 * stPkgNo * Double.parseDouble(stKms) * Double.parseDouble(data.getOutsidekmsrate()));

                                                            tvMiniKms.setText(stKms);
                                                            tvMiniKmsRate.setText(data.getOutsidekmsrate());
                                                            amnt=amnt+(15*amnt)/100;
                                                            tvMini.setText("Rs. " + String.valueOf(amnt));
                                                            btFareEstimate.setText("Approx. Fare Rs. " + String.valueOf(amnt));
                                                        }
                                                            break;
                                                      case 2:if(stCab.equals("SUV")) {
                                                            amnt = (int) Math.round(2 * stPkgNo * Double.parseDouble(stKms) * Double.parseDouble(data.getOutsidekmsrate()));

                                                            tvMiniKms.setText(stKms);
                                                            tvMiniKmsRate.setText(data.getOutsidekmsrate());
                                                            amnt=amnt+(15*amnt)/100;
                                                            tvMini.setText("Rs. " + String.valueOf(amnt));
                                                            btFareEstimate.setText("Approx. Fare Rs. " + String.valueOf(amnt));

                                                        }
                                                            break;

                                                        case 1: if(stCab.equals("Sedan")) {
                                                            amnt = (int) Math.round(2 * stPkgNo * Double.parseDouble(stKms) * Double.parseDouble(data.getOutsidekmsrate()));

                                                            tvMiniKms.setText(stKms);
                                                            tvMiniKmsRate.setText(data.getOutsidekmsrate());
                                                            amnt=amnt+(15*amnt)/100;
                                                            tvMini.setText("Rs. " + String.valueOf(amnt));
                                                            btFareEstimate.setText("Approx. Fare Rs. " + String.valueOf(amnt));

                                                        }
                                                            break;
                                                    }


                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<List<OutStationPojo>> call, Throwable t) {

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
                            progressDialog.dismiss();

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

                                            switch (i)
                                            {
                                                case 0:if(stCab.equals("Mini")) {
                                                    amnt = (int) Math.round(2 * stPkgNo * Double.parseDouble(stKms) * Double.parseDouble(data.getOutsidekmsrate()));
                                                    // tvMini.setText("Rs. " + String.valueOf(amnt));
                                                    // tvMiniKms.setText(stKms);
                                                    // tvMiniKmsRate.setText(data.getOutsidekmsrate());
                                                    //System.out.println("amnt befor "+amnt);
                                                    amnt=amnt+(15*amnt)/100;
                                                    //System.out.println("amnt after"+amnt);

                                                    btFareEstimate.setText("Approx. Fare Rs. " + String.valueOf(amnt));
                                                }
                                                    break;
                                               case 2:if(stCab.equals("SUV")) {
                                                    amnt = (int) Math.round(2 * stPkgNo * Double.parseDouble(stKms) * Double.parseDouble(data.getOutsidekmsrate()));
                                                    // tvMini.setText("Rs. " + String.valueOf(amnt));
                                                    // tvMiniKms.setText(stKms);
                                                    //  tvMiniKmsRate.setText(data.getOutsidekmsrate());
                                                    //System.out.println("amnt befor "+amnt);
                                                    amnt=amnt+(15*amnt)/100;
                                                   // System.out.println("amnt after"+amnt);

                                                    btFareEstimate.setText("Approx. Fare Rs. " + String.valueOf(amnt));

                                                }
                                                    break;

                                                case 1: if(stCab.equals("Sedan")) {
                                                    amnt = (int) Math.round(2 * stPkgNo * Double.parseDouble(stKms) * Double.parseDouble(data.getOutsidekmsrate()));
                                                    //  tvMini.setText("Rs. " + String.valueOf(amnt));
                                                    //  tvMiniKms.setText(stKms);
                                                    //  tvMiniKmsRate.setText(data.getOutsidekmsrate());
                                                    //System.out.println("amnt befor "+amnt);
                                                    amnt=amnt+(15*amnt)/100;
                                                    //System.out.println("amnt after"+amnt);

                                                    btFareEstimate.setText("Approx. Fare Rs. " + String.valueOf(amnt));


                                                }
                                                    break;
                                            }
                                        }

                                        stWalletAmount=dbAdapter.getWalletAmount();

                                        if(Integer.parseInt(stWalletAmount)<amnt)
                                        {
                                            Toast.makeText(getActivity(),"Insufficient balance! Please Add Money.",Toast.LENGTH_SHORT).show();
                                            btBook.setAlpha(Float.parseFloat("0.5"));
                                            btBook.setClickable(false);
                                            btBook.setEnabled(false);
                                            tvAddMoney.setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            btBook.setAlpha(1);
                                            btBook.setClickable(true);
                                            btBook.setEnabled(true);

                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<OutStationPojo>> call, Throwable t) {

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
}









