package com.hjsoft.guestbooktaxi.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hjsoft.guestbooktaxi.R;
import com.hjsoft.guestbooktaxi.activity.HomeActivity;
import com.hjsoft.guestbooktaxi.activity.LocalPackagesActivity;
import com.hjsoft.guestbooktaxi.activity.OutStationActivity;
import com.hjsoft.guestbooktaxi.activity.PackagesActivity;
import com.hjsoft.guestbooktaxi.activity.PlacesAutoCompleteActivity;
import com.hjsoft.guestbooktaxi.activity.TeleCallActivity;
import com.hjsoft.guestbooktaxi.adapter.DBAdapter;
import com.hjsoft.guestbooktaxi.model.CabArrivalTimePojo;
import com.hjsoft.guestbooktaxi.model.CabData;
import com.hjsoft.guestbooktaxi.model.CabPojo;
import com.hjsoft.guestbooktaxi.model.ServiceLocationPojo;
import com.hjsoft.guestbooktaxi.webservices.API;
import com.hjsoft.guestbooktaxi.webservices.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 15/12/16.
 */

public class TrackCabsFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener,
        GoogleMap.OnMarkerClickListener {

    SupportMapFragment mapFragment;
    GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected boolean mRequestingLocationUpdates;
    final static int REQUEST_LOCATION = 199;
    protected Location mLastLocation;
    double latitude,longitude,current_lat,current_long;
    Geocoder geocoder;
    List<Address> addresses;
    LatLng lastLoc,curntloc;
    String complete_address;
    float[] results=new float[3];
    long res=0;
    boolean entered=false;
    String city;
    API REST_CLIENT;
    DBAdapter dbAdapter;
    TextView tvPickup,tvDrop,tvDropTitle;
    Button btBookCab;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";
    String pickupLat,pickupLong,dropLat,dropLong;
    String guestProfileId="UP0000001";
    Handler hStart,h;
    Runnable rStart,r;
    CoordinatorLayout cLayout;
    String stClicked;
    TextView tvMini,tvMicro,tvSedan,tvOutStation,tvSuv;
    boolean pickupChanged=false;
    ImageButton ibLocation;
    String stCategorySelected="Mini";
    ArrayList<CabData> cabDataList=new ArrayList<>();
    TextView tvMiniNoCab,tvMicraNoCab,tvSedanNoCab,tvOutStationNoCab,tvSuvNoCab;
    LinearLayout llMini,llMicra,llSedan,llOutStation,llSuv;
    BottomSheetDialogFragment myBottomSheet;
    ProgressDialog progressDialog;
    String locLat,locLong;
    View rootView;
    boolean first=true;
    Marker cab;
    String companyId="CMP00001";
    RadioButton rbLocal,rbOutstation;
    TextView tvOutstationPkg;
    String stTravelType="Local";
    boolean connectivity=true;
    String dropCity;
    //double cityCenterLat=17.3850,cityCenterLong=78.4867;
    double cityCenterLat,cityCenterLong;
    LinearLayout ll_12_40_seater,llLocalPackages;
    TextView tvLocalPkg,tvLocalOneWay;
    String stLocalPkg="",stFare="";
    Button btRideLater;
    TextView tvCity;
    TextView tvLoc1,tvLoc2,tvLoc3,tvLoc4,tvLoc5;
    int j=0;
    int radius;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.fragment_track_cabs);
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        //tvCloc=(TextView)findViewById(R.id.at_tv_cloc);
        dbAdapter=new DBAdapter(getContext());
        dbAdapter=dbAdapter.open();

        pref = getActivity().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        city=pref.getString("city",null);
        cityCenterLat=Double.parseDouble(pref.getString("cityCenterLat",null));
        cityCenterLong=Double.parseDouble(pref.getString("cityCenterLong",null));
        radius=Integer.parseInt(pref.getString("radius","30"));

        mRequestingLocationUpdates=false;

        REST_CLIENT= RestClient.get();

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
                    Toast.makeText(getActivity()," Location Permission is required for this app to run !",Toast.LENGTH_LONG).show();
                }
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_track_cabs, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        tvPickup=(TextView)rootView.findViewById(R.id.ftc_tv_pickup);
        tvDrop=(TextView)rootView.findViewById(R.id.ftc_tv_drop);
        tvDropTitle=(TextView)rootView.findViewById(R.id.ftc_tv_drop_title);
        btBookCab=(Button)rootView.findViewById(R.id.ftc_bt_ride_now);
        tvMini=(TextView)rootView.findViewById(R.id.ftc_tv_mini);
        tvMicro=(TextView)rootView.findViewById(R.id.ftc_tv_micro);
        tvSedan=(TextView)rootView.findViewById(R.id.ftc_tv_sedan);
        tvOutStation=(TextView)rootView.findViewById(R.id.ftc_tv_outstation);
        ibLocation=(ImageButton)rootView.findViewById(R.id.ftc_ib_location);
        tvMiniNoCab=(TextView)rootView.findViewById(R.id.ftc_tv_mini_nocab);
        tvMicraNoCab=(TextView)rootView.findViewById(R.id.ftc_tv_micra_nocab);
        tvSedanNoCab=(TextView)rootView.findViewById(R.id.ftc_tv_sedan_nocab);
        tvOutStationNoCab=(TextView)rootView.findViewById(R.id.ftc_tv_outstation_nocab);
        llMini=(LinearLayout)rootView.findViewById(R.id.ftc_ll_mini);
        llMicra=(LinearLayout)rootView.findViewById(R.id.ftc_ll_micra);
        llSedan=(LinearLayout)rootView.findViewById(R.id.ftc_ll_sedan);
        llOutStation=(LinearLayout)rootView.findViewById(R.id.ftc_ll_outstation);
        myBottomSheet = MyBottomSheetDialogFragment.newInstance("Modal Bottom Sheet");
        rbLocal=(RadioButton)rootView.findViewById(R.id.ftc_rb_local);
        rbOutstation=(RadioButton)rootView.findViewById(R.id.ftc_rb_out_station);
        llSuv=(LinearLayout)rootView.findViewById(R.id.ftc_ll_suv);
        tvSuv=(TextView)rootView.findViewById(R.id.ftc_tv_suv);
        tvSuvNoCab=(TextView)rootView.findViewById(R.id.ftc_tv_suv_nocab);

        tvLocalPkg=(TextView)rootView.findViewById(R.id.ftc_tv_local_pkg);
        tvLocalOneWay=(TextView)rootView.findViewById(R.id.ftc_tv_local_one_way);
        ll_12_40_seater=(LinearLayout)rootView.findViewById(R.id.ftc_ll_12_40_seater);
        btRideLater=(Button)rootView.findViewById(R.id.ftc_bt_ride_later);
        llLocalPackages=(LinearLayout)rootView.findViewById(R.id.ftc_ll_local_pkgs);
        tvOutstationPkg=(TextView)rootView.findViewById(R.id.ftc_tv_outstation_pkg);
        tvOutstationPkg.setVisibility(View.GONE);

        tvCity=(TextView)rootView.findViewById(R.id.ftc_tv_my_loc);
        tvCity.setText(city);

        /*

        final Animation animation = new AlphaAnimation(1,0); // Change alpha from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE);
        */

        //btBookCab.startAnimation(animation);

        tvCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectServiceLocations();

            }
        });

        cLayout=(CoordinatorLayout)rootView.findViewById(R.id.ftc_cLayout);

        tvLocalPkg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if(stCategorySelected.equals("Micra"))
