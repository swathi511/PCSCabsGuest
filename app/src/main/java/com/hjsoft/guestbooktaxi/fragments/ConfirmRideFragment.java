package com.hjsoft.guestbooktaxi.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hjsoft.guestbooktaxi.Constants;
import com.hjsoft.guestbooktaxi.R;
import com.hjsoft.guestbooktaxi.SessionManager;
import com.hjsoft.guestbooktaxi.activity.HomeActivity;
import com.hjsoft.guestbooktaxi.activity.PaymentActivity;
import com.hjsoft.guestbooktaxi.activity.RideInProgressActivity;
import com.hjsoft.guestbooktaxi.adapter.DBAdapter;
import com.hjsoft.guestbooktaxi.model.BookCabPojo;
import com.hjsoft.guestbooktaxi.model.CabData;
import com.hjsoft.guestbooktaxi.model.CabDist;
import com.hjsoft.guestbooktaxi.model.CabPojo;
import com.hjsoft.guestbooktaxi.model.Distance;
import com.hjsoft.guestbooktaxi.model.Duration;
import com.hjsoft.guestbooktaxi.model.DurationPojo;
import com.hjsoft.guestbooktaxi.model.Element;
import com.hjsoft.guestbooktaxi.model.OutStationPojo;
import com.hjsoft.guestbooktaxi.model.Row;
import com.hjsoft.guestbooktaxi.webservices.API;
import com.hjsoft.guestbooktaxi.webservices.RestClient;
import com.inrista.loggliest.Loggly;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNReconnectionPolicy;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 16/12/16.
 */
public class ConfirmRideFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener,
        GoogleMap.OnMarkerClickListener{

    SupportMapFragment mapFragment;
    GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected boolean mRequestingLocationUpdates;
    final static int REQUEST_LOCATION = 199;
    protected Location mLastLocation;
    double latitude,longitude,current_lat,current_long;
    double locLat,locLng;
    Geocoder geocoder;
    List<Address> addresses;
    LatLng lastLoc,curntloc;
    String complete_address;
    float[] results=new float[3];
    long res=0;
    boolean entered=false;
    String city;
    API REST_CLIENT;
    TextView tvPickup,tvDrop;
    Button btConfirmRide;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";
    String pickupLat,pickupLong,dropLat,dropLong,pickupLoc,dropLoc;
    String guestProfileId;
    String requestId;
    int count=-10;
    Handler handler,hStart;
    Runnable r,rStart;
    boolean waitingForDriver=false;
    String stCategorySelected;
    BottomSheetDialogFragment myBottomSheet;
    ArrayList<CabData> cabDataList=new ArrayList<>();
    ArrayList<CabDist> cabDistList=new ArrayList<>();
    ArrayList<CabDist> sortedCabDistList=new ArrayList<>();
    long smallerDist=0;
    boolean isRequestAccepted;
    int forPositionAccepted;
    int h=0;
    AlertDialog alertDialog;
    boolean accepted=false;
    boolean showingCabsForCategory=false;
    HashMap<String, String> user;
    SessionManager session;
    View rootView;
    DBAdapter dbAdapter;
    TextView tvCash,tvWallet;
    ImageView ivTick;
    boolean isPaymentSelected=false;
    String companyId="CMP00001";
    TextView tvRideEstimate;
    String stLocalPkg,stKms,stTravelType;
    boolean connectivity=true;
    TextView tvAddMoney;
    String stFareEstimate="0";
    String stPaymentMode="none";
    boolean enoughBalance=true;
    String stWalletAmount;
    String travelPackage="-",slabHr="-",slabKm="-",travelType="local";
    String stLocalFare;
    String stFare;
    TextView tvClose,tvFareMsg;
    String stTime="0";
    TextView tvApplyCoupon;
    String stCoupon="-";
    double surgeValue=0.0,gst=0.0;
    double surgeRatio=0.0;
    LinearLayout llFareEstimate;
    TextView tvSurgeCharges;
    boolean first=true;
    LinearLayout llCoupon;
    HomeActivity a;
    //private final static String API_KEY = "3PzQvg.MchECw:Brb2D4FEUuEXMuKs";
    int p=0;
    CabDist c;
    CabData cda;
    PubNub pubnub;
    boolean debugLogs;

   /* private Thread.UncaughtExceptionHandler androidDefaultUEH;

    private Thread.UncaughtExceptionHandler handler1 = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {

            Log.e("TestApplication", "Uncaught exception is: ", ex);
            // log it & phone home.

            String trace = ex.toString() + "\n";

            for (StackTraceElement e1 : ex.getStackTrace()) {
                trace += "\t at " + e1.toString() + "\n";
            }

            Loggly.i("ConfirmRideFragment","Uncaught Exception: "+trace);
            Loggly.forceUpload();

            androidDefaultUEH.uncaughtException(thread, ex);
        }
    };*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        geocoder = new Geocoder(getContext(), Locale.getDefault());
        pref = getActivity().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        debugLogs=pref.getBoolean("debugLogs",true);

        REST_CLIENT= RestClient.get();

        stCategorySelected = getArguments().getString("CategorySelected");
        locLat=getArguments().getDouble("latitude");
        locLng=getArguments().getDouble("longitude");
        city=getArguments().getString("city");
        stLocalPkg=getArguments().getString("localPackage");
        stTravelType=getArguments().getString("travelType");
        stFare=getArguments().getString("fare");

        session=new SessionManager(getActivity());

        user=session.getUserDetails();

        guestProfileId=user.get(SessionManager.KEY_PROFILE_ID);

        dbAdapter=new DBAdapter(getActivity());
        dbAdapter=dbAdapter.open();

        /*androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler1);*/

        if(Build.VERSION.SDK_INT<23)
        {
            establishConnection();
        }
        else
        {
            if(getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
            {
                establishConnection();
            }
            else
            {
                if(shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION))
                {
                    Toast.makeText(getActivity(),"Location Permission is required for this app to run!",Toast.LENGTH_LONG).show();
                }
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
            }
        }

        // showCabsForCategory();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_confirm_ride, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        tvPickup=(TextView)rootView.findViewById(R.id.fcr_tv_pickup);
        tvDrop=(TextView)rootView.findViewById(R.id.fcr_tv_drop);
        btConfirmRide=(Button)rootView.findViewById(R.id.fcr_bt_confirm_ride);
        tvCash=(TextView)rootView.findViewById(R.id.fcr_tv_cash);
        tvWallet=(TextView)rootView.findViewById(R.id.fcr_tv_wallet);
        ivTick=(ImageView)rootView.findViewById(R.id.fcr_iv_tick);
        tvRideEstimate=(TextView)rootView.findViewById(R.id.fcr_tv_ride_estimate);
        tvAddMoney=(TextView)rootView.findViewById(R.id.fcr_tv_add_money);
        tvFareMsg=(TextView)rootView.findViewById(R.id.fcr_tv_fare_msg);
        tvApplyCoupon=(TextView)rootView.findViewById(R.id.fcr_tv_apply_coupon);
        llFareEstimate=(LinearLayout)rootView.findViewById(R.id.fcr_ll_fare_estimate);
        tvSurgeCharges=(TextView)rootView.findViewById(R.id.fcr_tv_surge);
        tvSurgeCharges.setVisibility(View.GONE);
        llCoupon=(LinearLayout)rootView.findViewById(R.id.fcr_ll_coupon);

        pickupLoc=pref.getString("pickup_location",null);
        dropLoc=pref.getString("drop_location",null);
        pickupLat=pref.getString("pickup_lat",null);
        pickupLong=pref.getString("pickup_long",null);
        dropLat=pref.getString("drop_lat",null);
        dropLong=pref.getString("drop_long",null);

        tvPickup.setText(pickupLoc);
        tvDrop.setText(dropLoc);
        ivTick.setVisibility(View.GONE);

        if(stLocalPkg.equals("")) {
            getFareEstimate();
        }
        else {

            String stFareLocal[]=stFare.split(" ");
            stLocalFare=stFareLocal[1];

            /*tvRideEstimate.setEnabled(false);
            tvRideEstimate.setClickable(false);
            tvRideEstimate.setText("Fare Estimate ~ "+getString(R.string.Rs)+" " +stLocalFare);*/
            llFareEstimate.setEnabled(false);
            llFareEstimate.setClickable(false);
            tvFareMsg.setText(getString(R.string.Rs)+" " +stLocalFare+"+ Taxes apply");
            stFareEstimate=stLocalFare;
            travelPackage=stLocalPkg;

            String localpkg[]=stLocalPkg.split("/");
            String data1=localpkg[0];
            String data2=localpkg[1];

            String stHr[]=data1.split(" ");
            String stHour=stHr[0];

            String stKm[]=data2.trim().split(" ");
            String stKmeter=stKm[0];

            slabHr=stHour;
            slabKm=stKmeter;

            travelType="Packages";
            getFareEstimate();
        }

        if(dbAdapter.isWalletPresent()>0)
        {
            stWalletAmount=dbAdapter.getWalletAmount();
        }
        else {
            stWalletAmount = "0";
        }
        tvWallet.setText(getString(R.string.Rs)+" "+stWalletAmount+" WALLET");

        llCoupon.setOnClickListener(new View.OnClickListener() {
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

                            tvApplyCoupon.setText(stCoupon);
                            tvApplyCoupon.setTypeface(null, Typeface.BOLD);
                            tvApplyCoupon.setTextColor(Color.parseColor("#FF6F00"));
                            //tvApplyCoupon.setTextColor(Color.parseColor("#FBC02D"));
                            //tvApplyCoupon.setBackgroundColor(Color.parseColor("#FFF176"));

                            tvApplyCoupon.setTextSize(13);
                            alertDialog.dismiss();
                        }



                        //Promcode Not Applicable
                    }
                });
            }
        });

        tvAddMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stPaymentMode="none";
                Intent i=new Intent(getActivity(), PaymentActivity.class);
                i.putExtra("value","yes");
                startActivity(i);
                //getActivity().finish();
            }
        });

        tvCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stPaymentMode="cash";
                tvCash.setTextColor(Color.parseColor("#000000"));
                tvWallet.setTextColor(Color.parseColor("#a9a8a8"));
                tvAddMoney.setVisibility(View.GONE);
                btConfirmRide.setAlpha(1);
                enoughBalance=true;
            }
        });

        tvWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stPaymentMode = "wallet";

                stWalletAmount=dbAdapter.getWalletAmount();
                tvWallet.setText(getString(R.string.Rs)+" "+stWalletAmount+" WALLET");

                if (Double.parseDouble(stFareEstimate) > Double.parseDouble(stWalletAmount))
                {
                    enoughBalance=false;
                    Toast.makeText(getActivity(),"Insufficient Balance! Please Add Money.",Toast.LENGTH_SHORT).show();
                    tvAddMoney.setVisibility(View.VISIBLE);
                    tvWallet.setTextColor(Color.parseColor("#000000"));
                    tvCash.setTextColor(Color.parseColor("#a9a8a8"));
                    btConfirmRide.setAlpha(Float.parseFloat("0.5"));

                }
                else {
                    enoughBalance=true;
                    tvWallet.setTextColor(Color.parseColor("#000000"));
                    tvCash.setTextColor(Color.parseColor("#a9a8a8"));
                }
            }
        });

        myBottomSheet = MyBottomSheetDialogFragment.newInstance("Modal Bottom Sheet");

        llFareEstimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Getting Fare Estimate ..");
                progressDialog.show();