//                {
//                   Toast.makeText(getActivity(),"Packages available for Mini and Sedan only !",Toast.LENGTH_LONG).show();
//                }
//                else {

                btBookCab.setText("Ride Now");
                tvLocalPkg.setText("Package");
                tvLocalPkg.setTextColor(Color.parseColor("#0067de"));
                tvLocalOneWay.setTextColor(Color.parseColor("#9e9e9e"));
                stLocalPkg = "localPkg";

                tvDrop.setTextColor(Color.parseColor("#000000"));
                tvDrop.setText("-");

                btRideLater.setVisibility(View.VISIBLE);

                Intent i=new Intent(getActivity(), PackagesActivity.class);
                i.putExtra("cabCat",stCategorySelected);
                i.putExtra("city",city);
                startActivityForResult(i,3);
                //  }
            }
        });

        tvLocalOneWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvDrop.setText("");

                btBookCab.setText("Book Now");
                tvLocalPkg.setText("Package");
                tvLocalOneWay.setTextColor(Color.parseColor("#0067de"));
                tvLocalPkg.setTextColor(Color.parseColor("#9e9e9e"));
                stLocalPkg="";
                btRideLater.setVisibility(View.GONE);

            }
        });

        rbLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stTravelType="Local";

                tvOutstationPkg.setVisibility(View.GONE);
                //stOutstationPkg="";
            }
        });

        llOutStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ///
                tvLocalOneWay.setVisibility(View.VISIBLE);

                llLocalPackages.setVisibility(View.GONE);

                tvOutStation.setTextColor(Color.parseColor("#0067de"));
                tvMini.setTextColor(Color.parseColor("#414040"));
                tvMicro.setTextColor(Color.parseColor("#414040"));
                tvSedan.setTextColor(Color.parseColor("#414040"));
                tvSuv.setTextColor(Color.parseColor("#414040"));
                //tvLocalPkg.setTextColor(Color.parseColor("#414040"));

                llOutStation.setBackground(getResources().getDrawable(R.drawable.round_bg_cabs));
                llMicra.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));
                llMini.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));
                llSedan.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));
                llSuv.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));
                //llLocalPackages.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));
                // llLocalPackages.setVisibility(View.GONE);

                btBookCab.setText("CONTINUE BOOKING");
                btRideLater.setVisibility(View.GONE);
                stCategorySelected="Outstation";
                // hStart.removeCallbacks(rStart);
                // hStart.post(rStart);

                //remove comments if handlers are used
                // hStart.removeCallbacks(rStart);


                //h.removeCallbacks(r);
                btBookCab.setEnabled(true);
                mMap.clear();

                if(!pickupChanged) {
                    if (curntloc != null) {
                        mMap.addMarker(new MarkerOptions().position(curntloc)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
                    } else if (lastLoc.latitude!=0.0&&lastLoc.longitude!=0.0) {
                        mMap.addMarker(new MarkerOptions().position(lastLoc)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
                    }
                }else {

                    LatLng pLatLng=new LatLng(Double.parseDouble(locLat),Double.parseDouble(locLong));
                    mMap.addMarker(new MarkerOptions().position(pLatLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
                }

                //mMap.clear();

                /*

                stTravelType="OutStation";

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.alert_out_station, null);
                dialogBuilder.setView(dialogView);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                RadioButton drop=(RadioButton)dialogView.findViewById(R.id.aos_rb_drop);
                RadioButton roundTrip=(RadioButton)dialogView.findViewById(R.id.aos_rb_round_trip);

                switch(stOutstationPkg)
                {
                    case "Drop":
                        drop.setTextColor(Color.parseColor("#2679cc"));
                        roundTrip.setTextColor(Color.parseColor("#414040"));
                        break;
                    case "Round":
                        drop.setTextColor(Color.parseColor("#414040"));
                        roundTrip.setTextColor(Color.parseColor("#2679cc"));
                        break;
                }

                RadioGroup rgList=(RadioGroup)dialogView.findViewById(R.id.aos_rg_list);
                rgList.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {

                        tvOutstationPkg.setVisibility(View.VISIBLE);

                        switch (i)
                        {
                            case R.id.aos_rb_drop:
                                //Toast.makeText(getActivity(),"Mini clicked",Toast.LENGTH_LONG).show();
                                stOutstationPkg="Drop";
                                tvOutstationPkg.setText("Drop");
                                alertDialog.dismiss();
                                break;
                            case R.id.aos_rb_round_trip:
                                //Toast.makeText(getActivity(),"Micra clicked",Toast.LENGTH_LONG).show();
                                stOutstationPkg="Round";
                                tvOutstationPkg.setText("Round Trip");
                                alertDialog.dismiss();
                                break;
                        }
                        // alertDialog.dismiss();
                    }
                });*/


                ///coding here

             /*   if(tvDrop.getText().toString().trim().equals(""))
                {
                    Toast.makeText(getContext(),"Please enter drop location",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getContext(), PlacesAutoCompleteActivity.class);
                    startActivityForResult(i, 2);
                    stClicked = "drop";
                }
                else {


                    Location.distanceBetween(cityCenterLat,cityCenterLong,Double.parseDouble(dropLat),Double.parseDouble(dropLong),results);

                    long outstationDistance=0;

                    outstationDistance=outstationDistance+(long)results[0];
                    outstationDistance=outstationDistance/1000;
                    System.out.println("outstation distance is"+outstationDistance);

                    if(outstationDistance>30.0)
                    {
                       Intent i = new Intent(getActivity(),OutStationActivity.class);
                        i.putExtra("city", city);
                        startActivity(i);
                        getActivity().finish();
                    }
                    else {

                        Toast.makeText(getActivity(),"Invalid Outstation Drop! Change Location.",Toast.LENGTH_LONG).show();
                    }
                }*/
            }
        });

        ll_12_40_seater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(getActivity(), TeleCallActivity.class);
                startActivity(i);
                // getActivity().finish();
            }
        });

        btRideLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tvPickup.getText().toString().trim().equals("Unable to get the location details")||
                        tvPickup.getText().toString().trim().equals("-"))
                {
                    Toast.makeText(getContext(), "Please enter pickup location !", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getContext(), PlacesAutoCompleteActivity.class);
                    startActivityForResult(i, 2);
                    stClicked = "pickup";
                    pickupChanged=true;
                }
                else {

                    Intent i = new Intent(getActivity(), LocalPackagesActivity.class);
                    i.putExtra("time", "later");
                    i.putExtra("cabCat", stCategorySelected);
                    i.putExtra("pkg", stLocalPkg);
                    i.putExtra("fare", stFare);
                    i.putExtra("city", city);
                    startActivity(i);
                }
            }
        });

        /*llLocalPackages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                llLocalPackages.setBackground(getResources().getDrawable(R.drawable.round_bg_cabs));
                llMicra.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));
                llMini.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));
                llSedan.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));
                llOutStation.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));

                tvLocalPkg.setTextColor(Color.parseColor("#0067de"));
                tvMicro.setTextColor(Color.parseColor("#414040"));
                tvSedan.setTextColor(Color.parseColor("#414040"));
                tvOutStation.setTextColor(Color.parseColor("#414040"));
                tvMini.setTextColor(Color.parseColor("#414040"));


                btBookCab.setText("Ride Now");
                btRideLater.setVisibility(View.VISIBLE);
                btBookCab.setEnabled(true);

                stCategorySelected="Packages";
            }
        });*/




        tvPickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                pickupChanged=true;
                Intent i=new Intent(getContext(),PlacesAutoCompleteActivity.class);
                startActivityForResult(i,2);
                stClicked="pickup";


            }
        });

        tvDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(getContext(),PlacesAutoCompleteActivity.class);
                startActivityForResult(i,2);
                stClicked="drop";
            }
        });

        tvDropTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(getContext(),PlacesAutoCompleteActivity.class);
                startActivityForResult(i,2);
                stClicked="drop";
            }
        });

        llMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(view.getContext());
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Getting Cabs ...");
                progressDialog.show();

                //new
                tvLocalOneWay.setVisibility(View.VISIBLE);

                llLocalPackages.setVisibility(View.VISIBLE);

                llMini.setBackground(getResources().getDrawable(R.drawable.round_bg_cabs));
                llMicra.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));
                llSedan.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));
                llOutStation.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));
                llSuv.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));

                tvMini.setTextColor(Color.parseColor("#0067de"));
                tvMicro.setTextColor(Color.parseColor("#414040"));
                tvSedan.setTextColor(Color.parseColor("#414040"));
                tvOutStation.setTextColor(Color.parseColor("#414040"));
                tvSuv.setTextColor(Color.parseColor("#414040"));
                // tvLocalPkg.setTextColor(Color.parseColor("#414040"));

                tvLocalPkg.setText("Package");
                tvLocalOneWay.setTextColor(Color.parseColor("#0067de"));
                tvLocalPkg.setTextColor(Color.parseColor("#9e9e9e"));
                stLocalPkg="";
                //tvDrop.setText("");
                btRideLater.setVisibility(View.GONE);
                btBookCab.setText("Book Now");

                stCategorySelected="Mini";

                //remove comments if handlers are used
                hStart.removeCallbacks(rStart);
                hStart.post(rStart);

//                getCabs();
//                getCabTimes();

            }
        });

        llSuv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(view.getContext());
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Getting Cabs ...");
                progressDialog.show();

                llLocalPackages.setVisibility(View.VISIBLE);

                llMini.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));
                llMicra.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));
                llSedan.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));
                llOutStation.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));
                llSuv.setBackground(getResources().getDrawable(R.drawable.round_bg_cabs));

                tvSuv.setTextColor(Color.parseColor("#0067de"));
                tvMini.setTextColor(Color.parseColor("#414040"));
                tvMicro.setTextColor(Color.parseColor("#414040"));
                tvSedan.setTextColor(Color.parseColor("#414040"));
                tvOutStation.setTextColor(Color.parseColor("#414040"));
                // tvLocalPkg.setTextColor(Color.parseColor("#414040"));

                tvLocalPkg.setText("Package");
                tvLocalOneWay.setTextColor(Color.parseColor("#0067de"));
                tvLocalPkg.setTextColor(Color.parseColor("#9e9e9e"));
                stLocalPkg="";
                //tvDrop.setText("");
                btRideLater.setVisibility(View.GONE);
                btBookCab.setText("Book Now");

                stCategorySelected="SUV";

                //remove comments if handlers are used

                hStart.removeCallbacks(rStart);
                hStart.post(rStart);

//                getCabs();
//                getCabTimes();

                btBookCab.setText("Ride Now");
                tvLocalPkg.setText("Package");
                tvLocalPkg.setTextColor(Color.parseColor("#0067de"));
                tvLocalOneWay.setTextColor(Color.parseColor("#9e9e9e"));
                tvLocalOneWay.setVisibility(View.GONE);
                stLocalPkg = "localPkg";

                tvDrop.setTextColor(Color.parseColor("#000000"));
                tvDrop.setText("-");

                btRideLater.setVisibility(View.VISIBLE);

                Intent i=new Intent(getActivity(), PackagesActivity.class);
                i.putExtra("cabCat",stCategorySelected);
                i.putExtra("city",city);
                startActivityForResult(i,3);
            }
        });

        llMicra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  if(stLocalPkg.equals("")) {

                progressDialog = new ProgressDialog(view.getContext());
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Getting Cabs... ");
                progressDialog.show();

                //new
                tvLocalOneWay.setVisibility(View.VISIBLE);


                llLocalPackages.setVisibility(View.VISIBLE);


                llMicra.setBackground(getResources().getDrawable(R.drawable.round_bg_cabs));
                llMini.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));
                llSedan.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));
                llOutStation.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));
                llSuv.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));

                tvMicro.setTextColor(Color.parseColor("#0067de"));
                tvMini.setTextColor(Color.parseColor("#414040"));
                tvSedan.setTextColor(Color.parseColor("#414040"));
                tvOutStation.setTextColor(Color.parseColor("#414040"));
                tvSuv.setTextColor(Color.parseColor("#414040"));
                // tvLocalPkg.setTextColor(Color.parseColor("#414040"));

                tvLocalPkg.setText("Package");
                tvLocalOneWay.setTextColor(Color.parseColor("#0067de"));
                tvLocalPkg.setTextColor(Color.parseColor("#9e9e9e"));
                stLocalPkg="";
                //tvDrop.setText("");
                btRideLater.setVisibility(View.GONE);
                btBookCab.setText("Book Now");

                stCategorySelected = "Micra";

                //remove comments if handlers are used
                hStart.removeCallbacks(rStart);
                hStart.post(rStart);

//                getCabs();
//                getCabTimes();

            }
        });

        llSedan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(view.getContext());
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Getting Cabs ...");
                progressDialog.show();

                //new
                tvLocalOneWay.setVisibility(View.VISIBLE);


                llLocalPackages.setVisibility(View.VISIBLE);

                llSedan.setBackground(getResources().getDrawable(R.drawable.round_bg_cabs));
                llMicra.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));
                llMini.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));
                llOutStation.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));
                llSuv.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));

                tvSedan.setTextColor(Color.parseColor("#0067de"));
                tvMicro.setTextColor(Color.parseColor("#414040"));
                tvMini.setTextColor(Color.parseColor("#414040"));
                tvOutStation.setTextColor(Color.parseColor("#414040"));
                tvSuv.setTextColor(Color.parseColor("#414040"));
                // tvLocalPkg.setTextColor(Color.parseColor("#414040"));

                tvLocalPkg.setText("Package");
                tvLocalOneWay.setTextColor(Color.parseColor("#0067de"));
                tvLocalPkg.setTextColor(Color.parseColor("#9e9e9e"));
                stLocalPkg="";
                //tvDrop.setText("");
                btRideLater.setVisibility(View.GONE);
                btBookCab.setText("Book Now");

                stCategorySelected="Sedan";

                //remove comments if handlers are used
                hStart.removeCallbacks(rStart);
                hStart.post(rStart);