//                String urlString="https://maps.googleapis.com/maps/api/distancematrix/json?" +
//                        "origins="+pickupLat+","+pickupLong+"&destinations="+dropLat+","+dropLong+"&key=AIzaSyB0z7WOiu8JIcf1fKj2LqiI7MVRmX5ZwR8";

                String urlString="https://maps.googleapis.com/maps/api/distancematrix/json?" +
                        "origins="+pickupLat+","+pickupLong+"&destinations="+dropLat+","+dropLong+"&key=AIzaSyBNlJ8qfN-FCuka8rjh7NEK1rlwWmxG1Pw";

                //System.out.println("url String is  "+urlString);

                Call<DurationPojo> call=REST_CLIENT.getDistanceDetails(urlString);
                call.enqueue(new Callback<DurationPojo>() {
                    @Override
                    public void onResponse(Call<DurationPojo> call, Response<DurationPojo> response) {

                        DurationPojo d;

                        if(response.isSuccessful())
                        {
                            d=response.body();
                            List<Row> r=d.getRows();
                            Row r1;
                            for(int a=0;a<r.size();a++)
                            {
                                r1=r.get(a);
                                List<Element> e=r1.getElements();

                                Element e1;

                                for(int b=0;b<e.size();b++)
                                {
                                    e1=e.get(b);
                                    Distance t1=e1.getDistance();

                                    Duration t2=e1.getDuration();

                                    String stDist=t1.getText();
                                    String stDistDetails[]=stDist.split(" ");
                                    stKms=stDistDetails[0];
                                    progressDialog.dismiss();

                                    stTime=String.valueOf((t2.getValue())/60);

                                    if(stTime==null)
                                    {
                                        stTime="0";
                                    }

                                    //System.out.println("d & t is "+stKms+":"+stTime);
                                }
                            }

                            //System.out.println("distance issss & time  "+stKms+"::"+stTime);

                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

                            LayoutInflater inflater = getActivity().getLayoutInflater();
                            final View dialogView = inflater.inflate(R.layout.alert_ride_estimate, null);
                            dialogBuilder.setView(dialogView);

                            final AlertDialog alertDialog = dialogBuilder.create();
                            alertDialog.show();

                            final TextView tvRideEstimate=(TextView)dialogView.findViewById(R.id.are_tv_fare);
                            final TextView tvBaseFare=(TextView)dialogView.findViewById(R.id.are_tv_base_fare);
                            final TextView tvbSlab=(TextView)dialogView.findViewById(R.id.are_tv_bslab_fare);
                            final TextView tvaSlab=(TextView)dialogView.findViewById(R.id.are_tv_aslab_fare);
                            final TextView tvRideTime=(TextView)dialogView.findViewById(R.id.are_tv_ride_time_fare);
                            final TextView tvMinFare=(TextView)dialogView.findViewById(R.id.are_tv_min_fare);
                            final TextView tvCabCat=(TextView)dialogView.findViewById(R.id.are_tv_cab_cat);
                            final TextView tvSurgeValue=(TextView)dialogView.findViewById(R.id.are_tv_surge);
                            final TextView tvGST=(TextView)dialogView.findViewById(R.id.are_tv_gst);
                            final TextView tvSlabKm=(TextView)dialogView.findViewById(R.id.are_tv_slab_km);


                            JsonObject v=new JsonObject();
                            v.addProperty("companyid",companyId);
                            v.addProperty("location",city);
                            v.addProperty("traveltype","local");
                            v.addProperty("vehicle_type",stCategorySelected);
                            v.addProperty("approxkms",stKms);
                            v.addProperty("duration",stTime);


                            Call<List<OutStationPojo>> call1=REST_CLIENT.getFareEstimate(v);

                            call1.enqueue(new Callback<List<OutStationPojo>>() {
                                @Override
                                public void onResponse(Call<List<OutStationPojo>> call, Response<List<OutStationPojo>> response) {

                                    List<OutStationPojo> dataList;
                                    OutStationPojo data;

                                    if(response.isSuccessful()) {
                                        dataList = response.body();

                                        data = dataList.get(0);

                                        if (data.getTotalfare().equals("")) {

                                        } else {

                                            if(!(data.getPeakhoursdata().equals(""))) {


                                                String v[] = data.getPeakhoursdata().split(",");

                                                outerloop:

                                                for (int i = 0; i < v.length; i++) {
                                                    String w = v[i];
                                                    String y[] = w.split("-");

                                                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                                                    try {
                                                        Date d1 = format.parse(getCurrentTime());
                                                        Date d2 = format.parse(y[0]);
                                                        Date d3 = format.parse(y[1]);

                                                        String z[] = y[4].split("\\|");

                                                        if (z.length != 0) {
                                                            for (int k = 0; k < z.length; k++) {

                                                                //System.out.println("z[]k isssss "+z[k] );

                                                                if (z[k].equals("local")) {

                                                                    if (isWithinRange(d1, d2, d3)) {

                                                                        //System.out.println("is Within Range");

                                                                        double a = (Double.parseDouble(y[2]) * Double.parseDouble(data.getTotalfare()));
                                                                        double b = a / Integer.parseInt(y[3]);

                                                                        surgeValue = b;
                                                                        //surgeRatio = (Double.parseDouble(y[2]) + Double.parseDouble(y[3])) / 100;

                                                                        surgeRatio = Double.parseDouble(y[2]);
                                                                        tvSurgeCharges.setVisibility(View.VISIBLE);
                                                                        tvSurgeValue.setText(y[2]+" - "+y[3]);
                                                                        break outerloop;
                                                                        //break;
                                                                    }
                                                                    else {
                                                                        tvSurgeCharges.setVisibility(View.GONE);
                                                                    }

                                                                }
                                                            }
                                                        } else {

                                                            if (y[4].equals("local")) {
                                                                if (isWithinRange(d1, d2, d3)) {
                                                                    double a = (Double.parseDouble(y[2]) * Double.parseDouble(data.getTotalfare()));
                                                                    double b = a / Integer.parseInt(y[3]);

                                                                    surgeValue = b;
                                                                    //surgeRatio = Double.parseDouble(y[2]) / Double.parseDouble(y[3])/100;

                                                                    surgeRatio = Double.parseDouble(y[2]);
                                                                    tvSurgeCharges.setVisibility(View.VISIBLE);
                                                                    tvSurgeValue.setText(y[2]+" - "+y[3]);
                                                                    break outerloop;
                                                                }
                                                                else {
                                                                    tvSurgeCharges.setVisibility(View.GONE);
                                                                }
                                                            }

                                                        }


                                                        //System.out.println(date);
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }

                                            int f = (int) Double.parseDouble(data.getTotalfare());

                                            tvCabCat.setText(stCategorySelected);
                                            tvBaseFare.setText(getString(R.string.Rs) + " " + data.getBasefare());
                                            tvbSlab.setText(getString(R.string.Rs) + " " + data.getSlabkmrate() + " per km");
                                            tvaSlab.setText(getString(R.string.Rs) + " " + data.getAfterslabkm() + " per km");
                                            tvRideTime.setText(getString(R.string.Rs) + " " + data.getRidetimepermin() + " per min");
                                            tvMinFare.setText(getString(R.string.Rs) + " " + data.getMinimumfare());
                                            tvSlabKm.setText(data.getSlabkm());
                                            int b = (int) (Double.parseDouble(data.getServicetax()) * (f+surgeValue)) / 100;
                                            tvGST.setText(getString(R.string.Rs) + " " + b);

                                            if (String.valueOf(surgeRatio).equals("0.0")) {

                                                //tvSurgeValue.setVisibility(View.GONE);

                                            } else {

                                                //tvSurgeValue.setText(String.valueOf(surgeRatio));
                                            }

                                            //f = f + (15 * f) / 100;

                                            stFareEstimate = String.valueOf((int)(f + surgeValue));

                                            tvRideEstimate.setText(getString(R.string.Rs) + " " + String.valueOf((int)(f + surgeValue)));
                                            tvFareMsg.setText(getString(R.string.Rs) + " " + stFareEstimate + " + " + getString(R.string.Rs) + " " + b + " GST");
                                            gst=b;
                                        }

                                    }
                                }

                                @Override
                                public void onFailure(Call<List<OutStationPojo>> call, Throwable t) {

                                    Toast.makeText(getActivity(),"Connectivity Issue! Please Check.",Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                        else
                        {
                            progressDialog.dismiss();
                        }

                    }

                    @Override
                    public void onFailure(Call<DurationPojo> call, Throwable t) {

                        progressDialog.dismiss();
                        Toast.makeText(getActivity(),"Please Check Internet Connecttion !",Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        btConfirmRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(connectivity) {

                    if(!(stPaymentMode.equals("none"))) {

                        if (enoughBalance) {

                            if (pickupLoc.equals("Unable to get the location details") || pickupLoc.equals("-")) {

                            } else {

                                if (dbAdapter.checkIfPlaceNameExists(dropLoc)) {

                                } else {

                                    if (!(dropLat.equals("-")) || !(dropLong.equals("-"))) {
                                        dbAdapter.insertUserLocation("1", dropLoc, Double.parseDouble(dropLat), Double.parseDouble(dropLong));
                                    }
                                }

                                if (cabDataList.size() != 0) {

                                    //remove commnets if handler is used
                                    // hStart.removeCallbacks(rStart);

                                    btConfirmRide.setEnabled(false);
                                    btConfirmRide.setClickable(false);

                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(view.getContext());

                                    LayoutInflater inflater = getActivity().getLayoutInflater();
                                    final View dialogView = inflater.inflate(R.layout.alert_waiting_count, null);
                                    dialogBuilder.setView(dialogView);

                                    alertDialog = dialogBuilder.create();
                                    alertDialog.show();
                                    alertDialog.setCanceledOnTouchOutside(false);
                                    alertDialog.setCancelable(false);

                                    sendCabRequestStatus();

                                    tvClose = (TextView) dialogView.findViewById(R.id.awc_tv_close);

                                    tvClose.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            alertDialog.dismiss();
                                        }
                                    });

                                    Button cancelRide = (Button) dialogView.findViewById(R.id.awc_bt_cancel_ride);
                                    cancelRide.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            handler.removeCallbacks(r);
                                            alertDialog.dismiss();
                                            //showCabsForCategory();
                                            h = 0;
                                            count = 0;
                                        }
                                    });

                                } else {
                                    Toast.makeText(getActivity(), "No Cabs Found !!", Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), "No Sufficient Balance! Change the Payment mode to proceed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {

                        Toast.makeText(getActivity(), "Please select the payment mode!", Toast.LENGTH_SHORT).show();

                    }
                }
                else{

                    Toast.makeText(getActivity(), "Please Check Internet Connection.", Toast.LENGTH_SHORT).show();
                }

            }
        });


        return  rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==REQUEST_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                establishConnection();

            } else {
                Toast.makeText(getActivity(), "Permission not granted", Toast.LENGTH_LONG).show();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void showCabsForCategory()
    {
        showingCabsForCategory=true;

        /*
        hStart=new Handler();
        rStart=new Runnable() {
            @Override
            public void run() {

                hStart.postDelayed(this,15000);
                getCabsForSelectedCategory();
            }
        };
        hStart.post(rStart);
        */
        getCabsForSelectedCategory();
    }

    public void sendCabRequestStatus(){

        if(sortedCabDistList.size()!=0) {
            sortedCabDistList.clear();
        }

        if(cabDistList.size()!=0)
        {
            cabDistList.clear();
        }

        for(int i=0;i<cabDataList.size();i++)
        {
            CabData c=cabDataList.get(i);
            Location.distanceBetween(locLat,locLng,Double.parseDouble(c.getLatitude()),Double.parseDouble(c.getLongitude()),results);
            res=(long)results[0];
            cabDistList.add(new CabDist(res,i));
        }

        int pos=0;
        int j=cabDistList.size();
        int n=0;

        while(n<j) {

            CabDist c1 = cabDistList.get(0);
            smallerDist = c1.getDist();

            for (int i = 0; i < cabDistList.size(); i++) {

                c = cabDistList.get(i);

                if (smallerDist >=c.getDist()) {
                    smallerDist = c.getDist();
                    pos = c.getPosition();
                    p=i;
                }
            }

            sortedCabDistList.add(new CabDist(smallerDist,pos));
            cabDistList.remove(p);
            n++;
        }

        for(int h=0;h<sortedCabDistList.size();h++)
        {
            CabDist w=sortedCabDistList.get(h);
        }

        sendRequestCheckAcceptance(0);

    }

    public void sendRequestCheckAcceptance(final int p)
    {

        //System.out.println("soretdcab "+sortedCabDistList.size()+"@@pp"+p);
        //System.out.println("cabDatalst "+cabDataList.size());

        if(p<sortedCabDistList.size()) {
            c = sortedCabDistList.get(p);

            //System.out.println("c.getPos "+c.getPosition());
            cda = cabDataList.get(c.getPosition());

            final JsonObject v = new JsonObject();

            v.addProperty("Profileid", cda.getProfileId());
            v.addProperty("GuestProfileid", guestProfileId);
            v.addProperty("PickupLat", pickupLat);
            v.addProperty("PickupLong", pickupLong);
            v.addProperty("DropLat", dropLat);
            v.addProperty("DropLong", dropLong);
            v.addProperty("PickupLoc", pickupLoc);
            v.addProperty("DropLoc", dropLoc);
            v.addProperty("companyid",companyId);
            v.addProperty("travelType",travelType);
            v.addProperty("bookingType","AppBooking");
            v.addProperty("location",city);
            v.addProperty("payment_type",stPaymentMode);
            //v.addProperty("fare_estimate",stFareEstimate);
            v.addProperty("fare_estimate",String.valueOf(Double.parseDouble(stFareEstimate)+gst));
            v.addProperty("travelPackage",travelPackage);
            v.addProperty("slabhours",slabHr);
            v.addProperty("slabkms",slabKm);
            v.addProperty("vehiclecategory",stCategorySelected);
            v.addProperty("Promocode",stCoupon);

            System.out.println("*******************************************");
            System.out.println("fare estimate iss "+String.valueOf(Double.parseDouble(stFareEstimate)+gst));
            System.out.println(cda.getProfileId());
            System.out.println(cda.getVehRegNo() + ":" + cda.getCabCat());
            System.out.println("Driver "+cda.getDriverName());
            System.out.println(guestProfileId);
            System.out.println(pickupLat);
            System.out.println(pickupLong);
            System.out.println(dropLat);
            System.out.println(dropLong);
            System.out.println(pickupLoc);
            System.out.println(dropLoc);
            System.out.println(companyId);
            System.out.println(travelType);
            System.out.println(city);
            System.out.println(stPaymentMode);
            System.out.println(stFareEstimate);
            System.out.println(travelPackage);
            System.out.println(slabHr);
            System.out.println(slabKm);
            System.out.println(stCategorySelected);
            System.out.println(stCoupon);
            System.out.println("****************************************");


            Call<BookCabPojo> call = REST_CLIENT.sendCabRequest(v);
            call.enqueue(new Callback<BookCabPojo>() {
                @Override
                public void onResponse(Call<BookCabPojo> call, Response<BookCabPojo> response) {

                    BookCabPojo msg;
                    if (response.isSuccessful()) {

                        msg = response.body();

                        System.out.println("********** "+msg.getMessage());

                        String d[]=msg.getMessage().split(",");

                        //System.out.println("********** "+d.length);

                        if(d.length==1)
                        {
                            if(msg.getMessage().equals("Access denied"))
                            {
                                alertDialog.dismiss();
                                btConfirmRide.setEnabled(true);
                                btConfirmRide.setClickable(true);
                                Toast.makeText(getActivity(),"Access denied!",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                requestId = msg.getMessage();

                            /*try {

                                System.out.println("profileid is "+cda.getProfileId());

                                initAbly(cda.getProfileId());
                                publishMessage("cab request");

                            }catch (AblyException e)
                            {
                                e.printStackTrace();
                            }*/

                                initPubNub(cda.getProfileId());
                                publish("cab request", cda.getProfileId());

                                final JsonObject v1 = new JsonObject();
                                v1.addProperty("requestid", requestId);
                                v1.addProperty("companyid", companyId);
                                System.out.println("********** Request Id is " + requestId);

                                handler = new Handler();
                                r = new Runnable() {
                                    @Override
                                    public void run() {

                                        count = count + 10;

                                        //previously it was 20 sec
                                        if (count >= 20) {

                                            //channel.unsubscribe();
                                            pubnub.unsubscribe();
                                            pubnub.removeListener(subscribeCallback);

                                            //remove comments if useing handler code & remopve below API call:getCabRequestStatus
                                    /*count = -5;
                                    handler.removeCallbacks(r);
                                    waitingForDriver = false;
                                    isRequestAccepted = false;
                                    h = h + 1;

                                    sendRequestCheckAcceptance(h);
                                    */

                                            Call<BookCabPojo> callStatus = REST_CLIENT.getCabRequestStatus(v1);
                                            callStatus.enqueue(new Callback<BookCabPojo>() {
                                                @Override
                                                public void onResponse(Call<BookCabPojo> call, Response<BookCabPojo> response) {

                                                    BookCabPojo msg;

                                                    if (response.isSuccessful()) {
                                                        msg = response.body();

                                                        if (msg.getMessage().equals("1")) {

                                                            count = -10;
                                                            handler.removeCallbacks(r);
                                                            waitingForDriver = false;
                                                            isRequestAccepted = false;
                                                            h = h + 1;

                                                            JsonObject v = new JsonObject();
                                                            v.addProperty("profileid", cda.getProfileId());
                                                            v.addProperty("requestid", requestId);
                                                            v.addProperty("status", "6");//3 No Response
                                                            v.addProperty("companyid", companyId);

                                                            Call<BookCabPojo> call1 = REST_CLIENT.sendCabAcceptanceStatus(v);
                                                            call1.enqueue(new Callback<BookCabPojo>() {
                                                                @Override
                                                                public void onResponse(Call<BookCabPojo> call, Response<BookCabPojo> response) {


                                                                }

                                                                @Override
                                                                public void onFailure(Call<BookCabPojo> call, Throwable t) {

                                                                }
                                                            });


                                                            sendRequestCheckAcceptance(h);


                                                        } else if (msg.getMessage().equals("3")) {

                                                            alertDialog.dismiss();
                                                            handler.removeCallbacks(r);
                                                            //  Toast.makeText(getContext(), "Cab request accepted by Driver", Toast.LENGTH_LONG).show();
                                                            count = 0;
                                                            waitingForDriver = false;
                                                            isRequestAccepted = true;
                                                            accepted = true;
                                                            forPositionAccepted = p;

                                                            CabData cd = cabDataList.get(c.getPosition());
                                                            //CabData cd = cabDataList.get(forPositionAccepted);
                                                            ArrayList<CabData> cabAcceptedData = new ArrayList<CabData>();
                                                            cabAcceptedData.add(new CabData(cd.getProfileId(), cd.getVehRegNo(),
                                                                    cd.getPhoneNumber(), cd.getCabCat(), cd.getLatitude(), cd.getLongitude(), cd.getDriverName(), cd.getDriverPic(), cd.getDutyPerform()));

                                                            dbAdapter.insertUserRideStatus(requestId, "not arrived");

                                                            Intent i = new Intent(getActivity(), RideInProgressActivity.class);
                                                            i.putExtra("list", cabAcceptedData);
                                                            i.putExtra("requestId", requestId);
                                                            i.putExtra("selectedCategory", stCategorySelected);
                                                            i.putExtra("localPackage", stLocalPkg);
                                                            editor.putString("driverId", cda.getProfileId());
                                                            editor.commit();
                                                            startActivity(i);
                                                            getActivity().finish();
                                                        } else if (msg.getMessage().equals("5")) {

                                                            handler.removeCallbacks(r);

                                                            count = -10;
                                                            waitingForDriver = false;
                                                            isRequestAccepted = false;

                                                            h = h + 1;
                                                            sendRequestCheckAcceptance(h);
                                                            // alertDialog.dismiss();
                                                        } else {

                                                            handler.removeCallbacks(r);

                                                            count = -10;
                                                            waitingForDriver = false;
                                                            isRequestAccepted = false;

                                                            h = h + 1;

                                                            //send Noresponse logic..

                                                   /* JsonObject v = new JsonObject();
                                                    v.addProperty("profileid", cda.getProfileId());
                                                    v.addProperty("requestid", requestId);
                                                    v.addProperty("status", "6");//3 No Response
                                                    v.addProperty("companyid", companyId);

                                                    //System.out.println("req id "+data.getRequestId()+" profile id"+data.getGuestProfileid());

                                                    Call<BookCabPojo> call1=REST_CLIENT.sendCabAcceptanceStatus(v);
                                                    call1.enqueue(new Callback<BookCabPojo>() {
                                                        @Override
                                                        public void onResponse(Call<BookCabPojo> call, Response<BookCabPojo> response) {


                                                        }

                                                        @Override
                                                        public void onFailure(Call<BookCabPojo> call, Throwable t) {

                                                        }
                                                    });*/

                                                            //System.out.println("send req cab acc "+h);
                                                            sendRequestCheckAcceptance(h);


                                                        }

                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<BookCabPojo> call, Throwable t) {

                                                    Toast.makeText(getContext(), "Connectivity Issue! Please check", Toast.LENGTH_LONG).show();
                                                    alertDialog.dismiss();
                                                    handler.removeCallbacks(r);
                                                }
                                            });

                                        } else {

                                            handler.postDelayed(r, 10000);

                                            //System.out.println("checking for responseeeeeeeeeeee");

                                        /*Call<BookCabPojo> callStatus = REST_CLIENT.getCabRequestStatus(v1);
                                        callStatus.enqueue(new Callback<BookCabPojo>() {
                                            @Override
                                            public void onResponse(Call<BookCabPojo> call, Response<BookCabPojo> response) {

                                                BookCabPojo msg;

                                                if (response.isSuccessful()) {
                                                    msg = response.body();

                                                  if (msg.getMessage().equals("3")) {

                                                        alertDialog.dismiss();
                                                        handler.removeCallbacks(r);
                                                        //  Toast.makeText(getContext(), "Cab request accepted by Driver", Toast.LENGTH_LONG).show();
                                                        count = 0;
                                                        waitingForDriver = false;
                                                        isRequestAccepted = true;
                                                        accepted = true;
                                                        forPositionAccepted = p;

                                                        CabData cd = cabDataList.get(c.getPosition());
                                                        //CabData cd = cabDataList.get(forPositionAccepted);
                                                        ArrayList<CabData> cabAcceptedData = new ArrayList<CabData>();
                                                        cabAcceptedData.add(new CabData(cd.getProfileId(), cd.getVehRegNo(),
                                                                cd.getPhoneNumber(), cd.getCabCat(), cd.getLatitude(), cd.getLongitude(), cd.getDriverName(), cd.getDriverPic(), cd.getDutyPerform()));

                                                        dbAdapter.insertUserRideStatus(requestId, "not arrived");

                                                        Intent i = new Intent(getActivity(), RideInProgressActivity.class);
                                                        i.putExtra("list", cabAcceptedData);
                                                        i.putExtra("requestId", requestId);
                                                        i.putExtra("selectedCategory", stCategorySelected);
                                                        i.putExtra("localPackage", stLocalPkg);
                                                        startActivity(i);
                                                        getActivity().finish();
                                                    }else if (msg.getMessage().equals("5")) {

                                                      handler.removeCallbacks(r);

                                                      count = -10;
                                                      waitingForDriver = false;
                                                      isRequestAccepted = false;

                                                      h = h + 1;
                                                      sendRequestCheckAcceptance(h);
                                                      // alertDialog.dismiss();
                                                  }  else  {

                                                      //handler.postDelayed(r, 10000);


                                                    }

                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<BookCabPojo> call, Throwable t) {

                                                Toast.makeText(getContext(), "Connectivity Issue! Please check", Toast.LENGTH_LONG).show();
                                                //alertDialog.dismiss();
                                                //handler.removeCallbacks(r);
                                            }
                                        });*/

                                            //remove comments if handler is used

                                    /*


                                    Call<BookCabPojo> callStatus = REST_CLIENT.getCabRequestStatus(v1);
                                    callStatus.enqueue(new Callback<BookCabPojo>() {
                                        @Override
                                        public void onResponse(Call<BookCabPojo> call, Response<BookCabPojo> response) {

                                            BookCabPojo msg;

                                            if (response.isSuccessful()) {
                                                msg = response.body();
                                                //System.out.println("Request id is......" + v1.get("requestid"));
                                                if (msg.getMessage().equals("0")) {

                                                } else {

                                                    if (msg.getMessage().equals("1")) {

                                                        alertDialog.dismiss();
                                                        handler.removeCallbacks(r);
                                                        //  Toast.makeText(getContext(), "Cab request accepted by Driver", Toast.LENGTH_LONG).show();
                                                        count = 0;
                                                        waitingForDriver = false;
                                                        isRequestAccepted = true;
                                                        accepted=true;
                                                        forPositionAccepted=p;

                                                        CabData cd = cabDataList.get(c.getPosition());
                                                        //CabData cd = cabDataList.get(forPositionAccepted);
                                                        ArrayList<CabData> cabAcceptedData = new ArrayList<CabData>();
                                                        cabAcceptedData.add(new CabData(cd.getProfileId(), cd.getVehRegNo(),
                                                                cd.getPhoneNumber(), cd.getCabCat(), cd.getLatitude(), cd.getLongitude(),cd.getDriverName(),cd.getDriverPic(),cd.getDutyPerform()));

                                                        // System.out.println("accepted data size is " + cabAcceptedData.size());

                                                       //System.out.println("accepted data size is "+cabAcceptedData.size());
                                                       // System.out.println("profile id "+cd.getProfileId());
                                                       // System.out.println("Veh Reg No.."+cd.getVehRegNo());
                                                       // System.out.println("Cab Category.."+cd.getCabCat());
                                                       // System.out.println("Phone Number .."+cd.getPhoneNumber());
                                                       // System.out.println("Latitude .."+cd.getLatitude());
                                                       // System.out.println("Longitude .."+cd.getLongitude());
                                                       // System.out.println("Driver name"+cd.getDriverName());

                                                        dbAdapter.insertUserRideStatus(requestId,"not arrived");

                                                        Intent i=new Intent(getActivity(),RideInProgressActivity.class);
                                                        i.putExtra("list",cabAcceptedData);
                                                        i.putExtra("requestId",requestId);
                                                        i.putExtra("selectedCategory",stCategorySelected);
                                                        i.putExtra("localPackage",stLocalPkg);
                                                        startActivity(i);
                                                        getActivity().finish();
                                                    }
                                                    else {
                                                      //System.out.println("status ;::::::"+msg.getMessage());

                                                        // System.out.println("decline part getting calleddddddddddddddd*******");
                                                        handler.removeCallbacks(r);

                                                        count = -5;
                                                        waitingForDriver = false;
                                                        isRequestAccepted = false;

                                                        h = h + 1;
                                                        sendRequestCheckAcceptance(h);
                                                        // alertDialog.dismiss();
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<BookCabPojo> call, Throwable t) {

                                            Toast.makeText(getContext(), "Connectivity Issue! Please check", Toast.LENGTH_LONG).show();
                                            //alertDialog.dismiss();
                                            //handler.removeCallbacks(r);
                                        }
                                    });
                                    */
                                        }
                                    }
                                };

                                handler.post(r);
                            }


                        }
                        else {

                            alertDialog.dismiss();

                            if(d[0].equals("Access denied"))
                            {
                                btConfirmRide.setEnabled(true);
                                btConfirmRide.setClickable(true);
                                Toast.makeText(getActivity(),msg.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                            else {
                                tvApplyCoupon.setText("Apply");
                                tvApplyCoupon.setTypeface(null, Typeface.BOLD);
                                tvApplyCoupon.setTextSize(12);
                                tvApplyCoupon.setTextColor(Color.parseColor("#FF8F00"));
                                stCoupon = "-";
                                Toast.makeText(getActivity(), "Coupon: " + d[1], Toast.LENGTH_SHORT).show();

                                btConfirmRide.setEnabled(true);
                                btConfirmRide.setClickable(true);
                            }

                        }

                       /* if (msg.getMessage().equals("Promcode Not Applicable")) {

                            alertDialog.dismiss();
                            tvApplyCoupon.setText("Apply Coupon");
                            tvApplyCoupon.setTypeface(null, Typeface.NORMAL);
                            tvApplyCoupon.setTextSize(12);
                            tvApplyCoupon.setTextColor(Color.parseColor("#000000"));
                            stCoupon = "-";
                            Toast.makeText(getActivity(), "Coupon code is not applicable", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //code here
                        }*/
                    }
                    else {

                        alertDialog.dismiss();
                        Toast.makeText(getContext(),response.message()+":"+response.isSuccessful()+":"+response.code(),Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<BookCabPojo> call, Throwable t1) {

                    Toast.makeText(getContext(), "Connectivity Issue! Please check", Toast.LENGTH_LONG).show();
                    alertDialog.dismiss();
                    btConfirmRide.setEnabled(true);
                    btConfirmRide.setClickable(true);

                    String trace = t1.toString() + "\n";

                    for (StackTraceElement e1 : t1.getStackTrace()) {
                        trace += "\t at " + e1.toString() + "\n";
                    }
                    Loggly.i("ConfirmRideFragment",guestProfileId+" "+requestId+"[API failed,UserDetailsToCab/AddUserDetails] "+trace);
                }
            });
        }

        if(!accepted&&(p==sortedCabDistList.size()))
        {

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                    pubnub.unsubscribe();
                    pubnub.removeListener(subscribeCallback);

                    Toast.makeText(getActivity(),"Sorry, No response from driver.\nPlease Try Again !",Toast.LENGTH_LONG).show();
                    h=0;
                    alertDialog.dismiss();
                    btConfirmRide.setEnabled(true);
                    btConfirmRide.setClickable(true);
                    if(mMap!=null)
                    {
                        mMap.clear();
                    }

                    getCabsForSelectedCategory();
                }
            });

            //remove comments if handler is used
            //hStart.post(rStart);
        }
        if(accepted)
        {
            // Toast.makeText(getContext(),"request accepted..",Toast.LENGTH_LONG).show();
            alertDialog.dismiss();
        }

    }

/*
    public int sendCabRequests(){

        System.out.println("sorted list size.. "+sortedCabDistList.size());
        System.out.println("k here is.... "+k);

        if(k<(sortedCabDistList.size()))
        {
            if(sendingCabRequests(k)==true)
            {
                return 1;
            }
            else
            {
                k++;
                sendCabRequests();
            }
        }

        return  0;
    }

    public boolean sendingCabRequests(final int m){

        final JsonObject v=new JsonObject();
        CabDist c=sortedCabDistList.get(m);
        CabData cda=cabDataList.get(c.getPosition());
        System.out.println("pooooooooooooosssssssss"+c.getPosition());
        forPositionAccepted=c.getPosition();

        v.addProperty("Profileid", cda.getProfileId());
        v.addProperty("GuestProfileid", guestProfileId);
        v.addProperty("PickupLat", pickupLat);
        v.addProperty("PickupLong", pickupLong);
        v.addProperty("DropLat", dropLat);
        v.addProperty("DropLong", dropLong);
        v.addProperty("PickupLoc", pickupLoc);
        v.addProperty("DropLoc", dropLoc);

        System.out.println("****************************************");
        System.out.println(cda.getProfileId());
        System.out.println(cda.getVehRegNo()+":"+cda.getCabCat());
        System.out.println(guestProfileId);
        System.out.println(pickupLat);
        System.out.println(pickupLong);
        System.out.println(dropLat);
        System.out.println(dropLong);
        System.out.println(pickupLoc);
        System.out.println(dropLoc);
        System.out.println("****************************************");

        Call<BookCabPojo> call=REST_CLIENT.sendCabRequest(v);
        System.out.println("call being sent.............");
        call.enqueue(new Callback<BookCabPojo>() {

            @Override
            public void onResponse(Call<BookCabPojo> call, Response<BookCabPojo> response) {

                System.out.println("getting response.........");

                BookCabPojo msg;
                if(response.isSuccessful())
                {
                    msg=response.body();
                    requestId=msg.getMessage();
                    System.out.println("request id-----------msg is "+msg.getMessage());
                    isRequestAccepted=findCabRequestStatus();
                }
                else
                {
                    System.out.println("msg is "+response.errorBody());
                }

            }

            @Override
            public void onFailure(Call<BookCabPojo> call, Throwable t) {

                Toast.makeText(getContext(),"Network Error",Toast.LENGTH_LONG).show();
            }
        });

        System.out.println("returrrrrrrrrrrrning herrrrrrrrrrrrrrrrrrrreeeeeeeeeeeeee");
        return isRequestAccepted;
    }

    public boolean findCabRequestStatus()
    {
        waitingForDriver=true;

        final JsonObject v=new JsonObject();
        v.addProperty("requestid",requestId);
        System.out.println("********** request id is "+requestId);


        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {

                count = count + 5;

                System.out.println("count is............................ "+count);

                if (count >= 30) {

                    count = 0;
                    handler.removeCallbacks(r);
                    waitingForDriver = false;
                    isRequestAccepted = false;
                } else {

                    handler.postDelayed(this, 5000);

                    Call<BookCabPojo> callStatus = REST_CLIENT.getCabRequestStatus(v);
                    callStatus.enqueue(new Callback<BookCabPojo>() {
                        @Override
                        public void onResponse(Call<BookCabPojo> call, Response<BookCabPojo> response) {

                            BookCabPojo msg;
                            System.out.println("response is " + response.isSuccessful());

                            if (response.isSuccessful()) {
                                msg = response.body();
                                System.out.println("msg.getMsg() " + msg.getMessage());
                                System.out.println("Request id is......"+v.get("requestid"));
                                if (msg.getMessage().equals("0")) {



                                } else {

                                    if (msg.getMessage().equals("1")) {

                                        alertDialog.dismiss();
                                        handler.removeCallbacks(r);
                                        Toast.makeText(getContext(), "Cab request accepted by Driver", Toast.LENGTH_LONG).show();
                                        count = 0;
                                        waitingForDriver = false;
                                        isRequestAccepted = true;
                                        System.out.println("for position accepted " + forPositionAccepted);

                                        CabData cd = cabDataList.get(forPositionAccepted);
                                        ArrayList<CabData> cabAcceptedData = new ArrayList<CabData>();
                                        cabAcceptedData.add(new CabData(cd.getProfileId(), cd.getVehRegNo(),
                                                cd.getPhoneNumber(), cd.getCabCat(), cd.getLatitude(), cd.getLongitude()));

                                        System.out.println("accepted data size is "+cabAcceptedData.size());
                                        System.out.println("profile id "+cd.getProfileId());
                                        System.out.println("Veh Reg No.."+cd.getVehRegNo());
                                        System.out.println("Cab Category.."+cd.getCabCat());
                                        System.out.println("Phone Number .."+cd.getPhoneNumber());
                                        System.out.println("Latitude .."+cd.getLatitude());
                                        System.out.println("Longitude .."+cd.getLongitude());

                                        /*

                                        Fragment frag = new TrackRideFragment();
                                        Bundle args = new Bundle();
                                        args.putSerializable("ad",cabAcceptedData);

                                        frag.setArguments(args);
                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.content_frame, frag);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();*/

    // checkForArrivalNotification();
    //   */
    /*

                                    } else {

                                        handler.removeCallbacks(r);

                                        count = 0;
                                        waitingForDriver = false;
                                        isRequestAccepted = false;
                                        // alertDialog.dismiss();
                                    }
                                }
                            }

                        }


                        @Override
                        public void onFailure(Call<BookCabPojo> call, Throwable t) {

                            Toast.makeText(getContext(), "Check Internet Connection", Toast.LENGTH_LONG).show();

                        }
                    });
                }

                System.out.println("in handler.................................................................");
            }
        };

        handler.post(r);

        return isRequestAccepted;
    }


    /*
    public void getCabsForCategory()
    {

        System.out.println("gettttting caaaaaaaaaaaaaaabs**** ^^^^^^^^^^^^^^^^^^^^^ fooooooooooor caaaaaaaaaaaaaaategory");

        System.out.println(city+":"+current_lat+":"+current_long+":"+stCategorySelected);


        cabDataList.clear();

        Call<List<CabPojo>> call=REST_CLIENT.getAllCabs(city,String.valueOf(current_lat),String.valueOf(current_long),stCategorySelected);
        call.enqueue(new Callback<List<CabPojo>>() {
            @Override
            public void onResponse(Call<List<CabPojo>> call, Response<List<CabPojo>> response) {

                if(myBottomSheet.isAdded())
                {
                    myBottomSheet.dismiss();

                }

                List<CabPojo> cabs;
                CabPojo cabData;

                if(response.isSuccessful())
                {
                    cabs=response.body();

                    for(int i=0;i<cabs.size();i++)
                    {
                        cabData=cabs.get(i);
                        cabDataList.add(new CabData(cabData.getProfileid(),cabData.getVehichleregno(),cabData.getPhonenumber(),cabData.getVehicleCategory(),cabData.getLatitude(), cabData.getLongitude()));
                        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");

                    }
                    //showCabsOnMap();
                }
                else
                {

                }

            }

            @Override
            public void onFailure(Call<List<CabPojo>> call, Throwable t) {

                System.out.println("????????????????????????///////////////////////////////////");

                // Toast.makeText(getContext(),"Check Internet Connection",Toast.LENGTH_LONG).show();


                if(myBottomSheet.isAdded())
                {
                    System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                    //return;

                }
                else
                {
                    System.out.println("displaying bottom sheet...");
                    myBottomSheet.show(getChildFragmentManager(), myBottomSheet.getTag());
                }

            }
        });
    }*/

    public void showCabsOnMap(){

        if(mMap!=null)
        {
            mMap.clear();
        }

        if(mMap!=null)
        {

            showMapWithPickupDropPoints();

            for(int i=0;i<cabDataList.size();i++) {
                CabData c = cabDataList.get(i);
                LatLng ll2 = new LatLng(Double.parseDouble(c.getLatitude()), Double.parseDouble(c.getLongitude()));


                if(stCategorySelected.equals("Mini")||stCategorySelected.equals("Micra"))
                {
                    mMap.addMarker(new MarkerOptions().position(ll2).icon(BitmapDescriptorFactory.fromResource(R.drawable.mini_15)));
                }
                else {
                    mMap.addMarker(new MarkerOptions().position(ll2).icon(BitmapDescriptorFactory.fromResource(R.drawable.prime_15)));
                }

                //mMap.addMarker(new MarkerOptions().position(ll2)
                // .icon(BitmapDescriptorFactory.fromResource(R.drawable.cab_icon)));
            }

        }
    }

    public void establishConnection(){

        buildGoogleApiClient();
        buildLocationSettingsRequest();
        entered=true;
        //showCabsForCategory();
    }

    @Override
    public void onStart() {

        super.onStart();

        if(Build.VERSION.SDK_INT>=23)
        {
            if(!entered)
            {
            }
            else
            {
                mGoogleApiClient.connect();
                //super.onStart();
            }
        }
        else
        {
            mGoogleApiClient.connect();
            // super.onStart();
        }

        if(entered) {

            //remove comments if handler is used
            /*
            if (hStart != null) {
                hStart.post(rStart);
            }
            */

        }
    }

    @Override
    public void onStop() {

        if(mGoogleApiClient!=null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();

        if(entered) {

            //remove comments if handler is used
            /*
            if (hStart != null) {
                hStart.removeCallbacks(rStart);
            }
            */
        }


    }

    @Override
    public void onPause() {
        super.onPause();

        if(mGoogleApiClient!=null) {
            if (mGoogleApiClient.isConnected()) {
                stopLocationUpdates();

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(entered)
        {
            if(mGoogleApiClient.isConnecting()||mGoogleApiClient.isConnected())
            {
            }
            else {
                mGoogleApiClient.connect();
            }
        }

        if(mGoogleApiClient!=null) {

            if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
                startLocationUpdates();
            }
        }

        //stPaymentMode="cash";
        if(stPaymentMode.equals("cash"))
        {
            tvCash.setTextColor(Color.parseColor("#000000"));
            tvWallet.setTextColor(Color.parseColor("#9e9e9e"));
        }
        else if(stPaymentMode.equals("wallet"))
        {
            tvCash.setTextColor(Color.parseColor("#9e9e9e"));
            tvWallet.setTextColor(Color.parseColor("#000000"));
        }
        else {
            stPaymentMode="none";
            tvCash.setTextColor(Color.parseColor("#9e9e9e"));
            tvWallet.setTextColor(Color.parseColor("#9e9e9e"));
        }
        stWalletAmount=dbAdapter.getWalletAmount();
        tvWallet.setText(getString(R.string.Rs)+" "+stWalletAmount+" WALLET");
        tvAddMoney.setVisibility(View.GONE);
        btConfirmRide.setAlpha(1);
        enoughBalance=true;
    }


    protected synchronized void buildGoogleApiClient() {

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();
    }

    protected void buildLocationSettingsRequest() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {

                    case LocationSettingsStatusCodes.SUCCESS:
                        //Location Settings Satisfied
                        startLocationUpdates();
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to resolve it
                        break;
                }
            }
        });
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(45000);//45 sec
        mLocationRequest.setFastestInterval(30000);//5 sec
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);
            mRequestingLocationUpdates=true;
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    protected void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mapFragment.getMapAsync(this);
        if (mLastLocation == null) {
            try {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    latitude = mLastLocation.getLatitude();
                    longitude = mLastLocation.getLongitude();
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        if(mRequestingLocationUpdates)
        {
            startLocationUpdates();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if(mLastLocation!=null) {

            current_lat=location.getLatitude();
            current_long=location.getLongitude();
            if (current_lat != 0 && current_long != 0) {
                curntloc = new LatLng(current_lat, current_long);
                //  Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(), current_lat, current_long, results);
                location.getAccuracy();
                // res = res + (long) results[0];

                /*

                try {
                    addresses = geocoder.getFromLocation(current_lat, current_long, 1);
                    int l=addresses.get(0).getMaxAddressLineIndex();
                    String add="",add1="",add2="";

                    for(int k=0;k<l;k++)
                    {
                        add=add+addresses.get(0).getAddressLine(k);
                        add=add+" ";

                        if(k==1)
                        {
                            add1=addresses.get(0).getAddressLine(k);
                        }
                        if(k==2)
                        {
                            add2=addresses.get(0).getAddressLine(k);
                        }
                    }
                    String address = addresses.get(0).getAddressLine(0);
                    String add_1=addresses.get(0).getAddressLine(1);//current place name eg:Nagendra nagar,Hyderabad
                    String add_2=addresses.get(0).getAddressLine(2);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    //complete_address=address+" "+add1+" "+add2;
                    //tvCloc.setText(add);
                    complete_address=add;
                }
                catch(IOException e) {
                    e.printStackTrace();
                    //complete_address="No response from server";
                    // tvCloc.setText(complete_address);

                    // showCabsOnMap();
                }
                //  getCabsForCategory();
                */
            }
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        lastLoc = new LatLng(latitude, longitude);

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);

        // mMap.addMarker(new MarkerOptions().position(lastLoc)
        // .title("Current Location")
        //  .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_image)));
       /* mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(lastLoc)
                .zoom(14)
                //.bearing(30).tilt(45)
                .build()));*/

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        try {
            mMap.setMyLocationEnabled(true);
        }catch (SecurityException e){e.printStackTrace();}

        // mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());


        try{
            addresses = geocoder.getFromLocation(latitude,longitude, 1);
            if(addresses.size()!=0) {
                int l = addresses.get(0).getMaxAddressLineIndex();
                String add = "", add1 = "", add2 = "";

                for (int k = 0; k < l; k++) {
                    add = add + addresses.get(0).getAddressLine(k);
                    add = add + " ";

                    if (k == 1) {
                        add1 = addresses.get(0).getAddressLine(k);
                    }
                    if (k == 2) {
                        add2 = addresses.get(0).getAddressLine(k);
                    }
                }
                //  city = addresses.get(0).getLocality();
            }
        }
        catch (Exception e){e.printStackTrace();}

        if(first) {

            showMapWithPickupDropPoints();
            getCabsForSelectedCategory();
            first=false;
        }
    }

    public void showMapWithPickupDropPoints(){

        if(mMap!=null) {

            LatLng p=new LatLng(Double.parseDouble(pickupLat),Double.parseDouble(pickupLong));

            if(!(dropLat.equals("-"))||!(dropLong.equals("-"))) {
                LatLng d = new LatLng(Double.parseDouble(dropLat), Double.parseDouble(dropLong));
                mMap.addMarker(new MarkerOptions().position(d)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_pink)));
            }

            mMap.addMarker(new MarkerOptions().position(p)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));

            if (mapFragment != null) {
                final View mapView = mapFragment.getView();

                if (mapView != null) {

                    if (mapView.getViewTreeObserver().isAlive()) {
                        mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @SuppressLint("NewApi")
                            @Override
                            public void onGlobalLayout() {

                                if (!(dropLat.equals("-")) || !(dropLong.equals("-"))) {

                                    LatLngBounds.Builder bld = new LatLngBounds.Builder();

                                    for(int i=0;i<cabDataList.size();i++) {

                                        CabData c = cabDataList.get(i);
                                        LatLng ll2 = new LatLng(Double.parseDouble(c.getLatitude()), Double.parseDouble(c.getLongitude()));

                                        bld.include(ll2);

                                        /*if (stCategorySelected.equals("Mini") || stCategorySelected.equals("Micra")) {
                                            mMap.addMarker(new MarkerOptions().position(ll2).icon(BitmapDescriptorFactory.fromResource(R.drawable.mini_20)));
                                        } else {
                                            mMap.addMarker(new MarkerOptions().position(ll2).icon(BitmapDescriptorFactory.fromResource(R.drawable.prime_20)));
                                        }*/
                                    }



                                    LatLng ll = new LatLng(Double.parseDouble(pickupLat), Double.parseDouble(pickupLong));
                                    bld.include(ll);


                                    LatLng ll1 = new LatLng(Double.parseDouble(dropLat), Double.parseDouble(dropLong));
                                    bld.include(ll1);

                                    LatLngBounds bounds = bld.build();
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, mapView.getWidth(), mapView.getHeight(), 120));
                                    mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                }
                                else {


                                    LatLng ll = new LatLng(Double.parseDouble(pickupLat), Double.parseDouble(pickupLong));

                                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                            .target(ll)
                                            .zoom(Float.parseFloat("12.8"))
                                            //.bearing(30).tilt(45)
                                            .build()));
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    public void getCabsForSelectedCategory(){

        //System.out.println("API calleddddddddddddddddd");
        cabDataList.clear();

        Call<List<CabPojo>> call=REST_CLIENT.getAllCabs(city,String.valueOf(locLat),String.valueOf(locLng),stCategorySelected,companyId);
        call.enqueue(new Callback<List<CabPojo>>() {
            @Override
            public void onResponse(Call<List<CabPojo>> call, Response<List<CabPojo>> response) {

                connectivity=true;

               /* if(myBottomSheet!=null) {

                    if (myBottomSheet.isAdded()) {
                        myBottomSheet.dismiss();

                    }
                }*/

                List<CabPojo> cabs;
                CabPojo cabData;

                if(response.isSuccessful())
                {
                    cabs=response.body();
                    //cabDataList.clear();

                    for(int i=0;i<cabs.size();i++)
                    {
                        cabData=cabs.get(i);

                        if(cabData.getDutyPerform().equals("Local")||cabData.getDutyPerform().equals("Both")) {

                            cabDataList.add(new CabData(cabData.getProfileid(), cabData.getVehichleregno(), cabData.getPhonenumber(), cabData.getVehicleCategory(), cabData.getLatitude(), cabData.getLongitude(), cabData.getDriverName(), cabData.getDriverPic(), cabData.getDutyPerform()));
                        }

                    }
                    showCabsOnMap();
                }
                else
                {

                }

            }

            @Override
            public void onFailure(Call<List<CabPojo>> call, Throwable t) {

                connectivity=false;

            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();

        //remove comments if handler is used
        //hStart.removeCallbacks(rStart);

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if(pubnub!=null)
        {
            pubnub.removeListener(subscribeCallback);
            pubnub.unsubscribe();
        }


        //t=null;
    }

    public String  getFareEstimate()
    {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Getting Fare Estimate ..");
        progressDialog.show();

        // AIzaSyC4Ccgq_w6OhyF6IVblH3KByt5tKuJNtdM
//        String urlString="https://maps.googleapis.com/maps/api/distancematrix/json?" +
//                "origins="+pickupLat+","+pickupLong+"&destinations="+dropLat+","+dropLong+"&key=AIzaSyC4Ccgq_w6OhyF6IVblH3KByt5tKuJNtdM";

        if(travelType.equals("local")) {

            String urlString = "https://maps.googleapis.com/maps/api/distancematrix/json?" +
                    "origins=" + pickupLat + "," + pickupLong + "&destinations=" + dropLat + "," + dropLong + "&key=AIzaSyBNlJ8qfN-FCuka8rjh7NEK1rlwWmxG1Pw";

            Call<DurationPojo> call = REST_CLIENT.getDistanceDetails(urlString);
            call.enqueue(new Callback<DurationPojo>() {
                @Override
                public void onResponse(Call<DurationPojo> call, Response<DurationPojo> response) {

                    DurationPojo d;

                    if (response.isSuccessful()) {
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

                                stTime = String.valueOf((t2.getValue()) / 60);

                                if(stTime==null)
                                {
                                    stTime="0";
                                }

                                //System.out.println("d & t is " + stKms + ":" + stTime);

                            }
                        }

                    /*

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.alert_ride_estimate, null);
                    dialogBuilder.setView(dialogView);

                    final AlertDialog alertDialog = dialogBuilder.create();
                    alertDialog.show();

                    final TextView tvRideEstimate=(TextView)dialogView.findViewById(R.id.are_tv_fare);
                    final TextView tvBaseFare=(TextView)dialogView.findViewById(R.id.are_tv_base_fare);
                    final TextView tvbSlab=(TextView)dialogView.findViewById(R.id.are_tv_bslab_fare);
                    final TextView tvaSlab=(TextView)dialogView.findViewById(R.id.are_tv_aslab_fare);
                    final TextView tvRideTime=(TextView)dialogView.findViewById(R.id.are_tv_ride_time_fare);
                    final TextView tvMinFare=(TextView)dialogView.findViewById(R.id.are_tv_min_fare);
                    final TextView tvCabCat=(TextView)dialogView.findViewById(R.id.are_tv_cab_cat);

*/


                        JsonObject v = new JsonObject();
                        v.addProperty("companyid", companyId);
                        v.addProperty("location", city);
                        v.addProperty("traveltype", "local");
                        v.addProperty("vehicle_type", stCategorySelected);
                        v.addProperty("approxkms", stKms);
                        v.addProperty("duration", stTime);

                        Call<List<OutStationPojo>> call1 = REST_CLIENT.getFareEstimate(v);

                        call1.enqueue(new Callback<List<OutStationPojo>>() {
                            @Override
                            public void onResponse(Call<List<OutStationPojo>> call, Response<List<OutStationPojo>> response) {

                                List<OutStationPojo> dataList;
                                OutStationPojo data;

                                progressDialog.dismiss();

                                if (response.isSuccessful()) {
                                    dataList = response.body();

                                    data = dataList.get(0);

                                    if (data.getTotalfare().equals("")) {


                                    } else {
                                        if (data.getPeakhoursdata().equals("")) {

                                        } else {

                                            //System.out.println("^^^^^ "+data.getPeakhoursdata());

                                            String v[] = data.getPeakhoursdata().split(",");

                                            //System.out.println("v.lenght "+v.length);

                                            outerloop:

                                            for (int i = 0; i < v.length; i++) {
                                                String w = v[i];
                                                String y[] = w.split("-");

                                                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                                                try {


                                                    Date d1 = format.parse(getCurrentTime());
                                                    //Date d1 = format.parse("08:00:00");
                                                    Date d2 = format.parse(y[0]);
                                                    Date d3 = format.parse(y[1]);

                                                    //System.out.println("y[4]"+y[4]);

                                                    String z[] = y[4].split("\\|");

                                                    //System.out.println("z.lenght"+z.length);

                                                    if (z.length != 0) {
                                                        for (int k = 0; k < z.length; k++) {

                                                            //System.out.println("z[k]"+z[k]);

                                                            if (z[k].equals("local")) {

                                                                if (isWithinRange(d1, d2, d3)) {
                                                                    double a = (Double.parseDouble(y[2]) * Double.parseDouble(data.getTotalfare()));
                                                                    double b = a / Integer.parseInt(y[3]);

                                                                    surgeValue = b;
                                                                    //surgeRatio = (Double.parseDouble(y[2]) + Double.parseDouble(y[3])) / 100;

                                                                    tvSurgeCharges.setVisibility(View.VISIBLE);

                                                                    break outerloop;
                                                                    //break;
                                                                }
                                                                else {
                                                                    tvSurgeCharges.setVisibility(View.GONE);
                                                                }

                                                            }
                                                        }
                                                    } else {

                                                        if (y[4].equals("local")) {
                                                            if (isWithinRange(d1, d2, d3)) {

                                                                //System.out.println("inside eeeeeeeeee");


                                                                double a = (Double.parseDouble(y[2]) * Double.parseDouble(data.getTotalfare()));
                                                                double b = a / Integer.parseInt(y[3]);

                                                                surgeValue = b;
                                                                //surgeRatio = Double.parseDouble(y[2]) / Double.parseDouble(y[3])/100;

                                                                tvSurgeCharges.setVisibility(View.VISIBLE);
                                                                break outerloop;
                                                            }
                                                            else {
                                                                tvSurgeCharges.setVisibility(View.GONE);
                                                            }


                                                        }

                                                    }


                                                    //System.out.println(date);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                           /* for (int i = 0; i < v.length; i++) {
                                                String w = v[i];
                                                String y[] = w.split("-");

                                                System.out.println("y[4] "+y[4]);

                                                if(y[4].equals("local")) {

                                                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                                                    try {
                                                        Date d1 = format.parse(getCurrentTime());
                                                        Date d2 = format.parse(y[0]);
                                                        Date d3 = format.parse(y[1]);

                                                        if (isWithinRange(d1, d2, d3)) {
                                                            int a = Integer.parseInt(y[2]) * Integer.parseInt(data.getTotalfare());
                                                            int b = a / Integer.parseInt(y[3]);

                                                            surgeValue = b;

                                                            break;
                                                        }
                                                        //System.out.println(date);
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }*/
                                        }

                                        int f = (int) Double.parseDouble(data.getTotalfare());
                                        //f = f + (15 * f) / 100;

                                        stFareEstimate = String.valueOf((int)(f + surgeValue));
                                        //tvRideEstimate.setText("Fare Estimate ~  " + getString(R.string.Rs) + " " + stFareEstimate );
                                        //tvFareMsg.setText("  +  "+getString(R.string.Rs)+" "+data.getServicetax()+"  GST");

                                        int b = (int) (Double.parseDouble(data.getServicetax()) * (f+surgeValue)) / 100;
                                        tvFareMsg.setText(getString(R.string.Rs) + " " + stFareEstimate + " + " + getString(R.string.Rs) + " " + b + " GST");
                                        //tvFareMsg.setText(" + "+getString(R.string.Rs) + " " +b+" GST");
                                        gst=b;
                                    }

                                }

                            }

                            @Override
                            public void onFailure(Call<List<OutStationPojo>> call, Throwable t) {

                                progressDialog.dismiss();

                                Toast.makeText(getActivity(), "Connectivity Issue! Please Check.", Toast.LENGTH_SHORT).show();

                            }
                        });
                    } else {
                        progressDialog.dismiss();
                    }

                }

                @Override
                public void onFailure(Call<DurationPojo> call, Throwable t) {

                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Please Check Internet Connection !", Toast.LENGTH_SHORT).show();

                }
            });
        }
        else {

            tvRideEstimate.setText("FARE ESTIMATE");

            JsonObject v = new JsonObject();
            v.addProperty("companyid", companyId);
            v.addProperty("location", city);
            v.addProperty("traveltype", "local");
            v.addProperty("vehicle_type", "All");
            v.addProperty("approxkms", 0);
            v.addProperty("duration", 0);

            Call<List<OutStationPojo>> call1 = REST_CLIENT.getFareEstimate(v);

            call1.enqueue(new Callback<List<OutStationPojo>>() {
                @Override
                public void onResponse(Call<List<OutStationPojo>> call, Response<List<OutStationPojo>> response) {

                    List<OutStationPojo> dataList;
                    OutStationPojo data;

                    progressDialog.dismiss();

                    if (response.isSuccessful()) {
                        dataList = response.body();

                        for(int k=0;k<dataList.size();k++) {

                            //System.out.println(" k & data size "+k+"::"+dataList.size()+stCategorySelected);

                            data = dataList.get(k);

                            switch (k) {
                                case 0:
                                    if (stCategorySelected.equals("Mini")) {

//                                        if (data.getTotalfare().equals("")) {
//
//
//                                        } else {

                                        if (data.getPeakhoursdata().equals("")) {

                                        } else {

                                            //System.out.println("^^^^^ " + data.getPeakhoursdata());

                                            String v[] = data.getPeakhoursdata().split(",");

                                            outerloop:

                                            for (int i = 0; i < v.length; i++) {
                                                String w = v[i];
                                                String y[] = w.split("-");

                                                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                                                try {
                                                    Date d1 = format.parse(getCurrentTime());
                                                    //Date d1=format.parse("04:00:00");
                                                    Date d2 = format.parse(y[0]);
                                                    Date d3 = format.parse(y[1]);

                                                    // System.out.println("y[4]" + y[4]);

                                                    String z[] = y[4].split("\\|");

                                                    //System.out.println("z.lenght" + z.length);

                                                    if (z.length != 0) {

                                                        for (int l = 0;l < z.length; l++) {

                                                            //System.out.println("z[k]" + z[l]);

                                                            if (z[l].equals("packages")) {

                                                                //System.out.println("inside equals ....");

                                                                if (isWithinRange(d1, d2, d3)) {

                                                                    //System.out.println("is within range ....");

                                                                    double a = (Double.parseDouble(y[2]) * Double.parseDouble(stLocalFare));
                                                                    double b = a / Integer.parseInt(y[3]);

                                                                    surgeValue = b;
                                                                    //surgeRatio = (Double.parseDouble(y[2]) + Double.parseDouble(y[3])) / 100;
                                                                    surgeRatio = Double.parseDouble(y[2]);

                                                                    tvSurgeCharges.setVisibility(View.VISIBLE);

                                                                    break outerloop;
                                                                    //break;
                                                                }
                                                                else {
                                                                    tvSurgeCharges.setVisibility(View.GONE);
                                                                }
                                                            }
                                                        }
                                                    } else {

                                                        if (y[4].equals("packages")) {
                                                            if (isWithinRange(d1, d2, d3)) {

                                                                //System.out.println("inside eeeeeeeeee");


                                                                double a = (Double.parseDouble(y[2]) * Double.parseDouble(stLocalFare));
                                                                double b = a / Integer.parseInt(y[3]);

                                                                surgeValue = b;
                                                                surgeRatio = Double.parseDouble(y[2]) / Double.parseDouble(y[3]) / 100;

                                                                tvSurgeCharges.setVisibility(View.VISIBLE);
                                                                break outerloop;
                                                                //break;
                                                            }
                                                            else {
                                                                tvSurgeCharges.setVisibility(View.GONE);
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
                                        //f = f + (15 * f) / 100;

                                        // stFareEstimate = String.valueOf(f + surgeValue);
                                        //tvFareMsg.setText(getString(R.string.Rs)+" " +stLocalFare+"+ Taxes apply");
                                        stFareEstimate = String.valueOf(Integer.parseInt(stLocalFare) + surgeValue);

                                        //tvRideEstimate.setText("Fare Estimate ~  " + getString(R.string.Rs) + " " + stFareEstimate );
                                        //tvFareMsg.setText("  +  "+getString(R.string.Rs)+" "+data.getServicetax()+"  GST");

                                        int b = (int) (Double.parseDouble(stFareEstimate) * Double.parseDouble(data.getServicetax())) / 100;
                                        tvFareMsg.setText(getString(R.string.Rs) + " " + stFareEstimate + " + " + getString(R.string.Rs) + " " + b + " GST");
                                        //tvFareMsg.setText(" + "+getString(R.string.Rs) + " " +b+" GST");
                                        gst=b;
                                        //}

                                    }
                                    break;
                                case 2:
                                    if (stCategorySelected.equals("SUV")) {

//                                        if (data.getTotalfare().equals("")) {
//
//
//                                        } else {

                                        if (data.getPeakhoursdata().equals("")) {

                                        } else {

                                            //System.out.println("^^^^^ " + data.getPeakhoursdata());

                                            String v[] = data.getPeakhoursdata().split(",");

                                            outerloop:

                                            for (int i = 0; i < v.length; i++) {
                                                String w = v[i];
                                                String y[] = w.split("-");

                                                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                                                try {
                                                    Date d1 = format.parse(getCurrentTime());
                                                    Date d2 = format.parse(y[0]);
                                                    Date d3 = format.parse(y[1]);

                                                    //System.out.println("y[4]" + y[4]);

                                                    String z[] = y[4].split("\\|");

                                                    //System.out.println("z.lenght" + z.length);

                                                    if (z.length != 0) {
                                                        for (int l = 0; l < z.length; l++) {

                                                            //System.out.println("z[k]" + z[l]);

                                                            if (z[l].equals("packages")) {

                                                                if (isWithinRange(d1, d2, d3)) {
                                                                    double a = (Double.parseDouble(y[2]) * Double.parseDouble(stLocalFare));
                                                                    double b = a / Integer.parseInt(y[3]);

                                                                    surgeValue = b;
                                                                    //surgeRatio = (Double.parseDouble(y[2]) + Double.parseDouble(y[3])) / 100;
                                                                    surgeRatio = Double.parseDouble(y[2]);

                                                                    tvSurgeCharges.setVisibility(View.VISIBLE);

                                                                    break outerloop;
                                                                    //break;
                                                                }
                                                                else {
                                                                    tvSurgeCharges.setVisibility(View.GONE);
                                                                }
                                                            }
                                                        }
                                                    } else {

                                                        if (y[4].equals("packages")) {
                                                            if (isWithinRange(d1, d2, d3)) {

                                                                //System.out.println("inside eeeeeeeeee");


                                                                double a = (Double.parseDouble(y[2]) * Double.parseDouble(stLocalFare));
                                                                double b = a / Integer.parseInt(y[3]);

                                                                surgeValue = b;
                                                                surgeRatio = Double.parseDouble(y[2]) / Double.parseDouble(y[3]) / 100;

                                                                tvSurgeCharges.setVisibility(View.VISIBLE);

                                                                break outerloop;
                                                                //break;
                                                            }
                                                            else {
                                                                tvSurgeCharges.setVisibility(View.GONE);
                                                            }


                                                        }

                                                    }
                                                    //System.out.println(date);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                        //  int f = (int) Double.parseDouble(data.getTotalfare());
                                        //f = f + (15 * f) / 100;

                                        // stFareEstimate = String.valueOf(f + surgeValue);
                                        //tvFareMsg.setText(getString(R.string.Rs)+" " +stLocalFare+"+ Taxes apply");
                                        stFareEstimate = String.valueOf(Integer.parseInt(stLocalFare) + surgeValue);

                                        //tvRideEstimate.setText("Fare Estimate ~  " + getString(R.string.Rs) + " " + stFareEstimate );
                                        //tvFareMsg.setText("  +  "+getString(R.string.Rs)+" "+data.getServicetax()+"  GST");

                                        int b = (int) (Double.parseDouble(stFareEstimate) * Double.parseDouble(data.getServicetax())) / 100;
                                        tvFareMsg.setText(getString(R.string.Rs) + " " + stFareEstimate + " + " + getString(R.string.Rs) + " " + b + " GST");
                                        //tvFareMsg.setText(" + "+getString(R.string.Rs) + " " +b+" GST");
                                        //}
                                        gst=b;
                                    }
                                    break;
                                case 1:
                                    if (stCategorySelected.equals("Sedan")) {

//                                        if (data.getTotalfare().equals("")) {
//
//
//                                        } else {

                                        if (data.getPeakhoursdata().equals("")) {

                                        } else {

                                            //System.out.println("^^^^^ " + data.getPeakhoursdata());

                                            String v[] = data.getPeakhoursdata().split(",");

                                            outerloop:

                                            for (int i = 0; i < v.length; i++) {
                                                String w = v[i];
                                                String y[] = w.split("-");

                                                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                                                try {
                                                    Date d1 = format.parse(getCurrentTime());
                                                    Date d2 = format.parse(y[0]);
                                                    Date d3 = format.parse(y[1]);

                                                    // System.out.println("y[4]" + y[4]);

                                                    String z[] = y[4].split("\\|");

                                                    //System.out.println("z.lenght" + z.length);

                                                    if (z.length != 0) {
                                                        for (int l = 0; l < z.length; l++) {

                                                            //System.out.println("z[k]" + z[l]);

                                                            if (z[l].equals("Packages")) {

                                                                if (isWithinRange(d1, d2, d3)) {
                                                                    double a = (Double.parseDouble(y[2]) * Double.parseDouble(stLocalFare));
                                                                    double b = a / Integer.parseInt(y[3]);

                                                                    surgeValue = b;
                                                                    //surgeRatio = (Double.parseDouble(y[2]) + Double.parseDouble(y[3])) / 100;
                                                                    surgeRatio = Double.parseDouble(y[2]);

                                                                    tvSurgeCharges.setVisibility(View.VISIBLE);

                                                                    break outerloop;
                                                                    //break;
                                                                }
                                                                else {
                                                                    tvSurgeCharges.setVisibility(View.GONE);
                                                                }
                                                            }
                                                        }
                                                    } else {

                                                        if (y[4].equals("Packages")) {
                                                            if (isWithinRange(d1, d2, d3)) {

                                                                // System.out.println("inside eeeeeeeeee");


                                                                double a = (Double.parseDouble(y[2]) * Double.parseDouble(stLocalFare));
                                                                double b = a / Integer.parseInt(y[3]);

                                                                surgeValue = b;
                                                                surgeRatio = Double.parseDouble(y[2]) / Double.parseDouble(y[3]) / 100;

                                                                tvSurgeCharges.setVisibility(View.VISIBLE);

                                                                break outerloop;
                                                                //break;
                                                            }
                                                            else {
                                                                tvSurgeCharges.setVisibility(View.GONE);
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
                                        //f = f + (15 * f) / 100;

                                        // stFareEstimate = String.valueOf(f + surgeValue);
                                        //tvFareMsg.setText(getString(R.string.Rs)+" " +stLocalFare+"+ Taxes apply");
                                        stFareEstimate = String.valueOf(Integer.parseInt(stLocalFare) + surgeValue);

                                        //tvRideEstimate.setText("Fare Estimate ~  " + getString(R.string.Rs) + " " + stFareEstimate );
                                        //tvFareMsg.setText("  +  "+getString(R.string.Rs)+" "+data.getServicetax()+"  GST");

                                        int b = (int) (Double.parseDouble(stFareEstimate) * Double.parseDouble(data.getServicetax())) / 100;
                                        tvFareMsg.setText(getString(R.string.Rs) + " " + stFareEstimate + " + " + getString(R.string.Rs) + " " + b + " GST");
                                        //tvFareMsg.setText(" + "+getString(R.string.Rs) + " " +b+" GST");
                                        //  }
                                        gst=b;
                                    }
                                    break;


                            }
                        }

                    }

                }

                @Override
                public void onFailure(Call<List<OutStationPojo>> call, Throwable t) {

                    progressDialog.dismiss();

                    Toast.makeText(getActivity(), "Connectivity Issue! Please Check.", Toast.LENGTH_SHORT).show();

                }
            });

        }

        return stFareEstimate;
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //myBottomSheet = MyBottomSheetDialogFragment.newInstance("Modal Bottom Sheet");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof HomeActivity){
            a=(HomeActivity) context;
        }

    }

    /*private void initAbly(final String driverId) throws AblyException {

        System.out.println("ABLY IS INITIALIZED!!!");
        System.out.println("driverId is "+driverId);

        //AblyRealtime realtime = new AblyRealtime(API_KEY);

        channel = realtime.channels.get(driverId);
        //Toast.makeText(getBaseContext(), "Message received: " + messages.data, Toast.LENGTH_SHORT).show();

        channel.subscribe(new Channel.MessageListener() {

            @Override
            public void onMessage(final Message messages) {

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        System.out.println("****** msg received!!!"+messages.data.toString());

                        if(messages.data.toString().equals(requestId+"accept"))
                        {
                            //channel.unsubscribe();
                            pubnub.unsubscribe();

                            alertDialog.dismiss();
                            handler.removeCallbacks(r);
                            //  Toast.makeText(getContext(), "Cab request accepted by Driver", Toast.LENGTH_LONG).show();
                            count = 0;
                            waitingForDriver = false;
                            isRequestAccepted = true;
                            accepted = true;
                            forPositionAccepted = p;

                            CabData cd = cabDataList.get(c.getPosition());
                            //CabData cd = cabDataList.get(forPositionAccepted);
                            ArrayList<CabData> cabAcceptedData = new ArrayList<CabData>();
                            cabAcceptedData.add(new CabData(cd.getProfileId(), cd.getVehRegNo(),
                                    cd.getPhoneNumber(), cd.getCabCat(), cd.getLatitude(), cd.getLongitude(), cd.getDriverName(), cd.getDriverPic(), cd.getDutyPerform()));

                            dbAdapter.insertUserRideStatus(requestId, "not arrived");

                            Intent i = new Intent(getActivity(), RideInProgressActivity.class);
                            i.putExtra("list", cabAcceptedData);
                            i.putExtra("requestId", requestId);
                            i.putExtra("selectedCategory", stCategorySelected);
                            i.putExtra("localPackage", stLocalPkg);
                            editor.putString("driverId",cda.getProfileId());
                            editor.commit();
                            startActivity(i);
                            getActivity().finish();
                        }

                        else if(messages.data.toString().equals(requestId+"decline"))
                        {
                            //channel.unsubscribe();
                            pubnub.unsubscribe();

                            handler.removeCallbacks(r);

                            count = -10;
                            waitingForDriver = false;
                            isRequestAccepted = false;

                            h = h + 1;
                            sendRequestCheckAcceptance(h);
                        }

                    }
                });

//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getActivity(), "Msg** "+messages.data, Toast.LENGTH_SHORT).show();
//                    }
//                });

            }

        });
    }


    public void publishMessage(String msg) throws AblyException{

        channel.publish("update", msg, new CompletionListener() {
            @Override
            public void onSuccess() {

                System.out.println("***************** success");

                //Toast.makeText(getBaseContext(), "Message sent", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(ErrorInfo reason) {

                System.out.println("********************** error");

                // Toast.makeText(getBaseContext(), "Message not sent", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    private final void initPubNub(String driverId) {
        PNConfiguration config = new PNConfiguration();

        config.setPublishKey(Constants.PUBNUB_PUBLISH_KEY);
        config.setSubscribeKey(Constants.PUBNUB_SUBSCRIBE_KEY);
        config.setReconnectionPolicy(PNReconnectionPolicy.LINEAR);
        // config.setUuid(this.mUsername);
        config.setSecure(true);

        pubnub=new PubNub(config);

        pubnub.subscribe()
                .channels(Arrays.asList(driverId)) // subscribe to channels
                .execute();

        pubnub.addListener(subscribeCallback);

        if(debugLogs)
        {
            Loggly.i("ConfirmRideFragment",guestProfileId+" [Pubnub Initialized]");
        }

    }

    public void publish(final String msg, final String profileId)
    {
        pubnub.publish()
                .message(msg)
                .channel(profileId)
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        // handle publish result, status always present, result if successful
                        // status.isError() to see if error happened
                        if(!status.isError()) {
                            //System.out.println("pub timetoken: " + result.getTimetoken());
                            if(debugLogs)
                            {
                                Loggly.i("ConfirmRideFragment",guestProfileId+" "+profileId+" "+requestId+" "+msg+" [published]");
                            }
                        } else {
                            Loggly.i("ConfirmRideFragment",guestProfileId+" "+profileId+" "+requestId+" "+msg+" [error,published] "+status.isError());
                        }
                        //System.out.println("pub status code: " + status.getStatusCode());
                    }
                });
    }

    SubscribeCallback subscribeCallback=new SubscribeCallback() {
        @Override
        public void status(PubNub pubnub, PNStatus status) {
           /* switch (status.getOperation()) {
                // let's combine unsubscribe and subscribe handling for ease of use
                case PNSubscribeOperation:
                case PNUnsubscribeOperation:
                    // note: subscribe statuses never have traditional
                    // errors, they just have categories to represent the
                    // different issues or successes that occur as part of subscribe

                    //System.out.println("*******"+status.getCategory());*/

            switch (status.getCategory()) {
                case PNConnectedCategory:
                    //Toast.makeText(MainActivity.this, "hey", Toast.LENGTH_SHORT).show();
                    // this is expected for a subscribe, this means there is no error or issue whatsoever
                    break;
                case PNReconnectedCategory:
                    // this usually occurs if subscribe temporarily fails but reconnects. This means
                    // there was an error but there is no longer any issue
                    break;
                case PNDisconnectedCategory:
                    // this is the expected category for an unsubscribe. This means there
                    // was no error in unsubscribing from everything
                    break;

                case PNUnexpectedDisconnectCategory:

                    pubnub.reconnect();

                    break;
                // this is usually an issue with the internet connection, this is an error, handle appropriately
                case PNTimeoutCategory:

                    pubnub.reconnect();

                    break;
                case PNAccessDeniedCategory:
                    // this means that PAM does allow this client to subscribe to this
                    // channel and channel group configuration. This is another explicit error
                    break;
                default:
                    // More errors can be directly specified by creating explicit cases for other
                    // error categories of `PNStatusCategory` such as `PNTimeoutCategory` or `PNMalformedFilterExpressionCategory` or `PNDecryptionErrorCategory`
                    break;
            }

                /*case PNHeartbeatOperation:
                    // heartbeat operations can in fact have errors, so it is important to check first for an error.
                    // For more information on how to configure heartbeat notifications through the status
                    // PNObjectEventListener callback, consult <link to the PNCONFIGURATION heartbeart config>
                    if (status.isError()) {
                        // There was an error with the heartbeat operation, handle here
                    } else {
                        // heartbeat operation was successful
                    }
                default: {
                    // Encountered unknown status type
                }
            }*/
        }

        @Override
        public void message(PubNub pubnub, PNMessageResult message) {

            System.out.println(message.toString());

            JsonElement msg = message.getMessage();
            String s=message.toString();

            if(debugLogs)
            {
                Loggly.i("ConfirmRideFragment",guestProfileId+" "+requestId+" "+msg.getAsString()+" [subscribe msg]");
            }

            if(msg.getAsString().equals(requestId+"accept"))
            {
                //channel.unsubscribe();
                pubnub.unsubscribe();

                alertDialog.dismiss();
                handler.removeCallbacks(r);
                //  Toast.makeText(getContext(), "Cab request accepted by Driver", Toast.LENGTH_LONG).show();
                count = 0;
                waitingForDriver = false;
                isRequestAccepted = true;
                accepted = true;
                forPositionAccepted = p;

                CabData cd = cabDataList.get(c.getPosition());
                //CabData cd = cabDataList.get(forPositionAccepted);
                ArrayList<CabData> cabAcceptedData = new ArrayList<CabData>();
                cabAcceptedData.add(new CabData(cd.getProfileId(), cd.getVehRegNo(),
                        cd.getPhoneNumber(), cd.getCabCat(), cd.getLatitude(), cd.getLongitude(), cd.getDriverName(), cd.getDriverPic(), cd.getDutyPerform()));

                dbAdapter.insertUserRideStatus(requestId, "not arrived");

                Intent i = new Intent(getActivity(), RideInProgressActivity.class);
                i.putExtra("list", cabAcceptedData);
                i.putExtra("requestId", requestId);
                i.putExtra("selectedCategory", stCategorySelected);
                i.putExtra("localPackage", stLocalPkg);
                editor.putString("driverId",cda.getProfileId());
                editor.commit();
                startActivity(i);
                getActivity().finish();
            }

            else if(msg.getAsString().equals(requestId+"decline"))
            {
                //channel.unsubscribe();
                pubnub.unsubscribe();
                pubnub.removeListener(subscribeCallback);


                handler.removeCallbacks(r);

                count = -10;
                waitingForDriver = false;
                isRequestAccepted = false;

                h = h + 1;
                sendRequestCheckAcceptance(h);
            }

            //getHistory();

        }

        @Override
        public void presence(PubNub pubnub, PNPresenceEventResult presence) {

        }

    };




}