//                getCabs();
//                getCabTimes();
            }
        });

        btBookCab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (connectivity) {

                    if (tvDrop.getText().toString().trim().equals("")) {

                        if(stCategorySelected.equals("Packages"))
                        {
//
//                            Intent i=new Intent(getActivity(),LocalPackagesActivity.class);
//                            i.putExtra("time","now");
//                            startActivity(i);
//                            getActivity().finish();

                        }
                        else {

                            Toast.makeText(getContext(), "Please enter drop location!", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getContext(), PlacesAutoCompleteActivity.class);
                            startActivityForResult(i, 2);
                            stClicked = "drop";

                        }

                    }
                    else if(tvPickup.getText().toString().trim().equals("Unable to get the location details")||
                            tvPickup.getText().toString().trim().equals("-")||tvPickup.getText().toString().trim().equals(""))
                    {
                        Toast.makeText(getContext(), "Please enter pickup location !", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getContext(), PlacesAutoCompleteActivity.class);
                        startActivityForResult(i, 2);
                        stClicked = "pickup";
                        pickupChanged=true;
                    }
                    else {

                        //Pickup and Drop Bookings
                        if(stCategorySelected.equals("Mini")||stCategorySelected.equals("Micra")||stCategorySelected.equals("Sedan")||stCategorySelected.equals("SUV"))
                        {
                            if(stLocalPkg.equals("")) {

                                if(!(tvDrop.getText().toString().trim().equals("-")))
                                {
                                    Location.distanceBetween(cityCenterLat, cityCenterLong, Double.parseDouble(pickupLat), Double.parseDouble(pickupLong), results);

                                    long localDistance = 0;

                                    localDistance = localDistance + (long) results[0];
                                    localDistance = localDistance / 1000;

                                    if(localDistance < radius)
                                    {
                                        //Location.distanceBetween(cityCenterLat, cityCenterLong, Double.parseDouble(dropLat), Double.parseDouble(dropLong), results);
                                        //actual logic
                                        Location.distanceBetween(Double.parseDouble(pickupLat), Double.parseDouble(pickupLong), Double.parseDouble(dropLat), Double.parseDouble(dropLong), results);

                                        long outstationDistance = 0;

                                        outstationDistance = outstationDistance + (long) results[0];
                                        outstationDistance = outstationDistance / 1000;

                                        if (outstationDistance < radius) {

                                            view.clearAnimation();

                                            //hStart.removeCallbacks(rStart);
                                            //h.removeCallbacks(r);
                                            //Reason for not removing callbacks is in case if this is called again from confirmfragment.
                                            Fragment frag = new ConfirmRideFragment();
                                            Bundle args = new Bundle();
                                            args.putString("CategorySelected", stCategorySelected);
                                            args.putDouble("latitude", Double.parseDouble(locLat));
                                            args.putDouble("longitude", Double.parseDouble(locLong));
                                            args.putString("city", city);
                                            args.putString("localPackage", stLocalPkg);
                                            args.putString("travelType", stTravelType);
                                            args.putString("fare",stFare);
                                            frag.setArguments(args);
                                            FragmentManager fragmentManager = getFragmentManager();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            fragmentTransaction.add(R.id.content_frame, frag, "confirm_ride");
                                            fragmentTransaction.addToBackStack(null);
                                            fragmentTransaction.commit();
                                        } else {

                                            Toast.makeText(getActivity(), " Invalid Drop! Location outside City.", Toast.LENGTH_SHORT).show();

                                        }
                                        //actual logic
                                    }
                                    else
                                    {
                                        Toast.makeText(getActivity(),"Invalid Local Booking! Change location.",Toast.LENGTH_LONG).show();
                                    }
                                }
                                else {

                                    Toast.makeText(getContext(), "Please enter drop location!", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(getContext(), PlacesAutoCompleteActivity.class);
                                    startActivityForResult(i, 2);
                                    stClicked = "drop";  }
                            }
                            else {

                                Fragment frag = new ConfirmRideFragment();
                                Bundle args = new Bundle();
                                args.putString("CategorySelected", stCategorySelected);
                                args.putDouble("latitude", Double.parseDouble(locLat));
                                args.putDouble("longitude", Double.parseDouble(locLong));
                                args.putString("city", city);
                                args.putString("localPackage", stLocalPkg);
                                args.putString("travelType", stTravelType);
                                args.putString("fare",stFare);
                                frag.setArguments(args);
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.add(R.id.content_frame, frag, "confirm_ride");
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            }
                        }
                        else {

                            if (stCategorySelected.equals("Outstation")) {

                                if(tvDrop.getText().toString().trim().equals("-"))
                                {
                                    Toast.makeText(getContext(), "Please enter drop location!", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(getContext(), PlacesAutoCompleteActivity.class);
                                    startActivityForResult(i, 2);
                                    stClicked = "drop";
                                }

                                else {

                                    Location.distanceBetween(cityCenterLat, cityCenterLong, Double.parseDouble(pickupLat), Double.parseDouble(pickupLong), results);

                                    long localDistance = 0;

                                    localDistance = localDistance + (long) results[0];
                                    localDistance = localDistance / 1000;

                                    if(localDistance < radius) {

                                        Location.distanceBetween(Double.parseDouble(pickupLat), Double.parseDouble(pickupLong), Double.parseDouble(dropLat), Double.parseDouble(dropLong), results);

                                        long outstationDistance = 0;

                                        outstationDistance = outstationDistance + (long) results[0];
                                        outstationDistance = outstationDistance / 1000;

                                        if (outstationDistance > radius) {
                                            //hStart.removeCallbacks(rStart);

                                            Intent i = new Intent(getActivity(), OutStationActivity.class);
                                            i.putExtra("city", city);
                                            startActivity(i);
                                            //getActivity().finish();
                                        } else {

                                            Toast.makeText(getActivity(), "Invalid Outstation Drop! Change Location.", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                    else {

                                        Intent i = new Intent(getActivity(), OutStationActivity.class);
                                        i.putExtra("city", city);
                                        startActivity(i);
                                    }

                                }

                            } else {

                            }
                        }
                    }
                }
                else
                {
                    Toast.makeText(getActivity(),"Connectivity Issue! Please Check.",Toast.LENGTH_SHORT).show();
                }
            }

        });

        ibLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(pickupChanged) {

                    progressDialog = new ProgressDialog(view.getContext());
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Showing current location.\nPlease wait ...");
                    progressDialog.show();


                    pickupChanged = false;
                    LatLng locLatLng = new LatLng(current_lat, current_long);
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(locLatLng)
                            .zoom(Float.parseFloat("12.8"))
                            //.bearing(30).tilt(45)
                            .build()));
                    tvPickup.setText("");

                    locLat=String.valueOf(current_lat);
                    locLong= String.valueOf(current_long);
//                    getCabs();
//                    getCabTimes();

                    hStart.removeCallbacks(rStart);
                    hStart.post(rStart);
                    h.removeCallbacks(r);
                    h.post(r);
                }

            }
        });

        return rootView;
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

    public void getCabTimes()
    {

        h=new Handler();
        r=new Runnable() {
            @Override
            public void run() {

                //System.out.println("getting cab timessssssssssss");

                h.postDelayed(this,15000);
                System.out.println("city is "+city);
                System.out.println("lat "+locLat);
                System.out.println("long "+locLong);

                tvMiniNoCab.setText("No Cabs");
                tvMicraNoCab.setText("No Cabs");
                tvSedanNoCab.setText("No Cabs");
                tvSuvNoCab.setText("No Cabs");

                Call<List<CabArrivalTimePojo>> call=REST_CLIENT.getCabArrivalTimes(city,locLat,locLong,companyId);
                call.enqueue(new Callback<List<CabArrivalTimePojo>>() {
                    @Override
                    public void onResponse(Call<List<CabArrivalTimePojo>> call, Response<List<CabArrivalTimePojo>> response) {

                        CabArrivalTimePojo data;
                        List<CabArrivalTimePojo> dataList;

                        if(myBottomSheet.isAdded())
                        {
                            myBottomSheet.dismiss();
                        }

                        if(response.isSuccessful())
                        {
                            dataList=response.body();

                            for(int i=0;i<dataList.size();i++)
                            {
                                data=dataList.get(i);

                                switch (data.getVehicleCategory()) {
                                    case "Mini":
                                        if(stCategorySelected.equals("Mini")) {

//                                            if(cabDataList.size()!=0)
//                                            {
//                                                tvMiniNoCab.setText(data.getTime());
//                                            }
//                                            else {
//                                                tvMiniNoCab.setText("No Cabs");
//                                            }

                                            tvMiniNoCab.setText(data.getTime());

                                        }
                                        else {
                                            tvMiniNoCab.setText(data.getTime());
                                        }

                                        break;
                                    case "Micra":
                                        if(stCategorySelected.equals("Micra")) {

//                                            if(cabDataList.size()!=0)
//                                            {
//                                                tvMicraNoCab.setText(data.getTime());
//                                            }
//                                            else {
//                                                tvMicraNoCab.setText("No Cabs");
//                                            }
                                            //the condition is kept bcz the filtering is done where only the Local or Both Cabs are retrieved.
                                            //review and remove the comments.
                                            tvMicraNoCab.setText(data.getTime());

                                        }
                                        else {
                                            tvMicraNoCab.setText(data.getTime());
                                        }

                                        break;
                                    case "Sedan":
                                        if(stCategorySelected.equals("Sedan")) {

//                                            if(cabDataList.size()!=0)
//                                            {
//                                                tvSedanNoCab.setText(data.getTime());
//                                            }
//                                            else {
//                                                tvSedanNoCab.setText("No Cabs");
//                                            }

                                            tvSedanNoCab.setText(data.getTime());
                                        }
                                        else {
                                            tvSedanNoCab.setText(data.getTime());
                                        }

                                        break;
                                    case "SUV":
                                        if(stCategorySelected.equals("SUV")) {

//                                            if(cabDataList.size()!=0)
//                                            {
//                                                tvSedanNoCab.setText(data.getTime());
//                                            }
//                                            else {
//                                                tvSedanNoCab.setText("No Cabs");
//                                            }

                                            tvSuvNoCab.setText(data.getTime());
                                        }
                                        else {
                                            tvSuvNoCab.setText(data.getTime());
                                        }

                                        break;
                                }

                            }
                        }
                        else
                        {
                            // tvMiniNoCab.setText(" - ");
                            //  tvMicraNoCab.setText(" - ");
                            //  tvSedanNoCab.setText(" - ");

                            tvMiniNoCab.setText("No Cabs");
                            tvMicraNoCab.setText("No Cabs ");
                            tvSedanNoCab.setText("No Cabs");
                            tvSuvNoCab.setText("No Cabs");

                            // Toast.makeText(getActivity(),"Error in Internet Connection",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CabArrivalTimePojo>> call, Throwable t) {


                    }
                });

            }
        };
        h.post(r);


        /*
        System.out.println("city is "+city);
        System.out.println("lat "+locLat);
        System.out.println("long "+locLong);

        tvMiniNoCab.setText("No Cabs");
        tvMicraNoCab.setText("No Cabs");
        tvSedanNoCab.setText("No Cabs");
        tvSuvNoCab.setText("No Cabs");

        Call<List<CabArrivalTimePojo>> call=REST_CLIENT.getCabArrivalTimes(city,locLat,locLong,companyId);
        call.enqueue(new Callback<List<CabArrivalTimePojo>>() {
            @Override
            public void onResponse(Call<List<CabArrivalTimePojo>> call, Response<List<CabArrivalTimePojo>> response) {

                CabArrivalTimePojo data;
                List<CabArrivalTimePojo> dataList;

                if(myBottomSheet.isAdded())
                {
                    myBottomSheet.dismiss();
                }

                if(response.isSuccessful())
                {
                    dataList=response.body();

                    for(int i=0;i<dataList.size();i++)
                    {
                        data=dataList.get(i);

                        switch (data.getVehicleCategory()) {
                            case "Mini":
                                if(stCategorySelected.equals("Mini")) {

//                                            if(cabDataList.size()!=0)
//                                            {
//                                                tvMiniNoCab.setText(data.getTime());
//                                            }
//                                            else {
//                                                tvMiniNoCab.setText("No Cabs");
//                                            }

                                    tvMiniNoCab.setText(data.getTime());

                                }
                                else {
                                    tvMiniNoCab.setText(data.getTime());
                                }

                                break;
                            case "Micra":
                                if(stCategorySelected.equals("Micra")) {

//                                            if(cabDataList.size()!=0)
//                                            {
//                                                tvMicraNoCab.setText(data.getTime());
//                                            }
//                                            else {
//                                                tvMicraNoCab.setText("No Cabs");
//                                            }
                                    //the condition is kept bcz the filtering is done where only the Local or Both Cabs are retrieved.
                                    //review and remove the comments.
                                    tvMicraNoCab.setText(data.getTime());

                                }
                                else {
                                    tvMicraNoCab.setText(data.getTime());
                                }

                                break;
                            case "Sedan":
                                if(stCategorySelected.equals("Sedan")) {

//                                            if(cabDataList.size()!=0)
//                                            {
//                                                tvSedanNoCab.setText(data.getTime());
//                                            }
//                                            else {
//                                                tvSedanNoCab.setText("No Cabs");
//                                            }

                                    tvSedanNoCab.setText(data.getTime());
                                }
                                else {
                                    tvSedanNoCab.setText(data.getTime());
                                }

                                break;
                            case "SUV":
                                if(stCategorySelected.equals("SUV")) {

//                                            if(cabDataList.size()!=0)
//                                            {
//                                                tvSedanNoCab.setText(data.getTime());
//                                            }
//                                            else {
//                                                tvSedanNoCab.setText("No Cabs");
//                                            }

                                    tvSuvNoCab.setText(data.getTime());
                                }
                                else {
                                    tvSuvNoCab.setText(data.getTime());
                                }

                                break;
                        }

                    }
                }
                else
                {
                    // tvMiniNoCab.setText(" - ");
                    //  tvMicraNoCab.setText(" - ");
                    //  tvSedanNoCab.setText(" - ");

                    tvMiniNoCab.setText("No Cabs");
                    tvMicraNoCab.setText("No Cabs ");
                    tvSedanNoCab.setText("No Cabs");
                    tvSuvNoCab.setText("No Cabs");

                    // System.out.println(response.message()+":::"+response.isSuccessful()+response.errorBody());

                    // Toast.makeText(getActivity(),"Error in Internet Connection",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<CabArrivalTimePojo>> call, Throwable t) {


            }
        });
        */

    }

    public void getCabs() {

        // gettingCabs=true;
        hStart=new Handler();
        rStart=new Runnable() {
            @Override
            public void run() {

                //System.out.println("getting cabsssssssssssssss");

                hStart.postDelayed(this,15000);
                cabDataList.clear();
                //  tvMiniNoCab.setVisibility(View.GONE);
                //    tvMicraNoCab.setVisibility(View.GONE);
                //   tvSedanNoCab.setVisibility(View.GONE);
                //  tvMiniTime.setVisibility(View.VISIBLE);
                //tvMicraTime.setVisibility(View.VISIBLE);
                //  tvSedanTime.setVisibility(View.VISIBLE);

                if(mMap!=null)
                {
                    mMap.clear();
                }

                Call<List<CabPojo>> call=REST_CLIENT.getAllCabs(city,locLat,locLong,stCategorySelected,companyId);
                call.enqueue(new Callback<List<CabPojo>>() {
                    @Override
                    public void onResponse(Call<List<CabPojo>> call, Response<List<CabPojo>> response) {

                        connectivity=true;

                        if(progressDialog!=null){

                            progressDialog.dismiss();
                        }
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

                                if(cabData.getDutyPerform().equals("Local")||cabData.getDutyPerform().equals("Both")) {
                                    // if(cabData.getDutyPerform().equals("Outstation")) {

                                    cabDataList.add(new CabData(cabData.getProfileid(), cabData.getVehichleregno(), cabData.getPhonenumber(), cabData.getVehicleCategory(), cabData.getLatitude(), cabData.getLongitude(), cabData.getDriverName(), cabData.getDriverPic(), cabData.getDutyPerform()));

                                    // System.out.println("duty perform is "+cabData.getDutyPerform());
                                }

                                if(!btBookCab.isEnabled())
                                {
                                    btBookCab.setEnabled(true);
                                }
                            }
                            showCabsOnMap();
                        }
                        else
                        {
                            if(!pickupChanged) {
                                if (curntloc != null) {
                                    mMap.addMarker(new MarkerOptions().position(curntloc)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
                                } else if (lastLoc.latitude!=0.0&&lastLoc.longitude!=0.0) {
                                    mMap.addMarker(new MarkerOptions().position(lastLoc)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
                                }
                            }else {

                                LatLng pLatLng=new LatLng(Double.parseDouble(locLat),Double.parseDouble(locLong));
                                mMap.addMarker(new MarkerOptions().position(pLatLng)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
                            }


                            switch(stCategorySelected)
                            {
                                case "Mini":tvMiniNoCab.setVisibility(View.VISIBLE);

                                    tvMiniNoCab.setText("No Cabs");

                                    btBookCab.setEnabled(false);
                                    if(locLat!=null&&locLong!=null) {
                                        LatLng pLatLng1 = new LatLng(Double.parseDouble(locLat), Double.parseDouble(locLong));
                                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                                .target(pLatLng1)
                                                .zoom(Float.parseFloat("12.8"))
                                                //.bearing(30).tilt(45)
                                                .build()));
                                    }
                                    break;
                                case "Micra":tvMicraNoCab.setVisibility(View.VISIBLE);

                                    tvMicraNoCab.setText("No Cabs");

                                    btBookCab.setEnabled(false);
                                    if(locLat!=null&&locLong!=null) {
                                        LatLng pLatLng2 = new LatLng(Double.parseDouble(locLat), Double.parseDouble(locLong));
                                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                                .target(pLatLng2)
                                                .zoom(Float.parseFloat("12.8"))
                                                //.bearing(30).tilt(45)
                                                .build()));
                                    }
                                    break;
                                case "Sedan":tvSedanNoCab.setVisibility(View.VISIBLE);

                                    tvSedanNoCab.setText("No Cabs");

                                    btBookCab.setEnabled(false);
                                    if(locLat!=null&&locLong!=null) {
                                        LatLng pLatLng3 = new LatLng(Double.parseDouble(locLat), Double.parseDouble(locLong));
                                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                                .target(pLatLng3)
                                                .zoom(Float.parseFloat("12.8"))
                                                //.bearing(30).tilt(45)
                                                .build()));
                                    }
                                    break;
                                case "SUV":tvSuvNoCab.setVisibility(View.VISIBLE);

                                    tvSuvNoCab.setText("No Cabs");

                                    btBookCab.setEnabled(false);
                                    if(locLat!=null&&locLong!=null) {
                                        LatLng pLatLng3 = new LatLng(Double.parseDouble(locLat), Double.parseDouble(locLong));
                                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                                .target(pLatLng3)
                                                .zoom(Float.parseFloat("12.8"))
                                                //.bearing(30).tilt(45)
                                                .build()));
                                    }
                                    break;
                            }

                            // Toast.makeText(getActivity(),"Error in Internet Connection",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CabPojo>> call, Throwable t) {

                        connectivity=false;

                        if(progressDialog!=null){

                            progressDialog.dismiss();
                        }

                        if(rootView.isShown()) {

                            if (myBottomSheet.isAdded()) {
                                return;
                            }
                            else {
                                //System.out.println(rootView.isActivated()+":"+rootView.isFocusable()+":"+rootView.isShown()+":"+rootView.isFocused());


                                myBottomSheet.show(getChildFragmentManager(), myBottomSheet.getTag());

                            }
                        }
                    }
                });
            }
        };
        hStart.post(rStart);


        //cabDataList.clear();
        //  tvMiniNoCab.setVisibility(View.GONE);
        //    tvMicraNoCab.setVisibility(View.GONE);
        //   tvSedanNoCab.setVisibility(View.GONE);
        //  tvMiniTime.setVisibility(View.VISIBLE);
        //tvMicraTime.setVisibility(View.VISIBLE);
        //  tvSedanTime.setVisibility(View.VISIBLE);


        /*
        if(mMap!=null)
        {
            mMap.clear();
        }

        Call<List<CabPojo>> call=REST_CLIENT.getAllCabs(city,locLat,locLong,stCategorySelected,companyId);
        call.enqueue(new Callback<List<CabPojo>>() {
            @Override
            public void onResponse(Call<List<CabPojo>> call, Response<List<CabPojo>> response) {

                connectivity=true;

                if(progressDialog!=null){

                    progressDialog.dismiss();
                }
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

                        if(cabData.getDutyPerform().equals("Local")||cabData.getDutyPerform().equals("Both")) {
                            // if(cabData.getDutyPerform().equals("Outstation")) {

                            cabDataList.add(new CabData(cabData.getProfileid(), cabData.getVehichleregno(), cabData.getPhonenumber(), cabData.getVehicleCategory(), cabData.getLatitude(), cabData.getLongitude(), cabData.getDriverName(), cabData.getDriverPic(), cabData.getDutyPerform()));

                            // System.out.println("duty perform is "+cabData.getDutyPerform());
                        }

                        if(!btBookCab.isEnabled())
                        {
                            btBookCab.setEnabled(true);
                        }
                    }
                    showCabsOnMap();
                }
                else
                {
                    if(!pickupChanged) {
                        if (curntloc != null) {
                            mMap.addMarker(new MarkerOptions().position(curntloc)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
                        } else if (lastLoc != null) {
                            mMap.addMarker(new MarkerOptions().position(lastLoc)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
                        }
                    }else {

                        LatLng pLatLng=new LatLng(Double.parseDouble(locLat),Double.parseDouble(locLong));
                        mMap.addMarker(new MarkerOptions().position(pLatLng)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
                    }


                    switch(stCategorySelected)
                    {
                        case "Mini":tvMiniNoCab.setVisibility(View.VISIBLE);

                            tvMiniNoCab.setText("No Cabs");

                            btBookCab.setEnabled(false);
                            if(locLat!=null&&locLong!=null) {
                                LatLng pLatLng1 = new LatLng(Double.parseDouble(locLat), Double.parseDouble(locLong));
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                        .target(pLatLng1)
                                        .zoom(13)
                                        //.bearing(30).tilt(45)
                                        .build()));
                            }
                            break;
                        case "Micra":tvMicraNoCab.setVisibility(View.VISIBLE);

                            tvMicraNoCab.setText("No Cabs");

                            btBookCab.setEnabled(false);
                            if(locLat!=null&&locLong!=null) {
                                LatLng pLatLng2 = new LatLng(Double.parseDouble(locLat), Double.parseDouble(locLong));
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                        .target(pLatLng2)
                                        .zoom(13)
                                        //.bearing(30).tilt(45)
                                        .build()));
                            }
                            break;
                        case "Sedan":tvSedanNoCab.setVisibility(View.VISIBLE);

                            tvSedanNoCab.setText("No Cabs");

                            btBookCab.setEnabled(false);
                            if(locLat!=null&&locLong!=null) {
                                LatLng pLatLng3 = new LatLng(Double.parseDouble(locLat), Double.parseDouble(locLong));
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                        .target(pLatLng3)
                                        .zoom(13)
                                        //.bearing(30).tilt(45)
                                        .build()));
                            }
                            break;
                        case "SUV":tvSuvNoCab.setVisibility(View.VISIBLE);

                            tvSuvNoCab.setText("No Cabs");

                            btBookCab.setEnabled(false);
                            if(locLat!=null&&locLong!=null) {
                                LatLng pLatLng3 = new LatLng(Double.parseDouble(locLat), Double.parseDouble(locLong));
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                        .target(pLatLng3)
                                        .zoom(13)
                                        //.bearing(30).tilt(45)
                                        .build()));
                            }
                            break;
                    }

                    // Toast.makeText(getActivity(),"Error in Internet Connection",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<CabPojo>> call, Throwable t) {

                connectivity=false;

                if(progressDialog!=null){

                    progressDialog.dismiss();
                }

                if(rootView.isShown()) {

                    if (myBottomSheet.isAdded()) {
                        return;
                    }
                    else {
                        //System.out.println(rootView.isActivated()+":"+rootView.isFocusable()+":"+rootView.isShown()+":"+rootView.isFocused());


                        myBottomSheet.show(getChildFragmentManager(), myBottomSheet.getTag());

                    }
                }
            }
        });*/

    }

    public void showCabsOnMap() {


        if(mMap!=null)
        {
            mMap.clear();
        }


        if(!pickupChanged) {
            if (curntloc != null) {
                mMap.addMarker(new MarkerOptions().position(curntloc)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
            } else if (lastLoc.latitude!=0.0&&lastLoc.longitude!=0.0) {
                mMap.addMarker(new MarkerOptions().position(lastLoc)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
            }
        }else {

            LatLng pLatLng=new LatLng(Double.parseDouble(locLat),Double.parseDouble(locLong));
            mMap.addMarker(new MarkerOptions().position(pLatLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
        }

        CabData c;
        //newly added
        if(cabDataList.size()!=0) {

            for (int i = 0; i < cabDataList.size(); i++) {
                c = cabDataList.get(i);

                double cabLat = Double.parseDouble(c.getLatitude());
                double cabLng = Double.parseDouble(c.getLongitude());
                LatLng cabLoc = new LatLng(cabLat, cabLng);

                if (!pickupChanged) {
                    if (stCategorySelected.equals("Mini") || stCategorySelected.equals("Micra")) {
                        mMap.addMarker(new MarkerOptions().position(cabLoc).icon(BitmapDescriptorFactory.fromResource(R.drawable.mini_15)));
                    }
                    else {
                        mMap.addMarker(new MarkerOptions().position(cabLoc).icon(BitmapDescriptorFactory.fromResource(R.drawable.prime_15)));
                    }
                } else {
                    LatLng pLatLng = new LatLng(Double.parseDouble(locLat), Double.parseDouble(locLong));
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(pLatLng)
                            .zoom(Float.parseFloat("12.8"))
                            //.bearing(30).tilt(45)
                            .build()));

                    if (stCategorySelected.equals("Mini") || stCategorySelected.equals("Micra")) {
                        mMap.addMarker(new MarkerOptions().position(cabLoc).icon(BitmapDescriptorFactory.fromResource(R.drawable.mini_15)));
                    }
                    else {
                        mMap.addMarker(new MarkerOptions().position(cabLoc).icon(BitmapDescriptorFactory.fromResource(R.drawable.prime_15)));
                    }
                }
                // mMap.addMarker(new MarkerOptions().position(cabLoc).icon(BitmapDescriptorFactory.fromResource(R.drawable.car_image)));
            }
        }
        else {

            //newly added

            switch(stCategorySelected)
            {
                case "Mini":tvMiniNoCab.setVisibility(View.VISIBLE);
                    tvMiniNoCab.setText("No Cabs");

                    btBookCab.setEnabled(false);
                    if(locLat!=null&&locLong!=null) {
                        LatLng pLatLng1 = new LatLng(Double.parseDouble(locLat), Double.parseDouble(locLong));
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(pLatLng1)
                                .zoom(Float.parseFloat("12.8"))
                                //.bearing(30).tilt(45)
                                .build()));
                    }
                    break;
                case "Micra":tvMicraNoCab.setVisibility(View.VISIBLE);
                    tvMicraNoCab.setText("No Cabs");

                    btBookCab.setEnabled(false);
                    if(locLat!=null&&locLong!=null) {
                        LatLng pLatLng2 = new LatLng(Double.parseDouble(locLat), Double.parseDouble(locLong));
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(pLatLng2)
                                .zoom(Float.parseFloat("12.8"))
                                //.bearing(30).tilt(45)
                                .build()));
                    }
                    break;
                case "Sedan":tvSedanNoCab.setVisibility(View.VISIBLE);
                    tvSedanNoCab.setText("No Cabs");

                    btBookCab.setEnabled(false);
                    if(locLat!=null&&locLong!=null) {
                        LatLng pLatLng3 = new LatLng(Double.parseDouble(locLat), Double.parseDouble(locLong));
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(pLatLng3)
                                .zoom(Float.parseFloat("12.8"))
                                //.bearing(30).tilt(45)
                                .build()));
                    }
                    break;
                case "SUV":tvSuvNoCab.setVisibility(View.VISIBLE);
                    tvSuvNoCab.setText("No Cabs");

                    btBookCab.setEnabled(false);
                    if(locLat!=null&&locLong!=null) {
                        LatLng pLatLng3 = new LatLng(Double.parseDouble(locLat), Double.parseDouble(locLong));
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(pLatLng3)
                                .zoom(Float.parseFloat("12.8"))
                                //.bearing(30).tilt(45)
                                .build()));
                    }
                    break;
            }

        }
    }


    public void establishConnection(){

        buildGoogleApiClient();
        buildLocationSettingsRequest();
        entered=true;
        //getCabs();
    }


    @Override
    public void onStart() {

        super.onStart();

        if(Build.VERSION.SDK_INT>=23)
        {
            if(!entered)
            {
                // System.out.println("value of entered in 'if' "+entered);
            }
            else
            {
                //System.out.println("value of entered in 'else' "+entered);
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

            // System.out.println("in start calling handler post");
            //remove comments if handlers are used


            if (h != null) {
                h.post(r);
            }

            if (hStart != null) {
                hStart.post(rStart);
            }

        }
    }

    @Override
    public void onStop() {

        if(mGoogleApiClient!=null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
        //gettingCabs=true;

        if(entered) {
            //remove comments if handlers are used

            if (h != null) {
                h.removeCallbacks(r);
            }

            if (hStart != null) {
                hStart.removeCallbacks(rStart);
            }

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

            if (mGoogleApiClient.isConnected()&& !mRequestingLocationUpdates) {//&& mRequestingLocationUpdates
                //
                startLocationUpdates();
            }
        }
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
                            status.startResolutionForResult(getActivity(),REQUEST_LOCATION);
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
        mLocationRequest.setInterval(5000);//45 sec
        mLocationRequest.setFastestInterval(5000);//5 sec
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

        if(mLastLocation==null)
        {
            current_lat=location.getLatitude();
            current_long=location.getLongitude();
            if (current_lat != 0 && current_long != 0) {
                curntloc = new LatLng(current_lat, current_long);

//                Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(), current_lat, current_long, results);
//                location.getAccuracy();
//                res = res + (long) results[0];

                try {
                    addresses = geocoder.getFromLocation(current_lat, current_long, 1);

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
                        String address = addresses.get(0).getAddressLine(0);
                        String add_1 = addresses.get(0).getAddressLine(1);//current place name eg:Nagendra nagar,Hyderabad
                        String add_2 = addresses.get(0).getAddressLine(2);
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();

                        if(add_1!=null&&add_2!=null) {
                            complete_address = address + " " + add_1 + " " + add_2;
                        }
                        else {
                            complete_address=address;
                        }
                        //tvCloc.setText(add);
                        //complete_address = add;
                    }
                    else {
                        complete_address="-";
                    }
                }
                catch(IOException e) {
                    e.printStackTrace();
                    complete_address="Unable to get the location details";
                    // tvCloc.setText(complete_address);
                }

                if(!pickupChanged)
                {
                    locLat=String.valueOf(current_lat);
                    locLong= String.valueOf(current_long);
                    pickupLat=locLat;
                    pickupLong=locLong;
                    tvPickup.setText(complete_address);
                    editor.putString("pickup_location",tvPickup.getText().toString().trim());
                    editor.putString("pickup_lat",String.valueOf(current_lat));
                    editor.putString("pickup_long",String.valueOf(current_long));
                    editor.commit();

                    if (cab != null) {
                        cab.setPosition(curntloc);
                    } else {
                        cab = mMap.addMarker(new MarkerOptions().position(lastLoc)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
                    }

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curntloc, Float.parseFloat("12.8")));
                    cab.setPosition(curntloc);
//                    getCabs();
//                    getCabTimes();
                }

                //  mMap.addMarker(new MarkerOptions().position(curntloc)
                //   .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_image)));
                //
                mMap.getUiSettings().setMapToolbarEnabled(false);
            }
        }


        if(mLastLocation!=null) {

            current_lat=location.getLatitude();
            current_long=location.getLongitude();
            if (current_lat != 0 && current_long != 0) {
                curntloc = new LatLng(current_lat, current_long);

//                Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(), current_lat, current_long, results);
//                location.getAccuracy();
//                res = res + (long) results[0];

                try {
                    addresses = geocoder.getFromLocation(current_lat, current_long, 1);

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
                        String address = addresses.get(0).getAddressLine(0);
                        String add_1 = addresses.get(0).getAddressLine(1);//current place name eg:Nagendra nagar,Hyderabad
                        String add_2 = addresses.get(0).getAddressLine(2);
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();

                        if(add_1!=null&&add_2!=null) {
                            complete_address = address + " " + add_1 + " " + add_2;
                        }
                        else {
                            complete_address=address;
                        }
                        //tvCloc.setText(add);
                        //complete_address = add;
                    }
                    else {
                        complete_address="-";
                    }
                }
                catch(IOException e) {
                    e.printStackTrace();
                    complete_address="Unable to get the location details";
                    // tvCloc.setText(complete_address);
                }

                if(!pickupChanged)
                {
                    locLat=String.valueOf(current_lat);
                    locLong= String.valueOf(current_long);
                    pickupLat=locLat;
                    pickupLong=locLong;
                    tvPickup.setText(complete_address);
                    editor.putString("pickup_location",tvPickup.getText().toString().trim());
                    editor.putString("pickup_lat",String.valueOf(current_lat));
                    editor.putString("pickup_long",String.valueOf(current_long));
                    editor.commit();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curntloc, Float.parseFloat("12.8")));
                    cab.setPosition(curntloc);
//                    getCabs();
//                    getCabTimes();
                }

                //  mMap.addMarker(new MarkerOptions().position(curntloc)
                //   .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_image)));
                //
                mMap.getUiSettings().setMapToolbarEnabled(false);
            }
        }

        if(first) {

            getCabs();
            getCabTimes();
            first=false;

        }

        mLastLocation=location;

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
        //LatLng sydney = new LatLng(-34, 151);

        // mMap.addMarker(new MarkerOptions().position(lastLoc)
        // .title("Current Location")
        //  .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_image)));
        if(!pickupChanged)
        {
            if(lastLoc.latitude!=0.0&&lastLoc.longitude!=0.0) {

                locLat = String.valueOf(lastLoc.latitude);
                locLong = String.valueOf(lastLoc.longitude);
                pickupLat = locLat;
                pickupLong = locLong;
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(lastLoc)
                        .zoom(Float.parseFloat("12.8"))
                        //.bearing(30).tilt(45)
                        .build()));
                if (cab != null) {
                    cab.setPosition(lastLoc);
                } else {
                    cab = mMap.addMarker(new MarkerOptions().position(lastLoc)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
                }
            }
        }

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        try {
            mMap.setMyLocationEnabled(true);
        }catch (SecurityException e){e.printStackTrace();}

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
                //tvCloc.setText(add);
                // city = addresses.get(0).getLocality();
            }
        }
        catch (Exception e){e.printStackTrace();}

        // locLat=String.valueOf(latitude);
        // locLong=String.valueOf(longitude);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return  true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //getCabs();
        switch (requestCode)
        {
            case 2:
                if(data!=null)
                {
                    Double stLat = data.getDoubleExtra("lat", 0.0);
                    Double stLng = data.getDoubleExtra("lng", 0.0);
                    String locationName=data.getStringExtra("loc");

                    try {
                        addresses = geocoder.getFromLocation(stLat, stLng, 1);

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
                        }

                        if(stClicked.equals("pickup"))
                        {
                            tvPickup.setText(locationName);
                            pickupLat=String.valueOf(stLat);
                            pickupLong=String.valueOf(stLng);
                            locLat=pickupLat;
                            locLong=pickupLong;
                            editor.putString("pickup_location",tvPickup.getText().toString().trim());
                            editor.putString("pickup_lat",pickupLat);
                            editor.putString("pickup_long",pickupLong);
                            LatLng pLatLng=new LatLng(Double.parseDouble(pickupLat),Double.parseDouble(pickupLong));
                            //cab.setPosition(pLatLng);
                            if(cab!=null)
                            {
                                cab.setPosition(pLatLng);
                            }
                            else {
                                cab = mMap.addMarker(new MarkerOptions().position(pLatLng)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
                            }

                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                    .target(pLatLng)
                                    .zoom(Float.parseFloat("12.7"))
                                    //.bearing(30).tilt(45)
                                    .build()));

                            editor.commit();

                            //remove comments if handlers are used

//                            hStart.removeCallbacks(rStart);
//                            hStart.post(rStart);
//
//                            h.removeCallbacks(r);
//                            h.post(r);


//                            getCabs();
//                            getCabTimes();

                            if (dbAdapter.checkIfPlaceNameExists(locationName)) {

                            } else {

                                if (!(pickupLat.equals("-")) || !(pickupLong.equals("-"))) {
                                    dbAdapter.insertUserLocation("1", locationName, Double.parseDouble(pickupLat), Double.parseDouble(pickupLong));
                                }
                            }
                        }
                        else
                        {
                            tvDrop.setTextColor(Color.parseColor("#000000"));
                            tvDrop.setText(locationName);
                            dropLat=String.valueOf(stLat);
                            dropLong=String.valueOf(stLng);
                            editor.putString("drop_location",tvDrop.getText().toString().trim());
                            editor.putString("drop_lat",dropLat);
                            editor.putString("drop_long",dropLong);
                            editor.commit();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                {

                }
                break;
            case 3:

                if(data!=null)
                {
                    stLocalPkg=data.getStringExtra("localpkg");
                    stFare=data.getStringExtra("fare");

                    if(stLocalPkg.equals(""))
                    {

                        if(stCategorySelected.equals("SUV"))
                        {

                            tvLocalOneWay.setVisibility(View.VISIBLE);
                            llMini.setBackground(getResources().getDrawable(R.drawable.round_bg_cabs));
                            llSuv.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));

                            tvMini.setTextColor(Color.parseColor("#0067de"));
                            tvSuv.setTextColor(Color.parseColor("#414040"));
                            // tvLocalPkg.setTextColor(Color.parseColor("#414040"));

                            stCategorySelected="Mini";
//                            getCabs();
//                            getCabTimes();
                            //remove comments if handlers are used
//                            hStart.removeCallbacks(rStart);
//                            hStart.post(rStart);
                        }

                        tvLocalOneWay.setTextColor(Color.parseColor("#0067de"));
                        tvLocalPkg.setTextColor(Color.parseColor("#9e9e9e"));
                        stLocalPkg="";
                        tvDrop.setText("");
                        btRideLater.setVisibility(View.GONE);
                        btBookCab.setText("Book Now");
                    }
                    else {

                        tvLocalPkg.setText(stLocalPkg);
                        tvLocalPkg.setTextColor(Color.parseColor("#0067de"));
                        //tvLocalOneWay.setTextColor(Color.parseColor("#9e9e9e"));
                        tvDrop.setTextColor(Color.parseColor("#000000"));
                        tvDrop.setText("-");

                        dropLat="-";
                        dropLong="-";
                        editor.putString("drop_location","-");
                        editor.putString("drop_lat",dropLat);
                        editor.putString("drop_long",dropLong);
                        editor.commit();
                    }
                }
                else {

                    if(stCategorySelected.equals("SUV"))
                    {

                        llMini.setBackground(getResources().getDrawable(R.drawable.round_bg_cabs));
                        llSuv.setBackground(getResources().getDrawable(R.drawable.round_bg_white_cabs));

                        tvMini.setTextColor(Color.parseColor("#0067de"));
                        tvSuv.setTextColor(Color.parseColor("#414040"));
                        // tvLocalPkg.setTextColor(Color.parseColor("#414040"));

                        stCategorySelected="Mini";
                        //remove comments if handlers are used
//                        hStart.removeCallbacks(rStart);
//                        hStart.post(rStart);

//                        getCabs();
//                        getCabTimes();
                    }
                    else {

                    }


                    tvLocalOneWay.setVisibility(View.VISIBLE);
                    tvLocalOneWay.setTextColor(Color.parseColor("#0067de"));
                    tvLocalPkg.setTextColor(Color.parseColor("#9e9e9e"));
                    stLocalPkg="";
                    //System.out.println("no data");
                    tvDrop.setText("");
                    btRideLater.setVisibility(View.GONE);
                    btBookCab.setText("Book Now");
                }
                break;
        }
    }

    public void selectServiceLocations() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_locations, null);

        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
//        alertDialog.setCancelable(false);
//        alertDialog.setCanceledOnTouchOutside(false);

        tvLoc1 = (TextView) dialogView.findViewById(R.id.ale_tv_loc1);
        tvLoc2 = (TextView) dialogView.findViewById(R.id.ale_tv_loc2);
        tvLoc3 = (TextView) dialogView.findViewById(R.id.ale_tv_loc3);
        tvLoc4 = (TextView) dialogView.findViewById(R.id.ale_tv_loc4);
        tvLoc5 = (TextView) dialogView.findViewById(R.id.ale_tv_loc5);

        tvLoc1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvLoc1.setTextColor(Color.parseColor("#000000"));


                city = tvLoc1.getText().toString().trim();
                tvCity.setText(city);
                editor.putString("city", city);
                editor.commit();
                alertDialog.dismiss();
            }
        });

        tvLoc2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvLoc2.setTextColor(Color.parseColor("#000000"));

                city = tvLoc2.getText().toString().trim();
                tvCity.setText(city);
                editor.putString("city", city);
                editor.commit();

                alertDialog.dismiss();
            }
        });

        tvLoc3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvLoc3.setTextColor(Color.parseColor("#000000"));


                city = tvLoc3.getText().toString().trim();
                tvCity.setText(city);

                editor.putString("city", city);
                editor.commit();

                alertDialog.dismiss();
            }
        });

        tvLoc4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvLoc4.setTextColor(Color.parseColor("#000000"));


                city = tvLoc4.getText().toString().trim();
                tvCity.setText(city);

                editor.putString("city", city);
                editor.commit();

                alertDialog.dismiss();
            }
        });

        tvLoc5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvLoc5.setTextColor(Color.parseColor("#000000"));


                city = tvLoc5.getText().toString().trim();
                tvCity.setText(city);

                editor.putString("city", city);
                editor.commit();

                alertDialog.dismiss();
            }
        });


        Call<List<ServiceLocationPojo>> call = REST_CLIENT.getServiceLocations("CMP00001");
        call.enqueue(new Callback<List<ServiceLocationPojo>>() {
            @Override
            public void onResponse(Call<List<ServiceLocationPojo>> call, Response<List<ServiceLocationPojo>> response) {

                ServiceLocationPojo data;
                List<ServiceLocationPojo> dataList;

                if (response.isSuccessful()) {
                    dataList = response.body();
                    j=0;

                    for (int i = 0; i < dataList.size(); i++) {
                        data = dataList.get(i);

                        switch (j) {
                            case 0:
                                tvLoc1.setText(data.getLocation());
                                tvLoc1.setVisibility(View.VISIBLE);
                                j++;
                                break;
                            case 1:
                                tvLoc2.setText(data.getLocation());
                                tvLoc2.setVisibility(View.VISIBLE);
                                j++;
                                break;
                            case 2:
                                tvLoc3.setText(data.getLocation());
                                tvLoc3.setVisibility(View.VISIBLE);
                                j++;
                                break;
                            case 3:
                                tvLoc4.setText(data.getLocation());
                                tvLoc4.setVisibility(View.VISIBLE);
                                j++;
                                break;
                            case 4:
                                tvLoc5.setText(data.getLocation());
                                tvLoc5.setVisibility(View.VISIBLE);
                                j++;
                                break;
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<List<ServiceLocationPojo>> call, Throwable t) {

                Toast.makeText(getActivity(), "Check Internet COnnection", Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //hStart.removeCallbacks(rStart);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //remove comments if handlers are used

        if(hStart!=null) {
            hStart.removeCallbacks(rStart);
        }

        if(h!=null) {
            h.removeCallbacks(r);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }
}
